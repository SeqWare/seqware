package net.sourceforge.seqware.pipeline.modules.alignment;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.runtools.RunTools;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;

import org.openide.util.lookup.ServiceProvider;

/**
 * Given a set of database tables, construct a reference set of transcripts.
 *
 * This module generates a transcript reference set given a set of specifically formatted database tables.  In order
 * for a transcript to be included in the reference set, an accession & a sequence must be available for extraction.
 * Pairwise mapping (transcript vs genome) [for exon coverage & splice junction counts] and gene association
 * information [for gene-level RPKM] are encouraged but not required.
 *
 * Underlying script:  sw_module_PrepTranscriptDB.pl
 *
 * Necessary programs:  perl, BWA
 *
 * Input criteria:  Four (or five) tables are required.  All follow UCSC standards.  Please reference the example
 * tables for details such as column names, format, etc.
 * 1. BlatTable - contains blat results for mapping of transcripts to genome; Example = hg19.kgTargetAli; REQUIRED
 * 2. ExonTable - contains locus start/stop, cds start/stop, and mapped exons; Example = hg19.knownGene; REQUIRED
 * 3. SeqTable - contains sequence of each transcript; Example = hg19.knownGeneMrna; REQUIRED
 * 4. GeneTable - lists transcript-gene relationship; Example = hg19.knownToLocusLink; REQUIRED
 * 5. RefLink - gives information linking genes, protein transcript, mRNA transcript, etc.  The file is only used
 * convert LocusLink/EntrezIDs to Entrez Gene Symbols.; Example = hg19.refLink; If the gene type is "entrez", this
 * table is REQUIRED. Otherwise, this table is not used.
 *
 * The following list describes the mySQL queries that are used in the script.  You will notice that not all columns
 * in all tables are used.  As long as the following queries are successful, any missing data is the extraneous columns
 * is not problematic.
 * 1.  SELECT strand,qName,qSize,tName,blockSizes,qStarts,tStarts FROM $BlatTable
 * 2.  SELECT name,txStart,txEnd,cdsStart,cdsEnd FROM $ExonTable
 * 3.  SELECT name,seq FROM $SeqTable
 * 4a. If the gene type is "entrez"...
 *     SELECT DISTINCT $GeneTable.name,$GeneTable.value,$RefLink.name FROM $GeneTable LEFT JOIN $RefLink
 *     ON $GeneTable.value=$RefLink.locusLinkId
 * 4b. If the gene type is "other"...
 *     SELECT name,value FROM $GeneTable
 * (Note: txStart & txEnd from $ExonTable are only used in UNC hacked/custom version.)
 *
 * Expected output:  6 output files are generated.  BWA index files are also generated, although this step may move
 * to some other module later on.
 * 1. A tab-delimited file of trascript/gene assocations & mapping info: transcript accession, associated gene,
 * transcript length in bases, genomic coordinates in pairwise alignment (e.g. "chrX:100-50,30-20:-"), transcript
 * coordinates in pairwise alignment (e.g. "1-51,52-62"), cds start in transcript coordinates, and cds stop in
 * transcript coordinates.
 * 2. The transcript sequences in fasta format.
 * 3. A tab-delimited file of composite transcripts: composite ID, genomic coordinates in pairwise alignment (e.g.
 * "chrX:100-50,30-20:-"), transcript coordinates in pairwise alignment (e.g. "1-51,52-62"), transcript list
 * 4. A tab-delimited file of composite exons: genomic coordinates (e.g. "chr1:17055-16858:-"), gene list, transcript list if gene is "null"
 * 5. A tab-delimited file of known splice junctions: genomic coordinates (e.g. "chr1:12227:+,chr1:12595:+"), gene list,
 * number of transcripts, transcript list
 * 6. Composite transcripts in GTF format.
 * 7. BWA index files ~.abm, ~.ann, ~.bwt, ~.pac, ~.rbwt, ~.rpac, ~.rsa, ~.sa
 *
 * @author sacheek@med.unc.edu
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class PrepTranscriptDB extends Module {

  private OptionSet options = null;
  
  /**
   * getOptionParser is an internal method to parse command line args.
   *
   * @return OptionParser this is used to get command line option
   */
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("DBname", "Database name for database connection (e.g. dbi:mysql:DBname:DBhost)").withRequiredArg();
    parser.accepts("DBhost", "Host for database connnection (e.g. dbi:mysql:DBname:DBhost").withRequiredArg();
    parser.accepts("username", "Username for database connection.").withRequiredArg();
    parser.accepts("password", "Password for database connection.").withRequiredArg();
    parser.accepts("genetype", "[entrez] for LocusLink/EntrezIDs, [other] for anything else.").withRequiredArg();
    parser.accepts("BlatTable", "See UCSC.hg19.kgTargetAli for example.  The following query must be successful: " +
    		"SELECT strand,qName,qSize,tName,blockSizes,qStarts,tStarts FROM BlatTable.").withRequiredArg();
    parser.accepts("ExonTable", "See UCSC.hg19.knownGene for example.  The following query must be successful: " +
    		"SELECT name,txStart,txEnd,cdsStart,cdsEnd FROM ExonTable.").withRequiredArg();
    parser.accepts("SeqTable", "See UCSC.hg19.knownGeneMrna for example.  The following query must be successful: " +
    		"SELECT name,seq FROM SeqTable.").withRequiredArg();
    parser.accepts("GeneTable", "See UCSC.hg19.knownToLocusLink for example.  If gene type is [entrez], the following query " +
    		"must be successful: SELECT DISTINCT GeneTable.name,GeneTable.value,RefLink.name FROM GeneTable LEFT JOIN RefLink." +
    		"If gene type is [other], the following query must be successful: SELECT name,value FROM GeneTable.").withRequiredArg();
    parser.accepts("RefLink", "See UCSC.hg19.refLink for example.  If gene type is [entrez], see GeneTable for required query. " +
    		"If gene type is [other], this table is not used.").withRequiredArg();
    parser.accepts("canonical", "[yes]/[no]: restrict transcripts to only those entries mapped to canonical human chromosomes " +
    		"(chr1-22,X,Y,M).").withRequiredArg();
    parser.accepts("outinfo", "Output file of transcript association & coordinate data.").withRequiredArg();
    parser.accepts("outfasta", "Output file of transcripts in FASTA format.").withRequiredArg();
    parser.accepts("outgtf", "Output file of composite transcripts in GTF format.").withRequiredArg();
    parser.accepts("outcomposite", "Output file of composite transcripts & pairwise mapping info.").withRequiredArg();
    parser.accepts("outexon", "Output file of composite exons.").withRequiredArg();
    parser.accepts("outjunction", "Output file of known splice junctions.").withRequiredArg();
    parser.accepts("perl", "Path to perl").withRequiredArg();
    parser.accepts("script", "Path to perl script: sw_module_PrepTranscriptDB.pl").withRequiredArg();
    parser.accepts("bwa", "Path to BWA").withRequiredArg();
    return (parser);
  }
  
  /**
   * {@inheritDoc}
   *
   * A method used to return the syntax for this module
   */
  @Override
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
   * All necessary setup for the module.
   * Populate the "processing" table in seqware_meta_db.
   * Create a temporary directory.
   */
  @Override
  public ReturnValue init() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    // fill in the [xxx] fields in the processing table
    ret.setAlgorithm("PrepTranscriptDB");
    ret.setDescription("Generate transcript reference set given database tables.");
    ret.setVersion("0.7.0");
    
    try {
      OptionParser parser = getOptionParser();
      // The parameters object is actually an ArrayList of Strings created
      // by splitting the command line options by space. JOpt expects a String[]
      options = parser.parse(this.getParameters().toArray(new String[0]));
      // create a temp directory in current working directory
      // tempDir = FileTools.createTempDirectory(new File("."));
      // you can write to "stdout" or "stderr" which will be persisted back to the DB
      ret.setStdout(ret.getStdout()+"Output: "+(String)options.valueOf("outinfo")+"\nOutput: "+(String)options.valueOf("outfasta")+
          "\nOutput: "+(String)options.valueOf("outgtf")+"\nOutput: "+(String)options.valueOf("outcomposite")+
          "\nOutput: "+(String)options.valueOf("outexon")+"\nOutput: "+(String)options.valueOf("outjunction")+"\n");
    } catch (OptionException e) {
      e.printStackTrace();
      ret.setStderr(e.getMessage());
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
    } //catch (IOException e) {
      //e.printStackTrace();
      //ret.setStderr(e.getMessage());
      //ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
    //}
    
    return (ret);
  }
  
  /**
   * {@inheritDoc}
   *
   * Verify that the parameters are defined & make sense.
   */
  @Override
  public ReturnValue do_verify_parameters() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    // now look at the options and make sure they make sense
    for (String option : new String[] {
        "DBname", "DBhost", "username", "password", "genetype", "BlatTable", "ExonTable", "GeneTable", "SeqTable", "canonical", "outinfo", "outfasta", "outgtf", "outcomposite", "outexon", "outjunction", "perl", "script", "bwa"
      }) {
      if (!options.has(option)) {
        ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        String stdErr = ret.getStderr();
        ret.setStderr(stdErr+"Must include parameter: --"+option+"\n");
      }
    }

    // Is connection to database successfully made?  ("DBname", "DBhost", "username", "password")
    // FIXME: this needs to be changed to be MYSQL not POSTGRESQL
    String DBconn = "jdbc:mysql://" + (String)options.valueOf("DBhost") + "/" + (String)options.valueOf("DBname");
    try { Class.forName("com.mysql.jdbc.Driver").newInstance(); }
    catch ( ClassNotFoundException e) {
      e.printStackTrace();
      ret.setStderr(e.getMessage());
      ret.setExitStatus(ReturnValue.DBCOULDNOTINITIALIZE);
    } catch ( IllegalAccessException e) {
      e.printStackTrace();
      ret.setStderr(e.getMessage());
      ret.setExitStatus(ReturnValue.DBCOULDNOTINITIALIZE);
    } catch ( InstantiationException e) {
      e.printStackTrace();
      ret.setStderr(e.getMessage());
      ret.setExitStatus(ReturnValue.DBCOULDNOTINITIALIZE);
    }
    try{ 
      // Just trying to connect, then immediately close
      Connection dbConnection=DriverManager.getConnection(DBconn, (String)options.valueOf("username"), (String)options.valueOf("password")); 
      dbConnection.close();
    } catch( SQLException e ){
      e.printStackTrace();
      ret.setStderr(e.getMessage());
      ret.setExitStatus(ReturnValue.DBCOULDNOTINITIALIZE);
    }
    
    return ret;
  }

  /**
   * {@inheritDoc}
   *
   * Verify anything needed to run the module is ready (e.g. input files exist, etc).
   */
  @Override
  public ReturnValue do_verify_input() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    // Is output file path writable (for all outputs)?
    File output1 = new File((String) options.valueOf("outinfo"));
    if (FileTools.dirPathExistsAndWritable(output1.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory of " + (String)options.valueOf("outinfo"));
    }
    File output2 = new File((String) options.valueOf("outfasta"));
    if (FileTools.dirPathExistsAndWritable(output2.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory of " + (String)options.valueOf("outfasta"));
    }
    File output3 = new File((String) options.valueOf("outgtf"));
    if (FileTools.dirPathExistsAndWritable(output3.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory of " + (String)options.valueOf("outgtf"));
    }
    File output4 = new File((String) options.valueOf("outcomposite"));
    if (FileTools.dirPathExistsAndWritable(output4.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory of " + (String)options.valueOf("outcomposite"));
    }
    File output5 = new File((String) options.valueOf("outexon"));
    if (FileTools.dirPathExistsAndWritable(output5.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory of " + (String)options.valueOf("outexon"));
    }
    File output6 = new File((String) options.valueOf("outjunction"));
    if (FileTools.dirPathExistsAndWritable(output6.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory of " + (String)options.valueOf("outjunction"));
    }
    
    
    
    // Is 'genetype' either "entrez" or "other"?
    if (!"entrez".equals((String)options.valueOf("genetype")) && !"other".equals((String)options.valueOf("genetype"))) {
      ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
      ret.setStderr("genetype must be entrez or other");
    }
    
    // Is 'canonical' either "yes" or "no"?
    if (!"yes".equals((String)options.valueOf("canonical")) && !"no".equals((String)options.valueOf("canonical"))) {
      ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
      ret.setStderr("canonical must be yes or no");
    }
    
    // Is 'perl' executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("perl"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("perl"));
    }
    
    // Does 'script' (sw_module_PrepTranscriptDB.pl) exist?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("script"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Could not find script at "+(String)options.valueOf("script"));
    }  
    
    // Is 'bwa' executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("bwa"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("bwa"));
    }
    
    return (ret);

  }
  
  /**
   * {@inheritDoc}
   *
   * Optional:  Test program on a known dataset.  Not implemented in this module.
   */
  @Override
  public ReturnValue do_test() {
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.NOTIMPLEMENTED);
    return(ret);
  }
  
  /**
   * {@inheritDoc}
   *
   * Run core of module.
   * Based on script sw_module_PrepTranscriptDB.pl
   */
  @Override
  public ReturnValue do_run() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    String output1 = (String)options.valueOf("outinfo");
    String output2 = (String)options.valueOf("outfasta");
    String output3 = (String)options.valueOf("outgtf");
    String output4 = (String)options.valueOf("outcomposite");
    String output5 = (String)options.valueOf("outexon");
    String output6 = (String)options.valueOf("outjunction");
    String bwaidx1 = (String)options.valueOf("outfasta") + ".abm";
    String bwaidx2 = (String)options.valueOf("outfasta") + ".ann";
    String bwaidx3 = (String)options.valueOf("outfasta") + ".bwt";
    String bwaidx4 = (String)options.valueOf("outfasta") + ".pac";
    String bwaidx5 = (String)options.valueOf("outfasta") + ".rbwt";
    String bwaidx6 = (String)options.valueOf("outfasta") + ".rpac";
    String bwaidx7 = (String)options.valueOf("outfasta") + ".rsa";
    String bwaidx8 = (String)options.valueOf("outfasta") + ".sa"; 
    
    StringBuffer cmd = new StringBuffer();
    cmd.append(options.valueOf("perl") + " " + options.valueOf("script") + " dbi:mysql:");
    cmd.append(options.valueOf("DBname") + ":" + options.valueOf("DBhost") + " " + options.valueOf("username") + " ");
    cmd.append(options.valueOf("password") + " " + options.valueOf("BlatTable") + " " + options.valueOf("ExonTable") + " ");
    cmd.append(options.valueOf("SeqTable") + " " + options.valueOf("GeneTable") + " ");
    if ("entrez".equals((String)options.valueOf("genetype"))) { cmd.append(options.valueOf("RefLink")); }
    if ("other".equals((String)options.valueOf("genetype"))) { cmd.append("undefined"); }
    cmd.append(" " + options.valueOf("genetype") + " " + options.valueOf("outinfo") + " " + options.valueOf("outfasta") + " ");
    cmd.append(options.valueOf("outgtf") + " " + options.valueOf("outcomposite") + " " + options.valueOf("outexon") + " ");
    cmd.append(options.valueOf("outjunction") + " " + options.valueOf("bwa") + " " + options.valueOf("canonical"));
    
    RunTools.runCommand( new String[] { "bash", "-c", cmd.toString() } );

    // record the file output
    FileMetadata fm1 = new FileMetadata();
    fm1.setMetaType("text/transcript-mapping");
    fm1.setFilePath(output1);
    fm1.setType("PrepTranscriptDB-transcript-mapping");
    fm1.setDescription("Tab-delimited text file of transcripts, coordinate mapping, & gene association.");
    ret.getFiles().add(fm1);
    
    FileMetadata fm2 = new FileMetadata();
    fm2.setMetaType("chemical/seq-na-fasta");
    fm2.setFilePath(output2);
    fm2.setType("PrepTranscriptDB-fasta");
    fm2.setDescription("Nucleotide sequences in fasta format.");
    ret.getFiles().add(fm2);
    
    FileMetadata fm3 = new FileMetadata();
    fm3.setMetaType("text/BWA-index-abm");
    fm3.setFilePath(bwaidx1);
    fm3.setType("PrepTranscriptDB-BWAidx-abm");
    fm3.setDescription("BWA index file ~.abm");
    ret.getFiles().add(fm3);
    
    FileMetadata fm4 = new FileMetadata();
    fm4.setMetaType("text/BWA-index-ann");
    fm4.setFilePath(bwaidx2);
    fm4.setType("PrepTranscriptDB-BWAidx-ann");
    fm4.setDescription("BWA index file ~.ann");
    ret.getFiles().add(fm4);
    
    FileMetadata fm5 = new FileMetadata();
    fm5.setMetaType("text/BWA-index-bwt");
    fm5.setFilePath(bwaidx3);
    fm5.setType("PrepTranscriptDB-BWAidx-bwt");
    fm5.setDescription("BWA index file ~.bwt");
    ret.getFiles().add(fm5);
        
    FileMetadata fm6 = new FileMetadata();
    fm6.setMetaType("text/BWA-index-pac");
    fm6.setFilePath(bwaidx4);
    fm6.setType("PrepTranscriptDB-BWAidx-pac");
    fm6.setDescription("BWA index file ~.pac");
    ret.getFiles().add(fm6);
    
    FileMetadata fm7 = new FileMetadata();
    fm7.setMetaType("text/BWA-index-rbwt");
    fm7.setFilePath(bwaidx5);
    fm7.setType("PrepTranscriptDB-BWAidx-rbwt");
    fm7.setDescription("BWA index file ~.rbwt");
    ret.getFiles().add(fm7);
    
    FileMetadata fm8 = new FileMetadata();
    fm8.setMetaType("text/BWA-index-rpac");
    fm8.setFilePath(bwaidx6);
    fm8.setType("PrepTranscriptDB-BWAidx-rpac");
    fm8.setDescription("BWA index file ~.rpac");
    ret.getFiles().add(fm8);
    
    FileMetadata fm9 = new FileMetadata();
    fm9.setMetaType("text/BWA-index-rsa");
    fm9.setFilePath(bwaidx7);
    fm9.setType("PrepTranscriptDB-BWAidx-rsa");
    fm9.setDescription("BWA index file ~.rsa");
    ret.getFiles().add(fm9);
    
    FileMetadata fm10 = new FileMetadata();
    fm10.setMetaType("text/BWA-index-sa");
    fm10.setFilePath(bwaidx8);
    fm10.setType("PrepTranscriptDB-BWAidx-sa");
    fm10.setDescription("BWA index file ~.sa");
    ret.getFiles().add(fm10);
    
    FileMetadata fm11 = new FileMetadata();
    fm11.setMetaType("text/gtf");
    fm11.setFilePath(output3);
    fm11.setType("PrepTranscriptDB-composite-gtf");
    fm11.setDescription("Composite transcripts in GTF format.");
    ret.getFiles().add(fm11);
    
    FileMetadata fm12 = new FileMetadata();
    fm12.setMetaType("text/composite-reference");
    fm12.setFilePath(output4);
    fm12.setType("PrepTranscriptDB-composite-reference");
    fm12.setDescription("Tab-delimited text file of composite transcripts & pairwise genomic mapping.");
    ret.getFiles().add(fm12);
    
    FileMetadata fm13 = new FileMetadata();
    fm13.setMetaType("text/exon-reference");
    fm13.setFilePath(output5);
    fm13.setType("PrepTranscriptDB-exon-reference");
    fm13.setDescription("Tab-delimited text file of composite exons & associated gene(s).");
    ret.getFiles().add(fm13);
    
    FileMetadata fm14 = new FileMetadata();
    fm14.setMetaType("text/junction-reference");
    fm14.setFilePath(output6);
    fm14.setType("PrepTranscriptDB-junction-reference");
    fm14.setDescription("Tab-delimited text file of known splice junctions & associated gene(s) and transcript(s).");
    ret.getFiles().add(fm14);
    
    return(ret);
  }
  
  /**
   * {@inheritDoc}
   *
   * Check to make sure the output was created correctly.
   */
  @Override
  public ReturnValue do_verify_output() {
    // just make sure the files exist
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    if ((FileTools.fileExistsAndNotEmpty(new File((String) options.valueOf("outinfo"))).getExitStatus() != ReturnValue.SUCCESS) || 
        (FileTools.fileExistsAndNotEmpty(new File((String) options.valueOf("outfasta"))).getExitStatus() != ReturnValue.SUCCESS) ||
        (FileTools.fileExistsAndNotEmpty(new File((String) options.valueOf("outgtf"))).getExitStatus() != ReturnValue.SUCCESS) ||
        (FileTools.fileExistsAndNotEmpty(new File((String) options.valueOf("outcomposite"))).getExitStatus() != ReturnValue.SUCCESS) ||
        (FileTools.fileExistsAndNotEmpty(new File((String) options.valueOf("outexon"))).getExitStatus() != ReturnValue.SUCCESS) ||
        (FileTools.fileExistsAndNotEmpty(new File((String) options.valueOf("outjunction"))).getExitStatus() != ReturnValue.SUCCESS)) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Expected output is empty or does not exist");
    }
    return(ret);
  }
  
  /**
   * {@inheritDoc}
   *
   * Optional:  Cleanup.
   * Cleanup files that are outside the current working directory since Pegasus won't do that for you.
   */
  @Override
  public ReturnValue clean_up() {
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    return(ret);
  }
  
}
