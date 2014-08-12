package net.sourceforge.seqware.pipeline.workflowV2;

import io.seqware.pipeline.SqwKeys;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.Rethrow;
import net.sourceforge.seqware.common.util.maptools.MapTools;
import net.sourceforge.seqware.common.util.maptools.ReservedIniKeys;
import net.sourceforge.seqware.pipeline.bundle.Bundle;

/**
 * a utils class for creating the AbstractWorkflowDataModel, by reading the metadata.xml file, will load a Java based objectModel or XML-
 * based ObjectModel
 * 
 * @author yliang
 * 
 */
public class WorkflowDataModelFactory {

    private final Map<String, String> config;
    private final Metadata metadata;

    /**
     * This constructs the factory with only the parameters which shouldn't change from workflow to workflow.
     * 
     * @param config
     *            config generated from the .seqware/settings
     * @param metadata
     */
    public WorkflowDataModelFactory(Map<String, String> config, Metadata metadata) {
        this.config = config;
        this.metadata = metadata;
    }

    /**
     * load metadata.xml and load the class.
     * 
     * This method still needs work because it requires a lot of parameters which
     * 
     * @param bundlePath
     * @param workflowAccession
     * @param workflowRunAccession
     * @param workflowEngine
     * @return
     */
    public synchronized AbstractWorkflowDataModel getWorkflowDataModel(String bundlePath, int workflowAccession, int workflowRunAccession,
            String workflowEngine) {

        File bundle = new File(bundlePath);
        // change to absolute path
        bundlePath = bundle.getAbsolutePath();
        Map<String, String> metaInfo = metadata.get_workflow_info(workflowAccession);
        Log.info("Bundle Path: " + bundlePath);
        if (!bundle.exists()) {

            // then first try to see if we can get it from it's permenant location instead
            if (metaInfo.get("permanent_bundle_location") != null) {
                bundle = new File(getAndProvisionBundle(metaInfo.get("permanent_bundle_location")));
            }
            // if we still can't get the bundle then error out
            if (!bundle.exists()) {
                Log.error("ERROR: Bundle is null or doesn't exist! The bundle must be either a zip file or a directory structure.");
                return null;
            }
        }

        metaInfo = WorkflowV2Utility.parseMetaInfo(bundle);
        if (metaInfo == null) {
            Log.error("ERROR: Bundle structure is incorrect, unable to parse metadata.");
            return null;
        }
        Log.info("bundle for workflowdatamodel found");

        // check FTL exist?
        boolean workflowJava = true;
        if (metaInfo.get("workflow_template") != null && !metaInfo.get("workflow_template").isEmpty()) {
            workflowJava = false;
        }

        // Java object or FTL
        AbstractWorkflowDataModel dataModel = null;
        Class<?> clazz = null;
        if (workflowJava) {
            // String clazzPath = metaInfo.get("classes");
            // Log.stdout("looking for classes at " + clazzPath);
            // Log.info("CLASSPATH: " + clazzPath);
            // // get user defined classes
            String classpath = metaInfo.get("workflow_class");
            Log.debug("Attempting to instantiate " + classpath);
            WorkflowClassFinder finder = new WorkflowClassFinder();
            clazz = finder.findFirstWorkflowClass(classpath);

            if (null != clazz) {
                Log.debug("using java object");
                try {
                    Object object = clazz.newInstance();
                    dataModel = (AbstractWorkflowDataModel) object;
                } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException ex) {
                    Log.error(ex, ex);
                    throw Rethrow.rethrow(ex);
                }
            } else {
                Log.stdout("failed looking for classes at " + classpath);
                throw new RuntimeException("Unable to construct workflow class");
            }
        } else {
            throw new RuntimeException("Non-Java workflows not currently supported");
        }
        if (dataModel == null) {
            throw new RuntimeException("Unable to construct datamodel");
        }
        Log.info("datamodel generated");
        // load metadata.xml
        dataModel.setTags(metaInfo);
        // set name, version in workflow
        dataModel.setName(metaInfo.get("name"));
        dataModel.setVersion(metaInfo.get("workflow_version"));
        dataModel.setBundle_version(metaInfo.get("bundle_version"));
        dataModel.setSeqware_version(metaInfo.get("seqware_version"));
        dataModel.setWorkflow_directory_name(metaInfo.get("workflow_directory_name"));
        dataModel.setWorkflowBundleDir(bundlePath);
        dataModel.setWorkflowBasedir(metaInfo.get("basedir"));
        // set memory, network, compute to environment
        dataModel.getEnv().setCompute(metaInfo.get("compute"));
        dataModel.getEnv().setNetwork(metaInfo.get("network"));
        dataModel.getEnv().setMemory(metaInfo.get("memory"));

        Log.info("loading ini files");
        // load ini config
        WorkflowRun workflowRun = metadata.getWorkflowRun(workflowRunAccession);
        Map<String, String> iniString2Map = MapTools.iniString2Map(workflowRun.getIniFile());
        dataModel.setConfigs(iniString2Map);

        // 0.13.6.5 : The Java workflow launcher was not originally designed to schedule, hence it is not properly getting
        // parent accessions from saved ini files (as opposed to on the command line)
        ArrayList<String> parseParentAccessions = parseParentAccessions(dataModel.getConfigs());
        dataModel.setParentAccessions(parseParentAccessions);

        // merge command line option with configs, command-line options should override parent accession set above if present
        this.mergeCmdOptions(dataModel, workflowAccession, workflowRunAccession, workflowEngine);
        // merge version, and name ??? TODO

        // set random, date, wait
        // magic variables always set
        Date date = new Date();
        dataModel.setDate(date.toString());

        // set random
        Random rand = new Random(System.currentTimeMillis());
        int randInt = rand.nextInt(100000000);
        dataModel.setRandom("" + randInt);
        // copy some properties from .settings to configs
        dataModel.getEnv().setOOZIE_URL(config.get(SqwKeys.OOZIE_URL.getSettingKey()));
        dataModel.getEnv().setOOZIE_APP_ROOT(config.get(SqwKeys.OOZIE_APP_ROOT.getSettingKey()));
        dataModel.getEnv().setOOZIE_JOBTRACKER(config.get(SqwKeys.OOZIE_JOBTRACKER.getSettingKey()));
        dataModel.getEnv().setOOZIE_NAMENODE(config.get(SqwKeys.OOZIE_NAMENODE.getSettingKey()));
        dataModel.getEnv().setOOZIE_QUEUENAME(config.get(SqwKeys.OOZIE_QUEUENAME.getSettingKey()));
        dataModel.getEnv().setMapred_job_tracker(config.get(SqwKeys.OOZIE_JOBTRACKER.getSettingKey()));
        dataModel.getEnv().setFs_default_name(config.get("FS.DEFAULT.NAME"));
        dataModel.getEnv().setFs_defaultFS(config.get(SqwKeys.OOZIE_NAMENODE.getSettingKey()));
        dataModel.getEnv().setFs_hdfs_impl(config.get(SqwKeys.FS_HDFS_IMPL.getSettingKey()));
        dataModel.getEnv().setOOZIE_WORK_DIR(config.get(SqwKeys.OOZIE_WORK_DIR.getSettingKey()));

        // get workflow-run-accession
        // in 1.1 we're going to make metadata writeback of at least workflow runs mandatory
        dataModel.setWorkflow_run_accession(String.valueOf(workflowRunAccession));
        dataModel.setWorkflow_accession(String.valueOf(workflowAccession));

        // parse XML or Java Object for
        if (workflowJava) {
            try {
                Method m = clazz.getMethod("setupDirectory");
                m.invoke(dataModel);
                m = clazz.getMethod("setupFiles");
                m.invoke(dataModel);
                // handle the provisionedPath
                // this.setupProvisionedPath(dataModel.getFiles());
                m = clazz.getMethod("setupWorkflow");
                m.invoke(dataModel);
                m = clazz.getMethod("setupEnvironment");
                m.invoke(dataModel);
                m = clazz.getMethod("buildWorkflow");
                m.invoke(dataModel);
            } catch (NullPointerException e) {
                Log.error("NullPointerException", e);
                throw Rethrow.rethrow(e);
            } catch (SecurityException e) {
                Log.error("SecurityException", e);
                throw Rethrow.rethrow(e);
            } catch (NoSuchMethodException e) {
                Log.error("NoSuchMethodException", e);
                throw Rethrow.rethrow(e);
            } catch (IllegalArgumentException e) {
                Log.error("IllegalArgumentException", e);
                throw Rethrow.rethrow(e);
            } catch (IllegalAccessException e) {
                Log.error("IllegalAccessException", e);
                throw Rethrow.rethrow(e);
            } catch (InvocationTargetException e) {
                Log.error("InvocationTargetException", e);
                throw Rethrow.rethrow(e);
            }
        } else {
            throw new RuntimeException("No other workflow engine is currently supported.");
        }
        AbstractWorkflowDataModel.prepare(dataModel);
        Log.info("returning datamodel");
        return dataModel;
    }

    /**
     * I'm copying this from BasicWorkflow since I don't know if the package net.sourceforge.seqware.pipeline.workflow will be removed or if
     * all the workflowV2 will be merged.
     * 
     * This code will either copy or download from S3, unzip, and return unzip location.
     * 
     * It's used when the local workflow bundle dir is null or doesn't exist which is a sign that the workflow bundle should be retrieved
     * from the permanent location
     * 
     * @param permLoc
     * @return
     */
    private String getAndProvisionBundle(String permLoc) {
        String result = null;
        Bundle bundle = new Bundle(metadata, config);
        ReturnValue ret;
        if (permLoc.startsWith("s3://")) {
            ret = bundle.unpackageBundleFromS3(permLoc);
        } else {
            ret = bundle.unpackageBundle(new File(permLoc));
        }
        if (ret != null) {
            return (ret.getAttribute("outputDir"));
        }
        return (result);
    }

    // FIXME should iterate all options automatically
    /**
     * This method is badly named now. There are no command-line options if we always schedule. Instead we only need to retain the process
     * of getting information from the DB into the workflow data model so that we can use it.
     * 
     * @param model
     * @param workflowAccession
     * @param workflowRunAccession
     * @param metadataOutputFilePrefix
     * @param metadataOutputDir
     * @param workflowEngine
     */
    private void mergeCmdOptions(AbstractWorkflowDataModel model, int workflowAccession, int workflowRunAccession, String workflowEngine) {
        // merge parent-accessions
        model.setWorkflow_run_accession(String.valueOf(workflowRunAccession));
        model.setWorkflow_accession(String.valueOf(workflowAccession));

        if (model.hasPropertyAndNotNull(ReservedIniKeys.METADATA.getKey())) {
            try {
                // TODO: fix this magic name
                boolean metadataWriteBack = model.getProperty(ReservedIniKeys.METADATA.getKey()).equals("metadata");
                Log.info("Launching with metadataWriteback = " + metadataWriteBack + " since property was "
                        + model.getProperty(ReservedIniKeys.METADATA.getKey()));
                model.setMetadataWriteBack(metadataWriteBack);
            } catch (Exception ex) {
                Logger.getLogger(WorkflowDataModelFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // metadata-output-file-prefix
        if (model.hasPropertyAndNotNull(ReservedIniKeys.OUTPUT_PREFIX.getKey())) {
            try {
                model.setMetadata_output_file_prefix(model.getProperty(ReservedIniKeys.OUTPUT_PREFIX.getKey()));
            } catch (Exception ex) {
                Logger.getLogger(WorkflowDataModelFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Log.error("You need to specify the output prefix for your workflow using either an override parameter at schedule-time or in your workflow INI file as "
                    + ReservedIniKeys.OUTPUT_PREFIX.getKey());
        }
        // metadata-output-dir
        if (model.hasPropertyAndNotNull(ReservedIniKeys.OUTPUT_DIR.getKey())) {
            try {
                model.setMetadata_output_dir(model.getProperty(ReservedIniKeys.OUTPUT_DIR.getKey()));
            } catch (Exception ex) {
                Logger.getLogger(WorkflowDataModelFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Log.error("You need to specify the output dir for your workflow using either an override parameter at schedule-time or in your workflow INI file as output_dir!");
        }
        // workflow_engine
        if (workflowEngine != null) {
            model.setWorkflow_engine(workflowEngine);
        }
    }

    /**
     * reads a map and tries to find the parent accessions, the result is de-duplicated.
     * 
     * @param map
     * @return
     */
    private static ArrayList<String> parseParentAccessions(Map<String, String> map) {
        ArrayList<String> results = new ArrayList<>();
        HashMap<String, String> resultsDeDup = new HashMap<>();

        for (String key : map.keySet()) {
            if (ReservedIniKeys.PARENT_ACCESSION.getKey().equals(key) || ReservedIniKeys.PARENT_UNDERSCORE_ACCESSIONS.getKey().equals(key)
                    || ReservedIniKeys.PARENT_DASH_ACCESSIONS.getKey().equals(key)) {
                resultsDeDup.put(map.get(key), "null");
            }
        }

        for (String accession : resultsDeDup.keySet()) {
            results.add(accession);
        }

        // for hotfix 0.13.6.3
        // GATK reveals an issue where parent_accession is setup with a correct list
        // of accessions while parent-accessions and parent_accessions are set to 0
        // when the three are mushed together, the rogue zero is transferred to
        // parent_accession and causes it to crash the workflow
        // I'm going to allow a single 0 in case (god forbid) some workflow relies
        // upon this, but otherwise a 0 should not occur in a list of valid
        // parent_accessions
        if (results.contains("0") && results.size() > 1) {
            results.remove("0");
        }

        return results;
    }
}
