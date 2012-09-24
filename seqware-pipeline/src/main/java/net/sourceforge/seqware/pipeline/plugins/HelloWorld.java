/**
 * 
 */
package net.sourceforge.seqware.pipeline.plugins;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import joptsimple.OptionException;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.common.util.Log;

import org.openide.util.lookup.ServiceProvider;

/**
 * @author boconnor
 * ProviderFor(PluginInterface.class)
 */
@ServiceProvider(service=PluginInterface.class)
public class HelloWorld extends Plugin {

  ReturnValue ret = new ReturnValue();

  public HelloWorld() {
    super();
    parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
    ret.setExitStatus(ReturnValue.SUCCESS);
  }

  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#setConfig(java.util.Map)
   */
  @Override
  public void setConfig(Map<String, String> config) {
    println("Setting Config");
    println("Config File Contents:");
    for(String key : config.keySet()) {
      println("  "+key+" "+config.get(key));
    }
  }

  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#setParams(java.util.List)
   */
  @Override
  public void setParams(List<String> params) {
    println("Setting Params: "+params);
    this.params = params.toArray(new String[0]);
  }

  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#setMetadata(net.sourceforge.seqware.pipeline.metadata.Metadata)
   */
  @Override
  public void setMetadata(Metadata metadata) {
    println("Setting Metadata: "+metadata);
    this.metadata = metadata;
  }
  
  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#get_syntax()
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
  @Override
  public ReturnValue init() {
    return ret;
  }
  
  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_test()
   */
  @Override
  public ReturnValue do_test() {
    // TODO Auto-generated method stub
    return ret;
  }

  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_run()
   */
  @Override
  public ReturnValue do_run() {
    // TODO Auto-generated method stub
    println("  Hello");
    return ret;
  }
  
  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#clean_up()
   */
  @Override
  public ReturnValue clean_up() {
    // TODO Auto-generated method stub
    return ret;
  }
  
  public String get_description() {
    return("A very simple HelloWorld to show how to make plugins.");
  }

}
