package net.sourceforge.seqware.pipeline.bundle;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.runtools.RunTools;
import net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.filetools.ProvisionFilesUtil;

/**
 * This is a utility class that lets you manipulate a workflow bundle.
 *
 * @author briandoconnor@gmail.com
 *
 */
public class Bundle {

    protected String permanentBundleLocation = null;
    protected String bundleDir = null;
    protected ReturnValue ret = new ReturnValue();
    protected Metadata metadata = null;
    protected Map<String, String> config = null;
    // this is used as the location of the workflow bundle location 
    protected String outputDir = null;
    // this is used as the location of the workflow bundle zip
    protected String outputZip = null;
    protected ArrayList<File> filesArray = new ArrayList<File>();

    public Bundle() {
        super();
    }

    public Bundle(Metadata metadata, Map<String, String> config) {
        super();
        this.metadata = metadata;
        this.config = config;
        permanentBundleLocation = config.get("SW_BUNDLE_REPO_DIR");
        bundleDir = config.get("SW_BUNDLE_DIR");
    }

    public BundleInfo getBundleInfo(File bundle, File metadataFile) {

        if (bundle == null || !bundle.exists()) {
            Log.error("ERROR: Bundle is null or doesn't exist! The bundle must be either a zip file or a directory structure.");
            return (null);
        }

        BundleInfo bi = new BundleInfo();
        ReturnValue returned = new ReturnValue(ReturnValue.SUCCESS);
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
                if ("metadata.xml".equals(file.getName()) && (currMetadata == null || currMetadata.getAbsolutePath().length() > file.getAbsolutePath().length())) {
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

    public ReturnValue unpackageBundle(File bundle) {

        ret.setExitStatus(ReturnValue.SUCCESS);

        // check the bundleDir
        if (bundleDir == null || "".equals(bundleDir)) {
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            ret.setStderr("ERROR: the SW_BUNDLE_DIR variable in your SEQWARE_SETTINGS (default .seqware/settings) file appears to be undefined!");
            return (ret);
        }

        // check the bundleDir
        if (bundle == null || !bundle.isFile()) {
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            ret.setStderr("ERROR: the bundle you passed is either null or is not a file. It must be a zip file!");
            return (ret);
        }

        String bundleName = bundle.getName();
        bundleName = bundleName.replaceAll(".zip", "");
        File outputDir = new File(bundleDir + File.separator + bundleName);
        FileTools.unzipFile(bundle, outputDir);
        ret.setAttribute("outputDir", outputDir.getAbsolutePath());
        setOutputDir(outputDir.getAbsolutePath());
        FileTools.listFilesRecursive(outputDir, filesArray);

        return (ret);
    }

    public ReturnValue unpackageBundleFromS3(String bundleURL) {

        ret.setExitStatus(ReturnValue.SUCCESS);

        // check the bundleDir
        if (bundleDir == null || "".equals(bundleDir)) {
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            ret.setStderr("ERROR: the SW_BUNDLE_DIR variable in your SEQWARE_SETTINGS (default .seqware/settings) file appears to be undefined!");
            return (ret);
        }

        // check the bundleDir
        if (bundleURL == null) {
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
            ret.setStderr("ERROR: the bundle you passed is either null or is not a file. It must be a zip file!");
            return (ret);
        }

        // attempt to find output location, use the perm bundle location
        // unless it points to S3, in this case just make a temp dir 
        // in the provisioned bundle dir to download the zip into
        String zipDownloadDir = permanentBundleLocation;
        if (permanentBundleLocation.startsWith("s3://")) {
            // then can't use it as a temp dir
            File tempDir = null;
            try {
                tempDir = FileTools.createDirectoryWithUniqueName(new File(this.bundleDir), "wokflow_zip_temp");
            } catch (Exception e) {
                Log.error("Problem creating a temp directory to use for zipping workflow " + e.getMessage());
                ret.setExitStatus(ReturnValue.FAILURE);
                return (ret);
            }
            zipDownloadDir = tempDir.getAbsolutePath();
        }
        
        // download from S3
        ProvisionFiles pf = new ProvisionFiles();
        pf.setParameters(Arrays.asList("--input-file", bundleURL, "--output-dir", zipDownloadDir, "--force-copy"));
        pf.do_verify_parameters();
        ret = pf.do_run();
        if (ret.getExitStatus() != ReturnValue.SUCCESS) { return(ret); }
        
        // name
        String[] path = bundleURL.split(File.separator);
        String bundleName = path[path.length - 1];
        bundleName = bundleName.replaceAll(".zip", "");
        
        FileTools.unzipFile(new File(zipDownloadDir+File.separator+bundleName+".zip"), new File(bundleDir + File.separator + bundleName));
        ret.setAttribute("outputDir", bundleDir+File.separator+bundleName);
        setOutputDir(bundleDir+File.separator+bundleName);
        FileTools.listFilesRecursive(new File(bundleDir+File.separator+bundleName), filesArray);

        return (ret);
    }

    public ReturnValue packageBundle(File bundlePath, File bundleOutput) {
        ret.setExitStatus(ReturnValue.SUCCESS);

        if (bundlePath == null || !bundlePath.isDirectory()) {
            ret.setExitStatus(ReturnValue.INVALIDFILE);
            ret.setStderr("ERROR: the bundle path you're trying to zip up is either null or not a directory!");
            return (ret);
        }

        File outputZipFile = new File(bundleOutput.getAbsolutePath() + File.separator + bundlePath.getName() + ".zip");

        if (!FileTools.zipDirectoryRecursive(bundlePath, outputZipFile, null, true, true)) {
            //tests
            ret.setExitStatus(ReturnValue.FAILURE);
        }

        // save the location of the zip file
        // FIXME: correct?
        this.outputZip = outputZipFile.getAbsolutePath();

        return (ret);
    }

    public ReturnValue packageBundleToS3(File bundlePath, String bundleOutputPrefix) {
        ret.setExitStatus(ReturnValue.SUCCESS);

        if (bundlePath == null || !bundlePath.isDirectory()) {
            ret.setExitStatus(ReturnValue.INVALIDFILE);
            ret.setStderr("ERROR: the bundle path you're trying to zip up is either null or not a directory!");
            return (ret);
        }

        File tempDir = null;
        try {
            tempDir = FileTools.createDirectoryWithUniqueName(new File(this.bundleDir), "wokflow_zip_temp");
        } catch (Exception e) {
            Log.error("Problem creating a temp directory to use for zipping workflow " + e.getMessage());
            ret.setExitStatus(ReturnValue.FAILURE);
            return (ret);
        }

        if (!FileTools.zipDirectoryRecursive(bundlePath, new File(tempDir.getAbsolutePath() + File.separator + bundlePath.getName() + ".zip"), null, true, true)) {
            ret.setExitStatus(ReturnValue.FAILURE);
        }

        String zipFile = tempDir.getAbsolutePath() + File.separator + bundlePath.getName() + ".zip";
        ProvisionFilesUtil fileUtil = new ProvisionFilesUtil();
        int bufLen = 5000 * 1024;
        Log.stdout("Copying local file " + zipFile + " to output " + bundleOutputPrefix + " this may take a long time!");
        BufferedInputStream reader = fileUtil.getSourceReader(zipFile, bufLen, 0L);
        boolean result = fileUtil.putToS3(reader, bundleOutputPrefix);

        if (!result) {
            Log.error("Failed to copy file to S3!");
            ret.setExitStatus(ReturnValue.FAILURE);
            return (ret);
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

        return (ret);
    }

    public ReturnValue copyBundleToS3(File bundle, String bundleOutputPrefix) {
        ret.setExitStatus(ReturnValue.SUCCESS);
        if (bundle == null || !bundle.isFile() || !bundle.getName().endsWith(".zip")) {
            ret.setExitStatus(ReturnValue.INVALIDFILE);
            ret.setStderr("ERROR: the bundle zip you're trying to copy up is either null or not a zip file!");
            return (ret);
        }

        ProvisionFilesUtil fileUtil = new ProvisionFilesUtil();
        int bufLen = 5000 * 1024;
        Log.stdout("Copying local file " + bundle.getAbsolutePath() + " to output " + bundleOutputPrefix + " this may take a long time!");
        BufferedInputStream reader = fileUtil.getSourceReader(bundle.getAbsolutePath(), bufLen, 0L);
        boolean result = fileUtil.putToS3(reader, bundleOutputPrefix);

        if (!result) {
            Log.error("Failed to copy file to S3!");
            ret.setExitStatus(ReturnValue.FAILURE);
            return (ret);
        }

        // save the location of the zip file
        // FIXME: correct?
        this.outputZip = bundleOutputPrefix + File.separator + bundle.getName();

        // now delete the local zip file
        Log.stdout("Finished copying file to S3!");
        Log.stdout("You may want to delete (or archive locally) the local zip file: " + bundle.getAbsolutePath());

        return (ret);
    }

    public ReturnValue copyBundle(String sourceFile, String targetDir) {
        ReturnValue result = new ReturnValue(ReturnValue.SUCCESS);
        File source = new File(sourceFile);
        String sourceName = source.getName();
        this.outputZip = targetDir + File.separator + sourceName;
        if (sourceFile.equals(targetDir + File.separator + sourceName)) {
            Log.error("Cannot copy file onto itself!");
            result.setExitStatus(ReturnValue.FAILURE);
        } else {
            ProvisionFiles pf = new ProvisionFiles();
            pf.setParameters(Arrays.asList("--input-file", sourceFile, "--output-dir", targetDir, "--force-copy"));
            pf.do_verify_parameters();
            result = pf.do_run();
        }
        return result;
    }

    public ReturnValue validateBundle(File bundle) {

        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

        if (bundle == null || !bundle.exists()) {
            ret.setExitStatus(ReturnValue.INVALIDFILE);
            ret.setStderr("ERROR: the bundle path you're trying to zip up is either null or not a directory!");
            return (ret);
        }

        if (bundle.isDirectory()) {
            outputDir = bundle.getAbsolutePath();
            FileTools.listFilesRecursive(bundle, filesArray);
        } else {
            ret = unpackageBundle(bundle);
            outputDir = ret.getAttribute("outputDir");
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
            }
        } catch (Exception e) {
            ret.setExitStatus(ReturnValue.FAILURE);
            ret.setStderr("ERROR: problems validating " + bundle.getAbsolutePath());
        }

        if (ret.getExitStatus() == ReturnValue.SUCCESS) {
            Log.info("Validated Bundle: " + bundle.getAbsolutePath());
        }
        return (ret);
    }

    public ReturnValue testBundle(File bundle, File metadataFile) {
        return (testBundle(bundle, metadataFile, null, null));
    }

    /**
     * A bundle test occurs without metadata writeback
     *
     * @param bundle
     * @return ReturnValue
     */
    public ReturnValue testBundle(File bundle, File metadataFile, String workflow, String version) {

        // unzip the package and get metadata
        // FIXME: revise once getBundleInfo no longer unzips the file, could run the metadata.xml through template process
        BundleInfo bi = getBundleInfo(bundle, metadataFile);

        try {

            for (WorkflowInfo wi : bi.getWorkflowInfo()) {

                if ((workflow == null || version == null) || (workflow != null && version != null && workflow.equals(wi.getName()) && version.equals(wi.getVersion()))) {

                    // actual test command
                    String testCmd = wi.getTestCmd();
                    testCmd = testCmd.replaceAll("\\$\\{workflow_bundle_dir\\}", getOutputDir());
                    Log.stdout("  Running Test Command:\n" + testCmd);

                    ReturnValue runReturn = RunTools.runCommand(testCmd);
                    if (runReturn.getExitStatus() != ReturnValue.SUCCESS) {
                        Log.error("Command Run Failed!\n" + runReturn.getStderr());
                        ret.setReturnValue(runReturn.getExitStatus());
                    }

                    // now parse out the return status from the pegasus tool 
                    // FIXME: there should be more direct way (calling API directly) than this
                    String stdOut = runReturn.getStdout();
                    Pattern p = Pattern.compile("(pegasus-status -l \\S+)");
                    Matcher m = p.matcher(stdOut);
                    if (m.find()) {
                        String statusCmd = m.group(1);
                        Log.info("STATUS: " + statusCmd);
                        boolean cont = true;
                        Thread.sleep(20000);
                        while (cont) {
                            ReturnValue statusReturn = RunTools.runCommand(statusCmd);
                            Log.info(statusReturn.getStdout());
                            if (statusReturn.getStdout().contains("FAILED")) {
                                ret.setExitStatus(ReturnValue.FAILURE);
                                Log.info("Workflow failed");
                                cont = false;
                            } else if (statusReturn.getStdout().contains("COMPLETED")) {
                                Log.info("Workflow Completed!");
                                cont = false;
                            }
                            Thread.sleep(5000);
                        }
                    }
                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return (ret);
    }

    public ReturnValue installBundleZipOnly(File bundle, File metadataFile) {
        return (installBundle(bundle, metadataFile, true, false));
    }

    public ReturnValue installBundleDirOnly(File bundle, File metadataFile) {
        return (installBundle(bundle, metadataFile, false, true));
    }

    public ReturnValue installBundle(File bundle, File metadataFile) {
        return (installBundle(bundle, metadataFile, true, true));
    }

    /**
     * This method allows you to install the workflow bundle to the metadb
     * optionally building a zip file along the way and archiving it to a safe
     * location.
     *
     * @param bundle
     * @param metadataFile
     * @param packageIntoZip
     * @param unzipIntoDir
     * @return
     */
    protected ReturnValue installBundle(File bundle, File metadataFile, boolean packageIntoZip, boolean unzipIntoDir) {

        ReturnValue ret = new ReturnValue(ReturnValue.SUCCESS);

        // installing from an unzipped workflow bundle directory
        if (bundle != null && bundle.isDirectory() && packageIntoZip) {

            if (permanentBundleLocation == null) {
                Log.error("You tried to install a bundle and create a .zip file of the bundle without having a SW_BUNDLE_REPO_DIR defined in your seqware settings file! This needs to be defined and pointed to a location where a .zip file can be written.");
                return (new ReturnValue(ReturnValue.FAILURE));
            } else if (permanentBundleLocation.startsWith("s3://")) {
                Log.stdout("Now packaging " + bundle.getAbsolutePath() + " to a zip file and transferring to the S3 location: " + permanentBundleLocation + " Please be aware, this process can take hours if the bundle is many GB in size.");
                packageBundleToS3(bundle, permanentBundleLocation);
            } else {
                // then it's a directory          
                // now package this up
                Log.stdout("Now packaging " + bundle.getAbsolutePath() + " to a zip file and transferring to the directory: " + permanentBundleLocation + " Please be aware, this process can take hours if the bundle is many GB in size.");
                packageBundle(bundle, new File(permanentBundleLocation));
            }
        } // installing from a zip file (will be unzipped below by getBundleInfo) copy to permanent location
        else if (bundle != null && bundle.isFile() && bundle.getName().endsWith(".zip")) {
            // FIXME: the getBundleInfo will unzip this below, should only do that if the request is for unzip
            if (permanentBundleLocation == null) {
                Log.error("You tried to install a bundle from a .zip file without having a SW_BUNDLE_REPO_DIR defined in your seqware settings file! This needs to be defined and pointed to a location where a .zip file can be copied to.");
                return (new ReturnValue(ReturnValue.FAILURE));
            } else if (permanentBundleLocation.startsWith("s3://")) {
                Log.stdout("Now packaging " + bundle.getAbsolutePath() + " to a zip file and transferring to the S3 location: " + permanentBundleLocation + " Please be aware, this process can take hours if the bundle is many GB in size.");
                copyBundleToS3(bundle, permanentBundleLocation);
            } else {
                // then it's a directory          
                // now package this up
                Log.stdout("Now packaging " + bundle.getAbsolutePath() + " to a zip file and transferring to the directory: " + permanentBundleLocation + " Please be aware, this process can take hours if the bundle is many GB in size.");
                copyBundle(bundle.getAbsolutePath(), permanentBundleLocation);
            }
        }

        // asumption here is this unbundles it, in the future this won't be the case!
        // FIXME: this code should reach inside a zip file to get metadata.xml without unzipping it!
        BundleInfo info = getBundleInfo(bundle, metadataFile);

        for (WorkflowInfo w : info.getWorkflowInfo()) {

            // FIXME: this could cause a lot of problems since the downstream tools may not do this substitution
            //String command = w.getCommand().replaceAll("\\$\\{workflow_bundle_dir\\}", getOutputDir());
            //String configPath = w.getConfigPath().replaceAll("\\$\\{workflow_bundle_dir\\}", getOutputDir());
            //String templatePath = w.getTemplatePath().replaceAll("\\$\\{workflow_bundle_dir\\}", getOutputDir());

            if (packageIntoZip && unzipIntoDir) {
                ret = metadata.addWorkflow(w.getName(), w.getVersion(), w.getDescription(), w.getCommand(), w.getConfigPath(), w.getTemplatePath(), this.outputDir, true, this.outputZip, true);
            } else if (packageIntoZip && !unzipIntoDir) {
                ret = metadata.addWorkflow(w.getName(), w.getVersion(), w.getDescription(), w.getCommand(), w.getConfigPath(), w.getTemplatePath(), this.outputDir, false, this.outputZip, true);
            } else if (!packageIntoZip && unzipIntoDir) {
                ret = metadata.addWorkflow(w.getName(), w.getVersion(), w.getDescription(), w.getCommand(), w.getConfigPath(), w.getTemplatePath(), this.outputDir, true, this.outputZip, false);
            } else {
                Log.error("You need to specify an workflow bundle dir, workflow bundle zip file or both when you install a workflow.");
                ret.setExitStatus(ReturnValue.FAILURE);
            }

            if (ret.getExitStatus() == ReturnValue.FAILURE) {
                Log.error("The workflow install failed for " + w.getName() + " version " + w.getVersion());
                return (ret);
            }
            /*
             * int workflowId = ret.getReturnValue(); String url =
             * permanentBundleLocation + File.separator + bundle.getName(); ret =
             * metadata.updateWorkflow(workflowId, url);
             */
        }
        return (ret);
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public ArrayList<File> getFilesArray() {
        return filesArray;
    }

    public void setFilesArray(ArrayList<File> filesArray) {
        this.filesArray = filesArray;
    }
}
