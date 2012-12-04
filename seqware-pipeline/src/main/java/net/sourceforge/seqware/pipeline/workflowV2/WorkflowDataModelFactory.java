package net.sourceforge.seqware.pipeline.workflowV2;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.model.WorkflowParam;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.maptools.MapTools;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.pipeline.workflow.BasicWorkflow;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.StringUtils;
import net.sourceforge.seqware.pipeline.workflowV2.model.XmlWorkflowDataModel;

/**
 * a utils class for creating the AbstractWorkflowDataModel, by reading the
 * metadata.xml file, will load a Java based objectModel or XML based
 * ObjectModel
 *
 * @author yliang
 *
 */
public class WorkflowDataModelFactory {

    private Map<String, String> config;
    private OptionSet options;
    private String[] params;
    private Metadata metadata;

    public WorkflowDataModelFactory(OptionSet options, Map<String, String> config, String[] params, Metadata metadata) {
        this.options = options;
        this.config = config;
        this.params = params;
        this.metadata = metadata;
    }



    /**
     * load metadata.xml, if FTL, parse the FTL to XML, and translate it to Java
     * based Object if Java, load the class.
     * @param workflowAccession if this is present, we grab metadata information from 
     * the database, not the options
     * @return
     */
    public AbstractWorkflowDataModel getWorkflowDataModel(Integer workflowAccession, Integer workflowRunAccession) {
        assert(workflowAccession == null && workflowRunAccession == null || workflowAccession != null && workflowRunAccession != null);
        String bundlePath = null;        
        Map<String, String> metaInfo = null;
        Log.stdout("factory attempting to find bundle");
        if (workflowAccession != null) {
            Log.stdout("factory attempting to find bundle from DB");
            // this execution path is hacked in for running from the database and can be refactored into BasicWorkflow
            metaInfo = this.metadata.get_workflow_info(workflowAccession);
            WorkflowInfo wi = BasicWorkflow.parseWorkflowMetadata(config);
            bundlePath = wi.getWorkflowDir();
        } else {
            Log.stdout("factory attempting to find bundle from options");
            bundlePath = WorkflowV2Utility.determineRelativeBundlePath(options);
            File bundle = new File(bundlePath);
            //change to absolute path
            bundlePath = bundle.getAbsolutePath();
            Log.info("Bundle Path: " + bundlePath);
            if (bundle == null || !bundle.exists()) {
                Log.error("ERROR: Bundle is null or doesn't exist! The bundle must be either a zip file or a directory structure.");
                return null;
            }

            metaInfo = WorkflowV2Utility.parseMetaInfo(bundle);
            if (metaInfo == null) {
                Log.error("ERROR: Bundle structure is incorrect, unable to parse metadata.");
                return null;
            }
        }

        //check FTL exist?
        boolean workflow_java = true;
        if (metaInfo.get("workflow_template") != null && !metaInfo.get("workflow_template").toString().isEmpty()) {
            workflow_java = false;
        }


        //Java object or FTL
        AbstractWorkflowDataModel dataModel = null;
        Class<?> clazz = null;
        if (workflow_java) {
            String clazzPath = metaInfo.get("classes");
            Log.info("CLASSPATH: " + clazzPath);
            // get user defined classes
            WorkflowClassFinder finder = new WorkflowClassFinder();
            clazz = finder.findFirstWorkflowClass(clazzPath);
            if (null != clazz) {
                Log.debug("using java object");
                try {
                    Object object = clazz.newInstance();
                    dataModel = (AbstractWorkflowDataModel) object;
                } catch (InstantiationException ex) {
                    Log.error(ex);
                } catch (IllegalAccessException ex) {
                    Log.error(ex);
                } catch (SecurityException ex) {
                    Log.error(ex);
                } catch (IllegalArgumentException ex) {
                    Log.error(ex);
                }
            }
        } else {
            dataModel = new XmlWorkflowDataModel();
        }
        //load metadata.xml
        dataModel.setTags(metaInfo);
        //set name, version in workflow
        dataModel.setName(metaInfo.get("name"));
        dataModel.setVersion(metaInfo.get("workflow_version"));
        dataModel.setBundle_version(metaInfo.get("bundle_version"));
        dataModel.setSeqware_version(metaInfo.get("seqware_version"));
        dataModel.setWorkflow_directory_name(metaInfo.get("workflow_directory_name"));
        dataModel.setWorkflowBundleDir(bundlePath);
        dataModel.setWorkflowBasedir(metaInfo.get("basedir"));
        //set memory, network, compute to environment
        dataModel.getEnv().setCompute(metaInfo.get("compute"));
        dataModel.getEnv().setNetwork(metaInfo.get("network"));
        dataModel.getEnv().setMemory(metaInfo.get("memory"));

        //load ini config
        Map<String, String> configs = this.loadIniConfigs(workflowAccession, workflowRunAccession);

        //merge command line option with configs
        this.mergeCmdOptions(dataModel);
        //merge version, and name ??? TODO 

        //set random, date, wait
        // magic variables always set
        Date date = new Date();
        dataModel.setDate(date.toString());

        //set random
        Random rand = new Random(System.currentTimeMillis());
        int randInt = rand.nextInt(100000000);
        dataModel.setRandom("" + randInt);
        //copy some properties from .settings to configs
        dataModel.getEnv().setPegasusConfigDir(config.get("SW_PEGASUS_CONFIG_DIR"));
        dataModel.getEnv().setDaxDir(config.get("SW_DAX_DIR"));
        dataModel.getEnv().setSwCluster(config.get("SW_CLUSTER"));
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

        //get workflow-run-accession
        if (options.has("status") == false && options.has(("workflow-accession"))) {
            int workflowAccession_options = Integer.parseInt((String) options.valueOf("workflow-accession"));
            int workflowrunaccession = this.metadata.add_workflow_run(workflowAccession_options);
            //configs.put("workflow-run-accession", ""+workflowrunaccession);
            dataModel.setWorkflow_run_accession(String.valueOf(workflowrunaccession));
        }
        dataModel.setConfigs(configs);

        //parse XML or Java Object for
        if (workflow_java) {
            try {
                Method m = null;
                m = clazz.getMethod("setupDirectory");
                m.invoke(dataModel);
                m = clazz.getMethod("setupFiles");
                m.invoke(dataModel);
                //handle the provisionedPath
                //this.setupProvisionedPath(dataModel.getFiles());
                m = clazz.getMethod("setupWorkflow");
                m.invoke(dataModel);
                m = clazz.getMethod("setupEnvironment");
                m.invoke(dataModel);
                m = clazz.getMethod("buildWorkflow");
                m.invoke(dataModel);
            } catch (SecurityException e) {
                Log.error(e);
            } catch (NoSuchMethodException e) {
                Log.error(e);
            } catch (IllegalArgumentException e) {
                Log.error(e);
            } catch (IllegalAccessException e) {
                Log.error(e);
            } catch (InvocationTargetException e) {
                Log.error(e);
            }
        } else {
            WorkflowXmlParser xmlParser = new WorkflowXmlParser();
            xmlParser.parseXml(dataModel, metaInfo.get("workflow_template"));
        }
        //set wait
        dataModel.setWait(this.options.has("wait"));
        return dataModel;
    }

    private Map<String, String> resolveMap(Map<String, String> input) {
        Map<String, String> result = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : input.entrySet()) {
            String value = entry.getValue();
            if (StringUtils.hasVariable(value)) {
                value = StringUtils.replace(value, input);
            }
            result.put(entry.getKey(), value);
        }
        return result;
    }

    private Map<String, String> loadIniConfigs(Integer workflowAccession, Integer workflowRunAccession) {
        // the map
	HashMap<String, String> map = new HashMap<String, String>();
        if (workflowAccession != null) {
            // TODO: this code is from BasicWorkflow, make a notice of that when refactoring

            // get the workflow run
            WorkflowRun wr = this.metadata.getWorkflowRunWithWorkflow(workflowRunAccession.toString());
            // iterate over all the generic default params
            // these params are created when a workflow is installed
            SortedSet<WorkflowParam> workflowParams = this.metadata
                    .getWorkflowParams(workflowAccession.toString());
            for (WorkflowParam param : workflowParams) {
                map.put(param.getKey(), param.getValue());
            }

            // FIXME: this needs to be implemented otherwise portal submitted won't
            // work!
            // now iterate over the params specific for this workflow run
            // this is where the SeqWare Portal will populate parameters for
            // a scheduled workflow
	/*
             * workflowParams =
             * this.metadata.getWorkflowRunParams(workflowRunAccession);
             * for(WorkflowParam param : workflowParams) { map.put(param.getKey(),
             * param.getValue()); }
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


            Map<String, String> ret = new HashMap<String, String>();
            //set conifg, pass the config files to Map<String,String>, also put the .settings to Map<String,String>
            // ini-files
            ArrayList<String> iniFiles = new ArrayList<String>();
            if (options.has("ini-files")) {
                List opts = options.valuesOf("ini-files");
                for (Object opt : opts) {
                    String[] tokens = ((String) opt).split(",");
                    for (String t : tokens) {
                        iniFiles.add(t);
                    }
                }
            }
            for (String ini : iniFiles) {
                Log.stdout("  INI FILE: " + ini);
                if ((new File(ini)).exists()) {
                    MapTools.ini2Map(ini, map);
                }
            }
        }
        // allow the command line options to override options in the map
        // Parse command line options for additional configuration. Note that we
        // do it last so it takes precedence over the INI
        MapTools.cli2Map(this.params, map);
        MapTools.mapExpandVariables(map);

        Map<String, String> ret = this.resolveMap(map);
        return ret;
    }

    //FIXME should iterate all options automatically
    private void mergeCmdOptions(AbstractWorkflowDataModel model) {
        Map<String, String> map = model.getConfigs();
        //merge parent-accessions
        if (options.has("parent-accessions")) {
            // parent accessions
            ArrayList<String> parentAccessions = new ArrayList<String>();
            if (options.has("parent-accessions")) {
                List opts = options.valuesOf("parent-accessions");
                for (Object opt : opts) {
                    String[] tokens = ((String) opt).split(",");
                    for (String t : tokens) {
                        parentAccessions.add(t);
                    }
                }
            }
            model.setParentAccessions(parentAccessions);
        }
        //merge 
        // link-workflow-run-to-parents
/*    	if (options.has("link-workflow-run-to-parents")) {
         ArrayList<String> parentsLinkedToWR = new ArrayList<String>();
         List opts = options.valuesOf("link-workflow-run-to-parents");
         for (Object opt : opts) {
         String[] tokens = ((String) opt).split(",");
         for (String t : tokens) {
         parentsLinkedToWR.add(t);
         }
         }
         map.put("link-workflow-run-to-parents", org.apache.commons.lang.StringUtils.join(parentsLinkedToWR,","));
         }*/
        //merge workflow-accession
        if (options.has("workflow-accession")) {
            model.setWorkflow_accession((String) options.valueOf("workflow-accession"));
        }
        //merge "workflow-run-accession"
        if (options.has("workflow-run-accession")) {
            model.setWorkflow_run_accession((String) options.valueOf("workflow-run-accession"));
        }
        //merge schedule
        if (options.has("schedule")) {
            map.put("schedule", "true");
        }
        //merge bundle
        if (options.has("bundle")) {
            map.put("bundle", (String) options.valueOf("bundle"));
        }
        //bundle "provisioned-bundle-dir"
        if (options.has("provisioned-bundle-dir")) {
            map.put("provisioned-bundle-dir", (String) options.valueOf("provisioned-bundle-dir"));
        }
        //launch-scheduled
        if (options.has("launch-scheduled")) {
            List<String> scheduledAccessions = (List<String>) options
                    .valuesOf("launch-scheduled");
            map.put("launch-scheduled", org.apache.commons.lang.StringUtils.join(scheduledAccessions, ","));
        }
        //host
        if (options.has("host")) {
            map.put("host", (String) options.valueOf("host"));
        }
        //metadatawriteback
        boolean metadataWriteback = true;
        if (options.has("no-metadata") || options.has("no-meta-db") || options.has("status")) {
            metadataWriteback = false;
        }
        map.put("metadata", Boolean.toString(metadataWriteback));
        model.setMetadataWriteBack(metadataWriteback);
        //metadata-output-file-prefix
        if (options.has("metadata-output-file-prefix")) {
            model.setMetadata_output_file_prefix((String) options.valueOf("metadata-output-file-prefix"));
        }
        //metadata-output-dir
        if (options.has("metadata-output-dir")) {
            model.setMetadata_output_dir((String) options.valueOf("metadata-output-dir"));
        }
        //workflow_engine
        if (options.has("workflow-engine")) {
            model.setWorkflow_engine((String) options.valueOf("workflow-engine"));
        }
    }

}
