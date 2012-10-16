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
 * Align reads via BWA.
 *
 * This module launches a BWA alignment of given fastq file(s) against a given reference
 * sequence database.  This reference database is assumed to be already indexed.  Output
 * is a sorted BAM file of aligned reads.
 *
 * Underlying script:  sw_module_BWA.pl
 * Necessary programs:  BWA, perl, java, Picard (SamFormatConverter.jar & SortSam.jar)
 *
 * Expected output:  outfile
 *
 * @author sacheek@med.unc.edu
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class BWA extends Module {

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
    parser.accepts("outfile", "Output BAM file (reads aligned & sorted by coordinate)").withRequiredArg();
    parser.accepts("fastaDB", "Path to pre-indexed reference sequence database in fasta format").withRequiredArg();
    parser.accepts("bwa", "Path to BWA").withRequiredArg();
    parser.accepts("perl", "Path to perl").withRequiredArg();
    parser.accepts("script", "Path to perl script: sw_module_BWA.pl").withRequiredArg();
    parser.accepts("java", "Path to java").withRequiredArg();
    parser.accepts("PicardConvert", "Path to SamFormatConverter.jar").withRequiredArg();
    parser.accepts("PicardSort", "Path to SortSam.jar" ).withRequiredArg();
    parser.accepts("bwa-threads", "Number of threads for use with 'bwa aln'." ).withRequiredArg();
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
    ret.setAlgorithm("BWA");
    ret.setDescription("Align reads via BWA & sort by coordinate.");
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
        "fastq1", "outfile", "fastaDB", "bwa", "perl", "script", "java", "PicardConvert", "PicardSort", "bwa-threads"
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
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("fastaDB"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Sequence database file " + (String)options.valueOf("fastaDB") + " is not readable");
    }
    
    // Does the index for 'fastaDB' exist & is it readable?
    if (FileTools.fileExistsAndReadable(new File((String)options.valueOf("fastaDB")+".rsa")).getExitStatus() != ReturnValue.SUCCESS || 
        FileTools.fileExistsAndReadable(new File((String)options.valueOf("fastaDB")+".sa")).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Index files for sequence database " + (String)options.valueOf("fastaDB") + " is not readable");
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

    // Is 'java' executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("java"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("java"));
    }
    
    // Does 'PicardConvert' (SamFormatConverter.jar) exist?
  	if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("PicardConvert"))).getExitStatus() != ReturnValue.SUCCESS) {
    	ret.setExitStatus(ReturnValue.FILENOTREADABLE);
    	ret.setStderr("Could not find SamFormatConverter.jar at "+(String)options.valueOf("PicardConvert"));
    }
    	
    // Does 'PicardSort' (SamSort.jar) exist?
  	if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("PicardSort"))).getExitStatus() != ReturnValue.SUCCESS) {
    	ret.setExitStatus(ReturnValue.FILENOTREADABLE);
    	ret.setStderr("Could not find SamSort.jar at "+(String)options.valueOf("PicardSort"));
    }
    
  	// Is tempDir writeable?
    if (FileTools.dirPathExistsAndWritable(tempDir).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to temp directory");
    }
  	
    return (ret);

  }
  
  /**
   * {@inheritDoc}
   *
   * Optional:  Test program on a known dataset.  Not implemented in this module.
   */
  @Override
  public ReturnValue do_test() {
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.NOTIMPLEMENTED);
    return(ret);
  }
  
  /**
   * {@inheritDoc}
   *
   * Run core of module.
   * Based on script sw_module_BWA.pl
   */
  @Override
  public ReturnValue do_run() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    ret.setRunStartTstmp(new Date());
    
    String output = (String)options.valueOf("outfile");
    StringBuffer cmd = new StringBuffer();
    cmd.append(options.valueOf("perl") + " " + options.valueOf("script") + " " + options.valueOf("fastq1") + " ");
    if (options.has("fastq2")) { cmd.append(options.valueOf("fastq2") + " "); }
    else { cmd.append("undefined" + " "); }
    cmd.append(options.valueOf("outfile") + " " + options.valueOf("fastaDB") + " " + options.valueOf("bwa") + " " + options.valueOf("java") + " ");
    cmd.append(options.valueOf("PicardConvert")+ " " + options.valueOf("PicardSort") + " " + tempDir.getAbsolutePath());
    cmd.append(" " + options.valueOf("bwa-threads"));
    
    RunTools.runCommand( new String[] { "bash", "-c", cmd.toString() } );
    
    // record the file output
    FileMetadata fm = new FileMetadata();
    fm.setMetaType("application/bam");
    fm.setFilePath(output);
    fm.setType("BWA-BAM-output");
    fm.setDescription("BAM file output of BWA module.");
    ret.getFiles().add(fm);
    
    ret.setRunStopTstmp(new Date());
    return(ret);
  }
  
  /**
   * {@inheritDoc}
   *
   * Check to make sure the output was created correctly.
   */
  @Override
  public ReturnValue do_verify_output() {
    // just make sure the file exists
    return(FileTools.fileExistsAndNotEmpty(new File((String)options.valueOf("outfile"))));
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
    if (!tempDir.delete()) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't delete folder: "+tempDir.getAbsolutePath());
    }
    return(ret);
  }
  
}
