package	net.sourceforge.solexatools.webapp.controller;				// -*- tab-width: 4 -*-
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.WorkflowService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * RegistrationSetupController
 * This is invoked upon entry to Registration.jsp or RegistrationUpdate.jsp
 */
public class WorkflowListSetupController extends BaseCommandController {
  
  WorkflowService workflowService = null;
  
  public WorkflowListSetupController() {
    super();
    setSupportedMethods(new String[] {METHOD_GET});
  }

  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request,
      HttpServletResponse response)
  throws Exception {

    Registration registration = Security.getRegistration(request);
    if(registration == null)
      return new ModelAndView("redirect:/login.htm");
    
    ModelAndView modelAndView = null;
    HashMap<String,Object>  model     = new HashMap<String,Object>();
    
    List<Workflow> workflowList = getWorkflowService().list();
    
    if (workflowList != null) {
      //request.setAttribute(getCommandName(), workflow);
      model.put("strategy", "submit");
      model.put("workflowList", workflowList);
      modelAndView = new ModelAndView("WorkflowList", model);
    } 
    return modelAndView;
  }
  
  
  public WorkflowService getWorkflowService() {
    return workflowService;
  }

  public void setWorkflowService(WorkflowService workflowService) {
    this.workflowService = workflowService;
  }
}

// ex:sw=4:ts=4:
