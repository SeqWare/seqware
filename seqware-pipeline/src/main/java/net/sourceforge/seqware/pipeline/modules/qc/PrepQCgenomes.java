package net.sourceforge.seqware.pipeline.modules.qc;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
 * Prepare the "QC Genomes" database from external sources.
 * 
 * This module makes a FASTA file and a KEY file for human rRNA and complete viral genomes.
 * The wget tool is used to extract the following files:
 * a) http://www.arb-silva.de/no_cache/download/archive/release_102/Exports/lsu-parc.fasta.tgz
 * b) http://www.arb-silva.de/no_cache/download/archive/release_102/Exports/ssu-parc.fasta.tgz
 * c) ftp://ftp.ncbi.nih.gov/genomes/Viruses/all.fna.tar.gz
 * 
 * IMPORTANT NOTE:  The resource files are currently hard-coded into the underlying script!!!

 * Another Note:  I've found SILVA to be extremely quick to block multiple 'wget' attempts, so I'd advise users avoid 
 * running this module more than once in a short time span.
 * 
 * Underlying script:  sw_module_PrepQCgenomes.pl
 * Necessary programs:  perl
 * 
 * Expected output:  3 files.  Although the user must supply the output directory name, the actual file are fixed.
 * (DATE is determined via java.util.Date)
 * 1)  {outputDIR}/QCgenomes.DATE.log -- A brief log of the database construction process.
 * 2)  {outputDIR}/QCgenomes.DATE.fa -- The extracted human rRNA & viral sequences in FASTA format.
 * 3)  {outputDIR}/QCgenomes.DATE.key -- 2 column, tab-delimited: sequence accession & sequence type (i.e. rRNA, viral) 
 * 
 * @author sacheek@med.unc.edu
 *
 */
@ServiceProvider(service=ModuleInterface.class)
public class PrepQCgenomes extends Module {

  private OptionSet options = null;
  
  Date now = new Date();
  DateFormat dfm = new SimpleDateFormat("yyyyMMdd");
//  DateFormat dfm.setTimeZone(TimeZone.getTimeZone("America/New_York"));
  String thisdate = dfm.format(now);
  
  /**
   * getOptionParser is an internal method to parse command line args.
   * 
   * @return OptionParser this is used to get command line options
   */  
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("outputDIR", "directory for ~.fa and ~.key output files").withRequiredArg();
    parser.accepts("perl", "Path to perl").withRequiredArg();
    parser.accepts("script", "Path to perl script: sw_module_PrepQCgenomes.pl").withRequiredArg();
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
    ret.setAlgorithm("PrepQCgenomes");
    ret.setDescription("Make datafiles files for QC Genomes sequences (human rRNA + viral genomes).");
    ret.setVersion("0.7.0");

    try {
      OptionParser parser = getOptionParser();
      // The parameters object is actually an ArrayList of Strings created
      // by splitting the command line options by space. JOpt expects a String[]
      options = parser.parse(this.getParameters().toArray(new String[0]));
      // create a temp directory in current working directory
      // tempDir = FileTools.createTempDirectory(new File("."));
      // you can write to "stdout" or "stderr" which will be persisted back to the DB
      ret.setStdout(ret.getStdout()+"Output: "+(String)options.valueOf("outputDIR")+"/QCgenomes."+(String)thisdate+".log\nOutput: "+
          (String)options.valueOf("outputDIR")+"/QCgenomes."+(String)thisdate+".fa\nOutput: "+(String)options.valueOf("outputDIR")+
          "/QCgenomes."+(String)thisdate+".key\n");
    } catch (OptionException e) {
      e.printStackTrace();
      ret.setStderr(e.getMessage());
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
    } //catch (IOException e) {
      //e.printStackTrace();
      //ret.setStderr(e.getMessage());
      //ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
    //}
    
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
        "outputDIR", "perl", "script"
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
   
    // Is output directory writable ('outputDIR')? 
    File outdir = new File((String) options.valueOf("outputDIR"));
    if (FileTools.dirPathExistsAndWritable(outdir).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory");
    }
    
    // Is 'perl' executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("perl"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("perl"));
    }
    
    // Does 'script' (sw_module_PrepQCgenomes.pl) exist?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("script"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Could not find script at "+(String)options.valueOf("script"));
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
   * Based on script sw_module_PrepQCgenomes.pl
   * 
   * @return a ReturnValue object
   */
  @Override
  public ReturnValue do_run() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    ret.setRunStartTstmp(new Date());
    String outputL = (String)options.valueOf("outputDIR")+"/QCgenomes."+(String)thisdate+".log";
    String outputF = (String)options.valueOf("outputDIR")+"/QCgenomes."+(String)thisdate+".fa";
    String outputK = (String)options.valueOf("outputDIR")+"/QCgenomes."+(String)thisdate+".key";
    
    StringBuffer cmd = new StringBuffer();
    cmd.append(options.valueOf("perl") + " " + options.valueOf("script") + " " + (String)thisdate);
    cmd.append(" " + options.valueOf("outputDIR"));
    
    RunTools.runCommand( new String[] { "bash", "-c", cmd.toString() } );
    
    // record the file output
    FileMetadata fm1 = new FileMetadata();
    fm1.setMetaType("text/plain");
    fm1.setFilePath(outputL);
    fm1.setType("QCgenomes-log");
    fm1.setDescription("Brief database construction log.");
    ret.getFiles().add(fm1);
    
    FileMetadata fm2 = new FileMetadata();
    fm2.setMetaType("chemical/seq-na-fasta");
    fm2.setFilePath(outputF);
    fm2.setType("QCgenomes-fasta");
    fm2.setDescription("QC Genomes sequences in fasta format.");
    ret.getFiles().add(fm2);
    
    FileMetadata fm3 = new FileMetadata();
    fm3.setMetaType("text/plain");
    fm3.setFilePath(outputK);
    fm3.setType("QCgenomes-key");
    fm3.setDescription("QC Genomes accession & sequence type.");
    ret.getFiles().add(fm3);
    
    
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
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    File outputL = new File((String)options.valueOf("outputDIR")+"/QCgenomes."+(String)thisdate+".log");
    File outputF = new File((String)options.valueOf("outputDIR")+"/QCgenomes."+(String)thisdate+".fa");
    File outputK = new File((String)options.valueOf("outputDIR")+"/QCgenomes."+(String)thisdate+".key");
    if ((FileTools.fileExistsAndNotEmpty(outputL).getExitStatus() != ReturnValue.SUCCESS) || 
        (FileTools.fileExistsAndNotEmpty(outputF).getExitStatus() != ReturnValue.SUCCESS) ||
        (FileTools.fileExistsAndNotEmpty(outputK).getExitStatus() != ReturnValue.SUCCESS)) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Expected output is empty or does not exist");
    }
    return(ret);
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
    return(ret);
  }
  
}
