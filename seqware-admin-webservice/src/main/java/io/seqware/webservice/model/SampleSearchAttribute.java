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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "sample_search_attribute")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "SampleSearchAttribute.findAll", query = "SELECT s FROM SampleSearchAttribute s"),
  @NamedQuery(name = "SampleSearchAttribute.findBySampleSearchAttributeId", query = "SELECT s FROM SampleSearchAttribute s WHERE s.sampleSearchAttributeId = :sampleSearchAttributeId"),
  @NamedQuery(name = "SampleSearchAttribute.findByTag", query = "SELECT s FROM SampleSearchAttribute s WHERE s.tag = :tag"),
  @NamedQuery(name = "SampleSearchAttribute.findByValue", query = "SELECT s FROM SampleSearchAttribute s WHERE s.value = :value")})
public class SampleSearchAttribute implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "sample_search_attribute_id")
  private Integer sampleSearchAttributeId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "tag")
  private String tag;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "value")
  private String value;
  @JoinColumn(name = "sample_search_id", referencedColumnName = "sample_search_id")
  @ManyToOne(optional = false)
  private SampleSearch sampleSearchId;

  public SampleSearchAttribute() {
  }

  public SampleSearchAttribute(Integer sampleSearchAttributeId) {
    this.sampleSearchAttributeId = sampleSearchAttributeId;
  }

  public SampleSearchAttribute(Integer sampleSearchAttributeId, String tag, String value) {
    this.sampleSearchAttributeId = sampleSearchAttributeId;
    this.tag = tag;
    this.value = value;
  }

  public Integer getSampleSearchAttributeId() {
    return sampleSearchAttributeId;
  }

  public void setSampleSearchAttributeId(Integer sampleSearchAttributeId) {
    this.sampleSearchAttributeId = sampleSearchAttributeId;
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

  public SampleSearch getSampleSearchId() {
    return sampleSearchId;
  }

  public void setSampleSearchId(SampleSearch sampleSearchId) {
    this.sampleSearchId = sampleSearchId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (sampleSearchAttributeId != null ? sampleSearchAttributeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof SampleSearchAttribute)) {
      return false;
    }
    SampleSearchAttribute other = (SampleSearchAttribute) object;
    if ((this.sampleSearchAttributeId == null && other.sampleSearchAttributeId != null) || (this.sampleSearchAttributeId != null && !this.sampleSearchAttributeId.equals(other.sampleSearchAttributeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.SampleSearchAttribute[ sampleSearchAttributeId=" + sampleSearchAttributeId + " ]";
  }
  
}
