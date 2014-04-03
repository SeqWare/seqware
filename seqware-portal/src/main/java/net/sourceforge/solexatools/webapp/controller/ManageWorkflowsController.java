package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>ManageWorkflowsController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ManageWorkflowsController  extends BaseCommandController {

	public WorkflowService workflowService;
	
	/**
	 * <p>Constructor for ManageWorkflowsController.</p>
	 */
	public ManageWorkflowsController() {
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

		model.put("workflowList", getWorkflowService().list(registration));

		modelAndView = new ModelAndView("ManageWorkflows", model);
		
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
