package net.sourceforge.seqware.pipeline.workflowV2;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private String workflowBundleDir;
    private Map<String,String> tags;
    protected Map<String,String> configs;
    private boolean wait;
    private boolean metadataWriteBack;
    private Map<String,SqwFile> files;
    private Collection<String> dirs;
    private Collection<String> parentAccessions;
    private String workflow_accession;
    private String workflow_run_accession;
    private String random;
    private String date;
    private String metadata_output_file_prefix;
    private String metadata_output_dir;
    //default is pegasus
    private String workflow_engine;

    public AbstractWorkflowDataModel() {
    	this.env = new Environment();
    	this.files = new LinkedHashMap<String, SqwFile>();
    	this.setTags(new HashMap<String,String>());
    	this.configs = new HashMap<String,String>();
    	this.workflow = new Workflow();
    	this.dirs = new ArrayList<String>();
    	this.parentAccessions = new ArrayList<String>();
    }
    
    /**
     * to be Overridden by the workflow author
     */
    public void setupDirectory() {
    	
    }
    
    /**
     * to be Overridden by the workflow author
     * you generally don't call this as the workflow author since it promotes hardcoding
     * this will typically be filled in by maven
     */
    public void setupWorkflow() {
    	
    }
    /**
     * to be Overridden by the workflow author
     * generally people don't override this. 
     */
    public void setupEnvironment() {
    	
    }
    /**
     * to be Overridden by the workflow author
     * the place to specify inputs and outputs to the program
     * also, a user can specify inputs and outputs on jobs directly in which 
     * case they are still provisioned properly with respect to the job (inputs before, outputs after)
     * when you define inputs/outputs here they are provisioned before all jobs
     * and after all jobs respectively
     */
    public Map<String, SqwFile> setupFiles() {
    	return this.files;
    }
    /**
     * to be Overridden by the workflow author
     */
    public abstract void buildWorkflow();
    /**
     * to be Overridden by the workflow author
     */
    public void wrapup() {
    	
    }

    /**
     * 
     * @return pre-defined date variable
     */
	public String getDate() {
		return date;
	}
	/**
	 * set the pre-defined date variable
	 * @param date
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * 
	 * @return the pre-defined random variable
	 */
	public String getRandom() {
		return random;
	}
	/**
	 * set the pre-defined random variable
	 * @param random
	 */
	public void setRandom(String random) {
		this.random = random;
	}
	/**
	 * 
	 * @return the workflow bundle Dir
	 */
	public String getWorkflowBundleDir() {
		return this.workflowBundleDir;
	}
	
	public void setWorkflowBundleDir(String dir) {
		this.workflowBundleDir = dir;
	}

	/**
	 * 
	 * @return current workflow name
	 */
	public String getName() {
		return name;
	}
	/**
	 * set the workflow name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * 
	 * @return pre-defined workflow object;
	 */
	public Workflow getWorkflow() {
		return this.workflow;
	}
	
	/**
	 * need metadata writeback? user can override this setting by using --no-metadata or --metadata from command line
	 * @return
	 */
	public boolean isMetadataWriteBack() {
		return metadataWriteBack;
	}
	/**
	 * need metadata writeback? user can override this setting by using --no-metadata or --metadata from command line
	 * @return
	 */	
	public void setMetadataWriteBack(boolean b) {
		this.metadataWriteBack = b;
	}
	
	/**
	 * 
	 * @return the key-value properties from INI files
	 */
	public Map<String,String> getConfigs() {
		return this.configs;
	}
	/**
	 * set the key-value properties for workflow
	 * @param configs
	 */
	public void setConfigs(Map<String,String> configs) {
		this.configs = configs;
	}
	/**
	 * 
	 * @return workflow version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * set workflow version
	 * @param version
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	/**
	 * 
	 * @return user defined files
	 */
	public Map<String,SqwFile> getFiles() {
		return files;
	}

	/**
	 * wait for the workflow to finished, user can override this by using --wait from command line
	 * @return
	 */
	public boolean isWait() {
		return wait;
	}

	/**
	 * wait for the workflow to finished, user can override this by using --wait from command line
	 * @param wait
	 */
	public void setWait(boolean wait) {
		this.wait = wait;
	}

	/**
	 * @return ${workflow_bundle_dir}/Workflow_Bundle_${workflow-directory-name}/${version}
	 */
	public String getWorkflowBaseDir() {
		return this.getWorkflowBundleDir() + File.separator + "Workflow_Bundle_"+this.getName()+ File.separator + this.getVersion();
	}

	/**
	 * the key-value from metadata.xml
	 * @return
	 */
	public Map<String,String> getTags() {
		return tags;
	}

	/**
	 * set the key-value properties from metadata.xml
	 * @param tags
	 */
	public void setTags(Map<String,String> tags) {
		this.tags = tags;
	}

	/**
	 * 
	 * @return workflow environment
	 */
	public Environment getEnv() {
		return env;
	}
	/**
	 * create a user defined directory before all jobs started
	 * @param name:  directory name
	 * @return 
	 */
	public void addDirectory(String name) {
		this.dirs.add(name);
	}
	/**
	 * 
	 * @return user defined directories
	 */
	public Collection<String> getDirectories() {
		return this.dirs;
	}
	
	/**
	 * create a sqwfile
	 * @param name
	 * @return the created sqwfile
	 */
	public SqwFile createFile(String name) {
		SqwFile file = new SqwFile();
		this.files.put(name, file);
		return file;		
	}

	/**
	 * 
	 * @return parent_accessions separated by ","
	 */
	public Collection<String> getParentAccessions() {
		return this.parentAccessions;
	}

	/**
	 * 
	 * @param parent_accessions parent_accessions separated by ","
	 */
	void setParentAccessions(Collection<String> parentAccessions) {
		this.parentAccessions.addAll(parentAccessions);
	}

	/**
	 * 
	 * @return a workflow_accession number
	 */
	public String getWorkflow_accession() {
		return workflow_accession;
	}

	/**
	 * set workflow_accession 
	 * @param workflow_accession 
	 */
	void setWorkflow_accession(String workflow_accession) {
		this.workflow_accession = workflow_accession;
	}

	/**
	 * 
	 * @return workflow run accession number
	 */
	public String getWorkflow_run_accession() {
		return workflow_run_accession;
	}

	/**
	 * set workflow run accession
	 * @param workflow_run_accession
	 */
	void setWorkflow_run_accession(String workflow_run_accession) {
		this.workflow_run_accession = workflow_run_accession;
	}

	/**
	 * 
	 * @return output file prefix
	 */
	public String getMetadata_output_file_prefix() {
		return metadata_output_file_prefix;
	}

	/**
	 * set output file prefix, used by provisionfiles output
	 * @param metadata_output_file_prefix
	 */
	void setMetadata_output_file_prefix(
			String metadata_output_file_prefix) {
		this.metadata_output_file_prefix = metadata_output_file_prefix;
	}

	public String getMetadata_output_dir() {
		return metadata_output_dir;
	}

	void setMetadata_output_dir(String metadata_output_dir) {
		this.metadata_output_dir = metadata_output_dir;
	}

	/**
	 * 
	 * @return engine that workflow will run on, default is pegasus
	 */
	public String getWorkflow_engine() {
		return workflow_engine;
	}

	/**
	 * default is pegasus
	 * @param workflow_engine
	 */
	public void setWorkflow_engine(String workflow_engine) {
		this.workflow_engine = workflow_engine;
	}
	
	/**
	 * get the user defined INI files properties
	 * @param key
	 * @return value of the key.
	 * @throws Exception 
	 */
	public String getProperty(String key) throws Exception {
		if(!this.configs.containsKey(key))
			throw new Exception("Key not found");
		return configs.get(key);
	}
	
}