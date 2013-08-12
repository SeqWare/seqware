package net.sourceforge.seqware.common.factory;

import net.sourceforge.seqware.common.ContextImpl;
import net.sourceforge.seqware.common.business.ExperimentLibraryDesignService;
import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.ExperimentSpotDesignReadSpecService;
import net.sourceforge.seqware.common.business.ExperimentSpotDesignService;
import net.sourceforge.seqware.common.business.FileAttributeService;
import net.sourceforge.seqware.common.business.StudyTypeService;
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
import net.sourceforge.seqware.common.business.ValidationReportService;
import net.sourceforge.seqware.common.business.WorkflowParamService;
import net.sourceforge.seqware.common.business.WorkflowParamValueService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.ExperimentLibraryDesign;

import org.hibernate.SessionFactory;

/**
 * <p>Abstract BeanFactory class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public abstract class BeanFactory {

  /**
   * <p>getStudyServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.StudyService} object.
   */
  public static StudyService getStudyServiceBean() {
    // return (StudyService)
    // ContextImpl.getInstance().getApplicationContext().getBean("studyService");
    return ContextImpl.getInstance().getStudyService();
  }

  /**
   * <p>getExperimentServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ExperimentService} object.
   */
  public static ExperimentService getExperimentServiceBean() {
    return ContextImpl.getInstance().getExperimentService();
  }

  /**
   * <p>getFileServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.FileService} object.
   */
  public static FileService getFileServiceBean() {
    return ContextImpl.getInstance().getFileService();
  }

  /**
   * <p>getIUSServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.IUSService} object.
   */
  public static IUSService getIUSServiceBean() {
    return ContextImpl.getInstance().getIusService();
  }

  /**
   * <p>getLaneServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.LaneService} object.
   */
  public static LaneService getLaneServiceBean() {
    return ContextImpl.getInstance().getLaneService();
  }

  /**
   * <p>getProcessingServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ProcessingService} object.
   */
  public static ProcessingService getProcessingServiceBean() {
    return ContextImpl.getInstance().getProcessingService();
  }

  /**
   * <p>getSampleServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.SampleService} object.
   */
  public static SampleService getSampleServiceBean() {
    return ContextImpl.getInstance().getSampleService();
  }

  /**
   * <p>getSequencerRunServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.SequencerRunService} object.
   */
  public static SequencerRunService getSequencerRunServiceBean() {
    return ContextImpl.getInstance().getSequencerRunService();
  }

  /**
   * <p>getWorkflowRunServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.WorkflowRunService} object.
   */
  public static WorkflowRunService getWorkflowRunServiceBean() {
    return ContextImpl.getInstance().getWorkflowRunService();
  }

  /**
   * <p>getWorkflowServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.WorkflowService} object.
   */
  public static WorkflowService getWorkflowServiceBean() {
    return ContextImpl.getInstance().getWorkflowService();
  }

  /**
   * <p>getRegistrationServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.RegistrationService} object.
   */
  public static RegistrationService getRegistrationServiceBean() {
    return ContextImpl.getInstance().getRegistrationService();
  }

  /**
   * <p>getProcessingRelationshipServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ProcessingRelationshipService} object.
   */
  public static ProcessingRelationshipService getProcessingRelationshipServiceBean() {
    return ContextImpl.getInstance().getProcessingRelationshipService();
  }

  /**
   * <p>getProcessingExperimentServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ProcessingExperimentsService} object.
   */
  public static ProcessingExperimentsService getProcessingExperimentServiceBean() {
    return ContextImpl.getInstance().getProcessingExperimentsService();
  }

  /**
   * <p>getProcessingIUSServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ProcessingIUSService} object.
   */
  public static ProcessingIUSService getProcessingIUSServiceBean() {
    return ContextImpl.getInstance().getProcessingIusService();
  }

  /**
   * <p>getProcessingLaneServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ProcessingLanesService} object.
   */
  public static ProcessingLanesService getProcessingLaneServiceBean() {
    return ContextImpl.getInstance().getProcessingLaneService();
  }

  /**
   * <p>getProcessingSampleServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ProcessingSamplesService} object.
   */
  public static ProcessingSamplesService getProcessingSampleServiceBean() {
    return ContextImpl.getInstance().getProcessingSamplesService();

  }

  /**
   * <p>getProcessingSequencerRunsServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ProcessingSequencerRunsService} object.
   */
  public static ProcessingSequencerRunsService getProcessingSequencerRunsServiceBean() {
    return ContextImpl.getInstance().getProcessingSequencerRunService();
  }

  /**
   * <p>getProcessingStudiesServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ProcessingStudiesService} object.
   */
  public static ProcessingStudiesService getProcessingStudiesServiceBean() {
    return ContextImpl.getInstance().getProcessingStudiesService();
  }

  /**
   * <p>getWorkflowParamServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.WorkflowParamService} object.
   */
  public static WorkflowParamService getWorkflowParamServiceBean() {
    return ContextImpl.getInstance().getWorkflowParamService();

  }

  /**
   * <p>getWorkflowParamValueServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.WorkflowParamValueService} object.
   */
  public static WorkflowParamValueService getWorkflowParamValueServiceBean() {
    return ContextImpl.getInstance().getWorkflowParamValueService();

  }

  /**
   * <p>getSessionFactoryBean.</p>
   *
   * @return a {@link org.hibernate.SessionFactory} object.
   */
  public static SessionFactory getSessionFactoryBean() {
    return ContextImpl.getInstance().getSessionFactory();

  }

  /**
   * <p>getPlatformServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.PlatformService} object.
   */
  public static PlatformService getPlatformServiceBean() {
    return ContextImpl.getInstance().getPlatformService();
  }

  /**
   * <p>getSampleReportServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.SampleReportService} object.
   */
  public static SampleReportService getSampleReportServiceBean() {
    return ContextImpl.getInstance().getSampleReportService();
  }

  /**
   * <p>getLibraryServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.LibraryService} object.
   */
  public static LibraryService getLibraryServiceBean() {
    return ContextImpl.getInstance().getLibraryService();
  }

  /**
   * <p>getFileValidationServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ValidationReportService} object.
   */
  public static ValidationReportService getFileValidationServiceBean() {
    return ContextImpl.getInstance().getValidationReportService();
  }

  /**
   * <p>getSampleSearchServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.SampleSearchService} object.
   */
  public static SampleSearchService getSampleSearchServiceBean() {
    return ContextImpl.getInstance().getSampleSearchService();
  }

  /**
   * <p>getStudyTypeServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.StudyTypeService} object.
   */
  public static StudyTypeService getStudyTypeServiceBean() {
    return ContextImpl.getInstance().getStudyTypeService();
  }
  
  /**
   * <p>getLibrarySelectionServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.LibrarySelectionServiceBean} object.
   */
  public static LibrarySelectionService getLibrarySelectionServiceBean() {
    return ContextImpl.getInstance().getLibrarySelectionService();
  }

  /**
   * <p>getLibrarySourceServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.LibrarySourceServiceBean} object.
   */
  public static LibrarySourceService getLibrarySourceServiceBean() {
    return ContextImpl.getInstance().getLibrarySourceService();
  }
  
  /**
   * <p>getLibraryStrategyServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.LibraryStrategyServiceBean} object.
   */
  public static LibraryStrategyService getLibraryStrategyServiceBean() {
    return ContextImpl.getInstance().getLibraryStrategyService();
  }
  
  /**
   * <p>getOrganismServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.OrganismServiceBean} object.
   */
  public static OrganismService getOrganismServiceBean() {
    return ContextImpl.getInstance().getOrganismService();
  }
  
  /**
   * <p>getFileAttributeServiceBean.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.FileAttributeServiceBean} object.
   */
  public static FileAttributeService getFileAttributeServiceBean() {
    return ContextImpl.getInstance().getFileAttributeService();
  }
  
  public static ExperimentLibraryDesignService getExperimentLibraryDesignServiceBean(){
        return ContextImpl.getInstance().getExperimentLibraryDesignService();
  }

  public static ExperimentSpotDesignService getExperimentSpotDesignServiceBean() {
    return ContextImpl.getInstance().getExperimentSpotDesignService();
  }

  public static ExperimentSpotDesignReadSpecService getExperimentSpotDesignReadSpecServiceBean() {
    return ContextImpl.getInstance().getExperimentSpotDesignReadSpecService();
  }
  
}
