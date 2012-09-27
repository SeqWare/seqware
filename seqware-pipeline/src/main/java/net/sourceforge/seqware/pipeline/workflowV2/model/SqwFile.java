package net.sourceforge.seqware.pipeline.workflowV2.model;

public class SqwFile {
	private String type;
	private String location;
	private boolean input;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public boolean isOutput() {
		return !input;
	}
	
	public boolean isInput() {
		return input;
	}
	
	public void setIsInput(boolean isInupt) {
		this.input = isInupt;
	}
	
	public void setIsOutput(boolean isOutput) {
		this.input = !isOutput;
	}
}