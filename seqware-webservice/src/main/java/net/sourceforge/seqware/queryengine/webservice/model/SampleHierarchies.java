package net.sourceforge.seqware.queryengine.webservice.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SampleHierarchies {
	private List<SampleHierarchy> sampleHierarchies;

	public List<SampleHierarchy> getSampleHierarchies() {
		return sampleHierarchies;
	}

	public void setSampleHierarchies(List<SampleHierarchy> sampleHierarchies) {
		this.sampleHierarchies = sampleHierarchies;
	}
	
}