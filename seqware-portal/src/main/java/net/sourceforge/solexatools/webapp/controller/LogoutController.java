package net.sourceforge.solexatools.webapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sourceforge.solexatools.util.LaunchWorkflowUtil;
import net.sourceforge.solexatools.util.SetNodeIdInSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


public class LogoutController extends AbstractController {

  public LogoutController() {
    // TODO Auto-generated constructor stub
    super();
    setSupportedMethods(new String[] {METHOD_GET});
  }

  /**
   * Handles a user's logout request.
   */
  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request,
                  HttpServletResponse response)
                  throws Exception {
          HttpSession session = request.getSession(false);
          if (session != null) {
	          session.removeAttribute("registration");
	          
	          // Dowload files
	          session.removeAttribute("bulkDownloadFilePage");
	          session.removeAttribute("bulkDownloadFiles");
	          session.removeAttribute("analysisBulkDownloadFiles");
	          
	          session.removeAttribute("selectInputPage");
	          
	          
	          // Selected Nodes Id
	          session.removeAttribute("selectedNodes");
	          session.removeAttribute("analysisSelectedNodes");
	          session.removeAttribute("launchSelectedNodes");
	          
	          // Study Bulk Download
	          session.removeAttribute("myBulkDownloadPage");
	          session.removeAttribute("bulkDownloadSharedWithMe");
	          
	          session.removeAttribute("ascBulkDownloadMyListStudy");
	          session.removeAttribute("ascBulkDownloadSharedWithMeListStudy");
	          
	          
	          // Analysis Bulk Download
	          session.removeAttribute("myAnalisysBulkDownloadPage");
	          session.removeAttribute("analisysBulkDownloadSharedWithMe");
	          
	          
	          session.removeAttribute("ascBulkDownloadMyListAnalysis");
	          session.removeAttribute("ascBulkDownloadSharedWithMeListAnalysis");
	          
	          // Study
	          session.removeAttribute("myStudiesPage");
	          session.removeAttribute("mySharedStudyPage");
	          session.removeAttribute("studySharedWithMe");

	          session.removeAttribute("ascMyListStudy");
	          session.removeAttribute("ascMyShareListStudy");
	          session.removeAttribute("ascByMeShareListStudy");
	          
	          // Analysis Workflow
	          session.removeAttribute("myAnalisysesPage");
	          session.removeAttribute("mySharedAnalisysesPage");
	          session.removeAttribute("analisysesSharedWithMePage");
	          session.removeAttribute("runningAnalisysesPage");    
	          
	          session.removeAttribute("ascMyListAnalysis");
	          session.removeAttribute("ascMySharedAnalysises");
	          session.removeAttribute("ascAnalysisesSharedWithMe");
	          session.removeAttribute("ascMyRunningListAnalysis");
	          
	          // Sequencer Run
	          session.removeAttribute("mySequencerPage");
	          
	          session.removeAttribute("ascMyListSequencerRun");	          
	          
	          // Launch Workflow
	          session.removeAttribute("mySelectInputPage");
	          
	          session.removeAttribute("ascLaunchListStudy");
	          
	          session.removeAttribute("workflow");
	          session.removeAttribute("workflowRun");
	          session.removeAttribute("workflowParam");
	          session.removeAttribute("summaryData");
	          LaunchWorkflowUtil.removeSelectedItemsLaunchWorkflow(request);
	          
	          // Saved Node Id
	          SetNodeIdInSession.removeStudy(request);
	          SetNodeIdInSession.removeWorkflowRun(request);
	          SetNodeIdInSession.removeSequencerRun(request);
	          
	          // Remove Saving Nodes Id
	          session.removeAttribute("listStudyNodeId");
	          session.removeAttribute("listWorkflowRunNodeId");
	          session.removeAttribute("listWorkflowRunRunningNodeId");
	          session.removeAttribute("listSequencerRunNodeId");
	          
	       // session.removeAttribute("isUserAbortedViewIndexPage");
          }
          return new ModelAndView("redirect:/Welcome.htm");
  }

}
