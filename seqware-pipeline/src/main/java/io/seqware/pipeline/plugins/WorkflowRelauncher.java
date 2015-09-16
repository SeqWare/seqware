package io.seqware.pipeline.plugins;

import io.seqware.pipeline.api.WorkflowTools;
import io.seqware.pipeline.engines.whitestar.Persistence;
import io.seqware.pipeline.engines.whitestar.WhiteStarWorkflowEngine;
import java.io.File;
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
 * The Workflow relauncher is only responsible for relaunching failed workflows.
 *
 * This started as a fork of the WorkflowWatcher since it uses a similar path to initialize the workflow data model. This is only currently
 * applicable for SeqWare Whitestar.
 *
 * @author dyuen
 */
@ServiceProvider(service = PluginInterface.class)
public class WorkflowRelauncher extends Plugin {

    private final ArgumentAcceptingOptionSpec<String> nfsWorkDir;

    public WorkflowRelauncher() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");

        this.nfsWorkDir = parser
                .acceptsAll(Arrays.asList("working-dir", "w"), "Required: The working directory of the workflow run to watch")
                .withRequiredArg().ofType(String.class).required();
    }

    @Override
    public ReturnValue init() {
        return new ReturnValue(ExitStatus.SUCCESS);
    }

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
        return "A plugin that lets you re-launch failed workflow runs.";
    }

    @Override
    public ReturnValue do_run() {
        File workDir = new File(options.valueOf(nfsWorkDir));
        Persistence persistence = new Persistence(workDir);
        WorkflowRun workflowRun = persistence.readWorkflowRun();
        Integer workflowAccession = workflowRun.getWorkflowAccession();
        String bundleDir = workflowRun.getWorkflow().getCwd();
        WorkflowDataModelFactory factory = new WorkflowDataModelFactory(config, metadata);

        AbstractWorkflowDataModel dataModel = factory.getWorkflowDataModel(bundleDir, workflowAccession, workflowRun.getSwAccession(),
                workflowRun.getWorkflowEngine());
        WhiteStarWorkflowEngine workflowEngine = (WhiteStarWorkflowEngine) WorkflowTools.getWorkflowEngine(dataModel, config, false);
        workflowEngine.prepareWorkflow(dataModel, workDir);
        workflowEngine.runWorkflow(persistence.readCompletedJobs());
        return new ReturnValue();
    }

}
