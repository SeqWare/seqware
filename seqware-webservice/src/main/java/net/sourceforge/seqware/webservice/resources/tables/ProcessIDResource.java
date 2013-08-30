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

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.sf.beanlib.CollectionPropertyName;

import net.sf.beanlib.hibernate3.Hibernate3DtoCopier;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentLibraryDesign;
import net.sourceforge.seqware.common.model.ExperimentSpotDesign;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.model.ProcessingStatus;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.xmltools.JaxbObject;
import net.sourceforge.seqware.common.util.xmltools.XmlTools;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.dbutils.ResultSetHandler;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * <p>ProcessIDResource class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class ProcessIDResource extends DatabaseIDResource {

    /**
     * <p>Constructor for ProcessIDResource.</p>
     */
    public ProcessIDResource() {
        super("processId");
    }

    /**
     * <p>getXml.</p>
     */
    @Get
    public void getXml() {
        JaxbObject<Lane> jaxbTool = new JaxbObject<Lane>();
        Hibernate3DtoCopier copier = new Hibernate3DtoCopier();


        authenticate();

        ProcessingService ss = BeanFactory.getProcessingServiceBean();
        Processing processing = (Processing) testIfNull(ss.findBySWAccession(getId()));
        Processing dto = copier.hibernate2dto(Processing.class, processing);

        if (fields.contains("workflowRun")) {
            WorkflowRun wr = processing.getWorkflowRun();
            if (wr != null) {
                WorkflowRun copyWR = copier.hibernate2dto(WorkflowRun.class, wr);
                dto.setWorkflowRun(copyWR);
            } else if (wr == null) {
                Log.info("Could not be found : workflow run");
            }
        }
        if (fields.contains("attributes")) {
            Set<ProcessingAttribute> pas = processing.getProcessingAttributes();
            if (pas != null && !pas.isEmpty()) {
                Set<ProcessingAttribute> newpas = new TreeSet<ProcessingAttribute>();
                for (ProcessingAttribute pa : pas) {
                    newpas.add(copier.hibernate2dto(ProcessingAttribute.class, pa));
                }
                dto.setProcessingAttributes(newpas);
            }
        }

        Document line = XmlTools.marshalToDocument(jaxbTool, dto);
        getResponse().setEntity(XmlTools.getRepresentation(line));
    }

    /** {@inheritDoc} */
    @Override
    public Representation put(Representation rep) {
        authenticate();
        Representation toreturn = null;
        if (rep.getMediaType().equals(MediaType.APPLICATION_XML)) {
            JaxbObject<Processing> jo = new JaxbObject<Processing>();
            Processing p = null;
            try {
                String text = rep.getText();
                p = (Processing) XmlTools.unMarshal(jo, new Processing(), text);
            } catch (IOException e) {
                e.printStackTrace();
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
            } catch (SAXException ex) {
                ex.printStackTrace();
                throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY, ex);
            }
            try {

                Log.info("Updating Processing " + p.getSwAccession());
                // SEQWARE-1679 see HHH-3030 Hibernate assumes that version columns are not null and fails if they are
                // let's try setting it if it is not set first, should fix this systematically in the database
                MetadataDB mdb0 = DBAccess.get();
                try {
                    ReturnValue ret = mdb0.set_processing_update_tstmp_if_null(p.getProcessingId());
                    if (ret.getExitStatus() != ReturnValue.SUCCESS) {
                        throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
                                "Error in Update Processing update timestamp with error  " + ret.getExitStatus());
                    }
                } finally {
                    DBAccess.close();
                }

                // Move the Hibernate calls before the direct DB access so that
                // the authentication of this user is checked by the Hibernate layer
                ProcessingService ps = BeanFactory.getProcessingServiceBean();
                Processing processing = (Processing) testIfNull(ps.findBySWAccession(p.getSwAccession()));
                Log.debug("Checking processing permission for " + registration.getEmailAddress());
                processing.givesPermission(registration);

                if (p.getOwner() != null) {
                    processing.setOwner(BeanFactory.getRegistrationServiceBean().findByEmailAddress(p.getOwner().getEmailAddress()));
                } else {
                    processing.setOwner(registration);
                }
                processing.setStatus(p.getStatus() == null ? ProcessingStatus.pending : p.getStatus());
                processing.setTaskGroup(p.isTaskGroup());
                if (p.getRunStartTimestamp() != null) {
                    processing.setRunStartTimestamp(p.getRunStartTimestamp());
                }
                if (p.getRunStopTimestamp() != null) {
                    processing.setRunStopTimestamp(p.getRunStopTimestamp());
                }
                if (p.getFiles() != null) {
                    if (processing.getFiles() == null) {
                        processing.setFiles(p.getFiles());
                    } else {
                        processing.getFiles().addAll(p.getFiles());
                    }
                }
                if (p.getIUS() != null) {
                    HashSet<IUS> set = new HashSet<IUS>();
                    IUSService is = BeanFactory.getIUSServiceBean();
                    for (IUS i : p.getIUS()) {
                        IUS newI = is.findBySWAccession(i.getSwAccession());
                        if (newI != null && newI.givesPermission(registration)) {
                            set.add(newI);
                        } else if (newI == null) {
                            Log.info("Could not be found " + i);
                        }
                    }
                    if (processing.getIUS() == null) {
                        processing.setIUS(set);
                    } else {
                        processing.getIUS().addAll(set);
                    }
                }
                if (p.getLanes() != null) {
                    HashSet<Lane> set = new HashSet<Lane>();
                    LaneService ls = BeanFactory.getLaneServiceBean();
                    for (Lane l : p.getLanes()) {
                        Lane newL = ls.findBySWAccession(l.getSwAccession());
                        if (newL != null && newL.givesPermission(registration)) {
                            set.add(newL);
                        } else if (newL == null) {
                            Log.info("Could not be found " + l);
                        }
                    }
                    if (processing.getLanes() == null) {
                        processing.setLanes(set);
                    } else {
                        processing.getLanes().addAll(set);
                    }
                }
                if (p.getSamples() != null) {
                    HashSet<Sample> set = new HashSet<Sample>();
                    SampleService ss = BeanFactory.getSampleServiceBean();
                    for (Sample s : p.getSamples()) {
                        Sample newS = ss.findBySWAccession(s.getSwAccession());
                        if (newS != null && newS.givesPermission(registration)) {
                            set.add(newS);
                        } else if (newS == null) {
                            Log.info("Could not be found " + s);
                        }
                    }
                    if (processing.getSamples() == null) {
                        processing.setSamples(set);
                    } else {
                        processing.getSamples().addAll(set);
                    }
                }

                if (p.getSequencerRuns() != null) {
                    HashSet<SequencerRun> set = new HashSet<SequencerRun>();
                    SequencerRunService srs = BeanFactory.getSequencerRunServiceBean();
                    for (SequencerRun sr : p.getSequencerRuns()) {
                        SequencerRun newSR = srs.findBySWAccession(sr.getSwAccession());
                        if (newSR != null && newSR.givesPermission(registration)) {
                            set.add(newSR);
                        } else if (newSR == null) {
                            Log.info("Could not be found " + sr);
                        }
                    }
                    if (processing.getSequencerRuns() == null) {
                        processing.setSequencerRuns(set);
                    } else {
                        processing.getSequencerRuns().addAll(set);
                    }
                }
                if (p.getStudies() != null) {
                    HashSet<Study> set = new HashSet<Study>();
                    StudyService srs = BeanFactory.getStudyServiceBean();
                    for (Study sr : p.getStudies()) {
                        Study newS = srs.findBySWAccession(sr.getSwAccession());
                        if (newS != null && newS.givesPermission(registration)) {
                            set.add(newS);
                        } else if (newS == null) {
                            Log.info("Could not be found " + sr);
                        }
                    }
                    if (processing.getSequencerRuns() == null) {
                        processing.setStudies(set);
                    } else {
                        processing.getStudies().addAll(set);
                    }
                }

                if (p.getChildren() != null || p.getParents() != null) {
                    HashSet<Processing> childSet = new HashSet<Processing>();
                    for (Processing proc : p.getChildren()) {
                        Processing newProc = ps.findBySWAccession(proc.getSwAccession());
                        if (newProc != null && newProc.givesPermission(registration)) {
                            childSet.add(newProc);
                        } else if (newProc == null) {
                            Log.info("Could not be found " + proc);
                        }
                    }
                    if (processing.getChildren() == null) {
                        processing.setChildren(childSet);
                    } else {
                        processing.getChildren().addAll(childSet);
                    }
                    HashSet<Processing> parentSet = new HashSet<Processing>();
                    for (Processing proc : p.getParents()) {
                        Processing newProc = ps.findBySWAccession(proc.getSwAccession());
                        if (newProc != null && newProc.givesPermission(registration)) {
                            parentSet.add(newProc);
                        } else if (newProc == null) {
                            Log.info("Could not be found " + proc);
                        }
                    }
                    if (processing.getParents() == null) {
                        processing.setParents(parentSet);
                    } else {
                        processing.getParents().addAll(parentSet);
                    }
                }

                if (p.getWorkflowRun() != null
                        && (processing.getWorkflowRun() == null
                        || p.getWorkflowRun().getSwAccession() != processing.getWorkflowRun().getSwAccession())) {
                    WorkflowRunService wrs = BeanFactory.getWorkflowRunServiceBean();
                    WorkflowRun newWr = wrs.findBySWAccession(p.getWorkflowRun().getSwAccession());
                    if (newWr != null && newWr.givesPermission(registration)) {
                        processing.setWorkflowRun(newWr);
                    } else if (newWr == null) {
                        Log.info("Could not be found " + p.getWorkflowRun());
                    }
                }


                if (p.getWorkflowRunByAncestorWorkflowRunId() != null
                        && (processing.getWorkflowRunByAncestorWorkflowRunId() == null
                        || p.getWorkflowRunByAncestorWorkflowRunId().getSwAccession() != processing.getWorkflowRunByAncestorWorkflowRunId().getSwAccession())) {
                    WorkflowRunService wrs = BeanFactory.getWorkflowRunServiceBean();
                    WorkflowRun newWr = wrs.findBySWAccession(p.getWorkflowRunByAncestorWorkflowRunId().getSwAccession());
                    if (newWr != null && newWr.givesPermission(registration)) {
                        processing.setWorkflowRunByAncestorWorkflowRunId(newWr);
                    } else if (newWr == null) {
                        Log.info("Could not be found " + p.getWorkflowRunByAncestorWorkflowRunId());
                    }
                }

		if (p.getProcessingAttributes() != null && !p.getProcessingAttributes().isEmpty()) {
                    //SEQWARE-1577 - AttributeAnnotator cascades deletes when annotating
                    this.mergeAttributes(processing.getProcessingAttributes(), p.getProcessingAttributes(), processing);
                }
                ps.update(registration, processing);

                //Direct DB calls
                if (p.getFiles() != null) {
                    addNewFiles(p);
                }
                if (p.getIUS() != null) {

                    addNewIUSes(p);
                }
                if (p.getLanes() != null) {
                    addNewLanes(p);
                }
                if (p.getSequencerRuns() != null) {

                    addNewSequencerRuns(p);
                }
                if (p.getStudies() != null) {
                    addNewStudies(p);
                }
                if (p.getExperiments() != null) {
                    addNewExperiments(p);
                }
                if (p.getSamples() != null) {
                    addNewSamples(p);
                }
                if (p.getChildren() != null || p.getParents() != null) {
                    addNewRelationships(p);
                }

                if (p.getWorkflowRun() != null) {
                    MetadataDB mdb = DBAccess.get();
                    try{
                    ReturnValue ret = mdb.update_processing_workflow_run(
                            p.getProcessingId(),
                            p.getWorkflowRun().getSwAccession());
                    if (ret.getExitStatus() != ReturnValue.SUCCESS) {
                        throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
                                "Error in Update Processing Workflow Run with error  " + ret.getExitStatus());
                    }
                    } finally{
                        DBAccess.close();
                    }
                }

                if (p.getWorkflowRunByAncestorWorkflowRunId() != null) {
                    MetadataDB mdb = DBAccess.get();
                    try{
                    mdb.add_workflow_run_ancestor(
                            p.getWorkflowRunByAncestorWorkflowRunId().getSwAccession(),
                            p.getProcessingId());
                    } finally{
                        DBAccess.close();
                    }

                }



                ReturnValue newProcessing = Processing.clone(p), ret;
                MetadataDB mdb = DBAccess.get();
                try{
                ret = DBAccess.get().update_processing_event(p.getProcessingId(), newProcessing);
                } finally{
                    DBAccess.close();
                }
                if (ret.getExitStatus() != ReturnValue.SUCCESS) {
                    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Updating the Processing failed with error " + ret.getExitStatus());
                }

                Hibernate3DtoCopier copier = new Hibernate3DtoCopier();
                Document line = XmlTools.marshalToDocument(jo, copier.hibernate2dto(processing));

                toreturn = XmlTools.getRepresentation(line);
                getResponse().setEntity(toreturn);
                getResponse().setStatus(Status.SUCCESS_CREATED);
            } catch (SecurityException e) {
                getResponse().setStatus(Status.CLIENT_ERROR_FORBIDDEN, e);
            } catch (Exception e) {
                e.printStackTrace();
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
            } finally {
                DBAccess.close();
            }
        } else {
            throw new ResourceException(Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);
        }
        return toreturn;
    }

    public static class IntListByName implements ResultSetHandler<List<Integer>>{
      private final String col;
      public IntListByName(String col){
        this.col = col;
      }
      @Override
      public List<Integer> handle(ResultSet rs) throws SQLException {
        List<Integer> ids = new ArrayList<Integer>();
        while (rs.next()){
          ids.add(rs.getInt(col));
        }
        return ids;
      }
    }
    
    private void addNewFiles(Processing p) throws SQLException, ResourceException {
        Log.debug("Starting addNewFiles() with " + p.toString());
        Set<Integer> newFiles = new HashSet<Integer>();
        for (File file : p.getFiles()) {
            newFiles.add(file.getFileId());
        }
        Log.debug("Adding " + newFiles.size() + " files");
        
        MetadataDB mdb = null;
        try{
        
        String query = "SELECT file_id "
                + "FROM processing_files "
                + "WHERE processing_id = " + p.getProcessingId();
        Log.debug("Executing query: " + query);
        mdb = DBAccess.get();
        List<Integer> fileIDs = mdb.executeQuery(query, new IntListByName("file_id"));
        
        for (int fileID : fileIDs) {
            newFiles.remove(fileID);
        } 
        } finally{
            DBAccess.close();
        }
        for (int fileID : newFiles) {
            Log.debug("Linking " + fileID + " to " + p.getSwAccession());
            DBAccess.get().linkProcessingAndFile(p.getProcessingId(), fileID);
        }
    }

    private void addNewIUSes(Processing p) throws SQLException, ResourceException {
        Log.debug("Starting addNewIUS() with " + p.toString());
        Set<Integer> newIUSswa = new HashSet<Integer>();
        for (IUS ius : p.getIUS()) {
            newIUSswa.add(ius.getSwAccession());
        }
        
        MetadataDB mdb = DBAccess.get();
        try{

        List<Integer> swas = mdb.executeQuery(
                "SELECT i.sw_accession "
                + "FROM processing_ius pi, ius i "
                + "WHERE pi.ius_id = i.ius_id "
                + "AND pi.processing_id = " + p.getProcessingId(),
                new IntListByName("sw_accession"));
        
        for (int swa : swas) {
            newIUSswa.remove(swa);
        }
        
        } finally{
            DBAccess.close();
        }
        
        
        for (int swa : newIUSswa) {
            boolean toReturn = DBAccess.get().linkAccessionAndParent(swa, p.getProcessingId());
            if (!toReturn) {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Error " + toReturn + " while linking new iuses: SWID not found: " + swa);
            }
        }
    }

    private void addNewLanes(Processing p) throws SQLException, ResourceException {
        Log.debug("Starting addNewLanes() with " + p.toString());
        Set<Integer> newLane = new HashSet<Integer>();
        for (Lane lane : p.getLanes()) {
            newLane.add(lane.getSwAccession());
        }

        MetadataDB mdb = DBAccess.get();
        try{
        List<Integer> swas = mdb.executeQuery(
                "SELECT l.sw_accession "
                + "FROM processing_lanes pl, lane l "
                + "WHERE pl.lane_id = l.lane_id "
                + "AND pl.processing_id = " + p.getProcessingId(),
                new IntListByName("sw_accession"));
        for (int swa : swas) {
            newLane.remove(swa);
        }
        } finally{
            DBAccess.close();
        }
        
        for (int laneID : newLane) {
            boolean toReturn = DBAccess.get().linkAccessionAndParent(laneID, p.getProcessingId());
            if (!toReturn) {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Error  " + toReturn + " while adding new lanes: SWID not found: " + laneID);
            }
        }
    }

    private void addNewSequencerRuns(Processing p) throws SQLException, ResourceException {
        Log.debug("Starting addNewSequencerRuns() with " + p.toString());
        Set<Integer> newObj = new HashSet<Integer>();
        for (SequencerRun obj : p.getSequencerRuns()) {
            newObj.add(obj.getSwAccession());
        }

        MetadataDB mdb = DBAccess.get();
        try{
        List<Integer> swas = mdb.executeQuery(
                "SELECT o.sw_accession "
                + "FROM processing_sequencer_runs po, sequencer_run o "
                + "WHERE po.sequencer_run_id = o.sequencer_run_id "
                + "AND po.processing_id = " + p.getProcessingId(),
                new IntListByName("sw_accession"));
        for (int swa : swas) {
            newObj.remove(swa);
        }      
        } finally{
            DBAccess.close();
        }
        
        
        for (int objID : newObj) {
            boolean toReturn = DBAccess.get().linkAccessionAndParent(objID, p.getProcessingId());
            if (!toReturn) {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Error  " + toReturn + " while adding new sequencer runs: SWID not found: " + objID);
            }
        }
    }

    private void addNewStudies(Processing p) throws SQLException, ResourceException {
        Log.debug("Starting addNewStudies() with " + p.toString());
        Set<Integer> newObj = new HashSet<Integer>();
        for (Study obj : p.getStudies()) {
            newObj.add(obj.getSwAccession());
        }

        MetadataDB mdb = DBAccess.get();
        try{
        List<Integer> swas = mdb.executeQuery(
                "SELECT o.sw_accession "
                + "FROM processing_studies po, study o "
                + "WHERE po.study_id = o.study_id "
                + "AND po.processing_id = " + p.getProcessingId(),
                new IntListByName("sw_accession"));
        for (int swa : swas) {
            newObj.remove(swa);
        }  
        } finally{
            DBAccess.close();
        }
        
        for (int objID : newObj) {
            boolean toReturn = DBAccess.get().linkAccessionAndParent(objID, p.getProcessingId());
            if (!toReturn) {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Error  " + toReturn + " while adding new studies: SWID not found: " + objID);
            }
        }
    }

    private void addNewExperiments(Processing p) throws SQLException, ResourceException {
        Log.debug("Starting addNewExperiments() with " + p.toString());
        Set<Integer> newObj = new HashSet<Integer>();
        for (Experiment obj : p.getExperiments()) {
            newObj.add(obj.getSwAccession());
        }

        MetadataDB mdb = DBAccess.get();
        try{
        List<Integer> swas = mdb.executeQuery(
                "SELECT o.sw_accession "
                + "FROM processing_experiments po, experiment o "
                + "WHERE po.experiment_id = o.experiment_id "
                + "AND po.processing_id = " + p.getProcessingId(),
                new IntListByName("sw_accession"));
        for (int swa : swas) {
            newObj.remove(swa);
        }               
        } finally{
            DBAccess.close();
        }
        
        for (int objID : newObj) {
            boolean toReturn = DBAccess.get().linkAccessionAndParent(objID, p.getProcessingId());
            if (!toReturn) {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Error  " + toReturn + " while adding new experiments: SWID not found: " + objID);
            }
        }
    }

    private void addNewSamples(Processing p) throws SQLException, ResourceException {
        Log.debug("Starting addNewSamples() with " + p.toString());
        Set<Integer> newObj = new HashSet<Integer>();
        for (Sample obj : p.getSamples()) {
            newObj.add(obj.getSwAccession());
        }

        MetadataDB mdb = DBAccess.get();
        try{
        List<Integer> swas = DBAccess.get().executeQuery(
                "SELECT o.sw_accession "
                + "FROM processing_samples po, sample o "
                + "WHERE po.sample_id = o.sample_id "
                + "AND po.processing_id = " + p.getProcessingId(),
                new IntListByName("sw_accession"));
        for (int swa : swas) {
            newObj.remove(swa);
        }              
        } finally{
            DBAccess.close();
        }
        
        for (int objID : newObj) {
            boolean toReturn = DBAccess.get().linkAccessionAndParent(objID, p.getProcessingId());
            if (!toReturn) {
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Error  " + toReturn + " while adding new samples: SWID not found: " + objID);
            }
        }
    }

    private void addNewRelationships(Processing p) throws SQLException {
        Log.debug("Starting addNewRelationships() with " + p.toString());
        Set<Processing> children = p.getChildren();
        Set<Processing> parents = p.getParents();

        //parents
        Set<Integer> newParents = new HashSet<Integer>();
        for (Processing pr : parents) {
            newParents.add(pr.getProcessingId());
        }

        MetadataDB mdb = DBAccess.get();
        try{
        List<Integer> parentIds = mdb.executeQuery(
                "SELECT parent_id "
                + "FROM processing_relationship "
                + "WHERE child_id = " + p.getProcessingId(),
                new IntListByName("parent_id"));

        for (int parent : parentIds) {
            newParents.remove(parent);
        }
              
        } finally{
            DBAccess.close();
        }

//children
        Set<Integer> newChildren = new HashSet<Integer>();
        for (Processing pr : children) {
            newChildren.add(pr.getProcessingId());
        }

        mdb = DBAccess.get();
        try{
        List<Integer> childIds = mdb.executeQuery(
                "SELECT child_id "
                + "FROM processing_relationship "
                + "WHERE parent_id = " + p.getProcessingId(),
                new IntListByName("child_id"));

        for (int child : childIds) {
            newParents.remove(child);
        }          
        } finally{
            DBAccess.close();
        }

        //make simple arrays
        int[] parentArr = new int[newParents.size()];

        int i = 0;
        for (Iterator<Integer> iterator = newParents.iterator(); iterator.hasNext(); i++) {
            parentArr[i] = iterator.next();
        }

        int[] childArr = new int[newChildren.size()];

        i = 0;
        for (Iterator<Integer> iterator = newChildren.iterator(); iterator.hasNext(); i++) {
            childArr[i] = iterator.next();
        }

        //finally do something about it
        mdb = DBAccess.get();
        try{
            ReturnValue ret = mdb.associate_processing_event_with_parents_and_child(p.getProcessingId(), parentArr, childArr);
            if (ret.getExitStatus() != ReturnValue.SUCCESS) {
                throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Failed while adding parents and children with error " + ret.getExitStatus());
            }
        } finally{
            DBAccess.close();
        }  

    }

}
