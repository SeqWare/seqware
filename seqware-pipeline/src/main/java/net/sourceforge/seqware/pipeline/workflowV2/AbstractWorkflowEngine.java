package net.sourceforge.seqware.pipeline.workflowV2;

import net.sourceforge.seqware.common.module.ReturnValue;

public abstract class AbstractWorkflowEngine {
	private AbstractWorkflowDataModel objectModel;
	
	/**
	 * get the objectModel for the workflow
	 * @return
	 */
	public AbstractWorkflowDataModel getDataModel() {
		return objectModel;
	}
	
	
	/**
	 * launch the workflow according to the info filled in the input objectModel
	 * @param objectModel
	 * @return
	 */
	public abstract ReturnValue launchWorkflow(AbstractWorkflowDataModel objectModel);
	
	public abstract String getId();
	public abstract String getStatus(String id);
	public abstract String getStdErr(String id);
	public abstract String getStdOut(String id);
	
	
}