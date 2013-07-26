/*
 * Copyright (C) 2011 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.webservice.resources.tables;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sf.beanlib.CollectionPropertyName;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.lists.WorkflowRunList;
import net.sourceforge.seqware.common.model.lists.WorkflowRunList2;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.ArrayUtils;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;

/**
 * This resource will pull back the workflow runs that are generated from a
 * particular file.
 *
 * In order: a) Workflow runs found via processing, processing_relationship,
 * workflow_run b) Workflow runs found via the IUS, ius_workflow_runs c)
 * Workflow runs found via the lane, lane_workflow_runs
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class FileChildWorkflowRunsResource extends DatabaseResource {
    /**
     * A direct search uses the workflow_run_files table rather than 
     * attempting a search via the ius_workflow_run and lane_workflow_run and processing hierarchy
     */
    public static final String DIRECT_SEARCH = "DIRECT_SEARCH";

    /**
     * <p>Constructor for FileChildWorkflowRunsResource.</p>
     */
    public FileChildWorkflowRunsResource() {
        super("file");
    }

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() throws SQLException {
        authenticate();
        final Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject jaxbTool;
        Log.debug("Dealing with FileChildWorkflowRunsResource");

        if (queryValues.keySet().contains(DIRECT_SEARCH) && queryValues.get(DIRECT_SEARCH).equalsIgnoreCase("true")) {
            handleDirectGetXML();
            return;
        }
        
        
        FileService fs = BeanFactory.getFileServiceBean();
        Set<File> files = new HashSet<File>();
        SEARCH_TYPE searchType = SEARCH_TYPE.CHILDREN_VIA_PROCESSING_RELATIONSHIP;
        Log.debug("File service started");
        for (String key : queryValues.keySet()) {
            Log.debug("key: " + key + " -> " + queryValues.get(key));
            if (key.equals("files")) {
                String value = queryValues.get(key);
                String[] filesSWIDs = value.split(",");
                for (String swid : filesSWIDs) {
                    File findByID = (File) testIfNull(fs.findBySWAccession(Integer.valueOf(swid)));
                    files.add(findByID);
                }
            }
            if (key.equals("search")) {
                String value = queryValues.get(key);
                try {
                    searchType = SEARCH_TYPE.valueOf(value);
                } catch (IllegalArgumentException e) {
                    throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Object cannot be found");
                }
            }
        }
        Log.debug("Working with " + files.size() + " files and doing a search of type: " + searchType.toString());

        // these variables will be used to return information
        jaxbTool = new JaxbObject<WorkflowRunList>();
        WorkflowRunList2 eList = new WorkflowRunList2();
        eList.setList(new ArrayList());
        Log.debug("JaxbObjects started");

        assert eList.getList().isEmpty();
        // the logic here is, we consider first workflow_run children found via the children of the processing
        // if that is empty, then we consider workflow_run found via the IUS (ius_workflow_run table)
        // if that is empty, then we consider workflow_run found via the lane (lane_workflow_run table)
        if (searchType == SEARCH_TYPE.CHILDREN_VIA_PROCESSING_RELATIONSHIP){
            eList = handleWorkflowRunsViaChild(files, copier);
        } else if (searchType == SEARCH_TYPE.CHILDREN_VIA_IUS_WORKFLOW_RUN){
            eList = handleWorkflowRunsViaIUS(files, copier);
        } else if (searchType == SEARCH_TYPE.CHILDREN_VIA_LANE_WORKFLOW_RUN){
            eList = handleWorkflowRunsViaLane(files, copier);
        }
        final Document line = XmlTools.marshalToDocument(jaxbTool, eList);
        getResponse().setEntity(XmlTools.getRepresentation(line));
        getResponse().setStatus(Status.SUCCESS_CREATED);
    }

    protected WorkflowRunList2 handleWorkflowRunsViaChild(final Set<File> files, final Hibernate3DtoCopier copier) {
        final WorkflowRunList2 results = new WorkflowRunList2();
        for (File file : files) {
            //1) check if we have children in the processing tree that are relevant
            for (Processing p : file.getProcessings()) {
                for (Processing c : p.getChildren()) {
                    if (c.getWorkflowRun() == null) {
                        continue;
                    }
                    WorkflowRun dto = copier.hibernate2dto(WorkflowRun.class, c.getWorkflowRun());
                    results.add(dto);
                }
            }
        }
        if (results.getList().size() > 0) {
            Log.debug("Found " + results.getList().size() + " workflow runs via Processing children");
        } else {
            Log.debug("Did not find any workflow runs via Processing children");
        }
        return results;
    }

    protected WorkflowRunList2 handleWorkflowRunsViaIUS(Set<File> files, final Hibernate3DtoCopier copier) {
        WorkflowRunList2 result = new WorkflowRunList2();
        Set<WorkflowRun> parentWorkflowRuns = new HashSet<WorkflowRun>();
        for (File file : files) {
            //2) check if we have children in the ius_workflow_runs that are relevant
            for (Processing p : file.getProcessings()) {
                WorkflowRun parentRun = p.getWorkflowRun();
                if (parentRun == null){
                    parentRun = p.getWorkflowRunByAncestorWorkflowRunId();
                }
                if (parentRun == null) {
                    continue;
                }
                parentWorkflowRuns.add(parentRun);
                for (IUS ius : parentRun.getIus()) {
                    for (WorkflowRun anyRun : ius.getWorkflowRuns()) {
                        isWorkflowRunAttached(parentWorkflowRuns, anyRun);
                        WorkflowRun dto = copier.hibernate2dto(WorkflowRun.class, anyRun);
                        result.add(dto);
                    }
                }
            }
        }
        if (result.getList().size() > 0) {
            Log.debug("Found " + result.getList().size() + " workflow runs via ius");
        } else {
            Log.debug("Did not find any workflow runs via ius");
        }
        return result;
    }

    protected WorkflowRunList2 handleWorkflowRunsViaLane(final Set<File> files, final Hibernate3DtoCopier copier) {
        final WorkflowRunList2 result = new WorkflowRunList2();
        Set<WorkflowRun> parentWorkflowRuns = new HashSet<WorkflowRun>();
        for (File file : files) {
            //3) check if we have children in the lane_workflow_runs that are relevant
            for (Processing p : file.getProcessings()) {
                WorkflowRun parentRun = p.getWorkflowRun();
                if (parentRun == null){
                    parentRun = p.getWorkflowRunByAncestorWorkflowRunId();
                }
                if (parentRun == null) {
                    continue;
                }
                parentWorkflowRuns.add(parentRun);
                for (Lane lane : parentRun.getLanes()) {
                    for (WorkflowRun anyRun : lane.getWorkflowRuns()) {
                        if (isWorkflowRunAttached(parentWorkflowRuns, anyRun)) continue;
                        final WorkflowRun dto = copier.hibernate2dto(WorkflowRun.class, anyRun);
                        result.add(dto);
                    }
                }
            }
        }
        if (result.getList().size() > 0) {
            Log.debug("Found " + result.getList().size() + " workflow runs via lane");
        } else {
            Log.debug("Did not find any workflow runs via lane");
        }
        return result;
    }

    /**
     * Disallows a workflow run if it should not be considered since it is properly attached to the processing hierarchy
     * @param parentWorkflowRuns
     * @param anyRun
     * @return 
     */
    private boolean isWorkflowRunAttached(Set<WorkflowRun> parentWorkflowRuns, WorkflowRun anyRun) {
        if (parentWorkflowRuns.contains(anyRun)) {
            Log.debug("Disallowed " + anyRun.getSwAccession() + " because we have seen it on the same level as a file");
            return true;
        }
        // check that this workflow run has not actually been linked up into the Processing hierarchy
        if (anyRun.getProcessings() != null && anyRun.getProcessings().size() > 0) {
            Log.debug("Disallowed " + anyRun.getSwAccession() + " because it is already attached via Processing.workflow_run_id");
            return true;
        }
        if (anyRun.getOffspringProcessings() != null && anyRun.getOffspringProcessings().size() > 0) {
            Log.debug("Disallowed " + anyRun.getSwAccession() + " because it is already attached via Processing.ancestor_workflow_run_id");
            return true;
        }
        return false;
    }

    /**
     * Use SQL to directly retrieve relevant workflows runs (defined as any workflow runs that 
     * include one or more files in the set we were given)
     * @param files
     * @return
     * @throws SQLException 
     */
    private WorkflowRunList2 directRetrieveWorkflowRuns(List<Integer> files) throws SQLException {
        final Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        final WorkflowRunList2 runs = new WorkflowRunList2();
        runs.setList(new ArrayList());
        if (files.size() > 0) {

            ResultSet rs = null;
            MetadataDB mdb = null;
            try {
                WorkflowRunService ss = BeanFactory.getWorkflowRunServiceBean();
                StringBuilder query = new StringBuilder();
                query.append("select distinct r.sw_accession from workflow_run r, ");
                query.append("workflow_run_files f WHERE r.workflow_run_id = f.workflow_run_id AND (");
                for(int i = 0; i < files.size() - 1; i++){
                    Integer fInt = files.get(i);
                    query.append(" f.file_sw_accession = ").append(fInt).append(" OR");
                }
                Integer fInt = files.get(files.size() -1);
                query.append(" f.file_sw_accession = ").append(fInt).append(")");
                query.append(" ORDER BY sw_accession");
                
                Log.info("Executing query: " + query);
                mdb = DBAccess.get();
                rs = mdb.executeQuery(query.toString());
                while (rs.next()) {
                    int workflowSWID = rs.getInt("sw_accession");
                    WorkflowRun workflowRun = (WorkflowRun) testIfNull(ss.findBySWAccession(workflowSWID));
                    CollectionPropertyName<WorkflowRun>[] createCollectionPropertyNames = CollectionPropertyName.createCollectionPropertyNames(WorkflowRun.class, new String[]{"inputFileAccessions"});
                    WorkflowRun dto = copier.hibernate2dto(WorkflowRun.class, workflowRun, ArrayUtils.EMPTY_CLASS_ARRAY, createCollectionPropertyNames);
                    runs.add(dto);
                }
            } finally {
                if (mdb != null) {
                    DbUtils.closeQuietly(mdb.getDb(), mdb.getSql(), rs);
                }
                DBAccess.close();
            }
        }
        return runs;
    }

    private void handleDirectGetXML() throws SQLException, NumberFormatException {
        JaxbObject jaxbTool;
        Log.info("Using direct search");
        List<Integer> files = new ArrayList<Integer>();
        for (String key : queryValues.keySet()) {
            Log.debug("key: " + key + " -> " + queryValues.get(key));
            if (key.equals("files")) {
                String value = queryValues.get(key);
                String[] filesSWIDs = value.split(",");
                for (String swid : filesSWIDs) {
                    files.add(Integer.valueOf(swid));
                }
            }
        }
        Log.debug("Working with " + files.size() + " files");
        WorkflowRunList2 runs = directRetrieveWorkflowRuns(files);
        // these variables will be used to return information
        jaxbTool = new JaxbObject<WorkflowRunList>();
        Log.debug("JaxbObjects started");
        assert runs.getList().isEmpty();
        final Document line = XmlTools.marshalToDocument(jaxbTool, runs);
        getResponse().setEntity(XmlTools.getRepresentation(line));
        getResponse().setStatus(Status.SUCCESS_CREATED);
    }

    public enum SEARCH_TYPE {
        CHILDREN_VIA_PROCESSING_RELATIONSHIP,
        CHILDREN_VIA_IUS_WORKFLOW_RUN,
        CHILDREN_VIA_LANE_WORKFLOW_RUN
    }
}
