/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "sample_relationship")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "SampleRelationship.findAll", query = "SELECT s FROM SampleRelationship s"),
  @NamedQuery(name = "SampleRelationship.findBySampleRelationshipId", query = "SELECT s FROM SampleRelationship s WHERE s.sampleRelationshipId = :sampleRelationshipId"),
  @NamedQuery(name = "SampleRelationship.findByRelationship", query = "SELECT s FROM SampleRelationship s WHERE s.relationship = :relationship")})
public class SampleRelationship implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "sample_relationship_id")
  private Integer sampleRelationshipId;
  @Size(max = 2147483647)
  @Column(name = "relationship")
  private String relationship;
  @JoinColumn(name = "parent_id", referencedColumnName = "sample_id")
  @ManyToOne
  private Sample parentId;
  @JoinColumn(name = "child_id", referencedColumnName = "sample_id")
  @ManyToOne
  private Sample childId;

  public SampleRelationship() {
  }

  public SampleRelationship(Integer sampleRelationshipId) {
    this.sampleRelationshipId = sampleRelationshipId;
  }

  public Integer getSampleRelationshipId() {
    return sampleRelationshipId;
  }

  public void setSampleRelationshipId(Integer sampleRelationshipId) {
    this.sampleRelationshipId = sampleRelationshipId;
  }

  public String getRelationship() {
    return relationship;
  }

  public void setRelationship(String relationship) {
    this.relationship = relationship;
  }

  public Sample getParentId() {
    return parentId;
  }

  public void setParentId(Sample parentId) {
    this.parentId = parentId;
  }

  public Sample getChildId() {
    return childId;
  }

  public void setChildId(Sample childId) {
    this.childId = childId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (sampleRelationshipId != null ? sampleRelationshipId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof SampleRelationship)) {
      return false;
    }
    SampleRelationship other = (SampleRelationship) object;
    if ((this.sampleRelationshipId == null && other.sampleRelationshipId != null) || (this.sampleRelationshipId != null && !this.sampleRelationshipId.equals(other.sampleRelationshipId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.SampleRelationship[ sampleRelationshipId=" + sampleRelationshipId + " ]";
  }
  
}
