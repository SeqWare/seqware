package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * <p>StudyType class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StudyType implements Serializable, SecondTierModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer studyTypeId;
	private String  name;
	private String	description;
	
	/**
	 * <p>compareTo.</p>
	 *
	 * @param that a {@link net.sourceforge.seqware.common.model.StudyType} object.
	 * @return a int.
	 */
	public int compareTo(StudyType that) {
		if(that == null)
			return -1;

		if(that.getStudyTypeId() == this.getStudyTypeId())	// when both names are null
			return 0;

		if(that.getName() == null)
			return -1;							// when only the other name is null

		return(that.getStudyTypeId().compareTo(this.getStudyTypeId()));
	}
        
        /** {@inheritDoc} */
        @Override
        public String toString() {
          return new StringBuilder().append("StudyType ").append(studyTypeId)
                  .append(":\t").append(name).append("\t").append(description).toString();
        }
        
        /** {@inheritDoc} */
        @Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof StudyType) ) return false;
		StudyType castOther = (StudyType) other;
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
     * <p>Getter for the field <code>studyTypeId</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getStudyTypeId() {
        return studyTypeId;
    }

    /**
     * <p>Setter for the field <code>studyTypeId</code>.</p>
     *
     * @param studyTypeId a {@link java.lang.Integer} object.
     */
    public void setStudyTypeId(Integer studyTypeId) {
        this.studyTypeId = studyTypeId;
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
     * <p>Getter for the field <code>serialVersionUID</code>.</p>
     *
     * @return a long.
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public int getModelId() {
        return this.getStudyTypeId();
    }

}
