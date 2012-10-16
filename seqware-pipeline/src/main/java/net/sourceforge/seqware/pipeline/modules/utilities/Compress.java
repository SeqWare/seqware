package net.sourceforge.seqware.pipeline.modules.utilities;

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
 * This module extends allows for compressing and decompressing files.
 * It is designed to be an abstraction from pbzip2, bzip2, gzip, pigz, etc.
 *
 * Notes/TODO/FIXME:
 *
 * * For now it has only been tested with pbzip2
 *
 * @author jmendler
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class Compress extends Module {

  private OptionSet options = null;
  
  /**
   * <p>getOptionParser.</p>
   *
   * @return a {@link joptsimple.OptionParser} object.
   */
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.acceptsAll( Arrays.asList("input", "i"), "Required: input file location" ).withRequiredArg().describedAs("input file path");
    parser.acceptsAll( Arrays.asList("output", "o"),"Required: output file location" ).withRequiredArg().describedAs("output file path");
    parser.acceptsAll( Arrays.asList("binary", "b"),"Required: Binary file for compression" ).withRequiredArg().describedAs("compression program path");
    parser.acceptsAll( Arrays.asList("compress", "c"), "Specifies we want to compress the file (Cannot be combined with --decompress)" );
    parser.acceptsAll( Arrays.asList("decompress", "d"), "Specifies we want to decompress the file (Cannot be combined with --compress)" );
    parser.acceptsAll( Arrays.asList("keep", "k"), "Specifies we want to keep the original file (Default: Remove the original input file after compression)");
    
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
   * * FIXME
   */
  @Override
  public ReturnValue do_test() {  
    return new ReturnValue(ReturnValue.NOTIMPLEMENTED);    
  }
  
  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_parameters() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    try {
      OptionParser parser = getOptionParser();
      
      // can use testng where needed e.g.
      // Assert.assertNotNull(store);
     
      options = parser.parse(this.getParameters().toArray(new String[0]));      
    } catch (OptionException e) {
       ret.setStderr(e.getMessage() + System.getProperty("line.separator") + this.get_syntax() );
       ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
       return ret;
    }
    
    // Can't have both compress and decompress
    if ( options.has("compress") && options.has("decompress") ) {
      ret.setStderr("Cannot set both --compress/-c and --decompress/-d options" + System.getProperty("line.separator") + this.get_syntax() );
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
      return ret;
    }
    
    // Require either --compress or --decompress
    if ( ! (options.has("compress") || options.has("decompress") )) {
      ret.setStderr("Must specify either --compress/-c or --decompress/-d options" + System.getProperty("line.separator") + this.get_syntax());
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
      return ret;
    }
    
    // Must specify input, output and binary file
    for ( String requiredOption : new String[]{ "input", "output", "binary" } ) {
      if ( ! options.has(requiredOption) ) {
        ret.setStderr("Must specify a --" + requiredOption +  " or -" + requiredOption.charAt(0) +  " option" + System.getProperty("line.separator") + this.get_syntax());
        ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        return ret;
      }
    }
    
    return(ret);
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_input() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);

    // Verify input file, binary and output file
    if ( FileTools.fileExistsAndReadable(new File((String) options.valueOf("input") )).getExitStatus() != ReturnValue.SUCCESS ) {
      return new ReturnValue(null, "Cannot find input file: " + options.valueOf("input"), ReturnValue.FILENOTREADABLE);
    }
    
    if ( FileTools.fileExistsAndReadable(new File((String) options.valueOf("binary") )).getExitStatus() != ReturnValue.SUCCESS ) {
      return new ReturnValue(null, "Cannot find binary: " + options.valueOf("binary"), ReturnValue.FILENOTREADABLE);
    }
    
    File output = new File( (String) options.valueOf("output") );
    if (FileTools.dirPathExistsAndWritable( output.getParentFile() ).getExitStatus() != ReturnValue.SUCCESS) {
        ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
        ret.setStderr("Can't write to output directory");
        return (ret);
    }    
    
    return (ret);
  }
  
  /** {@inheritDoc} */
  @Override
  public ReturnValue do_run() {
    // Start with binary
    StringBuffer cmd = new StringBuffer();
    cmd.append((String) options.valueOf("binary") + " -c " );
    
    // Add option arguments
    if ( options.has("decompress") ) {
      cmd.append(" -d ");
    }
    if ( options.has("keep") ) {
      cmd.append(" -k ");
    }

    // Add filename to compress/decompress
    cmd.append( (String) options.valueOf("input") + " ");
    
    // Add output redirection to output file
    cmd.append( " > " + (String) options.valueOf("output") );
    
    return RunTools.runCommand( new String[] { "bash", "-c", cmd.toString() } );
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_output() {
    return(FileTools.verifyFile(new File((String) options.valueOf("output"))));
  }
}
