package net.sourceforge.solexatools.webapp.controller;

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
 * <p>WorkflowParamListController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowParamListController extends BaseCommandController {
	private WorkflowService workflowService;
	
	/**
	 * <p>Constructor for WorkflowParamListController.</p>
	 */
	public WorkflowParamListController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}

	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
												 HttpServletResponse response)
		throws Exception {

		//Registration registration = Security.requireRegistration(request, response);
		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		/**
		 * Pass registration so that we can filter the list if its appropriate
		 * to do so.
		 */
		Integer workflowId 			= getRequestedWorflowId(request);
		Workflow workflow	 		= getWorkflowService().findByID(workflowId); 
		ModelAndView modelAndView	= new ModelAndView("WorkflowParamList");
		
		// validation workflow for Launch workflow process
		Boolean isHasError = false;
		String errorMessage = "";
		
		if(workflowId==null){
			isHasError = true;
			errorMessage = this.getMessageSourceAccessor().getMessage("launcWorkflow.error.no.defined.workflow");
		}
/*		if(!isHasError && (workflow.getVisibleWorkflowParams() == null || 
				workflow.getVisibleWorkflowParams().size() == 0) )
		{
			isHasError = true;
			errorMessage = this.getMessageSourceAccessor().
				getMessage("error.list.visible.workflow.param.empty", new Object[] {workflow.getFullName()});
		}
*/
		if(!workflow.isLaunch()){
			isHasError = true;
			errorMessage = this.getMessageSourceAccessor().
				getMessage("error.list.visible.workflow.param.empty", new Object[] {workflow.getFullName()});
		}
		
		String defaultValueIfValueEmpty =  
			this.getMessageSourceAccessor().getMessage("defaultValue.if.value.workflow.param.empty");
		
		modelAndView.addObject("defaultValueIfValueEmpty", defaultValueIfValueEmpty);
		modelAndView.addObject("isHasError", isHasError);
		modelAndView.addObject("errorMessage", errorMessage);
		modelAndView.addObject("workflow", workflow);
//		workflow.getVisibleWorkflowParams();
		
		modelAndView.addObject("registration", registration);
		
		// remove all selected Items in Session
		//LaunchWorkflowUtil.removeSelectedItemsLaunchWorkflow(request);
		Workflow currWorkflow = LaunchWorkflowUtil.getCurrentWorkflow(request);
		if(currWorkflow.getWorkflowId()!=null && !currWorkflow.equals(workflow)){
			LaunchWorkflowUtil.removeSelectedItemsLaunchWorkflow(request);
			//request.getSession(false).removeAttribute("selectedWorkflowParamValueId");
		}
		
		return modelAndView;
	}
	
	private Integer getRequestedWorflowId(HttpServletRequest request){
		Integer workflowId = null;
		String strWorkflowId = request.getParameter("workflowId");
		if(strWorkflowId != null && !strWorkflowId.equals("null")){
			workflowId = Integer.parseInt(strWorkflowId);
		}
		return workflowId;
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
