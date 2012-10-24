package net.sourceforge.solexatools.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.BulkUtil;
import net.sourceforge.solexatools.util.Constant;
import net.sourceforge.solexatools.util.PageInfo;
import net.sourceforge.solexatools.util.PaginationUtil;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>AnalisysBulkDownloadListDetailsController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class AnalisysBulkDownloadListDetailsController extends BaseCommandController {
	private WorkflowRunService workflowRunService;
	private SampleService sampleService;
	private LaneService laneService;
	private ProcessingService processingService;
	
	/**
	 * <p>Constructor for AnalisysBulkDownloadListDetailsController.</p>
	 */
	public AnalisysBulkDownloadListDetailsController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}
	
	private Boolean getRequestedAsc(HttpServletRequest request) {
		Boolean isAsc = null;
		String strAsc = request.getParameter("asc");
		
		if ("true".equals(strAsc)) {
			isAsc = true;
		} else
		if ("false".equals(strAsc)) {
			isAsc = false;
		}
		return isAsc;
	}
	
	private Boolean saveAscInSession(HttpServletRequest request, String attrNameInSession){
		Boolean isAsc = getRequestedAsc(request);
		if(isAsc != null){
			request.getSession(false).setAttribute(attrNameInSession, isAsc);
		}
		return isAsc(request, attrNameInSession);
	}
	
	private Boolean isAsc(HttpServletRequest request, String attrNameInSession){		
		Boolean isAsc = (Boolean)request.getSession(false).getAttribute(attrNameInSession);
		if(isAsc == null){
			isAsc = true;
		}
		return isAsc;
	}
	
	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
												 HttpServletResponse response)
		throws Exception {
		
		Registration registration = Security.getRegistration(request);
		if(registration == null){
		//	return new ModelAndView("redirect:/login.htm");
			return new ModelAndView("AnalisysListRoot");
		}
		/**
		 * Pass registration so that we can filter the list if its appropriate
		 * to do so.
		 */
		
		List<String> selectedIds = (List<String>)request.getSession(false).getAttribute("analysisSelectedNodes");
		
		WorkflowRun wfrs = new WorkflowRun();
		Sample sam = new Sample();
		Lane lane = new Lane();
		Processing proc = new Processing();
		Map<WorkflowRun, List<Processing>> wfrProc = new HashMap<WorkflowRun, List<Processing>>();
		
		PageInfo pageInfo = null;
		Boolean isHasError = false;
		String errorMessage = "";
		
		List<WorkflowRun>  listAll = new ArrayList<WorkflowRun>();
		List<WorkflowRun>  listView = new ArrayList<WorkflowRun>();
		
		String   root     = (String)request.getParameter("root");
		System.err.println("ROOT: "+root);
		if (root == null || "".equals(root) || "source".equals(root)) {
			MessageSourceAccessor ma = this.getMessageSourceAccessor();
			String nameOneItem = "analysis.list.pagination.nameOneItem"; 
			String nameLotOfItem = "analysis.list.pagination.nameLotOfItem";
			String typeList = getRequestedTypeList(request);
			if(typeList.equals("mylist")){
				Boolean isAsc = saveAscInSession(request, "ascBulkDownloadMyListAnalysis");
				listAll = getWorkflowRunService().list(registration, isAsc);
				// list = getWorkflowRunService().listRoot(registration);
				//list = getWorkflowRunService().listRootWithSample(registration);
				listView = PaginationUtil.subList(request, "myAnalisysBulkDownloadPage", listAll);
				pageInfo = PaginationUtil.getPageInfo(request, "myAnalisysBulkDownloadPage", listView, listAll, nameOneItem, nameLotOfItem, ma);
				
				// set error if user dont owned Workflow Run
				if(listAll.size() == 0){
				  isHasError = true;
				  errorMessage = this.getMessageSourceAccessor().getMessage("analysis.list.required.one.item");
				}
			}
			if(typeList.equals("bymesharelist")){
				 //list = getWorkflowService().listSharedWithMe(registration);
				 // list = getWorkflowRunService().listSharedWithMe(registration);
				Boolean isAsc = saveAscInSession(request, "ascBulkDownloadSharedWithMeListAnalysis");
		//		listAll = getWorkflowRunService().listSharedWithMeWithSample(registration, isAsc);
				listAll = getWorkflowRunService().listSharedWithMe(registration, isAsc);
				listView = PaginationUtil.subList(request, "analisysBulkDownloadSharedWithMe", listAll);
				pageInfo = PaginationUtil.getPageInfo(request, "myAnalisysBulkDownloadPage", listView, listAll, nameOneItem, nameLotOfItem, ma);
			}
			BulkUtil.selectWorkflowRunNode(selectedIds, listView);
			listView = getWorkflowRunService().listWithHasFile(listView);
		} else {
			if(root.indexOf("ae_") != -1){
				proc = getProcessingService().findByID(Constant.getId(root));
				BulkUtil.selectProcessingNode(selectedIds, proc);
				getProcessingService().setWithHasFile(proc.getChildren());
				fillWorkflowProcessingMap(proc, wfrProc);
			}else
			if(root.indexOf("wfrs_") != -1){
				wfrs = getWorkflowRunService().findByIDWithIUS(Constant.getId(root));
			}else
/*			if(root.indexOf("sam_") != -1){
			//	Integer samID = Integer.parseInt(root.substring(root.lastIndexOf("_") + 1, root.length()));
				sam = getSampleService().findByID(Constant.getId(root));
			}else
*/
			{
		//	  WorkflowRun s = getWorkflowRunService().findByID(Integer.parseInt(root));
			  WorkflowRun wfr = getWorkflowRunService().findByIDWithIUS(Integer.parseInt(root));
		//	  list			= new ArrayList<WorkflowRun>();
			  listView.add(wfr);
			  BulkUtil.selectProcessingNode(selectedIds, wfr);
			  getProcessingService().setWithHasFile(wfr.getProcessings());
			/*  
			  Log.info("PROCS:");
			  SortedSet<Processing> procs =  s.getProcessings();
			  Iterator<Processing> it = procs.iterator();
			  while (it.hasNext()) {
				Processing processing = (Processing) it.next();
				Log.info("ID=" + processing.getProcessingId());
			  }*/
			}
		}
		request.getSession(false).setAttribute("analysisSelectedNodes", selectedIds);
		
		//System.err.println("Workflow length: "+listAll.size());
		ModelAndView    modelAndView;
		if(root.indexOf("ae_") != -1){
			System.err.println("RENDERING INDIVIDUAL File with Processing");
			modelAndView  = new ModelAndView("StudyListFileProcessing");
			modelAndView.addObject("root", root);
			modelAndView.addObject("wfrproc", wfrProc);
			modelAndView.addObject("wfrprockeys", wfrProc.keySet());
		}else
		if(root.indexOf("wfrs_") != -1){
			System.err.println("RENDERING INDIVIDUAL WorkflowRun with IUS");
			modelAndView  = new ModelAndView("AnalisysListIUS");
		}else
/*		if(root.indexOf("sam_") != -1){
			System.err.println("RENDERING INDIVIDUAL Sample");
			modelAndView  = new ModelAndView("AnalisysListLane");
			modelAndView.addObject("root", root);
		}else
*/		
		if (root != null && !"".equals(root) && !"source".equals(root) && Integer.parseInt(root) > 0) {
		  System.err.println("RENDERING INDIVIDUAL WorkflowRun");
		  modelAndView  = new ModelAndView("AnalisysListDetails");
		  modelAndView.addObject("root", root);
		} else {
		  System.err.println("RENDERING ALL WORKFLOWRUNS");
		  modelAndView	= new ModelAndView("AnalisysListRoot");
		  modelAndView.addObject("pageInfo", pageInfo);
		}
		
		// set error data
		modelAndView.addObject("isHasError", isHasError);
		modelAndView.addObject("errorMessage", errorMessage);
		
		modelAndView.addObject("isBulkPage", true);
		modelAndView.addObject("workflowRuns", listView);
		modelAndView.addObject("registration", registration);
		
		modelAndView.addObject("workflowRun", wfrs);
		modelAndView.addObject("sample", sam);
		modelAndView.addObject("lane", lane);
		modelAndView.addObject("processing", proc);
		modelAndView.addObject("typeList", "tree");

		return modelAndView;
	}
	
	private void fillWorkflowProcessingMap(Processing proc,
			Map<WorkflowRun, List<Processing>> wfrProc) {
		for (Processing child: proc.getChildren()) {
			List<Processing> processings = wfrProc.get(child.getWorkflowRun());
			if (processings == null) {
				processings = new ArrayList<Processing>();
			}
			processings.add(child);
			if (child.getWorkflowRun() != null) {
				wfrProc.put(child.getWorkflowRun(), processings);
			}
		}
	}
	
    private String getRequestedTypeList(HttpServletRequest request) {
		String	typeList = (String)request.getParameter("typeList");
		if (typeList == null){
			typeList = "";
		}
		return typeList;
	}

	/**
	 * <p>Getter for the field <code>workflowRunService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.WorkflowRunService} object.
	 */
	public WorkflowRunService getWorkflowRunService() {
		return workflowRunService;
	}

	/**
	 * <p>Setter for the field <code>workflowRunService</code>.</p>
	 *
	 * @param workflowRunService a {@link net.sourceforge.seqware.common.business.WorkflowRunService} object.
	 */
	public void setWorkflowRunService(WorkflowRunService workflowRunService) {
		this.workflowRunService = workflowRunService;
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

	/**
	 * <p>Getter for the field <code>processingService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.ProcessingService} object.
	 */
	public ProcessingService getProcessingService() {
		return processingService;
	}

	/**
	 * <p>Setter for the field <code>processingService</code>.</p>
	 *
	 * @param processingService a {@link net.sourceforge.seqware.common.business.ProcessingService} object.
	 */
	public void setProcessingService(ProcessingService processingService) {
		this.processingService = processingService;
	}
}

