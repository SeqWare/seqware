package net.sourceforge.seqware.pipeline.workflowV2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import joptsimple.OptionSet;
import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.maptools.MapTools;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.StringUtils;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;
import net.sourceforge.seqware.pipeline.workflowV2.model.XmlWorkflowDataModel;

/**
 * a utils class for creating the AbstractWorkflowDataModel,
 * by reading the metadata.xml file, will load a Java based objectModel or XML based ObjectModel
 * @author yliang
 *
 */
public class WorkflowDataModelFactory {
	private Map<String, String> config;
	private OptionSet options;
	private String[] params;
	private Metadata metadata;
	
	public WorkflowDataModelFactory(OptionSet options, Map<String,String> config, String[] params, Metadata metadata) {
		this.options = options;
		this.config = config;	
		this.params = params;
		this.metadata = metadata;
	}
	
	/**
	 * load metadata.xml, if FTL, parse the FTL to XML, and translate it to Java based Object
	 * if Java, load the class. 
	 * @return
	 */
	public AbstractWorkflowDataModel getWorkflowDataModel() {	
		// get bundle path
	    String bundlePath = "";
	    if (options.has("bundle")) {
	    	bundlePath = (String) options.valueOf("bundle");
	    } else {
	    	bundlePath = (String) options.valueOf("provisioned-bundle-dir");
	    }
	    File bundle = new File(bundlePath);
	    Log.info("Bundle Path: " + bundlePath);
	    if (bundle == null || !bundle.exists()) {
	        Log.error("ERROR: Bundle is null or doesn't exist! The bundle must be either a zip file or a directory structure.");
	        return null;
	    }
	    //parset metadata.xml to Map<String,String>
	    @SuppressWarnings("unchecked") //safe to use <File>
		Iterator<File> it = FileUtils.iterateFiles(bundle, new String[]{"xml"}, true);
	    if(!it.hasNext())
	    	return null;
	    
	    File metadataFile = null;
	    while(it.hasNext()) {
	    	File file = it.next();
	    	if(file.getName().equals("metadata.xml")) {
	    		metadataFile = file;
	    		break;
	    	}
	    }
	    
	    if(metadataFile == null)
	    	return null;
	    Map<String,String> metaInfo = this.parseMetadataInfo(metadataFile, bundlePath);	    
	    //check FTL exist?
		boolean workflow_java = true;
	    if(metaInfo.get("workflow_template") != null && !metaInfo.get("workflow_template").toString().isEmpty())
	    	workflow_java = false;
	    

		//Java object or FTL
		AbstractWorkflowDataModel ret = null;
		Class<?> clazz = null;
		if(workflow_java) {
			String clazzPath = metaInfo.get("classes");
			Log.info("CLASSPATH: " + clazzPath);
	    	// get user defined classes
	    	WorkflowClassFinder finder = new WorkflowClassFinder();
	    	clazz = finder.findFirstWorkflowClass(clazzPath);
	    	if (null != clazz) {
	    	    Log.debug("using java object");
	    	    try {
		    		Object object = clazz.newInstance();
		    		ret = (AbstractWorkflowDataModel) object;
	    	    } catch (InstantiationException ex) {
	    	    	Log.error(ex);
	    	    } catch (IllegalAccessException ex) {
	    	    	Log.error(ex);
	    	    }  catch (SecurityException ex) {
	    	    	Log.error(ex);
	    	    } catch (IllegalArgumentException ex) {
	    	    	Log.error(ex);
	    	    } 
	    	}
		} else {
			ret = new XmlWorkflowDataModel();
		}
		//load metadata.xml
		ret.setTags(metaInfo);
		//set name, version in workflow
		ret.setName(metaInfo.get("name"));
		ret.setVersion(metaInfo.get("workflow_version"));
		ret.setWorkflowBundleDir(bundlePath);
		//set memory, network, compute to environment
		ret.getEnv().setCompute(metaInfo.get("compute"));
		ret.getEnv().setNetwork(metaInfo.get("network"));
		ret.getEnv().setMemory(metaInfo.get("memory"));
		
		//load ini config
		Map<String, String> configs = this.loadIniConfigs();
		configs.put("workflow_bundle_dir", bundlePath);
		configs.put("workflow_name", ret.getName());
        //String basedir = bundlePath + File.separator + "Workflow_Bundle_"+ret.getName()+ File.separator + ret.getVersion();
		//configs.put("basedir", basedir);	
		//Log.error("basedir " + basedir);
		
		//merge command line option with configs
		this.mergeCmdOptions(ret);
		//merge version, and name ??? TODO 
		
		//set random, date, wait
        // magic variables always set
        Date date = new Date();
        ret.setDate(date.toString());

        //set random
        Random rand = new Random(System.currentTimeMillis());
        int randInt = rand.nextInt(100000000);
        ret.setRandom(""+randInt);
        //copy some properties from .settings to configs
        ret.getEnv().setPegasusConfigDir(config.get("SW_PEGASUS_CONFIG_DIR"));
        ret.getEnv().setDaxDir(config.get("SW_DAX_DIR"));
        ret.getEnv().setSwCluster(config.get("SW_CLUSTER"));
        
        //get workflow-run-accession
        if(options.has(("workflow-accession"))) {
        	int workflowAccession = Integer.parseInt((String)options.valueOf("workflow-accession"));
        	int workflowrunaccession = this.metadata.add_workflow_run(workflowAccession);
        	configs.put("workflow-run-accession", ""+workflowrunaccession);
        }
		ret.setConfigs(configs);
		
		//parse XML or Java Object for
		if(workflow_java) {
	        try {
	        	Method m = null;
	        	m = clazz.getMethod("setupDirectory");
	        	m.invoke(ret);
	        	m = clazz.getMethod("setupFiles");
	        	m.invoke(ret);
	        	//handle the provisionedPath
	        	//this.setupProvisionedPath(ret.getFiles());
	        	m = clazz.getMethod("setupWorkflow");
	        	m.invoke(ret);
	        	m = clazz.getMethod("setupEnvironment");
	        	m.invoke(ret);
	        	m = clazz.getMethod("buildWorkflow");
	        	m.invoke(ret);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}			
		} else {
			WorkflowXmlParser xmlParser = new WorkflowXmlParser();
			xmlParser.parseXml(ret, metaInfo.get("workflow_template"));
		}
		//set wait
		ret.setWait(this.options.has("wait"));
		return ret;
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

   
    private Map<String, String> parseMetadataInfo(File file, String bundleDir) {
    	Map<String,String> ret = new HashMap<String,String>();
		//parse metadataFile
		SAXBuilder builder = new SAXBuilder();
		try {
		    Document document = (Document) builder.build(file);
		    Element root = document.getRootElement();
		    ret.put("bundle_version", root.getAttributeValue("version"));
		    Element wf = root.getChild("workflow");
		    ret.put("name", wf.getAttributeValue("name"));
		    ret.put("workflow_version", wf.getAttributeValue("version"));
		    ret.put("seqware_version", wf.getAttributeValue("seqware_version"));
		    ret.put("description", wf.getChildText("description"));
		    Element command = wf.getChild("workflow_command");
		    if(command != null)
		    	ret.put("workflow_command", command.getAttributeValue("command").replaceFirst("\\$\\{workflow_bundle_dir\\}",bundleDir));
		    Element template = wf.getChild("workflow_template");
		    if(template != null)
		    	ret.put("workflow_template", template.getAttributeValue("path").replaceFirst("\\$\\{workflow_bundle_dir\\}",bundleDir));
		    Element config = wf.getChild("config");
		    if(config != null)
		    	ret.put("config", config.getAttributeValue("path").replaceFirst("\\$\\{workflow_bundle_dir\\}",bundleDir));
		    Element classes = wf.getChild("classes");
		    if(classes != null)
		    	ret.put("classes", classes.getAttributeValue("path").replaceFirst("\\$\\{workflow_bundle_dir\\}",bundleDir));
		    Element build = wf.getChild("build");
		    if(build != null)
		    	ret.put("build", build.getAttributeValue("command").replaceFirst("\\$\\{workflow_bundle_dir\\}",bundleDir));
		    Element requirements = wf.getChild("requirements");
		    if(requirements != null) {
		    	ret.put("compute", requirements.getAttributeValue("compute"));
		    	ret.put("memory", requirements.getAttributeValue("memory"));
		    	ret.put("network", requirements.getAttributeValue("network"));
		    }
		} catch (JDOMException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}		
    	return ret;
    }
    
    private Map<String, String> loadIniConfigs() {
    	Map<String, String> ret = new HashMap<String,String>();
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
		Map<String,String> map = new HashMap<String,String>();
		for(String ini: iniFiles) {
			Log.stdout("  INI FILE: "+ini);
            if ((new File(ini)).exists()) {
            	MapTools.ini2Map(ini, map);
            }
		}
		// allow the command line options to override options in the map
        // Parse command line options for additional configuration. Note that we
        // do it last so it takes precedence over the INI
		MapTools.cli2Map(this.params, map);
		MapTools.mapExpandVariables(map);
        
        ret = this.resolveMap(map);
    	return ret;
    }
    
    //FIXME should iterate all options automatically
    private void mergeCmdOptions(AbstractWorkflowDataModel model) {
    	Map<String,String> map = model.getConfigs();
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
    		//map.put("parent-accessions", org.apache.commons.lang.StringUtils.join(parentAccessions,","));
    		model.setParent_accessions(org.apache.commons.lang.StringUtils.join(parentAccessions,","));
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
    	if(options.has("workflow-accession")) {
    		//map.put("workflow-accession", (String) options.valueOf("workflow-accession"));
    		model.setWorkflow_accession((String) options.valueOf("workflow-accession"));
    	}
    	//merge "workflow-run-accession"
    	if(options.has("workflow-run-accession")) {
    		//map.put("workflow-run-accession", (String) options.valueOf("workflow-run-accession"));
    		model.setWorkflow_run_accession((String) options.valueOf("workflow-run-accession"));
    	}
    	//merge schedule
    	if(options.has("schedule")) {
    		map.put("schedule", "true");
    	}
    	//merge bundle
    	if(options.has("bundle")) {
    		map.put("bundle", (String) options.valueOf("bundle"));
    	}
    	//bundle "provisioned-bundle-dir"
    	if(options.has("provisioned-bundle-dir")) {
    		map.put("provisioned-bundle-dir", (String) options.valueOf("provisioned-bundle-dir"));
    	}
    	//launch-scheduled
    	if (options.has("launch-scheduled")) {
    		 List<String> scheduledAccessions = (List<String>) options
    				    .valuesOf("launch-scheduled");
    		 map.put("launch-scheduled", org.apache.commons.lang.StringUtils.join(scheduledAccessions,","));
    	}
    	//host
    	if(options.has("host")) {
    		map.put("host", (String) options.valueOf("host"));
    	}
    	//metadatawriteback
    	boolean metadataWriteback = true;
    	if (options.has("no-metadata") || options.has("no-meta-db")) {
    	    metadataWriteback = false;
    	}
    	map.put("metadata", Boolean.toString(metadataWriteback));
    	//metadata-output-file-prefix
    	if (options.has("metadata-output-file-prefix")) {
//    		map.put("metadata-output-file-prefix", (String) options.valueOf("metadata-output-file-prefix"));
    		model.setMetadata_output_file_prefix((String) options.valueOf("metadata-output-file-prefix"));
    	}
    	//metadata-output-dir
    	if (options.has("metadata-output-dir")) {
//    		map.put("metadata-output-dir", (String) options.valueOf("metadata-output-dir"));
    		model.setMetadata_output_dir((String) options.valueOf("metadata-output-dir"));
    	}
    }

/*    private void setupProvisionedPath(Map<String,SqwFile> map) {
    	int inCount = 0;
    	for(Map.Entry<String, SqwFile> entry: map.entrySet()) {
    		if(entry.getValue().isInput()) {
    			SqwFile file = entry.getValue();
    			String fileName = FilenameUtils.getName(file.getSourcePath());
    			entry.getValue().setProvisionedPath("provisionfiles/"+inCount+"/"+fileName);
    			inCount ++;
    		}
    	}
    }*/
}
