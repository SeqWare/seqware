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
@Table(name = "share_experiment")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ShareExperiment.findAll", query = "SELECT s FROM ShareExperiment s"),
  @NamedQuery(name = "ShareExperiment.findByShareExperimentId", query = "SELECT s FROM ShareExperiment s WHERE s.shareExperimentId = :shareExperimentId"),
  @NamedQuery(name = "ShareExperiment.findByActive", query = "SELECT s FROM ShareExperiment s WHERE s.active = :active"),
  @NamedQuery(name = "ShareExperiment.findBySwAccession", query = "SELECT s FROM ShareExperiment s WHERE s.swAccession = :swAccession"),
  @NamedQuery(name = "ShareExperiment.findByCreateTstmp", query = "SELECT s FROM ShareExperiment s WHERE s.createTstmp = :createTstmp"),
  @NamedQuery(name = "ShareExperiment.findByUpdateTstmp", query = "SELECT s FROM ShareExperiment s WHERE s.updateTstmp = :updateTstmp")})
public class ShareExperiment implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "share_experiment_id")
  private Integer shareExperimentId;
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
  @JoinColumn(name = "registration_id", referencedColumnName = "registration_id")
  @ManyToOne(optional = false)
  private Registration registrationId;
  @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")
  @ManyToOne(optional = false)
  private Experiment experimentId;

  public ShareExperiment() {
  }

  public ShareExperiment(Integer shareExperimentId) {
    this.shareExperimentId = shareExperimentId;
  }

  public ShareExperiment(Integer shareExperimentId, Date createTstmp) {
    this.shareExperimentId = shareExperimentId;
    this.createTstmp = createTstmp;
  }

  public Integer getShareExperimentId() {
    return shareExperimentId;
  }

  public void setShareExperimentId(Integer shareExperimentId) {
    this.shareExperimentId = shareExperimentId;
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

  public Registration getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(Registration registrationId) {
    this.registrationId = registrationId;
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
    hash += (shareExperimentId != null ? shareExperimentId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ShareExperiment)) {
      return false;
    }
    ShareExperiment other = (ShareExperiment) object;
    if ((this.shareExperimentId == null && other.shareExperimentId != null) || (this.shareExperimentId != null && !this.shareExperimentId.equals(other.shareExperimentId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ShareExperiment[ shareExperimentId=" + shareExperimentId + " ]";
  }
  
}
