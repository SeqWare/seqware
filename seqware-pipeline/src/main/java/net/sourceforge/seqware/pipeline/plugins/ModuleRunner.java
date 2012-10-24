/**
 * 
 */
package net.sourceforge.seqware.pipeline.plugins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.pipeline.runner.Runner;
import net.sourceforge.seqware.common.util.Log;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * <p>ModuleRunner class.</p>
 *
 * @author boconnor
 * ProviderFor(PluginInterface.class)
 *
 * TODO: need to merge/migrate the Runner code into this plugin framework. For now I'm just calling the other object.
 * @version $Id: $Id
 */
@ServiceProvider(service=PluginInterface.class)
public class ModuleRunner extends Plugin {

  ReturnValue ret = new ReturnValue();
  Runner runner = new Runner();

  /**
   * <p>Constructor for ModuleRunner.</p>
   */
  public ModuleRunner() {
    super();
    //parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
    ret.setExitStatus(ReturnValue.SUCCESS);
  }

  /**
   * <p>parse_parameters.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public ReturnValue parse_parameters() {
    return(ret); 
  }

  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#init()
   */
  /** {@inheritDoc} */
  @Override
  public ReturnValue init() {
    return ret;
  }

  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_test()
   */
  /** {@inheritDoc} */
  @Override
  public ReturnValue do_test() {
    return ret;
  }

  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_run()
   */
  /** {@inheritDoc} */
  @Override
  public ReturnValue do_run() {

    if (params == null || params.length == 0 || contains(params, "help")) {
      doHelp();
      Log.stdout("NOTE: This ModuleRunner will simply pass all parameters to the Runner object. To use one of the modules above simple execute:\n" +
      		"\n" +
      		"  java -jar seqware-pipeline-*.jar -p net.sourceforge.seqware.pipeline.plugins.ModuleRunner [RunnerParameters (see help message below)] --module [module from list above] -- [ModuleParameters]" +
      		"\n\nRunner Help Message:\n");
      runner.run(new String[0]);
    } else {
      runner.run(params);
    }

    return ret; 

  }
  
  private boolean contains(String[] arr, String search) {
    for (String token : arr) {
      Log.debug("Array:"+token);
      if (token.contains(search)) {
        return(true);
      }
    }
    return (false);
  }

  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#clean_up()
   */
  /** {@inheritDoc} */
  @Override
  public ReturnValue clean_up() {
    return ret;
  }

  /**
   * <p>get_description.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String get_description() {
    return("Description: A wrapper around Runner which will either list all Modules in the classpath (if no args are passed) or trigger a specific Module. Great for running Modules standalone.");
  }

  private void doHelp() {
    // then list all the modules in the classpath!
    Collection<? extends ModuleInterface> mods;
    mods = Lookup.getDefault().lookupAll(ModuleInterface.class);

    Log.stdout("\nAvailable Module List:");

    HashMap<String, ArrayList<String>> modules = new HashMap<String, ArrayList<String>>();

    for (ModuleInterface mod : mods) {

      if (modules.get(mod.getClass().getPackage().getName()) == null) {
        ArrayList<String> modulesInPackage = new ArrayList<String>();
        modules.put(mod.getClass().getPackage().getName(), modulesInPackage);
      }
      ArrayList<String> modulesInPackage = modules.get(mod.getClass().getPackage().getName());
      modulesInPackage.add(mod.getClass().getPackage().getName()+"."+mod.getClass().getSimpleName());
    }

    for (String pack : modules.keySet()) {
      Log.stdout("\n  Package: "+pack+"\n");
      for (String mod : modules.get(pack)) {
        Log.stdout("    Module: "+mod);
      }
    }
    
  }

}
