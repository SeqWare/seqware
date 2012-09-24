package net.sourceforge.solexatools.webapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.FileReportService;
import net.sourceforge.seqware.common.business.SampleReportService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.model.FileReportRow;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.PaginationUtil;
import net.sourceforge.solexatools.webapp.metamodel.Flexigrid;
import net.sourceforge.solexatools.webapp.metamodel.Flexigrid.Cells;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

import com.google.gson.Gson;
import net.sourceforge.seqware.common.util.Log;

@SuppressWarnings("deprecation")
public class StudyReportTableController extends BaseCommandController {

  public final static String STUDY_ID = "study_id";
  public final static String JSON = "json";
  public final static String SORT_NAME = "sortname";
  public final static String SORT_ORDER = "sortorder";
  public final static String SORT_ORDER_ASC = "asc";
  public final static String SORT_ORDER_DESC = "desc";
  public final static String PAGE_NUM = "page";
  public final static String ROWS_PER_PAGE = "rp";
  public final static String TABLE_SEL = "tablesel";
  public final static String TABLE_MODEL = "tablemodel";

  // Sortfields
  public final static String FILE_SAMPLE = "f_sample";
  public final static String FILE_TISSUE = "f_tissue";
  public final static String FILE_LIBRARY = "f_library";
  public final static String FILE_TEMPLATE = "f_template";
  public final static String FILE_IUS = "f_ius";
  public final static String FILE_LANE = "f_lane";
  public final static String FILE_FILE = "f_file";

  // New sort fields
  public final static String FILE_STUDY_TITLE = "f_study_title";
  public final static String FILE_STUDY_SWID = "f_study_swid";
  public final static String FILE_EXPERIMENT_NAME = "f_exp_name";
  public final static String FILE_EXPERIMENT_SWID = "f_exp_swid";
  public final static String FILE_PARENT_SAMPLE_NAME = "f_sample_name";
  public final static String FILE_PARENT_SAMPLE_SWID = "f_sample_swid";
  public final static String FILE_SAMPLE_NAME = "f_child_sample_name";
  public final static String FILE_SAMPLE_SWID = "f_child_sample_swid";
  public final static String FILE_SEQUENCER_NAME = "f_seqrun_name";
  public final static String FILE_SEQUENCER_SWID = "f_seqrun_swid";
  public final static String FILE_LANE_NAME = "f_lane_name";
  public final static String FILE_LANE_NUMBER = "f_lane_num";
  public final static String FILE_LANE_SWID = "f_lane_swid";
  public final static String FILE_IUS_TAG = "f_ius_tag";
  public final static String FILE_IUS_SWID = "f_ius_swid";
  public final static String FILE_WF_NAME = "f_wf_name";
  public final static String FILE_WF_VERSION = "f_wf_version";
  public final static String FILE_WF_SWID = "f_wf_swid";
  public final static String FILE_RUN_NAME = "f_run_name";
  public final static String FILE_RUN_SWID = "f_run_swid";
  public final static String FILE_PROCESSING_ALG = "f_processing_alg";
  public final static String FILE_PROCESSING_SWID = "f_processing_swid";
  public final static String FILE_META_TYPE = "f_file_meta";
  public final static String FILE_SWID = "f_file_swid";
  public final static String FILE_PATH = "f_file_path";

  public final static String SAMPLE_SAMPLE = "s_sample";
  public final static String SAMPLE_CHILD = "s_child";

  // Services
  private StudyService studyService;
  private SampleService sampleService;
  private FileReportService fileReportService;
  private SampleReportService sampleReportService;

  // Statuses
  private final static String NOT_STARTED = "notstarted";

  // Table model data
  private final static int WIDTH = 100;

  // Various data
  private List<Workflow> workflows;

  private Flexigrid fileFlexigrid;
  private Flexigrid sampleFlexigrid;

  public StudyReportTableController() {
    super();
  }

  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    Registration registration = Security.getRegistration(request);
    if (registration == null)
      return null;

    String study_id = request.getParameter(STUDY_ID);
    String tableSel = request.getParameter(TABLE_SEL);
    String tableModel = request.getParameter(TABLE_MODEL);
    String sortName = request.getParameter(SORT_NAME);
    String sortOrder = request.getParameter(SORT_ORDER);
    String pageStr = request.getParameter(PAGE_NUM);
    String rowsPagesStr = request.getParameter(ROWS_PER_PAGE);

    Integer studyId = null;
    int page = 1;
    int rowsPages = 15;
    try {
      studyId = Integer.parseInt(study_id);
      page = Integer.parseInt(pageStr);
      rowsPages = Integer.parseInt(rowsPagesStr);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }

    // Study was not int??
    if (studyId == null) {
      return null;
    }

    Study study = studyService.findByID(studyId);
    if (study == null) {
      return null;
    }

    // This workflows. Statuses are generated for them.
    // We sort them to have the same model as we have
    // while generating table model

    response.setContentType("application/json");

    if (tableModel != null) {
      // Clear all the data
      fileFlexigrid = null;
      sampleFlexigrid = null;
      workflows = sampleReportService.getWorkflowsForStudy(study);
      Collections.sort(workflows);
      // Get Model for sample
      String json = createSampleTableModelJson(study);
      response.getWriter().write(json);
      response.flushBuffer();
      return null;
    }
    if ("sample".equals(tableSel)) {
      String json = createSampleTableJson(study, page, rowsPages, sortName, sortOrder);
      response.getWriter().write(json);
      response.flushBuffer();
    }
    if ("file".equals(tableSel)) {
      String json = createFileTableJson(study, page, rowsPages, sortName, sortOrder);
      response.getWriter().write(json);
      response.flushBuffer();
    }
    return null;
  }

  private String createSampleTableModelJson(Study study) {
    List<Flexigrid.ColumnModel> model = new LinkedList<Flexigrid.ColumnModel>();
    model.add(new Flexigrid.ColumnModel("Sample", "s_sample", WIDTH, true, "left"));
    model.add(new Flexigrid.ColumnModel("Child Sample", "s_child", WIDTH, true, "left"));
    for (Workflow workflow : this.workflows) {
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

  private String createFileTableJson(Study study, int page, int rp, String sortName, String sortOrder) {
    fileFlexigrid = createFileFlexigrid(study, page, rp, sortName, sortOrder);
    fileFlexigrid.setPage(page);
    List<Cells> rowsAll = fileFlexigrid.getRows();
    fileFlexigrid.setRows(rowsAll);
    // Convert to Flexigrid JSON
    Gson gson = new Gson();
    String json = gson.toJson(fileFlexigrid);
    fileFlexigrid.setRows(rowsAll);
    return json;
  }

  private Flexigrid createFileFlexigrid(Study study, int page, int rp, String sortName, String sortOrder) {
    // long start = System.nanoTime();
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
    if (!sortOrder.equals("asc") && !sortOrder.equals("desc")) {
      sortOrder = "asc";
    }
    List<FileReportRow> rows = fileReportService.getReportForStudy(study, sortField, sortOrder, (page - 1) * rp, rp);
    Flexigrid flexigrid = new Flexigrid(0, 0);
    int total = fileReportService.countOfRows(study);

    for (FileReportRow row : rows) {
      List<String> cellsModel = new ArrayList<String>();
      // The model is
      // Study Title\tStudy SWID\tExperiment Name\tExperiment SWID\tParent
      // Sample Name\t" +
      // "Parent Sample SWID | Parent Sample Attributes |" +
      // "Sample Name | Sample SWID | Sample Attributes |" +
      // "Sequencer Run Name | Sequencer Run SWID |" +
      // "Lane Name | Lane Number | Lane SWID | Lane Attributes |" +
      // "IUS Tag | IUS SWID |" +
      // "Workflow Name | Workflow Version | Workflow SWID |" +
      // "Workflow Run Name | Workflow Run SWID |" +
      // "Processing Algorithm | Processing SWID |" +
      // "File Meta-Type | File SWID | File Path"
      cellsModel.add(wrapSwAccession(row.getStudy().getSwAccession(), row.getStudy().getTitle()));
      cellsModel.add(wrapSwAccession(row.getStudy().getSwAccession(), row.getStudy().getSwAccession().toString()));
      cellsModel.add(wrapSwAccession(row.getExperiment().getSwAccession(), row.getExperiment().getName()));
      cellsModel.add(wrapSwAccession(row.getExperiment().getSwAccession(), row.getExperiment().getSwAccession()
          .toString()));
      cellsModel.add(wrapSwAccession(row.getSample().getSwAccession(), row.getSample().getName()));
      cellsModel.add(wrapSwAccession(row.getSample().getSwAccession(), row.getSample().getSwAccession().toString()));
      StringBuffer attributes = new StringBuffer();
      // get all the sample attributes
      for (SampleAttribute attribute : row.getSample().getSampleAttributes()) {
        attributes.append(attribute.getTag() + "=" + attribute.getValue() + "<br />");
      }
      cellsModel.add(attributes.toString());
      cellsModel.add(wrapSwAccession(row.getChildSample().getSwAccession(), row.getChildSample().getName()));
      cellsModel.add(wrapSwAccession(row.getChildSample().getSwAccession(), row.getChildSample().getSwAccession()
          .toString()));
      attributes = new StringBuffer();
      // get all the child sample attributes
      for (SampleAttribute attribute : row.getChildSample().getSampleAttributes()) {
        attributes.append(attribute.getTag() + "=" + attribute.getValue() + "<br />");
      }
      cellsModel.add(attributes.toString());

      Lane lane = row.getLane();
      SequencerRun sequencerRun = null;
      if (lane != null) {
        // if there is lane, get appropriate sequencer run for it
        sequencerRun = lane.getSequencerRun();
      }

      if (sequencerRun != null) {
        cellsModel.add(wrapSwAccession(sequencerRun.getSwAccession(), sequencerRun.getName()));
        cellsModel.add(wrapSwAccession(sequencerRun.getSwAccession(), sequencerRun.getSwAccession().toString()));
      } else {
        // no sequencer run - skip it
        cellsModel.add("");
        cellsModel.add("");
      }

      if (lane != null) {
        cellsModel.add(wrapSwAccession(lane.getSwAccession(), lane.getName()));
        cellsModel.add(lane.getLaneIndex().toString());
        cellsModel.add(wrapSwAccession(lane.getSwAccession(), lane.getSwAccession().toString()));
        attributes = new StringBuffer();
        // get all the lane attributes
        for (LaneAttribute attribute : lane.getLaneAttributes()) {
          attributes.append(attribute.getTag() + "=" + attribute.getValue() + "<br />");
        }
        cellsModel.add(attributes.toString());
      } else {
        cellsModel.add("");
        cellsModel.add("");
        cellsModel.add("");
        cellsModel.add("");
      }

      cellsModel.add(wrapSwAccession(row.getIus().getSwAccession(), row.getIus().getTag()));
      cellsModel.add(wrapSwAccession(row.getIus().getSwAccession(), row.getIus().getSwAccession().toString()));

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
        cellsModel.add(workflow.getVersion());
        cellsModel.add(workflow.getSwAccession().toString());
      } else {
        cellsModel.add("");
        cellsModel.add("");
        cellsModel.add("");
      }

      if (run != null) {
        cellsModel.add(run.getName());
        cellsModel.add(run.getSwAccession().toString());
      } else {
        cellsModel.add("");
        cellsModel.add("");
      }

      if (processing != null) {
        cellsModel.add(processing.getAlgorithm());
        cellsModel.add(processing.getSwAccession().toString());
      } else {
        cellsModel.add("");
        cellsModel.add("");
      }

      cellsModel.add(row.getFile().getMetaType());
      cellsModel.add(row.getFile().getSwAccession().toString());
      cellsModel.add(wrapSwAccession(row.getFile().getSwAccession(), row.getFile().getFilePath()));

      Flexigrid.Cells cells = flexigrid.new Cells(cellsModel);
      flexigrid.addRow(cells);
    }

    flexigrid.setTotal(total);
    // long end = System.nanoTime() - start;
    // Log.info("Flexigrid FileTable created. Time: " + end / 1e6);
    return flexigrid;
  }

  private String createSampleTableJson(Study study, int page, int rp, String sortName, String sortOrder) {
    if (sampleFlexigrid == null) {
      sampleFlexigrid = createSampleFlexigrid(study, page);
    }
    sampleFlexigrid.setPage(page);
    List<Cells> rowsAll = sampleFlexigrid.getRows();

    if (!"undefined".equals(sortOrder)) {
      long start = System.nanoTime();
      sortRows(rowsAll, sortOrder, sortName);
      long end = System.nanoTime() - start;
      Log.info("Sorting SampleTable Time: " + end / 1e6);
    }
    @SuppressWarnings("unchecked")
    List<Cells> pagedCells = PaginationUtil.subList(page - 1, rp, rowsAll);
    sampleFlexigrid.setRows(pagedCells);

    // Convert to Flexigrid JSON
    Gson gson = new Gson();
    String json = gson.toJson(sampleFlexigrid);
    sampleFlexigrid.setRows(rowsAll);
    return json;
  }

  private Flexigrid createSampleFlexigrid(Study study, int page) {
    long start = System.nanoTime();
    List<Sample> childSamples = sampleReportService.getChildSamples(study);
    // Generate Rows
    Flexigrid flexigrid = new Flexigrid(childSamples.size(), page);
    for (Sample sample : childSamples) {
      Sample rootSample = sampleService.getRootSample(sample);
      // Fill statuses with row model
      List<String> statusesOut = new ArrayList<String>();
      for (Workflow workflow : this.workflows) {
        String status = sampleReportService.getStatus(study, sample, workflow);
        if (status == null) {
          statusesOut.add(wrapStatus(NOT_STARTED));
        } else {
          statusesOut.add(wrapStatus(status));
        }
      }
      List<String> cellsModel = new LinkedList<String>();
      cellsModel.add(wrapSwAccession(rootSample.getSwAccession(), rootSample.getTitle()));
      cellsModel.add(wrapSwAccession(sample.getSwAccession(), rootSample.getSampleId().intValue() != sample
          .getSampleId().intValue() ? sample.getTitle() : "no child"));
      cellsModel.addAll(statusesOut);
      cellsModel.add(wrapOverall());

      Flexigrid.Cells cells = flexigrid.new Cells(cellsModel);
      flexigrid.addRow(cells);
    }
    long end = System.nanoTime() - start;
    Log.info("Flexigrid SampleTable created. Time: " + end / 1e6);
    return flexigrid;
  }

  @SuppressWarnings("unchecked")
  private void sortRows(List<Cells> rowsAll, String sortOrder, String sortName) {
    int columnPos = 0;
    if (FILE_SAMPLE.equals(sortName) || (SAMPLE_SAMPLE.equals(sortName))) {
      columnPos = 0;
    }
    if (FILE_TISSUE.equals(sortName) || (SAMPLE_CHILD.equals(sortName))) {
      columnPos = 1;
    }
    if (FILE_LIBRARY.equals(sortName)) {
      columnPos = 2;
    }
    if (FILE_TEMPLATE.equals(sortName)) {
      columnPos = 3;
    }
    if (FILE_IUS.equals(sortName)) {
      columnPos = 4;
    }
    if (FILE_LANE.equals(sortName)) {
      columnPos = 5;
    }
    if (FILE_FILE.equals(sortName)) {
      columnPos = 6;
    }

    @SuppressWarnings("rawtypes")
    Comparator comparator = null;
    if (SORT_ORDER_ASC.equals(sortOrder)) {
      comparator = new CellsComparator(columnPos);
    }
    if (SORT_ORDER_DESC.equals(sortOrder)) {
      comparator = Collections.reverseOrder(new CellsComparator(columnPos));
    }

    Collections.sort(rowsAll, comparator);
  }

  @SuppressWarnings({ "rawtypes" })
  private class CellsComparator implements Comparator {

    private int pos;

    public CellsComparator(int pos) {
      this.pos = pos;
    }

    @Override
    public int compare(Object o1, Object o2) {
      return (((Cells) o1).getCell().get(pos)).compareToIgnoreCase(((Cells) o2).getCell().get(pos));
    }

  };

  private String wrapSwAccession(Integer swId, String label) {
    return "<div class=\"label\">" + label + "</div>" + "<div class=\"sw\" swid=\"" + swId + "\"></div>";
  }

  private String wrapStatus(String status) {
    return "<div class=\"status\" >" + status + "</div>";
  }

  private String wrapOverall() {
    return "<div class=\"overall\" />";
  }

  public void setStudyService(StudyService studyService) {
    this.studyService = studyService;
  }

  public void setSampleService(SampleService service) {
    this.sampleService = service;
  }

  public void setFileReportService(FileReportService service) {
    this.fileReportService = service;
  }

  public void setSampleReportService(SampleReportService service) {
    this.sampleReportService = service;
  }
}
