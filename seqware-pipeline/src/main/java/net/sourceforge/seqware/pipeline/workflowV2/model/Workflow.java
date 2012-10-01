package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.List;


public class Workflow {

	private String accession;
	private String runAccession;
	private String workflowBundleDir;
	
	private List<String> parentAccessions;
	private List<String> parentsLinkedToWR;
	//may be better to use Map
	private List<Job1> jobs;
	

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
	
	public Job createJavaJob() {
		return null;
	}
	
	public Job createBashJob() {
		return null;
	}
	
	public Job createPerlJob() {
		return null;
	}
	
	public Job createSqwJob_GenericCommandRunner() {
		
		return null;
	}

	
}