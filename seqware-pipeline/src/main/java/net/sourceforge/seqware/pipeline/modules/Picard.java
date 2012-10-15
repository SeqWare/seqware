package net.sourceforge.seqware.pipeline.modules;

import java.io.File;

import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.runtools.RunTools;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;

import org.apache.commons.lang.StringUtils;
import org.openide.util.lookup.ServiceProvider;

/**
 * <p>Picard class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class Picard extends Module {
  
  /** {@inheritDoc} */
  @Override
  public ReturnValue init() {
    // Picard has different modules, so first figure out which was requested. Strip off leading path and trailing '.jar'
    if (this.getParameters().size() > 0 ) {
      int locationOfSlash = this.getParameters().get(0).toString().lastIndexOf("/") + 1;
      
      if ( locationOfSlash == 1 ) {
        locationOfSlash = 0;
      }
      
      int locationOfJar = this.getParameters().get(0).toString().lastIndexOf(".jar");
      String algo = this.getParameters().get(0).toString().substring(locationOfSlash, locationOfJar);
      
      ReturnValue ret = new ReturnValue();
      ret.setAlgorithm(this.getAlgorithm() + " " + algo );
      
      return ret;
    } else
      return new ReturnValue(null,
          "Picard requires an arg[0] to specify which jar to run", 2);
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_parameters() {
    //FIXME: Need to verify parameters for each supported picard operation
    return new ReturnValue();
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_run() {
    // Prepare argument array
    String cmd = System.getenv("JAVA_HOME") + "/bin/" + "java -jar "; 
    cmd += StringUtils.join( this.getParameters(), ' ' );
    
    if ( this.getStdoutFile() != null ) {
      cmd += " > " + this.getStdoutFile().getAbsolutePath();
      
      // Launch the process
      return RunTools.runCommand( new String[] { "bash", "-c", cmd } );
    }
    else {
      return RunTools.runCommand( cmd );
    }
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_input() {
    // FIXME: Need to implement
    return new ReturnValue();
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_test() {
    // FIXME: Need to implement
    return new ReturnValue();
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_output() {
    ReturnValue ret = new ReturnValue();
    
    // Verify BAM output file was created, and set it in return value
    int outputFiles = 0;
    for ( String parameter: this.getParameters() ) {
      int equalChar = parameter.lastIndexOf("OUTPUT=");
      
      if ( equalChar != 0 ) {
        outputFiles++;
        String output = parameter.substring( equalChar + 1 );
        
        // Verify output file, and add if it exists and not zero
        ReturnValue verifyRet = FileTools.verifyFile(new File(output));
        
        if ( verifyRet.getExitStatus() == 0 ) { 
          ret.getFiles().add(new FileMetadata( output, "bam" ) );
        }
        else {
          return verifyRet; 
        }
      }
    }

    // If got down here, there we no issues verifying output, unless there was non
    if ( outputFiles > 0 ) {
      return ret;
    }
    else {
      return new ReturnValue(null, "No output files found", ReturnValue.INVALIDFILE );
    }
  }

  /** {@inheritDoc} */
  @Override
  public String get_syntax() {
    // FIXME: Need to implement
    return "";
  }
}
