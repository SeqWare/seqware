/**
 *
 */
package net.sourceforge.seqware.pipeline.plugins;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import joptsimple.HelpFormatter;
import joptsimple.OptionDescriptor;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;
import org.openide.util.Lookup;

import org.openide.util.lookup.ServiceProvider;

/**
 * <p>This plugin outputs documentation for our plugins and modules in markdown
 * format</p>
 *
 * @author dyuen ProviderFor(PluginInterface.class)
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public class MarkdownPlugin extends Plugin {

    private ReturnValue ret = new ReturnValue();

    /**
     * <p>Constructor for HelloWorld.</p>
     */
    public MarkdownPlugin() {
        super();
        parser.acceptsAll(Arrays.asList("skip", "s"), "Optional: comma separated list of module/plugin names to skip").requiresArgument();
        parser.acceptsAll(Arrays.asList("modules", "m"), "Optional: if provided will list out modules instead of plugins.");
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
        ret.setExitStatus(ReturnValue.SUCCESS);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#setConfig(java.util.Map)
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public void setConfig(Map<String, String> config) {
        /**
         * explicitly no nothing
         */
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#setParams(java.util.List)
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public void setParams(List<String> params) {
        this.params = params.toArray(new String[0]);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#setMetadata(net.sourceforge.seqware.pipeline.metadata.Metadata)
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public void setMetadata(Metadata metadata) {
        //println("Setting Metadata: " + metadata);
        this.metadata = metadata;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#get_syntax()
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public String get_syntax() {

        try {
            parser.printHelpOn(System.err);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.fatal(e);
        }
        return ("");
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#parse_parameters()
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue parse_parameters() {

        try {
            options = parser.parse(params);
        } catch (OptionException e) {
            get_syntax();
            ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
        }
        return ret;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#init()
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue init() {
        return ret;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_test()
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue do_test() {
        // TODO Auto-generated method stub
        return ret;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_run()
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue do_run() {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        Set<String> toSkip = new HashSet<String>();
        if (options.has("skip")) {
            Object valueOf = options.valuesOf("skip");
            String[] vals = valueOf.toString().split(",");
            toSkip.addAll(Arrays.asList(vals));
        }
        
        if (options.has("modules")) {
            Collection<ModuleInterface> mods;
            mods = (Collection<ModuleInterface>) Lookup.getDefault().lookupAll(ModuleInterface.class);
            List<ModuleInterface> modsList = new ArrayList<ModuleInterface>();
            modsList.addAll(mods);
            Collections.sort(modsList, new ModuleComparator());
            handlePlugins(bufferedWriter, modsList, toSkip);
        } else {
            Collection<PluginInterface> mods;
            mods = (Collection<PluginInterface>) Lookup.getDefault().lookupAll(PluginInterface.class);
            List<PluginInterface> modsList = new ArrayList<PluginInterface>();
            modsList.addAll(mods);
            Collections.sort(modsList, new PluginComparator());
            handlePlugins(bufferedWriter, modsList, toSkip);
        }

        return ret;
    }

    private static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getField(superClass, fieldName);
            }
        }
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#clean_up()
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnValue clean_up() {
        // TODO Auto-generated method stub
        return ret;
    }

    /**
     * <p>get_description.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String get_description() {
        return ("A plugin that generates markdown documentation for all plugins.");
    }

    private void handlePlugins(BufferedWriter bufferedWriter, List<? extends Object> plugs, Set<String> skip) {
        writePageHeader(bufferedWriter, plugs);

        for (Object plug : plugs) {
            try {
                bufferedWriter.newLine();
                if (plug instanceof PluginInterface) {
                    writePluginDescription(bufferedWriter, plug, skip);
                } else if (plug instanceof Module) {
                    writeModuleDescription(plug, bufferedWriter);
                }

            } catch (NoSuchFieldException ex) {
                Log.fatal(ex, ex);
            } catch (IllegalAccessException ex) {
                Log.fatal(ex, ex);
            } catch (IOException ex) {
                Log.fatal(ex, ex);
            }

        }
    }

    private void writePageHeader(BufferedWriter bufferedWriter, List<? extends Object> plugs) {
        try {
            bufferedWriter.append("---");
            bufferedWriter.newLine();
            bufferedWriter.append("");
            bufferedWriter.newLine();
            if (plugs.toArray()[0] instanceof PluginInterface) {
                bufferedWriter.append("title:                 \"Plugins\"");
            } else if (plugs.toArray()[0] instanceof Module) {
                bufferedWriter.append("title:                 \"Modules\"");
            } else {
                bufferedWriter.append("title:                 \"Unknown\"");
            }
            bufferedWriter.newLine();
            bufferedWriter.append("toc_includes_sections: true");
            bufferedWriter.newLine();
            bufferedWriter.append("markdown:              advanced");
            bufferedWriter.newLine();
            bufferedWriter.append("");
            bufferedWriter.newLine();
            bufferedWriter.append("---");
            bufferedWriter.newLine();
        } catch (IOException ex) {
            Log.fatal(ex, ex);
        }
    }

    private void writePluginDescription(BufferedWriter bufferedWriter, Object plug, Set<String> skip) throws IllegalAccessException, SecurityException, IOException, IllegalArgumentException, NoSuchFieldException {
        // check for skipping 
        final String simpleName = plug.getClass().getSimpleName();
        if (skip.contains(simpleName)) {
            return;
        }
            
        bufferedWriter.newLine();
        bufferedWriter.append("##  " + simpleName);
        bufferedWriter.newLine();
        bufferedWriter.append(plug.getClass().getPackage().getName() + "." + simpleName);
        bufferedWriter.append(((PluginInterface) plug).get_description());
        bufferedWriter.newLine();
        bufferedWriter.newLine();
        Class myClass = plug.getClass();
        Field myField = getField(myClass, "parser");
        // required if field is not normally accessible
        myField.setAccessible(true);
        OptionParser get = (OptionParser) myField.get(plug);
        get.formatHelpWith(new MarkDownFormatter());
        get.printHelpOn(bufferedWriter);
    }

    private void writeModuleDescription(Object plug, BufferedWriter bufferedWriter) throws SecurityException, IllegalAccessException, IOException, IllegalArgumentException {
        Module mod = (Module) plug;
        String description = "";
        try {
            ReturnValue init = mod.init();
            description = init.getDescription() == null ? "" : init.getDescription();
        } catch (Exception e) {
            Log.info("Could not print description for " + mod.getClass());
        }
        bufferedWriter.append(description);

        bufferedWriter.newLine();
        bufferedWriter.newLine();

        Class myClass = plug.getClass();
        try {
            Method getOptionParserMethod = myClass.getDeclaredMethod("getOptionParser");
            getOptionParserMethod.setAccessible(true);
            Object invoke = getOptionParserMethod.invoke(plug, new Object[]{});
            OptionParser get = (OptionParser) invoke;
            get.formatHelpWith(new MarkDownFormatter());
            get.printHelpOn(bufferedWriter);
        } catch (InvocationTargetException ex) {
            Log.info("Could not retrieve OptionParser for " + mod.getClass());
        } catch (NoSuchMethodException ex) {
            Log.info(ex, ex);
        }
    }

    public class MarkDownFormatter implements HelpFormatter {

        @Override
        public String format(Map<String, ? extends OptionDescriptor> options) {
            if (options.isEmpty()) {
                return "";
            }
            StringBuilder buffer = new StringBuilder();
            buffer.append("| Command-line option | Description |\n");
            buffer.append("|--------------------|--------------|\n");
            // not sure why options are reported once per
            Set<String> done = new HashSet<String>();
            for (Entry<String, ? extends OptionDescriptor> e : options.entrySet()) {
                if (done.contains(e.getValue().description())) {
                    continue;
                }
                done.add(e.getValue().description());

                buffer.append("|");
                for (String o : e.getValue().options()) {
                    buffer.append("--").append(o).append(", ");
                }
                buffer.deleteCharAt(buffer.length() - 1);
                buffer.deleteCharAt(buffer.length() - 1);
                buffer.append("|");
                buffer.append(e.getValue().description());
                buffer.append("|");
                buffer.append("\n");
            }
            return buffer.toString();
        }
    }

    public static void main(String[] args) {
        MarkdownPlugin mp = new MarkdownPlugin();
        mp.init();
        List<String> arr = new ArrayList<String>();
        mp.setParams(arr);
        mp.parse_parameters();
        mp.do_run();
    }

    public class ModuleComparator implements Comparator<ModuleInterface> {

        @Override
        public int compare(ModuleInterface t, ModuleInterface t1) {
            return (t.getClass().getSimpleName().compareTo(t1.getClass().getSimpleName()));
        }
    }

    public class PluginComparator implements Comparator<PluginInterface> {

        @Override
        public int compare(PluginInterface t, PluginInterface t1) {
            return (t.getClass().getSimpleName().compareTo(t1.getClass().getSimpleName()));
        }
    }
}
