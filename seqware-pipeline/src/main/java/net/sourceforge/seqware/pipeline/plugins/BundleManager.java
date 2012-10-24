/**
 * @author briandoconnor@gmail.com
 * 
 * The WorkflowLauncher is responsible for launching workflows with or without metadata writeback.
 * 
 */
package net.sourceforge.seqware.pipeline.plugins;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.pipeline.bundle.Bundle;
import net.sourceforge.seqware.pipeline.bundle.BundleInfo;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;
import net.sourceforge.seqware.common.util.Log;

import org.openide.util.lookup.ServiceProvider;

/**
 * <p>BundleManager class.</p>
 *
 * @author boconnor
 *
 * ProviderFor(PluginInterface.class)
 *
 * TODO: improve the use of workflow-accession, see https://jira.oicr.on.ca/browse/SEQWARE-539
 * TODO: the param listing won't currently work for a local provisioned directory
 * @version $Id: $Id
 */
@ServiceProvider(service = PluginInterface.class)
public class BundleManager extends Plugin {

    ReturnValue ret = new ReturnValue();

    /**
     * <p>Constructor for BundleManager.</p>
     */
    public BundleManager() {
        super();
        parser.acceptsAll(Arrays.asList("help", "h", "?"), "Optional: Provides this help message.");
        parser.acceptsAll(Arrays.asList("bundle", "b"), "The path to a bundle zip file, can specify this or the workflow-run-accession of an already-installed bundle.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("list", "l"), "Optional: List the workflows contained in this bundle.");
        parser.acceptsAll(Arrays.asList("list-installed", "list-install"), "Optional: List the workflows contained in this bundle. The database/webservice must be enabled in your .seqware/settings for this option to work.");
        parser.acceptsAll(Arrays.asList("workflow-accession", "wa"), "Optional: The sw_accession of the workflow. Specify this or the workflow, version, and bundle. Currently used in conjunction with the list-workflow-params for now.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("list-workflow-params", "list-params"), "Optional: List the parameters for a given workflow and version. You need to supply a workflow accession and you need a database/webservice enabled in your .seqware/settings for this option to work.");
        parser.acceptsAll(Arrays.asList("validate", "v"), "Optional: Run a light basic validation on this bundle.");
        parser.acceptsAll(Arrays.asList("test", "t"), "Optional: This will trigger the test setup in the metadata.xml within this bundle.");
        parser.acceptsAll(Arrays.asList("install", "i"), "Optional: if the --bundle param points to a .zip file then the install process will first unzip into the directory specified by the directory defined by SW_BUNDLE_DIR in the .seqware/settings file (skipping files that already exit).  It will then copy the whole zip file to the SW_BUNDLE_ARCHIVE_DIR which can be a directory or S3 prefix (the copy will be skipped if the file is already at this location). It will finish this process by installing this bundle in the database with the permanent_bundle_location pointed to the zip file location and current_working_dir pointed to the unzipped location.  If the --bundle param point to a directory then this will first create a zip of the bundle and place it in SW_BUNDLE_ARCHIVE_DIR. It will then install this bundle in the database with the permanent_bundle_location pointed to the zip file location and current_working_dir pointed to the unzipped location. The method (direct database or web service) and server location of the SeqWare  MetaDB is controlled via the .seqware/settings file.");
        parser.acceptsAll(Arrays.asList("install-zip-only", "izo"), "Optional: This will suppress the unzipping of a zip file, it is only valid if the --bundle points to a zip file and not a directory. It will take a workflow bundle zip file, copy it to the SW_BUNDLE_ARCHIVE_DIR location, and then installs that workflow into the database.  Only the permanent_bundle_location location will be defined, the current_working_dir will be null. (PROBLEM: can't read the metadata.xml if the workflow zip isn't unzipped!)");
        parser.acceptsAll(Arrays.asList("install-dir-only", "ido"), "Optional: This will suppress the creation of a zip file from a workflow bundle directory. It will simply install the workflow into the database and set the current_working_dir but leave permanent_bundle_location null.");
        parser.acceptsAll(Arrays.asList("path-to-package", "p"), "Optional: When combined with a bundle zip file specified via --bundle this option specifies an input directory to zip to create a bundle output file.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("workflow", "w"), "The name of the workflow to be used. This must be used in conjunction with a version and bundle. Will restrict action to this workflow and version if specified.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("version", "workflow-version"), "The workflow version to be used. This must be used in conjunction with a version and bundle. Will restrict action to this workflow and version if specified.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("download"), "Downloads a workflow bundle zip. This must be used in conjunction with a workflow name and version.");
        parser.acceptsAll(Arrays.asList("download-url"), "Downloads a workflow bundle zip from a URL to the local directory.").withRequiredArg();
        parser.acceptsAll(Arrays.asList("metadata", "m"), "Specify the path to the metadata.xml file.").withRequiredArg();

        ret.setExitStatus(ReturnValue.SUCCESS);
    }


    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#init()
     */
    /** {@inheritDoc} */
    @Override
    public ReturnValue init() {
        return ret;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_test()
     */
    /** {@inheritDoc} */
    @Override
    public ReturnValue do_test() {
        // TODO Auto-generated method stub
        return ret;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#do_run()
     */
    /** {@inheritDoc} */
    @Override
    public ReturnValue do_run() {

        // setup bundle object
        Bundle b = new Bundle(metadata, config);
        
        // metadata file to use if provided
        String specificMetadataStr = (String)options.valueOf("metadata");
        File specificMetadataFile = null;
        if (specificMetadataStr != null) { specificMetadataFile = new File(specificMetadataStr); }

        // list the contents of the bundle
        if (options.has("bundle") && (options.has("list") || options.has("help"))) {
            String bundlePath = (String) options.valueOf("bundle");
            File bundle = new File(bundlePath);
            BundleInfo bi = b.getBundleInfo(bundle, specificMetadataFile);

            println("\nList Workflows:\n");

            for (WorkflowInfo wi : bi.getWorkflowInfo()) {
                println(" Workflow:");

                println("  Name : " + wi.getName());
                println("  Version : " + wi.getVersion());
                println("  Description : " + wi.getDescription());

                println("  Test Command: " + wi.getTestCmd());
                println("  Template Path:" + wi.getTemplatePath());
                println("  Config Path:" + wi.getConfigPath());
                println("  Requirements Compute: " + wi.getComputeReq() + " Memory: " + wi.getMemReq() + " Network: " + wi.getNetworkReq() + "\n");

            }
        } else if (options.has("bundle") && options.has("validate")) {
            println("Validating Bundle");
            String bundlePath = (String) options.valueOf("bundle");
            File bundle = new File(bundlePath);
            ret = b.validateBundle(bundle);
            if (ret.getExitStatus() == ReturnValue.SUCCESS) {
                println("Bundle Validates!");
            }
        } else if (options.has("bundle") && options.has("test")) {
            println("Testing Bundle");
            String bundlePath = (String) options.valueOf("bundle");
            File bundle = new File(bundlePath);
            if (options.has("workflow") && options.has("version")) {
                // then just run the test for a particular workflow and version
                ret = b.testBundle(bundle, specificMetadataFile, (String) options.valueOf("workflow"), (String) options.valueOf("version"));
            } else {
                ret = b.testBundle(bundle, specificMetadataFile);
            }
            if (ret.getExitStatus() == ReturnValue.SUCCESS) {
                println("Bundle Passed Test!");
            }
        } else if (options.has("bundle") && options.has("path-to-package")) {
            println("Packaging Bundle");
            String bundleOutput = (String) options.valueOf("bundle");
            String bundlePath = (String) options.valueOf("path-to-package");
            println("Bundle: " + bundleOutput + " path: " + bundlePath);
            ret = b.packageBundle(new File(bundlePath), new File(bundleOutput));
            if (ret.getExitStatus() == ReturnValue.SUCCESS) {
                println("Bundle Has Been Packaged to " + options.valueOf("bundle") + "!");
            }
        } else if (options.has("bundle") && options.has("install")) {
            println("Installing Bundle");
            String bundleFile = (String) options.valueOf("bundle");
            println("Bundle: " + bundleFile);
            ret = b.installBundle(new File(bundleFile), specificMetadataFile);
            if (ret.getExitStatus() == ReturnValue.SUCCESS) {
                println("Bundle Has Been Installed to the MetaDB and Provisioned to " + options.valueOf("bundle") + "!");
            }
        } else if (options.has("bundle") && options.has("install-zip-only")) {
            println("Installing Bundle (Zip Bundle Archive Only)");
            String bundleFile = (String) options.valueOf("bundle");
            println("Bundle: " + bundleFile);
            if (bundleFile.endsWith(".zip")) {
              ret = b.installBundleZipOnly(new File(bundleFile), specificMetadataFile);
              if (ret.getExitStatus() == ReturnValue.SUCCESS) {
                  println("Bundle Has Been Installed to the MetaDB and Provisioned to " + options.valueOf("bundle") + "!");
              }
            } else {
              Log.error("Attempting to install a workflow bundle zip file but the bundle does not end in .zip! "+bundleFile);
              ret.setExitStatus(ret.FAILURE);
            }
        } else if (options.has("bundle") && options.has("install-dir-only")) {
            println("Installing Bundle (Working Directory Only)");
            String bundleFile = (String) options.valueOf("bundle");
            println("Bundle: " + bundleFile);
            File bundleDir = new File(bundleFile);
            if (bundleDir.exists() && bundleDir.isDirectory()) {
              ret = b.installBundleDirOnly(bundleDir, specificMetadataFile);
              if (ret.getExitStatus() == ReturnValue.SUCCESS) {
                println("Bundle Has Been Installed to the MetaDB and Provisioned to " + options.valueOf("bundle") + "!");
              }
            } else {
              Log.error("Attempting to install a workflow bundle from an unzipped bundle directory but the bundle does not exit or point to a directory! "+bundleFile);
              ret.setExitStatus(ret.FAILURE);
            }
        } else if (options.has("list-installed")) {            
            println("=====================================================");
            println("===============INSTALLED WORKFLOWS===================");
            println("=====================================================");
            println("Name\tVersion\tCreation Date\tSeqWare Accession\tBundle Location");
            println("-----------------------------------------------------");
            String params = metadata.listInstalledWorkflows();
            println(params);
            println("-----------------------------------------------------");
        // ((options.has("workflow") && options.has("version") && options.has("bundle")) || options.has("workflow-accession"))
        } else if (options.has("list-workflow-params") && options.has("workflow-accession")) {
            println("=====================================================");
            println("=================WORKFLOW PARAMS=====================");
            println("=====================================================");
            println("-----------------------------------------------------");
            String params = metadata.listInstalledWorkflowParams((String)options.valueOf("workflow-accession"));
            println(params);
            println("-----------------------------------------------------");

        } else if (options.has("workflow") && options.has("version") && options.has("download")) {
            println("Downloading Bundle");
            String workflowName = (String) options.valueOf("workflow");
            println("Workflow: " + workflowName);
            String version = (String) options.valueOf("version");
            println("Version: " + version);

            int accession = metadata.getWorkflowAccession(workflowName, version);
            Map<String, String> info = metadata.get_workflow_info(accession);

            String sourceFile = info.get("permanent_bundle_location");
            String targetDir = new File(".").getAbsolutePath();

            ret = b.copyBundle(sourceFile, targetDir);
            if (ret.getExitStatus() == ReturnValue.SUCCESS) {
                println("Bundle has been copied to the current directory");
            }
        } 
        else if (options.has("download-url"))
        {
            String sourceFile = (String) options.valueOf("download-url");
            String targetDir = new File(".").getAbsolutePath();
            ret = b.copyBundle(sourceFile, targetDir);
            if (ret.getExitStatus() == ReturnValue.SUCCESS) {
                println("Bundle has been copied to the current directory");
            }
            
        }
        else {
            println("Combination of parameters not recognized!");
            println(this.get_syntax());
            ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
        }

        return ret;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.seqware.pipeline.plugin.PluginInterface#clean_up()
     */
    /** {@inheritDoc} */
    @Override
    public ReturnValue clean_up() {
        // TODO Auto-generated method stub
        return ret;
    }

    /**
     * <p>get_description.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String get_description() {
        return ("A plugin that lets you create, test, and install workflow bundles.");
    }
}
