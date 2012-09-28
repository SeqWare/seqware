package net.sourceforge.seqware.pipeline.workflowV2;

import net.sourceforge.seqware.pipeline.workflowV2.model.WorkflowObjectModel;

public abstract class AbstractWorkflowDataModel  {
    protected WorkflowObjectModel wfom;

    protected void setupWorkflow() {
    	
    }
    protected void setupEnvironment() {
    	
    }
    protected void setupFiles() {
    	
    }
    protected abstract void buildWorkflow();
    protected void wrapup() {
    	
    }

    protected void setWorkflowObjectModel(WorkflowObjectModel wfom) {
    	this.wfom = wfom;
    }
}