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
@Table(name = "study_link")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "StudyLink.findAll", query = "SELECT s FROM StudyLink s"),
  @NamedQuery(name = "StudyLink.findByStudyLinkId", query = "SELECT s FROM StudyLink s WHERE s.studyLinkId = :studyLinkId"),
  @NamedQuery(name = "StudyLink.findByLabel", query = "SELECT s FROM StudyLink s WHERE s.label = :label"),
  @NamedQuery(name = "StudyLink.findByUrl", query = "SELECT s FROM StudyLink s WHERE s.url = :url"),
  @NamedQuery(name = "StudyLink.findByDb", query = "SELECT s FROM StudyLink s WHERE s.db = :db"),
  @NamedQuery(name = "StudyLink.findById", query = "SELECT s FROM StudyLink s WHERE s.id = :id")})
public class StudyLink implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "study_link_id")
  private Integer studyLinkId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "label")
  private String label;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "url")
  private String url;
  @Size(max = 2147483647)
  @Column(name = "db")
  private String db;
  @Size(max = 2147483647)
  @Column(name = "id")
  private String id;
  @JoinColumn(name = "study_id", referencedColumnName = "study_id")
  @ManyToOne(optional = false)
  private Study studyId;

  public StudyLink() {
  }

  public StudyLink(Integer studyLinkId) {
    this.studyLinkId = studyLinkId;
  }

  public StudyLink(Integer studyLinkId, String label, String url) {
    this.studyLinkId = studyLinkId;
    this.label = label;
    this.url = url;
  }

  public Integer getStudyLinkId() {
    return studyLinkId;
  }

  public void setStudyLinkId(Integer studyLinkId) {
    this.studyLinkId = studyLinkId;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getDb() {
    return db;
  }

  public void setDb(String db) {
    this.db = db;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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
    hash += (studyLinkId != null ? studyLinkId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof StudyLink)) {
      return false;
    }
    StudyLink other = (StudyLink) object;
    if ((this.studyLinkId == null && other.studyLinkId != null) || (this.studyLinkId != null && !this.studyLinkId.equals(other.studyLinkId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.StudyLink[ studyLinkId=" + studyLinkId + " ]";
  }
  
}
