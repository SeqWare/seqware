package io.seqware.pipeline.plugins.sanity;



import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public final class SanityCheck extends Plugin {
    public static final int NUMBER_TO_OUTPUT = 100;


    /**
     * <p>Constructor for HelloWorld.</p>
     */
    public SanityCheck() {
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
        Collection<SanityCheckPluginInterface> plugins = (Collection<SanityCheckPluginInterface>) Lookup.getDefault().lookupAll(SanityCheckPluginInterface.class);
        MetadataDB metadataDB = null;
        try{
             metadataDB = DBAccess.get();
        } catch(RuntimeException e){
            if (e.getMessage().equals(MetadataFactory.NO_DATABASE_CONFIG)){
                System.err.println("Warning: No or invalid SeqWare metadb settings");
            } else{
                throw e;
            }
        }
        
        for(SanityCheckPluginInterface plugin : plugins){
            Log.info("Running " + plugin.getClass().getSimpleName());
            try{        
                boolean check = plugin.check(metadataDB == null? null : new QueryRunner(metadataDB), metadata);
                if (!check){
                    System.err.println("Failed check: " + plugin.getClass().getSimpleName());
                    ret.setExitStatus(ReturnValue.FAILURE);
                    return ret;
                } else{
                    System.err.println("Passed check: " + plugin.getClass().getSimpleName());
                }
            } catch (Exception e){
                Log.fatal("Plugin " + plugin.getClass().getSimpleName() + " died", e);
                System.err.println("Crashed and failed check: " + plugin.getClass().getSimpleName());
                ret.setExitStatus(ReturnValue.FAILURE);
                return ret;
            }
        }
        return new ReturnValue();
    }


    /**
     * <p>get_description.</p>
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
        List<String> arr = new ArrayList<String>();
        mp.setParams(arr);
        mp.parse_parameters();
        ReturnValue do_run = mp.do_run();
    }

    @Override
    public final ReturnValue clean_up() {
        return new ReturnValue();
    }

}
