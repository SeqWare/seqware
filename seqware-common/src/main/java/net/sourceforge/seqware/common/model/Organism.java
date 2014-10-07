package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * <p>
 * Organism class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class Organism implements Serializable, Comparable<Organism>, SecondTierModel {

    private static final long serialVersionUID = 3472028192033390568L;
    private Integer organismId;
    private String name;
    private String code;
    private String accession;
    private Integer ncbiTaxId;

    /**
     * <p>
     * Constructor for Organism.
     * </p>
     */
    public Organism() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @param that
     */
    @Override
    public int compareTo(Organism that) {
        if (that == null) return -1;

        if (that.getName() == null && this.getName() == null) // when both names are null
            return 0;

        if (that.getName() == null) return -1; // when only the other name is null

        return (that.getName().compareTo(this.getName()));
    }

    /**
     * {@inheritDoc}
     * 
     * @param other
     */
    @Override
    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof Organism)) return false;
        Organism castOther = (Organism) other;
        return new EqualsBuilder().append(this.getName(), castOther.getName()).isEquals();
    }

    @Override
    public String toString() {
        return new StringBuilder().append("Organism ").append(organismId).append(":\t").append(name).toString();
    }

    /**
     * <p>
     * hashCode.
     * </p>
     * 
     * @return a int.
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getName()).toHashCode();
    }

    /**
     * <p>
     * Getter for the field <code>organismId</code>.
     * </p>
     * 
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getOrganismId() {
        return organismId;
    }

    /**
     * <p>
     * Setter for the field <code>organismId</code>.
     * </p>
     * 
     * @param organismId
     *            a {@link java.lang.Integer} object.
     */
    public void setOrganismId(Integer organismId) {
        this.organismId = organismId;
    }

    /**
     * <p>
     * Getter for the field <code>name</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>
     * Setter for the field <code>name</code>.
     * </p>
     * 
     * @param name
     *            a {@link java.lang.String} object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>
     * Getter for the field <code>code</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getCode() {
        return code;
    }

    /**
     * <p>
     * Setter for the field <code>code</code>.
     * </p>
     * 
     * @param code
     *            a {@link java.lang.String} object.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * <p>
     * Getter for the field <code>accession</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getAccession() {
        return accession;
    }

    /**
     * <p>
     * Setter for the field <code>accession</code>.
     * </p>
     * 
     * @param accession
     *            a {@link java.lang.String} object.
     */
    public void setAccession(String accession) {
        this.accession = accession;
    }

    /**
     * <p>
     * Getter for the field <code>ncbiTaxId</code>.
     * </p>
     * 
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getNcbiTaxId() {
        return ncbiTaxId;
    }

    /**
     * <p>
     * Setter for the field <code>ncbiTaxId</code>.
     * </p>
     * 
     * @param ncbiTaxId
     *            a {@link java.lang.Integer} object.
     */
    public void setNcbiTaxId(Integer ncbiTaxId) {
        this.ncbiTaxId = ncbiTaxId;
    }

    @Override
    public int getModelId() {
        return this.getOrganismId();
    }

}
