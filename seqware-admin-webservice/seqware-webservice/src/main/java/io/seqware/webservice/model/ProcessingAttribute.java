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
@Table(name = "processing_attribute")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ProcessingAttribute.findAll", query = "SELECT p FROM ProcessingAttribute p"),
  @NamedQuery(name = "ProcessingAttribute.findByProcessingAttributeId", query = "SELECT p FROM ProcessingAttribute p WHERE p.processingAttributeId = :processingAttributeId"),
  @NamedQuery(name = "ProcessingAttribute.findByTag", query = "SELECT p FROM ProcessingAttribute p WHERE p.tag = :tag"),
  @NamedQuery(name = "ProcessingAttribute.findByValue", query = "SELECT p FROM ProcessingAttribute p WHERE p.value = :value"),
  @NamedQuery(name = "ProcessingAttribute.findByUnits", query = "SELECT p FROM ProcessingAttribute p WHERE p.units = :units")})
public class ProcessingAttribute implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "processing_attribute_id")
  private Integer processingAttributeId;
  @Size(max = 2147483647)
  @Column(name = "tag")
  private String tag;
  @Size(max = 2147483647)
  @Column(name = "value")
  private String value;
  @Size(max = 2147483647)
  @Column(name = "units")
  private String units;
  @JoinColumn(name = "processing_id", referencedColumnName = "processing_id")
  @ManyToOne(optional = false)
  private Processing processingId;

  public ProcessingAttribute() {
  }

  public ProcessingAttribute(Integer processingAttributeId) {
    this.processingAttributeId = processingAttributeId;
  }

  public Integer getProcessingAttributeId() {
    return processingAttributeId;
  }

  public void setProcessingAttributeId(Integer processingAttributeId) {
    this.processingAttributeId = processingAttributeId;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public Processing getProcessingId() {
    return processingId;
  }

  public void setProcessingId(Processing processingId) {
    this.processingId = processingId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (processingAttributeId != null ? processingAttributeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ProcessingAttribute)) {
      return false;
    }
    ProcessingAttribute other = (ProcessingAttribute) object;
    if ((this.processingAttributeId == null && other.processingAttributeId != null) || (this.processingAttributeId != null && !this.processingAttributeId.equals(other.processingAttributeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ProcessingAttribute[ processingAttributeId=" + processingAttributeId + " ]";
  }
  
}
