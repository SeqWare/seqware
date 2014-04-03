package	net.sourceforge.solexatools.webapp.controller;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.util.Bool;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.Constant;
import net.sourceforge.solexatools.util.SetNodeIdInSession;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * SequencerRunController
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ProcessingController extends MultiActionController {
	private SequencerRunService	sequencerRunService;
	private ProcessingService	processingService;
	private Validator			validator;

	/**
	 * <p>Constructor for ProcessingController.</p>
	 */
	public ProcessingController() {
		super();
	}

	/**
	 * <p>Constructor for ProcessingController.</p>
	 *
	 * @param delegate a {@link java.lang.Object} object.
	 */
	public ProcessingController(Object delegate) {
		super(delegate);
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

	/**
	 * <p>Getter for the field <code>validator</code>.</p>
	 *
	 * @return a {@link org.springframework.validation.Validator} object.
	 */
	public Validator getValidator() {
		return validator;
	}

	/**
	 * <p>Setter for the field <code>validator</code>.</p>
	 *
	 * @param validator a {@link org.springframework.validation.Validator} object.
	 */
	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	/**
	 * Handles the user's request to submit a new sequencerRun.
	 *
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param command SequencerRun command object
	 * @return ModelAndView
	 * @throws java.lang.Exception if any.
	 */
	public ModelAndView handleSubmit(HttpServletRequest request,
									 HttpServletResponse response,
									 Processing command) throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView	modelAndView = null;
		HttpSession		session		 = request.getSession(false);
		SequencerRun	sequencerRun = getCurrentSequencerRun(request);
		BindingResult	errors		 = this.validateProcessing(request,command);

		if (errors.hasErrors()) {
			Map model = errors.getModel();
			modelAndView = new ModelAndView("Processing", model);
		} else {
			getProcessingService().insert(sequencerRun, command);
			// Now direct the user to the lane setup page
			modelAndView = new ModelAndView("redirect:/sequencerRunList.htm");
		}

		request.getSession(false).removeAttribute("sequencerRun");
		request.getSession(false).removeAttribute("processing");

		return modelAndView;
	}

	/**
	 * Handles the user's request to reset the sequencerRun page during a new or
	 * update sequencerRun.
	 *
	 * @param command SequencerRun command object
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @return a {@link org.springframework.web.servlet.ModelAndView} object.
	 * @throws java.lang.Exception if any.
	 */
	public ModelAndView handleReset(HttpServletRequest	request,
									HttpServletResponse	response,
									Processing			command)
		throws Exception {

		ModelAndView	modelAndView	= null;
		Processing		processing		= getCurrentProcessing(request);

		if (processing.getUpdateTimestamp() == null) {
			modelAndView = new ModelAndView("Processing");
		} else {
			modelAndView = new ModelAndView("ProcessingUpdate");
		}
		request.setAttribute(getCommandName(command), processing);
		return modelAndView;
	}

	/**
	 * Handles the user's request to cancel the sequencerRun
	 * or the sequencerRun update page.
	 *
	 * @param command SequencerRun command object
	 * @return ModelAndView
	 * @throws java.lang.Exception if any.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 */
	public ModelAndView handleCancel(HttpServletRequest request,
									 HttpServletResponse response,
									 Processing command) throws Exception {
		return new ModelAndView("redirect:/sequencerRunList.htm");
	}

	/**
	 * Handles the user's request to update their sequencerRun.
	 *
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param command SequencerRun command object
	 * @return ModelAndView
	 * @throws java.lang.Exception if any.
	 */
	public ModelAndView handleUpdate(HttpServletRequest request,
									 HttpServletResponse response,
									 Processing command) throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView modelAndView = null;
		BindingResult errors = this.validateProcessing(request, command);
		if (errors.hasErrors()) {
			Map model = errors.getModel();
			modelAndView = new ModelAndView("ProcessingUpdate", model);
		} else {
			Processing newProcessing = command;
			Processing oldProcessing = getCurrentProcessing(request);
			if (newProcessing != null && oldProcessing != null) {
				new ServletRequestDataBinder(oldProcessing).bind(request);
				getProcessingService().update(oldProcessing);
				modelAndView = new ModelAndView("redirect:/sequencerRunList.htm");
			} else {
				modelAndView = new ModelAndView("redirect:/Error.htm");
			}
		}

		request.getSession(false).removeAttribute("processing");

		return modelAndView;
	}
	
	private String getViewName(HttpServletRequest request){
		String typeTree = (String)request.getSession(false).getAttribute("typeTree");
		String viewName = Constant.getViewName(typeTree);
		request.getSession(false).removeAttribute("typeTree");
		return viewName;
	}
	/**
	 * Handles the user's request to delete their processing.
	 *
	 * @param command Processing command object
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @return a {@link org.springframework.web.servlet.ModelAndView} object.
	 * @throws java.lang.Exception if any.
	 */
	public ModelAndView handleDelete(HttpServletRequest		request,
									 HttpServletResponse	response,
									 Processing				command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();
		Processing				processing		= getRequestedProcessing(request);
		
		ServletContext context = this.getServletContext();
		boolean deleteRealFiles = Bool.parse(context.getInitParameter("delete.files.for.node.deletion"));
		
//		String viewName = "redirect:/myStudyList.htm";

//		String typeTree = request.getParameter("tt");
//		if(typeTree != null && typeTree.equals("sr"))
//			viewName = "redirect:/sequencerRunList.htm";
		
//		String viewName = getViewName(request);
		
		if (processing != null) {			
		    if(registration.equals(processing.getOwner()) || registration.isLIMSAdmin()){
		   // 	setNodeIdAfterDelete(processing, request);
		    	getProcessingService().delete(processing, deleteRealFiles);
		    }
		} 
		modelAndView = new ModelAndView(getViewName(request), model);
		return modelAndView;
	}
	
	private void setNodeIdAfterDelete(Processing processing, HttpServletRequest request){
		String typeTree = request.getParameter("tt");
		
		Log.info("TT FOR PROC = " + typeTree);
		
		Set<Lane> lanes = processing.getLanes();
		if(lanes != null && lanes.size() > 0){
			for (Lane lane : lanes) {
				if(typeTree.equals("st")){
					SetNodeIdInSession.setLaneForStudy(lane, request);
				}
				if((typeTree.equals("sr"))){
					SetNodeIdInSession.setLaneForSequrncerRun(lane, request);
				}
				if((typeTree.equals("wfr"))){
				}
			}
		}else{
			Set<Processing> parents = processing.getParents();
			for (Processing parent : parents) {
				if(typeTree.equals("st")){
					SetNodeIdInSession.setProcessingForStudy(parent, request);
				}
				if(typeTree.equals("sr")){
					SetNodeIdInSession.setProcessingForSequrncerRun(parent, request);
				}
				if((typeTree.equals("wfr"))){
				}

			}
		}
	}

	/**
	 * Validates a sequencerRun.
	 *
	 * @param request HttpServletRequest
	 * @param command the Command instance as an Object
	 *
	 * @return BindingResult validation errors
	 *
	 */
	private BindingResult validateProcessing(HttpServletRequest request, Object command) {
		BindingResult errors = new BindException(command, getCommandName(command));
		return errors;
	}

	/**
	 * Gets the emailAddress from the sequencerRun in the session.
	 *
	 * @param request HttpServletRequest
	 *
	 * @return the emailAddress from the sequencerRun in the session, or null if
	 * there is no sequencerRun in the session
	 */
	private String getNameFromSession(HttpServletRequest request) {
		return getCurrentSequencerRun(request).getName();
	}

	/**
	 * Gets the sequencerRun from the session.
	 *
	 * @param request HttpServletRequest
	 *
	 * @return instance of SequencerRun from the session, or a new instance
	 * if the sequencerRun is not in the session (e.g. the user is not logged in)
	 */
	private SequencerRun getCurrentSequencerRun(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object sequencerRun = session.getAttribute("sequencerRun");
			if (sequencerRun != null) {
				return (SequencerRun)sequencerRun;
			}
		}
		return new SequencerRun();
	}

	/**
	 * Gets the sequencerRun from the session.
	 *
	 * @param request HttpServletRequest
	 *
	 * @return instance of SequencerRun from the session, or a new instance
	 * if the sequencerRun is not in the session (e.g. the user is not logged in)
	 */
	private Processing getCurrentProcessing(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object processing = session.getAttribute("processing");
			if (processing != null) {
				return (Processing)processing;
			}
		}
		return new Processing();
	}
	/*
	private Registration getOwner(Processing processing){
		Registration registration = null;
		Lane lane = null;
		Set<Lane> lanes = null;
		Set<Processing> processings = null;
		
		do{
			processings = processing.getParents();
			if(processings != null && !processings.isEmpty()){
				processing = processings.iterator().next();
			}
		}while(processings != null && !processings.isEmpty());
			
			
		lanes = processing.getLanes();  
		if(lanes != null && !lanes.isEmpty()){
			lane = lanes.iterator().next();
			registration = lane.getOwner();
		}
		
		return registration;
	}
	*/
	private Processing getRequestedProcessing(HttpServletRequest request) {
		Processing	processing	= null;
		String		id		= (String)request.getParameter("objectId");
		
		if (id != null) {
			Integer processingID = Integer.parseInt(id);
			processing = getProcessingService().findByID(processingID);
		}

		return processing;
	}
}
