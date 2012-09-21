package net.sourceforge.solexatools.webapp.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.WorkflowParamService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class WorkflowParamController extends MultiActionController{
	private WorkflowParamService workflowParamService;
    
	private Validator validator;

	public WorkflowParamController() {
		super();
	}

	public WorkflowParamController(Object delegate) {
		super(delegate);
	}

	public Validator getValidator() {
		return validator;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

    public WorkflowParamService getWorkflowParamService() {
		return workflowParamService;
	}

	public void setWorkflowParamService(WorkflowParamService workflowParamService) {
		this.workflowParamService = workflowParamService;
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
									 WorkflowParam			command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView	modelAndView = null;
		BindingResult	errors = this.validateWorkflowParam(request, command);
		if (errors.hasErrors()) {
			Map model = errors.getModel();
			model.put("strategy", "submit");
			modelAndView = new ModelAndView("WorkflowParam", model);
		} else {
			Workflow workflow = getCurrentWorkflow(request);
			
			Log.info("wokflow = " + workflow);
			Log.info("wokflowId = " + workflow.getWorkflowId());
			
			command.setWorkflow(workflow);
			getWorkflowParamService().insert(command);
			
			String isAddValue = request.getParameter("isAddValue");
			
			Log.info("	isAddValue = " + isAddValue);
			
			if ("true".equals(isAddValue)) {
				modelAndView = new ModelAndView("redirect:/workflowParamValueSetup.htm");
				modelAndView.addObject("workflowParamId", command.getWorkflowParamId());
		//		Log.info("  add PAram in session = ");
		//		request.getSession(false).setAttribute("workflowParam", command);
			} else {
			
				modelAndView = new ModelAndView("redirect:/workflowSetup.htm");
				modelAndView.addObject("workflowId", workflow.getWorkflowId());
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
									 WorkflowParam			command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView	modelAndView= null;
		BindingResult	errors		= this.validateWorkflowParam(request, command);
		if (errors.hasErrors()) {
			Map model = errors.getModel();
			model.put("strategy", "update");
			modelAndView = new ModelAndView("WorkflowParam", model);
		} else {
			WorkflowParam newWorkflowParam = command;
			WorkflowParam oldWorkflowParam = getCurrentWorkflowParam(request);
			if (newWorkflowParam != null && oldWorkflowParam != null) {
				new ServletRequestDataBinder(oldWorkflowParam).bind(request);
				getWorkflowParamService().update(oldWorkflowParam);
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
									 WorkflowParam			command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
//		HashMap<String,Object>	model			= new HashMap<String,Object>();
		WorkflowParam			workflowParam	= getRequestedWorkflowParam(request);

		if (workflowParam != null) {
		    if(registration.isLIMSAdmin()){
		    	getWorkflowParamService().delete(workflowParam);
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
	private BindingResult validateWorkflowParam(HttpServletRequest request, Object command) {
		BindingResult errors = new BindException(command, getCommandName(command));
		ValidationUtils.invokeValidator(getValidator(), command, errors);
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
		
	private WorkflowParam getRequestedWorkflowParam(HttpServletRequest request) {
		WorkflowParam	workflowParam = null;
		String	id	= (String)request.getParameter("objectId");
		
		if (id != null) {
			Integer workflowParamID = Integer.parseInt(id);
			workflowParam = getWorkflowParamService().findByID(workflowParamID);
		}
		return workflowParam;
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
}
