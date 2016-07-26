package io.seqware.cli;

import io.seqware.Engines;
import io.seqware.pipeline.SqwKeys;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.pipeline.bundle.Bundle;
import net.sourceforge.seqware.pipeline.bundle.BundleInfo;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.util.ArrayList;
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
 * Collects the various SeqWare bundle commands
 */
class BundleCommands {

    static class BundleInstallCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "install";
        }

        @Override public void invoke(List<String> args) {
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

        @Override public String displayOneLineDescription() {
            return "Inform the Seqware system of the availability of a standard bundle";
        }
    }

    static class BundleInstallDirCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "install-dir-only";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
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

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Inform the Seqware system of the availability of an unzipped bundle";
        }
    }

    static class BundleLaunchCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "launch";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
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

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Launch a specified workflow in a bundle directory";
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

    private static WorkflowInfo findWorkflowInfo(File dir, String name, String version) {
        BundleInfo bi = Bundle.findBundleInfo(dir);

        List<WorkflowInfo> found = new ArrayList<>();

        for (WorkflowInfo wi : bi.getWorkflowInfo()) {
            boolean n = name == null || wi.getName().equals(name);
            boolean v = version == null || wi.getVersion().equals(version);
            if (n && v)
                found.add(wi);
        }

        if (found.isEmpty()) {
            if (name == null && version == null) {
                kill("seqware: no workflow defined in " + bi.parsedFrom().getAbsolutePath());
            } else if (version == null) {
                kill("seqware: no workflow with name '" + name + "' defined in " + bi.parsedFrom().getAbsolutePath());
            } else if (name == null) {
                kill("seqware: no workflow with version '" + version + "' defined in " + bi.parsedFrom().getAbsolutePath());
            } else {
                kill("seqware: no workflow with name '" + name + "' and version '" + version + "' defined in " + bi.parsedFrom()
                        .getAbsolutePath());
            }
        } else if (found.size() > 1) {
            if (name == null && version == null) {
                kill("seqware: multiple workflows defined in " + bi.parsedFrom().getAbsolutePath());
            } else if (version == null) {
                kill("seqware: multiple workflows with name '" + name + "' defined in " + bi.parsedFrom().getAbsolutePath());
            } else if (name == null) {
                kill("seqware: multiple workflows with version '" + version + "' defined in " + bi.parsedFrom().getAbsolutePath());
            } else {
                kill("seqware: multiple workflows with name '" + name + "' and version '" + version + "' defined in " + bi.parsedFrom()
                        .getAbsolutePath());
            }
        }

        return found.get(0);
    }

    static class BundleDryRunCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "dry-run";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
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
                    run("--plugin", "io.seqware.pipeline.plugins.WorkflowLifecycle", "--", "--bundle", dir, "--workflow", name, "--version",
                            version, "--ini-files", cdl(inis), "--no-run");
                } else {
                    run("--plugin", "io.seqware.pipeline.plugins.WorkflowLifecycle", "--", "--bundle", dir, "--workflow", name, "--version",
                            version, "--ini-files", cdl(inis), "--workflow-engine", engine, "--no-run");
                }
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Perform all steps to prepare for a launch, but not actually launch";
        }
    }

    static class BundleListCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "list";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
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

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "List workflows within a bundle directory";
        }
    }

    static class BundlePackageCommand implements CommandLeaf {

        @Override public String getCommand() {
            return "package";
        }

        /**
         * Call this command (with the preceding tree removed)
         *
         * @param args arguments
         */
        @Override public void invoke(List<String> args) {
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

                run("--plugin", "net.sourceforge.seqware.pipeline.plugins.BundleManager", "--", "--path-to-package", dir, "--bundle",
                        outdir);
            }
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Package a bundle directory into a zip file";
        }
    }
}
