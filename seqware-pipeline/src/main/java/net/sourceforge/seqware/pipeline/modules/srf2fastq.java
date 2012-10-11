package net.sourceforge.seqware.pipeline.modules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
public class srf2fastq extends Module {
  private static String[] aligners = { "bfast" }; // maq, bwa
  public static String[] sequencers = { "illumina-ga2", "solid-3" }; // 454, etc

  String aligner = null;
  String sequencer = null;
  String input = null;
  String outputPrefix = null;
  int ends = 1;
  int numOut = 1;
  ArrayList<File> outputFiles = new ArrayList<File>();

  @Override
  public ReturnValue init() {
    ReturnValue ret = new ReturnValue();

    // Parse relevant parameters
    for (int i = 1; i < this.getParameters().size() - 1; i++) {
      if (this.getParameters().get(i).compareTo("-a") == 0
          || this.getParameters().get(i).compareTo("--aligner") == 0) {
        aligner = this.getParameters().get(++i).toLowerCase();
      } else if (this.getParameters().get(i).compareTo("-s") == 0
          || this.getParameters().get(i).compareTo("--sequencer") == 0) {
        sequencer = this.getParameters().get(++i).toLowerCase();
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
          numOut = 0;
        }
      } else if (this.getParameters().get(i).compareTo("-o") == 0
          || this.getParameters().get(i).compareTo("--output-prefix") == 0) {
        outputPrefix = this.getParameters().get(++i);
      }
    }

    // Set prefix if not defined
    if (outputPrefix == null) {
      // Strip off directories before
      int start = input.lastIndexOf(System.getProperty("file.separator"));
      if (start < 0)
        start = 0;

      // Strip off .srf ending
      int end = input.lastIndexOf(".srf");
      if (end > 0)
        outputPrefix = input.substring(start + 1, end);
    }

    // Setup output files
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
        ret.setExitStatus(1);
        return ret;
      }

      // Otherwise we are in the clear, so add to file array
      outputFiles.add(file);
    }

    // If we got here, no problems, so we are good
    ret.setExitStatus(0);
    return ret;
  }

  @Override
  public ReturnValue do_verify_parameters() {
    // Aligner: Look at each supported method to make sure there is a match
    boolean valid = false;
    for (int j = 0; j < srf2fastq.getAligners().length; j++) {
      if (aligner != null && aligner.compareTo(srf2fastq.getAligners(j)) == 0) {
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
    for (int j = 0; j < srf2fastq.getSequencers().length; j++) {
      if (sequencer != null
          && sequencer.compareTo(srf2fastq.getSequencers(j)) == 0) {
        valid = true;
        break;
      }
    }
    if (!valid)
      return new ReturnValue(
          null,
          "srf2fastq requires a valid -s or --sequencer argument, followed by a supported sequencer\n\n"
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

    // Return object
    return new ReturnValue();
  }

  @Override
  public ReturnValue do_run() {
    ReturnValue ret = new ReturnValue();

    if (aligner.compareTo("bfast") == 0) {
      // Append initial command and options
      StringBuffer cmd = new StringBuffer(this.getParameters().get(0));
      cmd.append(" -c ");
      
      // If multiple ends, they should be sequential
      if ( ends > 1 ) {
        cmd.append(" -S ");
      }

      // For paired end Illumina runs, add options to reverse compliments ends
      // great than 1
      if (ends > 1 && sequencer.compareTo("illumina-ga2") == 0) {
        cmd.append(" -r 1");

        for (int i = 2; i < ends; i++)
          cmd.append("," + i);

        cmd.append(" ");
      }

      // For Solid, have to add option to append Primer to Sequence
      if (sequencer.compareTo("solid-3") == 0) {
        cmd.append(" -e ");
      }

      // Add file name to the end
      cmd.append(" " + input);

      // Run command
      Process p = null;

      Log.error("Starting srf2fastq");// FIXME
      try {
        p = Runtime.getRuntime().exec(cmd.toString());
      } catch (IOException e) {
        return new ReturnValue(null, e.toString(), 4);
      }
      Log.error("srf2fastq launched");// FIXME

      // Read from stdout and write to output files. Each output file gets 4 *
      // ends before moving to next entry. This assures all ends for a given
      // read are together
      try {
        // FIXME: This first function call is just for now, srf2fastq strips primer automatically
        if ( sequencer.compareTo("solid-3") == 0 ) {
          StdOut.stdout2fileEncodedStripQualPrimer(p, outputFiles, ends * 4); 
        }
        else {
          StdOut.stdout2file(p, outputFiles, false, ends * 4);
        }
      }
      catch ( IOException e ) {
        //FIXME: Need to clean up error codes and all
        p.destroy();
        return new ReturnValue(null, "stdout2file encountered an error", 4);
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
    }

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
    for (int i = 0; i < outputFiles.size(); i++) {
      ReturnValue retval = Validate.Fastq(outputFiles.get(i), this.sequencer, this.aligner, this.ends);
      if (retval.getExitStatus() != 0)
        return retval;
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

    for (int i = 0; i < srf2fastq.getAligners().length; i++) {
      ReturnString.append("\t" + srf2fastq.getAligners(i));
      ReturnString.append(System.getProperty("line.separator"));
    }
    ReturnString.append(System.getProperty("line.separator"));

    ReturnString.append("Supported Sequencers (for -s argument):");
    ReturnString.append(System.getProperty("line.separator"));

    for (int i = 0; i < srf2fastq.getSequencers().length; i++) {
      ReturnString.append("\t" + srf2fastq.getSequencers(i));
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
