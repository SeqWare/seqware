package net.sourceforge.solexatools.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;

import org.apache.commons.io.FileUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>DeleteLoadedIndexPageController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class DeleteLoadedIndexPageController extends BaseCommandController {
	
	/** Constant <code>SEPARATOR="java.io.File.separator"</code> */
	public final static String SEPARATOR = java.io.File.separator;
	
	/**
	 * <p>Constructor for DeleteLoadedIndexPageController.</p>
	 */
	public DeleteLoadedIndexPageController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}

	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
												 HttpServletResponse response)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		/**
		 * Pass registration so that we can filter the list if its appropriate
		 * to do so.
		 */
		String pathToTempStore = "webapps" + this.getServletContext().getContextPath() + 
		/*SEPARATOR +"WEB-INF" + */ SEPARATOR +  "temp";
		
		String pathToUserTempStore = pathToTempStore + SEPARATOR + registration.getEmailAddress();
		
		Log.info("Delete index page. pathToUserTempStore = " + pathToUserTempStore);
		
		java.io.File userTempStore = new java.io.File(pathToUserTempStore);
		
		FileUtils.deleteDirectory(userTempStore);
		
		ModelAndView modelAndView	= null; //new ModelAndView("XXX");
		
		
		return modelAndView;
	}
}

