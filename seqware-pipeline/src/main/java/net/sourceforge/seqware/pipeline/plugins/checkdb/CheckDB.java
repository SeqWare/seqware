package net.sourceforge.seqware.pipeline.plugins.checkdb;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;
import org.openide.util.Lookup;

import org.openide.util.lookup.ServiceProvider;

/**
 * A hbck for your SeqWare metadb
 *
 * @author dyuen ProviderFor(PluginInterface.class)
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public class CheckDB extends Plugin {

    private ReturnValue ret = new ReturnValue();

    /**
     * <p>Constructor for HelloWorld.</p>
     */
    public CheckDB() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
        ret.setExitStatus(ReturnValue.SUCCESS);
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
        Collection<CheckDBPlugin> plugins = (Collection<CheckDBPlugin>) Lookup.getDefault().lookupAll(CheckDBPlugin.class);
        for(CheckDBPlugin plugin : plugins){
            System.out.println("Running " + plugin.getClass().getSimpleName());
            
        }

        return ret;
    }


    /**
     * <p>get_description.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String get_description() {
        return ("An hbck for your seqware install.");
    }




    

    public static void main(String[] args) {
        CheckDB mp = new CheckDB();
        mp.init();
        List<String> arr = new ArrayList<String>();
        mp.setParams(arr);
        mp.parse_parameters();
        mp.do_run();
    }

    @Override
    public ReturnValue clean_up() {
        return ret;
    }

}
