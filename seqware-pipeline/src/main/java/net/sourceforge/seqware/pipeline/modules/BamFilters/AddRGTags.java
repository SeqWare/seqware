package net.sourceforge.seqware.pipeline.modules.BamFilters;

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
 * Annotate BAM file with read group tags.
 *
 * This module adds the read group (RG) tag to every read in a BAM file,
 * updates the header info, and sorts the entries by coordinate.
 *
 * Underlying script:  sw_module_AddRGTags.pl
 * Necessary programs:  perl, java, Picard (SamFormatConverter.jar & SortSam.jar), samtools
 *
 * Expected output:  outfile
 *
 * @author sacheek@med.unc.edu
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class AddRGTags extends Module {

  private OptionSet options = null;
  private File tempDir = null;
  
  /**
   * getOptionParser is an internal method to parse command line args.
   *
   * @return OptionParser this is used to get command line options
   */
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("infile", "Input BAM file").withRequiredArg();
    parser.accepts("outfile", "Output BAM file (with RG tags)").withRequiredArg();
    parser.accepts("RG", "read group tag").withRequiredArg();
    parser.accepts("perl", "Path to perl").withRequiredArg();
    parser.accepts("script", "Path to perl script: sw_module_AddRGTags.pl").withRequiredArg();
    parser.accepts("java", "Path to java").withRequiredArg();
    parser.accepts("PicardConvert", "Path to SamFormatConverter.jar").withRequiredArg();
    parser.accepts("PicardSort", "Path to SortSam.jar" ).withRequiredArg();
    parser.accepts("samtools", "Path to samtools").withRequiredArg();
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
    ret.setAlgorithm("AddRGTags");
    ret.setDescription("Add read group tags, update BAM header, sort by coordinate.");
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
        "infile", "outfile", "RG", "perl", "script", "java", "PicardConvert", "PicardSort", "samtools"
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
    
    // Does 'script' (sw_module_AddRGTags.pl) exist?
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
  	

    // Is 'samtools' executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("samtools"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("samtools"));
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
   * Based on script sw_module_AddRGtag.pl
   */
  @Override
  public ReturnValue do_run() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    ret.setRunStartTstmp(new Date());
    
    String output = (String)options.valueOf("outfile");
    String output2 = ((String)options.valueOf("outfile") + ".bai");
    StringBuffer cmd = new StringBuffer();
    cmd.append(options.valueOf("perl") + " " + options.valueOf("script") + " " + options.valueOf("infile") + " ");
    cmd.append(options.valueOf("outfile") + " " + options.valueOf("RG") + " " + options.valueOf("java") + " ");
    cmd.append(options.valueOf("PicardConvert")+ " " + options.valueOf("PicardSort") + " " + tempDir.getAbsolutePath()  + " " + options.valueOf("samtools"));
    
    RunTools.runCommand( new String[] { "bash", "-c", cmd.toString() } );
    
    // record the file output
    FileMetadata fm = new FileMetadata();
    fm.setMetaType("application/bam");
    fm.setFilePath(output);
    fm.setType("AddRGtags-BAM-output");
    fm.setDescription("BAM file output of AddRGTags module.");
    ret.getFiles().add(fm);
    
    FileMetadata fm2 = new FileMetadata();
    fm2.setMetaType("application/bam-index");
    fm2.setFilePath(output2);
    fm2.setType("ConvertT2G-BAI-output");
    fm2.setDescription("Index file (BAI) output of ConvertBAMTranscript2Genome module.");
    ret.getFiles().add(fm2);

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
    return(FileTools.fileExistsAndNotEmpty(new File((String)options.valueOf("outfile"))) );
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
