package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>IUSSetupController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class IUSSetupController extends BaseCommandController {
	
	private IUSService iusService;

	/**
	 * <p>Getter for the field <code>iusService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.IUSService} object.
	 */
	public IUSService getIusService() {
		return iusService;
	}

	/**
	 * <p>Setter for the field <code>iusService</code>.</p>
	 *
	 * @param iusService a {@link net.sourceforge.seqware.common.business.IUSService} object.
	 */
	public void setIusService(IUSService iusService) {
		this.iusService = iusService;
	}
	
	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView modelAndView = null;
		HashMap <String,String> model = new HashMap<String,String>();

		IUS ius = figureOutIUS(request);

		if (ius != null) {
			request.setAttribute(getCommandName(), ius);
			model.put("strategy", "update");
			modelAndView = new ModelAndView("IUS", model);
		} 

		return modelAndView;
	}

	private IUS figureOutIUS(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		IUS ius = null;

		String id = (String)request.getParameter("iusID");
		if (id != null) {
			ius	= iusService.findByID(Integer.parseInt(id));
			session.setAttribute("ius", ius);
		}

		return ius;
	}

}
