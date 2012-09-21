package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.solexatools.Security;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class WorkflowController extends MultiActionController{
	private WorkflowService workflowService;
    
	private Validator validator;

	public WorkflowController() {
		super();
	}

	public WorkflowController(Object delegate) {
		super(delegate);
	}

	public WorkflowService getWorkflowService() {
		return workflowService;
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public Validator getValidator() {
		return validator;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
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
									 Workflow				command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView	modelAndView = null;
		BindingResult	errors = this.validateWorkflow(request, command);
		if (errors.hasErrors()) {
			Map model = errors.getModel();
			modelAndView = new ModelAndView("Workflow", model);
		} else {
		    command.setOwner(registration);
			getWorkflowService().insert(command);
			
			String isAddValue = request.getParameter("isAddValue");
			
			if ("true".equals(isAddValue)) {
				modelAndView = new ModelAndView("redirect:/workflowParamSetup.htm");
			//	modelAndView.addObject("workflowId", command.getWorkflowId());
				request.getSession(false).setAttribute("workflow", command);
			} else {
			
				modelAndView = new ModelAndView("redirect:/manageWorkflows.htm");
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

		return new ModelAndView("redirect:/manageWorkflows.htm"); 
	}

	/**
	 * Handles the user's request to update their study.
	 *
	 * @param command Study command object
	 */
	public ModelAndView handleUpdate(HttpServletRequest		request,
									 HttpServletResponse	response,
									 Workflow				command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView	modelAndView= null;
		BindingResult	errors		= this.validateWorkflow(request, command);
		if (errors.hasErrors()) {
			Map model = errors.getModel();
			request.setAttribute("swid", getCurrentWorkflow(request).getSwAccession());
			model.put("strategy", "update");
			modelAndView = new ModelAndView("Workflow", model);
		} else {
			Workflow newWorkflow = command;
			Workflow oldWorkflow = getCurrentWorkflow(request);
			if (newWorkflow != null && oldWorkflow != null) {
				new ServletRequestDataBinder(oldWorkflow).bind(request);
				oldWorkflow.setOwner(registration);
				getWorkflowService().update(oldWorkflow);
				modelAndView = new ModelAndView("redirect:/manageWorkflows.htm");
			} else {
				modelAndView = new ModelAndView("redirect:/Error.htm");
			}
		}	 
		return modelAndView;
	}
	
	/**
	 * Handles the user's request to delete their study.
	 *
	 * @param command Study command object
	 */
	public ModelAndView handleDelete(HttpServletRequest		request,
									 HttpServletResponse	response,
									 Workflow				command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();
		Workflow				workflow		= getRequestedWorkflow(request);

		if (workflow != null) {
		    if(registration.equals(workflow.getOwner()) || registration.isLIMSAdmin()){
		    	getWorkflowService().delete(workflow);
		    }
		} 
		
		modelAndView = new ModelAndView("redirect:/manageWorkflows.htm", model);
		return modelAndView;
	}
	
	/**
	 * Handles the user's request to share their study.
	 *
	 * @param command Study command object
	 */
	public ModelAndView handleShare(HttpServletRequest		request,
									 HttpServletResponse	response,
									 Study				command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();
	/*	Study				    study			= getRequestedStudy(request);

		if (study != null) {
			Integer ownerId = study.getOwner().getRegistrationId();
			Integer registrationId = registration.getRegistrationId();
			String[] emails = getRequestedEmails(request);
			
		    if(registrationId.equals(ownerId)){
		    	for (String email : emails) {
		    		ShareStudy shareStudy = new ShareStudy();
		    		shareStudy.setEmail(email);
		    		shareStudy.setStudyId(study.getStudyId());
					getShareStudyService().insert(shareStudy);
				}
		    }
		} 
	*/
		modelAndView = new ModelAndView("redirect:/myAnalisysList.htm", model);
		return modelAndView;
	}

	/**
	 * Validates a study.
	 *
	 * @param command the Command instance as an Object
	 *
	 * @return BindingResult validation errors
	 */
	private BindingResult validateWorkflow(HttpServletRequest request, Object command) {
		BindingResult errors = new BindException(command, getCommandName(command));
		//ValidationUtils.invokeValidator(getValidator(), command, errors);
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
		
	private Workflow getRequestedWorkflow(HttpServletRequest request) {
		Workflow	workflow	= null;
		String		id		= (String)request.getParameter("objectId");
		
		if (id != null) {
			Integer studyID = Integer.parseInt(id);
			workflow = getWorkflowService().findByID(studyID);
		}
		return workflow;
	}
}
