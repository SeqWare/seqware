package net.sourceforge.seqware.common.metadata;

import io.seqware.common.model.ProcessingStatus;
import io.seqware.common.model.SequencerRunStatus;
import io.seqware.common.model.WorkflowRunStatus;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.ExperimentLibraryDesign;
import net.sourceforge.seqware.common.model.ExperimentSpotDesign;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * MetadataNoConnection class.
 * </p>
 *
 * @author boconnor@oicr.on.ca
 *
 *         This Metadata object essentially does nothing. It returns null, 0, or a successful ReturnValue for all methods. This lets us do
 *         absolutely no metadata writeback with objects that expect a validate Metadata object. Keep in mind this may break code that
 *         assumes it's talking to a Database- or WebService-backed Metadata object!
 * @version $Id: $Id
 */
public class MetadataNoConnection implements Metadata {

    private final Logger logger = LoggerFactory.getLogger(MetadataNoConnection.class);

    @Override
    public void fileProvenanceReport(Map<FileProvenanceParam, List<String>> params, Writer out) {
        try {
            out.write("Last Modified\tStudy Title\tStudy SWID\tStudy Attributes\tExperiment Name\tExperiment SWID\tExperiment Attributes\tParent Sample Name\tParent Sample SWID\tParent Sample Attributes\tSample Name\tSample SWID\tSample Attributes\tSequencer Run Name\tSequencer Run SWID\tSequencer Run Attributes\tLane Name\tLane Number\tLane SWID\tLane Attributes\tIUS Tag\tIUS SWID\tIUS Attributes\tWorkflow Name\tWorkflow Version\tWorkflow SWID\tWorkflow Run Name\tWorkflow Run Status\tWorkflow Run SWID\tProcessing Algorithm\tProcessing SWID\tProcessing Attributes\tFile Meta-Type\tFile SWID\tFile Path\tSkip");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, String>> fileProvenanceReport(Map<FileProvenanceParam, List<String>> params) {
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue addStudy(String title, String description, String centerName, String centerProjectName, Integer studyTypeId) {
        logger.info("No metadata connection");
        return (new ReturnValue(ReturnValue.SUCCESS));
    }

    /**
     * {@inheritDoc}
     *
     * @param experimentLibraryDesignId
     *            the value of experimentLibraryDesignId
     * @param experimentSpotDesignId
     *            the value of experimentSpotDesignId
     */
    @Override
    public ReturnValue addExperiment(Integer studySwAccession, Integer platformId, String description, String title,
            Integer experimentLibraryDesignId, Integer experimentSpotDesignId) {
        logger.info("No metadata connection");
        return (new ReturnValue(ReturnValue.SUCCESS));
    }

    /**
     * {@inheritDoc}
     *
     * @param parentSampleAccession
     */
    @Override
    public ReturnValue addSample(Integer experimentAccession, Integer parentSampleAccession, Integer organismId, String description,
            String title) {
        logger.info("No metadata connection");
        return (new ReturnValue(ReturnValue.SUCCESS));
    }

    @Override
    public ReturnValue addSequencerRun(Integer platformAccession, String name, String description, boolean pairdEnd, boolean skip,
            String filePath, SequencerRunStatus status) {
        logger.info("No metadata connection");
        return (new ReturnValue(ReturnValue.SUCCESS));
    }

    @Override
    public ReturnValue addLane(Integer sequencerRunAccession, Integer studyTypeId, Integer libraryStrategyId, Integer librarySelectionId,
            Integer librarySourceId, String name, String description, String cycleDescriptor, boolean skip, Integer laneNumber) {
        logger.info("No metadata connection");
        return (new ReturnValue(ReturnValue.SUCCESS));
    }

    @Override
    public ReturnValue addIUS(Integer laneAccession, Integer sampleAccession, String name, String description, String barcode, boolean skip) {
        logger.info("No metadata connection");
        return (new ReturnValue(ReturnValue.SUCCESS));
    }

    @Override
    public List<Platform> getPlatforms() {
        logger.info("No metadata connection");
        return (new ArrayList<>());
    }

    @Override
    public List<Organism> getOrganisms() {
        logger.info("No metadata connection");
        return (new ArrayList<>());
    }

    @Override
    public List<StudyType> getStudyTypes() {
        logger.info("No metadata connection");
        return (new ArrayList<>());
    }

    @Override
    public List<LibraryStrategy> getLibraryStrategies() {
        logger.info("No metadata connection");
        return (new ArrayList<>());
    }

    @Override
    public List<LibrarySelection> getLibrarySelections() {
        logger.info("No metadata connection");
        return (new ArrayList<>());
    }

    @Override
    public List<LibrarySource> getLibrarySource() {
        logger.info("No metadata connection");
        return (new ArrayList<>());
    }

    // FIXME: This should all be a transaction. For now, we end up with cruft in
    // the DB if something failed.
    /*
     * FIXME: instead of taking in parentID's here, need to take in tubles to discuss the relationship. Different types of relationships:
     * match1 -> variant1 is process match -> variant is algorithm match -> match1, match2, etc is subprocess
     */
    /** {@inheritDoc} */
    @Override
    public ReturnValue add_empty_processing_event(int[] parentIDs) {
        logger.info("No metadata connection");
        ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
        finished.setExitStatus(ReturnValue.SUCCESS);
        return finished;
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue add_empty_processing_event_by_parent_accession(int[] parentAccessions) {
        logger.info("No metadata connection");
        ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
        finished.setExitStatus(ReturnValue.SUCCESS);
        return finished;
    }

    /**
     * {@inheritDoc}
     *
     * This maps processing_id to sw_accession for that event.
     */
    @Override
    public int mapProcessingIdToAccession(int processingId) {
        logger.info("No metadata connection");
        return (0);
    }

    /**
     * {@inheritDoc}
     *
     * TODO: needs to support more relationship types, but will need to add to the SQL schema to support this
     */
    @Override
    public boolean linkWorkflowRunAndParent(int workflowRunId, int parentAccession) throws SQLException {
        logger.info("No metadata connection");
        return (true);
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue processing_event_to_task_group(int processingID, int parentIDs[], int[] childIDs, String algorithm,
            String description) {
        logger.info("No metadata connection");
        ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
        finished.setExitStatus(ReturnValue.SUCCESS);
        return finished;
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue add_task_group(int parentIDs[], int[] childIDs, String algorithm, String description) {
        logger.info("No metadata connection");
        ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
        finished.setExitStatus(ReturnValue.SUCCESS);
        return finished;
    }

    /** {@inheritDoc} */
    @Override
    /*
     * FIXME: this should check if association is already made, to make duplicates impossible
     */
    public ReturnValue associate_processing_event_with_parents_and_child(int processingID, int[] parentIDs, int[] childIDs) {
        logger.info("No metadata connection");
        ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
        finished.setExitStatus(ReturnValue.SUCCESS);
        return finished;
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue update_processing_status(int processingID, ProcessingStatus status) {
        logger.info("No metadata connection");
        ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
        finished.setExitStatus(ReturnValue.SUCCESS);
        return finished;

    }

    /** {@inheritDoc} */
    @Override
    public int add_workflow_run(int workflowAccession) {
        logger.info("No metadata connection");
        return (0);
    }

    /** {@inheritDoc} */
    @Override
    public int get_workflow_run_accession(int workflowRunId) {
        logger.info("No metadata connection");
        return (0);
    }

    /** {@inheritDoc} */
    @Override
    public int get_workflow_run_id(int workflowRunAccession) {
        logger.info("No metadata connection");
        return (0);
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowRun getWorkflowRun(int workflowRunAccession) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void add_workflow_run_ancestor(int workflowRunAccession, int processingId) {
        logger.info("No metadata connection");
    }

    /**
     * {@inheritDoc}
     *
     * @param workflowRunAccession
     */
    @Override
    public ReturnValue update_processing_workflow_run(int processingID, int workflowRunAccession) {
        logger.info("No metadata connection");
        ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
        finished.setExitStatus(ReturnValue.SUCCESS);
        return finished;

    }

    /**
     * {@inheritDoc}
     *
     * @param inputFiles
     */
    @Override
    public ReturnValue update_workflow_run(int workflowRunId, String pegasusCmd, String workflowTemplate, WorkflowRunStatus status,
            String statusCmd, String workingDirectory, String dax, String ini, String host, String stdErr, String stdOut,
            String workflowEngine, Set<Integer> inputFiles) {
        logger.info("No metadata connection");
        ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
        finished.setExitStatus(ReturnValue.SUCCESS);
        return finished;

    }

    @Override
    public void updateWorkflowRun(WorkflowRun wr) {
        logger.info("No metadata connection");
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue update_processing_event(int processingID, ReturnValue retval) {
        logger.info("No metadata connection");
        ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
        finished.setExitStatus(ReturnValue.SUCCESS);
        return finished;
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue clean_up() {
        logger.info("No metadata connection");
        ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
        finished.setExitStatus(ReturnValue.SUCCESS);
        return finished;
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public ReturnValue addWorkflow(String name, String version, String description, String baseCommand, String configFile,
            String templateFile, String provisionDir, boolean storeProvisionDir, String archiveZip, boolean storeArchiveZip,
            String workflowClass, String workflowType, String workflowEngine, String seqwareVersion) {
        logger.info("No metadata connection");
        ReturnValue finished = new ReturnValue(ReturnValue.PROCESSING);
        finished.setExitStatus(ReturnValue.SUCCESS);
        return finished;

    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> get_workflow_info(int workflowAccession) {
        logger.info("No metadata connection");
        HashMap<String, String> map = new HashMap<>();
        return (map);

    }

    /** {@inheritDoc} */
    @Override
    public Boolean isDuplicateFile(String filepath) {
        logger.info("No metadata connection");
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue updateWorkflow(int workflowId, String permanentBundleLocation) {
        logger.info("No metadata connection");
        return new ReturnValue();
    }

    /** {@inheritDoc} */
    @Override
    public String listInstalledWorkflows() {
        logger.info("No metadata connection");
        return "";
    }

    /** {@inheritDoc} */
    @Override
    public String listInstalledWorkflowParams(String workflowAccession) {
        logger.info("No metadata connection");
        return "";
    }

    /** {@inheritDoc} */
    @Override
    public int getWorkflowAccession(String name, String version) {
        logger.info("No metadata connection");
        return 1;
    }

    /** {@inheritDoc} */
    @Override
    public List<WorkflowRun> getWorkflowRunsByStatus(WorkflowRunStatus status) {
        logger.info("No metadata connection");
        return new ArrayList<>();
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowRun getWorkflowRunWithWorkflow(String workflowRunAccession) {
        return null;
    }

    /**
     * <p>
     * getAllStudies.
     * </p>
     *
     * @return a {@link java.util.List} object.
     */
    @Override
    public List<Study> getAllStudies() {
        logger.info("No metadata connection");
        return new ArrayList<>();
    }

    /**
     * <p>
     * getSequencerRunReport.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String getSequencerRunReport() {
        logger.info("No metadata connection");
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @param iusSWID
     */
    @Override
    public void annotateIUS(int iusSWID, IUSAttribute iusAtt, Boolean skip) {
        logger.info("No metadata connection");
    }

    /** {@inheritDoc} */
    @Override
    public void annotateLane(int laneSWID, LaneAttribute laneAtt, Boolean skip) {
        logger.info("No metadata connection");
    }

    /** {@inheritDoc} */
    @Override
    public void annotateSequencerRun(int sequencerRunSWID, SequencerRunAttribute sequencerRunAtt, Boolean skip) {
        logger.info("No metadata connection");
    }

    /** {@inheritDoc} */
    @Override
    public void annotateExperiment(int experimentSWID, ExperimentAttribute att, Boolean skip) {
        logger.info("No metadata connection");
    }

    /** {@inheritDoc} */
    @Override
    public void annotateProcessing(int processingSWID, ProcessingAttribute att, Boolean skip) {
        logger.info("No metadata connection");
    }

    /** {@inheritDoc} */
    @Override
    public void annotateSample(int sampleSWID, SampleAttribute att, Boolean skip) {
        logger.info("No metadata connection");
    }

    /** {@inheritDoc} */
    @Override
    public void annotateStudy(int studySWID, StudyAttribute att, Boolean skip) {
        logger.info("No metadata connection");
    }

    /** {@inheritDoc} */
    @Override
    public String getWorkflowRunReport(int workflowRunSWID) {
        logger.info("No metadata connection");
        return "";
    }

    /** {@inheritDoc} */
    @Override
    public String getWorkflowRunReport(int workflowSWID, Date earliestDate, Date latestDate) {
        logger.info("No metadata connection");
        return "";
    }

    /** {@inheritDoc} */
    @Override
    public String getWorkflowRunReport(Date earliestDate, Date latestDate) {
        logger.info("No metadata connection");
        return "";
    }

    /** {@inheritDoc} */
    @Override
    public net.sourceforge.seqware.common.model.File getFile(int swAccession) {
        return new net.sourceforge.seqware.common.model.File();
    }

    /** {@inheritDoc} */
    @Override
    public SortedSet<WorkflowParam> getWorkflowParams(String swAccession) {
        logger.info("No metadata connection");
        return new TreeSet<>();
    }

    /** {@inheritDoc} */
    @Override
    public void annotateWorkflow(int workflowSWID, WorkflowAttribute att, Boolean skip) {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public void annotateWorkflowRun(int workflowrunSWID, WorkflowRunAttribute att, Boolean skip) {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public void annotateIUS(int laneSWID, Set<IUSAttribute> iusAtts) {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public void annotateLane(int laneSWID, Set<LaneAttribute> laneAtts) {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public void annotateSequencerRun(int sequencerRunSWID, Set<SequencerRunAttribute> sequencerRunAtts) {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public void annotateExperiment(int experimentSWID, Set<ExperimentAttribute> atts) {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public void annotateProcessing(int processingSWID, Set<ProcessingAttribute> atts) {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public void annotateSample(int sampleSWID, Set<SampleAttribute> atts) {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public void annotateStudy(int studySWID, Set<StudyAttribute> atts) {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public void annotateWorkflow(int workflowSWID, Set<WorkflowAttribute> atts) {
        // TODO Auto-generated method stub

    }

    /** {@inheritDoc} */
    @Override
    public void annotateWorkflowRun(int workflowSWID, Set<WorkflowRunAttribute> atts) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getProcessingRelations(String swAccession) {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public String getWorkflowRunReportStdErr(int workflowRunSWID) {
        return ("");
    }

    @Override
    public String getWorkflowRunReportStdOut(int workflowRunSWID) {
        return ("");
    }

    @Override
    public Workflow getWorkflow(int workflowAccession) {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public List<SequencerRun> getAllSequencerRuns() {
        return new ArrayList<>();
    }

    @Override
    public List<Lane> getLanesFrom(int sequencerRunAccession) {
        return new ArrayList<>();
    }

    @Override
    public List<IUS> getIUSFrom(int laneOrSampleAccession) {
        return new ArrayList<>();
    }

    @Override
    public List<Experiment> getExperimentsFrom(int studyAccession) {
        return new ArrayList<>();
    }

    @Override
    public List<Sample> getSamplesFrom(int experimentAccession) {
        return new ArrayList<>();
    }

    @Override
    public List<Sample> getChildSamplesFrom(int parentSampleAccession) {
        return new ArrayList<>();
    }

    @Override
    public List<Sample> getParentSamplesFrom(int childSampleAccession) {
        return new ArrayList<>();
    }

    @Override
    public List<WorkflowRun> getWorkflowRunsAssociatedWithInputFiles(List<Integer> fileAccessions) {
        return new ArrayList<>();
    }

    @Override
    public List<WorkflowRun> getWorkflowRunsAssociatedWithInputFiles(List<Integer> fileAccessions, List<Integer> workflowAccessions) {
        return new ArrayList<>();
    }

    @Override
    public List<WorkflowRun> getWorkflowRunsAssociatedWithFiles(List<Integer> fileAccessions, String search_type) {
        return new ArrayList<>();
    }

    @Override
    public void annotateFile(int laneSWID, FileAttribute iusAtt, Boolean skip) {
        logger.info("No metadata connection");
    }

    @Override
    public void annotateFile(int fileSWID, Set<FileAttribute> iusAtts) {
        logger.info("No metadata connection");
    }

    @Override
    public Lane getLane(int laneAccession) {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public SequencerRun getSequencerRun(int sequencerRunAccession) {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public List<ExperimentLibraryDesign> getExperimentLibraryDesigns() {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public List<ExperimentSpotDesignReadSpec> getExperimentSpotDesignReadSpecs() {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public List<ExperimentSpotDesign> getExperimentSpotDesigns() {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public Experiment getExperiment(int swAccession) {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public List<ParentAccessionModel> getViaParentAccessions(int[] potentialParentAccessions) {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public List<Object> getViaAccessions(int[] accessions) {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public void fileProvenanceReportTrigger() {
        logger.info("No metadata connection");
    }

    @Override
    public List<Study> getStudyByName(String name) {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public List<Sample> getSampleByName(String name) {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public SequencerRun getSequencerRunByName(String name) {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public Processing getProcessing(int processingAccession) {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public String getWorkflowRunReport(WorkflowRunStatus status, Date earliestDate, Date latestDate) {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public String getWorkflowRunReport(Integer workflowSWID, WorkflowRunStatus status, Date earliestDate, Date latestDate) {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public List<WorkflowRun> getWorkflowRunsByStatusCmd(String statusCmd) {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public Map<String, String> getEnvironmentReport() {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public boolean checkClientServerMatchingVersion() {
        return false;
    }

    @Override
    public IUS getIUS(int swAccession) {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public Sample getSample(int swAccession) {
        logger.info("No metadata connection");
        return null;
    }

    @Override
    public Study getStudy(int swAccession) {
        logger.info("No metadata connection");
        return null;
    }
}
