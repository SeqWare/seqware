package io.seqware.pipeline.plugins;

import io.seqware.pipeline.api.WorkflowEngine;
import io.seqware.pipeline.api.WorkflowTools;
import java.util.Arrays;
import joptsimple.ArgumentAcceptingOptionSpec;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.module.ReturnValue.ExitStatus;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.WorkflowDataModelFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 * The Workflow Watcher is only responsible for watching workflows and reporting on their status in a real-time manner.
 *
 * This started as a fork of the WorkflowLauncher intended to de-tangle the functions of launching, scheduling, waiting, etc.
 *
 * @author dyuen
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowWatcher extends Plugin {

    private final ArgumentAcceptingOptionSpec<String> workflowRunAccessionSpec;

    public WorkflowWatcher() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");

        this.workflowRunAccessionSpec = parser
                .acceptsAll(Arrays.asList("workflow-run-accession", "wra", "r"), "Required: The sw_accession of the workflow run to watch")
                .withRequiredArg().ofType(String.class).required();
    }

    public static final String OVERRIDE_INI_DESC = "Override ini options on the command after the separator \"--\" with pairs of \"--<key> <value>\"";

    @Override
    public ReturnValue init() {
        return new ReturnValue(ExitStatus.SUCCESS);
    }

    /*
     */
    @Override
    public ReturnValue do_test() {
        return new ReturnValue(ExitStatus.SUCCESS);
    }

    @Override
    public ReturnValue clean_up() {
        return new ReturnValue(ExitStatus.SUCCESS);
    }

    @Override
    public String get_description() {
        return "A plugin that lets you watch running workflow_runs.";
    }

    @Override
    public ReturnValue do_run() {
        String workflowRunAccession = options.valueOf(workflowRunAccessionSpec);
        WorkflowRun workflowRunWithWorkflow = metadata.getWorkflowRunWithWorkflow(workflowRunAccession);
        Integer workflowAccession = workflowRunWithWorkflow.getWorkflowAccession();
        String bundleDir = workflowRunWithWorkflow.getWorkflow().getCwd();
        WorkflowDataModelFactory factory = new WorkflowDataModelFactory(config, metadata);

        AbstractWorkflowDataModel dataModel = factory.getWorkflowDataModel(bundleDir, workflowAccession,
                Integer.parseInt(workflowRunAccession), workflowRunWithWorkflow.getWorkflowEngine());
        WorkflowEngine workflowEngine = WorkflowTools.getWorkflowEngine(dataModel, config, false);

        workflowEngine.watchWorkflow(workflowRunWithWorkflow.getStatusCmd());
        return new ReturnValue();
    }

}
