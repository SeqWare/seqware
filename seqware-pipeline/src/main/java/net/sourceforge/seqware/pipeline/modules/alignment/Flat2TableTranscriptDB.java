package net.sourceforge.seqware.pipeline.modules.alignment;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
 * Given a set reference transcripts in flat files (fasta sequence file & a mapping/assocation file), create & populate
 * database tables in UCSC format.  This module performs essentially the opposite task as PrepTranscriptDB.
 *
 * This module generates the necessary UCSC-format database tables for a given transcript reference set.  The user will
 * input the following information:  transcript mappings/associations, transcript sequences, gene type (entrez or other),
 * database information, and table names.
 *
 * The only input file that is absolutely required is the fasta file (with transcript accessions & sequences).  The other
 * (optional) inputs provide information that allows exon-level, gene-level, and splice junction summaries.
 *
 * Underlying script:  sw_module_Flat2TableTranscriptDB.pl
 *
 * Necessary programs:  perl (uses DBI)
 *
 * Input criteria:
 * 1. [optional]  Mapping/association file.  This tab-delimited file gives info about pairwise alignment of transcripts to the genome,
 * gene assocations, CDS positions, etc.  The columns must be as follows...
 *    TranscriptID            <transcript accession/ID must exactly match the fasta input>
 *    GeneID                  <for entrez/locuslink expected syntax is 'geneSYM|geneID', otherwise entire string is taken as gene id>
 *    TranscriptLength        <length of transcript in bases>
 *    GenomicCoordinates      <genomic coordinates from pairwise alignment with transcript; should always correspond with transcript
 *                             coordinates (which are always ascending), thus a transcript mapped to the + strand should have ascending
 *                             genomic coordinates (chrX:1000-3000,50000-55000:+) while a transcript mapped to the - strand should have
 *                             descending genomic coordinates (chrX:55000-50000,3000-1000:-)>
 *    TranscriptCoordinates   <transcript coordinates from pairwise alignment with genome; should always be ascending regardless of which
 *                             strand of the genome the transcript maps to (e.g. 1-50,51-100)>
 *    CDSstart_Transcript     <transcript position of the first base of the CDS; 'unk' if no or unknown CDS>
 *    CDSend_Transcript       <transcript position of the last base of the CDS; 'unk' if no or unknown CDS>
 * 2. [required] Sequences in fasta format.  Transcript ID/accession must exactly match first column of the mapping/assocation file.
 * 3. [required] Gene type ('entrez' or 'other').
 * 4. [required] Database connection info (database name, database host, username, password).
 * 5. [required] Table names for BlatTable, ExonTable, SeqTable, & GeneTable.  Also table name for RefLink if genetype is 'entrez.
 *
 * Expected output:
 * 1. Four (or five) database tables will be created & populated.  (If not mapping or gene association data is available, the output
 * tables may partially or completely empty.)
 *    a. BlatTable - contains blat results for mapping of transcripts to genome; Example = hg19.kgTargetAli
 *    b. ExonTable - contains locus start/stop, cds start/stop, and mapped exons; Example = hg19.knownGene
 *    c. SeqTable - contains sequence of each transcript; Example = hg19.knownGeneMrna
 *    d. GeneTable - lists transcript-gene relationship; Example = hg19.knownToLocusLink
 *    e. RefLink - gives information linking gene IDs and gene symbols, only for genetype=entrez; Example = hg19.refLink
 * 2. A text file giving the line count for each new table.  If all line counts = 0, no file will be output.
 *
 * @author sacheek@med.unc.edu
 * @version $Id: $Id
 */
@ServiceProvider(service=ModuleInterface.class)
public class Flat2TableTranscriptDB extends Module {

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
    parser.accepts("BlatTable", "Output table will resemble UCSC.hg19.kgTargetAli.").withRequiredArg();
    parser.accepts("ExonTable", "Output table will resemble UCSC.hg19.knownGene, though not all columns are used.").withRequiredArg();
    parser.accepts("SeqTable", "Output table will resemble UCSC.hg19.knownGeneMrna, though not all columns are used.").withRequiredArg();
    parser.accepts("GeneTable", "Output table will resemble UCSC.hg19.knownToLocusLink, though not all columns are used.").withRequiredArg();
    parser.accepts("RefLink", "If gene type is [entrez], output table will resemble UCSC.hg19.refLink for example, though not " +
    		"all columns are used.  If gene type is [other], this table is not created.").withRequiredArg();
    parser.accepts("outsummary", "Output text file line count per new table.  Not generated if all line counts = 0.").withRequiredArg();
    parser.accepts("intrmap", "(Optional) input file of transcript association & coordinate data, must be in specified format (7 tab-delimited columns):" +
    		" TranscriptID [must exactly match fasta input], GeneID ['geneSYM|geneID' for entrez, otherwise entire string is taken as gene id, empty if " +
    		"no gene], TranscriptLength, GenomicCoordinates [from pairwise transcript/genome alignment; should always correspond with transcript coordinates " +
    		"(which are always ascending), thus a transcript mapped to the + strand should have ascending genomic coordinates (chrX:1000-3000,50000-" +
    		"55000:+) while a transcript mapped to the - strand should have descending genomic coordinates (chrX:55000-50000,3000-1000:-); empty if no " +
    		"mapping], TranscriptCoordinates [from pairwise transcript/genome alignment; should always be ascending regardless of which genome strand the " +
    		"transcript maps to; empty if no mapping], CDSstart_Transcript [transcript position of the first base of the CDS; 'unk' if no or unknown CDS], " +
    		"CDSend_Transcript [transcript position of the last base of the CDS; 'unk' if no or unknown CDS]").withRequiredArg();
    parser.accepts("infasta", "(Required) input file of transcripts in FASTA format.").withRequiredArg();
    parser.accepts("perl", "Path to perl").withRequiredArg();
    parser.accepts("script", "Path to perl script: sw_module_Flat2TableTranscriptDB.pl").withRequiredArg();
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
    ret.setAlgorithm("Flat2TableTranscriptDB");
    ret.setDescription("Create & populate database tables given flat files for a transcript reference set.");
    ret.setVersion("0.7.0");
    
    try {
      OptionParser parser = getOptionParser();
      // The parameters object is actually an ArrayList of Strings created
      // by splitting the command line options by space. JOpt expects a String[]
      options = parser.parse(this.getParameters().toArray(new String[0]));
      // create a temp directory in current working directory
      // tempDir = FileTools.createTempDirectory(new File("."));
      // you can write to "stdout" or "stderr" which will be persisted back to the DB
      ret.setStdout(ret.getStdout()+"Output: "+(String)options.valueOf("outsummary")+"\n");
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
        "DBname", "DBhost", "username", "password", "genetype", "BlatTable", "ExonTable", "GeneTable", "SeqTable", "outsummary", "infasta", "perl", "script"
      }) {
      if (!options.has(option)) {
        ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        String stdErr = ret.getStderr();
        ret.setStderr(stdErr+"Must include parameter: --"+option+"\n");
      }
    }

    // Is connection to database successfully made?  ("DBname", "DBhost", "username", "password")
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
    
    // Is output file path writable?
    File output1 = new File((String) options.valueOf("outsummary"));
    if (FileTools.dirPathExistsAndWritable(output1.getParentFile()).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to output directory of " + (String)options.valueOf("outsummary"));
    }

    // Is 'genetype' either "entrez" or "other"?
    if (!"entrez".equals((String)options.valueOf("genetype")) && !"other".equals((String)options.valueOf("genetype"))) {
      ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
      ret.setStderr("genetype must be entrez or other");
    }
    
    // If 'intrmap' was input, does the input file 'intrmap' exist & is it readable?
    if (options.has("intrmap")) {
      if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("intrmap"))).getExitStatus() != ReturnValue.SUCCESS) {
        ret.setExitStatus(ReturnValue.FILENOTREADABLE);
        ret.setStderr("Input file " + (String)options.valueOf("intrmap") + " is not readable");
      }
    }
      
    // Does input file 'infasta' exist & is it readable?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("infasta"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Input file " + (String)options.valueOf("infasta") + " is not readable");
    }
    
    // Is 'perl' executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("perl"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("perl"));
    }
    
    // Does 'script' (sw_module_Flat2TableTranscriptDB.pl) exist?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("script"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("Could not find script at "+(String)options.valueOf("script"));
    }  
    
    // Do the user-specified tables already exist?  ("BlatTable", "ExonTable", "GeneTable", "SeqTable", "RefLink")
    int count1=0, count2=0, count3=0, count4=0, count5=0;
    try{ 
      String DBconn = "jdbc:mysql://" + (String)options.valueOf("DBhost") + "/" + (String)options.valueOf("DBname");
      Connection dbConnection=DriverManager.getConnection(DBconn, (String)options.valueOf("username"), (String)options.valueOf("password")); 
      Statement s1 = dbConnection.createStatement ();
      String query1 = "SHOW TABLES like '" + (String)options.valueOf("BlatTable") + "'";
      ResultSet rs1 = s1.executeQuery(query1);
      while (rs1.next ()) { ++count1; }  rs1.close ();
      String query2 = "SHOW TABLES like '" + (String)options.valueOf("ExonTable") + "'";
      ResultSet rs2 = s1.executeQuery(query2);
      while (rs2.next ()) { ++count2; }  rs2.close ();
      String query3 = "SHOW TABLES like '" + (String)options.valueOf("SeqTable") + "'";
      ResultSet rs3 = s1.executeQuery(query3);
      while (rs3.next ()) { ++count3; }  rs3.close ();      
      String query4 = "SHOW TABLES like '" + (String)options.valueOf("GeneTable") + "'";
      ResultSet rs4 = s1.executeQuery(query4);
      while (rs4.next ()) { ++count4; }  rs4.close ();
      if (options.has("RefLink")) { 
        String query5 = "SHOW TABLES like '" + (String)options.valueOf("RefLink") + "'";
        ResultSet rs5 = s1.executeQuery(query5);
        while (rs5.next ()) { ++count5; }  rs5.close ();
      }
      s1.close ();     
      dbConnection.close();
    } catch( SQLException e ){
      e.printStackTrace();
      ret.setStderr(e.getMessage());
      ret.setExitStatus(ReturnValue.DBCOULDNOTINITIALIZE);
    }
    int countALL = count1+count2+count3+count4+count5;
    if (countALL > 0) {
      StringBuffer ExistingTables = new StringBuffer();
      if (count1 != 0) { ExistingTables.append((String)options.valueOf("BlatTable") + " "); }
      if (count2 != 0) { ExistingTables.append((String)options.valueOf("ExonTable") + " "); }
      if (count3 != 0) { ExistingTables.append((String)options.valueOf("SeqTable") + " "); }
      if (count4 != 0) { ExistingTables.append((String)options.valueOf("GeneTable") + " "); }
      if (count5 != 0) { ExistingTables.append((String)options.valueOf("RefLink") + " "); }
      ret.setStderr("One or more database tables already exist: " + ExistingTables.toString() + "\nDelete old tables or choose other names.\n");
      ret.setExitStatus(ReturnValue.INVALIDARGUMENT);
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
   * Based on script sw_module_Flat2TableTranscriptDB.pl
   */
  @Override
  public ReturnValue do_run() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    String output = (String)options.valueOf("outsummary");
    
    StringBuffer cmd = new StringBuffer();
    cmd.append(options.valueOf("perl") + " " + options.valueOf("script") + " dbi:mysql:");
    cmd.append(options.valueOf("DBname") + ":" + options.valueOf("DBhost") + " " + options.valueOf("username") + " ");
    cmd.append(options.valueOf("password") + " " + options.valueOf("BlatTable") + " " + options.valueOf("ExonTable") + " ");
    cmd.append(options.valueOf("SeqTable") + " " + options.valueOf("GeneTable") + " ");
    if ("entrez".equals((String)options.valueOf("genetype"))) { cmd.append(options.valueOf("RefLink")); }
    if ("other".equals((String)options.valueOf("genetype"))) { cmd.append("undefined"); }
    cmd.append(" " + options.valueOf("genetype") + " ");
    if (options.has("intrmap")) { cmd.append(options.valueOf("intrmap")); }
    else { cmd.append("undefined"); }
    cmd.append(" " + options.valueOf("infasta"));
    cmd.append(" " + options.valueOf("outsummary"));
    
    RunTools.runCommand( new String[] { "bash", "-c", cmd.toString() } );

    // record the file output
    FileMetadata fm1 = new FileMetadata();
    fm1.setMetaType("text/plain");
    fm1.setFilePath(output);
    fm1.setType("Flat2TableTranscriptDB-summary-txt");
    fm1.setDescription("Row count for each new table.");
    ret.getFiles().add(fm1);
    
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
    if (FileTools.fileExistsAndNotEmpty(new File((String) options.valueOf("outsummary"))).getExitStatus() != ReturnValue.SUCCESS) {
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
