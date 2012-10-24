package	net.sourceforge.seqware.common.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p>Platform class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Platform implements Serializable, Comparable<Platform> {
	private static final long serialVersionUID = 3681345328115990568L;
	
	private Integer		platformId;
	private String 		name;
	private String 		description;
	private String 		instrumentModel;

	/**
	 * <p>Constructor for Platform.</p>
	 */
	public Platform() {
		super();
	}

    /** {@inheritDoc} */
    @Override
	public int compareTo(Platform that) {
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
			.append("platformId", getPlatformId())
			.toString();
	}

    /** {@inheritDoc} */
    @Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof Platform) ) return false;
		Platform castOther = (Platform) other;
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
     * <p>Getter for the field <code>platformId</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getPlatformId() {
        return platformId;
    }

    /**
     * <p>Setter for the field <code>platformId</code>.</p>
     *
     * @param platformId a {@link java.lang.Integer} object.
     */
    public void setPlatformId(Integer platformId) {
        this.platformId = platformId;
    }

    /**
     * <p>getLongName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLongName() {
        return (name+" - "+instrumentModel);
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
     * <p>Getter for the field <code>instrumentModel</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getInstrumentModel() {
        return instrumentModel;
    }

    /**
     * <p>Setter for the field <code>instrumentModel</code>.</p>
     *
     * @param instrumentModel a {@link java.lang.String} object.
     */
    public void setInstrumentModel(String instrumentModel) {
        this.instrumentModel = instrumentModel;
    }

    /**
     * <p>Getter for the field <code>serialVersionUID</code>.</p>
     *
     * @return a long.
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
