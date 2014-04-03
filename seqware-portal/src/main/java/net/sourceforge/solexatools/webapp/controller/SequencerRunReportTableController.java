package net.sourceforge.solexatools.webapp.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.FileReportService;
import net.sourceforge.seqware.common.business.SampleReportService;
import net.sourceforge.seqware.common.business.SampleReportService.Status;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.model.FileReportRow;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunReportId;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.webapp.metamodel.Flexigrid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import net.sourceforge.seqware.common.util.Log;

@Controller
/**
 * <p>SequencerRunReportTableController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SequencerRunReportTableController implements Serializable {

  private static final long serialVersionUID = 1L;
  /** Constant <code>SEQ_RUN_ID="seq_run_id"</code> */
  public final static String SEQ_RUN_ID = "seq_run_id";
  /** Constant <code>SORT_NAME="sortname"</code> */
  public final static String SORT_NAME = "sortname";
  /** Constant <code>SORT_ORDER="sortorder"</code> */
  public final static String SORT_ORDER = "sortorder";
  /** Constant <code>SORT_ORDER_ASC="asc"</code> */
  public final static String SORT_ORDER_ASC = "asc";
  /** Constant <code>SORT_ORDER_DESC="desc"</code> */
  public final static String SORT_ORDER_DESC = "desc";
  /** Constant <code>PAGE_NUM="page"</code> */
  public final static String PAGE_NUM = "page";
  /** Constant <code>ROWS_PER_PAGE="rp"</code> */
  public final static String ROWS_PER_PAGE = "rp";
  /** Constant <code>TABLE_SEL="tablesel"</code> */
  public final static String TABLE_SEL = "tablesel";
  /** Constant <code>TABLE_MODEL="tablemodel"</code> */
  public final static String TABLE_MODEL = "tablemodel";
  /** Constant <code>CSV_TYPE="csvtype"</code> */
  public final static String CSV_TYPE = "csvtype";
  /** Constant <code>CHECK="check"</code> */
  public final static String CHECK = "check";

  // Sortfields
  /** Constant <code>FILE_SAMPLE="f_sample"</code> */
  public final static String FILE_SAMPLE = "f_sample";
  /** Constant <code>FILE_TISSUE="f_tissue"</code> */
  public final static String FILE_TISSUE = "f_tissue";
  /** Constant <code>FILE_LIBRARY="f_library"</code> */
  public final static String FILE_LIBRARY = "f_library";
  /** Constant <code>FILE_TEMPLATE="f_template"</code> */
  public final static String FILE_TEMPLATE = "f_template";
  /** Constant <code>FILE_IUS="f_ius"</code> */
  public final static String FILE_IUS = "f_ius";
  /** Constant <code>FILE_LANE="f_lane"</code> */
  public final static String FILE_LANE = "f_lane";
  /** Constant <code>FILE_FILE="f_file"</code> */
  public final static String FILE_FILE = "f_file";

  // New sort fields
  /** Constant <code>FILE_STUDY_TITLE="f_study_title"</code> */
  public final static String FILE_STUDY_TITLE = "f_study_title";
  /** Constant <code>FILE_STUDY_SWID="f_study_swid"</code> */
  public final static String FILE_STUDY_SWID = "f_study_swid";
  /** Constant <code>FILE_EXPERIMENT_NAME="f_exp_name"</code> */
  public final static String FILE_EXPERIMENT_NAME = "f_exp_name";
  /** Constant <code>FILE_EXPERIMENT_SWID="f_exp_swid"</code> */
  public final static String FILE_EXPERIMENT_SWID = "f_exp_swid";
  /** Constant <code>FILE_PARENT_SAMPLE_NAME="f_sample_name"</code> */
  public final static String FILE_PARENT_SAMPLE_NAME = "f_sample_name";
  /** Constant <code>FILE_PARENT_SAMPLE_SWID="f_sample_swid"</code> */
  public final static String FILE_PARENT_SAMPLE_SWID = "f_sample_swid";
  /** Constant <code>FILE_SAMPLE_NAME="f_child_sample_name"</code> */
  public final static String FILE_SAMPLE_NAME = "f_child_sample_name";
  /** Constant <code>FILE_SAMPLE_SWID="f_child_sample_swid"</code> */
  public final static String FILE_SAMPLE_SWID = "f_child_sample_swid";
  /** Constant <code>FILE_SEQUENCER_NAME="f_seqrun_name"</code> */
  public final static String FILE_SEQUENCER_NAME = "f_seqrun_name";
  /** Constant <code>FILE_SEQUENCER_SWID="f_seqrun_swid"</code> */
  public final static String FILE_SEQUENCER_SWID = "f_seqrun_swid";
  /** Constant <code>FILE_LANE_NAME="f_lane_name"</code> */
  public final static String FILE_LANE_NAME = "f_lane_name";
  /** Constant <code>FILE_LANE_NUMBER="f_lane_num"</code> */
  public final static String FILE_LANE_NUMBER = "f_lane_num";
  /** Constant <code>FILE_LANE_SWID="f_lane_swid"</code> */
  public final static String FILE_LANE_SWID = "f_lane_swid";
  /** Constant <code>FILE_IUS_NAME="f_ius_name"</code> */
  public final static String FILE_IUS_NAME = "f_ius_name";
  /** Constant <code>FILE_IUS_TAG="f_ius_tag"</code> */
  public final static String FILE_IUS_TAG = "f_ius_tag";
  /** Constant <code>FILE_IUS_SWID="f_ius_swid"</code> */
  public final static String FILE_IUS_SWID = "f_ius_swid";
  /** Constant <code>FILE_WF_NAME="f_wf_name"</code> */
  public final static String FILE_WF_NAME = "f_wf_name";
  /** Constant <code>FILE_WF_VERSION="f_wf_version"</code> */
  public final static String FILE_WF_VERSION = "f_wf_version";
  /** Constant <code>FILE_WF_SWID="f_wf_swid"</code> */
  public final static String FILE_WF_SWID = "f_wf_swid";
  /** Constant <code>FILE_RUN_NAME="f_run_name"</code> */
  public final static String FILE_RUN_NAME = "f_run_name";
  /** Constant <code>FILE_RUN_SWID="f_run_swid"</code> */
  public final static String FILE_RUN_SWID = "f_run_swid";
  /** Constant <code>FILE_PROCESSING_ALG="f_processing_alg"</code> */
  public final static String FILE_PROCESSING_ALG = "f_processing_alg";
  /** Constant <code>FILE_PROCESSING_SWID="f_processing_swid"</code> */
  public final static String FILE_PROCESSING_SWID = "f_processing_swid";
  /** Constant <code>FILE_META_TYPE="f_file_meta"</code> */
  public final static String FILE_META_TYPE = "f_file_meta";
  /** Constant <code>FILE_SWID="f_file_swid"</code> */
  public final static String FILE_SWID = "f_file_swid";
  /** Constant <code>FILE_PATH="f_file_path"</code> */
  public final static String FILE_PATH = "f_file_path";
  private static final String SAMPLE_SEQUENCER_NAME = "s_seqrun_name";

  // Table model data
  private final static int WIDTH = 100;
  private static final String INIT = "init";

  private String orderFieldSeqRun;
  private String orderTypeSeqRun;
  private String orderFieldFile;
  private String orderTypeFile;

  private boolean isProgressDownloaded;
  private boolean isFileDownloaded;

  @Autowired
  private SequencerRunService sequencerRunService;
  @Autowired
  private SampleReportService sampleReportService;
  @Autowired
  private FileReportService fileReportService;

  private List<Workflow> workflows;

  /**
   * <p>Setter for the field <code>sequencerRunService</code>.</p>
   *
   * @param service a {@link net.sourceforge.seqware.common.business.SequencerRunService} object.
   */
  public void setSequencerRunService(SequencerRunService service) {
    this.sequencerRunService = service;
  }

  /**
   * <p>Setter for the field <code>sampleReportService</code>.</p>
   *
   * @param service a {@link net.sourceforge.seqware.common.business.SampleReportService} object.
   */
  public void setSampleReportService(SampleReportService service) {
    this.sampleReportService = service;
  }

  /**
   * <p>Setter for the field <code>fileReportService</code>.</p>
   *
   * @param service a {@link net.sourceforge.seqware.common.business.FileReportService} object.
   */
  public void setFileReportService(FileReportService service) {
    this.fileReportService = service;
  }

  /**
   * <p>doCreateTableJson.</p>
   *
   * @param request a {@link javax.servlet.http.HttpServletRequest} object.
   * @param response a {@link javax.servlet.http.HttpServletResponse} object.
   * @return a {@link org.springframework.web.servlet.ModelAndView} object.
   * @throws java.io.IOException if any.
   */
  @RequestMapping("/reportSeqRunTable.htm")
  public ModelAndView doCreateTableJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Registration registration = Security.getRegistration(request);
    if (registration == null)
      return null;

    // Read all the possible parameters
    String seqRun_id = request.getParameter(SEQ_RUN_ID);
    String tableSel = request.getParameter(TABLE_SEL);
    String tableModel = request.getParameter(TABLE_MODEL);
    String sortName = request.getParameter(SORT_NAME);
    String sortOrder = request.getParameter(SORT_ORDER);
    String pageStr = request.getParameter(PAGE_NUM);
    String rowsPagesStr = request.getParameter(ROWS_PER_PAGE);
    String init = request.getParameter(INIT);
    String csvtype = request.getParameter(CSV_TYPE);
    String check = request.getParameter(CHECK);

    Integer seqRunId = null;
    int page = 1;
    int rowsPages = 15;
    try {
      page = Integer.parseInt(pageStr);
    } catch (NumberFormatException e) {
      // this is ok. Default value is used.
    }
    try {
      seqRunId = Integer.parseInt(seqRun_id);
    } catch (NumberFormatException e) {
      // this is ok. Default value is used.
    }
    try {
      rowsPages = Integer.parseInt(rowsPagesStr);
    } catch (NumberFormatException e) {
      // this is ok. Default value is used.
    }

    response.setContentType("application/json");

    SequencerRun seqRun = null;
    if (seqRunId != null) {
      seqRun = sequencerRunService.findByID(seqRunId);
    }

    if (csvtype != null) {
      if ("progress".equals(csvtype)) {
        if (check != null) {
          response.getWriter().write(Boolean.toString(isProgressDownloaded));
          // reset
          if (isProgressDownloaded) {
            isProgressDownloaded = false;
          }
          response.flushBuffer();
          return null;
        }
        isProgressDownloaded = false;
        sendCsvProgressReport(response, seqRun);
        isProgressDownloaded = true;
        return null;
      }
      if ("file".equals(csvtype)) {
        if (check != null) {
          response.getWriter().write(Boolean.toString(isFileDownloaded));
          // reset
          if (isFileDownloaded) {
            isFileDownloaded = false;
          }
          response.flushBuffer();
          return null;
        }
        isFileDownloaded = false;
        sendCsvFileReport(response, seqRun);
        isFileDownloaded = true;
        return null;
      }
    }
    if (init != null) {
      // Clear all the data
      workflows = getSequencersWorkflows(seqRun, registration);
      Collections.sort(workflows);
      ModelAndView modelAndView = new ModelAndView("ReportSequencerRun");
      createChartModel(seqRun, modelAndView);
      modelAndView.addObject("seq_run_id", seqRun_id);
      return modelAndView;
    }
    if (tableModel != null) {
      // Get Model for sample
      String json = createSampleTableModelJson(workflows);
      response.getWriter().write(json);
      response.flushBuffer();
      return null;
    }
    if ("seqrun".equals(tableSel)) {
      orderFieldSeqRun = sortName;
      orderTypeSeqRun = sortOrder;
      String json = createSeqRunsTableContentJson(seqRun, page, rowsPages, sortName, sortOrder);
      response.getWriter().write(json);
      response.flushBuffer();
      return null;
    }
    if ("file".equals(tableSel)) {
      orderFieldFile = sortName;
      orderTypeFile = sortOrder;
      String json = createFileTableJson(seqRun, page, rowsPages, sortName, sortOrder);
      response.getWriter().write(json);
      response.flushBuffer();
      return null;
    }
    return null;
  }

  private void sendCsvProgressReport(HttpServletResponse response, SequencerRun seqRun) throws IOException {
    String sortField = getHibernateSortField(orderFieldSeqRun, orderTypeSeqRun);
    if (!orderTypeSeqRun.equals("asc") && !orderTypeSeqRun.equals("desc")) {
      orderTypeSeqRun = "asc";
    }
    StringBuffer sb = new StringBuffer();
    sb.append("Sequencer_Run\tLane\tIUS\tSample\t");
    for (Workflow workflow : workflows) {
      sb.append(workflow.getName() + " " + workflow.getVersion()).append("\t");
    }

    sb.append("\n");

    List<SequencerRunReportId> rows = sampleReportService.getSequencerRunReportIds(seqRun, sortField, orderTypeSeqRun);
    for (SequencerRunReportId row : rows) {
      sb.append(removeNulls(row.getSequencerRun().getName())).append("\t");
      sb.append(removeNulls(row.getLane().getName())).append("\t");
      sb.append(removeNulls(row.getIus().getSwAccession().toString())).append("\t");
      sb.append(removeNulls(row.getChildSample().getTitle())).append("\t");
      for (Workflow workflow : workflows) {
        Status status = sampleReportService.getStatus(row.getStudy(), row.getChildSample(), row.getIus(),
            row.getLane(), row.getSequencerRun(), workflow);
        if (status == null) {
          sb.append(Status.notstarted.name()).append("\t");
        } else {
          sb.append(status).append("\t");
        }
      }
      sb.append("\n");
    }

    response.setContentType("text/csv");
    response.addHeader("Content-Disposition", "attachment;filename=progressReport.csv");
    response.addHeader("Content-Type", "application/force-download");
    response.getWriter().write(sb.toString());
    response.flushBuffer();
  }

  private void sendCsvFileReport(HttpServletResponse response, SequencerRun seqRun) throws IOException {
    String sortField = getHibernateSortField(orderFieldFile, orderTypeFile);
    if (!orderTypeFile.equals("asc") && !orderTypeFile.equals("desc")) {
      orderTypeFile = "asc";
    }
    StringBuffer sb = new StringBuffer();
    sb.append("Sequencer_Run\tLane\tIUS\tSample\tExperiment\tStudy\tWorkflow\tWorkflow_Run\tProcessing\tFile\n");
    List<FileReportRow> rows = fileReportService.getReportForSequencerRun(seqRun, sortField, orderTypeFile);
    for (FileReportRow row : rows) {

      sb.append(removeNulls(row.getLane().getSequencerRun().getName())).append("\t");
      sb.append(removeNulls(row.getLane().getName())).append("\t");
      sb.append(removeNulls(row.getIus().getSwAccession().toString())).append("\t");
      sb.append(removeNulls(row.getChildSample().getTitle())).append("\t");
      sb.append(removeNulls(row.getExperiment().getTitle())).append("\t");
      sb.append(removeNulls(row.getStudy().getTitle())).append("\t");

      Processing processing = row.getProcessing();
      Workflow workflow = null;
      WorkflowRun run = null;
      if (processing != null) {
        // try to get processing related workflow run
        run = processing.getWorkflowRun();
        if (run == null) {
          // if no workflow run found, let's try to get workflow run by ancestor
          // run id
          run = processing.getWorkflowRunByAncestorWorkflowRunId();
        }
        if (run != null) {
          // if run is found, get workflow
          workflow = run.getWorkflow();
        }
      }

      if (workflow != null) {
        sb.append(workflow.getName()).append("\t");
      } else {
        sb.append("\t");
      }

      if (run != null) {
        sb.append(run.getName()).append("\t");
      } else {
        sb.append("\t");
      }

      if (processing != null) {
        sb.append(processing.getAlgorithm()).append("\t");
      } else {
        sb.append("\t");
      }

      sb.append(row.getFile().getFilePath()).append("\t");
      sb.append("\n");

    }
    response.setContentType("text/csv");
    response.addHeader("Content-Type", "application/force-download");
    response.addHeader("Content-Disposition", "attachment;filename=fileReport.csv");
    response.getWriter().write(sb.toString());
    response.flushBuffer();
  }

  private void createChartModel(SequencerRun seqRun, ModelAndView modelAndView) {
    createOverallChart(modelAndView, seqRun);
    createWorkflowCharts(modelAndView, seqRun);
  }

  private void createWorkflowCharts(ModelAndView modelAndView, SequencerRun seqRun) {
    Map<Workflow, String> workflowCharts = new HashMap<Workflow, String>();
    for (Workflow workflow : workflows) {
      List<Status> statuses = sampleReportService.getStatusesForWorkflow(seqRun, workflow);
      Map<Status, Integer> statusCount = new LinkedHashMap<Status, Integer>();
      statusCount.put(Status.failed, 0);
      statusCount.put(Status.pending, 0);
      statusCount.put(Status.running, 0);
      statusCount.put(Status.notstarted, 0);
      statusCount.put(Status.completed, 0);
      for (Status status : statuses) {
        int count = sampleReportService.countOfStatus(seqRun, workflow, status);
        statusCount.put(status, count);
      }
      int current = 0;
      StringBuilder out = new StringBuilder();
      for (Status status : statusCount.keySet()) {
        current++;
        int count = statusCount.get(status);
        String sStatus = status.toString();
        if (Status.notstarted == status) {
          sStatus = "not started";
        }
        out.append("['" + sStatus + "'," + count + "]");
        if (current != statusCount.keySet().size()) {
          out.append(",");
        }
      }
      workflowCharts.put(workflow, out.toString());
    }
    modelAndView.addObject("names", workflows);
    modelAndView.addObject("chartData", workflowCharts);
  }

  private void createOverallChart(ModelAndView modelAndView, SequencerRun seqRun) {
    List<Status> statuses = sampleReportService.getStatusesForSequencerRun(seqRun);
    Map<Status, Integer> statusCount = new LinkedHashMap<Status, Integer>();
    statusCount.put(Status.failed, 0);
    statusCount.put(Status.pending, 0);
    statusCount.put(Status.running, 0);
    statusCount.put(Status.notstarted, 0);
    statusCount.put(Status.completed, 0);
    for (Status status : statuses) {
      int count = sampleReportService.countOfStatus(seqRun, status);
      statusCount.put(status, count);
    }
    int current = 0;
    StringBuilder out = new StringBuilder();
    for (Status status : statusCount.keySet()) {
      current++;
      int count = statusCount.get(status);
      String sStatus = status.toString();
      if (Status.notstarted == status) {
        sStatus = "not started";
      }
      out.append("['" + sStatus + "'," + count + "]");
      if (current != statusCount.keySet().size()) {
        out.append(",");
      }
    }
    modelAndView.addObject("overallChartData", out.toString());
  }

  private String createFileTableJson(SequencerRun seqRun, int page, int rowsPages, String sortName, String sortOrder) {
    Flexigrid fileFlexigrid = createFileFlexigrid(seqRun, page, rowsPages, sortName, sortOrder);

    // Convert to Flexigrid JSON
    Gson gson = new Gson();
    String json = gson.toJson(fileFlexigrid);
    return json;
  }

  private Flexigrid createFileFlexigrid(SequencerRun seqRun, int page, int rowsPages, String sortName, String sortOrder) {
    String sortField = getHibernateSortField(sortName, sortOrder);
    if (!sortOrder.equals("asc") && !sortOrder.equals("desc")) {
      sortOrder = "asc";
    }
    List<FileReportRow> rows = fileReportService.getReportForSequencerRun(seqRun, sortField, sortOrder, (page - 1)
        * rowsPages, rowsPages);
    Flexigrid flexigrid = new Flexigrid();

    for (FileReportRow row : rows) {
      List<String> cellsModel = new ArrayList<String>();

      cellsModel.add(wrapSwAccession(row.getLane().getSequencerRun().getSwAccession(), removeNulls(row.getLane()
          .getSequencerRun().getName())));
      cellsModel.add(wrapSwAccession(row.getLane().getSwAccession(), removeNulls(row.getLane().getName())));
      cellsModel.add(wrapSwAccession(row.getIus().getSwAccession(), removeNulls(row.getIus().getSwAccession()
          .toString())));
      cellsModel.add(wrapSwAccession(row.getChildSample().getSwAccession(),
          removeNulls(row.getChildSample().getTitle())));
      cellsModel
          .add(wrapSwAccession(row.getExperiment().getSwAccession(), removeNulls(row.getExperiment().getTitle())));
      cellsModel.add(wrapSwAccession(row.getStudy().getSwAccession(), removeNulls(row.getStudy().getTitle())));

      Processing processing = row.getProcessing();
      Workflow workflow = null;
      WorkflowRun run = null;
      if (processing != null) {
        // try to get processing related workflow run
        run = processing.getWorkflowRun();
        if (run == null) {
          // if no workflow run found, let's try to get workflow run by ancestor
          // run id
          run = processing.getWorkflowRunByAncestorWorkflowRunId();
        }
        if (run != null) {
          // if run is found, get workflow
          workflow = run.getWorkflow();
        }
      }

      if (workflow != null) {
        cellsModel.add(workflow.getName());
      } else {
        cellsModel.add("");
      }

      if (run != null) {
        cellsModel.add(run.getName());
      } else {
        cellsModel.add("");
      }

      if (processing != null) {
        cellsModel.add(processing.getAlgorithm());
      } else {
        cellsModel.add("");
      }

      cellsModel.add(wrapSwAccession(row.getFile().getSwAccession(), row.getFile().getFilePath()));

      Flexigrid.Cells cells = flexigrid.new Cells(cellsModel);
      flexigrid.addRow(cells);
    }

    int total = fileReportService.countOfRows(seqRun);
    flexigrid.setTotal(total);
    flexigrid.setPage(page);
    // long end = System.nanoTime() - start;
    // Log.info("Flexigrid FileTable created. Time: " + end / 1e6);
    return flexigrid;
  }

  private String getHibernateSortField(String sortName, String sortOrder) {
    String sortField = "study.studyId";
    if (!"undefined".equals(sortOrder)) {
      if (FILE_STUDY_TITLE.equals(sortName)) {
        sortField = "study.title";
      }
      if (FILE_STUDY_SWID.equals(sortName)) {
        sortField = "study.swAccession";
      }
      if (FILE_EXPERIMENT_NAME.equals(sortName)) {
        sortField = "experiment.name";
      }
      if (FILE_EXPERIMENT_SWID.equals(sortName)) {
        sortField = "experiment.swAccession";
      }
      if (FILE_PARENT_SAMPLE_NAME.equals(sortName)) {
        sortField = "sample.name";
      }
      if (FILE_PARENT_SAMPLE_SWID.equals(sortName)) {
        sortField = "sample.swAccession";
      }
      if (FILE_SAMPLE_NAME.equals(sortName)) {
        sortField = "childSample.name";
      }
      if (FILE_SAMPLE_SWID.equals(sortName)) {
        sortField = "childSample.swAccession";
      }
      if (FILE_SEQUENCER_NAME.equals(sortName)) {
        sortField = "lane.sequencerRun.name";
      }
      if (SAMPLE_SEQUENCER_NAME.equals(sortName)) {
        sortField = "sequencerRun.name";
      }
      if (FILE_SEQUENCER_SWID.equals(sortName)) {
        sortField = "lane.sequencerRun.swid";
      }
      if (FILE_LANE_NAME.equals(sortName)) {
        sortField = "lane.name";
      }
      if (FILE_LANE_NUMBER.equals(sortName)) {
        sortField = "lane.laneIndex";
      }
      if (FILE_LANE_SWID.equals(sortName)) {
        sortField = "lane.swAccession";
      }
      if (FILE_IUS_TAG.equals(sortName)) {
        sortField = "ius.tag";
      }
      if (FILE_IUS_SWID.equals(sortName)) {
        sortField = "ius.swAccession";
      }
      if (FILE_WF_NAME.equals(sortName)) {
        sortField = "processing.workflowRun.workflow.name";
      }
      if (FILE_WF_SWID.equals(sortName)) {
        sortField = "processing.workflowRun.workflow.swAccession";
      }
      if (FILE_WF_VERSION.equals(sortName)) {
        sortField = "processing.workflowRun.workflow.version";
      }
      if (FILE_RUN_NAME.equals(sortName)) {
        sortField = "processing.workflowRun.name";
      }
      if (FILE_RUN_SWID.equals(sortName)) {
        sortField = "processing.workflowRun.swAccession";
      }
      if (FILE_PROCESSING_ALG.equals(sortName)) {
        sortField = "processing.algorithm";
      }
      if (FILE_PROCESSING_SWID.equals(sortName)) {
        sortField = "processing.swAccession";
      }
      if (FILE_META_TYPE.equals(sortName)) {
        sortField = "file.metaType";
      }
      if (FILE_SWID.equals(sortName)) {
        sortField = "file.swAccession";
      }
      if (FILE_PATH.equals(sortName)) {
        sortField = "file.filePath";
      }
    }
    return sortField;
  }

  private String createSeqRunsTableContentJson(SequencerRun seqRuns, int page, int rp, String sortName, String sortOrder) {
    Flexigrid seqFlexigrid = createSeqFlexigrid(seqRuns, page, rp, sortName, sortOrder);

    // Convert to Flexigrid JSON
    Gson gson = new Gson();
    String json = gson.toJson(seqFlexigrid);
    return json;
  }

  private Flexigrid createSeqFlexigrid(SequencerRun seqRun, int page, int rp, String sortName, String sortOrder) {
    long start = System.nanoTime();

    Flexigrid flexigrid = new Flexigrid();
    String sortField = getHibernateSortField(sortName, sortOrder);
    if (!sortOrder.equals("asc") && !sortOrder.equals("desc")) {
      sortOrder = "asc";
    }
    List<SequencerRunReportId> rows = sampleReportService.getSequencerRunReportIds(seqRun, sortField, sortOrder,
        (page - 1) * rp, rp);
    for (SequencerRunReportId srriD : rows) {
      String[] statusesOut = new String[this.workflows.size()];
      for (int i = 0; i < this.workflows.size(); i++) {
        Status status = sampleReportService.getStatus(srriD.getStudy(), srriD.getChildSample(), srriD.getIus(),
            srriD.getLane(), srriD.getSequencerRun(), this.workflows.get(i));
        if (status == null) {
          statusesOut[i] = wrapStatus(Status.notstarted);
        } else {
          statusesOut[i] = wrapStatus(status);
        }
      }
      List<String> cellsModel = new LinkedList<String>();
      cellsModel.add(wrapSwAccession(srriD.getSequencerRun().getSwAccession(), removeNulls(srriD.getSequencerRun()
          .getName())));
      cellsModel.add(wrapSwAccession(srriD.getLane().getSwAccession(), removeNulls(srriD.getLane().getName())));
      cellsModel.add(wrapSwAccession(srriD.getIus().getSwAccession(), removeNulls(srriD.getIus().getSwAccession()
          .toString())));
      cellsModel.add(wrapSwAccession(srriD.getChildSample().getSwAccession(), removeNulls(srriD.getChildSample()
          .getTitle())));
      cellsModel.addAll(Arrays.asList(statusesOut));
      cellsModel.add(wrapOverall());

      Flexigrid.Cells cells = flexigrid.new Cells(cellsModel);
      flexigrid.addRow(cells);
    }

    int total = sampleReportService.countOfRows(seqRun);
    flexigrid.setTotal(total);
    flexigrid.setPage(page);

    long end = System.nanoTime() - start;
    Log.info("Flexigrid SampleTable created. Time: " + end / 1e6);
    return flexigrid;
  }

  private String createSampleTableModelJson(List<Workflow> workflows) {
    List<Flexigrid.ColumnModel> model = new LinkedList<Flexigrid.ColumnModel>();
    model.add(new Flexigrid.ColumnModel("SequencerRun", SAMPLE_SEQUENCER_NAME, WIDTH, true, "left"));
    model.add(new Flexigrid.ColumnModel("Lane", FILE_LANE_NAME, WIDTH, true, "left"));
    model.add(new Flexigrid.ColumnModel("IUS", FILE_IUS_SWID, WIDTH, true, "left"));
    model.add(new Flexigrid.ColumnModel("Sample", FILE_SAMPLE_NAME, WIDTH, true, "left"));
    for (Workflow workflow : workflows) {
      Flexigrid.ColumnModel column = new Flexigrid.ColumnModel(workflow.getName() + " " + workflow.getVersion(), "wf_"
          + workflow.getSwAccession(), WIDTH, false, "left");
      model.add(column);
    }
    model.add(new Flexigrid.ColumnModel("Overall", "overall", WIDTH, false, "left"));

    // Convert to Flexigrid JSON
    Gson gson = new Gson();
    String json = gson.toJson(model);
    return json;
  }

  private List<Workflow> getSequencersWorkflows(SequencerRun seqRun, Registration registration) {
    return sampleReportService.getWorkflows(seqRun);// listSequencerRunsWorkflows(seqRun);
  }

  private String wrapSwAccession(Integer swId, String label) {
    return "<div class=\"label\">" + label + "</div>" + "<div class=\"sw\" swid=\"" + swId + "\"></div>";
  }

  private String wrapStatus(Status status) {
    return "<div class=\"status\" >" + status + "</div>";
  }

  private String wrapOverall() {
    return "<div class=\"overall\" />";
  }

  private String removeNulls(String in) {
    if (in == null) {
      return "";
    }
    return in;
  }

}
