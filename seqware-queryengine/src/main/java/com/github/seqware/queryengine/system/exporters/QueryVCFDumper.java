package com.github.seqware.queryengine.system.exporters;

import com.github.seqware.queryengine.factory.SWQEFactory;
import com.github.seqware.queryengine.kernel.RPNStack;
import com.github.seqware.queryengine.model.FeatureSet;
import com.github.seqware.queryengine.model.QueryFuture;
import com.github.seqware.queryengine.system.Utility;
import com.github.seqware.queryengine.system.importers.*;
import org.apache.commons.cli.HackedPosixParser;
import com.github.seqware.queryengine.util.SGID;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.cli.*;
import org.apache.log4j.Logger;

/**
 * Exporter using a new interface to the parameters in order to support queries
 * triggerable from the command-line. Note that this will need to be run using
 * the "hadoop jar" command instead of "java -classpath" when running in the
 * large assembled jar file in the distribution folder
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class QueryVCFDumper extends Importer {

    /** Constant <code>FEATURE_SET_PARAM='f'</code> */
    public static final char FEATURE_SET_PARAM = 'f';
    /** Constant <code>KEYVALUE_OUT_PARAM='k'</code> */
    public static final char KEYVALUE_OUT_PARAM = 'k';
    /** Constant <code>QUERY_PARAM_FILE='p'</code> */
    public static final char QUERY_PARAM_FILE = 'p';
    /** Constant <code>QUERY_PARAM_STRING='s'</code> */
    public static final char QUERY_PARAM_STRING = 's';
    /** Constant <code>OUTPUTFILE_PARAM='o'</code> */
    public static final char OUTPUTFILE_PARAM = 'o';

    /**
     * Command-line interface
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        Stack<SGID> mainMethod = QueryVCFDumper.runMain(args);
        if (mainMethod == null || mainMethod.size() == 0) {
            System.exit(FeatureImporter.EXIT_CODE_INVALID_FILE);
        }
    }

    /**
     * Interface for mock-testing
     *
     * @param args an array of {@link java.lang.String} objects.
     * @return a {@link java.util.Stack} object.
     */
    public static Stack<SGID> runMain(String[] args) {
        // create Options object
        Options options = new Options();
        Option option1 = OptionBuilder.withArgName("feature set ID").withDescription("(required) the ID of the featureset that we will be querying and exporting").isRequired().hasArgs(1).create(FEATURE_SET_PARAM);
        options.addOption(option1);
        Option option2 = OptionBuilder.withArgName("keyValue file").withDescription("(optional) a key value file that includes the featureset ID of each featureset that is created during querying and the final featureset ID").hasArgs(1).create(KEYVALUE_OUT_PARAM);
        options.addOption(option2);

        Option option3a = OptionBuilder.withArgName("query parameters").withDescription("(required) full classname for the class implementing QueryDumperInterface").hasArgs(1).create(QUERY_PARAM_FILE);
        options.addOption(option3a);
        Option option3b = OptionBuilder.withArgName("query string").withDescription("(required) plain text query").hasArgs(1).create(QUERY_PARAM_STRING);
        options.addOption(option3b);

        OptionGroup optionGroup = new OptionGroup();
        optionGroup.setRequired(true);
        optionGroup.addOption(option3a);
        optionGroup.addOption(option3b);
        options.addOptionGroup(optionGroup);

        Option option4 = OptionBuilder.withArgName("output file").withDescription("(optional) output file for the VCF").hasArgs(1).create(OUTPUTFILE_PARAM);
        options.addOption(option4);

        try {
            CommandLineParser parser = new HackedPosixParser();
            CommandLine cmd = parser.parse(options, args);

            // parse a SGID from a String representation, we need a more elegant solution here
            String featureSetID = cmd.getOptionValue(FEATURE_SET_PARAM);
            SGID sgid = Utility.parseSGID(featureSetID);
            FeatureSet fSet = SWQEFactory.getQueryInterface().getLatestAtomBySGID(sgid, FeatureSet.class);

            // if this featureSet does not exist
            if (fSet == null) {
                System.out.println("featureSet ID not found");
                System.exit(-2);
            }


            File keyFile = null;
            if (cmd.hasOption(KEYVALUE_OUT_PARAM)) {
                String outputFilename = cmd.getOptionValue(KEYVALUE_OUT_PARAM);
                keyFile = Utility.checkOutput(outputFilename);
            }

            String outputFile = cmd.getOptionValue(OUTPUTFILE_PARAM);

            // verify queries from paramFile
            // open the file
            Stack<FeatureSet> stack = new Stack<FeatureSet>();
            Stack<SGID> sgidStack = new Stack<SGID>();
            stack.add(fSet);

            processQueryClassFile(cmd, stack);
            processPlainTextQuery(cmd, stack);

            FeatureSet finalSet = stack.pop();
            sgidStack.add(finalSet.getSGID());
            // output key value file
            Map<String, String> keyValues = new HashMap<String, String>();
            keyValues.put("Initial-FeatureSetID", fSet.getSGID().getRowKey());
            keyValues.put("Final-FeatureSetID", finalSet.getSGID().getRowKey());
            while (!stack.empty()) {
                FeatureSet set = stack.pop();
                sgidStack.add(set.getSGID());
                keyValues.put(stack.size() + "-featureSetID", set.getSGID().getRowKey());
            }
            Utility.writeKeyValueFile(keyFile, keyValues);
            VCFDumper.dumpVCFFromFeatureSetID(finalSet, outputFile);
            return sgidStack;
        } catch (IOException ex) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(QueryVCFDumper.class.getSimpleName(), options);
            Logger.getLogger(QueryVCFDumper.class.getName()).fatal(null, ex);
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        } catch (MissingOptionException e) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(QueryVCFDumper.class.getSimpleName(), options);
            Logger.getLogger(QueryVCFDumper.class.getName()).fatal(null, e);
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        } catch (ParseException e) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(QueryVCFDumper.class.getSimpleName(), options);
//            Logger.getLogger(QueryVCFDumper.class.getName()).fatal(null, e);
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        } catch (ClassNotFoundException e) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(QueryVCFDumper.class.getSimpleName(), options);
            Logger.getLogger(QueryVCFDumper.class.getName()).fatal(null, e);
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        } catch (InstantiationException e) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(QueryVCFDumper.class.getSimpleName(), options);
            Logger.getLogger(QueryVCFDumper.class.getName()).fatal(null, e);
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        } catch (IllegalAccessException e) {
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(QueryVCFDumper.class.getSimpleName(), options);
            Logger.getLogger(QueryVCFDumper.class.getName()).fatal(null, e);
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        }
        return null;
    }

    private static void processQueryClassFile(CommandLine cmd, Stack<FeatureSet> stack) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String paramClassName = cmd.getOptionValue(QUERY_PARAM_FILE);
        if (paramClassName == null) {
            return;
        }

        Class c = Class.forName(paramClassName);
        QueryDumperInterface queries = (QueryDumperInterface) c.newInstance();
        try {
            String l;
            for (int i = 0; i < queries.getNumQueries(); i++) {
                QueryFuture<FeatureSet> query = queries.getQuery(stack.peek(), i);
                FeatureSet resultingSet = query.get();
                stack.push(resultingSet);
            }
        } catch (Exception e) {
            Logger.getLogger(QueryVCFDumper.class.getName()).fatal(null, e);
            System.out.println("Error running queries");
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        }
    }

    private static void processPlainTextQuery(CommandLine cmd, Stack<FeatureSet> stack) {
        String query = cmd.getOptionValue(QUERY_PARAM_STRING);
        if (query == null) {
            return;
        }
        try {
            RPNStack compiledQuery = RPNStack.compileQuery(query);
            QueryFuture<FeatureSet> queryFuture = SWQEFactory.getQueryInterface().getFeaturesByAttributes(1, stack.peek(), compiledQuery);
            FeatureSet resultingSet = queryFuture.get();
            stack.push(resultingSet);
        } catch (RecognitionException ex) {
            Logger.getLogger(QueryVCFDumper.class.getName()).fatal(null, ex);
            System.out.println("Error lexing/parsing query");
            System.exit(FeatureImporter.EXIT_CODE_INVALID_ARGS);
        }
    }
}
