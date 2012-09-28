package net.sourceforge.seqware.pipeline.workflowV2;

import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow;

public abstract class AbstractWorkflowDataModel  {
    protected Workflow workflow;

    protected void setupWorkflow() {
    	
    }
    protected void setupEnvironment() {
    	
    }
    protected void setupFiles() {
    	
    }
    protected abstract void buildWorkflow();
    protected void wrapup() {
    	
    }

    protected Workflow setWorkflowObjectModel(Workflow wfom) {
    	this.workflow = wfom;
    	return this.workflow;
    }
}