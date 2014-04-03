package	net.sourceforge.solexatools.webapp.controller;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * RegistrationSetupController
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ProcessingSetupController extends BaseCommandController {
	private SequencerRunService sequencerRunService;
	private ProcessingService processingService;

	/**
	 * <p>Constructor for ProcessingSetupController.</p>
	 */
	public ProcessingSetupController() {
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

		ModelAndView modelAndView = null;
		HashMap <String,String> model = new HashMap<String,String>();

		SequencerRun sequencerRun = figureOutSequencerRun(request);
		Processing processing = figureOutProcessing(request);
		boolean isReport = request.getParameter("report") != null;

		if (sequencerRun != null) {
			model.put("expID", sequencerRun.getSequencerRunId().toString());
		}

		if (processing != null) {
			request.setAttribute(getCommandName(), processing);
			model.put("strategy", "update");
			if (!isReport) {
				modelAndView = new ModelAndView("Processing", model);
			} else {
				modelAndView = new ModelAndView("ProcessingReport", model);
			}
		} 
		
		if (sequencerRun != null && processing == null) {
				request.setAttribute(getCommandName(), new Processing());
				model.put("strategy", "submit");
				modelAndView = new ModelAndView("Processing", model);
		}

		return modelAndView;
	}

	private SequencerRun figureOutSequencerRun(HttpServletRequest request) {

		HttpSession session = request.getSession(false);
		SequencerRun sequencerRun = null;

		String id = (String)request.getParameter("expID");
		if (id != null) {
			sequencerRun	= getSequencerRunService().findByID(Integer.parseInt(id));
			session.setAttribute("sequencerRun", sequencerRun);
		}

		return sequencerRun;
	}

	private Processing figureOutProcessing(HttpServletRequest request) {
		HttpSession	session		= request.getSession(false);
		Processing	processing	= null;

		String id = (String)request.getParameter("procID");

		if (id != null) {
			Integer procID = Integer.parseInt(id);
			processing = getProcessingService().findByID(procID);
			session.setAttribute("processing", processing);
		}

		return processing;
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


	/**
	 * <p>Getter for the field <code>processingService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.ProcessingService} object.
	 */
	public ProcessingService getProcessingService() {
		return processingService;
	}

	/**
	 * <p>Setter for the field <code>processingService</code>.</p>
	 *
	 * @param processingService a {@link net.sourceforge.seqware.common.business.ProcessingService} object.
	 */
	public void setProcessingService(ProcessingService processingService) {
		this.processingService = processingService;
	}
}
