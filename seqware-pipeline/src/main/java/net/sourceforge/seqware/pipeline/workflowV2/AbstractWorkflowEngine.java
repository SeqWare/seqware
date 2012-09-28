package net.sourceforge.seqware.pipeline.workflowV2;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow;

public abstract class AbstractWorkflowEngine {
	private Workflow objectModel;
	private ReturnValue returnValue;
	
	/**
	 * get the objectModel for the workflow
	 * @return
	 */
	public Workflow getObjectModel() {
		return objectModel;
	}
	
	/**
	 * get the return value of the running workflow, if the workflow is not finished, return null;
	 * @return
	 */
	public ReturnValue getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(ReturnValue returnValue) {
		this.returnValue = returnValue;
	}
	
	/**
	 * launch the workflow according to the info filled in the input objectModel
	 * @param objectModel
	 * @return
	 */
	public abstract ReturnValue launchWorkflow(Workflow objectModel);
	
	
}