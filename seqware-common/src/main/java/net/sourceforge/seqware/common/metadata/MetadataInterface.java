package net.sourceforge.seqware.common.metadata;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.common.model.SequencerRunAttribute;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyAttribute;
import net.sourceforge.seqware.common.model.StudyType;
import net.sourceforge.seqware.common.model.WorkflowAttribute;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunAttribute;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;

public interface MetadataInterface {

  ReturnValue init(String database, String username, String password); // Open
                                                                       // connection
                                                                       // to
                                                                       // meta
                                                                       // store

  ReturnValue clean_up(); // Close out connection to Meta store

  int mapProcessingIdToAccession(int processingId);

  public ReturnValue addStudy(String title, String description, String accession, StudyType studyType,
      String centerName, String centerProjectName, Integer studyTypeId);

  public ReturnValue addExperiment(Integer studySwAccession, Integer platformId, String description, String title);

  public ReturnValue addSample(Integer experimentAccession, Integer organismId, String description, String title);

  ReturnValue add_empty_processing_event(int parentIDs[]); // Return Processing
                                                           // ID of record just
                                                           // created,
                                                           // deprecated use
                                                           // add_empty_processing_event_by_parent_accession

  ReturnValue add_empty_processing_event_by_parent_accession(int parentAccessions[]);

  ReturnValue add_task_group(int parentIDs[], int[] childIDs, String algorithm, String description); // Return
                                                                                                     // Processing
                                                                                                     // ID
                                                                                                     // of
                                                                                                     // record
                                                                                                     // just
                                                                                                     // created

  ReturnValue processing_event_to_task_group(int processingID, int parentIDs[], int[] childIDs, String algorithm,
      String description); // Return Processing ID of record just created

  ReturnValue update_processing_event(int processingID, ReturnValue retval);

  ReturnValue update_processing_status(int processingID, String status);

  ReturnValue associate_processing_event_with_parents_and_child(int processingID, int[] parentIDs, int[] childIDs);

  ArrayList<String> fix_file_paths(String prefix, ArrayList<String> files); // this
                                                                            // is
                                                                            // going
                                                                            // to
                                                                            // take
                                                                            // relative
                                                                            // path
                                                                            // from
                                                                            // run
                                                                            // and
                                                                            // fix
                                                                            // them
                                                                            // to
                                                                            // the
                                                                            // root
                                                                            // where
                                                                            // file
                                                                            // are
                                                                            // staged
                                                                            // back
                                                                            // to

  int add_workflow_run(int workflowAccession);

  ReturnValue update_processing_workflow_run(int processingID, int workflowRunID);

  void add_workflow_run_ancestor(int workflowRunAccession, int processingId);

  int get_workflow_run_accession(int workflowRunId);

  int get_workflow_run_id(int workflowRunAccession);

  WorkflowRun getWorkflowRun(int workflowRunAccession);

  Map<String, String> get_workflow_info(int workflowAccession);

  boolean linkWorkflowRunAndParent(int workflowRunId, int parentAccession) throws SQLException;

  ReturnValue update_workflow_run(int workflowRunId, String pegasusCmd, String workflowTemplate, String status,
      String statusCmd, String workingDirectory, String dax, String ini, String host, int currStep, int totalSteps,
      String stdErr, String stdOut);

  List<ReturnValue> findFilesAssociatedWithASample(String sampleName);

  ReturnValue addWorkflow(String name, String version, String description, String baseCommand, String configFile,
      String templateFile, String provisionDir, boolean storeProvisionDir, String archiveZip, boolean storeArchiveZip);

  ReturnValue updateWorkflow(int workflowId, String permanentBundleLocation);

  String listInstalledWorkflows();

  String listInstalledWorkflowParams(String workflowAccession);

  public int getWorkflowAccession(String name, String version);

  public List<ReturnValue> findFilesAssociatedWithAStudy(String studyName);

  public ReturnValue saveFileForIus(int workflowRunId, int iusAccession, FileMetadata file);

  Boolean isDuplicateFile(String filepath);

  public List<ReturnValue> findFilesAssociatedWithASequencerRun(String sequencerRunName);

  public List<WorkflowRun> getWorkflowRunsByStatus(String status);

  public List<WorkflowRun> getWorkflowRunsByHost(String host);

  public WorkflowRun getWorkflowRunWithWorkflow(String workflowRunAccession);

  public List<Study> getAllStudies();

  public String getSequencerRunReport();

  void annotateIUS(int laneSWID, IUSAttribute iusAtt, Boolean skip);

  void annotateIUS(int laneSWID, Set<IUSAttribute> iusAtts);

  void annotateLane(int laneSWID, LaneAttribute laneAtt, Boolean skip);

  void annotateLane(int laneSWID, Set<LaneAttribute> laneAtts);

  void annotateSequencerRun(int sequencerRunSWID, SequencerRunAttribute sequencerRunAtt, Boolean skip);

  void annotateSequencerRun(int sequencerRunSWID, Set<SequencerRunAttribute> sequencerRunAtts);

  void annotateExperiment(int experimentSWID, ExperimentAttribute att, Boolean skip);

  void annotateExperiment(int experimentSWID, Set<ExperimentAttribute> atts);

  void annotateProcessing(int processingSWID, ProcessingAttribute att, Boolean skip);

  void annotateProcessing(int processingSWID, Set<ProcessingAttribute> atts);

  void annotateSample(int sampleSWID, SampleAttribute att, Boolean skip);

  void annotateSample(int sampleSWID, Set<SampleAttribute> atts);

  void annotateStudy(int studySWID, StudyAttribute att, Boolean skip);

  void annotateStudy(int studySWID, Set<StudyAttribute> atts);

  void annotateWorkflow(int workflowSWID, WorkflowAttribute att, Boolean skip);

  void annotateWorkflow(int workflowSWID, Set<WorkflowAttribute> atts);

  void annotateWorkflowRun(int workflowrunSWID, WorkflowRunAttribute att, Boolean skip);

  void annotateWorkflowRun(int workflowSWID, Set<WorkflowRunAttribute> atts);

  // void annotateFile(int fileSWID, FileAttribute att, Boolean skip);

  public String getWorkflowRunReport(int workflowRunSWID);

  public String getWorkflowRunReport(int workflowSWID, Date earliestDate, Date latestDate);

  public String getWorkflowRunReport(Date earliestDate, Date latestDate);

  public net.sourceforge.seqware.common.model.File getFile(int swAccession);

  public SortedSet<WorkflowParam> getWorkflowParams(String swAccession);
}