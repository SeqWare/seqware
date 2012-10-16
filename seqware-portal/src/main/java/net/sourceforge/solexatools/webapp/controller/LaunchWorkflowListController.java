package net.sourceforge.solexatools.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>LaunchWorkflowListController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LaunchWorkflowListController  extends BaseCommandController {
	private StudyService studyService;
	
	/**
	 * <p>Constructor for LaunchWorkflowListController.</p>
	 */
	public LaunchWorkflowListController() {
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
		String timeout = this.getServletContext().getInitParameter("timeout.load.report.bundle");
		
//		List<Study>	list			= getStudyService().list(registration);
		
		ModelAndView		modelAndView	= new ModelAndView("SelectInput");
//		modelAndView.addObject("studys", list);
		modelAndView.addObject("registration", registration);
		modelAndView.addObject("timeout", timeout);
		
		HttpSession session = request.getSession(false);

		// clear list analysis bulk download files from session
	//	Log.info("RemAnalysis");
        session.removeAttribute("selectedInput"); // bulkDownloadFiles
        session.removeAttribute("launchWorkflowIds");
        session.removeAttribute("launchWorkflowNodes");
				

		return modelAndView;
	}

	/**
	 * <p>Getter for the field <code>studyService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.StudyService} object.
	 */
	public StudyService getStudyService() {
		return studyService;
	}

	/**
	 * <p>Setter for the field <code>studyService</code>.</p>
	 *
	 * @param studyService a {@link net.sourceforge.seqware.common.business.StudyService} object.
	 */
	public void setStudyService(StudyService studyService) {
		this.studyService = studyService;
	}
}
