package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.List;
import java.util.Map;

import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;

public class WorkflowObjectModel {
	private String name;
	private String version;
	private boolean metadataWriteback;
	private boolean wait;
	private String accession;
	private String runAccession;
	private String workflowBundleDir;
	
	private WorkflowInfo workflowInfo;
	private Environment env;
	
	private List<String> cmdOptions;
	private Map<String,String> iniConfigs;
	private List<String> parentAccessions;
	private List<String> parentsLinkedToWR;
	//may be better to use Map
	private List<Job1> jobs;
	
	/**
	 * 
	 * @return current workflow name
	 */
	public String getName() {
		return name;
	}
	/**
	 * set workflow name
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 
	 * @return current workflow version
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
	 * 	extra params, these will be passed directly to the Java Object/FTL, 
	 *  so you can use this to override key/values from the ini files
	 *  very useful if you're calling the workflow from another system
	 *  and want to pass in arguments on the command line rather than ini file
	 * @return
	 */
	public List<String> getCmdOptions() {
		return cmdOptions;
	}
	/**
	 * set the command line options
	 * @param cmdOptions
	 */
	public void setCmdOptions(List<String> cmdOptions) {
		this.cmdOptions = cmdOptions;
	}
	/**
	 * has metadata write back for the workflow
	 * @return
	 */
	public boolean isMetadataWriteback() {
		return metadataWriteback;
	}
	/**
	 * set metadata writeback, if true, the metadata will be stored in database
	 * @param metadataWriteback
	 */
	public void setMetadataWriteback(boolean metadataWriteback) {
		this.metadataWriteback = metadataWriteback;
	}
	/**
	 * get all jobs in current workflow
	 * @return
	 */
	public List<Job1> getJobs() {
		return jobs;
	}

	/**
	 * add job to the workflow
	 * @param job
	 */
	public void addJobs(Job1 job) {
		this.jobs.add(job);
	}
	
	/**
	 * get link-workflow-run-to-parents
	 * @return
	 */
	public List<String> getParentsLinkedToWR() {
		return parentsLinkedToWR;
	}
	/**
	 * set link-workflow-run-to-parents
	 * @param parentsLinkedToWR
	 */
	public void setParentsLinkedToWR(List<String> parentsLinkedToWR) {
		this.parentsLinkedToWR = parentsLinkedToWR;
	}
	/**
	 * get parent accessions
	 * @return
	 */
	public List<String> getParentAccessions() {
		return parentAccessions;
	}
	/**
	 * set parent accessions
	 * @param parentAccessions
	 */
	public void setParentAccessions(List<String> parentAccessions) {
		this.parentAccessions = parentAccessions;
	}
	/**
	 * 
	 * @return the workflow environment
	 */
	public Environment getEnv() {
		return env;
	}
	/**
	 * set the workflow environment
	 * @param env
	 */
	public void setEnv(Environment env) {
		this.env = env;
	}
	/**
	 * get the workflowInfo, which contains the information from metadata.xml
	 * @return
	 */
	public WorkflowInfo getWorkflowInfo() {
		return workflowInfo;
	}
	/**
	 * set the workflowInfo from metadata.xml
	 * @param workflowInfo
	 */
	public void setWorkflowInfo(WorkflowInfo workflowInfo) {
		this.workflowInfo = workflowInfo;
	}
	/**
	 * 
	 * @return the workflowbundle diretory
	 */
	public String getWorkflowBundleDir() {
		return workflowBundleDir;
	}
	/**
	 * set the workflowBundle directory
	 * @param workflowBundleDir
	 */
	public void setWorkflowBundleDir(String workflowBundleDir) {
		this.workflowBundleDir = workflowBundleDir;
	}
	/**
	 * 
	 * @return workflow run accession id
	 */
	public String getRunAccession() {
		return runAccession;
	}
	/**
	 * set workflow run accession id
	 * @param runAccession
	 */
	public void setRunAccession(String runAccession) {
		this.runAccession = runAccession;
	}
	/**
	 * get workflow accession id
	 * @return
	 */
	public String getAccession() {
		return accession;
	}
	/**
	 * set workflow accession id
	 * @param accession
	 */
	public void setAccession(String accession) {
		this.accession = accession;
	}
	/**
	 * is waiting for the workflow to finish
	 * @return
	 */
	public boolean isWait() {
		return wait;
	}
	/**
	 * if true, the program will wait for the workflow to finish
	 * @param wait
	 */
	public void setWait(boolean wait) {
		this.wait = wait;
	}
	/**
	 * get the key value settings from all INI files
	 * @return
	 */
	public Map<String,String> getIniConfigs() {
		return iniConfigs;
	}
	/**
	 * set the key value settings from all INI files
	 * @param iniConfigs
	 */
	public void setIniConfigs(Map<String,String> iniConfigs) {
		this.iniConfigs = iniConfigs;
	}
	
}