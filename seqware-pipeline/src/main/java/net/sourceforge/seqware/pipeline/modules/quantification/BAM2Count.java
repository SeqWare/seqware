package net.sourceforge.seqware.pipeline.modules.quantification;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

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
 * Get gene level count from BAM file.
 *
 * This module takes input BAM file, converts to SAM, filters unmapped,
 * gets counts on the transcript level and collapses onto gene level
 * using comboID
 *
 * Underlying script:   BAM2count.pl
 * Necessary programs:  perl, java, Picard (SamFormatConverter.jar)
 *
 * Dependency: /datastore/nextgenproc/analysis/alignment_databases/custom_refseq_length_hg19.txt
 *             /datastore/nextgenproc/analysis/alignment_databases/median_isoform_customeRefseq_length_hg19.txt
 *             /home/jyli/NextGenSeq/java_stuff/picard-tools-1.19/
 *
 * Expected output:  sample_count.txt
 *
 * @author jyli@med.unc.edu
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class BAM2Count extends Module {

  private OptionSet options = null;
  private File tempDir = null;
  
  /**
   * getOptionParser is an internal method to parse command line args.
   *
   * @return OptionParser this is used to get command line options
   */
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("outfile", "Out put count file").withRequiredArg();
    parser.accepts("bamInput", "BAM input file").withRequiredArg();
    parser.accepts("trxDB", "Path to transcript database IDs").withRequiredArg();
    parser.accepts("medianTrxDB", "Path to median length info of transcript database").withRequiredArg();
    parser.accepts("perl", "Path to perl").withRequiredArg();
    parser.accepts("script", "Path to perl script: sw_module_BAM2count.pl").withRequiredArg();
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
    ret.setAlgorithm("BAM2Count");
    ret.setDescription("Parse BAM file and get count.");
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
    
    // To check that all the parameters are provided
    for (String option : new String[] {
         "outfile", "bamInput", "trxDB", "medianTrxDB", "perl", "script", "java", "PicardConvert"
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
    
       
    // Does input file ('bamInput') exist & is it readable?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("bamInput"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Input file " + (String)options.valueOf("bamInput") + " is not readable");
    }

       
    // Does 'trxDB' exist & is it readable?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("trxDB"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Transcript database file " + (String)options.valueOf("trxDB") + " is not readable");
    }
    
      // Does the index for 'medianTrxDB' exist & is it readable?  
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("medianTrxDB"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Median length isoform file " + (String)options.valueOf("medianTrxDB") + " is not readable");
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
    
    // Does 'script' (sw_module_BAM2Count.pl) exist and readable?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("script"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Could not find sw_module_BWA.pl at "+(String)options.valueOf("script"));
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
   * Based on script sw_module_BAM2Count.pl
   */
  @Override
  public ReturnValue do_run() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    String output = (String)options.valueOf("outfile");
    StringBuffer cmd = new StringBuffer();
    cmd.append(options.valueOf("perl") + " " + options.valueOf("script") + " " + options.valueOf("bamInput") + " ");
    cmd.append(options.valueOf("trxDB") + " " + options.valueOf("medianTrxDB") + " " + options.valueOf("outfile") + " " + tempDir.getAbsolutePath()+ " " );//+ tempDir);
    cmd.append(options.valueOf("java") + " " + options.valueOf("PicardConvert"));
   // cmd.append(options.valueOf("PicardConvert")+ " " + options.valueOf("PicardSort") + " " + tempDir.getAbsolutePath());
    
    RunTools.runCommand( new String[] { "bash", "-c", cmd.toString() } );
    
    // record the file output
    FileMetadata fm = new FileMetadata();
    fm.setMetaType("application/bam2count");
    fm.setFilePath(output);
    fm.setType("tab-delimit-output");
    fm.setDescription("Count.txt file output of BAM2Count module.");
    ret.getFiles().add(fm);
    
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
