package net.sourceforge.solexatools.util;

import java.util.List;
import java.util.Set;

import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.WorkflowRun;

/**
 * <p>WorkflowRunHtmlUtil class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowRunHtmlUtil {
	/** Constant <code>TYPE_TREE_COMPLETED="wfr"</code> */
	public final static String TYPE_TREE_COMPLETED = "wfr";
	/** Constant <code>TYPE_TREE_RUNNNING="wfrr"</code> */
	public final static String TYPE_TREE_RUNNNING = "wfrr";
	
	/**
	 * <p>getHtml.</p>
	 *
	 * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @param typeTree a {@link java.lang.String} object.
	 * @param isWorkflowRunWithSample a boolean.
	 * @param treeType a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getHtml(WorkflowRun workflowRun, Registration registration, String typeTree, boolean isWorkflowRunWithSample, String treeType){
		String html = "";
		if(isWorkflowRunWithSample){
	//		Log.info("Show AS Sample");
			html = getAllHtmlWorkflowRunWithSample(workflowRun, registration, typeTree, null, treeType);
		} else {
			html = getAllHtmlWorkflowRun(workflowRun, registration, typeTree, treeType);
		}
		return html;
	}
	/**
	 * <p>getHtml.</p>
	 *
	 * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
	 * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @param typeTree a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getHtml(Sample sample, WorkflowRun workflowRun, Registration registration, String typeTree){
		return getAllHtml(sample, workflowRun, registration, typeTree, null);
	}
	/**
	 * <p>getHtml.</p>
	 *
	 * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
	 * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @param typeTree a {@link java.lang.String} object.
	 * @param listWorkflowNodeId a {@link java.util.List} object.
	 * @param treeType a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getHtml(Processing processing, WorkflowRun workflowRun, Registration registration, String typeTree, List<String> listWorkflowNodeId, String treeType){
		return getAllHtml(processing, workflowRun, registration, typeTree, 0, listWorkflowNodeId, null, true, false, treeType);
	}
	
	/**
	 * <p>getFileHtml.</p>
	 *
	 * @param proc a {@link net.sourceforge.seqware.common.model.Processing} object.
	 * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @param typeTree a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getFileHtml(Processing proc, WorkflowRun workflowRun, Registration registration, String typeTree){
	//	return getAllHtml(proc, workflowRun, registration, typeTree, 0, listWorkflowNodeId, null, true, true);
		return "";
	}
	
	private static String getAllHtmlWorkflowRun(WorkflowRun workflowRun, Registration registration, String typeTree, String treeType){
		return NodeHtmlUtil.getWorkflowRunHtml(workflowRun, registration, typeTree, null, treeType);
	}
	
	private static String getAllHtmlWorkflowRunWithSample(WorkflowRun workflowRun, Registration registration, String typeTree, String openingNodeId, String treeType){
		String childHtml = NodeHtmlUtil.getWorkflowRunHtmlWithIUSs(workflowRun, registration, typeTree, openingNodeId, treeType);
		String parentHtml = NodeHtmlUtil.getWorkflowRunHtml(workflowRun, registration, typeTree, Constant.WORKFLOW_RUN_PREFIX + workflowRun.getWorkflowRunId(), treeType);
		String parentId = Constant.WORKFLOW_RUN_PREFIX + workflowRun.getWorkflowRunId();
		return NodeHtmlUtil.pasteHtmlIntoParentNode(childHtml, parentHtml, parentId, true);
	}
	
	private static String getAllHtml(Sample sample, WorkflowRun workflowRun, Registration registration, String typeTree, String openingNodeId){
		String childHtml = "";//NodeHtmlUtil.getSampleHtml(sample, registration, typeTree, openingNodeId, true);
		String parentHtml = ""; // getAllHtmlWorkflowRunWithSample(workflowRun, registration, typeTree, sample.getSampleId());
		String parentId = Constant.SAMPLE_PREFIX + sample.getSampleId();
		return NodeHtmlUtil.pasteHtmlIntoParentNode(childHtml, parentHtml, parentId, true);
	}
	private static String getParentId(Integer currPosId, List<String> listNodeId){
		//	currPosId = currPosId + 1;
			return listNodeId.get(currPosId);
	}
	
	// BRANCH WITH PROC
	private static String getAllHtml(Processing processing, WorkflowRun workflowRun, Registration registration, String typeTree,
			Integer currPosId, List<String> listNodeId, String openingNodeId, boolean isFirstCall, boolean isViewCurrentNode, String treeType)
	{
		currPosId++;
		String id = getParentId(currPosId, listNodeId);
		if(id.indexOf("wfr_") != -1 && currPosId + 1 < listNodeId.size()){
			currPosId++;
			id = getParentId(currPosId, listNodeId);
		}

		if(listNodeId.size() - 1 == currPosId){
			return NodeHtmlUtil.getWorkflowRunHtml(workflowRun, registration, typeTree, openingNodeId, treeType);
		}else{
						
			Processing currProc;
			if(isFirstCall){
				currProc = processing;
			}else{ 
				currProc = getParentProcessing(processing, Constant.getId(id));
				if(currProc == null){
					return "";
				}
				
				openingNodeId = getParentId(currPosId-1, listNodeId);
				if(openingNodeId.indexOf("wfr_") != -1){
					openingNodeId = getParentId(currPosId-2, listNodeId);
				}
			}
			
			if(typeTree.equals(TYPE_TREE_COMPLETED)){
				currProc.resetCompletedChildren();
			}
			if(typeTree.equals(TYPE_TREE_RUNNNING)){
				currProc.resetRunningChildren();
			}
			
			String childHtml = NodeHtmlUtil.getProcessingHtml(currProc, registration, typeTree, openingNodeId, treeType);
			String parentHtml = getAllHtml(currProc, workflowRun, registration, typeTree, currPosId, listNodeId, id/*currProc.getProcessingId()*/, false, false, treeType);
			String parentId = Constant.PROCESSING_PREFIX + currProc.getProcessingId();
			return NodeHtmlUtil.pasteHtmlIntoParentNode(childHtml, parentHtml, parentId, true);
		}
	}
	
	private static Processing getParentProcessing(Processing processing, Integer parentId){
		Processing parentProcessing = null;
		Set<Processing> processings = processing.getParents();
		for (Processing p : processings) {
			if(parentId.equals(p.getProcessingId())){
				parentProcessing = p;
			}
		}
		return parentProcessing;
	}
	/*
	private static Processing getRootProcessing(WorkflowRun workflowRun){
		Processing root = null;
		SortedSet<Processing> rootProcessings = workflowRun.getProcessings();
		for (Processing rootProcessing : rootProcessings) {
			root = rootProcessing;
		}
		return root;
	}
	
	private static Processing getParentProcessing(Processing processing){
		Processing parent = null;
		Set<Processing> parents = processing.getParents();
		for (Processing pr : parents) {
			parent = pr;
		}
		return parent;
	}
	*/
}
