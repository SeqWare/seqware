package	net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.business.ShareWorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.ShareWorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * StudyController
 */

public class AnalisysEventController extends MultiActionController {
    private WorkflowRunService workflowRunService;
    private ShareWorkflowRunService shareWorkflowRunService;
    private RegistrationService registrationService;

	public AnalisysEventController() {
		super();
	}

	public AnalisysEventController(Object delegate) {
		super(delegate);
	}

    public ShareWorkflowRunService getShareWorkflowRunService() {
		return shareWorkflowRunService;
	}

	public void setShareWorkflowRunService(
			ShareWorkflowRunService shareWorkflowRunService) {
		this.shareWorkflowRunService = shareWorkflowRunService;
	}
	
	public WorkflowRunService getWorkflowRunService() {
		return workflowRunService;
	}

	public void setWorkflowRunService(WorkflowRunService workflowRunService) {
		this.workflowRunService = workflowRunService;
	}
	
	public void setRegistrationService(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	/**
	 * Handles the user's request to delete their study.
	 *
	 * @param command Study command object
	 */
	public ModelAndView handleDelete(HttpServletRequest		request,
									 HttpServletResponse	response,
									 WorkflowRun			command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();
		WorkflowRun				workflowRun		= getRequestedWorkflowRun(request);
		
		ServletContext context = this.getServletContext();
		String deleteRealFiles = context.getInitParameter("delete.files.for.node.deletion");

		if (workflowRun != null) {			
		    if(registration.equals(workflowRun.getOwner()) || registration.isLIMSAdmin()){
		    	getWorkflowRunService().delete(workflowRun, deleteRealFiles);
		    }
		} 
		modelAndView = new ModelAndView("redirect:/myAnalisysList.htm", model);
		return modelAndView;
	}
	
	/**
	 * Handles the user's request to share their WorkflowRun.
	 *
	 * @param command WorkflowRun command object
	 */
	public ModelAndView handleShare(HttpServletRequest		request,
									 HttpServletResponse	response,
									 WorkflowRun			command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();
		WorkflowRun			    workflowRun		= getRequestedWorkflowRun(request);

		if (workflowRun != null) {
			Integer ownerId = workflowRun.getOwner().getRegistrationId();
			Integer registrationId = registration.getRegistrationId();
			String[] emails = getRequestedEmails(request);
					
		    if(registrationId.equals(ownerId)){
		    	for (String email : emails) {
		    	//	if(!getShareWorkflowRunService().isExistsShare(workflowRun.getWorkflowRunId(), email)){
			    		ShareWorkflowRun shareWorkflowRun = new ShareWorkflowRun();
			    		Registration shareReg = registrationService.findByEmailAddress(email);
			    		shareWorkflowRun.setRegistration(shareReg);
			    		shareWorkflowRun.setActive(true);
			    	//	shareStudy.setEmail(email);
			    		shareWorkflowRun.setWorkflowRunId(workflowRun.getWorkflowRunId());
						getShareWorkflowRunService().insert(shareWorkflowRun);
		    	//	}
				}
		    }
		} 
		modelAndView = new ModelAndView("redirect:/myAnalisysList.htm", model);
		return modelAndView;
	}
	
	/**
	 * Handles the user's request to delete their study.
	 *
	 * @param command Study command object
	 */
	public ModelAndView handleCancel(HttpServletRequest		request,
									 HttpServletResponse	response,
									 WorkflowRun			command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();
		WorkflowRun				workflowRun		= getRequestedWorkflowRun(request);

		if (workflowRun != null) {
			Integer ownerId = workflowRun.getOwner().getRegistrationId();
			Integer registrationId = registration.getRegistrationId();
			
		    if(registrationId.equals(ownerId)){
		    	workflowRun.setStatus("cancelled");
		    	getWorkflowRunService().update(workflowRun);
		    }
		} 
		modelAndView = new ModelAndView("redirect:/myAnalisysList.htm", model);
		return modelAndView;
	}
	
	private WorkflowRun getRequestedWorkflowRun(HttpServletRequest request) {
		WorkflowRun workflowRun	= null;
		String		id		= (String)request.getParameter("objectId");
		
		if (id != null) {
			Integer workflowRunID = Integer.parseInt(id);
			workflowRun = getWorkflowRunService().findByID(workflowRunID);
		}
		return workflowRun;
	}
	
	private String[] getRequestedEmails(HttpServletRequest request) {
		String[]	emails	= (String[])request.getParameterValues("emails[]");
		return emails;
	}
}
