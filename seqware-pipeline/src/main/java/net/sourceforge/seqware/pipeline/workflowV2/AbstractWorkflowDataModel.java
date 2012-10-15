package net.sourceforge.seqware.pipeline.workflowV2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.pipeline.workflowV2.model.Environment;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow;

public abstract class AbstractWorkflowDataModel  {
    private Workflow workflow;
    private String name;
    private String version;
    private Environment env;
    private WorkflowInfo workflowInfo;
    private Map<String,String> tags;
    protected Map<String,String> configs;
    private boolean wait;
    private boolean metadataWriteBack;
    private Map<String,SqwFile> files;
	/**
	 * 	extra params, these will be passed directly to the Java Object/FTL, 
	 *  so you can use this to override key/values from the ini files
	 *  very useful if you're calling the workflow from another system
	 *  and want to pass in arguments on the command line rather than ini file
	 * @return
	 */
    private Collection<String> cmdOptions;
    private String random;
    private String date;

    public AbstractWorkflowDataModel() {
    	this.env = new Environment();
    	this.files = new HashMap<String, SqwFile>();
    	this.setTags(new HashMap<String,String>());
    	this.configs = new HashMap<String,String>();
    	this.workflow = new Workflow();
    }
    
    public void setupDirectory() {
    	
    }
    
    public void setupWorkflow() {
    	
    }
    public void setupEnvironment() {
    	
    }
    public Map<String, SqwFile> setupFiles() {
    	return this.files;
    }
    public abstract void buildWorkflow();
    public void wrapup() {
    	
    }

    protected Workflow setWorkflowObjectModel(Workflow wfom) {
    	this.workflow = wfom;
    	return this.workflow;
    }
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getRandom() {
		return random;
	}
	public void setRandom(String random) {
		this.random = random;
	}
	/**
	 * 
	 * @return the workflow bundle Dir
	 */
	public String getWorkflowBundleDir() {
		return this.workflowInfo == null? null:this.workflowInfo.getWorkflowDir();
	}

	/**
	 * 
	 * @return current workflow name
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Collection<String> getCmdOptions() {
		return cmdOptions;
	}
	public void setCmdOptions(Collection<String> cmdOptions) {
		this.cmdOptions = cmdOptions;
	}
	public WorkflowInfo getWorkflowInfo() {
		return workflowInfo;
	}
	public void setWorkflowInfo(WorkflowInfo workflowInfo) {
		this.workflowInfo = workflowInfo;
	}
	
	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}
	/**
	 * 
	 * @return pre-defined workflow object;
	 */
	public Workflow getWorkflow() {
		return this.workflow;
	}
	public boolean isMetadataWriteBack() {
		return metadataWriteBack;
	}
	public void setMetadataWriteBack(boolean b) {
		this.metadataWriteBack = b;
	}
	
	public Map<String,String> getConfigs() {
		return this.configs;
	}
	
	public void setConfigs(Map<String,String> configs) {
		this.configs = configs;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Map<String,SqwFile> getFiles() {
		return files;
	}

	public boolean isWait() {
		return wait;
	}

	public void setWait(boolean wait) {
		this.wait = wait;
	}

	/**
	 * @return ${workflow_bundle_dir}/Workflow_Bundle_${workflow-directory-name}/${version}
	 */
	public String getWorkflowBaseDir() {
		return "";
	}

	public Map<String,String> getTags() {
		return tags;
	}

	public void setTags(Map<String,String> tags) {
		this.tags = tags;
	}

	public Environment getEnv() {
		return env;
	}


	
}