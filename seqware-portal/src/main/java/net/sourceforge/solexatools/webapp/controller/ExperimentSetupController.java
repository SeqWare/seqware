package net.sourceforge.solexatools.webapp.controller;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.LibrarySelectionService;
import net.sourceforge.seqware.common.business.LibrarySourceService;
import net.sourceforge.seqware.common.business.LibraryStrategyService;
import net.sourceforge.seqware.common.business.PlatformService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;
import net.sourceforge.solexatools.util.SetNodeIdInSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * RegistrationSetupController
 */

public class ExperimentSetupController extends BaseCommandController {

  private StudyService studyService;
  private ExperimentService experimentService;
  private PlatformService platformService;
  private LibrarySelectionService librarySelectionService;
  private LibrarySourceService librarySourceService;
  private LibraryStrategyService libraryStrategyService;

  public ExperimentSetupController() {
    super();
    setSupportedMethods(new String[] {METHOD_GET});
  }

  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest	 request,
      HttpServletResponse response)
  throws Exception {

    Registration registration = Security.getRegistration(request);
    if(registration == null)
      return new ModelAndView("redirect:/login.htm");

    ModelAndView			modelAndView	= null;
    HashMap<String,Object>	model			= new HashMap<String,Object>();
    Experiment				experiment		= getRequestedExperiment(request);
    boolean isReport = request.getParameter("report") != null;

    model.put("platformList", getPlatformService().list(registration));
    model.put("expLibDesignSelectionList", getLibrarySelectionService().list(registration));
    model.put("expLibDesignSourceList", getLibrarySourceService().list(registration));
    model.put("expLibDesignStrategyList", getLibraryStrategyService().list(registration));
    if (experiment != null) {

      request.setAttribute(getCommandName(), experiment);
      request.setAttribute("studyId", experiment.getStudy().getStudyId());
      request.setAttribute("swid", experiment.getSwAccession());
      model.put("strategy", "update");

      // now fill in some variables that will setup the pulldowns
      // these are all non-persisted fields in the experiment object 
      /* model.put("platformId", experiment.getPlatform().getPlatformId());
			model.put("expLibDesignSelectionId", experiment.getExpLibDesignSelection());
			model.put("expLibDesignSourceId", experiment.getExpLibDesignSource());
			model.put("expLibDesignStrategyId", experiment.getExpLibDesignStrategy());*/
      experiment.setPlatformInt(experiment.getPlatform().getPlatformId());
      experiment.setExpLibDesignName(experiment.getExperimentLibraryDesign().getName());
      experiment.setExpLibDesignDesc(experiment.getExperimentLibraryDesign().getDescription());
      experiment.setExpLibDesignProtocol(experiment.getExperimentLibraryDesign().getConstructionProtocol());
      experiment.setExpLibDesignStrategy(experiment.getExperimentLibraryDesign().getStrategy().getLibraryStrategyId());
      experiment.setExpLibDesignSource(experiment.getExperimentLibraryDesign().getSource().getLibrarySourceId());
      experiment.setExpLibDesignSelection(experiment.getExperimentLibraryDesign().getSelection().getLibrarySelectionId());
      experiment.setSpotDesignReadSpec(experiment.getExperimentSpotDesign().getReadSpec());

    } else {
      experiment = new Experiment();
      experiment.setOwner(registration);
      Integer studyId = Integer.parseInt(request.getParameter("studyId"));
      experiment.setStudy(getStudyService().findByID(studyId));
      request.setAttribute(getCommandName(), experiment);
      request.setAttribute("studyId", studyId);
      model.put("strategy", "submit");
    }
    if (!isReport) {
    	modelAndView = new ModelAndView("Experiment", model);
    } else {
    	modelAndView = new ModelAndView("ExperimentReport", model);
    }
    
    // add study id
    setOpenNode(request);
    
    return modelAndView;
  }
  
  private void setOpenNode(HttpServletRequest request){
	  String studyId = request.getParameter("studyId");
	  if(studyId!=null){
		  SetNodeIdInSession.setStudy(Integer.parseInt(studyId), request);
	  }
  }

  private Experiment getRequestedExperiment(HttpServletRequest request) {
    HttpSession	session		= request.getSession(false);
    Experiment	experiment	= null;
    String		id			= (String)request.getParameter("experimentId");
    session.removeAttribute("experiment");

    if (id != null) {
      Integer expID = Integer.parseInt(id);
      experiment = getExperimentService().findByID(expID);
      session.setAttribute("experiment", experiment);
    }

    return experiment;
  }


  public LibrarySelectionService getLibrarySelectionService() {
    return librarySelectionService;
  }

  public void setLibrarySelectionService(
      LibrarySelectionService librarySelectionService) {
    this.librarySelectionService = librarySelectionService;
  }

  public LibrarySourceService getLibrarySourceService() {
    return librarySourceService;
  }

  public void setLibrarySourceService(LibrarySourceService librarySourceService) {
    this.librarySourceService = librarySourceService;
  }

  public LibraryStrategyService getLibraryStrategyService() {
    return libraryStrategyService;
  }

  public void setLibraryStrategyService(
      LibraryStrategyService libraryStrategyService) {
    this.libraryStrategyService = libraryStrategyService;
  }

  public PlatformService getPlatformService() {
    return platformService;
  }

  public void setPlatformService(PlatformService platformService) {
    this.platformService = platformService;
  }

  public ExperimentService getExperimentService() {
    return experimentService;
  }

  public void setExperimentService(ExperimentService experimentService) {
    this.experimentService = experimentService;
  }

  public StudyService getStudyService() {
    return studyService;
  }

  public void setStudyService(StudyService studyService) {
    this.studyService = studyService;
  }

}
