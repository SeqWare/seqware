package net.sourceforge.seqware.webservice.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@XmlRootElement
@JsonAutoDetect
@JsonSerialize(include = Inclusion.NON_NULL)
public class OrganismDto {

	private String name;
	
	private String code;
	@JsonProperty("ncbi_taxonomy_id")
	private Integer ncbiTaxonomyId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getNcbiTaxonomyId() {
		return ncbiTaxonomyId;
	}

	public void setNcbiTaxonomyId(Integer ncbiTaxonomyId) {
		this.ncbiTaxonomyId = ncbiTaxonomyId;
	}
	
}
