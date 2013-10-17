/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "organism")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Organism.findAll", query = "SELECT o FROM Organism o"),
  @NamedQuery(name = "Organism.findByOrganismId", query = "SELECT o FROM Organism o WHERE o.organismId = :organismId"),
  @NamedQuery(name = "Organism.findByCode", query = "SELECT o FROM Organism o WHERE o.code = :code"),
  @NamedQuery(name = "Organism.findByName", query = "SELECT o FROM Organism o WHERE o.name = :name"),
  @NamedQuery(name = "Organism.findByAccession", query = "SELECT o FROM Organism o WHERE o.accession = :accession"),
  @NamedQuery(name = "Organism.findByNcbiTaxid", query = "SELECT o FROM Organism o WHERE o.ncbiTaxid = :ncbiTaxid")})
public class Organism implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "organism_id")
  private Integer organismId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "code")
  private String code;
  @Size(max = 2147483647)
  @Column(name = "name")
  private String name;
  @Size(max = 2147483647)
  @Column(name = "accession")
  private String accession;
  @Column(name = "ncbi_taxid")
  private Integer ncbiTaxid;
  @OneToMany(mappedBy = "organismId")
  private Collection<Lane> laneCollection;
  @OneToMany(mappedBy = "organismId")
  private Collection<Sample> sampleCollection;

  public Organism() {
  }

  public Organism(Integer organismId) {
    this.organismId = organismId;
  }

  public Organism(Integer organismId, String code) {
    this.organismId = organismId;
    this.code = code;
  }

  public Integer getOrganismId() {
    return organismId;
  }

  public void setOrganismId(Integer organismId) {
    this.organismId = organismId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAccession() {
    return accession;
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public Integer getNcbiTaxid() {
    return ncbiTaxid;
  }

  public void setNcbiTaxid(Integer ncbiTaxid) {
    this.ncbiTaxid = ncbiTaxid;
  }

  @XmlTransient
  public Collection<Lane> getLaneCollection() {
    return laneCollection;
  }

  public void setLaneCollection(Collection<Lane> laneCollection) {
    this.laneCollection = laneCollection;
  }

  @XmlTransient
  public Collection<Sample> getSampleCollection() {
    return sampleCollection;
  }

  public void setSampleCollection(Collection<Sample> sampleCollection) {
    this.sampleCollection = sampleCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (organismId != null ? organismId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Organism)) {
      return false;
    }
    Organism other = (Organism) object;
    if ((this.organismId == null && other.organismId != null) || (this.organismId != null && !this.organismId.equals(other.organismId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.Organism[ organismId=" + organismId + " ]";
  }
  
}
