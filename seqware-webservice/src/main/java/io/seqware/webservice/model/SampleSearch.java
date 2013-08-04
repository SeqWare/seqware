/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "sample_search")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "SampleSearch.findAll", query = "SELECT s FROM SampleSearch s"),
  @NamedQuery(name = "SampleSearch.findBySampleSearchId", query = "SELECT s FROM SampleSearch s WHERE s.sampleSearchId = :sampleSearchId"),
  @NamedQuery(name = "SampleSearch.findByCreateTstmp", query = "SELECT s FROM SampleSearch s WHERE s.createTstmp = :createTstmp")})
public class SampleSearch implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "sample_search_id")
  private Integer sampleSearchId;
  @Basic(optional = false)
  @NotNull
  @Column(name = "create_tstmp")
  @Temporal(TemporalType.TIMESTAMP)
  private Date createTstmp;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "sampleSearchId")
  private Collection<SampleSearchAttribute> sampleSearchAttributeCollection;
  @JoinColumn(name = "sample_id", referencedColumnName = "sample_id")
  @ManyToOne(optional = false)
  private Sample sampleId;

  public SampleSearch() {
  }

  public SampleSearch(Integer sampleSearchId) {
    this.sampleSearchId = sampleSearchId;
  }

  public SampleSearch(Integer sampleSearchId, Date createTstmp) {
    this.sampleSearchId = sampleSearchId;
    this.createTstmp = createTstmp;
  }

  public Integer getSampleSearchId() {
    return sampleSearchId;
  }

  public void setSampleSearchId(Integer sampleSearchId) {
    this.sampleSearchId = sampleSearchId;
  }

  public Date getCreateTstmp() {
    return createTstmp;
  }

  public void setCreateTstmp(Date createTstmp) {
    this.createTstmp = createTstmp;
  }

  @XmlTransient
  public Collection<SampleSearchAttribute> getSampleSearchAttributeCollection() {
    return sampleSearchAttributeCollection;
  }

  public void setSampleSearchAttributeCollection(Collection<SampleSearchAttribute> sampleSearchAttributeCollection) {
    this.sampleSearchAttributeCollection = sampleSearchAttributeCollection;
  }

  public Sample getSampleId() {
    return sampleId;
  }

  public void setSampleId(Sample sampleId) {
    this.sampleId = sampleId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (sampleSearchId != null ? sampleSearchId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof SampleSearch)) {
      return false;
    }
    SampleSearch other = (SampleSearch) object;
    if ((this.sampleSearchId == null && other.sampleSearchId != null) || (this.sampleSearchId != null && !this.sampleSearchId.equals(other.sampleSearchId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.SampleSearch[ sampleSearchId=" + sampleSearchId + " ]";
  }
  
}
