package net.sourceforge.seqware.pipeline.bundle;

import io.seqware.pipeline.SqwKeys;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.filetools.ProvisionFilesUtil;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.BufferedInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * This is a utility class that lets you manipulate a workflow bundle.
 * 
 * @author briandoconnor@gmail.com
 * @version $Id: $Id
 */
public class Bundle {

    protected String permanentBundleLocation = null;
    protected String bundleDir = null;
    protected Metadata metadata = null;
    // this is used as the location of the workflow bundle location
    protected String outputDir = null;
    // this is used as the location of the workflow bundle zip
    protected String outputZip = null;
    protected ArrayList<File> filesArray = new ArrayList<>();

    /**
     * <p>
     * Constructor for Bundle.
     * </p>
     */
    public Bundle() {
        super();
    }

    /**
     * <p>
     * Constructor for Bundle.
     * </p>
     * 
     * @param metadata
     *            a {@link net.sourceforge.seqware.common.metadata.Metadata} object.
     * @param config
     *            a {@link java.util.Map} object.
     */
    public Bundle(Metadata metadata, Map<String, String> config) {
        super();
        this.metadata = metadata;
        permanentBundleLocation = config.get(SqwKeys.SW_BUNDLE_REPO_DIR.getSettingKey());
        bundleDir = config.get(SqwKeys.SW_BUNDLE_DIR.getSettingKey());
    }

    public static BundleInfo findBundleInfo(File bundleDir) {
        bundleDir = bundleDir.getAbsoluteFile();
        Collection<File> files = FileUtils.listFiles(bundleDir, new NameFileFilter("metadata.xml"), TrueFileFilter.TRUE);
        if (files.isEmpty()) {
            throw new RuntimeException("Could not find metadata.xml.");
        } else {
            BundleInfo bi = new BundleInfo();
            bi.parseFromFile(files.iterator().next());
            return bi;
        }
    }

    public static String resolveWorkflowBundleDirPath(File bundleDir, String path) {
        if (path.contains("${workflow_bundle_dir}")) {
            path = path.replaceAll("\\$\\{workflow_bundle_dir\\}", bundleDir.getAbsolutePath());
        }
        return path;
    }

    /**
     * <p>
     * getBundleInfo.
     * </p>
     * 
     * @param bundle
     *            a {@link java.io.File} object.
     * @param metadataFile
     *            a {@link java.io.File} object.
     * @return a {@link net.sourceforge.seqware.pipeline.bundle.BundleInfo} object.
     */
    public BundleInfo getBundleInfo(File bundle, File metadataFile) {

        if (bundle == null || !bundle.exists()) {
            Log.error("ERROR: Bundle is null or doesn't exist! The bundle must be either a zip file or a directory structure.");
            return null;
        }

        BundleInfo bi = new BundleInfo();
        ReturnValue returned;
        // unbundle
        // FIXME: no need to unzip the bundle, replace with code to read metadata files directly from zip
        if (bundle.isDirectory()) {
            outputDir = bundle.getAbsolutePath();
            FileTools.listFilesRecursive(bundle, filesArray);
        } else {
            returned = unpackageBundle(bundle);
            outputDir = returned.getAttribute("outputDir");
        }

        // now read metadata info
        // find the metadata file
        if (metadataFile == null || !metadataFile.exists()) {
            File currMetadata = null;
            for (File file : filesArray) {
                if ("metadata.xml".equals(file.getName())
                        && (currMetadata == null || currMetadata.getAbsolutePath().length() > file.getAbsolutePath().length())) {
                    currMetadata = file;
                }
            }
            if (currMetadata != null && currMetadata.exists()) {
                bi.parseFromFile(currMetadata);
            }
        } else {
            bi.parseFromFile(metadataFile);
        }

        return (bi);
    }

    /**
     * <p>
     * unpackageBundle.
     * </p>
     * 
     * @param bundle
     *            a {@link java.io.File} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public ReturnValue unpackageBundle(File bundle) {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);

        // check the bundleDir
        if (bundleDir == null || "".equals(bundleDir)) {
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            ret.setStderr("ERROR: the " + SqwKeys.SW_BUNDLE_DIR.getSettingKey()
                    + " variable in your SEQWARE_SETTINGS (default .seqware/settings) file appears to be undefined!");
        }

        // check the bundleDir
        if (bundle == null || !bundle.isFile()) {
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            ret.setStderr("ERROR: the bundle you passed is either null or is not a file. It must be a zip file!");
            return ret;
        }

        // check the bundle dir
        File bundleDirFile = new File(bundleDir);
        Path toPath = bundleDirFile.toPath();
        if (!Files.isReadable(toPath)) {
            throw new RuntimeException("ERROR: The provisioned bundle directory you provided is not readable");
        }
        if (!Files.isWritable(toPath)) {
            throw new RuntimeException("\"ERROR: The provisioned bundle directory you provided is not writeable\"");
        }

        String bundleName = bundle.getName();
        bundleName = bundleName.replaceAll(".zip", "");
        File localOutputDir = new File(bundleDir + File.separator + bundleName);
        if (localOutputDir.exists()) {
            Log.stdout("Expanded bundle directory already exists, skipping unzip.");
        } else {
            FileTools.unzipFile(bundle, localOutputDir);
        }
        ret.setAttribute("outputDir", localOutputDir.getAbsolutePath());
        setOutputDir(localOutputDir.getAbsolutePath());
        FileTools.listFilesRecursive(localOutputDir, filesArray);

        return ret;
    }

    /**
     * <p>
     * unpackageBundleFromS3.
     * </p>
     * 
     * @param bundleURL
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public ReturnValue unpackageBundleFromS3(String bundleURL) {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);

        // check the bundleDir
        if (bundleDir == null || "".equals(bundleDir)) {
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            ret.setStderr("ERROR: the " + SqwKeys.SW_BUNDLE_DIR.getSettingKey()
                    + " variable in your SEQWARE_SETTINGS (default .seqware/settings) file appears to be undefined!");
        }

        // check the bundleDir
        if (bundleURL == null) {
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            ret.setStderr("ERROR: the bundle you passed is either null or is not a file. It must be a zip file!");
            return ret;
        }

        // attempt to find output location, use the perm bundle location
        // unless it points to S3, in this case just make a temp dir
        // in the provisioned bundle dir to download the zip into
        String zipDownloadDir = permanentBundleLocation;
        if (permanentBundleLocation.startsWith("s3://")) {
            // then can't use it as a temp dir
            File tempDir;
            try {
                tempDir = FileTools.createDirectoryWithUniqueName(new File(this.bundleDir), "wokflow_zip_temp");
            } catch (Exception e) {
                Log.error("Problem creating a temp directory to use for zipping workflow " + e.getMessage());
                ret.setExitStatus(ReturnValue.FAILURE);
                return ret;
            }
            zipDownloadDir = tempDir.getAbsolutePath();
        }

        // download from S3
        ProvisionFiles pf = new ProvisionFiles();
        pf.setParameters(Arrays.asList("--input-file", bundleURL, "--output-dir", zipDownloadDir, "--force-copy"));
        pf.do_verify_parameters();
        ret = pf.do_run();
        if (ret.getExitStatus() != ReturnValue.SUCCESS) {
            return ret;
        }

        // name
        String[] path = bundleURL.split(File.separator);
        String bundleName = path[path.length - 1];
        bundleName = bundleName.replaceAll(".zip", "");

        FileTools.unzipFile(new File(zipDownloadDir + File.separator + bundleName + ".zip"), new File(bundleDir + File.separator
                + bundleName));
        ret.setAttribute("outputDir", bundleDir + File.separator + bundleName);
        setOutputDir(bundleDir + File.separator + bundleName);
        FileTools.listFilesRecursive(new File(bundleDir + File.separator + bundleName), filesArray);

        return ret;
    }

    /**
     * <p>
     * packageBundle.
     * </p>
     * 
     * @param bundlePath
     *            a {@link java.io.File} object.
     * @param bundleOutput
     *            a {@link java.io.File} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public ReturnValue packageBundle(File bundlePath, File bundleOutput) {

        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);

        if (bundlePath == null || !bundlePath.isDirectory()) {
            ret.setExitStatus(ReturnValue.INVALIDFILE);
            ret.setStderr("ERROR: the bundle path you're trying to zip up is either null or not a directory!");
            return ret;
        }

        File outputZipFile = new File(bundleOutput.getAbsolutePath() + File.separator + bundlePath.getName() + ".zip");
        if (outputZipFile.exists()) {
            Log.stdout("Overwriting " + outputZipFile.getAbsolutePath());
        }

        boolean compression = true;
        Map<String, String> settings = ConfigTools.getSettings();
        if (settings.containsKey(SqwKeys.BUNDLE_COMPRESSION.getSettingKey())
                && settings.get(SqwKeys.BUNDLE_COMPRESSION.getSettingKey()).equals("OFF")) {
            compression = false;
        }
        if (!FileTools.zipDirectoryRecursive(bundlePath, outputZipFile, compression)) {
            // tests
            ret.setExitStatus(ReturnValue.FAILURE);
        }

        // save the location of the zip file
        // FIXME: correct?
        this.outputZip = outputZipFile.getAbsolutePath();

        return ret;
    }

    /**
     * <p>
     * packageBundleToS3.
     * </p>
     * 
     * @param bundlePath
     *            a {@link java.io.File} object.
     * @param bundleOutputPrefix
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public ReturnValue packageBundleToS3(File bundlePath, String bundleOutputPrefix) {
        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);

        if (bundlePath == null || !bundlePath.isDirectory()) {
            ret.setExitStatus(ReturnValue.INVALIDFILE);
            ret.setStderr("ERROR: the bundle path you're trying to zip up is either null or not a directory!");
            return ret;
        }

        File tempDir = null;
        try {
            tempDir = FileTools.createDirectoryWithUniqueName(new File(this.bundleDir), "wokflow_zip_temp");
        } catch (Exception e) {
            Log.error("Problem creating a temp directory to use for zipping workflow " + e.getMessage());
            ret.setExitStatus(ReturnValue.FAILURE);
            return ret;
        }

        boolean compression = true;
        Map<String, String> settings = ConfigTools.getSettings();
        if (settings.containsKey(SqwKeys.BUNDLE_COMPRESSION.getSettingKey())
                && settings.get(SqwKeys.BUNDLE_COMPRESSION.getSettingKey()).equals("OFF")) {
            compression = false;
        }
        if (!FileTools.zipDirectoryRecursive(bundlePath, new File(tempDir.getAbsolutePath() + File.separator + bundlePath.getName()
                + ".zip"), compression)) {
            ret.setExitStatus(ReturnValue.FAILURE);
        }

        String zipFile = tempDir.getAbsolutePath() + File.separator + bundlePath.getName() + ".zip";
        ProvisionFilesUtil fileUtil = new ProvisionFilesUtil();
        int bufLen = 5000 * 1024;
        Log.stdout("Copying local file " + zipFile + " to output " + bundleOutputPrefix + " this may take a long time!");
        BufferedInputStream reader = fileUtil.getSourceReader(zipFile, bufLen, 0L);
        boolean result = fileUtil.putToS3(reader, bundleOutputPrefix, false);

        if (!result) {
            Log.error("Failed to copy file to S3!");
            ret.setExitStatus(ReturnValue.FAILURE);
            return ret;
        }

        // save the location of the zip file
        // FIXME: correct?
        this.outputZip = bundleOutputPrefix + File.separator + bundlePath.getName() + ".zip";
        if (bundleOutputPrefix.endsWith(File.separator)) {
            this.outputZip = bundleOutputPrefix + bundlePath.getName() + ".zip";
        }

        // now delete the local zip file
        Log.stdout("Finished copying file to S3!");
        Log.stdout("You should delete (or archive locally) the local zip file: " + zipFile);

        return ret;
    }

    /**
     * <p>
     * copyBundleToS3.
     * </p>
     * 
     * @param bundle
     *            a {@link java.io.File} object.
     * @param bundleOutputPrefix
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public ReturnValue copyBundleToS3(File bundle, String bundleOutputPrefix) {
        ReturnValue ret = new ReturnValue();
        ret.setExitStatus(ReturnValue.SUCCESS);
        if (bundle == null || !bundle.isFile() || !bundle.getName().endsWith(".zip")) {
            ret.setExitStatus(ReturnValue.INVALIDFILE);
            ret.setStderr("ERROR: the bundle zip you're trying to copy up is either null or not a zip file!");
            return ret;
        }

        ProvisionFilesUtil fileUtil = new ProvisionFilesUtil();
        int bufLen = 5000 * 1024;
        Log.stdout("Copying local file " + bundle.getAbsolutePath() + " to output " + bundleOutputPrefix + " this may take a long time!");
        BufferedInputStream reader = fileUtil.getSourceReader(bundle.getAbsolutePath(), bufLen, 0L);
        boolean result = fileUtil.putToS3(reader, bundleOutputPrefix, false);

        if (!result) {
            Log.error("Failed to copy file to S3!");
            ret.setExitStatus(ReturnValue.FAILURE);
            return ret;
        }

        // save the location of the zip file
        // FIXME: correct?
        this.outputZip = bundleOutputPrefix + File.separator + bundle.getName();

        // now delete the local zip file
        Log.stdout("Finished copying file to S3!");
        Log.stdout("You may want to delete (or archive locally) the local zip file: " + bundle.getAbsolutePath());

        return ret;
    }

    /**
     * <p>
     * copyBundle.
     * </p>
     * 
     * @param sourceFile
     *            a {@link java.lang.String} object.
     * @param targetDir
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public ReturnValue copyBundle(String sourceFile, String targetDir) {
        ReturnValue result = new ReturnValue(ReturnValue.SUCCESS);
        File source = new File(sourceFile);
        String sourceName = source.getName();
        this.outputZip = targetDir + File.separator + sourceName;
        if (new File(outputZip).exists()) {
            Log.stdout("Bundle archive already in target directory, skipping copy.");
        } else {
            ProvisionFiles pf = new ProvisionFiles();
            pf.setParameters(Arrays.asList("--input-file", sourceFile, "--output-dir", targetDir, "--force-copy"));
            pf.do_verify_parameters();
            result = pf.do_run();
        }
        return result;
    }

    /**
     * <p>
     * validateBundle.
     * </p>
     * 
     * @param bundle
     *            a {@link java.io.File} object.
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public ReturnValue validateBundle(File bundle) {

        ReturnValue localRet = new ReturnValue(ReturnValue.SUCCESS);

        if (bundle == null || !bundle.exists()) {
            localRet.setExitStatus(ReturnValue.INVALIDFILE);
            localRet.setStderr("ERROR: the bundle path you're trying to zip up is either null or not a directory!");
            return (localRet);
        }

        if (bundle.isDirectory()) {
            outputDir = bundle.getAbsolutePath();
            FileTools.listFilesRecursive(bundle, filesArray);
        } else {
            localRet = unpackageBundle(bundle);
            outputDir = localRet.getAttribute("outputDir");
        }

        try {
            BundleInfo bi = new BundleInfo();
            for (File file : filesArray) {
                if ("metadata.xml".equals(file.getName())) {
                    // try to parse this, will throw exception if incorrect
                    bi = new BundleInfo();
                    bi.parseFromFile(file);
                }
                // TODO: add more validation here based on what's pulled out of the metadata bundle

                for (WorkflowInfo wi : bi.getWorkflowInfo()) {
                    // ensure conf file exists
                    String orig = wi.getConfigPath();
                    String abs = orig.replaceAll("\\$\\{workflow_bundle_dir\\}", outputDir);
                    File f = new File(abs);
                    if (!f.exists()) {
                        localRet.setExitStatus(ReturnValue.FAILURE);
                        localRet.setStderr("ERROR: Configuration file does not exist: " + orig);
                    }
                }

            }
        } catch (Exception e) {
            localRet.setExitStatus(ReturnValue.FAILURE);
            localRet.setStderr("ERROR: problems validating " + bundle.getAbsolutePath());
        }

        if (localRet.getExitStatus() == ReturnValue.SUCCESS) {
            Log.info("Validated Bundle: " + bundle.getAbsolutePath());
        }
        return (localRet);
    }

    /**
     * <p>
     * installBundleZipOnly.
     * </p>
     * 
     * @param bundle
     *            a {@link java.io.File} object.
     * @param metadataFile
     *            a {@link java.io.File} object.
     * @param workflows
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public ReturnValue installBundleZipOnly(File bundle, File metadataFile, List<String> workflows) {
        return installBundle(bundle, metadataFile, true, false, workflows);
    }

    /**
     * <p>
     * installBundleDirOnly.
     * </p>
     * 
     * @param bundle
     *            a {@link java.io.File} object.
     * @param metadataFile
     *            a {@link java.io.File} object.
     * @param workflows
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public ReturnValue installBundleDirOnly(File bundle, File metadataFile, List<String> workflows) {
        return installBundle(bundle, metadataFile, false, true, workflows);
    }

    /**
     * <p>
     * installBundle.
     * </p>
     * 
     * @param bundle
     *            a {@link java.io.File} object.
     * @param metadataFile
     *            a {@link java.io.File} object.
     * @param workflows
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    public ReturnValue installBundle(File bundle, File metadataFile, List<String> workflows) {
        // seqware-1933 - throw error when the provisioned or archive directories are not present
        if (this.bundleDir == null) {
            Log.stdout("Could not install bundle, please check that your " + SqwKeys.SW_BUNDLE_DIR.getSettingKey() + " is defined");
            return new ReturnValue(ReturnValue.SETTINGSFILENOTFOUND);
        }
        if (this.permanentBundleLocation == null) {
            Log.stdout("Could not install bundle, please check that your " + SqwKeys.SW_BUNDLE_REPO_DIR.getSettingKey() + " is defined");
            return new ReturnValue(ReturnValue.SETTINGSFILENOTFOUND);
        }
        return installBundle(bundle, metadataFile, true, true, workflows);
    }

    /**
     * This method allows you to install the workflow bundle to the metadb optionally building a zip file along the way and archiving it to
     * a safe location.
     * 
     * @param bundle
     *            a {@link java.io.File} object.
     * @param metadataFile
     *            a {@link java.io.File} object.
     * @param packageIntoZip
     *            a boolean.
     * @param unzipIntoDir
     *            a boolean.
     * @param workflows
     *            a list to store installed workflows for output
     * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
     */
    protected ReturnValue installBundle(File bundle, File metadataFile, boolean packageIntoZip, boolean unzipIntoDir, List<String> workflows) {

        ReturnValue localRet = new ReturnValue(ReturnValue.SUCCESS);

        // installing from an unzipped workflow bundle directory
        if (bundle != null && bundle.isDirectory() && packageIntoZip) {

            if (permanentBundleLocation == null) {
                Log.error("You tried to install a bundle and create a .zip file of the bundle without having a "
                        + SqwKeys.SW_BUNDLE_REPO_DIR.getSettingKey()
                        + " defined in your seqware settings file! This needs to be defined and pointed to a location where a .zip file can be written.");
                return (new ReturnValue(ReturnValue.FAILURE));
            } else if (permanentBundleLocation.startsWith("s3://")) {
                Log.stdout("Now packaging " + bundle.getAbsolutePath() + " to a zip file and transferring to the S3 location: "
                        + permanentBundleLocation + " Please be aware, this process can take hours if the bundle is many GB in size.");
                localRet = packageBundleToS3(bundle, permanentBundleLocation);
            } else {
                // then it's a directory
                // now package this up
                Log.stdout("Now packaging " + bundle.getAbsolutePath() + " to a zip file and transferring to the directory: "
                        + permanentBundleLocation + " Please be aware, this process can take hours if the bundle is many GB in size.");
                localRet = packageBundle(bundle, new File(permanentBundleLocation));
            }
        } // installing from a zip file (will be unzipped below by getBundleInfo) copy to permanent location
        else if (bundle != null && bundle.isFile() && bundle.getName().endsWith(".zip")) {
            // FIXME: the getBundleInfo will unzip this below, should only do that if the request is for unzip
            if (permanentBundleLocation == null) {
                Log.error("You tried to install a bundle from a .zip file without having a "
                        + SqwKeys.SW_BUNDLE_REPO_DIR.getSettingKey()
                        + " defined in your seqware settings file! This needs to be defined and pointed to a location where a .zip file can be copied to.");
                return (new ReturnValue(ReturnValue.FAILURE));
            } else if (permanentBundleLocation.startsWith("s3://")) {
                Log.stdout("Now packaging " + bundle.getAbsolutePath() + " to a zip file and transferring to the S3 location: "
                        + permanentBundleLocation + " Please be aware, this process can take hours if the bundle is many GB in size.");
                localRet = copyBundleToS3(bundle, permanentBundleLocation);
            } else {
                Log.stdout("Now transferring " + bundle.getAbsolutePath() + " to the directory: " + permanentBundleLocation
                        + " Please be aware, this process can take hours if the bundle is many GB in size.");
                localRet = copyBundle(bundle.getAbsolutePath(), permanentBundleLocation);
            }
        }

        if (localRet.getExitStatus() != ReturnValue.SUCCESS) {
            Log.error("The workflow install failed");
            return localRet;
        }

        // asumption here is this unbundles it, in the future this won't be the case!
        // FIXME: this code should reach inside a zip file to get metadata.xml without unzipping it!
        BundleInfo info = getBundleInfo(bundle, metadataFile);

        for (WorkflowInfo w : info.getWorkflowInfo()) {

            // FIXME: this could cause a lot of problems since the downstream tools may not do this substitution
            // String command = w.getCommand().replaceAll("\\$\\{workflow_bundle_dir\\}", getOutputDir());
            // String configPath = w.getConfigPath().replaceAll("\\$\\{workflow_bundle_dir\\}", getOutputDir());
            // String templatePath = w.getTemplatePath().replaceAll("\\$\\{workflow_bundle_dir\\}", getOutputDir());

            if (packageIntoZip && unzipIntoDir) {
                localRet = metadata.addWorkflow(w.getName(), w.getVersion(), w.getDescription(), w.getCommand(), w.getConfigPath(),
                        w.getTemplatePath(), this.outputDir, true, this.outputZip, true, w.getWorkflowClass(), w.getWorkflowType(),
                        w.getWorkflowEngine(), w.getWorkflowSqwVersion());
            } else if (packageIntoZip && !unzipIntoDir) {
                localRet = metadata.addWorkflow(w.getName(), w.getVersion(), w.getDescription(), w.getCommand(), w.getConfigPath(),
                        w.getTemplatePath(), this.outputDir, false, this.outputZip, true, w.getWorkflowClass(), w.getWorkflowType(),
                        w.getWorkflowEngine(), w.getWorkflowSqwVersion());
            } else if (!packageIntoZip && unzipIntoDir) {
                localRet = metadata.addWorkflow(w.getName(), w.getVersion(), w.getDescription(), w.getCommand(), w.getConfigPath(),
                        w.getTemplatePath(), this.outputDir, true, this.outputZip, false, w.getWorkflowClass(), w.getWorkflowType(),
                        w.getWorkflowEngine(), w.getWorkflowSqwVersion());
            } else {
                Log.error("You need to specify an workflow bundle dir, workflow bundle zip file or both when you install a workflow.");
                localRet.setExitStatus(ReturnValue.FAILURE);
            }

            if (localRet.getExitStatus() == ReturnValue.FAILURE) {
                Log.error("The workflow install failed for " + w.getName() + " version " + w.getVersion());
                return (localRet);
            } else {
                // record the bundle
                String workflowAccession = localRet.getAttribute("sw_accession");
                workflows.add(workflowAccession);
            }
            /*
             * int workflowId = ret.getReturnValue(); String url = permanentBundleLocation + File.separator + bundle.getName(); ret =
             * metadata.updateWorkflow(workflowId, url);
             */
        }
        return localRet;
    }

    /**
     * <p>
     * Getter for the field <code>metadata</code>.
     * </p>
     * 
     * @return a {@link net.sourceforge.seqware.common.metadata.Metadata} object.
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * <p>
     * Setter for the field <code>metadata</code>.
     * </p>
     * 
     * @param metadata
     *            a {@link net.sourceforge.seqware.common.metadata.Metadata} object.
     */
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    /**
     * <p>
     * Getter for the field <code>outputDir</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getOutputDir() {
        return outputDir;
    }

    /**
     * <p>
     * Setter for the field <code>outputDir</code>.
     * </p>
     * 
     * @param outputDir
     *            a {@link java.lang.String} object.
     */
    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * <p>
     * Getter for the field <code>filesArray</code>.
     * </p>
     * 
     * @return a {@link java.util.ArrayList} object.
     */
    public ArrayList<File> getFilesArray() {
        return filesArray;
    }

    /**
     * <p>
     * Setter for the field <code>filesArray</code>.
     * </p>
     * 
     * @param filesArray
     *            a {@link java.util.ArrayList} object.
     */
    public void setFilesArray(ArrayList<File> filesArray) {
        this.filesArray = filesArray;
    }
}
