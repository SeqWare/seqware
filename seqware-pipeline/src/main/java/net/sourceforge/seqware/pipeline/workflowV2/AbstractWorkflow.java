package net.sourceforge.seqware.pipeline.workflowV2;

import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow;

public abstract class AbstractWorkflow implements WorkflowInterface {
    protected Workflow workflow;

    protected abstract void beforeWorkflow();

    protected abstract void generateWorkflow();

    protected abstract void afterWorkflow();
}