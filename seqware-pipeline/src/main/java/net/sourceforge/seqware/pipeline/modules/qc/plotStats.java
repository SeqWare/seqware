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
 * Depends on qualStat module, parse sample_stat.txt and
 * generates boxplot sample_bxp.png for base calling scores
 * and per base/cycle % nucleotide distribution sample_nt_distr.png
 *
 * Underlying script:  /usr/bin/fastq_nucleotide_distribution_graph.sh
 *                     /usr/bin/fastq_quality_boxplot_graph.sh
 *
 * Dependency:  fastx_toolkits from http://hannonlab.cshl.edu/fastx_toolkit/commandline.html
 *
 * Expected output:  sample_bxp.png & sample_nt_distr.png
 *
 *  *
 *
 * @author jyli@med.unc.edu
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class plotStats extends Module {
  
  private OptionSet options = null;
  private File tempDir = null;
  
  /**
   * getOptionParser is an internal method to parse command line args.
   *
   * @return OptionParser this is used to get command line options
   */
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("fastxBoxplot", "/usr/bin/fastq_quality_boxplot_graph.sh").withRequiredArg();
    parser.accepts("fastxNTdistribution", "/usr/bin/fastx_nucleotide_distribution_graph.sh").withRequiredArg();
    parser.accepts("infile", "fastq format Illumina raw file").withRequiredArg();
    parser.accepts("boxplot", "Per base calling scores box plot, sample_bxp.png").withRequiredArg();
    parser.accepts("ntDistribution", "Per base/cycle % nucleotide distribution sample_nt_distr.png").withRequiredArg();
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
    ret.setAlgorithm("plotStats");
    ret.setDescription("Per lane based statistics plottings");
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
    "fastxBoxplot", "fastxNTdistribution", "infile", "boxplot","ntDistribution"
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
    
    // Is /usr/bin/fastq_quality_boxplot_graph.sh executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("fastxBoxplot"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("fastxBoxplot"));
    }
    
    
    // Is /usr/bin/fastq_nucleotide_distribution_graph.sh executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("fastxNTdistribution"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("fastxNTdistribution"));
    }
    
    
    if (FileTools.fileExistsAndReadable(new File((String)options.valueOf("infile"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Input file does not exist or is not readable");
    }
       
    // Is boxplot file path writable?
    File output = new File((String) options.valueOf("boxplot"));
    if (FileTools.dirPathExistsAndWritable(output.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory");
    }
    //Is nt_distribution file path writable
    File output2 = new File((String) options.valueOf("ntDistribution"));
    if (FileTools.dirPathExistsAndWritable(output.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory");
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
    String outputBox = (String)options.valueOf("boxplot");
    String outputNT = (String)options.valueOf("ntDistribution");

    StringBuffer cmd = new StringBuffer();

    cmd.append(options.valueOf("fastxBoxplot") +  " " + "-i" + " " );
    cmd.append(options.valueOf("infile") + " " +  "-o" + " " + options.valueOf("boxplot") + " " + tempDir.getAbsolutePath() + "\n");
     
    cmd.append(options.valueOf("fastxNTdistribution") +  " " + "-i" + " " );
    cmd.append(options.valueOf("infile") + " " +  "-o" + " " + options.valueOf("ntDistribution") + " " + tempDir.getAbsolutePath());
    
    RunTools.runCommand( new String[] { "bash", "-c", cmd.toString() } );
  
 

    // record the file output
    FileMetadata fm1 = new FileMetadata();
    fm1.setMetaType("png/boxplot");
    fm1.setFilePath(outputBox);
    fm1.setType("Performing boxplot on per lane base calling scores");
    fm1.setDescription("PNG boxplot output from qc/plotStats module.");
    ret.getFiles().add(fm1);
    
    FileMetadata fm2 = new FileMetadata();
    fm2.setMetaType("png/ntDistribution");
    fm2.setFilePath(outputNT);
    fm2.setType("Per lane nucleotide distribution plot");
    fm2.setDescription("PNG nucleotide distribution plot output from qc/plotStats module.");
    ret.getFiles().add(fm2);
    ret.setRunStopTstmp(new Date());
    return(ret);
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_output() {
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    if ((FileTools.fileExistsAndNotEmpty(new File((String) options.valueOf("boxplot"))).getExitStatus() != ReturnValue.SUCCESS) || 
        (FileTools.fileExistsAndNotEmpty(new File((String) options.valueOf("ntDistribution"))).getExitStatus() != ReturnValue.SUCCESS)) {
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
    if (!FileTools.deleteDirectoryRecursive(tempDir)) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't delete folder: "+tempDir.getAbsolutePath());
    }
    return(ret);
  }
}
