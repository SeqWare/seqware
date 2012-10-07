package net.sourceforge.seqware.pipeline.workflowV2;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import joptsimple.OptionSet;

import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.maptools.MapTools;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.pipeline.bundle.Bundle;
import net.sourceforge.seqware.pipeline.bundle.BundleInfo;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.StringUtils;
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow;
import net.sourceforge.seqware.pipeline.workflowV2.model.XmlWorkflowDataModel;

public class WorkflowDataModelFactory {
	private Map<String, String> config;
	private Metadata metadata;
	private OptionSet options;
	private String[] params;
	
	public WorkflowDataModelFactory(Metadata metadata, OptionSet options, Map<String,String> config, String[] params) {
		this.metadata = metadata;
		this.options = options;
		this.config = config;	
		this.params = params;
	}
	
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
		if(workflow_java) {
			String clazzPath = "";
			Log.info("CLASSPATH: " + clazzPath);
	    	// get user defined classes
	    	WorkflowClassFinder finder = new WorkflowClassFinder();
	    	Class<?> clazz = finder.findFirstWorkflowClass(clazzPath);
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
		ret.setVersion(metaInfo.get("version"));
		//set memory, network, compute to environment
		ret.getEnv().setCompute(metaInfo.get("compute"));
		ret.getEnv().setNetwork(metaInfo.get("network"));
		ret.getEnv().setMemory(metaInfo.get("memory"));
		
		//load ini config
		Map<String, String> configs = this.loadIniConfigs();
		configs.put("workflow_bundle_dir", bundlePath);
		configs.put("workflow_name", ret.getName());
        String basedir = bundlePath + File.separator + "Workflow_Bundle_"+ret.getName()+ File.separator + ret.getVersion();
		configs.put("basedir", basedir);	
		
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
		ret.setConfigs(configs);	
		
		//parse XML or Java Object for
		if(workflow_java) {
			
		} else {
			WorkflowXmlParser xmlParser = new WorkflowXmlParser();
			xmlParser.parseXml(ret, metaInfo.get("workflow_template"));
		}
		return ret;
	}
	
	private void loadMetadataInfo(AbstractWorkflowDataModel datamodel) {
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
	        return;
	    }


		String metadataFile = (String) options.valueOf("metadata");
		File metadataFileObj = null;
		if (metadataFile != null) {
		    metadataFileObj = new File(metadataFile);
		}
		
		if (metadataFileObj == null) {
			//search for the metadata.xml
			metadataFile = bundlePath + File.separator + "workflow" + File.separator + "metadata.xml";
		}
		//parse metadataFile
		SAXBuilder builder = new SAXBuilder();
		try {
		    Document document = (Document) builder.build(metadataFile);
		    Element root = document.getRootElement();
		    
		} catch (JDOMException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}		
		
	}
	
    private AbstractWorkflowDataModel loadDataModel() {
    	AbstractWorkflowDataModel res = null;
    	//parse metadata.xml
    	WorkflowInfo wfi = this.setupWorkflowInfo();
    	String clazzPath = wfi.getClassesDir();
    	clazzPath = clazzPath.replaceFirst("\\$\\{workflow_bundle_dir\\}",
    			wfi.getWorkflowDir());
    	Log.info("CLASSPATH: " + clazzPath);
    	// get user defined classes
    	WorkflowClassFinder finder = new WorkflowClassFinder();
    	Class<?> clazz = finder.findFirstWorkflowClass(clazzPath);
    	if (null != clazz) {
    	    Log.debug("using java object");
    	    try {
	    		Object object = clazz.newInstance();
	    		res = (AbstractWorkflowDataModel) object;
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
    	if(res == null) {
    		Log.debug("using FTL");
    		String ftlPath = wfi.getTemplatePath();
    		WorkflowXmlParser xmlparser = new WorkflowXmlParser();
    		return null;
    	}
    	//set command line options
    	res.setCmdOptions(new ArrayList<String>(Arrays.asList(this.params)));
    	//set workflowInfo
    	res.setWorkflowInfo(wfi);

    	//set Workflow
    	Workflow workflow = new Workflow();
    	res.setWorkflow(workflow);
		// figure out what was passed as params and make structs to pass to the
		// workflow layer
		// metadata
		boolean metadataWriteback = true;
		if (options.has("no-metadata") || options.has("no-meta-db")) {
		    metadataWriteback = false;
		}
		//this.setPrivateField(res, field, value);
		res.setMetadataWriteBack(metadataWriteback);
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
		
		map.put("workflow_bundle_dir",res.getWorkflowBundleDir());
      
		//set name
        res.setName(map.get("workflow"));
        //set version
        res.setVersion(map.get("version"));
		//set metadata
        //set base dir
        String basedir = res.getWorkflowBundleDir() + File.separator + "Workflow_Bundle_"+res.getName()+ File.separator + res.getVersion();
		map.put("basedir", basedir);
		// Expand variables in the map
        MapTools.mapExpandVariables(map);
        
        this.addExtraConfigs(map);
        Map<String,String> newMap = this.resolveMap(map);
        res.setConfigs(newMap);
        //call the tmplate methods
        try {
        	Method m = clazz.getDeclaredMethod("setupFiles");
        	m.invoke(res);
/*        	m = clazz.getDeclaredMethod("setupWorkflow");
        	m.invoke(res);
        	m = clazz.getDeclaredMethod("setupEnvironment");
        	m.invoke(res);*/
			m = clazz.getDeclaredMethod("buildWorkflow");
			m.invoke(res);
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

        res.setWait(options.has("wait"));
    	return res;
    }

    private void addExtraConfigs(Map<String,String> map) {
        //set date
        // magic variables always set
        Date date = new Date();
        map.put("date", date.toString());

        //set random
        Random rand = new Random(System.currentTimeMillis());
        int randInt = rand.nextInt(100000000);
        map.put("random", ""+randInt);
        //copy some properties from .settings to configs
        map.put("SW_PEGASUS_CONFIG_DIR", config.get("SW_PEGASUS_CONFIG_DIR"));
        map.put("SW_DAX_DIR", config.get("SW_DAX_DIR"));
        map.put("SW_CLUSTER", config.get("SW_CLUSTER"));
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

    private WorkflowInfo setupWorkflowInfo() {
    	if ((options.has("bundle") || options
    			.has("provisioned-bundle-dir"))
    			&& options.has("workflow")
    			&& options.has("version") && options.has("ini-files")) {
		    // then your launching direclty and not something that has been
		    // installed
		    Log.info("FYI: You are running the workflow without metadata writeback since you are running directly from a bundle zip file or directory.");
		    // then run the workflow specified
		    String bundlePath = "";
		    if (options.has("bundle")) {
		    	bundlePath = (String) options.valueOf("bundle");
		    } else {
		    	bundlePath = (String) options.valueOf("provisioned-bundle-dir");
		    }
		    Log.info("Bundle Path: " + bundlePath);

		    String workflow = (String) options.valueOf("workflow");
		    String version = (String) options.valueOf("version");
    		String metadataFile = (String) options.valueOf("metadata");
    		File metadataFileObj = null;
    		if (metadataFile != null) {
    		    metadataFileObj = new File(metadataFile);
    		}
    		// pull back information from metadata
    		Bundle bundleUtil = new Bundle(metadata, config);
    		Log.info("Bundle: " + bundlePath);
    		BundleInfo bundleInfo = bundleUtil.getBundleInfo(new File(bundlePath),
    			metadataFileObj);
    		for (WorkflowInfo wi : bundleInfo.getWorkflowInfo()) {

    		    Log.info("Workflow: " + wi.getName() + " Version: "
    			    + wi.getVersion());

    		    if (wi.getName().equals(workflow)
    			    && wi.getVersion().equals(version)) {

	    			Log.info("Match!");
	    			// then this is the workflow we need to run
	    			String bundleoutPath = bundleUtil.getOutputDir();	
	    			wi.setWorkflowDir(bundleoutPath);	
	    			return wi;
	    		 }
    		}
    	}
    	return null;
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

}
