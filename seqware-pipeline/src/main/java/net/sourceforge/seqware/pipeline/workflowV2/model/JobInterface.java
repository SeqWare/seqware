package net.sourceforge.seqware.pipeline.workflowV2.model;

public interface JobInterface {
	public void addFile(SqwFile file);
	
	public Command setCommand(String cmd);
	public Command getCommand();
	public JobInterface addParent(JobInterface parent);
	
	public JobInterface addRequirement(Requirement requirement);
	public JobInterface setMaxMemory(String mem);
	public JobInterface setThreads(int count);
	public JobInterface setQueue(String queue);
}
