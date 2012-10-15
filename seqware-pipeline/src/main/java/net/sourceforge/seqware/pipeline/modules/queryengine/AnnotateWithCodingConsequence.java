package net.sourceforge.seqware.pipeline.modules.queryengine;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

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
 * This module uses perl annotation scripts from SeqWare Pipeline to annotate
 * with coding consequence status and then generates annotations tag files for
 * loading in the database.
 *
 * The overall flow for this module is to call: <ul>
 * <li>sw_map_snps_to_fxn_genomic_alignment.pl (perl script)</li>
 * <li>sw_create_coding_consequence_tag_file.pl (perl script)</li> </ul>
 *
 * Sample of commands this actually runs:
 *
 * # consequence perl -I /opt/rocks/lib/perl5/site_perl/5.8.8 -I
 * /home/solexa/svnroot/solexatools/solexa-pipeline/lib
 * /home/solexa/svnroot/solexatools/solexa-pipeline/bin/map_snps_to_fxn_genomic_alignment.pl
 * --bed bed/dbSNP/lane_7.homo.notDbSNP.bed --genome-dir /scratch0/genomes/hg18
 * --known-gene-annotation-file /scratch0/genomes/hg18_refMrna/knownGene.txt
 * --output-dir bed/dbSNP/codingChange/knownGene --sql-host 10.67.183.249
 * --sql-username solexa --sql-password solexa --sql-db hg18 --table knownGene
 * --blosum-matrix
 * /home/solexa/svnroot/solexatools/solexa-pipeline/data/BLOSUM90.txt;
 *
 * TMPDIR=/state/partition1/tmp/hlee perl -I
 * /opt/rocks/lib/perl5/site_perl/5.8.8 -I
 * /home/solexa/svnroot/solexatools/solexa-pipeline/lib
 * /home/solexa/svnroot/solexatools/solexa-pipeline/bin/map_snps_to_fxn_genomic_alignment.pl
 * --bed $bed_file --genome-dir /scratch0/genomes/hg18
 * --known-gene-annotation-file /scratch0/genomes/hg18_refMrna/knownGene.txt
 * --output-dir $output_dir --sql-host 10.1.1.2 --sql-username solexa
 * --sql-password solexa --sql-db hg18 --table knownGene --blosum-matrix
 * /home/solexa/svnroot/solexatools/solexa-pipeline/data/BLOSUM90.txt # script
 * to create tag files... /home/solexa/programs/jdk1.6.0_13/bin/java
 * -Djava.library.path=/home/solexa/programs/BerkeleyDB.4.7/lib -cp
 * /home/solexa/svnroot/solexatools/solexa-queryengine/tools/java/lib/db.jar:/home/solexa/svnroot/solexatools/solexa-queryengine/tools/java/dist/seqware-qe-0.2.0.jar
 * net.sourceforge.seqware.queryengine.tools.TagAnnotationByPositionImporter
 * /state/partition1/tmp/solexa/bestscore_no_pcr_duplicates_mismatch_only_last_5_bases_removed_20090804/bbed_0.2.0_20090826
 * mismatch 4294967296
 * /home/solexa/abi_datasets/Reports/U87_whole_genome_bfast_alignment/boconnor_nonsynonymous_variant_reports/boconnor_nonsynonymous_variant_reports-2009-08-03/data/bestscore_no_pcr_duplicates_mismatch_only_last_5_bases_removed_20090804/snv_bed_by_chr/dbSNP_filter/coding_change/knownGene/bfast.U87.ABI.slides.1-5.bestscore.mismatches.only.rmdup.chr22.not_dbSNP_mutation_consequence_report.txt.tags
 *
 * FIXME/TODO: * this module will be fragile because it's assuming a lot of
 * things 1) the db dump files are consistent between genomes 2) that a hg18 or
 * equivalent database exits and is accessible 3) queries are embedded in the
 * module * this module will only work "out of the box" if an h18 database/data
 * dump exists, other genomes/non-whole genome datasets will probably break *
 * Note the assumption about the filepath of the gene model table files, this
 * could totally break!!! Should be passed in as a variable
 *
 * @author boconnor
 * @version $Id: $Id
 */
@ServiceProvider(service = ModuleInterface.class)
public class AnnotateWithCodingConsequence extends Module {

    private OptionSet options = null;
    private File tempDir = null;
    private String[] tableFiles = null;
    private String[] tableQueries = null;
    private String[] tableNames = null;

    /** {@inheritDoc} */
    @Override
    public ReturnValue init() {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);

        try {

            OptionParser parser = getOptionParser();

            for (String param : getParameters().toArray(new String[0])) {
                Log.info("Param: " + param);
            }

            options = parser.parse(getParameters().toArray(new String[0]));

            // create a temp directory
            tempDir = FileTools.createTempDirectory(new File(options.valueOf("local-temp-path").toString()));

            // populate file arrays
            tableFiles = ((String) options.valueOf("table-files")).split(",");
            tableQueries = ((String) options.valueOf("table-queries")).split("\\|");
            tableNames = ((String) options.valueOf("gene-model-names")).split(",");

        } catch (OptionException e) {
            ret.setStderr(e.getMessage());
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        } catch (IOException e) {
            ret.setStderr(e.getMessage());
            ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
        }

        return (ret);

    }

    /**
     * <p>getOptionParser.</p>
     *
     * @return a {@link joptsimple.OptionParser} object.
     */
    protected OptionParser getOptionParser() {
        OptionParser parser = new OptionParser();
        parser.accepts("bed-input-file").withRequiredArg();
        parser.accepts("genome-dir").withRequiredArg().describedAs("The hg18 reference genome directory where database dump file sync from UCSC lives.");
        parser.accepts("sql-host").withRequiredArg();
        parser.accepts("sql-username").withRequiredArg();
        parser.accepts("sql-password").withRequiredArg();
        parser.accepts("sql-db").withRequiredArg();
        parser.accepts("gene-model-names").withRequiredArg().describedAs("The names of the gene model tables, comma separated, no space. Used for naming output.");
        parser.accepts("table-files").withRequiredArg().describedAs("Tables of genome models to search, comma separated, no space, relative to the genome-dir without the extension. For example /genome-dir/knownGene.txt becomes knownGene");
        parser.accepts("table-queries").withRequiredArg().describedAs("Queries for annotations from tables of genome models, bar '|' separated. Currently not used!");
        parser.accepts("blosum-matrix").withRequiredArg();
        parser.accepts("local-temp-path").withRequiredArg();
        parser.accepts("perl-path").withRequiredArg();
        parser.accepts("perl-library-path").withRequiredArg();
        parser.accepts("bioperl-library-path").withRequiredArg();
        parser.accepts("perl-script-path").withRequiredArg();
        parser.accepts("output-path").withRequiredArg();
        parser.accepts("output-tag-file").withRequiredArg();
        parser.accepts("output-consequence-file").withRequiredArg();
        parser.accepts("output-in-transcribed-file").withRequiredArg();
        parser.accepts("output-not-in-transcribed-file").withRequiredArg();
        return (parser);
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
            return (output.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return (e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     *
     * Things to check: *
     */
    @Override
    public ReturnValue do_test() {

        /*
         * Things to test: * version of programs
         */
        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);
        // FIXME: could process a test dataset

        return (ret);

    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue do_verify_parameters() {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);
        // FIXME: need to verify more params
        // FIXME: add back table-queries when I start using it!
        for (String option : new String[]{"bed-input-file", "genome-dir", "sql-host", "sql-username",
                    "sql-password", "sql-db", "table-files", "blosum-matrix", "local-temp-path", "perl-path", "perl-library-path",
                    "perl-script-path", "output-path", "output-tag-file", "output-consequence-file",
                    "output-not-in-transcribed-file", "output-in-transcribed-file", "bioperl-library-path", "gene-model-names"
                }) {
            if (!options.has(option)) {
                ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
                String stdErr = ret.getStderr();
                ret.setStderr(stdErr + "Must include parameter: --" + option + "\n");
            }
        }

        return (ret);

    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue do_verify_input() {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);

        // check that the following directories exist and are readable
        for (String path : new String[]{"genome-dir", "perl-path", "perl-library-path", "perl-script-path", "bioperl-library-path"}) {
            if (FileTools.dirPathExistsAndReadable(new File((String) options.valueOf(path))).getExitStatus() != ReturnValue.SUCCESS) {
                ret.setExitStatus(ReturnValue.DIRECTORYNOTREADABLE);
                ret.setStderr("Can't read directory: " + options.valueOf(path));
                return (ret);
            }
        }

        // check that the following directories exist and are writable
        for (String path : new String[]{"local-temp-path", "output-path"}) {
            if (FileTools.dirPathExistsAndWritable(new File((String) options.valueOf(path))).getExitStatus() != ReturnValue.SUCCESS) {
                ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
                ret.setStderr("Can't write to directory: " + options.valueOf(path));
                return (ret);
            }
        }

        // check that the following files can be read
        for (String path : new String[]{options.valueOf("perl-path") + File.separator + "perl", (String) options.valueOf("bed-input-file"),
                    (String) options.valueOf("perl-script-path") + File.separator + "sw_map_snps_to_fxn_genomic_alignment.pl",
                    (String) options.valueOf("blosum-matrix")}) {
            if (FileTools.fileExistsAndReadable(new File(path)).getExitStatus() != ReturnValue.SUCCESS) {
                ret.setExitStatus(ReturnValue.FILENOTREADABLE);
                ret.setStderr("Can't read file: " + path + " with value: " + options.valueOf(path));
                return (ret);
            }
        }

        // check that table queries and table files are equal
        if (tableFiles.length != tableQueries.length) {
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            ret.setStderr("Number of gene model tables and number of gene model queries is mismatched. Gene tables: " + tableFiles.length + " vs. queries: " + tableQueries.length + "\n");
            return (ret);
        }

        // check that table queries and table files are equal
        if (tableFiles.length != tableNames.length) {
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            ret.setStderr("Number of gene model tables and number of gene model names is mismatched. Gene tables: " + tableFiles.length + " vs. model names: " + tableNames.length + "\n");
            return (ret);
        }

        // now check that each table annotation file exists
        for (String tableFile : tableFiles) {
            if (FileTools.fileExistsAndReadable(new File(tableFile)).getExitStatus() != ReturnValue.SUCCESS) {
                ret.setExitStatus(ReturnValue.FILENOTREADABLE);
                ret.setStderr("Can't read gene model table file: " + tableFile + "\n");
                return (ret);
            }
        }

        // FIXME: could test to see if DB connection is OK

        return (ret);

    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue do_run() {

        // FIXME: I should really use the String[] runCommand, especially for bash

        HashMap<String, String> env = new HashMap<String, String>();

        // the script below needs this env var set
        env.put("TMPDIR", (String) options.valueOf("local-temp-path"));

        // Using all 6 gene models
        // These are some reasonable defaults
    /*
         * HashMap<String, String> defaultParams = new HashMap<String, String>()
         * {{ put("knownGene", "'select mRNA, spID, spDisplayID, geneSymbol,
         * refseq, protAcc, description from kgXref where kgID = ?' ");
         * put("ccdsGene", "none --shift "); put("ensGene", "none --shift ");
         * put("mgcGenes", "none --shift "); put("refGene", "'select name,
         * product, mrnaAcc, protAcc, geneName, prodName, locusLinkId, omimId
         * from refLink where mrnaAcc = ?' --shift "); put("vegaGene", "none
         * --shift "); }};
         */

        ReturnValue retValue = null;

        // now figure out what tables we want to use
        HashMap<String, String> params = new HashMap<String, String>();
        int i = 0;
        Log.info("HERE " + tableFiles.length);
        for (String currTableFile : tableFiles) {
            Log.info("Looking at: " + currTableFile);
            // FIXME: splitting the string is dangerous
            // figure out the gene table
            String[] tokens = currTableFile.split(File.separator);
            //String[] tokens2 = tokens[tokens.length-1].split("\\.");
            // useful vars
            // unreliable to parse
            //String tableName = tokens2[0];
            String tableQuery = tableQueries[i];
            Log.info("Table query: " + tableQuery);

            // now I'm ready to ready to run the command

            // FIXME: this bash call should be relative to the software directory provisioned out
            // do the consequence detection
            retValue = RunTools.runCommand(
                    options.valueOf("perl-path") + File.separator + "perl -I "
                    + options.valueOf("perl-library-path") + " -I " + options.valueOf("bioperl-library-path") + " "
                    + options.valueOf("perl-script-path") + File.separator + "sw_map_snps_to_fxn_genomic_alignment.pl "
                    + "--bed " + options.valueOf("bed-input-file") + " --genome-dir " + options.valueOf("genome-dir") + " "
                    + "--known-gene-annotation-file " + currTableFile + " "
                    + "--output-dir " + options.valueOf("output-path") + " " + "--output-consequence-file " + tableNames[i] + "." + options.valueOf("output-consequence-file") + " "
                    + "--output-in-transcribed-file " + tableNames[i] + "." + options.valueOf("output-in-transcribed-file") + " "
                    + "--output-not-in-transcribed-file " + tableNames[i] + "." + options.valueOf("output-not-in-transcribed-file") + " "
                    + "--sql-host " + options.valueOf("sql-host") + " "
                    + "--sql-username " + options.valueOf("sql-username") + " --sql-password " + options.valueOf("sql-password") + " "
                    + "--sql-db " + options.valueOf("sql-db") + " --table " + tableNames[i] + " --gene-xref-query " + tableQuery + " "
                    + "--blosum-matrix " + options.valueOf("blosum-matrix"));

            if (retValue.getExitStatus() != ReturnValue.SUCCESS || retValue.getProcessExitStatus() != ReturnValue.SUCCESS) {
                Log.error("Run command to map snps failed.");
                Log.error("Exit status was " + retValue.getExitStatus() + " and process exit status was " + retValue.getProcessExitStatus());
                retValue.setExitStatus(ReturnValue.PROGRAMFAILED);
                return (retValue);
            }

            // now create a tag file for the gene names and other key pieces of information
            // FIXME: need to create tag annotation file, rethink the way extra columns get packed on to output file to make this robust!
            // make sure include gene model name in the output file!
            i++;
        }

        return (retValue);
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue do_verify_output() {

        // FIXME: need to check tag output file when it gets created

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);

        for (String tableName : tableNames) {
            for (String path : new String[]{options.valueOf("output-path") + File.separator + tableName + "." + options.valueOf("output-consequence-file"),
                        options.valueOf("output-path") + File.separator + tableName + "." + options.valueOf("output-in-transcribed-file"),
                        options.valueOf("output-path") + File.separator + tableName + "." + options.valueOf("output-not-in-transcribed-file")
                    }) {
                if (FileTools.fileExistsAndReadable(new File(path)).getExitStatus() != ReturnValue.SUCCESS) {
                    ret.setExitStatus(ReturnValue.FILENOTREADABLE);
                    ret.setStderr("Can't read file: " + path + " with value: " + options.valueOf(path));
                    return (ret);
                }
            }
        }

        return (ret);

    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue clean_up() {
        ReturnValue ret = new ReturnValue();
        // FIXME: need to get the cleaned DB to destination then delete temp dir
        //FileTools.deleteDirectoryRecursive(tempDir);
        return (ret);
    }
}
