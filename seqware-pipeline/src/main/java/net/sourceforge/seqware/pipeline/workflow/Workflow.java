package net.sourceforge.seqware.pipeline.workflow;

import java.util.Map;

import net.sourceforge.seqware.common.metadata.Metadata;

/**
 * FIXME: This Workflow object should have much of the implementation move to
 * the common package, see WorkflowTools
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Workflow extends BasicWorkflow {

    /**
     * <p>Constructor for Workflow.</p>
     *
     * @param metadata a {@link net.sourceforge.seqware.common.metadata.Metadata} object.
     * @param config a {@link java.util.Map} object.
     */
    public Workflow(Metadata metadata, Map<String, String> config) {
	super(metadata, config);
    }

}
