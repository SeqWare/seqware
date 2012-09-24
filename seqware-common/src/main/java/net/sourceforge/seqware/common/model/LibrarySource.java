package	net.sourceforge.seqware.common.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


public class LibrarySource implements Serializable, Comparable<LibrarySource> {
	private static final long serialVersionUID = 3681345318915990568L;
	
	private Integer		librarySourceId;
	private String 		name;
	private String 		description;

	public LibrarySource() {
		super();
	}

    @Override
	public int compareTo(LibrarySource that) {
		if(that == null)
			return -1;

		if(that.getName()==null && this.getName()==null)	// when both names are null
			return 0;

		if(that.getName() == null)
			return -1;							// when only the other name is null

		return(that.getName().compareTo(this.getName()));
	}

    @Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("librarySourceId", getLibrarySourceId())
			.toString();
	}

    @Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof LibrarySource) ) return false;
		LibrarySource castOther = (LibrarySource) other;
		return new EqualsBuilder()
			.append(this.getName(), castOther.getName())
			.isEquals();
	}

    @Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getName())
			.toHashCode();
	}



	public Integer getLibrarySourceId() {
		return librarySourceId;
	}

	public void setLibrarySourceId(Integer librarySourceId) {
		this.librarySourceId = librarySourceId;
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
	
}
