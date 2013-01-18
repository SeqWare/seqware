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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.lists.SampleList;
import net.sourceforge.seqware.common.model.lists.WorkflowRunList;
import net.sourceforge.seqware.common.model.lists.WorkflowRunList2;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;
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
     * <p>Constructor for FileChildWorkflowRunsResource.</p>
     */
    public FileChildWorkflowRunsResource() {
        super("file");
    }

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        authenticate();
        final Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
        JaxbObject jaxbTool;
        Log.debug("Dealing with FileChildWorkflowRunsResource");

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

    public enum SEARCH_TYPE {
        CHILDREN_VIA_PROCESSING_RELATIONSHIP,
        CHILDREN_VIA_IUS_WORKFLOW_RUN,
        CHILDREN_VIA_LANE_WORKFLOW_RUN
    }
}
