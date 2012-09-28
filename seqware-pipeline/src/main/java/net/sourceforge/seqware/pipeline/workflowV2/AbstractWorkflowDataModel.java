package net.sourceforge.seqware.pipeline.workflowV2;

import java.util.Collection;
import java.util.Map;

import net.sourceforge.seqware.common.metadata.Metadata;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.pipeline.workflowV2.model.Environment;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow;

public abstract class AbstractWorkflowDataModel  {
    protected Workflow workflow;
    private String name;
    protected String version;
    protected Environment env;
    private WorkflowInfo workflowInfo;
    protected Map<String,String> configs;
    private Metadata metadata;
    protected boolean wait;
    protected boolean metadataWriteBack;
    protected Collection<SqwFile> files;
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

    protected void setupWorkflow() {
    	
    }
    protected void setupEnvironment() {
    	
    }
    protected void setupFiles() {
    	
    }
    protected abstract void buildWorkflow();
    protected void wrapup() {
    	
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
}