package net.sourceforge.solexatools.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
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
 * <p>BulkDownloadListDetailsController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class BulkDownloadListDetailsController extends BaseCommandController {
	private StudyService studyService;
	private ExperimentService experimentService;
	private SampleService sampleService;
	private LaneService laneService;
	private ProcessingService processingService;
	private FileService fileService;
	private IUSService iusService;
	
	/**
	 * <p>Constructor for BulkDownloadListDetailsController.</p>
	 */
	public BulkDownloadListDetailsController() {
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
			return new ModelAndView("StudyListRoot");
		}

		/**
		 * Pass registration so that we can filter the list if its appropriate
		 * to do so.
		 */
		
		List<String> selectedIds = (List<String>)request.getSession(false).getAttribute("selectedNodes");
		
		Experiment exp = new Experiment();
		Sample sam = new Sample();
	//	Lane lane = new Lane();
		IUS ius = new IUS();
		Processing proc = new Processing();
		Map<WorkflowRun, List<Processing>> wfrProc = new HashMap<WorkflowRun, List<Processing>>();
		
		PageInfo pageInfo = null;
		Boolean isHasError = false;
		String errorMessage = "";
		
		List<Study>  listAll = new ArrayList<Study>();
		List<Study>  listView = new ArrayList<Study>();
		
		String   root     = (String)request.getParameter("root");
		System.err.println("ROOT: "+root);
		if (root == null || "".equals(root) || "source".equals(root)) {
			MessageSourceAccessor ma = this.getMessageSourceAccessor();
			String nameOneItem = "study.list.pagination.nameOneItem"; 
			String nameLotOfItem = "study.list.pagination.nameLotOfItem";
			String typeList = getRequestedTypeList(request);
			if(typeList.equals("mylist")){
				Boolean isAsc = saveAscInSession(request, "ascBulkDownloadMyListStudy");
				listAll = getStudyService().list(registration, isAsc);
				listView = PaginationUtil.subList(request, "myBulkDownloadPage", listAll);
				pageInfo = PaginationUtil.getPageInfo(request, "myBulkDownloadPage", listView, listAll, nameOneItem, nameLotOfItem, ma);
				 
				// set error
				if(listAll.size() == 0){
					isHasError = true;
					errorMessage = this.getMessageSourceAccessor().getMessage("study.list.required.one.item");
				}
			}
			if(typeList.equals("bymesharelist")){
				Boolean isAsc = saveAscInSession(request, "ascBulkDownloadSharedWithMeListStudy");
				listAll = getStudyService().listSharedWithMe(registration, isAsc);
				listView = PaginationUtil.subList(request, "bulkDownloadSharedWithMe", listAll);
				pageInfo = PaginationUtil.getPageInfo(request, "bulkDownloadSharedWithMe", listView, listAll, nameOneItem, nameLotOfItem, ma);
			}
			BulkUtil.selectStudyNode(selectedIds, listView); 
			listView = getStudyService().listWithHasFile(listView);
		} else {
			if(root.indexOf("ae_") != -1){
				proc = getProcessingService().findByID(Constant.getId(root));
				BulkUtil.selectProcessingNode(selectedIds, proc);
				getProcessingService().setWithHasFile(proc.getChildren());
				fillWorkflowProcessingMap(proc, wfrProc);
			}else
			if(root.indexOf("ius_") != -1){
				Integer iusID = Integer.parseInt(root.substring(root.lastIndexOf("_") + 1, root.length()));
				ius = getIUSService().findByID(iusID);
				BulkUtil.selectProcessingNode(selectedIds, ius);
				getProcessingService().setWithHasFile(ius.getProcessings());
			}else
	/*		if(root.indexOf("seq_") != -1){
				lane = getLaneService().findByID(Constant.getId(root));
				BulkUtil.selectProcessingNode(selectedIds, lane);
				getProcessingService().setWithHasFile(lane.getProcessings());
			}else
	*/		
			if(root.indexOf("sam_") != -1){
				sam = getSampleService().findByID(Constant.getId(root));
				BulkUtil.selectIUSNode(selectedIds, sam);
				getIUSService().setWithHasFile(sam.getIUS());
				getProcessingService().setWithHasFile(sam.getProcessings());
				SortedSet<Sample> children = new TreeSet<Sample>(sam.getChildren());
				getSampleService().setWithHasFile(null, children);
			}else
			if(root.indexOf("exp_") != -1){
				Integer expId = Constant.getId(root);
				exp = getExperimentService().findByID(expId);
				BulkUtil.selectSampleNode(selectedIds, exp);
				getSampleService().setWithHasFile(expId, exp.getSamples());
				getProcessingService().setWithHasFile(exp.getProcessings());
			}else
			{
				Study s = getStudyService().findByID(Integer.parseInt(root));
				listView.add(s);
				
				BulkUtil.selectExperimentNode(selectedIds, s);
				getExperimentService().setWithHasFile(s.getExperiments());
				getProcessingService().setWithHasFile(s.getProcessings());
			}
		}
		request.getSession(false).setAttribute("selectedNodes", selectedIds);
		
    System.err.println("Study Bulk Download length: "+listAll.size());
		ModelAndView    modelAndView;
		if(root.indexOf("ae_") != -1){
			System.err.println("RENDERING INDIVIDUAL File with Processing");
			modelAndView  = new ModelAndView("StudyListFileProcessing");
			modelAndView.addObject("processing", proc);
			modelAndView.addObject("wfrproc", wfrProc);
			modelAndView.addObject("wfrprockeys", wfrProc.keySet());
		//	modelAndView.addObject("typeTree", "st");
		}else
		if(root.indexOf("ius_") != -1){
			System.err.println("RENDERING INDIVIDUAL IUS");
			modelAndView  = new ModelAndView("StudyListProcessing");
			modelAndView.addObject("ius", ius);
		}else
/*		if(root.indexOf("seq_") != -1){
			System.err.println("RENDERING INDIVIDUAL Processing");
			modelAndView  = new ModelAndView("StudyListProcessing");
			modelAndView.addObject("lane", lane);
		//	modelAndView.addObject("typeTree", "st");
		}else
*/		
		if(root.indexOf("sam_") != -1){
			System.err.println("RENDERING INDIVIDUAL Sample");
			modelAndView  = new ModelAndView("StudyListIUS");
			modelAndView.addObject("sample", sam);
		}else
		if(root.indexOf("exp_") != -1){
			System.err.println("RENDERING INDIVIDUAL EXPERIMENT");
			modelAndView  = new ModelAndView("StudyListSample");
			modelAndView.addObject("experiment", exp);
		}else
		if (root != null && !"".equals(root) && !"source".equals(root) && Integer.parseInt(root) > 0) {
		  System.err.println("RENDERING INDIVIDUAL BULK DOWNLOAD STUDY");
		  modelAndView  = new ModelAndView("StudyListDetails");
          modelAndView.addObject("root", root);
		} else {
		  System.err.println("RENDERING ALL BULK DOWNLOAD STUDIES");
		  modelAndView	= new ModelAndView("StudyListRoot");
		  modelAndView.addObject("pageInfo", pageInfo);		  
		}
		
		// set error data
		modelAndView.addObject("isHasError", isHasError);
		modelAndView.addObject("errorMessage", errorMessage);
		
		modelAndView.addObject("isBulkPage", true);
		modelAndView.addObject("studies", listView);
		modelAndView.addObject("registration", registration);
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
	  String typeList = (String)request.getParameter("typeList");
	  if (typeList == null){
		  typeList = "";
	  }
	  return typeList;
  }

  /**
   * <p>Getter for the field <code>studyService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.StudyService} object.
   */
  public StudyService getStudyService() {
    return studyService;
  }

  /**
   * <p>Setter for the field <code>studyService</code>.</p>
   *
   * @param studyService a {@link net.sourceforge.seqware.common.business.StudyService} object.
   */
  public void setStudyService(StudyService studyService) {
    this.studyService = studyService;
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

  /**
   * <p>Getter for the field <code>fileService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.FileService} object.
   */
  public FileService getFileService() {
	return fileService;
  }

  /**
   * <p>Setter for the field <code>fileService</code>.</p>
   *
   * @param fileService a {@link net.sourceforge.seqware.common.business.FileService} object.
   */
  public void setFileService(FileService fileService) {
	this.fileService = fileService;
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
