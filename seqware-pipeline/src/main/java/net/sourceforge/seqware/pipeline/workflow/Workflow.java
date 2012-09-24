package net.sourceforge.seqware.pipeline.workflow;

import java.util.Map;

import net.sourceforge.seqware.common.metadata.Metadata;

/**
 * FIXME: This Workflow object should have much of the implementation move to
 * the common package, see WorkflowTools
 * 
 * @author boconnor
 */
public class Workflow extends BasicWorkflow {

    public Workflow(Metadata metadata, Map<String, String> config) {
	super(metadata, config);
    }

}
