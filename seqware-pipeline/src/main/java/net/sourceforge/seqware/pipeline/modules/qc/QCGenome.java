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
 * To get alignment to QC genome, currenly, human rRNA and viral genome
 * 
 * 
 *  
 * Underlying script:  sw_module_RiboAndViralAln.pl (by Sara Grimm)
 *               
 * Necessary programs:  perl path to BWA
 *  
 * Expected output: "mock" alignment to qc genome, text file with key values
 * 
 * 
 * 
 * @author jyli@med.unc.edu
 *
 */
@ServiceProvider(service=ModuleInterface.class)
public class QCGenome extends Module {
  
  private OptionSet options = null;
  private File tempDir = null;
  
  /**
   * getOptionParser is an internal method to parse command line args.
   * 
   * @return OptionParser this is used to get command line options
   */  
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("fastq1", "fastq format of input reads: first reads in pair if paired end; all reads if single end").withRequiredArg();
    parser.accepts("fastq2", "fastq format of input reads: second reads in pair if paired end").withRequiredArg();
    parser.accepts("qcdbkey", "QC Genomes sequence key, 2 tab-delimited columns: sequence accession & type (e.g. rRNA, viral)").withRequiredArg();
    parser.accepts("qcdbfa", "Path to QC genome database in fasta format").withRequiredArg();
    parser.accepts("bwa", "Path to BWA").withRequiredArg();
    parser.accepts("perl", "Path to perl").withRequiredArg();
    parser.accepts("script", "Path to perl script: sw_module_RiboAndViralAln.pl").withRequiredArg();
    parser.accepts("outfile", "QC genome assessment: flowcell.lane.qcGenome.txt").withRequiredArg();
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
    ret.setAlgorithm("QCgenome");
    ret.setDescription("Getting qc genome assessment");
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
        "fastq1", "qcdbfa", "qcdbkey", "bwa", "outfile", "perl", "script"
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
    
        
    // Does input file ('fastq1') exist & is it readable?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("fastq1"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Input file " + (String)options.valueOf("fastq1") + " is not readable");
    }

    // If input file ('fastq2') is defined, does it exist & is it readable?  
    if (options.has("fastq2")) {
      if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("fastq2"))).getExitStatus() != ReturnValue.SUCCESS) {
        ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        ret.setStderr("Input file " + (String)options.valueOf("fastq2") + " not readable");
      }
    }
    
    // Does 'fastaDB' exist & is it readable?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("qcdbfa"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("QC genome sequence database file " + (String)options.valueOf("qcdbfa") + " is not readable");
    }
    
    
    // Does 'fastaDB' exist & is it readable?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("qcdbkey"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Genome sequence key file " + (String)options.valueOf("qcdbkey") + " is not readable");
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
    
    // Does 'script' (sw_module_BWA.pl) exist?
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
    cmd.append(options.valueOf("perl") + " " + options.valueOf("script") + " " + options.valueOf("fastq1") + " ");
    if (options.has("fastq2")) { cmd.append(options.valueOf("fastq2") + " "); }
    else {cmd.append("undefined" + " "); }
    cmd.append(options.valueOf("qcdbfa") + " "+ options.valueOf("qcdbkey") + " ");
    cmd.append(options.valueOf("outfile") + " " + options.valueOf("bwa")+ " " + tempDir.getAbsolutePath()); 
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
    fm.setMetaType("text/key_value");
    fm.setFilePath(output);
    fm.setType("Getting QC genome statistics");
    fm.setDescription("Text file output of QCGenome module.");
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
