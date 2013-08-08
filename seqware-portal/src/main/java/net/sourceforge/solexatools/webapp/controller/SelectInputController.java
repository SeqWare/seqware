package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.LaunchWorkflowUtil;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * <p>SelectInputController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SelectInputController  extends MultiActionController {
	private WorkflowService workflowService;
	private WorkflowRunService workflowRunService;
	
	/**
	 * <p>Constructor for SelectInputController.</p>
	 */
	public SelectInputController() {
		super();
	}

	/**
	 * <p>Constructor for SelectInputController.</p>
	 *
	 * @param delegate a {@link java.lang.Object} object.
	 */
	public SelectInputController(Object delegate) {
		super(delegate);
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
	 * Handles the user's request to submit a new study.
	 *
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param command Study command object
	 * @return ModelAndView
	 * @throws java.lang.Exception if any.
	 */
	public ModelAndView handleNext (HttpServletRequest		request,
									HttpServletResponse	response,
									Workflow				command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView	modelAndView = null;
		
		boolean isHasNextParam = nextWorkflowParam(request); 
		if(isHasNextParam){
			modelAndView = new ModelAndView("SelectInput");
		}else{
			
			// get current workflow
			Workflow workflow = getCurrentWorkflow(request);
			SortedSet<WorkflowParam> visibleParams = workflow.getVisibleWorkflowParams();
			
			workflow = getWorkflowService().findByID(workflow.getWorkflowId());
		//	workflow.setWorkflowParams(visibleParams);
			
			// create new workflow
			WorkflowRun workflowRun = new WorkflowRun();
			
			workflowRun.setWorkflow(workflow);
			workflowRun.setStatus(WorkflowRun.Status.pending);
			workflowRun.setOwner(registration);
			
			
			getWorkflowRunService().insert(workflowRun, LaunchWorkflowUtil.getWorkflowRunParam(visibleParams), LaunchWorkflowUtil.getAllSelectedFiles(request));
			request.getSession(false).setAttribute("workflowRun", workflowRun);
			
			request.setAttribute("summaryData", LaunchWorkflowUtil.getSummaryData(request));
			
			clearSession(request);
			
			modelAndView = new ModelAndView("SummaryLaunchWorkflow");
		}
		
		// set this attribute for view Select Inputs bar
		request.setAttribute("isHasSelectedInputMenu", true);
		
		return modelAndView;
	}
	
	private void clearSession(HttpServletRequest request){
		LaunchWorkflowUtil.removeSelectedItemsLaunchWorkflow(request);
	}
/*	
	private SortedSet<WorkflowRunParam> getWorkflowRunParam(SortedSet<WorkflowParam> params){
		SortedSet<WorkflowRunParam> runParams = new TreeSet<WorkflowRunParam>();
	//	SortedSet<WorkflowParam> params = workflow.getWorkflowParams();
	//  SortedSet<WorkflowParam> params = workflow.getVisibleWorkflowParams();
		for (WorkflowParam param : params) {
			WorkflowRunParam runParam = new WorkflowRunParam();
			runParam.setWorkflowRunParamId(param.getWorkflowParamId());
			runParam.setKey(param.getKey());
			runParam.setType(param.getType());
			runParam.setValue(param.getValue());
			
			Log.info("Key = " + param.getKey() + "; Type = " + param.getType() + "; Value = " + param.getValue() + ";");
			runParams.add(runParam);
		}
		return runParams;
	}
*/
	private boolean nextWorkflowParam(HttpServletRequest request){
		Workflow workflow = getCurrentWorkflow(request);
		WorkflowParam currWorkflowParam = getCurrentWorkflowParam(request);
		
		boolean isHasNext = false;
		boolean isFindCurr = false;
		WorkflowParam nextParam = null; 
		//SortedSet<WorkflowParam> workflowParams = workflow.getVisibleWorkflowParams();
		SortedSet<WorkflowParam> workflowParams = workflow.getWorkflowParamsWithDifferentFileMetaType();
		for (WorkflowParam workflowParam : workflowParams) {
			if(isFindCurr){
				isHasNext = true;
				nextParam = workflowParam;
				break;
			}
			if(currWorkflowParam.equals(workflowParam)){
				isFindCurr = true;
			}
		}
		
		if(isHasNext){
			request.getSession(false).setAttribute("workflowParam", nextParam);
		}
		return isHasNext;
	}

	/**
	 * Handles the user's request to cancel the study
	 * or the study update page.
	 *
	 * @param command Study command object
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @return a {@link org.springframework.web.servlet.ModelAndView} object.
	 * @throws java.lang.Exception if any.
	 */
	public ModelAndView handlePrevious (HttpServletRequest request,
									    HttpServletResponse response,
									    Workflow command) throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");
		
		ModelAndView	modelAndView = null;
		
		boolean isHasPrevParam = previousWorkflowParam(request); 
		if(isHasPrevParam){
			modelAndView = new ModelAndView("SelectInput");
		}else{
			HashMap<String,Object> model = new HashMap<String,Object>();
			Workflow workflow = getCurrentWorkflow(request);
			request.setAttribute(getCommandName(command), workflow);
			
			List<Workflow> list = getWorkflowService().list();
			model.put("workflows", list);
			
			modelAndView = new ModelAndView("LaunchWorkflow", model);
		}
		
		// set this attribute for view Select Inputs bar
		request.setAttribute("isHasSelectedInputMenu", true);
		
		return modelAndView;
	}

	private boolean previousWorkflowParam(HttpServletRequest request){
		Workflow workflow = getCurrentWorkflow(request);
		WorkflowParam currWorkflowParam = getCurrentWorkflowParam(request);
		
		boolean isHasPrev = true;
		boolean isFirstParam = true;
		//SortedSet<WorkflowParam> workflowParams = workflow.getVisibleWorkflowParams();
		SortedSet<WorkflowParam> workflowParams = workflow.getWorkflowParamsWithDifferentFileMetaType();
		WorkflowParam previousParam = null;
		
		for (WorkflowParam workflowParam : workflowParams) {
			if(isFirstParam){
				isFirstParam = false;
				if(currWorkflowParam.equals(workflowParam)){
					isHasPrev = false;
				}
			}
			if(currWorkflowParam.equals(workflowParam)){
				break;
			}			
			previousParam = workflowParam;
		}
		
		// remove selected items in session
		if(isHasPrev){
		//	LaunchWorkflowUtil.removeSelectedItemsLastParam(request);
		}else{
		//	LaunchWorkflowUtil.removeSelectedItemsLaunchWorkflow(request);
		}
			
		
		if(previousParam!=null){
			request.getSession(false).setAttribute("workflowParam", previousParam);
		}
		
		return isHasPrev;
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
	
	private WorkflowParam getCurrentWorkflowParam(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object workflowParam = session.getAttribute("workflowParam");
			if (workflowParam != null) {
				return (WorkflowParam)workflowParam;
			}
		}
		return new WorkflowParam();
	}
}

