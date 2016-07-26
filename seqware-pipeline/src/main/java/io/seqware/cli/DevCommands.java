package io.seqware.cli;

import com.google.common.collect.Lists;
import io.seqware.WorkflowRuns;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

import java.util.List;
import java.util.stream.Collectors;

import static io.seqware.cli.Main.extras;
import static io.seqware.cli.Main.isHelp;
import static io.seqware.cli.Main.kill;
import static io.seqware.cli.Main.optVal;
import static io.seqware.cli.Main.optVals;
import static io.seqware.cli.Main.out;
import static io.seqware.cli.Main.reqVals;

/**
 * Collects the various SeqWare commands that are under development
 */
class DevCommands {

    static class Files2WorkflowRunsCommand implements CommandLeaf{

        @Override public String getCommand() {
            return "files2workflowruns";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware dev files2workflowruns --help");
                out("       seqware dev files2workflowruns <params>");
                out("");
                out("Description:");
                out("  Identify workflow runs that used specified files as input for workflow runs");
                out("");
                out("Optional parameters:");
                out("  --file <file-swa>            List of files by sw_accession, repeat for multiple files");
                out("  --workflow <workflow-swa>    List of workflows, repeat for multiple workflows");
                out("");
            } else {
                List<String> fileVals = reqVals(args, "--file");
                List<Integer> fileSWIDs = Lists.newArrayList();
                fileSWIDs.addAll(fileVals.stream().map(Main::swid).collect(Collectors.toList()));

                List<String> workflowVals = optVals(args, "--workflow");
                List<Integer> workflowSWIDs = Lists.newArrayList();
                workflowSWIDs.addAll(workflowVals.stream().map(Main::swid).collect(Collectors.toList()));

                extras(args, "dev files2workflowruns");

                Metadata md = MetadataFactory.get(ConfigTools.getSettings());
                List<WorkflowRun> relevantWorkflows = md.getWorkflowRunsAssociatedWithInputFiles(fileSWIDs, workflowSWIDs);
                for (WorkflowRun run : relevantWorkflows) {
                    out(Integer.toString(run.getSwAccession()));
                }
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Identify workflow runs that used files as input";
        }
    }


    static class MapCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "map";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware dev map --help");
                out("       seqware dev map <params>");
                out("");
                out("Description:");
                out("  Map from various unique identifiers to unique identifiers.");
                out("");
                out("Optional parameters:");
                out("  --engine-id <engine-id>          Convert from an engine ID (for oozie this is a job ID) to workflow run accession");
                out("  --action-id <sge-id>             Convert from an external ID (for oozie-sge this is a SGE ID) to workflow run accession (expensive)");
                out("");
            } else {
                String optVal = optVal(args, "--engine-id", null);
                if (optVal != null) {
                    WorkflowRun workflowrun = WorkflowRuns.getWorkflowRunByStatusCmd(optVal);
                    if (workflowrun == null) {
                        kill("No workflow run found");
                    }
                    out(Integer.toString(workflowrun.getSwAccession()));
                    return;
                }
                optVal = optVal(args, "--action-id", null);
                if (optVal != null) {
                    Integer swAccession = WorkflowRuns.getAccessionByActionExternalID(optVal);
                    if (swAccession == null) {
                        kill("No workflow run found");
                    }
                    out(Integer.toString(swAccession));
                    return;
                }

                kill("No desired mapping provided");
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Convert between various identifiers";
        }
    }
}
