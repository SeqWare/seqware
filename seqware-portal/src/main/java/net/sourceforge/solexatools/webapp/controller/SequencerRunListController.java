package	net.sourceforge.solexatools.webapp.controller;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.SequencerRunService;
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
public class SequencerRunListController extends BaseCommandController {
	private SequencerRunService sequencerRunService;
	/**
	 * <p>Constructor for SequencerRunListController.</p>
	 */
	public SequencerRunListController() {
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
//		List<SequencerRun>	list			= getSequencerRunService().list(registration);
		
		ModelAndView		modelAndView	= new ModelAndView("SequencerRunList");
//		modelAndView.addObject("sequencerRuns", list);
		modelAndView.addObject("registration", registration);
		modelAndView.addObject("warningSize", warningSize);

		return modelAndView;
	}
	
	private void initSortingTreeAttr(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		if (session.getAttribute("ascMyListSequencerRun") == null){
			session.setAttribute("ascMyListSequencerRun", true);
		}
	}

	/**
	 * <p>Getter for the field <code>sequencerRunService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.SequencerRunService} object.
	 */
	public SequencerRunService getSequencerRunService() {
		return sequencerRunService;
	}

	/**
	 * <p>Setter for the field <code>sequencerRunService</code>.</p>
	 *
	 * @param sequencerRunService a {@link net.sourceforge.seqware.common.business.SequencerRunService} object.
	 */
	public void setSequencerRunService(SequencerRunService sequencerRunService) {
		this.sequencerRunService = sequencerRunService;
	}
}
