package io.seqware.cli;

import io.seqware.common.model.WorkflowRunStatus;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static io.seqware.cli.Main.extras;
import static io.seqware.cli.Main.flag;
import static io.seqware.cli.Main.isHelp;
import static io.seqware.cli.Main.kill;
import static io.seqware.cli.Main.optVals;
import static io.seqware.cli.Main.out;
import static io.seqware.cli.Main.reqVal;
import static io.seqware.cli.Main.run;
import static io.seqware.cli.Main.swid;

/**
 * Collects the various SeqWare create commands
 */
class CreateCommands {

    static class CreateExperimentCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "experiment";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware create experiment [--help]");
                out("       seqware create experiment --interactive");
                out("       seqware create experiment <fields>");
                out("");
                out("Note: It is strongly recommended that the '--interactive' mode be used when");
                out("      possible, since some columns have a dynamic set of allowable values.");
                out("");
                out("Required fields:");
                out("  --description <val>");
                out("  --platform-id <val>      Dynamic-valued column");
                out("  --study-accession <val>");
                out("  --title <val>");
                out("");
            } else {
                runCreateTable(args, "experiment", "description", "platform_id", "study_accession", "title");
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "";
        }
    }

    static class CreateIusCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "ius";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware create ius [--help]");
                out("       seqware create ius --interactive");
                out("       seqware create ius <fields>");
                out("");
                out("Required fields:");
                out("  --barcode <val>");
                out("  --description <val>");
                out("  --lane-accession <val>");
                out("  --name <val>");
                out("  --sample-accession <val>");
                out("  --skip <val>");
                out("");
            } else {
                runCreateTable(args, "ius", "barcode", "description", "lane_accession", "name", "sample_accession", "skip");
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "";
        }
    }

    static class CreateLaneCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "lane";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware create lane [--help]");
                out("       seqware create lane --interactive");
                out("       seqware create lane <fields>");
                out("");
                out("Note: It is strongly recommended that the '--interactive' mode be used when");
                out("      possible, since some columns have a dynamic set of allowable values.");
                out("");
                out("Required fields:");
                out("  --cycle-descriptor <val>");
                out("  --description <val>");
                out("  --lane-number <val>");
                out("  --library-selection-accession <val>  Dynamic-valued field");
                out("  --library-source-accession <val>     Dynamic-valued field");
                out("  --library-strategy-accession <val>   Dynamic-valued field");
                out("  --name <val>");
                out("  --sequencer-run-accession <val>");
                out("  --skip <val>");
                out("  --study-type-accession <val>         Dynamic-valued field");
                out("");
            } else {
                runCreateTable(args, "lane", "cycle_descriptor", "description", "lane_number", "library_selection_accession",
                        "library_source_accession", "library_strategy_accession", "name", "sequencer_run_accession", "skip",
                        "study_type_accession");
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "";
        }
    }

    static class CreateSampleCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "sample";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware create sample [--help]");
                out("       seqware create sample --interactive");
                out("       seqware create sample <fields>");
                out("");
                out("Note: It is strongly recommended that the '--interactive' mode be used when");
                out("      possible, since some columns have a dynamic set of allowable values.");
                out("");
                out("Required fields:");
                out("  --description <val>");
                out("  --experiment-accession <val>");
                out("  --organism-id <val>           Dynamic-valued field");
                out("  --title <val>");
                out("Optional fields:");
                out("  --parent-sample-accession <val>");
                out("");
            } else {
                runCreateTable(args, "sample", "description", "experiment_accession", "organism_id", "title");
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "";
        }
    }

    static class CreateSequencerRunCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "sequencer-run";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware create sequencer-run [--help]");
                out("       seqware create sequencer-run --interactive");
                out("       seqware create sequencer-run <fields>");
                out("");
                out("Note: It is strongly recommended that the '--interactive' mode be used when");
                out("      possible, since some columns have a dynamic set of allowable values.");
                out("");
                out("Required fields:");
                out("  --description <val>");
                out("  --file-path <val>");
                out("  --name <val>");
                out("  --paired-end <val>");
                out("  --platform-accession <val>  Dynamic-valued field");
                out("  --skip <val>");
                out("");
            } else {
                runCreateTable(args, "sequencer_run", "description", "file_path", "name", "paired_end", "platform_accession", "skip");
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "";
        }
    }

    static class CreateStudyCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "study";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware create study [--help]");
                out("       seqware create study --interactive");
                out("       seqware create study <fields>");
                out("");
                out("Note: It is strongly recommended that the '--interactive' mode be used when");
                out("      possible, since some columns have a dynamic set of allowable values.");
                out("");
                out("Required fields:");
                out("  --accession <val>");
                out("  --center-name <val>");
                out("  --center-project-name <val>");
                out("  --description <val>");
                out("  --study-type <val>           Dynamic-valued field");
                out("  --title <val>");
                out("");
            } else {
                runCreateTable(args, "study", "accession", "center_name", "center_project_name", "description", "study_type", "title");
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "";
        }
    }

    static class CreateWorkflowRunCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "workflow-run";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware create workflow-run [--help]");
                out("       seqware create workflow-run --interactive");
                out("       seqware create workflow-run <fields>");
                out("");
                out("Required fields:");
                out("  --workflow-accession <val>");
                out("  --parent-accession <swid>  The SWID of a parent to the workflow run");
                out("                             Repeat this parameter to provide multiple parents");
                out("Optional fields:");
                out("  --file <type::meta-type::path>       Add (output) files as a part of the workflow run.");
                out("                                       Repeat this parameter to add multiple files");
                out("  --input-file <swid>                  Add (input) files as a part of the workflow run.");
                out("                                       Repeat this parameter to add multiple files");
                out("");
            } else {
                args.add("--status");
                args.add(WorkflowRunStatus.completed.toString());
                runCreateTable(args, "workflow_run", "workflow_accession", "status");
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "";
        }
    }

    static class CreateWorkflowCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "workflow";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware create workflow [--help]");
                out("       seqware create workflow --interactive");
                out("       seqware create workflow <fields>");
                out("");
                out("Required fields:");
                out("  --name <val>");
                out("  --version <val>");
                out("  --description <val>");
                out("");
            } else {
                runCreateTable(args, "workflow", "name", "version", "description");
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "";
        }
    }

    private static void processArgument(List<String> args, List<String> runnerArgs, String col) {
        runnerArgs.add("--field");
        String key = "--" + col.replace('_', '-');
        String arg = String.format("%s::%s", col, reqVal(args, key));
        runnerArgs.add(arg);
    }

    private static void runCreateTable(List<String> args, String table, String... cols) {
        if (flag(args, "--interactive")) {
            extras(args, "create " + table.replace('_', '-'));

            run("--plugin", "net.sourceforge.seqware.pipeline.plugins.Metadata", "--", "--table", table, "--create", "--interactive");
        } else {
            List<String> runnerArgs = new ArrayList<>();
            runnerArgs.add("--plugin");
            runnerArgs.add("net.sourceforge.seqware.pipeline.plugins.Metadata");
            runnerArgs.add("--");
            runnerArgs.add("--table");
            runnerArgs.add(table);
            runnerArgs.add("--create");

            for (String col : cols) {
                processArgument(args, runnerArgs, col);
            }

            // workflow-run tables can have (potentially multiple) file parameters, pass these through after doing some validation
            if (table.equals("sample")) {
                final boolean match = args.stream().anyMatch(val -> val.contains("--parent-sample-accession"));
                if (match) {
                    processArgument(args, runnerArgs, "parent_sample_accession");
                }
            } else if (table.equals("workflow_run")) {
                List<String> files = optVals(args, "--file");
                for (String file : files) {
                    // do validation
                    if (StringUtils.countMatches(file, "::") != 2) {
                        kill("seqware: improper number of separator :: in '%s'.", file);
                    }
                    if (file.split("::").length != 3) {
                        kill("seqware: improper format of file values in '%s'.", file);
                    }
                    runnerArgs.add("--file");
                    runnerArgs.add(file);
                }
                List<String> inputFiles = optVals(args, "--input-file");
                for (String file : inputFiles) {
                    runnerArgs.add("--input-file");
                    // validate
                    int swid = swid(file);
                    runnerArgs.add(String.valueOf(swid));
                }

                // workflow runs should also have parent accessions in order to be visible to deciders
                List<String> parentAccessions = optVals(args, "--parent-accession");
                if (parentAccessions.size() < 1) {
                    kill("seqware: by convention, workflow runs should be hooked up to parent accessions for metadata tracking and deciders.");
                }
                for (String parentAccession : parentAccessions) {
                    runnerArgs.add("--parent-accession");
                    runnerArgs.add(parentAccession);
                }
            }

            extras(args, "create " + table.replace('_', '-'));

            run(runnerArgs);
        }
    }

}
