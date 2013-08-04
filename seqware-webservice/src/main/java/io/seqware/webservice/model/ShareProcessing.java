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
@Table(name = "share_processing")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ShareProcessing.findAll", query = "SELECT s FROM ShareProcessing s"),
  @NamedQuery(name = "ShareProcessing.findByActive", query = "SELECT s FROM ShareProcessing s WHERE s.active = :active"),
  @NamedQuery(name = "ShareProcessing.findBySwAccession", query = "SELECT s FROM ShareProcessing s WHERE s.swAccession = :swAccession"),
  @NamedQuery(name = "ShareProcessing.findByCreateTstmp", query = "SELECT s FROM ShareProcessing s WHERE s.createTstmp = :createTstmp"),
  @NamedQuery(name = "ShareProcessing.findByUpdateTstmp", query = "SELECT s FROM ShareProcessing s WHERE s.updateTstmp = :updateTstmp"),
  @NamedQuery(name = "ShareProcessing.findByShareProcessingId", query = "SELECT s FROM ShareProcessing s WHERE s.shareProcessingId = :shareProcessingId")})
public class ShareProcessing implements Serializable {
  private static final long serialVersionUID = 1L;
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
  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "share_processing_id")
  private Integer shareProcessingId;
  @JoinColumn(name = "registration_id", referencedColumnName = "registration_id")
  @ManyToOne(optional = false)
  private Registration registrationId;
  @JoinColumn(name = "processing_id", referencedColumnName = "processing_id")
  @ManyToOne(optional = false)
  private Processing processingId;

  public ShareProcessing() {
  }

  public ShareProcessing(Integer shareProcessingId) {
    this.shareProcessingId = shareProcessingId;
  }

  public ShareProcessing(Integer shareProcessingId, Date createTstmp) {
    this.shareProcessingId = shareProcessingId;
    this.createTstmp = createTstmp;
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

  public Integer getShareProcessingId() {
    return shareProcessingId;
  }

  public void setShareProcessingId(Integer shareProcessingId) {
    this.shareProcessingId = shareProcessingId;
  }

  public Registration getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(Registration registrationId) {
    this.registrationId = registrationId;
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
    hash += (shareProcessingId != null ? shareProcessingId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ShareProcessing)) {
      return false;
    }
    ShareProcessing other = (ShareProcessing) object;
    if ((this.shareProcessingId == null && other.shareProcessingId != null) || (this.shareProcessingId != null && !this.shareProcessingId.equals(other.shareProcessingId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ShareProcessing[ shareProcessingId=" + shareProcessingId + " ]";
  }
  
}
