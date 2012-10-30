package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus;

import java.io.File;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowEngine;

public class OozieWorkflowEngine extends AbstractWorkflowEngine {

	@Override
	public ReturnValue launchWorkflow(AbstractWorkflowDataModel objectModel) {
		//parse objectmodel 
		ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
		this.parseDataModel(objectModel);
		//ret = this.runWorkflow(objectModel, dax);
		return ret;
	}
	
	/**
	 * create a working directory in hadoop
	 */
	private void setupEnvironment() {
		
	}
	
	/**
	 * return a workflow.xml for hadoop
	 * @param objectModel
	 * @return
	 */
	private File parseDataModel(AbstractWorkflowDataModel objectModel) {
		return null;
	}
}