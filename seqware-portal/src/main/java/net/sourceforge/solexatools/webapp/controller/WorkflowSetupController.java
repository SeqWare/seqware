package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

public class WorkflowSetupController extends BaseCommandController {
	private WorkflowService workflowService;

	public WorkflowSetupController() {
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

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();
		Workflow				workflow		= getRequestedWorkflow(request);
		boolean isReport = request.getParameter("report") != null;

		if (workflow != null) {
			request.setAttribute(getCommandName(), workflow);
			request.setAttribute("swid", workflow.getSwAccession());
			model.put("strategy", "update");
			if (!isReport) {
				modelAndView = new ModelAndView("Workflow", model);
			} else {
				modelAndView = new ModelAndView("WorkflowReport", model);
			}
		} else {
			request.setAttribute(getCommandName(), new Workflow());
			model.put("strategy", "submit");
			modelAndView = new ModelAndView("Workflow", model);
		}
		
		return modelAndView;
	}
	
	private Workflow getRequestedWorkflow(HttpServletRequest request) {
		HttpSession	session	= request.getSession(false);
		Workflow workflow = null;
		String id = (String)request.getParameter("workflowId");
		session.removeAttribute("workflow");

		if (id != null) {
			Integer expID = Integer.parseInt(id);
			workflow = getWorkflowService().findByID(expID);
			session.setAttribute("workflow", workflow);
		}

		return workflow;
	}


	public WorkflowService getWorkflowService() {
		return workflowService;
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}
}
