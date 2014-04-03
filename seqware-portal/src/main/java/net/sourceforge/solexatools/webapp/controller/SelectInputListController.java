package net.sourceforge.solexatools.webapp.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.BulkUtil;
import net.sourceforge.solexatools.util.Constant;
import net.sourceforge.solexatools.util.FindChildUtil;
import net.sourceforge.solexatools.util.LaunchWorkflowUtil;
import net.sourceforge.solexatools.util.PageInfo;
import net.sourceforge.solexatools.util.PaginationUtil;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>SelectInputListController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SelectInputListController extends BaseCommandController {
	private StudyService studyService;
	private ExperimentService experimentService;
	private SampleService sampleService;
	private LaneService laneService;
	private IUSService iusService;
	private ProcessingService processingService;
	private WorkflowRunService workflowRunService;
	private FileService fileService;
	
	
	/**
	 * <p>Constructor for SelectInputListController.</p>
	 */
	public SelectInputListController() {
		super();
	//	setSupportedMethods(new String[] {METHOD_GET});
		setSupportedMethods(new String[] {METHOD_POST});
	}
	
	private String getRequestedOption(HttpServletRequest request) {
		return request.getParameter("option");
	}
	private String getRequestedTypeNode(HttpServletRequest request) {
		return request.getParameter("typeNode");
	}
	private String[] getRequestedAllSelectIds(HttpServletRequest request) {
		return request.getParameter("allSelectIds").split(",");
	}
	private String[] getRequestedAllSelectStatuses(HttpServletRequest request) {
		return request.getParameter("allSelectStatuses").split(",");
	}
	private boolean getRequestedIsSelect(HttpServletRequest request) {
		return Boolean.parseBoolean(request.getParameter("isSelect"));
	}
	private String getRequestedNodeId(HttpServletRequest request) {
		return request.getParameter("nodeId");
	}
	private String getRequestedChildId(HttpServletRequest request) {
		return request.getParameter("childId");
	}
	private String getNameFileListInSession(){
		return "selectedLaunchFiles";
	}
	private String getNameInputListInSession(){
		return "selectedInputs";
	}
	private String getNameSelectedIdsInSession(){
		return "launchSelectedNodes";
	}
	
	private Map<String, List<File>> getAllListFile(HttpServletRequest request){
		return LaunchWorkflowUtil.getAllSelectedFiles(request);
	}
	
	private List<File> getCurrentListFile(HttpServletRequest request){
	//	List<File> files = (List<File>)request.getSession(false).getAttribute(getNameFileListInSession());
	//	if(files == null){
	//		files = new LinkedList<File>();
	//	}
	//	return files;
		List<File> files = LaunchWorkflowUtil.getCurrentSelectedFiles(request); 
		if(files == null){
			files = new LinkedList<File>();
		}
		return files;
	}
	
	private List<Lane> getCurrentListInput(HttpServletRequest request){
		List<Lane> lanes = (List<Lane>)request.getSession(false).getAttribute(getNameInputListInSession());
		if(lanes == null){
			lanes = new LinkedList<Lane>();
		}
		return lanes;
	}
	
	private void setFiles(HttpServletRequest request, String nameList, List<File> list){
	//	HttpSession	session	= request.getSession(false);
	//	session.setAttribute(nameList, list);
		LaunchWorkflowUtil.setCurrentSelectedFiles(request, list);
	}
	
	private void setInputs(HttpServletRequest request, String nameList, List<Lane> list){
	//	HttpSession	session	= request.getSession(false);
	//	session.setAttribute(nameList, list);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
  @Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
												 HttpServletResponse response)
		throws Exception {

		//Registration registration = Security.requireRegistration(request, response);
		Registration registration = Security.getRegistration(request);
		if(registration == null){
		//	return new ModelAndView("redirect:/login.htm");
			return new ModelAndView("FileList");
		}
		
		/**
		 * Pass registration so that we can filter the list if its appropriate
		 * to do so.
		 */
		String metaType = getCurrentWorkflowParam(request).getFileMetaType();

		String option = getRequestedOption(request);
		String typeNode = getRequestedTypeNode(request);
		boolean isSelect = getRequestedIsSelect(request);
//		String typeBulkTree = getRequestedTypeBulkTree(request);
		
//		String nameInputListInSession = "selectedInputs"; //getNameFileListInSession(typeBulkTree, option); // "bulkDownloadFiles"
//		String nameFileListInSession = "selectedLaunchFiles";
		//List<File>	list = BulkUtil.getFiles(request, nameFileListInSession);
		
		
//		List<Lane> inputList = getCurrentListInput(request);
		
		List<File> files = new ArrayList<File>();
		if(option.equals("updateFileList")){
			// update file list
			Integer nodeId = Integer.parseInt(getRequestedNodeId(request));
			List<String> nodeIds = new LinkedList<String>();
			
			if(typeNode.equals("study")){
				files = getStudyService().getFiles(nodeId, metaType);
//				lanes = getStudyService().listLaneWithHasFile(nodeId, metaType);
				nodeIds = FindChildUtil.getNodeIds(getStudyService().findByID(nodeId));
			}else
			if(typeNode.equals("exp")){
				files = getExperimentService().getFiles(nodeId, metaType);
//				lanes = getExperimentService().listLaneWithHasFile(nodeId, metaType);
				nodeIds = FindChildUtil.getNodeIds(getExperimentService().findByID(nodeId));
			}else
			if(typeNode.equals("sam")){
				files = getSampleService().getFiles(nodeId, metaType);
//				lanes = getSampleService().listLaneWithHasFile(nodeId, metaType);
				nodeIds = FindChildUtil.getNodeIds(getSampleService().findByID(nodeId));
			}else
/*			if(typeNode.equals("lane")){
				files = getLaneService().getFiles(nodeId, metaType);
//				lanes.add(getLaneService().findByID(nodeId));
				nodeIds.add(nodeId.toString());
			}else	*/			
			if(typeNode.equals("ius")){
				files = getIUSService().getFiles(nodeId, metaType);
//				lanes.add(getLaneService().findByID(nodeId));
				nodeIds.add(nodeId.toString());
			}else
			if(typeNode.equals("ae")){
				files = getProcessingService().getFiles(nodeId, metaType);
				nodeIds = FindChildUtil.getNodeIds(getProcessingService().findByID(nodeId));
			}else
			if(typeNode.equals("file")){
				files = getFileService().getFiles(nodeId, metaType);
				nodeIds.add(nodeId.toString());
			}if(typeNode.equals("wfr")){
			//	Integer processingId = getWorkflowRunService().getRootProcessing(nodeId).getProcessingId();
				WorkflowRun wr = getWorkflowRunService().findByID(nodeId);
				String strChildId = getRequestedChildId(request);
				if(strChildId != null && !"".equals(strChildId)){
					Integer processingId = Constant.getId(strChildId);
					files = getProcessingService().getFiles(processingId);
					
					// get Processing
					SortedSet<Processing> processings = new TreeSet<Processing>();
					processings.add(getProcessingService().findByID(processingId));
					
					// set one Processing in Workflow Run 
					wr.setProcessings(processings);
				}else{
					files = getWorkflowRunService().getFiles(wr.getWorkflowRunId());
				}
				
				nodeIds = FindChildUtil.getNodeIds(wr);
			}

			List<File> fileList = getCurrentListFile(request);
			
			fileList = updateFiles(isSelect, files, fileList);
			
//			inputList = updateLanes(isSelect, lanes, inputList);

			setFiles(request, getNameFileListInSession(), fileList);
//			setInputs(request, getNameInputListInSession(), inputList);
			
			// update Selected nodes in tree view 
	//		String[] allSelectIds = getRequestedAllSelectIds(request);
	//		String[] allSelectStatuses = getRequestedAllSelectStatuses(request);
			String nameSelectedIdsInSession = getNameSelectedIdsInSession(); //getNameSelectedIdsInSession(typeBulkTree);
	//		BulkUtil.getSelectedIds(request, nameSelectedIdsInSession, allSelectIds, allSelectStatuses);
			BulkUtil.updateSelectedIds(request, nameSelectedIdsInSession, isSelect, nodeIds);

		}
		
		MessageSourceAccessor ma = this.getMessageSourceAccessor();
		/**
		 * 
		 */
		Map<String, List<File>> listAllFile = LaunchWorkflowUtil.getAllSelectedFiles(request);
		
		// flatten out the list since we don't need to track the param name for these files here
/*		List<File> compositeList = new LinkedList<File>();
		for(String paramName : listAllFile.keySet()) {
		  List<File> currFileList = listAllFile.get(paramName);
		  if (currFileList != null) {
		    compositeList.addAll(currFileList);
		  }
		}
*/		
		List<File> compositeList = getCurrentListFile(request);
		
		List<File> listViewFile = PaginationUtil.subListSD(request, "selectInputPage", compositeList);
		PageInfo pageInfo = PaginationUtil.getPageInfoSD(request, "selectInputPage", listViewFile, compositeList, ma);
		/**
		 * 
		 */
		/*
		try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		 */
/*		
		Log.info("Launch Files:");
		for(File file: listViewFile){
			Log.info(file.getFileName());
		}
*/		
		
		
		ModelAndView modelAndView = new ModelAndView("FileList");
		modelAndView.addObject("isSelectedInput", true);
		modelAndView.addObject("files", listViewFile);
		modelAndView.addObject("pageInfo", pageInfo);
		modelAndView.addObject("registration", registration);

		return modelAndView;
	}
	
	private synchronized List<Lane> updateLanes(boolean isSelect, List<Lane> lanes, List<Lane> list){
		if(isSelect){
			list = addLanes(list, lanes);
		}else{
			list = removeLanes(list, lanes);
		}
		return list;
	}
	
	private List<Lane> addLanes(List<Lane> list, List<Lane> addList){
		for(Lane addFile: addList){
			boolean isAdd = true;
			for (Lane file: list) {
				if(file.equals(addFile)){
					isAdd = false;
					break;
				}
			}
			if(isAdd){
				list.add(addFile);
			}
		}
		return list;
	}
	
	private List<Lane> removeLanes(List<Lane> list, List<Lane> removeList){
		for (Lane removefile : removeList) {
			list.remove(removefile);
		}
		return list;
	}
	
	private synchronized List<File> updateFiles(boolean isSelect, List<File> files, List<File> list){
		if(isSelect){
			list = addFiles(list, files);
		}else{
			list = removeFiles(list, files);
		}
		return list;
	}
	
	private List<File> addFiles(List<File> list, List<File> addList){
	//	list.addAll(addList);
		for(File addFile: addList){
		//	if(!list.contains(addFile)){
		//		list.add(addFile);
		//	}
			boolean isAdd = true;
			for (File file: list) {
				if(file.equals(addFile)){
					isAdd = false;
					break;
				}
			}
			if(isAdd){
				list.add(addFile);
			}
		}
	
		return list;
	}
	
	private List<File> removeFiles(List<File> list, List<File> removeList){
	//	list.removeAll(removeList);
		for (File removefile : removeList) {
			list.remove(removefile);
		/*	boolean isRemove = false;
			for(File file: list){
				if(file.equals(removefile)){
					isRemove = true;
				}
			}
			if(isRemove){
				list.remove(removefile);
			}
		*/
		}
		return list;
	}
	
	private WorkflowParam getCurrentWorkflowParam(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object workflowParam = session.getAttribute("workflowParam");
			if (workflowParam != null) {
				return (WorkflowParam)workflowParam;
			}
		}
		return new WorkflowParam();
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
	 * <p>Getter for the field <code>workflowRunService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.WorkflowRunService} object.
	 */
	public WorkflowRunService getWorkflowRunService() {
		return workflowRunService;
	}

	/**
	 * <p>Setter for the field <code>workflowRunService</code>.</p>
	 *
	 * @param workflowRunService a {@link net.sourceforge.seqware.common.business.WorkflowRunService} object.
	 */
	public void setWorkflowRunService(WorkflowRunService workflowRunService) {
		this.workflowRunService = workflowRunService;
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
}
