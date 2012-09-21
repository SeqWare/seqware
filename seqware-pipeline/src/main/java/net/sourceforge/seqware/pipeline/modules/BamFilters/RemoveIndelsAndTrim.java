package net.sourceforge.seqware.pipeline.modules.BamFilters;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.runtools.RunTools;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;

import org.openide.util.lookup.ServiceProvider;

/**
 * 
 * Purpose:
 * 
 * Module to wrap remove_indels_and_trim script.
 * 
 * @author jmendler
 * 
 */
@ServiceProvider(service=ModuleInterface.class)
public class RemoveIndelsAndTrim extends Module {
  private OptionSet options = null;

  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();

    parser.acceptsAll(Arrays.asList("input", "i"), "Required: The input file").withRequiredArg().describedAs("Input File");
    parser.acceptsAll(Arrays.asList("output", "o"), "Required: The output file").withRequiredArg().describedAs("Output File");
    parser.acceptsAll(Arrays.asList("fasta", "f"),  "Required: The reference fasta file").withRequiredArg().describedAs("Fasta file");
    parser.acceptsAll(Arrays.asList("filter-script"), "Required: The filter script").withRequiredArg().describedAs("Executable filter script");
    parser.acceptsAll(Arrays.asList("trim-size"), "Required: Number of bases to trim off").withRequiredArg().ofType(Integer.class).describedAs("Integer");
    parser.acceptsAll(Arrays.asList("samtools-binary"), "Required: Samtools binary location").withRequiredArg().describedAs("Path to the samtools binary");
    parser.acceptsAll(Arrays.asList("perl-path"), "Required: Perl binary location").withRequiredArg().describedAs("Path to the Perl binary with includes");

    return (parser);
  }

  public String get_syntax() {
    OptionParser parser = getOptionParser();
    StringWriter output = new StringWriter();
    try {
      parser.printHelpOn(output);
      return (output.toString());
    } catch (IOException e) {
      e.printStackTrace();
      return (e.getMessage());
    }
  }

  @Override
  public ReturnValue do_verify_parameters() {
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    try {
      OptionParser parser = getOptionParser();

      options = parser.parse(this.getParameters().toArray(new String[0]));
    } catch (OptionException e) {
      ret.setStderr(e.getMessage() + System.getProperty("line.separator")
          + this.get_syntax());
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
      return ret;
    }
    
    for ( String arg : new String[]{ "fasta", "input", "output", "filter-script", "samtools-binary", "trim-size", "perl-path" } ) {
      if (! options.has(arg)) {
        ret.setStderr("Must specify all arguments"
            + System.getProperty("line.separator") + this.get_syntax());
        ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        return ret;    
      }
    }
    
    // Otherwise no problems so return success
    return (ret);
  }

  // FIXME: Got until here with wrapping, still need to do the rest
  @Override
  public ReturnValue do_verify_input() {
    for ( String arg : new String[]{ "fasta", "input", "filter-script", "samtools-binary" } ) {
      if (! options.has(arg)) {
        
        // Verify input file and binary
        ReturnValue ret = FileTools.verifyFile(new File((String) options.valueOf(arg)));
        if (ret.getExitStatus() != 0) {
          return ret;
        }
      }
    }

    return new ReturnValue();
  }

  @Override
  public ReturnValue do_verify_output() {
    // Verify output file
    return FileTools.verifyFile(new File((String) options.valueOf("output")));
  }

  @Override
  public ReturnValue do_test() {
    return new ReturnValue(ReturnValue.NOTIMPLEMENTED);
  }

  @Override
  public ReturnValue do_run() {
    StringBuffer cmd = new StringBuffer();
    cmd.append(options.valueOf("samtools-binary") + " view " + options.valueOf("input") + " | ");
    cmd.append(options.valueOf("perl-path") + " " + options.valueOf("filter-script") + " " + options.valueOf("trim-size") + " | " );
    cmd.append(options.valueOf("samtools-binary") + " import " + options.valueOf("fasta") + " - " + options.valueOf("output") );
    
    return RunTools.runCommand( new String[] { "bash", "-c", cmd.toString() } );
  }
}
