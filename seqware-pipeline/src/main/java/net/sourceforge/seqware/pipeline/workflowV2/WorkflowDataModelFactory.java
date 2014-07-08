package net.sourceforge.seqware.pipeline.workflowV2;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.Rethrow;
import net.sourceforge.seqware.common.util.maptools.MapTools;
import net.sourceforge.seqware.common.util.maptools.ReservedIniKeys;
import net.sourceforge.seqware.pipeline.bundle.Bundle;
import net.sourceforge.seqware.pipeline.workflow.BasicWorkflow;

/**
 * a utils class for creating the AbstractWorkflowDataModel, by reading the metadata.xml file, will load a Java based objectModel or XML
 * based ObjectModel
 * 
 * @author yliang
 * 
 */
public class WorkflowDataModelFactory {

    private final Map<String, String> config;
    private final OptionSet options;
    private final String[] params;
    private final Metadata metadata;

    /**
     * 
     * @param options
     *            options from the WorkflowLauncher
     * @param config
     *            config generated from the .seqware/settings
     * @param params
     *            ini parameters on the command-line
     * @param metadata
     */
    public WorkflowDataModelFactory(OptionSet options, Map<String, String> config, String[] params, Metadata metadata) {
        this.options = options;
        this.config = config;
        this.params = params;
        this.metadata = metadata;

        // need to do options ret
    }

    /**
     * a simple method to replace the ${workflow_bundle_dir} variable (copied from BasicWorkflow)
     * 
     * @param input
     * @param wbd
     * @return
     */
    private String replaceWBD(String input, String wbd) {
        if (input != null) {
            return (input.replaceAll("\\$\\{" + MapTools.VAR_BUNDLE_DIR + "\\}", wbd).replaceAll("\\$\\{" + MapTools.LEGACY_VAR_BUNDLE_DIR
                    + "\\}", wbd));
        } else {
            return null;
        }
    }

    /**
     * load metadata.xml and load the class.
     * 
     * @param bundlePath
     * @param workflowAccession
     *            if this is present, we grab metadata information from the database, not the options
     * @param workflowRunAccession
     * @return
     */
    public AbstractWorkflowDataModel getWorkflowDataModel(String bundlePath, Integer workflowAccession, Integer workflowRunAccession) {

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
        boolean workflow_java = true;
        if (metaInfo.get("workflow_template") != null && !metaInfo.get("workflow_template").isEmpty()) {
            workflow_java = false;
        }

        // Java object or FTL
        AbstractWorkflowDataModel dataModel = null;
        Class<?> clazz = null;
        if (workflow_java) {
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
                }
            } else {
                Log.stdout("failed looking for classes at " + classpath);
            }
        } else {
            throw new RuntimeException("Non-Java workflows not currently supported");
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
        Map<String, String> configs = this.loadIniConfigs(workflowAccession, workflowRunAccession, bundlePath);
        dataModel.setConfigs(configs);

        // 0.13.6.5 : The Java workflow launcher was not originally designed to schedule, hence it is not properly getting
        // parent accessions from saved ini files (as opposed to on the command line)
        ArrayList<String> parseParentAccessions = BasicWorkflow.parseParentAccessions(configs);
        dataModel.setParentAccessions(parseParentAccessions);

        // merge command line option with configs, command-line options should override parent accession set above if present
        this.mergeCmdOptions(dataModel);
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
        dataModel.getEnv().setOOZIE_URL(config.get("OOZIE_URL"));
        dataModel.getEnv().setOOZIE_APP_ROOT(config.get("OOZIE_APP_ROOT"));
        dataModel.getEnv().setOOZIE_JOBTRACKER(config.get("OOZIE_JOBTRACKER"));
        dataModel.getEnv().setOOZIE_NAMENODE(config.get("OOZIE_NAMENODE"));
        dataModel.getEnv().setOOZIE_QUEUENAME(config.get("OOZIE_QUEUENAME"));
        dataModel.getEnv().setHbase_master(config.get("HBASE.MASTER"));
        dataModel.getEnv().setHbase_zookeeper_quorum(config.get("HBASE.ZOOKEEPER.QUORUM"));
        dataModel.getEnv().setHbase_zookeeper_property_clientPort(config.get("HBASE.ZOOKEEPER.PROPERTY.CLIENTPORT"));
        dataModel.getEnv().setMapred_job_tracker(config.get("MAPRED.JOB.TRACKER"));
        dataModel.getEnv().setFs_default_name(config.get("FS.DEFAULT.NAME"));
        dataModel.getEnv().setFs_defaultFS(config.get("FS.DEFAULTFS"));
        dataModel.getEnv().setFs_hdfs_impl(config.get("FS.HDFS.IMPL"));
        dataModel.getEnv().setOOZIE_WORK_DIR(config.get("OOZIE_WORK_DIR"));
        dataModel.getEnv().setOOZIE_APP_PATH(config.get("OOZIE_APP_PATH"));

        // get workflow-run-accession
        if (options.has("status") == false && dataModel.isMetadataWriteBack()) {
            if (workflowAccession != null && workflowRunAccession == null) {
                int workflowrunid = this.metadata.add_workflow_run(workflowAccession);
                int workflowrunaccession = this.metadata.get_workflow_run_accession(workflowrunid);
                dataModel.setWorkflow_accession(Integer.toString(workflowAccession));
                dataModel.setWorkflow_run_accession(String.valueOf(workflowrunaccession));
            } else if (workflowAccession != null && workflowRunAccession != null) {
                dataModel.setWorkflow_accession(Integer.toString(workflowAccession));
                dataModel.setWorkflow_run_accession(String.valueOf(workflowRunAccession));
            } else {
                assert (false);
                Log.error("This condition should never be reached");
                throw new UnsupportedOperationException();
            }
        }

        // parse XML or Java Object for
        if (workflow_java) {
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
            } catch (SecurityException e) {
                Log.error("SecurityException", e);
                Rethrow.rethrow(e);
            } catch (NoSuchMethodException e) {
                Log.error("NoSuchMethodException", e);
                Rethrow.rethrow(e);
            } catch (IllegalArgumentException e) {
                Log.error("IllegalArgumentException", e);
                Rethrow.rethrow(e);
            } catch (IllegalAccessException e) {
                Log.error("IllegalAccessException", e);
                Rethrow.rethrow(e);
            } catch (InvocationTargetException e) {
                Log.error("InvocationTargetException", e);
                Rethrow.rethrow(e);
            }
        } else {
            throw new RuntimeException("No other workflow engine is currently supported.");
        }
        AbstractWorkflowDataModel.prepare(dataModel);
        // set wait
        dataModel.setWait(this.options.has("wait"));
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
        Bundle bundle = new Bundle(this.metadata, this.config);
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

    private Map<String, String> loadIniConfigs(Integer workflowAccession, Integer workflowRunAccession, String bundlePath) {
        // the map
        HashMap<String, String> map = new HashMap<>();
        if (workflowRunAccession != null) {
            Log.info("loading ini files from DB");
            // TODO: this code is from BasicWorkflow, make a notice of that when refactoring

            // get the workflow run
            WorkflowRun wr = this.metadata.getWorkflowRunWithWorkflow(workflowRunAccession.toString());
            // iterate over all the generic default params
            // these params are created when a workflow is installed
            SortedSet<WorkflowParam> workflowParams = this.metadata.getWorkflowParams(workflowAccession.toString());
            for (WorkflowParam param : workflowParams) {
                // SEQWARE-1909 - for installed workflows, interpret a null default as blank
                map.put(param.getKey(), param.getDefaultValue() == null ? "" : param.getDefaultValue());
            }

            // FIXME: this needs to be implemented otherwise portal submitted won't
            // work!
            // now iterate over the params specific for this workflow run
            // this is where the SeqWare Portal will populate parameters for
            // a scheduled workflow
            /*
             * workflowParams = this.metadata.getWorkflowRunParams(workflowRunAccession); for(WorkflowParam param : workflowParams) {
             * map.put(param.getKey(), param.getValue()); }
             */

            // Workflow Runs that are scheduled by the web service don't populate
            // their
            // params into the workflow_run_params table but, instead, directly
            // write
            // to the ini field.
            // FIXME: the web service should just use the same approach as the
            // Portal
            // and this will make it more robust to pass in the
            // parent_processing_accession
            // via the DB rather than ini_file field
            map.putAll(MapTools.iniString2Map(wr.getIniFile()));
        } else {
            Log.info("loading ini files from options");

            Map<String, String> ret = new HashMap<>();
            // set conifg, pass the config files to Map<String,String>, also put the .settings to Map<String,String>
            // ini-files
            ArrayList<String> iniFiles = new ArrayList<>();
            if (options.has("ini-files")) {
                List opts = options.valuesOf("ini-files");
                for (Object opt : opts) {
                    String[] tokens = ((String) opt).split(",");
                    iniFiles.addAll(Arrays.asList(tokens));
                }
            }
            for (String ini : iniFiles) {
                // the ini file path might actually have ${workflow_bundle_dir} in the name
                String newIni = replaceWBD(ini, bundlePath);
                Log.debug("  INI FILE: " + ini);
                if ((new File(ini)).exists()) {
                    MapTools.ini2Map(ini, map);
                }
            }
        }
        // allow the command line options to override options in the map
        // Parse command line options for additional configuration. Note that we
        // do it last so it takes precedence over the INI
        MapTools.cli2Map(this.params, map);
        return MapTools.expandVariables(map, MapTools.providedMap(bundlePath));
    }

    // FIXME should iterate all options automatically
    private void mergeCmdOptions(AbstractWorkflowDataModel model) {
        Map<String, String> map = model.getConfigs();
        // merge parent-accessions
        if (options.has("parent-accessions")) {
            // parent accessions
            ArrayList<String> parentAccessions = new ArrayList<>();
            if (options.has("parent-accessions")) {
                List opts = options.valuesOf("parent-accessions");
                for (Object opt : opts) {
                    String[] tokens = ((String) opt).split(",");
                    parentAccessions.addAll(Arrays.asList(tokens));
                }
            }
            model.setParentAccessions(parentAccessions);
        }
        // merge
        // link-workflow-run-to-parents
        /*
         * if (options.has("link-workflow-run-to-parents")) { ArrayList<String> parentsLinkedToWR = new ArrayList<String>(); List opts =
         * options.valuesOf("link-workflow-run-to-parents"); for (Object opt : opts) { String[] tokens = ((String) opt).split(","); for
         * (String t : tokens) { parentsLinkedToWR.add(t); } } map.put("link-workflow-run-to-parents",
         * org.apache.commons.lang.StringUtils.join(parentsLinkedToWR,",")); }
         */
        // merge workflow-accession
        if (options.has("workflow-accession")) {
            model.setWorkflow_accession((String) options.valueOf("workflow-accession"));
        }
        // merge "workflow-run-accession"
        if (options.has("workflow-run-accession")) {
            model.setWorkflow_run_accession((String) options.valueOf("workflow-run-accession"));
        }
        // merge schedule
        if (options.has("schedule")) {
            map.put("schedule", "true");
        }
        // merge bundle
        if (options.has("bundle")) {
            map.put("bundle", (String) options.valueOf("bundle"));
        }
        // bundle "provisioned-bundle-dir"
        if (options.has("provisioned-bundle-dir")) {
            map.put("provisioned-bundle-dir", (String) options.valueOf("provisioned-bundle-dir"));
        }
        // launch-scheduled
        if (options.has("launch-scheduled")) {
            List<String> scheduledAccessions = (List<String>) options.valuesOf("launch-scheduled");
            map.put("launch-scheduled", org.apache.commons.lang.StringUtils.join(scheduledAccessions, ","));
        }
        // host
        if (options.has("host")) {
            map.put("host", (String) options.valueOf("host"));
        }
        // metadatawriteback
        boolean metadataWriteback = true;
        if (options.has("no-metadata") || options.has("no-meta-db") || options.has("status")) {
            metadataWriteback = false;
        }
        map.put(ReservedIniKeys.METADATA.getKey(), Boolean.toString(metadataWriteback));
        model.setMetadataWriteBack(metadataWriteback);
        // metadata-output-file-prefix
        if (options.has("metadata-output-file-prefix")) {
            model.setMetadata_output_file_prefix((String) options.valueOf("metadata-output-file-prefix"));
        } else if (model.hasPropertyAndNotNull(ReservedIniKeys.OUTPUT_PREFIX.getKey())) {
            try {
                model.setMetadata_output_file_prefix(model.getProperty(ReservedIniKeys.OUTPUT_PREFIX.getKey()));
            } catch (Exception ex) {
                Logger.getLogger(WorkflowDataModelFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Log.error("You need to specify the output prefix for your workflow using either --metadata-output-file-prefix as a WorkflowLauncher param or in your workflow INI file as "
                    + ReservedIniKeys.OUTPUT_PREFIX.getKey());
        }
        // metadata-output-dir
        if (options.has("metadata-output-dir")) {
            model.setMetadata_output_dir((String) options.valueOf("metadata-output-dir"));
        } else if (model.hasPropertyAndNotNull(ReservedIniKeys.OUTPUT_DIR.getKey())) {
            try {
                model.setMetadata_output_dir(model.getProperty(ReservedIniKeys.OUTPUT_DIR.getKey()));
            } catch (Exception ex) {
                Logger.getLogger(WorkflowDataModelFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Log.error("You need to specify the output dir for your workflow using either --metadata-output-dir as a WorkflowLauncher param or in your workflow INI file as output_dir!");
        }
        // workflow_engine
        if (options.has("workflow-engine")) {
            model.setWorkflow_engine((String) options.valueOf("workflow-engine"));
        }
    }
}
