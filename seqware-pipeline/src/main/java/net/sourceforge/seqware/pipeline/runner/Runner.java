package net.sourceforge.seqware.pipeline.runner;

import io.seqware.common.model.ProcessingStatus;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.err.NotFoundException;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.common.metadata.MetadataFactory;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.common.util.exceptiontools.ExceptionTools;
import net.sourceforge.seqware.common.util.filetools.lock.LockingFileTools;
import net.sourceforge.seqware.common.util.processtools.ProcessTools;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleMethod;
import net.sourceforge.seqware.pipeline.module.StderrRedirect;
import net.sourceforge.seqware.pipeline.module.StdoutRedirect;
import org.apache.commons.io.FileUtils;

// FIXME: auto-adding to rc.data, support "," delimited
// FIXME: When adding STDOUT/STDERR to metadb, we should add a timestamp or something else to make it easier to merge. Right now, it is hard to tell which stdout message corresponds to which step in stderr 
// FIXME: is parent accession comma separated?

/*
 * Run each method for the requested module object. Based on return value, either continue or exit:
 *   Return values > 0 are errors that will cause the runner to exit.
 *   Return of 0 implies success and the runner will continue, assuming all is well.
 *   Return of -1 implies the method was not implemented for that Module, AND IS NOT AN ERROR!!! By default the runner will continue on with steps as if it succeeded!  
 */
//Create a main() function here, which will be compiled in, to parse input and run all steps
/**
 * <p>
 * Runner class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class Runner {

    private int processingID = 0;
    private ArrayList<File> processingIDFiles;
    private ArrayList<File> processingAccessionFiles;
    private File processingAccessionFileCheck = null;
    private int processingAccession = 0;
    private final NonOptionArgumentSpec<String> nonOptionSpec;

    private static final OptionParser PARSER = new OptionParser();
    private OptionSet options = null;
    private Module app = null;
    private Metadata meta = null;
    // I (Xiaoshu Wang) am not sure if it is a good idea to make these two
    // property static because if the same JVM calls
    // Runner twice, the value of the previous stdout/stderr will be kept.
    private final StringBuffer stdout = new StringBuffer();
    private final StringBuffer stderr = new StringBuffer();

    public Runner() {
        PARSER.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
        PARSER.accepts("module", "Required: Specifies the module to run. All modules implement the ModuleInterface.").withRequiredArg()
                .ofType(String.class).describedAs("This is the module you wish to run.");
        PARSER.accepts("output", "Optional: redirect StdOut to file.").withRequiredArg().ofType(String.class)
                .describedAs("File path to redirect StdOut to.");
        // added by Xiaoshu Wang (xiao@renci.org)
        PARSER.accepts("stderr", "Optional: redirect Stderr to file.").withRequiredArg().ofType(String.class)
                .describedAs("File path to redirect Stderr to.");

        PARSER.accepts("module-pkg", "Optional: but replaces need to specify module, by specifying instead a pre-packaged module to run.")
                .withRequiredArg().ofType(String.class).describedAs("This is the module you wish to run.");
        // MetaDB stuff
        PARSER.acceptsAll(
                Arrays.asList("meta-db", "metadata"),
                "Optional: This argument really has no effect since we attempt metadata writeback so long as --no-meta-db or --no-metadata aren't passed in. This is really an argument to make it easier to have an if/else control over metadata writeback in calling programs.");
        PARSER.acceptsAll(Arrays.asList("no-meta-db", "no-metadata"),
                "Optional: Do not use metadata writeback. Otherwise, we will attempt it.");
        PARSER.accepts("metadata-config-database",
                "Required for metadata (DEPRECATED, use .seqware/settings file instead): The JDBC path for connection").withRequiredArg()
                .ofType(String.class).describedAs("Example: jdbc:postgresql://127.0.0.1/seqware_meta_db");
        PARSER.accepts("metadata-config-username",
                "Required for metadata (DEPRECATED, use .seqware/settings file instead): Database username for connection")
                .withRequiredArg().ofType(String.class).describedAs("Database Username");
        PARSER.accepts("metadata-config-password",
                "Required for metadata (DEPRECATED, use .seqware/settings file instead): Database password for connection")
                .withRequiredArg().ofType(String.class).describedAs("Database Password");

        PARSER.accepts(
                "metadata-parentID",
                "Optional (DEPRECATED): Specifies one of the parentID for metadata write back. This option can be specified zero or more times. This is deprecated, use metadata-parent-accession going forward.")
                .withRequiredArg().ofType(Integer.class)
                .describedAs("The processingID of the parent for this event, for constructing the dependency tree in the metadb");
        PARSER.accepts(
                "metadata-parentID-file",
                "Optional (DEPRECATED): The same as --metadata-parentID, but is a path to a file, to parse for parent processing ID's. This is deprecated, use metadata-parent-accession-file going forward.")
                .withRequiredArg().ofType(String.class)
                .describedAs("Path to a line-delimited file containing one or more parent processing IDs");
        PARSER.accepts(
                "metadata-parent-accession",
                "Optional: Specifies one of the SeqWare accessions (sw_accession column in the DB) for metadata write back. This is an alternative "
                        + "to processing parentID (see --metadata-parentID) that allows you to specify an IUS, lane, sequencer run, or other processing event as a parent. "
                        + " This option can be specified zero or more times and the value can also be comma-separated.").withRequiredArg()
                .withValuesSeparatedBy(',').ofType(Integer.class)
                .describedAs("The sw_accession of the parent for this event, for constructing the dependency tree in the metadb");
        PARSER.accepts("metadata-parent-accession-file",
                "Optional: The same as --metadata-parent-accession, but is a path to a file, to parse for parent processing sw_accessions.")
                .withRequiredArg().ofType(String.class)
                .describedAs("Path to a line-delimeted file containing one or more parent sw_accessions");
        PARSER.accepts("metadata-output-file-prefix",
                "Optional: Specifies a path to prepend to every file returned by the module. Useful for dealing when staging files back.")
                .withRequiredArg().ofType(String.class).describedAs("Path to prepend to each file location.");
        PARSER.accepts(
                "metadata-processingID-file",
                "Optional (DEPRECATED): Specifies the path to a file, which we will write our processingID, for future processing events to parse. This is deprecated, use metadata-processing-accession-file going forward.")
                .withRequiredArg().ofType(String.class).describedAs("Path for where we should create a new file with our processing ID");
        PARSER.accepts("metadata-processing-accession-file",
                "Optional: Specifies the path to a file, which we will write our processing accession, for future processing events to parse.")
                .withRequiredArg().ofType(String.class).describedAs("Path for where we should create a new file with our processing ID");
        PARSER.accepts("metadata-processing-accession-file-lock",
                "Optional: Specifies the path to a file, which we will write/check our processing accession, for use to prevent repeated runs.")
                .withRequiredArg().ofType(String.class).describedAs("Path for where we should create a new file with our processing ID");
        PARSER.accepts("metadata-tries-number",
                "Optional: After a failure, how many times we should try metadata write back operations, such as obtaining a lock, writing to DB, etc.")
                .withRequiredArg().ofType(Integer.class).defaultsTo(60).describedAs("Number of tries (Default: 60)");
        PARSER.accepts("metadata-tries-delay",
                "Optional: After a failure, how long we should wait before trying again (in accordance with metadata-tries-number)")
                .withRequiredArg().ofType(Integer.class).defaultsTo(5).describedAs("Number of seconds between tries (Default: 5)");
        PARSER.accepts(
                "metadata-workflow-accession",
                "Optional: Specifies the workflow accession that this run of the workflow should be associated with. This creates a new row in the workflow_run table and links it to the workflow row specified by this accession.")
                .withRequiredArg().ofType(Integer.class).describedAs("The sw_accession of a workflow table row.");
        PARSER.accepts("metadata-workflow-run-accession",
                "Optional: Specifies the workflow-run accession that should be saved in this processing event's workflow_run_id column.")
                .withRequiredArg().ofType(Integer.class)
                .describedAs("The sw_accession of a workflow-run table row that should be filled into the workflow_run_id field.");
        PARSER.accepts(
                "metadata-workflow-run-accession-output-file",
                "Optional: Specifies the file that the workflow-run accession is written out to. This is so subsequent steps can reference it to correctly populate the ancestor_workflow_run_id in child processing events.")
                .withRequiredArg().ofType(String.class)
                .describedAs("The file a sw_accession of a workflow-run table row should be written to.");
        PARSER.accepts("metadata-workflow-run-ancestor-accession",
                "Optional: Specifies the workflow-run accession that should be saved in this processing event's ancestor_workflow_run_id column.")
                .withRequiredArg().ofType(Integer.class)
                .describedAs("The sw_accession of a workflow-run table row that should be filled into the ancestor_workflow_run_id field.");
        PARSER.accepts(
                "metadata-workflow-run-ancestor-accession-input-file",
                "Optional: Specifies the workflow-run accession file that should be read and the contained workflow-run accession should be saved in this processing event's ancestor_workflow_run_id column.")
                .withRequiredArg()
                .ofType(String.class)
                .describedAs(
                        "The file contains the sw_accession of a workflow-run table row that should be filled into the ancestor_workflow_run_id field.");
        // Debugging stuff
        PARSER.accepts("sleep-between-steps", "Optional: For debugging, allows one to specify a time to sleep between steps")
                .withRequiredArg().ofType(Integer.class).defaultsTo(0).describedAs("Time in Seconds (Default: 0)");
        PARSER.accepts("suppress-unimplemented-warnings", "Optional: For debugging, hide warnings about unimplemented methods");
        PARSER.accepts("verbose", "Show debug information");
        this.nonOptionSpec = PARSER
                .nonOptions("Specify arguments for the module by providding an additional -- and then --<key> <value> pairs");
    }

    /**
     * <p>
     * getSyntax.
     * </p>
     * 
     * @param parser
     *            a {@link joptsimple.OptionParser} object.
     * @param errorMessage
     *            a {@link java.lang.String} object.
     */
    public static void getSyntax(OptionParser parser, String errorMessage) {
        if (errorMessage != null && errorMessage.length() > 0) {
            Log.stderr("ERROR: " + errorMessage);
            Log.stderr("");
        }
        Log.stdout("Syntax: java net.sourceforge.seqware.pipeline.runner.Runner [--help] [--verbose] [--output std_out_file] [other_runner_params] --module Module -- [ModuleParameters]");
        Log.stdout("");
        Log.stdout("--> ModuleParameters are passed directly to the Module and ignored by the Runner. ");
        Log.stdout("--> You must pass '--' right after the Module in order to prevent the ModuleParameters being parsed by the runner!");
        Log.stdout("");
        Log.stdout("Runner parameters are limited to the following:");
        Log.stdout("-help, --help, -h      display this help message");
        try {
            parser.printHelpOn(System.err);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        System.exit(-1);
    }

    private void writeProcessingAccessionToFile(File file, boolean append) {
        int maxTries = (Integer) options.valueOf("metadata-tries-number");
        for (int i = 0; i < maxTries; i++) {
            // Break on success
            if (LockingFileTools.lockAndWrite(file, processingAccession + System.getProperty("line.separator"), append)) {
                break;
            } // Sleep if going to try again
            else if (i < maxTries) {
                ProcessTools.sleep((Integer) options.valueOf("metadata-tries-delay"));
            } // Return error if failed on last try
            else {
                ReturnValue retval = new ReturnValue();
                retval.printAndAppendtoStderr("Could not write to processingAccession File for metadata");
                retval.setExitStatus(ReturnValue.METADATAINVALIDIDCHAIN);
                meta.update_processing_event(processingID, retval);
                meta.update_processing_status(processingID, ProcessingStatus.failed);
                System.exit(retval.getExitStatus());
            }
        }
    }

    /**
     * <p>
     * printAndAppendtoStderr.
     * </p>
     * 
     * @param buffer
     *            a {@link java.lang.String} object.
     */
    public void printAndAppendtoStderr(String buffer) {
        stderr.append(buffer).append("\n");
        System.err.print(buffer + "\n");
    }

    /**
     * <p>
     * printAndAppendtoStdout.
     * </p>
     * 
     * @param buffer
     *            a {@link java.lang.String} object.
     */
    public void printAndAppendtoStdout(String buffer) {
        stdout.append(buffer).append("\n");
        System.out.print(buffer + "\n");
    }

    // If exit status was greater than zero, print returned error and exit
    // FIXME: this method is doing a poor job of trapping errors, needs to be
    // cleaned up, the runner should not try to continue with subsequent steps if
    // one fails!
    /**
     * <p>
     * evaluateReturn.
     * </p>
     * 
     * @param app
     *            a {@link net.sourceforge.seqware.pipeline.module.Module} object.
     * @param methodName
     *            a {@link java.lang.String} object.
     */
    public void evaluateReturn(Module app, String methodName) {

        Log.debug("EvaluateReturn for " + methodName);
        // If metaDB is defined, let's update status to methodName so we know what
        // we are running
        if (meta != null && processingID != 0) {
            meta.update_processing_status(processingID, ProcessingStatus.running);
        }

        Method method;
        ReturnValue newReturn = null;

        try {
            method = app.getClass().getMethod(methodName);
            app.setProcessingAccession(this.processingAccession);
            if (meta != null) {
                app.setMetadata(meta);
            }
            newReturn = (ReturnValue) method.invoke(app);

            // Fix filepaths if specified
            if (options.has("metadata-output-file-prefix")) {
                ArrayList<FileMetadata> files = newReturn.getFiles();
                if (files != null) {
                    for (FileMetadata file : files) {
                        file.prependToFilePath((String) options.valueOf("metadata-output-file-prefix"));
                    }
                }
            }

        } catch (Exception e) {
            printAndAppendtoStderr("Module caught exception during method: " + methodName + ":" + e.getMessage());
            printAndAppendtoStderr(ExceptionTools.stackTraceToString(e));
            ReturnValue ret = new ReturnValue(stdout.toString(), stderr.toString(), ReturnValue.RUNNERERR);

            // Update processing table to show it failed
            if (meta != null && processingID != 0) {
                meta.update_processing_event(processingID, ret);
                meta.update_processing_status(processingID, ProcessingStatus.failed);
            }

            // Exit on error
            System.exit(ReturnValue.RUNNERERR);
        }

        Log.debug("Past section1 of EvaluateReturn " + methodName);

        // Print STDERR/STDOUT and then set the full stderr/stdout in the
        // returnvalue
        if (newReturn.getStdout() != null) {
            printAndAppendtoStdout(newReturn.getStdout());
            newReturn.setStdout(stdout.toString());
        }
        if (newReturn.getStderr() != null) {
            printAndAppendtoStderr(newReturn.getStderr());
            newReturn.setStderr(stderr.toString());
        }

        Log.debug("Past section2 of EvaluateReturn " + methodName);

        // On failure, update metadb and exit
        if (newReturn.getExitStatus() > ReturnValue.SUCCESS) {
            Log.debug("Section3 of EvaluateReturn, failure");
            printAndAppendtoStderr("The method '" + methodName + "' exited abnormally so the Runner will terminate here!");
            printAndAppendtoStderr("Return value was: " + newReturn.getExitStatus());

            // Update processing table to show it failed
            if (meta != null && processingID != 0) {
                newReturn.setStdout(stdout.toString());
                newReturn.setStderr(stderr.toString());
                meta.update_processing_event(processingID, newReturn);
                meta.update_processing_status(processingID, ProcessingStatus.failed);
            }
            Log.debug("Attempting exit");
            System.exit(newReturn.getExitStatus());
        } // Otherwise we will continue, after updating metadata
        else {
            Log.debug("Section3 of EvaluateReturn, success");
            // If it returned unimplemented, let's warn
            if (newReturn.getExitStatus() < ReturnValue.SUCCESS) {
                newReturn.setExitStatus(ReturnValue.NULL);
            }
            Log.debug("Section3 of EvaluateReturn, update metadata");
            // Update metadata if we can
            if (meta != null && processingID != 0) {
                newReturn.setStdout(stdout.toString());
                newReturn.setStderr(stderr.toString());
                meta.update_processing_event(processingID, newReturn);
            }
        }
        Log.debug("Past section3 of EvaluateReturn " + methodName);

        // If were are supposed to sleep after steps, do so
        ProcessTools.sleep((Integer) options.valueOf("sleep-between-steps"));
    }

    /**
     * FIXME: this needs to be migrated to something that is ZIP64 aware. Try using the unzip feature of FileTools.java
     * 
     * @param zipFile
     *            a {@link java.lang.String} object.
     * @throws java.util.zip.ZipException
     *             if any.
     * @throws java.io.IOException
     *             if any.
     */
    public static void unzipPkg(String zipFile) throws ZipException, IOException {

        Log.info(zipFile);
        int buffer = 2048;
        File file = new File(zipFile);

        ZipFile zip = new ZipFile(file);

        String[] pkgNames = zipFile.substring(0, zipFile.length() - 4).split(File.separator);
        String pkgName = pkgNames[pkgNames.length - 1];
        // Log.info(pkgName);
        String newPath = System.getProperty("user.dir").concat(File.separator).concat(pkgName);

        Log.info(newPath);

        new File(newPath).mkdir();
        Enumeration zipFileEntries = zip.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();

            String currentEntry = entry.getName();

            File destFile = new File(newPath, currentEntry);
            destFile = new File(newPath, destFile.getName());
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();
            if (!entry.isDirectory()) {
                try (BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry))) {
                    int currentByte;
                    // establish buffer for writing file
                    byte data[] = new byte[buffer];

                    // write the current file to disk
                    FileOutputStream fos = new FileOutputStream(destFile);
                    // read and write until last byte is encountered
                    try (BufferedOutputStream dest = new BufferedOutputStream(fos, buffer)) {
                        // read and write until last byte is encountered
                        while ((currentByte = is.read(data, 0, buffer)) != -1) {
                            dest.write(data, 0, currentByte);
                        }
                        dest.flush();
                    }
                }
            }
            if (currentEntry.endsWith(".zip")) {
                // found a zip file, try to open
                unzipPkg(destFile.getAbsolutePath());
            }
        }
    }

    /**
     * <p>
     * main.
     * </p>
     * 
     * @param args
     *            an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        new Runner().run(args);
    }

    /**
     * Do all the syntax here
     */
    private void checkSyntax() {
        // Check if help was requested
        if (options.has("help") || options.has("h") || options.has("?")) {
            getSyntax(PARSER, "");
        }

        // check if verbose was requested, then override the log4j.properties
        if (options.has("verbose")) {
            Log.setVerbose(true);
        }

        /**
         * Why consider this wrong? Can a user module have their arugment as "--help"? If people want to see the Runner's help, they know
         * that they should do it as optional argument.
         */
        if (options.nonOptionArguments().contains("--help") || options.nonOptionArguments().contains("-help")
                || options.nonOptionArguments().contains("-h") || options.nonOptionArguments().contains("-?")) {
            Log.error(app.get_syntax());
            System.exit(-1);
        }

        // check if there is conflict between no-metadata and other setup
        // I'm taking this out because we want to be able to pass in all the
        // metadata options to a module but suppress writeback to the
        // DB by including the --no-metadata option. This makes it much easier to
        // have workflows that can write back to the DB or not
        // depending on one small flag set by the runner rather than a whole if/then
        // block to specify either --no-metadb or all the metadb params
        // --BOC 20110810
        if (bothInOptions(new String[] { "no-metadata", "no-meta-db" }, new String[] { "metadata", "meta-db" })) {
            Log.error("The module argument is inconsistent: both \'no-metadata\' and \'metadata-xxx\' are specified");
            System.exit(-1);
        }

        /*
         * From check above "metadata-config-database", "metadata-config-username", "metadata-config-password", "metadata-parentID",
         * "metadata-parentID-file", "metadata-processingID-file", "metadata-parent-accession", "metadata-parent-accession-file",
         * "metadata-processing-accession-file", "metadata-tries-number", "metadata-tries-delay", "metadata-workflow-accession",
         * "metadata-workflow-run-accession-output-file", "metadata-workflow-run-ancestor-accession",
         * "metadata-workflow-run-ancestor-accession-input-file"
         */

        if (bothInOptions(new String[] { "metadata-parentID", "metadata-parentID-file", "metadata-processingID-file" }, new String[] {
                "metadata-parent-accession", "metadata-parent-accession-file", "metadata-processing-accession-file" })) {
            Log.error("You cannot specify both parent processing IDs and parent processing Accessions. You should use one or the other (use Accessions since IDs are deprecated).");
            System.exit(-1);
        }

        if (options.valuesOf("metadata-workflow-accession").size() > 1) {
            Log.error("You can't have more than one metadata-workflow-accession");
            System.exit(ReturnValue.INVALIDARGUMENT);
        }

        if (options.valuesOf("metadata-workflow-run-accession").size() > 1) {
            Log.error("You can't have more than one metadata-workflow-run-accession values");
            System.exit(ReturnValue.INVALIDARGUMENT);
        }
    }

    private void setupModuleApp() {
        String moduleName = null;
        if (options.has("module")) {
            moduleName = options.valueOf("module").toString();
            Log.info(moduleName);
        } else if (options.has("module-pkg")) {
            // Log.error("Failure in module-pkg detection");
            String modName = options.valueOf("module-pkg").toString();
            String[] modNames = modName.substring(0, modName.length() - 4).split(File.separator);
            // temp we will look in examples, but really we just want all these in
            // "net.sourceforge.seqware.pipeline.modules."
            // ModuleName = "wrapper.".concat(modNames[modNames.length -1]);
            moduleName = modNames[modNames.length - 1].concat(".wrapper.").concat(modNames[modNames.length - 1]);
            // ModuleName = modNames[modNames.length
            // -1].concat(".net.sourceforge.seqware.pipeline.modules.").concat(modNames[modNames.length
            // -1]);
            Log.info(moduleName);
        } else {
            getSyntax(PARSER, "You must specifiy a --module or a --module-pkg parameter");
        }

        try {
            app = (Module) Class.forName(moduleName).newInstance();
            app.setAlgorithm(moduleName);

            if (options.has("output")) {
                app.setStdoutFile(new File(options.valueOf("output").toString()));
            }

            if (options.has("stderr")) {
                app.setStderrFile(new File(options.valueOf("stderr").toString()));
            }
        } catch (ClassNotFoundException e) {
            Log.error("Could not find the Module class for '" + moduleName + "'");
            System.exit(-1);
        } catch (Throwable e) {
            e.printStackTrace();
            Log.error(e);
            System.exit(-1);
        }
        app.setParameters(options.valuesOf(nonOptionSpec));
    }

    private void preProcessMetadata() {
        if (optionHasOneOf(new String[] { "no-metadata", "no-meta-db" })) {
            Log.debug("Metadata writeback disabled. To enable metadata writeback make sure you setup your .seqware/settings file properly (recommened) or provide --metadata-config-database, --metadata-config-username and --metadata-config-password as arguments to this program");
            return;
        }

        Map<String, String> settings = ConfigTools.getSettings();

        // Should try settings only if when user does not specify
        // "metadata-config-database" etc. Although it is legacy
        // code, however, user using those will not know to set up the .seqware
        // file.

        if (optionHasOneOf(new String[] { "metadata-config-database", "metadata-config-username", "metadata-config-password" })) {
            // legacy mode
            String user = options.valueOf("metdata-config-username").toString();
            String pass = options.valueOf("metadata-config-password").toString();
            String connection = options.valueOf("metadata-config-database").toString();
            try {
                meta = new MetadataDB(connection, user, pass);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            meta = MetadataFactory.get(settings);
        }

        // If we established a metadb connection, let's find all parentID
        // processingIDs
        // This is deprecated, should use metadata-parent-accession and
        // metadata-parent-accession file in the future
        int workflowRunId = 0;
        int workflowRunAccession = 0;
        ArrayList<Integer> parentIDs = new ArrayList<>();
        processingIDFiles = new ArrayList<>();
        ArrayList<Integer> parentAccessions = new ArrayList<>();
        processingAccessionFiles = new ArrayList<>();
        int ancestorWorkflowRunAccession = 0;

        // Abort run if lock file is valid and points to valid processing event
        if (options.has("metadata-processing-accession-file-lock")) {
            String outputFile = (String) options.valueOf("metadata-processing-accession-file-lock");
            File file = new File(outputFile);
            try {
                // if the file exists, check to see if it has a valid processing accession first
                if (file.exists() && file.canRead()) {
                    String readFileToString = FileUtils.readFileToString(file).trim();
                    try {
                        int processingAccessionFromFile = Integer.valueOf(readFileToString);
                        Processing proc = meta.getProcessing(processingAccessionFromFile);
                        if (proc != null) {
                            if (proc.getStatus().equals(ProcessingStatus.success)) {
                                // if a previous run was successful, simply abort
                                Log.error("Lock file exists with a previous success, skipping");
                                System.exit(ReturnValue.SUCCESS);
                            }
                        }
                    } catch (NumberFormatException | NotFoundException ne) {
                        // means that the file doesn't contain a valid processing sw_accession, proceed
                        Log.error("Lock file exists with an invalid processing accession, continuing");
                    }
                    Log.error("Lock file exists with a non-success, continuing");
                }

                if ((file.exists() || file.createNewFile()) && file.canWrite()) {
                    processingAccessionFileCheck = file;
                } else {
                    Log.error("Could not create processingAccession check File for metadata");
                    System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
                }
            } catch (IOException e) {
                Log.error("Could not create processingAccession check File for metadata: " + e.getMessage());
                System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
            }
        }

        // create a workflow_run row and link it back to the correct workflow ID
        // we have checked earlier that metadata-workflow-accession cannot have more
        // than one value
        // List<Integer> workflowAccessions = (List<Integer>)
        // options.valuesOf("metadata-workflow-accession");
        if (options.has("metadata-workflow-accession")) {
            workflowRunId = meta.add_workflow_run((Integer) options.valueOf("metadata-workflow-accession"));
            workflowRunAccession = meta.get_workflow_run_accession(workflowRunId);
            // now try to write out the workflow_run_accession created above
            // only doing this for the first file
            for (String outputFile : (List<String>) options.valuesOf("metadata-workflow-run-accession-output-file")) {
                File file = new File(outputFile);

                try {
                    if ((file.exists() || file.createNewFile()) && file.canWrite()) {
                        int maxTries = (Integer) options.valueOf("metadata-tries-number");
                        for (int i = 0; i < maxTries; i++) {
                            // Break on success
                            if (LockingFileTools.lockAndAppend(file, workflowRunAccession + System.getProperty("line.separator"))) {
                                break;
                            } // Sleep if going to try again
                            else if (i < maxTries) {
                                ProcessTools.sleep((Integer) options.valueOf("metadata-tries-delay"));
                            } // Return error if failed on last try
                            else {
                                ReturnValue retval = new ReturnValue();
                                retval.printAndAppendtoStderr("Could not write to processingID File for metadata");
                                retval.setExitStatus(ReturnValue.METADATAINVALIDIDCHAIN);
                                meta.update_processing_event(workflowRunAccession, retval);
                                meta.update_processing_status(workflowRunAccession, ProcessingStatus.failed);
                                System.exit(retval.getExitStatus());
                            }
                        }
                    } else {
                        Log.error("Could not create processingAccession File for metadata");
                        System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
                    }
                } catch (IOException e) {
                    Log.error("Could not create processingAccession File for metadata: " + e.getMessage());
                    System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
                }

                break;
            }
        }

        // you can pass in a workflow_run sw_accession directly so an existing
        // record can be used
        // associate with an existing workflow_run row
        List<Integer> workflowRunAccessions = (List<Integer>) options.valuesOf("metadata-workflow-run-accession");
        if (workflowRunAccessions.size() == 1) {
            workflowRunAccession = workflowRunAccessions.get(0);
        }

        // collect the processing parent IDs. This is deprecated, use the
        // parentAccessions going forward.
        for (Integer parent : (List<Integer>) options.valuesOf("metadata-parentID")) {
            parentIDs.add(parent);
        }
        for (String file : (List<String>) options.valuesOf("metadata-parentID-file")) {
            try {
                BufferedReader r;
                String line;
                r = new BufferedReader(new FileReader(file));

                while ((line = r.readLine()) != null) {
                    try {
                        parentIDs.add(Integer.parseInt(line));
                    } catch (NumberFormatException ex) {
                        Log.error("Non number found when parsing parentID file '" + line + "'");
                        System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
                    }
                }
                r.close();
            } catch (Exception e) {
                Log.error("Could not open parentID file for metadata: " + e.getMessage());
                System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
            }
        }

        // Now that we've collected processing event IDs that are used as parents,
        // do the same thing but
        // for sw_accessions that can be from processing, lane, ius, or sequence_run
        // tables
        for (Integer parent : (List<Integer>) options.valuesOf("metadata-parent-accession")) {
            parentAccessions.add(parent);
        }
        for (String file : (List<String>) options.valuesOf("metadata-parent-accession-file")) {
            try {
                BufferedReader r;
                String line;
                r = new BufferedReader(new FileReader(file));

                while ((line = r.readLine()) != null) {
                    try {
                        parentAccessions.add(Integer.parseInt(line));
                    } catch (NumberFormatException ex) {
                        Log.error("Non number found when parsing parent accession file '" + line + "'");
                        System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
                    }
                }
                r.close();
            } catch (Exception e) {
                Log.error("Could not open parent accession file for metadata-parent-accession-file: " + e.getMessage());
                System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
            }
        }

        // collect the workflow-run-ancestor-accessions
        for (Integer accession : (List<Integer>) options.valuesOf("metadata-workflow-run-ancestor-accession")) {
            ancestorWorkflowRunAccession = accession;
        }

        // collect the workflow_run_id for the ancestor for this event, only the
        // latest ID is saved if there are multiple in the file
        for (String file : (List<String>) options.valuesOf("metadata-workflow-run-ancestor-accession-input-file")) {
            try {
                BufferedReader r;
                String line;
                r = new BufferedReader(new FileReader(file));

                while ((line = r.readLine()) != null) {
                    try {
                        ancestorWorkflowRunAccession = Integer.parseInt(line);
                    } catch (NumberFormatException ex) {
                        Log.error("Non number found when parsing parent accession file '" + line + "'");
                        System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
                    }
                }
                r.close();
            } catch (Exception e) {
                Log.error("Could not open parent accession file for metadata-workflow-run-ancestor-accession-input-file: " + e.getMessage());
                System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
            }
        }

        // Create new file to write ID to if specified, this is deprecated, should
        // use metadata-processing-accession-file
        for (String outputFile : (List<String>) options.valuesOf("metadata-processingID-file")) {
            File file = new File(outputFile);

            try {
                if ((file.exists() || file.createNewFile()) && file.canWrite()) {
                    processingIDFiles.add(file);
                } else {
                    Log.error("Could not create processingID File for metadata");
                    System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
                }
            } catch (IOException e) {
                Log.error("Could not create processingID File for metadata: " + e.getMessage());
                System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
            }
        }

        // Create new file to write accession to if specified
        for (String outputFile : (List<String>) options.valuesOf("metadata-processing-accession-file")) {
            File file = new File(outputFile);

            try {
                if ((file.exists() || file.createNewFile()) && file.canWrite()) {
                    processingAccessionFiles.add(file);
                } else {
                    Log.error("Could not create processingAccession File for metadata");
                    System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
                }
            } catch (IOException e) {
                Log.error("Could not create processingAccession File for metadata: " + e.getMessage());
                System.exit(ReturnValue.METADATAINVALIDIDCHAIN);
            }
        }

        // Create a processing event associating metadata with parents and writing
        // to child files
        // BUGFIX: this processingID is a global variable for this object, defining
        // it here as a local variable broke metadata writeback!!
        // int processingID = 0;
        // alternative processing accession that serves the same purpose
        // BUGFIX: this processingAccession is a global variable for this object,
        // defining it here as a local variable broke metadata writeback!!
        processingAccession = 0;
        if (meta != null) {

            int[] parents = new int[parentIDs.size()];
            for (int i = 0; i < parentIDs.size(); i++) {
                parents[i] = parentIDs.get(i);
            }
            // by accessions
            int[] parentAccessionsArray = new int[parentAccessions.size()];
            for (int i = 0; i < parentAccessions.size(); i++) {
                parentAccessionsArray[i] = parentAccessions.get(i);
            }

            // adding based on processing IDs (deprecated, should use sw_accession)
            ReturnValue metaret;
            // adding based on sw_accession
            if (parentAccessionsArray.length > 0) {
                metaret = meta.add_empty_processing_event_by_parent_accession(parentAccessionsArray);
                if (metaret.getExitStatus() == ReturnValue.SUCCESS) {
                    processingID = metaret.getReturnValue();
                    // translate to accession
                    processingAccession = meta.mapProcessingIdToAccession(processingID);
                    printAndAppendtoStdout("MetaDB ProcessingAccession for this run is: " + processingAccession);
                } else {
                    printAndAppendtoStderr("MetaDB failed with exit: " + metaret.getExitStatus());
                    if (metaret.getStdout() != null) {
                        printAndAppendtoStdout("STDOUT: " + metaret.getStdout());
                    }
                    if (metaret.getStderr() != null) {
                        printAndAppendtoStderr("STDERR: " + metaret.getStderr());
                    }

                    System.exit(ReturnValue.SQLQUERYFAILED);
                }
            } else {
                metaret = meta.add_empty_processing_event(parents);
                if (metaret.getExitStatus() == ReturnValue.SUCCESS) {
                    processingID = metaret.getReturnValue();
                    // translate to accession
                    processingAccession = meta.mapProcessingIdToAccession(processingID);
                    printAndAppendtoStdout("MetaDB ProcessingAccession for this run is: " + processingAccession);
                } else {
                    printAndAppendtoStderr("MetaDB failed with exit: " + metaret.getExitStatus());
                    if (metaret.getStdout() != null) {
                        printAndAppendtoStdout("STDOUT: " + metaret.getStdout());
                    }
                    if (metaret.getStderr() != null) {
                        printAndAppendtoStderr("STDERR: " + metaret.getStderr());
                    }

                    System.exit(ReturnValue.SQLQUERYFAILED);
                }
            }

            // now associate processing event ancestor_workflow_run_id
            if (ancestorWorkflowRunAccession > 0 && processingID > 0) {
                meta.add_workflow_run_ancestor(ancestorWorkflowRunAccession, processingID);
            }

            // associate with the workflow_run
            if (workflowRunAccession > 0 && processingID > 0) {
                meta.update_processing_workflow_run(processingID, workflowRunAccession);
            }
        }
    }

    private void postProcessMetadata() {
        Log.debug("Running postProcessMetadata");
        if (meta != null && processingID != 0) {

            // write out the accessions to file iff success
            // Try to write to each processingIDFile until success or timeout
            for (File file : processingIDFiles) {
                Log.debug("Writing out accession to " + file.toString());
                int maxTries = (Integer) options.valueOf("metadata-tries-number");
                for (int i = 0; i < maxTries; i++) {
                    // Break on success
                    if (LockingFileTools.lockAndAppend(file, processingID + System.getProperty("line.separator"))) {
                        break;
                    } // Sleep if going to try again
                    else if (i < maxTries) {
                        ProcessTools.sleep((Integer) options.valueOf("metadata-tries-delay"));
                    } // Return error if failed on last try
                    else {
                        ReturnValue retval = new ReturnValue();
                        retval.printAndAppendtoStderr("Could not write to processingID File for metadata");
                        retval.setExitStatus(ReturnValue.METADATAINVALIDIDCHAIN);
                        meta.update_processing_event(processingID, retval);
                        meta.update_processing_status(processingID, ProcessingStatus.failed);
                        System.exit(retval.getExitStatus());
                    }
                }
            }
            Log.debug("Completed processingIDFiles");

            // Try to write to each processingAccessionFile until success or timeout
            for (File file : processingAccessionFiles) {
                Log.debug("Writing out to " + file.toString());
                writeProcessingAccessionToFile(file, true);
            }
            Log.debug("Completed processingAccessionFiles");
            if (processingAccessionFileCheck != null) {
                writeProcessingAccessionToFile(processingAccessionFileCheck, false);
            }
            Log.debug("Completed processingAccessionFileCheck");
            meta.update_processing_status(processingID, ProcessingStatus.success);
        }
    }

    private void invokeModuleMethods() {
        // figure out if there is any change for redirecting stdout/stderr
        ModuleMethod outStart = null;
        ModuleMethod outEnd = null;
        StdoutRedirect outAnn = app.getClass().getAnnotation(StdoutRedirect.class);
        outStart = outAnn == null ? ModuleMethod.do_run : outAnn.startsBefore();
        outEnd = outAnn == null ? ModuleMethod.do_run : outAnn.endsAfter();

        ModuleMethod errStart;
        ModuleMethod errEnd;
        StderrRedirect errAnn = app.getClass().getAnnotation(StderrRedirect.class);
        errStart = errAnn == null ? ModuleMethod.do_run : errAnn.startsBefore();
        errEnd = errAnn == null ? ModuleMethod.do_run : errAnn.endsAfter();

        PrintStream oldOut = null;
        PrintStream oldErr = null;

        for (ModuleMethod m : ModuleMethod.values()) {
            Log.debug("Running method " + m.toString());
            // check stdout redirect
            if ((m == outStart) && (app.getStdoutFile() != null)) {
                try {
                    oldOut = System.out;
                    System.setOut(new PrintStream(new FileOutputStream(app.getStdoutFile())));
                } catch (FileNotFoundException e) {
                    printAndAppendtoStderr("Error in redirecting stdout to '" + app.getStdoutFile().getAbsolutePath() + "'.");
                    printAndAppendtoStderr(e.getMessage());
                    System.exit(ReturnValue.INVALIDFILE);
                }
            }
            // check stdout redirect
            if ((m == errStart) && (app.getStderrFile() != null)) {
                try {
                    oldErr = System.err;
                    System.setErr(new PrintStream(new FileOutputStream(app.getStderrFile())));
                } catch (FileNotFoundException e) {
                    printAndAppendtoStderr("Error in redirecting stderr to '" + app.getStderrFile().getAbsolutePath() + "'.");
                    printAndAppendtoStderr(e.getMessage());
                    System.exit(ReturnValue.INVALIDFILE);
                }
            }
            evaluateReturn(app, m.name());
            if ((m == outEnd) && (oldOut != null)) {
                System.setOut(oldOut);
            }

            if ((m == errEnd) && (oldErr != null)) {
                System.setErr(oldErr);
            }
        }
        Log.debug("Finishing invokeModuleMethods");
    }

    /**
     * <p>
     * run.
     * </p>
     * 
     * @param args
     *            an array of {@link java.lang.String} objects.
     */
    public void run(String[] args) {
        // 1. Parse the options
        try {
            options = PARSER.parse(args);
        } catch (OptionException e) {
            getSyntax(PARSER, e.getMessage());
        }
        // 2. Do syntax check
        checkSyntax();
        // 3. Set up app and return are needed forever
        setupModuleApp();
        // 4. handle metadata
        preProcessMetadata();
        // 5. Call each method
        invokeModuleMethods();
        // 6. postProcess
        postProcessMetadata();
        System.exit(ReturnValue.SUCCESS);
    }

    // -----Utility Method
    private boolean optionHasOneOf(String[] opts) {
        for (String o : opts) {
            if (options.has(o)) {
                return true;
            }
        }
        return false;
    }

    private boolean bothInOptions(String[] o1, String[] o2) {
        return optionHasOneOf(o1) && optionHasOneOf(o2);
    }
}
