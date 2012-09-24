package net.sourceforge.solexatools.webapp.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.solexatools.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SequencerRunReportSetupController {

  @Autowired
  private SequencerRunService sequencerRunService;

  public void setSequencerRunService(SequencerRunService service) {
    this.sequencerRunService = service;
  }

  @RequestMapping("/reportSeqRunSetup.htm")
  public ModelAndView doSetupReport(HttpServletRequest request) {
    Registration registration = Security.getRegistration(request);
    if (registration == null) {
      return new ModelAndView("redirect:/login.htm");
    }

    List<SequencerRun> runs = sequencerRunService.list(registration);
    ModelAndView modelAndView = new ModelAndView("ReportSequencerRunSetup");
    modelAndView.addObject("seqRuns", runs);
    return modelAndView;
  }

}
