package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.FileTypeService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.model.FileType;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.UploadSequence;
import net.sourceforge.solexatools.Security;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * <p>UploadSequenceSetupController class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class UploadSequenceSetupController extends BaseCommandController{
	private SampleService sampleService;
	private FileTypeService fileTypeService;

	/**
	 * <p>Constructor for UploadSequenceSetupController.</p>
	 */
	public UploadSequenceSetupController() {
		super();
		setSupportedMethods(new String[] {METHOD_GET});
	}

	/** {@inheritDoc} */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest	 request,
												 HttpServletResponse response)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();
		Sample				sample		= getRequestedSample(request);
		
		UploadSequence uploadSequence = new UploadSequence();
		//model.put("organismList", getOrganismService().list(registration));
		
		List<Sample> listSample = getSampleService().listSample(registration);
		
	    List<FileType> listFileType = getFileTypeService().list();
	    model.put("listFileType", listFileType);
		
		// if operation Upload cant be launch
		if(sample==null && listSample.size()==0){
			BindingResult errors = new BindException(uploadSequence, getCommandName());
			errors.reject("upload.required.sample");
			Map errorModel = errors.getModel();
			errorModel.put("strategy", "any_node");
			errorModel.put("sampleList", listSample);
			modelAndView = new ModelAndView("UploadSequence", errorModel);
		}else{
			if(sample != null){
				model.put("strategy", "defined_node");
				uploadSequence.setSample(sample);
				model.put("sample", sample);
				//listSample = setEndElement(sample, listSample);
			}else{
				model.put("strategy", "any_node");
				// get list sample for view on the web page
				model.put("sampleList", listSample);
			}
			request.setAttribute(getCommandName(), uploadSequence);
			modelAndView = new ModelAndView("UploadSequence", model);
		}
		 String typeTree = request.getParameter("tt");
		 request.getSession(false).setAttribute("typeTree", typeTree);
/*
		if (sample != null) {
			request.setAttribute(getCommandName(), sample);
			model.put("strategy", "update");
			sample.setOrganismId(sample.getOrganism().getOrganismId());
			modelAndView = new ModelAndView("Sample", model);
		} else {
			sample = new Sample();
			sample.setOwner(registration);
			sample.setExperiment(getExperimentService().findByID(Integer.parseInt(request.getParameter("experimentId"))));
			request.setAttribute(getCommandName(), sample);
			model.put("strategy", "submit");
			modelAndView = new ModelAndView("Sample", model);
		}*/
		
	/*	
		 String typeTree = request.getParameter("tt");
		 request.getSession(false).setAttribute("typeTree", typeTree);
		 Log.info("TYPE TREE SETUP UPLOAD = " + typeTree);
		 if(typeTree!= null){
			 if(typeTree.equals("st")){
				 if(sample != null){
					 SetNodeIdInSession.setExperiment(sample.getExperiment(), request);
				 }
			 }
			 if(typeTree.equals("wfr")){
				 SetNodeIdInSession.setWorkflowRunWithSample(request);
			 }
			 if(typeTree.equals("wfrr")){
				 SetNodeIdInSession.setWorkflowRunRunningWithSample(request);
			 }
		 }
	*/	
		return modelAndView;
	}

	private Sample getRequestedSample(HttpServletRequest request) {
		HttpSession	session		= request.getSession(false);
		Sample	sample	= null;
		String		id			= (String)request.getParameter("sampleId");
		session.removeAttribute("uploadSample");
		if (id != null) {
			Integer expID = Integer.parseInt(id);
			sample = getSampleService().findByID(expID);
			session.setAttribute("uploadSample", sample);
		}
		return sample;
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
	 * <p>Getter for the field <code>fileTypeService</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.business.FileTypeService} object.
	 */
	public FileTypeService getFileTypeService() {
		return fileTypeService;
	}

	/**
	 * <p>Setter for the field <code>fileTypeService</code>.</p>
	 *
	 * @param fileTypeService a {@link net.sourceforge.seqware.common.business.FileTypeService} object.
	 */
	public void setFileTypeService(FileTypeService fileTypeService) {
		this.fileTypeService = fileTypeService;
	}
}
