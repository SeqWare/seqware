package net.sourceforge.seqware.common;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.FileAttributeService;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.LibraryService;
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

  public StudyService getStudyService() {
    return studyService;
  }

  public void setStudyService(StudyService studyService) {
    this.studyService = studyService;
  }

  public ExperimentService getExperimentService() {
    return experimentService;
  }

  public void setExperimentService(ExperimentService experimentService) {
    this.experimentService = experimentService;
  }

  public FileService getFileService() {
    return fileService;
  }

  public void setFileService(FileService fileService) {
    this.fileService = fileService;
  }

  public IUSService getIusService() {
    return iusService;
  }

  public void setIusService(IUSService iusService) {
    this.iusService = iusService;
  }

  public LaneService getLaneService() {
    return laneService;
  }

  public void setLaneService(LaneService laneService) {
    this.laneService = laneService;
  }

  public ProcessingExperimentsService getProcessingExperimentsService() {
    return processingExperimentsService;
  }

  public void setProcessingExperimentsService(ProcessingExperimentsService processingExperimentsService) {
    this.processingExperimentsService = processingExperimentsService;
  }

  public ProcessingIUSService getProcessingIusService() {
    return processingIusService;
  }

  public void setProcessingIusService(ProcessingIUSService processingIusService) {
    this.processingIusService = processingIusService;
  }

  public ProcessingLanesService getProcessingLaneService() {
    return processingLaneService;
  }

  public void setProcessingLaneService(ProcessingLanesService processingLaneService) {
    this.processingLaneService = processingLaneService;
  }

  public ProcessingRelationshipService getProcessingRelationshipService() {
    return processingRelationshipService;
  }

  public void setProcessingRelationshipService(ProcessingRelationshipService processingRelationshipService) {
    this.processingRelationshipService = processingRelationshipService;
  }

  public ProcessingSamplesService getProcessingSamplesService() {
    return processingSamplesService;
  }

  public void setProcessingSamplesService(ProcessingSamplesService processingSamplesService) {
    this.processingSamplesService = processingSamplesService;
  }

  public ProcessingSequencerRunsService getProcessingSequencerRunService() {
    return processingSequencerRunService;
  }

  public void setProcessingSequencerRunService(ProcessingSequencerRunsService processingSequencerRunService) {
    this.processingSequencerRunService = processingSequencerRunService;
  }

  public ProcessingService getProcessingService() {
    return processingService;
  }

  public void setProcessingService(ProcessingService processingService) {
    this.processingService = processingService;
  }

  public ProcessingStudiesService getProcessingStudiesService() {
    return processingStudiesService;
  }

  public void setProcessingStudiesService(ProcessingStudiesService processingStudiesService) {
    this.processingStudiesService = processingStudiesService;
  }

  public RegistrationService getRegistrationService() {
    return registrationService;
  }

  public void setRegistrationService(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  public SampleService getSampleService() {
    return sampleService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public SequencerRunService getSequencerRunService() {
    return sequencerRunService;
  }

  public void setSequencerRunService(SequencerRunService sequencerRunService) {
    this.sequencerRunService = sequencerRunService;
  }

  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  public void setSessionFactory(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  public WorkflowParamService getWorkflowParamService() {
    return workflowParamService;
  }

  public void setWorkflowParamService(WorkflowParamService workflowParamService) {
    this.workflowParamService = workflowParamService;
  }

  public WorkflowParamValueService getWorkflowParamValueService() {
    return workflowParamValueService;
  }

  public void setWorkflowParamValueService(WorkflowParamValueService workflowParamValueService) {
    this.workflowParamValueService = workflowParamValueService;
  }

  public WorkflowRunService getWorkflowRunService() {
    return workflowRunService;
  }

  public void setWorkflowRunService(WorkflowRunService workflowRunService) {
    this.workflowRunService = workflowRunService;
  }

  public WorkflowService getWorkflowService() {
    return workflowService;
  }

  public void setWorkflowService(WorkflowService workflowService) {
    this.workflowService = workflowService;
  }

  public PlatformService getPlatformService() {
    return platformService;
  }

  public void setPlatformService(PlatformService platformService) {
    this.platformService = platformService;
  }

  public SampleReportService getSampleReportService() {
    return sampleReportService;
  }

  public void setSampleReportService(SampleReportService sampleReportService) {
    this.sampleReportService = sampleReportService;
  }

  public LibraryService getLibraryService() {
    return libraryService;
  }

  public void setLibraryService(LibraryService libraryService) {
    this.libraryService = libraryService;
  }

  public ValidationReportService getValidationReportService() {
    return validationReportService;
  }

  public void setValidationReportService(ValidationReportService validationReportService) {
    this.validationReportService = validationReportService;
  }

  public SampleSearchService getSampleSearchService() {
    return sampleSearchService;
  }

  public FileAttributeService getFileAttributeService() {
    return fileAttributeService;
  }

}
