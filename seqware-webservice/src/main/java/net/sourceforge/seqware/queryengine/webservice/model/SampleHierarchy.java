package net.sourceforge.seqware.queryengine.webservice.model;

public class SampleHierarchy {
	private int sampleId;
	private int parentId = -1;
	public int getSampleId() {
		return sampleId;
	}
	public void setSampleId(int sampleId) {
		this.sampleId = sampleId;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
}