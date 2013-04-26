package net.sourceforge.seqware.pipeline.workflowV2;

import net.sourceforge.seqware.common.module.ReturnValue;

public abstract class AbstractWorkflowEngine {

	
	
	
	/**
	 * launch the workflow according to the info filled in the input objectModel
	 * @param objectModel
	 * @return
	 */
	public abstract ReturnValue launchWorkflow(AbstractWorkflowDataModel objectModel);
	
	/**
	 * 
	 * @return the id assigned by the workflow engine
	 */
	public abstract String getId();
	/**
	 * find the workflow by input id, and return the status of the workflow
	 * @param id
	 * @return the workflow status
	 */
	public abstract String getStatus(String id);
	/**
	 * find the workflow by input id, return the first failed job's error message 
	 * if no failed job, return empty string
	 * @param id
	 * @return
	 */
	public abstract String getStdErr(String id);
	public abstract String getStdOut(String id);
	
	public abstract String getStatus();
	
}