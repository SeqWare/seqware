package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>WorkflowRunSetupController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowRunSetupController extends BaseCommandController {
	private WorkflowRunService workflowRunService;

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
	
	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView modelAndView = null;
		HashMap <String,String> model = new HashMap<String,String>();

		WorkflowRun workflowRun = figureOutWorkflowRun(request);

		if (workflowRun != null) {
			request.setAttribute(getCommandName(), workflowRun);
			model.put("strategy", "update");
			modelAndView = new ModelAndView("WorkflowRunReport", model);
		} 

		return modelAndView;
	}

	private WorkflowRun figureOutWorkflowRun(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		WorkflowRun workflowRun = null;

		String id = (String)request.getParameter("workflowRunId");
		if (id != null) {
			workflowRun	= workflowRunService.findByID(Integer.parseInt(id));
			session.setAttribute("workflowrun", workflowRun);
		}

		return workflowRun;
	}

}
