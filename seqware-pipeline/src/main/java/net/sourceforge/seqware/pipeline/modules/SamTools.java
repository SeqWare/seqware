package net.sourceforge.seqware.pipeline.modules;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.processtools.stderr.StdErr;
import net.sourceforge.seqware.common.util.runtools.RunTools;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;

import org.apache.commons.lang.StringUtils;
import org.openide.util.lookup.ServiceProvider;

/**
 * FIXME: if creating pileup requres a .fai file!
 *
 * @author boconnor
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class SamTools extends Module {
  
  /** {@inheritDoc} */
  @Override
  public ReturnValue init() {
    // Samtools has different modules (i.e. match, localalign), so first figure
    // out which was requested
    ReturnValue ret = new ReturnValue();
    if (this.getParameters().size() > 1) {
      this.setAlgorithm(this.getParameters().get(1)); // algorithm
      ret.setAlgorithm(this.getParameters().get(1));
      return(ret);
    } else
      return new ReturnValue(null,
          "Samtools requires an arg[1] to specify which module to run", 2);
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_parameters() {
    ReturnValue ret = new ReturnValue();

    // Verify arg[1] exists. If it does not return error and Samtools help
    // message.
    if (this.getParameters().size() < 2) {
      StringBuffer syntax = new StringBuffer(
          "To run Samtools you must specify atleast one command. Usage:"
              + System.getProperty("line.separator"));
      syntax.append(get_syntax());
      ret.setStderr(syntax.toString());
      ret.setExitStatus(2);
      return ret;
    }

    // Verify arg[1] is valid, by checking samtools output
    // FIXME: This should use RunTools instead of getRuntime.exec
    try {
      String[] args = { this.getParameters().get(0),
          this.getParameters().get(1) };
      Process p = Runtime.getRuntime().exec(args);
      p.waitFor();

      // Capture Stderr to determine if it was an invalid command
      String stdError = StdErr.stderr2string(p);
      String invalid = new String("[main] unrecognized command '"
          + this.getParameters().get(1) + "'"
          + System.getProperty("line.separator"));

      // If it matched, we have a problem so return a command saying so
      if (stdError.compareTo(invalid) == 0) {
        StringBuffer syntax = new StringBuffer(this.getParameters().get(1)
            + " is not a valid command for Samtools. Usage:"
            + System.getProperty("line.separator"));
        syntax.append(get_syntax());
        ret.setStderr(syntax.toString());
        ret.setExitStatus(1);
      }
    } catch (IOException e) {
      ret.setExitStatus(4);
      ret.setStderr(e.toString());
    } catch (InterruptedException e) {
      ret.setExitStatus(5);
      ret.setStderr(e.toString());
    }

    // If there was some kind of problem, return error
    if (ret.getExitStatus() != 0)
      return ret;

    /* Make sure we have at least the minimum parameters specified */
    // checks parameters for appropriate Samtools commands
    if (this.getParameters().get(1).compareTo("import") == 0) {
      // import requires *.fai, in.sam, out.bam
      if (!(this.getParameters().size() >= 5
          && this.getParameters().get(2).endsWith(".fai")
          && this.getParameters().get(3).endsWith(".sam") && this
          .getParameters().get(4).endsWith(".bam"))) {
        ret.setExitStatus(3);
        ret
            .setStderr("Samtools import always requires .fai, .sam and .bam arguments in sequential order");
        return ret;
      }
    } else if (this.getParameters().get(1).compareTo("merge") == 0) {
      // merge requires three or more bams for output and at least 2 inputs to
      // merge
      int bams = 0;
      for (int i = 2; i < this.getParameters().size(); i++) {
        // Look at all bam inputs
        if (this.getParameters().get(i).endsWith(".bam")) {
          bams++;
        }
      }

      if (bams < 3) {
        ret.setExitStatus(3);
        ret
            .setStderr("Samtools merge always requires 3 or more bam files: 1 output, and 2 or more inputs to merge");
        return ret;
      }
    } else if (this.getParameters().get(1).compareTo("sort") == 0) {
      // sort requires in.bam, out.prefix
      boolean valid = false;
      for (int i = 2; i < (this.getParameters().size() - 1); i++) {
        if (this.getParameters().get(i).endsWith(".bam")
            && this.getParameters().get(i + 1) != null) {
          valid = true;
          break;
        }
      }

      if (valid == false) {
        ret.setExitStatus(3);
        ret
            .setStderr("Samtools sort always requires a .bam argument followed by output prefix argument");
        return ret;
      }
    } else if (this.getParameters().get(1).compareTo("index") == 0) {
      // index requires in.bam argument
      if (!(this.getParameters().size() >= 3 && this.getParameters().get(2)
          .endsWith(".bam"))) {
        ret.setExitStatus(3);
        ret
            .setStderr("Samtools index requires the first argument to be in.bam");
        return ret;
      }
    } else if (this.getParameters().get(1).compareTo("rmdup") == 0) {
      // rmdup requires in.bam out.bam
      for (int i = 2; i < (this.getParameters().size() - 1); i++) {
        if (!(this.getParameters().get(i).endsWith(".bam") && this
            .getParameters().get(i + 1).endsWith(".bam"))) {
          ret.setExitStatus(3);
          ret.setStderr("Samtools rmdup requires in.bam and out.bam arguments");
          return ret;
        }
      }
    } else if (this.getParameters().get(1).compareTo("pileup") == 0 ) {
      // Pileup writes to stdout, so make sure it was captured
      if (this.getStdoutFile() == null) {
        ret
            .setStderr("Samtools pileup writes results to stdout, so you must redirect stdout to a file in order to use it. See the --output option for the seqware runner.");
        ret.setExitStatus(ReturnValue.STDOUTERR);
        return ret;
      }

      // pileup requires last argument to be a sam or bam
      if (!(this.getParameters().size() >= 3 && (this.getParameters().get(
          this.getParameters().size() - 1).endsWith(".sam") || this.getParameters()
          .get(this.getParameters().size() - 1).endsWith(".bam")))) {
        ret.setExitStatus(3);
        ret
            .setStderr("Samtools pileup requires the last argument to be a sam or bam file");
        return ret;
      }
    } else if (this.getParameters().get(1).compareTo("view") == 0) {
      // FIXME
    }

    // If we did not return there were no problems
    ret.setExitStatus(0);
    return ret;
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_run() {
    
    // prepare the return value
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    // track the start time of do_run for timing purposes
    ret.setRunStartTstmp(new Date());
    
    /* Run Samtools */
    // Prepare argument array
    String cmd = StringUtils.join( this.getParameters(), ' ' );
    
    ReturnValue result = null;
    if ( this.getStdoutFile() != null ) {
      cmd += " > " + this.getStdoutFile().getAbsolutePath();
      
      // Launch the process
      result = RunTools.runCommand( new String[] { "bash", "-c", cmd } );
    }
    else {
      result =  RunTools.runCommand( cmd );
    }
    
    if (result.getProcessExitStatus() !=  ReturnValue.SUCCESS || result.getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(result.getExitStatus());
      ret.setProcessExitStatus(result.getProcessExitStatus());
      ret.setRunStopTstmp(new Date());
      return(ret);
    }
    
    // record the file output
    FileMetadata fm = new FileMetadata();
    if (cmd.indexOf("pileup") > -1 && cmd.indexOf("-c") > -1) {
      fm.setMetaType("text/pileup-consensus");
    } else if (cmd.indexOf("pileup") > -1) {
      fm.setMetaType("text/pileup");
    } else {
      // FIXME: will need to guess for other possibilities since can be SAM or BAM... Jordan made this as a passthrough which is a mistake...
      fm.setMetaType("text/plain");
    }
    fm.setFilePath(this.getStdoutFile().getPath());
    fm.setType("samtools-output");
    fm.setDescription("Output from SamTools which could produce many different output formats, see the meta-type.");
    ret.getFiles().add(fm);
    
    // note the time do_run finishes
    ret.setRunStopTstmp(new Date());
    return(ret);
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_input() {
    // import takes in a fai and sam files
    if (this.getParameters().get(1).compareTo("import") == 0) {
      for (int i = 2; i < this.getParameters().size(); i++) {
        if (this.getParameters().get(i).endsWith(".fai")
            || this.getParameters().get(i).endsWith(".sam")) {
          ReturnValue retval = FileTools.verifyFile(new File(this
              .getParameters().get(i)));
          if (retval.getExitStatus() != 0)
            return retval;
        }
      }
    } else if (this.getParameters().get(1).compareTo("merge") == 0) {
      boolean firstBam = true;
      for (int i = 2; i < this.getParameters().size(); i++) {
        // Look at all bam inputs
        if (this.getParameters().get(i).endsWith(".bam")) {
          // Ignore first bam, which will be output
          if (firstBam) {
            firstBam = false;
            continue;
          }

          // Verify all others
          ReturnValue retval = FileTools.verifyFile(new File(this
              .getParameters().get(i)));
          if (retval.getExitStatus() != 0)
            return retval;
        }
      }
    }
    // Everything else takes in a bam
    else {
      for (int i = 2; i < this.getParameters().size(); i++) {
        String argument = this.getParameters().get(i);
        if (argument.endsWith(".bam")) {
          ReturnValue retval = FileTools.verifyFile(new File(this
              .getParameters().get(i)));
          if (retval.getExitStatus() != 0) {
            return retval;
          }
          break;
        }
      }
    }

    // Otherwise all verifications succeeded so return success
    return new ReturnValue(null, null, 0);
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_test() {
    ReturnValue ret = new ReturnValue();

    // FIXME: Need to implement this. Question is where should we find the
    // reference file needed for test? Should it be a config of some sort?
    if (this.getParameters().get(1).compareTo("import") == 0) {
      // FIXME: samtools import
      // /scratch0/bfast/genomes/custom/hg18.chrX.31000000-38000000/hg18.fa.fai
      // expected.sam expected.bam
    } else if (this.getParameters().get(1).compareTo("sort") == 0) {
      // FIXME: samtools sort expected.bam expected.sorted
    } else if (this.getParameters().get(1).compareTo("index") == 0) {
      // FIXME: samtools index expected.rmdup.bam
    } else if (this.getParameters().get(1).compareTo("merge") == 0) {
      // FIXME: samtools merge
    } else if (this.getParameters().get(1).compareTo("rmdup") == 0) {
      // FIXME: samtools rmdup expected.sorted.bam expected.rmdup.bam
    } else if (this.getParameters().get(1).compareTo("pileup") == 0) {
      // FIXME: samtools pileup expected.rmdup.bam > expected.pileup
    }

    return ret;
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_output() {
    // import, rmdup produces a .bam that is the last argument
    if (this.getParameters().get(1).compareTo("import") == 0
        || this.getParameters().get(1).compareTo("rmdup") == 0
        || this.getParameters().get(1).compareTo("") == 0) {
      ReturnValue retval = FileTools.verifyFile(new File(this.getParameters()
          .get(this.getParameters().size() - 1)));
      if (retval.getExitStatus() != 0)
        return retval;
    }
    // Merge produces a bam
    else if (this.getParameters().get(1).compareTo("merge") == 0) {
      // Look for first bam
      for (int i = 2; i < this.getParameters().size(); i++) {
        if (this.getParameters().get(i).endsWith(".bam")) {
          // Verify it
          ReturnValue retval = FileTools.verifyFile(new File(this
              .getParameters().get(i)));
          if (retval.getExitStatus() != 0)
            return retval;

          // Break since rest of the bams will be input
          break;
        }
      }
    }
    // sort produces a bam with .bam appended to the last argument
    else if (this.getParameters().get(1).compareTo("sort") == 0) {
      ReturnValue retval = FileTools.verifyFile(new File(this.getParameters()
          .get(this.getParameters().size() - 1)
          + ".bam"));
      if (retval.getExitStatus() != 0)
        return retval;
    }
    // index
    else if (this.getParameters().get(1).compareTo("index") == 0) {
      ReturnValue retval = FileTools.verifyFile(new File(this.getParameters()
          .get(this.getParameters().size() - 1)
          + ".bai"));
      if (retval.getExitStatus() != 0)
        return retval;
    }
    // pileup
    else if (this.getParameters().get(1).compareTo("pileup") == 0) {
      ReturnValue retval = FileTools.verifyFile(this.getStdoutFile());
      if (retval.getExitStatus() != 0)
        return retval;
    }
    else if (this.getParameters().get(1).compareTo("view") == 0) {
      ReturnValue retval = FileTools.verifyFile(this.getStdoutFile());
      if (retval.getExitStatus() != 0)
        return retval;
    }
    else {
      return new ReturnValue(null, "do_verify_output not implemented for "
          + this.getParameters().get(1), ReturnValue.NOTIMPLEMENTED);
    }

    // Otherwise all verifications succeeded so return success
    return new ReturnValue(null, null, 0);
  }

  /** {@inheritDoc} */
  @Override
  public String get_syntax() {
    StringBuffer ReturnString = new StringBuffer(
        "Module to Samtools. Samtools syntax:"
            + System.getProperty("line.separator"));
    // Module return codes, then print Samtools help for module if valid or
    // Samtools in general if not valid
    /*
     * Module Return values: 0: success 1: Other Samtools error 2: invalid
     * Samtools command 3: Missing parameters 4: Caught IOException 5: Caught
     * InterruptedException 6: Other Samtools error
     */
    Process p = null;
    try {
      if (this.getParameters().size() > 1) {
        p = Runtime.getRuntime().exec(
            this.getParameters().get(0) + " " + this.getParameters().get(1));
      } else {
        p = Runtime.getRuntime().exec(this.getParameters().get(0));
      }
    } catch (IOException e) {
      return new String(
          "Exception occured when trying to get_syntax from Samtools. Please make sure it is setup properly."
              + System.getProperty("line.separator") + e.toString());
    }

    try {
      p.waitFor();
    } catch (InterruptedException e) {
      return new String(
          "Exception occured when trying to get_syntax from Samtools. Please make sure it is setup properly."
              + System.getProperty("line.separator") + e.toString());
    }

    // If there was a param, let's make sure it was valid
    String stderr = null;
    try {
      stderr = StdErr.stderr2string(p);
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    if (this.getParameters().size() > 1) {
      String invalid = new String("[main] unrecognized command '"
          + this.getParameters().get(1) + "'"
          + System.getProperty("line.separator"));

      if (stderr.compareTo(invalid) == 0) {
        try {
          p = Runtime.getRuntime().exec(this.getParameters().get(0));
        } catch (IOException e) {
          return new String(
              "Exception occured when trying to get_syntax from Samtools. Please make sure it is setup properly."
                  + System.getProperty("line.separator") + e.toString());
        }
        try {
          p.waitFor();
        } catch (InterruptedException e) {
          return new String(
              "Exception occured when trying to get_syntax from Samtools. Please make sure it is setup properly."
                  + System.getProperty("line.separator") + e.toString());
        }

        try {
          stderr = StdErr.stderr2string(p);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }

    // Return stderr as string
    ReturnString.append(stderr);
    return ReturnString.toString();
  }
}
