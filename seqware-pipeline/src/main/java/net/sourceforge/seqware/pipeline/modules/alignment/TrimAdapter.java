package net.sourceforge.seqware.pipeline.modules.alignment;

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
 * Trim adapter segment (and any following sequence) from reads.
 * 
 * This module takes a given fastq file and trims all reads to eliminate any adapter
 * contamination.  The 'adapter' is the reverse complement of the sequencing primer,
 * which the user must provide.  A non-A/C/G/T base in the primer is converted to an N,
 * and all Ns in the adapter are considered matches to any base in the read.  (The opposite
 * case is not true -- Ns in read sequences are NOT considered matches to the adapter bases.)
 * Otherwise, no mis-matches are allowed.  The minimum length for an adapter/read match is
 * 5 bases.  The script checks for internal & terminal adapter segments.  The trimmed read 
 * (& trimmed base scores) are output in fastq format, unless the effective read length is 
 * zero [an adapter dimer].  A collection of basic trimming statistics is an optional
 * output as well.
 * 
 * Underlying script:  sw_module_TrimAdapter.pl
 * Necessary programs:  perl
 * 
 * Expected output:  outfastq (required), outstats (optional)
 * 
 * @author sacheek@med.unc.edu
 *
 */
@ServiceProvider(service=ModuleInterface.class)
public class TrimAdapter extends Module {

  private OptionSet options = null;
  private File tempDir = null;
  
  /**
   * getOptionParser is an internal method to parse command line args.
   * 
   * @return OptionParser this is used to get command line options
   */  
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("infastq", "input: fastq format of reads").withRequiredArg();
    parser.accepts("primerseq", "sequencing primer; adapter is reverse complement of this sequence").withRequiredArg();
    parser.accepts("outfastq", "required output: fastq format of trimmed reads").withRequiredArg();
    parser.accepts("outstats", "optional output: some trimming statistics (distribution of effective read lengths, removed reads, etc)").withRequiredArg();
    parser.accepts("perl", "Path to perl").withRequiredArg();
    parser.accepts("script", "Path to perl script: sw_module_TrimAdapter.pl").withRequiredArg();
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
    ret.setAlgorithm("TrimAdapter");
    ret.setDescription("Trim reads to remove adapter segments before alignment.");
    ret.setVersion("0.7.0");
    
    try {
      OptionParser parser = getOptionParser();
      // The parameters object is actually an ArrayList of Strings created
      // by splitting the command line options by space. JOpt expects a String[]
      options = parser.parse(this.getParameters().toArray(new String[0]));
      // create a temp directory in current working directory
      tempDir = FileTools.createTempDirectory(new File("."));
      // you can write to "stdout" or "stderr" which will be persisted back to the DB
      ret.setStdout(ret.getStdout()+"Output: "+(String)options.valueOf("outfastq")+"\n");
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
        "infastq", "primerseq", "outfastq", "perl", "script"
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
    
    // Does input file ('infastq') exist & is it readable?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("infastq"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Input file " + (String)options.valueOf("infastq") + " is not readable");
    }
    
    // Is output file path writable (for 'outfastq')?
    File output1 = new File((String) options.valueOf("outfastq"));
    if (FileTools.dirPathExistsAndWritable(output1.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory");
    }
   
    // Is output file path writable (for 'outstats', if defined)? 
    if (options.has("outstats")) {
      File output2 = new File((String) options.valueOf("outstats"));
      if (FileTools.dirPathExistsAndWritable(output2.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
        ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
        ret.setStderr("Can't write to output directory");
      }
    }
    
    // Is 'perl' executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("perl"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("perl"));
    }
    
    // Does 'script' (sw_module_TrimAdapter.pl) exist?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("script"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Could not find script at "+(String)options.valueOf("script"));
    }
    
    // Is tempDir writeable?
    if (FileTools.dirPathExistsAndWritable(tempDir).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to temp directory");
    }
  	
    return (ret);

  }
  
  /**
   * Optional:  Test program on a known dataset.  Not implemented in this module.
   * 
   * @return a ReturnValue object
   */
  @Override
  public ReturnValue do_test() {
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.NOTIMPLEMENTED);
    return(ret);
  }
  
  /**
   * Run core of module.
   * Based on script sw_module_TrimAdapter.pl
   * 
   * @return a ReturnValue object
   */
  @Override
  public ReturnValue do_run() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    ret.setRunStartTstmp(new Date());
    
    StringBuffer cmd = new StringBuffer();
    cmd.append(options.valueOf("perl") + " " + options.valueOf("script") + " " + options.valueOf("primerseq"));
    cmd.append(" " + options.valueOf("infastq") + " " + options.valueOf("outfastq") + " ");
    if (options.has("outstats")) { cmd.append(options.valueOf("outstats")); }
    else { cmd.append("undefined"); }
    
    RunTools.runCommand( new String[] { "bash", "-c", cmd.toString() } );
    
    // record the file output
    String outputF = (String)options.valueOf("outfastq");
    FileMetadata fm1 = new FileMetadata();
    fm1.setMetaType("chemical/seq-na-fastq");
    fm1.setFilePath(outputF);
    fm1.setType("TrimAdapter-fastq");
    fm1.setDescription("Trimmed reads in fastq format.");
    ret.getFiles().add(fm1);
    
    if (options.has("outstats")) {
      String outputS = (String)options.valueOf("outstats");
      FileMetadata fm2 = new FileMetadata();
      fm2.setMetaType("text/plain");
      fm2.setFilePath(outputS);
      fm2.setType("TrimAdapter-stats");
      fm2.setDescription("Trim statistics from TrimAdapter module.");
      ret.getFiles().add(fm2);
    }
    
    ret.setRunStopTstmp(new Date());
    return(ret);
  }
  
  /**
   * Check to make sure the output was created correctly.
   * 
   * @return a ReturnValue object
   */
  @Override
  public ReturnValue do_verify_output() {
    // just make sure the file exists
    return(FileTools.fileExistsAndReadable(new File((String)options.valueOf("outfastq"))));
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
    if (!tempDir.delete()) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't delete folder: "+tempDir.getAbsolutePath());
    }
    return(ret);
  }
  
}
