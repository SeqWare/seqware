package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class FileType implements Serializable, Comparable<FileType> {
	private static final long serialVersionUID = 2681345328115990568L;
	
	private Integer	fileTypeId;
	private String	displayName;
	private String 	metaType;
	private String	extension;

	public FileType() {
		super();
	}

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

    @Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("platformId", getFileTypeId())
			.toString();
	}

    @Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof FileType) ) return false;
		FileType castOther = (FileType) other;
		return new EqualsBuilder()
			.append(this.getFileTypeId(), castOther.getFileTypeId())
			.isEquals();
	}

    @Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getFileTypeId())
			.toHashCode();
	}

	public Integer getFileTypeId() {
		return fileTypeId;
	}

	public void setFileTypeId(Integer fileTypeId) {
		this.fileTypeId = fileTypeId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getMetaType() {
		return metaType;
	}

	public void setMetaType(String metaType) {
		this.metaType = metaType;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	public String getInfo(){
		return displayName + "    |    " + metaType + "    |    " + extension;
	}
}
