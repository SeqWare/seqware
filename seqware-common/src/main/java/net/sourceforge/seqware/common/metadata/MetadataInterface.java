package net.sourceforge.seqware.common.metadata;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.LibrarySelection;
import net.sourceforge.seqware.common.model.LibrarySource;
import net.sourceforge.seqware.common.model.LibraryStrategy;
import net.sourceforge.seqware.common.model.Organism;
import net.sourceforge.seqware.common.model.Platform;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.model.Sample;
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

/**
 * <p>MetadataInterface interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface MetadataInterface {

  /**
   * <p>init.</p>
   *
   * @param database a {@link java.lang.String} object.
   * @param username a {@link java.lang.String} object.
   * @param password a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  ReturnValue init(String database, String username, String password); // Open
                                                                       // connection
                                                                       // to
                                                                       // meta
                                                                       // store

  /**
   * <p>clean_up.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  ReturnValue clean_up(); // Close out connection to Meta store

  /**
   * <p>mapProcessingIdToAccession.</p>
   *
   * @param processingId a int.
   * @return a int.
   */
  int mapProcessingIdToAccession(int processingId);

  /**
   * <p>addStudy.</p>
   *
   * @param title a {@link java.lang.String} object.
   * @param description a {@link java.lang.String} object.
   * @param accession a {@link java.lang.String} object.
   * @param studyType a {@link net.sourceforge.seqware.common.model.StudyType} object.
   * @param centerName a {@link java.lang.String} object.
   * @param centerProjectName a {@link java.lang.String} object.
   * @param studyTypeId a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public ReturnValue addStudy(String title, String description, String accession, StudyType studyType,
      String centerName, String centerProjectName, Integer studyTypeId);

  /**
   * <p>addExperiment.</p>
   *
   * @param studySwAccession a {@link java.lang.Integer} object.
   * @param platformId a {@link java.lang.Integer} object.
   * @param description a {@link java.lang.String} object.
   * @param title a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public ReturnValue addExperiment(Integer studySwAccession, Integer platformId, String description, String title);

  /**
   * <p>addSample.</p>
   *
   * @param experimentAccession a {@link java.lang.Integer} object.
   * @param organismId a {@link java.lang.Integer} object.
   * @param description a {@link java.lang.String} object.
   * @param title a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public ReturnValue addSample(Integer experimentAccession, Integer organismId, String description, String title);
  
    /**
   * <p>addSample.</p>
   *
   * @param experimentAccession a {@link java.lang.Integer} object.
   * @param organismId a {@link java.lang.Integer} object.
   * @param description a {@link java.lang.String} object.
   * @param title a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public ReturnValue addSequencerRun(Integer experimentAccession, Integer organismAccession, Integer platformAccession, String name, String description, boolean pairdEnd, boolean skip);
  
    /**
   * <p>addSample.</p>
   *
   * @param experimentAccession a {@link java.lang.Integer} object.
   * @param organismId a {@link java.lang.Integer} object.
   * @param description a {@link java.lang.String} object.
   * @param title a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public ReturnValue addLane(Integer sequencerRunAccession, Integer studyTypeId, Integer libraryStrategyId, Integer librarySelectionId, Integer librarySourceId, String name, String description, String cycleDescriptor, boolean skip);
  
    /**
   * <p>addSample.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  
  public ReturnValue addIUS(Integer laneAccession, Integer sampleAccession, String name, String description, String barcode, boolean skip);
  
  /**
   * <p>getPlatforms.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.Platform} object.
   */
  public List<Platform> getPlatforms();
  
    /**
   * <p>getOrganisms.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.Organism} object.
   */
  public List<Organism> getOrganisms();
  
 /**
   * <p>getStudyTypes.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.StudyType} object.
   */
  public List<StudyType> getStudyTypes();
  
   /**
   * <p>getLibraryStrategies.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.LibraryStrategy} object.
   */
  public List<LibraryStrategy> getLibraryStrategies();
  
  /**
   * <p>getLibrarySelections.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.LibrarySelection} object.
   */
  public List<LibrarySelection> getLibrarySelections();
  
  /**
   * <p>getLibrarySource.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.LibrarySource} object.
   */
  public List<LibrarySource> getLibrarySource();
  
  

  /**
   * <p>add_empty_processing_event.</p>
   *
   * @param parentIDs an array of int.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  ReturnValue add_empty_processing_event(int parentIDs[]); // Return Processing
                                                           // ID of record just
                                                           // created,
                                                           // deprecated use
                                                           // add_empty_processing_event_by_parent_accession

  /**
   * <p>add_empty_processing_event_by_parent_accession.</p>
   *
   * @param parentAccessions an array of int.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  ReturnValue add_empty_processing_event_by_parent_accession(int parentAccessions[]);

  /**
   * <p>add_task_group.</p>
   *
   * @param parentIDs an array of int.
   * @param childIDs an array of int.
   * @param algorithm a {@link java.lang.String} object.
   * @param description a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  ReturnValue add_task_group(int parentIDs[], int[] childIDs, String algorithm, String description); // Return
                                                                                                     // Processing
                                                                                                     // ID
                                                                                                     // of
                                                                                                     // record
                                                                                                     // just
                                                                                                     // created

  /**
   * <p>processing_event_to_task_group.</p>
   *
   * @param processingID a int.
   * @param parentIDs an array of int.
   * @param childIDs an array of int.
   * @param algorithm a {@link java.lang.String} object.
   * @param description a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  ReturnValue processing_event_to_task_group(int processingID, int parentIDs[], int[] childIDs, String algorithm,
      String description); // Return Processing ID of record just created

  /**
   * <p>update_processing_event.</p>
   *
   * @param processingID a int.
   * @param retval a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  ReturnValue update_processing_event(int processingID, ReturnValue retval);

  /**
   * <p>update_processing_status.</p>
   *
   * @param processingID a int.
   * @param status a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  ReturnValue update_processing_status(int processingID, String status);

  /**
   * <p>associate_processing_event_with_parents_and_child.</p>
   *
   * @param processingID a int.
   * @param parentIDs an array of int.
   * @param childIDs an array of int.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  ReturnValue associate_processing_event_with_parents_and_child(int processingID, int[] parentIDs, int[] childIDs);

  /**
   * <p>fix_file_paths.</p>
   *
   * @param prefix a {@link java.lang.String} object.
   * @param files a {@link java.util.ArrayList} object.
   * @return a {@link java.util.ArrayList} object.
   */
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

  /**
   * <p>add_workflow_run.</p>
   *
   * @param workflowAccession a int.
   * @return a int.
   */
  int add_workflow_run(int workflowAccession);

  /**
   * <p>update_processing_workflow_run.</p>
   *
   * @param processingID a int.
   * @param workflowRunID a int.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  ReturnValue update_processing_workflow_run(int processingID, int workflowRunID);

  /**
   * <p>add_workflow_run_ancestor.</p>
   *
   * @param workflowRunAccession a int.
   * @param processingId a int.
   */
  void add_workflow_run_ancestor(int workflowRunAccession, int processingId);

  /**
   * <p>get_workflow_run_accession.</p>
   *
   * @param workflowRunId a int.
   * @return a int.
   */
  int get_workflow_run_accession(int workflowRunId);

  /**
   * <p>get_workflow_run_id.</p>
   *
   * @param workflowRunAccession a int.
   * @return a int.
   */
  int get_workflow_run_id(int workflowRunAccession);

  /**
   * <p>getWorkflowRun.</p>
   *
   * @param workflowRunAccession a int.
   * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  WorkflowRun getWorkflowRun(int workflowRunAccession);

  /**
   * <p>get_workflow_info.</p>
   *
   * @param workflowAccession a int.
   * @return a {@link java.util.Map} object.
   */
  Map<String, String> get_workflow_info(int workflowAccession);

  /**
   * <p>linkWorkflowRunAndParent.</p>
   *
   * @param workflowRunId a int.
   * @param parentAccession a int.
   * @return a boolean.
   * @throws java.sql.SQLException if any.
   */
  boolean linkWorkflowRunAndParent(int workflowRunId, int parentAccession) throws SQLException;

  /**
   * <p>update_workflow_run.</p>
   *
   * @param workflowRunId a int.
   * @param pegasusCmd a {@link java.lang.String} object.
   * @param workflowTemplate a {@link java.lang.String} object.
   * @param status a {@link java.lang.String} object.
   * @param statusCmd a {@link java.lang.String} object.
   * @param workingDirectory a {@link java.lang.String} object.
   * @param dax a {@link java.lang.String} object.
   * @param ini a {@link java.lang.String} object.
   * @param host a {@link java.lang.String} object.
   * @param currStep a int.
   * @param totalSteps a int.
   * @param stdErr a {@link java.lang.String} object.
   * @param stdOut a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  ReturnValue update_workflow_run(int workflowRunId, String pegasusCmd, String workflowTemplate, String status,
      String statusCmd, String workingDirectory, String dax, String ini, String host, int currStep, int totalSteps,
      String stdErr, String stdOut);

  /**
   * <p>findFilesAssociatedWithASample.</p>
   *
   * @param sampleName a {@link java.lang.String} object.
   * @return a {@link java.util.List} object.
   */
  List<ReturnValue> findFilesAssociatedWithASample(String sampleName);

  /**
   * <p>addWorkflow.</p>
   *
   * @param name a {@link java.lang.String} object.
   * @param version a {@link java.lang.String} object.
   * @param description a {@link java.lang.String} object.
   * @param baseCommand a {@link java.lang.String} object.
   * @param configFile a {@link java.lang.String} object.
   * @param templateFile a {@link java.lang.String} object.
   * @param provisionDir a {@link java.lang.String} object.
   * @param storeProvisionDir a boolean.
   * @param archiveZip a {@link java.lang.String} object.
   * @param storeArchiveZip a boolean.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  ReturnValue addWorkflow(String name, String version, String description, String baseCommand, String configFile,
      String templateFile, String provisionDir, boolean storeProvisionDir, String archiveZip, boolean storeArchiveZip);

  /**
   * <p>updateWorkflow.</p>
   *
   * @param workflowId a int.
   * @param permanentBundleLocation a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  ReturnValue updateWorkflow(int workflowId, String permanentBundleLocation);

  /**
   * <p>listInstalledWorkflows.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  String listInstalledWorkflows();

  /**
   * <p>listInstalledWorkflowParams.</p>
   *
   * @param workflowAccession a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  String listInstalledWorkflowParams(String workflowAccession);

  /**
   * <p>getWorkflowAccession.</p>
   *
   * @param name a {@link java.lang.String} object.
   * @param version a {@link java.lang.String} object.
   * @return a int.
   */
  public int getWorkflowAccession(String name, String version);

  /**
   * <p>findFilesAssociatedWithAStudy.</p>
   *
   * @param studyName a {@link java.lang.String} object.
   * @return a {@link java.util.List} object.
   */
  public List<ReturnValue> findFilesAssociatedWithAStudy(String studyName);

  /**
   * <p>saveFileForIus.</p>
   *
   * @param workflowRunId a int.
   * @param iusAccession a int.
   * @param file a {@link net.sourceforge.seqware.common.module.FileMetadata} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public ReturnValue saveFileForIus(int workflowRunId, int iusAccession, FileMetadata file);

  /**
   * <p>isDuplicateFile.</p>
   *
   * @param filepath a {@link java.lang.String} object.
   * @return a {@link java.lang.Boolean} object.
   */
  Boolean isDuplicateFile(String filepath);

  /**
   * <p>findFilesAssociatedWithASequencerRun.</p>
   *
   * @param sequencerRunName a {@link java.lang.String} object.
   * @return a {@link java.util.List} object.
   */
  public List<ReturnValue> findFilesAssociatedWithASequencerRun(String sequencerRunName);

  /**
   * <p>getWorkflowRunsByStatus.</p>
   *
   * @param status a {@link java.lang.String} object.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> getWorkflowRunsByStatus(String status);

  /**
   * <p>getWorkflowRunsByHost.</p>
   *
   * @param host a {@link java.lang.String} object.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> getWorkflowRunsByHost(String host);

  /**
   * <p>getWorkflowRunWithWorkflow.</p>
   *
   * @param workflowRunAccession a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  public WorkflowRun getWorkflowRunWithWorkflow(String workflowRunAccession);

  /**
   * <p>getAllStudies.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<Study> getAllStudies();

  /**
   * <p>getSequencerRunReport.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getSequencerRunReport();

  /**
   * <p>annotateIUS.</p>
   *
   * @param laneSWID a int.
   * @param iusAtt a {@link net.sourceforge.seqware.common.model.IUSAttribute} object.
   * @param skip a {@link java.lang.Boolean} object.
   */
  void annotateIUS(int laneSWID, IUSAttribute iusAtt, Boolean skip);

  /**
   * <p>annotateIUS.</p>
   *
   * @param laneSWID a int.
   * @param iusAtts a {@link java.util.Set} object.
   */
  void annotateIUS(int laneSWID, Set<IUSAttribute> iusAtts);

  /**
   * <p>annotateLane.</p>
   *
   * @param laneSWID a int.
   * @param laneAtt a {@link net.sourceforge.seqware.common.model.LaneAttribute} object.
   * @param skip a {@link java.lang.Boolean} object.
   */
  void annotateLane(int laneSWID, LaneAttribute laneAtt, Boolean skip);

  /**
   * <p>annotateLane.</p>
   *
   * @param laneSWID a int.
   * @param laneAtts a {@link java.util.Set} object.
   */
  void annotateLane(int laneSWID, Set<LaneAttribute> laneAtts);

  /**
   * <p>annotateSequencerRun.</p>
   *
   * @param sequencerRunSWID a int.
   * @param sequencerRunAtt a {@link net.sourceforge.seqware.common.model.SequencerRunAttribute} object.
   * @param skip a {@link java.lang.Boolean} object.
   */
  void annotateSequencerRun(int sequencerRunSWID, SequencerRunAttribute sequencerRunAtt, Boolean skip);

  /**
   * <p>annotateSequencerRun.</p>
   *
   * @param sequencerRunSWID a int.
   * @param sequencerRunAtts a {@link java.util.Set} object.
   */
  void annotateSequencerRun(int sequencerRunSWID, Set<SequencerRunAttribute> sequencerRunAtts);

  /**
   * <p>annotateExperiment.</p>
   *
   * @param experimentSWID a int.
   * @param att a {@link net.sourceforge.seqware.common.model.ExperimentAttribute} object.
   * @param skip a {@link java.lang.Boolean} object.
   */
  void annotateExperiment(int experimentSWID, ExperimentAttribute att, Boolean skip);

  /**
   * <p>annotateExperiment.</p>
   *
   * @param experimentSWID a int.
   * @param atts a {@link java.util.Set} object.
   */
  void annotateExperiment(int experimentSWID, Set<ExperimentAttribute> atts);

  /**
   * <p>annotateProcessing.</p>
   *
   * @param processingSWID a int.
   * @param att a {@link net.sourceforge.seqware.common.model.ProcessingAttribute} object.
   * @param skip a {@link java.lang.Boolean} object.
   */
  void annotateProcessing(int processingSWID, ProcessingAttribute att, Boolean skip);

  /**
   * <p>annotateProcessing.</p>
   *
   * @param processingSWID a int.
   * @param atts a {@link java.util.Set} object.
   */
  void annotateProcessing(int processingSWID, Set<ProcessingAttribute> atts);

  /**
   * <p>annotateSample.</p>
   *
   * @param sampleSWID a int.
   * @param att a {@link net.sourceforge.seqware.common.model.SampleAttribute} object.
   * @param skip a {@link java.lang.Boolean} object.
   */
  void annotateSample(int sampleSWID, SampleAttribute att, Boolean skip);

  /**
   * <p>annotateSample.</p>
   *
   * @param sampleSWID a int.
   * @param atts a {@link java.util.Set} object.
   */
  void annotateSample(int sampleSWID, Set<SampleAttribute> atts);

  /**
   * <p>annotateStudy.</p>
   *
   * @param studySWID a int.
   * @param att a {@link net.sourceforge.seqware.common.model.StudyAttribute} object.
   * @param skip a {@link java.lang.Boolean} object.
   */
  void annotateStudy(int studySWID, StudyAttribute att, Boolean skip);

  /**
   * <p>annotateStudy.</p>
   *
   * @param studySWID a int.
   * @param atts a {@link java.util.Set} object.
   */
  void annotateStudy(int studySWID, Set<StudyAttribute> atts);

  /**
   * <p>annotateWorkflow.</p>
   *
   * @param workflowSWID a int.
   * @param att a {@link net.sourceforge.seqware.common.model.WorkflowAttribute} object.
   * @param skip a {@link java.lang.Boolean} object.
   */
  void annotateWorkflow(int workflowSWID, WorkflowAttribute att, Boolean skip);

  /**
   * <p>annotateWorkflow.</p>
   *
   * @param workflowSWID a int.
   * @param atts a {@link java.util.Set} object.
   */
  void annotateWorkflow(int workflowSWID, Set<WorkflowAttribute> atts);

  /**
   * <p>annotateWorkflowRun.</p>
   *
   * @param workflowrunSWID a int.
   * @param att a {@link net.sourceforge.seqware.common.model.WorkflowRunAttribute} object.
   * @param skip a {@link java.lang.Boolean} object.
   */
  void annotateWorkflowRun(int workflowrunSWID, WorkflowRunAttribute att, Boolean skip);

  /**
   * <p>annotateWorkflowRun.</p>
   *
   * @param workflowSWID a int.
   * @param atts a {@link java.util.Set} object.
   */
  void annotateWorkflowRun(int workflowSWID, Set<WorkflowRunAttribute> atts);

  // void annotateFile(int fileSWID, FileAttribute att, Boolean skip);

  /**
   * <p>getWorkflowRunReport.</p>
   *
   * @param workflowRunSWID a int.
   * @return a {@link java.lang.String} object.
   */
  public String getWorkflowRunReport(int workflowRunSWID);

  /**
   * <p>getWorkflowRunReport.</p>
   *
   * @param workflowSWID a int.
   * @param earliestDate a {@link java.util.Date} object.
   * @param latestDate a {@link java.util.Date} object.
   * @return a {@link java.lang.String} object.
   */
  public String getWorkflowRunReport(int workflowSWID, Date earliestDate, Date latestDate);

  /**
   * <p>getWorkflowRunReport.</p>
   *
   * @param earliestDate a {@link java.util.Date} object.
   * @param latestDate a {@link java.util.Date} object.
   * @return a {@link java.lang.String} object.
   */
  public String getWorkflowRunReport(Date earliestDate, Date latestDate);

  /**
   * <p>getFile.</p>
   *
   * @param swAccession a int.
   * @return a {@link net.sourceforge.seqware.common.model.File} object.
   */
  public net.sourceforge.seqware.common.model.File getFile(int swAccession);

  /**
   * <p>getWorkflowParams.</p>
   *
   * @param swAccession a {@link java.lang.String} object.
   * @return a {@link java.util.SortedSet} object.
   */
  public SortedSet<WorkflowParam> getWorkflowParams(String swAccession);
}
