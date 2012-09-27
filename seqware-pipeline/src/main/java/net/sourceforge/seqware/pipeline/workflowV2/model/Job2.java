package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.Collection;


public class Job2 {
	/**
	 * a private id to identify the job for internal use
	 */
	private String id;
	private String algo;
	private Collection<String> arguments;
	private Collection<Job2> parents;
	private Collection<SqwFile> files;
	private Module module;
	private Command command;
	private Collection<Requirement> requirements;
	
	
	/**
	 * 
	 * @return a job command object
	 */
	public Command getCommand() {
		return command;
	}
	/**
	 * set the command object to current job
	 * @param command
	 */
	public void setCommand(Command command) {
		this.command = command;
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
	public Collection<Job2> getParents() {
		return parents;
	}
	/**
	 * set parent jobs
	 * @param parents
	 */
	public void setParents(Collection<Job2> parents) {
		this.parents = parents;
	}
	/**
	 * add a parent
	 * @param parent
	 */
	public void addParent(Job2 parent) {
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
}