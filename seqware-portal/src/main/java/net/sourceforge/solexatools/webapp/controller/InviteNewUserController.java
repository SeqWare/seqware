package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.invitation.InvitationParams;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.validation.LoginValidator;

import org.springframework.mail.MailSender;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>InviteNewUserController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class InviteNewUserController extends BaseCommandController {
	private RegistrationService registrationService;
	private MailSender sender;
	
	/**
	 * <p>Getter for the field <code>sender</code>.</p>
	 *
	 * @return a {@link org.springframework.mail.MailSender} object.
	 */
	public MailSender getSender() {
		return sender;
	}

	/**
	 * <p>Setter for the field <code>sender</code>.</p>
	 *
	 * @param sender a {@link org.springframework.mail.MailSender} object.
	 */
	public void setSender(MailSender sender) {
		this.sender = sender;
	}
	
	/**
	 * <p>Constructor for InviteNewUserController.</p>
	 */
	public InviteNewUserController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET, METHOD_POST});
	}

	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest	request,
									HttpServletResponse	response) 
	throws Exception 
	{
		ModelAndView	modelAndView	= null;
		HashMap<String,Object>	model	= new HashMap<String,Object>();
		
		Registration registration = Security.getRegistration(request);
		if(registration == null || !registration.isLIMSAdmin())
			return new ModelAndView("redirect:/login.htm");
		
//		Log.info("Do invite = " + request.getParameter("submitlink"));
//		Log.info("Do cancel = " + request.getParameter("cancel"));
		
		String doCancel =  request.getParameter("cancel");
		if("submit".equals(doCancel)){
			modelAndView = new ModelAndView("redirect:/myStudyList.htm", model);
		}

		Boolean isHasError = false;
		List<String> errorMessages = new LinkedList<String>();
		List<String> emailsHasError = new LinkedList<String>();
	
		String[] emails = getRequestedEmails(request);
		
		if(emails == null || ( emails.length == 1 && emails[0].trim().equals("") ) ){
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
		
		if(isHasError){
			model.put("isHasError", isHasError);
			model.put("errorMessages", errorMessages);
			modelAndView = new ModelAndView("InviteNewUser", model);
		}else{
			ServletContext context = this.getServletContext();
			String isInvitationCode = context.getInitParameter("invitation.code");
            String subjectEmail = context.getInitParameter("subject.invite.email");
            String templateEmail = context.getInitParameter("template.invite.email");
			InvitationParams invParams = new InvitationParams(isInvitationCode, subjectEmail, templateEmail);
			
			getRegistrationService().insert(emails, invParams, getSender());
			
			modelAndView = new ModelAndView("redirect:/myStudyList.htm", model);
		}
		
		return modelAndView;
	}
		
	private String[] getRequestedEmails(HttpServletRequest request) {
		String[]	emails	= (String[])request.getParameterValues("emails[]");
		return emails;
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

	/**
	 * <p>Getter for the field <code>registrationService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.RegistrationService} object.
	 */
	public RegistrationService getRegistrationService() {
		return registrationService;
	}

	/**
	 * <p>Setter for the field <code>registrationService</code>.</p>
	 *
	 * @param registrationService a {@link net.sourceforge.seqware.common.business.RegistrationService} object.
	 */
	public void setRegistrationService(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}
}
