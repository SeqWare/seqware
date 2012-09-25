/**
 * @author briandoconnor@gmail.com
 *
 * The WorkflowLauncher is responsible for launching workflows with or without
 * metadata writeback.
 *
 */
package net.sourceforge.seqware.pipeline.plugins;

import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.plugin.WorkflowPlugin;
import net.sourceforge.seqware.pipeline.workflow.BasicWorkflow;
import net.sourceforge.seqware.pipeline.workflowV2.pegasus.PegasusWorkflowEngine;

import org.openide.util.lookup.ServiceProvider;

/**
 * @author yongliang ProviderFor(PluginInterface.class)
 * 
 *         TODO: validate at all the option below (especially
 *         link-parent-to-workflow-run) actually work!
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowLauncherV2 extends WorkflowPlugin {

    public WorkflowLauncherV2() {
	super();
    }

    public String get_description() {
	return ("A plugin that lets you launch workflow bundles once you have installed them via the BundleManager.");
    }

    @Override
    public BasicWorkflow createWorkflow() {
	return new PegasusWorkflowEngine(metadata, config);
    }
}
