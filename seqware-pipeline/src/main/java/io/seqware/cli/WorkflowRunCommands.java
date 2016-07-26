package io.seqware.cli;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import io.seqware.Engines;
import io.seqware.WorkflowRuns;
import io.seqware.pipeline.SqwKeys;
import net.sourceforge.seqware.common.util.Log;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.seqware.cli.Main.cdl;
import static io.seqware.cli.Main.extras;
import static io.seqware.cli.Main.flag;
import static io.seqware.cli.Main.isHelp;
import static io.seqware.cli.Main.kill;
import static io.seqware.cli.Main.optVal;
import static io.seqware.cli.Main.optVals;
import static io.seqware.cli.Main.out;
import static io.seqware.cli.Main.outWithoutFormatting;
import static io.seqware.cli.Main.reqVal;
import static io.seqware.cli.Main.reqVals;
import static io.seqware.cli.Main.run;
import static io.seqware.cli.Main.swid;

/**
 * Collects the various SeqWare workflow run commands
 */
class WorkflowRunCommands {

    static class WorkflowRunReportCommand implements CommandLeaf{

        @Override public String getCommand() {
            return "report";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware workflow-run report --help");
                out("       seqware workflow-run report <params>");
                out("");
                out("Description:");
                out("  The details of a given workflow-run.");
                out("");
                out("Required parameters:");
                out("  --accession <swid>  The SWID of the workflow run");
                out("");
                out("Optional parameters:");
                out("  --out <file>        The name of the report file");
                out("  --tsv               Emit a tab-separated values report");
                out("");
            } else {
                String swid = reqVal(args, "--accession");
                String out = optVal(args, "--out", null);
                boolean tsv = flag(args, "--tsv");

                extras(args, "workflow-run report");

                List<String> runnerArgs = new ArrayList<>();
                runnerArgs.add("--plugin");
                runnerArgs.add("net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter");
                runnerArgs.add("--");
                runnerArgs.add("--workflow-run-accession");
                runnerArgs.add(swid);
                if (out != null) {
                    runnerArgs.add("--output-filename");
                    runnerArgs.add(out);
                } else {
                    runnerArgs.add("--stdout");
                }
                if (!tsv) {
                    runnerArgs.add("--human");
                }

                run(runnerArgs);
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "The details of a given workflow-run";
        }
    }

    static class WorkflowRunDeleteCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "delete";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware workflow-run delete --help");
                out("       seqware workflow-run delete <params>");
                out("");
                out("Description:");
                out("  Recursively delete workflow runs based on the SWID of an ancestral sequencer run, lane, or workflow run.");
                out("");
                out("Required parameters:");
                out("  --accession <swid>  The SWID of the desired target");
                out("");
                out("Optional parameters:");
                out("  --key <file>        Delete workflow runs given a key file containing records to be deleted in one transaction.");
                out("  --out <file>        Override the filename for where to write a key file containing records.");
                out("");
            } else {
                String swid = reqVal(args, "--accession");
                String key = optVal(args, "--key", null);
                String out = optVal(args, "--out", null);

                extras(args, "workflow-run delete");

                List<String> runnerArgs = new ArrayList<>();
                runnerArgs.add("--plugin");
                runnerArgs.add("net.sourceforge.seqware.pipeline.plugins.deletion.DeletionDB");
                runnerArgs.add("--");
                runnerArgs.add("--workflowrun");
                runnerArgs.add(swid);
                if (key != null) {
                    runnerArgs.add("--key");
                    runnerArgs.add(key);
                }
                if (out != null) {
                    runnerArgs.add("--out");
                    runnerArgs.add(out);
                }

                run(runnerArgs);
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Recursively delete workflow-runs";
        }
    }

    static class WorkflowRunReschedule implements CommandLeaf{

        @Override public String getCommand() {
            return "reschedule";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware workflow-run reschedule [--help]");
                out("       seqware workflow-run reschedule <params>");
                out("");
                out("Description:");
                out("  Reschedule a workflow-run to be rescheduled to run from scratch as a new workflow-run.");
                out("");
                out("Required parameters:");
                out("  --accession <swid>         The SWID of the workflow-run to be rescheduled");
                out("");
                out("Optional parameters:");
                out("  --host <host>              The host on which to launch the workflow run");
                out("  --engine <type>            The engine that will process the workflow run.");
                out("                             May be one of: " + Engines.ENGINES_LIST);
                out("                             Defaults to the value of " + SqwKeys.SW_DEFAULT_WORKFLOW_ENGINE.getSettingKey());
                out("                             or '" + Engines.DEFAULT_ENGINE + "' if not specified.");
                out("");
            } else {
                String wfId = reqVal(args, "--accession");
                String host = optVal(args, "--host", null);
                String engine = optVal(args, "--engine", null);

                extras(args, "workflow-run reschedule");

                List<String> runnerArgs = new ArrayList<>();
                runnerArgs.add("--plugin");
                runnerArgs.add("io.seqware.pipeline.plugins.WorkflowRescheduler");
                runnerArgs.add("--");
                runnerArgs.add("--workflow-run");
                runnerArgs.add(wfId);
                if (engine != null) {
                    runnerArgs.add("--workflow-engine");
                    runnerArgs.add(engine);
                }
                if (host != null) {
                    runnerArgs.add("--host");
                    runnerArgs.add(host);
                }

                String[] totalArgs = ArrayUtils.addAll(runnerArgs.toArray(new String[runnerArgs.size()]));
                run(totalArgs);
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Reschedule a workflow-run to re-run from scratch as a new run";
        }
    }

    static class WorkflowRunWatchCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "watch";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware workflow-run watch --help");
                out("       seqware workflow-run watch <params>");
                out("");
                out("Description:");
                out("  Watch a workflow run in progress.");
                out("");
                out("Required parameters:");
                out("  --accession <swid>  The SWID of the workflow run");
                out("");
            } else {
                String swid = reqVal(args, "--accession");

                extras(args, "workflow-run watch");

                List<String> runnerArgs = new ArrayList<>();
                runnerArgs.add("--plugin");
                runnerArgs.add("io.seqware.pipeline.plugins.WorkflowWatcher");
                runnerArgs.add("--");
                runnerArgs.add("--workflow-run-accession");
                runnerArgs.add(swid);

                run(runnerArgs);
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Watch a workflow-run in progress";
        }
    }

    static class WorkflowRunIniCommand implements CommandLeaf{

        @Override public String getCommand() {
            return "ini";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware workflow-run ini --help");
                out("       seqware workflow-run ini <params>");
                out("");
                out("Description:");
                out("  Display the ini file used to run a workflow run.");
                out("");
                out("Required parameters:");
                out("  --accession <swid>  The SWID of the workflow run");
                out("Optional parameters:");
                out("  --out <file>        The name of the ini file");
                out("");
            } else {
                String swid = reqVal(args, "--accession");
                String out = optVal(args, "--out", null);

                extras(args, "workflow-run ini");

                if (out != null) {
                    try {
                        FileUtils.writeStringToFile(new File(out), WorkflowRuns.workflowRunIni(Integer.parseInt(swid)), StandardCharsets.UTF_8);
                    } catch (IOException ex) {
                        kill("seqware: cannot write to '%s'.", out);
                    }
                } else {
                    outWithoutFormatting(WorkflowRuns.workflowRunIni(Integer.parseInt(swid)));
                }
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Output the effective ini for a workflow run";
        }
    }

    static class WorkflowRunLaunchScheduled implements CommandLeaf{

        @Override public String getCommand() {
            return "launch-scheduled";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, false)) {
                out("");
                out("Usage: seqware workflow-run launch-scheduled --help");
                out("       seqware workflow-run launch-scheduled");
                out("");
                out("Description:");
                out("  Launch scheduled workflow runs.");
                out("");
                out("Optional parameters:");
                out("  --accession <swid>   Launch the specified workflow-run. Repeat this parameter");
                out("                       to provide multiple runs.");
                out("  --host <value>       Use the specified value instead of the local hostname");
                out("                       when selecting which workflow-runs to launch.");
                out("");
            } else {
                List<String> ids = optVals(args, "--accession");
                String host = optVal(args, "--host", null);

                extras(args, "workflow-run launch-scheduled");

                if (!ids.isEmpty() && host != null) {
                    kill("seqware: cannot specify both '--accession' and '--host'.");
                }

                List<String> runnerArgs = new ArrayList<>();
                runnerArgs.add("--plugin");
                runnerArgs.add("io.seqware.pipeline.plugins.WorkflowLauncher");
                runnerArgs.add("--");

                if (host != null) {
                    runnerArgs.add("--force-host");
                    runnerArgs.add(host);
                }

                runnerArgs.add("--launch-scheduled");
                if (!ids.isEmpty()) {
                    runnerArgs.add(cdl(ids));
                }

                run(runnerArgs);
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Launch scheduled workflow runs";
        }
    }

    static class WorkflowRunPropagateStatusesCommand implements CommandLeaf{

        @Override public String getCommand() {
            return "propagate-statuses";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, false)) {
                out("");
                out("Usage: seqware workflow-run propagate-statuses --help");
                out("       seqware workflow-run propagate-statuses <params>");
                out("");
                out("Description:");
                out("  Propagate workflow engine statuses to seqware meta DB.");
                out("");
                out("Optional parameters:");
                out("  --accession <swid>   Propagate the status of the specified workflow-run.");
                out("                       Repeat this parameter to specify multiple workflow-runs.");
                out("  --host <value>       Use the specified value instead of the local hostname");
                out("                       when selecting which workflow-runs to check.");
                out("  --threads <num>      The number of concurrent worker threads (default 1)");
                out("");
            } else {
                String threads = optVal(args, "--threads", null);
                List<String> ids = optVals(args, "--accession");
                String host = optVal(args, "--host", null);

                extras(args, "workflow-run propagate-statuses");

                if (!ids.isEmpty() && host != null) {
                    kill("seqware: cannot specify both '--accession' and '--host'.");
                }

                List<String> runnerArgs = new ArrayList<>();
                runnerArgs.add("--plugin");
                runnerArgs.add("net.sourceforge.seqware.pipeline.plugins.WorkflowStatusChecker");
                runnerArgs.add("--");

                if (host != null) {
                    runnerArgs.add("--force-host");
                    runnerArgs.add(host);
                }

                if (threads != null) {
                    runnerArgs.add("--threads-in-thread-pool");
                    runnerArgs.add(threads);
                }
                if (!ids.isEmpty()) {
                    runnerArgs.add("--workflow-run-accession");
                    runnerArgs.add(cdl(ids));
                }

                Log.stdoutWithTime("Propagated workflow engine statuses");
                run(runnerArgs);
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Propagate workflow engine statuses to seqware meta DB";
        }
    }

    static class WorkflowRunStderrCommand implements CommandLeaf{

        @Override public String getCommand() {
            return "stderr";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware workflow-run stderr --help");
                out("       seqware workflow-run stderr <params>");
                out("");
                out("Description:");
                out("  Obtain the stderr output of the run.");
                out("");
                out("Required parameters:");
                out("  --accession <swid>  The SWID of the workflow run");
                out("");
                out("Optional parameters:");
                out("  --out <file>        The name of the file to write the stderr");
                out("                      Defaults to <swid>.err");
                out("");
            } else {
                String swid = reqVal(args, "--accession");
                String out = optVal(args, "--out", swid + ".err");

                extras(args, "workflow-run stderr");

                List<String> runnerArgs = new ArrayList<>();
                runnerArgs.add("--plugin");
                runnerArgs.add("net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter");
                runnerArgs.add("--");
                runnerArgs.add("--workflow-run-accession");
                runnerArgs.add(swid);
                runnerArgs.add("--output-filename");
                runnerArgs.add(out);
                runnerArgs.add("--wr-stderr");
                run(runnerArgs);
                out("Created file " + out);
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Obtain the stderr output of the run";
        }
    }

    static class WorkflowRunStdoutCommand implements CommandLeaf{

        @Override public String getCommand() {
            return "stdout";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware workflow-run stdout --help");
                out("       seqware workflow-run stdout <params>");
                out("");
                out("Description:");
                out("  Obtain the stdout output of the run.");
                out("");
                out("Required parameters:");
                out("  --accession <swid>  The SWID of the workflow run");
                out("");
                out("Optional parameters:");
                out("  --out <file>        The name of the file to write the stdout");
                out("                      Defaults to <swid>.out");
                out("");
            } else {
                String swid = reqVal(args, "--accession");
                String out = optVal(args, "--out", swid + ".out");

                extras(args, "workflow-run stdout");

                List<String> runnerArgs = new ArrayList<>();
                runnerArgs.add("--plugin");
                runnerArgs.add("net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter");
                runnerArgs.add("--");
                runnerArgs.add("--workflow-run-accession");
                runnerArgs.add(swid);
                runnerArgs.add("--output-filename");
                runnerArgs.add(out);
                runnerArgs.add("--wr-stdout");
                run(runnerArgs);
                out("Created file " + out);
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Obtain the stdout output of the run";
        }
    }

    static class WorkflowRunCancelCommand implements CommandLeaf{

        @Override public String getCommand() {
            return "cancel";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware workflow-run cancel --help");
                out("       seqware workflow-run cancel <params>");
                out("");
                out("Description:");
                out("  Cancel a submitted or running workflow run.");
                out("");
                out("Required parameters:");
                out("  --accession <swid>  The SWID of the workflow run, comma separated (no-spaces) for multiple SWIDs");
                out("");
            } else {
                List<String> reqVals = reqVals(args, "--accession");
                List<Integer> swids = Lists.newArrayList();
                for (String val : reqVals) {
                    swids.add(swid(val));
                }
                int[] swidArr = Ints.toArray(swids);

                extras(args, "workflow-run cancel");

                WorkflowRuns.submitCancel(swidArr);
                out("Submitted request to cancel workflow run with SWID(s) " + Arrays.toString(swidArr));
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Cancel a submitted or running workflow run";
        }
    }

    static class WorkflowRunRetryCommand implements CommandLeaf{

        @Override public String getCommand() {
            return "retry";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware workflow-run retry --help");
                out("       seqware workflow-run retry <params>");
                out("");
                out("Description:");
                out("  Retry a failed or cancelled workflow run.");
                out("");
                out("Parameters:");
                out("  --accession <swid>  The SWID of the workflow run, comma separated (no-spaces) for multiple SWIDs");
                out("  --working-dir <dir> The working directory of the whitestar run");
                out("");
            } else {
                List<String> optVals = optVals(args, "--working-dir");
                if (!optVals.isEmpty()) {
                    for (String val : optVals) {
                        List<String> runnerArgs = new ArrayList<>();
                        runnerArgs.add("--plugin");
                        runnerArgs.add("io.seqware.pipeline.plugins.WorkflowRelauncher");
                        runnerArgs.add("--");
                        runnerArgs.add("--working-dir");
                        runnerArgs.add(val);
                        run(runnerArgs);
                    }
                    return;
                }

                List<String> reqVals = reqVals(args, "--accession");
                List<Integer> swids = Lists.newArrayList();
                for (String val : reqVals) {
                    swids.add(swid(val));
                }
                int[] swidArr = Ints.toArray(swids);

                extras(args, "workflow-run retry");

                WorkflowRuns.submitRetry(swidArr);
                out("Submitted request to retry workflow run with SWID " + Arrays.toString(swidArr));
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Retry a failed or cancelled workflow run skipping completed steps";
        }
    }

}
