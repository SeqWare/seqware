package net.sourceforge.solexatools.webapp.controller;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sourceforge.seqware.common.business.StudyService;

import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.PaginationUtil;
import net.sourceforge.solexatools.webapp.metamodel.Flexigrid;
import net.sourceforge.solexatools.webapp.metamodel.Flexigrid.Cells;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * 
 * TODO:
 * 
 *  * it would be best if this controller would actually render the workflow_runs
 *    too. That will require paging to work properly in the Hibernate layer
 *    which VRP never actually did (no wonder the app is so slow!).
 * 
 * RegistrationSetupController
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StudyTableControllerDetails extends BaseCommandController {

  private StudyService studyService;
  private Flexigrid flexigrid;

  /**
   * <p>Constructor for AnalisysListController.</p>
   */
  public StudyTableControllerDetails() {
    super();
    setSupportedMethods(new String[]{METHOD_GET, METHOD_POST});
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request,
          HttpServletResponse response)
          throws Exception {

    //Registration registration = Security.requireRegistration(request, response);
    Registration registration = Security.getRegistration(request);
    if (registration == null) {
      return (null);
    }

    String tableSel = request.getParameter("tablesel");
    String tableModel = request.getParameter("tablemodel");
    String sortName = request.getParameter("sortname");
    String sortOrder = request.getParameter("sortorder");
    String pageStr = request.getParameter("page");
    String rowsPagesStr = request.getParameter("rp");
    String filter = request.getParameter("filter");
    String qtype = request.getParameter("qtype");
    String query = request.getParameter("query");

    int page = 1;
    int rowsPages = 15;
    try {
      page = Integer.parseInt(pageStr);
      rowsPages = Integer.parseInt(rowsPagesStr);
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }

    if (studyService != null) {

      // This workflows. Statuses are generated for them.
      // We sort them to have the same model as we have
      // while generating table model

      response.setContentType("application/json");

      List<Study> studies = null;

      String search_query = "";
      if (qtype != null && !"".equals(qtype) && query != null && !"".equals(query)) {
        search_query = " and cast(wr." + qtype + " as string) like '%" + query + "%'";
      }
      
      // LEFT OFF HERE!!!

      studies = studyService.list(registration);

      String json = createStudyTableJson(studies, page, rowsPages, sortName, sortOrder);
      response.getWriter().write(json);
      response.flushBuffer();

    }

    return null;


  }

  private String createStudyTableJson(List<Study> studies, int page, int rp, String sortName, String sortOrder) {

    // FIXME:
    flexigrid = createStudyFlexigrid(studies, page);

    if (flexigrid == null) {
      //flexigrid = createSampleFlexigrid(workflowRuns, page);
    }

    flexigrid.setPage(page);
    List<Cells> rowsAll = flexigrid.getRows();

    if (!"undefined".equals(sortOrder)) {
      //long start = System.nanoTime();
      sortRows(rowsAll, sortOrder, sortName);
      //long end = System.nanoTime() - start;
      //Log.info("Sorting SampleTable Time: " + end / 1e6);
    }
    @SuppressWarnings("unchecked")
    List<Cells> pagedCells = PaginationUtil.subList(page - 1, rp, rowsAll);
    flexigrid.setRows(pagedCells);

    // Convert to Flexigrid JSON
    Gson gson = new Gson();
    String json = gson.toJson(flexigrid);
    flexigrid.setRows(rowsAll);
    return json;
  }

  private Flexigrid createStudyFlexigrid(List<Study> studies, int page) {

    Flexigrid flexigrid = new Flexigrid(studies.size(), page);
    for (Study study : studies) {

      for (Experiment exp : study.getExperiments()) {

        for (Sample sample : exp.getSamples()) {

          ArrayList<String> sampleStrs = new ArrayList<String>();
          getSampleStrings(sample.getTitle(), sample, sampleStrs);
          for (String sampleStr : sampleStrs) {
            List<String> cellsModel = new LinkedList<String>();

            cellsModel.add(study.getTitle());
            cellsModel.add(study.getCreateTimestamp().toString());
            cellsModel.add(exp.getTitle());
            cellsModel.add(sampleStr);
            cellsModel.add("workflows");
            Flexigrid.Cells cells = flexigrid.new Cells(cellsModel);
            flexigrid.addRow(cells);
          }
          /* // not doing this right now
          HashMap<WorkflowRun, String> workflows = new HashMap<WorkflowRun, String>();
          getWorkflowRuns(sample, workflows, "");

          for (WorkflowRun workflow : workflows.keySet()) {

            List<String> cellsModel = new LinkedList<String>();

            cellsModel.add(study.getTitle());
            cellsModel.add(study.getCreateTimestamp().toString());
            cellsModel.add(exp.getTitle());

            String sampleStr = workflows.get(workflow);
            cellsModel.add(sampleStr);
            cellsModel.add(workflow.getWorkflow().getName()+" "+workflow.getWorkflow().getVersion());

            Flexigrid.Cells cells = flexigrid.new Cells(cellsModel);
            flexigrid.addRow(cells);
          }
          */
        }   
      }
    }
    return flexigrid;
  }
  
  /**
   * This finds all the possible paths to leaf sample nodes.
   * @param currString
   * @param s
   * @param sampleStrs 
   */
  private void getSampleStrings(String currString, Sample s, ArrayList<String> sampleStrs) {
    if (s.getChildren() == null || s.getChildren().size() == 0) {
      sampleStrs.add(currString);
    } else {
      for (Sample s2 : s.getChildren()) {
        getSampleStrings(currString + " / " + s2.getTitle(), s2, sampleStrs);
      }
    }
  }
  
  private void getWorkflowRuns(Sample sample, HashMap<WorkflowRun, String> workflows, String sampleString) {
    
    String sampleStr = sampleString + " / " + sample.getTitle();
    
    // find any workflows
    List<WorkflowRun> workflowList = findSampleWorkflowRuns(sample);
    for(WorkflowRun w : workflowList) {
      workflows.put(w, sampleStr);
    }
    
    /*for (Sample child : sample.getChildren()) {
      getWorkflowRuns(child, workflows, sampleStr);
    }*/

  }
  
  private List<WorkflowRun> findSampleWorkflowRuns(Sample sample) {
    ArrayList<WorkflowRun> wr = new ArrayList<WorkflowRun>();
    HashMap<String, WorkflowRun> pwr = new HashMap<String, WorkflowRun>();
    findProcessingWorkflowRun(sample.getProcessings(), pwr);
    // the hash should be unique, now dump everything into array list for return
    for (WorkflowRun cwr : pwr.values()) {
      wr.add(cwr);
    }
    return(wr);
  }
  
  private void findProcessingWorkflowRun(Set<Processing> processings, HashMap<String, WorkflowRun> pwr) {
    for (Processing p : processings) {
      WorkflowRun directWr = p.getWorkflowRun();
      WorkflowRun ancestorWr = p.getWorkflowRunByAncestorWorkflowRunId();
      if (directWr != null) { pwr.put(directWr.getSwAccession().toString(), directWr); }
      if (ancestorWr != null) { pwr.put(ancestorWr.getSwAccession().toString(), ancestorWr); }
      Log.stderr(p.getAlgorithm()+" "+p.getSwAccession()+" "+directWr);
      //findProcessingWorkflowRun(p.getChildren(), pwr);
    }
  }

  private String sampleToHtml(Set<Sample> samples) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    if (samples != null) {
      for (Sample s : samples) {
        if (!first) {
          sb.append(", ");
        }
        sb.append(s.getTitle() + " (SWID:" + s.getSwAccession() + ")");
        first = false;
      }
    }
    return (sb.toString());
  }

  private String iUSToHtml(Set<IUS> ius) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    if (ius != null) {
      for (IUS s : ius) {
        if (!first) {
          sb.append(", ");
        }
        sb.append(s.getName() + " (SWID:" + s.getSwAccession() + ")");
        first = false;
      }
    }
    return (sb.toString());
  }

  private String laneToHtml(Set<Lane> lanes) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    if (lanes != null) {
      for (Lane s : lanes) {
        if (!first) {
          sb.append(", ");
        }
        sb.append(s.getName() + " (SWID:" + s.getSwAccession() + ")");
        first = false;
      }
    }
    return (sb.toString());
  }

  private void sortRows(List<Cells> rowsAll, String sortOrder, String sortName) {
    int columnPos = 0;
    if ("date".equals(sortName)) {
      columnPos = 0;
    } else if ("status".equals(sortName)) {
      columnPos = 1;
    } else if ("swid".equals(sortName)) {
      columnPos = 2;
    }

    @SuppressWarnings("rawtypes")
    Comparator comparator = null;
    if ("asc".equals(sortOrder)) {
      comparator = new StudyTableControllerDetails.CellsComparator(columnPos);
    } else if ("desc".equals(sortOrder)) {
      comparator = Collections.reverseOrder(new StudyTableControllerDetails.CellsComparator(columnPos));
    }

    Collections.sort(rowsAll, comparator);
  }

  private void initSortingTreeAttr(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session.getAttribute("ascMyListAnalysis") == null) {
      session.setAttribute("ascMyListAnalysis", true);
      session.setAttribute("ascMySharedAnalysises", true);
      session.setAttribute("ascAnalysisesSharedWithMe", true);
      session.setAttribute("ascMyRunningListAnalysis", true);
    }
  }

  /**
   * <p>Getter for the field
   * <code>studyService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.StudyService}
   * object.
   */
  public StudyService getStudyService() {
    return studyService;
  }

  /**
   * <p>Setter for the field
   * <code>studyService</code>.</p>
   *
   * @param studyService a
   * {@link net.sourceforge.seqware.common.business.StudyService} object.
   */
  public void setStudyService(StudyService studyService) {
    this.studyService = studyService;
  }

  @SuppressWarnings({"rawtypes"})
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
}
