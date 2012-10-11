package net.sourceforge.seqware.pipeline.modules.examples;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;

import net.sourceforge.seqware.pipeline.module.Module;

public class echo extends Module {

  @Override
  public ReturnValue do_run() {
    ReturnValue ret;

    try {
      this.getParameters().add(0, "echo");
      String params[] = new String[getParameters().size()];
      getParameters().toArray(params);
      
      StringBuffer myOut = new StringBuffer();

      Process p = Runtime.getRuntime().exec(params);

      p.waitFor();

      BufferedReader stdout = new BufferedReader(new InputStreamReader(p
          .getInputStream()));
      String output = null;

      while ((output = stdout.readLine()) != null) {
        myOut.append(output + "\n");
      }
      
      ret = new ReturnValue(myOut.toString(), null, p.exitValue());

      // If supposed to write to a file
      if ( this.getStdoutFile() != null ) {
        this.getStdoutFile().createNewFile();
        BufferedWriter file = new BufferedWriter(new FileWriter(this.getStdoutFile()) );
        file.write( myOut.toString() );
        file.close();
        myOut = null;
        
        ret.getFiles().add(new FileMetadata(this.getStdoutFile().getAbsolutePath(), "echo file"));
        ret.setStdout(null);
      }
    } catch (IOException e) {
      // Set error on exception
      ret = new ReturnValue(null, e.getStackTrace().toString(), 1);
    } catch (InterruptedException e) {
      // Set error on exception
      ret = new ReturnValue(null, e.getStackTrace().toString(), 2);
    }

    return ret;
  }

  @Override
  public ReturnValue do_test() {
    ReturnValue ret = new ReturnValue();

    try {
      Process p = Runtime.getRuntime().exec("echo hello");
      p.waitFor();
      // Capture output from STDIN and STDOUT and make sure it matches hello
      BufferedReader stdout = new BufferedReader(new InputStreamReader(p
          .getInputStream()));
      String output = stdout.readLine().toString();

      // Check for errors
      if (p.exitValue() != 0) {
        ret.setExitStatus(p.exitValue());
      } else if (output.compareTo("hello") != 0) {
        ret.setExitStatus(1);
        ret.setStderr(new String(
            "Test failed: 'echo hello' did not print 'hello' to STDOUT"));
      }
      // No problems
      else {
        ret.setExitStatus(p.exitValue());
      }
    } catch (IOException e) {
      ret.setExitStatus(2);
      ret.setStderr(e.toString());
    } catch (InterruptedException e) {
      ret.setExitStatus(3);
      ret.setStderr(e.toString());
    }

    return ret;
  }

  @Override
  public String get_syntax() {
    return new String("Module to echo.\nSyntax: echo hello world\n");
  }

  @Override
  public ReturnValue do_verify_input() {
    // TODO Auto-generated method stub
    return ReturnValue.featureNotImplemented();
  }

  @Override
  public ReturnValue do_verify_output() {
    // TODO Auto-generated method stub
    return ReturnValue.featureNotImplemented();
  }

  @Override
  public ReturnValue do_verify_parameters() {
    // TODO Auto-generated method stub
    return ReturnValue.featureNotImplemented();
  }
  
  @Override
  public ReturnValue init() {
    ReturnValue ret = new ReturnValue();
    ret.setAlgorithm("echo");
    return ret;
  }
}
