package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

public class SampleWorkflowRunsController extends BaseCommandController {

	// Services
	private SampleService sampleService;
	private WorkflowRunService workflowRunService;

	// URL Params
	public final static String SAMPLE_SWID = "sw";

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Registration registration = Security.getRegistration(request);
		if (registration == null) {
			return new ModelAndView("redirect:/login.htm");
		}

		String idStr = request.getParameter(SAMPLE_SWID);

		int sampleId = 0;

		try {
			sampleId = Integer.parseInt(idStr);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		Map<Workflow, Set<WorkflowRun>> tableModel = new HashMap<Workflow, Set<WorkflowRun>>();
		Set<Workflow> usedWorkflows = new HashSet<Workflow>();
		Sample currentSample = sampleService.findBySWAccession(sampleId);

		if (currentSample != null) {

			Set<WorkflowRun> runs = new HashSet<WorkflowRun>();
			for (IUS ius : currentSample.getIUS()) {
				// Get All required runs belongs to Sample IUSs
				runs.addAll(workflowRunService.findRunsForIUS(ius));
			}

			// Now map each workflowRun to its workflow
			for (WorkflowRun run : runs) {
				Workflow wf = run.getWorkflow();
				Set<WorkflowRun> wfWorkflowRuns = tableModel.get(wf);
				// If no mapping for current Workflow
				if (wfWorkflowRuns == null) {
					wfWorkflowRuns = new HashSet<WorkflowRun>();
				}
				wfWorkflowRuns.add(run);
				tableModel.put(wf, wfWorkflowRuns);
				usedWorkflows.add(wf);
			}

			ModelAndView modelAndView = new ModelAndView(
					"StudySampleWorkflowRuns");
			modelAndView.addObject("usedWorkflows", usedWorkflows);
			modelAndView.addObject("tableModel", tableModel);
			modelAndView.addObject("sample", currentSample);
			return modelAndView;

		} else {
			throw new Exception("Wrong value for Study SWID " + idStr);
		}
	}

	public void setSampleService(SampleService sampleService) {
		this.sampleService = sampleService;
	}

	public void setWorkflowRunService(WorkflowRunService workflowRunService) {
		this.workflowRunService = workflowRunService;
	}
}
