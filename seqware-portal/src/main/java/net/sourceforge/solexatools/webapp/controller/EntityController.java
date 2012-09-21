package net.sourceforge.solexatools.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * The controller. Dispatch to proper Entity determined by SWID. 
 * 
 * @author Oleg Lopatin
 *
 */
public class EntityController extends BaseCommandController {

	// Services Used for Search
	private StudyService studyService;
	private ExperimentService experimentService;
	private SampleService sampleService;
	private IUSService IUSService;
	private SequencerRunService sequencerRunService;
	private LaneService laneService;
	private ProcessingService processingService;
	private FileService fileService;
	private WorkflowService workflowService;
	private WorkflowRunService workflowRunService;

	public EntityController() {
	}

	public void setStudyService(StudyService studyService) {
		this.studyService = studyService;
	}

	public void setExperimentService(ExperimentService experimentService) {
		this.experimentService = experimentService;
	}

	public void setSampleService(SampleService sampleService) {
		this.sampleService = sampleService;
	}

	public void setIUSService(IUSService iusService) {
		this.IUSService = iusService;
	}

	public void setSequencerRunService(SequencerRunService sequencerRunService) {
		this.sequencerRunService = sequencerRunService;
	}

	public void setLaneService(LaneService laneService) {
		this.laneService = laneService;
	}

	public void setProcessingService(ProcessingService processingService) {
		this.processingService = processingService;
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	public void setWorkflowService(WorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	public void setWorkflowRunService(WorkflowRunService workflowRunService) {
		this.workflowRunService = workflowRunService;
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Object foundEntity = findEntity(request);
		if (foundEntity == null) {
			ModelAndView modelAndView = new ModelAndView("noswfound");
			modelAndView.addObject("sw", request.getParameter("sw"));
			return modelAndView;
		}
		ModelAndView modelAndView = getModel(foundEntity);
		return modelAndView;
	}

	private ModelAndView getModel(Object foundEntity) {
		ModelAndView modelAndView = null;
		if (foundEntity instanceof Study) {
			modelAndView = new ModelAndView("forward:/studyUpdateSetup.htm?studyID=" + ((Study)foundEntity).getStudyId() + "&report=yes");
		} else if (foundEntity instanceof Experiment) {
			modelAndView = new ModelAndView("forward:/experimentSetup.htm?experimentId=" + ((Experiment)foundEntity).getExperimentId() + "&report=yes");
		} else if (foundEntity instanceof Sample) {
			modelAndView = new ModelAndView("forward:/sampleSetup.htm?sampleId=" + ((Sample)foundEntity).getSampleId() + "&report=yes");
		} else if (foundEntity instanceof IUS) {
			modelAndView = new ModelAndView("forward:/iusSetup.htm?iusID=" + ((IUS)foundEntity).getIusId());
		} else if (foundEntity instanceof SequencerRun) {
			modelAndView = new ModelAndView("forward:/sequencerRunWizardEdit.htm?sequencerRunId=" + ((SequencerRun)foundEntity).getSequencerRunId() + "&report=yes");
		} else if (foundEntity instanceof Lane) {
			modelAndView = new ModelAndView("forward:/laneSetup.htm?laneId=" + ((Lane)foundEntity).getLaneId() + "&report=yes");
		} else if (foundEntity instanceof Processing) {
			modelAndView = new ModelAndView("forward:/processingSetup.htm?procID=" + ((Processing)foundEntity).getProcessingId() + "&report=yes");
		} else if (foundEntity instanceof File) {
			modelAndView = new ModelAndView("forward:/fileSetup.htm?fileID=" + ((File)foundEntity).getFileId());
		} else if (foundEntity instanceof Workflow) {
			modelAndView = new ModelAndView("forward:/workflowSetup.htm?workflowId=" + ((Workflow)foundEntity).getWorkflowId() + "&report=yes");
		} else if (foundEntity instanceof WorkflowRun) {
			modelAndView = new ModelAndView("forward:/workflowRunSetup.htm?workflowRunId=" + ((WorkflowRun)foundEntity).getWorkflowRunId());
		}
		return modelAndView;
	}

	private Object findEntity(HttpServletRequest request) {
		Integer sw = null;
		try {
			sw = Integer.valueOf(request.getParameter("sw"));
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			return null;
		}
		
		if (sw == null)
			return null;
		
		Object res = studyService.findBySWAccession(sw);
		if (res != null) {
			return res;
		}
			
		res = experimentService.findBySWAccession(sw);
		if (res != null) {
			return res;
		}
		
		res = sampleService.findBySWAccession(sw);
		if (res != null) {
			return res;
		}
		
		res = IUSService.findBySWAccession(sw);
		if (res != null) {
			return res;
		}
		
		res = sequencerRunService.findBySWAccession(sw);
		if (res != null) {
			return res;
		}
		
		res = laneService.findBySWAccession(sw);
		if (res != null) {
			return res;
		}
		
		res = processingService.findBySWAccession(sw);
		if (res != null) {
			return res;
		}
		
		res = fileService.findBySWAccession(sw);
		if (res != null) {
			return res;
		}
		
		res = workflowService.findBySWAccession(sw);
		if (res != null) {
			return res;
		}
		
		res = workflowRunService.findBySWAccession(sw);
		if (res != null) {
			return res;
		}
		
		return null;
	}

}
