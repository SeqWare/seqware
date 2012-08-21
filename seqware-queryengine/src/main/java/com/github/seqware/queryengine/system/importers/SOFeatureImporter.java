package com.github.seqware.queryengine.system.importers;

import com.github.seqware.queryengine.system.Utility;
import com.github.seqware.queryengine.util.SGID;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.*;

/**
 * Importer using a new interface to the parameters in order to support SO
 * importing and the specification of a tag set and reference to attach to.
 *
 * @author dyuen
 */
public class SOFeatureImporter extends Importer {

    public static final char ADHOC_TAGSETS_PARAM = 'a';
    public static final char COMPRESSED_PARAM = 'c';
    public static final char INPUT_FILES_PARAM = 'i';
    public static final char NUMBER_THREADS_PARAM = 't';
    public static final char OUTPUT_FILE_PARAM = 'o';
    public static final char REFERENCE_ID_PARAM = 'r';
    public static final char TAGSETS_PARAM = 's';
    public static final char VALUE_SEPARATOR_PARAM = ',';
    public static final char WORKER_CHAR_PARAM = 'w';
    public static final char BATCH_SIZE_PARAM = 'b';
    
    public static final int BATCH_SIZE = 100000;

    /**
     * Command-line interface
     *
     * @param args
     */
    public static void main(String[] args) {
        SGID mainMethod = SOFeatureImporter.runMain(args);
        if (mainMethod == null) {
            System.exit(FeatureImporter.EXIT_CODE_INVALID_FILE);
        }
    }

    /**
     * Interface for mock-testing
     *
     * @param args
     * @return
     */
    public static SGID runMain(String[] args) {
        // create Options object
        Options options = new Options();
        Option option1 = OptionBuilder.withArgName("worker").withDescription("(required) the work module and thus the type of file we are working with").isRequired().hasArgs(1).create(WORKER_CHAR_PARAM);
        options.addOption(option1);
        Option option2 = OptionBuilder.withArgName("threads").withDescription("(optional: default 1) the number of threads to use in our import").hasArgs(1).create(NUMBER_THREADS_PARAM);
        options.addOption(option2);
        Option option3 = OptionBuilder.withArgName("compressed").withDescription("(optional) whether we are working with compressed input").create(COMPRESSED_PARAM);
        options.addOption(option3);
        Option option4 = OptionBuilder.withArgName("reference").withDescription("(required) the reference ID to attach our FeatureSet to").isRequired().hasArgs(1).create(REFERENCE_ID_PARAM);
        options.addOption(option4);
        Option option5 = OptionBuilder.withArgName("inputFile").withDescription("(required) comma separated input files").hasArgs().withValueSeparator(VALUE_SEPARATOR_PARAM).isRequired().create(INPUT_FILES_PARAM);
        options.addOption(option5);
        Option option6 = OptionBuilder.withArgName("outputFile").withDescription("(optional) output file with our resulting key values").hasArgs(1).create(OUTPUT_FILE_PARAM);
        options.addOption(option6);
        Option option7 = OptionBuilder.withArgName("tagSet").withDescription("(optional) comma separated TagSet IDs, new Tags will be linked to the first set that they appear, these TagSets will not be modified").withValueSeparator(VALUE_SEPARATOR_PARAM).hasArgs().create(TAGSETS_PARAM);
        options.addOption(option7);
        Option option8 = OptionBuilder.withArgName("adHocTagSet").withDescription("(optional) an ID for an ad hoc TagSet, Tags will either be found or added to this set, a new TagSet will be generated if this option is not used").hasArgs().create(ADHOC_TAGSETS_PARAM);
        options.addOption(option8);
        Option option9 = OptionBuilder.withArgName("batch_size").withDescription("(optional) batch-size for the number of features in memory to keep before a flush, will automatically be chosen if not specified, we use " +BATCH_SIZE+ " for now").hasArgs(1).create(BATCH_SIZE_PARAM);
        options.addOption(option9);

        try {
            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse(options, args);

            String worker = cmd.getOptionValue(WORKER_CHAR_PARAM);
            int threads = Integer.valueOf(cmd.getOptionValue(NUMBER_THREADS_PARAM, "1"));
            boolean compressed = Boolean.valueOf(cmd.hasOption(COMPRESSED_PARAM));
            // process referenceID
            SGID referenceSGID = Utility.parseSGID(cmd.getOptionValue(REFERENCE_ID_PARAM));
            
            int batch_size = Integer.valueOf(cmd.getOptionValue(BATCH_SIZE_PARAM, String.valueOf(BATCH_SIZE)));

            List<String> inputFiles = new ArrayList<String>();
            inputFiles.addAll(Arrays.asList(cmd.getOptionValues(INPUT_FILES_PARAM)));

            File outputFile = null;
            if (cmd.hasOption(OUTPUT_FILE_PARAM)){
                String outputFilename = cmd.getOptionValue(OUTPUT_FILE_PARAM);
                outputFile = Utility.checkOutput(outputFilename);
            }
            
            List<SGID> tagSetSGIDs = new ArrayList<SGID>();
            if (cmd.hasOption(TAGSETS_PARAM)) {
                List<String> tagSetIDs = new ArrayList<String>();
                tagSetIDs.addAll(Arrays.asList(cmd.getOptionValues(TAGSETS_PARAM)));
                for (String ID : tagSetIDs) {
                    tagSetSGIDs.add(Utility.parseSGID(ID));
                }
            }

            // process ad hoc tag set
            SGID adhocSGID = cmd.hasOption(ADHOC_TAGSETS_PARAM) ? Utility.parseSGID(cmd.getOptionValue(ADHOC_TAGSETS_PARAM)) : null;

            SGID mainMethod = FeatureImporter.performImport(referenceSGID, threads, inputFiles, worker, compressed, outputFile, tagSetSGIDs, adhocSGID, batch_size);
            if (mainMethod == null) {
                return null;
            }
            return mainMethod;

        } catch (IOException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(SOFeatureImporter.class.getSimpleName(), options);
            Logger.getLogger(SOFeatureImporter.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        } catch (MissingOptionException e) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(SOFeatureImporter.class.getSimpleName(), options);
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        } catch (ParseException e) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(SOFeatureImporter.class.getSimpleName(), options);
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        }
        return null;
    }
}
