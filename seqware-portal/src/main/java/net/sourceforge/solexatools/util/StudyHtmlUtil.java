package net.sourceforge.solexatools.util;

import java.util.List;
import java.util.Set;

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.util.Log;

/**
 * <p>StudyHtmlUtil class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StudyHtmlUtil {
	/** Constant <code>TYPE_TREE="st"</code> */
	public final static String  TYPE_TREE = "st"; 
		
	/**
	 * <p>getHtml.</p>
	 *
	 * @param obj a {@link java.lang.Object} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @param listStudyNodeId a {@link java.util.List} object.
	 * @param treeType a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getHtml(Object obj, Registration registration, List<String> listStudyNodeId, String treeType){
		String html = "";
		if(obj instanceof Study){
			html = getAllHtml((Study)obj, registration, false, treeType); //getStydyHtml((Study)obj, registration, null);
		}
		if(obj instanceof Experiment){
			html = getAllHtml((Experiment)obj, registration, null, false, treeType);
		}
		if(obj instanceof Sample){
			html = getAllHtml((Sample)obj, registration, null, 0, listStudyNodeId, true, false, false, treeType);
		}
		if(obj instanceof IUS){
			html = getAllHtml((IUS)obj, registration, null, 1, listStudyNodeId, false, false, treeType);
		}
		if(obj instanceof Processing){
			html = getAllHtml((Processing)obj, registration, null, 0, listStudyNodeId, true, false, treeType);
		}
		return html;
	}
	
	/**
	 * <p>getFileHtml.</p>
	 *
	 * @param proc a {@link net.sourceforge.seqware.common.model.Processing} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @param listStudyNodeId a {@link java.util.List} object.
	 * @param treeType a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getFileHtml(Processing proc, Registration registration, List<String> listStudyNodeId, String treeType){
		return getAllHtml(proc, registration, null, 1, listStudyNodeId, true, true, treeType);
	}

	private static String getAllHtml(Study study, Registration registration, boolean isOpenProc, String treeType){
		return NodeHtmlUtil.getStydyHtml(study, registration, null, isOpenProc, treeType);
	}
	
	private static String getParentId(Integer currPosId, List<String> listNodeId){
	//	currPosId = currPosId + 1;
		return listNodeId.get(currPosId);
	}
	
	private static String getAllHtml(Experiment experiment, Registration registration, String openingNodeId, boolean isOpenProc, String treeType){
		String childHtml = NodeHtmlUtil.getExperimentHtml(experiment, registration, TYPE_TREE, openingNodeId, isOpenProc, treeType);
		String parentHtml = NodeHtmlUtil.getStydyHtml(experiment.getStudy(), registration, Constant.EXPERIMENT_PREFIX + experiment.getExperimentId(), false, treeType);
		String parentId = Constant.EXPERIMENT_PREFIX + experiment.getExperimentId();
		return NodeHtmlUtil.pasteHtmlIntoParentNode(childHtml, parentHtml, parentId, true);
	}
	
	private static String getAllHtml(Sample sample, Registration registration, String openingNodeId, 
		Integer currPosId, List<String> listNodeId, boolean isFirstCall, boolean isViewCurrentNode,
		boolean isOpenProc, String treeType)
	{
		currPosId++;
		String id = getParentId(currPosId, listNodeId);
		
	//	openingNodeId = getParentId(currPosId - 1, listNodeId);
		Log.info("Sample All Html. Parent id = " + id);
		
		if (id.indexOf("exp_") != -1 && !isViewCurrentNode) {
			Log.info(" - Experiment HTML");
		//	boolean isOpenSample = !isFirstCall;
			Experiment experiment = sample.getExperiment();
			if(experiment == null){
				return "";
			}
			openingNodeId = getParentId(currPosId - 1, listNodeId);
			return getAllHtml(experiment, registration, openingNodeId, false, treeType);
		} 
		else {
			Log.info(" - Sample HTML");
			Sample currSample;
			if(isFirstCall){
				currSample = sample;
			}else{ 
				currSample = getParentSample(sample, Constant.getId(id));
				openingNodeId = getParentId(currPosId - 1, listNodeId);
				if(currSample == null){
					return "";
				}
			}
			
		//	openingNodeId = getParentId(currPosId-1, listNodeId);
			
			Log.info("  -> openingNodeId = " + openingNodeId);
			
			String childHtml = NodeHtmlUtil.getSampleHtml(currSample, registration, TYPE_TREE, openingNodeId, false, false, treeType);
				
			String parentHtml = getAllHtml(currSample, registration, id/*openingNodeId*/, currPosId, listNodeId, false, isViewCurrentNode, isOpenProc, treeType);
			String parentId = Constant.SAMPLE_PREFIX + currSample.getSampleId();
			return NodeHtmlUtil.pasteHtmlIntoParentNode(childHtml, parentHtml, parentId, true);
		}
	}
	
	/**
	 * <p>getAllHtml.</p>
	 *
	 * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @param openingNodeId a {@link java.lang.String} object.
	 * @param currPosId a {@link java.lang.Integer} object.
	 * @param listNodeId a {@link java.util.List} object.
	 * @param isFirstCall a boolean.
	 * @param isViewCurrentNode a boolean.
	 * @param treeType a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getAllHtml(Processing processing, Registration registration, String openingNodeId, 
			Integer currPosId, List<String> listNodeId, boolean isFirstCall, boolean isViewCurrentNode, String treeType)
	{			
	//	Log.info(" -- Show  Processing -- ");
		
		currPosId++;
		String id = getParentId(currPosId, listNodeId);
		
		if(id.indexOf("wfr_") != -1){
			currPosId++;
			id = getParentId(currPosId, listNodeId);
		}
		
		Log.info("Proc All Html. Parent id = " + id);
		
		if (id.indexOf("study_") != -1 && !isViewCurrentNode) {
			Log.info("Proc Study HTML");
			boolean isOpenProc = !isFirstCall;
			Study study = getParentStudy(processing, Constant.getId(id));
			if(study == null){
				return "";
			}
			return getAllHtml(study, registration, isOpenProc, treeType);
		} else 
		if (id.indexOf("exp_") != -1 && !isViewCurrentNode) {
			Log.info("Proc EXPERIMENT HTML");
			Experiment experiment = getParentExperiment(processing, Constant.getId(id));
			if (experiment == null) {
				return "";
			}
			return getAllHtml(experiment, registration, openingNodeId, !isFirstCall, treeType);
		} else 
		if (id.indexOf("sam_") != -1 && !isViewCurrentNode) {
			Log.info("Proc Sample HTML");
			Sample sample = getParentSample(processing, Constant.getId(id));
			if (sample == null) {
				return "";
			}
			currPosId--;
			return getAllHtml(sample, registration, openingNodeId, currPosId, listNodeId, true, isViewCurrentNode, false, treeType);
		} else 
		if (id.indexOf("ius_") != -1 && !isViewCurrentNode) {
			Log.info("Proc IUS HTML");
			IUS ius = getParentIUS(processing, Constant.getId(id));
			if (ius == null) {
				return "";
			}
		return getAllHtml(ius, registration, openingNodeId, currPosId, listNodeId, !isFirstCall, true, treeType);
		}   
		else {
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
				
			String parentHtml = getAllHtml(currProc, registration, /*currProc.getProcessingId().toString()*/id, currPosId, listNodeId, false, false, treeType);
			String parentId = Constant.PROCESSING_PREFIX + currProc.getProcessingId();
			return NodeHtmlUtil.pasteHtmlIntoParentNode(childHtml, parentHtml, parentId, true);
		}
	}

	/**
	 * <p>getAllHtml.</p>
	 *
	 * @param ius a {@link net.sourceforge.seqware.common.model.IUS} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @param openingNodeId a {@link java.lang.String} object.
	 * @param currPosId a {@link java.lang.Integer} object.
	 * @param listNodeId a {@link java.util.List} object.
	 * @param isOpenProc a boolean.
	 * @param isVisibleProc a boolean.
	 * @param treeType a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getAllHtml(IUS ius, Registration registration, String openingNodeId,
			Integer currPosId, List<String> listNodeId, boolean isOpenProc, boolean isVisibleProc, String treeType)
	{
		Log.info(" - IUS HTML, openingNodeId = " + getParentId(currPosId, listNodeId));
		
		String childHtml = NodeHtmlUtil.getIUSHtml(ius, registration, TYPE_TREE, Constant.PROCESSING_PREFIX + openingNodeId, isOpenProc, isVisibleProc, treeType);
		String parentHtml = getAllHtml(ius.getSample(), registration, Constant.IUS_PREFIX + ius.getIusId(), currPosId, listNodeId, true, false, isOpenProc, treeType);
		String parentId = Constant.IUS_PREFIX + ius.getIusId();
		return NodeHtmlUtil.pasteHtmlIntoParentNode(childHtml, parentHtml, parentId, true);
	}
	
	private static Study getParentStudy(Processing processing, Integer parentId){
		Study parentStudy = null;
		Set<Study> studies = processing.getStudies();
		for (Study s : studies) {
			if(parentId.equals(s.getStudyId())){
				parentStudy = s;
			}
		}
		return parentStudy;
	}
	
	private static Experiment getParentExperiment(Processing processing, Integer parentId){
		Experiment parentExperiment = null;
		Set<Experiment> experiments = processing.getExperiments();
		for (Experiment ex : experiments) {
			if(parentId.equals(ex.getExperimentId())){
				parentExperiment = ex;
			}
		}
		return parentExperiment;
	}	
	
	private static Sample getParentSample(Sample sample, Integer parentId){
		Sample parentSample = null;
		Set<Sample> samples = sample.getParents();
		for (Sample s : samples) {
			if(parentId.equals(s.getSampleId())){
				parentSample = s;
			}
		}
		return parentSample;
	}
	
	private static Sample getParentSample(Processing processing, Integer parentId){
		Sample parentSample = null;
		Set<Sample> samples = processing.getSamples();
		for (Sample s : samples) {
			if(parentId.equals(s.getSampleId())){
				parentSample = s;
			}
		}
		return parentSample;
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
}
