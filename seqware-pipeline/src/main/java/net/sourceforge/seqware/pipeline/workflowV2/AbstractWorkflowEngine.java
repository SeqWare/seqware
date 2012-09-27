package net.sourceforge.seqware.pipeline.workflowV2;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.workflowV2.model.WorkflowObjectModel;

public abstract class AbstractWorkflowEngine {
	private WorkflowObjectModel objectModel;
	private ReturnValue returnValue;
	
	/**
	 * get the objectModel for the workflow
	 * @return
	 */
	public WorkflowObjectModel getObjectModel() {
		return objectModel;
	}
	
	/**
	 * get the return value of the running workflow
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
	public ReturnValue launchWorkflow(WorkflowObjectModel objectModel) {
		return null;
	}
	
	
}