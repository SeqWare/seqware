package net.sourceforge.solexatools.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.model.Registration;

import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * RegistrationSetupController
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StudyTableController extends BaseCommandController {

  private StudyService studyService;

  /**
   * <p>Constructor for AnalisysListController.</p>
   */
  public StudyTableController() {
    super();
    setSupportedMethods(new String[]{METHOD_GET});
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request,
          HttpServletResponse response)
          throws Exception {

    //Registration registration = Security.requireRegistration(request, response);
    Registration registration = Security.getRegistration(request);
    if (registration == null) {
      return new ModelAndView("redirect:/login.htm");
    }

    initSortingTreeAttr(request);

    ModelAndView modelAndView = new ModelAndView("StudyTable");
    modelAndView.addObject("registration", registration);

    return modelAndView;
  }

  private void initSortingTreeAttr(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session.getAttribute("ascMyListAnalysis") == null) {
      session.setAttribute("ascMyListAnalysis", true);
      session.setAttribute("ascMySharedAnalysises", true);
      session.setAttribute("ascAnalysisesSharedWithMe", true);
      session.setAttribute("ascMyRunningListAnalysis", true);
    }
  }

  public StudyService getStudyService() {
    return studyService;
  }

  public void setStudyService(StudyService studyService) {
    this.studyService = studyService;
  }
}
