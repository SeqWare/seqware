package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.FileTypeService;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.business.UploadFile;
import net.sourceforge.seqware.common.model.FileType;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>UploadFileSetupController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class UploadFileSetupController extends BaseCommandController{
	private StudyService studyService;
	private ExperimentService experimentService;
	private SampleService sampleService;
	private LaneService laneService;
	private ProcessingService processingService;
	private SequencerRunService sequencerRunService;
	private IUSService iusService;
	private FileTypeService fileTypeService;
	
	/**
	 * <p>Constructor for UploadFileSetupController.</p>
	 */
	public UploadFileSetupController() {
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
		Map<String,Object>		model			= new HashMap<String,Object>();
		
		Integer id = getRequestedId(request);
		String typeNode = getRequestedTypeNode(request);
		String nameNode = getNameNode(id, typeNode);
		String typeTree = request.getParameter("tt");
		request.getSession(false).setAttribute("typeTree", typeTree);
		
		List<FileType> listFileType = getFileTypeService().list();
		
		Log.info("ID = " + id);
		Log.info("typeNode = " + typeNode);
		Log.info("nameNode = " + nameNode);
		Log.info("typeTree = " + typeTree);	
		
		UploadFile uploadFile = new UploadFile();
		
		if(listFileType.size() == 0){
			BindingResult errors = new BindException(uploadFile, getCommandName());
			errors.reject("error.upload.file.file.type.empty");
			model = errors.getModel();
		}else{
			request.setAttribute(getCommandName(), uploadFile);
		}
		
		model.put("id",id);
		model.put("tn",typeNode);
		model.put("nameNode",nameNode);		
		model.put("listFileType", listFileType);
					
		modelAndView = new ModelAndView("UploadFile", model);
		
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
			name = "SWID: " + getProcessingService().findByID(id).getSwAccession().toString();
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
}
