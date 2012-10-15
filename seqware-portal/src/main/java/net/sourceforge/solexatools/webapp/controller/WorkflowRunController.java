package	net.sourceforge.solexatools.webapp.controller;				// -*- tab-width: 4 -*-
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.RegistrationDTO;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunWizardDTO;
import net.sourceforge.solexatools.Security;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * <p>WorkflowRunController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowRunController extends MultiActionController {
	private SequencerRunService	sequencerRunService;
	private Validator			validator;

	/**
	 * Handles the user's request to submit a new registration.
	 *
	 * @param command RegistrationDTO command object
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @return a {@link org.springframework.web.servlet.ModelAndView} object.
	 * @throws java.lang.Exception if any.
	 */
	public ModelAndView handleSubmit(HttpServletRequest		request,
									 HttpServletResponse	response,
									 SequencerRunWizardDTO		command)
		throws Exception {

		ModelAndView modelAndView;
		HttpSession session   = request.getSession(false);
		
		BindingResult errors = this.validateRegistration(request, command);
		if (errors.hasErrors()) {
			Map model = errors.getModel();
			// I think this is doing the JSP redirect!?
			modelAndView = new ModelAndView("SequencerRunWizard", model);
		} else {
			// TODO: need to translate form DTO to SequencerRun object for persistence
		  getSequencerRunService().insert(command);
		  System.err.println("sequencerrunid: "+command.getSequencerRunId());
		  //SequencerRun seqRun = getSequencerRunService().findByID(command.getSequencerRunId());
		  // now save this SequencerRun in the session
		  session.setAttribute("sequencerRun", command);
		  //modelAndView = new ModelAndView("redirect:/sequencerRunList.htm");
		  modelAndView = new ModelAndView("redirect:/sequencerRunWizardEdit.htm?sequencerRunId="+command.getSequencerRunId());
		}

		return modelAndView;
	}

	/**
	 * Handles the user's request to reset the registration page during a new or
	 * update registration.
	 *
	 * @param command RegistrationDTO command object
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @return a {@link org.springframework.web.servlet.ModelAndView} object.
	 * @throws java.lang.Exception if any.
	 */
	public ModelAndView handleReset(HttpServletRequest	request,
									HttpServletResponse	response,
									RegistrationDTO		command)
		throws Exception {

		/* ModelAndView	modelAndView	= null;
		RegistrationDTO	registration	= getCurrentRegistration(request);
		if (registration.getUpdateTimestamp() == null) {
			modelAndView = new ModelAndView("Registration");
		} else {
			modelAndView = new ModelAndView("RegistrationUpdate");
		}
		request.setAttribute(getCommandName(command), registration);
		*/
		
	  // TODO: fill in this method
		ModelAndView  modelAndView  = new ModelAndView("redirect:/sequencerRunList.htm");
		return modelAndView;
	}

	/**
	 * Handles the user's request to cancel the registration
	 * or the registration update page.
	 *
	 * @param command RegistrationDTO command object
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @return a {@link org.springframework.web.servlet.ModelAndView} object.
	 * @throws java.lang.Exception if any.
	 */
	public ModelAndView handleCancel(HttpServletRequest request,
									 HttpServletResponse response,
									 RegistrationDTO command)
		throws Exception {
		return new ModelAndView("redirect:/sequencerRunList.htm");
	}

	/**
	 * Handles the user's request to update their registration.
	 *
	 * @param command RegistrationDTO command object
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @return a {@link org.springframework.web.servlet.ModelAndView} object.
	 * @throws java.lang.Exception if any.
	 */
	public ModelAndView handleUpdate(HttpServletRequest request,
									 HttpServletResponse response,
									 SequencerRun command)
		throws Exception {

	  Registration registration = Security.getRegistration(request);
    if(registration == null)
      return new ModelAndView("redirect:/login.htm");
    
		ModelAndView modelAndView = null;
		// TODO: need to validate
		/*BindingResult errors = this.validateRegistration(request, command);
		if (errors.hasErrors()) {
			Map model = errors.getModel();
			modelAndView = new ModelAndView("RegistrationUpdate", model);
		} else {*/
		
		  
		  SequencerRun oldSequencerRun = getRequestedSequencerRun(request);
		  SequencerRun newSequencerRun = command;
		  HttpSession session   = request.getSession(false);

		  System.err.println("ID: "+(String)request.getParameter("sequencerRunId"));
		  
		  System.err.println("oldSeqRun: "+oldSequencerRun.getSequencerRunId());
		  
			if (oldSequencerRun != null && newSequencerRun != null) {
			  
			  new ServletRequestDataBinder(oldSequencerRun).bind(request);	  
			  getSequencerRunService().update(oldSequencerRun);
			  
			  session.setAttribute("sequencerRun", command);
	      //modelAndView = new ModelAndView("redirect:/sequencerRunList.htm");
	      modelAndView = new ModelAndView("redirect:/sequencerRunWizardEdit.htm?sequencerRunId="+command.getSequencerRunId());
	      
			} else {
				modelAndView = new ModelAndView("redirect:/Error.htm");
			}
		//}
		
		// TODO: fill in this method
		request.getSession(false).removeAttribute("sequencerRun");
		return modelAndView;
	}

	/**
	 * Validates a registration.
	 * @return BindingResult validation errors
	 */
	private BindingResult validateRegistration(HttpServletRequest request, Object command) {

		BindingResult errors = new BindException(command, getCommandName(command));
		
		/* ValidationUtils.invokeValidator(getValidator(), command, errors);

		if (!errors.hasErrors()) {

			RegistrationDTO registration = (RegistrationDTO) command;

			// Make sure that the email address has not yet been used.
			if (getRegistrationService()
				.hasEmailAddressBeenUsed(getEmailAddressFromSession(request),
				registration.getEmailAddress())) {
				errors.reject("error.registration.email.used");
			}
		}
    */
		return errors;
	}

	/**
	 * Gets the emailAddress from the registration in the session.
	 * @return the emailAddress from the registration in the session, or null if
	 * there is no registration in the session
	 */
	private String getEmailAddressFromSession(HttpServletRequest request) {
		return getCurrentRegistration(request).getEmailAddress();
	}

	/**
	 * Gets the registration from the session.
	 * @return instance of RegistrationDTO from the session, or a new instance
	 * if the registration is not in the session (e.g. the user is not logged in)
	 */
	private RegistrationDTO getCurrentRegistration(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object registration = session.getAttribute("registration");
			if (registration != null) {
				return (RegistrationDTO)registration;
			}
		}
		return new RegistrationDTO();
	}

  private SequencerRun getRequestedSequencerRun(HttpServletRequest request) {
    SequencerRun sequencerRun = null;
    HttpSession session   = request.getSession(false);
    //Study study = null;
    String    id      = (String)request.getParameter("sequencerRunId");
    if (id != null && !"".equals(id)) {
      sequencerRun = sequencerRunService.findByID(Integer.parseInt(id));
      //sequencerRun = (SequencerRun)session.getAttribute("sequencerRun");
    }
    return(sequencerRun);
  }
	
	/* ********************************************************************** */
	/* Constructors */
	/**
	 * <p>Constructor for WorkflowRunController.</p>
	 */
	public WorkflowRunController() {
		super();
	}

	/**
	 * <p>Constructor for WorkflowRunController.</p>
	 *
	 * @param delegate a {@link java.lang.Object} object.
	 */
	public WorkflowRunController(Object delegate) {
		super(delegate);
	}

	/* ********************************************************************** */
	/* Property SETters and GETters */

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
}

// ex:sw=4:ts=4:
