package net.sourceforge.solexatools.webapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.solexatools.Security;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.BaseCommandController;

/**
 * RegistrationSetupController
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StudyReportController extends BaseCommandController {
  private StudyService studyService;
  
  /**
   * <p>Constructor for StudyReportController.</p>
   */
  public StudyReportController() {
    super();
    setSupportedMethods(new String[] { METHOD_GET });
  }

  /** {@inheritDoc} */
  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    // Registration registration = Security.requireRegistration(request,
    // response);

    Registration registration = Security.getRegistration(request);
    if (registration == null)
      return new ModelAndView("redirect:/login.htm");

    /**
     * Pass registration so that we can filter the list if its appropriate to do
     * so.
     */
    String warningSize = this.getServletContext().getInitParameter("report.bundle.slow.display.warning.size");

    // initSortingTreeAttr(request);

    String[] studyIds = request.getParameterValues("studyId");
    String studyId = null;
    if (studyIds != null && studyIds.length > 0) {
      studyId = studyIds[0];
    }

    // find the right study
    List<Study> list = new ArrayList<Study>();
    if (studyId != null) {
      Study currStudy = getStudyService().findByID(Integer.parseInt(studyId));
      list.add(currStudy);
    } else {
      list = getStudyService().list(registration);
    }

    // calculate the number of root samples associated with this study
    HashMap<String, Integer> samplesWithProcMap = new HashMap<String, Integer>();

    // main sample hash
    HashMap<String, Object> mainStudyHash = new HashMap<String, Object>();
    // main sample hash for samples with IUS
    HashMap<String, Object> mainStudyHashSamplesWithIUS = new HashMap<String, Object>();
    // main sample hash for samples with Proc
    HashMap<String, Object> samplesToWorlflows = new HashMap<String, Object>();

    // samples without IUS or processing
    HashMap<String, String> noSeqSamples = new HashMap<String, String>();

    // sample flowcell and ius info
    HashMap<String, ArrayList<String>> sampleFlowcellInfo = new HashMap<String, ArrayList<String>>();

    // sampleFiles
    HashMap<String, HashMap<String, String>> sampleFiles = new HashMap<String, HashMap<String, String>>();
    HashMap<String, Integer> samplesCount = new HashMap<String, Integer>();
    ModelAndView modelAndView = new ModelAndView("StudyReportList");
    
    int rootSamples = 0;
    int nonRootSamples = 0;
    int totalSamples = 0;
    int samplesWithIUS = 0;
    for (Study study : list) {
      // main sample hash
      HashMap<String, Sample> mainSampleHash = new HashMap<String, Sample>();
      HashMap<String, Sample> mainSampleHashWithIUS = new HashMap<String, Sample>();

      mainStudyHash.put(study.getTitle(), mainSampleHash);
      mainStudyHashSamplesWithIUS.put(study.getTitle(), mainSampleHashWithIUS);
      for (Experiment exp : study.getExperiments()) {
        for (Sample sample : exp.getSamples()) {
          HashMap<String, Workflow> sampleWorkflows = new HashMap<String, Workflow>();
          mainSampleHash.put(sample.getName(), sample);
          totalSamples++;
          if (sample.getParents() == null || sample.getParents().size() == 0) {
            rootSamples++;
          } else {
            nonRootSamples++;
          }
          SortedSet<IUS> iuses = sample.getIUS();
          if (iuses != null && iuses.size() > 0) {
            samplesWithIUS++;
            mainSampleHashWithIUS.put(sample.getName(), sample);
          } else {
            noSeqSamples.put(sample.getSwAccession().toString(), "");
          }
          for (IUS ius : iuses) {

            // figure out flowcell etc
            Integer laneIndex = ius.getLane().getLaneIndex();
            laneIndex++;
            String laneName = laneIndex.toString();
            String flowcell = ius.getLane().getSequencerRun().getName();
            String barcode = ius.getTag();
            if (barcode == null) {
              barcode = "NoIndex";
            }
            if (sampleFlowcellInfo.get(sample.getName()) == null) {
              sampleFlowcellInfo.put(sample.getName(), new ArrayList<String>());
            }
            sampleFlowcellInfo.get(sample.getName()).add(
                "<tr><td>" + flowcell + "</td><td align=\"center\">" + laneName + "</td><td>" + barcode + " (IUS:"
                    + ius.getSwAccession() + ")</td></tr>");

            Set<Processing> processings = ius.getProcessings();
            if (processings != null && processings.size() > 0) {
              samplesWithProcMap.put(sample.getSwAccession().toString(), 1);
            }

            HashMap<String, String> fileInfo = new HashMap<String, String>();
            for (Processing proc : processings) {
              if (proc.getWorkflowRun() != null) {
                Workflow w = proc.getWorkflowRun().getWorkflow();
                if (w != null) {
                  sampleWorkflows.put(w.getName(), w);
                }
              }
              findFiles(proc, fileInfo);
            }
            sampleFiles.put(sample.getName(), fileInfo);
          }
          // save workflows for this sample
          samplesToWorlflows.put(sample.getName(), sampleWorkflows);
        }
      }
    }
    samplesCount.put("rootSamples", rootSamples);
    samplesCount.put("nonRootSamples", nonRootSamples);
    samplesCount.put("totalSamples", totalSamples);
    samplesCount.put("samplesWithIUS", samplesWithIUS);
    samplesCount.put("samplesWithProcess", samplesWithProcMap.keySet().size());

    modelAndView.addObject("studys", list);
    modelAndView.addObject("studyStats", samplesCount);
    modelAndView.addObject("sampleFlowcellInfo", sampleFlowcellInfo);
    modelAndView.addObject("mainStudyHash", mainStudyHash);
    modelAndView.addObject("mainStudyHashWithIUS", mainStudyHashSamplesWithIUS);
    modelAndView.addObject("samplesToWorkflows", samplesToWorlflows);
    modelAndView.addObject("registration", registration);
    modelAndView.addObject("warningSize", warningSize);

    return modelAndView;
  }

  private void findFiles(Processing proc, HashMap<String, String> fileInfo) {
    for (File file : proc.getFiles()) {
      String name = file.getFilePath();
      String metatype = file.getMetaType();
      String id = file.getSwAccession().toString();
      String formatStr = "<tr><td>" + metatype + "</td><td>SWID:" + id + "</td><td>" + name + "</td></tr>";
      fileInfo.put(name, formatStr);
    }
    for (Processing child : proc.getChildren()) {
      findFiles(child, fileInfo);
    }
  }

  /**
   * <p>Getter for the field <code>studyService</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.business.StudyService} object.
   */
  public StudyService getStudyService() {
    return studyService;
  }

  /**
   * <p>Setter for the field <code>studyService</code>.</p>
   *
   * @param studyService a {@link net.sourceforge.seqware.common.business.StudyService} object.
   */
  public void setStudyService(StudyService studyService) {
    this.studyService = studyService;
  }
  
}
