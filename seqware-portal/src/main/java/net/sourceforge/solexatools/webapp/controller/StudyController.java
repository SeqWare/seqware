package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.business.ShareStudyService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.business.StudyTypeService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.ShareStudy;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyType;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.SetNodeIdInSession;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * StudyController
 */

public class StudyController extends MultiActionController {
  private StudyService studyService;
  private StudyTypeService studyTypeService;
  private ShareStudyService shareStudyService;
  private RegistrationService registrationService;

  private Validator studyNewValidator;
  private Validator studyUpdateValidator;

  public StudyController() {
    super();
  }

  public StudyController(Object delegate) {
    super(delegate);
  }

  public StudyService getStudyService() {
    return studyService;
  }

  public void setStudyService(StudyService studyService) {
    this.studyService = studyService;
  }

  public ShareStudyService getShareStudyService() {
    return shareStudyService;
  }

  public void setShareStudyService(ShareStudyService shareStudyService) {
    this.shareStudyService = shareStudyService;
  }

  public void setRegistrationService(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  public Validator getValidator() {
    return studyNewValidator;
  }

  public void setValidator(Validator validator) {
    this.studyNewValidator = validator;
  }

  public Validator getUpdateValidator() {
    return studyUpdateValidator;
  }

  public void setUpdateValidator(Validator validator) {
    this.studyUpdateValidator = validator;
  }

  public StudyTypeService getStudyTypeService() {
    return studyTypeService;
  }

  public void setStudyTypeService(StudyTypeService studyTypeService) {
    this.studyTypeService = studyTypeService;
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
   * 
   * @return ModelAndView
   * 
   * @throws Exception
   */
  public ModelAndView handleSubmit(HttpServletRequest request, HttpServletResponse response, Study command)
      throws Exception {

    Registration registration = Security.getRegistration(request);
    if (registration == null)
      return new ModelAndView("redirect:/login.htm");

    ModelAndView modelAndView = null;
    BindingResult errors = this.validateStudy(request, command);
    if (errors.hasErrors()) {
      Map<String, Object> model = errors.getModel();
      model.put("studyTypeList", getStudyTypeService().list(registration));
      // Study study = getCurrentStudy(request);
      model.put("strategy", "submit");
      modelAndView = new ModelAndView("Study", model);
      // request.setAttribute(getCommandName(command), study);
    } else {
      Integer studyTypeInt = command.getExistingTypeInt();
      StudyType st = getStudyTypeService().findByID(studyTypeInt);
      command.setExistingType(st);
      command.setOwner(registration);
      getStudyService().insert(command);
      modelAndView = new ModelAndView("redirect:/myStudyList.htm");
    }

    request.getSession(false).removeAttribute("study");

    return modelAndView;
  }

  /**
   * Handles the user's request to reset the study page during a new or update
   * study.
   * 
   * @param command
   *          Study command object
   */
  public ModelAndView handleReset(HttpServletRequest request, HttpServletResponse response, Study command)
      throws Exception {

    Registration registration = Security.getRegistration(request);
    if (registration == null)
      return new ModelAndView("redirect:/login.htm");

    ModelAndView modelAndView = null;
    HashMap<String, Object> model = new HashMap<String, Object>();
    Study study = getCurrentStudy(request);
    model.put("studyTypeList", getStudyTypeService().list(registration));
    if (study.getUpdateTimestamp() == null) {
      model.put("strategy", "submit");
    } else {
      // study.setExistingTypeInt(study.getExistingType().getStudyTypeId());
      model.put("strategy", "update");
      model.put("swid", study.getSwAccession());
      // modelAndView = new ModelAndView("StudyUpdate");
    }
    modelAndView = new ModelAndView("Study", model);
    request.setAttribute(getCommandName(command), study);
    return modelAndView;
  }

  /**
   * Handles the user's request to cancel the study or the study update page.
   * 
   * @param command
   *          Study command object
   */
  public ModelAndView handleCancel(HttpServletRequest request, HttpServletResponse response, Study command)
      throws Exception {

    return new ModelAndView("redirect:/myStudyList.htm"); // redirect:/Welcome.htm
  }

  /**
   * Handles the user's request to update their study.
   * 
   * @param command
   *          Study command object
   */
  public ModelAndView handleUpdate(HttpServletRequest request, HttpServletResponse response, Study command)
      throws Exception {

    Registration registration = Security.getRegistration(request);
    if (registration == null)
      return new ModelAndView("redirect:/login.htm");

    ModelAndView modelAndView = null;
    command.setSwAccession(getCurrentStudy(request).getSwAccession());
    BindingResult errors = this.validateUpdateStudy(request, command);
    if (errors.hasErrors()) {
      Map<String, Object> model = errors.getModel();
      request.setAttribute("swid", getCurrentStudy(request).getSwAccession());
      model.put("studyTypeList", getStudyTypeService().list(registration));
      model.put("strategy", "update");
      modelAndView = new ModelAndView("Study", model);
    } else {
      Study newStudy = command;
      Study oldStudy = getCurrentStudy(request);
      if (newStudy != null && oldStudy != null) {
        Integer studyTypeInt = newStudy.getExistingTypeInt();
        StudyType st = getStudyTypeService().findByID(studyTypeInt);
        new ServletRequestDataBinder(oldStudy).bind(request);
        oldStudy.setExistingType(st);
        oldStudy.setOwner(registration);
        getStudyService().merge(oldStudy);

        // add study id
        // request.getSession(false).setAttribute("nodeId", "study_" +
        // oldStudy.getStudyId());

        modelAndView = new ModelAndView("redirect:/myStudyList.htm");
      } else {
        modelAndView = new ModelAndView("redirect:/Error.htm");
      }
      request.getSession(false).removeAttribute("study");
    }

    // request.getSession(false).removeAttribute("study");

    return modelAndView;
  }

  /**
   * Handles the user's request to delete their study.
   * 
   * @param command
   *          Study command object
   */
  public ModelAndView handleDelete(HttpServletRequest request, HttpServletResponse response, Study command)
      throws Exception {

    Registration registration = Security.getRegistration(request);
    if (registration == null)
      return new ModelAndView("redirect:/login.htm");

    ModelAndView modelAndView = null;
    HashMap<String, Object> model = new HashMap<String, Object>();
    Study study = getRequestedStudy(request);

    ServletContext context = this.getServletContext();
    String deleteRealFiles = context.getInitParameter("delete.files.for.node.deletion");

    if (study != null) {
      if (registration.equals(study.getOwner()) || registration.isLIMSAdmin()) {
        getStudyService().delete(study, deleteRealFiles);
      }
    }
    modelAndView = new ModelAndView("redirect:/myStudyList.htm", model);
    return modelAndView;
  }

  /**
   * Handles the user's request to share their study.
   * 
   * @param command
   *          Study command object
   */
  public ModelAndView handleShare(HttpServletRequest request, HttpServletResponse response, Study command)
      throws Exception {

    Registration registration = Security.getRegistration(request);
    if (registration == null)
      return new ModelAndView("redirect:/login.htm");

    ModelAndView modelAndView = null;
    HashMap<String, Object> model = new HashMap<String, Object>();
    Study study = getRequestedStudy(request);

    if (study != null) {
      Integer ownerId = study.getOwner().getRegistrationId();
      Integer registrationId = registration.getRegistrationId();
      String[] emails = getRequestedEmails(request);

      if (registrationId.equals(ownerId) && emails != null) {
        for (String email : emails) {
          // if(!getShareStudyService().isExistsShare(study.getStudyId(),
          // email)){
          ShareStudy shareStudy = new ShareStudy();
          Registration shareReg = registrationService.findByEmailAddress(email);
          shareStudy.setRegistration(shareReg);
          shareStudy.setActive(true);
          // shareStudy.setEmail(email);
          shareStudy.setStudyId(study.getStudyId());
          getShareStudyService().insert(shareStudy);
          // }
        }
      }
    }
    SetNodeIdInSession.removeStudy(request);

    modelAndView = new ModelAndView("redirect:/myStudyList.htm", model);
    return modelAndView;
  }

  /**
   * Validates a new study.
   * 
   * @param command
   *          the Command instance as an Object
   * 
   * @return BindingResult validation errors
   */
  private BindingResult validateStudy(HttpServletRequest request, Object command) {
    BindingResult errors = new BindException(command, getCommandName(command));
    ValidationUtils.invokeValidator(getValidator(), command, errors);
    return errors;
  }

  /**
   * Validates an update study.
   * 
   * @param command
   *          the Command instance as an Object
   * 
   * @return BindingResult validation errors
   */
  private BindingResult validateUpdateStudy(HttpServletRequest request, Object command) {
    BindingResult errors = new BindException(command, getCommandName(command));
    ValidationUtils.invokeValidator(getUpdateValidator(), command, errors);
    return errors;
  }

  /**
   * Gets the study from the session.
   * 
   * @param request
   *          HttpServletRequest
   * 
   * @return instance of Study from the session, or a new instance if the study
   *         is not in the session (e.g. the user is not logged in)
   */
  private Study getCurrentStudy(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      Object study = session.getAttribute("study");
      if (study != null) {
        return (Study) study;
      }
    }
    return new Study();
  }

  private Study getRequestedStudy(HttpServletRequest request) {
    Study study = null;
    String id = (String) request.getParameter("objectId");

    if (id != null) {
      Integer studyID = Integer.parseInt(id);
      study = getStudyService().findByID(studyID);
    }
    return study;
  }

  private String[] getRequestedEmails(HttpServletRequest request) {
    String[] emails = (String[]) request.getParameterValues("emails[]");
    return emails;
  }
}
