package net.sourceforge.solexatools.webapp.controller;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.Constant;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * LaneController
 */

public class LaneController extends MultiActionController {
	private SampleService sampleService;
	private LaneService laneService;
	private Validator validator;

	public LaneController() {
		super();
	}

	public SampleService getSampleService() {
		return sampleService;
	}

	public void setSampleService(SampleService sampleService) {
		this.sampleService = sampleService;
	}

	public LaneController(Object delegate) {
		super(delegate);
	}

	public LaneService getLaneService() {
		return laneService;
	}

	public void setLaneService(LaneService laneService) {
		this.laneService = laneService;
	}

	public Validator getValidator() {
		return validator;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	/**
	 * Handles the user's request to submit a new lane.
	 *
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param command Lane command object
	 *
	 * @return ModelAndView
	 *
	 * @throws Exception
	 */
	public ModelAndView handleSubmit(HttpServletRequest request,
									 HttpServletResponse response,
									 Lane command) throws Exception {
		ModelAndView modelAndView = null;
		HttpSession session = request.getSession(false);
		
		BindingResult errors = this.validateLane(request, command);
		if (errors.hasErrors()) {
			Map model = errors.getModel();
		//	Integer sampleId = Integer.parseInt(request.getParameter("sampleId"));
		//  request.setAttribute("sampleId", sampleId);
		    model.put("strategy", "submit");
			modelAndView = new ModelAndView("Lane", model);
		} else {
		    request.setAttribute("command", command);
		    request.getSession(false).removeAttribute("lane");
			getLaneService().insert(command);
			SequencerRun seqRun = (SequencerRun)session.getAttribute("sequencerRun");
			modelAndView = new ModelAndView("redirect:/sequencerRunWizardEdit.htm?sequencerRunId="+seqRun.getSequencerRunId());
			//modelAndView = new ModelAndView("redirect:/myStudyList.htm");
				
			//	request.getSession(false).setAttribute("nodeId", "ae_" + newProcessingId);
  		    // add sample id
  	//	    Sample sample = getSampleService().findByID(command.getSample().getSampleId());
  		    
  	//	    SetNodeIdInSession.setSample(sample, request);
  		 
  	//	    Study rootStudy = FindRootUtil.getStudy(sample);
  	//	    request.getSession(false).setAttribute("rootStudyId", rootStudy.getStudyId());
  	//	    request.getSession(false).setAttribute("objectId", "sam_" + sampleId);	
		}

		return modelAndView;
	}

	/**
	 * Handles the user's request to reset the lane page during a new or
	 * update lane.
	 *
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param command Lane command object
	 *
	 * @return ModelAndView
	 *
	 * @throws Exception
	 */
	public ModelAndView handleReset(HttpServletRequest request,
									HttpServletResponse response,
									Lane command) throws Exception {

		ModelAndView modelAndView = null;
		Lane lane = getCurrentLane(request);
		HashMap<String,Object> model = new HashMap<String,Object>();
		if (lane.getUpdateTimestamp() == null) {
		//	Integer sampleId = Integer.parseInt(request.getParameter("sampleId"));
		//  request.setAttribute("sampleId", sampleId);
			model.put("strategy", "submit");
		} else {
		//	request.setAttribute("sampleId", lane.getSample().getSampleId());
			model.put("strategy", "update");
		}
		modelAndView = new ModelAndView("Lane", model);
		request.setAttribute(getCommandName(command), lane);
		return modelAndView;
	}

	/**
	 * Handles the user's request to cancel the lane
	 * or the lane update page.
	 *
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param command Lane command object
	 *
	 * @return ModelAndView
	 *
	 * @throws Exception
	 */
	public ModelAndView handleCancel(HttpServletRequest request,
									 HttpServletResponse response,
									 Lane command) throws Exception {
		
		// add sample id
//		Integer sampleId = Integer.parseInt(request.getParameter("sampleId"));
//		request.getSession(false).setAttribute("nodeId", "sam_" + sampleId);
		
//		Sample sample = getSampleService().findByID(sampleId);
//		Study rootStudy = FindRootUtil.getStudy(sample);
//		request.getSession(false).setAttribute("rootStudyId", rootStudy.getStudyId());
//		request.getSession(false).setAttribute("objectId", "sam_" + sampleId);
		
		return new ModelAndView(getViewName(request));
	}
	
	private String getViewName(HttpServletRequest request){
		String typeTree = (String)request.getSession(false).getAttribute("typeTree");
		String viewName = Constant.getViewName(typeTree);
		request.getSession(false).removeAttribute("typeTree");
		return viewName;
	}

	/**
	 * Handles the user's request to update their lane.
	 *
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param command Lane command object
	 *
	 * @return ModelAndView
	 *
	 * @throws Exception
	 */
	public ModelAndView handleUpdate(HttpServletRequest request,
									 HttpServletResponse response,
									 Lane command) throws Exception {

	   Registration registration = Security.getRegistration(request);
	    if(registration == null)
	      return new ModelAndView("redirect:/login.htm");
	  
	    HttpSession session = request.getSession(false);
		ModelAndView modelAndView = null;
		BindingResult errors = this.validateLane(request, command);
		if (errors.hasErrors()) {
			Map model = errors.getModel();
		//	Integer sampleId = Integer.parseInt(request.getParameter("sampleId"));
		//  request.setAttribute("sampleId", sampleId);
			model.put("strategy", "update");
			modelAndView = new ModelAndView("Lane", model);
		} else {
			Lane oldLane = getCurrentLane(request);
			if (oldLane != null) {
			  // should fill in with all the new data
			  new ServletRequestDataBinder(oldLane).bind(request);
				getLaneService().update(oldLane);
				request.getSession(false).removeAttribute("lane");
				request.setAttribute("command", command);
				//SequencerRun seqRun = command.getSequencerRun();
				SequencerRun seqRun = (SequencerRun)session.getAttribute("sequencerRun");
        if (seqRun != null) {
          modelAndView = new ModelAndView("redirect:/sequencerRunWizardEdit.htm?sequencerRunId="+seqRun.getSequencerRunId());
        } else {
        	
          modelAndView = new ModelAndView(getViewName(request)); //redirect:/sequencerRunList.htm
          
        //  request.getSession(false).setAttribute("nodeId", "seq_" + oldLane.getLaneId());
          
  		  // add sample id
//  		  Integer sampleId = Integer.parseInt(request.getParameter("sampleId"));
//  		  Study rootStudy = FindRootUtil.getStudy(oldLane);
//  		  request.getSession(false).setAttribute("rootStudyId", rootStudy.getStudyId());
//  		  request.getSession(false).setAttribute("objectId", "sam_" + sampleId);
        }
			} else {
				modelAndView = new ModelAndView("redirect:/Error.htm");
			}
		}
		return modelAndView;
	}

	/**
	 * Validates a lane.
	 *
	 * @param request HttpServletRequest
	 * @param command the Command instance as an Object
	 *
	 * @return BindingResult validation errors
	 *
	 */
	private BindingResult validateLane(HttpServletRequest request, Object command) {
		BindingResult errors = new BindException(command, getCommandName(command));
		ValidationUtils.invokeValidator(getValidator(), command, errors);

		/* if (!errors.hasErrors()) {

		Lane lane = (Lane) command;

		// Make sure that the email address has not yet been used.
		if (getLaneService()
		.hasNameBeenUsed(getNameFromSession(request),
		lane.getName())) {
		errors.reject("lane.error.name");
		}
		} */

		return errors;
	}

	/**
	 * Gets the name from the lane in the session.
	 *
	 * @param request HttpServletRequest
	 *
	 * @return the name from the lane in the session, or null if
	 * there is no lane in the session
	 */
	private String getNameFromSession(HttpServletRequest request) {
		return getCurrentLane(request).getName();
	}

	/**
	 * Gets the lane from the session.
	 *
	 * @param request HttpServletRequest
	 *
	 * @return instance of Lane from the session, or a new instance
	 * if the lane is not in the session (e.g. the user is not logged in)
	 */
	private Lane getCurrentLane(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object lane = session.getAttribute("lane");
			if (lane != null) {
				return (Lane)lane;
			}
		}
		return new Lane();
	}
	/**
	 * Handles the user's request to delete their lane.
	 *
	 * @param command Lane command object
	 */
	public ModelAndView handleDelete(HttpServletRequest		request,
									 HttpServletResponse	response,
									 Lane				command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();
		Lane				    lane			= getRequestedLane(request);
		
		ServletContext context = this.getServletContext();
		String deleteRealFiles = context.getInitParameter("delete.files.for.node.deletion");
		
//		String typeTree = request.getParameter("tt");
//		String viewName = Constant.getViewName(typeTree);

		if (lane != null) {
		    if(registration.equals(lane.getOwner()) || registration.isLIMSAdmin() ){
		    	getLaneService().delete(lane, deleteRealFiles);
		//    	if(typeTree.equals("st")){
		//    		Integer sampleId = lane.getSample().getSampleId(); 
		//	    	getLaneService().delete(lane);
		//	    	Sample sample = getSampleService().findByID(sampleId);
		//   		SetNodeIdInSession.setSampleForStudy(sample, request);
		//   	}
		//    	if(typeTree.equals("sr")){
		//   		Integer sequencerRunId = lane.getSequencerRun().getSequencerRunId(); 
		//	    	getLaneService().delete(lane);
	//	    		SetNodeIdInSession.setSequencerRun(sequencerRunId, request);
		    	//	viewName = "redirect:/sequencerRunList.htm";
		//    	}
		    }
		} 
		modelAndView = new ModelAndView(getViewName(request), model);
		return modelAndView;
	}
	
	private Lane getRequestedLane(HttpServletRequest request) {
		Lane	lane	= null;
		String	id		= (String)request.getParameter("objectId");
		
		if (id != null) {
			Integer laneID = Integer.parseInt(id);
			lane = getLaneService().findByID(laneID);
		}
		return lane;
	}
}

// ex:sw=4:ts=4:
