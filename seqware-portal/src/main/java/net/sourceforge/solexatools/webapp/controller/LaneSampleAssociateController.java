package	net.sourceforge.solexatools.webapp.controller;				// -*- tab-width: 4 -*-
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
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
public class LaneSampleAssociateController extends BaseCommandController {
  
  SampleService sampleService = null;
  LaneService laneService = null;
  IUSService iusService = null;
  
  /**
   * <p>Constructor for LaneSampleAssociateController.</p>
   */
  public LaneSampleAssociateController() {
    super();
    setSupportedMethods(new String[] {METHOD_GET, METHOD_POST});
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
    Sample sample = getRequestedSample(request);

    if (lane != null && sample != null) {
      
      lane.setSample(sample);
      // only set the lane's owner to sample owner if this lane has no tags/barcodes
      if(sample.getTags() == null || sample.getTags().length() == 0) lane.setOwner(sample.getOwner());
      laneService.update(lane);
      // create an IUS for each tag
      if (sample.getTags() != null) {
        String[] tags = sample.getTags().split(",");
        for (String tag : tags) {
          IUS ius = new IUS();
          ius.setLane(lane);
          ius.setSample(sample);
          ius.setTag(tag);
          ius.setName("ius_"+tag+"_from_"+lane.getName());
          ius.setOwner(sample.getOwner());
          iusService.insert(ius);
        }
      } else { // otherwise just create one IUS per Sample/Lane combo
        IUS ius = new IUS();
        ius.setLane(lane);
        ius.setSample(sample);
        ius.setName("ius_from_"+lane.getName());
        ius.setOwner(sample.getOwner());
        iusService.insert(ius);
      }
      
      
      return new ModelAndView("redirect:/sequencerRunWizardEdit.htm?sequencerRunId="+(String)request.getParameter("sequencerRunId"));
    } else {
      return new ModelAndView("redirect:/Error.htm");
    }
  }
  
  private Lane getRequestedLane(HttpServletRequest request) {
    Lane lane = null;
    HttpSession session   = request.getSession(false);
    String    id      = (String)request.getParameter("laneId");
    if (id != null && !"".equals(id)) {
      lane = laneService.findByID(Integer.parseInt(id));
    }
    return(lane);
  }
  
  private Sample getRequestedSample(HttpServletRequest request) {
    Sample sample = null;
    HttpSession session   = request.getSession(false);
    String    id      = (String)request.getParameter("sampleId");
    if (id != null && !"".equals(id)) {
      sample = sampleService.findByID(Integer.parseInt(id));
    }
    return(sample);
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

  /**
   * <p>Getter for the field <code>iusService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.IUSService} object.
   */
  public IUSService getIusService() {
    return iusService;
  }

  /**
   * <p>Setter for the field <code>iusService</code>.</p>
   *
   * @param iusService a {@link net.sourceforge.seqware.common.business.IUSService} object.
   */
  public void setIusService(IUSService iusService) {
    this.iusService = iusService;
  }
  
  /**
   * <p>getIUSService.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.IUSService} object.
   */
  public IUSService getIUSService() {
    return iusService;
  }

  /**
   * <p>setIUSService.</p>
   *
   * @param iusService a {@link net.sourceforge.seqware.common.business.IUSService} object.
   */
  public void setIUSService(IUSService iusService) {
    this.iusService = iusService;
  }

}
