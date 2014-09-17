package io.seqware.cli;

import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import com.google.common.primitives.Ints;
import io.seqware.Engines;
import io.seqware.Reports;
import io.seqware.Studies;
import io.seqware.WorkflowRuns;
import io.seqware.common.model.WorkflowRunStatus;
import io.seqware.pipeline.SqwKeys;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.TabExpansionUtil;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.pipeline.bundle.Bundle;
import net.sourceforge.seqware.pipeline.bundle.BundleInfo;
import net.sourceforge.seqware.pipeline.plugins.fileprovenance.ProvenanceUtility;
import net.sourceforge.seqware.pipeline.plugins.fileprovenance.ProvenanceUtility.HumanProvenanceFilters;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

/*
 * TODO:
 * - add descriptions to fields of create
 */
public class Main {

    /**
     * Take a List and create a 'delim' delimited String.
     * 
     * @param tokens
     * @param delim
     * @return
     */
    private static String dl(List<String> tokens, String delim) {
        if (tokens.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder(tokens.get(0));
            for (int i = 1; i < tokens.size(); i++) {
                sb.append(delim);
                sb.append(tokens.get(i));
            }
            return sb.toString();
        }
    }

    /**
     * Create a comma delimited string from a List
     * 
     * @param tokens
     * @return
     */
    private static String cdl(List<String> tokens) {
        return dl(tokens, ",");
    }

    private static int swid(String swid) {
        try {
            return Integer.parseInt(swid);
        } catch (NumberFormatException e) {
            kill("seqware: invalid seqware accession: '" + swid + "'");
            return 0; // non-reachable
        }
    }

    private static void out(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

    private static void err(String format, Object... args) {
        System.err.println(String.format(format, args));
    }

    private static List<String> processOverrideParams(List<String> override) {
        List<String> overrideParams = new ArrayList<>();
        if (!override.isEmpty()) {
            overrideParams.add("--");
            for (String entry : override) {
                String key = entry.substring(0, entry.indexOf('='));
                String value = entry.substring(entry.indexOf('=') + 1);
                overrideParams.add("--" + key);
                overrideParams.add(value);
            }
        }
        return overrideParams;
    }

    private static class Kill extends RuntimeException {
    }

    private static void kill(String format, Object... args) {
        err(format, args);
        throw new Kill();
    }

    private static void invalid(String cmd) {
        kill("seqware: '%s' is not a seqware command. See 'seqware --help'.", cmd);
    }

    private static void invalid(String cmd, String sub) {
        kill("seqware: '%s %s' is not a seqware command. See 'seqware %s --help'.", cmd, sub, cmd);
    }

    private static void extras(List<String> args, String curCommand) {
        if (args.size() > 0) {
            kill("seqware: unexpected arguments to '%s': %s", curCommand, dl(args, " "));
        }
    }

    private static boolean flag(List<String> args, String flag) {
        boolean found = false;
        for (int i = 0; i < args.size(); i++) {
            if (flag.equals(args.get(i))) {
                if (found) {
                    kill("seqware: multiple instances of '%s'.", flag);
                } else {
                    found = true;
                    args.remove(i);
                }
            }
        }
        return found;
    }

    private static List<String> optVals(List<String> args, String key) {
        List<String> vals = new ArrayList<>();

        for (int i = 0; i < args.size();) {
            String s = args.get(i);
            if (key.equals(s)) {
                args.remove(i);
                if (i < args.size()) {
                    String val = args.remove(i);
                    if (!val.startsWith("--")) {
                        String[] ss = val.split(",");
                        if (ss.length > 0) {
                            vals.addAll(Arrays.asList(ss));
                            continue;
                        }
                    }
                }
                kill("seqware: missing required argument to '%s'.", key);
            } else {
                i++;
            }
        }

        return vals;
    }

    private static List<String> reqVals(List<String> args, String key) {
        List<String> vals = optVals(args, key);

        if (vals.isEmpty()) {
            kill("seqware: missing required flag '%s'.", key);
        }

        return vals;
    }

    private static String optVal(List<String> args, String key, String defaultVal) {
        String val = defaultVal;

        List<String> vals = optVals(args, key);
        if (vals.size() == 1) {
            val = vals.get(0);
        } else if (vals.size() > 1) {
            kill("seqware: multiple instances of '%s'.", key);
        }

        return val;
    }

    private static String reqVal(List<String> args, String key) {
        String val = optVal(args, key, null);

        if (val == null) {
            kill("seqware: missing required flag '%s'.", key);
        }

        return val;
    }

    private static boolean isHelp(List<String> args, boolean valOnEmpty) {
        if (args.isEmpty()) return valOnEmpty;

        String first = args.get(0);
        return first.equals("-h") || first.equals("--help");
    }

    public static final AtomicBoolean DEBUG = new AtomicBoolean(false);
    public static final AtomicBoolean VERBOSE = new AtomicBoolean(false);

    private static void run(String... args) {
        if (VERBOSE.get()) {
            args = ObjectArrays.concat("--verbose", args);
        }
        if (DEBUG.get()) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].contains(" ")) {
                    args[i] = "'" + args[i] + "'";
                }
            }
            out("PluginRunner.main: %s", dl(Arrays.asList(args), " "));
        } else {
            PluginRunner.main(args);
        }
    }

    private static void run(List<String> runnerArgs) {
        run(runnerArgs.toArray(new String[runnerArgs.size()]));
    }

    // COMMANDS:

    private static final SortedSet<String> ANNO_OBJS = new TreeSet<>(Arrays.asList("experiment", "file", "ius", "lane", "processing",
            "sample", "sequencer-run", "study", "workflow", "workflow-run"));

    private static void annotateHelp() {
        out("");
        out("Usage: seqware annotate [--help]");
        out("       seqware annotate <object> --accession <swid> --key <key> --val <value>");
        out("       seqware annotate <object> --accession <swid> --skip [--reason <text>]");
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
        out("  --reason <text>     The reason the object is skipped");
        out("  --skip              Sets the skip attribute flag on the object");
        out("  --val <value>       The value of the annotation");
        out("");
    }

    private static void annotate(List<String> args) {
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
                    String reason = optVal(args, "--reason", null);
                    String csv = optVal(args, "--csv", null);

                    extras(args, "annotate " + obj);

                    if (swid != null && key != null && val != null & skip == false && csv == null) {
                        String idFlag = "--" + obj + "-accession";
                        run("--plugin", "net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator", "--", idFlag, swid, "--key", key,
                                "--value", val);
                    } else if (swid != null && key == null && val == null & skip == true && csv == null) {
                        String idFlag = "--" + obj + "-accession";
                        if (reason == null) {
                            run("--plugin", "net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator", "--", idFlag, swid, "--skip",
                                    "true");
                        } else {
                            run("--plugin", "net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator", "--", idFlag, swid, "--skip",
                                    "true", "--value", reason);
                        }
                    } else if (swid == null && key == null && val == null & skip == false && csv != null) {
                        run("--plugin", "net.sourceforge.seqware.pipeline.plugins.AttributeAnnotator", "--", "--file", csv);
                    } else {
                        kill("seqware: invalid set of parameters to 'seqware annotate'. See 'seqware annotate --help'.");
                    }
                }
            }
        }
    }

    private static void bundleInstall(List<String> args) {
        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware bundle install [--help]");
            out("       seqware bundle install --zip <bundle-zip>");
            out("");
            out("Description:");
            out("  Inform the Seqware system of the availability of a bundle.");
            out("");
            out("Parameters:");
            out("  --zip <bundle-zip>  The zip file of the bundle");
            out("");
        } else {
            String zip = reqVal(args, "--zip");

            extras(args, "bundle install");

            run("--plugin", "net.sourceforge.seqware.pipeline.plugins.BundleManager", "--", "--install", "--bundle", zip);
        }
    }

    private static void bundleInstallDirOnly(List<String> args) {
        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware bundle install-dir-only [--help]");
            out("       seqware bundle install-dir-only --dir <bundle-dir>");
            out("");
            out("Description:");
            out("  Inform the Seqware system of the availability of a bundle.");
            out("");
            out("Parameters:");
            out("  --dir <bundle-dir>  The zip file of the bundle");
            out("");
        } else {
            String zip = reqVal(args, "--dir");

            extras(args, "bundle install-dir-only");

            run("--plugin", "net.sourceforge.seqware.pipeline.plugins.BundleManager", "--", "--install-dir-only", "--bundle", zip);
        }
    }

    private static WorkflowInfo findWorkflowInfo(File dir, String name, String version) {
        BundleInfo bi = Bundle.findBundleInfo(dir);

        List<WorkflowInfo> found = new ArrayList<>();

        for (WorkflowInfo wi : bi.getWorkflowInfo()) {
            boolean n = name == null || wi.getName().equals(name);
            boolean v = version == null || wi.getVersion().equals(version);
            if (n && v) found.add(wi);
        }

        if (found.isEmpty()) {
            if (name == null && version == null) {
                kill("seqware: no workflow defined in " + bi.parsedFrom().getAbsolutePath());
            } else if (version == null) {
                kill("seqware: no workflow with name '" + name + "' defined in " + bi.parsedFrom().getAbsolutePath());
            } else if (name == null) {
                kill("seqware: no workflow with version '" + version + "' defined in " + bi.parsedFrom().getAbsolutePath());
            } else {
                kill("seqware: no workflow with name '" + name + "' and version '" + version + "' defined in "
                        + bi.parsedFrom().getAbsolutePath());
            }
        } else if (found.size() > 1) {
            if (name == null && version == null) {
                kill("seqware: multiple workflows defined in " + bi.parsedFrom().getAbsolutePath());
            } else if (version == null) {
                kill("seqware: multiple workflows with name '" + name + "' defined in " + bi.parsedFrom().getAbsolutePath());
            } else if (name == null) {
                kill("seqware: multiple workflows with version '" + version + "' defined in " + bi.parsedFrom().getAbsolutePath());
            } else {
                kill("seqware: multiple workflows with name '" + name + "' and version '" + version + "' defined in "
                        + bi.parsedFrom().getAbsolutePath());
            }
        }

        return found.get(0);
    }

    private static void bundleLaunch(List<String> args) {
        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware bundle launch [--help]");
            out("       seqware bundle launch <params>");
            out("");
            out("Description:");
            out("  Launch a specified workflow in a bundle directory.");
            out("");
            out("Required parameters:");
            out("  --dir <bundle-dir>  The root directory of the bundle");
            out("");
            out("Optional parameters:");
            out("  --engine <type>            The engine that will process the workflow run.");
            out("                             May be one of: " + Engines.ENGINES_LIST);
            out("                             Defaults to the value of " + SqwKeys.SW_DEFAULT_WORKFLOW_ENGINE.getSettingKey());
            out("                             or '" + Engines.DEFAULT_ENGINE + "' if not specified.");
            out("  --ini <ini-file>           An ini file to configure the workflow run.");
            out("                             Repeat this parameter to provide multiple files.");
            out("                             Defaults to the value of the 'config' node in metadata.xml.");
            out("  --name <wf-name>           The name of the workflow in the bundle.");
            out("  --version <ver>            The version of the workflow in the bundle.");
            out("  --override <key=value>     Override specific parameters from the workflow.ini");
            out("  --no-metadata              Run without per-step workflow metadata tracking");
            out("");
        } else {
            String dir = reqVal(args, "--dir");
            List<String> inis = optVals(args, "--ini");
            String name = optVal(args, "--name", null);
            String version = optVal(args, "--version", null);
            String engine = optVal(args, "--engine", null);
            List<String> override = optVals(args, "--override");
            boolean noMetadata = flag(args, "--no-metadata");

            extras(args, "bundle launch");

            File bundleDir = new File(dir);
            WorkflowInfo wi = findWorkflowInfo(bundleDir, name, version);
            name = wi.getName();
            version = wi.getVersion();
            if (inis.isEmpty()) {
                inis.add(wi.getConfigPath());
            }
            inis = resolveFiles(bundleDir, inis);
            List<String> overrideParams = processOverrideParams(override);

            out("Performing launch of workflow '" + name + "' version '" + version + "'");

            String[] runParams;
            if (engine == null) {
                runParams = new String[] { "--plugin", "io.seqware.pipeline.plugins.WorkflowLifecycle", "--", "--wait", "--bundle", dir,
                        "--workflow", name, "--version", version, "--ini-files", cdl(inis) };
            } else {
                runParams = new String[] { "--plugin", "io.seqware.pipeline.plugins.WorkflowLifecycle", "--", "--wait", "--bundle", dir,
                        "--workflow", name, "--version", version, "--ini-files", cdl(inis), "--workflow-engine", engine };
            }
            if (noMetadata) {
                runParams = ArrayUtils.add(runParams, "--no-metadata");
            }
            String[] addAll = ArrayUtils.addAll(runParams, overrideParams.toArray(new String[overrideParams.size()]));
            run(addAll);
        }
    }

    private static List<String> resolveFiles(File bundleDir, List<String> filenames) {
        List<String> resolved = new ArrayList<>();
        for (String filename : filenames) {
            String s = Bundle.resolveWorkflowBundleDirPath(bundleDir, filename);
            File f = new File(s);
            if (!f.exists()) {
                kill("seqware: could not find file " + f.getAbsolutePath());
            } else {
                resolved.add(f.getAbsolutePath());
            }
        }
        return resolved;
    }

    private static void bundleDryRun(List<String> args) {
        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware bundle dry-run [--help]");
            out("       seqware bundle dry-run <params>");
            out("");
            out("Description:");
            out("  Perform all steps to prepare for a launch, but not actually launch.");
            out("");
            out("Required parameters:");
            out("  --dir <bundle-dir>  The root directory of the bundle");
            out("");
            out("Optional parameters:");
            out("  --engine <type>     The engine that will process the workflow run.");
            out("                      May be one of: " + Engines.ENGINES_LIST);
            out("                      Defaults to the value of " + SqwKeys.SW_DEFAULT_WORKFLOW_ENGINE.getSettingKey());
            out("                      or '" + Engines.DEFAULT_ENGINE + "' if not specified.");
            out("  --ini <ini-file>    An ini file to configure the workflow run");
            out("                      Repeat this parameter to provide multiple files");
            out("                      Defaults to the value of the 'config' node in metadata.xml");
            out("  --name <wf-name>    The name of the workflow in the bundle");
            out("  --version <ver>     The version of the workflow in the bundle");
            out("");
        } else {
            String dir = reqVal(args, "--dir");
            List<String> inis = optVals(args, "--ini");
            String name = optVal(args, "--name", null);
            String version = optVal(args, "--version", null);
            String engine = optVal(args, "--engine", null);

            extras(args, "bundle dry-run");

            File bundleDir = new File(dir);
            WorkflowInfo wi = findWorkflowInfo(bundleDir, name, version);
            name = wi.getName();
            version = wi.getVersion();
            if (inis.isEmpty()) {
                inis.add(wi.getConfigPath());
            }
            inis = resolveFiles(bundleDir, inis);

            out("Performing dry-run of workflow '" + name + "' version '" + version + "'");

            if (engine == null) {
                run("--plugin", "io.seqware.pipeline.plugins.WorkflowLauncher", "--", "--bundle", dir, "--workflow", name, "--version",
                        version, "--ini-files", cdl(inis), "--no-run");
            } else {
                run("--plugin", "io.seqware.pipeline.plugins.WorkflowLauncher", "--", "--bundle", dir, "--workflow", name, "--version",
                        version, "--ini-files", cdl(inis), "--no-run", "--workflow-engine", engine);
            }
        }
    }

    private static void bundleList(List<String> args) {
        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware bundle list [--help]");
            out("       seqware bundle list --dir <bundle-dir>");
            out("");
            out("Description:");
            out("  List workflows within a bundle directory.");
            out("");
            out("Parameters:");
            out("  --dir <bundle-dir>  The root directory of the bundle");
            out("");
        } else {
            String dir = reqVal(args, "--dir");

            extras(args, "bundle list");

            run("--plugin", "net.sourceforge.seqware.pipeline.plugins.BundleManager", "--", "--list", "--bundle", dir);
        }
    }

    private static void bundlePackage(List<String> args) {
        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware bundle package [--help]");
            out("       seqware bundle package <params>");
            out("");
            out("Description:");
            out("  Package a bundle directory into a zip file.");
            out("");
            out("Required parameters:");
            out("  --dir <bundle-dir>  The root directory of the bundle");
            out("");
            out("Optional parameters:");
            out("  --to <dir>          The directory to place the zip");
            out("                      Defaults to the current directory");
            out("");
        } else {
            String dir = new File(reqVal(args, "--dir")).getAbsolutePath();
            String to = optVal(args, "--to", null);

            extras(args, "bundle package");

            String outdir;
            if (to == null) {
                outdir = new File("").getAbsolutePath();
            } else {
                outdir = new File(to).getAbsolutePath();
            }

            run("--plugin", "net.sourceforge.seqware.pipeline.plugins.BundleManager", "--", "--path-to-package", dir, "--bundle", outdir);
        }
    }

    private static void bundle(List<String> args) {
        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware bundle [--help]");
            out("       seqware bundle <sub-command> [--help]");
            out("");
            out("Description:");
            out("  Interact with a workflow bundle.");
            out("");
            out("Sub-commands:");
            out("  dry-run           Perform all steps to prepare for a launch, but not actually launch");
            out("  install           Inform the Seqware system of the availability of a standard bundle");
            out("  install-dir-only  Inform the Seqware system of the availability of an unzipped bundle");
            out("  launch            Launch a specified workflow in a bundle directory");
            out("  list              List workflows within a bundle directory");
            out("  package           Package a bundle directory into a zip file");
            out("");
        } else {
            String cmd = args.remove(0);
            if (null != cmd) switch (cmd) {
            case "dry-run":
                bundleDryRun(args);
                break;
            case "install":
                bundleInstall(args);
                break;
            case "install-dir-only":
                bundleInstallDirOnly(args);
                break;
            case "launch":
                bundleLaunch(args);
                break;
            case "list":
                bundleList(args);
                break;
            case "package":
                bundlePackage(args);
                break;
            default:
                invalid("bundle", cmd);
                break;
            }
        }
    }

    private static void copy(List<String> args) {
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
                runnerArgs.add("--field");
                String key = "--" + col.replace('_', '-');
                String arg = String.format("%s::%s", col, reqVal(args, key));
                runnerArgs.add(arg);
            }

            // workflow-run tables can have (potentially multiple) file parameters, pass these through after doing some validation
            if (table.equals("workflow_run")) {
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

    private static void createExperiment(List<String> args) {
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

    private static void createIus(List<String> args) {
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

    private static void createLane(List<String> args) {
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

    private static void createSample(List<String> args) {
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
            out("");
        } else {
            runCreateTable(args, "sample", "description", "experiment_accession", "organism_id", "title");
        }
    }

    private static void createSequencerRun(List<String> args) {
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

    private static void createStudy(List<String> args) {
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

    private static void createWorkflowRun(List<String> args) {
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
            out("  --file <type::meta-type::path>       Add files as a part of the workflow run.");
            out("                                       Repeat this parameter to add multiple files");
            out("");
        } else {
            args.add("--status");
            args.add(WorkflowRunStatus.completed.toString());
            runCreateTable(args, "workflow_run", "workflow_accession", "status");
        }
    }

    private static void createWorkflow(List<String> args) {
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

    private static void create(List<String> args) {
        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware create [--help]");
            out("       seqware create <object> [--help]");
            out("");
            out("Description:");
            out("  Create new seqware objects (e.g., study).");
            out("");
            out("Objects:");
            out("  experiment");
            out("  file");
            out("  ius");
            out("  lane");
            out("  sample");
            out("  sequencer-run");
            out("  study");
            out("  workflow");
            out("  workflow-run");
            out("");
        } else {
            String obj = args.remove(0);
            if (null != obj) switch (obj) {
            case "experiment":
                createExperiment(args);
                break;
            case "ius":
                createIus(args);
                break;
            case "lane":
                createLane(args);
                break;
            case "sample":
                createSample(args);
                break;
            case "sequencer-run":
                createSequencerRun(args);
                break;
            case "study":
                createStudy(args);
                break;
            case "workflow":
                createWorkflow(args);
                break;
            case "workflow-run":
                createWorkflowRun(args);
                break;
            default:
                kill("seqware: '%s' is not a valid object type.  See 'seqware create --help'.", obj);
                break;
            }
        }
    }

    private static void filesReport(List<String> args) {
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
            for (HumanProvenanceFilters filter : ProvenanceUtility.HumanProvenanceFilters.values()) {
                out("  --" + filter.human_str + " <value>            Limit files to the specified " + filter.desc
                        + ". Can occur multiple times.");
            }
            out("");
        } else {
            Map<ProvenanceUtility.HumanProvenanceFilters, List<String>> map = new HashMap<>();
            for (HumanProvenanceFilters filter : ProvenanceUtility.HumanProvenanceFilters.values()) {
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
            for (Entry<ProvenanceUtility.HumanProvenanceFilters, List<String>> e : map.entrySet()) {
                if (!e.getValue().isEmpty()) {
                    allEmpty = false;
                }
            }

            if (allEmpty) {
                runnerArgs.add("--all");
            } else {
                for (Entry<ProvenanceUtility.HumanProvenanceFilters, List<String>> e : map.entrySet()) {
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

    private static void files(List<String> args) {
        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware files --help");
            out("       seqware files <sub-command> [--help]");
            out("");
            out("Description:");
            out("  Extract information about workflow output files.");
            out("");
            out("Sub-commands:");
            out("  report          A report of the provenance of output files");
            out("  refresh         Refresh the static simplified provenance report table");
            out("");
        } else {
            String cmd = args.remove(0);
            if (null != cmd) switch (cmd) {
            case "report":
                filesReport(args);
                break;
            case "refresh":
                Log.stdoutWithTime("Triggered provenance report");
                Reports.triggerProvenanceReport();
                break;
            default:
                invalid("files", cmd);
                break;
            }
        }
    }

    private static void studyList(List<String> args) {
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
            boolean tsv = flag(args, "--tsv");

            extras(args, "study list");

            if (tsv) {
                out(Studies.studiesTsv());
            } else {
                out(TabExpansionUtil.expansion(Studies.studiesTsv()));
            }
        }
    }

    private static void study(List<String> args) {
        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware study --help");
            out("       seqware study <sub-command> [--help]");
            out("");
            out("Description:");
            out("  Extract information about studies.");
            out("");
            out("Sub-commands:");
            out("  list          List all studies");
            out("");
        } else {
            String cmd = args.remove(0);
            if ("list".equals(cmd)) {
                studyList(args);
            } else {
                invalid("study", cmd);
            }
        }
    }

    private static void workflowIni(List<String> args) {
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

    private static void workflowList(List<String> args) {
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

    private static void workflowReport(List<String> args) {
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

    private static void workflowSchedule(List<String> args) {
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
            out("");
        } else {
            String wfId = reqVal(args, "--accession");
            String host = reqVal(args, "--host");
            List<String> iniFiles = optVals(args, "--ini");
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

    private static void workflowRunReschedule(List<String> args) {
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

    private static void workflowRunWatch(List<String> args) {
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

    private static void workflowRunIni(List<String> args) {
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
                    FileUtils.writeStringToFile(new File(out), WorkflowRuns.workflowRunIni(Integer.valueOf(swid)));
                } catch (IOException ex) {
                    kill("seqware: cannot write to '%s'.", out);
                }
            } else {
                out(WorkflowRuns.workflowRunIni(Integer.valueOf(swid)));
            }
        }
    }

    private static void workflowRunReport(List<String> args) {
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

    private static void workflowRunDelete(List<String> args) {
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

    private static void workflow(List<String> args) {
        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware workflow [--help]");
            out("       seqware workflow <sub-command> [--help]");
            out("");
            out("Description:");
            out("  Interact with workflows.");
            out("");
            out("Sub-commands:");
            out("  ini           Generate an ini file for a workflow");
            out("  list          List all installed workflows");
            out("  report        List the details of all runs of a given workflow");
            out("  schedule      Schedule a workflow to be run");
            out("");
        } else {
            String cmd = args.remove(0);
            if (null != cmd) switch (cmd) {
            case "ini":
                workflowIni(args);
                break;
            case "list":
                workflowList(args);
                break;
            case "report":
                workflowReport(args);
                break;
            case "schedule":
                workflowSchedule(args);
                break;
            default:
                invalid("workflow", cmd);
                break;
            }
        }
    }

    private static void workflowRunLaunchScheduled(List<String> args) {
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

    private static void workflowRunPropagateStatuses(List<String> args) {
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

    private static void workflowRunStderr(List<String> args) {
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

    private static void workflowRunStdout(List<String> args) {
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

    private static void files2workflowruns(List<String> args) {
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
            for (String val : fileVals) {
                fileSWIDs.add(swid(val));
            }

            List<String> workflowVals = optVals(args, "--workflow");
            List<Integer> workflowSWIDs = Lists.newArrayList();
            for (String val : workflowVals) {
                workflowSWIDs.add(swid(val));
            }

            extras(args, "dev files2workflowruns");

            Metadata md = MetadataFactory.get(ConfigTools.getSettings());
            List<WorkflowRun> relevantWorkflows = md.getWorkflowRunsAssociatedWithInputFiles(fileSWIDs, workflowSWIDs);
            for (WorkflowRun run : relevantWorkflows) {
                out(Integer.toString(run.getSwAccession()));
            }
        }
    }

    private static void map(List<String> args) {
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

    private static void workflowRunCancel(List<String> args) {
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

    private static void workflowRunRetry(List<String> args) {
        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware workflow-run retry --help");
            out("       seqware workflow-run retry <params>");
            out("");
            out("Description:");
            out("  Retry a failed or cancelled workflow run.");
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

            extras(args, "workflow-run retry");

            WorkflowRuns.submitRetry(swidArr);
            out("Submitted request to retry workflow run with SWID " + Arrays.toString(swidArr));
        }
    }

    private static void dev(List<String> args) {
        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware dev --help");
            out("       seqware dev <sub-command> [--help]");
            out("");
            out("Description:");
            out("  Advanced commands for debugging and developers.");
            out("");
            out("Sub-commands:");
            out("  map                      Convert between various identifiers");
            out("  files2workflowruns       Identify workflow runs that used files as input");
            out("");
        } else {
            String cmd = args.remove(0);
            if (null != cmd) switch (cmd) {
            case "map":
                map(args);
                break;
            case "files2workflowruns":
                files2workflowruns(args);
                break;
            default:
                invalid("dev", cmd);
                break;
            }
        }
    }

    private static void workflowRun(List<String> args) {
        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware workflow-run --help");
            out("       seqware workflow-run <sub-command> [--help]");
            out("");
            out("Description:");
            out("  Interact with workflow runs.");
            out("");
            out("Sub-commands:");
            out("  cancel              Cancel a submitted or running workflow run");
            out("  launch-scheduled    Launch scheduled workflow runs");
            out("  propagate-statuses  Propagate workflow engine statuses to seqware meta DB");
            out("  retry               Retry a failed or cancelled workflow run skipping completed steps");
            out("  reschedule          Reschedule a workflow-run to re-run from scratch as a new run");
            out("  stderr              Obtain the stderr output of the run");
            out("  stdout              Obtain the stdout output of the run");
            out("  report              The details of a given workflow-run");
            out("  watch               Watch a workflow-run in progress");
            out("  ini                 Output the effective ini for a workflow run");
            out("  delete              Recursively delete workflow-runs");
            out("");
        } else {
            String cmd = args.remove(0);
            if (null != cmd) switch (cmd) {
            case "cancel":
                workflowRunCancel(args);
                break;
            case "launch-scheduled":
                workflowRunLaunchScheduled(args);
                break;
            case "propagate-statuses":
                workflowRunPropagateStatuses(args);
                break;
            case "retry":
                workflowRunRetry(args);
                break;
            case "reschedule":
                workflowRunReschedule(args);
                break;
            case "stderr":
                workflowRunStderr(args);
                break;
            case "stdout":
                workflowRunStdout(args);
                break;
            case "report":
                workflowRunReport(args);
                break;
            case "watch":
                workflowRunWatch(args);
                break;
            case "ini":
                workflowRunIni(args);
                break;
            case "delete":
                workflowRunDelete(args);
                break;
            default:
                invalid("workflow-run", cmd);
                break;
            }
        }
    }

    private static void checkdb(List<String> args) {
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

    public static void main(String[] argv) {
        List<String> args = new ArrayList<>(Arrays.asList(argv));
        if (flag(args, "--debug")) {
            DEBUG.set(true);
        }
        if (flag(args, "--verbose")) {
            VERBOSE.set(true);
        }

        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware [<flag>]");
            out("       seqware <command> [--help]");
            out("");
            out("Commands:");
            out("  annotate      Add arbitrary key/value pairs to seqware objects");
            out("  bundle        Interact with a workflow bundle during development/admin");
            out("  copy          Copy files between local and remote file systems");
            out("  create        Create new seqware objects (e.g., study)");
            out("  files         Extract information about workflow output files");
            out("  study         Extract information about studies");
            out("  workflow      Interact with workflows");
            out("  workflow-run  Interact with workflow runs");
            out("  checkdb       Check the seqware database for convention errors");
            out("  check         Check the seqware environment for configuration issues");
            out("  dev           Advanced commands that are useful for developers or debugging");
            out("");
            out("Flags:");
            out("  --help        Print help out");
            // handled in seqware script:
            out("  --version     Print Seqware's version");
            out("");
        } else {
            try {
                String cmd = args.remove(0);
                if (null != cmd) switch (cmd) {
                case "-v":
                case "--version":
                    kill("seqware: version information is provided by the wrapper script.");
                    break;
                case "annotate":
                    annotate(args);
                    break;
                case "bundle":
                    bundle(args);
                    break;
                case "copy":
                    copy(args);
                    break;
                case "create":
                    create(args);
                    break;
                case "files":
                    files(args);
                    break;
                case "study":
                    study(args);
                    break;
                case "workflow":
                    workflow(args);
                    break;
                case "workflow-run":
                    workflowRun(args);
                    break;
                case "dev":
                    dev(args);
                    break;
                case "checkdb":
                    checkdb(args);
                    break;
                default:
                    invalid(cmd);
                    break;
                }
            } catch (Kill k) {
                System.exit(1);
            }
        }
    }
}
