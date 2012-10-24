package net.sourceforge.solexatools.webapp.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>SaveOpenNodeController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SaveOpenNodeController extends BaseCommandController {
	private ExperimentService experimentService;
	private SampleService sampleService;
	private LaneService laneService;
	private ProcessingService processingService;
	private FileService fileService;
	
	/**
	 * <p>Constructor for SaveOpenNodeController.</p>
	 */
	public SaveOpenNodeController() {
		super();
		setSupportedMethods(new String[] {METHOD_POST});
	}
	
	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest	 request,
												 HttpServletResponse response)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");
		
		String typeTree = request.getParameter("tt");
		String listNodeId = request.getParameter("listNodeId");
		
		List<String> ids = Arrays.asList(listNodeId.split(","));
		
		Log.info("AJAX tt = " + typeTree);
		Log.info("AJAX listNodeId = " + listNodeId);
		
		String rootId = getEndId(ids);
		String openNodeId = getFirstId(ids);
		
		Log.info("AJAX ROOT ID = " + rootId);
		Log.info("AJAX openNodeId = " + openNodeId);
	
	/*	if(typeTree == null || typeTree.equals("")){
			if(rootId.indexOf("study_") != -1){
				typeTree = "st";
			}
			if(rootId.indexOf("wfr_") != -1){
				typeTree = "wfr";
			}
			if(rootId.indexOf("sr_") != -1){
				typeTree = "sr";
			}
		}
	*/			
		//if(typeTree.equals("wfrr")){
		
		request.getSession(false).setAttribute("typeTree", typeTree);
		
		if(typeTree!=null && openNodeId != null){
			// if Study Tree
			if(typeTree.equals("st")){
				if(openNodeId.indexOf("study_") != -1){
					Log.info("Remove STUDY");
					request.getSession(false).removeAttribute("listStudyNodeId");
				}else{
					Log.info("Add List Node for Study");
					request.getSession(false).setAttribute("listStudyNodeId", ids);
				}
			}
			// if Workflow Run Tree
			if(typeTree.equals("wfr")){
				if(openNodeId.indexOf("wfr_") != -1 && ids.size() == 1){
					Log.info("Remove Workflow Run");
					request.getSession(false).removeAttribute("listWorkflowRunNodeId");
				}else{
					Log.info("Add List Node for Workflwo Run");
					request.getSession(false).setAttribute("listWorkflowRunNodeId", ids);
				}
			}
			// if Workflow Run Running Tree
			if(typeTree.equals("wfrr")){
				if(openNodeId.indexOf("wfr_") != -1 && ids.size() == 1){
					Log.info("Remove Workflow Run Running");
					request.getSession(false).removeAttribute("listWorkflowRunRunningNodeId");
				}else{
					Log.info("Add List Node for Workflow Run Running");
					request.getSession(false).setAttribute("listWorkflowRunRunningNodeId", ids);
				}
			}
			
			// if Sequencer Run
			if(typeTree.equals("sr")){
				if(openNodeId.indexOf("sr_") != -1 && ids.size() == 1){
					Log.info("Remove Sequencer Run");
					request.getSession(false).removeAttribute("listSequencerRunNodeId");
				}else{
					Log.info("Add List Node for Sequencer Run");
					request.getSession(false).setAttribute("listSequencerRunNodeId", ids);
				}
			}
		}

		return null;
	}
	
	private String getFirstId(List<String> ids){
		return ids.get(0);
	}
	
	private String getEndId(List<String> ids){
		return ids.get(ids.size()-1);
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
}
