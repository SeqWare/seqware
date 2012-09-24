package net.sourceforge.seqware.pipeline.decider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;

public abstract class Decider implements DeciderInterface {
  public static final String SUCCESS = "success";
  public static final String FAILED = "failed";
  public static final String PENDING = "pending";
  public static final String RUNNING = "running";
  
  protected OptionParser parser = new OptionParser();
  protected OptionSet options = null;
  protected String[] params = null;
  protected Metadata metadata = null;
  protected Map<String, String> config = null;
  
  /**
   * Notes:
   * 
   * Things I would like taken care of for me:
   * 
   * * all params in a hash (from both DB and ini file provided)
   * * a list of hashmap that represent a filtered list of files by sample, study, experiment, workflow, etc
   * * a list of files that represent the output from this workflow
   * * a hash that joins the output of this workflow to their parents 
   * * the master list of results should be configurable, either to have
   * all the processing events removed where this workflow has already been run
   * or to include these
   * 
   * 
   */
  public Decider() {
    super();
  }

  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#setConfig(java.util.Map)
   */
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
  @Override
  public void setParams(List<String> params) {
    Log.info("Setting Params: "+params);
    this.params = params.toArray(new String[0]);
  }
  
  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#setMetadata(net.sourceforge.seqware.pipeline.metadata.Metadata)
   */
  @Override
  public void setMetadata(Metadata metadata) {
    Log.info("Setting Metadata: "+metadata);
    this.metadata = metadata;
  }
  
  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#get_syntax()
   */
  @Override
  public String get_syntax() {
    
    try {
      parser.printHelpOn(System.out);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return ("");
  }
  
  @Override
  public String get_description() {
    return("");
  }
  
  /* (non-Javadoc)
   * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#parse_parameters()
   */
  @Override
  public ReturnValue parse_parameters() {
    
    ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
    try {
      options = parser.parse(params);
    } catch (OptionException e) {
      Log.error(e.getMessage());
     
      get_syntax();
      ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
    }
    return ret;
  }
  
  protected List<Map<String, String>> filterFiles() {
    List<Map<String, String>> ret = new ArrayList<Map<String, String>>();

    return(ret);
  } 
  
}
