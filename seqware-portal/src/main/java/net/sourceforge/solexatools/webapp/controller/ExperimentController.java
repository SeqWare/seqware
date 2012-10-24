package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.ExperimentLibraryDesignService;
import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.ExperimentSpotDesignReadSpecService;
import net.sourceforge.seqware.common.business.ExperimentSpotDesignService;
import net.sourceforge.seqware.common.business.LibrarySelectionService;
import net.sourceforge.seqware.common.business.LibrarySourceService;
import net.sourceforge.seqware.common.business.LibraryStrategyService;
import net.sourceforge.seqware.common.business.PlatformService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentLibraryDesign;
import net.sourceforge.seqware.common.model.ExperimentSpotDesign;
import net.sourceforge.seqware.common.model.ExperimentSpotDesignReadSpec;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.SetNodeIdInSession;
import net.sourceforge.solexatools.validation.CustomIntegerEditor;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * ExperimentController
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ExperimentController extends MultiActionController {
  private StudyService studyService;
  private ExperimentService experimentService;
  private Validator validator;
  private Validator updateValidator;
  private LibrarySelectionService librarySelectionService;
  private LibrarySourceService librarySourceService;
  private LibraryStrategyService libraryStrategyService;
  private PlatformService platformService;
  private ExperimentLibraryDesignService experimentLibraryDesignService;
  private ExperimentSpotDesignService experimentSpotDesignService;
  private ExperimentSpotDesignReadSpecService experimentSpotDesignReadSpecService;

  /**
   * <p>Constructor for ExperimentController.</p>
   */
  public ExperimentController() {
    super();
  }

  /**
   * <p>Constructor for ExperimentController.</p>
   *
   * @param delegate a {@link java.lang.Object} object.
   */
  public ExperimentController(Object delegate) {
    super(delegate);
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
   * <p>Getter for the field <code>validator</code>.</p>
   *
   * @return a {@link org.springframework.validation.Validator} object.
   */
  public Validator getValidator() {
    return validator;
  }

  /**
   * <p>Setter for the field <code>validator</code>.</p>
   *
   * @param validator a {@link org.springframework.validation.Validator} object.
   */
  public void setValidator(Validator validator) {
    this.validator = validator;
  }

  /**
   * <p>Getter for the field <code>updateValidator</code>.</p>
   *
   * @return a {@link org.springframework.validation.Validator} object.
   */
  public Validator getUpdateValidator() {
    return updateValidator;
  }

  /**
   * <p>Setter for the field <code>updateValidator</code>.</p>
   *
   * @param validator a {@link org.springframework.validation.Validator} object.
   */
  public void setUpdateValidator(Validator validator) {
    this.updateValidator = validator;
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
   * <p>Getter for the field <code>librarySelectionService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.LibrarySelectionService} object.
   */
  public LibrarySelectionService getLibrarySelectionService() {
    return librarySelectionService;
  }

  /**
   * <p>Setter for the field <code>librarySelectionService</code>.</p>
   *
   * @param librarySelectionService a {@link net.sourceforge.seqware.common.business.LibrarySelectionService} object.
   */
  public void setLibrarySelectionService(LibrarySelectionService librarySelectionService) {
    this.librarySelectionService = librarySelectionService;
  }

  /**
   * <p>Getter for the field <code>librarySourceService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.LibrarySourceService} object.
   */
  public LibrarySourceService getLibrarySourceService() {
    return librarySourceService;
  }

  /**
   * <p>Setter for the field <code>librarySourceService</code>.</p>
   *
   * @param librarySourceService a {@link net.sourceforge.seqware.common.business.LibrarySourceService} object.
   */
  public void setLibrarySourceService(LibrarySourceService librarySourceService) {
    this.librarySourceService = librarySourceService;
  }

  /**
   * <p>Getter for the field <code>libraryStrategyService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.LibraryStrategyService} object.
   */
  public LibraryStrategyService getLibraryStrategyService() {
    return libraryStrategyService;
  }

  /**
   * <p>Setter for the field <code>libraryStrategyService</code>.</p>
   *
   * @param libraryStrategyService a {@link net.sourceforge.seqware.common.business.LibraryStrategyService} object.
   */
  public void setLibraryStrategyService(LibraryStrategyService libraryStrategyService) {
    this.libraryStrategyService = libraryStrategyService;
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
   * <p>Getter for the field <code>experimentLibraryDesignService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ExperimentLibraryDesignService} object.
   */
  public ExperimentLibraryDesignService getExperimentLibraryDesignService() {
    return experimentLibraryDesignService;
  }

  /**
   * <p>Setter for the field <code>experimentLibraryDesignService</code>.</p>
   *
   * @param experimentLibraryDesignService a {@link net.sourceforge.seqware.common.business.ExperimentLibraryDesignService} object.
   */
  public void setExperimentLibraryDesignService(ExperimentLibraryDesignService experimentLibraryDesignService) {
    this.experimentLibraryDesignService = experimentLibraryDesignService;
  }

  /** {@inheritDoc} */
  protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
    // Uncomment the following line if you're using a Double object
    // binder.registerCustomEditor(Integer.class, new CustomIntegerEditor());
    binder.registerCustomEditor(Integer.TYPE, new CustomIntegerEditor());
    super.initBinder(request, binder);
  }

  // @Override
  /** {@inheritDoc} */
  protected void bind(HttpServletRequest arg0, Object arg1) throws Exception {
    // TODO Auto-generated method stub
    super.bind(arg0, arg1);
  }

  /**
   * Handles the user's request to submit a new study.
   *
   * @param request
   *          HttpServletRequest
   * @param response
   *          HttpServletResponse
   * @param command
   *          Study command object
   * @return ModelAndView
   * @throws java.lang.Exception if any.
   */
  public ModelAndView handleSubmit(HttpServletRequest request, HttpServletResponse response, Experiment command)
      throws Exception {

    Registration registration = Security.getRegistration(request);
    if (registration == null)
      return new ModelAndView("redirect:/login.htm");

    ModelAndView modelAndView = null;
    BindingResult errors = this.validate(request, command);
    if (errors.hasErrors()) {
      Map<String, Object> model = errors.getModel();
      // Experiment experiment = getCurrentExperiment(request);
      model.put("platformList", getPlatformService().list(registration));
      model.put("expLibDesignSelectionList", getLibrarySelectionService().list(registration));
      model.put("expLibDesignSourceList", getLibrarySourceService().list(registration));
      model.put("expLibDesignStrategyList", getLibraryStrategyService().list(registration));

      // experiment.setOwner(registration);
      // Integer studyId = Integer.parseInt(request.getParameter("studyId"));
      // experiment.setStudy(getStudyService().findByID(studyId));
      Integer studyId = Integer.parseInt(request.getParameter("studyId"));
      request.setAttribute("studyId", studyId);
      model.put("strategy", "submit");

      // request.setAttribute(getCommandName(command), experiment);
      modelAndView = new ModelAndView("Experiment", model);
    } else {
      command.setOwner(registration);

      // fill in experiment and linked tables
      populateExperiment(command, request);

      getExperimentService().insert(command);

      // add study id
      Integer studyId = command.getStudy().getStudyId();
      // request.getSession(false).setAttribute("nodeId", "study_" + studyId);

      SetNodeIdInSession.setStudy(studyId, request);

      modelAndView = new ModelAndView("redirect:/myStudyList.htm");

    }

    request.getSession(false).removeAttribute("experiment");

    return modelAndView;
  }

  /**
   * Handles the user's request to reset the experiment page during a new or
   * update experiment.
   *
   * @param command
   *          Experiment command object
   * @param request a {@link javax.servlet.http.HttpServletRequest} object.
   * @param response a {@link javax.servlet.http.HttpServletResponse} object.
   * @return a {@link org.springframework.web.servlet.ModelAndView} object.
   * @throws java.lang.Exception if any.
   */
  public ModelAndView handleReset(HttpServletRequest request, HttpServletResponse response, Experiment command)
      throws Exception {

    Registration registration = Security.getRegistration(request);
    if (registration == null)
      return new ModelAndView("redirect:/login.htm");

    ModelAndView modelAndView = null;
    HashMap<String, Object> model = new HashMap<String, Object>();
    Experiment experiment = getCurrentExperiment(request);
    model.put("platformList", getPlatformService().list(registration));
    model.put("expLibDesignSelectionList", getLibrarySelectionService().list(registration));
    model.put("expLibDesignSourceList", getLibrarySourceService().list(registration));
    model.put("expLibDesignStrategyList", getLibraryStrategyService().list(registration));
    if (experiment.getUpdateTimestamp() == null) {
      experiment.setOwner(registration);
      Integer studyId = Integer.parseInt(request.getParameter("studyId"));
      experiment.setStudy(getStudyService().findByID(studyId));
      request.setAttribute("studyId", studyId);
      model.put("strategy", "submit");
    } else {
      experiment.setPlatformInt(experiment.getPlatform().getPlatformId());
      experiment.setExpLibDesignName(experiment.getExperimentLibraryDesign().getName());
      experiment.setExpLibDesignDesc(experiment.getExperimentLibraryDesign().getDescription());
      experiment.setExpLibDesignProtocol(experiment.getExperimentLibraryDesign().getConstructionProtocol());
      experiment.setExpLibDesignStrategy(experiment.getExperimentLibraryDesign().getStrategy().getLibraryStrategyId());
      experiment.setExpLibDesignSource(experiment.getExperimentLibraryDesign().getSource().getLibrarySourceId());
      experiment.setExpLibDesignSelection(experiment.getExperimentLibraryDesign().getSelection()
          .getLibrarySelectionId());
      experiment.setSpotDesignReadSpec(experiment.getExperimentSpotDesign().getReadSpec());

      request.setAttribute("studyId", experiment.getStudy().getStudyId());
      model.put("swid", experiment.getSwAccession());
      model.put("strategy", "update");
    }
    modelAndView = new ModelAndView("Experiment", model);
    request.setAttribute(getCommandName(command), experiment);
    return modelAndView;
  }

  /**
   * Handles the user's request to cancel the study or the study update page.
   *
   * @param command
   *          Study command object
   * @param request a {@link javax.servlet.http.HttpServletRequest} object.
   * @param response a {@link javax.servlet.http.HttpServletResponse} object.
   * @return a {@link org.springframework.web.servlet.ModelAndView} object.
   * @throws java.lang.Exception if any.
   */
  public ModelAndView handleCancel(HttpServletRequest request, HttpServletResponse response, Study command)
      throws Exception {

    // add study id
    Integer studyId = Integer.parseInt(request.getParameter("studyId"));
    // request.getSession(false).setAttribute("nodeId", "study_" + studyId);
    // SetNodeIdInSession.setStudy(studyId, request);

    return new ModelAndView("redirect:/myStudyList.htm");
  }

  /**
   * Handles the user's request to update their study.
   *
   * @param command
   *          Study command object
   * @param request a {@link javax.servlet.http.HttpServletRequest} object.
   * @param response a {@link javax.servlet.http.HttpServletResponse} object.
   * @return a {@link org.springframework.web.servlet.ModelAndView} object.
   * @throws java.lang.Exception if any.
   */
  public ModelAndView handleUpdate(HttpServletRequest request, HttpServletResponse response, Experiment command)
      throws Exception {

    Registration registration = Security.getRegistration(request);
    if (registration == null)
      return new ModelAndView("redirect:/login.htm");

    ModelAndView modelAndView = null;
    command.setSwAccession(getCurrentExperiment(request).getSwAccession());
    BindingResult errors = this.validateUpdate(request, command);
    if (errors.hasErrors()) {
      Map model = errors.getModel();
      Integer studyId = Integer.parseInt(request.getParameter("studyId"));
      request.setAttribute("studyId", studyId);
      request.setAttribute("swid", getCurrentExperiment(request).getSwAccession());
      model.put("platformList", getPlatformService().list(registration));
      model.put("expLibDesignSelectionList", getLibrarySelectionService().list(registration));
      model.put("expLibDesignSourceList", getLibrarySourceService().list(registration));
      model.put("expLibDesignStrategyList", getLibraryStrategyService().list(registration));
      model.put("strategy", "update");
      modelAndView = new ModelAndView("Experiment", model);
    } else {
      Experiment newExperiment = command;
      Log.info("Print runs in controller. Runs=" + command.getExpectedNumberRuns());
      Experiment oldExperiment = getCurrentExperiment(request);
      if (newExperiment != null && oldExperiment != null) {
        new ServletRequestDataBinder(oldExperiment).bind(request);

        // set variable which convert with String to Integer and Long
        oldExperiment.setExpectedNumberRuns(newExperiment.getExpectedNumberRuns());
        oldExperiment.setExpectedNumberReads(newExperiment.getExpectedNumberReads());

        // fill in experiment and linked tables
        populateExperiment(oldExperiment, request);

        getExperimentService().updateDetached(oldExperiment);
        // getExperimentService().merge(oldExperiment);

        // add experiment id
        // request.getSession(false).setAttribute("nodeId", "exp_" +
        // oldExperiment.getExperimentId());

        // add study id
        // Integer studyId = FindRootUtil.getStudy(oldExperiment).getStudyId();
        // SetNodeIdInSession.setStudy(studyId, request);

        modelAndView = new ModelAndView("redirect:/myStudyList.htm");

      } else {
        modelAndView = new ModelAndView("redirect:/Error.htm");
      }
      request.getSession(false).removeAttribute("experiment");
    }

    // request.getSession(false).removeAttribute("experiment");

    return modelAndView;
  }

  /**
   * Handles the user's request to delete their experiment.
   *
   * @param command
   *          Experiment command object
   * @param request a {@link javax.servlet.http.HttpServletRequest} object.
   * @param response a {@link javax.servlet.http.HttpServletResponse} object.
   * @return a {@link org.springframework.web.servlet.ModelAndView} object.
   * @throws java.lang.Exception if any.
   */
  public ModelAndView handleDelete(HttpServletRequest request, HttpServletResponse response, Study command)
      throws Exception {

    Registration registration = Security.getRegistration(request);
    if (registration == null)
      return new ModelAndView("redirect:/login.htm");

    ModelAndView modelAndView = null;
    HashMap<String, Object> model = new HashMap<String, Object>();
    Experiment experiment = getRequestedExperiment(request);

    ServletContext context = this.getServletContext();
    String deleteRealFiles = context.getInitParameter("delete.files.for.node.deletion");

    if (experiment != null) {
      if (registration.equals(experiment.getOwner()) || registration.isLIMSAdmin()) {
        // Integer studyId = experiment.getStudy().getStudyId();
        getExperimentService().delete(experiment, deleteRealFiles);
        // SetNodeIdInSession.setStudy(studyId, request);
      }
    }
    modelAndView = new ModelAndView("redirect:/myStudyList.htm", model);
    return modelAndView;
  }

  private void populateExperiment(Experiment command, HttpServletRequest request) {

    // setup parent study
    command.setStudy(getStudyService().findByID(Integer.parseInt(request.getParameter("studyId"))));

    // setup platform
    command.setPlatform(getPlatformService().findByID(command.getPlatformInt()));

    // setup library design
    ExperimentLibraryDesign eld = command.getExperimentLibraryDesign();
    if (eld == null) {
      eld = new ExperimentLibraryDesign();
    }
    eld.setDescription(command.getExpLibDesignDesc());
    eld.setName(command.getExpLibDesignName());
    eld.setConstructionProtocol(command.getExpLibDesignProtocol());

    // now setup library selectionlibrarySelectionService
    eld.setSelection(librarySelectionService.findByID(command.getExpLibDesignSelection()));

    // now setup library strategy
    eld.setStrategy(libraryStrategyService.findByID(command.getExpLibDesignStrategy()));

    // now setup library source
    eld.setSource(librarySourceService.findByID(command.getExpLibDesignSource()));

    // save the new object
    if (command.getExperimentLibraryDesign() == null) {
      getExperimentLibraryDesignService().insert(eld);
    } else {
      getExperimentLibraryDesignService().update(eld);
    }

    // now associate with experiment
    command.setExperimentLibraryDesign(eld);

    // spot design
    // the trick to this is parsing the read spec and then correctly populating
    // the
    // experiment_spot_design and child experiment_spot_design_read_spec rows
    String readSpec = command.getSpotDesignReadSpec();
    ExperimentSpotDesign esd = command.getExperimentSpotDesign();
    if (esd == null) {
      esd = new ExperimentSpotDesign();
    }
    esd.setReadSpec(readSpec);

    // save this
    if (command.getExperimentSpotDesign() == null) {
      getExperimentSpotDesignService().insert(esd);
    } else {
      getExperimentSpotDesignService().update(esd);
    }

    // clean out previous read specs if they exist
    for (ExperimentSpotDesignReadSpec rs : esd.getReadSpecs()) {
      // esd.getReadSpecs().remove(rs);
      getExperimentSpotDesignReadSpecService().delete(rs);
    }

    // now parse each of the ReadSpecs
    String[] tokens = readSpec.split("}");
    Pattern p = Pattern.compile("\\{([FRAPLBOfraplbo\\.]+)\\**(\\d*)");
    int readIndex = 0;
    int position = 1;
    for (String token : tokens) {
      Matcher matcher = p.matcher(token);
      matcher.find();
      if (matcher.matches() && matcher.groupCount() == 2 && token.indexOf("..") == -1) {
        String type = matcher.group(1);
        if (!"\\.\\.".equals(type)) { // just skipping '{..}'
          int count = Integer.parseInt(matcher.group(2));
          ExperimentSpotDesignReadSpec esdrs = new ExperimentSpotDesignReadSpec();
          esdrs.setExperimentSpotDesign(esd);
          esdrs.setBaseCoord(position);
          esdrs.setReadIndex(readIndex);
          esdrs.setLength(count);
          // FIXME: hard coded!!! I don't know if I'm populating this
          // correctly!!!
          esdrs.setReadClass("Application Read");
          if ("F".equals(type)) {
            esdrs.setReadType("Forward");
          } else if ("R".equals(type)) {
            esdrs.setReadType("Reverse");
          } else if ("A".equals(type)) {
            esdrs.setReadType("Adapter");
            esdrs.setReadClass("Technical Read");
          } else if ("P".equals(type)) {
            esdrs.setReadType("Primer");
            esdrs.setReadClass("Technical Read");
          } else if ("L".equals(type)) {
            esdrs.setReadType("Linker");
            esdrs.setReadClass("Technical Read");
          } else if ("B".equals(type)) {
            esdrs.setReadType("BarCode");
          } else {
            esdrs.setReadType("Other");
          }
          readIndex++;
          esdrs.setBaseCoord(position);
          // add
          this.getExperimentSpotDesignReadSpecService().insert(esdrs);
          position += count;
          esd.getReadSpecs().add(esdrs);
        }
      }
    }
    // specify the correct number of reads
    esd.setReadsPerSpot(readIndex);

    // update
    this.getExperimentSpotDesignService().update(esd);

    // now add esd to command
    command.setExperimentSpotDesign(esd);
  }

  /**
   * Validates a study.
   * 
   * @param command
   *          the Command instance as an Object
   * 
   * @return BindingResult validation errors
   */
  private BindingResult validate(HttpServletRequest request, Object command) {
    BindingResult errors = new BindException(command, getCommandName(command));
    ValidationUtils.invokeValidator(getValidator(), command, errors);
    return errors;
  }

  private BindingResult validateUpdate(HttpServletRequest request, Object command) {
    BindingResult errors = new BindException(command, getCommandName(command));
    ValidationUtils.invokeValidator(getUpdateValidator(), command, errors);
    return errors;
  }

  /**
   * Gets the emailAddress from the study in the session.
   * 
   * @param request
   *          HttpServletRequest
   * 
   * @return the emailAddress from the study in the session, or null if there is
   *         no study in the session
   */
  private String getNameFromSession(HttpServletRequest request) {
    return getCurrentExperiment(request).getTitle();
  }

  /**
   * Gets the experiment from the session.
   * 
   * @param request
   *          HttpServletRequest
   * 
   * @return instance of Experiment from the session, or a new instance if the
   *         experiment is not in the session (e.g. the user is not logged in)
   */
  private Experiment getCurrentExperiment(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      Object experiment = session.getAttribute("experiment");
      if (experiment != null) {
        return (Experiment) experiment;
      }
    }
    return new Experiment();
  }

  private Experiment getRequestedExperiment(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    Experiment experiment = null;
    String id = (String) request.getParameter("objectId");

    if (id != null) {
      Integer experimentID = Integer.parseInt(id);
      experiment = getExperimentService().findByID(experimentID);
    }
    return experiment;
  }

  /**
   * <p>Getter for the field <code>experimentSpotDesignService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ExperimentSpotDesignService} object.
   */
  public ExperimentSpotDesignService getExperimentSpotDesignService() {
    return experimentSpotDesignService;
  }

  /**
   * <p>Setter for the field <code>experimentSpotDesignService</code>.</p>
   *
   * @param experimentSpotDesignService a {@link net.sourceforge.seqware.common.business.ExperimentSpotDesignService} object.
   */
  public void setExperimentSpotDesignService(ExperimentSpotDesignService experimentSpotDesignService) {
    this.experimentSpotDesignService = experimentSpotDesignService;
  }

  /**
   * <p>Getter for the field <code>experimentSpotDesignReadSpecService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.ExperimentSpotDesignReadSpecService} object.
   */
  public ExperimentSpotDesignReadSpecService getExperimentSpotDesignReadSpecService() {
    return experimentSpotDesignReadSpecService;
  }

  /**
   * <p>Setter for the field <code>experimentSpotDesignReadSpecService</code>.</p>
   *
   * @param experimentSpotDesignReadSpecService a {@link net.sourceforge.seqware.common.business.ExperimentSpotDesignReadSpecService} object.
   */
  public void setExperimentSpotDesignReadSpecService(
      ExperimentSpotDesignReadSpecService experimentSpotDesignReadSpecService) {
    this.experimentSpotDesignReadSpecService = experimentSpotDesignReadSpecService;
  }

}
