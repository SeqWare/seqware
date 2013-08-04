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
@Table(name = "share_sample")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ShareSample.findAll", query = "SELECT s FROM ShareSample s"),
  @NamedQuery(name = "ShareSample.findByActive", query = "SELECT s FROM ShareSample s WHERE s.active = :active"),
  @NamedQuery(name = "ShareSample.findBySwAccession", query = "SELECT s FROM ShareSample s WHERE s.swAccession = :swAccession"),
  @NamedQuery(name = "ShareSample.findByCreateTstmp", query = "SELECT s FROM ShareSample s WHERE s.createTstmp = :createTstmp"),
  @NamedQuery(name = "ShareSample.findByUpdateTstmp", query = "SELECT s FROM ShareSample s WHERE s.updateTstmp = :updateTstmp"),
  @NamedQuery(name = "ShareSample.findByShareSampleId", query = "SELECT s FROM ShareSample s WHERE s.shareSampleId = :shareSampleId")})
public class ShareSample implements Serializable {
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
  @Column(name = "share_sample_id")
  private Integer shareSampleId;
  @JoinColumn(name = "sample_id", referencedColumnName = "sample_id")
  @ManyToOne(optional = false)
  private Sample sampleId;
  @JoinColumn(name = "registration_id", referencedColumnName = "registration_id")
  @ManyToOne(optional = false)
  private Registration registrationId;

  public ShareSample() {
  }

  public ShareSample(Integer shareSampleId) {
    this.shareSampleId = shareSampleId;
  }

  public ShareSample(Integer shareSampleId, Date createTstmp) {
    this.shareSampleId = shareSampleId;
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

  public Integer getShareSampleId() {
    return shareSampleId;
  }

  public void setShareSampleId(Integer shareSampleId) {
    this.shareSampleId = shareSampleId;
  }

  public Sample getSampleId() {
    return sampleId;
  }

  public void setSampleId(Sample sampleId) {
    this.sampleId = sampleId;
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
    hash += (shareSampleId != null ? shareSampleId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ShareSample)) {
      return false;
    }
    ShareSample other = (ShareSample) object;
    if ((this.shareSampleId == null && other.shareSampleId != null) || (this.shareSampleId != null && !this.shareSampleId.equals(other.shareSampleId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ShareSample[ shareSampleId=" + shareSampleId + " ]";
  }
  
}
