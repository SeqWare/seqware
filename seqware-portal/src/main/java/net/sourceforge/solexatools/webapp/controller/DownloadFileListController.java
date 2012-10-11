package net.sourceforge.solexatools.webapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.plaf.metal.MetalFileChooserUI;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.BulkUtil;
import net.sourceforge.solexatools.util.Constant;
import net.sourceforge.solexatools.util.FindChildUtil;
import net.sourceforge.solexatools.util.PageInfo;
import net.sourceforge.solexatools.util.PaginationUtil;
//import net.sourceforge.solexatools.webapp.metamodel.FileMeta;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

public class DownloadFileListController extends BaseCommandController {
	private StudyService studyService;
	private ExperimentService experimentService;
	private SampleService sampleService;
	private LaneService laneService;
	private ProcessingService processingService;
	private FileService fileService;
	private WorkflowRunService workflowRunService;
		
 
	public DownloadFileListController() {
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
	private String getRequestedTypeBulkTree(HttpServletRequest request) {
		return request.getParameter("typeBulkTree");
	}
	private String getRequestedChildId(HttpServletRequest request) {
		return request.getParameter("childId");
	}
	private String getNameFileListInSession(String typeNode, String option){
		String nameListInSession = "";
		if(option.equals("getCurrentFileList")){
			nameListInSession = "unknow";
		}
		if(option.equals("updateFileList")){
			if(typeNode.equals("study")){
				nameListInSession = "bulkDownloadFiles";
			}
			if(typeNode.equals("analisys")){
				nameListInSession = "analysisBulkDownloadFiles";
			}
		}
		
		return nameListInSession;
	}
	private String getNameSelectedIdsInSession(String typeNode){
		String nameListInSession = "";
		if(typeNode.equals("study")){
			nameListInSession = "selectedNodes";
		}
		if(typeNode.equals("analisys")){
			nameListInSession = "analysisSelectedNodes";
		}
		
		return nameListInSession;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
												 HttpServletResponse response)
		throws Exception {

		//Registration registration = Security.requireRegistration(request, response);
		Registration registration = Security.getRegistration(request);
		if(registration == null){
			//return new ModelAndView("redirect:/login.htm");
			return new ModelAndView("FileList");
		}
		
		/**
		 * Pass registration so that we can filter the list if its appropriate
		 * to do so.
		 */

		String option = getRequestedOption(request);
		String typeNode = getRequestedTypeNode(request);
		boolean isSelect = getRequestedIsSelect(request);
		String typeBulkTree = getRequestedTypeBulkTree(request);
		
		String nameFileListInSession = getNameFileListInSession(typeBulkTree, option);
		
		List<File>	listAllFile = BulkUtil.getFiles(request, nameFileListInSession);
		
		List<File> files = new ArrayList<File>();
		if(option.equals("updateFileList")){
			// update file list
			Integer nodeId = Integer.parseInt(getRequestedNodeId(request));
			List<String> nodeIds = new LinkedList<String>();
			
			if(typeNode.equals("study")){
				files = getStudyService().getFiles(nodeId);
				nodeIds = FindChildUtil.getNodeIds(getStudyService().findByID(nodeId));
			}else
			if(typeNode.equals("exp")){
				files = getExperimentService().getFiles(nodeId);
				nodeIds = FindChildUtil.getNodeIds(getExperimentService().findByID(nodeId));
			}else
			if(typeNode.equals("sam")){
				files = getSampleService().getFiles(nodeId);
			//	nodeIds = FindChildUtil.getNodeIds(getStudyService().findByID(nodeId));
				nodeIds = FindChildUtil.getNodeIds(getSampleService().findByID(nodeId));
			}else
			if(typeNode.equals("lane")){
				files = getLaneService().getFiles(nodeId);
				nodeIds = FindChildUtil.getNodeIds(getLaneService().findByID(nodeId));
			//	nodeIds.add(nodeId.toString());
			}else
			if(typeNode.equals("ae")){
				files = getProcessingService().getFiles(nodeId);
				nodeIds = FindChildUtil.getNodeIds(getProcessingService().findByID(nodeId));
			}else
			if(typeNode.equals("file")){
				files = getFileService().getFiles(nodeId);
				nodeIds.add(nodeId.toString());
			}if(typeNode.equals("wfr")){
			//	Integer processingId = getWorkflowRunService().getRootProcessing(nodeId).getProcessingId();
				WorkflowRun wr = getWorkflowRunService().findByID(nodeId);
				String strChildId = getRequestedChildId(request);
				if(strChildId != null && !"".equals(strChildId)){
					Log.info("One Proc in Workflow Run");
					
					Integer processingId = Constant.getId(strChildId);
					files = getProcessingService().getFiles(processingId);
					
					// get Processing
					SortedSet<Processing> processings = new TreeSet<Processing>();
					processings.add(getProcessingService().findByID(processingId));
					
					// set one Processing in Workflow Run 
					wr.setProcessings(processings);
				}else{
					Log.info("More One Proc in Workflow Run");
					files = getWorkflowRunService().getFiles(wr.getWorkflowRunId());
				}
				nodeIds = FindChildUtil.getNodeIds(wr);
			}
			
			listAllFile = updateFiles(isSelect, files, listAllFile);

			setFiles(request, nameFileListInSession, listAllFile);
			
			// update Selected nodes in tree view 
	//		String[] allSelectIds = getRequestedAllSelectIds(request);
	//		String[] allSelectStatuses = getRequestedAllSelectStatuses(request);
			String nameSelectedIdsInSession = getNameSelectedIdsInSession(typeBulkTree);
		//	BulkUtil.getSelectedIds(request, nameSelectedIdsInSession, allSelectIds, allSelectStatuses);
			BulkUtil.updateSelectedIds(request, nameSelectedIdsInSession, isSelect, nodeIds);
			
		}
		
		MessageSourceAccessor ma = this.getMessageSourceAccessor();
		
		/**
		 * 
		 */
		List<File> listViewFile = null;
		PageInfo pageInfo = null;
		synchronized (listAllFile) {
		//	listViewFile = Collections.synchronizedList(PaginationUtil.subListSD(request, "bulkDownloadFilePage", listAllFile));
			listViewFile = Collections.synchronizedList(PaginationUtil.subListSD(request, "bulkDownloadFilePage", Collections.synchronizedList(listAllFile)));
		//	listViewFile = PaginationUtil.subListSD(request, "bulkDownloadFilePage", listAllFile);
			pageInfo = PaginationUtil.getPageInfoSD(request, "bulkDownloadFilePage", listViewFile, listAllFile, ma);
		}
		
		/**
		 * 
		 */
		
		ModelAndView modelAndView = new ModelAndView("FileList");
		modelAndView.addObject("isSelectedInput", false);
		modelAndView.addObject("files", listViewFile);
		modelAndView.addObject("pageInfo", pageInfo);
		modelAndView.addObject("registration", registration);

		return modelAndView;
	}
	
	/*private void updateMetaFile(List<FileMeta> metaFiles, List<File> files) {
		// TODO Auto-generated method stub
		for (File file: files) {
			
			//FileMeta metaFile = new FileMeta(path, size));
		}
	}*/

	private synchronized List<File> updateFiles(boolean isSelect, List<File> files, List<File> list){
		if(isSelect){
			list = addFiles(list, files);
		}else{
			list = removeFiles(list, files);
		}
		return list;
	}
	
	private List<File> addFiles(List<File> list, List<File> addList){
		Set<File> temp = new HashSet<File>();
		temp.addAll(list);
		temp.addAll(addList);
		
		List<File> result = Collections.synchronizedList(new LinkedList<File>());
		result.addAll(temp);
		
		return result;
		/*
		for(File addFile: addList){
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
	*/
//		if(!list.contains(addFile)){
		//		list.add(addFile);
		//	}
	//	return list;
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
	
	private void setSelectedIds(HttpServletRequest request, String typeNode, String selectedIds){
		HttpSession	session	= request.getSession(false);
		if(typeNode.equals("study")){
			session.setAttribute("selectedIds", selectedIds);
		}
		if(typeNode.equals("analisys")){
			session.setAttribute("analysisSelectedIds", selectedIds);
		}
	}
	
	private void setFiles(HttpServletRequest request, String nameList, List<File> list){
		HttpSession	session	= request.getSession(false);
		session.setAttribute(nameList, list);
	}

	public StudyService getStudyService() {
		return studyService;
	}

	public void setStudyService(StudyService studyService) {
		this.studyService = studyService;
	}

	public ExperimentService getExperimentService() {
		return experimentService;
	}

	public void setExperimentService(ExperimentService experimentService) {
		this.experimentService = experimentService;
	}

	public SampleService getSampleService() {
		return sampleService;
	}

	public void setSampleService(SampleService sampleService) {
		this.sampleService = sampleService;
	}

	public LaneService getLaneService() {
		return laneService;
	}

	public void setLaneService(LaneService laneService) {
		this.laneService = laneService;
	}

	public ProcessingService getProcessingService() {
		return processingService;
	}

	public void setProcessingService(ProcessingService processingService) {
		this.processingService = processingService;
	}

	public FileService getFileService() {
		return fileService;
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	public WorkflowRunService getWorkflowRunService() {
		return workflowRunService;
	}

	public void setWorkflowRunService(WorkflowRunService workflowRunService) {
		this.workflowRunService = workflowRunService;
	}
}
