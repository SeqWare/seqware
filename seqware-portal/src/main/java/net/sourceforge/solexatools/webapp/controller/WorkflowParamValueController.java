package net.sourceforge.solexatools.webapp.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.WorkflowParamValueService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowParamValue;
import net.sourceforge.solexatools.Security;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class WorkflowParamValueController extends MultiActionController{
	//private WorkflowParamService workflowParamService;
	private WorkflowParamValueService workflowParamValueService;
    
	private Validator validator;

	public WorkflowParamValueController() {
		super();
	}

	public WorkflowParamValueController(Object delegate) {
		super(delegate);
	}

	public Validator getValidator() {
		return validator;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}
	
	/*
    public WorkflowParamService getWorkflowParamService() {
		return workflowParamService;
	}

	public void setWorkflowParamService(WorkflowParamService workflowParamService) {
		this.workflowParamService = workflowParamService;
	}
*/
	public WorkflowParamValueService getWorkflowParamValueService() {
		return workflowParamValueService;
	}

	public void setWorkflowParamValueService(
			WorkflowParamValueService workflowParamValueService) {
		this.workflowParamValueService = workflowParamValueService;
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
	public ModelAndView handleSubmit(HttpServletRequest		request,
									 HttpServletResponse	response,
									 WorkflowParamValue		command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView	modelAndView = null;
		BindingResult	errors = this.validateWorkflowParamValue(request, command);
		if (errors.hasErrors()) {
			Map model = errors.getModel();
			model.put("strategy", "submit");
			modelAndView = new ModelAndView("WorkflowParam", model);
		} else {
			WorkflowParam workflowParam = getCurrentWorkflowParam(request);
			command.setWorkflowParam(workflowParam);
						
			getWorkflowParamValueService().insert(command);
			
			String isAddValue = request.getParameter("isAddValue");
			
			if ("true".equals(isAddValue)) {
				modelAndView = new ModelAndView("redirect:/workflowParamValueSetup.htm");
				modelAndView.addObject("workflowParamId", workflowParam.getWorkflowParamId());
			} else {
				modelAndView = new ModelAndView("redirect:/workflowSetup.htm");
				modelAndView.addObject("workflowId", getCurrentWorkflow(request).getWorkflowId());
			}
		}

		return modelAndView;
	}


	/**
	 * Handles the user's request to reset the study page during a new or
	 * update study.
	 *
	 * @param command Study command object
	 */
	public ModelAndView handleReset(HttpServletRequest request,
									HttpServletResponse response,
									Workflow command) throws Exception {

		ModelAndView modelAndView = null;
		return modelAndView;
	}

	/**
	 * Handles the user's request to cancel the study
	 * or the study update page.
	 *
	 * @param command Study command object
	 */
	public ModelAndView handleCancel(HttpServletRequest request,
									 HttpServletResponse response,
									 Workflow command) throws Exception {

		ModelAndView modelAndView = new ModelAndView("redirect:/workflowSetup.htm");
		modelAndView.addObject("workflowId", getCurrentWorkflow(request).getWorkflowId());
		return modelAndView; 
	}

	/**
	 * Handles the user's request to update their study.
	 *
	 * @param command Study command object
	 */
	public ModelAndView handleUpdate(HttpServletRequest		request,
									 HttpServletResponse	response,
									 WorkflowParamValue			command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView	modelAndView= null;
		BindingResult	errors		= this.validateWorkflowParamValue(request, command);
		if (errors.hasErrors()) {
			Map model = errors.getModel();
			model.put("strategy", "update");
			modelAndView = new ModelAndView("WorkflowParamValue", model);
		} else {
			WorkflowParamValue newWorkflowParamValue = command;
			WorkflowParamValue oldWorkflowParamValue = getCurrentWorkflowParamValue(request);
			if (newWorkflowParamValue != null && oldWorkflowParamValue != null) {
				new ServletRequestDataBinder(oldWorkflowParamValue).bind(request);
				getWorkflowParamValueService().update(oldWorkflowParamValue);
				modelAndView = new ModelAndView("redirect:/workflowSetup.htm");
				modelAndView.addObject("workflowId", getCurrentWorkflow(request).getWorkflowId());
			} else {
				modelAndView = new ModelAndView("redirect:/Error.htm");
			}
		}

		request.getSession(false).removeAttribute("Workflow");
	 
		return modelAndView;
	}
	
	/**
	 * Handles the user's request to delete their study.
	 *
	 * @param command Study command object
	 */
	public ModelAndView handleDelete(HttpServletRequest		request,
									 HttpServletResponse	response,
									 WorkflowParamValue		command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
//		HashMap<String,Object>	model			= new HashMap<String,Object>();
//		WorkflowParam			workflowParam	= getRequestedWorkflowParam(request);
		WorkflowParamValue		workflowParamValue	= getRequestedWorkflowParamValue(request);

		if (workflowParamValue != null) {
		    if(registration.isLIMSAdmin()){
		    	getWorkflowParamValueService().delete(workflowParamValue);
		    }
		} 
		modelAndView = new ModelAndView("redirect:/workflowSetup.htm");
		modelAndView.addObject("workflowId", getCurrentWorkflow(request).getWorkflowId());
		return modelAndView;
	}

	/**
	 * Validates a study.
	 *
	 * @param command the Command instance as an Object
	 *
	 * @return BindingResult validation errors
	 */
	private BindingResult validateWorkflowParamValue(HttpServletRequest request, Object command) {
		BindingResult errors = new BindException(command, getCommandName(command));
	//	ValidationUtils.invokeValidator(getValidator(), command, errors);
		return errors;
	}

	/**
	 * Gets the study from the session.
	 *
	 * @param request HttpServletRequest
	 *
	 * @return instance of Study from the session, or a new instance
	 * if the study is not in the session (e.g. the user is not logged in)
	 */
	
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

	private WorkflowParamValue getRequestedWorkflowParamValue(HttpServletRequest request) {
		WorkflowParamValue	workflowParamValue = null;
		String	id	= (String)request.getParameter("objectId");
		if (id != null) {
			Integer workflowParamValueId = Integer.parseInt(id);
			workflowParamValue = getWorkflowParamValueService().findByID(workflowParamValueId);
		}
		return workflowParamValue;
	}
	
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
	
	private WorkflowParamValue getCurrentWorkflowParamValue(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object workflowParamValue = session.getAttribute("workflowParamValue");
			if (workflowParamValue != null) {
				return (WorkflowParamValue)workflowParamValue;
			}
		}
		return new WorkflowParamValue();
	}
}
