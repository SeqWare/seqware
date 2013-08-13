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
    switch (wr.getStatus()) {
    case submitted:
    case pending:
    case running:
      wr.setStatus(WorkflowRunStatus.submitted_cancel);
      md.updateWorkflowRun(wr);
    default: // do nothing
    }
  }

  public static void submitRetry(int workflowRunAccession) {
    Metadata md = MetadataFactory.get(ConfigTools.getSettings());
    WorkflowRun wr = md.getWorkflowRun(workflowRunAccession);
    switch (wr.getStatus()) {
    case failed:
    case cancelled:
      wr.setStatus(WorkflowRunStatus.submitted_retry);
      md.updateWorkflowRun(wr);
    default: // do nothing
    }
  }

}
