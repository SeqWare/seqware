package net.sourceforge.solexatools.webapp.controller;

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
import net.sourceforge.seqware.common.model.WorkflowParamValue;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.LaunchWorkflowUtil;

import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

/**
 * <p>LaunchWorkflowController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LaunchWorkflowController extends SimpleFormController {
    private WorkflowService workflowService;
    private WorkflowRunService workflowRunService;

    /**
     * <p>Constructor for LaunchWorkflowController.</p>
     */
    public LaunchWorkflowController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET, METHOD_POST});
		setCommandClass(Workflow.class);
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
	 * {@inheritDoc}
	 *
	 * Handles the user's request to submit a new study.
	 */
	protected ModelAndView onSubmit(HttpServletRequest	request,
			HttpServletResponse	response,
			Object				command,
			BindException		errors) throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView	modelAndView = null;
		Workflow comm = (Workflow)command;
		
		Log.info("WORKFLOW ID = " + comm.getWorkflowId());
		
		Workflow workflow = getWorkflowService().findByID(comm.getWorkflowId());
		
		boolean isHasError = false;
		if(workflow == null || !workflow.isLaunch()/*workflow.getVisibleWorkflowParams().size() == 0*/){
		//	errors.reject("error.launch.workflow.hasnot.params");
			isHasError = true;
		}
		
		if(!isHasError){
			isHasError = hasError(request, workflow, errors);
		}
		
		if(isHasError){
			List<Workflow> list = getWorkflowService().list();
			request.setAttribute("workflows", list);
			request.setAttribute(getCommandName(), new Workflow());
			modelAndView = showForm(request, response, errors);
		}else{
			setRequestedValues(request, workflow);
			
			boolean isHasParam = setRequestedFirstParam(request, workflow);
			
			request.getSession(false).setAttribute("workflow", workflow);
			
			// set this attribute for view Select Inputs bar
			request.setAttribute("isHasSelectedInputMenu", true);
			if (isHasParam) {
				
				HttpSession session = request.getSession(false);
		        if (session.getAttribute("ascLaunchListStudy") == null ){
		        	session.setAttribute("ascLaunchListStudy", true);
		        } 
		        
				modelAndView = new ModelAndView(getSuccessView());
			} else {
				
				// get current workflow
				SortedSet<WorkflowParam> visibleParams = workflow.getVisibleWorkflowParams();
				
				workflow = getWorkflowService().findByID(workflow.getWorkflowId());
				
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
		}
		
		return modelAndView;
	}
	
	private void clearSession(HttpServletRequest request){
		LaunchWorkflowUtil.removeSelectedItemsLaunchWorkflow(request);
	}
	
	private boolean setRequestedFirstParam(HttpServletRequest request, Workflow workflow){
		//SortedSet<WorkflowParam> params = workflow.getWorkflowParams();
		//for (WorkflowParam workflowParam : params) {
		//	request.getSession(false).setAttribute("workflowParam", workflowParam);
		//}
	//	WorkflowParam workflowParam = workflow.getVisibleWorkflowParams().first();
		boolean isHasParam = false;
		
		WorkflowParam workflowParam = null;
		if(workflow.getWorkflowParamsWithDifferentFileMetaType()!= null){
			if(workflow.getWorkflowParamsWithDifferentFileMetaType().size() > 0){
				workflowParam = workflow.getWorkflowParamsWithDifferentFileMetaType().first();
				isHasParam = true;
			}
		}		
		request.getSession(false).setAttribute("workflowParam", workflowParam);
		return isHasParam;
	}
	
	private boolean setRequestedValues(HttpServletRequest request, Workflow workflow){
		boolean isHasNotError = true;
		SortedSet<WorkflowParam> visibleParams = workflow.getVisibleWorkflowParams();
		for (WorkflowParam visibleWorkflowParam : visibleParams) {
			// if param display
			if(visibleWorkflowParam.getDisplay()){
				//String value = request.getParameter(workflowParam.getWorkflowParamId().toString());
				
				String valueParam = request.getParameter(visibleWorkflowParam.getWorkflowParamId().toString());
				
				Log.info(" Get Launch param value");
				Log.info("	name = " + visibleWorkflowParam.getWorkflowParamId().toString());
				Log.info("	valueParam = " + valueParam);
				
				String value = valueParam;
				
				if("pulldown".equals(visibleWorkflowParam.getType()) && visibleWorkflowParam.getValues().size() > 1){
					Log.info(" Type workflow param is PULLDOWN");	
					
					String[] values = getValues(visibleWorkflowParam, valueParam);
					value = values[0];
					String displayValue = values[1];
					
					Log.info("value = " + value);
					Log.info("displayValue = " + displayValue);
					
					visibleWorkflowParam.setDisplayValue(displayValue);
				}
				
				visibleWorkflowParam.setValue(value);
			}
		}
		return isHasNotError;
	}
	
	private String[] getValues(WorkflowParam workflowParam, String valueParam){
		String value = workflowParam.getDefaultValue();
		String displayValue = workflowParam.getDisplayName();
		
		//if single param DEFAULT VALUE
		if(workflowParam.getValues().size() == 0 && !value.equals("")){
			
		}else
		// if single param ONE PARAM VALUE
		if(workflowParam.getValues().size() == 1 && value.equals("")){
			// get first param value
			value = workflowParam.getValues().first().getValue();
			displayValue = workflowParam.getValues().first().getDisplayName();
		}else
		
		// if multi values
		if(!valueParam.equals("defaultValue")){
			
			SortedSet<WorkflowParamValue> values =  workflowParam.getValues();
			
			for (WorkflowParamValue paramValue : values) {
				if(paramValue.getWorkflowParamValueId().equals(Integer.parseInt(valueParam))){
					value = paramValue.getValue();
					displayValue = paramValue.getDisplayName();
				}
			}	
		}
		String values[] = {value, displayValue};
		return values;
	}
	
	private boolean hasError(HttpServletRequest request, Workflow workflow, BindException errors){
		boolean isHasError = false;
		SortedSet<WorkflowParam> params = workflow.getVisibleWorkflowParams();
		for (WorkflowParam workflowParam : params) {
			// if param display
			if(workflowParam.getDisplay()){
				String valueParamId = request.getParameter(workflowParam.getWorkflowParamId().toString());
			/*	if( (workflowParam.getValues().size() == 0 && workflowParam.getDefaultValue() == null) ||
					(workflowParam.getValues().size() == 0 && workflowParam.getDefaultValue().equals("")) )
				{
					isHasError = true;
					Log.info("Has ERROR! Empty value");
					errors.reject("error.launch.workflow.hasnot.value.for.param");
				}else{
						if( (valueParamId.equals("defaultValue") && workflowParam.getDefaultValue() == null) ||
							(valueParamId.equals("defaultValue") && workflowParam.getDefaultValue().equals("")) )
						{
							isHasError = true;
							errors.reject("error.launch.workflow.not.set.params");
						}
					
				}
		*/		
		//		if(workflowParam.getDefaultValue() != null){
					String valueParam = request.getParameter(workflowParam.getWorkflowParamId().toString());
					if("".equals(valueParam)){
					//	isHasError = true;
					//	errors.reject("error.launch.workflow.not.set.params");
					}else{
						Log.info("workflowParam.getType() = " + workflowParam.getType());
						if("int".equals(workflowParam.getType())){
							if(isHasErrorIntValue(valueParam)){
								isHasError = true;
							//	errors.reject("error.launch.workflow.bad.format.int.value");
							//	Log.info("workflowParam.getDisplayName() = " + workflowParam.getDisplayName());
							//	Log.info("valueParam = " + valueParam);
								
								errors.reject("error.launch.workflow.bad.format.int.value", 
										new Object[] {workflowParam.getDisplayName()}, "bad value");
							}
						}
						if("float".equals(workflowParam.getType())){
							if(isHasErrorFloatValue(valueParam)){
								isHasError = true;
							//	errors.reject("error.launch.workflow.bad.format.float.value");
								errors.reject("error.launch.workflow.bad.format.float.value", 
										new Object[] {workflowParam.getDisplayName()}, "bad value");
							}
						}
					}
		//		}
			}
		}
		return isHasError;
	}
	
	private boolean isHasErrorIntValue(String value){
		boolean isHasError = false;
		try {
			Integer.parseInt(value);
		} catch (Exception e) {
			isHasError = true;
		}
		return isHasError;
	}
	private boolean isHasErrorFloatValue(String value){
		boolean isHasError = false;
		try {
			Float.parseFloat(value);
		} catch (Exception e) {
			isHasError = true;
		}
		return isHasError;
	}
}
