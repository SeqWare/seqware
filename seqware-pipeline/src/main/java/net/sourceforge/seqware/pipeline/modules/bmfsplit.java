package net.sourceforge.seqware.pipeline.modules;

import java.io.File;
import java.io.IOException;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.processtools.stderr.StdErr;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;
import net.sourceforge.seqware.common.util.Log;

import org.openide.util.lookup.ServiceProvider;

/**
 * <p>bmfsplit class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class bmfsplit extends Module {

  // FIXME: Should remove all instances of getRuntime.exec and replace with runCommand, for this and all other classes
  /** {@inheritDoc} */
  @Override
  public ReturnValue do_run() {
    Process p = null;

    Log.error("Starting splitBMF");// FIXME

    /* Run bmfsplit */
    try {
      String[] arguments = new String[this.getParameters().size()];
      this.getParameters().toArray(arguments);
      p = Runtime.getRuntime().exec(arguments);
    } catch (IOException e) {
      return new ReturnValue(null, e.toString(), 1);
    }

    // Wait on process
    try {
      p.waitFor();
    } catch (InterruptedException e) {
      p.destroy();
      return new ReturnValue(null, e.toString(), 1);
    }

    if (p.exitValue() != 0) {
      try {
        return new ReturnValue(null, StdErr.stderr2string(p), 2);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    Log.error("bmfsplit done");

    return new ReturnValue();
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_parameters() {
    if (this.getParameters().size() != 4) {
      return new ReturnValue(null, "bmfsplit requires exactly 3 arguments", 1);
    }

    if (!this.getParameters().get(1).endsWith(".bmf"))
      return new ReturnValue(null,
          "the first argument to bmfsplit must be a bmf file", 1);

    try {
      Integer.parseInt(this.getParameters().get(2));
    } catch (NumberFormatException e) {
      return new ReturnValue(null,
          "the second argument to bmfsplit must be a number", 1);
    }

    if (this.getParameters().get(3) == null)
      return new ReturnValue(null,
          "bmfsplit must have a third argument that is the prefix", 1);

    // Otherwise return success
    return new ReturnValue();
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_input() {
    ReturnValue retval = FileTools.verifyFile(new File(this.getParameters()
        .get(1)));
    if (retval.getExitStatus() != 0)
      return retval;

    // Otherwise all verifications succeeded so return success
    return new ReturnValue();
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_output() {
    for (int i = 0; i < Integer.parseInt(this.getParameters().get(2)); i++) {
      String output = this.getParameters().get(3) + "." + i + ".bmf";
      ReturnValue retval = FileTools.verifyFile(new File(output));
      if (retval.getExitStatus() != 0)
        return retval;
    }

    // Otherwise all verifications succeeded so return success
    return new ReturnValue();
  }

  /** {@inheritDoc} */
  @Override
  public String get_syntax() {
    StringBuffer ReturnString = new StringBuffer(
        "Module to bmfsplit. bmfsplit syntax:"
            + System.getProperty("line.separator"));

    Process p = null;
    try {
      p = Runtime.getRuntime().exec(this.getAlgorithm());
    } catch (IOException e) {
      return new String(
          "Exception occured when trying to get_syntax from bmfsplit. Please make sure it is setup properly."
              + System.getProperty("line.separator") + e.toString());
    }
    try {
      p.waitFor();
    } catch (InterruptedException e) {
      return new String(
          "Exception occured when trying to get_syntax from bmfsplit. Please make sure it is setup properly."
              + System.getProperty("line.separator") + e.toString());
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

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_test() {
    // TODO Auto-generated method stub
    return ReturnValue.featureNotImplemented();
  }
}
