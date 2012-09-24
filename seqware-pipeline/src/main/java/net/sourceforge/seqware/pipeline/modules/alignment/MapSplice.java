package net.sourceforge.seqware.pipeline.modules.alignment;


import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

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
 * Align reads with via MapSplice
 *
 * This module launches a MapSplice alignment of given reads file(s) against given reference
 * sequences files.
 *
 * Underlying script:  mapsplice_segments.py in MapSplice package
 * Necessary programs:  MapSplice package, python, java
 *
 * Expected output:  output directory specified
 *
 * @author kai.wang@uky.edu
 * @author briandoconnor@gmail.com
 *
 */
@ServiceProvider(service=ModuleInterface.class)
public class MapSplice extends Module {

  private OptionSet options = null;
  private File tempDir = null;
  private File output = null;

  /**
   * getOptionParser is an internal method to parse command line args.
   * 
   * @return OptionParser this is used to get command line options
   */
  protected OptionParser getOptionParser() {
    OptionParser parser = new OptionParser();
    parser.accepts("python", "Path to python").withRequiredArg();
    parser.accepts("pyscript", "Path to MapSplice python script: mapsplice_segments.py").withRequiredArg().ofType(String.class).describedAs("required");
    parser.accepts("output-dir", "The output directory").withRequiredArg().ofType(String.class).describedAs("required");
    // modules don't accept config files, only command line options
    //parser.accepts("cfg", "A config file to run MapSplice.").withRequiredArg().ofType(String.class).describedAs("required");
    parser.accepts("min-output-seg", "An option to output incomplete alignments. The minimal number of segments contained in alignment. eg. If read length is 75bp, segment_length is 25, set min_output_seg = 2 will output 50bp alignments if there are no 75bp alignments for the corresponding reads. The default is output alignments of full read length .").withRequiredArg().ofType(Integer.class).describedAs("required");
    parser.accepts("not-rerun-all", "?");
    parser.accepts("fusion", "If specified, output fusion junctions (reads should be long enough to be divided into more than 2 segments for fusion alignment). Reads not aligned as normal unspliced or spliced alignments are consider as fusion candidates. The outputs are \"fusion.junction\" and \"fusion_junction.unique\"");
    parser.accepts("DEBUG", "Turn debug output on.");
    parser.accepts("Q", "Format of input reads, FASTA OR FASTQ").withRequiredArg().ofType(String.class).describedAs("required");
    parser.accepts("R", "The maximum number of mismatches that are allowed for remapping. The default is 2. Should be in range [0-3]").withRequiredArg().ofType(Integer.class).describedAs("required");
    parser.accepts("X", "Number of threads to run bowtie to map reads").withRequiredArg().ofType(Integer.class).describedAs("required");
    parser.accepts("full-running", "If specified, run remap step to increase the junction coverage");
    parser.accepts("not-rem-temp", "If specified, not remove tmp directory after MapSplice is finished running");
    parser.accepts("i", "The \"minimum intron length\". Mapsplice will not report the alignment with a gap less than these many bases. The default is 10.").withRequiredArg().ofType(Integer.class).describedAs("required");
    parser.accepts("x", "The \"maximum intron length\". Mapsplice will not report the alignment with a gap longer than these many bases apart for single anchored spliced alignment. The default is 200000.").withRequiredArg().ofType(Integer.class).describedAs("required");
    parser.accepts("o", "The name of the directory in which MapSplice will write its output. The default is \"mapsplice_out/\" under current directory where you run MapSplice. ").withRequiredArg().ofType(String.class).describedAs("required");
    parser.accepts("m", "The maximum number of mismatches that are allowed in a segment crossing splice junction. The default is 1.").withRequiredArg().ofType(Integer.class).describedAs("required");
    parser.accepts("w", "Input read length, read length can be arbitrary long").withRequiredArg().ofType(Integer.class).describedAs("required");
    parser.accepts("S", "?").withRequiredArg().ofType(String.class).describedAs("required");
    parser.accepts("c", "The directory containing the sequence files corresponding to the reference genome (in FASTA format)").withRequiredArg().ofType(String.class).describedAs("required");
    parser.accepts("bam", "BAM input, unmapped reads will be extracted and used").withRequiredArg().ofType(String.class).describedAs("required");
    parser.accepts("B", "The path and basename of index to be searched by Bowtie. ").withRequiredArg().ofType(String.class).describedAs("required");
    parser.accepts("L", "Length of segment reads").withRequiredArg().ofType(Integer.class).describedAs("required");
    parser.accepts("pairend", "Whether or not the input reads are paired end or single");
    parser.accepts("non-canonical", "Whether or not the semi-canonical and non-canonical junctions should be outputted. If --non-canonical specified, output all junctions. If --semi-canonical specified, output semi-canonical and canonical junctions. If none of them specified, only output canonical junction.");
    parser.accepts("semi-canonical", "Whether or not the semi-canonical and non-canonical junctions should be outputted. If --non-canonical specified, output all junctions. If --semi-canonical specified, output semi-canonical and canonical junctions. If none of them specified, only output canonical junction.");
    parser.accepts("fusion-non-canonical", "Whether or not the semi-canonical and non-canonical junctions should be outputted.");
    parser.accepts("fusion-semi-canonical", "Whether or not the semi-canonical and non-canonical junctions should be outputted.");
    parser.accepts("n", "The anchor length that will be used for single anchored spliced alignment").withRequiredArg().ofType(Integer.class).describedAs("required");
    parser.accepts("E", "The maximum number of mismatches (Hamming distance) that are allowed in a unspliced aligned read and segment. The default is 1. Must be in range [0-3]").withRequiredArg().ofType(Integer.class).describedAs("required");
    parser.accepts("max-hits", "max_hits x 10 is the maximal repeated hits during segments mapping and reads mapping(default is 4 x 10 = 40) ").withRequiredArg().ofType(Integer.class).describedAs("required");
    parser.accepts("r", "The maximal small indels length(default is 3, suggested to be in [0-3])").withRequiredArg().ofType(Integer.class).describedAs("required");
    parser.accepts("search-whole-chromosome", " If specified, search whole chromosomes to find splice alignment, instead of searching exonic regions. Able to find small exons which have length < segment length at head and tai, but will use more running time.");
    parser.accepts("map-segments-directly", "If yes, MapSplice will try to find spliced alignments and unspliced alignments of a read, and select best alignment. (will use more running time)");
    parser.accepts("run-MapPER", "If specified, run MapPER and generate reads mappings based on a probabilistic framework (39), valid for PER reads");
    //parser.accepts("", "").withRequiredArg().ofType(Integer.class).describedAs("required");
    
    /*
     * Example run:
     *  --min-output-seg 3 --not-rerun-all --fusion --DEBUG -Q fq -R 3 -X 8 --full-running --not-rem-temp -i 10 -x 200000 
     *  -o /home/brianoc/scratch/test_mapsplice_output_4 -m 1 -w 76 -S fa -c /home/darshan/MapSplice/hg19 
     *  --bam /datastore/nextgenout/seqware-analysis/illumina/100603_UNC3-RDR300156_0007_61MM1AAXX/seqware-0.7.0_RNASeqA
     *  lignmentBWA-0.7.4/seqware-0.7.0_BWA-0.7.0/100603_UNC3-RDR300156_0007_61MM1AAXX.1.trimmed.raw.bam 
     *  -B /home/darshan/MapSplice/hg19/humanchridx -L 25 --fusion-non-canonical -n 8 -E 1 --max-hits 4 -r 3
     *  
     *  --min-output-seg 3 --not-rerun-all --fusion -Q fq -R 3 -X 8 --full-running -i 10 -x 200000 
     *  -o /home/brianoc/scratch/test_mapsplice_output_5 -m 1 -w 76 -S fa -c /home/darshan/MapSplice/hg19 
     *  --bam /datastore/nextgenout/seqware-analysis/illumina/100603_UNC3-RDR300156_0007_61MM1AAXX/seqware-0.7.0_RNASeqA
     *  lignmentBWA-0.7.4/seqware-0.7.0_BWA-0.7.0/100603_UNC3-RDR300156_0007_61MM1AAXX.1.trimmed.raw.bam 
     *  -B /home/darshan/MapSplice/hg19/humanchridx -L 25 --fusion-non-canonical -n 8 -E 1 --max-hits 4 -r 3
     *  
     *  There are a lot more options that can be added! These are what's referenced in the script mapsplice_segments.py
     *                                   "seed-length=",
                                         "splice-mismatches=",
                                         "segment-mismatches=",
                                         "read-file-suffix=",
                                         "min-anchor-length=",
                                         "min-intron-length=",
                                         "max-intron-length=",
                                         "extend-exons=",
                                         "read-width=",
                                         "non-canonical",
                                         "semi-canonical",
                                         "fusion-non-canonical",
                                         "fusion-semi-canonical",
                                         "delta=",
                                         "gamma=",
                                         "threshold=",
                                         "boundary=",
                                         "Rank=",
                                         "Bowtieidx=",
                                         "threads=",
                                         "islands-file=",
                                         "reads-file=",
                                         "chromosome-files-dir=",
                                         "all-chromosomes-files=",
                                         "FASTA-files=",
                                         "unmapped-reads=",
                                         "sam-file=",
                                         "full-running",
                                         "num-anchor=",
                                         "pileup-file=",
                                         "numseg=",
                                         "seglen=",
                                         "fixholefile=",
                                         "synthetic=",
                                         "tophat=",
                                         "pairend",
                                         "fastq=",
                                         "extend-bits=",
                                         "total-mismatch=",
                                         "total-fusion-mismatch=",
                                         "output-dir=",
                                         "chrom-size=",
                                         "skip-bowtie=",
                                         "prefix-match=",
                                         "collect-stat",
                                         "max-hits=",
                                         "not-rem-temp",
                                         "not-rerun-all",
                                         "format-chromos",
                                         "format-reads",
                                         "fusion",
                                         "cluster",
                                         "DEBUG",
                                         "run-MapPER",
                                         "search-whole-chromosome",
                                         "map-segments-directly",
                                         "remap-mismatches=",
                                         "avoid-regions=",
                                         "config=",
                                         "interested-regions=",
                                         "max-insert=",
                                         "bam=",
                                         "min-output-seg=",
                                         "append-mismatch="]
     */
    
    parser.accepts("log-file", "This is the output log file name").withRequiredArg().ofType(String.class).describedAs("optional");

    return (parser);
  }

  /**
   * A method used to return the syntax for this module
   * @return a string describing the syntax
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
   * The init method is where you put any code needed to setup your module.
   * Here I set some basic information in the ReturnValue object which will eventually
   * populate the "processing" table in seqware_meta_db. I also create a temporary
   * directory using the FileTools object.
   * 
   * init is optional
   * 
   * @return A ReturnValue object that contains information about the status of init
   */
  @Override
  public ReturnValue init() {

    // setup the return value object, notice that we use
    // ExitStatus, this is what SeqWare uses to track the status
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    // fill in the algorithm field in the processing table
    ret.setAlgorithm("MapSplice");
    // fill in the description field in the processing table
    ret.setDescription("Align reads via MapSplice.");
    // fill in the version field in the processing table
    ret.setVersion("0.7.1");
    
    try {

      OptionParser parser = getOptionParser();

      // The parameters object is actually an ArrayList of Strings created
      // by splitting the command line options by space. JOpt expects a String[]
      options = parser.parse(this.getParameters().toArray(new String[0]));

      // create a temp directory in current working directory
      tempDir = FileTools.createTempDirectory(new File("."));

      // you can write to "stdout" or "stderr" which will be persisted back to the DB
      ret.setStdout(ret.getStdout()+"Output: "+(String)options.valueOf("log-file")+"\n");

    } catch (OptionException e) {
      e.printStackTrace();
      ret.setStderr(e.getMessage());
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
    } catch (IOException e) {
      e.printStackTrace();
      ret.setStderr(e.getMessage());
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
    }
    
    // now return the ReturnValue
    return (ret);

  }

  /**
   * Verifies that the parameters make sense
   * 
   * @return a ReturnValue object
   */
  @Override
  public ReturnValue do_verify_parameters() {
    
    // most methods return a ReturnValue object
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    // now look at the options and make sure they make sense
    // TODO: more checks need to go here
    for (String option : new String[] {
        "python", "log-file", "pyscript", "output-dir"
      }) {
      if (!options.has(option)) {
        ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        String stdErr = ret.getStderr();
        ret.setStderr(stdErr+"Must include parameter: --"+option+"\n");
      }
    }
    
    return ret;
  }

  /**
   * The do_verify_input method ensures that the input files exist. It
   * may also do validation of the input files or anything that is needed
   * to make sure the module has everything it needs to run. There is some
   * overlap between this method and do_verify_parameters. This one is more
   * focused on validating files, making sure web services are up, DBs can be
   * connected to etc.  While do_verify_parameters is primarily used to
   * validate that the minimal parameters are passed in. The overlap between
   * these two methods is at the discretion of the developer
   * 
   * @return a ReturnValue object
   */
  @Override
  public ReturnValue do_verify_input() {
    
    // not much to do, let's make sure the
    // temp directory is writable
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);

    // Is 'python' executable?
    if (FileTools.fileExistsAndExecutable(new File((String) options.valueOf("python"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTEXECUTABLE);
      ret.setStderr("Not executable: " +(String)options.valueOf("python"));
    }

    /* // Does 'cfg' exist & is it readable?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("cfg"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("config file " + (String)options.valueOf("cfg") + " is not readable");
    }*/
    
    // Does 'pyscript' exist & is it readable?
    if (FileTools.fileExistsAndReadable(new File((String) options.valueOf("pyscript"))).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.FILENOTREADABLE);
      ret.setStderr("python script " + (String)options.valueOf("pyscript") + " is not readable");
    }
   
    // Notice the FileTools actually returns ReturnValue objects too!
    if (FileTools.dirPathExistsAndWritable(tempDir).getExitStatus() != ReturnValue.SUCCESS) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't write to temp directory");
    }
    
    return (ret);

  }

  /**
   * This is really an optional method but a very good idea. You
   * would test the programs your calling here by running them on
   * a "known good" test dataset and then compare the new answer
   * with the previous known good answer. Other forms of testing could be
   * encapsulated here as well.
   * 
   * @return a ReturnValue object
   */
  @Override
  public ReturnValue do_test() {
    
    // notice the use of "NOTIMPLEMENTED", this signifies that we simply 
    // aren't doing this step. It's better than just saying SUCCESS
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.NOTIMPLEMENTED);

    // not much to do, just return
    return(ret);
  }

  /**
   * Run core of module.
   * Based on script sw_module_BWA.pl
   *
   * @return a ReturnValue object
   */
  @Override
  public ReturnValue do_run() {

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    ret.setRunStartTstmp(new Date());

    String output = (String)options.valueOf("output-dir");
    StringBuffer cmd = new StringBuffer();
    
    //cmd.append("hostname ");
    
    cmd.append(options.valueOf("python") + " " + options.valueOf("pyscript") + " ");

    // FIXME: will need to pass in all possible command line arguments
    if (options.has("min-output-seg")) { cmd.append("--min-output-seg "+ options.valueOf("min-output-seg") + " "); }
    if (options.has("not-rerun-all")) { cmd.append("--not-rerun-all "); }
    if (options.has("fusion")) { cmd.append("--fusion "); }
    if (options.has("DEBUG")) { cmd.append("--DEBUG "); }
    if (options.has("Q")) { cmd.append("-Q "+ options.valueOf("Q") + " "); }
    if (options.has("R")) { cmd.append("-R "+ options.valueOf("R") + " "); }
    if (options.has("X")) { cmd.append("-X "+ options.valueOf("X") + " "); }
    if (options.has("full-running")) { cmd.append("--full-running "); }
    if (options.has("not-rem-temp")) { cmd.append("--not-rem-temp "); }
    if (options.has("i")) { cmd.append("-i "+ options.valueOf("i") + " "); }
    if (options.has("x")) { cmd.append("-x "+ options.valueOf("x") + " "); }
    if (options.has("o")) { cmd.append("-o "+ options.valueOf("o") + " "); }
    if (options.has("m")) { cmd.append("-m "+ options.valueOf("m") + " "); }
    if (options.has("w")) { cmd.append("-w "+ options.valueOf("w") + " "); }
    if (options.has("S")) { cmd.append("-S "+ options.valueOf("S") + " "); }
    if (options.has("c")) { cmd.append("-c "+ options.valueOf("c") + " "); }
    if (options.has("bam")) { cmd.append("--bam "+ options.valueOf("bam") + " "); }
    if (options.has("B")) { cmd.append("-B "+ options.valueOf("B") + " "); }
    if (options.has("L")) { cmd.append("-L "+ options.valueOf("L") + " "); }
    if (options.has("fusion-non-canonical")) { cmd.append("--fusion-non-canonical "); }
    if (options.has("n")) { cmd.append("-n "+ options.valueOf("n") + " "); }
    if (options.has("E")) { cmd.append("-E "+ options.valueOf("E") + " "); }
    if (options.has("max-hits")) { cmd.append("--max-hits "+ options.valueOf("max-hits") + " "); }
    if (options.has("r")) { cmd.append("-r "+ options.valueOf("r") + " "); }

    cmd.append(" 2> " + options.valueOf("log-file"));

    RunTools.runCommand( new String[] { "bash", "-c", cmd.toString() } );

    // record the file output
    FileMetadata fm = new FileMetadata();
    fm.setMetaType("text/sam");
    fm.setFilePath(output+File.separator+"alignments.sam");
    fm.setType("MapSplice-SAM-output");
    fm.setDescription("SAM file output of MapSplice module.");
    ret.getFiles().add(fm);
    
    // record the file output
    fm = new FileMetadata();
    fm.setMetaType("chemical/seq-na-fasta");
    fm.setFilePath(output+File.separator+"fusion_remap_junction.unique.chr_seq.fa");
    fm.setType("MapSplice-fasta-output");
    fm.setDescription("Fasta file output of MapSplice module.");
    ret.getFiles().add(fm);
    
    // record the file output
    fm = new FileMetadata();
    fm.setMetaType("text/maf");
    fm.setFilePath(output+File.separator+"fusion.remapped.unique.maf");
    fm.setType("MapSplice-maf-output");
    fm.setDescription("MAF file output of MapSplice module.");
    ret.getFiles().add(fm);
    
    // record the file output
    fm = new FileMetadata();
    fm.setMetaType("text/bed");
    fm.setFilePath(output+File.separator+"best_remapped_junction.bed");
    fm.setType("MapSplice-bed-output");
    fm.setDescription("BED file output of MapSplice module.");
    ret.getFiles().add(fm);
    
    for (String option : new String[] {
            "fusion_remap_junction.unique.chr_seq.extracted.filtered", "fusion_remap_junction.unique.chr_seq_append.out",
            "fusion_remap_junction.unique.chr_seq.out",
            "fusion_remap_junction.unique.chr_seq.extracted.root.matched", 
            "fusion_remap_junction.unique.chr_seq.extracted.root.matched_junc",
            "fusion_remap_junction.unique.chr_seq.extracted.root.range_matched",
            "fusion_remap_junction.unique.chr_seq.extracted.root.range_matched_junc",
            "fusion_remap_junction.unique.chr_seq.extracted.root.sidematched",
            "fusion_remap_junction.unique.chr_seq.extracted.root.sidematched_junc",
            "fusion_remap_junction.unique.chr_seq.extracted.root.range_sidematched",
            "fusion_remap_junction.unique.chr_seq.extracted.root.range_sidematched_junc",
            "fusion_remap_junction.unique.chr_seq.extracted.root.notmatched",
            "fusion_remap_junction.unique.chr_seq.extracted", "fusion_remap_junction.unique",
            "fusion_remap_junction.unique.filtered",
            "fusion.remapped.unique", "annotated.gene",
            "fusion.junction"
          }) {
        // record the file output
        fm = new FileMetadata();
        String metaType = option;
        metaType.replace('.', '-');
        fm.setMetaType("text/MapSplice-output-"+metaType);
        fm.setFilePath(output+File.separator+option);
        fm.setType("MapSplice-output");
        fm.setDescription("Output of MapSplice module.");
        ret.getFiles().add(fm);
    }
    
    ret.setRunStopTstmp(new Date());
    return(ret);
  }

  /**
   * A method to check to make sure the output was created correctly
   * 
   * @return a ReturnValue object
   */
  @Override
  public ReturnValue do_verify_output() {
    
    // this is easy, just make sure the file exists
    //return(FileTools.fileExistsAndReadable(new File((String)options.valueOf("log-file"))));
    
    String output = (String)options.valueOf("output-dir");
    
    boolean success = true;
    for (String option : new String[] {
        "fusion_remap_junction.unique.chr_seq.extracted.filtered", "fusion_remap_junction.unique.chr_seq_append.out",
        "fusion_remap_junction.unique.chr_seq.out",
        "fusion_remap_junction.unique.chr_seq.extracted.root.matched", 
        "fusion_remap_junction.unique.chr_seq.extracted.root.matched_junc",
        "fusion_remap_junction.unique.chr_seq.extracted.root.range_matched",
        "fusion_remap_junction.unique.chr_seq.extracted.root.range_matched_junc",
        "fusion_remap_junction.unique.chr_seq.extracted.root.sidematched",
        "fusion_remap_junction.unique.chr_seq.extracted.root.sidematched_junc",
        "fusion_remap_junction.unique.chr_seq.extracted.root.range_sidematched",
        "fusion_remap_junction.unique.chr_seq.extracted.root.range_sidematched_junc",
        "fusion_remap_junction.unique.chr_seq.extracted.root.notmatched",
        "fusion_remap_junction.unique.chr_seq.extracted", "fusion_remap_junction.unique",
        "fusion_remap_junction.unique.filtered",
        "fusion.remapped.unique", "annotated.gene",
        "fusion.junction", "alignments.sam", "fusion_remap_junction.unique.chr_seq.fa",
        "fusion.remapped.unique.maf", "best_remapped_junction.bed"
      }) {
      
      ReturnValue rv = FileTools.fileExistsAndReadable(new File(output+File.separator+option));
      if (rv.getExitStatus() != rv.SUCCESS) { return (rv); }

    }

    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    return(ret);
    
  }
  
  /**
   * A cleanup method, make sure you cleanup files that are outside the current working directory
   * since Pegasus won't clean those for you.
   * 
   * clean_up is optional
   */
  @Override
  public ReturnValue clean_up() {
    
    ReturnValue ret = new ReturnValue();
    ret.setExitStatus(ReturnValue.SUCCESS);
    
    if (!tempDir.delete()) {
      ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
      ret.setStderr("Can't delete folder: "+tempDir.getAbsolutePath());
    }
    
    return(ret);
  }
  
}
