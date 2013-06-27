package net.sourceforge.seqware.pipeline.workflowV2;

import net.sourceforge.seqware.common.module.ReturnValue;

public abstract class AbstractWorkflowEngine {

  /**
   * Prepare the workflow according to the info filled in the input objectModel.
   * 
   * @param objectModel
   *          model of the workflow to prepare to run
   */
  public abstract void prepareWorkflow(AbstractWorkflowDataModel objectModel);

  /**
   * Run the prepared workflow.
   */
  public abstract ReturnValue runWorkflow();

  /**
   * An engine-specific token for this workflow run that can be used to lookup
   * relevant runtime data.
   * 
   * @return the token
   */
  public abstract String getLookupToken();

//  /**
//   * Obtain the status of the workflow run.
//   * 
//   * @param token
//   *          the token created during launch of the workflow
//   * @return the workflow run status
//   */
//  public abstract String lookupStatus(String token);
//
//  /**
//   * Obtain the stdout of the workflow run.
//   * 
//   * @param token
//   *          the token created during launch of the workflow
//   * @return the stdout contents
//   */
//  public abstract String lookupStdErr(String token);
//
//  /**
//   * Obtain the stdout of the workflow run.
//   * 
//   * @param token
//   *          the token created during launch of the workflow
//   * @return the stdout contents
//   */
//  public abstract String lookupStdOut(String token);
//
}