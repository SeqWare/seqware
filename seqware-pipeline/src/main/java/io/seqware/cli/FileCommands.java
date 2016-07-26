package io.seqware.cli;

import io.seqware.Reports;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugins.fileprovenance.ProvenanceUtility;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.seqware.cli.Main.extras;
import static io.seqware.cli.Main.isHelp;
import static io.seqware.cli.Main.optVal;
import static io.seqware.cli.Main.optVals;
import static io.seqware.cli.Main.out;
import static io.seqware.cli.Main.run;

/**
 * Collects the various SeqWare file commands
 */
class FileCommands {

    static class FileReportCommand implements CommandLeaf {

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
                out("Usage: seqware files report --help");
                out("       seqware files report <params>");
                out("");
                out("Description:");
                out("  A report of the provenance of output files.");
                out("");
                out("Optional parameters:");
                out("  --out <file>                   The name of the output file");
                for (ProvenanceUtility.HumanProvenanceFilters filter : ProvenanceUtility.HumanProvenanceFilters.values()) {
                    out("  --" + filter.human_str + " <value>            Limit files to the specified " + filter.desc
                            + ". Can occur multiple times.");
                }
                out("");
            } else {
                Map<ProvenanceUtility.HumanProvenanceFilters, List<String>> map = new HashMap<>();
                for (ProvenanceUtility.HumanProvenanceFilters filter : ProvenanceUtility.HumanProvenanceFilters.values()) {
                    List<String> optVals = optVals(args, "--" + filter.human_str);
                    map.put(filter, optVals);
                }
                String file = optVal(args, "--out", (new Date() + ".tsv").replace(" ", "_"));

                extras(args, "files report");

                List<String> runnerArgs = new ArrayList<>();
                runnerArgs.add("--plugin");
                runnerArgs.add("net.sourceforge.seqware.pipeline.plugins.fileprovenance.FileProvenanceReporter");
                runnerArgs.add("--");

                // check if all values in the map are empty
                boolean allEmpty = true;
                for (Map.Entry<ProvenanceUtility.HumanProvenanceFilters, List<String>> e : map.entrySet()) {
                    if (!e.getValue().isEmpty()) {
                        allEmpty = false;
                    }
                }

                if (allEmpty) {
                    runnerArgs.add("--all");
                } else {
                    for (Map.Entry<ProvenanceUtility.HumanProvenanceFilters, List<String>> e : map.entrySet()) {
                        for (String val : e.getValue()) {
                            runnerArgs.add("--" + e.getKey().human_str);
                            runnerArgs.add(val);
                        }
                    }
                }
                if (file != null) {
                    runnerArgs.add("--out");
                    runnerArgs.add(file);
                }

                run(runnerArgs);
                out("Created file " + file);
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "A report of the provenance of output files";
        }
    }

    static class FileRefreshCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "refresh";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            Log.stdoutWithTime("Triggered provenance report");
            Reports.triggerProvenanceReport();
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Refresh the static simplified provenance report table";
        }
    }

}
