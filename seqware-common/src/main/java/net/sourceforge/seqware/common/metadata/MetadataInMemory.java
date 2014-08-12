/*
 * Copyright (C) 2014 SeqWare
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

package net.sourceforge.seqware.common.metadata;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import io.seqware.common.model.ProcessingStatus;
import io.seqware.common.model.SequencerRunStatus;
import io.seqware.common.model.WorkflowRunStatus;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.ExperimentLibraryDesign;
import net.sourceforge.seqware.common.model.ExperimentSpotDesign;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileAttribute;
import net.sourceforge.seqware.common.model.FileProvenanceParam;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.LibrarySelection;
import net.sourceforge.seqware.common.model.LibrarySource;
import net.sourceforge.seqware.common.model.LibraryStrategy;
import net.sourceforge.seqware.common.model.Organism;
import net.sourceforge.seqware.common.model.ParentAccessionModel;
import net.sourceforge.seqware.common.model.Platform;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunAttribute;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyAttribute;
import net.sourceforge.seqware.common.model.StudyType;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowAttribute;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunAttribute;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;

/**
 * This stores some metadata in memory as an exploration of running workflows without a running database or web service.
 * 
 * Data will only be stored while this VM is still active and cannot be accessed by other clients.
 * 
 * @author dyuen
 */
public class MetadataInMemory implements Metadata {

    /**
     * Stores SWID/id -> Model object. Unlike the postgres database, we re-use the sw accession as the id
     */
    private static final Table<Integer, Class, Object> store = HashBasedTable.create();

    /**
     * Not really thread-safe, why does Guava not have a synchronized wrapper?
     * 
     * @return the store
     */
    private synchronized static Table<Integer, Class, Object> getStore() {
        return store;
    }

    @Override
    public ReturnValue clean_up() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int mapProcessingIdToAccession(int processingId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue addStudy(String title, String description, String centerName, String centerProjectName, Integer studyTypeId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue addExperiment(Integer studySwAccession, Integer platformId, String description, String title,
            Integer experimentLibraryDesignId, Integer experimentSpotDesignId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue addSample(Integer experimentAccession, Integer parentSampleAccession, Integer organismId, String description,
            String title) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue addSequencerRun(Integer platformAccession, String name, String description, boolean pairdEnd, boolean skip,
            String filePath, SequencerRunStatus status) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue addLane(Integer sequencerRunAccession, Integer studyTypeId, Integer libraryStrategyId, Integer librarySelectionId,
            Integer librarySourceId, String name, String description, String cycleDescriptor, boolean skip, Integer laneNumber) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue addIUS(Integer laneAccession, Integer sampleAccession, String name, String description, String barcode, boolean skip) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Platform> getPlatforms() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Experiment getExperiment(int swAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ExperimentLibraryDesign> getExperimentLibraryDesigns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ExperimentSpotDesignReadSpec> getExperimentSpotDesignReadSpecs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ExperimentSpotDesign> getExperimentSpotDesigns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Organism> getOrganisms() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<StudyType> getStudyTypes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<LibraryStrategy> getLibraryStrategies() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<LibrarySelection> getLibrarySelections() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<LibrarySource> getLibrarySource() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue add_empty_processing_event(int[] parentIDs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue add_empty_processing_event_by_parent_accession(int[] parentAccessions) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue add_task_group(int[] parentIDs, int[] childIDs, String algorithm, String description) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue processing_event_to_task_group(int processingID, int[] parentIDs, int[] childIDs, String algorithm,
            String description) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue update_processing_event(int processingID, ReturnValue retval) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue update_processing_status(int processingID, ProcessingStatus status) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue associate_processing_event_with_parents_and_child(int processingID, int[] parentIDs, int[] childIDs) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int add_workflow_run(int workflowAccession) {
        WorkflowRun wr = new WorkflowRun();
        wr.setSwAccession(this.getNextSwAccession());
        wr.setWorkflowRunId(wr.getSwAccession());
        wr.setCreateTimestamp(new Date());
        wr.setUpdateTimestamp(new Date());
        Workflow workflow = (Workflow) MetadataInMemory.getStore().get(workflowAccession, Workflow.class);
        wr.setWorkflow(workflow);
        MetadataInMemory.getStore().put(wr.getSwAccession(), WorkflowRun.class, wr);
        return wr.getSwAccession();
    }

    @Override
    public ReturnValue update_processing_workflow_run(int processingID, int workflowRunID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void add_workflow_run_ancestor(int workflowRunAccession, int processingId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int get_workflow_run_accession(int workflowRunId) {
        return workflowRunId;
    }

    @Override
    public int get_workflow_run_id(int workflowRunAccession) {
        return workflowRunAccession;
    }

    @Override
    public WorkflowRun getWorkflowRun(int workflowRunAccession) {
        return (WorkflowRun) MetadataInMemory.getStore().get(workflowRunAccession, WorkflowRun.class);
    }

    @Override
    public List<WorkflowRun> getWorkflowRunsAssociatedWithInputFiles(List<Integer> fileAccessions) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<WorkflowRun> getWorkflowRunsAssociatedWithInputFiles(List<Integer> fileAccessions, List<Integer> workflowAccessions) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<WorkflowRun> getWorkflowRunsAssociatedWithFiles(List<Integer> fileAccessions, String search_type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, String> get_workflow_info(int workflowAccession) {
        Workflow workflow = (Workflow) getStore().get(workflowAccession, Workflow.class);
        Map<String, String> convertWorkflowToMap = MetadataWS.convertWorkflowToMap(workflow);
        return convertWorkflowToMap;
    }

    @Override
    public boolean linkWorkflowRunAndParent(int workflowRunId, int parentAccession) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReturnValue update_workflow_run(int workflowRunId, String pegasusCmd, String workflowTemplate, WorkflowRunStatus status,
            String statusCmd, String workingDirectory, String dax, String ini, String host, String stdErr, String stdOut,
            String workflowEngine, Set<Integer> inputFiles) {
        WorkflowRun workflowRun = (WorkflowRun) MetadataInMemory.getStore().get(workflowRunId, WorkflowRun.class);
        MetadataWS.convertParamsToWorkflowRun(workflowRun, pegasusCmd, workflowTemplate, status, statusCmd, workingDirectory, dax, ini,
                host, stdErr, stdOut, workflowEngine, inputFiles);
        ReturnValue returnValue = new ReturnValue();
        returnValue.setReturnValue(workflowRun.getSwAccession());
        return returnValue;
    }

    @Override
    public void updateWorkflowRun(WorkflowRun wr) {
        MetadataInMemory.getStore().put(wr.getSwAccession(), WorkflowRun.class, wr);
    }

    @Override
    public ReturnValue addWorkflow(String name, String version, String description, String baseCommand, String configFile,
            String templateFile, String provisionDir, boolean storeProvisionDir, String archiveZip, boolean storeArchiveZip,
            String workflowClass, String workflowType, String workflowEngine) {
        int nextKey = getNextSwAccession();
        Workflow workflow = MetadataWS.convertParamsToWorkflow(baseCommand, name, description, version, configFile, storeProvisionDir,
                provisionDir, templateFile, storeArchiveZip, archiveZip, workflowClass, workflowType, workflowEngine);
        workflow.setCreateTimestamp(new Date());
        workflow.setUpdateTimestamp(new Date());
        workflow.setSwAccession(nextKey);
        MetadataInMemory.getStore().put(nextKey, Workflow.class, workflow);
        ReturnValue returnValue = new ReturnValue();
        Log.stdout("Added '" + workflow.getName() + "' (SWID: " + workflow.getSwAccession() + ")");
        returnValue.setAttribute("sw_accession", String.valueOf(workflow.getSwAccession()));
        returnValue.setReturnValue(workflow.getSwAccession());

        HashMap<String, Map<String, String>> hm = MetadataWS.convertIniToMap(configFile, provisionDir);
        TreeSet<WorkflowParam> setOfDefaultParams = new TreeSet<>();
        for (Entry<String, Map<String, String>> e : hm.entrySet()) {
            WorkflowParam workflowParam = MetadataWS.convertMapToWorkflowParam(e.getValue(), workflow);
            setOfDefaultParams.add(workflowParam);
        }
        workflow.setWorkflowParams(setOfDefaultParams);
        return returnValue;
    }

    private synchronized int getNextSwAccession() {
        int nextKey = MetadataInMemory.getStore().rowKeySet().size() + 1;
        return nextKey;
    }

    @Override
    public ReturnValue updateWorkflow(int workflowId, String permanentBundleLocation) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String listInstalledWorkflows() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String listInstalledWorkflowParams(String workflowAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getWorkflowAccession(String name, String version) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fileProvenanceReportTrigger() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void fileProvenanceReport(Map<FileProvenanceParam, List<String>> params, Writer out) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Map<String, String>> fileProvenanceReport(Map<FileProvenanceParam, List<String>> params) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Boolean isDuplicateFile(String filepath) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<WorkflowRun> getWorkflowRunsByStatus(WorkflowRunStatus status) {
        Map<Integer, Object> column = MetadataInMemory.getStore().column(WorkflowRun.class);
        List<WorkflowRun> returnList = new ArrayList<>();
        for (Entry<Integer, Object> e : column.entrySet()) {
            WorkflowRun r = (WorkflowRun) e.getValue();
            if (r.getStatus() == status) {
                returnList.add(r);
            }
        }
        return returnList;
    }

    @Override
    public WorkflowRun getWorkflowRunWithWorkflow(String workflowRunAccession) {
        return (WorkflowRun) MetadataInMemory.getStore().get(Integer.valueOf(workflowRunAccession), WorkflowRun.class);
    }

    @Override
    public List<Study> getAllStudies() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getSequencerRunReport() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateFile(int laneSWID, FileAttribute iusAtt, Boolean skip) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateFile(int fileSWID, Set<FileAttribute> iusAtts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateIUS(int laneSWID, IUSAttribute iusAtt, Boolean skip) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateIUS(int laneSWID, Set<IUSAttribute> iusAtts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateLane(int laneSWID, LaneAttribute laneAtt, Boolean skip) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateLane(int laneSWID, Set<LaneAttribute> laneAtts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateSequencerRun(int sequencerRunSWID, SequencerRunAttribute sequencerRunAtt, Boolean skip) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateSequencerRun(int sequencerRunSWID, Set<SequencerRunAttribute> sequencerRunAtts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateExperiment(int experimentSWID, ExperimentAttribute att, Boolean skip) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateExperiment(int experimentSWID, Set<ExperimentAttribute> atts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateProcessing(int processingSWID, ProcessingAttribute att, Boolean skip) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateProcessing(int processingSWID, Set<ProcessingAttribute> atts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateSample(int sampleSWID, SampleAttribute att, Boolean skip) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateSample(int sampleSWID, Set<SampleAttribute> atts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateStudy(int studySWID, StudyAttribute att, Boolean skip) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateStudy(int studySWID, Set<StudyAttribute> atts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateWorkflow(int workflowSWID, WorkflowAttribute att, Boolean skip) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateWorkflow(int workflowSWID, Set<WorkflowAttribute> atts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateWorkflowRun(int workflowrunSWID, WorkflowRunAttribute att, Boolean skip) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void annotateWorkflowRun(int workflowSWID, Set<WorkflowRunAttribute> atts) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getWorkflowRunReport(int workflowRunSWID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getWorkflowRunReportStdErr(int workflowRunSWID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getWorkflowRunReportStdOut(int workflowRunSWID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getWorkflowRunReport(int workflowSWID, Date earliestDate, Date latestDate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getWorkflowRunReport(Date earliestDate, Date latestDate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public File getFile(int swAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SortedSet<WorkflowParam> getWorkflowParams(String swAccession) {
        Workflow workflow = (Workflow) MetadataInMemory.getStore().get(Integer.valueOf(swAccession), Workflow.class);
        return workflow.getWorkflowParams();
    }

    @Override
    public String getProcessingRelations(String swAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Workflow getWorkflow(int workflowAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<SequencerRun> getAllSequencerRuns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Lane getLane(int laneAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Processing getProcessing(int processingAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SequencerRun getSequencerRun(int sequencerRunAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Lane> getLanesFrom(int sequencerRunAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<IUS> getIUSFrom(int laneOrSampleAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Experiment> getExperimentsFrom(int studyAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Sample> getSamplesFrom(int experimentAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Sample> getChildSamplesFrom(int parentSampleAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Sample> getParentSamplesFrom(int childSampleAccession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ParentAccessionModel> getViaParentAccessions(int[] potentialParentAccessions) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Object> getViaAccessions(int[] potentialAccessions) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Study getStudyByName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Sample> getSampleByName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public SequencerRun getSequencerRunByName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
