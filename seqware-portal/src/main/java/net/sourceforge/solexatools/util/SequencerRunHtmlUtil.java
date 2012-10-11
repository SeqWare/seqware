package net.sourceforge.solexatools.util;

import java.util.List;
import java.util.Set;

import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.util.Log;

public class SequencerRunHtmlUtil {
	public final static String  TYPE_TREE = "sr"; 
	
	public static String getHtml(Object obj, Registration registration,  List<String> listSeqencerRunNodeId, String treeType){
		String html = "";
		if(obj instanceof SequencerRun){
			html = getAllHtml((SequencerRun)obj, registration, null, treeType);
		}
		if(obj instanceof Lane){
			html = getAllHtml((Lane)obj, registration, null, treeType);
		}
		if(obj instanceof IUS){
			Log.info("ius call");
			html = getAllHtml((IUS)obj, registration, null, false, false, treeType);
		}
		if(obj instanceof Processing){
			html = getAllHtml((Processing)obj, registration, null, 0, listSeqencerRunNodeId, true, false, treeType);
		}
		return html;
	}
	
	public static String getFileHtml(Processing proc, Registration registration){
	//	return getAllHtml(proc, registration, null, true, true);
		return "";
	}
	
	private static String getAllHtml(SequencerRun sequencerRun, Registration registration, String openingNodeId, String treeType){
		return  NodeHtmlUtil.getSequencerRunHtml(sequencerRun, registration, openingNodeId, treeType);
	}
	
	public static String getAllHtml(Lane lane, Registration registration, String openingNodeId, String treeType){
		String childHtml = NodeHtmlUtil.getLaneHtml(lane, registration, TYPE_TREE, openingNodeId, treeType);
		String parentHtml = getAllHtml(lane.getSequencerRun(), registration, Constant.LANE_PREFIX + lane.getLaneId(), treeType);
		String parentId = Constant.LANE_PREFIX + lane.getLaneId();
		return NodeHtmlUtil.pasteHtmlIntoParentNode(childHtml, parentHtml, parentId, true);
	}
	
	public static String getAllHtml(IUS ius, Registration registration, String openingNodeId, boolean isOpenProc,boolean isVisibleProc, String treeType){
	//	Log.info("ius call All html");
		String childHtml = NodeHtmlUtil.getIUSHtml(ius, registration, TYPE_TREE, openingNodeId, isOpenProc, isVisibleProc, treeType);
		String parentHtml = getAllHtml(ius.getLane(), registration, Constant.IUS_PREFIX + ius.getIusId(), treeType);
		String parentId = Constant.IUS_PREFIX + ius.getIusId();
		return NodeHtmlUtil.pasteHtmlIntoParentNode(childHtml, parentHtml, parentId, true);
	}
	
	public static String getAllHtml(Processing processing, Registration registration, String openingNodeId,
			Integer currPosId, List<String> listNodeId, boolean isFirstCall, boolean isViewCurrentNode, String treeType)
	{
		currPosId++;
		String id = getParentId(currPosId, listNodeId);
		
		if(id.indexOf("wfr_") != -1){
			currPosId++;
			id = getParentId(currPosId, listNodeId);
		}
		
		if(id.indexOf("sr_") != -1 && !isViewCurrentNode){
			SequencerRun sequencerRun = getParentSequencerRun(processing, Constant.getId(id));
			if(sequencerRun == null){
				return "";
			}
			Log.info("openingNodeId = " + openingNodeId);
			return getAllHtml(sequencerRun, registration, openingNodeId/*processing.getProcessingId()*/, treeType);
		} else
		if(id.indexOf("seq_") != -1 && !isViewCurrentNode){
			Lane lane = getParentLane(processing, Constant.getId(id));
			if(lane == null){
				return "";
			}
			return getAllHtml(lane, registration, openingNodeId, treeType);
		//1	return getAllHtml(lane, registration, processing.getProcessingId());
		//2	return getAllHtml(lane, registration, processing.getProcessingId(), isOpenProc, true);
		} else
		if (id.indexOf("ius_") != -1 && !isViewCurrentNode) {
			Log.info("Proc IUS HTML");
			IUS ius = getParentIUS(processing, Constant.getId(id));
			if (ius == null) {
				return "";
			}
			return getAllHtml(ius, registration, openingNodeId, !isFirstCall, true, treeType);			
		} else {
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
			
			Log.info("  -> openingNodeId Proc = " + openingNodeId);
			
			String childHtml = NodeHtmlUtil.getProcessingHtml(currProc, registration, TYPE_TREE, openingNodeId, treeType);
				
			String parentHtml = getAllHtml(currProc, registration, id, currPosId, listNodeId, false, false, treeType);
			String parentId = Constant.PROCESSING_PREFIX + currProc.getProcessingId();
			return NodeHtmlUtil.pasteHtmlIntoParentNode(childHtml, parentHtml, parentId, true);
			
//			Log.info("currProcId = " + currProc.getProcessingId());
			
//			String childHtml = NodeHtmlUtil.getProcessingHtml(currProc, registration, TYPE_TREE, openingNodeId);
			
//			String parentHtml = getAllHtml(currProc, registration, currProc.getProcessingId(), currPosId, listNodeId, false, false);
//			String parentId = Constant.PROCESSING_PREFIX + currProc.getProcessingId();
//			return NodeHtmlUtil.pasteHtmlIntoParentNode(childHtml, parentHtml, parentId, true);
		}
	}

	private static String getParentId(Integer currPosId, List<String> listNodeId){
		return listNodeId.get(currPosId);
	}
	
	private static SequencerRun getParentSequencerRun(Processing processing, Integer parentId){
		SequencerRun parentSequencerRun = null;
		Set<SequencerRun> sequencerRuns = processing.getSequencerRuns();
		for (SequencerRun sr : sequencerRuns) {
			if(parentId.equals(sr.getSequencerRunId())){
				parentSequencerRun = sr;
			}
		}
		return parentSequencerRun;
	}
	
	public static Lane getParentLane(Processing processing, Integer parentId){
		Lane parentLane = null;
	//	Log.info("	LANE PARENT ID = " + parentId);
		Set<Lane> lanes = processing.getLanes();
		for (Lane l : lanes) {
			if(parentId.equals(l.getLaneId())){
				parentLane = l;
			}
		}
		return parentLane;
	}
	
	private static IUS getParentIUS(Processing processing, Integer parentId){
		IUS parentIUS = null;
		Set<IUS> ius = processing.getIUS();
		for (IUS i : ius) {
			if(parentId.equals(i.getIusId())){
				parentIUS = i;
			}
		}
		return parentIUS;
	}
	
	public static Processing getParentProcessing(Processing processing, Integer parentId){
		Processing parentProcessing = null;
		Set<Processing> processings = processing.getParents();
		for (Processing p : processings) {
			if(parentId.equals(p.getProcessingId())){
				parentProcessing = p;
			}
		}
		return parentProcessing;
	}
}
