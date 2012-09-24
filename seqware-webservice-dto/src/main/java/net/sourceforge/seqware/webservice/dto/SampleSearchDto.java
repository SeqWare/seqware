package net.sourceforge.seqware.webservice.dto;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;


@XmlRootElement
@JsonAutoDetect
@JsonSerialize(include = Inclusion.NON_NULL)
public class SampleSearchDto {
	
	private String url;
	
	@JsonProperty("sample_url")
	private String sampleUrl;
	
	private Set<AttributeDto> attributes;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSampleUrl() {
		return sampleUrl;
	}

	public void setSampleUrl(String sampleUrl) {
		this.sampleUrl = sampleUrl;
	}

	public Set<AttributeDto> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<AttributeDto> attributes) {
		this.attributes = attributes;
	}

}
