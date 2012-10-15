package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.LaunchWorkflowUtil;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>LaunchWorkflowSetupController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LaunchWorkflowSetupController extends BaseCommandController {
    private WorkflowService workflowService;

	/**
	 * <p>Constructor for LaunchWorkflowSetupController.</p>
	 */
	public LaunchWorkflowSetupController() {
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

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();	
		
		
		List<Workflow> list = getWorkflowService().list();
		model.put("workflows", list);
		
		request.setAttribute(getCommandName(), new Workflow());
		
		// set this attribute for view Select Inputs bar
		request.setAttribute("isHasSelectedInputMenu", true);
		
		// remove selected items
		LaunchWorkflowUtil.removeSelectedItemsLaunchWorkflow(request);
		//request.getSession(false).removeAttribute("selectedWorkflowParamValueId");
		
		modelAndView = new ModelAndView("LaunchWorkflow", model);
		return modelAndView;
	}

	/**
	 * <p>Getter for the field <code>workflowService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.WorkflowService} object.
	 */
	public WorkflowService getWorkflowService() {
		return workflowService;
	}

	/**
	 * <p>Setter for the field <code>workflowService</code>.</p>
	 *
	 * @param workflowService a {@link net.sourceforge.seqware.common.business.WorkflowService} object.
	 */
	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}
}
