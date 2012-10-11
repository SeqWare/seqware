package	net.sourceforge.solexatools.webapp.controller;				// -*- tab-width: 4 -*-
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.model.RegistrationDTO;
import net.sourceforge.solexatools.authentication.Authentication;
import net.sourceforge.solexatools.validation.LoginValidator;
import net.sourceforge.solexatools.validation.RegistrationValidator;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class RegistrationController extends MultiActionController {
	private RegistrationService	registrationService;
	private Validator			validator;

//	private static String trueInvitationCode = "19dks1-12i393-12991-2219k";
	/**
	 * Handles the user's request to submit a new registration.
	 *
	 * @param command RegistrationDTO command object
	 */
	public ModelAndView handleSubmit(HttpServletRequest		request,
									 HttpServletResponse	response,
									 RegistrationDTO		command)
		throws Exception {

		ModelAndView modelAndView;
		
    // used to specify the authentication module if not using the default
    ServletContext context = this.getServletContext();
    String authStr = context.getInitParameter("authenticator");
    // only set these if defined in the context
    if (context.getInitParameter("java.security.krb5.realm") != null) {
      System.setProperty("java.security.krb5.realm", context.getInitParameter("java.security.krb5.realm"));
    }
    if (context.getInitParameter("java.security.krb5.kdc") != null) {
      System.setProperty("java.security.krb5.kdc", context.getInitParameter("java.security.krb5.kdc"));
    }

    if(command.getFirstName() == null){
    	command.setFirstName("");
    }
    if(command.getLastName() == null){
    	command.setLastName("");
    }
    if(command.getConfirmEmailAddress() == null){
    	command.setConfirmEmailAddress(command.getEmailAddress());
    }

    	String isInvitationCode = context.getInitParameter("invitation.code");
    	
		BindingResult errors = this.validateRegistration(request, command, authStr, true, isInvitationCode);
		if (errors.hasErrors()) {
			Map model = errors.getModel();
			model.put("isInvitationCode", isInvitationCode);
			modelAndView = new ModelAndView("SignUp", model); // Registration
		} else {
			System.err.println("Registration ID Submit: "+command.getRegistrationId());
			// do not write password to DB if using external authenticator!
			if (authStr != null && !"".equals(authStr)) {
				command.setPassword(null);
				command.setConfirmPassword(null);
			}
			
			RegistrationDTO oldRegistration = getRegistrationService().findByEmailAddress(command.getEmailAddress());
			
			if(oldRegistration != null /*"true".equals(isInvitationCode)*/){
			//	RegistrationDTO oldRegistration = getCurrentRegistration(request);
			
				new ServletRequestDataBinder(oldRegistration).bind(request);
				request.getSession(false).removeAttribute("registration");
				
				getRegistrationService().update(oldRegistration);
			}else{
				getRegistrationService().insert(command);
			}
			
			// now save the registration as a session variable
			request.getSession(true).setAttribute("registration", getRegistrationService().findByEmailAddress(command.getEmailAddress()));
			modelAndView = new ModelAndView("redirect:/myStudyList.htm"); // sequencerRunList.htm
		}

		return modelAndView;
	}

	/**
	 * Handles the user's request to reset the registration page during a new or
	 * update registration.
	 *
	 * @param command RegistrationDTO command object
	 */
	public ModelAndView handleReset(HttpServletRequest	request,
									HttpServletResponse	response,
									RegistrationDTO		command)
		throws Exception {

		ModelAndView	modelAndView	= null;
		RegistrationDTO	registration	= getCurrentRegistration(request);
		if (registration.getUpdateTimestamp() == null) {
			modelAndView = new ModelAndView("Registration");
		} else {
			modelAndView = new ModelAndView("RegistrationUpdate");
		}
		request.setAttribute(getCommandName(command), registration);
		return modelAndView;
	}

	/**
	 * Handles the user's request to cancel the registration
	 * or the registration update page.
	 *
	 * @param command RegistrationDTO command object
	 */
	public ModelAndView handleCancel(HttpServletRequest request,
									 HttpServletResponse response,
									 RegistrationDTO command)
		throws Exception {
		return new ModelAndView("redirect:/myStudyList.htm");
	}

	/**
	 * Handles the user's request to update their registration.
	 *
	 * @param command RegistrationDTO command object
	 */
	public ModelAndView handleUpdate(HttpServletRequest request,
									 HttpServletResponse response,
									 RegistrationDTO command)
		throws Exception {

    // used to specify the authentication module if not using the default
    ServletContext context = this.getServletContext();
    String authStr = context.getInitParameter("authenticator");
    // only set these if defined in the context
    if (context.getInitParameter("java.security.krb5.realm") != null) {
      System.setProperty("java.security.krb5.realm", context.getInitParameter("java.security.krb5.realm"));
    }
    if (context.getInitParameter("java.security.krb5.kdc") != null) {
      System.setProperty("java.security.krb5.kdc", context.getInitParameter("java.security.krb5.kdc"));
    }
	  
		ModelAndView modelAndView = null;
		BindingResult errors = this.validateRegistration(request, command, authStr, false, "false");
		if (errors.hasErrors()) {
			Map model = errors.getModel();
			modelAndView = new ModelAndView("RegistrationUpdate", model);
		} else {
			RegistrationDTO registration = getCurrentRegistration(request);
			RegistrationDTO updatedRegistration = command;
			if (registration != null) {
			  new ServletRequestDataBinder(registration).bind(request);
			  // LEFT OFF HERE: need to figure out why registration isn't being updated but instead creates new user!?
			  // it's because they are both being posted to the "new" URL!!
			  System.err.println("Registration ID: "+registration.getRegistrationId());
		     // do not write password to DB if using external authenticator!
	      if (authStr != null && !"".equals(authStr)) {
	        registration.setPassword(null);
	        registration.setConfirmPassword(null);
	      }
	      	getRegistrationService().update(registration);
	      	request.getSession(false).setAttribute("registration", registration);
	      	modelAndView = new ModelAndView("redirect:/myStudyList.htm");

			  /*if (registration.getUpdateTimestamp() != null) {
				RegistrationDTO updatedRegistration = command;
				updatedRegistration.setDomainObject(registration.getDomainObject());
				getRegistrationService().update(updatedRegistration);
				request.getSession(false).setAttribute("registration", updatedRegistration);
				registration.setDomainObject(null);
				modelAndView = new ModelAndView("redirect:/sequencerRunList.htm");*/
			} else {
				modelAndView = new ModelAndView("redirect:/Error.htm");
			}
		}
		return modelAndView;
	}

	/**
	 * Validates a registration.
	 * @return BindingResult validation errors
	 */
	private BindingResult validateRegistration(HttpServletRequest request, Object command,
			String authenticator, boolean isSignUp, String isInvitationCode) 
	{

		BindingResult errors = new BindException(command, getCommandName(command));
		ValidationUtils.invokeValidator(getValidator(), command, errors);
		
		// addition validate
		if(!isSignUp){
			RegistrationDTO registration = (RegistrationDTO) command;
			
			LoginValidator loginValidator = new LoginValidator();
			
			loginValidator.validateEmail("confirmEmailAddress", registration.getConfirmEmailAddress(), errors);
			
			ValidationUtils.rejectIfEmpty(errors, "passwordHint", "required.passwordHint");
			ValidationUtils.rejectIfEmpty(errors, "firstName", "required.firstName");
			ValidationUtils.rejectIfEmpty(errors, "lastName", "required.lastName");
			RegistrationValidator.validateEmailAddressesMatch(registration.getEmailAddress(),
											 registration.getConfirmEmailAddress(), errors);
		}

		if (!errors.hasErrors()) {

			RegistrationDTO registration = (RegistrationDTO) command;

			/* Make sure that the email address has not yet been used. */
			if(!isSignUp){
				// check update email
				if(!errors.hasErrors()){
					String newEmail = registration.getEmailAddress();
					String oldEmail = getCurrentRegistration(request).getEmailAddress();
					
					// is change email
					if(!newEmail.equals(oldEmail)){
						if (getRegistrationService().hasEmailAddressBeenUsed(registration.getEmailAddress())) {
							errors.reject("error.registration.email.used");
						}
					}
				}
			}
			
			if(isSignUp){
				RegistrationDTO  reg = getRegistrationService().findByEmailAddress(registration.getEmailAddress());
				
				if(reg != null && reg.getPassword() != null && !reg.getPassword().equals("")){
					errors.reject("error.registration.email.used");
				}
				
				if("true".equals(isInvitationCode)) {
					if(reg == null){
						errors.reject("error.registration.not.registered.email");
					}
					
					// check invitation code
					if(!errors.hasErrors()) {
						String invitationCode = registration.getInvitationCode();
						String trueInvitationCode = reg.getInvitationCode();
				    	
						if (errors.getFieldError("invitationCode") == null/*!errors.hasErrors()*/){
							if(invitationCode == null || "".equals(invitationCode.trim())){
								errors.reject("error.registration.invitationCode");
							}else
							if(!invitationCode.trim().equals(trueInvitationCode)){
								errors.reject("error.registration.invitationCode.false");
							}
						}
					}
				}
		//		else{
		//			if (getRegistrationService().hasEmailAddressBeenUsed(registration.getEmailAddress())) {
		//				errors.reject("error.registration.email.used");
		//			}
		//		}
			}
			
	     // check if using an authenticator, if so check the password
      if (authenticator != null && !"".equals(authenticator)) {
        boolean error = true;
        try {
          Authentication auth = (Authentication) Class.forName(authenticator).newInstance();
          if (auth != null) {
            if (auth.loginSuccess(registration.getEmailAddress(), registration.getPassword())) {
              error = false;
            }
          }
        } catch (Exception e) {
          error = true;
        }
        if (error) {
          errors.reject("error.registration.auth.password");
        }
      }
		}

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

	/* ********************************************************************** */
	/* Constructors */
	public RegistrationController() {
		super();
	}

	public RegistrationController(Object delegate) {
		super(delegate);
	}

	/* ********************************************************************** */
	/* Property SETters and GETters */
	public RegistrationService getRegistrationService() {
		return registrationService;
	}

	public void setRegistrationService(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	public Validator getValidator() {
		return validator;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}
}

// ex:sw=4:ts=4:
