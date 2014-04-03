package	net.sourceforge.solexatools.webapp.controller;				// -*- tab-width: 4 -*-
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * RegistrationSetupController
 * This is invoked upon entry to Registration.jsp or RegistrationUpdate.jsp
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LaneSampleAssociateSetupController extends BaseCommandController {
  
  SampleService sampleService = null;
  LaneService laneService = null;
  
  /**
   * <p>Constructor for LaneSampleAssociateSetupController.</p>
   */
  public LaneSampleAssociateSetupController() {
    super();
    setSupportedMethods(new String[] {METHOD_GET});
  }

  /** {@inheritDoc} */
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

    System.err.println("LANE: "+lane.getLaneId());

    if (lane != null) {
      System.err.println("LANE: "+lane.getLaneId());
      model.put("strategy", "submit");
      // TODO populate list of samples, lane object
      model.put("laneId", lane.getLaneId());
      model.put("sequencerRunId", (String)request.getParameter("sequencerRunId"));
      System.err.println("Is sampleService null? "+sampleService);
      System.err.println("list: "+sampleService.listComplete());
      model.put("completeSamples", sampleService.listComplete());
      model.put("incompleteSamples", sampleService.listIncomplete());
      System.err.println("COMPLETE: "+sampleService.listComplete().size());
      System.err.println("INCOMPLETE: "+sampleService.listIncomplete().size());
      modelAndView = new ModelAndView("LaneSampleAssociation", model);
      request.setAttribute(getCommandName(), lane);
    } else {
      return new ModelAndView("redirect:/Error.htm");
    }
    return modelAndView;
  }
  
  private Lane getRequestedLane(HttpServletRequest request) {
    Lane lane = null;
    HttpSession session   = request.getSession(false);
    String    id      = (String)request.getParameter("laneId");
    System.err.println("Looking up lane: "+id);
    if (id != null && !"".equals(id)) {
      lane = laneService.findByID(Integer.parseInt(id));
    }
    if (lane == null) {
      System.err.println("Lane is null!");
    }
    return(lane);
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
   * <p>Getter for the field <code>laneService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.LaneService} object.
   */
  public LaneService getLaneService() {
    return laneService;
  }

  /**
   * <p>Setter for the field <code>laneService</code>.</p>
   *
   * @param laneService a {@link net.sourceforge.seqware.common.business.LaneService} object.
   */
  public void setLaneService(LaneService laneService) {
    this.laneService = laneService;
  }

}

// ex:sw=4:ts=4:
