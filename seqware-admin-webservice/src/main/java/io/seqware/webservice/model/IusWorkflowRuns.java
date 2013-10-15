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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "ius_workflow_runs")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "IusWorkflowRuns.findAll", query = "SELECT i FROM IusWorkflowRuns i"),
  @NamedQuery(name = "IusWorkflowRuns.findByIusWorkflowRunsId", query = "SELECT i FROM IusWorkflowRuns i WHERE i.iusWorkflowRunsId = :iusWorkflowRunsId")})
public class IusWorkflowRuns implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ius_workflow_runs_id")
  private Integer iusWorkflowRunsId;
  @JoinColumn(name = "workflow_run_id", referencedColumnName = "workflow_run_id")
  @ManyToOne(optional = false)
  private WorkflowRun workflowRunId;
  @JoinColumn(name = "ius_id", referencedColumnName = "ius_id")
  @ManyToOne(optional = false)
  private Ius iusId;

  public IusWorkflowRuns() {
  }

  public IusWorkflowRuns(Integer iusWorkflowRunsId) {
    this.iusWorkflowRunsId = iusWorkflowRunsId;
  }

  public Integer getIusWorkflowRunsId() {
    return iusWorkflowRunsId;
  }

  public void setIusWorkflowRunsId(Integer iusWorkflowRunsId) {
    this.iusWorkflowRunsId = iusWorkflowRunsId;
  }

  public WorkflowRun getWorkflowRunId() {
    return workflowRunId;
  }

  public void setWorkflowRunId(WorkflowRun workflowRunId) {
    this.workflowRunId = workflowRunId;
  }

  public Ius getIusId() {
    return iusId;
  }

  public void setIusId(Ius iusId) {
    this.iusId = iusId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (iusWorkflowRunsId != null ? iusWorkflowRunsId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof IusWorkflowRuns)) {
      return false;
    }
    IusWorkflowRuns other = (IusWorkflowRuns) object;
    if ((this.iusWorkflowRunsId == null && other.iusWorkflowRunsId != null) || (this.iusWorkflowRunsId != null && !this.iusWorkflowRunsId.equals(other.iusWorkflowRunsId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.IusWorkflowRuns[ iusWorkflowRunsId=" + iusWorkflowRunsId + " ]";
  }
  
}
