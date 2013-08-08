package net.sourceforge.solexatools.util;

import java.util.SortedSet;

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.Log;

/**
 * <p>TreeNodeHtmlUtil class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class TreeNodeHtmlUtil {
	
	private static final String htmlEmptyNode = 
		"<ul style='display: none;'><li id='placeholder' class='last'>placeholder</li></ul>";
	private static final String endHtmlEmptyNode = 
		"<ul style='display: none;'><li id='placeholder' class='last'>placeholder</li></ul></li>";
	
	/**
	 * <p>getHtml.</p>
	 *
	 * @param obj a {@link java.lang.Object} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @param treeType a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getHtml(Object obj, Registration registration, String treeType){
		String html = "";
		if(obj instanceof Study){
			html = getAllHtml((Study)obj, registration, treeType); //getStydyHtml((Study)obj, registration, null);
		}
		if(obj instanceof Experiment){
			html = getAllHtml((Experiment)obj, registration, null, treeType);
		}
		if(obj instanceof Sample){
			html = getAllHtml((Sample)obj, registration, null, treeType);
		}
		if(obj instanceof Lane){
			html = getAllHtml((Lane)obj, registration, null, treeType);
		}
		if(obj instanceof Processing){
		//	html = getAllHtml((Experiment)obj, registration);
		}
		
		return html;
	}
	
	/**
	 * <p>getAllHtml.</p>
	 *
	 * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @param treeType a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getAllHtml(Study study, Registration registration, String treeType){
		return  getStydyHtml(study, registration, null, treeType);
	}
	
	/**
	 * <p>getAllHtml.</p>
	 *
	 * @param experiment a {@link net.sourceforge.seqware.common.model.Experiment} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @param openingNodeId a {@link java.lang.Integer} object.
	 * @param treeType a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getAllHtml(Experiment experiment, Registration registration, Integer openingNodeId, String treeType){
		String childHtml = getExperimentHtml(experiment, registration, openingNodeId);
		String parentHtml = getStydyHtml(experiment.getStudy(), registration, experiment.getExperimentId(), treeType);
		String parentId = Constant.EXPERIMENT_PREFIX + experiment.getExperimentId();
		return pasteHtmlIntoParentNode(childHtml, parentHtml, parentId);
	}
	
	/**
	 * <p>getAllHtml.</p>
	 *
	 * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @param openingNodeId a {@link java.lang.Integer} object.
	 * @param treeType a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getAllHtml(Sample sample, Registration registration, Integer openingNodeId, String treeType){
		String childHtml = getSampleHtml(sample, registration, openingNodeId);
		String parentHtml = getAllHtml(sample.getExperiment(), registration, sample.getSampleId(), treeType);
		String parentId = Constant.SAMPLE_PREFIX + sample.getSampleId();
		return pasteHtmlIntoParentNode(childHtml, parentHtml, parentId);
	}
	
	/**
	 * <p>getAllHtml.</p>
	 *
	 * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @param openingNodeId a {@link java.lang.Integer} object.
	 * @param treeType a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getAllHtml(Lane lane, Registration registration, Integer openingNodeId, String treeType){
		String childHtml = getLaneHtml(lane, registration, openingNodeId);
		String parentHtml = getAllHtml(lane.getSample(), registration, lane.getLaneId(), treeType);
		String parentId = Constant.LANE_PREFIX + lane.getLaneId();
		return pasteHtmlIntoParentNode(childHtml, parentHtml, parentId);
	}
	
	private static String pasteHtmlIntoParentNode(String childHtml, String parentHtml, String parentId){
		
		// opening parent node
		
		
		String newHtml = "<ul style=''>" + childHtml + "</ul></li>";//"<ul style=''>" + childHtml + "</ul></li>";
		
		int start = parentHtml.indexOf(parentId);
		start = parentHtml.indexOf("<ul", start);
/*		
		Log.info("******************************Start**************************");
		Log.info(parentHtml.substring(0, start));
		Log.info();
		
		//Log.info("******************************New***************************");
		int nst = start+endHtmlEmptyNode.length();
		Log.info("Start = " + start + "; New start = " + nst + "; End = " + parentHtml.length());
		Log.info("******************************End**************************");
		Log.info(parentHtml.substring(nst, parentHtml.length()));
		
		Log.info();
		Log.info("******************************newHtml**************************");
		Log.info(newHtml);
		
*/		
		parentHtml = parentHtml.substring(0, start) + newHtml + 
						parentHtml.substring(start+endHtmlEmptyNode.length(), parentHtml.length());
		
		
		Log.info("");
		Log.info("******************************All**************************");
		Log.info(parentHtml);
		
	//	parentHtml = parentHtml.replaceFirst(htmlEmptyNode, newHtml);
		
//		int endIndex = parentHtml.length() - endHtmlEmptyNode.length();
//		parentHtml = parentHtml.substring(0, endIndex) + newHtml;
		
		return parentHtml;
	}
	
	// get html code all Processing in one Lane
	/**
	 * <p>getNodeHtml.</p>
	 *
	 * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getNodeHtml(Processing processing, Registration registration){
		String html = "";
		
		WorkflowRun wrf = processing.getWorkflowRun();
		
		if(wrf!=null){
			Integer wrfId = wrf.getWorkflowRunId();
			//html = "<li class='expandable'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div>" +
			//"<span id='wfr_"+ wrfId +"'>Analysis Workflow</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span> <span class='m-link'><a href='#'>edit</a> - <a href='#'>delete</a></span> <span class='m-description'>Description:</span><ul style='display: none;'></li>";
                        html = "<li class='expandable'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div>" +
			"<span id='wfr_"+ wrfId +"'>Analysis Workflow</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span> <span class='m-link'><a href='#'>edit</a></span> <span class='m-description'>Description:</span><ul style='display: none;'></li>";

		}
		
		Integer aeId = processing.getProcessingId();
		Integer swAccession = processing.getSwAccession();
		String decs = processing.getJsonEscapeDescription();
		String algorithm = processing.getAlgorithm();
		String updateTimestamp = processing.getUpdateTimestamp().toString();
		String status = processing.getStatus().toString();
		
		String name = algorithm + updateTimestamp +	" SWID: "+ swAccession;
		
		String ownerHtml = "";
		if(registration.getRegistrationId().equals(processing.getOwner().getRegistrationId())){
			String editLink = "<a href='#'>edit</a> -";
			//String deleteLink = "<a href='#' popup-delete='true' form-action='processingDelete.htm' object-id='"+ aeId + "' object-name='Analysis Event "+ name +"'>delete</a>";
			String deleteLink = "";
			ownerHtml = "<span class='m-link'> "+ editLink + deleteLink + "</span>"; 
		}		

		String aeHtml = 
		"<li id='liae_"+ aeId +
		"' class='hasChildren expandable'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id='ae_"+ aeId +
		"' >>Analysis Event: "+ name + " (" + status + ")" +
		"</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span>" +
		ownerHtml +
		"<span class='m-description'>Description: "+ decs + 
	//	"</span>"+ htmlEmptyNode +"</li>";
		"</span>"+ endHtmlEmptyNode;
		
		html = html + aeHtml;
			
		return html;
	}
	
	// get html code all Processing in one Lane
	/**
	 * <p>getLaneHtml.</p>
	 *
	 * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @param openingNodeId a {@link java.lang.Integer} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getLaneHtml(Lane lane, Registration registration, Integer openingNodeId){
		String html = "";
		return html;
	}
	
	// get html code all Lane in one Sample
	/**
	 * <p>getSampleHtml.</p>
	 *
	 * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @param openingNodeId a {@link java.lang.Integer} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getSampleHtml(Sample sample, Registration registration, Integer openingNodeId){
		String html = "";
		SortedSet<Lane> lanes = sample.getLanes();
		
		Integer sampleId = sample.getSampleId();
		Integer swAccessionSample = sample.getSwAccession();
		String sampleTitle = sample.getTitle();
		for (Lane lane: lanes) {
			Integer laneId = lane.getLaneId();
			Integer swAccession = lane.getSwAccession();
			String decs = lane.getJsonEscapeDescription();
			String name = lane.getName()/*.substring(0, 100)*/;
			
			String ownerHtml = "";
			if(registration.getRegistrationId().equals(lane.getOwner().getRegistrationId())){
				String editLink = "<a href='laneSetup.htm?laneId="+laneId +"'> edit </a>";
				String deleteLink = "<a href='#' popup-delete='true' form-action='laneDelete.htm' object-id='"+ laneId + "' object-name='Sequence "+ name +"'>delete</a>";
				String addLink = "<a href='sampleSetup.htm?sampleId="+sampleId +"'>Associated with Sample SWID:"+ swAccessionSample + " " + sampleTitle + "</a>";
				ownerHtml = "<span class='m-link'> "+ editLink + "- "+ deleteLink +" - "+ addLink +" </span>"; 
			}
			
			String statuses = "";
			Integer processingCnt = lane.getProcessingCnt();
			Integer processedCnt = lane.getProcessedCnt();
			Integer errorCnt = lane.getErrorCnt();
			if(processingCnt > 0 || processedCnt > 0 || errorCnt > 0){
				statuses = "( " + processedCnt + "successes";
				if(errorCnt > 0){
					statuses = statuses + ", " + errorCnt + "errors";
				}
				if(processingCnt > 0){
					statuses = statuses + ", " + processedCnt + "running";
				}
				statuses = statuses + ")";
			}
			
			String closeOpenHtml = "' class='hasChildren expandable'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id='seq_";
			if(openingNodeId != null && openingNodeId == laneId)
				closeOpenHtml = "' class='collapsable'><div class='hitarea hasChildren-hitarea collapsable-hitarea' ></div><span id='seq_";
			 
						
			String laneHtml = 
			"<li id='liseq_"+ laneId +
			closeOpenHtml + laneId +
			"' >Sequence: "+ name +
			" SWID: "+ swAccession + statuses +
			"</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span>" +
			ownerHtml +
			"<span class='m-description'>Description: "+ decs + 
		//	"</span>"+ htmlEmptyNode +"</li>";
			"</span>"+ endHtmlEmptyNode;
			
			html = html + laneHtml;
			
		}
		return html;
	}
	
	// get html code all Sample in one Experiment
	private static String getExperimentHtml(Experiment experiment, Registration registration, Integer openingNodeId){
		String html = "";
		SortedSet<Sample> samples = experiment.getSamples();
		
		for (Sample sample : samples) {
			Integer sampleId = sample.getSampleId();
			String decs = sample.getJsonEscapeDescription();
			String name = sample.getName()/*.substring(0, 100)*/;
			String title = sample.getTitle();
                        Integer swAccession = sample.getSwAccession();
			
			String ownerHtml = "";
			if(registration.getRegistrationId().equals(experiment.getOwner().getRegistrationId())){
				String editLink = "<a href='sampleSetup.htm?sampleId="+ sampleId + "'> edit </a>";
				String deleteLink = "<a href='#' popup-delete='true' form-action='sampleDelete.htm' object-id='"+ sampleId + "' object-name='Sample "+ title +"'>delete</a>";
				String addLink = "<a href='uploadSequenceSetup.htm?sampleId="+ sampleId + "'>upload sequence</a>";
				ownerHtml = "<span class='m-link'> "+ editLink + "- "+ deleteLink +" - "+ addLink +" </span>"; 
			}          
			
			String closeOpenHtml = "' class='hasChildren expandable'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id='sam_";
			if(openingNodeId != null && openingNodeId == sampleId)
				closeOpenHtml = "' class='collapsable'><div class='hitarea hasChildren-hitarea collapsable-hitarea' ></div><span id='sam_";
			 
			
			String sampleHtml = 
			"<li id='lisam_"+ sampleId +
			closeOpenHtml + sampleId +
			"' >Sample: "+ title + " SWID: " + swAccession +
			"</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span>" +
			ownerHtml +
			"<span class='m-description'>Description: "+ decs + 
		//	"</span>"+ htmlEmptyNode +"</li>";
			"</span>"+ endHtmlEmptyNode;
			
			html = html + sampleHtml;
		}
		
		return html;
	}
	// get html code all Experiment in one Study
	/**
	 * <p>getStydyHtml.</p>
	 *
	 * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
	 * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
	 * @param openingNodeId a {@link java.lang.Integer} object.
	 * @param treeType a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getStydyHtml(Study study, Registration registration, Integer openingNodeId, String treeType){
		String html = "";
		SortedSet<Experiment> experiments = study.getExperiments();
		
		Integer studyId = study.getStudyId();
		for (Experiment experiment : experiments) {
			Integer expId = experiment.getExperimentId();
			Integer swAccession = experiment.getSwAccession();
			String decs = experiment.getJsonEscapeDescription();
			String name = experiment.getName()/*.substring(0, 100)*/;
			String title = experiment.getTitle();
			
			String ownerHtml = "";
			if(registration.getRegistrationId().equals(experiment.getOwner().getRegistrationId())){
				String editLink = "<a href='experimentSetup.htm?experimentId="+ expId + "&studyId="+ studyId +"'> edit </a>";
				String deleteLink = "<a href='#' popup-delete='true' form-action='experimentDelete.htm' object-id='"+ expId + "' object-name='Experiment "+ title +"'>delete</a>";
				String addLink = "<a href='sampleSetup.htm?experimentId="+ expId +"&studyId=" + studyId +"'> add sample</a>";
				ownerHtml = "<span class='m-link'> "+ editLink + "- "+ deleteLink +" - "+ addLink +" </span>"; 
			}
			
			String closeOpenHtml = "' class='hasChildren expandable'><div class='hitarea hasChildren-hitarea expandable-hitarea' ></div><span id='exp_";
			if(openingNodeId != null && openingNodeId == expId)
				closeOpenHtml = "' class='collapsable'><div class='hitarea hasChildren-hitarea collapsable-hitarea' ></div><span id='exp_";
			
			String expHtml = "";
			if (treeType.equals("tree")) {
				expHtml = "<li id='liexp_"+ expId +
					closeOpenHtml + expId +
					"' >Experiment: "+ name +
					" SWID: "+ swAccession +
					"</span> <span><a class='m-question np-mousetrack supernote-hover-demo1' href='#demo1'><img src='i/ico/ico_question.gif'></a></span>" +
					ownerHtml +
					"<span class='m-description'>Description: "+ decs + 
				//	"</span>"+ htmlEmptyNode +"</li>";
					"</span>"+ endHtmlEmptyNode;
			} else {
				expHtml = "<li id='liexp_"+ expId +
						"<span id='exp_" + expId +
						"' >Experiment: "+ name +
						" SWID: "+ swAccession +
						"</span>" +
						ownerHtml
						+ endHtmlEmptyNode;
			}
			html = html + expHtml;
			
		}
		return html;
	}
}
