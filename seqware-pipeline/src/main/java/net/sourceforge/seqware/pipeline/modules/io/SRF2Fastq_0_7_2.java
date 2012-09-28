package net.sourceforge.seqware.pipeline.modules.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.filetools.validate.Validate;
import net.sourceforge.seqware.common.util.processtools.stderr.StdErr;
import net.sourceforge.seqware.common.util.processtools.stdout.StdOut;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;
import net.sourceforge.seqware.common.util.Log;

import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service=ModuleInterface.class)
public class SRF2Fastq_0_7_2 extends Module {
  private static String[] aligners = { "bfast", "bwa" }; // add aligners as we support more
  public static String[] sequencers = { "Solexa_1G_Genome_Analyzer", "Illumina_Genome_Analyzer", "Illumina_Genome_Analyzer_II", "Illumina_Genome_Analyzer_IIx", "Illumina_HiSeq_2000", "Illumina_HiScan_SQ", "Illumina_HiSeq_1000",  "AB_SOLiD_System", "AB_SOLiD_System_2.0", "AB_SOLiD_System_3.0", "AB_SOLiD_System_3_Plus", "AB_SOLiD_System_4", "AB_SOLiD_System_PI", "AB_SOLiD_5500xl", "AB_SOLiD_5500" }; // 454, etc

  /*
   * FIXME: is there a way to remove the aligner and sequencer parameters?
   * FIXME: bwa paired end output is always 2 files regardless of the num-output-files, this needs to be fixed
   * FIXME: really, the splitting functionality we added here should be in the C program, we should only have a very simple wrapper here, need to patch the C program and submit back
   * FIXME: this program splits across files but I think it assumes no reads actually are missing one of their pair for PE data, this may result in broken output! The splitter needs to detect if a read is unpaired.
   */
  
  String aligner = null;
  String sequencer = null;
  String input = null;
  String outputPrefix = null;
  int ends = 1;
  int numOut = 1;
  boolean filter = false;
  String srf2FastqPath;
  ArrayList<File> outputFiles = new ArrayList<File>();

  @Override
  public ReturnValue init() {
    ReturnValue ret = new ReturnValue();
    
    // some basics
    ret.setAlgorithm("SRF2Fastq");
    ret.setDescription("This module converts an SRF file to a Fastq for use with aligners. The output is Sanger scaled.");
    ret.setVersion("0.7.2");
    

    // Parse relevant parameters
    for (int i = 0; i < this.getParameters().size(); i++) {
      if (this.getParameters().get(i).compareTo("-a") == 0
          || this.getParameters().get(i).compareTo("--aligner") == 0) {
        aligner = this.getParameters().get(++i).toLowerCase();
      } else if (this.getParameters().get(i).compareTo("-s") == 0
          || this.getParameters().get(i).compareTo("--sequencer") == 0) {
        sequencer = this.getParameters().get(++i).toLowerCase();
      } else if (this.getParameters().get(i).compareTo("-b") == 0
          || this.getParameters().get(i).compareTo("--bin") == 0) {
        srf2FastqPath = this.getParameters().get(++i);
      } else if (this.getParameters().get(i).compareTo("-f") == 0
          || this.getParameters().get(i).compareTo("--filter") == 0) {
        filter = true;
      } else if (this.getParameters().get(i).compareTo("-e") == 0
          || this.getParameters().get(i).compareTo("--ends") == 0) {
        try {
          ends = Integer.parseInt(this.getParameters().get(++i));
        } catch (NumberFormatException e) {
          ends = 0;
        }
      } else if (this.getParameters().get(i).compareTo("-i") == 0
          || this.getParameters().get(i).compareTo("--input") == 0) {
        input = this.getParameters().get(++i);
      } else if (this.getParameters().get(i).compareTo("-n") == 0
          || this.getParameters().get(i).compareTo("--num-output-files") == 0) {
        try {
          numOut = Integer.parseInt(this.getParameters().get(++i));
        } catch (NumberFormatException e) {
          numOut = 1;
        }
      } else if (this.getParameters().get(i).compareTo("-o") == 0
          || this.getParameters().get(i).compareTo("--output-prefix") == 0) {
        outputPrefix = this.getParameters().get(++i);
      }
    }

    // Set prefix if not defined
    if (outputPrefix == null) {
      // Strip off directories before
      Log.error("INPUT: "+input);
      int start = input.lastIndexOf(System.getProperty("file.separator"));
      if (start < 0)
        start = 0;

      // Strip off .srf ending
      int end = input.lastIndexOf(".srf");
      if (end > 0)
        outputPrefix = input.substring(start + 1, end);
    }

    // for bwa output only make 1 output file (single end) or 2 output files (paired end)
    if ("bwa".equals(aligner)) {
      File file = new File(outputPrefix);
      if (file.getParentFile() != null)
        file.getParentFile().mkdirs();
      if (ends > 1) {
        outputFiles.add(file);
      } else {
        String outputFileName = new String(outputPrefix + ".fastq");
        file = new File(outputFileName);
        outputFiles.add(file);
      }
    } else if ("bfast".equals(aligner)) {
      // Setup output files for BFAST, supports multiple output files for large inputs
      for (int i = 0; i < numOut; i++) {
  
        String outputFileName = new String(outputPrefix + "." + i + ".fastq");
        File file = new File(outputFileName);
  
        // Make directory if needed
        if (file.getParentFile() != null)
          file.getParentFile().mkdirs();
  
        // Make sure we can write to this file
        try {
          file.createNewFile();
        } catch (IOException e) {
          // FIXME: If file exists, continue anyway. Should we do this?
        }
  
        if (!file.canWrite()) {
          ret.setStderr("File " + outputFileName
              + " could not be created in a writable manner\n");
          ret.setExitStatus(ReturnValue.FILENOTWRITABLE);
          return ret;
        }
  
        // Otherwise we are in the clear, so add to file array
        outputFiles.add(file);
      }
    }

    // If we got here, no problems, so we are good
    ret.setExitStatus(ReturnValue.SUCCESS);
    return ret;
  }

  @Override
  public ReturnValue do_verify_parameters() {
    // Aligner: Look at each supported method to make sure there is a match
    boolean valid = false;
    for (int j = 0; j < SRF2Fastq.getAligners().length; j++) {
      if (aligner != null && aligner.compareTo(SRF2Fastq.getAligners(j).toLowerCase()) == 0) {
        valid = true;
        break;
      }
    }

    if (!valid)
      return new ReturnValue(
          null,
          "srf2fastq requires a valid -a or --aligner argument, followed by a supported aligner\n\n"
              + get_syntax(), 1);

    // Sequencer: make sure a valid sequencer was specified
    valid = false;
    for (int j = 0; j < SRF2Fastq.getSequencers().length; j++) {
      if (sequencer != null
          && sequencer.compareTo(SRF2Fastq.getSequencers(j).toLowerCase()) == 0) {
        valid = true;
        break;
      }
    }
    if (!valid)
      return new ReturnValue(
          null,
          "srf2fastq requires a valid -s or --sequencer argument, followed by a supported sequencer\n\n"+sequencer+" \n\n"
              + get_syntax(), 1);

    // ends and numout must be greater than zero
    if (ends <= 0 || numOut <= 0) {
      return new ReturnValue(null,
          "When specifying -e or -n, they must be followed by an integer\n\n"
              + get_syntax(), 1);
    }

    // Input
    if (input == null || !input.endsWith(".srf")) {
      return new ReturnValue(null,
          "srf2fastq requires a -i or --input file which is a .srf file\n\n"
              + get_syntax(), 1);
    }
    
    if (FileTools.fileExistsAndExecutable(new File(srf2FastqPath)).getExitStatus() != ReturnValue.SUCCESS) {
      return new ReturnValue(null,
          "srf2fastq requires a -b or --bin file which is the srf2fastq executable\n\n"
              + get_syntax(), 1);
    }

    // Return object
    return new ReturnValue();
  }

  @Override
  public ReturnValue do_run() {
    ReturnValue ret = new ReturnValue();
    ret.setRunStartTstmp(new Date());

    // Append initial command and options
    StringBuffer cmd = new StringBuffer(srf2FastqPath);
    cmd.append(" -c ");

    // filter bad reads if requested
    if (filter) {
      cmd.append(" -C ");
    }

    // If multiple ends, they should be sequential if bfast
    if (ends > 1 && "bfast".equals(aligner)) {
      cmd.append(" -a -S ");
    } 
    // for bwa should be two different files, give the base as the first outputFile
    // NOTE: this means BWA output is one or two files, there's not shreading implemented for large inputs
    else if (ends > 1 && "bwa".equals(aligner)) {
      cmd.append(" -n -a -s "+outputFiles.get(0)+" ");
    }

    // For paired end Illumina runs, add options to reverse compliments ends
    // great than 1
    /* Brian 20101215: I don't think this is actually needed
    if (ends > 1 && sequencer.compareTo("illumina-ga2") == 0) {
      cmd.append(" -r 1");

      for (int i = 2; i < ends; i++)
        cmd.append("," + i);

      cmd.append(" ");
    }*/

    // For Solid, have to add option to append Primer to Sequence
    if (sequencer.indexOf("AB_SOLiD_") > -1) {
      cmd.append(" -e ");
    }

    // Add file name to the end
    cmd.append(" " + input);

    // Run command
    Process p = null;

    Log.error("Starting srf2fastq");// FIXME
    try {
      Log.error("Executing: " + cmd.toString());
      p = Runtime.getRuntime().exec(cmd.toString());
    } catch (IOException e) {
      return new ReturnValue(null, e.toString(), 4);
    }
    Log.error("srf2fastq launched");// FIXME

    // we're only parsing stdout and managing file creation for BFAST output (all one file)
    if ("bfast".equals(aligner) || ("bwa".equals(aligner) && ends == 1)) {
      // Read from stdout and write to output files. Each output file gets 4 *
      // ends before moving to next entry. This assures all ends for a given
      // read are together
      try {
        // FIXME: This first function call is just for now, srf2fastq strips
        // primer automatically
        if (sequencer.indexOf("AB_SOLiD_") > -1) {
          StdOut.stdout2fileEncodedStripQualPrimer(p, outputFiles, ends * 4);
        } else {
          StdOut.stdout2file(p, outputFiles, false, ends * 4);
        }
      } catch (IOException e) {
        // FIXME: Need to clean up error codes and all
        p.destroy();
        return new ReturnValue(null, "stdout2file encountered an error", 4);
      }
    }
    // else we let srf2fastq make the files for us
    else if ("bwa".equals(aligner) && ends > 1) {
      // the command will just run as prepared, don't need to process stdout
    }

    // Wait on process
    try {
      p.waitFor();
    } catch (InterruptedException e) {
      p.destroy();
      return new ReturnValue(null, e.toString(), 4);
    }

    Log.error("srf2fastq done");// FIXME

    // Otherwise return exit code
    ret.setExitStatus(p.exitValue());
    try {
      ret.setStderr(StdErr.stderr2string(p));
      ret.setExitStatus(p.exitValue());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    

    ret.setRunStopTstmp(new Date());
    return ret;
  }

  @Override
  public ReturnValue do_verify_input() {
    if (input.endsWith(".srf")) {
      ReturnValue retval = FileTools.verifyFile(new File(input));
      if (retval.getExitStatus() != 0)
        return retval;
    }

    // Otherwise all verifications succeeded so return success
    return new ReturnValue(null, null, 0);
  }

  @Override
  public ReturnValue do_test() {
    // FIXME
    ReturnValue ret = new ReturnValue();
    return ret;
  }

  @Override
  public ReturnValue do_verify_output() {
    // FIXME: need to deal with paired end output for BWA
    if ("bfast".equals(aligner) || ("bwa".equals(aligner) && ends == 1)) {
      for (int i = 0; i < outputFiles.size(); i++) {
        ReturnValue retval = Validate.Fastq(outputFiles.get(i), this.sequencer, this.aligner, this.ends);
        if (retval.getExitStatus() != 0)
          return retval;
      }
    } else if ("bwa".equals(aligner) && ends > 1) {
      File file1 = new File(outputFiles.get(0)+"_1.fastq");
      File file2 = new File(outputFiles.get(0)+"_2.fastq");
      if (!file1.exists() || !file2.exists()) {
        return(new ReturnValue("", "Can't find files "+file1.getAbsolutePath()+" "+file2.getAbsolutePath(), ReturnValue.FILENOTREADABLE));
      }
    }

    // Otherwise all verifications succeeded so return success
    return new ReturnValue(null, null, 0);
  }

  @Override
  public String get_syntax() {
    StringBuffer ReturnString = new StringBuffer(
        "Module to srf2fastq: Generate a fastq compatible with a particular aligner, directly from a SRF"
            + System.getProperty("line.separator")
            + System.getProperty("line.separator"));

    // Parse relevant parameters
    ReturnString
        .append("Example: srf2fastq -a bfast -s illumina [-e 2] [-n 2] -i input.srf [-o outputPrefix]");
    ReturnString.append(System.getProperty("line.separator"));
    ReturnString.append(System.getProperty("line.separator"));
    ReturnString.append("Required Parameters:");
    ReturnString.append(System.getProperty("line.separator"));
    ReturnString
    .append("\t-i, --input              {String}    Specifies the SRF file that will be read in to produce a fastq");
    ReturnString.append(System.getProperty("line.separator"));
    ReturnString
    .append("\t-b, --bin                {String}    Specifies the srf2fastq binary");
    ReturnString.append(System.getProperty("line.separator"));
    ReturnString
    .append("\t-f, --filter             {String}    Specifies that the quality filter for srf2fastq should be used");
    ReturnString.append(System.getProperty("line.separator"));
    ReturnString
        .append("\t-s, --sequencer          {String}    See below for currently supported sequencers");
    ReturnString.append(System.getProperty("line.separator"));
    ReturnString
        .append("\t-a, --aligner            {String}    See below for currently supported aligners");
    ReturnString.append(System.getProperty("line.separator"));
    ReturnString.append(System.getProperty("line.separator"));

    ReturnString.append("Optional Parameters:");
    ReturnString.append(System.getProperty("line.separator"));
    ReturnString
        .append("\t-e, --ends               {int}       Specifies the number of ends in this SRF. Default: 1 (single-end)");
    ReturnString.append(System.getProperty("line.separator"));
    ReturnString
        .append("\t-n, --num-output-files   {int}       Specifies the number of output files to split normal output files into. Default: 1");
    ReturnString.append(System.getProperty("line.separator"));
    ReturnString
        .append("\t-o, --output-prefix      {String}    Specifies the name to be prepend to each output file. Default: use the name of the input file, truncating '.srf'");
    ReturnString.append(System.getProperty("line.separator"));
    ReturnString.append(System.getProperty("line.separator"));

    ReturnString.append("Supported Aligners (for -a argument):");
    ReturnString.append(System.getProperty("line.separator"));

    for (int i = 0; i < SRF2Fastq.getAligners().length; i++) {
      ReturnString.append("\t" + SRF2Fastq.getAligners(i));
      ReturnString.append(System.getProperty("line.separator"));
    }
    ReturnString.append(System.getProperty("line.separator"));

    ReturnString.append("Supported Sequencers (for -s argument):");
    ReturnString.append(System.getProperty("line.separator"));

    for (int i = 0; i < SRF2Fastq.getSequencers().length; i++) {
      ReturnString.append("\t" + SRF2Fastq.getSequencers(i));
      ReturnString.append(System.getProperty("line.separator"));
    }

    // Return as string
    return ReturnString.toString();
  }

  public static String[] getAligners() {
    return aligners;
  }

  public static String getAligners(int i) {
    return aligners[i];
  }

  public static String[] getSequencers() {
    return sequencers;
  }

  public static String getSequencers(int i) {
    return sequencers[i];
  }
}
