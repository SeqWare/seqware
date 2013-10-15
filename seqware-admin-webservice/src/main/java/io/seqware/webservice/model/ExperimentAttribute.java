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
@Table(name = "experiment_attribute")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ExperimentAttribute.findAll", query = "SELECT e FROM ExperimentAttribute e"),
  @NamedQuery(name = "ExperimentAttribute.findByExperimentAttributeId", query = "SELECT e FROM ExperimentAttribute e WHERE e.experimentAttributeId = :experimentAttributeId"),
  @NamedQuery(name = "ExperimentAttribute.findByTag", query = "SELECT e FROM ExperimentAttribute e WHERE e.tag = :tag"),
  @NamedQuery(name = "ExperimentAttribute.findByValue", query = "SELECT e FROM ExperimentAttribute e WHERE e.value = :value"),
  @NamedQuery(name = "ExperimentAttribute.findByUnits", query = "SELECT e FROM ExperimentAttribute e WHERE e.units = :units")})
public class ExperimentAttribute implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "experiment_attribute_id")
  private Integer experimentAttributeId;
  @Size(max = 2147483647)
  @Column(name = "tag")
  private String tag;
  @Size(max = 2147483647)
  @Column(name = "value")
  private String value;
  @Size(max = 2147483647)
  @Column(name = "units")
  private String units;
  @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")
  @ManyToOne(optional = false)
  private Experiment experimentId;

  public ExperimentAttribute() {
  }

  public ExperimentAttribute(Integer experimentAttributeId) {
    this.experimentAttributeId = experimentAttributeId;
  }

  public Integer getExperimentAttributeId() {
    return experimentAttributeId;
  }

  public void setExperimentAttributeId(Integer experimentAttributeId) {
    this.experimentAttributeId = experimentAttributeId;
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

  public Experiment getExperimentId() {
    return experimentId;
  }

  public void setExperimentId(Experiment experimentId) {
    this.experimentId = experimentId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (experimentAttributeId != null ? experimentAttributeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ExperimentAttribute)) {
      return false;
    }
    ExperimentAttribute other = (ExperimentAttribute) object;
    if ((this.experimentAttributeId == null && other.experimentAttributeId != null) || (this.experimentAttributeId != null && !this.experimentAttributeId.equals(other.experimentAttributeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ExperimentAttribute[ experimentAttributeId=" + experimentAttributeId + " ]";
  }
  
}
