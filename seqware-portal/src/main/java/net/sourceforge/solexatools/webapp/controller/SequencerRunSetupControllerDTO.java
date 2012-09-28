package net.sourceforge.solexatools.webapp.controller;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunDTO;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * SequencingRunSetupController
 */

public class SequencerRunSetupControllerDTO extends BaseCommandController {
	private SequencerRunService sequencerRunService;

	public SequencerRunSetupControllerDTO() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest	 request,
												 HttpServletResponse response)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");
		
		ModelAndView			modelAndView	= null;
		
		HashMap<String,String>	model			= new HashMap<String,String>();
		
		String page = (String)request.getParameter("page");
		
		SequencerRunDTO srdto = (SequencerRunDTO)request.getAttribute("SequencerRunDTO");

		
		if (registration == null) {
		  
		}
		
		
		if (false) {
  		
  		SequencerRun				sequencerRun		= getRequestedSequencerRun(request);
  
  		if (sequencerRun != null) {
  			if (sequencerRun.getStatus() != null
  				&& sequencerRun.getStatus().startsWith("ready_to_process")) {
  				sequencerRun.setReadyToProcess("Y");
  			} else {
  				sequencerRun.setReadyToProcess(null);
  			}
  			request.setAttribute(getCommandName(), sequencerRun);
  			model.put("strategy", "update");
  			modelAndView = new ModelAndView("SequencerRun1", model);
  		} else {
  			request.setAttribute(getCommandName(), new SequencerRun());
  			model.put("strategy", "submit");
  			modelAndView = new ModelAndView("SequencerRun1", model);
  		}
		}
		
		return modelAndView;
	}

	private SequencerRun getRequestedSequencerRun(HttpServletRequest request) {
		HttpSession	session		= request.getSession(false);
		SequencerRun	sequencerRun	= null;
		String		id			= (String)request.getParameter("expID");

		if (id != null) {
			Integer expID = Integer.parseInt(id);
			sequencerRun = getSequencerRunService().findByID(expID);
			session.setAttribute("sequencerRun", sequencerRun);
		}

		return sequencerRun;
	}

	public SequencerRunService getSequencerRunService() {
		return sequencerRunService;
	}

	public void setSequencerRunService(SequencerRunService sequencerRunService) {
		this.sequencerRunService = sequencerRunService;
	}
}
