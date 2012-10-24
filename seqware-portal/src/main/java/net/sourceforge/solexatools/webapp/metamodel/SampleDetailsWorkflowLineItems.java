package net.sourceforge.solexatools.webapp.metamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.WorkflowRun;

/**
 * <p>SampleDetailsWorkflowLineItems class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SampleDetailsWorkflowLineItems {

  private HashMap<WorkflowRun, List<Processing>> workFlowMap = new HashMap<WorkflowRun, List<Processing>>();
  private WorkflowRun dummyWf = new WorkflowRun();

  /**
   * <p>Constructor for SampleDetailsWorkflowLineItems.</p>
   *
   * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
   */
  public SampleDetailsWorkflowLineItems(Sample sample) {
    init();
    if (sample.getProcessings() != null) {
      for (Processing processing : sample.getProcessings()) {
        appendProcessing(processing);
      }
    }
  }

  /**
   * <p>Constructor for SampleDetailsWorkflowLineItems.</p>
   *
   * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
   */
  public SampleDetailsWorkflowLineItems(Processing processing) {
    init();
    if (processing != null) {
      appendProcessing(processing);
    }
  }

  private void init() {
    dummyWf.setName("N/A");
  }

  /**
   * <p>addProcessings.</p>
   *
   * @param ius a {@link net.sourceforge.seqware.common.model.IUS} object.
   */
  public void addProcessings(IUS ius) {
    if (ius.getProcessings() != null) {
      for (Processing processing : ius.getProcessings()) {
        appendProcessing(processing);
      }
    }
  }

  private void appendProcessing(Processing processing) {
    if (processing.getChildren() != null) {
      for (Processing childProcessing : processing.getChildren()) {
        appendProcessing(childProcessing);
      }
    }
    List<Processing> processings = workFlowMap.get(processing.getWorkflowRun());
    if (processings == null) {
      processings = new ArrayList<Processing>();
    }
    processings.add(processing);
    if (processing.getWorkflowRun() == null) {
      workFlowMap.put(dummyWf, processings);
    } else {
      workFlowMap.put(processing.getWorkflowRun(), processings);
    }
  }

  /**
   * <p>getProcessingsByWorkflow.</p>
   *
   * @param wfRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @return a {@link java.util.List} object.
   */
  public List<Processing> getProcessingsByWorkflow(WorkflowRun wfRun) {
    return workFlowMap.get(wfRun);
  }

  /**
   * <p>getWorkflowsRun.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> getWorkflowsRun() {
    return new ArrayList<WorkflowRun>(workFlowMap.keySet());
  }
}
