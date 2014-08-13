package io.seqware.pipeline.plugins;

import io.seqware.pipeline.SqwKeys;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import joptsimple.OptionException;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import org.openide.util.lookup.ServiceProvider;

/**
 * <p>
 * This plugin outputs a commented config file
 * </p>
 * 
 * @author dyuen
 * @version 1.1.0
 */
@ServiceProvider(service = PluginInterface.class)
public class UserSettingsPlugin extends Plugin {

    public UserSettingsPlugin() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
    }

    /**
     * {@inheritDoc}
     * 
     * @param config
     */
    @Override
    public void setConfig(Map<String, String> config) {
        /**
         * explicitly no nothing
         */
    }

    /**
     * {@inheritDoc}
     * 
     * @param params
     */
    @Override
    public void setParams(List<String> params) {
        this.params = params.toArray(new String[params.size()]);
    }

    /**
     * {@inheritDoc}
     * 
     * @param metadata
     */
    @Override
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public String get_syntax() {

        try {
            parser.printHelpOn(System.err);
        } catch (IOException e) {
            Log.fatal(e);
        }
        return ("");
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public ReturnValue parse_parameters() {
        ReturnValue ret = new ReturnValue();
        try {
            options = parser.parse(params);
        } catch (OptionException e) {
            get_syntax();
            ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public ReturnValue init() {
        return new ReturnValue();
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public ReturnValue do_test() {
        return new ReturnValue();
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public ReturnValue do_run() {
        // output a description of our categories
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out))) {
            // output a description of our categories
            bufferedWriter.write("# SEQWARE PIPELINE SETTINGS\n" + "\n" + "# The settings in this file are tagged by when they are used.\n"
                    + "# COMMON: Used by all components\n" + "# INSTALL: Used when installing a workflow bundle\n"
                    + "# SCHEDULE: Used when a user wants to schedule a workflow run\n"
                    + "# LAUNCH: Used when a workflow run is to be launched (or dry-run)\n"
                    + "# DELETION: Used for the admin web service supporting deletion\n" + "#\n"
                    + "# Remote users need COMMON and SCHEDULE.\n" + "# Workflow developers need COMMON and LAUNCH for testing.\n"
                    + "# Administrators need COMMON, DELETION, and INSTALL.\n"
                    + "# Cronjobs/daemon processes will need COMMON and LAUNCH.\n\n"
                    + "# Keys that are required for a typical Oozie-sge with metadata via web service are marked as required.\n\n"
                    + "# Note that this document was auto-generated using the " + UserSettingsPlugin.class.getSimpleName() + "\n\n");

            SqwKeys.Categories currCategory = null;
            for (SqwKeys key : SqwKeys.values()) {
                // if looking at a new category, describe it
                if (key.getCategory() != currCategory) {
                    bufferedWriter.write("\n");
                    bufferedWriter.write("# " + key.getCategory().name() + "\n");
                    if (key.isRequired()) {
                        bufferedWriter.write("# required: ");
                    } else {
                        bufferedWriter.write("# optional: ");
                    }
                    bufferedWriter.write(key.getCategory().getCategoryDescription() + "\n");
                    bufferedWriter.write("\n");
                    currCategory = key.getCategory();
                }
                bufferedWriter.write("# " + key.getDescription() + "\n");
                bufferedWriter.write(key.getSettingKey() + "=" + key.getDefaultValue() + "\n");

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ReturnValue();
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public ReturnValue clean_up() {
        return new ReturnValue();
    }

    /**
     * <p>
     * get_description.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String get_description() {
        return ("A plugin that a documented .seqware settings file that can be used for documentation.");
    }

    public static void main(String[] args) {
        UserSettingsPlugin mp = new UserSettingsPlugin();
        mp.init();
        List<String> arr = new ArrayList<>();
        mp.params = new String[0];
        mp.parse_parameters();
        mp.do_run();
    }

}
