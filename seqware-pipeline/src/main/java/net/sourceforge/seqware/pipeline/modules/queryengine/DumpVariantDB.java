package net.sourceforge.seqware.pipeline.modules.queryengine;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
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
 * This module takes a BerkeleyDB SeqWare Query Engine database and
 * dumps the variants as a series of BED files, one per contig.
 *
 * The overall flow for this module is to call
 * <ul>
 *   <li>BEDExporter</li>
 * </ul>
 *
 * The input is a zip file of a SeqWare Query Engine database.
 *
 * The output is a zip file of BED files for each contig.
 *
 * Sample of commands this actually runs:
 *
 * /home/solexa/programs/jdk1.6.0_13/bin/java -Djava.library.path=/home/solexa/programs/BerkeleyDB.4.7/lib -cp /home/solexa/svnroot/solexatools/solexa-queryengine/tools/java/lib/db.jar:/home/solexa/svnroot/solexatools/solexa-queryengine/tools/java/dist/seqware-qe-0.2.0.jar net.sourceforge.seqware.queryengine.tools.BEDExporter /state/partition1/tmp/solexa/bestscore_no_pcr_duplicates_mismatch_only_last_5_bases_removed_20090804/bbed_0.2.0_20090826 test . true true 0 100000 0 0 0 false 0 0-100 0-100 4294967296 true true chr22 not_dbSNP
 *
 * Notes/TODO/FIXME:
 *
 * @author boconnor
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class DumpVariantDB extends Module {

  private OptionSet options = null;
  private File tempDir = null;
  private File bedTempDir = null;
  private File bedOutputDir = null;
  
  /**
   * <p>getOptionParser.</p>
   *
   * @return a {@link joptsimple.OptionParser} object.
   */
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("local-temp-path").withRequiredArg();
    parser.accepts("seqware-qe-db-zip").withRequiredArg();
    parser.accepts("java-path").withRequiredArg();
    parser.accepts("java-library-path").withRequiredArg();
    parser.accepts("db-jar").withRequiredArg();
    parser.accepts("seqware-qe-backend-jar").withRequiredArg();
    parser.accepts("min-coverage").withRequiredArg().ofType(Integer.class);
    parser.accepts("max-coverage").withRequiredArg().ofType(Integer.class);
    parser.accepts("min-observations").withRequiredArg().ofType(Integer.class);
    parser.accepts("min-observations-per-strand").withRequiredArg().ofType(Integer.class);
    parser.accepts("contig").withRequiredArg();
    parser.accepts("min-snp-quality").withRequiredArg().ofType(Integer.class);
    parser.accepts("cache-size").withRequiredArg().ofType(Long.class);
    parser.accepts("locks").withRequiredArg().ofType(Long.class);
    parser.accepts("bed-output-path").withRequiredArg();
    parser.accepts("bed-output-name").withRequiredArg();
    parser.accepts("database-backend").withRequiredArg().ofType(String.class).defaultsTo("BerkeleyDB");
    parser.accepts("reference-genome").withRequiredArg().ofType(String.class).defaultsTo("NA");
    parser.accepts("genome").withRequiredArg().ofType(String.class).defaultsTo("NA");
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
  
  /** {@inheritDoc} */
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
      // bed temp dir
      bedTempDir = FileTools.createTempDirectory(new File(options.valueOf("local-temp-path").toString()));
      // output dir
      bedOutputDir = new File(options.valueOf("bed-output-path").toString());
      
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
   * {@inheritDoc}
   *
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
  
  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
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
    if (FileTools.fileExistsAndReadable(new File((String)options.valueOf("seqware-qe-db-zip"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Can't find input database file: "+(String)options.valueOf("seqware-qe-db-zip"));
      return (ret);
    }
    // FIXME: could do a large amount of checking here!
    
    return (ret);
 
  }
  
  /** {@inheritDoc} */
  @Override
  public ReturnValue do_run() {

    int processErrorStatus = 0;
    int errorStatus = 0;
    
    // unzip the database
    // FIXME: check status
    FileTools.unzipFile(new File((String)options.valueOf("seqware-qe-db-zip")), tempDir);
    
    // export the BED 
    List<String> contigs = (List<String>) options.valuesOf("contig");
    StringBuffer contigsStrBuff = new StringBuffer();
    for(String contig : contigs) { contigsStrBuff.append(contig+","); }
    String contigsStr = contigsStrBuff.toString().substring(0, contigsStrBuff.length()-1);
    
    ReturnValue variantExportRV = RunTools.runCommand(options.valueOf("java-path")+File.separator+"java -Djava.library.path="+
        options.valueOf("java-library-path")+" -cp "+options.valueOf("db-jar")+":"+options.valueOf("seqware-qe-backend-jar")+
        " net.sourceforge.seqware.queryengine.tools.exporters.BEDExporter "+
        //tempDir.getAbsolutePath()+" seqware "+bedTempDir.getAbsolutePath()+" true true "+
        tempDir.getAbsolutePath()+" seqware "+bedOutputDir.getAbsolutePath()+" true true "+
        options.valueOf("min-coverage")+" "+options.valueOf("max-coverage")+" "+options.valueOf("min-observations")+" "+
        options.valueOf("min-observations-per-strand")+" "+options.valueOf("min-snp-quality")+" false 0 0-100 0-100 "+
        options.valueOf("cache-size")+" "+options.valueOf("locks")+" "+
        options.valueOf("database-backend")+" "+options.valueOf("genome")+" "+options.valueOf("reference-genome")+" "+
        "true false "+contigsStr
    );
    
    // now zip up the result
    // FIXME: check status
    /* File outputDir = new File((String)options.valueOf("bed-output-path"));
    if (!outputDir.exists()) { outputDir.mkdirs(); }
    FileTools.zipDirectoryRecursive(bedTempDir, new File(options.valueOf("bed-output-path")+File.separator+options.valueOf("bed-output-name")), null, true, true); */
    
    return(variantExportRV);
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue do_verify_output() {
    
    // FIXME: should check all files
    //return(FileTools.fileExistsAndNotEmpty(new File(options.valueOf("bed-output-path")+File.separator+options.valueOf("bed-output-name"))));
    // TODO: check all the BEDs
    //return(FileTools.fileExistsAndNotEmpty(new File(options.valueOf("bed-output-path")+File.separator+"seqware")));
    return(new ReturnValue());
  }
  

  /** {@inheritDoc} */
  @Override
  public ReturnValue clean_up() {
    ReturnValue ret = new ReturnValue();
    // FIXME: capture returnvalue
    //FileTools.deleteDirectoryRecursive(tempDir);
    //FileTools.deleteDirectoryRecursive(bedTempDir);
    return(ret);
  }

  
}
