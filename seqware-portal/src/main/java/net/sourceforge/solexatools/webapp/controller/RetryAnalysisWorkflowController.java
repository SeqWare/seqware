package net.sourceforge.solexatools.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>RetryAnalysisWorkflowController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class RetryAnalysisWorkflowController  extends BaseCommandController {
	private WorkflowRunService workflowRunService;
    
	/**
	 * <p>Constructor for RetryAnalysisWorkflowController.</p>
	 */
	public RetryAnalysisWorkflowController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}

	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest	 request,
												 HttpServletResponse response)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");
		
		WorkflowRun	workflowRun	= getRequestedWorkflowRun(request);
		
		if (workflowRun != null) {
			Integer ownerId = workflowRun.getOwner().getRegistrationId();
			Integer registrationId = registration.getRegistrationId();
			
		    if(registrationId.equals(ownerId)){
		    	if(WorkflowRun.Status.failed == workflowRun.getStatus() || WorkflowRun.Status.cancelled == workflowRun.getStatus())
                        {
		    		workflowRun.setStatus(WorkflowRun.Status.submitted);
                                workflowRun.setHost(null);
                                
		    		getWorkflowRunService().update(workflowRun);
		    	}
		    }
		} 
		
		return null;
	}
	
	private WorkflowRun getRequestedWorkflowRun(HttpServletRequest request) {
		WorkflowRun workflowRun	= null;
		String id = (String)request.getParameter("objectId");
		
		if (id != null) {
			Integer workflowRunID = Integer.parseInt(id);
			workflowRun = getWorkflowRunService().findByID(workflowRunID);
		}
		return workflowRun;
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
}
