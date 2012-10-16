package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.Constant;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * LaneController
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class FileController extends MultiActionController {
	private FileService fileService;
	private Validator validator;

	/**
	 * <p>Constructor for FileController.</p>
	 */
	public FileController() {
		super();
	}

	/**
	 * <p>Constructor for FileController.</p>
	 *
	 * @param delegate a {@link java.lang.Object} object.
	 */
	public FileController(Object delegate) {
		super(delegate);
	}

	/**
	 * <p>Getter for the field <code>fileService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.FileService} object.
	 */
	public FileService getFileService() {
		return fileService;
	}

	/**
	 * <p>Setter for the field <code>fileService</code>.</p>
	 *
	 * @param fileService a {@link net.sourceforge.seqware.common.business.FileService} object.
	 */
	public void setFileService(FileService fileService) {
		this.fileService = fileService;
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
	 * Handles the user's request to submit a new lane.
	 *
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param command Lane command object
	 * @return ModelAndView
	 * @throws java.lang.Exception if any.
	 */
	public ModelAndView handleSubmit(HttpServletRequest request,
									 HttpServletResponse response,
									 Lane command) throws Exception {
		ModelAndView modelAndView = null;
		return modelAndView;
	}

	/**
	 * Handles the user's request to reset the lane page during a new or
	 * update lane.
	 *
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param command Lane command object
	 * @return ModelAndView
	 * @throws java.lang.Exception if any.
	 */
	public ModelAndView handleReset(HttpServletRequest request,
									HttpServletResponse response,
									Lane command) throws Exception {

		ModelAndView modelAndView = null;
		return modelAndView;
	}

	/**
	 * Handles the user's request to cancel the lane
	 * or the lane update page.
	 *
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param command Lane command object
	 * @return ModelAndView
	 * @throws java.lang.Exception if any.
	 */
	public ModelAndView handleCancel(HttpServletRequest request,
									 HttpServletResponse response,
									 Lane command) throws Exception {
		return new ModelAndView("redirect:/Welcome.htm");
	}

	/**
	 * Handles the user's request to update their lane.
	 *
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @param command Lane command object
	 * @return ModelAndView
	 * @throws java.lang.Exception if any.
	 */
	public ModelAndView handleUpdate(HttpServletRequest request,
									 HttpServletResponse response,
									 Lane command) throws Exception {

	   Registration registration = Security.getRegistration(request);
	    if(registration == null)
	      return new ModelAndView("redirect:/login.htm");
	  
	    HttpSession session = request.getSession(false);
		ModelAndView modelAndView = null;
		
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
	private BindingResult validate(HttpServletRequest request, Object command) {
		BindingResult errors = new BindException(command, getCommandName(command));
		//ValidationUtils.invokeValidator(getValidator(), command, errors);

		return errors;
	}

	/**
	 * Handles the user's request to delete their file.
	 *
	 * @param command file command object
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @return a {@link org.springframework.web.servlet.ModelAndView} object.
	 * @throws java.lang.Exception if any.
	 */
	public ModelAndView handleDelete(HttpServletRequest		request,
									 HttpServletResponse	response,
									 File				command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();
		File				    file			= getRequestedFile(request);
		
		ServletContext context = this.getServletContext();
		String deleteRealFiles = context.getInitParameter("delete.files.for.node.deletion");

		if (file != null) {			
		    if(registration.equals(file.getOwner()) || registration.isLIMSAdmin()){
	    		getFileService().delete(file, deleteRealFiles);
		    }
		} 
		//modelAndView = new ModelAndView("redirect:/myStudyList.htm", model);
		modelAndView = new ModelAndView(getViewName(request), model);
		return modelAndView;
	}
	
	private String getViewName(HttpServletRequest request){
		String typeTree = (String)request.getSession(false).getAttribute("typeTree");
		String viewName = Constant.getViewName(typeTree);
		request.getSession(false).removeAttribute("typeTree");
		return viewName;
	}
	
	private Registration getOwner(File file){
		Registration registration = null;
		return registration;
	}
	
	private File getRequestedFile(HttpServletRequest request) {
		File	file	= null;
		String	id		= (String)request.getParameter("objectId");
		
		if (id != null) {
			Integer fileID = Integer.parseInt(id);
			file = getFileService().findByID(fileID);
		}
		return file;
	}
}

