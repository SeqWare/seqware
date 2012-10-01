package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.ArrayList;
import java.util.Collection;
import net.sourceforge.seqware.pipeline.workflowV2.model.Requirement.Type;


public class Job {
	/**
	 * a private id to identify the job for internal use
	 */
	private String id;
	private String algo;
	private Collection<String> arguments;
	private Collection<Job> parents;
	private Collection<SqwFile> files;
	private Module module;
	private Command command;
	private Collection<Requirement> requirements;
	
	public Job() {
		this.arguments = new ArrayList<String>();
		this.parents = new ArrayList<Job>();
		this.files = new ArrayList<SqwFile>();
		this.requirements = new ArrayList<Requirement>();
		this.command = new Command();
		this.initRequirements();
	}
	
	
	private void initRequirements() {
		Requirement threadR = new Requirement();
		threadR.setType(Type.THREADS);
		threadR.setValue("1");
		this.requirements.add(threadR);
		
		Requirement memR = new Requirement();
		memR.setType(Type.MAXMEMORY);
		memR.setValue("2000");
		this.requirements.add(memR);
		
		Requirement jobR = new Requirement();
		jobR.setType(Type.JOBTYPE);
		jobR.setValue("condor");
		this.requirements.add(jobR);
	}
	
	/**
	 * 
	 * @return a job command object
	 */
	public Command getCommand() {
		return command;
	}

	/**
	 * 
	 * @return the module for current job
	 */
	public Module getModule() {
		return module;
	}
	/**
	 * set the module for current job
	 * @param module
	 */
	public void setModule(Module module) {
		this.module = module;
	}
	public Collection<SqwFile> getFiles() {
		return files;
	}
	public void setFiles(Collection<SqwFile> files) {
		this.files = files;
	}
	public void addFile(SqwFile file) {
		this.files.add(file);
	}
	/**
	 * add all parent jobs
	 * @return
	 */
	public Collection<Job> getParents() {
		return parents;
	}
	/**
	 * set parent jobs
	 * @param parents
	 */
	public void setParents(Collection<Job> parents) {
		this.parents = parents;
	}
	/**
	 * add a parent
	 * @param parent
	 */
	public void addParent(Job parent) {
		this.parents.add(parent);
	}
	/**
	 * get all user defined job arguments
	 * @return
	 */
	public Collection<String> getArguments() {
		return arguments;
	}
	public void setArguments(Collection<String> arguments) {
		this.arguments = arguments;
	}
	/**
	 * get the job algorithm
	 * @return
	 */
	public String getAlgo() {
		return algo;
	}
	/**
	 * set the job algorithm
	 * @param algo
	 */
	public void setAlgo(String algo) {
		this.algo = algo;
	}
	/**
	 * get job requirements
	 * @return
	 */
	public Collection<Requirement> getRequirements() {
		return requirements;
	}
	public void setRequirements(Collection<Requirement> requirements) {
		this.requirements = requirements;
	}
	
	public int getThreads() {
		return 0;
	}
	
	public void setThreads(int count) {
		
	}
}