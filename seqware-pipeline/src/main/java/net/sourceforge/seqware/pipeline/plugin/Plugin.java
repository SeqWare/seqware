package net.sourceforge.seqware.pipeline.plugin;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import joptsimple.BuiltinHelpFormatter;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;

/**
 * <p>Abstract Plugin class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public abstract class Plugin implements PluginInterface {
  
  protected OptionParser parser = new OptionParser();
  protected OptionSet options = null;
  protected String[] params = null;
  protected Metadata metadata = null;
  protected Map<String, String> config = null;
  
  /**
   * <p>Constructor for Plugin.</p>
   */
  public Plugin() {
    super();
  }

  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#setConfig(java.util.Map)
   */
  /** {@inheritDoc} */
  @Override
  public void setConfig(Map<String, String> config) {
    this.config = config;
    Log.info("Setting Config");
    Log.info("Config File Contents:");
    for(String key : config.keySet()) {
      Log.info("  "+key+" "+config.get(key));
    }
  }
  
  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#setParams(java.util.List)
   */
  /** {@inheritDoc} */
  @Override
  public void setParams(List<String> params) {
    Log.info("Setting Params: "+params);
    this.params = params.toArray(new String[0]);
  }
  
  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#setMetadata(net.sourceforge.seqware.pipeline.metadata.Metadata)
   */
  /** {@inheritDoc} */
  @Override
  public void setMetadata(Metadata metadata) {
    Log.info("Setting Metadata: "+metadata);
    this.metadata = metadata;
  }
  
  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#get_syntax()
   */
  /** {@inheritDoc} */
  @Override
  public String get_syntax() {
    
    try {
        parser.formatHelpWith(new BuiltinHelpFormatter(160,2));
      parser.printHelpOn(System.out);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return ("");
  }
  
  /** {@inheritDoc} */
  @Override
  public String get_description() {
    return("");
  }
  
  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#parse_parameters()
   */
  /** {@inheritDoc} */
  @Override
  public ReturnValue parse_parameters() {
    
    ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
    try {
      options = parser.parse(params);
    } catch (OptionException e) {     
      get_syntax();
      ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
    }
    return ret;
  }
  
  /**
   * <p>print.</p>
   *
   * @param output a {@link java.lang.String} object.
   */
  public void print(String output) {
      
      // FIXME: Yong, do something cleaver here, for now I'm using System.out
      System.out.print(output);
      
  }
  
  /**
   * <p>println.</p>
   *
   * @param output a {@link java.lang.String} object.
   */
  public void println(String output) {
      
      // FIXME: Yong, do something cleaver here, for now I'm using System.out
      Log.stdout(output);
      
  }
  
}
