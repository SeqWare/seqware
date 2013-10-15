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
@Table(name = "processing_relationship")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ProcessingRelationship.findAll", query = "SELECT p FROM ProcessingRelationship p"),
  @NamedQuery(name = "ProcessingRelationship.findByProcessingRelationshipId", query = "SELECT p FROM ProcessingRelationship p WHERE p.processingRelationshipId = :processingRelationshipId"),
  @NamedQuery(name = "ProcessingRelationship.findByRelationship", query = "SELECT p FROM ProcessingRelationship p WHERE p.relationship = :relationship")})
public class ProcessingRelationship implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "processing_relationship_id")
  private Integer processingRelationshipId;
  @Size(max = 2147483647)
  @Column(name = "relationship")
  private String relationship;
  @JoinColumn(name = "parent_id", referencedColumnName = "processing_id")
  @ManyToOne
  private Processing parentId;
  @JoinColumn(name = "child_id", referencedColumnName = "processing_id")
  @ManyToOne
  private Processing childId;

  public ProcessingRelationship() {
  }

  public ProcessingRelationship(Integer processingRelationshipId) {
    this.processingRelationshipId = processingRelationshipId;
  }

  public Integer getProcessingRelationshipId() {
    return processingRelationshipId;
  }

  public void setProcessingRelationshipId(Integer processingRelationshipId) {
    this.processingRelationshipId = processingRelationshipId;
  }

  public String getRelationship() {
    return relationship;
  }

  public void setRelationship(String relationship) {
    this.relationship = relationship;
  }

  public Processing getParentId() {
    return parentId;
  }

  public void setParentId(Processing parentId) {
    this.parentId = parentId;
  }

  public Processing getChildId() {
    return childId;
  }

  public void setChildId(Processing childId) {
    this.childId = childId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (processingRelationshipId != null ? processingRelationshipId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ProcessingRelationship)) {
      return false;
    }
    ProcessingRelationship other = (ProcessingRelationship) object;
    if ((this.processingRelationshipId == null && other.processingRelationshipId != null) || (this.processingRelationshipId != null && !this.processingRelationshipId.equals(other.processingRelationshipId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ProcessingRelationship[ processingRelationshipId=" + processingRelationshipId + " ]";
  }
  
}
