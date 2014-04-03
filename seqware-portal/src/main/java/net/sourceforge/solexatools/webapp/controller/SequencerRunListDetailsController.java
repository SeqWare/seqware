package	net.sourceforge.solexatools.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.Constant;
import net.sourceforge.solexatools.util.PageInfo;
import net.sourceforge.solexatools.util.PaginationUtil;
import net.sourceforge.solexatools.util.SequencerRunHtmlUtil;

import org.apache.log4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;


/**
 * <p>SequencerRunListDetailsController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SequencerRunListDetailsController extends BaseCommandController {
	private SequencerRunService sequencerRunService;
	private LaneService laneService;
	private ProcessingService processingService;
	private IUSService iusService;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * <p>Constructor for SequencerRunListDetailsController.</p>
	 */
	public SequencerRunListDetailsController() {
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
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
												 HttpServletResponse response)
		throws Exception {

		//Registration registration = Security.requireRegistration(request, response);
		Registration registration = Security.getRegistration(request);
		if(registration == null){
		//	return new ModelAndView("redirect:/login.htm");
			return new ModelAndView("SequencerRunListRoot");
		}

		/**
		 * Pass registration so that we can filter the list if its appropriate
		 * to do so.
		 */
		
		String type = getTypeParameter(request);
		if (type != null){
			setTypeSession(request, type);
		}

		SequencerRun sr = new SequencerRun();
		Lane lane = new Lane();
		IUS ius = new IUS();
		Processing proc = new Processing();
		Map<WorkflowRun, List<Processing>> wfrProc = new HashMap<WorkflowRun, List<Processing>>();
		
		PageInfo pageInfo = null;
		Boolean isHasError = false;
		String errorMessage = "";
		
		List<SequencerRun>  listAll = new ArrayList<SequencerRun>();
		List<SequencerRun>  listView = new ArrayList<SequencerRun>();
		String   root     = (String)request.getParameter("root");
		System.err.println("ROOT: "+root);
		if (root == null || "".equals(root) || "source".equals(root)) {
			MessageSourceAccessor ma = this.getMessageSourceAccessor();
			String nameOneItem = "sequencerRun.list.pagination.nameOneItem"; 
			String nameLotOfItem = "sequencerRun.list.pagination.nameLotOfItem";
			String typeList = getRequestedTypeList(request);
			if(typeList.equals("mylist")){
				Boolean isAsc = saveAscInSession(request, "ascMyListSequencerRun");
				listAll = getSequencerRunService().list(registration, isAsc);
				listView = PaginationUtil.subList(request, "mySequencerPage", listAll);
				listView = getSequencerRunService().setProcCountInfo(listView);
				listView = loadNode(listView, registration, request);
				pageInfo = PaginationUtil.getPageInfo(request, "mySequencerPage", listView, listAll,  nameOneItem, nameLotOfItem, ma);
			}
		} else {
			if(root.indexOf("ae_") != -1){
				proc = getProcessingService().findByID(Constant.getId(root));
				fillWorkflowProcessingMap(proc, wfrProc);
			}else
			if(root.indexOf("ius_") != -1){
				ius = getIUSService().findByID(Constant.getId(root));
			}else
			if(root.indexOf("seq_") != -1){
				lane = getLaneService().findByID(Constant.getId(root));
			}else{
				sr = getSequencerRunService().findByID(Integer.parseInt(root));
			}
		}
		
		ModelAndView    modelAndView;
		if(root.indexOf("ae_") != -1){
			System.err.println("RENDERING INDIVIDUAL File with Processing");
			modelAndView  = new ModelAndView("StudyListFileProcessing");
			modelAndView.addObject("processing", proc);
			modelAndView.addObject("wfrproc", wfrProc);
			modelAndView.addObject("wfrprockeys", wfrProc.keySet());
		//	modelAndView.addObject("typeTree", "sr");
		}else
		if(root.indexOf("ius_") != -1){
			System.err.println("RENDERING INDIVIDUAL IUS");
			modelAndView  = new ModelAndView("StudyListProcessing");
			modelAndView.addObject("ius", ius);
			modelAndView.addObject("tab", "seqrun");
		//	modelAndView.addObject("typeTree", "sr");
		}else
		if(root.indexOf("seq_") != -1){
			System.err.println("RENDERING INDIVIDUAL LANE");
			modelAndView  = new ModelAndView("SequencerRunListIUS");
			modelAndView.addObject("lane", lane);
		//	modelAndView.addObject("typeTree", "sr");
		}else
		if (root != null && !"".equals(root) && !"source".equals(root) && Integer.parseInt(root) > 0) {
		  System.err.println("RENDERING INDIVIDUAL FLOWCELLS");
		  modelAndView  = new ModelAndView("SequencerRunListLane");
		  modelAndView.addObject("sequencerRun", sr);
		} else {
		  System.err.println("RENDERING ALL FLOWCELLS");
		  modelAndView	= new ModelAndView("SequencerRunListRoot");
		  modelAndView.addObject("pageInfo", pageInfo);
		  if(listAll.size() == 0){
			  isHasError = true;
			  errorMessage = this.getMessageSourceAccessor().getMessage("sequencerRun.list.required.one.item");
		  }
		  
	//	  modelAndView.addObject("typeTree", "sr");
		  modelAndView.addObject("isHasError", isHasError);
		  modelAndView.addObject("errorMessage", errorMessage);
		}
		
		modelAndView.addObject("typeTree", "sr");
		modelAndView.addObject("sequencerRuns", listView);
		modelAndView.addObject("registration", registration);
		modelAndView.addObject("typeList", getTypeSession(request));
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
	
	private List<SequencerRun> loadNode(List<SequencerRun> list, Registration registration, HttpServletRequest request){
		Integer openSequencerRunId = (Integer)request.getSession(false).getAttribute("rootSequencerRunId");
		//Object obj = request.getSession(false).getAttribute("nodeObject");
		String objId = (String)request.getSession(false).getAttribute("objectSRId");
		
		List<String> listSequencerRunNodeId = (List<String>)request.getSession(false).getAttribute("listSequencerRunNodeId");
		
		openSequencerRunId = null;
		objId = null;
		if(listSequencerRunNodeId != null){
			Log.info("End Study id = " + Constant.getId(getEndId(listSequencerRunNodeId)));
			Log.info("Start  id = " + getSecondId(listSequencerRunNodeId));
			openSequencerRunId = Constant.getId(getEndId(listSequencerRunNodeId));
			objId = getSecondId(listSequencerRunNodeId);
			
			if(objId.indexOf("wfr_") != -1){
				objId = listSequencerRunNodeId.get(2);
			}
		}
		
		// test open seq
//		openStudyId = 1;
//		objId = "seq_1995";
//		objId = "ae_53883";
//		objId = "ae_53851";
//		openSequencerRunId = 196;
//		objId = "ae_56567";
		String treeType = getTypeSession(request);
		
		if(openSequencerRunId != null){
			Log.info("rootSequencerRunId = " + openSequencerRunId);
			for (SequencerRun sequencerRun : list) {
				if(openSequencerRunId.equals(sequencerRun.getSequencerRunId())){
					//study.setHtml(TreeNodeHtmlUtil.getHtml(obj, registration));
					if(objId.indexOf("sr_") != -1){
						//Integer id = Integer.parseInt(objId);
						sequencerRun.setHtml(SequencerRunHtmlUtil.getHtml(sequencerRun, registration, listSequencerRunNodeId, treeType));
					}
					if(objId.indexOf("seq_") != -1){
						Lane currObj = getLaneService().findByID(Constant.getId(objId));
						sequencerRun.setHtml(SequencerRunHtmlUtil.getHtml(currObj, registration, listSequencerRunNodeId, treeType));
					}
					if(objId.indexOf("ius_") != -1){
						Log.info("ius call, obj id = " + objId );
						IUS currObj = getIUSService().findByID(Constant.getId(objId));
						sequencerRun.setHtml(SequencerRunHtmlUtil.getHtml(currObj, registration, listSequencerRunNodeId, treeType));
					}
					if(objId.indexOf("ae_") != -1){
						Processing currObj = getProcessingService().findByID(Constant.getId(objId));
						sequencerRun.setHtml(SequencerRunHtmlUtil.getHtml(currObj, registration, listSequencerRunNodeId, treeType));
					}
					if(objId.indexOf("aefl_") != -1){
						Processing currObj = getProcessingService().findByID(Constant.getId(objId));
						sequencerRun.setHtml(SequencerRunHtmlUtil.getFileHtml(currObj, registration));
					}
				}
			}
		}
		return list;
	}
	
	private String getTypeParameter(HttpServletRequest request) {
		String	type = (String)request.getParameter("type");
		return type;
	}
	
	private void setTypeSession(HttpServletRequest request, String type) {
		request.getSession(false).setAttribute("typeList", type);
	}
	
	private String getTypeSession(HttpServletRequest request) {
		if (request.getSession(false).getAttribute("typeList") == null) {
			return "tree";
		}
		String type = (String) request.getSession(false).getAttribute("typeList");
		return type;
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
	
    private String getRequestedTypeList(HttpServletRequest request) {
		String	typeList = (String)request.getParameter("typeList");
		if (typeList == null){
			typeList = "";
		}
		return typeList;
	}

	/**
	 * <p>Getter for the field <code>sequencerRunService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.SequencerRunService} object.
	 */
	public SequencerRunService getSequencerRunService() {
		return sequencerRunService;
	}

	/**
	 * <p>Setter for the field <code>sequencerRunService</code>.</p>
	 *
	 * @param sequencerRunService a {@link net.sourceforge.seqware.common.business.SequencerRunService} object.
	 */
	public void setSequencerRunService(SequencerRunService sequencerRunService) {
		this.sequencerRunService = sequencerRunService;
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
	/**
	 * <p>Getter for the field <code>iusService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.IUSService} object.
	 */
	public IUSService getIusService() {
		return iusService;
	}

	/**
	 * <p>Setter for the field <code>iusService</code>.</p>
	 *
	 * @param iusService a {@link net.sourceforge.seqware.common.business.IUSService} object.
	 */
	public void setIusService(IUSService iusService) {
		this.iusService = iusService;
	}
			  
	/**
	 * <p>getIUSService.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.IUSService} object.
	 */
	public IUSService getIUSService() {
		return iusService;
	}

	/**
	 * <p>setIUSService.</p>
	 *
	 * @param iusService a {@link net.sourceforge.seqware.common.business.IUSService} object.
	 */
	public void setIUSService(IUSService iusService) {
		this.iusService = iusService;
	}
}
