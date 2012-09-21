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
public class LibraryDto {

	private String url;
	
	private String name;
	
	private String description;
	@JsonProperty("create_time_stamp")
	private String createTimeStamp;
	@JsonProperty("update_time_stamp")
	private String updateTimeStamp;
	
	private OwnerDto owner;
	
	private OrganismDto organism;
	
	private Set<AttributeDto> attributes;
	@JsonProperty("parent_urls")
	private Set<String> parentUrls;
	@JsonProperty("children_urls")
	private Set<String> childrenUrls;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreateTimeStamp() {
		return createTimeStamp;
	}

	public void setCreateTimeStamp(String createTimeStamp) {
		this.createTimeStamp = createTimeStamp;
	}

	public String getUpdateTimeStamp() {
		return updateTimeStamp;
	}

	public void setUpdateTimeStamp(String updateTimeStamp) {
		this.updateTimeStamp = updateTimeStamp;
	}

	public OwnerDto getOwner() {
		return owner;
	}

	public void setOwner(OwnerDto owner) {
		this.owner = owner;
	}

	public OrganismDto getOrganism() {
		return organism;
	}

	public void setOrganism(OrganismDto organism) {
		this.organism = organism;
	}

	public Set<AttributeDto> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<AttributeDto> attributes) {
		this.attributes = attributes;
	}

	public Set<String> getParentUrls() {
		return parentUrls;
	}

	public void setParentUrls(Set<String> parentUrls) {
		this.parentUrls = parentUrls;
	}

	public Set<String> getChildrenUrls() {
		return childrenUrls;
	}

	public void setChildrenUrls(Set<String> childrenUrls) {
		this.childrenUrls = childrenUrls;
	}
	
}
