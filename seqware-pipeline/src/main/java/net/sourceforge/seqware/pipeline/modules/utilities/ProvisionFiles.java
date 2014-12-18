package net.sourceforge.seqware.pipeline.modules.utilities;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.retry.PredefinedRetryPolicies;
import io.seqware.pipeline.SqwKeys;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.crypto.Cipher;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.model.FileAttribute;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.filetools.ProvisionFilesUtil;
import net.sourceforge.seqware.pipeline.module.Module;
import net.sourceforge.seqware.pipeline.module.ModuleInterface;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * Purpose:
 *
 * This module takes one or more inputs (S3 URL, HTTP/HTTPS URL, or local file path) and copies the file to the specified output (S3 bucket
 * URL, HTTP/HTTPS URL, or local directory path). For S3 this bundle supports large, multipart file upload which is needed for files >2G.
 *
 * FIXME: needs to return errors on failures/exceptions
 *
 * @author boconnor
 * @version $Id: $Id
 */
@ServiceProvider(service = ModuleInterface.class)
public class ProvisionFiles extends Module {

    protected OptionSet options = null;
    protected String algorithmName = "ProvisionFiles";
    private final ProvisionFilesUtil filesUtil = new ProvisionFilesUtil();

    // S3 specific options
    protected int s3ConnectionTimeout = ClientConfiguration.DEFAULT_SOCKET_TIMEOUT;
    protected int s3MaxConnections = ClientConfiguration.DEFAULT_MAX_CONNECTIONS;
    protected int s3MaxErrorRetry = PredefinedRetryPolicies.DEFAULT_MAX_ERROR_RETRY;
    protected int s3SocketTimeout = ClientConfiguration.DEFAULT_SOCKET_TIMEOUT;
    private ArgumentAcceptingOptionSpec<String> annotationFileSpec;

    // FIXME: users have requested the ability to specify a single input file and
    // a single output file so they can copy and rename
    /**
     * <p>
     * getOptionParser.
     * </p>
     *
     * @return a {@link joptsimple.OptionParser} object.
     */
    @Override
    protected OptionParser getOptionParser() {
        OptionParser parser = new OptionParser();
        this.annotationFileSpec = parser
                .accepts("annotation-file",
                        "Specify this option to create annotations on the processing event that is created describing this command. ")
                .withRequiredArg().ofType(String.class).describedAs("Optional: file_path").ofType(String.class);
        parser.acceptsAll(Arrays.asList("input-file", "i"),
                "Required: use this or --input-file-metadata, this is the input file, multiple should be specified seperately")
                .withRequiredArg().describedAs("input file path");
        parser.acceptsAll(Arrays.asList("input-file-metadata", "im"),
                "Required: use this or --input-file, this is the input file, multiple should be specified seperately").withRequiredArg()
                .describedAs("a '::' delimited list of type, meta_type, and file_path.");
        parser.acceptsAll(Arrays.asList("algorithm", "a"),
                "Optional: by default the algorithm is 'ProvisionFiles' but you can override here if you like.").withRequiredArg()
                .describedAs("an algorithm name to save in the DB");
        parser.acceptsAll(Arrays.asList("encrypt-key", "e"),
                "Optional: if specified this key will be used to encrypt data before writing to its destination.").withRequiredArg()
                .describedAs("cryptographic DESede key in Base64 encoded text");
        parser.acceptsAll(
                Arrays.asList("encrypt-key-from-settings", "ekfs"),
                "Optional: if flag is specified then the key will be read from the SW_ENCRYPT_KEY field in your SeqWare settings file and used to encrypt data before writing to its destination.  "
                        + "If this option is specified along with --encrypt-key the key provided by the latter will be used.");
        parser.acceptsAll(Arrays.asList("decrypt-key", "d"),
                "Optional: if specified this key will be used to decrypt data when reading from its source.").withRequiredArg()
                .describedAs("cryptographic DESede key in Base64 encoded text");
        parser.acceptsAll(
                Arrays.asList("decrypt-key-from-settings", "dkfs"),
                "Optional: if flag is specified then the key will be read from the SW_DECRYPT_KEY field in your SeqWare settings file and used to decrypt data as its pulled from the source.  "
                        + "If this option is specified along with --decrypt-key the key provided by the latter will be used.");
        parser.acceptsAll(Arrays.asList("output-dir", "o"), "Required: output file location").withRequiredArg()
                .describedAs("output directory path");
        parser.acceptsAll(
                Arrays.asList("output-file", "of"),
                "Optional: output file path, if this is provided than the program accepts exactly one --input-file and one --output file. If an --output-dir is also specified an error will be thrown.")
                .withRequiredArg().describedAs("output file path");
        parser.acceptsAll(
                Arrays.asList("recursive", "r"),
                "Optional: if the input-file points to a local directory then this option will cause the program to recursively copy the directory and its contents to the destination. An actual copy will be done for local to local copies rather than symlinks.");
        parser.acceptsAll(Arrays.asList("verbose", "v"), "Optional: verbose causes the S3 transfer status to display.");
        parser.accepts(
                "force-copy",
                "Optional: if this is specified local to local file transfers are done with a copy rather than symlink. This is useful if you're writing to a temp area that will be deleted so you have to move the file essentially.");
        parser.accepts("skip-if-missing",
                "Optional: useful for workflows with variable output files, this will silently skip any missing inputs (this is a little dangerous).");
        parser.accepts(
                "s3-connection-timeout",
                "Optional: Sets the amount of time to wait (in milliseconds) when initially establishing a connection before giving up and timing out. Default is "
                        + s3ConnectionTimeout).withRequiredArg();
        parser.accepts("s3-max-connections",
                "Optional: Sets the maximum number of allowed open HTTPS connections. Default is " + s3MaxConnections).withRequiredArg();
        parser.accepts(
                "s3-max-error-retries",
                "Optional: Sets the maximum number of retry attempts for failed retryable requests (ex: 5xx error responses from services). Default is "
                        + s3MaxErrorRetry).withRequiredArg();
        parser.accepts(
                "s3-max-socket-timeout",
                "Optional: Sets the amount of time to wait (in milliseconds) for data to be transfered over an established, open connection before the connection times out and is closed. A value of 0 means infinity, and isn't recommended. Default is "
                        + s3SocketTimeout).withRequiredArg();
        parser.accepts("s3-no-server-side-encryption",
                "Optional: If specified, do not use S3 server-side encryption. Default is to use S3 server-side encryption for S3 destinations.");
        // SEQWARE-1608
        parser.accepts("skip-record-file", "Optional: If specified, do not record new entries in the file table.");

        return (parser);
    }

    /**
     * <p>
     * get_syntax.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String get_syntax() {
        OptionParser parser = getOptionParser();
        StringWriter output = new StringWriter();
        try {
            parser.printHelpOn(output);
            return (output.toString());
        } catch (IOException e) {
            Log.error("error paring input", e);
            return (e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     *
     * Things to check: * FIXME
     *
     * @return
     */
    @Override
    public ReturnValue do_test() {
        return new ReturnValue(ReturnValue.NOTIMPLEMENTED);
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public ReturnValue do_verify_parameters() {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);
        try {
            OptionParser parser = getOptionParser();
            options = parser.parse(this.getParameters().toArray(new String[this.getParameters().size()]));
        } catch (OptionException e) {
            ret.setStderr(e.getMessage() + System.getProperty("line.separator") + this.get_syntax());
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            Log.error("error paring input", e);
            return ret;
        }

        if (!options.has("input-file") && !options.has("input-file-metadata")) {
            ret.setStderr("Must specify one or more --input-file or --input-file-metadata options" + System.getProperty("line.separator")
                    + this.get_syntax());
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            return ret;
        }

        // Must specify input, output and binary file
        /*
         * for (String requiredOption : new String[] { "output-dir" }) { if (!options.has(requiredOption)) {
         * ret.setStderr("Must specify a --" + requiredOption + " or -" + requiredOption.charAt(0) + " option" +
         * System.getProperty("line.separator") + this.get_syntax()); ret.setExitStatus(ReturnValue.INVALIDPARAMETERS); return ret; } }
         */

        if (!options.has("output-file") && !options.has("output-dir")) {
            ret.setStderr("Must specify a single --output-file option or --output-dir option" + System.getProperty("line.separator")
                    + this.get_syntax());
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            return ret;
        }

        // deal with algorithm param
        if (options.has("algorithm") && options.valueOf("algorith") != null && options.valueOf("algorithm").toString().length() > 0) {
            algorithmName = (String) options.valueOf("algorithm");
        }

        // deal with output-file
        if (options.has("output-file") && !(options.has("input-file") || options.has("input-file-metadata"))) {
            ret.setStderr("Must specify a --input-file option (or --input-file-metadata) along with the --output-file option"
                    + System.getProperty("line.separator") + this.get_syntax());
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            return ret;
        }

        // deal with output-file
        if (options.has("output-file") && options.has("output-dir")) {
            ret.setStderr("Must specify a --output-file option or a --output-dir option but not both"
                    + System.getProperty("line.separator") + this.get_syntax());
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            return ret;
        }

        // deal with output-file
        if (options.has("output-file")
                && (options.has("input-file") || options.has("input-file-metadata"))
                && (options.valuesOf("output-file").size() > 1 || options.valuesOf("input-file").size() > 1 || options.valuesOf(
                        "input-file-metadata").size() > 1)) {
            ret.setStderr("Must specify a single --input-file option (or --input-file-metadata) along with a single --output-file option"
                    + System.getProperty("line.separator") + this.get_syntax());
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            return ret;
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public ReturnValue do_verify_input() {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);

        // Verify input file, binary and output file
        List<String> inputs = (List<String>) options.valuesOf("input-file");

        // parses through input files passed in as triplets of metadata and input
        // path
        // add all the metadata-containing inputs
        ArrayList<String> newArray = new ArrayList<>();
        List<String> metaInputs = (List<String>) options.valuesOf("input-file-metadata");
        if (metaInputs != null) {
            if (inputs != null && inputs.size() > 0) {
                newArray.addAll(inputs);
            }
            for (String input : metaInputs) {
                String[] tokens = input.split("::");
                if (tokens.length == 3) {
                    newArray.add(tokens[2]);
                }
            }
            inputs = newArray;
        }

        if (inputs.isEmpty()) {
            return new ReturnValue(null, "No valid input files provided", ReturnValue.INVALIDARGUMENT);
        }

        for (String input : inputs) {
            if (!input.startsWith("s3://") && !input.startsWith("http://") && !input.startsWith("https://") && !input.startsWith("hdfs://")
                    && !options.has("skip-if-missing")
                    && FileTools.fileExistsAndReadable(new File(input)).getExitStatus() != ReturnValue.SUCCESS
                    && FileTools.dirPathExistsAndReadable(new File(input)).getExitStatus() != ReturnValue.SUCCESS) {
                return new ReturnValue(null, "Cannot find input file: " + input, ReturnValue.FILENOTREADABLE);
            }
            if (new File(input).isDirectory() && !options.has("recursive")) {
                return new ReturnValue(null, "Cannot pass directories as input without specifying --recursive: " + input,
                        ReturnValue.INVALIDARGUMENT);
            }
        }

        if (options.has("output-dir") && !((String) options.valueOf("output-dir")).startsWith("s3://")
                && !((String) options.valueOf("output-dir")).startsWith("http://")
                && !((String) options.valueOf("output-dir")).startsWith("https://")) {
            File output = new File((String) options.valueOf("output-dir"));
            // try to create if it doesn't exist
            if (!output.exists()) {
                output.mkdirs();
            }
            if (FileTools.dirPathExistsAndWritable(output).getExitStatus() != ReturnValue.SUCCESS) {
                ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
                ret.setStderr("Can't write to output directory " + options.valueOf("output-dir"));
                return ret;
            }
        }

        if (options.has("output-file") && !((String) options.valueOf("output-file")).startsWith("s3://")
                && !((String) options.valueOf("output-file")).startsWith("http://")
                && !((String) options.valueOf("output-file")).startsWith("https://")) {

            String directory = FileTools.getFilePath((String) options.valueOf("output-file"));
            File output = new File(directory);
            // try to create if it doesn't exist
            if (!output.exists()) {
                output.mkdirs();
            }
            if (FileTools.dirPathExistsAndWritable(output).getExitStatus() != ReturnValue.SUCCESS) {
                ret.setExitStatus(ReturnValue.DIRECTORYNOTWRITABLE);
                ret.setStderr("Can't write to output directory for file " + options.valueOf("output-file"));
                return ret;
            }
        }

        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public ReturnValue do_run() {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);
        boolean skipIfMissing = options.has("skip-if-missing");
        boolean verbose = options.has("verbose");
        filesUtil.setVerbose(verbose);
        ret.setAlgorithm(algorithmName);

        ArrayList<FileMetadata> fileArray = ret.getFiles();
        // this sucks, but I cannot tell with all this fiddling which input file->FileMetadata we're dealing with
        Map<String, FileMetadata> input2metadataMap = new HashMap<>();

        List<String> inputs = (List<String>) options.valuesOf("input-file");

        if (options.has("input-file-metadata")) {
            List<String> metaInputs = (List<String>) options.valuesOf("input-file-metadata");
            List<String> outputFiles = (List<String>) options.valuesOf("output-file");
            ArrayList<String> newArray = new ArrayList<>();
            if (inputs != null && inputs.size() > 0) {
                newArray.addAll(inputs);
            }
            for (String input : metaInputs) {
                String[] tokens = input.split("::");
                if (tokens.length == 3) {
                    newArray.add(tokens[2]);
                    FileMetadata fmd = new FileMetadata();
                    fmd.setDescription(tokens[0]);
                    fmd.setMetaType(tokens[1]);
                    if (outputFiles != null && outputFiles.size() > 0) {
                        fmd.setFilePath(outputFiles.get(0));
                    } else {
                        fmd.setFilePath(tokens[2]);
                    }
                    fmd.setType(tokens[0]);
                    fileArray.add(fmd);
                    input2metadataMap.put(tokens[2], fmd);
                }
            }
            inputs = newArray;
        }

        // debug info, loop over inputs
        for (FileMetadata fmd : fileArray) {
            Log.info("FMD:\nDescription: " + fmd.getDescription() + "\nFile Path: " + fmd.getFilePath() + "\nMeta Type: "
                    + fmd.getMetaType() + "\nType: " + fmd.getType());
            // handle text/key-value
            if (fmd.getMetaType().equals("text/key-value") && this.getProcessingAccession() != 0) {
                handleAnnotations(fmd.getFilePath(), this.getProcessingAccession(), this.getMetadata());
            }
        }
        if (options.has(annotationFileSpec) && this.getProcessingAccession() != 0) {

            Map<String, String> map = FileTools.getKeyValueFromFile(options.valueOf(annotationFileSpec));
            Set<FileAttribute> atts = new TreeSet<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                FileAttribute a = new FileAttribute();
                a.setTag(entry.getKey());
                a.setValue(entry.getValue());
                atts.add(a);
            }
            // assumption is that annotation file applies to all files (normally just one with seqware-oozie)
            for (FileMetadata fmd : fileArray) {
                fmd.getAnnotations().addAll(atts);
            }
        }

        for (String input : inputs) {
            FileMetadata filemetadata = input2metadataMap.get(input);
            if (filemetadata != null) {
                Log.stdout("Found metadata for input: " + input);
            }
            Log.stdout("Processing input: " + input);
            if (options.has("output-dir")) {
                Log.stdout("      output-dir: " + options.valueOf("output-dir"));
            }
            if (options.has("output-file")) {
                Log.stdout("     output-file: " + options.valueOf("output-file"));
            }
            if (!input.startsWith("http") && !input.startsWith("s3") && new File(input).isDirectory()) {
                if (options.has("recursive")) {
                    ReturnValue currRet = recursivelyCopyDir(new File(input).getAbsolutePath(), new File(input).list(),
                            (String) options.valueOf("output-dir"), fileArray);
                    if (currRet.getExitStatus() != ReturnValue.SUCCESS) {
                        return (currRet);
                    }
                }
            } else if ((options.valuesOf("input-file").size() == 1 && options.valuesOf("input-file-metadata").isEmpty())
                    && options.has("output-file") && options.valuesOf("output-file").size() == 1) { // then this is a single file to single
                                                                                                    // file copy
                if (!provisionFile(input, (String) options.valueOf("output-file"), skipIfMissing, fileArray, true, filemetadata)) {
                    Log.error("Failed to copy file");
                    ret.setExitStatus(ReturnValue.FAILURE);
                    return ret;
                }
            } else if ((options.valuesOf("input-file").isEmpty() && options.valuesOf("input-file-metadata").size() == 1)
                    && options.has("input-file-metadata") && options.valuesOf("output-file").size() == 1) { // then this is a single file to
                                                                                                            // single file copy
                if (!provisionFile(input, (String) options.valueOf("output-file"), skipIfMissing, fileArray, true, filemetadata)) {
                    Log.error("Failed to copy file");
                    ret.setExitStatus(ReturnValue.FAILURE);
                    return ret;
                }
            } else if (options.has("output-dir")) { // then this is just a normal file copy
                if (!provisionFile(input, (String) options.valueOf("output-dir"), skipIfMissing, fileArray, false, filemetadata)) {
                    Log.error("Failed to copy file to dir");
                    ret.setExitStatus(ReturnValue.FAILURE);
                    return ret;
                }
            }
        }

        if (options.has("skip-record-file")) {
            fileArray.clear();
        }

        return ret;

    }

    private static void handleAnnotations(String file, int processingAccession, Metadata metadata) {
        Map<String, String> map = FileTools.getKeyValueFromFile(file);
        Set<ProcessingAttribute> atts = new TreeSet<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            ProcessingAttribute a = new ProcessingAttribute();
            a.setTag(entry.getKey());
            a.setValue(entry.getValue());
            atts.add(a);
        }
        metadata.annotateProcessing(processingAccession, atts);
    }

    private ReturnValue recursivelyCopyDir(String baseDir, String[] files, String outputDir, ArrayList<FileMetadata> fileArray) {
        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);
        boolean skipIfMissing = options.has("skip-if-missing");

        // Log.error("BASEDIR: "+baseDir);
        for (String file : files) {
            // Log.error("  FILE: "+file);

            // clean
            if (baseDir.endsWith("/")) {
                baseDir = baseDir.substring(0, baseDir.length() - 1);
            }
            if (outputDir.endsWith("/")) {
                outputDir = outputDir.substring(0, outputDir.length() - 1);
            }
            File currFile = new File(baseDir + "/" + file);
            String additionalPath = currFile.getAbsolutePath().replace(baseDir + "/", "");

            if (currFile.isDirectory()) {
                // Log.error("    CURR FILE IS DIR: "+currFile.getAbsolutePath()+"\n");
                ReturnValue currRet = recursivelyCopyDir(currFile.getAbsolutePath(), currFile.list(), outputDir + "/" + additionalPath,
                        fileArray);
                if (currRet.getExitStatus() != ReturnValue.SUCCESS) {
                    return (currRet);
                }
            } else {
                // Log.error("    CURR FILE IS FILE: "+currFile.getAbsolutePath()+"\n");
                Log.info("\n  COPYING FILE: " + currFile.getAbsolutePath() + "\n    to " + outputDir + "/" + additionalPath);
                // when this goes recursive, there is no single metadata that clearly corresponds so set that to null
                if (!provisionFile(currFile.getAbsolutePath(),
                        outputDir + "/" + additionalPath.substring(0, additionalPath.length() - file.length()), skipIfMissing, fileArray,
                        false, null)) {
                    ret.setExitStatus(ReturnValue.FAILURE);
                    return ret;
                }
            }
        }
        return ret;
    }

    /**
     * <p>
     * provisionFile.
     * </p>
     *
     * @param input
     *            original path to the file, a file path if local, otherwise a http path or s3 path
     * @param output
     *            path to the destination, a file path if local, otherwise a http path or s3 path
     * @param skipIfMissing
     *            skip missing files with only a warning
     * @param fileArray
     *            metadata about the file (files) to be copied
     * @param fullOutputPath
     *            the full output path overrides any concatenation of variables
     * @param metadata
     *            the specific filemetadata for this file, if there is clearly one
     * @return a boolean.
     */
    protected boolean provisionFile(String input, String output, boolean skipIfMissing, ArrayList<FileMetadata> fileArray,
            boolean fullOutputPath, FileMetadata metadata) {

        BufferedInputStream reader;
        int bufLen = 5000 * 1024; // 5M buffer

        // what is this madness?

        // finally record the metadata about the file if it was passed in using
        // --input-file-metadata, we ignore files passed in via --input-file since
        // they have no metadata associated with them
        // I believe this is done so the file path in the ReturnValue object is
        // ready for saving to the DB prefixed by output prefix
        for (FileMetadata fmd : fileArray) {
            Log.info("Examining: " + fmd.getFilePath() + " fileUti's file: " + filesUtil.getFileName() + " fileutil's original file: "
                    + filesUtil.getOriginalFileName());
            if (fmd.getFilePath() != null && fmd.getFilePath().equals(filesUtil.getOriginalFileName())) {
                fmd.setFilePath(filesUtil.getFileName());
            }
        }

        // now try to set these up
        reader = filesUtil.getSourceReader(input, bufLen, 0L);
        // just skip this if this option is set
        if (reader == null && skipIfMissing) {
            Log.warn("File does not exist: " + input + ". Skipping...");
            return true;
        } else if (reader == null) {
            Log.error("File does not exist: " + input);
            Log.error("To proceed, run ProvisionFile again with --skip-if-missing");
            return false;
        }

        Map<String, String> settings = ConfigTools.getSettings();
        if (settings.containsKey(SqwKeys.SW_PROVISION_FILES_MD5.getSettingKey()) && metadata != null) {
            String value = settings.get(SqwKeys.SW_PROVISION_FILES_MD5.getSettingKey());
            boolean usemd5 = Boolean.valueOf(value);
            if (usemd5) {
                ProvisionFilesUtil.calculateInputMetadata(input, metadata);
            }
        } else {
            ProvisionFilesUtil.calculateInputMetadata(input, metadata);
        }

        return putDestination(reader, output, bufLen, input, fileArray, fullOutputPath, metadata);

    }

    /**
     * HTTP writeback currently not supported S3 uses multi-part upload which should deal with failed uploads (maybe) I'm not really dealing
     * with failed upload recovery here... Keep in mind only the writeout to local file will attempt to recover from failed reader
     *
     * @param output
     *            where the file should be copied to
     * @param reader
     *            a reader open to the file that needs to be copied
     * @param bufLen
     *            passed right through to filesUtil
     * @param input
     *            where the file is copied from
     * @param fileArray
     *            a {@link java.util.ArrayList} object.
     * @param fullOutputPath
     * @param metadata
     *            metadata entry for the specific file
     * @return a boolean.
     */
    protected boolean putDestination(BufferedInputStream reader, String output, int bufLen, String input,
            ArrayList<FileMetadata> fileArray, boolean fullOutputPath, FileMetadata metadata) {

        // what is this madness?
        // finally record the metadata about the file, update the output path to
        // prepare this file for adding output-prefix as a prefix
        for (FileMetadata fmd : fileArray) {
            Log.info("Examining: " + fmd.getFilePath() + " fileUti's file: " + output + " " + filesUtil.getFileName()
                    + " fileutil's original file: " + filesUtil.getOriginalFileName());
            if (fmd.getFilePath() != null && fmd.getFilePath().equals(filesUtil.getOriginalFileName())) {
                fmd.setFilePath(filesUtil.getFileName());
                Log.info("    SETTING FINAL PATH: " + filesUtil.getFileName());
            }
        }

        // encryption
        Cipher decryptCipher = getDecryptCipher();
        Cipher encryptCipher = getEncryptCipher();

        // result
        boolean result;

        // now create output stream
        if (output.startsWith("s3://")) {

            // put to S3
            result = filesUtil.putToS3(reader, output, fullOutputPath, decryptCipher, encryptCipher);

        } else if (output.startsWith("http://") || output.startsWith("https://")) {

            // It's not supported yet
            result = filesUtil.putToHttp();

        }/**
         * else if (output.startsWith("hdfs://")) {
         *
         * // put to S3 result = filesUtil.putToHDFS(reader, output, fullOutputPath, decryptCipher, encryptCipher);
         *
         * }
         */
        else {

            // local copy
            if (input.startsWith("http://") || input.startsWith("https://") || input.startsWith("s3://") || options.has("force-copy")
                    || options.has("recursive")) {

                result = filesUtil.copyToFile(reader, output, fullOutputPath, bufLen, input, decryptCipher, encryptCipher, metadata) != null;

            } else {
                // If no "force-copy" and "recursive"
                // just make a sym link rather than copy
                // if the following option is set and it's local source and destination
                // do nothing, useful if using Pegasus for local files and this for
                // S3/HTTP
                File localInputFile = new File(input);
                result = filesUtil.createSymlink(output, fullOutputPath, localInputFile.getAbsolutePath());
            }

        }

        try {
            reader.close();
        } catch (IOException e) {
            Log.error(e.getMessage());
            return false;
        }

        return result;
    }

    /**
     * <p>
     * getDecryptCipher.
     * </p>
     *
     * @return a {@link javax.crypto.Cipher} object.
     */
    protected Cipher getDecryptCipher() {
        if (options.has("decrypt-key")) {
            return (filesUtil.getDecryptCipher((String) options.valueOf("decrypt-key")));
        } else if (options.has("decrypt-key-from-settings")) {
            try {
                HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
                return (filesUtil.getDecryptCipher(settings.get(SqwKeys.SW_DECRYPT_KEY.getSettingKey())));
            } catch (Exception e) {
                Log.error(e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * <p>
     * getEncryptCipher.
     * </p>
     *
     * @return a {@link javax.crypto.Cipher} object.
     */
    protected Cipher getEncryptCipher() {
        if (options.has("encrypt-key")) {
            return (filesUtil.getEncryptCipher((String) options.valueOf("encrypt-key")));
        } else if (options.has("encrypt-key-from-settings")) {
            try {
                HashMap<String, String> settings = (HashMap<String, String>) ConfigTools.getSettings();
                return (filesUtil.getEncryptCipher(settings.get(SqwKeys.SW_ENCRYPT_KEY.getSettingKey())));
            } catch (Exception e) {
                Log.error(e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public ReturnValue do_verify_output() {
        // TODO: should verify output, especially is they are local files!
        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);
        return ret;
    }
}
