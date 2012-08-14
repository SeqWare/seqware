package com.github.seqware.queryengine.system.importers;

import com.github.seqware.queryengine.util.SGID;
import org.apache.commons.cli.*;

;

/**
 * Importer using a new interface to the parameters in order to support SO
 * importing
 *
 * @author boconnor
 * @author dyuen
 */
public class NewFeatureImporter extends Importer {

    public static void main(String[] args) {

        // create Options object
        Options options = new Options();
        Option option1 = OptionBuilder.withArgName("worker").withDescription("(required) the work module and thus the type of file we are working with").isRequired().create('w');
        options.addOption(option1);
        Option option2 = OptionBuilder.withArgName("threads").withDescription("(optional: default 1) the number of threads to use in our import").hasArgs(1).create('t');
        options.addOption(option2);
        Option option3 = OptionBuilder.withArgName("compressed").withDescription("(optional) whether we are working with compressed input").create('c');
        options.addOption(option3);
        Option option4 = OptionBuilder.withArgName("reference").withDescription("(required) the reference ID to attach our FeatureSet to").isRequired().hasArgs(1).create('r');
        options.addOption(option4);
        Option option5 = OptionBuilder.withArgName("inputFile").withDescription("(required) comma separated input files").hasArgs().withValueSeparator(',').isRequired().create('w');
        options.addOption(option5);
        Option option6 = OptionBuilder.withArgName("outputFile").withDescription("(optional) output file with our resulting key values").hasArgs(1).create('o');
        options.addOption(option6);
        Option option7 = OptionBuilder.withArgName("tagSpec").withDescription("(optional) comma separated tag specification set IDs, new tags will be linked in in the first set that they appear (or in an ad hoc set if they do not)").hasArgs().create('s');
        options.addOption(option7);

        try {
            CommandLineParser parser = new PosixParser();
            CommandLine cmd = parser.parse(options, args);
            
            String worker = cmd.getOptionValue('w');
            int threads = Integer.valueOf(cmd.getOptionValue('t', "1"));
            boolean compressed = Boolean.valueOf(cmd.getOptionValue('c', "false"));

            //SGID mainMethod = ImporterImpl.performImport(null, threadCount, null, null, true, null);
            //if (mainMethod == null) {
            //    System.exit(FeatureImporter.EXIT_CODE_INVALID_FILE);
            //}

        } catch (MissingOptionException e) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(NewFeatureImporter.class.getSimpleName(), options);
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        } catch (ParseException e) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(NewFeatureImporter.class.getSimpleName(), options);
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        }

    }
}
