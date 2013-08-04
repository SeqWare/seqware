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
@Table(name = "sample_attribute")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "SampleAttribute.findAll", query = "SELECT s FROM SampleAttribute s"),
  @NamedQuery(name = "SampleAttribute.findBySampleAttributeId", query = "SELECT s FROM SampleAttribute s WHERE s.sampleAttributeId = :sampleAttributeId"),
  @NamedQuery(name = "SampleAttribute.findByTag", query = "SELECT s FROM SampleAttribute s WHERE s.tag = :tag"),
  @NamedQuery(name = "SampleAttribute.findByValue", query = "SELECT s FROM SampleAttribute s WHERE s.value = :value"),
  @NamedQuery(name = "SampleAttribute.findByUnits", query = "SELECT s FROM SampleAttribute s WHERE s.units = :units")})
public class SampleAttribute implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "sample_attribute_id")
  private Integer sampleAttributeId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "tag")
  private String tag;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "value")
  private String value;
  @Size(max = 2147483647)
  @Column(name = "units")
  private String units;
  @JoinColumn(name = "sample_id", referencedColumnName = "sample_id")
  @ManyToOne(optional = false)
  private Sample sampleId;

  public SampleAttribute() {
  }

  public SampleAttribute(Integer sampleAttributeId) {
    this.sampleAttributeId = sampleAttributeId;
  }

  public SampleAttribute(Integer sampleAttributeId, String tag, String value) {
    this.sampleAttributeId = sampleAttributeId;
    this.tag = tag;
    this.value = value;
  }

  public Integer getSampleAttributeId() {
    return sampleAttributeId;
  }

  public void setSampleAttributeId(Integer sampleAttributeId) {
    this.sampleAttributeId = sampleAttributeId;
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

  public Sample getSampleId() {
    return sampleId;
  }

  public void setSampleId(Sample sampleId) {
    this.sampleId = sampleId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (sampleAttributeId != null ? sampleAttributeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof SampleAttribute)) {
      return false;
    }
    SampleAttribute other = (SampleAttribute) object;
    if ((this.sampleAttributeId == null && other.sampleAttributeId != null) || (this.sampleAttributeId != null && !this.sampleAttributeId.equals(other.sampleAttributeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.SampleAttribute[ sampleAttributeId=" + sampleAttributeId + " ]";
  }
  
}
