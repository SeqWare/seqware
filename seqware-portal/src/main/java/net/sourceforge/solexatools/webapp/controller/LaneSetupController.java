package net.sourceforge.solexatools.webapp.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.solexatools.Debug;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;


/**
 * RegistrationSetupController
 */

public class LaneSetupController extends BaseCommandController {

  private LaneService laneService = null;
  
	public LaneSetupController() {
		super();
		this.setCommandName("command");
		this.setCommandClass(Lane.class);
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
		Lane lane = getRequestedLane(request);
		boolean isReport = request.getParameter("report") != null;

		Debug.put("LaneSetupController: command name is " + getCommandName());
		if (lane != null) {
		    System.err.println("Lane: command name is " + getCommandName());
			request.setAttribute(getCommandName(), lane);
			model.put("strategy", "update");
			if (!isReport) {
				modelAndView = new ModelAndView("Lane", model);
			} else {
				modelAndView = new ModelAndView("LaneReport", model);
			}
			
		}
		else {
		    lane = new Lane();
			request.setAttribute(getCommandName(), lane);
			model.put("strategy", "submit");
			modelAndView = new ModelAndView("Lane", model);
		}
		// save the lane object in the session
		HttpSession session = request.getSession(false);
		session.setAttribute("lane", lane);
		request.setAttribute(getCommandName(), lane);
		
		String typeTree = request.getParameter("tt");
		request.getSession(false).setAttribute("typeTree", typeTree);
		Log.info("TYPE TREE LANE SETUP = " + typeTree);
		
		// if study operation
	/*	if(typeTree!=null){
			if(typeTree.equals("st")){
				SetNodeIdInSession.setSampleForStudy(lane.getSample(), request);
			}		
			if(typeTree.equals("sr")){
				SetNodeIdInSession.setSequencerRun(lane.getSequencerRun().getSequencerRunId(), request);
			}		
			if(typeTree.equals("wfr")){
				SetNodeIdInSession.setSampleForWorkflowRun(lane.getSample(), request);
			}
			if(typeTree.equals("wfrr")){
				SetNodeIdInSession.setSampleForWorkflowRunRunning(lane.getSample(), request);
			}
		}
	*/	
		return modelAndView;
	}
	
  private Lane getRequestedLane(HttpServletRequest request) {
    Lane lane = null;
    HttpSession session   = request.getSession(false);
    //Study study = null;
    session.removeAttribute("lane");
    
    String    id      = (String)request.getParameter("laneId");
    if (id != null && !"".equals(id)) {
      lane = laneService.findByID(Integer.parseInt(id));
      //lane = (Lane)session.getAttribute("lane");
      session.setAttribute("lane", lane);
    }
    return lane;
  }
  
  public LaneService getLaneService() {
    return laneService;
  }

  public void setLaneService(LaneService laneService) {
    this.laneService = laneService;
  }
}

// ex:sw=4:ts=4:
