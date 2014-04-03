package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Workflow {

	private String workflowBundleDir;
	
	private Collection<String> tests;
	//may be better to use Map
	private List<AbstractJob> jobs;

	
	public Workflow() {
		this.jobs = new ArrayList<AbstractJob>();
		this.tests = new ArrayList<String>();
	}
	
	/**
	* get all jobs in current workflow
	* @return
	*/
	public List<AbstractJob> getJobs() {
		return jobs;
	}
	
	/**
	* add job to the workflow
	* @param job
	*/
	public void addJobs(AbstractJob job) {
		this.jobs.add(job);
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
	
	public Job createJavaSeqwareModuleJob(String algo, String cp, String module) {
		AbstractJob job = new JavaSeqwareModuleJob(algo, cp, module);
		this.jobs.add(job);
		return job;
	}
	
	public Job createJavaJob(String algo, String cp, String mainclass) {
		AbstractJob job = new JavaJob(algo,cp,mainclass);
		this.jobs.add(job);
		return job;
	}
	
	public Job createBashJob(String algo) {
		AbstractJob job = new BashJob(algo);
		this.jobs.add(job);
		return job;
	}
	
	public Job createPerlJob(String algo, String script) {
		AbstractJob job = new PerlJob(algo, "", script);
		this.jobs.add(job);
		return job;
	}
	
	/**
	* add a test command for the workflow
	* @param value
	*/
	public void addTest(String value) {
		this.tests.add(value);
	}
	
	/**
	* @return all test commands
	*/
	public Collection<String> getTests() {
		return this.tests;
	}
	

}


