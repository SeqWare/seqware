package	net.sourceforge.solexatools.webapp.controller;				// -*- tab-width: 4 -*-
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.PlatformService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.RegistrationDTO;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunWizardDTO;
import net.sourceforge.solexatools.Security;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class SequencerRunWizardController extends MultiActionController {
	private SequencerRunService	sequencerRunService;
	private PlatformService platformService;
	private Validator sequencerRunValidator;
	private Validator sequencerRunWizardValidator;
	/**
	 * Handles the user's request to submit a new registration.
	 *
	 * @param command RegistrationDTO command object
	 */
	public ModelAndView handleSubmit(HttpServletRequest		request,
									 HttpServletResponse	response,
									 SequencerRunWizardDTO	command)
		throws Exception {
		
		Registration registration = Security.getRegistration(request);
	  	if(registration == null)
	  		return new ModelAndView("redirect:/login.htm");

		ModelAndView modelAndView;
		HttpSession session   = request.getSession(false);
		
		BindingResult errors = this.validateSequencerRunWizard(request, command);
		if (errors.hasErrors()) {
			Map model = errors.getModel();
			model.put("platformList", getPlatformService().list(registration));
			model.put("strategy", "submit");
			modelAndView = new ModelAndView("SequencerRunWizard", model);
		} else {
		  command.setPlatform(getPlatformService().findByID(command.getPlatformInt()));
			// TODO: need to translate form DTO to SequencerRun object for persistence
		  command.setOwner(registration);
		  getSequencerRunService().insert(command);
		  //System.err.println("sequencerrunid: "+command.getSequencerRunId());
		  //SequencerRun seqRun = getSequencerRunService().findByID(command.getSequencerRunId());
		  // now save this SequencerRun in the session
		  session.setAttribute("sequencerRun", command);
		  //modelAndView = new ModelAndView("redirect:/sequencerRunList.htm");
		  modelAndView = new ModelAndView("redirect:/sequencerRunWizardEdit.htm?sequencerRunId="+command.getSequencerRunId()+"&tt=sr");
		}
		
	//	request.getSession(false).removeAttribute("sequencerRun");
		
		return modelAndView;
	}

	/**
	 * Handles the user's request to reset the registration page during a new or
	 * update registration.
	 *
	 * @param command RegistrationDTO command object
	 */
	public ModelAndView handleReset(HttpServletRequest		request,
									HttpServletResponse		response,
									SequencerRunWizardDTO	command)
		throws Exception {
		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");
		
		ModelAndView modelAndView = null;
		HashMap<String,Object> model = new HashMap<String,Object>();
		SequencerRun sequencerRun = getCurrentSequencerRun(request);
		model.put("platformList", getPlatformService().list(registration));
		if (sequencerRun.getUpdateTimestamp() == null) {
		    model.put("strategy", "submit");
		    request.setAttribute(getCommandName(command),  new SequencerRunWizardDTO() );
		} else {
			model.put("strategy", "update");
			model.put("swid", sequencerRun.getSwAccession());
			request.setAttribute(getCommandName(command), sequencerRun);
		}

		modelAndView = new ModelAndView("SequencerRunWizard", model);
		//request.setAttribute(getCommandName(command), sequencerRun);
		
		return modelAndView;
	}

	/**
	 * Handles the user's request to cancel the registration
	 * or the registration update page.
	 *
	 * @param command RegistrationDTO command object
	 */
	public ModelAndView handleCancel(HttpServletRequest request,
									 HttpServletResponse response,
									 RegistrationDTO command)
		throws Exception {
		return new ModelAndView("redirect:/sequencerRunList.htm");
	}

	/**
	 * Handles the user's request to update their registration.
	 *
	 * @param command RegistrationDTO command object
	 */
	public ModelAndView handleUpdate(HttpServletRequest request,
									 HttpServletResponse response,
									 SequencerRun command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
	  	if(registration == null)
	  		return new ModelAndView("redirect:/login.htm");
    
		ModelAndView modelAndView = null;
		
		BindingResult errors = this.validateSequencerRun(request, command);
		if (errors.hasErrors()) {
			Map model = errors.getModel();
			model.put("platformList", getPlatformService().list(registration));
			request.setAttribute("swid", getCurrentSequencerRun(request).getSwAccession());
			model.put("strategy", "update");
			modelAndView = new ModelAndView("SequencerRunWizard", model);
		} else {
		  
	//	  SequencerRun oldSequencerRun = getRequestedSequencerRun(request);
		  SequencerRun oldSequencerRun = getCurrentSequencerRun(request);
		  SequencerRun newSequencerRun = command;
		  
		  // set variable which convert with String to Integer
	      oldSequencerRun.setRefLane(newSequencerRun.getRefLane());
		  
		  HttpSession session   = request.getSession(false);
		  
		  if (oldSequencerRun != null && newSequencerRun != null) {
			  
			  oldSequencerRun.setPlatform(getPlatformService().findByID(newSequencerRun.getPlatformInt()));
			  new ServletRequestDataBinder(oldSequencerRun).bind(request);		  
			  if ("Y".equals(oldSequencerRun.getReadyToProcess())) {
			    oldSequencerRun.setProcess(new Boolean(true));
		  }
			  
			 getSequencerRunService().update(oldSequencerRun);
			 session.setAttribute("sequencerRun", command);
	         modelAndView = new ModelAndView("redirect:/sequencerRunList.htm");
	     // modelAndView = new ModelAndView("redirect:/sequencerRunWizardEdit.htm?sequencerRunId="+command.getSequencerRunId());
	      
		  } else {
			 modelAndView = new ModelAndView("redirect:/Error.htm");
		  }
			request.getSession(false).removeAttribute("sequencerRun");
		}
		
		// TODO: fill in this method
		//request.getSession(false).removeAttribute("sequencerRun");
		return modelAndView;
	}
	
	/**
	 * Handles the user's request to delete their study.
	 *
	 * @param command Study command object
	 */
	public ModelAndView handleDelete(HttpServletRequest		request,
									 HttpServletResponse	response,
									 SequencerRun			command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();
		SequencerRun		    sequencerRun	= getRequestedSequencerRun(request);

		if (sequencerRun != null) {
		    if(registration.equals(sequencerRun.getOwner()) || registration.isLIMSAdmin()){
		    	getSequencerRunService().delete(sequencerRun);
		    }
		} 
		modelAndView = new ModelAndView("redirect:/sequencerRunList.htm", model);
		return modelAndView;
	}
	
	 /**
	   * Validates a SequencerRunWizard.
	   *
	   * @param command the Command instance as an Object
	   *
	   * @return BindingResult validation errors
	   */
	  private BindingResult validateSequencerRunWizard(HttpServletRequest request, Object command) {
		BindingResult errors = new BindException(command, getCommandName(command));
	    ValidationUtils.invokeValidator(getSequencerRunWizardValidator(), command, errors);
	    return errors;
	  }
	  
	  /**
	   * Validates a SequencerRun.
	   *
	   * @param command the Command instance as an Object
	   *
	   * @return BindingResult validation errors
	   */
	  private BindingResult validateSequencerRun(HttpServletRequest request, Object command) {
		BindingResult errors = new BindException(command, getCommandName(command));
	    ValidationUtils.invokeValidator(getSequencerRunValidator(), command, errors);
	    return errors;
	  }
	
	private SequencerRun getCurrentSequencerRun(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			SequencerRun srTemp = (SequencerRun)session.getAttribute("sequencerRun");
			Object sequencerRun = sequencerRunService.findByID(srTemp.getSequencerRunId());//session.getAttribute("sequencerRun");
			if (sequencerRun != null) {
				return (SequencerRun)sequencerRun;
			}
		}
		return new SequencerRun();
	}

  private SequencerRun getRequestedSequencerRun(HttpServletRequest request) {
    SequencerRun sequencerRun = null;
    HttpSession session   = request.getSession(false);
    //Study study = null;
    //String    id      = (String)request.getParameter("sequencerRunId");
    String    id      = (String)request.getParameter("objectId");
    if (id != null && !"".equals(id)) {
      sequencerRun = sequencerRunService.findByID(Integer.parseInt(id));
      //sequencerRun = (SequencerRun)session.getAttribute("sequencerRun");
    }
    return(sequencerRun);
  }
	
	/* ********************************************************************** */
	/* Constructors */
	public SequencerRunWizardController() {
		super();
	}

	public SequencerRunWizardController(Object delegate) {
		super(delegate);
	}

	/* ********************************************************************** */
	/* Property SETters and GETters */

  public SequencerRunService getSequencerRunService() {
    return sequencerRunService;
  }

  public void setSequencerRunService(SequencerRunService sequencerRunService) {
    this.sequencerRunService = sequencerRunService;
  }

  public PlatformService getPlatformService() {
    return platformService;
  }

  public void setPlatformService(PlatformService platformService) {
    this.platformService = platformService;
  }

  public Validator getSequencerRunValidator() {
	return sequencerRunValidator;
  }

  public void setSequencerRunValidator(Validator sequencerRunValidator) {
	this.sequencerRunValidator = sequencerRunValidator;
  }

  public Validator getSequencerRunWizardValidator() {
	return sequencerRunWizardValidator;
  }

  public void setSequencerRunWizardValidator(Validator sequencerRunWizardValidator) {
	this.sequencerRunWizardValidator = sequencerRunWizardValidator;
  }
}

// ex:sw=4:ts=4:
