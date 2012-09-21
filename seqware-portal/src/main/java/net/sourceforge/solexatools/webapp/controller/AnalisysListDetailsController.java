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
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.Constant;
import net.sourceforge.solexatools.util.PageInfo;
import net.sourceforge.solexatools.util.PaginationUtil;
import net.sourceforge.solexatools.util.WorkflowRunHtmlUtil;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

public class AnalisysListDetailsController extends BaseCommandController {
	private WorkflowRunService workflowRunService;
	private SampleService sampleService;
	private LaneService laneService;
	private ProcessingService processingService;
	
	public AnalisysListDetailsController() {
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
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
												 HttpServletResponse response)
		throws Exception {

	/*	Registration registration = Security.requireRegistration(request, response);
		if(registration == null){
			ModelAndView modelAndView = new ModelAndView("login"); 
			//modelAndView.addObject("registration", registration);
			return modelAndView;
		}*/
		
		Registration registration = Security.getRegistration(request);
		if(registration == null){
		//	return new ModelAndView("redirect:/login.htm");
			return new ModelAndView("AnalisysListRoot");
		}
		
		/**
		 * Pass registration so that we can filter the list if its appropriate
		 * to do so.
		 */
		
		WorkflowRun wfrs = new WorkflowRun();
		Sample sam = new Sample();
//		Lane lane = new Lane();
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
				Boolean isAsc = saveAscInSession(request, "ascMyListAnalysis");
				listAll = getWorkflowRunService().list(registration, isAsc);
				// list = getWorkflowRunService().listRoot(registration);
				 //Log.info("ZZZ=" + listAll.size());
				 //list = getWorkflowRunService().listRootWithSample(registration);
				listView = PaginationUtil.subList(request, "myAnalisysesPage", listAll);
			    listView = loadNode(listView, registration, request);
				pageInfo = PaginationUtil.getPageInfo(request, "myAnalisysesPage", listView, listAll, nameOneItem, nameLotOfItem, ma);
				 
				if(listAll.size() == 0 && getWorkflowRunService().listRunning(registration).size() == 0){
					isHasError = true;
					errorMessage = this.getMessageSourceAccessor().getMessage("analysis.list.required.one.item");
				}
			}
			if(typeList.equals("mysharelist")){
				Boolean isAsc = saveAscInSession(request, "ascMySharedAnalysises");
				listAll = getWorkflowRunService().listMyShared(registration, isAsc);
				listView = PaginationUtil.subList(request, "mySharedAnalisysesPage", listAll);
				listView = loadNode(listView, registration, request);
				pageInfo = PaginationUtil.getPageInfo(request, "mySharedAnalisysesPage", listView, listAll, nameOneItem, nameLotOfItem, ma);
			}
			if(typeList.equals("bymesharelist")){
				Boolean isAsc = saveAscInSession(request, "ascAnalysisesSharedWithMe");
				listAll = getWorkflowRunService().listSharedWithMe(registration, isAsc);
				listView = PaginationUtil.subList(request, "analisysesSharedWithMePage", listAll);
				pageInfo = PaginationUtil.getPageInfo(request, "analisysesSharedWithMePage", listView, listAll, nameOneItem, nameLotOfItem, ma);
			}			  
		} else {
			if(root.indexOf("ae_") != -1){
				proc = getProcessingService().findByID(Constant.getId(root));
				proc.resetCompletedChildren();
				fillWorkflowProcessingMap(proc, wfrProc);
			}else
			if(root.indexOf("wfrs_") != -1){
				wfrs = getWorkflowRunService().findByIDWithIUS(Constant.getId(root));
				Log.info("	IUS size = " + wfrs.getIus().size());
			}/*else
			if(root.indexOf("sam_") != -1){
				sam = getSampleService().findByID(Constant.getId(root));
			}*/else{
		//	  WorkflowRun s = getWorkflowRunService().findByID(Integer.parseInt(root));
			  WorkflowRun s = getWorkflowRunService().findByIDWithIUS(Integer.parseInt(root));
			  listView.add(s);
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
		System.err.println("Workflow length: "+listAll.size());
		ModelAndView    modelAndView;
		if(root.indexOf("ae_") != -1){
			System.err.println("RENDERING INDIVIDUAL File with Processing");
			modelAndView  = new ModelAndView("StudyListFileProcessing");
			modelAndView.addObject("typeTree", "wfr");
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
		
		modelAndView.addObject("typeTree", "wfr");
		
		modelAndView.addObject("isBulkPage", false);
		modelAndView.addObject("workflowRuns", listView);
		modelAndView.addObject("registration", registration);
		
		modelAndView.addObject("workflowRun", wfrs);
		modelAndView.addObject("sample", sam);
//		modelAndView.addObject("lane", lane);
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
	
	private String getSecondId(List<String> ids){
		String id = null;
		if(ids != null){
			id = ids.get(1); 
		}
		return id;
	}
	
	private String getEndId(List<String> ids){
		String id = null;
		if(ids != null){
			id = ids.get(ids.size()-1); 
		}
		return id;
	}
	
	private List<WorkflowRun> loadNode(List<WorkflowRun> list, Registration registration, HttpServletRequest request){
		Integer openWorkflowRunId = (Integer)request.getSession(false).getAttribute("rootWorkflowRunId");
		String objId = (String)request.getSession(false).getAttribute("objectWFRId");
		
		
		List<String> listWorkflowRunNodeId = (List<String>)request.getSession(false).getAttribute("listWorkflowRunNodeId");
		String treeType = getTypeSession(request);
		
		openWorkflowRunId = null;
		objId = null;
		if(listWorkflowRunNodeId != null){
			openWorkflowRunId = Constant.getId(getEndId(listWorkflowRunNodeId));
			objId = getSecondId(listWorkflowRunNodeId);
			
			if(objId.equals("")){
				Log.info("It is ASSot Node");
				objId = "wfrs_" + openWorkflowRunId;
			}
			
			if(objId.indexOf("wfr_") != -1 && listWorkflowRunNodeId.size() > 2){
				objId = listWorkflowRunNodeId.get(2);
			}
			
			Log.info("ROOT WFR id = " + openWorkflowRunId);
			Log.info("Start POS id = " + objId);
		}
		
//		openWorkflowRunId = 4590;
//		objId = "ae_56520";
//		objId = "wfrs_4590";
//		objId = "sam_2006";
		
		if(openWorkflowRunId != null){
			Log.info("rootWorkflowRunId = " + openWorkflowRunId);
			for (WorkflowRun workflowRun : list) {
				if(openWorkflowRunId.equals(workflowRun.getWorkflowRunId())){
					if(objId.indexOf("wfr_") != -1){
						workflowRun = getWorkflowRunService().findByIDWithIUS(workflowRun.getWorkflowRunId());
						workflowRun.setHtml(WorkflowRunHtmlUtil.getHtml(workflowRun, registration, WorkflowRunHtmlUtil.TYPE_TREE_COMPLETED, false, treeType));
					}
					if(objId.indexOf("wfrs_") != -1){
						workflowRun = getWorkflowRunService().findByIDWithIUS(Constant.getId(objId));
						workflowRun.setHtml(WorkflowRunHtmlUtil.getHtml(workflowRun, registration, WorkflowRunHtmlUtil.TYPE_TREE_COMPLETED, true, treeType));
					}
			//		if(objId.indexOf("sam_") != -1){
			//			Sample sample = getSampleService().findByID(Constant.getId(objId));
			//			workflowRun = getWorkflowRunService().findByIDWithIUS(workflowRun.getWorkflowRunId());
			//			workflowRun.setHtml(WorkflowRunHtmlUtil.getHtml(sample, workflowRun, registration, WorkflowRunHtmlUtil.TYPE_TREE_COMPLETED));
			//		}
					if(objId.indexOf("ae_") != -1){
						Processing processing = getProcessingService().findByID(Constant.getId(objId));
						workflowRun = getWorkflowRunService().findByIDWithIUS(workflowRun.getWorkflowRunId());
						workflowRun.setHtml(WorkflowRunHtmlUtil.getHtml(processing, workflowRun, registration, WorkflowRunHtmlUtil.TYPE_TREE_COMPLETED, listWorkflowRunNodeId, treeType));
					}
					if(objId.indexOf("aefl_") != -1){
						Processing currObj = getProcessingService().findByID(Constant.getId(objId));
						workflowRun.setHtml(WorkflowRunHtmlUtil.getFileHtml(currObj, workflowRun, registration, WorkflowRunHtmlUtil.TYPE_TREE_COMPLETED));
					}
				}
			}
		}
		
		// test open seq
//		openStudyId = 1;
//		objId = "seq_1995";
		
		return list;
	}
	
	private String getTypeSession(HttpServletRequest request) {
		  String type = (String) request.getSession(false).getAttribute("typeList");
		  if (type == null) {
			  type = "tree";
		  }
		  return type;
	}
	
    private String getRequestedTypeList(HttpServletRequest request) {
		String	typeList = (String)request.getParameter("typeList");
		if (typeList == null){
			typeList = "";
		}
		return typeList;
	}

	public WorkflowRunService getWorkflowRunService() {
		return workflowRunService;
	}

	public void setWorkflowRunService(WorkflowRunService workflowRunService) {
		this.workflowRunService = workflowRunService;
	}

	public SampleService getSampleService() {
		return sampleService;
	}

	public void setSampleService(SampleService sampleService) {
		this.sampleService = sampleService;
	}

	public LaneService getLaneService() {
		return laneService;
	}

	public void setLaneService(LaneService laneService) {
		this.laneService = laneService;
	}

	public ProcessingService getProcessingService() {
		return processingService;
	}

	public void setProcessingService(ProcessingService processingService) {
		this.processingService = processingService;
	}
}

