package	net.sourceforge.seqware.common.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;


public class Organism implements Serializable, Comparable<Organism> {
	
	private static final long serialVersionUID = 3472028192033390568L;
	private Integer		organismId;
	private String 		name;
	private String 		code;
	private String 		accession;
	private Integer 	ncbiTaxId;

	public Organism() {
		super();
	}

    @Override
	public int compareTo(Organism that) {
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
			.append("organismId", getOrganismId())
			.toString();
	}

    @Override
	public boolean equals(Object other) {
		if ( (this == other ) ) return true;
		if ( !(other instanceof Organism) ) return false;
		Organism castOther = (Organism) other;
		return new EqualsBuilder()
			.append(this.getName(), castOther.getName())
			.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder()
			.append(getName())
			.toHashCode();
	}

	public Integer getOrganismId() {
		return organismId;
	}

	public void setOrganismId(Integer organismId) {
		this.organismId = organismId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public Integer getNcbiTaxId() {
		return ncbiTaxId;
	}

	public void setNcbiTaxId(Integer ncbiTaxId) {
		this.ncbiTaxId = ncbiTaxId;
	}
	
}
