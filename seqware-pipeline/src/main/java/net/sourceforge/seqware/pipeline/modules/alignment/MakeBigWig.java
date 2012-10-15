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
 * Generate bigWig file from a BAM file.
 *
 * This module takes as input a BAM file of aligned reads (sorted, aligned to genomic
 * coordinates).  It uses samtools pileup to get counts per genomic position, builds a
 * bedGraph file, then converts the bedGraph to a bigWig file.
 *
 * Underlying script:  sw_module_MakeBigWig.pl
 * Necessary programs:  perl, samtools, bedGraphToBigWig
 *
 * Input:  BAM file, chrom.sizes (output of UCSC binary tool 'fetchChromSizes')
 *
 * Expected output:  outfile
 *
 * @author sacheek@med.unc.edu
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class MakeBigWig extends Module {

  private OptionSet options = null;
  private File tempDir = null;
  
  /**
   * getOptionParser is an internal method to parse command line args.
   *
   * @return OptionParser this is used to get command line options
   */
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("bamfile", "input: sorted BAM, reads aligned to genomic coordinates").withRequiredArg();
    parser.accepts("outfile", "output: bigWig").withRequiredArg();
    parser.accepts("chrsizes", "chrom.sizes for genome to which reads are aligned; output for UCSC binary " +
    		"tool fetchChromSizes").withRequiredArg();
    parser.accepts("samtools", "Path to samtools").withRequiredArg();
    parser.accepts("bedGraphToBigWig", "Path to UCSC binary tool bedGraphToBigWig").withRequiredArg();
    parser.accepts("perl", "Path to perl").withRequiredArg();
    parser.accepts("script", "Path to perl script: sw_module_MakeBigWig.pl").withRequiredArg();
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
    ret.setAlgorithm("MakeBigWig");
    ret.setDescription("Make bigWig given sorted BAM (reads aligned to genome).");
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
        "bamfile", "outfile", "chrsizes", "samtools", "bedGraphToBigWig", "perl", "script"
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
    
    // Does input file ('bamfile') exist & is it readable?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("bamfile"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Input file " + (String)options.valueOf("bamfile") + " is not readable");
    }
    
    // Does 'chrsizes' exist & is it readable?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("chrsizes"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Chromosome sizes (chrom.sizes) file " + (String)options.valueOf("chrsizes") + " is not readable");
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
    
    // Does 'script' (sw_module_MakeBigWig.pl) exist?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("script"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Could not find script at "+(String)options.valueOf("script"));
    }

    // Is 'bedGraphToBigWig' executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("bedGraphToBigWig"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("bedGraphToBigWig"));
    }

    // Is 'samtools' executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("samtools"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("samtools"));
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
   * Based on script sw_module_MakeBigWig.pl
   */
  @Override
  public ReturnValue do_run() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    ret.setRunStartTstmp(new Date());
    
    String output = (String)options.valueOf("outfile");
    StringBuffer cmd = new StringBuffer();
    cmd.append(options.valueOf("perl") + " " + options.valueOf("script") + " " + options.valueOf("bamfile") + " ");
    cmd.append(options.valueOf("outfile") + " " + options.valueOf("chrsizes") + " " + options.valueOf("samtools") + " ");
    cmd.append(options.valueOf("bedGraphToBigWig")+ " " + tempDir.getAbsolutePath());
    
    RunTools.runCommand( new String[] { "bash", "-c", cmd.toString() } );
    
    // record the file output
    FileMetadata fm = new FileMetadata();
    fm.setMetaType("application/bigWig");
    fm.setFilePath(output);
    fm.setType("MakeBigWig-output");
    fm.setDescription("bigWig file output of MakeBigWig module.");
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
    return(FileTools.fileExistsAndReadable(new File((String)options.valueOf("outfile"))));
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
