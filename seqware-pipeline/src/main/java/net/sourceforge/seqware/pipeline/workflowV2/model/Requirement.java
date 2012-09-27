package net.sourceforge.seqware.pipeline.workflowV2.model;

/**
 * @author yliang
 *
 */
public class Requirement {
	enum Type {
		JOBTYPE,
		MAXMEMORY,
		THREADS,
		QUEUE
	}
	
	private Type type;
	private String value;
	
	public Type getType() {
		return this.type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}