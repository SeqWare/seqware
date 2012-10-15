package net.sourceforge.solexatools.webapp.controller;

import com.google.gson.Gson;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.PaginationUtil;
import net.sourceforge.solexatools.webapp.metamodel.Flexigrid;
import net.sourceforge.solexatools.webapp.metamodel.Flexigrid.Cells;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * RegistrationSetupController
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class AnalisysTableControllerDetails extends BaseCommandController {

    private WorkflowService workflowService;
    private WorkflowRunService workflowRunService;
    private Flexigrid flexigrid;

    /**
     * <p>Constructor for AnalisysListController.</p>
     */
    public AnalisysTableControllerDetails() {
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

	int page = 1;
	int rowsPages = 15;
	try {
	    page = Integer.parseInt(pageStr);
	    rowsPages = Integer.parseInt(rowsPagesStr);
	} catch (NumberFormatException e) {
	    e.printStackTrace();
	}

	if (workflowRunService != null) {

	    // This workflows. Statuses are generated for them.
	    // We sort them to have the same model as we have
	    // while generating table model

	    response.setContentType("application/json");

	    List<WorkflowRun> workflowRuns = null;
	    
	    if ("canceled".equals(filter)) {
		workflowRuns = workflowRunService.findByCriteria("wr.owner.registrationId = "+registration.getRegistrationId()
			+" and ( wr.status = 'canceled' or wr.status = 'cancelled')");
	    } else if ("failed".equals(filter)) {
		workflowRuns = workflowRunService.findByCriteria("wr.owner.registrationId = "+registration.getRegistrationId()
			+" and (wr.status = 'failed' or wr.status = 'failed-testing')");
	    } else if ("running".equals(filter)) {
		workflowRuns = workflowRunService.findByCriteria("wr.owner.registrationId = "+registration.getRegistrationId()
			+" and ( wr.status = 'runnning' or wr.status = 'pending')");
	    } else {
		workflowRuns = workflowRunService.findByOwnerID(registration.getRegistrationId());
	    }

	    String json = createSampleTableJson(workflowRuns, page, rowsPages, sortName, sortOrder);
	    response.getWriter().write(json);
	    response.flushBuffer();

	}

	return null;


    }

    private String createSampleTableJson(List<WorkflowRun> workflowRuns, int page, int rp, String sortName, String sortOrder) {

	// FIXME:
	flexigrid = createSampleFlexigrid(workflowRuns, page);

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

    private Flexigrid createSampleFlexigrid(List<WorkflowRun> workflowRuns, int page) {

	Flexigrid flexigrid = new Flexigrid(workflowRuns.size(), page);
	for (WorkflowRun workflowRun : workflowRuns) {

	    List<String> cellsModel = new LinkedList<String>();
	    cellsModel.add(workflowRun.getCreateTimestamp().toString());
	    cellsModel.add(workflowRun.getWorkflow().getName());
	    cellsModel.add(workflowRun.getWorkflow().getVersion());
	    cellsModel.add(workflowRun.getStatus());
	    cellsModel.add(workflowRun.getSwAccession().toString());
	    cellsModel.add(workflowRun.getHost());
	    if ("failed".equals(workflowRun.getStatus())) {
		cellsModel.add("<a href='#' popup-stderr='true' tt='wfr' stdout='"+workflowRun.getStdOut()+"' stderr='"+workflowRun.getStdErr()+"' >share</a>");
		cellsModel.add(workflowRun.getStdErr());
	    } else {
		cellsModel.add("");
		cellsModel.add("");
	    }

	    Flexigrid.Cells cells = flexigrid.new Cells(cellsModel);
	    flexigrid.addRow(cells);
	}
	return flexigrid;
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
	    comparator = new AnalisysTableControllerDetails.CellsComparator(columnPos);
	} else if ("desc".equals(sortOrder)) {
	    comparator = Collections.reverseOrder(new AnalisysTableControllerDetails.CellsComparator(columnPos));
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
     * <code>workflowService</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.business.WorkflowService}
     * object.
     */
    public WorkflowService getWorkflowService() {
	return workflowService;
    }

    /**
     * <p>Setter for the field
     * <code>workflowService</code>.</p>
     *
     * @param workflowService a
     * {@link net.sourceforge.seqware.common.business.WorkflowService} object.
     */
    public void setWorkflowService(WorkflowService workflowService) {
	this.workflowService = workflowService;
    }

    /**
     * <p>Getter for the field
     * <code>workflowRunService</code>.</p>
     *
     * @return a
     * {@link net.sourceforge.seqware.common.business.WorkflowRunService}
     * object.
     */
    public WorkflowRunService getWorkflowRunService() {
	return workflowRunService;
    }

    /**
     * <p>Setter for the field
     * <code>workflowRunService</code>.</p>
     *
     * @param workflowRunService a
     * {@link net.sourceforge.seqware.common.business.WorkflowRunService}
     * object.
     */
    public void setWorkflowRunService(WorkflowRunService workflowRunService) {
	this.workflowRunService = workflowRunService;
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
