package net.sourceforge.seqware.pipeline.modules.io;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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
import net.sourceforge.seqware.common.util.Log;

import org.openide.util.lookup.ServiceProvider;


/**
 * This module takes an Illumina Genome Analyzer run folder and converts what
 * it finds there into one SRF per lane. Really, this needs to do a little more,
 * I want one SRF file per Indivisible Unit of Sequence (IUS). What I mean by IUS
 * is the subset of reads that all share a common prefix e.g. barcode. So a given
 * lane without barcodes/multiplexed samples would be one IUS but a lane with 
 * multiplexed samples each with their own barcode would have >1 IUS, one per 
 * barcode used.
 * 
 * Keep in mind this module will check if the file Status.xml
 * and then look inside for a line that looks like the following:
 * 
 * <Software>Illumina RTA 1.6.*.*</Software>
 * 
 * This module will error out if the string does not match "^Illumina RTA 1.6."
 * so currently this module is only helpful for non-historic Illumina run folders.
 * 
 * TODO: add IUS support
 * 
 * @author briandoconnor@gmail.com
 *
 */
@ServiceProvider(service=ModuleInterface.class)
public class Illumina2SRF extends Module {

  private OptionSet options = null;
  private File tempDir = null;
  private File output = null;

  /**
   * getOptionParser is an internal method to parse command line args.
   * 
   * @return OptionParser this is used to get command line options
   */
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("qseq-path", "This is the path to qseq files in an Illumina-supplied run folder").withRequiredArg().ofType(String.class).describedAs("required");
    //parser.accepts("status-xml", "This is located in <qseq-path>/../../Status.xml as of RTA_1.6.x, it uses this file to pull out the specific RTA version used and make sure it's within the range of what this module supports.").withRequiredArg().ofType(String.class).describedAs("required");
    parser.accepts("lane", "The lane to convert (starting at 1). If the code for this module breaks it's likely here if Illumina changes its file naming conventions. It currently uses <qseq-path>/s_<lane>_*_qseq.txt").withRequiredArg().ofType(Integer.class).describedAs("required");
    parser.accepts("num-qseq-files", "The number of <qseq-path>/s_<lane>_*_qseq.txt files per lane. As of RTA 1.6.x this is 120 on the Illumina GAII. Clearly this value can/will change").withRequiredArg().ofType(Integer.class).describedAs("required");    
    parser.accepts("num-reads", "The number of reads, for the current Illumina platform this is 1 or 2.").withRequiredArg().ofType(Integer.class).describedAs("required");        
    parser.accepts("barcode", "CURRENTLY IGNORED: this will let you pass in the barcode sequence. The net result is an SRF file that only contains reads with this barcode.").withRequiredArg().ofType(String.class).describedAs("optional");
    parser.accepts("output-file", "This is the output SRF file name").withRequiredArg().ofType(String.class).describedAs("required");
    parser.accepts("illumina2srf-bin", "This is the path to the binary executable illumina2srf that does the heavy lifting here").withRequiredArg().ofType(String.class).describedAs("required");

    return (parser);
  }

  /**
   * The init method is where you put any code needed to setup your module.
   * Here I set some basic information in the ReturnValue object which will eventually
   * populate the "processing" table in seqware_meta_db. I also create a temporary
   * directory using the FileTools object.
   * 
   * init is optional
   * 
   * @return A ReturnValue object that contains information about the status of init
   */
  @Override
  public ReturnValue init() {

    // setup the return value object, notice that we use
    // ExitStatus, this is what SeqWare uses to track the status
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    // fill in the algorithm field in the processing table
    ret.setAlgorithm("Illumina2SRF");
    // fill in the description field in the processing table
    ret.setDescription("This converts a given lane/barcode combo in the Illumina run folder to an SRF file.");
    // fill in the version field in the processing table
    ret.setVersion("0.7.1");
    
    try {

      OptionParser parser = getOptionParser();

      // The parameters object is actually an ArrayList of Strings created
      // by splitting the command line options by space. JOpt expects a String[]
      options = parser.parse(this.getParameters().toArray(new String[0]));

      // create a temp directory in current working directory
      tempDir = FileTools.createTempDirectory(new File("."));

      // you can write to "stdout" or "stderr" which will be persisted back to the DB
      ret.setStdout(ret.getStdout()+"Output: "+(String)options.valueOf("output-file")+"\n");

    } catch (OptionException e) {
      e.printStackTrace();
      ret.setStderr(e.getMessage());
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
    } catch (IOException e) {
      e.printStackTrace();
      ret.setStderr(e.getMessage());
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
    }
    
    // now return the ReturnValue
    return (ret);

  }

  /**
   * Verifies that the required parameters are present
   * 
   * @return a ReturnValue object
   */
  @Override
  public ReturnValue do_verify_parameters() {
    
    // most methods return a ReturnValue object
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    // now look at the options and make sure they make sense
    for (String option : new String[] {
        "qseq-path", "lane", "output-file", "num-qseq-files", "illumina2srf-bin", "num-reads"
        // "status-xml", 
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
   * The do_verify_input method ensures that the input files exist. It
   * may also do validation of the input files or anything that is needed
   * to make sure the module has everything it needs to run. There is some
   * overlap between this method and do_verify_parameters. This one is more
   * focused on validating files, making sure web services are up, DBs can be
   * connected to etc.  While do_verify_parameters is primarily used to
   * validate that the minimal parameters are passed in. The overlap between
   * these two methods is at the discretion of the developer
   * 
   * @return a ReturnValue object
   */
  @Override
  public ReturnValue do_verify_input() {
    
    // not much to do, let's make sure the
    // temp directory is writable
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    // LEFT OFF HERE: 
    //ReturnValue inputRet =  FileTools.dirPathExistsAndReadable( new File( (String) options.valueOf("input-file") ));
    
    // If input-file argument was specified, make sure it exits and is readable
    if ( options.has("qseq-path") ) {
      ReturnValue inputRet =  FileTools.dirPathExistsAndReadable( new File( (String) options.valueOf("qseq-path") ));
      if ( inputRet.getExitStatus() != ReturnValue.SUCCESS)  {
        ret.setExitStatus(ReturnValue.DIRECTORYNOTREADABLE);
        ret.setStderr("Can't read from input directory " + (String) options.valueOf("qseq-path") + ": " + inputRet.getStderr() );
        return (ret);
      }      
    }
    
    // If illumina2srf-bin argument was specified, make sure it exits and is exec
    if ( options.has("illumina2srf-bin") ) {
      ReturnValue inputRet =  FileTools.fileExistsAndExecutable( new File( (String) options.valueOf("illumina2srf-bin") ));
      if ( inputRet.getExitStatus() != ReturnValue.SUCCESS)  {
        ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
        ret.setStderr("Can't execute binary " + (String) options.valueOf("illumina2srf-bin") );
        return (ret);
      }      
    }
    
    // If input-file argument was specified, make sure it exits and is readable
    /* if ( options.has("status-xml") ) {
      // can I find the RTA version file?
      ReturnValue inputRet =  FileTools.fileExistsAndReadable( new File( (String) options.valueOf("status-xml") ));
      if ( inputRet.getExitStatus() != ReturnValue.SUCCESS)  {
        ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        ret.setStderr("Can't read from input file " + (String) options.valueOf("status-xml")+": " + inputRet.getStderr() );
        return (ret);
      }
      // now check to see if we support this version of RTA that makes the qseq files
      boolean correctRTAVersion = false;
      try {
        BufferedReader br = new BufferedReader(new FileReader(new File( (String) options.valueOf("status-xml") )));
        String line = br.readLine();
        while (line != null) {
          if (line.matches("^.*Illumina RTA 1\\.6\\..*$")) { correctRTAVersion = true; }
          line = br.readLine();
        }
        br.close();
      } catch (FileNotFoundException e) {
        ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        ret.setStderr("Can't read from input file " + (String) options.valueOf("status-xml")+": " +e.getMessage());
        return(ret);
      } catch (IOException e) {
        ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        ret.setStderr("Can't read from input file " + (String) options.valueOf("status-xml")+": " +e.getMessage());
        return(ret);
      }
      if (!correctRTAVersion) {
        ret.setExitStatus(ReturnValue.INVALIDFILE);
        ret.setStderr("The "+(String) options.valueOf("status-xml")+" file references a version of RTA that I don't know (!= 1.6.x).");
        return(ret);
      }
    }*/
    
    if (options.has("output-file")) {
      File output = new File((String)options.valueOf("output-file"));
      try {
        output.createNewFile();
        if (!output.canWrite()) {
          ret.setExitStatus(ReturnValue.FILENOTWRITABLE);
          ret.setStderr("Can't open output file for writing: "+(String)options.valueOf("output-file"));
          return(ret);
        }
        output.delete();
      } catch (IOException e) {
        ret.setExitStatus(ReturnValue.FILENOTWRITABLE);
        ret.setStderr("Can't open output file for writing: "+(String)options.valueOf("output-file"));
        return(ret);
      }
    }
    
    if (options.has("lane")) {
      Integer lane = (Integer)options.valueOf("lane");
      Integer numFiles = (Integer)options.valueOf("num-qseq-files");
      Integer numReads = (Integer)options.valueOf("num-reads");
      if (numReads < 1 || numReads > 2) {
        ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
        ret.setStderr("The num-reads of "+numReads+" is not 1 or 2 which are currently the only supported number for the Illumina sequencer!");
        return(ret);
      }
      numFiles = numFiles * numReads;
      File qseqPath = new File( (String) options.valueOf("qseq-path"));
      if (qseqPath.isDirectory()) {
        File[] files = qseqPath.listFiles(new QseqFilter(lane));
        if (files.length != numFiles) {
          ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
          ret.setStderr("input qseq-path does not contain the correct number of qseq files for this lane (expecting "+numFiles*numReads+" but got "+files.length+"!)");
          return(ret);
        }
      } else {
        ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
        ret.setStderr("input qseq-path is not a dir!");
        return(ret);
      }
    }
    
    return (ret);

  }

  /**
   * This is really an optional method but a very good idea. You
   * would test the programs your calling here by running them on
   * a "known good" test dataset and then compare the new answer
   * with the previous known good answer. Other forms of testing could be
   * encapsulated here as well.
   * 
   * @return a ReturnValue object
   */
  @Override
  public ReturnValue do_test() {
    
    // notice the use of "NOTIMPLEMENTED", this signifies that we simply 
    // aren't doing this step. It's better than just saying SUCCESS
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.NOTIMPLEMENTED);

    // not much to do, just return
    return(ret);
  }

  /**
   * This is the core of a module. While some modules may be written in pure Java or use
   * various third-party Java APIs, the vast majority of modules will use this method to 
   * make calls out to the shell (typically the BASH shell in Linux) and use that shell
   * to execute various commands.  In an ideal world this would never happen, we would all 
   * write out code with a language-agnostic, network-aware API (e.g. thrift, SOAP, etc).
   * But until that day comes most programs in bioinformatics are command line tools
   * (or websites). So the heart of the module is it acts as a way for us to treat the
   * disparate tools as well-behaved modules that present a standard interface
   * and report back their metadata in well-defined ways. That's, ultimately, what this
   * object and, in particular this method, are all about.
   * 
   * There are other alternatives out there, such as Galaxy, that may provide an XML
   * syntax for accomplishing much of the same thing. For example, they make disparate tools
   * appear to function the same because the inputs/outputs are all described using a standardized 
   * language. We chose Java because it was more expressive than XML as a module running
   * descriptor. But clearly there are a lot of ways to solve this problem. The key concern,
   * though, is that a module should present very clear inputs and outputs based,
   * whenever possible, on standardized file types. This makes it easy to use modules in
   * novel workflows, rearranging them as needed.  Make every effort to make your modules
   * self-contained and robust!
   * 
   * @return a ReturnValue object
   */
  @Override
  public ReturnValue do_run() {
    
    // prepare the return value
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    ret.setRunStartTstmp(new Date());
    
    // /illumina2srf -o ~/scratch/srf_test/100423_UNC2-RDR300275_0004.1.srf /datastore/nextgenin/TCGA/100423_UNC2-RDR300275_0004/Data/Intensities/BaseCalls/s_1_*_qseq.txt
    String cmd = (String) options.valueOf("illumina2srf-bin") + " -force_config_machine_name -o " +
                     (String) options.valueOf("output-file") + " " + (String) options.valueOf("qseq-path") + File.separator + "s_" + (Integer) options.valueOf("lane") + "_*qseq.txt";
    Log.error(cmd);
    ret = RunTools.runCommand(new String[] { "bash", "-c", cmd } );
    
    FileMetadata fm = new FileMetadata();
    fm.setMetaType("application/srf");
    fm.setFilePath((String) options.valueOf("output-file"));
    fm.setType("srf");
    fm.setDescription("Short Read Format, a binary version of the FASTQ used by the SRA.");
    ret.getFiles().add(fm);
    ret.setRunStopTstmp(new Date());
    return(ret);
    
  }

  /**
   * A method to check to make sure the output was created correctly
   * 
   * @return a ReturnValue object
   */
  @Override
  public ReturnValue do_verify_output() {
    
    // this is easy, just make sure the file exists
    return(FileTools.fileExistsAndNotEmpty(new File((String)options.valueOf("output-file"))));
    
  }
  
  /**
   * A cleanup method, make sure you cleanup files that are outside the current working directory
   * since Pegasus won't clean those for you.
   * 
   * clean_up is optional
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
  
  // inner class for file name filtering
  private class QseqFilter implements FilenameFilter {

    private Integer lane;
    
    QseqFilter (Integer lane) {
      this.lane = lane;
    }
    
    @Override
    public boolean accept(File dir, String name) {
      //Log.error("FILENAME: "+name);
      return(name.matches("^.*s_"+getLane()+"_\\S+_qseq.txt$"));
    }

    public Integer getLane() {
      return lane;
    }

    public void setLane(Integer lane) {
      this.lane = lane;
    }
    
  }
  
}
