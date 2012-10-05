package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.ArrayList;
import java.util.Collection;
import net.sourceforge.seqware.pipeline.workflowV2.model.Requirement.Type;


public class AbstractJob implements Job {
	/**
	 * a private id to identify the job for internal use
	 */
	private String id;
	private String algo;
	private Collection<String> arguments;
	private Collection<Job> parents;
	private Collection<SqwFile> files;
	private Command command;
	private Collection<Requirement> requirements;
	private String cp;
	private String mainclass;
	
	/**
	 * for bash Job
	 * @param algo
	 */
	public AbstractJob(String algo) {
		this(algo, "", "");
	}
	
	/**
	 * for Java/Perl job
	 */
	public AbstractJob(String algo, String cp, String mainclass) {
		this.cp = cp;
		this.mainclass = mainclass;
		this.arguments = new ArrayList<String>();
		this.parents = new ArrayList<Job>();
		this.files = new ArrayList<SqwFile>();
		this.requirements = new ArrayList<Requirement>();
		this.command = new Command();
		this.algo = algo;
		this.initRequirements();
	}
	
	
	private void initRequirements() {
		Requirement jobR = new Requirement();
		jobR.setType(Type.JOBTYPE);
		jobR.setValue("condor");
		this.requirements.add(jobR);
		
		Requirement threadR = new Requirement();
		threadR.setType(Type.COUNT);
		threadR.setValue("1");
		this.requirements.add(threadR);
		
		Requirement memR = new Requirement();
		memR.setType(Type.MAXMEMORY);
		memR.setValue("2000");
		this.requirements.add(memR);
		
	}
	
	/**
	 * 
	 * @return a job command object
	 */
	public Command getCommand() {
		return command;
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
	public void addParent(AbstractJob parent) {
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
		return Integer.parseInt(this.getRequirementByType(Type.COUNT).getValue());
	}
	
	public AbstractJob setThreads(int count) {
		this.getRequirementByType(Type.COUNT).setValue(""+count);
		return this;
	}
	
	public String getMaxMemory() {
		return this.getRequirementByType(Type.MAXMEMORY).getValue();
	}
	
	public AbstractJob setMaxMemory(String mem) {
		this.getRequirementByType(Type.MAXMEMORY).setValue(mem);
		return this;
	}
	
	public String getClassPath() {
		return this.cp;
	}
	
	/**
	 * return the main class for a Java job, or the script.pl for a perl Job
	 * @return
	 */
	public String getMainClass() {
		return this.mainclass;
	}
	
	private Requirement getRequirementByType(Type type) {
		for(Requirement r: this.requirements) {
			if(r.getType() == type)
				return r;
		}
		return null;
	}

	@Override
	public Command setCommand(String cmd) {
		return this.command;
	}

	@Override
	public Job addParent(Job parent) {
		this.parents.add(parent);
		return this;
	}

	@Override
	public Job addRequirement(Requirement requirement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Job setQueue(String queue) {
		// TODO Auto-generated method stub
		return null;
	}

}