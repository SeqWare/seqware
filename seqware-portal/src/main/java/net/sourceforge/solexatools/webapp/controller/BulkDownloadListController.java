package net.sourceforge.solexatools.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

public class BulkDownloadListController extends BaseCommandController {
	private StudyService studyService;
	
	public BulkDownloadListController() {
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
		
//		List<Study>	list			= getStudyService().list(registration);
		
		ModelAndView		modelAndView	= new ModelAndView("BulkDownloadList");
//		modelAndView.addObject("studys", list);
		modelAndView.addObject("registration", registration);
		modelAndView.addObject("warningSize", warningSize);
		
		initSortingTreeAttr(request);
		
		// clear list analysis bulk download files from session
	//	Log.info("RemAnalysis");
		request.getSession(false).removeAttribute("analysisBulkDownloadFiles"); // bulkDownloadFiles
		request.getSession(false).removeAttribute("analysisSelectedIds");
		request.getSession(false).removeAttribute("analysisSelectedNodes");
				

		return modelAndView;
	}
	
	private void initSortingTreeAttr(HttpServletRequest request){
		HttpSession session = request.getSession(false);
        if(session.getAttribute("ascBulkDownloadMyListStudy") == null){
        	session.setAttribute("ascBulkDownloadMyListStudy", true);
        	session.setAttribute("ascBulkDownloadSharedWithMeListStudy", true);
        }
	}

	public StudyService getStudyService() {
		return studyService;
	}

	public void setStudyService(StudyService studyService) {
		this.studyService = studyService;
	}
}