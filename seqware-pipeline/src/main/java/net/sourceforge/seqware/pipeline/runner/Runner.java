package net.sourceforge.seqware.pipeline.runner;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.metadata.MetadataDB;
import net.sourceforge.seqware.common.metadata.MetadataWS;
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
 * <p>Runner class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Runner {

  private int processingID = 0;
  private ArrayList<File> processingIDFiles;
  private ArrayList<File> processingAccessionFiles;
  private int processingAccession = 0;

  public static interface Keys {

    String DB_USER = "SW_DB_USER";
    String DB_PASS = "SW_DB_PASS";
    String DB_NAME = "SW_DB";
    String DB_SERVER = "SW_DB_SERVER";
    String DB_CONN_STRING = "SW_DB_CONN_STRING";
    String WS_URL = "SW_REST_URL";
    String WS_USER = "SW_REST_USER";
    String WS_PASS = "SW_REST_PASS";
  }

  private static OptionParser parser = new OptionParser();
  private OptionSet options = null;
  private Module app = null;
  private Metadata meta = null;
  // I (Xiaoshu Wang) am not sure if it is a good idea to make these two
  // property static because if the same JVM calls
  // Runner twice, the value of the previous stdout/stderr will be kept.
  private StringBuffer stdout = new StringBuffer();
  private StringBuffer stderr = new StringBuffer();

  static {
    parser.acceptsAll(Arrays.asList("help", "h", "?"), "Provides this help message.");
    parser.accepts("module", "Required: Specifies the module to run. All modules implement the ModuleInterface.")
        .withRequiredArg().ofType(String.class).describedAs("This is the module you wish to run.");
    parser.accepts("output", "Optional: redirect StdOut to file.").withRequiredArg().ofType(String.class)
        .describedAs("File path to redirect StdOut to.");
    // added by Xiaoshu Wang (xiao@renci.org)
    parser.accepts("stderr", "Optional: redirect Stderr to file.").withRequiredArg().ofType(String.class)
        .describedAs("File path to redirect Stderr to.");

    parser
        .accepts("module-pkg",
            "Optional: but replaces need to specify module, by specifying instead a pre-packaged module to run.")
        .withRequiredArg().ofType(String.class).describedAs("This is the module you wish to run.");
    // MetaDB stuff
    parser
        .acceptsAll(
            Arrays.asList("meta-db", "metadata"),
            "Optional: This argument really has no effect since we attempt metadata writeback so long as --no-meta-db or --no-metadata aren't passed in. This is really an argument to make it easier to have an if/else control over metadata writeback in calling programs.");
    parser.acceptsAll(Arrays.asList("no-meta-db", "no-metadata"),
        "Optional: Do not use metadata writeback. Otherwise, we will attempt it.");
    parser
        .accepts("metadata-config-database",
            "Required for metadata (DEPRECATED, use .seqware/settings file instead): The JDBC path for connection")
        .withRequiredArg().ofType(String.class).describedAs("Example: jdbc:postgresql://127.0.0.1/seqware_meta_db");
    parser
        .accepts("metadata-config-username",
            "Required for metadata (DEPRECATED, use .seqware/settings file instead): Database username for connection")
        .withRequiredArg().ofType(String.class).describedAs("Database Username");
    parser
        .accepts("metadata-config-password",
            "Required for metadata (DEPRECATED, use .seqware/settings file instead): Database password for connection")
        .withRequiredArg().ofType(String.class).describedAs("Database Password");

    parser
        .accepts(
            "metadata-parentID",
            "Optional (DEPRECATED): Specifies one of the parentID for metadata write back. This option can be specified zero or more times. This is deprecated, use metadata-parent-accession going forward.")
        .withRequiredArg()
        .ofType(Integer.class)
        .describedAs(
            "The processingID of the parent for this event, for constructing the dependency tree in the metadb");
    parser
        .accepts(
            "metadata-parentID-file",
            "Optional (DEPRECATED): The same as --metadata-parentID, but is a path to a file, to parse for parent processing ID's. This is deprecated, use metadata-parent-accession-file going forward.")
        .withRequiredArg().ofType(String.class)
        .describedAs("Path to a line-delimeted file containing one or more parent processing IDs");
    parser
        .accepts(
            "metadata-parent-accession",
            "Optional: Specifies one of the SeqWare accessions (sw_accession column in the DB) for metadata write back. This is an alternative "
                + "to processing parentID (see --metadata-parentID) that allows you to specify an IUS, lane, sequencer run, or other processing event as a parent. "
                + " This option can be specified zero or more times.")
        .withRequiredArg()
        .ofType(Integer.class)
        .describedAs(
            "The sw_accession of the parent for this event, for constructing the dependency tree in the metadb");
    parser
        .accepts(
            "metadata-parent-accession-file",
            "Optional: The same as --metadata-parent-accession, but is a path to a file, to parse for parent processing sw_accessions.")
        .withRequiredArg().ofType(String.class)
        .describedAs("Path to a line-delimeted file containing one or more parent sw_accessions");
    parser
        .accepts(
            "metadata-output-file-prefix",
            "Optional: Specifies a path to prepend to every file returned by the module. Useful for dealing when staging files back.")
        .withRequiredArg().ofType(String.class).describedAs("Path to prepend to each file location.");
    parser
        .accepts(
            "metadata-processingID-file",
            "Optional (DEPRECATED): Specifies the path to a file, which we will write our processingID, for future processing events to parse. This is deprecated, use metadata-processing-accession-file going forward.")
        .withRequiredArg().ofType(String.class)
        .describedAs("Path for where we should create a new file with our processing ID");
    parser
        .accepts(
            "metadata-processing-accession-file",
            "Optional: Specifies the path to a file, which we will write our processing accession, for future processing events to parse.")
        .withRequiredArg().ofType(String.class)
        .describedAs("Path for where we should create a new file with our processing ID");
    parser
        .accepts(
            "metadata-tries-number",
            "Optional: After a failure, how many times we should try metadata write back operations, such as obtaining a lock, writing to DB, etc.")
        .withRequiredArg().ofType(Integer.class).defaultsTo(60).describedAs("Number of tries (Default: 60)");
    parser
        .accepts("metadata-tries-delay",
            "Optional: After a failure, how long we should wait before trying again (in accordance with metadata-tires-number)")
        .withRequiredArg().ofType(Integer.class).defaultsTo(5)
        .describedAs("Number of seconds between tries (Default: 5)");
    parser
        .accepts(
            "metadata-workflow-accession",
            "Optional: Specifies the workflow accession that this run of the workflow should be associated with. This creates a new row in the workflow_run table and links it to the workflow row specified by this accession.")
        .withRequiredArg().ofType(Integer.class).describedAs("The sw_accession of a workflow table row.");
    parser
        .accepts(
            "metadata-workflow-run-accession",
            "Optional: Specifies the workflow-run accession that should be saved in this processing event's workflow_run_id column.")
        .withRequiredArg()
        .ofType(Integer.class)
        .describedAs(
            "The sw_accession of a workflow-run table row that should be filled into the workflow_run_id field.");
    parser
        .accepts(
            "metadata-workflow-run-accession-output-file",
            "Optional: Specifies the file that the workflow-run accession is written out to. This is so subsequent steps can reference it to correctly populate the ancestor_workflow_run_id in child processing events.")
        .withRequiredArg().ofType(String.class)
        .describedAs("The file a sw_accession of a workflow-run table row should be written to.");
    parser
        .accepts(
            "metadata-workflow-run-ancestor-accession",
            "Optional: Specifies the workflow-run accession that should be saved in this processing event's ancestor_workflow_run_id column.")
        .withRequiredArg()
        .ofType(Integer.class)
        .describedAs(
            "The sw_accession of a workflow-run table row that should be filled into the ancestor_workflow_run_id field.");
    parser
        .accepts(
            "metadata-workflow-run-ancestor-accession-input-file",
            "Optional: Specifies the workflow-run accession file that should be read and the contained workflow-run accession should be saved in this processing event's ancestor_workflow_run_id column.")
        .withRequiredArg()
        .ofType(String.class)
        .describedAs(
            "The file contains the sw_accession of a workflow-run table row that should be filled into the ancestor_workflow_run_id field.");
    // Debugging stuff
    parser
        .accepts("sleep-between-steps", "Optional: For debugging, allows one to specify a time to sleep between steps")
        .withRequiredArg().ofType(Integer.class).defaultsTo(0).describedAs("Time in Seconds (Default: 0)");
    parser.accepts("suppress-unimplemented-warnings",
        "Optional: For debugging, hide warnings about unimplemented methods");
  }

  /**
   * <p>getSyntax.</p>
   *
   * @param parser a {@link joptsimple.OptionParser} object.
   * @param errorMessage a {@link java.lang.String} object.
   */
  public static void getSyntax(OptionParser parser, String errorMessage) {
    if (errorMessage != null && errorMessage.length() > 0) {
      Log.stderr("ERROR: " + errorMessage);
      Log.stderr("");
    }
    Log.stdout("Syntax: java net.sourceforge.seqware.pipeline.runner.Runner [--help] [--output std_out_file] [other_runner_params] --module Module -- [ModuleParameters]");
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

  /**
   * <p>printAndAppendtoStderr.</p>
   *
   * @param buffer a {@link java.lang.String} object.
   */
  public void printAndAppendtoStderr(String buffer) {
    stderr.append(buffer + "\n");
    System.err.print(buffer + "\n");
  }

  /**
   * <p>printAndAppendtoStdout.</p>
   *
   * @param buffer a {@link java.lang.String} object.
   */
  public void printAndAppendtoStdout(String buffer) {
    stdout.append(buffer + "\n");
    System.out.print(buffer + "\n");
  }

  // If exit status was greater than zero, print returned error and exit
  // FIXME: this method is doing a poor job of trapping errors, needs to be
  // cleaned up, the runner should not try to continue with subsequent steps if
  // one fails!
  /**
   * <p>evaluateReturn.</p>
   *
   * @param app a {@link net.sourceforge.seqware.pipeline.module.Module} object.
   * @param methodName a {@link java.lang.String} object.
   */
  public void evaluateReturn(Module app, String methodName) {
    printAndAppendtoStdout(app.getClass().getName() + "." + methodName);

    // If metaDB is defined, let's update status to methodName so we know what
    // we are running
    if (meta != null && processingID != 0) {
      meta.update_processing_status(processingID, methodName + " " + Metadata.RUNNING);
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
          for (int i = 0; i < files.size(); i++) {
            files.get(i).prependToFilePath((String) options.valueOf("metadata-output-file-prefix"));
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
        meta.update_processing_status(processingID, Metadata.FAILED);
      }

      // Exit on error
      System.exit(ReturnValue.RUNNERERR);
    }

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

    // On failure, update metadb and exit
    if (newReturn.getExitStatus() > ReturnValue.SUCCESS) {
      printAndAppendtoStderr("The method '" + methodName + "' exited abnormally so the Runner will terminate here!");
      printAndAppendtoStderr("Return value was: " + newReturn.getExitStatus());

      // Update processing table to show it failed
      if (meta != null && processingID != 0) {
        newReturn.setStdout(stdout.toString());
        newReturn.setStderr(stderr.toString());
        meta.update_processing_event(processingID, newReturn);
        meta.update_processing_status(processingID, Metadata.FAILED);
      }
      System.exit(newReturn.getExitStatus());
    } // Otherwise we will continue, after updating metadata
    else {
      // If it returned unimplemented, let's warn
      if (newReturn.getExitStatus() < ReturnValue.SUCCESS) {
        if (!options.has("suppress-unimplemented-warnings")) {
          // newReturn.printAndAppendtoStderr("WARNING: The method '" +
          // methodName + "' returned exit value of " +
          // newReturn.getExitStatus() + ".");
          // newReturn.printAndAppendtoStderr("WARNING: This means an unimplemented features (such as an unneeded optional cleanup or init step!");
          // newReturn.printAndAppendtoStderr("WARNING: If you are finding errors not being caught, check that your wrapper returns a positive exit code and not negative!");
        }

        newReturn.setExitStatus(ReturnValue.NULL);
      }

      // Update metadata if we can
      if (meta != null && processingID != 0) {
        newReturn.setStdout(stdout.toString());
        newReturn.setStderr(stderr.toString());
        meta.update_processing_event(processingID, newReturn);
      }
    }

    // If were are supposed to sleep after steps, do so
    ProcessTools.sleep((Integer) options.valueOf("sleep-between-steps"));
  }

  /**
   * FIXME: this needs to be migrated to something that is ZIP64 aware. Try
   * using the unzip feature of FileTools.java
   *
   * @param zipFile a {@link java.lang.String} object.
   * @throws java.util.zip.ZipException if any.
   * @throws java.io.IOException if any.
   */
  public static void unzipPkg(String zipFile) throws ZipException, IOException {

    Log.info(zipFile);
    int BUFFER = 2048;
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
        BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
        int currentByte;
        // establish buffer for writing file
        byte data[] = new byte[BUFFER];

        // write the current file to disk
        FileOutputStream fos = new FileOutputStream(destFile);
        BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

        // read and write until last byte is encountered
        while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
          dest.write(data, 0, currentByte);
        }
        dest.flush();
        dest.close();
        is.close();
      }
      if (currentEntry.endsWith(".zip")) {
        // found a zip file, try to open
        unzipPkg(destFile.getAbsolutePath());
      }
    }
  }

  /**
   * <p>main.</p>
   *
   * @param args an array of {@link java.lang.String} objects.
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
      getSyntax(parser, "");
    }

    /**
     * Why consider this wrong? Can a user module have their arugment as
     * "--help"? If people want to see the Runner's help, they know that they
     * should do it as optional argument.
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
     * From check above "metadata-config-database", "metadata-config-username",
     * "metadata-config-password", "metadata-parentID",
     * "metadata-parentID-file", "metadata-processingID-file",
     * "metadata-parent-accession", "metadata-parent-accession-file",
     * "metadata-processing-accession-file", "metadata-tries-number",
     * "metadata-tries-delay", "metadata-workflow-accession",
     * "metadata-workflow-run-accession-output-file",
     * "metadata-workflow-run-ancestor-accession",
     * "metadata-workflow-run-ancestor-accession-input-file"
     */

    if (bothInOptions(new String[] { "metadata-parentID", "metadata-parentID-file", "metadata-processingID-file" },
        new String[] { "metadata-parent-accession", "metadata-parent-accession-file",
            "metadata-processing-accession-file" })) {
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
    String ModuleName = null;
    if (options.has("module")) {
      ModuleName = options.valueOf("module").toString();
      Log.info(ModuleName);
    } else if (options.has("module-pkg")) {
      // Log.error("Failure in module-pkg detection");
      String ModName = options.valueOf("module-pkg").toString();
      String[] modNames = ModName.substring(0, ModName.length() - 4).split(File.separator);
      // temp we will look in examples, but really we just want all these in
      // "net.sourceforge.seqware.pipeline.modules."
      // ModuleName = "wrapper.".concat(modNames[modNames.length -1]);
      ModuleName = modNames[modNames.length - 1].concat(".wrapper.").concat(modNames[modNames.length - 1]);
      // ModuleName = modNames[modNames.length
      // -1].concat(".net.sourceforge.seqware.pipeline.modules.").concat(modNames[modNames.length
      // -1]);
      Log.info(ModuleName);
    } else {
      getSyntax(parser, "You must specifiy a --module or a --module-pkg parameter");
    }

    try {
      app = (Module) Class.forName(ModuleName).newInstance();
      app.setAlgorithm(ModuleName);

      if (options.has("output")) {
        app.setStdoutFile(new File(options.valueOf("output").toString()));
      }

      if (options.has("stderr")) {
        app.setStderrFile(new File(options.valueOf("stderr").toString()));
      }
    } catch (ClassNotFoundException e) {
      Log.error("Could not find the Module class for '" + ModuleName + "'");
      System.exit(-1);
    } catch (Throwable e) {
      e.printStackTrace();
      Log.error(e);
      System.exit(-1);
    }
    app.setParameters(options.nonOptionArguments());
  }

  private void preProcessMetadata() {
    if (optionHasOneOf(new String[] { "no-metadata", "no-meta-db" })) {
      Log.debug("Metadata writeback disabled. To enable metadata writeback make sure you setup your .seqware/settings file properly (recommened) or provide --metadata-config-database, --metadata-config-username and --metadata-config-password as arguments to this program");
      return;
    }

    String meta_db = null;
    Map<String, String> settings = null;

    // Should try settings only if when user does not specify
    // "metadata-config-database" etc. Although it is legacy
    // code, however, user using those will not know to set up the .seqware
    // file.

    String connection = null, user = null, pass = null;

    if (optionHasOneOf(new String[] { "metadata-config-database", "metadata-config-username",
        "metadata-config-password" })) {
      // legacy mode
      user = options.valueOf("metdata-config-username").toString();
      pass = options.valueOf("metadata-config-password").toString();
      connection = options.valueOf("metadata-config-database").toString();
      meta = new MetadataDB();
    } else {
      try {
        settings = ConfigTools.getSettings();
        if (settings.containsKey("SW_METADATA_METHOD")) {
          String value = settings.get("SW_METADATA_METHOD");
          if (value.equals("database")) {
            user = settings.get(Keys.DB_USER);
            pass = settings.get(Keys.DB_PASS);
            connection = "jdbc:postgresql://" + settings.get(Keys.DB_SERVER) + "/" + settings.get(Keys.DB_NAME);
            meta = new MetadataDB();
          } else if (value.equals("webservice")) {
            user = settings.get(Keys.WS_USER);
            pass = settings.get(Keys.WS_PASS);
            connection = settings.get(Keys.WS_URL);
            meta = new MetadataWS();
          } else {
            Log.error("The metadata method is not specified properly in the .seqware/settings file!");
            return;
          }
        }
      } catch (Exception e1) {
        // TODO Auto-generated catch block
        Log.error("Unable to obtain configuration for Seqware's metadata DB");
        e1.printStackTrace();
        System.exit(-1);
      }
    }

    ReturnValue ret = meta.init(connection, user, pass);
    if (ret.getExitStatus() != ReturnValue.SUCCESS) {
      Log.debug("ERROR connecting to metadata " + settings.get("SW_METADATA_METHOD") + ": " + connection + " " + user
          + " " + pass);
      Log.debug(ret.getStderr());
      System.exit(ret.getExitStatus());
    }

    // If we established a metadb connection, let's find all parentID
    // processingIDs
    // This is deprecated, should use metadata-parent-accession and
    // metadata-parent-accession file in the future
    int workflowRunId = 0;
    int workflowRunAccession = 0;
    ArrayList<Integer> parentIDs = new ArrayList<Integer>();
    processingIDFiles = new ArrayList<File>();
    ArrayList<Integer> parentAccessions = new ArrayList<Integer>();
    processingAccessionFiles = new ArrayList<File>();
    int ancestorWorkflowRunAccession = 0;

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
                meta.update_processing_status(workflowRunAccession, Metadata.FAILED);
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
        String line = null;
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
        String line = null;
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
        String line = null;
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
        Log.error("Could not open parent accession file for metadata-workflow-run-ancestor-accession-input-file: "
            + e.getMessage());
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
      ReturnValue metaret = new ReturnValue(ReturnValue.FAILURE);
      // adding based on sw_accession
      if (parentAccessionsArray.length > 0) {
        metaret = meta.add_empty_processing_event_by_parent_accession(parentAccessionsArray);
        if (metaret.getExitStatus() == ReturnValue.SUCCESS) {
          processingID = metaret.getReturnValue();
          // translate to accession
          processingAccession = meta.mapProcessingIdToAccession(processingID);
          printAndAppendtoStdout("MetaDB ProcessingID for this run is: " + processingID);
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
          printAndAppendtoStdout("MetaDB ProcessingID for this run is: " + processingID);
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
    if (meta != null && processingID != 0) {

      // write out the accessions to file iff success
      // Try to write to each processingIDFile until success or timeout
      for (File file : processingIDFiles) {
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
            meta.update_processing_status(processingID, Metadata.FAILED);
            System.exit(retval.getExitStatus());
          }
        }
      }

      // Try to write to each processingAccessionFile until success or timeout
      for (File file : processingAccessionFiles) {
        int maxTries = (Integer) options.valueOf("metadata-tries-number");
        for (int i = 0; i < maxTries; i++) {
          // Break on success
          if (LockingFileTools.lockAndAppend(file, processingAccession + System.getProperty("line.separator"))) {
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
            meta.update_processing_status(processingID, Metadata.FAILED);
            System.exit(retval.getExitStatus());
          }
        }
      }

      meta.update_processing_status(processingID, Metadata.SUCCESS);
    }
  }

  private void invokeModuleMethods() {
    // figure out if there is any change for redirecting stdout/stderr
    ModuleMethod outStart = null;
    ModuleMethod outEnd = null;
    StdoutRedirect outAnn = app.getClass().getAnnotation(StdoutRedirect.class);
    outStart = outAnn == null ? ModuleMethod.do_run : outAnn.startsBefore();
    outEnd = outAnn == null ? ModuleMethod.do_run : outAnn.endsAfter();

    ModuleMethod errStart = null;
    ModuleMethod errEnd = null;
    StderrRedirect errAnn = app.getClass().getAnnotation(StderrRedirect.class);
    errStart = errAnn == null ? ModuleMethod.do_run : errAnn.startsBefore();
    errEnd = errAnn == null ? ModuleMethod.do_run : errAnn.endsAfter();

    PrintStream old_out = null;
    PrintStream old_err = null;

    for (ModuleMethod m : ModuleMethod.values()) {
      // check stdout redirect
      if ((m == outStart) && (app.getStdoutFile() != null)) {
        old_out = System.out;
        try {
          old_out = System.out;
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
          old_err = System.err;
          System.setErr(new PrintStream(new FileOutputStream(app.getStderrFile())));
        } catch (FileNotFoundException e) {
          printAndAppendtoStderr("Error in redirecting stderr to '" + app.getStderrFile().getAbsolutePath() + "'.");
          printAndAppendtoStderr(e.getMessage());
          System.exit(ReturnValue.INVALIDFILE);
        }
      }
      evaluateReturn(app, m.name());
      if ((m == outEnd) && (old_out != null)) {
        System.setOut(old_out);
      }

      if ((m == errEnd) && (old_err != null)) {
        System.setErr(old_err);
      }
    }
  }

  /**
   * <p>run.</p>
   *
   * @param args an array of {@link java.lang.String} objects.
   */
  public void run(String[] args) {
    // 1. Parse the options
    try {
      options = parser.parse(args);
    } catch (OptionException e) {
      getSyntax(parser, e.getMessage());
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
