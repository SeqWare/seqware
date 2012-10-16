package net.sourceforge.solexatools.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>CancelBulkDownloaderController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class CancelBulkDownloaderController extends BaseCommandController {
    
	/**
	 * <p>Constructor for CancelBulkDownloaderController.</p>
	 */
	public CancelBulkDownloaderController() {
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

		request.getSession(false).removeAttribute("bulkDownloadFiles");
		request.getSession(false).removeAttribute("analysisBulkDownloadFiles");
		request.getSession(false).removeAttribute("selectedIds");
		request.getSession(false).removeAttribute("selectedNodes");
		request.getSession(false).removeAttribute("analysisSelectedNodes");//analisysSelectedNodes
		
//		request.getSession(false).removeAttribute("rootStudyId");
//		request.getSession(false).removeAttribute("objectId");		
		return null;
	}
}
