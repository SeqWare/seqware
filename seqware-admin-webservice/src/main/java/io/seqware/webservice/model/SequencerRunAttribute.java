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
@Table(name = "sequencer_run_attribute")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "SequencerRunAttribute.findAll", query = "SELECT s FROM SequencerRunAttribute s"),
  @NamedQuery(name = "SequencerRunAttribute.findBySequencerRunAttributeId", query = "SELECT s FROM SequencerRunAttribute s WHERE s.sequencerRunAttributeId = :sequencerRunAttributeId"),
  @NamedQuery(name = "SequencerRunAttribute.findByTag", query = "SELECT s FROM SequencerRunAttribute s WHERE s.tag = :tag"),
  @NamedQuery(name = "SequencerRunAttribute.findByValue", query = "SELECT s FROM SequencerRunAttribute s WHERE s.value = :value"),
  @NamedQuery(name = "SequencerRunAttribute.findByUnits", query = "SELECT s FROM SequencerRunAttribute s WHERE s.units = :units")})
public class SequencerRunAttribute implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "sequencer_run_attribute_id")
  private Integer sequencerRunAttributeId;
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
  @JoinColumn(name = "sample_id", referencedColumnName = "sequencer_run_id")
  @ManyToOne(optional = false)
  private SequencerRun sampleId;

  public SequencerRunAttribute() {
  }

  public SequencerRunAttribute(Integer sequencerRunAttributeId) {
    this.sequencerRunAttributeId = sequencerRunAttributeId;
  }

  public SequencerRunAttribute(Integer sequencerRunAttributeId, String tag, String value) {
    this.sequencerRunAttributeId = sequencerRunAttributeId;
    this.tag = tag;
    this.value = value;
  }

  public Integer getSequencerRunAttributeId() {
    return sequencerRunAttributeId;
  }

  public void setSequencerRunAttributeId(Integer sequencerRunAttributeId) {
    this.sequencerRunAttributeId = sequencerRunAttributeId;
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

  public SequencerRun getSampleId() {
    return sampleId;
  }

  public void setSampleId(SequencerRun sampleId) {
    this.sampleId = sampleId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (sequencerRunAttributeId != null ? sequencerRunAttributeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof SequencerRunAttribute)) {
      return false;
    }
    SequencerRunAttribute other = (SequencerRunAttribute) object;
    if ((this.sequencerRunAttributeId == null && other.sequencerRunAttributeId != null) || (this.sequencerRunAttributeId != null && !this.sequencerRunAttributeId.equals(other.sequencerRunAttributeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.SequencerRunAttribute[ sequencerRunAttributeId=" + sequencerRunAttributeId + " ]";
  }
  
}
