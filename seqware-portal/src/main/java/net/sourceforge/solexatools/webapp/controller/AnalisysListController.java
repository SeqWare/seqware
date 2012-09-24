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
 */

public class AnalisysListController extends BaseCommandController {
	private WorkflowService workflowService;
	private WorkflowRunService workflowRunService;
	
	public AnalisysListController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}

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
	public WorkflowService getWorkflowService() {
		return workflowService;
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public WorkflowRunService getWorkflowRunService() {
		return workflowRunService;
	}

	public void setWorkflowRunService(WorkflowRunService workflowRunService) {
		this.workflowRunService = workflowRunService;
	}
}
