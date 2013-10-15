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
@Table(name = "share_file")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ShareFile.findAll", query = "SELECT s FROM ShareFile s"),
  @NamedQuery(name = "ShareFile.findByActive", query = "SELECT s FROM ShareFile s WHERE s.active = :active"),
  @NamedQuery(name = "ShareFile.findBySwAccession", query = "SELECT s FROM ShareFile s WHERE s.swAccession = :swAccession"),
  @NamedQuery(name = "ShareFile.findByCreateTstmp", query = "SELECT s FROM ShareFile s WHERE s.createTstmp = :createTstmp"),
  @NamedQuery(name = "ShareFile.findByUpdateTstmp", query = "SELECT s FROM ShareFile s WHERE s.updateTstmp = :updateTstmp"),
  @NamedQuery(name = "ShareFile.findByShareFileId", query = "SELECT s FROM ShareFile s WHERE s.shareFileId = :shareFileId")})
public class ShareFile implements Serializable {
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
  @Column(name = "share_file_id")
  private Integer shareFileId;
  @JoinColumn(name = "registration_id", referencedColumnName = "registration_id")
  @ManyToOne(optional = false)
  private Registration registrationId;
  @JoinColumn(name = "file_id", referencedColumnName = "file_id")
  @ManyToOne(optional = false)
  private File fileId;

  public ShareFile() {
  }

  public ShareFile(Integer shareFileId) {
    this.shareFileId = shareFileId;
  }

  public ShareFile(Integer shareFileId, Date createTstmp) {
    this.shareFileId = shareFileId;
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

  public Integer getShareFileId() {
    return shareFileId;
  }

  public void setShareFileId(Integer shareFileId) {
    this.shareFileId = shareFileId;
  }

  public Registration getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(Registration registrationId) {
    this.registrationId = registrationId;
  }

  public File getFileId() {
    return fileId;
  }

  public void setFileId(File fileId) {
    this.fileId = fileId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (shareFileId != null ? shareFileId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ShareFile)) {
      return false;
    }
    ShareFile other = (ShareFile) object;
    if ((this.shareFileId == null && other.shareFileId != null) || (this.shareFileId != null && !this.shareFileId.equals(other.shareFileId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ShareFile[ shareFileId=" + shareFileId + " ]";
  }
  
}
