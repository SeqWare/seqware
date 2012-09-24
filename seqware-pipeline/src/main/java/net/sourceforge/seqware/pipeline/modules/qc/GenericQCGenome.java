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
 * Estimate % of reads in a given lane that align to a specified set of sequence databases.
 * 
 * This module takes a sampling of reads (1%, selected randomly) of a given lane and attempts
 * alignments against a series of sequence databases.  The output is a text file giving the number
 * of evaluated reads and the %aligned to each of the user-specified list of sequence databases.
 * There are two alignment options:  BWA or miRNA.  The first option runs BWA using the user-specified
 * sequence file as the reference.  The second option is meant for analysis of miRNA.  This second 
 * option will essentially scan each read for perfect matches to the reference sequence.  (The "miRNA"
 * alignment type is meant for miRNA analysis, but can be used for any cases where you wish to evaluate
 * reference sequences that may be smaller than the read length.)  The alignment type should be specified 
 * by the user in the 3rd column of the "DBlist" input file.
 * 
 * Underlying script:  sw_module_GenericQC.pl
 * Necessary programs: perl, BWA
 * 
 * Input criteria:
 * 1)  input reads in FASTQ format
 * 2)  "DBlist" -- file specifying sequence databases to compare against;
 *     format = 3 columns, tab-delimited: DB name, path to sequence file in FASTA format, alignment type [BWA or miRNA]
 * 3)  output file name
 * 4)  path to BWA
 * 5)  path to perl
 * 
 * IMPORTANT NOTE:  For any sequence DB that is to be evaluated by BWA (i.e. alignment type "BWA" is 
 * specified in the input list), the FASTA file *MUST* already be indexed for BWA.
 * 
 * Another note:  For paired end lanes (with two FASTQ files per lane), simply choose one or other for evaluation.
 * Since this is not an exhaustive comparison (only 1% of reads considered), evaluating one FASTQ per lane should
 * be sufficient, unless some specific problem is suspected.
 * 
 * Expected output:  tab-delimited file giving %aligned to each user-specified sequence DB; the first line gives
 * the total number of reads that were selected in the sampling process
 *
 * @author sacheek@med.unc.edu
 *
 */
@ServiceProvider(service=ModuleInterface.class)
public class GenericQCGenome extends Module {
  
  private OptionSet options = null;
  private File tempDir = null;
  
  /**
   * getOptionParser is an internal method to parse command line args.
   * 
   * @return OptionParser this is used to get command line options
   */  
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("fastq", "fastq format of input reads").withRequiredArg();
    parser.accepts("DBlist", "tab-delimited text file specifying reference sequence DBs; 3 columns = DB name, path to FASTA, " +
    		"alignment type [BWA or miRNA]; note that for BWA alignments, the script assumes the FASTA is already BWA-indexed").withRequiredArg();
    parser.accepts("outfile", "tab-delimited text file, %aligned to each sequence DB").withRequiredArg();
    parser.accepts("bwa", "Path to BWA").withRequiredArg();
    parser.accepts("perl", "Path to perl").withRequiredArg();
    parser.accepts("script", "Path to perl script: sw_module_GenericQC.pl").withRequiredArg();
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
    ret.setAlgorithm("GenericQCgenomes");
    ret.setDescription("Alignment against various reference sequence sets");
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
        "fastq", "DBlist", "outfile", "bwa", "perl", "script"
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
        
    // Does input file ('fastq') exist & is it readable?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("fastq"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Input file " + (String)options.valueOf("fastq") + " is not readable");
    }
    
    // Does 'DBlist' exist & is it readable?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("DBlist"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Input file " + (String)options.valueOf("DBlist") + " is not readable");
    }
    
    // Is output file path writable?
    File output = new File((String) options.valueOf("outfile"));
    if (FileTools.dirPathExistsAndWritable(output.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory");
    }
   
    // Is 'perl' executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("perl"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("perl"));
    }
    
    // Does 'script' (sw_module_GenericQC.pl) exist?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("script"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Could not find script at "+(String)options.valueOf("script"));
    }

    // Is 'bwa' executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("bwa"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("bwa"));
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
  
  
  @Override
public ReturnValue do_run() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    ret.setRunStartTstmp(new Date());
    String output = (String)options.valueOf("outfile");
    StringBuffer cmd = new StringBuffer();
    cmd.append(options.valueOf("perl") + " " + options.valueOf("script") + " " + options.valueOf("fastq") + " ");
    cmd.append(options.valueOf("DBlist") + " "+ options.valueOf("outfile") + " ");
    cmd.append(options.valueOf("bwa")+ " " + tempDir.getAbsolutePath()); 
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
    FileMetadata fm = new FileMetadata();
    fm.setMetaType("text/key-value");
    fm.setFilePath(output);
    fm.setType("GenericQCGenome-text");
    fm.setDescription("Text file giving %aligned to various sequence sets.");
    ret.getFiles().add(fm);
    ret.setRunStopTstmp(new Date());
    return(ret);
  }

  @Override
  public ReturnValue do_verify_output() {
    // just make sure the file exists
    return(FileTools.fileExistsAndNotEmpty(new File((String)options.valueOf("outfile"))));
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
