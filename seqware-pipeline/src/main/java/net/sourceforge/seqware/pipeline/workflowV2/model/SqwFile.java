package net.sourceforge.seqware.pipeline.workflowV2.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class SqwFile {
	private String type;
	private String location;
	private boolean input;
	private boolean forceCopy;
	
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
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SqwFile == false)
			return false;
		if(obj == this)
			return true;
		SqwFile rhs = (SqwFile)obj;
		return new EqualsBuilder().appendSuper(super.equals(obj)).append(type, rhs.type).
				append(location, rhs.location).append(input, rhs.input).isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(type).append(location).append(input).toHashCode();
	}
	public boolean isForceCopy() {
		return forceCopy;
	}
	public void setForceCopy(boolean forceCopy) {
		this.forceCopy = forceCopy;
	}
}