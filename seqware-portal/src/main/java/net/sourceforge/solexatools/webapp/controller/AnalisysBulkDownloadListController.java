package net.sourceforge.solexatools.webapp.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>AnalisysBulkDownloadListController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class AnalisysBulkDownloadListController extends BaseCommandController {
	private WorkflowRunService workflowRunService;
	
	/**
	 * <p>Constructor for AnalisysBulkDownloadListController.</p>
	 */
	public AnalisysBulkDownloadListController() {
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
		
		//List<Workflow>	list			= getWorkflowService().list(registration);
		List<WorkflowRun>	list			= getWorkflowRunService().list(registration);
		ModelAndView		modelAndView	= new ModelAndView("AnalisysBulkDownloadList");
		modelAndView.addObject("workflowRuns", list);
		modelAndView.addObject("registration", registration);
		modelAndView.addObject("warningSize", warningSize);

		initSortingTreeAttr(request);
		
		// clear list study bulk download files from session
		request.getSession(false).removeAttribute("bulkDownloadFiles");
		request.getSession(false).removeAttribute("selectedIds");
		request.getSession(false).removeAttribute("selectedNodes");
		
		return modelAndView;
	}
	

	private void initSortingTreeAttr(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		if (session.getAttribute("ascBulkDownloadMyListAnalysis") == null){
			session.setAttribute("ascBulkDownloadMyListAnalysis", true);
			session.setAttribute("ascBulkDownloadSharedWithMeListAnalysis", true);
		}
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
