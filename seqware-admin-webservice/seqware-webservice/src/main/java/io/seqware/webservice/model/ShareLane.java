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
@Table(name = "share_lane")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ShareLane.findAll", query = "SELECT s FROM ShareLane s"),
  @NamedQuery(name = "ShareLane.findByActive", query = "SELECT s FROM ShareLane s WHERE s.active = :active"),
  @NamedQuery(name = "ShareLane.findBySwAccession", query = "SELECT s FROM ShareLane s WHERE s.swAccession = :swAccession"),
  @NamedQuery(name = "ShareLane.findByCreateTstmp", query = "SELECT s FROM ShareLane s WHERE s.createTstmp = :createTstmp"),
  @NamedQuery(name = "ShareLane.findByUpdateTstmp", query = "SELECT s FROM ShareLane s WHERE s.updateTstmp = :updateTstmp"),
  @NamedQuery(name = "ShareLane.findByShareLaneId", query = "SELECT s FROM ShareLane s WHERE s.shareLaneId = :shareLaneId")})
public class ShareLane implements Serializable {
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
  @Column(name = "share_lane_id")
  private Integer shareLaneId;
  @JoinColumn(name = "registration_id", referencedColumnName = "registration_id")
  @ManyToOne(optional = false)
  private Registration registrationId;
  @JoinColumn(name = "lane_id", referencedColumnName = "lane_id")
  @ManyToOne(optional = false)
  private Lane laneId;

  public ShareLane() {
  }

  public ShareLane(Integer shareLaneId) {
    this.shareLaneId = shareLaneId;
  }

  public ShareLane(Integer shareLaneId, Date createTstmp) {
    this.shareLaneId = shareLaneId;
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

  public Integer getShareLaneId() {
    return shareLaneId;
  }

  public void setShareLaneId(Integer shareLaneId) {
    this.shareLaneId = shareLaneId;
  }

  public Registration getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(Registration registrationId) {
    this.registrationId = registrationId;
  }

  public Lane getLaneId() {
    return laneId;
  }

  public void setLaneId(Lane laneId) {
    this.laneId = laneId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (shareLaneId != null ? shareLaneId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ShareLane)) {
      return false;
    }
    ShareLane other = (ShareLane) object;
    if ((this.shareLaneId == null && other.shareLaneId != null) || (this.shareLaneId != null && !this.shareLaneId.equals(other.shareLaneId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ShareLane[ shareLaneId=" + shareLaneId + " ]";
  }
  
}
