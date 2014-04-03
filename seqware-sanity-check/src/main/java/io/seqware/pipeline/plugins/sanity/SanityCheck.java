package io.seqware.pipeline.plugins.sanity;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * A database validation tool for your SeqWare metadb
 *
 * @author dyuen ProviderFor(PluginInterface.class)
 * @author Raunaq Suri
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public final class SanityCheck extends Plugin {

    public static final int NUMBER_TO_OUTPUT = 100;
    private boolean masterMode = false;
    private boolean hasDBSettings = true;

    /**
     * <p>
     * Constructor for HelloWorld.</p>
     */
    public SanityCheck() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
        parser.acceptsAll(Arrays.asList("master", "m"), "To test on a master node");
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#init()
     */
    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public final ReturnValue init() {
        File settingsFile = new File(ConfigTools.getSettingsFilePath());
        if (!settingsFile.canRead()) {
            System.err.println("Unable to get read access to settings file");
            ReturnValue ret = new ReturnValue();
            ret.setExitStatus(ReturnValue.SETTINGSFILENOTFOUND);
        } else {
            try {

                HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
                // do a defensive check to see if we have a direct database connection available
                if (settings.get("SW_DB_SERVER") == null
                        || settings.get("SW_DB") == null
                        || settings.get("SW_DB_USER") == null
                        || settings.get("SW_DB_PASS") == null) {
                    hasDBSettings = false;
                    ReturnValue ret = new ReturnValue();
                    System.out.println("This utility requires direct access to the metadb. Configure  SW_DB_SERVER, SW_DB, SW_DB_USER, and SW_DB_PASS in your .seqware/setttings");
                    ret.setExitStatus(ReturnValue.SETTINGSFILENOTFOUND);
                    return (ret);
                }
            } catch (Exception e) {
                ReturnValue ret = new ReturnValue();
                ret.setExitStatus(ReturnValue.SETTINGSFILENOTFOUND);
                return (ret);
            }
        }
        return new ReturnValue();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_test()
     */
    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public ReturnValue do_test() {
        // TODO Auto-generated method stub
        return new ReturnValue();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_run()
     */
    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public final ReturnValue do_run() {
        ReturnValue ret = new ReturnValue();
        if (options.has("help") || options.has("h") || options.has("?")) {
            System.out.println("This plugin is to check to see if your seqware environment is running");
            System.out.println("Parameters:");
            System.out.println("--help, h, ?\t Provides this help message");
            System.out.println("--master, m \t Include this parameter to test if you are on a master node and not a user one");
            ret.setExitStatus(ReturnValue.SUCCESS);
        } else {
            if (options.has("master") || options.has("m")) {
                masterMode = true;
            }

            Collection<SanityCheckPluginInterface> plugins = (Collection<SanityCheckPluginInterface>) Lookup.getDefault().lookupAll(SanityCheckPluginInterface.class);
            MetadataDB metadataDB = null;
            try {
                metadataDB = DBAccess.get();
            } catch (RuntimeException e) {
                if (e.getMessage().equals(MetadataFactory.NO_DATABASE_CONFIG)) {
                    System.err.println("Warning: No or invalid SeqWare metadb settings");
                } else {
                    throw e;
                }
            }

            List<SanityCheckPluginInterface> pluginList = new ArrayList<>();
            pluginList.addAll(plugins);
            Comparator<SanityCheckPluginInterface> comp = new Comparator<SanityCheckPluginInterface>() {
                @Override
                public int compare(SanityCheckPluginInterface o1, SanityCheckPluginInterface o2) {
                    Integer n1 = new Integer(o1.getPriority());
                    Integer n2 = new Integer(o2.getPriority());
                    return n1.compareTo(n2);
                }
            };
            Collections.sort(pluginList, comp);

            //removes the tests that don't need to be ran
            removeChecks(pluginList);

            List<Boolean> passedTests = new ArrayList<>();

            for (SanityCheckPluginInterface plugin : pluginList) {
                System.err.println("Running " + plugin.getClass().getSimpleName());
                try {

                    boolean check = plugin.check(metadataDB == null ? null : new QueryRunner(metadataDB), metadata);
                    if (!check) {
                        passedTests.add(false);
                        System.err.println("Failed check: " + plugin.getClass().getSimpleName());
                        System.err.println(plugin.getDescription());
                        ret.setExitStatus(ReturnValue.FAILURE);
                        return ret;
                    } else {
                        passedTests.add(true);
                        System.err.println("Passed check: " + plugin.getClass().getSimpleName());
                    }
                } catch (Exception e) {
                    Log.fatal("Plugin " + plugin.getClass().getSimpleName() + " died", e);
                    System.err.println("Crashed and failed check: " + plugin.getClass().getSimpleName());
                    System.err.println(plugin.getDescription());
                    ret.setExitStatus(ReturnValue.FAILURE);
                    return ret;
                }
            }
            //Iterates through the array and sees if all the tests passed
            for (Boolean b : passedTests) {
                if (b.booleanValue() == false) {
                    System.err.println("One of the tests has failed. Exiting with an exit status of 1");
                    System.exit(1);
                }
            }

        }
        return new ReturnValue();
    }

    /**
     * Remove all checks that are not necessary
     *
     * @param pluginList the list of plugins
     */
    private void removeChecks(List<SanityCheckPluginInterface> pluginList) {
        for (int i = pluginList.size() - 1; i > -1; i--) {

            SanityCheckPluginInterface plugin = pluginList.get(i);
            if (!hasDBSettings && plugin.isDBTest()) {
                pluginList.remove(i);

            } else if (plugin.isTutorialTest()) {
                pluginList.remove(i);
            } else if (!masterMode && plugin.isMasterTest()) {
                pluginList.remove(i);
            }

        }
    }

    /**
     * <p>
     * get_description.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public final String get_description() {
        return ("A sanity check tool for your SeqWare install");
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        SanityCheck mp = new SanityCheck();
        mp.init();
        mp.setMetadata(MetadataFactory.getWS(ConfigTools.getSettings()));
        mp.setParams(Arrays.asList(args));
        mp.parse_parameters();
        ReturnValue do_run = mp.do_run();
    }

    @Override
    public final ReturnValue clean_up() {
        return new ReturnValue();
    }

}
