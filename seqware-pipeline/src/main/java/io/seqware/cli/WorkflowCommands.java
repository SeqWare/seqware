package io.seqware.cli;

import io.seqware.Engines;
import io.seqware.common.model.WorkflowRunStatus;
import io.seqware.pipeline.SqwKeys;
import org.apache.commons.lang3.ArrayUtils;

import java.io.FileNotFoundException;
import java.io.PrintStream;
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
import static io.seqware.cli.Main.processOverrideParams;
import static io.seqware.cli.Main.reqVal;
import static io.seqware.cli.Main.run;

/**
 * Collects the various SeqWare study commands
 */
class WorkflowCommands {

    static class WorkflowIniCommand implements CommandLeaf {

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
                out("Usage: seqware workflow ini --help");
                out("       seqware workflow ini <params>");
                out("");
                out("Description:");
                out("  Generate an ini file for a workflow.");
                out("");
                out("Required parameters:");
                out("  --accession <swid>  The SWID of the workflow");
                out("");
                out("Optional parameters:");
                out("  --out <file>        Where to write the file (defaults to 'workflow.ini')");
                out("");
            } else {
                String id = reqVal(args, "--accession");
                String outfile = optVal(args, "--out", "workflow.ini");

                extras(args, "workflow ini");

                PrintStream origOut = System.out;
                PrintStream temp = null;
                try {
                    temp = new PrintStream(outfile);
                    System.setOut(temp);
                    run("--plugin", "net.sourceforge.seqware.pipeline.plugins.BundleManager", "--", "--list-workflow-params",
                            "--workflow-accession", id);
                } catch (FileNotFoundException e) {
                    kill("seqware: cannot write to '%s'.", outfile);
                } finally {
                    System.setOut(origOut);
                    if (temp != null) {
                        temp.close();
                    }
                }
                out("Created '%s'.", outfile);
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Generate an ini file for a workflow";
        }
    }

    static class WorkflowList implements CommandLeaf {

        @Override public String getCommand() {
            return "list";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, false)) {
                out("");
                out("Usage: seqware workflow list --help");
                out("       seqware workflow list [params]");
                out("");
                out("Description:");
                out("  List all installed workflows.");
                out("");
                out("Optional parameters:");
                out("  --tsv             Emit a tab-separated values list");
                out("");
            } else {
                boolean tsv = flag(args, "--tsv");

                extras(args, "workflow list");

                if (tsv) {
                    run("--plugin", "net.sourceforge.seqware.pipeline.plugins.BundleManager", "--", "--list-installed");
                } else {
                    run("--plugin", "net.sourceforge.seqware.pipeline.plugins.BundleManager", "--", "--list-installed", "--human-expanded");
                }
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "List all installed workflows";
        }
    }

    static class WorkflowReportCommand implements CommandLeaf {

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
                out("Usage: seqware workflow report --help");
                out("       seqware workflow report <params>");
                out("");
                out("Description:");
                out("  List the details of all runs of a given workflow.");
                out("");
                out("Required parameters (one of):");
                out("  --accession <swid>  The SWID of the workflow");
                out("  --status <status>   One of " + Arrays.toString(WorkflowRunStatus.values()));
                out("");
                out("Optional parameters:");
                out("  --out <file>        The name of the report file");
                out("  --tsv               Emit a tab-separated values report");
                out("  --when <date>       The date or date-range of runs to include");
                out("                      If omitted, all runs included");
                out("                      Dates are in the form YYYY-MM-DD");
                out("                      Date ranges are in the form YYYY-MM-DD:YYYY-MM-DD");
                out("");
            } else {
                String swid = optVal(args, "--accession", null);
                String status = optVal(args, "--status", null);
                String when = optVal(args, "--when", null);
                String out = optVal(args, "--out", null);
                boolean tsv = flag(args, "--tsv");

                if (status == null && swid == null) {
                    kill("seqware: specify one of status or swid");
                }

                extras(args, "workflow report");

                List<String> runnerArgs = new ArrayList<>();
                runnerArgs.add("--plugin");
                runnerArgs.add("net.sourceforge.seqware.pipeline.plugins.WorkflowRunReporter");
                runnerArgs.add("--");
                if (swid != null) {
                    runnerArgs.add("--workflow-accession");
                    runnerArgs.add(swid);
                }
                if (when != null) {
                    runnerArgs.add("--time-period");
                    runnerArgs.add(when);
                }
                if (out != null) {
                    runnerArgs.add("--output-filename");
                    runnerArgs.add(out);
                } else {
                    runnerArgs.add("--stdout");
                }
                if (!tsv) {
                    runnerArgs.add("--human");
                }
                if (status != null) {
                    runnerArgs.add("--status");
                    runnerArgs.add(status);
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
            return "List the details of all runs of a given workflow";
        }
    }

    static class WorkflowScheduleCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "schedule";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware workflow schedule [--help]");
                out("       seqware workflow schedule <params>");
                out("");
                out("Description:");
                out("  Schedule a workflow to be run.");
                out("");
                out("Required parameters:");
                out("  --accession <swid>         The SWID of the workflow to be run");
                out("  --host <host>              The host on which to launch the workflow run");
                out("");
                out("Optional parameters:");
                out("  --engine <type>            The engine that will process the workflow run.");
                out("                             May be one of: " + Engines.ENGINES_LIST);
                out("                             Defaults to the value of " + SqwKeys.SW_DEFAULT_WORKFLOW_ENGINE.getSettingKey());
                out("                             or '" + Engines.DEFAULT_ENGINE + "' if not specified.");
                out("  --parent-accession <swid>  The SWID of a parent to the workflow run");
                out("                             Repeat this parameter to provide multiple parents");
                out("  --override <key=value>     Override specific parameters from the workflow.ini");
                out("  --ini <ini-file>           An ini file to configure the workflow run ");
                out("                             Repeat this parameter to provide multiple files");
                out("  --input-file <input-file>  Track input files to workflow runs");
                out("                             Repeat this parameter to provide multiple files");
                out("");
            } else {
                String wfId = reqVal(args, "--accession");
                String host = reqVal(args, "--host");
                List<String> iniFiles = optVals(args, "--ini");
                List<String> inputFiles = optVals(args, "--input-file");
                String engine = optVal(args, "--engine", null);
                List<String> parentIds = optVals(args, "--parent-accession");
                List<String> override = optVals(args, "--override");

                extras(args, "workflow schedule");

                List<String> runnerArgs = new ArrayList<>();
                runnerArgs.add("--plugin");
                runnerArgs.add("io.seqware.pipeline.plugins.WorkflowScheduler");
                runnerArgs.add("--");
                runnerArgs.add("--workflow-accession");
                runnerArgs.add(wfId);
                if (engine != null) {
                    runnerArgs.add("--workflow-engine");
                    runnerArgs.add(engine);
                }
                if (!iniFiles.isEmpty()) {
                    runnerArgs.add("--ini-files");
                    runnerArgs.add(cdl(iniFiles));
                }
                if (!parentIds.isEmpty()) {
                    runnerArgs.add("--parent-accessions");
                    runnerArgs.add(cdl(parentIds));
                }
                if (!inputFiles.isEmpty()) {
                    runnerArgs.add("--input-files");
                    runnerArgs.add(cdl(inputFiles));
                }
                if (host != null) {
                    runnerArgs.add("--host");
                    runnerArgs.add(host);
                }

                List<String> overrideParams = processOverrideParams(override);

                String[] totalArgs = ArrayUtils.addAll(runnerArgs.toArray(new String[runnerArgs.size()]),
                        overrideParams.toArray(new String[overrideParams.size()]));
                run(totalArgs);
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Schedule a workflow to be run";
        }
    }
}
