package net.sourceforge.solexatools.webapp.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.FileTypeService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.model.FileType;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.UploadSequence;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.Constant;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * <p>UploadSequenceController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class UploadSequenceController extends MultiActionController{
	  private SampleService sampleService;
	  private LaneService laneService;
	  private ProcessingService processingService;
	  private FileService fileService;
	  private FileTypeService fileTypeService;
	  private Validator validator;

	  /**
	   * <p>Constructor for UploadSequenceController.</p>
	   */
	  public UploadSequenceController() {
	    super();
	  }

	  /**
	   * <p>Constructor for UploadSequenceController.</p>
	   *
	   * @param delegate a {@link java.lang.Object} object.
	   */
	  public UploadSequenceController(Object delegate) {
	    super(delegate);
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
	   * <p>Getter for the field <code>sampleService</code>.</p>
	   *
	   * @return a {@link net.sourceforge.seqware.common.business.SampleService} object.
	   */
	  public SampleService getSampleService() {
	    return sampleService;
	  }

	  /**
	   * <p>Setter for the field <code>sampleService</code>.</p>
	   *
	   * @param sampleService a {@link net.sourceforge.seqware.common.business.SampleService} object.
	   */
	  public void setSampleService(SampleService sampleService) {
	    this.sampleService = sampleService;
	  }
	  
	  /**
	   * <p>Getter for the field <code>laneService</code>.</p>
	   *
	   * @return a {@link net.sourceforge.seqware.common.business.LaneService} object.
	   */
	  public LaneService getLaneService() {
		return laneService;
	  }

	  /**
	   * <p>Setter for the field <code>laneService</code>.</p>
	   *
	   * @param laneService a {@link net.sourceforge.seqware.common.business.LaneService} object.
	   */
	  public void setLaneService(LaneService laneService) {
		 this.laneService = laneService;
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
	   * <p>Getter for the field <code>fileTypeService</code>.</p>
	   *
	   * @return a {@link net.sourceforge.seqware.common.business.FileTypeService} object.
	   */
	  public FileTypeService getFileTypeService() {
		return fileTypeService;
	  }

	  /**
	   * <p>Setter for the field <code>fileTypeService</code>.</p>
	   *
	   * @param fileTypeService a {@link net.sourceforge.seqware.common.business.FileTypeService} object.
	   */
	  public void setFileTypeService(FileTypeService fileTypeService) {
		this.fileTypeService = fileTypeService;
	  }

	  /**
	   * Handles the user's request to submit a new study.
	   *
	   * @param request HttpServletRequest
	   * @param response HttpServletResponse
	   * @param command Study command object
	   * @return ModelAndView
	   * @throws java.lang.Exception if any.
	   */
	  public ModelAndView handleSubmit(HttpServletRequest		request,
	      HttpServletResponse	response,
	      UploadSequence		command)
	  throws Exception {

	    Registration registration = Security.getRegistration(request);
	    if(registration == null)
	      return new ModelAndView("redirect:/login.htm");
	    
	    ServletContext context = this.getServletContext();
	    command.setFolderStore(context.getInitParameter("path.to.upload.directory"));
	    command.setStrStartURL(context.getInitParameter("true.protocols"));
	    command.setSample(getRequestedCurrentSample(request, command));

	    ModelAndView	modelAndView = null;
	    BindingResult	errors = this.validate(request, command);
	    if (errors.hasErrors()) {
	      Map model = errors.getModel();
	      if(getCurrentSample(request) == null){
	    	List<Sample> listSample = getSampleService().listSample(registration);
	    	model.put("sampleList", listSample);
	    	model.put("strategy", "any_node");
	      }else{
	    	model.put("strategy", "defined_node");
			model.put("sample", getCurrentSample(request));
	      }
	      List<FileType> listFileType = getFileTypeService().list();
	      model.put("listFileType", listFileType);
	      
	      modelAndView = new ModelAndView("UploadSequence", model);
	    } else {
	    	
	   // 	String folderStore = context.getInitParameter("path.to.upload.directory");
	    	
	   // 	MultipartFile fileOne = command.getFileOne();
	   // 	MultipartFile fileTwo = command.getFileTwo();
	    	
	    	FileType fileType = getFileTypeService().findByID(command.getFileTypeId());
	    	    	
	    	// get current Sample by id
	    	Sample sample = getRequestedCurrentSample(request, command);
   	
	    	// get current Lane
	//    	Lane currentLane = getLane(sample);
	    	
	    	Integer newProcessingId;
	    	// is lane exist
	//    	if(currentLane == null){
	//    		Log.info("Create new Lane");
	    		newProcessingId = getLaneService().insertLane(registration, sample, command, fileType);
	//    	}
	//    	else{   
	//    		Log.info("Update Lane");
	//    		newProcessingId = getLaneService().updateLane(sample, fileOne, fileTwo, folderStore, registration);
	//    	}
	    	
	      // add lane id
	    //request.getSession(false).setAttribute("nodeId", "ae_" + newProcessingId);
	      Log.info("Proc ID =" + newProcessingId);
	      
	 //	  String typeTree = (String)request.getSession(false).getAttribute("typeTree");
		 //request.getSession(false).setAttribute("typeTree", typeTree);
	//	  Log.info("TYPE TREE UPLOAD = " + typeTree);
		  
	//	  if(typeTree!=null && typeTree.equals("st")){
	//	 	  Processing proc = getProcessingService().findByID(newProcessingId);
	//	      SetNodeIdInSession.setProcessingForStudy(proc, request);  
	//	  }    		      	
		  
		  request.getSession(false).removeAttribute("uploadSample");
	      modelAndView = new ModelAndView(getViewName(request));
	    }

//	    request.getSession(false).removeAttribute("sample");

	    return modelAndView;
	  }
	  
	  private String getViewName(HttpServletRequest request){
			String typeTree = (String)request.getSession(false).getAttribute("typeTree");
			String viewName = Constant.getViewName(typeTree);
			request.getSession(false).removeAttribute("typeTree");
			return viewName;
	  }
	  
	  private Sample getRequestedCurrentSample(HttpServletRequest request, UploadSequence command) {
			Sample	sample	= null;
			Integer	id	= command.getSampleId();
			if(id==null){
				String strCurrentSampleId = request.getParameter("currentSampleId");
				if(strCurrentSampleId != null){
					id = Integer.parseInt(strCurrentSampleId);
				}
			}
			sample = getSampleService().findByID(id);
			return sample;
	  }
	  
	  private Sample getCurrentSample(HttpServletRequest request) {
		  HttpSession session = request.getSession(false);
		  if (session != null) {
			  Object sample = session.getAttribute("uploadSample");
			  if (sample != null) {
				  return (Sample)sample;
			  }
		  }
		  return null;
	  }

	  private Lane getLane(Sample sample){
		  Lane lane = null;
		  Set<Lane> lanes = sample.getLanes();
		  
		  if(lanes != null && !lanes.isEmpty())
			  lane = lanes.iterator().next();
		  return lane;
	  }

	  /**
	   * Handles the user's request to cancel the study
	   * or the study update page.
	   *
	   * @param command Study command object
	   * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	   * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	   * @return a {@link org.springframework.web.servlet.ModelAndView} object.
	   * @throws java.lang.Exception if any.
	   */
	  public ModelAndView handleCancel(HttpServletRequest request,
	      HttpServletResponse response,
	      Study command) throws Exception {

		// add sample id
		//String sampleId = request.getParameter("currentSampleId");
		//if(sampleId == null)
		//	sampleId = request.getParameter("sampleId");
			
		//Integer sampleId = Integer.parseInt(request.getParameter("currentSampleId"));
		
		//request.getSession(false).setAttribute("nodeId", "sam_" + Integer.parseInt(sampleId));
		  
		  request.getSession(false).removeAttribute("uploadSample");
		
	    return new ModelAndView(getViewName(request));
	  }

	  /**
	   * Validates a study.
	   *
	   * @param command the Command instance as an Object
	   *
	   * @return BindingResult validation errors
	   */
	  private BindingResult validate(HttpServletRequest request, Object command) {
	    BindingResult errors = new BindException(command, getCommandName(command));
	    ValidationUtils.invokeValidator(getValidator(), command, errors);
	    return errors;
	  }

}
