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
@Table(name = "share_workflow_run")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ShareWorkflowRun.findAll", query = "SELECT s FROM ShareWorkflowRun s"),
  @NamedQuery(name = "ShareWorkflowRun.findByShareWorkflowRunId", query = "SELECT s FROM ShareWorkflowRun s WHERE s.shareWorkflowRunId = :shareWorkflowRunId"),
  @NamedQuery(name = "ShareWorkflowRun.findByActive", query = "SELECT s FROM ShareWorkflowRun s WHERE s.active = :active"),
  @NamedQuery(name = "ShareWorkflowRun.findBySwAccession", query = "SELECT s FROM ShareWorkflowRun s WHERE s.swAccession = :swAccession"),
  @NamedQuery(name = "ShareWorkflowRun.findByCreateTstmp", query = "SELECT s FROM ShareWorkflowRun s WHERE s.createTstmp = :createTstmp"),
  @NamedQuery(name = "ShareWorkflowRun.findByUpdateTstmp", query = "SELECT s FROM ShareWorkflowRun s WHERE s.updateTstmp = :updateTstmp")})
public class ShareWorkflowRun implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "share_workflow_run_id")
  private Integer shareWorkflowRunId;
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
  @JoinColumn(name = "workflow_run_id", referencedColumnName = "workflow_run_id")
  @ManyToOne(optional = false)
  private WorkflowRun workflowRunId;
  @JoinColumn(name = "registration_id", referencedColumnName = "registration_id")
  @ManyToOne(optional = false)
  private Registration registrationId;

  public ShareWorkflowRun() {
  }

  public ShareWorkflowRun(Integer shareWorkflowRunId) {
    this.shareWorkflowRunId = shareWorkflowRunId;
  }

  public ShareWorkflowRun(Integer shareWorkflowRunId, Date createTstmp) {
    this.shareWorkflowRunId = shareWorkflowRunId;
    this.createTstmp = createTstmp;
  }

  public Integer getShareWorkflowRunId() {
    return shareWorkflowRunId;
  }

  public void setShareWorkflowRunId(Integer shareWorkflowRunId) {
    this.shareWorkflowRunId = shareWorkflowRunId;
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

  public WorkflowRun getWorkflowRunId() {
    return workflowRunId;
  }

  public void setWorkflowRunId(WorkflowRun workflowRunId) {
    this.workflowRunId = workflowRunId;
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
    hash += (shareWorkflowRunId != null ? shareWorkflowRunId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ShareWorkflowRun)) {
      return false;
    }
    ShareWorkflowRun other = (ShareWorkflowRun) object;
    if ((this.shareWorkflowRunId == null && other.shareWorkflowRunId != null) || (this.shareWorkflowRunId != null && !this.shareWorkflowRunId.equals(other.shareWorkflowRunId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ShareWorkflowRun[ shareWorkflowRunId=" + shareWorkflowRunId + " ]";
  }
  
}
