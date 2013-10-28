package net.sourceforge.seqware.pipeline.plugins.checkdb;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.plugins.checkdb.CheckDBPluginInterface.Level;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.io.FileUtils;
import org.openide.util.Lookup;

import org.openide.util.lookup.ServiceProvider;
import org.rendersnake.HtmlCanvas;

/**
 * A database validation tool for your SeqWare metadb
 *
 * @author dyuen ProviderFor(PluginInterface.class)
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public final class CheckDB extends Plugin {
    public static final int NUMBER_TO_OUTPUT = 100;


    /**
     * <p>Constructor for HelloWorld.</p>
     */
    public CheckDB() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#init()
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public final ReturnValue init() {
         try {
            HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
            // do a defensive check to see if we have a direct database connection available
            if (settings.get("SW_DB_SERVER") == null
                    || settings.get("SW_DB") == null
                    || settings.get("SW_DB_USER") == null
                    || settings.get("SW_DB_PASS") == null) {
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
        return new ReturnValue();
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
        return new ReturnValue();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_run()
     */
    /**
     * {@inheritDoc}
     */
    @Override
    public final ReturnValue do_run() {
        ReturnValue ret = new ReturnValue();
        Collection<CheckDBPluginInterface> plugins = (Collection<CheckDBPluginInterface>) Lookup.getDefault().lookupAll(CheckDBPluginInterface.class);
        Map<CheckDBPluginInterface, SortedMap<CheckDBPluginInterface.Level, Set<String>>> resultMap = new HashMap<CheckDBPluginInterface, SortedMap<CheckDBPluginInterface.Level, Set<String>>>();
        for(CheckDBPluginInterface plugin : plugins){
            
            SortedMap<Level, Set<String>> result = new TreeMap<Level, Set<String>>();
            for(Level l : CheckDBPluginInterface.Level.values()){
                result.put(l, new HashSet<String>());
            }
            Log.info("Running " + plugin.getClass().getSimpleName());
            try{     
                plugin.check(new SelectQueryRunner(DBAccess.get()), result);
                resultMap.put(plugin, result);
            } catch (Exception e){
                Log.fatal("Plugin " + plugin.getClass().getSimpleName() + " died", e);
                if (!result.containsKey(Level.SEVERE)){
                    // defensive check in case plugin author decided to corrupt the map
                    result.put(Level.SEVERE, new HashSet<String>());
                }
                resultMap.get(plugin).get(Level.SEVERE).add("Plugin threw an exception and died");
                resultMap.put(plugin, result);
            }
        }
        // presumably, we would reformat resultMap and create a nice HTML report here
        HtmlCanvas html = new HtmlCanvas();
        try {
            html
                    .html()
                    .body()
                    .h1().content(this.getClass().getSimpleName() + " Report");
            for(Entry<CheckDBPluginInterface, SortedMap<CheckDBPluginInterface.Level, Set<String>>> pluginEntry : resultMap.entrySet()){
                html.h2().content(pluginEntry.getKey().getClass().getSimpleName());
               for(Entry<CheckDBPluginInterface.Level, Set<String>> warning : pluginEntry.getValue().entrySet()){
                   html.h3().content(warning.getKey().name());
                   html.ol();
                   for(String entry : warning.getValue()){
                       html.li().content(entry);
                   }
                   html._ol();
               }
            }
            html._body()._html();
            File createTempFile = File.createTempFile("report", ".html");
            FileUtils.write(createTempFile, html.toHtml());
            Log.stdout("Printed report to " + createTempFile.getAbsolutePath());
            ret.setUrl(createTempFile.toURI().toURL().toString());
        } catch (IOException ex) {
            Log.fatal("Could not render HTML report", ex);
            ret.setExitStatus(ReturnValue.FAILURE);
        }
        
        return ret;
    }


    /**
     * <p>get_description.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public final String get_description() {
        return ("A database validation tool for your SeqWare metadb.");
    }   

    public static void main(String[] args) throws IOException, URISyntaxException {
        CheckDB mp = new CheckDB();
        mp.init();
        List<String> arr = new ArrayList<String>();
        mp.setParams(arr);
        mp.parse_parameters();
        ReturnValue do_run = mp.do_run();
        Desktop.getDesktop().browse(new URI(do_run.getUrl()));
    }

    @Override
    public final ReturnValue clean_up() {
        return new ReturnValue();
    }

    /**
     * Convenience method for processing output and appending it to the warning map 
     * @param result
     * @param level
     * @param description
     * @param list 
     */
    public static void processOutput(SortedMap<Level, Set<String>> result, Level level, String description, List<Integer> list){
        if (list.size() > 0){
            Collections.sort(list);
            if (list.size() > NUMBER_TO_OUTPUT){
                result.get(level).add(description + list.subList(0, NUMBER_TO_OUTPUT).toString() + " (Truncated, "+list.size()+" in total)");
            } else{
                result.get(level).add(description + list.toString());
            } 
        }
    }
}
