package net.sourceforge.seqware.pipeline.modules.qc;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.runtools.RunTools;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;

import org.openide.util.lookup.ServiceProvider;

/**
 * Performing per lane based statistics
 *  
 * Underlying script:  sw_module_BCtrend.pl
 *                
 * Necessary programs:  perl 
 *  
 * Expected output:  read_depth.txt
 * 
 * 
 * 
 * @author jyli@med.unc.edu
 *
 */
@ServiceProvider(service=ModuleInterface.class)
public class BCtrend extends Module {
  
  private OptionSet options = null;
  private File tempDir = null;
  
  /**
   * getOptionParser is an internal method to parse command line args.
   * 
   * @return OptionParser this is used to get command line options
   */  
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("perl", "Path to perl").withRequiredArg();
    parser.accepts("script", "Path to perl script: sw_module_BCtrend.pl").withRequiredArg();
    parser.accepts("infile", "per base statistics , sample.stat.txt from qualStat module").withRequiredArg();
    parser.accepts("outfile", "Total read and unique read in a lane, sample.BCtrend.txt").withRequiredArg();
    parser.accepts("trendPlot", "Base calling score dropping trend, sample.BCtrend.png").withRequiredArg();
    parser.accepts("flagfile", "Base calling score dropping flag indicator, flowcell.lane.BCflag.txt").withRequiredArg();
    return (parser);
  }
  
  /**
   * A method used to return the syntax for this module
   * @return a string describing the syntax
   */
  @Override
  public String get_syntax() {
    OptionParser parser = getOptionParser();
    StringWriter output = new StringWriter();
    try {
      parser.printHelpOn(output);
      return(output.toString());
    } catch (IOException e) {
      e.printStackTrace();
      return(e.getMessage());
    }
  }

  /**
   * All necessary setup for the module.
   * Populate the "processing" table in seqware_meta_db. 
   * Create a temporary directory.
   *  
   * @return A ReturnValue object that contains information about the status of init.
   */
  @Override
  public ReturnValue init() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    // fill in the [xxx] fields in the processing table
    ret.setAlgorithm("BCtrend");
    ret.setDescription("BaseCalling score trend");
    ret.setVersion("0.7.0");
    
    try {
      OptionParser parser = getOptionParser();
      // The parameters object is actually an ArrayList of Strings created
      // by splitting the command line options by space. JOpt expects a String[]
      options = parser.parse(this.getParameters().toArray(new String[0]));
      // create a temp directory in current working directory
      tempDir = FileTools.createTempDirectory(new File("."));
      // you can write to "stdout" or "stderr" which will be persisted back to the DB
      ret.setStdout(ret.getStdout()+"Output: "+(String)options.valueOf("outfile")+"\n");
    } catch (OptionException e) {
      e.printStackTrace();
      ret.setStderr(e.getMessage());
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
    } catch (IOException e) {
      e.printStackTrace();
      ret.setStderr(e.getMessage());
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
    }
    
    return (ret);
  }
  
  /**
   * Verify that the parameters are defined & make sense.
   * 
   * @return a ReturnValue object
   */
  @Override
  public ReturnValue do_verify_parameters() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    // now look at the options and make sure they make sense
    for (String option : new String[] {
       "infile", "outfile","trendPlot","flagfile", "perl", "script"
      }) {
      if (!options.has(option)) {
        ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        String stdErr = ret.getStderr();
        ret.setStderr(stdErr+"Must include parameter: --"+option+"\n");
      }
    }

    return ret;
  }
  /**
   * Verify anything needed to run the module is ready (e.g. input files exist, etc).
   * 
   * @return a ReturnValue object
   */
  @Override

  public ReturnValue do_verify_input() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    if (FileTools.fileExistsAndReadable(new File((String)options.valueOf("infile"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Input file does not exist or is not readable");
    }
       
    // Is output file path writable?
    File output = new File((String) options.valueOf("outfile"));
    if (FileTools.dirPathExistsAndWritable(output.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory");
    }
   
    // Is output file path writable?
    File output1 = new File((String) options.valueOf("trendPlot"));
    if (FileTools.dirPathExistsAndWritable(output1.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory for BCtrend plot");
    }
    
    // Is output file path writable?
    File output2 = new File((String) options.valueOf("flagfile"));
    if (FileTools.dirPathExistsAndWritable(output2.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory for BCflag file");
    }
    
    
    // Is 'perl' executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("perl"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("perl"));
    }
    
    // Does 'script' (sw_module_parseIllQC.pl) exist?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("script"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Could not find sw_module_BCtrend.pl at "+(String)options.valueOf("script"));
    }
        
    // Is tempDir writeable?
    if (FileTools.dirPathExistsAndWritable(tempDir).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to temp directory");
    }
    
    return (ret);

  }
  
  /**
   * Optional:  Test program on a known data set.  Not implemented in this module.
   * 
   * @return a ReturnValue object
   */
  @Override
  public ReturnValue do_test() {
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.NOTIMPLEMENTED);
    return(ret);
  }
  
  
  @Override
public ReturnValue do_run() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    ret.setRunStartTstmp(new Date());
    
    String output    = (String)options.valueOf("outfile");
    String trendPlot = (String)options.valueOf("trendPlot");
    String flag      = (String)options.valueOf("flagfile");
    
    StringBuffer cmd = new StringBuffer();
    cmd.append(options.valueOf("perl") + " " + options.valueOf("script") + " " );
    cmd.append(options.valueOf("infile") + " " + options.valueOf("outfile") + " " ); 
    cmd.append(options.valueOf("trendPlot") + "  " + options.valueOf("flagfile") + " " + tempDir.getAbsolutePath());
     
    ReturnValue runRet = RunTools.runCommand( new String[] { "bash", "-c", cmd.toString() } );
    if (runRet.getExitStatus() != ReturnValue.SUCCESS || runRet.getProcessExitStatus() != ReturnValue.SUCCESS) {
      ret.setRunStopTstmp(new Date());
      ret.setStderr(runRet.getStderr());
      ret.setStdout(runRet.getStdout());
      ret.setExitStatus(runRet.getExitStatus());
      ret.setProcessExitStatus(runRet.getProcessExitStatus());
      return(ret);
    }
    
    // record the file output
    //FIXME, why this file does not get linked to LIMS?
    FileMetadata fm = new FileMetadata();
    fm.setMetaType("text/BCtrend"); 
    fm.setFilePath(output);
    fm.setType("Performing per lane base calling score trend");
    fm.setDescription("Text file output of qc/BCtrend module.");
    ret.getFiles().add(fm);
    
    FileMetadata fm2 = new FileMetadata();
    fm2.setMetaType("png/BCtrend");
    fm2.setFilePath(trendPlot);
    fm2.setType("Plottting bc dropping trend");
    fm2.setDescription("Figure file output of qc/BCtrend module.");
    ret.getFiles().add(fm2);
    
    FileMetadata fm3 = new FileMetadata();
    fm3.setMetaType("text/key_value");
    fm3.setFilePath(flag);
    fm3.setType("Store BC dropping flag as well as where it starts to drop dramatically 3");
    fm3.setDescription("Text file output of qc/BCtrend module.");
    ret.getFiles().add(fm3);
    
    ret.setRunStopTstmp(new Date());
    return(ret);
  }

  @Override
  public ReturnValue do_verify_output() {
    // just make sure the file exists
    ReturnValue runRet = FileTools.fileExistsAndNotEmpty(new File((String)options.valueOf("outfile")));
    if (runRet.getExitStatus() != ReturnValue.SUCCESS) { return(runRet); }
    runRet = FileTools.fileExistsAndNotEmpty(new File((String)options.valueOf("trendPlot")));
    if (runRet.getExitStatus() != ReturnValue.SUCCESS) { return(runRet); }
    return(FileTools.fileExistsAndNotEmpty(new File((String)options.valueOf("flagfile"))));
  }
  /**
   * Optional:  Cleanup.  Remove tempDir.
   * Cleanup files that are outside the current working directory since Pegasus won't do that for you.
   * 
   */
  @Override
  public ReturnValue clean_up() {
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    if (!FileTools.deleteDirectoryRecursive(tempDir)) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't delete folder: "+tempDir.getAbsolutePath());
    }
    return(ret);
  }
}
