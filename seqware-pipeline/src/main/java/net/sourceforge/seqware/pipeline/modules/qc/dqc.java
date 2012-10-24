package net.sourceforge.seqware.pipeline.modules.qc;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;

import org.openide.util.lookup.ServiceProvider;


/**
 *
 * Purpose:
 *
 * This module generates pre and post alignment QC reports using
 * dqc from the DNAA package.
 *
 * FIXME: Sample of commands this actually runs:
 *
 * @author jmendler
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class dqc extends Module {
  private OptionSet options = null;
  
  /**
   * <p>getOptionParser.</p>
   *
   * @return a {@link joptsimple.OptionParser} object.
   */
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.acceptsAll( Arrays.asList("pre"), "Do pre-alignment QC");
    parser.acceptsAll( Arrays.asList("post"),"Do post-alignment QC");
    parser.acceptsAll( Arrays.asList("path-to-R", "R"),"Required: The path to the R binary").withRequiredArg().describedAs("R binary");
    parser.acceptsAll( Arrays.asList("path-to-dqc", "dqc"),"Required: The path to the dqc binary").withRequiredArg().describedAs("dqc binary");
    parser.acceptsAll( Arrays.asList("output-prefix", "o"),"Required for pre and post: Prefix for output files").withRequiredArg().describedAs("Prefix");

    // Pre-align Options
    parser.acceptsAll( Arrays.asList("color-space", "cs"),"Required for pre: Which space to use").withRequiredArg().describedAs("0 for native space, 1 for color space").ofType(Integer.class);
    parser.acceptsAll( Arrays.asList("input-fastq", "if"),"Required for pre: Input file").withRequiredArg().describedAs("Path to fastq input");
    
    // Post-align options
    parser.acceptsAll( Arrays.asList("fasta-file", "f"),"Required for post: Path to fasta file").withRequiredArg().describedAs("Fasta file");
    parser.acceptsAll( Arrays.asList("insert-size-range", "r"),"Required for post: Insert size range").withRequiredArg().describedAs("e.g. -5000:5000");
    parser.acceptsAll( Arrays.asList("minimum-mapping-quality", "m"),"Required for post: Minimum mapping quality").withRequiredArg().describedAs("INT").ofType(Integer.class);
    parser.acceptsAll( Arrays.asList("input-bam", "ib"),"Required for post: Input file").withRequiredArg().describedAs("Path to BAM input");

    return(parser);
  }

  
  
  /**
   * <p>get_syntax.</p>
   *
   * @return a {@link java.lang.String} object.
   */
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
   * Things to check:
   * *
   */
  @Override
  public ReturnValue do_test() {
    // FIXME
    return new ReturnValue(ReturnValue.NOTIMPLEMENTED);    
  }
  
  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_parameters() {
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    try {
      OptionParser parser = getOptionParser();
      
      options = parser.parse(this.getParameters().toArray(new String[0]));      
    } catch (OptionException e) {
       ret.setStderr(e.getMessage() + System.getProperty("line.separator") + this.get_syntax() );
       ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
       return ret;
    }
    
    // Must have pre and/or post option
    if ( ! (options.has("pre") || options.has("post") )) {
      ret.setStderr("Must specify --pre and/or --post option" + System.getProperty("line.separator") + this.get_syntax());
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
      return ret;
    }
    
    // Must specify a path to R and dqc
    if ( ! (options.has("R") && options.has("dqc") && options.has("output-prefix") )) {
      ret.setStderr("Must specify both --R and --dqc paramateres for the respective binaries, as well as a --output-prefix option" + System.getProperty("line.separator") + this.get_syntax());
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
      return ret;
    }
    
    
    // Check pre/post specific options
    if ( options.has("pre") ) {
      if ( ! (options.has("color-space") && options.has("input-fastq")) ) {
        ret.setStderr("When running pre-alignment qc (with --pre), must specify a --color-space option and one or more --input-fastq files" + System.getProperty("line.separator") + this.get_syntax());
        ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        return ret;
      }
    }
    if ( options.has("post") ) {
      if ( ! (options.has("fasta-file") && options.has("insert-size-range") && options.has("minimum-mapping-quality") && options.has("input-bam")) ) {
        ret.setStderr("When running post-alignment qc (with --post), must specify a --fasta-file, --insert-size-range, --minimum-mapping-quality and one or more --input-bam files" + System.getProperty("line.separator") + this.get_syntax());
        ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        return ret;
      }
    }
    
    // Otherwise no problems so return success
    return(ret);
  }

  // FIXME: Got until here with wrapping, still need to do the rest
  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_input() {
    return new ReturnValue(ReturnValue.NOTIMPLEMENTED);
  }
  
  /** {@inheritDoc} */
  @Override
  public ReturnValue do_run() {
    return new ReturnValue(ReturnValue.NOTIMPLEMENTED);
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_output() {
    return new ReturnValue(ReturnValue.NOTIMPLEMENTED); 
  }
}
