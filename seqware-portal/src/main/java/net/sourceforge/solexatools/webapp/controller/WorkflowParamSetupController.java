package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.WorkflowParamService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>WorkflowParamSetupController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowParamSetupController extends BaseCommandController {
	private WorkflowParamService workflowParamService;

	/**
	 * <p>Constructor for WorkflowParamSetupController.</p>
	 */
	public WorkflowParamSetupController() {
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
		WorkflowParam			workflowParam	= getRequestedWorkflowParam(request);

		if (workflowParam != null) {
			request.setAttribute(getCommandName(), workflowParam);
			model.put("strategy", "update");
			modelAndView = new ModelAndView("WorkflowParam", model);
		} else {
			request.setAttribute(getCommandName(), new WorkflowParam());
			model.put("strategy", "submit");
			modelAndView = new ModelAndView("WorkflowParam", model);
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
}
