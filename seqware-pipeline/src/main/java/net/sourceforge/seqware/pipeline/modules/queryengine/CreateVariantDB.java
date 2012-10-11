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
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.runtools.RunTools;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;
import net.sourceforge.seqware.common.util.Log;

import org.openide.util.lookup.ServiceProvider;


/**
 *
 * Purpose:
 *
 * This module serves to create a variant database from the SeqWare
 * Query Engine project that contains SNVs, small indels, and
 * coverage. The backend here is based on BerkeleyDB, eventually this
 * will use the HBase backend instead which is more distributable.
 * 
 * The overall flow for this module is to call
 * <ul>
 *   <li>VariantImporter</li>
 *   <li>PileupCoverageImporter</li>
 *   <li>AnnotateVariantsWithContigAndZygosity</li>
 *   <li>checkpoint, clean and zip the database</li>
 * </ul>
 * 
 * The input is essentially a pileup file and the output is a variant DB directory.
 * 
 * Sample of commands this actually runs:
 * 
 * /home/solexa/programs/jdk1.6.0_13/bin/java -Djava.library.path=/home/solexa/programs/BerkeleyDB.4.7/lib -cp /home/solexa/svnroot/seqware-complete/trunk/seqware-queryengine/backend/lib/db.jar:/home/solexa/svnroot/seqware-complete/trunk/seqware-queryengine/backend/dist/seqware-qe-0.4.0.jar net.sourceforge.seqware.queryengine.tools.importers.VariantImporter PileupImportWorker /state/partition1/tmp/solexa/HuRef/bestscore_no_pcr_duplicates_20091020/20100104.bbed.0.4.0 true 1 100000 10 true true true 33 17179869184 10000000 8 /scratch0/tmp/solexa/HuRef/pileup_by_chr/*.pileup.gz 
 * /home/solexa/programs/jdk1.6.0_13/bin/java -Djava.library.path=/home/solexa/programs/BerkeleyDB.4.7/lib -cp /home/solexa/svnroot/solexatools/solexa-queryengine/backend/lib/db.jar:/home/solexa/svnroot/solexatools/solexa-queryengine/backend/dist/seqware-qe-0.3.0.jar net.sourceforge.seqware.queryengine.tools.importers.PileupCoverageImporter /state/partition1/tmp/solexa/HuRef/bestscore_no_pcr_duplicates_20091020/20091021.bbed.coverage.0.3.0 17179869184 10000000 true 1000 /scratch0/tmp/solexa/HuRef/pileup_by_chr/*.pileup.gz
 * /home/solexa/programs/jdk1.6.0_13/bin/java -Djava.library.path=/home/solexa/programs/BerkeleyDB.4.7/lib -cp /home/solexa/svnroot/seqware-complete/trunk/seqware-queryengine/backend/lib/db.jar:/home/solexa/svnroot/seqware-complete/trunk/seqware-queryengine/backend/dist/seqware-qe-0.4.0.jar net.sourceforge.seqware.queryengine.tools.annotators.AnnotateVariantsWithContigAndZygosity /state/partition1/tmp/solexa/HuRef/bestscore_no_pcr_duplicates_20091020/20100104.bbed.0.4.0 17179869184 10000000
 * ./programs/BerkeleyDB.4.7/bin/db_checkpoint -1 -h /state/partition1/tmp/solexa/HuRef/bestscore_no_pcr_duplicates_20091020/20100104.bbed.0.4.0.clean
 * ./programs/BerkeleyDB.4.7/bin/db_archive -d -h /state/partition1/tmp/solexa/HuRef/bestscore_no_pcr_duplicates_20091020/20100104.bbed.0.4.0.clean
 *
 * Notes/TODO/FIXME:
 * 
 * * There are assumptions about some of the parameters, for example, assumption that pileups are all gz, that there are 8 cores for loading etc
 * * Need to parameterize and add more checking code.  
 * * FIXME: Need to create db in local temp path then copy it to final output. See if Pegasus can do this instead (so tempDir would be /state/partition1/tmp... and Pegasus would stage out of this to /scratch/...)
 * @author boconnor
 *
 */
@ServiceProvider(service=ModuleInterface.class)
public class CreateVariantDB extends Module {

  private OptionSet options = null;
  private File tempDir = null;
  
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("local-temp-path").withRequiredArg();
    parser.accepts("java-path").withRequiredArg();
    parser.accepts("java-library-path").withRequiredArg();
    parser.accepts("db-jar").withRequiredArg();
    parser.accepts("db-path").withRequiredArg();
    parser.accepts("db-library-path").withRequiredArg();
    parser.accepts("seqware-qe-backend-jar").withRequiredArg();
    parser.accepts("min-coverage").withRequiredArg().ofType(Integer.class);
    parser.accepts("max-coverage").withRequiredArg().ofType(Integer.class);
    parser.accepts("min-snp-quality").withRequiredArg().ofType(Integer.class);
    parser.accepts("fastq-conversion-num").withRequiredArg().ofType(Integer.class);
    parser.accepts("cache-size").withRequiredArg().ofType(Long.class);
    parser.accepts("locks").withRequiredArg().ofType(Long.class);
    parser.accepts("coverage-block-size").withRequiredArg().ofType(Integer.class);
    parser.accepts("pileup").withRequiredArg();
    parser.accepts("compressed-pileup").withRequiredArg();
    parser.accepts("db-output-path").withRequiredArg();
    parser.accepts("db-output-name").withRequiredArg();
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
  public ReturnValue init() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    try {
      
      OptionParser parser = getOptionParser();
      
      // can use testng where needed e.g.
      // Assert.assertNotNull(store);
     
      options = parser.parse(this.getParameters().toArray(new String[0]));
    
      // create a temp directory
      tempDir = FileTools.createTempDirectory(new File(options.valueOf("local-temp-path").toString()));
      
      Log.info("Temp dir: "+tempDir.getAbsolutePath());
      
    } catch (OptionException e) {
       e.printStackTrace();
       ret.setStderr(e.getMessage());
       ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
    } catch (IOException e) {
      e.printStackTrace();
      ret.setStderr(e.getMessage());
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
    }
    
    return(ret);
    
  }
  
  /**
   * Things to check:
   * * 
   */
  @Override
  public ReturnValue do_test() {
    
    /*
     * Things to test:
     * * version of programs
     */
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    // FIXME: could process a test dataset
    
    // check to make sure we're working with the correct version of SeqWare
    if (options.valueOf("seqware-qe-backend-jar").toString().indexOf(Module.VERSION) == -1) {
      ret.setExitStatus(ReturnValue.NOTIMPLEMENTED);
      ret.setStderr("Trying to call an incorrect version of SeqWare tools: "+Module.VERSION+" support but using jar: "+options.valueOf("seqware-qe-backend-jar"));
    }
    
    return(ret);
    
  }
  
  @Override
  public ReturnValue do_verify_parameters() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    if ((Integer)options.valueOf("min-coverage") > (Integer)options.valueOf("max-coverage")) {
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
      ret.setStderr("min coverage needs to be less than max coverage: "+options.valueOf("min-coverage")+" < "+options.valueOf("max-coverage"));
      return (ret);
    }
    
    // FIXME: need to verify more params
    return(ret);
    
  }

  @Override
  public ReturnValue do_verify_input() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    for (String path : new String[]{"local-temp-path"}) {
      if (FileTools.dirPathExistsAndWritable(new File((String)options.valueOf(path))).getExitStatus() != ReturnValue.SUCCESS) {
        ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
        ret.setStderr("Can't write to temp directory");
        return (ret);
      }
    }
    if (FileTools.fileExistsAndReadable(new File(((String)options.valueOf("java-path"))+File.separator+"java")).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Can't find java executable: "+(String)options.valueOf("java-path")+File.separator+"java");
      return (ret);
    }
    for (String path : (List<String>)options.valuesOf("pileup")) {

      Log.info("TEST");
      
      
      
      
      Log.info("Pileup: "+path);
      
      if (FileTools.fileExistsAndReadable(new File(path)).getExitStatus() != ReturnValue.SUCCESS) {
        ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        ret.setStderr("Can't find input pileup "+path);
        return (ret);
      }      
    }
    // FIXME: could do a large amount of checking here!
    
    return (ret);
 
  }
  
  @Override
  public ReturnValue do_run() {

    // figure out the pileups passed in
    List<String> pileups = (List<String>) options.valuesOf("pileup");
    StringBuffer pileupsStr = new StringBuffer();
    for(String pileup : pileups) { pileupsStr.append(pileup+" "); }
    
    int processErrorStatus = 0;
    int errorStatus = 0;
    
    // this creates the DB and loads the 
    ReturnValue variantImporterRV = RunTools.runCommand(options.valueOf("java-path")+File.separator+"java -Djava.library.path="+
        options.valueOf("java-library-path")+" -cp "+options.valueOf("db-jar")+":"+options.valueOf("seqware-qe-backend-jar")+
        " net.sourceforge.seqware.queryengine.tools.importers.VariantImporter PileupImportWorker "+
        tempDir.getAbsolutePath()+" true "+options.valueOf("min-coverage")+" "+options.valueOf("max-coverage")+" "+
        options.valueOf("min-snp-quality")+" "+options.valueOf("compressed-pileup")+" true true "+options.valueOf("fastq-conversion-num")+" "+
        options.valueOf("cache-size")+" "+options.valueOf("locks")+" 8 "+options.valueOf("database-backend")+" "+
        options.valueOf("genome")+" "+options.valueOf("reference-genome")+" "+pileupsStr.toString().trim()
    );
    errorStatus = variantImporterRV.getExitStatus();
    processErrorStatus = variantImporterRV.getProcessExitStatus();
    
    ReturnValue pileupCoverageImporterRV = new ReturnValue();
    if (variantImporterRV.getProcessExitStatus() == ReturnValue.SUCCESS &&
        variantImporterRV.getExitStatus() == ReturnValue.SUCCESS) {
      pileupCoverageImporterRV = RunTools.runCommand(
          options.valueOf("java-path")+File.separator+"java -Djava.library.path="+
          options.valueOf("java-library-path")+" -cp "+options.valueOf("db-jar")+":"+options.valueOf("seqware-qe-backend-jar")+
          " net.sourceforge.seqware.queryengine.tools.importers.PileupCoverageImporter "+
          options.valueOf("database-backend")+" "+
          options.valueOf("genome")+" "+options.valueOf("reference-genome")+" "+
          tempDir.getAbsolutePath()+" "+options.valueOf("cache-size")+" "+options.valueOf("locks")+
          " "+options.valueOf("compressed-pileup")+" "+options.valueOf("coverage-block-size")+" "+pileupsStr.toString().trim()
      );
      errorStatus = pileupCoverageImporterRV.getExitStatus();
      processErrorStatus = pileupCoverageImporterRV.getProcessExitStatus();
    }
    
    ReturnValue annotateContigZygosityRV = new ReturnValue();
    if (pileupCoverageImporterRV.getProcessExitStatus() == ReturnValue.SUCCESS &&
        pileupCoverageImporterRV.getExitStatus() == ReturnValue.SUCCESS) {
      annotateContigZygosityRV = RunTools.runCommand(
          options.valueOf("java-path")+File.separator+"java -Djava.library.path="+
          options.valueOf("java-library-path")+" -cp "+options.valueOf("db-jar")+":"+options.valueOf("seqware-qe-backend-jar")+
          " net.sourceforge.seqware.queryengine.tools.annotators.AnnotateVariantsWithContigAndZygosity "+
          options.valueOf("database-backend")+" "+options.valueOf("genome")+" "+options.valueOf("reference-genome")+" "+
          tempDir.getAbsolutePath()+" "+options.valueOf("cache-size")+" "+options.valueOf("locks")
      );
      errorStatus = annotateContigZygosityRV.getExitStatus();
      processErrorStatus = annotateContigZygosityRV.getProcessExitStatus();
    }
    
    ReturnValue cleanupDbRV = new ReturnValue();
    if (annotateContigZygosityRV.getProcessExitStatus() == ReturnValue.SUCCESS &&
        annotateContigZygosityRV.getExitStatus() == ReturnValue.SUCCESS &&
        "BerkeleyDB".equals(options.valueOf("database-backend"))
        ) {
      HashMap<String, String> env = new HashMap<String,String>();
      env.put("LD_LIBRARY_PATH", (String)options.valueOf("db-library-path"));
      cleanupDbRV = RunTools.runCommand(
          env,
          options.valueOf("db-path")+File.separator+"db_checkpoint -1 -h "+tempDir.getAbsolutePath()
      );
      errorStatus = cleanupDbRV.getExitStatus();
      processErrorStatus = cleanupDbRV.getProcessExitStatus();
    } 
    
    ReturnValue archiveDbRV = new ReturnValue();
    if (cleanupDbRV.getProcessExitStatus() == ReturnValue.SUCCESS &&
        cleanupDbRV.getExitStatus() == ReturnValue.SUCCESS &&
        "BerkeleyDB".equals(options.valueOf("database-backend"))
        ) {
      HashMap<String, String> env = new HashMap<String,String>();
      env.put("LD_LIBRARY_PATH", (String)options.valueOf("db-library-path"));
      archiveDbRV = RunTools.runCommand(
          env,
          options.valueOf("db-path")+File.separator+"db_archive -d -h "+tempDir.getAbsolutePath()
      );
      errorStatus = archiveDbRV.getExitStatus();
      processErrorStatus = archiveDbRV.getProcessExitStatus();
    }
    
    String stdOut = variantImporterRV.getStdout()+"\n"+pileupCoverageImporterRV.getStdout()+"\n"+
      annotateContigZygosityRV.getStdout()+"\n"+cleanupDbRV.getStdout()+"\n"+
      archiveDbRV.getStdout();
    String stdErr = variantImporterRV.getStderr()+"\n"+pileupCoverageImporterRV.getStderr()+"\n"+
      annotateContigZygosityRV.getStderr()+"\n"+cleanupDbRV.getStderr()+"\n"+
      archiveDbRV.getStderr();
    ReturnValue ret = new ReturnValue();
    ret.setStdout(stdOut);
    ret.setStderr(stdErr);
    ret.setExitStatus(errorStatus);
    ret.setProcessExitStatus(processErrorStatus);
    
    return(ret);
  }

  @Override
  public ReturnValue do_verify_output() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    for (String path : new String[]{
        tempDir.getAbsolutePath()+File.separator+"mismatch.db",
        tempDir.getAbsolutePath()+File.separator+"mismatchAnnotationTag.db",
        tempDir.getAbsolutePath()+File.separator+"mismatchContigPositions.db",
        tempDir.getAbsolutePath()+File.separator+"coverageContigPosition.db",
        tempDir.getAbsolutePath()+File.separator+"coverage.db",
        tempDir.getAbsolutePath()+File.separator+"consequenceAnnotationToMismatch.db",
        tempDir.getAbsolutePath()+File.separator+"consequenceAnnotationTag.db",
        tempDir.getAbsolutePath()+File.separator+"consequenceAnnotation.db"
        }) {
      if (FileTools.fileExistsAndNotEmpty(new File(path)).getExitStatus() != ReturnValue.SUCCESS) {
        ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        ret.setStderr("Can't read output database file or file is empty: "+path+" with value: "+options.valueOf(path));
        return (ret);
      }
    }
    
    return(ret);
    
  }
  

  @Override
  public ReturnValue clean_up() {
    // need to get the cleaned DB to destination then delete temp dir
    File outputDir = new File((String)options.valueOf("db-output-path"));
    if (!outputDir.exists()) { outputDir.mkdirs(); }
    FileTools.zipDirectoryRecursive(tempDir, new File(options.valueOf("db-output-path")+File.separator+options.valueOf("db-output-name")), "__db.", true, true);
    //FileTools.deleteDirectoryRecursive(tempDir);
    return(FileTools.fileExistsAndNotEmpty(new File(options.valueOf("db-output-path")+File.separator+options.valueOf("db-output-name"))));
  }

  
}
