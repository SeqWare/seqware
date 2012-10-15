package net.sourceforge.seqware.webservice.dto;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;


@XmlRootElement
/**
 * <p>SampleSearchDto class.</p>
 *
 * @author tdebat
 * @version $Id: $Id
 */
@JsonAutoDetect
@JsonSerialize(include = Inclusion.NON_NULL)
public class SampleSearchDto {
	
	private String url;
	
	@JsonProperty("sample_url")
	private String sampleUrl;
	
	private Set<AttributeDto> attributes;

	/**
	 * <p>Getter for the field <code>url</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * <p>Setter for the field <code>url</code>.</p>
	 *
	 * @param url a {@link java.lang.String} object.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * <p>Getter for the field <code>sampleUrl</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSampleUrl() {
		return sampleUrl;
	}

	/**
	 * <p>Setter for the field <code>sampleUrl</code>.</p>
	 *
	 * @param sampleUrl a {@link java.lang.String} object.
	 */
	public void setSampleUrl(String sampleUrl) {
		this.sampleUrl = sampleUrl;
	}

	/**
	 * <p>Getter for the field <code>attributes</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<AttributeDto> getAttributes() {
		return attributes;
	}

	/**
	 * <p>Setter for the field <code>attributes</code>.</p>
	 *
	 * @param attributes a {@link java.util.Set} object.
	 */
	public void setAttributes(Set<AttributeDto> attributes) {
		this.attributes = attributes;
	}

}
