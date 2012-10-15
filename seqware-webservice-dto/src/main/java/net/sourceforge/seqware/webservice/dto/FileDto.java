package net.sourceforge.seqware.webservice.dto;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@XmlRootElement
/**
 * <p>FileDto class.</p>
 *
 * @author tdebat
 * @version $Id: $Id
 */
@JsonAutoDetect
@JsonSerialize(include = Inclusion.NON_NULL)
public class FileDto {

	private String url;
	@JsonProperty("file_path")
	private String filePath;
	private String type;
	@JsonProperty("meta_type")
	private String metaType;
	private String description;
	private String md5sum;
	private Long size;

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
	 * <p>Getter for the field <code>filePath</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * <p>Setter for the field <code>filePath</code>.</p>
	 *
	 * @param filePath a {@link java.lang.String} object.
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * <p>Getter for the field <code>type</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getType() {
		return type;
	}

	/**
	 * <p>Setter for the field <code>type</code>.</p>
	 *
	 * @param type a {@link java.lang.String} object.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * <p>Getter for the field <code>metaType</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getMetaType() {
		return metaType;
	}

	/**
	 * <p>Setter for the field <code>metaType</code>.</p>
	 *
	 * @param metaType a {@link java.lang.String} object.
	 */
	public void setMetaType(String metaType) {
		this.metaType = metaType;
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
	 * <p>Getter for the field <code>md5sum</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getMd5sum() {
		return md5sum;
	}

	/**
	 * <p>Setter for the field <code>md5sum</code>.</p>
	 *
	 * @param md5sum a {@link java.lang.String} object.
	 */
	public void setMd5sum(String md5sum) {
		this.md5sum = md5sum;
	}

	/**
	 * <p>Getter for the field <code>size</code>.</p>
	 *
	 * @return a {@link java.lang.Long} object.
	 */
	public Long getSize() {
		return size;
	}

	/**
	 * <p>Setter for the field <code>size</code>.</p>
	 *
	 * @param size a {@link java.lang.Long} object.
	 */
	public void setSize(Long size) {
		this.size = size;
	}

}
