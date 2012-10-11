package net.sourceforge.solexatools.util;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;

public class SetNodeIdInSession {
	
	public static void removeStudy(HttpServletRequest request){
		request.getSession(false).removeAttribute("rootStudyId");
		request.getSession(false).removeAttribute("objectId");
	}
	
	public static void setStudy(Integer studyId, HttpServletRequest request){
		request.getSession(false).setAttribute("rootStudyId",  studyId);
		request.getSession(false).setAttribute("objectId", "study_" + studyId);
	}
	
	public static void setExperiment(Experiment experiment, HttpServletRequest request){
		Study rootStudy = FindRootUtil.getStudy(experiment);
		request.getSession(false).setAttribute("rootStudyId", rootStudy.getStudyId());
		request.getSession(false).setAttribute("objectId", "exp_" + experiment.getExperimentId());
	}
	
	public static void setSampleForStudy(Sample sample, HttpServletRequest request){
	    Study rootStudy = FindRootUtil.getStudy(sample);
	    request.getSession(false).setAttribute("rootStudyId", rootStudy.getStudyId());
	    request.getSession(false).setAttribute("objectId", "sam_" + sample.getSampleId());	
	}
	
	public static void setLaneForStudy(Lane lane, HttpServletRequest request){
	    Study rootStudy = FindRootUtil.getStudy(lane);
	    request.getSession(false).setAttribute("rootStudyId", rootStudy.getStudyId());
	    request.getSession(false).setAttribute("objectId", "seq_" + lane.getLaneId());	
	}
	
	public static void setProcessingForStudy(Processing processing, HttpServletRequest request){
	    Study rootStudy = FindRootUtil.getStudy(processing);
	    request.getSession(false).setAttribute("rootStudyId", rootStudy.getStudyId());
	    request.getSession(false).setAttribute("objectId", "ae_" + processing.getProcessingId());	
	}
	
	public static void setFileForStudy(Processing processing, HttpServletRequest request){
	    Study rootStudy = FindRootUtil.getStudy(processing);
	    request.getSession(false).setAttribute("rootStudyId", rootStudy.getStudyId());
	    request.getSession(false).setAttribute("objectId", "aefl_" + processing.getProcessingId());	
	}
	
	// FOR SEQUENCER RUN
	public static void removeSequencerRun(HttpServletRequest request){
		request.getSession(false).removeAttribute("rootSequencerRunId");
		request.getSession(false).removeAttribute("objectSRId");
	}
	
	public static void setSequencerRun(Integer sequencerRunId, HttpServletRequest request){
		request.getSession(false).setAttribute("rootSequencerRunId",  sequencerRunId);
		request.getSession(false).setAttribute("objectSRId", "sr_" + sequencerRunId);
	}
	
	public static void setLaneForSequrncerRun(Lane lane, HttpServletRequest request){
		SequencerRun rootSequencerRun = FindRootUtil.getSequencerRun(lane);
	    request.getSession(false).setAttribute("rootSequencerRunId", rootSequencerRun.getSequencerRunId());
	    request.getSession(false).setAttribute("objectSRId", "seq_" + lane.getLaneId());	
	}
	
	public static void setProcessingForSequrncerRun(Processing processing, HttpServletRequest request){
		SequencerRun rootSequencerRun = FindRootUtil.getSequencerRun(processing);
	    request.getSession(false).setAttribute("rootSequencerRunId", rootSequencerRun.getSequencerRunId());
	    request.getSession(false).setAttribute("objectSRId", "ae_" + processing.getProcessingId());	
	}
	
	public static void setFileForSequrncerRun(Processing processing, HttpServletRequest request){
		SequencerRun rootSequencerRun = FindRootUtil.getSequencerRun(processing);
	    request.getSession(false).setAttribute("rootSequencerRunId", rootSequencerRun.getSequencerRunId());
	    request.getSession(false).setAttribute("objectSRId", "aefl_" + processing.getProcessingId());	
	}
	
	// FOR ANALYSIS WORKFLOW
	public static void removeWorkflowRun(HttpServletRequest request){
		request.getSession(false).removeAttribute("rootWorkflowRunId");
		request.getSession(false).removeAttribute("objectWFRId");
	}
	
	public static void setWorkflowRun(Integer workflowRunId, HttpServletRequest request){
		request.getSession(false).setAttribute("rootWorkflowRunId",  workflowRunId);
		request.getSession(false).setAttribute("objectWFRId", "wfr_" + workflowRunId);
	}
	
	public static void setWorkflowRunWithSample(HttpServletRequest request){
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
	    request.getSession(false).setAttribute("rootWorkflowRunId", rootId);
	    request.getSession(false).setAttribute("objectWFRId", "wfrs_" + rootId);	
	}
	
	public static void setSampleForWorkflowRun(Sample sample, HttpServletRequest request){
	////  Study rootStudy = FindRootUtil.getStudy(sample);
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
	    request.getSession(false).setAttribute("rootWorkflowRunId", rootId);
	    request.getSession(false).setAttribute("objectWFRId", "sam_" + sample.getSampleId());	
	}
	
	public static void setProcessingForWorkflowRun(Processing processing, HttpServletRequest request){
	////	SequencerRun rootSequencerRun = FindRootUtil.getSequencerRun(processing);
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
	    request.getSession(false).setAttribute("rootWorkflowRunId", rootId);
	    request.getSession(false).setAttribute("objectWFRId", "ae_" + processing.getProcessingId());	
	}
	
	public static void setFileForWorkflowRun(Processing processing, HttpServletRequest request){
	////	SequencerRun rootSequencerRun = FindRootUtil.getSequencerRun(processing);
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
	    request.getSession(false).setAttribute("rootWorkflowRunId", rootId);
	    request.getSession(false).setAttribute("objectWFRId", "aefl_" + processing.getProcessingId());	
	}
	
	// FOR ANALYSIS RUNNING WORKFLOW
	public static void removeWorkflowRunRunning(HttpServletRequest request){
		request.getSession(false).removeAttribute("rootWorkflowRunRunningId");
		request.getSession(false).removeAttribute("objectWFRRId");
	}
	
	public static void setWorkflowRunRunning(Integer workflowRunId, HttpServletRequest request){
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
		request.getSession(false).setAttribute("rootWorkflowRunRunningId",  rootId);
		request.getSession(false).setAttribute("objectWFRRId", "wfr_" + workflowRunId);
	}
	
	public static void setWorkflowRunRunningWithSample(HttpServletRequest request){
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
	    request.getSession(false).setAttribute("rootWorkflowRunRunningId", rootId);
	    request.getSession(false).setAttribute("objectWFRRId", "wfrs_" + rootId);	
	}
	
	public static void setSampleForWorkflowRunRunning(Sample sample, HttpServletRequest request){
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
	    request.getSession(false).setAttribute("rootWorkflowRunRunningId", rootId);
	    request.getSession(false).setAttribute("objectWFRRId", "sam_" + sample.getSampleId());	
	}
	
	public static void setProcessingForWorkflowRunRunning(Processing processing, HttpServletRequest request){
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
	    request.getSession(false).setAttribute("rootWorkflowRunRunningId", rootId);
	    request.getSession(false).setAttribute("objectWFRRId", "ae_" + processing.getProcessingId());	
	}
	
	public static void setFileForWorkflowRunRunning(Processing processing, HttpServletRequest request){
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
	    request.getSession(false).setAttribute("rootWorkflowRunRunningId", rootId);
	    request.getSession(false).setAttribute("objectWFRRId", "aefl_" + processing.getProcessingId());	
	}
}
