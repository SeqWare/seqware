package	net.sourceforge.seqware.common.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * <p>LibrarySource class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LibrarySource implements Serializable, Comparable<LibrarySource>, SecondTierModel {
	private static final long serialVersionUID = 3681345318915990568L;
	
	private Integer		librarySourceId;
	private String 		name;
	private String 		description;

	/**
	 * <p>Constructor for LibrarySource.</p>
	 */
	public LibrarySource() {
		super();
	}

    /** {@inheritDoc} */
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
        return new StringBuilder().append("LibrarySource ").append(librarySourceId)
                .append(":\t").append(name).append("\t").append(description).toString();
    }



    /** {@inheritDoc} */
    @Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof LibrarySource) ) return false;
		LibrarySource castOther = (LibrarySource) other;
		return new EqualsBuilder()
			.append(this.getName(), castOther.getName())
			.isEquals();
	}

    /** {@inheritDoc} */
    @Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getName())
			.toHashCode();
	}



	/**
	 * <p>Getter for the field <code>librarySourceId</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getLibrarySourceId() {
		return librarySourceId;
	}

	/**
	 * <p>Setter for the field <code>librarySourceId</code>.</p>
	 *
	 * @param librarySourceId a {@link java.lang.Integer} object.
	 */
	public void setLibrarySourceId(Integer librarySourceId) {
		this.librarySourceId = librarySourceId;
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

    @Override
    public int getModelId() {
        return this.getLibrarySourceId();
    }
	
}
