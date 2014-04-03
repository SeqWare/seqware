package net.sourceforge.solexatools.webapp.controller;

import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowParamValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.LaunchWorkflowUtil;
import net.sourceforge.solexatools.util.ModelUtil;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>ChangeWorkflowParamValueController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ChangeWorkflowParamValueController  extends BaseCommandController {
	private WorkflowService workflowService;
	
	/**
	 * <p>Constructor for ChangeWorkflowParamValueController.</p>
	 */
	public ChangeWorkflowParamValueController() {
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
		
		Integer workflowParamId		= getRequestedWorkflowParamId(request);
		Integer workflowParamValueId= getRequestedWorkflowParamValueId(request);
		String strWorkflowParamValueId	= request.getParameter("workflowParamValueId");
		
		LaunchWorkflowUtil.saveSelectedWorkflowParamValue(request, workflowParamId, strWorkflowParamValueId);
		
		String displayName = getDisplayName(workflow, workflowParamId, strWorkflowParamValueId, workflowParamValueId);
		
		
		ModelAndView modelAndView	= new ModelAndView("ResultChangeWorkflowParamValue");
		
		// validation workflow for Launch workflow process
		Boolean isHasError = false;
		String errorMessage = "";
		
		
		modelAndView.addObject("isHasError", isHasError);
		modelAndView.addObject("errorMessage", ModelUtil.forJSON(errorMessage));
		modelAndView.addObject("displayName", ModelUtil.forJSON(displayName));
		
		modelAndView.addObject("registration", registration);
		
		return modelAndView;
	}
	
	private String getDisplayName(Workflow workflow, Integer workflowParamId, 
			String strWorkflowParamValueId, Integer workflowParamValueId)
	{
		String displayName = "";
		String defaultValue = "";
		
		if(workflow != null && workflowParamId != null && strWorkflowParamValueId != null){
		//	SortedSet<WorkflowParam> wps = workflow.getWorkflowParams();
			SortedSet<WorkflowParam> wps = workflow.getVisibleWorkflowParams();
			
			//Log.info(" workflow param id = " + workflowId);
			for (WorkflowParam param : wps) {
				//Log.info("strWorkflowParamValueId = " + strWorkflowParamValueId);
				if("defaultValue".equals(strWorkflowParamValueId)){
					//Log.info(" if Default Value");
					displayName = param.getDisplayName();
					defaultValue = param.getDefaultValue();
					
					Log.info(" default value = " + defaultValue);
					if(workflowParamId.equals(param.getWorkflowParamId())){
						Log.info(" Finded workflow param!!!");
						
						// for Please select value
						if(param.getDefaultValue() == null || "".equals(param.getDefaultValue())){
							return param.getDisplayName();
						}
						
						SortedSet<WorkflowParamValue> wpvs = param.getValues();
						for (WorkflowParamValue value : wpvs) {
							
							Log.info(" value = " + value.getValue());
							if(defaultValue.equals(value.getValue())){
								displayName = value.getDisplayName();
								
								Log.info(" EQUALS. NAME = " + displayName);
							//	return displayName;
							}
						}
						return displayName;
					}
				}else{
				//	Integer workflowParamValueId = getRequestedWorkflowParamValueId(request);
					if(workflowParamId.equals(param.getWorkflowParamId())){
						SortedSet<WorkflowParamValue> wpvs = param.getValues();
						for (WorkflowParamValue value : wpvs) {
							if(workflowParamValueId.equals(value.getWorkflowParamValueId())){
								displayName = value.getDisplayName();
								return displayName;
							}
						}
					}
				}
			}
		}
		
		return displayName;
	}
	
	private Integer getRequestedWorflowId(HttpServletRequest request){
		Integer workflowId = null;
		String strWorkflowId = request.getParameter("workflowId");
		if(strWorkflowId != null && !strWorkflowId.equals("null")){
			workflowId = Integer.parseInt(strWorkflowId);
		}
		return workflowId;
	}
	
	private Integer getRequestedWorkflowParamId(HttpServletRequest request){
		Integer workflowParamId = null;
		String strWorkflowParamId = request.getParameter("workflowParamId");
		if(strWorkflowParamId != null && !strWorkflowParamId.equals("null")){
			workflowParamId = Integer.parseInt(strWorkflowParamId);
		}
		return workflowParamId;
	}
	
	private Integer getRequestedWorkflowParamValueId(HttpServletRequest request){
		Integer workflowParamValueId = null;
		String strWorkflowParamValueId = request.getParameter("workflowParamValueId");
		if(strWorkflowParamValueId != null && !strWorkflowParamValueId.equals("null") &&
				!strWorkflowParamValueId.equals("defaultValue"))
		{
			workflowParamValueId = Integer.parseInt(strWorkflowParamValueId);
		}
		return workflowParamValueId;
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
