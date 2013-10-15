/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
@Table(name = "study_type")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "StudyType.findAll", query = "SELECT s FROM StudyType s"),
  @NamedQuery(name = "StudyType.findByStudyTypeId", query = "SELECT s FROM StudyType s WHERE s.studyTypeId = :studyTypeId"),
  @NamedQuery(name = "StudyType.findByName", query = "SELECT s FROM StudyType s WHERE s.name = :name"),
  @NamedQuery(name = "StudyType.findByDescription", query = "SELECT s FROM StudyType s WHERE s.description = :description")})
public class StudyType implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "study_type_id")
  private Integer studyTypeId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "name")
  private String name;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "existingType")
  private Collection<Study> studyCollection;
  @OneToMany(mappedBy = "studyType")
  private Collection<Lane> laneCollection;

  public StudyType() {
  }

  public StudyType(Integer studyTypeId) {
    this.studyTypeId = studyTypeId;
  }

  public StudyType(Integer studyTypeId, String name) {
    this.studyTypeId = studyTypeId;
    this.name = name;
  }

  public Integer getStudyTypeId() {
    return studyTypeId;
  }

  public void setStudyTypeId(Integer studyTypeId) {
    this.studyTypeId = studyTypeId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @XmlTransient
  public Collection<Study> getStudyCollection() {
    return studyCollection;
  }

  public void setStudyCollection(Collection<Study> studyCollection) {
    this.studyCollection = studyCollection;
  }

  @XmlTransient
  public Collection<Lane> getLaneCollection() {
    return laneCollection;
  }

  public void setLaneCollection(Collection<Lane> laneCollection) {
    this.laneCollection = laneCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (studyTypeId != null ? studyTypeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof StudyType)) {
      return false;
    }
    StudyType other = (StudyType) object;
    if ((this.studyTypeId == null && other.studyTypeId != null) || (this.studyTypeId != null && !this.studyTypeId.equals(other.studyTypeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.StudyType[ studyTypeId=" + studyTypeId + " ]";
  }
  
}
