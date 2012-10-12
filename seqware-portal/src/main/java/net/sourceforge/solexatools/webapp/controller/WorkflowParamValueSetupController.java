package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.WorkflowParamService;
import net.sourceforge.seqware.common.business.WorkflowParamValueService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowParamValue;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>WorkflowParamValueSetupController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowParamValueSetupController extends BaseCommandController {
	private WorkflowParamService workflowParamService;
	private WorkflowParamValueService workflowParamValueService;

	/**
	 * <p>Constructor for WorkflowParamValueSetupController.</p>
	 */
	public WorkflowParamValueSetupController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}

	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest	 request,
												 HttpServletResponse response)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();
		
		getRequestedWorkflowParam(request);
		WorkflowParamValue		workflowParamValue	= getRequestedWorkflowParamValue(request);

		if (workflowParamValue != null) {
			request.setAttribute(getCommandName(), workflowParamValue);
			model.put("strategy", "update");
			modelAndView = new ModelAndView("WorkflowParamValue", model);
		} else {
			request.setAttribute(getCommandName(), new WorkflowParam());
			model.put("strategy", "submit");
			modelAndView = new ModelAndView("WorkflowParamValue", model);
		}
		
		return modelAndView;
	}
	
	private WorkflowParam getRequestedWorkflowParam(HttpServletRequest request) {
		HttpSession	session	= request.getSession(false);
		WorkflowParam workflowParam = null;
		String id = (String)request.getParameter("workflowParamId");
		session.removeAttribute("workflowParam");

		if (id != null) {
			Integer expID = Integer.parseInt(id);
			workflowParam = getWorkflowParamService().findByID(expID);
			session.setAttribute("workflowParam", workflowParam);
		}

		return workflowParam;
	}
	
	private WorkflowParamValue getRequestedWorkflowParamValue(HttpServletRequest request) {
		HttpSession	session	= request.getSession(false);
		WorkflowParamValue workflowParamValue = null;
		String id = (String)request.getParameter("workflowParamValueId");
	//	session.removeAttribute("workflowParamValue");

		if (id != null) {
			Integer workflowParamValueID = Integer.parseInt(id);
			workflowParamValue = getWorkflowParamValueService().findByID(workflowParamValueID);
			session.setAttribute("workflowParamValue", workflowParamValue);
		}

		return workflowParamValue;
	}
	
	/**
	 * <p>Getter for the field <code>workflowParamService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.WorkflowParamService} object.
	 */
	public WorkflowParamService getWorkflowParamService() {
		return workflowParamService;
	}

	/**
	 * <p>Setter for the field <code>workflowParamService</code>.</p>
	 *
	 * @param workflowParamService a {@link net.sourceforge.seqware.common.business.WorkflowParamService} object.
	 */
	public void setWorkflowParamService(WorkflowParamService workflowParamService) {
		this.workflowParamService = workflowParamService;
	}

	/**
	 * <p>Getter for the field <code>workflowParamValueService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.WorkflowParamValueService} object.
	 */
	public WorkflowParamValueService getWorkflowParamValueService() {
		return workflowParamValueService;
	}

	/**
	 * <p>Setter for the field <code>workflowParamValueService</code>.</p>
	 *
	 * @param workflowParamValueService a {@link net.sourceforge.seqware.common.business.WorkflowParamValueService} object.
	 */
	public void setWorkflowParamValueService(WorkflowParamValueService workflowParamValueService) {
		this.workflowParamValueService = workflowParamValueService;
	}
}
