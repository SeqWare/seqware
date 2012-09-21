package	net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.OrganismService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.Constant;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/**
 * SampleController
 */

public class SampleController extends MultiActionController {
  private ExperimentService experimentService;
  private SampleService sampleService;
  private OrganismService organismService;
  private Validator validator;

  public SampleController() {
    super();
  }

  public SampleController(Object delegate) {
    super(delegate);
  }

  public Validator getValidator() {
    return validator;
  }

  public void setValidator(Validator validator) {
    this.validator = validator;
  }

  public SampleService getSampleService() {
    return sampleService;
  }

  public void setSampleService(SampleService sampleService) {
    this.sampleService = sampleService;
  }

  public ExperimentService getExperimentService() {
    return experimentService;
  }

  public void setExperimentService(ExperimentService experimentService) {
    this.experimentService = experimentService;
  }

  public OrganismService getOrganismService() {
    return organismService;
  }

  public void setOrganismService(OrganismService organismService) {
    this.organismService = organismService;
  }

  /**
   * Handles the user's request to submit a new study.
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param command Study command object
   *
   * @return ModelAndView
   *
   * @throws Exception
   */
  public ModelAndView handleSubmit(HttpServletRequest		request,
      HttpServletResponse	response,
      Sample				command)
  throws Exception {

    Registration registration = Security.getRegistration(request);
    if(registration == null)
      return new ModelAndView("redirect:/login.htm");

    ModelAndView	modelAndView = null;
    BindingResult	errors = this.validate(request, command);
    if (errors.hasErrors()) {
      Map model = errors.getModel();
      model.put("organismList", getOrganismService().list(registration));
      
  //  Integer experimentId = Integer.parseInt(request.getParameter("experimentId"));
  //  request.setAttribute("experimentId", experimentId);
      
	  Integer experimentId = getRequestedExperimentId(request);
	  if (experimentId != null) {
		request.setAttribute("experimentId", experimentId);
	  } else {
		Integer parentSampleId = getRequestedParentId(request);
		request.setAttribute("parentSampleId", parentSampleId);
	  }
      
      request.setAttribute("swid",getCurrentSample(request).getSwAccession());
      model.put("strategy", "submit");
      modelAndView = new ModelAndView("Sample", model);
    } else {
      command.setOwner(registration);
      
      Integer experimentId = getRequestedExperimentId(request);
	  if (experimentId != null) {
	    command.setExperiment(getExperimentService().findByID(Integer.parseInt(request.getParameter("experimentId"))));
	    command.setOrganism(getOrganismService().findByID(command.getOrganismId()));
	    getSampleService().insert(command);
	  } else {
		Integer parentSampleId = getRequestedParentId(request);
		request.setAttribute("parentSampleId", parentSampleId);
		
		Sample parent = getSampleService().findByID(parentSampleId);
		// add New Sample to Parent
		parent.getChildren().add(command);
		// add Parent to New Sample
		command.getParents().add(parent);
	  }
      
//    command.setExperiment(getExperimentService().findByID(Integer.parseInt(request.getParameter("experimentId"))));
//    command.setOrganism(getOrganismService().findByID(command.getOrganismId()));
	  getSampleService().insert(command);
		
      modelAndView = new ModelAndView("redirect:/myStudyList.htm");
    }

    request.getSession(false).removeAttribute("sample");

    return modelAndView;
  }


  /**
   * Handles the user's request to reset the sample page during a new or
   * update sample.
   *
   * @param command Sample command object
   */
  public ModelAndView handleReset(HttpServletRequest request,
      HttpServletResponse response,
      Sample command) throws Exception {
	  
	Registration registration = Security.getRegistration(request);
	if(registration == null)
		return new ModelAndView("redirect:/login.htm");

    ModelAndView modelAndView = null;
    HashMap<String,Object> model = new HashMap<String,Object>();
    Sample sample = getCurrentSample(request);
    model.put("organismList", getOrganismService().list(registration));
    if (sample.getUpdateTimestamp() == null) {
    	sample = new Sample();
		sample.setOwner(registration);
		Integer experimentId = Integer.parseInt(request.getParameter("experimentId"));
		sample.setExperiment(getExperimentService().findByID(experimentId));
		request.setAttribute("experimentId", experimentId);
		model.put("strategy", "submit");
    } else {
		request.setAttribute("experimentId", sample.getExperiment().getExperimentId());
		model.put("swid", sample.getSwAccession());
		sample.setOrganismId(sample.getOrganism().getOrganismId());
    	model.put("strategy", "update");
    }
    modelAndView = new ModelAndView("Sample", model);
    request.setAttribute(getCommandName(command), sample);
    return modelAndView;
  }

  /**
   * Handles the user's request to cancel the study
   * or the study update page.
   *
   * @param command Study command object
   */
  public ModelAndView handleCancel(HttpServletRequest request,
      HttpServletResponse response,
      Sample command) throws Exception {

	// add experiment id
//	Integer experimentId = Integer.parseInt(request.getParameter("experimentId"));
//	request.getSession(false).setAttribute("nodeId", "exp_" + experimentId);
	
//	Experiment experiment = getExperimentService().findByID(experimentId);
//	Study rootStudy = FindRootUtil.getStudy(experiment);
//	request.getSession(false).setAttribute("rootStudyId", rootStudy.getStudyId());
//	request.getSession(false).setAttribute("objectId", "exp_" + experimentId);
	
    return new ModelAndView(getViewName(request));
  }
  
  private String getViewName(HttpServletRequest request){
	String typeTree = (String)request.getSession(false).getAttribute("typeTree");
	String viewName = Constant.getViewName(typeTree);
	request.getSession(false).removeAttribute("typeTree");
	return viewName;
  }

  /**
   * Handles the user's request to update their study.
   *
   * @param command Study command object
   */
  public ModelAndView handleUpdate(HttpServletRequest		request,
      HttpServletResponse	response,
      Sample				command)
  throws Exception {

    Registration registration = Security.getRegistration(request);
    if(registration == null)
      return new ModelAndView("redirect:/login.htm");

    ModelAndView	modelAndView= null;
    BindingResult	errors		= this.validate(request, command);
    if (errors.hasErrors()) {
      Map model = errors.getModel();
      model.put("organismList", getOrganismService().list(registration));
  //    Integer experimentId = Integer.parseInt(request.getParameter("experimentId"));
  //    request.setAttribute("experimentId", experimentId);
      model.put("strategy", "update");
      modelAndView = new ModelAndView("Sample", model);
    } else {
      Sample newSample = command;
      Sample oldSample = getCurrentSample(request);
      if (newSample != null && oldSample != null) {
        new ServletRequestDataBinder(oldSample).bind(request);
        oldSample.setOrganism(getOrganismService().findByID(newSample.getOrganismId()));
        
        // set variable which convert with String to Intager
        oldSample.setExpectedNumRuns(newSample.getExpectedNumRuns());
        oldSample.setExpectedNumReads(newSample.getExpectedNumReads());
        
        getSampleService().update(oldSample);
        
        // add sample id
	//	request.getSession(false).setAttribute("nodeId", "sam_" + oldSample.getSampleId());
		
		
	      // add experiment id
//	    Integer experimentId = oldSample.getExperiment().getExperimentId();
//		request.getSession(false).setAttribute("nodeId", "exp_" + experimentId);
		  
		//Experiment experiment = getExperimentService().findByID(experimentId);
//		Study rootStudy = FindRootUtil.getStudy(oldSample);
//		request.getSession(false).setAttribute("rootStudyId", rootStudy.getStudyId());
//		request.getSession(false).setAttribute("objectId", "exp_" + experimentId);
        
        modelAndView = new ModelAndView(getViewName(request));
        
        request.getSession(false).removeAttribute("sample");
      } else {
        modelAndView = new ModelAndView("redirect:/Error.htm");
      }
    }

   // request.getSession(false).removeAttribute("sample");

    return modelAndView;
  }
  
  /**
	 * Handles the user's request to delete their sample.
	 *
	 * @param command Sample command object
	 */
	public ModelAndView handleDelete(HttpServletRequest		request,
									 HttpServletResponse	response,
									 Sample				command)
		throws Exception {

		Registration registration = Security.getRegistration(request);
		if(registration == null)
			return new ModelAndView("redirect:/login.htm");

		ModelAndView			modelAndView	= null;
		HashMap<String,Object>	model			= new HashMap<String,Object>();
		Sample				    sample			= getRequestedSample(request);
		
		ServletContext context = this.getServletContext();
		String deleteRealFiles = context.getInitParameter("delete.files.for.node.deletion");

		if (sample != null) {
		    if(registration.equals(sample.getOwner()) || registration.isLIMSAdmin()){
		    	getSampleService().delete(sample, deleteRealFiles);
		 //   	Integer expId = sample.getExperiment().getExperimentId();
		 //   	Experiment exp = getExperimentService().findByID(expId);
		 //   	SetNodeIdInSession.setExperiment(exp, request);
		    }
		} 
		modelAndView = new ModelAndView("redirect:/myStudyList.htm", model);
		return modelAndView;
	}

  /**
   * Validates a study.
   *
   * @param command the Command instance as an Object
   *
   * @return BindingResult validation errors
   */
  private BindingResult validate(HttpServletRequest request, Object command) {
    BindingResult errors = new BindException(command, getCommandName(command));
    ValidationUtils.invokeValidator(getValidator(), command, errors);
    return errors;
  }

  /**
   * Gets the emailAddress from the study in the session.
   *
   * @param request HttpServletRequest
   *
   * @return the emailAddress from the study in the session, or null if
   * there is no study in the session
   */
  private String getNameFromSession(HttpServletRequest request) {
    return getCurrentSample(request).getName();
  }

  /**
   * Gets the sample from the session.
   *
   * @param request HttpServletRequest
   *
   * @return instance of Sample from the session, or a new instance
   * if the sample is not in the session (e.g. the user is not logged in)
   */
  private Sample getCurrentSample(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session != null) {
      Object sample = session.getAttribute("sample");
      if (sample != null) {
        return (Sample)sample;
      }
    }
    return new Sample();
  }
  
  private Integer getRequestedExperimentId(HttpServletRequest request) {
	Integer expID	= null;
	String	strId	= (String)request.getParameter("experimentId");
	if (strId != null && !strId.isEmpty()) {
		expID = Integer.parseInt(strId);
	}
	return expID;
  }
	
  private Integer getRequestedParentId(HttpServletRequest request) {
	Integer parentID= null;
	String	strId	= (String)request.getParameter("parentSampleId");
	if (strId != null && !strId.isEmpty()) {
		parentID = Integer.parseInt(strId);
	}
	return parentID;
  }

  private Sample getRequestedSample(HttpServletRequest request) {
	Sample	sample	= null;
	String	id		= (String)request.getParameter("objectId");
	if (id != null) {
		Integer sampleID = Integer.parseInt(id);
		sample = getSampleService().findByID(sampleID);
	}
	return sample;
  }

}
