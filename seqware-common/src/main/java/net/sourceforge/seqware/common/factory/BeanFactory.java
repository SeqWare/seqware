package net.sourceforge.seqware.common.factory;

import net.sourceforge.seqware.common.ContextImpl;
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

import org.hibernate.SessionFactory;

public abstract class BeanFactory {

  public static StudyService getStudyServiceBean() {
    // return (StudyService)
    // ContextImpl.getInstance().getApplicationContext().getBean("studyService");
    return ContextImpl.getInstance().getStudyService();
  }

  public static ExperimentService getExperimentServiceBean() {
    return ContextImpl.getInstance().getExperimentService();
  }

  public static FileService getFileServiceBean() {
    return ContextImpl.getInstance().getFileService();
  }

  public static IUSService getIUSServiceBean() {
    return ContextImpl.getInstance().getIusService();
  }

  public static LaneService getLaneServiceBean() {
    return ContextImpl.getInstance().getLaneService();
  }

  public static ProcessingService getProcessingServiceBean() {
    return ContextImpl.getInstance().getProcessingService();
  }

  public static SampleService getSampleServiceBean() {
    return ContextImpl.getInstance().getSampleService();
  }

  public static SequencerRunService getSequencerRunServiceBean() {
    return ContextImpl.getInstance().getSequencerRunService();
  }

  public static WorkflowRunService getWorkflowRunServiceBean() {
    return ContextImpl.getInstance().getWorkflowRunService();
  }

  public static WorkflowService getWorkflowServiceBean() {
    return ContextImpl.getInstance().getWorkflowService();
  }

  public static RegistrationService getRegistrationServiceBean() {
    return ContextImpl.getInstance().getRegistrationService();
  }

  public static ProcessingRelationshipService getProcessingRelationshipServiceBean() {
    return ContextImpl.getInstance().getProcessingRelationshipService();
  }

  public static ProcessingExperimentsService getProcessingExperimentServiceBean() {
    return ContextImpl.getInstance().getProcessingExperimentsService();
  }

  public static ProcessingIUSService getProcessingIUSServiceBean() {
    return ContextImpl.getInstance().getProcessingIusService();
  }

  public static ProcessingLanesService getProcessingLaneServiceBean() {
    return ContextImpl.getInstance().getProcessingLaneService();
  }

  public static ProcessingSamplesService getProcessingSampleServiceBean() {
    return ContextImpl.getInstance().getProcessingSamplesService();

  }

  public static ProcessingSequencerRunsService getProcessingSequencerRunsServiceBean() {
    return ContextImpl.getInstance().getProcessingSequencerRunService();
  }

  public static ProcessingStudiesService getProcessingStudiesServiceBean() {
    return ContextImpl.getInstance().getProcessingStudiesService();
  }

  public static WorkflowParamService getWorkflowParamServiceBean() {
    return ContextImpl.getInstance().getWorkflowParamService();

  }

  public static WorkflowParamValueService getWorkflowParamValueServiceBean() {
    return ContextImpl.getInstance().getWorkflowParamValueService();

  }

  public static SessionFactory getSessionFactoryBean() {
    return ContextImpl.getInstance().getSessionFactory();

  }

  public static PlatformService getPlatformServiceBean() {
    return ContextImpl.getInstance().getPlatformService();
  }

  public static SampleReportService getSampleReportServiceBean() {
    return ContextImpl.getInstance().getSampleReportService();
  }

  public static LibraryService getLibraryServiceBean() {
    return ContextImpl.getInstance().getLibraryService();
  }

  public static ValidationReportService getFileValidationServiceBean() {
    return ContextImpl.getInstance().getValidationReportService();
  }

  public static SampleSearchService getSampleSearchServiceBean() {
    return ContextImpl.getInstance().getSampleSearchService();
  }

  public static FileAttributeService getFileAttributeService() {
    return ContextImpl.getInstance().getFileAttributeService();
  }

}
