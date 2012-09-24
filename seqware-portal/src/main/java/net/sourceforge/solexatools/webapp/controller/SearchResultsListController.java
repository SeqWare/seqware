package net.sourceforge.solexatools.webapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.PageInfo;
import net.sourceforge.solexatools.util.PaginationUtil;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

public class SearchResultsListController extends BaseCommandController {

	private final String nameOneItem = "search.list.pagination.nameOneItem";
	private final String nameLotOfItem = "search.list.pagination.nameLotOfItem";
	
	private List<Object> searchResults;

	// Services Used for Search
	private StudyService studyService;
	private ExperimentService experimentService;
	private SampleService sampleService;
	private IUSService IUSService;
	private SequencerRunService sequencerRunService;
	private LaneService laneService;
	private ProcessingService processingService;
	private FileService fileService;
	private WorkflowService workflowService;
	private WorkflowRunService workflowRunService;
	
	public SearchResultsListController(){}

	public void setStudyService(StudyService studyService) {
		this.studyService = studyService;
	}

	public void setExperimentService(ExperimentService experimentService) {
		this.experimentService = experimentService;
	}

	public void setSampleService(SampleService sampleService) {
		this.sampleService = sampleService;
	}

	public void setIUSService(IUSService iusService) {
		this.IUSService = iusService;
	}

	public void setSequencerRunService(SequencerRunService sequencerRunService) {
		this.sequencerRunService = sequencerRunService;
	}

	public void setLaneService(LaneService laneService) {
		this.laneService = laneService;
	}

	public void setProcessingService(ProcessingService processingService) {
		this.processingService = processingService;
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setWorkflowRunService(WorkflowRunService workflowRunService) {
		this.workflowRunService = workflowRunService;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Registration registration = Security.getRegistration(request);
		if(registration == null){
			return new ModelAndView("redirect:/login.htm");
		}
		
		Boolean isHasError = false;
		String errorMessage = "";
		
		String criteria = request.getParameter("criteria");
		String type = request.getParameter("type");
		
//		if (criteria == null || type == null) {
//			return new ModelAndView("redirect:/search.htm");
//		}
		
		String mode = request.getParameter("mode");
		
		// Create ModelAndView
		ModelAndView modelAndView = new ModelAndView("SearchResultsList");
		if ("create".equals(mode)) {
			// return to the first page here
			request.getSession(false).removeAttribute("mySearchPage");
			boolean isCaseSens =  getCaseSens(request);
			searchResults = getListForCriteria(criteria, type, modelAndView, isCaseSens);
		}
		if ("create".equals(mode) || "sort".equals(mode)) {
			Comparator<Object> comp = new Comparator<Object>() {

				@Override
				public int compare(Object arg0, Object arg1) {
					// Compare by class name
					String name1 = arg0.getClass().getSimpleName();
					String name2 = arg1.getClass().getSimpleName();
					return name1.compareTo(name2);
				}
				
			};
			Collections.sort(searchResults, comp);
		}
		MessageSourceAccessor ma = this.getMessageSourceAccessor();
		List<Object> listView = PaginationUtil.subList(request,
				"mySearchPage", searchResults);
		PageInfo pageInfo = PaginationUtil.getPageInfo(request,
				"mySearchPage", listView, searchResults, nameOneItem, nameLotOfItem, ma);
		// set error data
		modelAndView.addObject("isHasError", isHasError);
		modelAndView.addObject("errorMessage", errorMessage);
		modelAndView.addObject("pageInfo", pageInfo);
		modelAndView.addObject("results", listView);
		return modelAndView;
	}

	private boolean getCaseSens(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String out = (String) session.getAttribute(SearchController.SEARCH_CASE_SENSITIVE);
		if ("true".equals(out)) {
			return true;
		}
		return false;
	}

	private List<Object> getListForCriteria(String criteria, String type,
			ModelAndView modelAndView, boolean isCaseSens) {
		modelAndView.addObject(criteria);
		List<Object> out = new ArrayList<Object>();
		// Create appropriate model for selected type
		if (type.equals("Study") || type.equals("All")) {
			List<Study> studies = studyService.findByCriteria(criteria, isCaseSens);
			out.addAll(studies);
		} 
		if (type.equals("Experiment") || type.equals("All")) {
			List<Experiment> experiments = experimentService
					.findByCriteria(criteria, isCaseSens);
			out.addAll(experiments);
		} 
		if (type.equals("Sample") || type.equals("All")) {
			List<Sample> samples = sampleService.findByCriteria(criteria, isCaseSens);
			out.addAll(samples);
		} 
		if (type.equals("IUS") || type.equals("All")) {
			List<IUS> iuses = IUSService.findByCriteria(criteria, isCaseSens);
			out.addAll(iuses);
		} 
		if (type.equals("SequencerRun") || type.equals("All")) {
			List<SequencerRun> seqRuns = sequencerRunService
					.findByCriteria(criteria, isCaseSens);
			out.addAll(seqRuns);
		} 
		if (type.equals("Lane") || type.equals("All")) {
			List<Lane> lanes = laneService.findByCriteria(criteria, isCaseSens);
			out.addAll(lanes);
		} 
		if (type.equals("Processing") || type.equals("All")) {
			List<Processing> processings = processingService
					.findByCriteria(criteria, isCaseSens);
			out.addAll(processings);
		} 
		if (type.equals("File") || type.equals("All")) {
			List<File> files = fileService.findByCriteria(criteria, isCaseSens);
			out.addAll(files);
		} 
		if (type.equals("Workflow") || type.equals("All")) {
			List<Workflow> workflows = workflowService
					.findByCriteria(criteria, isCaseSens);
			out.addAll(workflows);
		} 
		if (type.equals("WorkflowRun") || type.equals("All")) {
			List<WorkflowRun> workflowRuns = workflowRunService
					.findByCriteria(criteria, isCaseSens);
			out.addAll(workflowRuns);
		}
		
		return out;
	}
}
