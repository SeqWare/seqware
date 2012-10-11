package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.Collection;

public interface Job {
	public void addFile(SqwFile file);
	
	public Command setCommand(String cmd);
	public Command getCommand();
	public Job addParent(Job parent);
	public Collection<Job> getParents();
	
	//public Job addRequirement(Requirement requirement);
	public Job setMaxMemory(String mem);
	public Job setThreads(int count);
	public Job setQueue(String queue);
	public void setHasMetadataWriteback(boolean metadata);
	public boolean hasMetadataWriteback();
}
