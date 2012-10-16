package net.sourceforge.solexatools.webapp.controller;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.OrganismService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * RegistrationSetupController
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SampleSetupController extends BaseCommandController {
    
	private ExperimentService experimentService;
	private SampleService sampleService;
	private LaneService laneService;
	private OrganismService organismService;

	/**
	 * <p>Constructor for SampleSetupController.</p>
	 */
	public SampleSetupController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}

	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest	 request,
												 HttpServletResponse response)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();
		Sample				sample		= getRequestedSample(request);
		boolean isReport = request.getParameter("report") != null;
		
		model.put("organismList", getOrganismService().list(registration));

		if (sample != null) {
			request.setAttribute(getCommandName(), sample);
			request.setAttribute("experimentId", sample.getExperiment() != null ? sample.getExperiment().getExperimentId() : "");
			request.setAttribute("swid", sample.getSwAccession());
			model.put("strategy", "update");
			sample.setOrganismId(sample.getOrganism() != null ? sample.getOrganism().getOrganismId() : null);
			if (!isReport) {
				modelAndView = new ModelAndView("Sample", model);
			} else {
				modelAndView = new ModelAndView("SampleReport", model);
			}
		} else {
			sample = new Sample();
			sample.setOwner(registration);
			
		//	Integer experimentId = Integer.parseInt(request.getParameter("experimentId"));
		//	sample.setExperiment(getExperimentService().findByID(experimentId));
			
			Integer experimentId = getRequestedExperimentId(request);
			if (experimentId != null) {
				sample.setExperiment(getExperimentService().findByID(experimentId));
				request.setAttribute("experimentId", experimentId);
			} else {
				Integer parentSampleId = getRequestedParentId(request);
				request.setAttribute("parentSampleId", parentSampleId);
			}
			
			request.setAttribute(getCommandName(), sample);
			
			model.put("strategy", "submit");
			modelAndView = new ModelAndView("Sample", model);
		}
		
		String typeTree = request.getParameter("tt");
		request.getSession(false).setAttribute("typeTree", typeTree);
	/*	
		String laneId = request.getParameter("laneId");
				
		Log.info("TYPE TREE Sample Setup = " + typeTree);
		Log.info("Lane ID = " + laneId);
		
		if(typeTree!= null && typeTree.equals("st")){
				Log.info("In Study");
				SetNodeIdInSession.setSampleForStudy(sample, request);
		}else
		if(typeTree!= null && typeTree.equals("sr")){
			if(laneId != null && !laneId.equals("")){
				Log.info("In SR");
				Integer sequencerId = getLaneService().findByID(Integer.parseInt(laneId)).getSequencerRun().getSequencerRunId();
				SetNodeIdInSession.setSequencerRun(sequencerId, request);
			}
		}else
		if(typeTree!= null && typeTree.equals("wfr")){
			Log.info("In wfr");
			SetNodeIdInSession.setWorkflowRunWithSample(request);
		}else
		if(typeTree!= null && typeTree.equals("wfrr")){
				Log.info("In wfrr");
				SetNodeIdInSession.setWorkflowRunRunningWithSample(request);
		}else{
			Log.info("By DEFAULT");
			SetNodeIdInSession.setExperiment(sample.getExperiment(), request);
		}
		*/
		return modelAndView;
	}

	private Integer getRequestedExperimentId(HttpServletRequest request) {
		Integer expID	= null;
		String	strId	= (String)request.getParameter("experimentId");
		if (strId != null && !strId.isEmpty()) {
			expID = Integer.parseInt(strId);
		}
		return expID;
	}
	
	private Integer getRequestedParentId(HttpServletRequest request) {
		Integer parentID= null;
		String	strId	= (String)request.getParameter("parentSampleId");
		if (strId != null && !strId.isEmpty()) {
			parentID = Integer.parseInt(strId);
		}
		return parentID;
	}
	
	private Sample getRequestedSample(HttpServletRequest request) {
		HttpSession	session		= request.getSession(false);
		Sample	sample	= null;
		String		id			= (String)request.getParameter("sampleId");
		session.removeAttribute("sample");
		if (id != null) {
			Integer sampleID = Integer.parseInt(id);
			sample = getSampleService().findByID(sampleID);
			session.setAttribute("sample", sample);
		}
		return sample;
	}

	/**
	 * <p>Getter for the field <code>organismService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.OrganismService} object.
	 */
	public OrganismService getOrganismService() {
		return organismService;
	}

	/**
	 * <p>Setter for the field <code>organismService</code>.</p>
	 *
	 * @param organismService a {@link net.sourceforge.seqware.common.business.OrganismService} object.
	 */
	public void setOrganismService(OrganismService organismService) {
		this.organismService = organismService;
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
}
