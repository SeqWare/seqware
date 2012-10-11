package net.sourceforge.solexatools.webapp.controller;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.validation.LoginValidator;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

public class InviteNewUserValidatorController extends BaseCommandController {
	private RegistrationService registrationService;
	public InviteNewUserValidatorController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest	 request,
												 HttpServletResponse response)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null || !registration.isLIMSAdmin())
			return new ModelAndView("redirect:/login.htm");
		
		Boolean isHasError = false;
		List<String> errorMessages = new LinkedList<String>();
		List<String> emailsHasError = new LinkedList<String>();
		
		String[] emails = request.getParameterValues("emailsToString");
		
		if(emails == null || ( emails.length == 1 && emails[0].trim().equals("") ) ){
			Log.info("Emails is Empty");
			isHasError = true;
			errorMessages.add(this.getMessageSourceAccessor().getMessage("error.list.emails.empty"));
		}
		
		// Check is correct emails format
		if(!isHasError){
			for (String email : emails) {
				if (!LoginValidator.isCheckEmail(email)) {
					isHasError = true;
					emailsHasError.add(email);
				}
			}
			if(isHasError){
				errorMessages.add(this.getMessageSourceAccessor().getMessage("error.format.list.emails", new Object[] {getEmailsToString(emailsHasError)}));
			}
		}
		
		// emails already exists?
		if(!isHasError){
			for (String email : emails) {
				if(getRegistrationService().findByEmailAddress(email) != null){
					isHasError = true;
					emailsHasError.add(email);
				}
			}
			if(isHasError){
				errorMessages.add(this.getMessageSourceAccessor().getMessage("error.list.emails.exists", new Object[] {getEmailsToString(emailsHasError)}));
			}
		}

		ModelAndView modelAndView = new ModelAndView("ResultShareValidation");
		modelAndView.addObject("isHasError", isHasError);
		modelAndView.addObject("errorMessages", errorMessages);
		return modelAndView;
	}
	
	private String getEmailsToString(List<String> emails){
		String str="";
		if(emails != null && emails.size() > 0){
			for (String email : emails) {
				str = str + email + ", ";
			}
			str = str.substring(0, str.length()-2);
		}
		return str;
	}

	public RegistrationService getRegistrationService() {
		return registrationService;
	}

	public void setRegistrationService(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}
}
