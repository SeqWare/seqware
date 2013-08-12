package net.sourceforge.solexatools.webapp.controller;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunStatus;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * SequencingRunSetupController
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SequencerRunSetupController extends BaseCommandController {
	private SequencerRunService sequencerRunService;

	/**
	 * <p>Constructor for SequencerRunSetupController.</p>
	 */
	public SequencerRunSetupController() {
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

		ModelAndView			modelAndView	= null;
		HashMap<String,String>	model			= new HashMap<String,String>();
		SequencerRun				sequencerRun		= getRequestedSequencerRun(request);
		boolean isReport = request.getParameter("report") != null;

		if (sequencerRun != null) {
			if (sequencerRun.getStatus() == SequencerRunStatus.ready_to_process) {
				sequencerRun.setProcess(true);
			} else {
				sequencerRun.setProcess(false);
			}
			request.setAttribute(getCommandName(), sequencerRun);
			model.put("strategy", "update");
			if (!isReport) {
				modelAndView = new ModelAndView("SequencerRun1", model);
			} else {
				modelAndView = new ModelAndView("SequencerRunReport", model);
			}
		} else {
			request.setAttribute(getCommandName(), new SequencerRun());
			model.put("strategy", "submit");
			modelAndView = new ModelAndView("SequencerRun1", model);
		}
		return modelAndView;
	}

	private SequencerRun getRequestedSequencerRun(HttpServletRequest request) {
		HttpSession	session		= request.getSession(false);
		SequencerRun	sequencerRun	= null;
		String		id			= (String)request.getParameter("sequencerRunID");

		if (id != null) {
			Integer expID = Integer.parseInt(id);
			sequencerRun = getSequencerRunService().findByID(expID);
			session.setAttribute("sequencerRun", sequencerRun);
		}

		return sequencerRun;
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
