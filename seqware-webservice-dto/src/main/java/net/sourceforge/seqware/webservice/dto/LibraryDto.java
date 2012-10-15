package net.sourceforge.seqware.webservice.dto;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@XmlRootElement
/**
 * <p>LibraryDto class.</p>
 *
 * @author tdebat
 * @version $Id: $Id
 */
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
	 * <p>Getter for the field <code>description</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <p>Setter for the field <code>description</code>.</p>
	 *
	 * @param description a {@link java.lang.String} object.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * <p>Getter for the field <code>createTimeStamp</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCreateTimeStamp() {
		return createTimeStamp;
	}

	/**
	 * <p>Setter for the field <code>createTimeStamp</code>.</p>
	 *
	 * @param createTimeStamp a {@link java.lang.String} object.
	 */
	public void setCreateTimeStamp(String createTimeStamp) {
		this.createTimeStamp = createTimeStamp;
	}

	/**
	 * <p>Getter for the field <code>updateTimeStamp</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUpdateTimeStamp() {
		return updateTimeStamp;
	}

	/**
	 * <p>Setter for the field <code>updateTimeStamp</code>.</p>
	 *
	 * @param updateTimeStamp a {@link java.lang.String} object.
	 */
	public void setUpdateTimeStamp(String updateTimeStamp) {
		this.updateTimeStamp = updateTimeStamp;
	}

	/**
	 * <p>Getter for the field <code>owner</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.webservice.dto.OwnerDto} object.
	 */
	public OwnerDto getOwner() {
		return owner;
	}

	/**
	 * <p>Setter for the field <code>owner</code>.</p>
	 *
	 * @param owner a {@link net.sourceforge.seqware.webservice.dto.OwnerDto} object.
	 */
	public void setOwner(OwnerDto owner) {
		this.owner = owner;
	}

	/**
	 * <p>Getter for the field <code>organism</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.webservice.dto.OrganismDto} object.
	 */
	public OrganismDto getOrganism() {
		return organism;
	}

	/**
	 * <p>Setter for the field <code>organism</code>.</p>
	 *
	 * @param organism a {@link net.sourceforge.seqware.webservice.dto.OrganismDto} object.
	 */
	public void setOrganism(OrganismDto organism) {
		this.organism = organism;
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

	/**
	 * <p>Getter for the field <code>parentUrls</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<String> getParentUrls() {
		return parentUrls;
	}

	/**
	 * <p>Setter for the field <code>parentUrls</code>.</p>
	 *
	 * @param parentUrls a {@link java.util.Set} object.
	 */
	public void setParentUrls(Set<String> parentUrls) {
		this.parentUrls = parentUrls;
	}

	/**
	 * <p>Getter for the field <code>childrenUrls</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<String> getChildrenUrls() {
		return childrenUrls;
	}

	/**
	 * <p>Setter for the field <code>childrenUrls</code>.</p>
	 *
	 * @param childrenUrls a {@link java.util.Set} object.
	 */
	public void setChildrenUrls(Set<String> childrenUrls) {
		this.childrenUrls = childrenUrls;
	}
	
}
