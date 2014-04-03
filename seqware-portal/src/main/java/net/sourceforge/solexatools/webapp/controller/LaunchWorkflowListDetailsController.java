package net.sourceforge.solexatools.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.BulkUtil;
import net.sourceforge.solexatools.util.Constant;
import net.sourceforge.solexatools.util.LaunchWorkflowUtil;
import net.sourceforge.solexatools.util.PageInfo;
import net.sourceforge.solexatools.util.PaginationUtil;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>LaunchWorkflowListDetailsController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LaunchWorkflowListDetailsController extends BaseCommandController {
	private StudyService studyService;
	private ExperimentService experimentService;
	private SampleService sampleService;
	private LaneService laneService;
	private IUSService iusService;
	private ProcessingService processingService;
	private FileService fileService;

	/**
	 * <p>Constructor for LaunchWorkflowListDetailsController.</p>
	 */
	public LaunchWorkflowListDetailsController() {
		super();
		setSupportedMethods(new String[] { METHOD_GET });
	}

	private Boolean getRequestedAsc(HttpServletRequest request) {
		Boolean isAsc = null;
		String strAsc = request.getParameter("asc");

		if ("true".equals(strAsc)) {
			isAsc = true;
		} else if ("false".equals(strAsc)) {
			isAsc = false;
		}
		return isAsc;
	}

	private Boolean saveAscInSession(HttpServletRequest request,
			String attrNameInSession) {
		Boolean isAsc = getRequestedAsc(request);
		if (isAsc != null) {
			request.getSession(false).setAttribute(attrNameInSession, isAsc);
		}
		return isAsc(request, attrNameInSession);
	}

	private Boolean isAsc(HttpServletRequest request, String attrNameInSession) {
		Boolean isAsc = (Boolean) request.getSession(false).getAttribute(
				attrNameInSession);
		if (isAsc == null) {
			isAsc = true;
		}
		return isAsc;
	}

	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Registration registration = Security.getRegistration(request);
		if (registration == null)
			return new ModelAndView("redirect:/login.htm");

		/**
		 * Pass registration so that we can filter the list if its appropriate
		 * to do so.
		 */

		// List<String> selectedIds =
		// (List<String>)request.getSession(false).getAttribute("launchSelectedNodes");
		List<String> selectedIds = LaunchWorkflowUtil
				.getCurrentSelectedNodes(request);

		String metaType = getCurrentWorkflowParam(request).getFileMetaType();

		Experiment exp = new Experiment();
		Sample sam = new Sample();
		Lane lane = new Lane();
		IUS ius = new IUS();
		Processing proc = new Processing();
		Map<WorkflowRun, Set<Processing>> wfrProc = new HashMap<WorkflowRun, Set<Processing>>();

		PageInfo pageInfo = null;
		Boolean isHasError = false;
		String errorMessage = "";

		List<Study> listAll = new ArrayList<Study>();
		List<Study> listView = new ArrayList<Study>();

		String root = (String) request.getParameter("root");
		System.err.println("ROOT: " + root);
		if (root == null || "".equals(root) || "source".equals(root)) {
			MessageSourceAccessor ma = this.getMessageSourceAccessor();
			String nameOneItem = "study.list.pagination.nameOneItem";
			String nameLotOfItem = "study.list.pagination.nameLotOfItem";
			String typeList = getRequestedTypeList(request);
			if (typeList.equals("mylist")) {
				// listAll = getStudyService().list(registration);

				// set error if want
				if (getStudyService().list(registration).size() == 0) {
					isHasError = true;
					errorMessage = this.getMessageSourceAccessor().getMessage(
							"study.list.required.one.item");
				}

				// get study which has files needed META TYPE
				// listAll = getStudyService().listWithHasFile(listAll,
				// metaType);
				Boolean isAsc = saveAscInSession(request, "ascLaunchListStudy");
				listAll = getStudyService().listStudyHasFile(registration,
						metaType, isAsc);

				// set error if want
				if (!isHasError && listAll.size() == 0) {
					isHasError = true;
					errorMessage = this.getMessageSourceAccessor().getMessage(
							"launchWorkflow.list.no.item",
							new Object[] { metaType });
				}

				listView = PaginationUtil.subList(request, "mySelectInputPage",
						listAll);
				pageInfo = PaginationUtil.getPageInfo(request,
						"mySelectInputPage", listView, listAll, nameOneItem,
						nameLotOfItem, ma);

			}
			// listView = getStudyService().listWithHasFile(listView, metaType);
			BulkUtil.selectStudyNode(selectedIds, listView);
		} else {
			if (root.indexOf("ae_") != -1) {
				proc = getProcessingService().findByID(Constant.getId(root));
				proc.setChildren(getProcessingService().setWithHasFile(
						proc.getChildren(), metaType));
				proc.setFiles(getFileService().setWithHasFile(proc.getFiles(),
						metaType));
				BulkUtil.selectProcessingNode(selectedIds, proc);
				fillWorkflowProcessingMap(proc, wfrProc);
			} else if (root.indexOf("seq_") != -1) {
				lane = getLaneService().findByID(Constant.getId(root));
				lane.setProcessingsForView(getProcessingService()
						.setWithHasFile(lane.getProcessings(), metaType));
				BulkUtil.selectProcessingNode(selectedIds, lane);
			} else if (root.indexOf("ius_") != -1) {
				ius = getIUSService().findByID(Constant.getId(root));
				ius.setProcessings(getProcessingService().setWithHasFile(
						ius.getProcessings(), metaType));
				BulkUtil.selectProcessingNode(selectedIds, ius);
			} else if (root.indexOf("sam_") != -1) {
				sam = getSampleService().findByID(Constant.getId(root));
				SortedSet<IUS> iuss = getIUSService().listWithHasFile(
						sam.getIUS(), metaType);
				// Log.info("Lanes SIZE1 = " + lanes.size());

				// sam.setLanesForView(lanes);
				sam.setIUS(iuss);
				sam.setProcessings(getProcessingService().setWithHasFile(
						sam.getProcessings(), metaType));

				SortedSet<Sample> children = new TreeSet<Sample>(
						sam.getChildren());
				getSampleService().setWithHasFile(null, children);
				// Log.info("Lanes SIZE2 = " + sam.getLanes().size());

				BulkUtil.selectIUSNode(selectedIds, sam);
			} else if (root.indexOf("exp_") != -1) {
				Integer expId = Constant.getId(root);
				exp = getExperimentService().findByID(expId);
				exp.setSamples(getSampleService().listWithHasFile(expId,
						exp.getSamples(), metaType));
				exp.setProcessings(getProcessingService().setWithHasFile(
						exp.getProcessings(), metaType));
				BulkUtil.selectSampleNode(selectedIds, exp);
			} else {
				Study s = getStudyService().findByID(Integer.parseInt(root));
				listView.add(s);
				s.setExperiments(getExperimentService().listWithHasFile(
						s.getExperiments(), metaType));
				s.setProcessings(getProcessingService().setWithHasFile(
						s.getProcessings(), metaType));
				BulkUtil.selectExperimentNode(selectedIds, s);
			}
		}
		// request.getSession(false).setAttribute("launchSelectedNodes",
		// selectedIds);
		LaunchWorkflowUtil.setCurrentSelectedNodes(request, selectedIds);

		ModelAndView modelAndView;
		if (root.indexOf("ae_") != -1) {
			System.err.println("RENDERING INDIVIDUAL File with Processing");
			modelAndView = new ModelAndView("StudyListFileProcessing");
			modelAndView.addObject("processing", proc);
			modelAndView.addObject("wfrproc", wfrProc);
			modelAndView.addObject("wfrprockeys", wfrProc.keySet());
		} else if (root.indexOf("seq_") != -1) {
			System.err.println("RENDERING INDIVIDUAL Processing");
			modelAndView = new ModelAndView("StudyListProcessing");
			modelAndView.addObject("lane", lane);
		} else if (root.indexOf("ius_") != -1) {
			System.err.println("RENDERING INDIVIDUAL IUS");
			modelAndView = new ModelAndView("StudyListProcessing");
			modelAndView.addObject("ius", ius);
		} else if (root.indexOf("sam_") != -1) {
			System.err.println("RENDERING INDIVIDUAL Sample");
			modelAndView = new ModelAndView("StudyListIUS");
			modelAndView.addObject("sample", sam);
		} else if (root.indexOf("exp_") != -1) {
			System.err.println("RENDERING INDIVIDUAL EXPERIMENT");
			modelAndView = new ModelAndView("StudyListSample");
			modelAndView.addObject("experiment", exp);
		} else if (root != null && !"".equals(root) && !"source".equals(root)
				&& Integer.parseInt(root) > 0) {
			System.err.println("RENDERING INDIVIDUAL SELECT INPUT STUDY");
			modelAndView = new ModelAndView("StudyListDetails");
			modelAndView.addObject("root", root);
		} else {
			System.err.println("RENDERING ALL SELECT INPUT STUDIES");
			modelAndView = new ModelAndView("StudyListRoot");
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

	private WorkflowParam getCurrentWorkflowParam(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object workflowParam = session.getAttribute("workflowParam");
			if (workflowParam != null) {
				return (WorkflowParam) workflowParam;
			}
		}
		return new WorkflowParam();
	}

	private String getRequestedTypeList(HttpServletRequest request) {
		String typeList = (String) request.getParameter("typeList");
		if (typeList == null) {
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
