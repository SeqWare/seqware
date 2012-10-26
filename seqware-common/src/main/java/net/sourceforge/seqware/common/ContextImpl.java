package net.sourceforge.seqware.common;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.FileAttributeService;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.LibrarySelectionService;
import net.sourceforge.seqware.common.business.LibraryService;
import net.sourceforge.seqware.common.business.LibrarySourceService;
import net.sourceforge.seqware.common.business.LibraryStrategyService;
import net.sourceforge.seqware.common.business.OrganismService;
import net.sourceforge.seqware.common.business.PlatformService;
import net.sourceforge.seqware.common.business.ProcessingExperimentsService;
import net.sourceforge.seqware.common.business.ProcessingIUSService;
import net.sourceforge.seqware.common.business.ProcessingLanesService;
import net.sourceforge.seqware.common.business.ProcessingRelationshipService;
import net.sourceforge.seqware.common.business.ProcessingSamplesService;
import net.sourceforge.seqware.common.business.ProcessingSequencerRunsService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.ProcessingStudiesService;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.business.SampleReportService;
import net.sourceforge.seqware.common.business.SampleSearchService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.business.StudyTypeService;
import net.sourceforge.seqware.common.business.ValidationReportService;
import net.sourceforge.seqware.common.business.WorkflowParamService;
import net.sourceforge.seqware.common.business.WorkflowParamValueService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.util.Log;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ContextLoader;

/**
 * <p>ContextImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ContextImpl {

  private static ContextImpl ctx;

  private StudyService studyService;
  private ExperimentService experimentService;
  private FileService fileService;
  private IUSService iusService;
  private LaneService laneService;
  private ProcessingService processingService;
  private SampleService sampleService;
  private SampleReportService sampleReportService;
  private SequencerRunService sequencerRunService;
  private WorkflowRunService workflowRunService;
  private WorkflowService workflowService;
  private RegistrationService registrationService;
  private ProcessingRelationshipService processingRelationshipService;
  private ProcessingExperimentsService processingExperimentsService;
  private ProcessingIUSService processingIusService;
  private ProcessingLanesService processingLaneService;
  private ProcessingSamplesService processingSamplesService;
  private ProcessingSequencerRunsService processingSequencerRunService;
  private ProcessingStudiesService processingStudiesService;
  private WorkflowParamService workflowParamService;
  private WorkflowParamValueService workflowParamValueService;
  private SessionFactory sessionFactory;
  private PlatformService platformService;
  private StudyTypeService studyTypeService;
  private LibrarySelectionService librarySelectionService;
  private LibrarySourceService librarySourceService;
  private LibraryStrategyService libraryStrategyService;
  private OrganismService organismService;

  @Autowired
  private LibraryService libraryService;

  @Autowired
  private ValidationReportService validationReportService;

  @Autowired
  private SampleSearchService sampleSearchService;

  @Autowired
  private FileAttributeService fileAttributeService;

  private ContextImpl() {
    // appCtx = new ClassPathXmlApplicationContext("applicationContext.xml");
  }

  /**
   * <p>getInstance.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.ContextImpl} object.
   */
  public static synchronized ContextImpl getInstance() {
    if (ctx == null) {
      ApplicationContext c = ContextLoader.getCurrentWebApplicationContext();
      if (c == null) {
        Log.info("ContextImpl: Could not find web context. Switching to XML context.");
        c = new ClassPathXmlApplicationContext("applicationContext.xml");
      }
      ctx = (ContextImpl) c.getBean("contextImpl");
    }
    return ctx;
  }

  // public ApplicationContext getApplicationContext() {
  // return appCtx;
  // }

  /**
   * <p>Getter for the field <code>studyService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.StudyService} object.
   */
  public StudyService getStudyService() {
    return studyService;
  }

  /**
   * <p>Setter for the field <code>studyService</code>.</p>
   *
   * @param studyService a {@link net.sourceforge.seqware.common.business.StudyService} object.
   */
  public void setStudyService(StudyService studyService) {
    this.studyService = studyService;
  }

  /**
   * <p>Getter for the field <code>experimentService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ExperimentService} object.
   */
  public ExperimentService getExperimentService() {
    return experimentService;
  }

  /**
   * <p>Setter for the field <code>experimentService</code>.</p>
   *
   * @param experimentService a {@link net.sourceforge.seqware.common.business.ExperimentService} object.
   */
  public void setExperimentService(ExperimentService experimentService) {
    this.experimentService = experimentService;
  }

  /**
   * <p>Getter for the field <code>fileService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.FileService} object.
   */
  public FileService getFileService() {
    return fileService;
  }

  /**
   * <p>Setter for the field <code>fileService</code>.</p>
   *
   * @param fileService a {@link net.sourceforge.seqware.common.business.FileService} object.
   */
  public void setFileService(FileService fileService) {
    this.fileService = fileService;
  }

  /**
   * <p>Getter for the field <code>iusService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.IUSService} object.
   */
  public IUSService getIusService() {
    return iusService;
  }

  /**
   * <p>Setter for the field <code>iusService</code>.</p>
   *
   * @param iusService a {@link net.sourceforge.seqware.common.business.IUSService} object.
   */
  public void setIusService(IUSService iusService) {
    this.iusService = iusService;
  }

  /**
   * <p>Getter for the field <code>laneService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.LaneService} object.
   */
  public LaneService getLaneService() {
    return laneService;
  }

  /**
   * <p>Setter for the field <code>laneService</code>.</p>
   *
   * @param laneService a {@link net.sourceforge.seqware.common.business.LaneService} object.
   */
  public void setLaneService(LaneService laneService) {
    this.laneService = laneService;
  }

  /**
   * <p>Getter for the field <code>processingExperimentsService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ProcessingExperimentsService} object.
   */
  public ProcessingExperimentsService getProcessingExperimentsService() {
    return processingExperimentsService;
  }

  /**
   * <p>Setter for the field <code>processingExperimentsService</code>.</p>
   *
   * @param processingExperimentsService a {@link net.sourceforge.seqware.common.business.ProcessingExperimentsService} object.
   */
  public void setProcessingExperimentsService(ProcessingExperimentsService processingExperimentsService) {
    this.processingExperimentsService = processingExperimentsService;
  }

  /**
   * <p>Getter for the field <code>processingIusService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ProcessingIUSService} object.
   */
  public ProcessingIUSService getProcessingIusService() {
    return processingIusService;
  }

  /**
   * <p>Setter for the field <code>processingIusService</code>.</p>
   *
   * @param processingIusService a {@link net.sourceforge.seqware.common.business.ProcessingIUSService} object.
   */
  public void setProcessingIusService(ProcessingIUSService processingIusService) {
    this.processingIusService = processingIusService;
  }

  /**
   * <p>Getter for the field <code>processingLaneService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ProcessingLanesService} object.
   */
  public ProcessingLanesService getProcessingLaneService() {
    return processingLaneService;
  }

  /**
   * <p>Setter for the field <code>processingLaneService</code>.</p>
   *
   * @param processingLaneService a {@link net.sourceforge.seqware.common.business.ProcessingLanesService} object.
   */
  public void setProcessingLaneService(ProcessingLanesService processingLaneService) {
    this.processingLaneService = processingLaneService;
  }

  /**
   * <p>Getter for the field <code>processingRelationshipService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ProcessingRelationshipService} object.
   */
  public ProcessingRelationshipService getProcessingRelationshipService() {
    return processingRelationshipService;
  }

  /**
   * <p>Setter for the field <code>processingRelationshipService</code>.</p>
   *
   * @param processingRelationshipService a {@link net.sourceforge.seqware.common.business.ProcessingRelationshipService} object.
   */
  public void setProcessingRelationshipService(ProcessingRelationshipService processingRelationshipService) {
    this.processingRelationshipService = processingRelationshipService;
  }

  /**
   * <p>Getter for the field <code>processingSamplesService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ProcessingSamplesService} object.
   */
  public ProcessingSamplesService getProcessingSamplesService() {
    return processingSamplesService;
  }

  /**
   * <p>Setter for the field <code>processingSamplesService</code>.</p>
   *
   * @param processingSamplesService a {@link net.sourceforge.seqware.common.business.ProcessingSamplesService} object.
   */
  public void setProcessingSamplesService(ProcessingSamplesService processingSamplesService) {
    this.processingSamplesService = processingSamplesService;
  }

  /**
   * <p>Getter for the field <code>processingSequencerRunService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ProcessingSequencerRunsService} object.
   */
  public ProcessingSequencerRunsService getProcessingSequencerRunService() {
    return processingSequencerRunService;
  }

  /**
   * <p>Setter for the field <code>processingSequencerRunService</code>.</p>
   *
   * @param processingSequencerRunService a {@link net.sourceforge.seqware.common.business.ProcessingSequencerRunsService} object.
   */
  public void setProcessingSequencerRunService(ProcessingSequencerRunsService processingSequencerRunService) {
    this.processingSequencerRunService = processingSequencerRunService;
  }

  /**
   * <p>Getter for the field <code>processingService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ProcessingService} object.
   */
  public ProcessingService getProcessingService() {
    return processingService;
  }

  /**
   * <p>Setter for the field <code>processingService</code>.</p>
   *
   * @param processingService a {@link net.sourceforge.seqware.common.business.ProcessingService} object.
   */
  public void setProcessingService(ProcessingService processingService) {
    this.processingService = processingService;
  }

  /**
   * <p>Getter for the field <code>processingStudiesService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ProcessingStudiesService} object.
   */
  public ProcessingStudiesService getProcessingStudiesService() {
    return processingStudiesService;
  }

  /**
   * <p>Setter for the field <code>processingStudiesService</code>.</p>
   *
   * @param processingStudiesService a {@link net.sourceforge.seqware.common.business.ProcessingStudiesService} object.
   */
  public void setProcessingStudiesService(ProcessingStudiesService processingStudiesService) {
    this.processingStudiesService = processingStudiesService;
  }

  /**
   * <p>Getter for the field <code>registrationService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.RegistrationService} object.
   */
  public RegistrationService getRegistrationService() {
    return registrationService;
  }

  /**
   * <p>Setter for the field <code>registrationService</code>.</p>
   *
   * @param registrationService a {@link net.sourceforge.seqware.common.business.RegistrationService} object.
   */
  public void setRegistrationService(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  /**
   * <p>Getter for the field <code>sampleService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.SampleService} object.
   */
  public SampleService getSampleService() {
    return sampleService;
  }

  /**
   * <p>Setter for the field <code>sampleService</code>.</p>
   *
   * @param sampleService a {@link net.sourceforge.seqware.common.business.SampleService} object.
   */
  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  /**
   * <p>Getter for the field <code>sequencerRunService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.SequencerRunService} object.
   */
  public SequencerRunService getSequencerRunService() {
    return sequencerRunService;
  }

  /**
   * <p>Setter for the field <code>sequencerRunService</code>.</p>
   *
   * @param sequencerRunService a {@link net.sourceforge.seqware.common.business.SequencerRunService} object.
   */
  public void setSequencerRunService(SequencerRunService sequencerRunService) {
    this.sequencerRunService = sequencerRunService;
  }

  /**
   * <p>Getter for the field <code>sessionFactory</code>.</p>
   *
   * @return a {@link org.hibernate.SessionFactory} object.
   */
  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  /**
   * <p>Setter for the field <code>sessionFactory</code>.</p>
   *
   * @param sessionFactory a {@link org.hibernate.SessionFactory} object.
   */
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  /**
   * <p>Getter for the field <code>workflowParamService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.WorkflowParamService} object.
   */
  public WorkflowParamService getWorkflowParamService() {
    return workflowParamService;
  }

  /**
   * <p>Setter for the field <code>workflowParamService</code>.</p>
   *
   * @param workflowParamService a {@link net.sourceforge.seqware.common.business.WorkflowParamService} object.
   */
  public void setWorkflowParamService(WorkflowParamService workflowParamService) {
    this.workflowParamService = workflowParamService;
  }

  /**
   * <p>Getter for the field <code>workflowParamValueService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.WorkflowParamValueService} object.
   */
  public WorkflowParamValueService getWorkflowParamValueService() {
    return workflowParamValueService;
  }

  /**
   * <p>Setter for the field <code>workflowParamValueService</code>.</p>
   *
   * @param workflowParamValueService a {@link net.sourceforge.seqware.common.business.WorkflowParamValueService} object.
   */
  public void setWorkflowParamValueService(WorkflowParamValueService workflowParamValueService) {
    this.workflowParamValueService = workflowParamValueService;
  }

  /**
   * <p>Getter for the field <code>workflowRunService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.WorkflowRunService} object.
   */
  public WorkflowRunService getWorkflowRunService() {
    return workflowRunService;
  }

  /**
   * <p>Setter for the field <code>workflowRunService</code>.</p>
   *
   * @param workflowRunService a {@link net.sourceforge.seqware.common.business.WorkflowRunService} object.
   */
  public void setWorkflowRunService(WorkflowRunService workflowRunService) {
    this.workflowRunService = workflowRunService;
  }

  /**
   * <p>Getter for the field <code>workflowService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.WorkflowService} object.
   */
  public WorkflowService getWorkflowService() {
    return workflowService;
  }

  /**
   * <p>Setter for the field <code>workflowService</code>.</p>
   *
   * @param workflowService a {@link net.sourceforge.seqware.common.business.WorkflowService} object.
   */
  public void setWorkflowService(WorkflowService workflowService) {
    this.workflowService = workflowService;
  }

  /**
   * <p>Getter for the field <code>platformService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.PlatformService} object.
   */
  public PlatformService getPlatformService() {
    return platformService;
  }

  /**
   * <p>Setter for the field <code>platformService</code>.</p>
   *
   * @param platformService a {@link net.sourceforge.seqware.common.business.PlatformService} object.
   */
  public void setPlatformService(PlatformService platformService) {
    this.platformService = platformService;
  }

  /**
   * <p>Getter for the field <code>sampleReportService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.SampleReportService} object.
   */
  public SampleReportService getSampleReportService() {
    return sampleReportService;
  }

  /**
   * <p>Setter for the field <code>sampleReportService</code>.</p>
   *
   * @param sampleReportService a {@link net.sourceforge.seqware.common.business.SampleReportService} object.
   */
  public void setSampleReportService(SampleReportService sampleReportService) {
    this.sampleReportService = sampleReportService;
  }

  /**
   * <p>Getter for the field <code>libraryService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.LibraryService} object.
   */
  public LibraryService getLibraryService() {
    return libraryService;
  }

  /**
   * <p>Setter for the field <code>libraryService</code>.</p>
   *
   * @param libraryService a {@link net.sourceforge.seqware.common.business.LibraryService} object.
   */
  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  /**
   * <p>Getter for the field <code>validationReportService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ValidationReportService} object.
   */
  public ValidationReportService getValidationReportService() {
    return validationReportService;
  }

  /**
   * <p>Setter for the field <code>validationReportService</code>.</p>
   *
   * @param validationReportService a {@link net.sourceforge.seqware.common.business.ValidationReportService} object.
   */
  public void setValidationReportService(ValidationReportService validationReportService) {
    this.validationReportService = validationReportService;
  }

  /**
   * <p>Getter for the field <code>sampleSearchService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.SampleSearchService} object.
   */
  public SampleSearchService getSampleSearchService() {
    return sampleSearchService;
  }

  /**
   * <p>Getter for the field <code>fileAttributeService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.FileAttributeService} object.
   */
  public FileAttributeService getFileAttributeService() {
    return fileAttributeService;
  }

  public StudyTypeService getStudyTypeService() {
    return studyTypeService;
  }

  public void setStudyTypeService(StudyTypeService studyTypeService) {
    this.studyTypeService = studyTypeService;
  }

  public LibrarySelectionService getLibrarySelectionService() {
    return librarySelectionService;
  }

  public void setLibrarySelectionService(LibrarySelectionService librarySelectionService) {
    this.librarySelectionService = librarySelectionService;
  }

  public LibrarySourceService getLibrarySourceService() {
    return librarySourceService;
  }

  public void setLibrarySourceService(LibrarySourceService librarySourceService) {
    this.librarySourceService = librarySourceService;
  }

  public LibraryStrategyService getLibraryStrategyService() {
    return libraryStrategyService;
  }

  public void setLibraryStrategyService(LibraryStrategyService libraryStrategyService) {
    this.libraryStrategyService = libraryStrategyService;
  }

  public OrganismService getOrganismService() {
    return organismService;
  }

  public void setOrganismService(OrganismService organismService) {
    this.organismService = organismService;
  }

}
