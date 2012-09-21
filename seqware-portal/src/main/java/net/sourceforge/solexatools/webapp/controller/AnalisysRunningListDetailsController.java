package net.sourceforge.solexatools.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.Constant;
import net.sourceforge.solexatools.util.PageInfo;
import net.sourceforge.solexatools.util.PaginationUtil;
import net.sourceforge.solexatools.util.WorkflowRunHtmlUtil;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

public class AnalisysRunningListDetailsController extends BaseCommandController {
	private WorkflowRunService workflowRunService;
	private SampleService sampleService;
	private LaneService laneService;
	private ProcessingService processingService;
	
	public AnalisysRunningListDetailsController() {
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
		Lane lane = new Lane();
		Processing proc = new Processing();
		Map<WorkflowRun, Set<Processing>> wfrProc = new HashMap<WorkflowRun, Set<Processing>>();
		
		//List<Workflow>  list;
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
			if(typeList.equals("runninglist")){
				 Log.info("GET RUNNING TREE");
				 Boolean isAsc = saveAscInSession(request, "ascMyRunningListAnalysis");
				 listAll = getWorkflowRunService().listRunning(registration, isAsc);
				 listView = PaginationUtil.subList(request, "runningAnalisysesPage", listAll);
				 Log.info("ALL RUNNING SIZE=" + listAll.size());
				 listView = loadNode(listView, registration, request);
				 Log.info("VIEW RUNNING SIZE=" + listAll.size());
				 pageInfo = PaginationUtil.getPageInfo(request, "runningAnalisysesPage", listView, listAll, nameOneItem, nameLotOfItem, ma);
			}
		} else {
			if(root.indexOf("ae_") != -1){
				proc = getProcessingService().findByID(Constant.getId(root));
				proc.resetRunningChildren();
				fillWorkflowProcessingMap(proc, wfrProc);
			//	proc = getProcessingService().findByIDOnlyWithRunningWR(Constant.getId(root));
			}else
			if(root.indexOf("wfrs_") != -1){
			//	wfrs = getWorkflowRunService().findByIDWithSample(Constant.getId(root));
				wfrs = getWorkflowRunService().findByIDWithIUSAndRunningWR(Constant.getId(root));
			}else
			{
			//WorkflowRun s = getWorkflowRunService().findByIDWithSample(Integer.parseInt(root));
			  WorkflowRun s = getWorkflowRunService().findByIDWithIUSAndRunningWR(Integer.parseInt(root));
			  listView.add(s);
			  Log.info("listView SIZE = " + listView.size());
			}
		}
		System.err.println("Workflow length: "+listAll.size());
		ModelAndView    modelAndView;
		if(root.indexOf("ae_") != -1){
			System.err.println("RENDERING INDIVIDUAL File with Processing");
			modelAndView  = new ModelAndView("StudyListFileProcessing");
			modelAndView.addObject("root", root);
			modelAndView.addObject("wfrproc", wfrProc);
			modelAndView.addObject("wfrprockeys", wfrProc.keySet());
		}else
		if(root.indexOf("wfrs_") != -1){
			System.err.println("RENDERING INDIVIDUAL WorkflowRun RUNNING with IUS");
			modelAndView  = new ModelAndView("AnalisysListIUS");
		}else
/*		if(root.indexOf("sam_") != -1){
			System.err.println("RENDERING INDIVIDUAL Sample");
			modelAndView  = new ModelAndView("AnalisysListLane");
			modelAndView.addObject("root", root);
		}else
*/
		if (root != null && !"".equals(root) && !"source".equals(root) && Integer.parseInt(root) > 0) {
		  System.err.println("RENDERING INDIVIDUAL RUNNING WorkflowRun");
		  modelAndView  = new ModelAndView("AnalisysListDetails");
		  modelAndView.addObject("root", root);
		} else {
		  System.err.println("RENDERING ALL RUNNING WORKFLOWRUNS");
		  modelAndView	= new ModelAndView("AnalisysListRoot");
		  modelAndView.addObject("pageInfo", pageInfo);
		}
		
		// set error data
		modelAndView.addObject("isHasError", isHasError);
		modelAndView.addObject("errorMessage", errorMessage);
		
		modelAndView.addObject("typeTree", "wfrr");
		
		modelAndView.addObject("isBulkPage", false);
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
			Map<WorkflowRun, Set<Processing>> wfrProc) {
		for (Processing child : proc.getChildren()) {
			Set<Processing> processings = wfrProc.get(child.getWorkflowRun());
			if (processings == null) {
				processings = new HashSet<Processing>();
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
		Integer openWorkflowRunId = (Integer)request.getSession(false).getAttribute("rootWorkflowRunRunningId");
		String objId = (String)request.getSession(false).getAttribute("objectWFRRId");
		
		List<String> listWorkflowRunNodeId = (List<String>)request.getSession(false).getAttribute("listWorkflowRunRunningNodeId");
		
	//	openWorkflowRunId = 4653;
	//	objId = "ae_56668";
	//	objId = "wfrs_4590";
	//	objId = "sam_2006";
		
		openWorkflowRunId = null;
		objId = null;
		if(listWorkflowRunNodeId != null){
			openWorkflowRunId = Constant.getId(getEndId(listWorkflowRunNodeId));
			objId = getSecondId(listWorkflowRunNodeId);
			
			if(objId.equals("")){
				Log.info("It is ASS Node");
				objId = "wfrs_" + openWorkflowRunId;
			}
			
			if(objId.indexOf("wfr_") != -1 && listWorkflowRunNodeId.size() > 2){
				objId = listWorkflowRunNodeId.get(2);
			}
		}
		
		String treeType = getTypeSession(request);
		
		Log.info("ROOT ID RUNNING =" + openWorkflowRunId);
		Log.info("OBJ  ID RUNNING =" + objId);
		
		if(openWorkflowRunId != null){
			Log.info("rootWorkflowRunRunningId = " + openWorkflowRunId);
			for (WorkflowRun workflowRun : list) {
				if(openWorkflowRunId.equals(workflowRun.getWorkflowRunId())){
					if(objId.indexOf("wfr_") != -1){
						workflowRun.setHtml(WorkflowRunHtmlUtil.getHtml(workflowRun, registration, WorkflowRunHtmlUtil.TYPE_TREE_RUNNNING, false, treeType));
					}
					if(objId.indexOf("wfrs_") != -1){
						workflowRun = getWorkflowRunService().findByIDWithIUSAndRunningWR(Constant.getId(objId));
						workflowRun.setHtml(WorkflowRunHtmlUtil.getHtml(workflowRun, registration, WorkflowRunHtmlUtil.TYPE_TREE_RUNNNING, true, treeType));
					}
					if(objId.indexOf("sam_") != -1){
						Sample sample = getSampleService().findByID(Constant.getId(objId));
						workflowRun = getWorkflowRunService().findByIDWithIUSAndRunningWR(workflowRun.getWorkflowRunId());
						workflowRun.setHtml(WorkflowRunHtmlUtil.getHtml(sample, workflowRun, registration, WorkflowRunHtmlUtil.TYPE_TREE_RUNNNING));
					}
					if(objId.indexOf("ae_") != -1){
						Processing processing = getProcessingService().findByID(Constant.getId(objId));
						workflowRun = getWorkflowRunService().findByIDWithIUSAndRunningWR(workflowRun.getWorkflowRunId());
						workflowRun.setHtml(WorkflowRunHtmlUtil.getHtml(processing, workflowRun, registration, WorkflowRunHtmlUtil.TYPE_TREE_RUNNNING, listWorkflowRunNodeId, treeType));
					}
					if(objId.indexOf("aefl_") != -1){
						Processing currObj = getProcessingService().findByID(Constant.getId(objId));
						workflowRun.setHtml(WorkflowRunHtmlUtil.getFileHtml(currObj, workflowRun, registration, WorkflowRunHtmlUtil.TYPE_TREE_RUNNNING));
					}
				}
			}
		}
		
		// test open seq
	//	openStudyId = 1;
	//	objId = "seq_1995";
		
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

