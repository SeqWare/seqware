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
@Table(name = "study_attribute")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "StudyAttribute.findAll", query = "SELECT s FROM StudyAttribute s"),
  @NamedQuery(name = "StudyAttribute.findByStudyAttributeId", query = "SELECT s FROM StudyAttribute s WHERE s.studyAttributeId = :studyAttributeId"),
  @NamedQuery(name = "StudyAttribute.findByTag", query = "SELECT s FROM StudyAttribute s WHERE s.tag = :tag"),
  @NamedQuery(name = "StudyAttribute.findByValue", query = "SELECT s FROM StudyAttribute s WHERE s.value = :value"),
  @NamedQuery(name = "StudyAttribute.findByUnits", query = "SELECT s FROM StudyAttribute s WHERE s.units = :units")})
public class StudyAttribute implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "study_attribute_id")
  private Integer studyAttributeId;
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
  @JoinColumn(name = "study_id", referencedColumnName = "study_id")
  @ManyToOne(optional = false)
  private Study studyId;

  public StudyAttribute() {
  }

  public StudyAttribute(Integer studyAttributeId) {
    this.studyAttributeId = studyAttributeId;
  }

  public StudyAttribute(Integer studyAttributeId, String tag, String value) {
    this.studyAttributeId = studyAttributeId;
    this.tag = tag;
    this.value = value;
  }

  public Integer getStudyAttributeId() {
    return studyAttributeId;
  }

  public void setStudyAttributeId(Integer studyAttributeId) {
    this.studyAttributeId = studyAttributeId;
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

  public Study getStudyId() {
    return studyId;
  }

  public void setStudyId(Study studyId) {
    this.studyId = studyId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (studyAttributeId != null ? studyAttributeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof StudyAttribute)) {
      return false;
    }
    StudyAttribute other = (StudyAttribute) object;
    if ((this.studyAttributeId == null && other.studyAttributeId != null) || (this.studyAttributeId != null && !this.studyAttributeId.equals(other.studyAttributeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.StudyAttribute[ studyAttributeId=" + studyAttributeId + " ]";
  }
  
}
