package	net.sourceforge.seqware.common.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * <p>LibraryStrategy class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class LibraryStrategy implements Serializable, Comparable<LibraryStrategy> {
	private static final long serialVersionUID = 3681345328915990568L;
	
	private Integer		libraryStrategyId;
	private String 		name;
	private String 		description;

	/**
	 * <p>Constructor for LibraryStrategy.</p>
	 */
	public LibraryStrategy() {
		super();
	}

    /** {@inheritDoc} */
    @Override
	public int compareTo(LibraryStrategy that) {
		if(that == null)
			return -1;

		if(that.getName()==null && this.getName()==null)	// when both names are null
			return 0;

		if(that.getName() == null)
			return -1;							// when only the other name is null

		return(that.getName().compareTo(this.getName()));
	}

    /** {@inheritDoc} */
    @Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("libraryStrategyId", getLibraryStrategyId())
			.toString();
	}

    /** {@inheritDoc} */
    @Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof LibraryStrategy) ) return false;
		LibraryStrategy castOther = (LibraryStrategy) other;
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
	 * <p>Getter for the field <code>libraryStrategyId</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getLibraryStrategyId() {
		return libraryStrategyId;
	}

	/**
	 * <p>Setter for the field <code>libraryStrategyId</code>.</p>
	 *
	 * @param libraryStrategyId a {@link java.lang.Integer} object.
	 */
	public void setLibraryStrategyId(Integer libraryStrategyId) {
		this.libraryStrategyId = libraryStrategyId;
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
	
}
