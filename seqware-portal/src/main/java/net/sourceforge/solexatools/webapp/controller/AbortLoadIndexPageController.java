package net.sourceforge.solexatools.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>AbortLoadIndexPageController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class AbortLoadIndexPageController extends BaseCommandController {
    
	/**
	 * <p>Constructor for AbortLoadIndexPageController.</p>
	 */
	public AbortLoadIndexPageController() {
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
		
		request.getSession(false).setAttribute("isUserAbortedViewIndexPage", true);
			
		return null;
	}
}
