package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.ArrayList;
import java.util.List;


public class Workflow {

	private String accession;
	private String runAccession;
	private String workflowBundleDir;
	
	private List<String> parentAccessions;
	private List<String> parentsLinkedToWR;
	//may be better to use Map
	private List<Job> jobs;
	

	public Workflow() {
		this.jobs = new ArrayList<Job>();
	}
	
	/**
	 * get all jobs in current workflow
	 * @return
	 */
	public List<Job> getJobs() {
		return jobs;
	}

	/**
	 * add job to the workflow
	 * @param job
	 */
	public void addJobs(Job job) {
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
	
	public Job createJavaJob(String cp, String mainclass) {
		Job job = new Job();
		job.setModule(Module.Java);
		job.setAlgo("java");
		this.jobs.add(job);
		return job;
	}
	
	public Job createBashJob(String algo) {
		Job job = new Job();
		job.setModule(Module.Bash);
		job.setAlgo(algo);
		this.jobs.add(job);
		return job;
	}
	
	public Job createPerlJob() {
		Job job = new Job();
		job.setModule(Module.Java);
		job.setAlgo("java");
		this.jobs.add(job);
		return job;
	}

	
}