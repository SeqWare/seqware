package net.sourceforge.solexatools.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowRunParam;
import net.sourceforge.seqware.common.util.Log;

public class LaunchWorkflowUtil {
	
	public final static String SELECTED_NODES = "launchSelectedNodes";
	public final static String SELECTED_FILES = "selectedLaunchFiles";
	public final static String SELECTED_VALUES = "selectedWorkflowParamValueId";
	
	
	// methods for SELECTED NODES
	public static List<String> getCurrentSelectedNodes(HttpServletRequest request){
		Integer key = getCurrentWorkflowParam(request).getWorkflowParamId();
		Map<Integer, List<String>> selectedNodes = getSelectedNodesFromSession(request);
		return selectedNodes.get(key);
	}
	
	public static void setCurrentSelectedNodes(HttpServletRequest request, List<String> selectedNodesOneParam){
		Integer key = getCurrentWorkflowParam(request).getWorkflowParamId();
		Map<Integer, List<String>> selectedNodes = getSelectedNodesFromSession(request);
		
		selectedNodes.put(key, selectedNodesOneParam);
		request.getSession(false).setAttribute(SELECTED_NODES, selectedNodes);
	}
	
	private static void removeSelectedNodesCurrentParam(HttpServletRequest request){
		setCurrentSelectedNodes(request, null);
	}
	
	private static Map<Integer, List<String>> getSelectedNodesFromSession(HttpServletRequest request){
		Map<Integer, List<String>> selectedNodes = (HashMap<Integer, List<String>>)request.getSession(false).getAttribute(SELECTED_NODES);
		if(selectedNodes==null){
			selectedNodes = new HashMap<Integer, List<String>>();
		}
		return selectedNodes;
	}
	
	// methods for SELECTED FILES
	public static List<File> getCurrentSelectedFiles(HttpServletRequest request){
		Integer key = getCurrentWorkflowParam(request).getWorkflowParamId();
		Map<Integer, List<File>> selectedFiles = getSelectedFilesFromSession(request);
		return selectedFiles.get(key);
	}
	
	public static void setCurrentSelectedFiles(HttpServletRequest request, List<File> selectedFilesOneParam){
		Integer key = getCurrentWorkflowParam(request).getWorkflowParamId();
		Map<Integer, List<File>> selectedFiles = getSelectedFilesFromSession(request);
		
		selectedFiles.put(key, selectedFilesOneParam);
		request.getSession(false).setAttribute(SELECTED_FILES, selectedFiles);
	}
	
	private static void removeSelectedFilesCurrentParam(HttpServletRequest request){
		setCurrentSelectedFiles(request, null);
	}
	
	private static Map<Integer, List<File>> getSelectedFilesFromSession(HttpServletRequest request){
		Map<Integer, List<File>> selectedFiles = (HashMap<Integer, List<File>>)request.getSession(false).getAttribute(SELECTED_FILES);
		if(selectedFiles==null){
			selectedFiles = new HashMap<Integer, List<File>>();
		}
		return selectedFiles;
	}
	
	// remove Last param data form session
	public static void removeSelectedItemsCurrentParam(HttpServletRequest request){
		removeSelectedFilesCurrentParam(request);
		removeSelectedNodesCurrentParam(request);
	}
	
	public static void removeSelectedItemsLaunchWorkflow(HttpServletRequest request){
		request.getSession(false).removeAttribute(SELECTED_NODES);
		request.getSession(false).removeAttribute(SELECTED_FILES);
		request.getSession(false).removeAttribute(SELECTED_VALUES);
	}
	
	/** 
	 * Get all selected Files, the files are organized by the name of the parameter (key) and a list of files selected (value) via a hash.
	 *  
	 * @param request
	 * @return
	 */
	public static Map<String, List<File>> getAllSelectedFiles(HttpServletRequest request){
	  
		HashMap<String, List<File>> files = new HashMap<String, List<File>>();		
		SortedSet<WorkflowParam> params = getCurrentWorkflow(request).getWorkflowParamsWithDifferentFileMetaType();
		
		// if current workflow not set in session
		if(params== null)
			return files;
		
		Map<Integer, List<File>> selectedFiles = getSelectedFilesFromSession(request);
		
		for (WorkflowParam param : params) {
			Integer key = param.getWorkflowParamId();
			String paramName = param.getKey();
			
			List<File> filesOneParam = selectedFiles.get(key); 
			
			List<File> oldValues = files.get(paramName);
			
			if (oldValues == null) {
			  files.put(paramName, filesOneParam);
			} else {
			  oldValues.addAll(filesOneParam);
			}
			
		}
		return files;
	}
	
	public static SummaryData getSummaryData(HttpServletRequest request){
		SummaryData summaryDate = new SummaryData();
		List<SummaryLine> summaryLines = new LinkedList<SummaryLine>();
		
		Map<Integer, List<File>> selectedFiles = getSelectedFilesFromSession(request);
		SortedSet<WorkflowParam> visibleParams = getCurrentWorkflow(request).getVisibleWorkflowParams();
		SortedSet<WorkflowParam> differentParams = getCurrentWorkflow(request).getWorkflowParamsWithDifferentFileMetaType();
		
//		Map<Integer, String> selectedValues = getSelectedWorkflowParamValue(request);
		
//		Log.info("		=== GET SUMMARY DATA ===");
		for(WorkflowParam workflowParam: visibleParams){
			if(workflowParam.getDisplayValue() != null){
				workflowParam.setValue(workflowParam.getDisplayValue());
				workflowParam.setDisplayName(workflowParam.getDisplayName());
			}
		}
/*		for(WorkflowParam workflowParam: visibleParams){
			
			String strWorkflowParamValueId = selectedValues.get(workflowParam.getWorkflowParamId());
			Log.info("	strWorkflowParamValueId = " + strWorkflowParamValueId);
			// set default value
			if(strWorkflowParamValueId == null || "defaultValue".equals(strWorkflowParamValueId)){
				String defaultValue = workflowParam.getDefaultValue();
				// set single value
				if( defaultValue == null || "".equals(defaultValue)){
					if(workflowParam.getValues().size() == 1){
						workflowParam.setDisplayName(workflowParam.getValues().first().getDisplayName());
					}
				}
				//set value if not different value
				else{
					SortedSet<WorkflowParamValue> values = workflowParam.getValues();
					for (WorkflowParamValue workflowParamValue : values) {
						// equals by value
						if(defaultValue.equals(workflowParamValue.getValue())){
							workflowParam.setDisplayName(workflowParamValue.getDisplayName());
						}
					}
				}
			}
			// set if not default value
			else{
				Integer workflowParamValueId = Integer.parseInt(strWorkflowParamValueId);
				SortedSet<WorkflowParamValue> values = workflowParam.getValues();
				for (WorkflowParamValue workflowParamValue : values) {
					// equals by ID
					if(workflowParamValueId.equals(workflowParamValue.getWorkflowParamValueId())){
						workflowParam.setValue(workflowParamValue.getDisplayName());
						Log.info("set value = " + workflowParamValue.getDisplayName());
					}
				}
				Log.info("workflowParam display name = " + workflowParam.getDisplayName());
				Log.info("workflowParam value name = " + workflowParam.getValue());
			}
		}
*/		
		// set display name
/*		for(WorkflowParam workflowParam: visibleParams){
			String currValue = workflowParam.getValue();			
			if(currValue != null) {
				SortedSet<WorkflowParamValue> values = workflowParam.getValues();
				for (WorkflowParamValue workflowParamValue : values) {
					if(currValue.equals(workflowParamValue.getValue())){
						workflowParam.setDisplayName(workflowParamValue.getDisplayName());
					}
				}
			}
		}
*/		
		//set visible params
		summaryDate.setVisibleParams(visibleParams);
		
		// set summary lines
		for (WorkflowParam differentParam : differentParams) {
			Integer key = differentParam.getWorkflowParamId();
			differentParam.setFiles(selectedFiles.get(key));
			
			SummaryLine summaryLine = new SummaryLine();
			summaryLine.setFileMetaType(differentParam.getFileMetaType());
			summaryLine.setDisplayName(differentParam.getJsonEscapeDisplayName());
			summaryLine.setFiles(selectedFiles.get(key));
			
			SortedSet<WorkflowParam> summaryParams = new TreeSet<WorkflowParam>();
			
			for (WorkflowParam visibleParam : visibleParams) {
				if(summaryLine.getFileMetaType().equals(visibleParam.getFileMetaType())){
					summaryParams.add(visibleParam);
				}else{
					WorkflowParam emptyParamWithKey = new WorkflowParam();
					emptyParamWithKey.setWorkflowParamId(visibleParam.getWorkflowParamId());
					emptyParamWithKey.setKey(visibleParam.getKey());
					emptyParamWithKey.setValue("");
					summaryParams.add(emptyParamWithKey);
				}
			}
			summaryLine.setParams(summaryParams);			
			summaryLines.add(summaryLine);
			
		}
		summaryDate.setSummaryLines(summaryLines);
		//return params;
		return summaryDate;
	}
	
	public List<Integer> getIdsSelectedLane(HttpServletRequest request, WorkflowParam param){
		List<Integer> ids = new LinkedList<Integer>();
		
		Integer key = param.getWorkflowParamId();
		Map<Integer, List<String>> selectedNodes = getSelectedNodesFromSession(request);
		
		List<String> nodesOneParam = selectedNodes.get(key);
		
		for (String node : nodesOneParam) {
			if(node.indexOf(Constant.LANE_PREFIX) != -1){
				ids.add(Constant.getId(node));
			}
		}
		
		return ids;
	}
	
	
	// get current workflow from session
	public static Workflow getCurrentWorkflow(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object workflow = session.getAttribute("workflow");
			if (workflow != null) {
				return (Workflow)workflow;
			}
		}
		return new Workflow();
	}
	
	// get current workflow param from session
	public static WorkflowParam getCurrentWorkflowParam(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object workflowParam = session.getAttribute("workflowParam");
			if (workflowParam != null) {
				return (WorkflowParam)workflowParam;
			}
		}
		return new WorkflowParam();
	}
	
	public static void saveSelectedWorkflowParamValue(HttpServletRequest request, Integer workflowParamId, 
			String strWorkflowParamValueId)
	{
		Map<Integer, String> selectedValues = getSelectedWorkflowParamValue(request);
		
		selectedValues.put(workflowParamId, strWorkflowParamValueId);
		
		request.getSession(false).setAttribute(SELECTED_VALUES, selectedValues);
	}
	
	public static Map<Integer, String> getSelectedWorkflowParamValue(HttpServletRequest request)
	{
		Map<Integer, String> selectedValues = (Map<Integer, String>)request.getSession(false).getAttribute(SELECTED_VALUES);
		if(selectedValues == null){
			selectedValues = new HashMap<Integer, String>();
		}
		return selectedValues;
	}
	
	public static SortedSet<WorkflowRunParam> getWorkflowRunParam(SortedSet<WorkflowParam> params){
		SortedSet<WorkflowRunParam> runParams = new TreeSet<WorkflowRunParam>();
	//	SortedSet<WorkflowParam> params = workflow.getWorkflowParams();
	//  SortedSet<WorkflowParam> params = workflow.getVisibleWorkflowParams();
		for (WorkflowParam param : params) {
			WorkflowRunParam runParam = new WorkflowRunParam();
			runParam.setWorkflowRunParamId(param.getWorkflowParamId());
			runParam.setKey(param.getKey());
			runParam.setType(param.getType());
			runParam.setValue(param.getValue());
			
			Log.info("Key = " + param.getKey() + "; Type = " + param.getType() + "; Value = " + param.getValue() + ";");
			/*
			if(param.getDisplay()){
				runParam.setValue(param.getValue());
				Log.info("Set value = " + param.getValue() + ";");
			}else{
				runParam.setValue(param.getDefaultValue());
				Log.info("Set def value = " + param.getDefaultValue() + ";");
			}
			*/
			runParams.add(runParam);
		}
		return runParams;
	}
}
