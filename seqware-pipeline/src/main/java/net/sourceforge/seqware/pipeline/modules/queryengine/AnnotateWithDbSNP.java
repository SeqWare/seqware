package net.sourceforge.seqware.pipeline.modules.queryengine;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

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
 * This module uses an existing variant database, dumps the variants as a BED
 * file, uses perl annotation scripts from SeqWare Pipeline to annotate with
 * dbSNP status, and create a load file that can be loaded back into the DB.
 *
 * The overall flow for this module is to call: <ul> <li>BEDExporter</li>
 * <li>filter_bed_by_dbSNP.pl (perl script)</li> <li>tag creation perl
 * script</li> <li>TagAnnotationImporter</li> <li>checkpoint and clean the
 * database</li> </ul>
 *
 * Sample of commands this actually runs:
 *
 * # things not in dbSNP perl
 * /home/solexa/svnroot/solexatools/solexa-pipeline/bin/filter_bed_by_dbSNP.pl
 * --check-snp-bases --bed $bed_file --dbsnp /scratch0/genomes/hg18/snp129.txt
 * --remove-snps --snp-storable /scratch0/genomes/hg18/snps.storable --output
 * $output_file; # things in dbSNP perl
 * /home/solexa/svnroot/solexatools/solexa-pipeline/bin/filter_bed_by_dbSNP.pl
 * --check-snp-bases --bed $bed_file --dbsnp /scratch0/genomes/hg18/snp129.txt
 * --output $output_file # script to create tag files...
 * /home/solexa/programs/jdk1.6.0_13/bin/java
 * -Djava.library.path=/home/solexa/programs/BerkeleyDB.4.7/lib -cp
 * /home/solexa/svnroot/solexatools/solexa-queryengine/tools/java/lib/db.jar:/home/solexa/svnroot/solexatools/solexa-queryengine/tools/java/dist/seqware-qe-0.2.0.jar
 * net.sourceforge.seqware.queryengine.tools.TagAnnotationByPositionImporter
 * /state/partition1/tmp/solexa/bestscore_no_pcr_duplicates_mismatch_only_last_5_bases_removed_20090804/bbed_0.2.0_20090826
 * mismatch 4294967296
 * /home/solexa/abi_datasets/Reports/U87_whole_genome_bfast_alignment/boconnor_nonsynonymous_variant_reports/boconnor_nonsynonymous_variant_reports-2009-08-03/data/bestscore_no_pcr_duplicates_mismatch_only_last_5_bases_removed_20090804/snv_bed_by_chr/dbSNP_filter/coding_change/knownGene/bfast.U87.ABI.slides.1-5.bestscore.mismatches.only.rmdup.chr22.not_dbSNP_mutation_consequence_report.txt.tags
 *
 * Things to Consider:
 *
 * * assumes you want to break things up based on contig, can run one or more
 * contig
 *
 * @author boconnor
 * @version $Id: $Id
 */
@ServiceProvider(service = ModuleInterface.class)
public class AnnotateWithDbSNP extends Module {

    private OptionSet options = null;
    private File tempDir = null;

    /** {@inheritDoc} */
    @Override
    public ReturnValue init() {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);

        try {

            OptionParser parser = getOptionParser();

            options = parser.parse(getParameters().toArray(new String[0]));

            // create a temp directory
            tempDir = FileTools.createTempDirectory(new File(options.valueOf("local-temp-path").toString()));

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
        parser.accepts("local-temp-path").withRequiredArg();
        parser.accepts("perl-path").withRequiredArg();
        parser.accepts("perl-library-path").withRequiredArg();
        parser.accepts("perl-script-path").withRequiredArg();
        parser.accepts("dbSNP-annotations").withRequiredArg();
        parser.accepts("output-path").withRequiredArg();
        parser.accepts("output-is-dbsnp-file").withRequiredArg();
        parser.accepts("output-not-dbsnp-file").withRequiredArg();
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

        return (ret);

    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue do_verify_input() {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);

        for (String path : new String[]{"local-temp-path", "output-path"}) {
            if (FileTools.dirPathExistsAndWritable(new File((String) options.valueOf(path))).getExitStatus() != ReturnValue.SUCCESS) {
                ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
                ret.setStderr("Can't write to directory: " + options.valueOf(path));
                return (ret);
            }
        }
        for (String path : new String[]{options.valueOf("perl-path") + File.separator + "perl", (String) options.valueOf("bed-input-file"), (String) options.valueOf("perl-script-path") + File.separator + "sw_filter_bed_by_dbSNP.pl", (String) options.valueOf("dbSNP-annotations")}) {
            if (FileTools.fileExistsAndReadable(new File(path)).getExitStatus() != ReturnValue.SUCCESS) {
                ret.setExitStatus(ReturnValue.FILENOTREADABLE);
                ret.setStderr("Can't read file: " + path + " with value: " + options.valueOf(path));
                return (ret);
            }
        }

        // FIXME: could do a large amount of checking here!

        return (ret);

    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue do_run() {

        // FIXME: I should really use the String[] runCommand, especially for bash

        // create the dbSNP annotations and make tag import files
        ReturnValue notDbSNPRV = RunTools.runCommand(options.valueOf("perl-path") + File.separator + "perl -I "
                + options.valueOf("perl-library-path") + " " + options.valueOf("perl-script-path") + File.separator + "sw_filter_bed_by_dbSNP.pl "
                + "--bed " + options.valueOf("bed-input-file") + " --dbsnp " + options.valueOf("dbSNP-annotations") + " "
                + "--check-snp-bases --remove-snps --snp-storable " + tempDir.getAbsolutePath() + File.separator + "snps.storable "
                + "--output " + tempDir.getAbsolutePath() + File.separator + "not_dbSNP.bed");
        ReturnValue notDbSNPTagsRV = RunTools.runCommand("/bin/bash -c \"/bin/cat " + tempDir.getAbsolutePath() + File.separator + "not_dbSNP.bed | "
                + options.valueOf("perl-path") + File.separator + "perl -I "
                + options.valueOf("perl-library-path") + " " + options.valueOf("perl-script-path") + File.separator + "sw_create_not_dbSNP_tag_file.pl "
                + "> " + options.valueOf("output-path") + File.separator + options.valueOf("output-not-dbsnp-file") + "\"");

        ReturnValue isDbSNPRV = RunTools.runCommand(options.valueOf("perl-path") + File.separator + "perl -I "
                + options.valueOf("perl-library-path") + " " + options.valueOf("perl-script-path") + File.separator + "sw_filter_bed_by_dbSNP.pl "
                + "--bed " + options.valueOf("bed-input-file") + " --dbsnp " + options.valueOf("dbSNP-annotations") + " "
                + "--check-snp-bases --snp-storable " + tempDir.getAbsolutePath() + File.separator + "snps.storable "
                + "--output " + tempDir.getAbsolutePath() + File.separator + "is_dbSNP.bed");
        ReturnValue isDbSNPTagsRV = RunTools.runCommand("/bin/bash -c \"/bin/cat " + tempDir.getAbsolutePath() + File.separator + "is_dbSNP.bed.snps | "
                + options.valueOf("perl-path") + File.separator + "perl -I "
                + options.valueOf("perl-library-path") + " " + options.valueOf("perl-script-path") + File.separator + "sw_create_is_dbSNP_tag_file.pl "
                + "> " + options.valueOf("output-path") + File.separator + options.valueOf("output-is-dbsnp-file") + "\"");

        String stdOut = notDbSNPRV.getStdout() + "\n" + notDbSNPTagsRV.getStdout() + "\n"
                + isDbSNPRV.getStdout() + "\n" + isDbSNPTagsRV.getStdout();
        String stdErr = notDbSNPRV.getStderr() + "\n" + notDbSNPTagsRV.getStderr() + "\n"
                + isDbSNPRV.getStderr() + "\n" + isDbSNPTagsRV.getStderr();
        ReturnValue ret = new ReturnValue();
        ret.setStdout(stdOut);
        ret.setStderr(stdErr);
        if (notDbSNPRV.getExitStatus() != ReturnValue.SUCCESS || notDbSNPTagsRV.getExitStatus() != ReturnValue.SUCCESS
                || isDbSNPRV.getExitStatus() != ReturnValue.SUCCESS || isDbSNPTagsRV.getExitStatus() != ReturnValue.SUCCESS) {
            Log.error("Exit status was " + ret.getExitStatus() + " and process exit status was " + ret.getProcessExitStatus());
            ret.setExitStatus(ReturnValue.PROGRAMFAILED);
        } else {
            ret.setExitStatus(ReturnValue.SUCCESS);
        }
        if (notDbSNPRV.getProcessExitStatus() != ReturnValue.SUCCESS || notDbSNPTagsRV.getProcessExitStatus() != ReturnValue.SUCCESS
                || isDbSNPRV.getProcessExitStatus() != ReturnValue.SUCCESS || isDbSNPTagsRV.getProcessExitStatus() != ReturnValue.SUCCESS) {
            Log.error("Exit status was " + ret.getExitStatus() + " and process exit status was " + ret.getProcessExitStatus());
            ret.setProcessExitStatus(ReturnValue.PROGRAMFAILED);
        } else {
            ret.setProcessExitStatus(ReturnValue.SUCCESS);
        }
        return (ret);
    }

    /** {@inheritDoc} */
    @Override
    public ReturnValue do_verify_output() {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);

        for (String path : new String[]{options.valueOf("output-path") + File.separator + options.valueOf("output-is-dbsnp-file"), options.valueOf("output-path") + File.separator + options.valueOf("output-not-dbsnp-file")}) {
            if (FileTools.fileExistsAndReadable(new File(path)).getExitStatus() != ReturnValue.SUCCESS) {
                ret.setExitStatus(ReturnValue.FILENOTREADABLE);
                ret.setStderr("Can't read output file: " + path + " with value: " + options.valueOf(path));
                return (ret);
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
