package net.sourceforge.solexatools.webapp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentSpotDesign;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.Constant;
import net.sourceforge.solexatools.util.ControllerUtil;
import net.sourceforge.solexatools.util.PageInfo;
import net.sourceforge.solexatools.util.PaginationUtil;
import net.sourceforge.solexatools.webapp.metamodel.SampleDetailsLineItem;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class StudyListDetailsFlatController extends ApplicationObjectSupport {

  private static int BAR_WIDTH = 170;
  private static int BAR_HEIGHT = 22;

  @Autowired
  private StudyService studyService;
  @Autowired
  private ExperimentService experimentService;
  @Autowired
  private SampleService sampleService;
  @Autowired
  private LaneService laneService;
  @Autowired
  private ProcessingService processingService;
  @Autowired
  @Qualifier("IUSService")
  private IUSService iusService;
  @Autowired
  private WorkflowRunService workflowRunService;

  private Logger log = Logger.getLogger(this.getClass());

  @RequestMapping("/studyListDetailsFlat.htm")
  public ModelAndView doRetrieveListElements(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    Registration registration = Security.getRegistration(request);
    if (registration == null) {
      // return new ModelAndView("redirect:/login.htm");
      return new ModelAndView("StudyListRoot");
    }

    Study s = null;
    Experiment exp = new Experiment();
    Sample sam = new Sample();
    IUS ius = new IUS();
    Processing proc = new Processing();
    WorkflowRun workflowRun = new WorkflowRun();

    PageInfo pageInfo = null;
    Boolean isHasError = false;
    String errorMessage = "";

    List<Study> listAll = new ArrayList<Study>();
    List<Study> listView = new ArrayList<Study>();

    ModelAndView modelAndView;

    String root = (String) request.getParameter("root");
    log.debug("ROOT: " + root);

    int itemsPerPage = 20;
    try {
      itemsPerPage = Integer.parseInt(request.getParameter("pi"));
    } catch (NumberFormatException e) {
      log.warn("No items per page is provided. This is ok for the Tree view");
    }

    if (root == null || "".equals(root) || "source".equals(root)) {
      MessageSourceAccessor ma = this.getMessageSourceAccessor();
      String nameOneItem = "study.list.pagination.nameOneItem";
      String nameLotOfItem = "study.list.pagination.nameLotOfItem";
      String typeList = ControllerUtil.getRequestedTypeList(request);

      if (typeList.equals("mylist")) {
        Boolean isAsc = ControllerUtil.saveAscInSession(request, "ascMyListStudyFlat");
        listAll = getStudyService().list(registration, isAsc);
        listView = PaginationUtil.subList(request, "myStudiesPageFlat", itemsPerPage, listAll);
        pageInfo = PaginationUtil.getPageInfo(request, "myStudiesPageFlat", listView, listAll, nameOneItem,
            nameLotOfItem, ma);

        // set error if want
        if (listAll.size() == 0) {
          isHasError = true;
          errorMessage = this.getMessageSourceAccessor().getMessage("study.list.required.one.item");
        }
      }
      if (typeList.equals("mysharelist")) {
        Boolean isAsc = ControllerUtil.saveAscInSession(request, "ascMyShareListStudyFlat");
        listAll = getStudyService().listMyShared(registration, isAsc);
        listView = PaginationUtil.subList(request, "mySharedStudyPageFlat", itemsPerPage, listAll);
        pageInfo = PaginationUtil.getPageInfo(request, "mySharedStudyPageFlat", listView, listAll, nameOneItem,
            nameLotOfItem, ma);
      }
      if (typeList.equals("bymesharelist")) {
        Boolean isAsc = ControllerUtil.saveAscInSession(request, "ascByMeShareListStudyFlat");
        listAll = getStudyService().listSharedWithMe(registration, isAsc);
        listView = PaginationUtil.subList(request, "studySharedWithMeFlat", itemsPerPage, listAll);
        pageInfo = PaginationUtil.getPageInfo(request, "studySharedWithMeFlat", listView, listAll, nameOneItem,
            nameLotOfItem, ma);
      }
    } else {
      if (root.indexOf("assi_") != -1) {
        sam = getSampleService().findByID(Constant.getFirstId(root));
        workflowRun = getWorkflowRunService().findByID(Constant.getId(root));
      } else if (root.indexOf("sam_") != -1) {
        sam = getSampleService().findByID(Constant.getId(root));
        System.out.println("  CHILD SIZE = " + sam.getChildren().size());
      } else if (root.indexOf("exp_") != -1) {
        exp = getExperimentService().findByID(Constant.getId(root));
      } else if (root.indexOf("wr_") != -1) {
        workflowRun = getWorkflowRunService().findByID(Constant.getFirstId(root));
        sam = getSampleService().findByID(Constant.getId(root));
      } else if (root.indexOf("proc_") != -1) {
        proc = getProcessingService().findByID(Constant.getId(root));
      } else {
        s = getStudyService().findByID(Constant.getId(root));
        listView.add(s);
      }
    }

    if (root.indexOf("ius_") != -1) {
      log.debug("RENDERING INDIVIDUAL IUS");
      modelAndView = new ModelAndView("StudyListProcessing");
      modelAndView.addObject("ius", ius);
    } else if (root.indexOf("sam_") != -1) {
      log.debug("RENDERING INDIVIDUAL Sample");
      modelAndView = new ModelAndView("StudyListFlatSampleDetails");
      modelAndView.addObject("sample", sam);
      Set<WorkflowRun> workflowRuns = getWorkflowRunService().findRunsForSample(sam);
      Set<Processing> orphanedProcessings = getOrphanedProcessings(sam, workflowRuns);
      modelAndView.addObject("workflowRuns", workflowRuns);
      modelAndView.addObject("orphanProcessings", orphanedProcessings);
    } else if (root.indexOf("exp_") != -1) {
      log.debug("RENDERING INDIVIDUAL EXPERIMENT");
      modelAndView = new ModelAndView("StudyListSampleFlat");
      modelAndView.addObject("experiment", exp);
      populateExperimentSpotDesign(modelAndView, exp);
    } else if (root.indexOf("wr_") != -1) {
      log.debug("RENDERING INDIVIDUAL WORKFLOW_RUN");
      modelAndView = new ModelAndView("StudyListWorkflowRunFlat");
      modelAndView.addObject("run", workflowRun);
      modelAndView.addObject("iuses", CollectionUtils.intersection(workflowRun.getIus(), sam.getIUS()));
      modelAndView.addObject("processings", getProcessingService().findFor(sam, workflowRun));
      modelAndView.addObject("sample", sam);
    } else if (root.indexOf("assi_") != -1) {
      log.debug("RENDERING ASSOCIATED IUSES");
      modelAndView = new ModelAndView("StudyListAssociatedIusFlat");
      Set<IUS> wfIus = workflowRun.getIus();
      Set<IUS> sampleIus = sam.getIUS();
      modelAndView.addObject("iuses", CollectionUtils.intersection(wfIus, sampleIus));
      // modelAndView.addObject("iuses", sam.getIUS());
    } else if (root.indexOf("proc_") != -1) {
      log.debug("RENDERING PROCESSING");
      modelAndView = new ModelAndView("StudyListProcessingFlat");
      modelAndView.addObject("processing", proc);
    } else if (root != null && !"".equals(root) && !"source".equals(root) && Integer.parseInt(root) > 0) {
      log.debug("RENDERING INDIVIDUAL STUDY");
      modelAndView = new ModelAndView("StudyListDetailsFlat");
      addProgressBar(s, modelAndView);
    } else {
      log.debug("RENDERING ALL STUDIES");
      modelAndView = new ModelAndView("StudyListRootFlat");
      modelAndView.addObject("pageInfo", pageInfo);
    }

    // set error data
    modelAndView.addObject("isHasError", isHasError);
    modelAndView.addObject("errorMessage", errorMessage);

    modelAndView.addObject("studies", listView);
    modelAndView.addObject("registration", registration);

    return modelAndView;
  }

  /**
   * Returns processings, which doesn't belong to any workflow runs.
   * 
   * @param sample
   * @param workflowRuns
   * @return
   */
  private Set<Processing> getOrphanedProcessings(Sample sample, Set<WorkflowRun> workflowRuns) {
    Set<Processing> processings = getProcessingService().findFor(sample);
    for (WorkflowRun run : workflowRuns) {
      processings.removeAll(getProcessingService().findFor(sample, run));
    }
    return processings;
  }

  private void addProgressBar(Study study, ModelAndView modelAndView) {
    int completed = getStudyService().getFinishedCount(study);
    int running = getStudyService().getRunningCount(study);
    int failed = getStudyService().getFailedCount(study);

    int sum = completed + running + failed;

    int completedWidth = 0;
    int runningWidth = 0;
    int failedWidth = 0;

    if (sum > 0) {
      completedWidth = completed * BAR_WIDTH / sum;
      runningWidth = running * BAR_WIDTH / sum;
      failedWidth = failed * BAR_WIDTH / sum;
    }

    modelAndView.addObject("completedWidth", completedWidth);
    modelAndView.addObject("runningWidth", runningWidth);
    modelAndView.addObject("failedWidth", failedWidth);
    modelAndView.addObject("completedNum", completed);
    modelAndView.addObject("runningNum", running);
    modelAndView.addObject("failedNum", failed);
    modelAndView.addObject("bar_width", BAR_WIDTH);
    modelAndView.addObject("bar_height", BAR_HEIGHT);
  }

  private void populateExperimentSpotDesign(ModelAndView modelAndView, Experiment exp) {
    ExperimentSpotDesign spotDesign = exp.getExperimentSpotDesign();
    Set<ExperimentSpotDesignReadSpec> readSpecs = spotDesign.getReadSpecs();
    modelAndView.addObject("readSpecs", readSpecs);
  }

  private List<SampleDetailsLineItem> getSampleDetails(Sample sam) {
    List<SampleDetailsLineItem> sampleDetails = new ArrayList<SampleDetailsLineItem>();

    for (IUS ius : sam.getIUS()) {
      Lane lane = ius.getLane();
      SequencerRun sRun = lane.getSequencerRun();
      sampleDetails.add(new SampleDetailsLineItem(sRun, lane, ius));
    }
    return sampleDetails;
  }

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

  public SampleService getSampleService() {
    return sampleService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public LaneService getLaneService() {
    return laneService;
  }

  public void setLaneService(LaneService laneService) {
    this.laneService = laneService;
  }

  public ProcessingService getProcessingService() {
    return processingService;
  }

  public void setProcessingService(ProcessingService processingService) {
    this.processingService = processingService;
  }

  public IUSService getIUSService() {
    return iusService;
  }

  public void setIUSService(IUSService iusService) {
    this.iusService = iusService;
  }

  public WorkflowRunService getWorkflowRunService() {
    return workflowRunService;
  }

  public void setWorkflowRunService(WorkflowRunService workflowRunService) {
    this.workflowRunService = workflowRunService;
  }
}
