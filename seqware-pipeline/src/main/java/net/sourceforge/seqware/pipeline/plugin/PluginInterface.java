package net.sourceforge.seqware.pipeline.plugin;

import java.util.List;
import java.util.Map;

import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.module.ReturnValue;

/**
 * @author briandoconnor@gmail.com
 * @since 20110925
 * 
 * <p>
 * This interface defines the required behavior of a SeqWare Pipeline plugin.
 * Essentially it assumes a life-cycle made up of several phases for a plugin,
 * specifying a function for each of these phase that an implementation
 * overrides to provide the code executed during that phase. The phases are:
 * </p>
 * <ol>
 *  <li>{@link ModuleInterface.init() init()}</li>
 *  <li>{@link ModuleInterface.do_verify_parameters() do_verify_parameters()}</li>
 *  <li>{@link ModuleInterface.do_verify_input() do_verify_input()}</li>
 *  <li>{@link ModuleInterface.do_test() do_test()}</li>
 *  <li>{@link ModuleInterface.do_run() do_run()}</li>
 *  <li>{@link ModuleInterface.do_verify_output() do_verify_output()}</li>
 *  <li>{@link ModuleInterface.clean_up() clean_up()}</li>
 * </ol>
 * <p>Information about the the success or
 * failure of each step is packaged into a {@link ReturnValue} object,
 * including anything sent to STDOUT and STDERR.
 * </p>
 * <p>
 * The lifecyle is very similar to the module lifecycle but with a different emphasis.
 * Modules are designed to call an external tool from within workflows whereas plugins
 * are intended to extend the core functionality of SeqWare Pipeline tool itself and
 * have nothing to with calling external tools.
 * </p>
 * <p>
 * Methods that are not implemented can return ReturnValue.NOTIMPLEMENTED (-1).
 * The PluginRunner will skip over these steps, and only fail on error if a method returns > 0.
 * </p>
 */
public interface PluginInterface {
  
  /**
   * Let's you set the config this object should use.
   * 
   * @param config A map that includes the key/values from the SeqWare settings file.
   */
  public void setConfig(Map<String, String> config);
  
  /**
   * Lets you set a List of params so the plugin can deal with parameters
   * 
   * @param params The parameters specifically passed in to the plugin.
   */
  public void setParams(List<String> params);
  
  /**
   * Lets you set the metadata object
   * 
   * @param params An object of type {@link Metadata}.
   */
  public void setMetadata(Metadata metadata);
  
  /**
   * Generates a "help" message for the user of the plugin. This will
   * be called by the PluginRunner to provide information about using the
   * plugin. It is up to the implementation to defined the format for the
   * returned message. This is triggered if no options are provided to the
   * PluginRunner.
   * 
   * @return Description of the command line parameters for this plugin.
   */
  public String get_syntax();
  
  /**
   * Generates a short description of the plugin, preferably one line.
   * 
   * @return Description of the plugin.
   */
  public String get_description();
  
  /**
   * Parameter validation code goes here. Use to make sure all required
   * parameters are available, that parameters are known, that they have
   * reasonable values, etc. Validation involving multiple options 
   * happens here where the existence and content of input
   * data files will be checked.
   * 
   * @param params The string array of all the command line params after the plugin
   * @return Success or failure info from this phase, along with any
   * console output.
   */
  public ReturnValue parse_parameters();
  
  /**
   * Plugin initialization code goes here. This is the first phase of the
   * plugin lifecycle run. Might do things here like set up database
   * access, create temp files, define defaults, etc. Preprocessing can be
   * done here, although normally processing should be done by/during
   * do_run(). The SEQWARE_SETTINGS file contents is passed in here.
   * 
   * @param config A map containing the contents of the SeqWare settings file
   * @return Success or failure info from this phase, along with any
   * console output.
   */
  public ReturnValue init();
  
  /**
   * Perform any active system testing here. Use to make sure a DB is
   * available, that a command line tools can be run, etc.
   * You can even write functional tests that run the program you're
   * wrapping on a small known good and then verify the output.
   * 
   * @return Success or failure info from this phase, along with any
   * console output.
   */
  public ReturnValue do_test();

  /**
   * Performs the main tasks for the plugin. This is where you actually
   * execute your task and where the bulk of your implementation will go.
   * 
   * @return Success or failure info from this phase, along with any
   * console output.
   */
  public ReturnValue do_run();
  
  /**
   * Perform post-task clean-up here. Use to remove temporary files and
   * directories, flush buffers and close db connections, etc.
   * 
   * @return Success or failure info from this phase, along with any
   * console output.
   */
  public ReturnValue clean_up();
  
}