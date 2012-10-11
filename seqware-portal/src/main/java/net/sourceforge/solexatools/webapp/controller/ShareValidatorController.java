package net.sourceforge.solexatools.webapp.controller;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.business.ShareStudyService;
import net.sourceforge.seqware.common.business.ShareWorkflowRunService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.ShareStudy;
import net.sourceforge.seqware.common.model.ShareWorkflowRun;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.Constant;
import net.sourceforge.solexatools.validation.LoginValidator;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

public class ShareValidatorController extends BaseCommandController {
	private RegistrationService registrationService;
	private StudyService studyService;
	private ShareStudyService shareStudyService;
	private WorkflowRunService workflowRunService;
	private ShareWorkflowRunService shareWorkflowRunService;
	
	public ShareValidatorController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest	 request,
												 HttpServletResponse response)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");
		
		Boolean isHasError = false;
		List<String> errorMessages = new LinkedList<String>();
		List<String> emailsHasError = new LinkedList<String>();
		
		// get request params
		String openNodeId = request.getParameter("openNodeId");
		//String emailsToString = request.getParameter("emailsToString");
		String[] emails = request.getParameterValues("emailsToString");
		
		Integer objectId = Constant.getId(openNodeId);
		
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
		
		
		// Check share is Exists
		if(!isHasError){
			Log.info("Check is Exists");
			for (String email : emails) {
				Log.info("	Email: " + email);
				if(getRegistrationService().findByEmailAddress(email) == null){
					Log.info("	this email is already exists");
					isHasError = true;
					emailsHasError.add(email.trim());
				}
			}
			if(isHasError){
				if(emailsHasError.size()==1){
					errorMessages.add(this.getMessageSourceAccessor().getMessage("error.email.no.reregistered", new Object[] {getEmailsToString(emailsHasError)})); 
				}else{
					errorMessages.add(this.getMessageSourceAccessor().getMessage("error.emails.no.reregistered", new Object[] {getEmailsToString(emailsHasError)})); 
				}
			}
		}
			
		if(!isHasError){
			if(openNodeId.indexOf("study_")!= -1){
				for (String email : emails) {
					Registration reg = getRegistrationService().findByEmailAddress(email);
					ShareStudy shareStudy = getShareStudyService().findByStudyIdAndRegistrationId(objectId, reg.getRegistrationId());
					
					if(shareStudy != null){
						errorMessages.add(this.getMessageSourceAccessor().getMessage("error.study.shared.already.exixst", new Object[] {shareStudy.getCreateTimestamp(), email}));
						isHasError = true;
					}
				}
			}
			
			if(openNodeId.indexOf("wfr_")!= -1){
				for (String email : emails) {
					Registration reg = getRegistrationService().findByEmailAddress(email);
					ShareWorkflowRun shareWorkflowRun = getShareWorkflowRunService().findByWorkflowRunIdAndRegistrationId(objectId, reg.getRegistrationId());
					
					if(shareWorkflowRun != null){
						errorMessages.add(this.getMessageSourceAccessor().getMessage("error.workflowRun.shared.already.exixst", new Object[] {shareWorkflowRun.getCreateTimestamp(), email}));
						isHasError = true;
					}
				}
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

	public StudyService getStudyService() {
		return studyService;
	}

	public void setStudyService(StudyService studyService) {
		this.studyService = studyService;
	}

	public WorkflowRunService getWorkflowRunService() {
		return workflowRunService;
	}

	public void setWorkflowRunService(WorkflowRunService workflowRunService) {
		this.workflowRunService = workflowRunService;
	}

	public ShareStudyService getShareStudyService() {
		return shareStudyService;
	}

	public void setShareStudyService(ShareStudyService shareStudyService) {
		this.shareStudyService = shareStudyService;
	}

	public ShareWorkflowRunService getShareWorkflowRunService() {
		return shareWorkflowRunService;
	}

	public void setShareWorkflowRunService(
			ShareWorkflowRunService shareWorkflowRunService) {
		this.shareWorkflowRunService = shareWorkflowRunService;
	}
}

