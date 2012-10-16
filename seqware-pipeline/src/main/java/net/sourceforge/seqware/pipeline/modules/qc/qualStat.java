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
 * Underlying script:  fastx_quality_stats executable installed
 * Dependency:  fastx_quality_stats from http://hannonlab.cshl.edu/fastx_toolkit/commandline.html
 *              Currently installed at:  /usr/bin/fastx_quality_stats
 * Expected output:  qual_filter.txt
 *
 * @author jyli@med.unc.edu
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class qualStat extends Module {
  
  private OptionSet options = null;
  private File tempDir = null;
  
  /**
   * getOptionParser is an internal method to parse command line args.
   *
   * @return OptionParser this is used to get command line options
   */
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("infile", "fastq format Illumina raw file").withRequiredArg();
    parser.accepts("outfile", "per base statistics , sample.stat.txt").withRequiredArg();
    parser.accepts("outSummary", "25, 50 and 75% on specific cycle,sample.stat.summary.txt").withRequiredArg();
    parser.accepts("fastxStats", "/usr/bin/fastx_quality_stats ").withRequiredArg();
    parser.accepts("perl", "Path to perl").withRequiredArg();
    parser.accepts("convertScript", "Path to perl script: sanger2Ill.pl").withRequiredArg();
    parser.accepts("script", "Path to perl script: sw_module_perBaseStat.pl").withRequiredArg();
    return (parser);
  }
  
  /**
   * {@inheritDoc}
   *
   * A method used to return the syntax for this module
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
   * {@inheritDoc}
   *
   * All necessary setup for the module.
   * Populate the "processing" table in seqware_meta_db.
   * Create a temporary directory.
   */
  @Override
  public ReturnValue init() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    // fill in the [xxx] fields in the processing table
    ret.setAlgorithm("qualStat");
    ret.setDescription("Per lane based statistics");
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
   * {@inheritDoc}
   *
   * Verify that the parameters are defined & make sense.
   */
  @Override
  public ReturnValue do_verify_parameters() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    // now look at the options and make sure they make sense
    for (String option : new String[] {
       "fastxStats", "infile", "outfile", "outSummary", "perl", "convertScript", "script"
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
   * {@inheritDoc}
   *
   * Verify anything needed to run the module is ready (e.g. input files exist, etc).
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
      ret.setStderr("Can't write to output file directory");
    }
    
 // Is outSummary file path writable?
    File outSummary = new File((String) options.valueOf("outSummary"));
    if (FileTools.dirPathExistsAndWritable(output.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output summary directory");
    }
    
    // Is 'fastx_quality_stats' executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("fastxStats"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("fastxStats"));
    }
    // Is 'perl' executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("perl"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("perl"));
    }
    
    // Does 'script' (sw_module_parseIllQC.pl) exist?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("script"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Could not find sw_module_perBaseStat.pl at "+(String)options.valueOf("script"));
    }
       
    // Does 'convert script' (sanger2Ill.pl) exist?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("convertScript"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Could not find sanger2Ill.pl at "+(String)options.valueOf("convertScript"));
    }
    
    // Is tempDir writeable?
    if (FileTools.dirPathExistsAndWritable(tempDir).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to temp directory");
    }
    
    // If input cycle is defined, is it integer?  need to fix here
    if (options.has("cycle1")) {
        if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("cycle1"))).getExitStatus() != ReturnValue.SUCCESS) {
        ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        ret.setStderr("Cycle 1 is not integer.");
      }
    }
    if (options.has("cycle2")) {
      if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("cycle2"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Cycle 2 is not integer.");
      }
    }
    if (options.has("cycle3")) {
      if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("cycle2"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Cycle 3 is not integer.");
      }
    }
    return (ret);

  }
  
  /**
   * {@inheritDoc}
   *
   * Optional:  Test program on a known data set.  Not implemented in this module.
   */
  @Override
  public ReturnValue do_test() {
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.NOTIMPLEMENTED);
    return(ret);
  }
  
  
  /** {@inheritDoc} */
  @Override
public ReturnValue do_run() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    ret.setRunStartTstmp(new Date());
    String output      = (String)options.valueOf("outfile");
    String outSummary  = (String)options.valueOf("outSummary");

    StringBuffer cmd3 = new StringBuffer();
    cmd3.append(options.valueOf("perl") + " " + options.valueOf("convertScript") + " " + options.valueOf("infile") + "  " );
    cmd3.append(tempDir.getAbsolutePath()+ "/illumina.fastq");
    
    
    StringBuffer cmd = new StringBuffer();
    cmd.append(options.valueOf("fastxStats") +  " " + "-i" + " " );
  //  cmd.append(options.valueOf("infile") + " " +  "-o" + " " + options.valueOf("outfile") + " " + tempDir.getAbsolutePath()+ "  ");
    cmd.append(tempDir.getAbsolutePath()+ "/illumina.fastq" + " " +  "-o" + " " + options.valueOf("outfile") + " " + tempDir.getAbsolutePath()+ "  ");
    
    StringBuffer cmd2 = new StringBuffer();
    cmd2.append(options.valueOf("perl") + " " + options.valueOf("script") + " " + options.valueOf("outfile") + " ");
    cmd2.append(options.valueOf("outSummary") + "  ");
    
    
    if (options.has("cycle1")) { cmd2.append(options.valueOf("cycle1") + " "); }
    else { cmd2.append("undefined" + " "); }
    
    if (options.has("cycle2")) { cmd2.append(options.valueOf("cycle2") + " "); }
    else { cmd2.append("undefined" + " "); }
    
    if (options.has("cycle3")) { cmd2.append(options.valueOf("cycle3") + " "); }
    else { cmd2.append("undefined" + " "); }
    
    
    ReturnValue runRet = RunTools.runCommand( new String[] { "bash", "-c", cmd3.toString() } );
    if (runRet.getExitStatus() != ReturnValue.SUCCESS || runRet.getProcessExitStatus() != ReturnValue.SUCCESS) {
      ret.setRunStopTstmp(new Date());
      ret.setStderr(runRet.getStderr());
      ret.setStdout(runRet.getStdout());
      ret.setExitStatus(runRet.getExitStatus());
      ret.setProcessExitStatus(runRet.getProcessExitStatus());
      return(ret);
    }
    
    runRet = RunTools.runCommand( new String[] { "bash", "-c", cmd.toString() } );
    if (runRet.getExitStatus() != ReturnValue.SUCCESS || runRet.getProcessExitStatus() != ReturnValue.SUCCESS) {
      ret.setRunStopTstmp(new Date());
      ret.setStderr(runRet.getStderr());
      ret.setStdout(runRet.getStdout());
      ret.setExitStatus(runRet.getExitStatus());
      ret.setProcessExitStatus(runRet.getProcessExitStatus());
      return(ret);
    }
    
    runRet = RunTools.runCommand( new String[] { "bash", "-c", cmd2.toString() } );
    if (runRet.getExitStatus() != ReturnValue.SUCCESS || runRet.getProcessExitStatus() != ReturnValue.SUCCESS) {
      ret.setRunStopTstmp(new Date());
      ret.setStderr(runRet.getStderr());
      ret.setStdout(runRet.getStdout());
      ret.setExitStatus(runRet.getExitStatus());
      ret.setProcessExitStatus(runRet.getProcessExitStatus());
      return(ret);
    }
    
    // record the file output
    FileMetadata fm = new FileMetadata();
    fm.setMetaType("text/key_value");
    fm.setFilePath(output);
    fm.setType("Performing per lane based statistics");
    fm.setDescription("Text file output of qc/qualStat module.");
    ret.getFiles().add(fm);
    
    FileMetadata fm2 = new FileMetadata();
    fm2.setMetaType("text/stat_summary");
    fm2.setFilePath(outSummary);
    fm2.setType("Key statistics extracted from per lane statistics");
    fm2.setDescription("Text file output of qc/qualStat module.");
    ret.getFiles().add(fm2);
    ret.setRunStopTstmp(new Date());
    
    return(ret);
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_output() {
    // just make sure the file exists
    ReturnValue runRet = FileTools.fileExistsAndNotEmpty(new File((String)options.valueOf("outfile")));
    if (runRet.getExitStatus() != ReturnValue.SUCCESS) {
      return(runRet);
    }
    return(FileTools.fileExistsAndNotEmpty(new File((String)options.valueOf("outSummary"))));
    
  }
  /**
   * {@inheritDoc}
   *
   * Optional:  Cleanup.  Remove tempDir.
   * Cleanup files that are outside the current working directory since Pegasus won't do that for you.
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
