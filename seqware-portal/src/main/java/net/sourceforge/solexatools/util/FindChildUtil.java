package net.sourceforge.solexatools.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.WorkflowRun;

/**
 * <p>FindChildUtil class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class FindChildUtil {
	
	/**
	 * <p>getNodeIds.</p>
	 *
	 * @param node a {@link java.lang.Object} object.
	 * @return a {@link java.util.List} object.
	 */
	public static List<String> getNodeIds(Object node){
		List<String> ids = new LinkedList<String>();
		
		if(node instanceof Study){
			Study study = (Study)node;
			ids.add(Constant.STUDY_PREFIX + study.getStudyId());
			ids.addAll(getIds(study));
		}else
		if(node instanceof Experiment){
			Experiment experiment = (Experiment)node;
			ids.add(Constant.EXPERIMENT_PREFIX + experiment.getExperimentId());
			ids.addAll(getIds(experiment));
		}else
		if(node instanceof Sample){
			Sample sample = (Sample)node;
			ids.add(Constant.SAMPLE_PREFIX + sample.getSampleId());
			ids.addAll(getIds(sample));
		}else
//		if(node instanceof Lane){
//			Lane lane = (Lane)node;
//			ids.add(Constant.LANE_PREFIX + lane.getLaneId());
//			ids.addAll(getIds(lane));
//		}else
		if(node instanceof IUS){
			IUS ius = (IUS)node;
			ids.add(Constant.IUS_PREFIX + ius.getIusId());
			ids.addAll(getIds(ius));
		}else
		if(node instanceof Processing){
			Processing processing = (Processing)node;
			ids.add(Constant.PROCESSING_PREFIX + processing.getProcessingId());
			ids.addAll(getIds(processing));
		}else
		if(node instanceof WorkflowRun){
			WorkflowRun workflowRun = (WorkflowRun)node;
			ids.add(Constant.WORKFLOW_RUN_PREFIX + workflowRun.getWorkflowRunId());
			SortedSet<Processing> processings = workflowRun.getProcessings();
			for (Processing processing : processings) {
				ids.add(Constant.PROCESSING_PREFIX + processing.getProcessingId());
				ids.addAll(getIds(processing));	
			}
		//	Processing processing = getRootProcessing(workflowRun);
		//	ids.add(Constant.WORKFLOW_RUN_PREFIX + workflowRun.getWorkflowRunId());
			
		}
		
		return ids;
	}
	
	/**
	 * <p>getRootProcessing.</p>
	 *
	 * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
	 * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
	 */
	public static Processing getRootProcessing(WorkflowRun workflowRun){
		Set<Processing> processings = workflowRun.getProcessings();
		for (Processing processing : processings) {
			return processing;
		}
		return null;
	}
		
	private static List<String> getIds(Study study){
		List<String> ids = new LinkedList<String>();		
		SortedSet<Experiment> experiments = study.getExperiments();
		for (Experiment experiment : experiments) {
			ids.add(Constant.EXPERIMENT_PREFIX + experiment.getExperimentId());
			ids.addAll(getIds(experiment));
		}
		
		Set<Processing> processings = study.getProcessings();
		for (Processing processing : processings) {
			ids.add(Constant.PROCESSING_PREFIX + processing.getProcessingId());
			ids.addAll(getIds(processing));
		}
		return ids;
	}
	
	private static List<String> getIds(Experiment experiment){
		List<String> ids = new LinkedList<String>();
		SortedSet<Sample> samples = experiment.getSamples();
		for (Sample sample : samples) {
			ids.add(Constant.SAMPLE_PREFIX + sample.getSampleId());
			ids.addAll(getIds(sample));
		}
		
		Set<Processing> processings = experiment.getProcessings();
		for (Processing processing : processings) {
			ids.add(Constant.PROCESSING_PREFIX + processing.getProcessingId());
			ids.addAll(getIds(processing));
		}
		return ids;
	}
	
	private static List<String> getIds(Sample sample){
		List<String> ids = new LinkedList<String>();
		SortedSet<IUS> iuss = sample.getIUS();
		for (IUS ius : iuss) {
			ids.add(Constant.IUS_PREFIX + ius.getIusId());
			ids.addAll(getIds(ius));
		}
	/*	SortedSet<Lane> lanes = sample.getLanes();
		for (Lane lane : lanes) {
			ids.add(Constant.LANE_PREFIX +lane.getLaneId());
			ids.addAll(getIds(lane));
		}
	*/	
		Set<Processing> processings = sample.getProcessings();
		for (Processing processing : processings) {
			ids.add(Constant.PROCESSING_PREFIX + processing.getProcessingId());
			ids.addAll(getIds(processing));
		}
		
		Set<Sample> children = sample.getChildren();
		for (Sample child : children) {
			ids.add(Constant.SAMPLE_PREFIX + child.getSampleId());
			ids.addAll(getIds(child));
		}
		
		return ids;
	}
	
	private static List<String> getIds(IUS ius){
		List<String> ids = new LinkedList<String>();
		Set<Processing> processings = ius.getProcessings();
		for (Processing processing : processings) {
			ids.add(Constant.PROCESSING_PREFIX +processing.getProcessingId());
			ids.addAll(getIds(processing));
		}
		return ids;
	}
	
/*	private static List<String> getIds(Lane lane){
		List<String> ids = new LinkedList<String>();
		Set<Processing> processings = lane.getProcessings();
		for (Processing processing : processings) {
			ids.add(Constant.PROCESSING_PREFIX +processing.getProcessingId());
		//	if(processing.getWorkflowRun() != null){
		//		ids.add(Constant.WORKFLOW_RUN_PREFIX + processing.getWorkflowRun().getWorkflowRunId());
		//	}
			ids.addAll(getIds(processing));
		}
		return ids;
	}
*/	
	private static List<String> getIds(Processing processing){
		List<String> ids = new LinkedList<String>();
		ids.addAll(getIds(processing.getFiles()));
		
		Set<Processing> processings = processing.getChildren();
		for (Processing pr : processings) {
			// select Workflow Run
			
	//		if(pr.getWorkflowRun() != null){
	//			Log.info("Proc Id = " + pr.getProcessingId() + " WR Id = " + pr.getWorkflowRun().getWorkflowRunId());
	//		}
			
	//		if(pr.getWorkflowRun() != null){
	//			Log.info("Add WR ID = " + pr.getWorkflowRun().getWorkflowRunId() );
	//			ids.add(Constant.WORKFLOW_RUN_PREFIX + pr.getWorkflowRun().getWorkflowRunId());
	//		}
			// then select Processing
			ids.add(Constant.PROCESSING_PREFIX + pr.getProcessingId());
			
			// and then select File
	//		ids.addAll(getIds(pr.getFiles()));
			
			// also call function
			ids.addAll(getIds(pr));
		}
		return ids;
	}
	
	private static List<String> getIds(Set<File> files){
		List<String> ids = new LinkedList<String>();
		for (File file : files) {
			ids.add(Constant.FILE_PREFIX + file.getFileId());
		}
		return ids;
	}
}
