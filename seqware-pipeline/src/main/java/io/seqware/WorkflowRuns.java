package io.seqware;

import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunStatus;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

public class WorkflowRuns {

  public static void submitCancel(int workflowRunAccession) {
    Metadata md = MetadataFactory.get(ConfigTools.getSettings());
    WorkflowRun wr = md.getWorkflowRun(workflowRunAccession);
    if (Engines.supportsCancel(wr.getWorkflowEngine())) {
      switch (wr.getStatus()) {
      case submitted:
      case pending:
      case running:
        wr.setStatus(WorkflowRunStatus.submitted_cancel);
        md.updateWorkflowRun(wr);
      default: // do nothing
      }
    } else {
      throw new UnsupportedOperationException("Workflow run cancellation not supported for engine: "
                                              + wr.getWorkflowEngine());
    }
  }

  public static void submitRetry(int workflowRunAccession) {
    Metadata md = MetadataFactory.get(ConfigTools.getSettings());
    WorkflowRun wr = md.getWorkflowRun(workflowRunAccession);
    if (Engines.supportsRetry(wr.getWorkflowEngine())) {
      switch (wr.getStatus()) {
      case failed:
      case cancelled:
        wr.setStatus(WorkflowRunStatus.submitted_retry);
        md.updateWorkflowRun(wr);
      default: // do nothing
      }
    } else {
      throw new UnsupportedOperationException("Workflow run retrying not supported for engine: "
                                              + wr.getWorkflowEngine());
    }
  }

}
