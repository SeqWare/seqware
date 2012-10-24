package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p>FileType class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class FileType implements Serializable, Comparable<FileType> {
	private static final long serialVersionUID = 2681345328115990568L;
	
	private Integer	fileTypeId;
	private String	displayName;
	private String 	metaType;
	private String	extension;

	/**
	 * <p>Constructor for FileType.</p>
	 */
	public FileType() {
		super();
	}

    /** {@inheritDoc} */
    @Override
	public int compareTo(FileType that) {
		if(that == null)
			return -1;

		if(that.getFileTypeId() == this.getFileTypeId())	// when both names are null
			return 0;

		if(that.getFileTypeId() == null)
			return -1;							// when only the other name is null

		return(that.getFileTypeId().compareTo(this.getFileTypeId()));
	}

    /** {@inheritDoc} */
    @Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("platformId", getFileTypeId())
			.toString();
	}

    /** {@inheritDoc} */
    @Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof FileType) ) return false;
		FileType castOther = (FileType) other;
		return new EqualsBuilder()
			.append(this.getFileTypeId(), castOther.getFileTypeId())
			.isEquals();
	}

    /** {@inheritDoc} */
    @Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getFileTypeId())
			.toHashCode();
	}

	/**
	 * <p>Getter for the field <code>fileTypeId</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getFileTypeId() {
		return fileTypeId;
	}

	/**
	 * <p>Setter for the field <code>fileTypeId</code>.</p>
	 *
	 * @param fileTypeId a {@link java.lang.Integer} object.
	 */
	public void setFileTypeId(Integer fileTypeId) {
		this.fileTypeId = fileTypeId;
	}

	/**
	 * <p>Getter for the field <code>displayName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * <p>Setter for the field <code>displayName</code>.</p>
	 *
	 * @param displayName a {@link java.lang.String} object.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
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
	 * <p>Getter for the field <code>extension</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * <p>Setter for the field <code>extension</code>.</p>
	 *
	 * @param extension a {@link java.lang.String} object.
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	/**
	 * <p>getInfo.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getInfo(){
		return displayName + "    |    " + metaType + "    |    " + extension;
	}
}
