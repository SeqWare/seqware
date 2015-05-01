package net.sourceforge.seqware.pipeline.runner;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.common.util.exceptiontools.ExceptionTools;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.openide.util.Lookup;

/**
 * <p>
 * PluginRunner class.
 * </p>
 *
 * @author briandoconnor@gmail.com
 * @since 20110925
 *
 *        The PluginRunner is a command line utility that will provide a mechanism to extend the core functionality of SeqWare Pipeline in a
 *        more organized way then the current collection of Perl and other utilities used haphazardly in this subproject. The idea is that
 *        core SeqWare Pipeline functionality should be implemented in Java and packaged as a single jar file making the code more
 *        self-contained and easier to package up and install. This class is the browser and caller for plugins that are designed to extend
 *        the functionality of SeqWare pipeline. These include everything from utilities that uncompress and install workflow bundles, to
 *        the runner that calls modules, and to utilities designed to query the metadb and perform some utility such as deleting processing
 *        events and cleaning up files. Unlike the modules ({@link net.sourceforge.seqware.pipeline.module.ModuleInterface}), plugins are
 *        intended to be written by core SeqWare Pipeline developers and to exist within the source repository of the project not loaded
 *        from external jar files. Also unlike the module runner ({@link net.sourceforge.seqware.pipeline.runner.Runner}) this plugin runner
 *        does not save any state to the MetaDB, it's up to the plugin to do that but a metadata object (of the type specified in the users
 *        SEQWARE_SETTINGS file) is handed off to the plugin for its use.
 * @version $Id: $Id
 */
public class PluginRunner {

    private Map<String, String> config;
    private OptionParser parser = new OptionParser();
    private OptionSet options = null;
    private Plugin plugin = null;
    private Metadata meta = null;
    private HashMap<String, ArrayList<String>> map = new HashMap<>();

    /**
     * <p>
     * main.
     * </p>
     *
     * @param args
     *            an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        try {
            new PluginRunner().run(args);
        } catch (ExitException e) {
            System.exit(e.getExitCode());
        }
    }

    private NonOptionArgumentSpec<String> nonOptionSpec;

    /**
     * <p>
     * run.
     * </p>
     *
     * @param args
     *            an array of {@link java.lang.String} objects.
     */
    public void run(String[] args) {
        // Specific to the Plugin Runner
        // Setup the command line options supported by the PluginRunner
        setupOptions();

        // Parse the options
        try {
            options = parser.parse(args);
        } catch (OptionException e) {
            getSyntax(parser, e.getMessage());
        }

        // Do syntax check on runner args
        checkArguments();

        // read the metadata config
        setupConfig();

        // open a metadata object
        setupMetadata();

        // list plugins if that option was given (and then exit)
        listPlugins();

        // These are specific to the plugin if specified
        // setup the plugin by passing config
        setupPlugin();

        // Call each method
        invokePluginMethods();
    }

    /**
     * Setup the options parser.
     */
    private void setupOptions() {
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
        parser.acceptsAll(Arrays.asList("list", "l"), "Lists all the plugins available in this SeqWare Pipeline jar file.");
        parser.acceptsAll(Arrays.asList("plugin", "p"), "The plugin you wish to trigger.").withRequiredArg();
        this.nonOptionSpec = parser
                .nonOptions("Specify arguments for the plugin by providding an additional -- and then --<key> <value> pairs");
        parser.accepts("verbose", "Show debug information");
    }

    /**
     *
     * @param parser
     *            The options parser object
     * @param errorMessage
     *            Error message
     */
    private void getSyntax(OptionParser parser, String errorMessage) {
        if (errorMessage != null && errorMessage.length() > 0) {
            Log.stdout("ERROR: " + errorMessage);
            Log.stdout("");
        }
        PluginRunner it = new PluginRunner();
        String seqwareVersion = it.getClass().getPackage().getImplementationVersion();
        Log.stdout("Syntax: java seqware-distribution-" + seqwareVersion
                + "-full.jar [[--help]] [--list] [--verbose] [--plugin] PluginName -- [PluginParameters]");
        Log.stdout("");
        Log.stdout("--> PluginParameters are passed directly to the Plugin and ignored by the PluginRunner. ");
        Log.stdout("--> You must pass '--' right after the PluginName in order to prevent the parameters from being parsed by the PluginRunner!");
        Log.stdout("");
        Log.stdout("PluginRunner parameters are limited to the following:");
        try {
            parser.printHelpOn(System.err);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        throw new ExitException(ReturnValue.INVALIDARGUMENT);
    }

    /**
     * Do all the syntax here
     */
    private void checkArguments() {

        // Check if help was requested
        if ((options.has("help") || options.has("h") || options.has("?")) && !options.has("plugin") && !options.has("p")) {
            getSyntax(parser, "");
            throw new ExitException(ReturnValue.RETURNEDHELPMSG);
        }

        // FIXME: check single char options too
        if (options.has("list") && options.has("plugin")) {
            Log.error("You can't have both --list and --plugin defined at the same time!");
            throw new ExitException(ReturnValue.INVALIDARGUMENT);
        }

        // FIXME: check single char options too
        if (!(options.has("list") || options.has("plugin") || options.has("help"))) {
            getSyntax(parser, "You need to specify at least one of --list, --plugin, or --help!");
            throw new ExitException(ReturnValue.INVALIDARGUMENT);
        }

        // check if verbose was requested, then override the log4j.properties
        if (options.has("verbose")) {
            Log.setVerbose(true);
        }
    }

    /**
     * LEFT OFF HERE: take a look at http://download.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html and
     * http://wiki.netbeans.org/AboutLookup http://download.netbeans.org/netbeans/7.0.1/final/bundles/netbeans-7.0.1-ml-javase-linux.sh
     * http://weblogs.java.net/blog/timboudreau/archive/2008/08/simple_dependen.html This method uses reflection to list all the available
     * plugins in this SeqWare Pipeline jar. There's a good tutorial here:
     * http://hulles.supersized.org/archives/23-Tips-on-Using-Lookup-in-NetBeans.html There's an alternative project that may avoid the
     * compile time issues with Lookup: http://code.google.com/p/spi/
     */
    private void listPlugins() {

        Collection<? extends PluginInterface> plugs;
        plugs = Lookup.getDefault().lookupAll(PluginInterface.class);

        // PluginInterface p = (PluginInterface)Lookup.getDefault().lookup(PluginInterface.class);

        if (options.has("list") || options.has("l")) {
            Log.info("Plugin List:\n");
        }

        for (PluginInterface plug : plugs) {
            if (options.has("list") || options.has("l")) {
                Log.stdout("  Plugin: " + plug.getClass().getPackage().getName() + "." + plug.getClass().getSimpleName());
                Log.stdout("          " + plug.get_description() + "\n");
            }
            ArrayList<String> classList = map.get(plug.getClass().getPackage().getName());
            if (classList == null) {
                classList = new ArrayList<>();
            }
            classList.add(plug.getClass().getSimpleName());
            map.put(plug.getClass().getPackage().getName(), classList);
        }

    }

    /**
     * This method loads the plugin class and then passes the command line parameters to it.
     */
    private void setupPlugin() {

        String pluginName = null;
        if (options.has("plugin")) {
            pluginName = (String) options.valueOf("plugin");
            Log.debug("Running Plugin: " + pluginName);

            try {
                plugin = (Plugin) Class.forName(pluginName).newInstance();

            } catch (ClassNotFoundException e) {
                Log.error("Could not find the Plugin class for '" + pluginName + "'");
                throw new ExitException(ReturnValue.INVALIDPLUGIN);
            } catch (Throwable e) {
                e.printStackTrace();
                Log.error(e);
                throw new ExitException(ReturnValue.FAILURE);
            }

            if (options.has("help") || options.has("h")) {

                Log.stdout(plugin.get_syntax());

            } else {

                // try to parse the parameters after "--"
                plugin.setParams(options.valuesOf(nonOptionSpec));

                // pass in the config information from the settings file
                plugin.setConfig(config);

                // set the metadata object in case the plugin needs access to the DB
                plugin.setMetadata(meta);

                Log.debug("Setting Up Plugin: " + plugin);
            }

        } else if (options.has("list")) {
            PluginRunner it = new PluginRunner();
            String seqwareVersion = it.getClass().getPackage().getImplementationVersion();
            Log.stdout("For more information use \"java -jar seqware-distribution-" + seqwareVersion
                    + "-full.jar --plugin <plugin_name> --help\" to see options for each.\n");
        } else {
            getSyntax(parser, "You must specifiy a plugin with option --plugin");
            throw new ExitException(ReturnValue.INVALIDARGUMENT);
        }

    }

    /**
     * This method invokes each of the plugin methods in turn and evaluates if there are errors.
     *
     * @return ReturnValue which includes information about status and any errors
     */
    private void invokePluginMethods() {

        if ((options.has("plugin") || options.has("p")) && plugin != null) {

            Log.info("Invoking Plugin Methods:");

            // evaluate the plugin method parse_parameters
            evaluateReturn(plugin, "parse_parameters");

            // evaluate the plugin method init
            evaluateReturn(plugin, "init");

            // evaluate the plugin method do_test
            evaluateReturn(plugin, "do_test");

            // evaluate the plugin method do_run
            evaluateReturn(plugin, "do_run");

            // evaluate the plugin method clean_up
            evaluateReturn(plugin, "clean_up");

        }
    }

    /**
     * This method calls the specified plugin method and checks for errors.
     *
     * @param app
     * @param methodName
     */
    private void evaluateReturn(Plugin app, String methodName) {

        Method method;
        ReturnValue newReturn = null;

        try {
            Log.debug("  Invoking Method: " + methodName);
            method = app.getClass().getMethod(methodName);
            newReturn = (ReturnValue) method.invoke(app);

        } catch (Exception e) {
            Log.stderr("Module caught exception during method: " + methodName + ":" + e.getMessage());
            Log.stderr(ExceptionTools.stackTraceToString(e));
            // Exit on error
            throw new ExitException(ReturnValue.RUNNERERR);
        }

        // On failure, update metadb and exit
        if (newReturn.getExitStatus() > ReturnValue.SUCCESS) {
            if (newReturn.getStderr() != null) {
                Log.stderr(newReturn.getStderr());
            } else {
                Log.stderr("The method '" + methodName + "' exited abnormally so the Runner will terminate here!");
                Log.stderr("Return value was: " + newReturn.getExitStatus());
            }
            throw new ExitException(newReturn.getExitStatus());
        }

        // Otherwise we will continue, after updating metadata
        else {
            // If it returned unimplemented, let's warn
            if (newReturn.getExitStatus() < ReturnValue.SUCCESS) {
                if (!options.has("suppress-unimplemented-warnings")) {
                    Log.debug("The plugin method '" + methodName + "' returned exit value of " + newReturn.getExitStatus() + ".");
                    Log.debug("This means an unimplemented method was called (such as an unneeded optional cleanup or init step)");
                }
                newReturn.setExitStatus(ReturnValue.NULL);
            }
        }

    }

    private void setupConfig() {
        try {
            this.config = ConfigTools.getSettings();
        } catch (Exception e) {
            Log.stderr("Error reading settings file: " + e.getMessage());
            Log.fatal("Error reading settings file", e);
            throw new ExitException(ReturnValue.SETTINGSFILENOTFOUND);
        }
    }

    private void setupMetadata() {
        this.meta = MetadataFactory.get(config);
    }

    /**
     * The exit code exception is used to communicate exit code values to methods that may wish to call the PluginRunner without running
     * into System.exit calls
     */
    public static class ExitException extends RuntimeException {
        private final int exitCode;

        public ExitException(int exitCode) {
            this.exitCode = exitCode;
        }

        /**
         * @return the exitCode
         */
        public int getExitCode() {
            return exitCode;
        }
    }
}
