package io.seqware.cli;

import io.seqware.Studies;
import net.sourceforge.seqware.common.util.TabExpansionUtil;

import java.util.List;

import static io.seqware.cli.Main.extras;
import static io.seqware.cli.Main.isHelp;
import static io.seqware.cli.Main.out;

/**
 * Collects the various SeqWare study commands
 */
class StudyCommands {

    static class StudyListCommand implements CommandLeaf {

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
                out("Usage: seqware study list --help");
                out("       seqware study list [params]");
                out("");
                out("Description:");
                out("  List all studies.");
                out("");
                out("Optional parameters:");
                out("  --tsv             Emit a tab-separated values list");
                out("");
            } else {
                boolean tsv = Main.flag(args, "--tsv");

                extras(args, "study list");

                if (tsv) {
                    out(Studies.studiesTsv());
                } else {
                    out(TabExpansionUtil.expansion(Studies.studiesTsv()));
                }
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "List all studies";
        }
    }
}
