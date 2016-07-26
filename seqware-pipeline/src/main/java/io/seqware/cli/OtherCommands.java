package io.seqware.cli;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.model.Annotatable;
import net.sourceforge.seqware.common.model.Attribute;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static io.seqware.cli.Main.extras;
import static io.seqware.cli.Main.flag;
import static io.seqware.cli.Main.isHelp;
import static io.seqware.cli.Main.kill;
import static io.seqware.cli.Main.optVal;
import static io.seqware.cli.Main.out;
import static io.seqware.cli.Main.reqVal;
import static io.seqware.cli.Main.run;

/**
 * Other high-level commands
 */
class OtherCommands {
    private static final SortedSet<String> ANNO_OBJS = new TreeSet<>(
            Arrays.asList("experiment", "file", "ius", "lane", "processing", "sample", "sequencer-run", "study", "workflow",
                    "workflow-run"));

    static class AnnotateCommand implements CommandLeaf {

        public String bashOpts(){
            return Joiner.on(' ').join(ANNO_OBJS);
        }

        @Override public String getCommand() {
            return "annotate";
        }

        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                annotateHelp();
            } else {
                String obj = args.remove(0);
                if (!ANNO_OBJS.contains(obj)) {
                    kill("seqware: '%s' is not a valid object type.  See 'seqware annotate --help'.", obj);
                } else {
                    if (isHelp(args, true)) {
                        annotateHelp();
                    } else {
                        String swid = optVal(args, "--accession", null);
                        String key = optVal(args, "--key", null);
                        String val = optVal(args, "--val", null);
                        boolean skip = flag(args, "--skip");
                        boolean unskip = flag(args, "--unskip");
                        if (skip && unskip) {
                            kill("seqware: cannot both skip and unzip");
                        }
                        String reason = optVal(args, "--reason", null);
                        String csv = optVal(args, "--csv", null);

                        extras(args, "annotate " + obj);

                        if (swid != null && key != null && val != null && !skip && !unskip && csv == null) {
                            String idFlag = "--" + obj + "-accession";
                            run("--plugin", "net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator", "--", idFlag, swid, "--key", key,
                                    "--value", val);
                        } else if (swid != null && key == null && val == null && (skip || unskip) && csv == null) {
                            String idFlag = "--" + obj + "-accession";
                            if (reason == null) {
                                run("--plugin", "net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator", "--", idFlag, swid, "--skip",
                                        skip ? "true" : "false");
                            } else {
                                run("--plugin", "net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator", "--", idFlag, swid, "--skip",
                                        skip ? "true" : "false", "--value", reason);
                            }
                        } else if (swid == null && key == null && val == null && !skip && !unskip && csv != null) {
                            run("--plugin", "net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator", "--", "--file", csv);
                        } else {
                            kill("seqware: invalid set of parameters to 'seqware annotate'. See 'seqware annotate --help'.");
                        }
                    }
                }
            }
        }

        @Override public String displayOneLineDescription() {
            return "Add arbitrary key/value pairs to seqware objects";
        }

        private void annotateHelp() {
            out("");
            out("Usage: seqware annotate [--help]");
            out("       seqware annotate <object> --accession <swid> --key <key> --val <value>");
            out("       seqware annotate <object> --accession <swid> --skip [--reason <text>]");
            out("       seqware annotate <object> --accession <swid> --unskip [--reason <text>]");
            out("       seqware annotate <object> --csv <file>");
            out("");
            out("Description:");
            out("  Add arbitrary key/value pairs to seqware objects.");
            out("");
            out("Objects:");
            for (String obj : ANNO_OBJS) {
                out("  " + obj);
            }
            out("");
            out("Parameters:");
            out("  --csv <file>        Bulk annotation from CSV file of: accession, key, value");
            out("  --accession <swid>  The SWID of the object to annotate");
            out("  --key <key>         The identifier of the annotation");
            out("  --reason <text>     The reason the object is skipped/unskipped");
            out("  --skip              Sets the skip attribute flag on the object");
            out("  --unskip            Unsets the skip attribute flag on the object");
            out("  --val <value>       The value of the annotation");
            out("");
        }
    }

    static class QueryCommand implements CommandLeaf {

        public String bashOpts(){
            return Joiner.on(' ').join(ANNO_OBJS);
        }

        @Override public String getCommand() {
            return "query";
        }

        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                queryHelp();
            } else {
                String obj = args.remove(0);
                if (!ANNO_OBJS.contains(obj)) {
                    kill("seqware: '%s' is not a valid object type.  See 'seqware query --help'.", obj);
                } else {
                    if (isHelp(args, true)) {
                        queryHelp();
                    } else {
                        int swid = Integer.valueOf(reqVal(args, "--accession"));
                        boolean annotationsOnly = flag(args, "--annotations");
                        Metadata metadata = MetadataFactory.get(ConfigTools.getSettings());
                        Annotatable<? extends Attribute> target = null;
                        switch (obj) {
                        case "experiment":
                            target = metadata.getExperiment(swid);
                            break;
                        case "file":
                            target = metadata.getFile(swid);
                            break;
                        case "ius":
                            target = metadata.getIUS(swid);
                            break;
                        case "lane":
                            target = metadata.getLane(swid);
                            break;
                        case "processing":
                            target = metadata.getProcessing(swid);
                            break;
                        case "sample":
                            target = metadata.getSample(swid);
                            break;
                        case "sequencer-run":
                            target = metadata.getSequencerRun(swid);
                            break;
                        case "study":
                            target = metadata.getStudy(swid);
                            break;
                        case "workflow":
                            target = metadata.getWorkflow(swid);
                            break;
                        case "workflow-run":
                            target = metadata.getWorkflowRun(swid);
                            break;
                        default:
                            kill("seqware: invalid set of parameters to 'seqware query'. See 'seqware query --help'.");
                        }
                        Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
                        if (target == null) {
                            kill("seqware: could not complete query");
                        }
                        if (annotationsOnly) {
                            System.out.println(gson.toJson(target.getAnnotations()));
                        } else {
                            System.out.println(gson.toJson(target));
                        }
                    }
                }
            }
        }

        @Override public String displayOneLineDescription() {
            return "Display ad-hoc information about seqware objects";
        }

        private void queryHelp() {
            out("");
            out("Usage: seqware query [--help]");
            out("       seqware query <object> --accession <swid>");
            out("");
            out("Description:");
            out("  Display an object in JSON format.");
            out("");
            out("Objects:");
            for (String obj : ANNO_OBJS) {
                out("  " + obj);
            }
            out("");
            out("Parameters:");
            out("  --accession <swid>  The SWID of the object to display");
            out("  --annotations       Display only the annotations from the object");
            out("");
        }
    }

    static class CopyCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "copy";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
            if (isHelp(args, true)) {
                out("");
                out("Usage: seqware copy [--help]");
                out("       seqware copy <source> <destination>");
                out("");
                out("Description:");
                out("  Convenience tool to copy files between local and remote file systems, e.g. S3.");
                out("  If destination is a local directory, the input file's name will be used for the");
                out("  output file.");
                out("");
            } else {
                if (args.size() == 2) {
                    String src = args.remove(0);
                    String dest = args.remove(0);

                    extras(args, "copy");

                    String destFlag = new File(dest).isDirectory() ? "--output-dir" : "--output-file";
                    run("--plugin", "net.sourceforge.seqware.pipeline.plugins.ModuleRunner", "--", "--module",
                            "net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles", "--no-metadata", "--", "--force-copy",
                            "--input-file", src, destFlag, dest);
                } else {
                    kill("seqware: invalid arguments to 'seqware copy'. See 'seqware copy --help'.");
                }
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Copy files between local and remote file systems";
        }
    }

    static class CheckDBCommand implements CommandLeaf{

        @Override public String getCommand() {
            return "checkdb";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
                if (isHelp(args, false)) {
                    out("");
                    out("Usage: seqware checkdb --help");
                    out("       seqware checkdb");
                    out("");
                    out("Description:");
                    out("  Using a direct database connection, check whether the meta db contains any content that deviates from recommended conventions.");
                    out("");
                } else {

                    List<String> runnerArgs = new ArrayList<>();
                    runnerArgs.add("--plugin");
                    runnerArgs.add("net.sourceforge.seqware.pipeline.plugins.checkdb.CheckDB");
                    runnerArgs.add("--");

                    run(runnerArgs);
                }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Check the seqware database for convention errors";
        }
    }
}
