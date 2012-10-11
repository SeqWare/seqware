package net.sourceforge.solexatools.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

@SuppressWarnings("deprecation")
public class StudyReportBoxController extends BaseCommandController {

  private static final String NOT_STARTED = "notstarted";
  public final static String STUDY_ID = "study_id";
  public final static String JSON = "json";
  public final static String SORT_NAME = "sortname";
  public final static String SORT_ORDER = "sortorder";
  public final static String CSV_TYPE = "csvtype";
  public final static String CHECK = "check";

  // Statuses
  private final static String SUCCESS = "completed";
  private final static String PENDING = "pending";
  private final static String RUNNING = "running";
  private final static String FAILED = "failed";

  private StudyService studyService;
  private SampleService sampleService;
  private FileReportService fileReportService;
  private SampleReportService sampleReportService;

  private boolean isSampleDownloaded;
  private boolean isFileDownloaded;

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

  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    Registration registration = Security.getRegistration(request);
    if (registration == null) {
      return new ModelAndView("redirect:/login.htm");
    }

    boolean hasError = false;
    List<String> errMsgs = new ArrayList<String>();

    // Get StudyId, for which Report is generated
    String idStr = request.getParameter(STUDY_ID);
    String csvtype = request.getParameter(CSV_TYPE);
    String check = request.getParameter(CHECK);

    int studyId = 0;

    try {
      studyId = Integer.parseInt(idStr);
    } catch (NumberFormatException e) {
      e.printStackTrace();
      hasError = true;
      errMsgs.add(e.getMessage());
    }

    Study currentStudy = studyService.findByID(studyId);

    ModelAndView modelAndView = new ModelAndView("ReportStudy");

    if (!hasError || check != null) {
      if (csvtype != null) {
        // Sample CSV
        if (csvtype.equals("sample")) {
          if (check != null) {
            response.getWriter().write(Boolean.toString(isSampleDownloaded));
            // reset
            if (isSampleDownloaded) {
              isSampleDownloaded = false;
            }
            response.flushBuffer();
            return null;
          }

          StringBuffer sb = new StringBuffer();
          sb.append("Sample\tChild Sample\t");
          List<Workflow> workflows = sampleReportService.getWorkflowsForStudy(currentStudy);
          for (Workflow wf : workflows) {
            sb.append(wf.getName() + "\t");
          }
          sb.append("\n");
          List<Sample> childSamples = sampleReportService.getChildSamples(currentStudy);
          // Generate Rows
          for (Sample sample : childSamples) {
            Sample rootSample = sampleService.getRootSample(sample);
            sb.append(rootSample.getTitle() + "\t");
            sb.append(rootSample.getSampleId().intValue() != sample.getSampleId().intValue() ? sample.getTitle() + "\t"
                : "no child" + "\t");
            for (Workflow workflow : workflows) {
              String status = sampleReportService.getStatus(currentStudy, sample, workflow);
              if (status == null) {
                sb.append(NOT_STARTED + "\t");
              } else {
                sb.append(status + "\t");
              }
            }

            sb.append("\n");
          }

          response.setContentType("text/csv");
          response.addHeader("Content-Disposition", "attachment;filename=sampleReport.csv");
          response.getWriter().write(sb.toString());
          isSampleDownloaded = true;
          response.flushBuffer();
          return null;
        }

        // File CSV
        if (csvtype.equals("file")) {
          if (check != null) {
            response.getWriter().write(Boolean.toString(isFileDownloaded));
            // reset
            if (isFileDownloaded) {
              isFileDownloaded = false;
            }
            response.flushBuffer();
            return null;
          }
          StringBuffer sb = new StringBuffer();
          sb.append("Study Title\tStudy SWID\tExperiment Name\tExperiment SWID\tParent Sample Name\t"
              + "Parent Sample SWID\tParent Sample Attributes\t" + "Sample Name\tSample SWID\tSample Attributes\t"
              + "Sequencer Run Name\tSequencer Run SWID\t" + "Lane Name\tLane Number\tLane SWID\tLane Attributes\t"
              + "IUS Tag\tIUS SWID\t" + "Workflow Name\tWorkflow Version\tWorkflow SWID\t"
              + "Workflow Run Name\t Workflow Run SWID\t" + "Processing Algorithm\tProcessing SWID\t"
              + "File Meta-Type\tFile SWID\tFile Path\n");

          List<FileReportRow> rows = fileReportService.getReportForStudy(currentStudy);

          for (FileReportRow row : rows) {
            sb.append(row.getStudy().getTitle() + "\t");
            sb.append(row.getStudy().getSwAccession() + "\t");
            sb.append(row.getExperiment().getName() + "\t");
            sb.append(row.getExperiment().getSwAccession() + "\t");
            sb.append(row.getSample().getName() + "\t");
            sb.append(row.getSample().getSwAccession() + "\t");
            StringBuffer attributes = new StringBuffer();
            for (SampleAttribute attribute : row.getSample().getSampleAttributes()) {
              attributes.append(attribute.getTag() + "=" + attribute.getValue() + " ");
            }
            sb.append(attributes.toString() + "\t");
            sb.append(row.getChildSample().getName() + "\t");
            sb.append(row.getChildSample().getSwAccession() + "\t");
            attributes = new StringBuffer();
            for (SampleAttribute attribute : row.getChildSample().getSampleAttributes()) {
              attributes.append(attribute.getTag() + "=" + attribute.getValue() + " ");
            }
            sb.append(attributes.toString() + "\t");
            Lane lane = row.getLane();
            SequencerRun sequencerRun = null;
            if (lane != null) {
              sequencerRun = lane.getSequencerRun();
            }
            if (sequencerRun != null) {
              sb.append(sequencerRun.getName() + "\t");
              sb.append(sequencerRun.getSwAccession() + "\t");
            } else {
              sb.append(" \t");
              sb.append(" \t");
            }
            if (lane != null) {
              sb.append(lane.getName() + "\t");
              sb.append(lane.getLaneIndex() + "\t");
              sb.append(lane.getSwAccession() + "\t");
              attributes = new StringBuffer();
              for (LaneAttribute attribute : lane.getLaneAttributes()) {
                attributes.append(attribute.getTag() + "=" + attribute.getValue() + " ");
              }
              sb.append(attributes + "\t");
            } else {
              sb.append(" \t");
              sb.append(" \t");
              sb.append(" \t");
              sb.append(" \t");
            }
            sb.append(row.getIus().getTag() + "\t");
            sb.append(row.getIus().getSwAccession() + "\t");
            Processing processing = row.getProcessing();
            WorkflowRun run = null;
            Workflow workflow = null;
            if (processing != null) {
              run = processing.getWorkflowRun();
              if (run == null) {
                run = processing.getWorkflowRunByAncestorWorkflowRunId();
              }
            }
            if (run != null) {
              workflow = run.getWorkflow();

            }
            if (workflow != null) {
              sb.append(workflow.getName() + "\t");
              sb.append(workflow.getVersion() + "\t");
              sb.append(workflow.getSwAccession() + "\t");
            } else {
              sb.append(" \t");
              sb.append(" \t");
              sb.append(" \t");
            }
            if (run != null) {
              sb.append(run.getName() + "\t");
              sb.append(run.getSwAccession() + "\t");
            } else {
              sb.append(" \t");
              sb.append(" \t");
            }
            if (processing != null) {
              sb.append(processing.getAlgorithm() + "\t");
              sb.append(processing.getSwAccession() + "\t");
            } else {
              sb.append(" \t");
              sb.append(" \t");
            }
            sb.append(row.getFile().getMetaType() + "\t");
            sb.append(row.getFile().getSwAccession() + "\t");
            sb.append(row.getFile().getFilePath() + "\t");
            sb.append("\n");

          }
          response.setContentType("text/csv");
          response.addHeader("Content-Disposition", "attachment;filename=fileReport.csv");
          response.getWriter().write(sb.toString());
          isFileDownloaded = true;
          response.flushBuffer();
          return null;
        }
      }

      if (currentStudy != null) {
        ModelAndView mv = new ModelAndView("ReportStudy");
        createChartModel(currentStudy, mv);
        mv.addObject("study_id", studyId);
        return mv;
      }
    }
    return modelAndView;
  }

  private void createChartModel(Study study, ModelAndView modelAndView) {
    createOverallChart(modelAndView, study);
    createWorkflowCharts(modelAndView, study);
  }

  private void createWorkflowCharts(ModelAndView modelAndView, Study study) {
    Map<Workflow, String> workflowCharts = new HashMap<Workflow, String>();
    List<Workflow> usedWorkflows = sampleReportService.getWorkflowsForStudy(study);
    for (Workflow workflow : usedWorkflows) {
      List<String> statuses = sampleReportService.getStatusesForWorkflow(study, workflow);
      Map<String, Integer> statusCount = new LinkedHashMap<String, Integer>();
      statusCount.put(FAILED, 0);
      statusCount.put(PENDING, 0);
      statusCount.put(RUNNING, 0);
      statusCount.put(NOT_STARTED, 0);
      statusCount.put(SUCCESS, 0);
      for (String status : statuses) {
        int count = sampleReportService.countOfStatus(study, workflow, status);
        statusCount.put(status, count);
      }
      int current = 0;
      StringBuilder out = new StringBuilder();
      for (String status : statusCount.keySet()) {
        current++;
        int count = statusCount.get(status);
        if (NOT_STARTED.equals(status)) {
          status = "not started";
        }
        out.append("['" + status + "'," + count + "]");
        if (current != statusCount.keySet().size()) {
          out.append(",");
        }
      }
      workflowCharts.put(workflow, out.toString());
    }
    modelAndView.addObject("names", usedWorkflows);
    modelAndView.addObject("chartData", workflowCharts);
  }

  private void createOverallChart(ModelAndView modelAndView, Study study) {
    List<String> statuses = sampleReportService.getStatusesForStudy(study);
    Map<String, Integer> statusCount = new LinkedHashMap<String, Integer>();
    statusCount.put(FAILED, 0);
    statusCount.put(PENDING, 0);
    statusCount.put(RUNNING, 0);
    statusCount.put(NOT_STARTED, 0);
    statusCount.put(SUCCESS, 0);
    for (String status : statuses) {
      int count = sampleReportService.countOfStatus(study, status);
      statusCount.put(status, count);
    }
    int current = 0;
    StringBuilder out = new StringBuilder();
    for (String status : statusCount.keySet()) {
      current++;
      int count = statusCount.get(status);
      if (NOT_STARTED.equals(status)) {
        status = "not started";
      }
      out.append("['" + status + "'," + count + "]");
      if (current != statusCount.keySet().size()) {
        out.append(",");
      }
    }
    modelAndView.addObject("overallChartData", out.toString());
  }

}
