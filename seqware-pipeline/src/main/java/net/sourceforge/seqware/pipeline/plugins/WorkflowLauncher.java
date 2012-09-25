/**
 * @author briandoconnor@gmail.com
 *
 * The WorkflowLauncher is responsible for launching workflows with or without
 * metadata writeback.
 *
 * rules for command construction cd $cwd && $command --workflow-accession
 * $workflow_accession --workflow-run-accession $workflow_run_accession
 * --parent-accessions $parent_accessions --ini-files $temp_file --wait &
 *
 */
package net.sourceforge.seqware.pipeline.plugins;

import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.plugin.WorkflowPlugin;

import org.openide.util.lookup.ServiceProvider;

/**
 * @author boconnor ProviderFor(PluginInterface.class)
 * 
 *         TODO: validate at all the option below (especially
 *         link-parent-to-workflow-run) actually work!
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowLauncher extends WorkflowPlugin {

    public WorkflowLauncher() {
	super();
    }

}
