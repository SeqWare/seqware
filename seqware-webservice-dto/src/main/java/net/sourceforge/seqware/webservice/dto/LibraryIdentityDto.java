package net.sourceforge.seqware.webservice.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@XmlRootElement
/**
 * <p>LibraryIdentityDto class.</p>
 *
 * @author tdebat
 * @version $Id: $Id
 */
@JsonAutoDetect
@JsonSerialize(include = Inclusion.NON_NULL)
public class LibraryIdentityDto {
	
	@JsonProperty("sample_id")
	private String sampleId;
	@JsonProperty("sample_id")
	private String libraryId;

	/**
	 * <p>Getter for the field <code>sampleId</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getSampleId() {
		return sampleId;
	}

	/**
	 * <p>Setter for the field <code>sampleId</code>.</p>
	 *
	 * @param sampleId a {@link java.lang.String} object.
	 */
	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	/**
	 * <p>Getter for the field <code>libraryId</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getLibraryId() {
		return libraryId;
	}

	/**
	 * <p>Setter for the field <code>libraryId</code>.</p>
	 *
	 * @param libraryId a {@link java.lang.String} object.
	 */
	public void setLibraryId(String libraryId) {
		this.libraryId = libraryId;
	}

}
