package net.sourceforge.solexatools.util;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;

/**
 * <p>SetNodeIdInSession class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SetNodeIdInSession {
	
	/**
	 * <p>removeStudy.</p>
	 *
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void removeStudy(HttpServletRequest request){
		request.getSession(false).removeAttribute("rootStudyId");
		request.getSession(false).removeAttribute("objectId");
	}
	
	/**
	 * <p>setStudy.</p>
	 *
	 * @param studyId a {@link java.lang.Integer} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setStudy(Integer studyId, HttpServletRequest request){
		request.getSession(false).setAttribute("rootStudyId",  studyId);
		request.getSession(false).setAttribute("objectId", "study_" + studyId);
	}
	
	/**
	 * <p>setExperiment.</p>
	 *
	 * @param experiment a {@link net.sourceforge.seqware.common.model.Experiment} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setExperiment(Experiment experiment, HttpServletRequest request){
		Study rootStudy = FindRootUtil.getStudy(experiment);
		request.getSession(false).setAttribute("rootStudyId", rootStudy.getStudyId());
		request.getSession(false).setAttribute("objectId", "exp_" + experiment.getExperimentId());
	}
	
	/**
	 * <p>setSampleForStudy.</p>
	 *
	 * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setSampleForStudy(Sample sample, HttpServletRequest request){
	    Study rootStudy = FindRootUtil.getStudy(sample);
	    request.getSession(false).setAttribute("rootStudyId", rootStudy.getStudyId());
	    request.getSession(false).setAttribute("objectId", "sam_" + sample.getSampleId());	
	}
	
	/**
	 * <p>setLaneForStudy.</p>
	 *
	 * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setLaneForStudy(Lane lane, HttpServletRequest request){
	    Study rootStudy = FindRootUtil.getStudy(lane);
	    request.getSession(false).setAttribute("rootStudyId", rootStudy.getStudyId());
	    request.getSession(false).setAttribute("objectId", "seq_" + lane.getLaneId());	
	}
	
	/**
	 * <p>setProcessingForStudy.</p>
	 *
	 * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setProcessingForStudy(Processing processing, HttpServletRequest request){
	    Study rootStudy = FindRootUtil.getStudy(processing);
	    request.getSession(false).setAttribute("rootStudyId", rootStudy.getStudyId());
	    request.getSession(false).setAttribute("objectId", "ae_" + processing.getProcessingId());	
	}
	
	/**
	 * <p>setFileForStudy.</p>
	 *
	 * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setFileForStudy(Processing processing, HttpServletRequest request){
	    Study rootStudy = FindRootUtil.getStudy(processing);
	    request.getSession(false).setAttribute("rootStudyId", rootStudy.getStudyId());
	    request.getSession(false).setAttribute("objectId", "aefl_" + processing.getProcessingId());	
	}
	
	// FOR SEQUENCER RUN
	/**
	 * <p>removeSequencerRun.</p>
	 *
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void removeSequencerRun(HttpServletRequest request){
		request.getSession(false).removeAttribute("rootSequencerRunId");
		request.getSession(false).removeAttribute("objectSRId");
	}
	
	/**
	 * <p>setSequencerRun.</p>
	 *
	 * @param sequencerRunId a {@link java.lang.Integer} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setSequencerRun(Integer sequencerRunId, HttpServletRequest request){
		request.getSession(false).setAttribute("rootSequencerRunId",  sequencerRunId);
		request.getSession(false).setAttribute("objectSRId", "sr_" + sequencerRunId);
	}
	
	/**
	 * <p>setLaneForSequrncerRun.</p>
	 *
	 * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setLaneForSequrncerRun(Lane lane, HttpServletRequest request){
		SequencerRun rootSequencerRun = FindRootUtil.getSequencerRun(lane);
	    request.getSession(false).setAttribute("rootSequencerRunId", rootSequencerRun.getSequencerRunId());
	    request.getSession(false).setAttribute("objectSRId", "seq_" + lane.getLaneId());	
	}
	
	/**
	 * <p>setProcessingForSequrncerRun.</p>
	 *
	 * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setProcessingForSequrncerRun(Processing processing, HttpServletRequest request){
		SequencerRun rootSequencerRun = FindRootUtil.getSequencerRun(processing);
	    request.getSession(false).setAttribute("rootSequencerRunId", rootSequencerRun.getSequencerRunId());
	    request.getSession(false).setAttribute("objectSRId", "ae_" + processing.getProcessingId());	
	}
	
	/**
	 * <p>setFileForSequrncerRun.</p>
	 *
	 * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setFileForSequrncerRun(Processing processing, HttpServletRequest request){
		SequencerRun rootSequencerRun = FindRootUtil.getSequencerRun(processing);
	    request.getSession(false).setAttribute("rootSequencerRunId", rootSequencerRun.getSequencerRunId());
	    request.getSession(false).setAttribute("objectSRId", "aefl_" + processing.getProcessingId());	
	}
	
	// FOR ANALYSIS WORKFLOW
	/**
	 * <p>removeWorkflowRun.</p>
	 *
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void removeWorkflowRun(HttpServletRequest request){
		request.getSession(false).removeAttribute("rootWorkflowRunId");
		request.getSession(false).removeAttribute("objectWFRId");
	}
	
	/**
	 * <p>setWorkflowRun.</p>
	 *
	 * @param workflowRunId a {@link java.lang.Integer} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setWorkflowRun(Integer workflowRunId, HttpServletRequest request){
		request.getSession(false).setAttribute("rootWorkflowRunId",  workflowRunId);
		request.getSession(false).setAttribute("objectWFRId", "wfr_" + workflowRunId);
	}
	
	/**
	 * <p>setWorkflowRunWithSample.</p>
	 *
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setWorkflowRunWithSample(HttpServletRequest request){
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
	    request.getSession(false).setAttribute("rootWorkflowRunId", rootId);
	    request.getSession(false).setAttribute("objectWFRId", "wfrs_" + rootId);	
	}
	
	/**
	 * <p>setSampleForWorkflowRun.</p>
	 *
	 * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setSampleForWorkflowRun(Sample sample, HttpServletRequest request){
	////  Study rootStudy = FindRootUtil.getStudy(sample);
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
	    request.getSession(false).setAttribute("rootWorkflowRunId", rootId);
	    request.getSession(false).setAttribute("objectWFRId", "sam_" + sample.getSampleId());	
	}
	
	/**
	 * <p>setProcessingForWorkflowRun.</p>
	 *
	 * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setProcessingForWorkflowRun(Processing processing, HttpServletRequest request){
	////	SequencerRun rootSequencerRun = FindRootUtil.getSequencerRun(processing);
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
	    request.getSession(false).setAttribute("rootWorkflowRunId", rootId);
	    request.getSession(false).setAttribute("objectWFRId", "ae_" + processing.getProcessingId());	
	}
	
	/**
	 * <p>setFileForWorkflowRun.</p>
	 *
	 * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setFileForWorkflowRun(Processing processing, HttpServletRequest request){
	////	SequencerRun rootSequencerRun = FindRootUtil.getSequencerRun(processing);
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
	    request.getSession(false).setAttribute("rootWorkflowRunId", rootId);
	    request.getSession(false).setAttribute("objectWFRId", "aefl_" + processing.getProcessingId());	
	}
	
	// FOR ANALYSIS RUNNING WORKFLOW
	/**
	 * <p>removeWorkflowRunRunning.</p>
	 *
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void removeWorkflowRunRunning(HttpServletRequest request){
		request.getSession(false).removeAttribute("rootWorkflowRunRunningId");
		request.getSession(false).removeAttribute("objectWFRRId");
	}
	
	/**
	 * <p>setWorkflowRunRunning.</p>
	 *
	 * @param workflowRunId a {@link java.lang.Integer} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setWorkflowRunRunning(Integer workflowRunId, HttpServletRequest request){
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
		request.getSession(false).setAttribute("rootWorkflowRunRunningId",  rootId);
		request.getSession(false).setAttribute("objectWFRRId", "wfr_" + workflowRunId);
	}
	
	/**
	 * <p>setWorkflowRunRunningWithSample.</p>
	 *
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setWorkflowRunRunningWithSample(HttpServletRequest request){
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
	    request.getSession(false).setAttribute("rootWorkflowRunRunningId", rootId);
	    request.getSession(false).setAttribute("objectWFRRId", "wfrs_" + rootId);	
	}
	
	/**
	 * <p>setSampleForWorkflowRunRunning.</p>
	 *
	 * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setSampleForWorkflowRunRunning(Sample sample, HttpServletRequest request){
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
	    request.getSession(false).setAttribute("rootWorkflowRunRunningId", rootId);
	    request.getSession(false).setAttribute("objectWFRRId", "sam_" + sample.getSampleId());	
	}
	
	/**
	 * <p>setProcessingForWorkflowRunRunning.</p>
	 *
	 * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setProcessingForWorkflowRunRunning(Processing processing, HttpServletRequest request){
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
	    request.getSession(false).setAttribute("rootWorkflowRunRunningId", rootId);
	    request.getSession(false).setAttribute("objectWFRRId", "ae_" + processing.getProcessingId());	
	}
	
	/**
	 * <p>setFileForWorkflowRunRunning.</p>
	 *
	 * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
	 * @param request a {@link javax.servlet.http.HttpServletRequest} object.
	 */
	public static void setFileForWorkflowRunRunning(Processing processing, HttpServletRequest request){
		Integer rootId = Integer.parseInt(request.getParameter("rootId"));
	    request.getSession(false).setAttribute("rootWorkflowRunRunningId", rootId);
	    request.getSession(false).setAttribute("objectWFRRId", "aefl_" + processing.getProcessingId());	
	}
}
