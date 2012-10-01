package net.sourceforge.seqware.pipeline.workflowV2.model;

/**
 * @author yliang
 *
 */
public class Requirement {
	public enum Type {
		JOBTYPE,
		MAXMEMORY,
		THREADS,
		QUEUE
	}
	
	private Type type;
	private String value;
	private String namespace = "globus";
	
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

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	
}