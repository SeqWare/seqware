package net.sourceforge.seqware.pipeline.modules.queryengine;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.runtools.RunTools;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;

import org.openide.util.lookup.ServiceProvider;

/**
 * 
 * Purpose:
 * 
 * This module uses the VariantConsequenceImporter and
 * TagAnnotationImporter from the SeqWare QueryEngine Backend project
 * to annotate with coding consequence and dbSNP status. The input
 * for this program is a zip BerkeleyDB and various annotation files.
 * The output is an annotated database that is zipped up again.
 * 
 * The overall flow for this module is to call:
 * <ul>
 *   <li>VariantConsequenceImporter (Java program)</li>
 *   <li>TagAnnotationImporter (Java program)</li>
 *   <li>checkpoint, clean and zip the database</li>
 * </ul>
 * 
 * Sample of commands this actually runs:
 * 
 * # consequence import
 * java -cp lib/db.jar:dist/seqware-qe-0.1.1.jar net.sourceforge.seqware.queryengine.tools.MutationImporter
 *  data/test/tools/MutationImporter/working/database true 2 50000 10 false true true 33 536870912
 *  data/test/tools/MutationImporter/MutationImporter_Sample.pileup; java -cp lib/db.jar:dist/seqware-qe-0.1.1.jar
 *  net.sourceforge.seqware.queryengine.tools.ConsequenceImporter data/test/tools/MutationImporter/working/database 536870912
 *  data/test/tools/MutationConsequenceImporter/MutationConsequenceImporter_Sample.txt
 * # dbSNP import
 * 
 * FIXME/TODO:
 * 
 * @author boconnor
 *
 */
@ServiceProvider(service=ModuleInterface.class)
public class AnnotateVariantDB extends Module {
//public class AnnotateVariantDB {

  private OptionSet options = null;
  private File tempDBDir = null;
  
  //@Override
  public ReturnValue init() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    try {
      
      OptionParser parser = getOptionParser();
      
      // FIXME
      options = parser.parse(getParameters().toArray(new String[0]));
      
      // create a temp directory
      tempDBDir = FileTools.createTempDirectory(new File(options.valueOf("local-temp-path").toString()));
      
    } catch (OptionException e) {
      ret.setStderr(e.getMessage());
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
   } catch (IOException e) {
     ret.setStderr(e.getMessage());
     ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
   }
   
   return(ret);
   
  }
  
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("consequence-file").withRequiredArg().describedAs("Consequence files produced by sw_map_snps_to_fxn_genomic_alignment.pl");
    parser.accepts("tag-file").withRequiredArg().describedAs("Tag files created by sw_filter_bed_by_dbSNP.pl.");
    parser.accepts("local-temp-path").withRequiredArg(); 
    parser.accepts("java-path").withRequiredArg();
    parser.accepts("java-library-path").withRequiredArg();
    parser.accepts("db-jar").withRequiredArg();
    parser.accepts("db-path").withRequiredArg();
    parser.accepts("db-library-path").withRequiredArg();
    parser.accepts("seqware-qe-backend-jar").withRequiredArg();
    parser.accepts("cache-size").withRequiredArg().ofType(Long.class);
    parser.accepts("locks").withRequiredArg().ofType(Long.class);
    parser.accepts("db-output-path").withRequiredArg();
    parser.accepts("db-output-file").withRequiredArg();
    parser.accepts("db-input-file").withRequiredArg();
    parser.accepts("database-backend").withRequiredArg().ofType(String.class).defaultsTo("BerkeleyDB");
    parser.accepts("reference-genome").withRequiredArg().ofType(String.class).defaultsTo("NA");
    parser.accepts("genome").withRequiredArg().ofType(String.class).defaultsTo("NA");
    return(parser);
  }
  
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
  
  @Override
  public ReturnValue do_test() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    // FIXME: could process a test dataset
    return(ret);
    
  }
  
  @Override
  public ReturnValue do_verify_parameters() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    // must have either a tag file or consequence-file
    if (!(options.has("consequence-file") || options.has("tag-file"))) {
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
      String stdErr = ret.getStderr();
      ret.setStderr(stdErr+"\nMust include one or more --consequence-file or --tag-file parameters\n");
      return(ret);
    }

    for (String option : new String[] {
        "local-temp-path", "java-path", "java-library-path", "db-jar", "db-path", "db-library-path", 
        "seqware-qe-backend-jar", "cache-size", "locks", "db-output-path", "db-output-file", "db-input-file", 
        "database-backend"
      }) {
      if (!options.has(option)) {
        ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        String stdErr = ret.getStderr();
        ret.setStderr(stdErr+"Must include parameter: --"+option+"\n");
      }
    }
    
    if ("BerkeleyDB".equals(((String)options.valueOf("database-backend")))) {
      if (!(options.has("cache-size") && options.has("locks") && options.has("db-output-path") && options.has("db-output-file") && options.has("db-input-file"))) {
        ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        String stdErr = ret.getStderr();
        ret.setStderr(stdErr+"\nMust include --cache-size, --locks, --db-output-path, --db-output-file, --db-input-file in options when using BerkeleyDB backend\n");
        return(ret);
      }
    } else if ("HBase".equals(((String)options.valueOf("database-backend")))) {
      if (!(options.has("reference-genome") && options.has("genome"))) {
        ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        String stdErr = ret.getStderr();
        ret.setStderr(stdErr+"\nMust include --reference-genome and --genome in options when using HBase backend\n");
        return(ret);
      }
    } else {
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
      String stdErr = ret.getStderr();
      ret.setStderr(stdErr+"\nDon't understand what --database-backend type: "+options.valueOf("database-backend")+" is!\n");
      return(ret);
    }
    return(ret);
  }
  
  @Override
  public ReturnValue do_verify_input() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    // DIRECTORIES
    
    // check that the following directories exist and are readable
    for (String path : new String[]{"java-library-path", "db-library-path"}) {
      if (FileTools.dirPathExistsAndReadable(new File((String)options.valueOf(path))).getExitStatus() != ReturnValue.SUCCESS) {
        ret.setExitStatus(ReturnValue.DIRECTORYNOTREADABLE);
        ret.setStderr("Can't read directory: "+options.valueOf(path));
        return (ret);
      }
    }
    
    // check that the following directories exist and are writable
    for (String path : new String[]{"local-temp-path", "db-output-path"}) {
      if (FileTools.dirPathExistsAndWritable(new File((String)options.valueOf(path))).getExitStatus() != ReturnValue.SUCCESS) {
        ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
        ret.setStderr("Can't write to directory: "+options.valueOf(path));
        return (ret);
      }
    }
    
    // FILES
    
    // check all the input consequence-files
    for (String path : (List<String>)options.valuesOf("consequence-file")) {
      if (FileTools.fileExistsAndReadable(new File(path)).getExitStatus() != ReturnValue.SUCCESS) {
        ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        ret.setStderr("Can't read file: "+path+" with value: "+options.valueOf(path));
        return (ret);
      }
    }
    
    // check all the input tag-files
    for (String path : (List<String>)options.valuesOf("tag-file")) {
      if (FileTools.fileExistsAndReadable(new File(path)).getExitStatus() != ReturnValue.SUCCESS) {
        ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        ret.setStderr("Can't read file: "+path+" with value: "+options.valueOf(path));
        return (ret);
      }
    }
    
    // check that the following files can be read
    for (String path : new String[]{
          options.valueOf("java-path")+File.separator+"java",
          options.valueOf("db-path")+File.separator+"db_checkpoint",
          options.valueOf("db-path")+File.separator+"db_archive",
          (String)options.valueOf("seqware-qe-backend-jar"),
          (String)options.valueOf("db-input-file"),
          (String)options.valueOf("db-jar")
        }) {
      if (FileTools.fileExistsAndReadable(new File(path)).getExitStatus() != ReturnValue.SUCCESS) {
        ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        ret.setStderr("Can't read file: "+path+" with value: "+options.valueOf(path));
        return (ret);
      }
    }
    
    return (ret);
  }
  
  @Override
  public ReturnValue do_run() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    // setup database
    File inputDB = new File((String)options.valueOf("db-input-file"));
    if (!FileTools.unzipFile(inputDB, tempDBDir)) {
        Log.error("Unzip failed");
      ret.setExitStatus(ReturnValue.PROGRAMFAILED);
      return(ret);
    }
    
    // load consequence files
    if (((List<String>)options.valuesOf("consequence-file")).size() > 0) {
      StringBuffer consBuffer = new StringBuffer(); 
      for (String consequence : (List<String>)options.valuesOf("consequence-file")) {
        consBuffer.append(consequence+" ");
      }

      ret = RunTools.runCommand(
          options.valueOf("java-path")+File.separator+"java -Djava.library.path="+
          options.valueOf("java-library-path")+" -cp "+options.valueOf("db-jar")+":"+options.valueOf("seqware-qe-backend-jar")+
          " net.sourceforge.seqware.queryengine.tools.importers.VariantConsequenceImporter "+
          options.valueOf("database-backend")+" "+options.valueOf("genome")+" "+options.valueOf("reference-genome")+" "+
          tempDBDir.getAbsolutePath()+" "+options.valueOf("cache-size")+" "+options.valueOf("locks")+" "+
          consBuffer.toString()
      );
      if (ret.getExitStatus() != ReturnValue.SUCCESS || ret.getProcessExitStatus() != ReturnValue.SUCCESS) {
          Log.error("Run command to load consequence files failed.");
          Log.error("Exit status was "+ret.getExitStatus()+ " and process exit status was "+ret.getProcessExitStatus());
        ret.setExitStatus(ReturnValue.PROGRAMFAILED);
        return(ret);
      }
    }

    // load dbSNP tag files
    if (((List<String>)options.valuesOf("tag-file")).size() > 0) {
      StringBuffer tagBuffer = new StringBuffer(); 
      for (String tag : (List<String>)options.valuesOf("tag-file")) {
        tagBuffer.append(tag+" ");
      }

      // FIXME: note the hardcoding of the mismatch table, although the script never uses this info for now, future scripts should support adding tags to any table
      ret = RunTools.runCommand(
          options.valueOf("java-path")+File.separator+"java -Djava.library.path="+
          options.valueOf("java-library-path")+" -cp "+options.valueOf("db-jar")+":"+options.valueOf("seqware-qe-backend-jar")+
          " net.sourceforge.seqware.queryengine.tools.importers.TagAnnotationImporter "+
          options.valueOf("database-backend")+" "+options.valueOf("genome")+" "+options.valueOf("reference-genome")+" "+
          tempDBDir.getAbsolutePath()+" mismatch "+options.valueOf("cache-size")+" "+options.valueOf("locks")+" "+
          tagBuffer.toString()
      );
      if (ret.getExitStatus() != ReturnValue.SUCCESS || ret.getProcessExitStatus() != ReturnValue.SUCCESS) {
          Log.error("Run command for mismatch table failed.");
          Log.error("Exit status was "+ret.getExitStatus()+ " and process exit status was "+ret.getProcessExitStatus());
        ret.setExitStatus(ReturnValue.PROGRAMFAILED);
        return(ret);
      }
    }
    
    // clean database
    if ("BerkeleyDB".equals(options.valueOf("database-backend"))) {
      HashMap<String, String> env = new HashMap<String,String>();
      env.put("LD_LIBRARY_PATH", (String)options.valueOf("db-library-path"));
      ret = RunTools.runCommand(
          env,
          options.valueOf("db-path")+File.separator+"db_checkpoint -1 -h "+tempDBDir.getAbsolutePath()
      );
      if (ret.getExitStatus() != ReturnValue.SUCCESS || ret.getProcessExitStatus() != ReturnValue.SUCCESS) {
        Log.error("Run command to clean database failed.");
        Log.error("Exit status was "+ret.getExitStatus()+ " and process exit status was "+ret.getProcessExitStatus());
        ret.setExitStatus(ReturnValue.PROGRAMFAILED);
        return(ret);
      }

      ret = RunTools.runCommand(
          env,
          options.valueOf("db-path")+File.separator+"db_archive -d -h "+tempDBDir.getAbsolutePath()
      );
      if (ret.getExitStatus() != ReturnValue.SUCCESS || ret.getProcessExitStatus() != ReturnValue.SUCCESS) {
          Log.error("Run command to archive db (?) files failed.");
          Log.error("Exit status was "+ret.getExitStatus()+ " and process exit status was "+ret.getProcessExitStatus());
        ret.setExitStatus(ReturnValue.PROGRAMFAILED);
        return(ret);
      }
    }
    
    // zip and backup database
    if (!FileTools.zipDirectoryRecursive(tempDBDir, new File(options.valueOf("db-output-path")+File.separator+options.valueOf("db-output-file")), "__db.", true, true)) {
     Log.error("Run command to zip and backup database failed.");
     Log.error("Exit status was "+ret.getExitStatus()+ " and process exit status was "+ret.getProcessExitStatus());
      ret.setExitStatus(ReturnValue.PROGRAMFAILED);
    }
    
    return(ret);
  }
  
  @Override
  public ReturnValue do_verify_output() {
    
    // FIXME: should check all files
    return(FileTools.fileExistsAndNotEmpty(new File(options.valueOf("db-output-path")+File.separator+options.valueOf("db-output-file"))));
    
  }
  
  @Override
  public ReturnValue clean_up() {
    ReturnValue ret = new ReturnValue();
    // FIXME: need to get the cleaned DB to destination then delete temp dir
    //FileTools.deleteDirectoryRecursive(tempDBDir);
    return(ret);
  }
}
