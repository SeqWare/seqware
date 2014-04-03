package net.sourceforge.solexatools.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.Constant;
import net.sourceforge.solexatools.util.PageInfo;
import net.sourceforge.solexatools.util.PaginationUtil;
import net.sourceforge.solexatools.util.StudyHtmlUtil;
import net.sourceforge.solexatools.webapp.metamodel.SampleDetailsLineItem;
import net.sourceforge.solexatools.webapp.metamodel.SampleDetailsWorkflowLineItems;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>StudyListDetailsController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
@SuppressWarnings("deprecation")
public class StudyListDetailsController extends BaseCommandController {
  private StudyService studyService;
  private ExperimentService experimentService;
  private SampleService sampleService;
  private LaneService laneService;
  private ProcessingService processingService;
  private IUSService iusService;

  /**
   * <p>Constructor for StudyListDetailsController.</p>
   */
  public StudyListDetailsController() {
    super();
    setSupportedMethods(new String[] { METHOD_GET });
  }


  private Boolean getRequestedAsc(HttpServletRequest request) {
    Boolean isAsc = null;
    String strAsc = request.getParameter("asc");

    if ("true".equals(strAsc)) {
      isAsc = true;
    } else if ("false".equals(strAsc)) {
      isAsc = false;
    }
    return isAsc;
  }

  /**
   * <p>saveAscInSession.</p>
   *
   * @param request a {@link javax.servlet.http.HttpServletRequest} object.
   * @param attrNameInSession a {@link java.lang.String} object.
   * @return a {@link java.lang.Boolean} object.
   */
  protected Boolean saveAscInSession(HttpServletRequest request, String attrNameInSession) {
    Boolean isAsc = getRequestedAsc(request);
    if (isAsc != null) {
      request.getSession(false).setAttribute(attrNameInSession, isAsc);
    }
    return isAsc(request, attrNameInSession);
  }

  private Boolean isAsc(HttpServletRequest request, String attrNameInSession) {
    Boolean isAsc = (Boolean) request.getSession(false).getAttribute(attrNameInSession);
    if (isAsc == null) {
      isAsc = true;
    }
    return isAsc;
  }

  /** {@inheritDoc} */
  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // Registration registration = Security.requireRegistration(request,
    // response);
    Registration registration = Security.getRegistration(request);
    if (registration == null) {
      // return new ModelAndView("redirect:/login.htm");
      return new ModelAndView("StudyListRoot");
    }

    /**
     * Pass registration so that we can filter the list if its appropriate to do
     * so.
     */
    String type = getTypeParameter(request);
    if (type != null) {
      if ("list".equals(type)) {
        setTypeSession(request, type);
      } else {
        setTypeSession(request, "tree");
      }
    }

    Experiment exp = new Experiment();
    Sample sam = new Sample();
    // Lane lane = new Lane();
    IUS ius = new IUS();
    Processing proc = new Processing();
    Map<WorkflowRun, Set<Processing>> wfrProc = new HashMap<WorkflowRun, Set<Processing>>();

    PageInfo pageInfo = null;
    Boolean isHasError = false;
    String errorMessage = "";

    List<Study> listAll = new ArrayList<Study>();
    List<Study> listView = new ArrayList<Study>();

    String root = (String) request.getParameter("root");
    System.err.println("ROOT: " + root);
    if (root == null || "".equals(root) || "source".equals(root)) {
      MessageSourceAccessor ma = this.getMessageSourceAccessor();
      String nameOneItem = "study.list.pagination.nameOneItem";
      String nameLotOfItem = "study.list.pagination.nameLotOfItem";
      String typeList = getRequestedTypeList(request);
      if (typeList.equals("mylist")) {
        Boolean isAsc = saveAscInSession(request, "ascMyListStudy");
        listAll = getStudyService().list(registration, isAsc);
        listView = PaginationUtil.subList(request, "myStudiesPage", listAll);
        listView = loadNode(listView, registration, request);
        pageInfo = PaginationUtil.getPageInfo(request, "myStudiesPage", listView, listAll, nameOneItem, nameLotOfItem,
            ma);

        // set error if want
        if (listAll.size() == 0) {
          isHasError = true;
          errorMessage = this.getMessageSourceAccessor().getMessage("study.list.required.one.item");
        }
      }
      if (typeList.equals("mysharelist")) {
        Boolean isAsc = saveAscInSession(request, "ascMyShareListStudy");
        listAll = getStudyService().listMyShared(registration, isAsc);
        listView = PaginationUtil.subList(request, "mySharedStudyPage", listAll);
        listView = loadNode(listAll, registration, request);
        pageInfo = PaginationUtil.getPageInfo(request, "mySharedStudyPage", listView, listAll, nameOneItem,
            nameLotOfItem, ma);
      }
      if (typeList.equals("bymesharelist")) {
        Boolean isAsc = saveAscInSession(request, "ascByMeShareListStudy");
        listAll = getStudyService().listSharedWithMe(registration, isAsc);
        listView = PaginationUtil.subList(request, "studySharedWithMe", listAll);
        pageInfo = PaginationUtil.getPageInfo(request, "studySharedWithMe", listView, listAll, nameOneItem,
            nameLotOfItem, ma);
      }
    } else {
      if (root.indexOf("ae_") != -1) {
        proc = getProcessingService().findByID(Constant.getId(root));
        fillWorkflowProcessingMap(proc, wfrProc);
      } else if (root.indexOf("ius_") != -1) {
        ius = getIUSService().findByID(Constant.getId(root));
      } else if (root.indexOf("sam_") != -1) {
        sam = getSampleService().findByID(Constant.getId(root));
        System.out.println("	CHILD SIZE = " + sam.getChildren().size());
      } else if (root.indexOf("exp_") != -1) {
        exp = getExperimentService().findByID(Constant.getId(root));
      } else {
        Study s = getStudyService().findByID(Constant.getId(root));
        listView.add(s);
      }
    }
    // System.err.println("Study length: "+listAll.size());
    ModelAndView modelAndView;
    if (root.indexOf("ae_") != -1) {
      System.err.println("RENDERING INDIVIDUAL File with Processing");
      modelAndView = new ModelAndView("StudyListFileProcessing");
      modelAndView.addObject("processing", proc);
      modelAndView.addObject("wfrproc", wfrProc);
      modelAndView.addObject("wfrprockeys", wfrProc.keySet());
      // modelAndView.addObject("typeTree", "st");
    } else if (root.indexOf("ius_") != -1) {
      System.err.println("RENDERING INDIVIDUAL IUS");
      modelAndView = new ModelAndView("StudyListProcessing");
      modelAndView.addObject("ius", ius);
      // modelAndView.addObject("typeTree", "st");
    } else if (root.indexOf("sam_") != -1) {
      System.err.println("RENDERING INDIVIDUAL Sample");
      if (getTypeSession(request).equals("list")) {
        modelAndView = new ModelAndView("StudyListFlatSampleDetails");
        modelAndView.addObject("sample", sam);
        List<SampleDetailsLineItem> sampleLineItems = getSampleDetails(sam);
        modelAndView.addObject("lineItems", sampleLineItems);
        SampleDetailsWorkflowLineItems wfLineItems = new SampleDetailsWorkflowLineItems(sam);
        for (IUS sampleIus : sam.getIUS()) {
          wfLineItems.addProcessings(sampleIus);
        }

        modelAndView.addObject("workflows", wfLineItems);
      } else {
        modelAndView = new ModelAndView("StudyListIUS");
        modelAndView.addObject("sample", sam);
      }
    } else if (root.indexOf("exp_") != -1) {
      System.err.println("RENDERING INDIVIDUAL EXPERIMENT");
      modelAndView = new ModelAndView("StudyListSample");
      modelAndView.addObject("experiment", exp);
    } else if (root != null && !"".equals(root) && !"source".equals(root) && Integer.parseInt(root) > 0) {
      System.err.println("RENDERING INDIVIDUAL STUDY");
      modelAndView = new ModelAndView("StudyListDetails");
    } else {
      System.err.println("RENDERING ALL STUDIES");
      modelAndView = new ModelAndView("StudyListRoot");
      modelAndView.addObject("pageInfo", pageInfo);
    }

    modelAndView.addObject("typeTree", "st");
    modelAndView.addObject("typeList", getTypeSession(request));

    // set error data
    modelAndView.addObject("isHasError", isHasError);
    modelAndView.addObject("errorMessage", errorMessage);

    modelAndView.addObject("isBulkPage", false);
    modelAndView.addObject("studies", listView);
    modelAndView.addObject("registration", registration);

    return modelAndView;
  }

  /**
   * <p>fillWorkflowProcessingMap.</p>
   *
   * @param proc a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @param wfrProc a {@link java.util.Map} object.
   */
  protected void fillWorkflowProcessingMap(Processing proc, Map<WorkflowRun, Set<Processing>> wfrProc) {
    for (Processing child : proc.getChildren()) {
      Set<Processing> processings = wfrProc.get(child.getWorkflowRun());
      if (processings == null) {
        processings = new HashSet<Processing>();
      }
      processings.add(child);
      if (child.getWorkflowRun() != null) {
        wfrProc.put(child.getWorkflowRun(), processings);
      }
    }
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

  private List<Study> loadNode(List<Study> list, Registration registration, HttpServletRequest request) {
    Integer openStudyId = (Integer) request.getSession(false).getAttribute("rootStudyId");
    // Object obj = request.getSession(false).getAttribute("nodeObject");
    String objId = (String) request.getSession(false).getAttribute("objectId");

    List<String> listStudyNodeId = (List<String>) request.getSession(false).getAttribute("listStudyNodeId");

    String treeType = getTypeSession(request);

    openStudyId = null;
    objId = null;
    if (listStudyNodeId != null) {
      System.out.println("End Study id = " + Constant.getId(getEndId(listStudyNodeId)));
      System.out.println("Start  id = " + getSecondId(listStudyNodeId));
      openStudyId = Constant.getId(getEndId(listStudyNodeId));
      objId = getSecondId(listStudyNodeId);

      if (objId.indexOf("wfr_") != -1) {
        objId = listStudyNodeId.get(2);
      }
    }

    // test open seq
    // openStudyId = 1;
    // objId = "fl_46159";
    // objId = "seq_1995";
    // objId = "ae_53883";
    // objId = "ae_53851";
    // objId = "ae_53966";

    if (openStudyId != null) {
      System.out.println("rootStudyId = " + openStudyId);
      for (Study study : list) {
        if (openStudyId.equals(study.getStudyId())) {
          // study.setHtml(TreeNodeHtmlUtil.getHtml(obj, registration));
          if (objId.indexOf("study_") != -1) {
            // Integer id = Integer.parseInt(objId);
            System.out.println("	SET STUDY HTML");
            study.setHtml(StudyHtmlUtil.getHtml(study, registration, listStudyNodeId, treeType));
          }
          if (objId.indexOf("exp_") != -1) {
            System.out.println("	SET EXP HTML");
            Experiment currObj = getExperimentService().findByID(Constant.getId(objId));
            study.setHtml(StudyHtmlUtil.getHtml(currObj, registration, listStudyNodeId, treeType));
          }
          if (objId.indexOf("sam_") != -1) {
            System.out.println("	SET SAMPLE HTML");
            Sample currObj = getSampleService().findByID(Constant.getId(objId));
            study.setHtml(StudyHtmlUtil.getHtml(currObj, registration, listStudyNodeId, treeType));
          }
          if (objId.indexOf("ius_") != -1) {
            System.out.println("	SET IUS HTML");
            IUS currObj = getIUSService().findByID(Constant.getId(objId));
            study.setHtml(StudyHtmlUtil.getHtml(currObj, registration, listStudyNodeId, treeType));
          }
          if (objId.indexOf("ae_") != -1) {
            System.out.println("	SET Processing HTML");
            Processing currObj = getProcessingService().findByID(Constant.getId(objId));
            study.setHtml(StudyHtmlUtil.getHtml(currObj, registration, listStudyNodeId, treeType));
          }
          if (objId.indexOf("aefl_") != -1) {
            System.out.println("	SET Processing FL HTML");
            Processing currObj = getProcessingService().findByID(Constant.getId(objId));
            study.setHtml(StudyHtmlUtil.getFileHtml(currObj, registration, listStudyNodeId, treeType));
          }
        }
      }
    }
    return list;
  }

  private String getSecondId(List<String> ids) {
    String id = null;
    if (ids != null) {
      id = ids.get(1);
    }
    return id;
  }

  private String getEndId(List<String> ids) {
    String id = null;
    if (ids != null) {
      id = ids.get(ids.size() - 1);
    }
    return id;
  }

  /**
   * <p>getRequestedTypeList.</p>
   *
   * @param request a {@link javax.servlet.http.HttpServletRequest} object.
   * @return a {@link java.lang.String} object.
   */
  protected String getRequestedTypeList(HttpServletRequest request) {
    String typeList = (String) request.getParameter("typeList");
    if (typeList == null) {
      typeList = "";
    }
    return typeList;
  }

  private String getTypeParameter(HttpServletRequest request) {
    String type = (String) request.getParameter("type");
    return type;
  }

  private String getSearchCriteria(HttpServletRequest request) {
    String search = (String) request.getParameter("search");
    if (search == null) {
      search = "";
    }
    return search;
  }

  private void setTypeSession(HttpServletRequest request, String type) {
    request.getSession(false).setAttribute("typeList", type);
  }

  private String getTypeSession(HttpServletRequest request) {
    String type = (String) request.getSession(false).getAttribute("typeList");
    if (type == null) {
      type = "tree";
    }
    return type;
  }

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
   * <p>getIUSService.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.IUSService} object.
   */
  public IUSService getIUSService() {
    return iusService;
  }

  /**
   * <p>setIUSService.</p>
   *
   * @param iusService a {@link net.sourceforge.seqware.common.business.IUSService} object.
   */
  public void setIUSService(IUSService iusService) {
    this.iusService = iusService;
  }
}
