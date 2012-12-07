/**
 *
 */
package net.sourceforge.seqware.pipeline.plugins;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import joptsimple.HelpFormatter;
import joptsimple.OptionDescriptor;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.common.util.Log;
import org.openide.util.Lookup;

import org.openide.util.lookup.ServiceProvider;

/**
 * <p>This plugin outputs documentation for our plugins in markdown format</p>
 *
 * @author dyuen ProviderFor(PluginInterface.class)
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public class MarkdownPlugin extends Plugin {

    ReturnValue ret = new ReturnValue();

    /**
     * <p>Constructor for HelloWorld.</p>
     */
    public MarkdownPlugin() {
        super();
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
//        println("Setting Config");
//        println("Config File Contents:");
//        for (String key : config.keySet()) {
//            println("  " + key + " " + config.get(key));
//        }
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#setParams(java.util.List)
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public void setParams(List<String> params) {
        //println("Setting Params: " + params);
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
            e.printStackTrace();
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
        Collection<? extends PluginInterface> plugs;
        plugs = Lookup.getDefault().lookupAll(PluginInterface.class);
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(System.out));
        
        try {
            bufferedWriter.append("---");
            bufferedWriter.newLine();
            bufferedWriter.append("");
            bufferedWriter.newLine();
            bufferedWriter.append("title:                 \"Plugins\"");
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
        
        for (PluginInterface plug : plugs) {
            try {
                Class myClass = plug.getClass();
                Field myField = getField(myClass, "parser");
                myField.setAccessible(true); // required if field is not normally accessible
                OptionParser get = (OptionParser) myField.get(plug);
                get.formatHelpWith(new MarkDownFormatter());
                bufferedWriter.newLine();
                bufferedWriter.append("##  " +plug.getClass().getSimpleName());
                bufferedWriter.newLine();
                bufferedWriter.append(plug.getClass().getPackage().getName()+"."+plug.getClass().getSimpleName());
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                bufferedWriter.append( plug.get_description());
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                get.printHelpOn(bufferedWriter);
            } catch (NoSuchFieldException ex) {
                Log.fatal(ex, ex);
            } catch (IllegalAccessException ex) {
                Log.fatal(ex, ex);
            } catch (IOException ex) {
                Log.fatal(ex, ex);
            }

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
    public String get_description() {
        return ("A plugin that generates markdown documentation for all plugins.");
    }
    
    public class MarkDownFormatter implements HelpFormatter{

        @Override
        public String format(Map<String, ? extends OptionDescriptor> options) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("| Command-line option | Description |\n");
            // not sure why options are reported once per
            Set<String> done = new HashSet<String>();
            for(Entry<String, ? extends OptionDescriptor> e : options.entrySet()){
                if (done.contains(e.getValue().description())){
                    continue;
                }
                done.add(e.getValue().description());
                
                buffer.append("|");
                for(String o : e.getValue().options()){
                    buffer.append("--" + o + ", ");
                }
                buffer.deleteCharAt(buffer.length()-1);
                buffer.deleteCharAt(buffer.length()-1);
                buffer.append("|");
                buffer.append(e.getValue().description());
                buffer.append("|");
                buffer.append("\n");
            }
            return buffer.toString();
        }
        
    }
}
