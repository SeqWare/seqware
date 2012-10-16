package net.sourceforge.seqware.pipeline.modules.quantification;

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
 * Calculate counts per junction given a BAM file of aligned reads and a list of splice junctions.
 *
 * This module determines the raw read count for each given splice junction.
 *
 * Underlying script:  sw_module_SpliceJunctionCounts.pl
 *
 * Necessary programs:  perl, java, Picard (SamFormatConverter.jar)
 *
 * Necessary data file:  'junclist', which is a tab-delimited text file containing the list of splice junctions to be evaluated.
 * Only the first column is used -- any additional columns (containing annotation, for example) are ignored by this module.
 * Expected format for an exon is "chrN:position1,chrN:position2" or "chrN:position1:strand,chrN:position2:strand".  The
 * strand information is optional and not used by this module. The script does not care if position1 is less than postion2
 * (or vice versa), it will handle either case.  The script is designed to handle only intra-chromosomal junctions.
 * (e.g. ~/seqware-pipeline/data/annotation_reference/hg19_transcripts.hg19.20091027.spljxn.txt)
 *
 * Expected output:  ~.spljxn.quantification.txt
 * This is a tab-delimited files, 2 columns:  junction & counts.
 *
 * LIMITATIONS: The BAM file must contain reads mapped to genomic coordinates.
 * LIMITATIONS: Is not designed to handle inter-chromosomal junctions.
 *
 * @author sacheek@med.unc.edu
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class SpliceJunctionCounts extends Module {

  private OptionSet options = null;
  private File tempDir = null;
  
  /**
   * getOptionParser is an internal method to parse command line args.
   *
   * @return OptionParser this is used to get command line options
   */
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("infile", "Input BAM file, expects reads aligned to genomic coordinates.").withRequiredArg();
    parser.accepts("outSJ", "Output file for splice junction summary (raw read counts).").withRequiredArg();
    parser.accepts("junclist", "tab-delimited text file specifying junctions to evaluate; only first column is used; " +
    		"expected exon format = chrN:positionA,chrN:positionB or chrN:positionA:strand,chrN:positionB:strand (positionA " +
    		"does not need to be less than positionB); designed to handle only intra-chromosomal junctions").withRequiredArg();
    parser.accepts("perl", "Path to perl").withRequiredArg();
    parser.accepts("script", "Path to perl script: sw_module_SpliceJunctionCounts.pl").withRequiredArg();
    parser.accepts("java", "Path to java").withRequiredArg();
    parser.accepts("PicardConvert", "Path to SamFormatConverter.jar").withRequiredArg();
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
    ret.setAlgorithm("SpliceJunctionCounts");
    ret.setDescription("Calculates raw read counts per junction given BAM file and list of splice junctions.");
    ret.setVersion("0.7.0");
    
    try {
      OptionParser parser = getOptionParser();
      // The parameters object is actually an ArrayList of Strings created
      // by splitting the command line options by space. JOpt expects a String[]
      options = parser.parse(this.getParameters().toArray(new String[0]));
      // create a temp directory in current working directory
      tempDir = FileTools.createTempDirectory(new File("."));
      // you can write to "stdout" or "stderr" which will be persisted back to the DB
      ret.setStdout(ret.getStdout()+"Output: "+(String)options.valueOf("outSJ")+"\n");
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
        "infile", "outSJ", "junclist", "perl", "script", "java", "PicardConvert"
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
    
    // Does input file exist & is it readable?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("infile"))).getExitStatus() != ReturnValue.SUCCESS) {
    	ret.setExitStatus(ReturnValue.FILENOTREADABLE);
    	ret.setStderr("Input file is not readable");
    }
    
    // Is output file path writable (for both outputs)?
    File output1 = new File((String) options.valueOf("outSJ"));
    if (FileTools.dirPathExistsAndWritable(output1.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory of " + (String)options.valueOf("outSJ"));
    }
    
    // Does exonlist exist & is it readable?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("junclist"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Required data file (exonlist) is not readable");
    }
    
    // Is 'perl' executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("perl"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("perl"));
    }
    
    // Does 'script' (sw_module_SpliceJunctionCounts.pl) exist?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("script"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Could not find script at "+(String)options.valueOf("script"));
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
   * Based on script sw_module_GeneCountsRPKM.pl
   */
  @Override
  public ReturnValue do_run() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    ret.setRunStartTstmp(new Date());
    
    String outputC = (String)options.valueOf("outSJ");
    StringBuffer cmd = new StringBuffer();
    cmd.append(options.valueOf("perl") + " " + options.valueOf("script") + " " + options.valueOf("infile") + " ");
    cmd.append(options.valueOf("outSJ") + " " + options.valueOf("junclist") + " " + options.valueOf("java") + " ");
    cmd.append(options.valueOf("PicardConvert")+ " " + tempDir.getAbsolutePath());
    
    RunTools.runCommand( new String[] { "bash", "-c", cmd.toString() } );

    // record the file output
    FileMetadata fm1 = new FileMetadata();
    fm1.setMetaType("text/junction-quant");
    fm1.setFilePath(outputC);
    fm1.setType("SplJxn-Counts");
    fm1.setDescription("Tab-delimited text file of splice junction quantification.");
    ret.getFiles().add(fm1);
    
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
    // just make sure the files exist
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    if ((FileTools.fileExistsAndNotEmpty(new File((String) options.valueOf("outSJ"))).getExitStatus() != ReturnValue.SUCCESS)) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Expected output is empty or does not exist");
    }
    return(ret);
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
