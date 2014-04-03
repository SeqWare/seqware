package net.sourceforge.seqware.common.business.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Set;

import net.sourceforge.seqware.common.business.ValidationReportService;
import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Service
/**
 * <p>ValidationReportServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ValidationReportServiceImpl implements ValidationReportService {

  @Autowired
  private FileDAO fileDao;

  /** {@inheritDoc} */
  @Override
  public List<ReportEntry> fileLinkReport() {
    List<File> files = fileDao.list();
    List<Integer> fileSwas = Lists.newArrayList();
    for (File file : files) {
      fileSwas.add(file.getSwAccession());
    }
    return fileLinkReport(fileSwas);
  }

  private void visitProcessing(Processing processing, int in, StringBuilder sb) {
    sb.append(indent(in) + processing.getAlgorithm() + " SWA: " + processing.getSwAccession());
    sb.append("\n");
    visitSequencerRun(processing, in, sb);
    visitLane(processing, in, sb);
    visitIus(processing, in, sb);
    visitSample(processing, in, sb);
    visitExperiment(processing, in, sb);
    visitStudy(processing, in, sb);
    if (visitWorkflowRun(processing, in, sb)) {
      in++;
    }
    for (Processing parent : processing.getParents()) {
      visitProcessing(parent, in, sb);
    }
  }

  private void visitSequencerRun(Processing processing, int in, StringBuilder sb) {
    for (SequencerRun link : processing.getSequencerRuns()) {
      sb.append(indent(in++) + "LINK SequencerRun SWA: " + link.getSwAccession());
      sb.append('\n');
    }
  }

  private void visitLane(Processing processing, int in, StringBuilder sb) {
    for (Lane link : processing.getLanes()) {
      sb.append(indent(++in) + "LINK Lane SWA: " + link.getSwAccession());
      sb.append('\n');
    }
  }

  private void visitIus(Processing processing, int in, StringBuilder sb) {
    for (IUS link : processing.getIUS()) {
      sb.append(indent(++in) + "LINK IUS SWA: " + link.getSwAccession());
      sb.append('\n');
    }
  }

  private void visitSample(Processing processing, int in, StringBuilder sb) {
    for (Sample link : processing.getSamples()) {
      sb.append(indent(in++) + "LINK Sample SWA: " + link.getSwAccession());
      sb.append('\n');
    }
  }

  private void visitExperiment(Processing processing, int in, StringBuilder sb) {
    for (Experiment link : processing.getExperiments()) {
      sb.append(indent(in++) + "LINK Experiment SWA: " + link.getSwAccession());
      sb.append('\n');
    }
  }

  private void visitStudy(Processing processing, int in, StringBuilder sb) {
    for (Study link : processing.getStudies()) {
      sb.append(indent(in++) + "LINK Study SWA: " + link.getSwAccession());
      sb.append('\n');
    }
  }

  private boolean visitWorkflowRun(Processing processing, int in, StringBuilder sb) {
    boolean result = false;
    WorkflowRun workflowRun = processing.getWorkflowRun();
    if (workflowRun != null) {
      Workflow workflow = workflowRun.getWorkflow();
      if (workflow != null) {
        sb.append(indent(in++) + "WORKFLOW " + workflow.getFullName() + " SWA: " + workflow.getSwAccession());
        sb.append('\n');
        result = true;
      }
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public String fileReverseHierarchyDisplay(Integer swa) {
    checkNotNull(swa);
    StringBuilder sb = new StringBuilder();
    int in = 0;
    File file = fileDao.findBySWAccession(swa);
    sb.append(indent(in++) + "FILE SWA: " + file.getSwAccession() + " FILE NAME: " + file.getFileName()
        + " Processing Children: " + file.getProcessings().size());
    sb.append("\n");
    for (Processing process : file.getProcessings()) {
      visitProcessing(process, in, sb);
    }
    return sb.toString();
  }

  private String indent(int indentSize) {
    StringBuilder sb = new StringBuilder();
    sb.append("");
    for (int i = 0; i < indentSize; i++) {
      sb.append("   ");
    }
    return sb.toString();
  }

  /** {@inheritDoc} */
  @Override
  public List<ReportEntry> fileLinkReport(List<Integer> fileSwas) {
    List<ReportEntry> result = Lists.newArrayList();
    for (Integer swa : fileSwas) {
      ReportEntry entry = new ReportEntry();
      result.add(entry);
      entry.setFileSwa(swa.toString());
      File file = fileDao.findBySWAccession(swa);
      entry.setFilename(file.getFileName());
      for (Processing process : file.getProcessings()) {
        walkProcessing(process, entry);
      }
    }
    return result;
  }

  private void walkProcessing(Processing processing, ReportEntry entry) {
    entry.setRootProcessingName(processing.getAlgorithm());
    entry.setRootProcessingSwa(processing.getSwAccession().toString());
    walkSequencerRun(processing, entry);
    walkLane(processing, entry);
    walkIus(processing, entry);
    walkSample(processing, entry);
    walkExperiment(processing, entry);
    walkStudy(processing, entry);
    for (Processing parent : processing.getParents()) {
      walkProcessing(parent, entry);
    }
  }

  private void walkSequencerRun(Processing processing, ReportEntry entry) {
    for (SequencerRun link : processing.getSequencerRuns()) {
      entry.getSequencerRunSwas().add(link.getSwAccession().toString());
    }
  }

  private void walkLane(Processing processing, ReportEntry entry) {
    for (Lane link : processing.getLanes()) {
      entry.getLaneSwas().add(link.getSwAccession().toString());
    }
  }

  private void walkIus(Processing processing, ReportEntry entry) {
    for (IUS link : processing.getIUS()) {
      entry.getIusSwas().add(link.getSwAccession().toString());
    }
  }

  private void walkSample(Processing processing, ReportEntry entry) {
    for (Sample link : processing.getSamples()) {
      entry.getSampleSwas().add(link.getSwAccession().toString());
    }
  }

  private void walkExperiment(Processing processing, ReportEntry entry) {
    for (Experiment link : processing.getExperiments()) {
      entry.getExperimentSwas().add(link.getSwAccession().toString());
    }
  }

  private void walkStudy(Processing processing, ReportEntry entry) {
    for (Study link : processing.getStudies()) {
      entry.getStudySwas().add(link.getSwAccession().toString());
    }
  }

  public class ReportEntry {
    private String filename;
    private String fileSwa;
    private String rootProcessingName;
    private String rootProcessingSwa;
    private Set<String> sequencerRunSwas = Sets.newHashSet();
    private Set<String> laneSwas = Sets.newHashSet();
    private Set<String> iusSwas = Sets.newHashSet();
    private Set<String> sampleSwas = Sets.newHashSet();
    private Set<String> experimentSwas = Sets.newHashSet();
    private Set<String> studySwas = Sets.newHashSet();

    public String getFilename() {
      return filename;
    }

    public void setFilename(String filename) {
      this.filename = filename;
    }

    public String getFileSwa() {
      return fileSwa;
    }

    public void setFileSwa(String fileSwa) {
      this.fileSwa = fileSwa;
    }

    public String getRootProcessingName() {
      return rootProcessingName;
    }

    public void setRootProcessingName(String rootProcessingName) {
      this.rootProcessingName = rootProcessingName;
    }

    public String getRootProcessingSwa() {
      return rootProcessingSwa;
    }

    public void setRootProcessingSwa(String rootProcessingSwa) {
      this.rootProcessingSwa = rootProcessingSwa;
    }

    public Set<String> getSequencerRunSwas() {
      return sequencerRunSwas;
    }

    public void setSequencerRunSwas(Set<String> sequencerRunSwas) {
      this.sequencerRunSwas = sequencerRunSwas;
    }

    public Set<String> getLaneSwas() {
      return laneSwas;
    }

    public void setLaneSwas(Set<String> laneSwas) {
      this.laneSwas = laneSwas;
    }

    public Set<String> getIusSwas() {
      return iusSwas;
    }

    public void setIusSwas(Set<String> iusSwas) {
      this.iusSwas = iusSwas;
    }

    public Set<String> getSampleSwas() {
      return sampleSwas;
    }

    public void setSampleSwas(Set<String> sampleSwas) {
      this.sampleSwas = sampleSwas;
    }

    public Set<String> getExperimentSwas() {
      return experimentSwas;
    }

    public void setExperimentSwas(Set<String> experimentSwas) {
      this.experimentSwas = experimentSwas;
    }

    public Set<String> getStudySwas() {
      return studySwas;
    }

    public void setStudySwas(Set<String> studySwas) {
      this.studySwas = studySwas;
    }

  }
}
