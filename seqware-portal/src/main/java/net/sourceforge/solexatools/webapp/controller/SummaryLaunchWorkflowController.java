package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.LaunchWorkflowUtil;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class SummaryLaunchWorkflowController extends MultiActionController {
	private WorkflowService workflowService;
	private WorkflowRunService workflowRunService;
	
	public SummaryLaunchWorkflowController() {
		super();
	}

	public SummaryLaunchWorkflowController(Object delegate) {
		super(delegate);
	}

    public WorkflowService getWorkflowService() {
		return workflowService;
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public WorkflowRunService getWorkflowRunService() {
		return workflowRunService;
	}

	public void setWorkflowRunService(WorkflowRunService workflowRunService) {
		this.workflowRunService = workflowRunService;
	}

	/**
	 * Handles the user's request to submit a new study.
	 *
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param command Study command object
	 *
	 * @return ModelAndView
	 *
	 * @throws Exception
	 */
	public ModelAndView handleNext (HttpServletRequest		request,
									HttpServletResponse	response,
									Workflow				command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView	modelAndView = null;
		
		Integer workflowRunId = getCurrentWorkflowRun(request).getWorkflowRunId();
		Integer workflowId = getCurrentWorkflow(request).getWorkflowId();
		WorkflowRun workflowRun = getWorkflowRunService().findByID(workflowRunId);
		Workflow workflow = getWorkflowService().findByID(workflowId);
		
		if(workflowRun!=null){
			workflowRun.setStatus("submitted");
			if (workflow != null) {
			  workflowRun.setName(workflow.getName());
			  workflowRun.setCommand(workflow.getCommand());
			  workflowRun.setTemplate(workflow.getTemplate());
			  workflowRun.setSeqwareRevision(workflow.getVersion());
			  workflowRun.setCurrentWorkingDir(workflow.getCwd());
			  workflowRun.setCommand(workflow.getCommand());
			}
			getWorkflowRunService().update(workflowRun);

			clearSession(request);
		}
		
		request.setAttribute("isRunningTabSelected", true);
		modelAndView = new ModelAndView("AnalisysList");
		return modelAndView;
	}

	/**
	 * Handles the user's request to cancel the study
	 * or the study update page.
	 *
	 * @param command Study command object
	 */
	public ModelAndView handlePrevious (HttpServletRequest request,
									    HttpServletResponse response,
									    Workflow command) throws Exception {

		ModelAndView	modelAndView = null;
		
		Workflow workflow = getCurrentWorkflow(request);

		// set this attribute for view Select Inputs bar
		request.setAttribute("isHasSelectedInputMenu", true);
		
		if (workflow.getWorkflowParamsWithDifferentFileMetaType().size() > 0) {
			modelAndView = new ModelAndView("SelectInput");
		} else {
			HashMap<String,Object> model = new HashMap<String,Object>();
			request.setAttribute(getCommandName(command), workflow);
			
			List<Workflow> list = getWorkflowService().list();
			model.put("workflows", list);
			
			modelAndView = new ModelAndView("LaunchWorkflow", model);
		}
		
		return modelAndView;
	}
	
	/**
	 * Handles the user's request to cancel the study
	 * or the study update page.
	 *
	 * @param command Study command object
	 */
	public ModelAndView handleCancel (HttpServletRequest request,
									    HttpServletResponse response,
									    Workflow command) throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");
		
		ServletContext context = this.getServletContext();
		String deleteRealFiles = context.getInitParameter("delete.files.for.node.deletion");

		ModelAndView	modelAndView = null;
		
		WorkflowRun workflowRun = getCurrentWorkflowRun(request);
		clearSession(request);
		
		workflowRun = getWorkflowRunService().findByID(workflowRun.getWorkflowRunId());
		getWorkflowRunService().delete(workflowRun, deleteRealFiles);

		modelAndView = new ModelAndView("redirect:/launchWorkflowSetup.htm");
		return modelAndView;
	}
	
	private void clearSession(HttpServletRequest request){
		request.getSession(false).removeAttribute("workflow");
		request.getSession(false).removeAttribute("workflowRun");
		request.getSession(false).removeAttribute("workflowParam");
		request.getSession(false).removeAttribute("summaryData");
		LaunchWorkflowUtil.removeSelectedItemsLaunchWorkflow(request);
	}

	/**
	 * Gets the study from the session.
	 *
	 * @param request HttpServletRequest
	 *
	 * @return instance of Study from the session, or a new instance
	 * if the study is not in the session (e.g. the user is not logged in)
	 */
	private Workflow getCurrentWorkflow(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object workflow = session.getAttribute("workflow");
			if (workflow != null) {
				return (Workflow)workflow;
			}
		}
		return new Workflow();
	}
	
	private WorkflowRun getCurrentWorkflowRun(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object workflowRun = session.getAttribute("workflowRun");
			if (workflowRun != null) {
				return (WorkflowRun)workflowRun;
			}
		}
		return new WorkflowRun();
	}
}