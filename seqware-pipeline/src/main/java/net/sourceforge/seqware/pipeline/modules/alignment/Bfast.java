package net.sourceforge.seqware.pipeline.modules.alignment;

import java.io.File;
import java.io.IOException;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.processtools.stderr.StdErr;
import net.sourceforge.seqware.common.util.runtools.RunTools;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;

import org.apache.commons.lang.StringUtils;
import org.openide.util.lookup.ServiceProvider;

/**
 * <p>Bfast class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class Bfast extends Module {

  /** {@inheritDoc} */
  @Override
  public ReturnValue init() {
    // Bfast has different modules (i.e. match, localalign), so first figure out
    // which was requested
    if (this.getParameters().size() > 1) {
      this
          .setAlgorithm(this.getAlgorithm() + " " + this.getParameters().get(1)); // algorithm
      // with
      // be
      // "binary arg[1]"
      return new ReturnValue();
    } else
      return new ReturnValue(null,
          "Bfast requires an arg[1] to specify which module to run", 2);
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_parameters() {
    ReturnValue ret = new ReturnValue();

    // Verify arg[1] exists. If it does not return error and bfast help message.
    if (this.getParameters().size() < 2) {
      StringBuffer syntax = new StringBuffer(
          "To run bfast you must specify atleast one command. Usage:"
              + System.getProperty("line.separator"));
      syntax.append(get_syntax());
      ret.setStderr(syntax.toString());
      ret.setExitStatus(2);
      return ret;
    }

    // Verify arg[1] is valid. Bfast will return non-zero if arg[1] is not
    // supported.
    // FIXME: This should use RunTools instead of getRuntime.exec
    try {
      String[] args = { this.getParameters().get(0),
          this.getParameters().get(1) };
      Process p = Runtime.getRuntime().exec(args);
      p.waitFor();
      // Capture exit status
      ret.setExitStatus(p.exitValue());
      // If exit is non-zero we have a problem
      if (ret.getExitStatus() != 0) {
        StringBuffer syntax = new StringBuffer(this.getParameters().get(1)
            + " is not a valid command for bfast. Usage:"
            + System.getProperty("line.separator"));
        syntax.append(get_syntax());
        ret.setStderr(syntax.toString());
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
    // Bfast writes to stdout, so make sure it was captured
    if (this.getStdoutFile() == null) {
      ret
          .setStderr("Bfast writes results to stdout, so you must redirect stdout to a file in order to use it. See the -o option for the seqware runner.");
      ret.setExitStatus(ReturnValue.STDOUTERR);
      return ret;
    }

    // We must have a -f hg18.fa
    boolean valid = false;
    for (int i = 2; i < this.getParameters().size(); i++) {
      if (this.getParameters().get(i).compareTo("-f") == 0) {
        if (this.getParameters().get(i + 1).endsWith(".fa")) {
          valid = true;
          break;
        }
      }
    }

    if (valid == false) {
      ret.setExitStatus(3);
      ret.setStderr("Bfast always requires a '-f file.fa' argument");
      return ret;
    }

    // checks parameters for appropriate bfast commands
    if (this.getParameters().get(1).compareTo("match") == 0) {
      // Match requires readsFileName
      valid = false;
      for (int i = 2; i < this.getParameters().size(); i++) {
        if (this.getParameters().get(i).compareTo("-r") == 0) {
          if (this.getParameters().get(i + 1).endsWith(".fastq")) {
            valid = true;
            break;
          }
        }
      }

      if (valid == false) {
        ret.setExitStatus(3);
        ret.setStderr("Bfast match always requires a '-r file.fastq' argument");
        return ret;
      }
    } else if (this.getParameters().get(1).compareTo("localalign") == 0) {
      valid = false;
      for (int i = 2; i < this.getParameters().size(); i++) {
        if (this.getParameters().get(i).compareTo("-m") == 0) {
          if (this.getParameters().get(i + 1).endsWith(".bmf")) {
            valid = true;
            break;
          }
        }
      }

      if (valid == false) {
        ret.setExitStatus(3);
        ret
            .setStderr("Bfast localalign always requires a '-m file.bmf' argument");
        return ret;
      }
    } else if (this.getParameters().get(1).compareTo("postprocess") == 0) {
      valid = false;
      for (int i = 2; i < this.getParameters().size(); i++) {
        if (this.getParameters().get(i).compareTo("-i") == 0) {
          if (this.getParameters().get(i + 1).endsWith(".baf")) {
            valid = true;
            break;
          }
        }
      }

      if (valid == false) {
        ret.setExitStatus(3);
        ret
            .setStderr("Bfast postprocess always requires a '-i file.baf' argument");
        return ret;
      }
    }

    // If we did not return there were no problems
    ret.setExitStatus(0);
    return ret;
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_run() {
    /* Run Bfast */
    // Prepare argument array
    String cmd = StringUtils.join( this.getParameters(), ' ' );
    
    // FIXME: instead of redirecting stderr here, runCommand should thread and grab it. Otherwise it fills up the buffer.
    if ( this.getStdoutFile() != null ) {
      cmd += " > " + this.getStdoutFile().getAbsolutePath();
      cmd += " 2> " + this.getStdoutFile().getAbsolutePath() + ".stderr";
      
      // Launch the process
      return RunTools.runCommand( new String[] { "bash", "-c", cmd } );
    }
    else {
      return RunTools.runCommand( cmd );
    }
  }

  /** {@inheritDoc} */
  @Override
  // Instead of oding checks here, call util method to verify files, as in
  // samtools
  public ReturnValue do_verify_input() {
    // Everything requires a .fa file
    for (int i = 2; i < this.getParameters().size(); i++) {
      if (this.getParameters().get(i).endsWith(".fa")) {
        // FIXME: Should check appropriate .fa.* files based on algorithm, For
        // now, skip it.
      }
    }

    // For matches, verify fastq file
    if (this.getParameters().get(1).compareTo("match") == 0) {
      for (int i = 2; i < this.getParameters().size(); i++) {
        if (this.getParameters().get(i).endsWith(".fastq")) {
          // FIXME: For now, make sure it is readable and non-zero. Should have
          // an actual test.
          File file = new File(this.getParameters().get(i));
          if (!(file.exists() && file.canRead() && file.length() > 0)) {
            return new ReturnValue(null,
                ".fastq file must be readable and non-zero", 1);
          }
        }
      }
    }
    // For localalign, verify bmf
    else if (this.getParameters().get(1).compareTo("localalign") == 0) {
      for (int i = 2; i < this.getParameters().size(); i++) {
        if (this.getParameters().get(i).endsWith(".bmf")) {
          // FIXME: For now, make sure it is readable and non-zero. Should have
          // an actual test.
          File file = new File(this.getParameters().get(i));
          if (!(file.exists() && file.canRead() && file.length() > 0)) {
            return new ReturnValue(null,
                ".bmf file must be readable and non-zero", 1);
          }
        }
      }
    }
    // For postprocess, verify baf
    else if (this.getParameters().get(1).compareTo("postprocess") == 0) {
      for (int i = 2; i < this.getParameters().size(); i++) {
        if (this.getParameters().get(i).endsWith(".baf")) {
          // FIXME: For now, make sure it is readable and non-zero. Should have
          // an actual test.
          File file = new File(this.getParameters().get(i));
          if (!(file.exists() && file.canRead() && file.length() > 0)) {
            return new ReturnValue(null,
                ".baf file must be readable and non-zero", 1);
          }
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
    if (this.getParameters().get(1).compareTo("match") == 0) {
      // FIXME: bfast match -r
      // /scratch0/tmp/jordan/solexa/truncated/bfast.verysmall.fastq -f
      // /scratch0/bfast/genomes/custom/hg18.chrX.31000000-38000000/hg18.fa -n 8
      // > expected.bmf
    } else if (this.getParameters().get(1).compareTo("localalign") == 0) {
      // FIXME: bfast localalign -f
      // /scratch0/bfast/genomes/custom/hg18.chrX.31000000-38000000/hg18.fa -m
      // expected.bmf -n 8 > expected.baf
    } else if (this.getParameters().get(1).compareTo("postprocess") == 0) {
      // FIXME: bfast postprocess -f
      // /scratch0/bfast/genomes/custom/hg18.chrX.31000000-38000000/hg18.fa -i
      // expected.baf a 3 -O 3 > expected.sam
    }

    return ret;
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_output() {
    // For matches, verify bmf file
    if (this.getParameters().get(1).compareTo("match") == 0) {
      if (!(this.getStdoutFile().exists() && this.getStdoutFile().length() > 0)) {
        // FIXME: For now, make sure it is readable and non-zero. Should have an
        // actual test.
        return new ReturnValue(null, ".bmf file must be readable and non-zero",
            1);
      }
    }
    // For localalign, verify baf
    else if (this.getParameters().get(1).compareTo("localalign") == 0) {
      if (!(this.getStdoutFile().exists() && this.getStdoutFile().length() > 0)) {
        // FIXME: For now, make sure it is readable and non-zero. Should have an
        // actual test.
        return new ReturnValue(null, ".baf file must be readable and non-zero",
            1);
      }
    }
    // For postprocess, verify sam
    else if (this.getParameters().get(1).compareTo("postprocess") == 0) {
      if (!(this.getStdoutFile().exists() && this.getStdoutFile().length() > 0)) {
        // FIXME: For now, make sure it is readable and non-zero. Should have an
        // actual test.
        return new ReturnValue(null, ".sam file must be readable and non-zero",
            1);
      }
    }

    // Otherwise all verifications succeeded so return success
    return new ReturnValue(null, null, 0);
  }

  /** {@inheritDoc} */
  @Override
  public String get_syntax() {
    StringBuffer ReturnString = new StringBuffer(
        "Module to Bfast. Bfast syntax:"
            + System.getProperty("line.separator"));
    // Module return codes, then print bfast help for module if valid or bfast
    // in general if not valid
    /*
     * Module Return values: 0: success 1: Other bfast error 2: invalid bfast
     * command 3: Missing parameters 4: Caught IOException 5: Caught
     * InterruptedException 6: Other bfast error
     */
    Process p = null;
    try {
      if (this.getParameters().size() > 1) {
        p = Runtime.getRuntime().exec(
            this.getAlgorithm() + " " + this.getParameters().get(1));
      } else {
        p = Runtime.getRuntime().exec(this.getAlgorithm());
      }
    } catch (IOException e) {
      return new String(
          "Exception occured when trying to get_syntax from bfast. Please make sure it is setup properly."
              + System.getProperty("line.separator") + e.toString());
    }
    try {
      p.waitFor();
    } catch (InterruptedException e) {
      return new String(
          "Exception occured when trying to get_syntax from bfast. Please make sure it is setup properly."
              + System.getProperty("line.separator") + e.toString());
    }

    // If this returned 0, return the help message show, otherwise return main
    // bfast help
    if (p.exitValue() != 0 && this.getParameters().size() > 1) {
      try {
        p = Runtime.getRuntime().exec("bfast");
      } catch (IOException e) {
        return new String(
            "Exception occured when trying to get_syntax from bfast. Please make sure it is setup properly."
                + System.getProperty("line.separator") + e.toString());
      }
      try {
        p.waitFor();
      } catch (InterruptedException e) {
        return new String(
            "Exception occured when trying to get_syntax from bfast. Please make sure it is setup properly."
                + System.getProperty("line.separator") + e.toString());
      }
    }

    // Return stderr as string
    try {
      ReturnString.append(StdErr.stderr2string(p));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return ReturnString.toString();
  }
}
