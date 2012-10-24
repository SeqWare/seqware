package net.sourceforge.solexatools.webapp.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.FileTypeService;
import net.sourceforge.seqware.common.business.FileUploadService;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.business.UploadFile;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.FileType;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
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
 * <p>UploadFileController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class UploadFileController extends MultiActionController{
	  private StudyService studyService;
	  private ExperimentService experimentService;
	  private SampleService sampleService;
	  private LaneService laneService;
	  private ProcessingService processingService;
	  private FileService fileService;
	  private SequencerRunService sequencerRunService;
	  private IUSService iusService;
	  private FileTypeService fileTypeService;
	  private FileUploadService fileUploadService;
	  private Validator validator;

	  /**
	   * <p>Constructor for UploadFileController.</p>
	   */
	  public UploadFileController() {
		super();
	  }

	  /**
	   * <p>Constructor for UploadFileController.</p>
	   *
	   * @param delegate a {@link java.lang.Object} object.
	   */
	  public UploadFileController(Object delegate) {
		super(delegate);
	  }

	  /**
	   * <p>Getter for the field <code>studyService</code>.</p>
	   *
	   * @return a {@link net.sourceforge.seqware.common.business.StudyService} object.
	   */
	  public StudyService getStudyService() {
		return studyService;
	  }

	  /**
	   * <p>Setter for the field <code>studyService</code>.</p>
	   *
	   * @param studyService a {@link net.sourceforge.seqware.common.business.StudyService} object.
	   */
	  public void setStudyService(StudyService studyService) {
		this.studyService = studyService;
	  }

	  /**
	   * <p>Getter for the field <code>experimentService</code>.</p>
	   *
	   * @return a {@link net.sourceforge.seqware.common.business.ExperimentService} object.
	   */
	  public ExperimentService getExperimentService() {
		return experimentService;
  	  }

	  /**
	   * <p>Setter for the field <code>experimentService</code>.</p>
	   *
	   * @param experimentService a {@link net.sourceforge.seqware.common.business.ExperimentService} object.
	   */
	  public void setExperimentService(ExperimentService experimentService) {
		this.experimentService = experimentService;
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
			  
	  /**
	   * <p>getIUSService.</p>
	   *
	   * @return a {@link net.sourceforge.seqware.common.business.IUSService} object.
	   */
	  public IUSService getIUSService() {
		return iusService;
	  }

	  /**
	   * <p>setIUSService.</p>
	   *
	   * @param iusService a {@link net.sourceforge.seqware.common.business.IUSService} object.
	   */
	  public void setIUSService(IUSService iusService) {
			this.iusService = iusService;
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
	   * <p>Getter for the field <code>fileUploadService</code>.</p>
	   *
	   * @return a {@link net.sourceforge.seqware.common.business.FileUploadService} object.
	   */
	  public FileUploadService getFileUploadService() {
		return fileUploadService;
	  }

	  /**
	   * <p>Setter for the field <code>fileUploadService</code>.</p>
	   *
	   * @param fileUploadService a {@link net.sourceforge.seqware.common.business.FileUploadService} object.
	   */
	  public void setFileUploadService(FileUploadService fileUploadService) {
		this.fileUploadService = fileUploadService;
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
	      UploadFile			command)
	  throws Exception {

	    Registration registration = Security.getRegistration(request);
	    if(registration == null)
	      return new ModelAndView("redirect:/login.htm");
	    
	    ServletContext context = this.getServletContext();
	    command.setFolderStore(context.getInitParameter("path.to.upload.directory"));
	    command.setStrStartURL(context.getInitParameter("true.protocols"));

	    ModelAndView	modelAndView = null;
	    BindingResult	errors = this.validate(request, command);
	    if (errors.hasErrors()) {
	    	Map model = errors.getModel();
	    	Integer id = getRequestedId(request);
			String typeNode = getRequestedTypeNode(request);
			String nameNode = getNameNode(id, typeNode);
			List<FileType> listFileType = getFileTypeService().list();
			
			model.put("id",id);
			model.put("tn",typeNode);
			model.put("nameNode",nameNode);
			model.put("listFileType", listFileType);
			
			modelAndView = new ModelAndView("UploadFile", model);
	    } else {
	    	String folderStore = context.getInitParameter("path.to.upload.directory");
	    	
	    //	MultipartFile file = command.getFile();
	    	
	     //	MultipartFile file = command.getFileFromURL();
	    	
	    	Log.info("URL = " + command.getFileURL());
	    	Log.info("USE URL ? = " + command.getUseURL());
	    	
	    	FileType fileType = getFileTypeService().findByID(command.getFileTypeId());
	    	
			Integer id = getRequestedId(request);
			String typeNode = getRequestedTypeNode(request);
			
			Log.info("ID = " + id);
			Log.info("typeNode = " + typeNode);
	    	
	    	if("st".equals(typeNode)){
	    		Study study = getStudyService().findByID(id);
	    		getFileUploadService().uploadFile(study, command, fileType, registration);
	    	}
	    	
	    	if("exp".equals(typeNode)){
	    		Experiment experiment = getExperimentService().findByID(id);
	    		getFileUploadService().uploadFile(experiment, command, fileType, registration);
	    	}
	    	if("sam".equals(typeNode)){
	    		Sample sample = getSampleService().findByID(id);
	    		getFileUploadService().uploadFile(sample, command, fileType, registration);
	    	}
	    	if("seq".equals(typeNode)){
	    		Lane lane = getLaneService().findByID(id);
	    		getFileUploadService().uploadFile(lane, command, fileType, registration);
	    	}
	    	if("ius".equals(typeNode)){
	    		IUS ius = getIUSService().findByID(id);
	    		getFileUploadService().uploadFile(ius, command, fileType, registration);
	    	}
	    	if("ae".equals(typeNode)){
	    		Processing processing = getProcessingService().findByID(id);
	    		getFileUploadService().uploadFile(processing, command, fileType, registration);	
	    	}
	    	if("sr".equals(typeNode)){
	    		SequencerRun sequencerRun = getSequencerRunService().findByID(id);
	    		getFileUploadService().uploadFile(sequencerRun, command, fileType, registration);	
	    	}
	    	modelAndView = new ModelAndView(getViewName(request));
	    }

	    return modelAndView;
	  }
	  
	  private String getNameNode(Integer id, String typeNode){
		String name = "";
			
		if("st".equals(typeNode)){
			Log.info("find Study");
			name = getStudyService().findByID(id).getTitle();
		}
		if("exp".equals(typeNode)){
			Log.info("find Experiment");
			name = getExperimentService().findByID(id).getTitle();
		}
		if("sam".equals(typeNode)){
			Log.info("find Sample");
			name = getSampleService().findByID(id).getTitle();
		}
		if("seq".equals(typeNode)){
			Log.info("find Lane");
			name = getLaneService().findByID(id).getName();
		}
		if("ius".equals(typeNode)){
			Log.info("find IUS");
			name = getIUSService().findByID(id).getName();
		}
		if("ae".equals(typeNode)){
			Log.info("find Study");
			name = "SWID " +  getProcessingService().findByID(id).getSwAccession().toString();
		}
		if("sr".equals(typeNode)){
			Log.info("find Seq Run");
			name = getSequencerRunService().findByID(id).getName();
		}
		
		return name;
	}
	  
	private Integer getRequestedId(HttpServletRequest request){
		Integer id = null;
		String strId =  request.getParameter("id");
		if(strId != null && !strId.equals("")){
			id = Integer.parseInt(strId);
		}
		return id;
	}
	
	private String getRequestedTypeNode(HttpServletRequest request){
		return request.getParameter("tn");
	}
	  
	private String getViewName(HttpServletRequest request){
		String typeTree = (String)request.getSession(false).getAttribute("typeTree");
		String viewName = Constant.getViewName(typeTree);
		request.getSession(false).removeAttribute("typeTree");
		return viewName;
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
		  
	//	  request.getSession(false).removeAttribute("uploadSample");
		
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
