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
import net.sourceforge.seqware.common.model.WorkflowRunStatus;
import net.sourceforge.seqware.common.util.Bool;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * StudyController
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class AnalisysEventController extends MultiActionController {
    private WorkflowRunService workflowRunService;
    private ShareWorkflowRunService shareWorkflowRunService;
    private RegistrationService registrationService;

	/**
	 * <p>Constructor for AnalisysEventController.</p>
	 */
	public AnalisysEventController() {
		super();
	}

	/**
	 * <p>Constructor for AnalisysEventController.</p>
	 *
	 * @param delegate a {@link java.lang.Object} object.
	 */
	public AnalisysEventController(Object delegate) {
		super(delegate);
	}

    /**
     * <p>Getter for the field <code>shareWorkflowRunService</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.business.ShareWorkflowRunService} object.
     */
    public ShareWorkflowRunService getShareWorkflowRunService() {
		return shareWorkflowRunService;
	}

	/**
	 * <p>Setter for the field <code>shareWorkflowRunService</code>.</p>
	 *
	 * @param shareWorkflowRunService a {@link net.sourceforge.seqware.common.business.ShareWorkflowRunService} object.
	 */
	public void setShareWorkflowRunService(
			ShareWorkflowRunService shareWorkflowRunService) {
		this.shareWorkflowRunService = shareWorkflowRunService;
	}
	
	/**
	 * <p>Getter for the field <code>workflowRunService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.WorkflowRunService} object.
	 */
	public WorkflowRunService getWorkflowRunService() {
		return workflowRunService;
	}

	/**
	 * <p>Setter for the field <code>workflowRunService</code>.</p>
	 *
	 * @param workflowRunService a {@link net.sourceforge.seqware.common.business.WorkflowRunService} object.
	 */
	public void setWorkflowRunService(WorkflowRunService workflowRunService) {
		this.workflowRunService = workflowRunService;
	}
	
	/**
	 * <p>Setter for the field <code>registrationService</code>.</p>
	 *
	 * @param registrationService a {@link net.sourceforge.seqware.common.business.RegistrationService} object.
	 */
	public void setRegistrationService(RegistrationService registrationService) {
		this.registrationService = registrationService;
	}

	/**
	 * Handles the user's request to delete their study.
	 *
	 * @param command Study command object
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @return a {@link org.springframework.web.servlet.ModelAndView} object.
	 * @throws java.lang.Exception if any.
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
		boolean deleteRealFiles = Bool.parse(context.getInitParameter("delete.files.for.node.deletion"));

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
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @return a {@link org.springframework.web.servlet.ModelAndView} object.
	 * @throws java.lang.Exception if any.
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
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @return a {@link org.springframework.web.servlet.ModelAndView} object.
	 * @throws java.lang.Exception if any.
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
		    	workflowRun.setStatus(WorkflowRunStatus.cancelled);
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
