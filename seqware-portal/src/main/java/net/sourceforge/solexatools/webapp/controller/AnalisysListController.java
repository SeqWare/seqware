package	net.sourceforge.solexatools.webapp.controller;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * RegistrationSetupController
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class AnalisysListController extends BaseCommandController {
	private WorkflowService workflowService;
	private WorkflowRunService workflowRunService;
	
	/**
	 * <p>Constructor for AnalisysListController.</p>
	 */
	public AnalisysListController() {
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
	    String warningSize = this.getServletContext().getInitParameter("report.bundle.slow.display.warning.size");	
	    
	    initSortingTreeAttr(request);
	    
	//	List<Workflow>	list			= getWorkflowService().list(registration);
		ModelAndView		modelAndView	= new ModelAndView("AnalisysList");
	//	modelAndView.addObject("workflowRuns", list);
		modelAndView.addObject("registration", registration);
		modelAndView.addObject("warningSize", warningSize);

		return modelAndView;
	}

	private void initSortingTreeAttr(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		if (session.getAttribute("ascMyListAnalysis") == null){
			session.setAttribute("ascMyListAnalysis", true);
			session.setAttribute("ascMySharedAnalysises", true);
			session.setAttribute("ascAnalysisesSharedWithMe", true);
			session.setAttribute("ascMyRunningListAnalysis", true);
		}
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
}
