package net.sourceforge.seqware.webservice.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@XmlRootElement
/**
 * <p>OrganismDto class.</p>
 *
 * @author tdebat
 * @version $Id: $Id
 */
@JsonAutoDetect
@JsonSerialize(include = Inclusion.NON_NULL)
public class OrganismDto {

	private String name;
	
	private String code;
	@JsonProperty("ncbi_taxonomy_id")
	private Integer ncbiTaxonomyId;

	/**
	 * <p>Getter for the field <code>name</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>Setter for the field <code>name</code>.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * <p>Getter for the field <code>code</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCode() {
		return code;
	}

	/**
	 * <p>Setter for the field <code>code</code>.</p>
	 *
	 * @param code a {@link java.lang.String} object.
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * <p>Getter for the field <code>ncbiTaxonomyId</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getNcbiTaxonomyId() {
		return ncbiTaxonomyId;
	}

	/**
	 * <p>Setter for the field <code>ncbiTaxonomyId</code>.</p>
	 *
	 * @param ncbiTaxonomyId a {@link java.lang.Integer} object.
	 */
	public void setNcbiTaxonomyId(Integer ncbiTaxonomyId) {
		this.ncbiTaxonomyId = ncbiTaxonomyId;
	}
	
}
