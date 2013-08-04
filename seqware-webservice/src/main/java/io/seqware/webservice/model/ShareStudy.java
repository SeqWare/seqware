/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.model;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "share_study")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ShareStudy.findAll", query = "SELECT s FROM ShareStudy s"),
  @NamedQuery(name = "ShareStudy.findByShareStudyId", query = "SELECT s FROM ShareStudy s WHERE s.shareStudyId = :shareStudyId"),
  @NamedQuery(name = "ShareStudy.findByActive", query = "SELECT s FROM ShareStudy s WHERE s.active = :active"),
  @NamedQuery(name = "ShareStudy.findBySwAccession", query = "SELECT s FROM ShareStudy s WHERE s.swAccession = :swAccession"),
  @NamedQuery(name = "ShareStudy.findByCreateTstmp", query = "SELECT s FROM ShareStudy s WHERE s.createTstmp = :createTstmp"),
  @NamedQuery(name = "ShareStudy.findByUpdateTstmp", query = "SELECT s FROM ShareStudy s WHERE s.updateTstmp = :updateTstmp")})
public class ShareStudy implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "share_study_id")
  private Integer shareStudyId;
  @Column(name = "active")
  private Boolean active;
  @Column(name = "sw_accession")
  private Integer swAccession;
  @Basic(optional = false)
  @NotNull
  @Column(name = "create_tstmp")
  @Temporal(TemporalType.TIMESTAMP)
  private Date createTstmp;
  @Column(name = "update_tstmp")
  @Temporal(TemporalType.TIMESTAMP)
  private Date updateTstmp;
  @JoinColumn(name = "study_id", referencedColumnName = "study_id")
  @ManyToOne(optional = false)
  private Study studyId;
  @JoinColumn(name = "registration_id", referencedColumnName = "registration_id")
  @ManyToOne(optional = false)
  private Registration registrationId;

  public ShareStudy() {
  }

  public ShareStudy(Integer shareStudyId) {
    this.shareStudyId = shareStudyId;
  }

  public ShareStudy(Integer shareStudyId, Date createTstmp) {
    this.shareStudyId = shareStudyId;
    this.createTstmp = createTstmp;
  }

  public Integer getShareStudyId() {
    return shareStudyId;
  }

  public void setShareStudyId(Integer shareStudyId) {
    this.shareStudyId = shareStudyId;
  }

  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  public Integer getSwAccession() {
    return swAccession;
  }

  public void setSwAccession(Integer swAccession) {
    this.swAccession = swAccession;
  }

  public Date getCreateTstmp() {
    return createTstmp;
  }

  public void setCreateTstmp(Date createTstmp) {
    this.createTstmp = createTstmp;
  }

  public Date getUpdateTstmp() {
    return updateTstmp;
  }

  public void setUpdateTstmp(Date updateTstmp) {
    this.updateTstmp = updateTstmp;
  }

  public Study getStudyId() {
    return studyId;
  }

  public void setStudyId(Study studyId) {
    this.studyId = studyId;
  }

  public Registration getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(Registration registrationId) {
    this.registrationId = registrationId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (shareStudyId != null ? shareStudyId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ShareStudy)) {
      return false;
    }
    ShareStudy other = (ShareStudy) object;
    if ((this.shareStudyId == null && other.shareStudyId != null) || (this.shareStudyId != null && !this.shareStudyId.equals(other.shareStudyId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ShareStudy[ shareStudyId=" + shareStudyId + " ]";
  }
  
}
