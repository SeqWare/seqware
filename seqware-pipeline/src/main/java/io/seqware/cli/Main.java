package io.seqware.cli;

import com.google.common.base.Joiner;
import com.google.common.collect.ObjectArrays;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.runner.PluginRunner;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

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
    static String cdl(List<String> tokens) {
        return dl(tokens, ",");
    }

    static int swid(String swid) {
        try {
            return Integer.parseInt(swid);
        } catch (NumberFormatException e) {
            kill("seqware: invalid seqware accession: '" + swid + "'");
            return 0; // non-reachable
        }
    }

    static void out(String format, Object... args) {
        System.out.println(String.format(format, args));
    }

    static void err(String format, Object... args) {
        System.err.println(String.format(format, args));
    }

    static List<String> processOverrideParams(List<String> override) {
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

    static void kill(String format, Object... args) {
        err(format, args);
        throw new Kill();
    }

    static void invalid(String cmd) {
        kill("seqware: '%s' is not a seqware command. See 'seqware --help'.", cmd);
    }

    static void invalid(String cmd, String sub) {
        kill("seqware: '%s %s' is not a seqware command. See 'seqware %s --help'.", cmd, sub, cmd);
    }

    static void extras(List<String> args, String curCommand) {
        if (args.size() > 0) {
            kill("seqware: unexpected arguments to '%s': %s", curCommand, dl(args, " "));
        }
    }

    static boolean flag(List<String> args, String flag) {
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

    static List<String> optVals(List<String> args, String key) {
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

    static List<String> reqVals(List<String> args, String key) {
        List<String> vals = optVals(args, key);

        if (vals.isEmpty()) {
            kill("seqware: missing required flag '%s'.", key);
        }

        return vals;
    }

    static String optVal(List<String> args, String key, String defaultVal) {
        String val = defaultVal;

        List<String> vals = optVals(args, key);
        if (vals.size() == 1) {
            val = vals.get(0);
        } else if (vals.size() > 1) {
            kill("seqware: multiple instances of '%s'.", key);
        }

        return val;
    }

    static String reqVal(List<String> args, String key) {
        String val = optVal(args, key, null);

        if (val == null) {
            kill("seqware: missing required flag '%s'.", key);
        }

        return val;
    }

    static boolean isHelp(List<String> args, boolean valOnEmpty) {
        if (args.isEmpty()) return valOnEmpty;

        String first = args.get(0);
        return first.equals("-h") || first.equals("--help");
    }

    private static final AtomicBoolean DEBUG = new AtomicBoolean(false);
    private static final AtomicBoolean VERBOSE = new AtomicBoolean(false);

    static void run(String... args) {
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

    static void run(List<String> runnerArgs) {
        run(runnerArgs.toArray(new String[runnerArgs.size()]));
    }

    /**
     * Prints to the console without applying any formatting. Useful for situations where output contains unintended formatting strings,
     * which would break the {@link #out(String format, Object... args)} function. For example, if you try to print an INI file containing
     * the line "refExclude=XX,GL%,hs37d5,XX_001234" the <i>substring</i> "%,h" will cause String.format to throw an exception and fail. So
     * it is sometimes necessary to print output with no consideration to formatting.
     *
     * @param output String to output
     */
    static void outWithoutFormatting(String output) {
        System.out.println(output);
    }


    // this.commands:

    private static class BundleLayer extends CommandLayer {

        BundleLayer() {
            super(new BundleCommands.BundleDryRunCommand(), new BundleCommands.BundleInstallCommand(),
                    new BundleCommands.BundleInstallDirCommand(), new BundleCommands.BundleLaunchCommand(),
                    new BundleCommands.BundleListCommand(), new BundleCommands.BundlePackageCommand());
        }

        @Override public String getCommand() {
            return "bundle";
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override
        public String displayOneLineDescription() {
            return "Interact with a workflow bundle during development/admin";
        }
    }

    private static class CreateLayer extends CommandLayer {

        CreateLayer(){
            super(new CreateCommands.CreateExperimentCommand(), new CreateCommands.CreateIusCommand(), new CreateCommands.CreateLaneCommand()
                    , new CreateCommands.CreateSampleCommand(), new CreateCommands.CreateSequencerRunCommand()
                    , new CreateCommands.CreateStudyCommand(), new CreateCommands.CreateWorkflowCommand()
                    , new CreateCommands.CreateWorkflowRunCommand());
        }

        /**
         * Key for preceding layer
         *
         * @return
         */
        @Override public String getCommand() {
            return "create";
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override
        public String displayOneLineDescription() {
            return "Create new seqware objects (e.g., study)";
        }
    }

    private static class FileLayer extends CommandLayer{

        FileLayer(){
            super(new FileCommands.FileReportCommand(), new FileCommands.FileRefreshCommand());
        }

        /**
         * Key for preceding layer
         *
         * @return
         */
        @Override public String getCommand() {
            return "files";
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override
        public String displayOneLineDescription() {
            return "Extract information about workflow output files";
        }
    }

    private static class StudyLayer extends CommandLayer {

        StudyLayer(){
            super(new StudyCommands.StudyListCommand());
        }

        /**
         * Key for preceding layer
         *
         * @return
         */
        @Override public String getCommand() {
            return "study";
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override
        public String displayOneLineDescription() {
            return "Extract information about studies";
        }
    }

    private static class WorkflowLayer extends CommandLayer {

        WorkflowLayer() {
            super(new WorkflowCommands.WorkflowIniCommand(), new WorkflowCommands.WorkflowList(),
                    new WorkflowCommands.WorkflowReportCommand(), new WorkflowCommands.WorkflowScheduleCommand());
        }

        /**
         * Key for preceding layer
         *
         * @return
         */
        @Override public String getCommand() {
            return "workflow";
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override
        public String displayOneLineDescription() {
            return "Interact with workflows";
        }
    }

    private static class DevLayer extends CommandLayer {

        DevLayer() {
            this.commands.put("map", new DevCommands.MapCommand());
            this.commands.put("files2workflowruns", new DevCommands.Files2WorkflowRunsCommand());
        }

        /**
         * Key for preceding layer
         *
         * @return
         */
        @Override public String getCommand() {
            return "dev";
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Advanced commands that are useful for developers or debugging";
        }
    }


    private static class WorkflowRunLayer extends CommandLayer {

        WorkflowRunLayer() {
            super(new WorkflowRunCommands.WorkflowRunCancelCommand(), new WorkflowRunCommands.WorkflowRunLaunchScheduled(),
                    new WorkflowRunCommands.WorkflowRunPropagateStatusesCommand(), new WorkflowRunCommands.WorkflowRunRetryCommand(),
                    new WorkflowRunCommands.WorkflowRunReschedule(), new WorkflowRunCommands.WorkflowRunStderrCommand(),
                    new WorkflowRunCommands.WorkflowRunStdoutCommand(), new WorkflowRunCommands.WorkflowRunReportCommand(),
                    new WorkflowRunCommands.WorkflowRunWatchCommand(), new WorkflowRunCommands.WorkflowRunIniCommand(),
                    new WorkflowRunCommands.WorkflowRunDeleteCommand());
        }

        /**
         * Key for preceding layer
         *
         * @return
         */
        @Override public String getCommand() {
            return "workflow-run";
        }

        /**
         * Display a description of the command
         *
         * @return a one line description of the command
         */
        @Override public String displayOneLineDescription() {
            return "Interact with workflow runs";
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

        LinkedHashMap<String, CommandLeaf> commands = new LinkedHashMap<>();
        commands.put("annotate", new OtherCommands.AnnotateCommand());
        commands.put("query", new OtherCommands.QueryCommand());
        commands.put("bundle", new BundleLayer());
        commands.put("copy", new OtherCommands.CopyCommand());
        commands.put("create", new CreateLayer());
        commands.put("files", new FileLayer());
        commands.put("study", new StudyLayer());
        commands.put("workflow", new WorkflowLayer());
        commands.put("workflow-run", new WorkflowRunLayer());
        commands.put("checkdb", new OtherCommands.CheckDBCommand());
        commands.put("dev", new DevLayer());


        if (isHelp(args, true)) {
            out("");
            out("Usage: seqware [<flag>]");
            out("       seqware <command> [--help]");
            out("");
            out("Commands:");
            for(Entry<String, CommandLeaf> entry : commands.entrySet()){
                out("    %-15s %-60s", entry.getKey(), entry.getValue().displayOneLineDescription());
            }
            out("");
            out("Flags:");
            out("  --help        Print help out");
            // handled in seqware script:
            out("  --version     Print Seqware's version");
            out("  --metadata    Print metadata environment");
            out("  --completion  Output Bash completion script");
            out("");
        } else {
            try {
                String cmd = args.remove(0);
                if (null != cmd && commands.containsKey(cmd)){
                    commands.get(cmd).invoke(args);
                    return;
                }
                if (null != cmd) switch (cmd) {
                case "-v":
                case "--version":
                    kill("seqware: version information is provided by the wrapper script.");
                    break;
                case "--metadata":
                    Metadata md = MetadataFactory.get(ConfigTools.getSettings());
                    Map<String, String> result = md.getEnvironmentReport();
                    Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
                    System.out.println(gson.toJson(result));
                    break;
                case "--completion":
                    outputCompletionScript(commands);
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

    private static void outputCompletionScript(LinkedHashMap<String, CommandLeaf> commands) {

        String baseLevelCommands = "--help --verbose --version --debug --metadata --completion";
        baseLevelCommands = baseLevelCommands + " " + Joiner.on(' ').join(commands.keySet());

        StringBuilder builder = new StringBuilder();

        builder.append("_seqware()\n");
        builder.append("{\n");
        builder.append("   local cur prev opts \n");
        builder.append("   COMPREPLY=()\n");
        builder.append("   cur=\"${COMP_WORDS[COMP_CWORD]}\"\n");
        builder.append("   prev=\"${COMP_WORDS[COMP_CWORD-1]}\"\n");
        builder.append("   opts=\""+ baseLevelCommands+"\"\n");
        builder.append("   #\n");
        builder.append("   # Nested commands\n");
        builder.append("   #\n");
        builder.append("   case \"${prev}\" in\n");
        for (Entry<String, CommandLeaf> entry : commands.entrySet()) {
            if (!entry.getValue().bashOpts().isEmpty()) {
                builder.append("   " + entry.getKey() + ")\n");
                builder.append("      local nestedOpts=\"" + entry.getValue().bashOpts() + "\"\n");
                builder.append("      COMPREPLY=( $(compgen -W \"${nestedOpts}\" -- ${cur}) )\n");
                builder.append("      return 0\n");
                builder.append("      ;;\n");
            }
        }
        builder.append("   *)\n");
        builder.append("   ;;\n");
        builder.append("   esac\n");
        builder.append("   \n");
        builder.append("   if [ \"${prev}\" == \"seqware\" ] \n");
        builder.append("      then\n");
        builder.append("         COMPREPLY=( $(compgen -W \"${opts}\" -- ${cur}) )\n");
        builder.append("   fi\n");
        builder.append("   return 0\n");
        builder.append("}\n");
        builder.append("complete -F _seqware seqware\n");

        try {
            final File tempFile = File.createTempFile("tmp", "txt");
            FileUtils.write(tempFile, builder.toString(), StandardCharsets.UTF_8);
            CommandLine commandline = CommandLine.parse("sudo cp "+tempFile.getAbsolutePath()+" /etc/bash_completion.d/seqware");
            DefaultExecutor exec = new DefaultExecutor();
            exec.execute(commandline);
        } catch (IOException e) {
            Log.error("Execution failed with:", e);
            System.exit(1);
        }
        out("Close and re-open your terminal to load the Bash completion script");
    }
}
