package net.sourceforge.seqware.common.model;

public enum WorkflowRunStatus {
  /**
   * The workflow run exists in seqware but has not yet been launched on a workflow engine
   */
  submitted,
  
  /**
   * The workflow run has been launched on a workflow engine; status in the engine has not yet been propagated.
   */
  pending,
  
  /**
   * The workflow run is being executed on a workflow engine.
   */
  running,
  
  /**
   * The workflow run has completed unsuccessfully.
   */
  failed,
  
  /**
   * The workflow run has completed successfully.
   */
  completed,
  
  /**
   * The workflow run is scheduled for cancellation.
   */
  submitted_cancel,
  
  /**
   * The workflow run was cancelled from the running state (i.e., did not complete/fail since submitted_cancel was set.
   */
  cancelled,
  
  /**
   * The workflow run that had been cancelled or failed is now scheduled to be retried
   */
  submitted_retry,
}