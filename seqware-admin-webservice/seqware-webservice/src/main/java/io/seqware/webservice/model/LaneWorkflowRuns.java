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
@Table(name = "lane_workflow_runs")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "LaneWorkflowRuns.findAll", query = "SELECT l FROM LaneWorkflowRuns l"),
  @NamedQuery(name = "LaneWorkflowRuns.findByLaneWorkflowRunsId", query = "SELECT l FROM LaneWorkflowRuns l WHERE l.laneWorkflowRunsId = :laneWorkflowRunsId")})
public class LaneWorkflowRuns implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "lane_workflow_runs_id")
  private Integer laneWorkflowRunsId;
  @JoinColumn(name = "workflow_run_id", referencedColumnName = "workflow_run_id")
  @ManyToOne(optional = false)
  private WorkflowRun workflowRunId;
  @JoinColumn(name = "lane_id", referencedColumnName = "lane_id")
  @ManyToOne(optional = false)
  private Lane laneId;

  public LaneWorkflowRuns() {
  }

  public LaneWorkflowRuns(Integer laneWorkflowRunsId) {
    this.laneWorkflowRunsId = laneWorkflowRunsId;
  }

  public Integer getLaneWorkflowRunsId() {
    return laneWorkflowRunsId;
  }

  public void setLaneWorkflowRunsId(Integer laneWorkflowRunsId) {
    this.laneWorkflowRunsId = laneWorkflowRunsId;
  }

  public WorkflowRun getWorkflowRunId() {
    return workflowRunId;
  }

  public void setWorkflowRunId(WorkflowRun workflowRunId) {
    this.workflowRunId = workflowRunId;
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
    hash += (laneWorkflowRunsId != null ? laneWorkflowRunsId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof LaneWorkflowRuns)) {
      return false;
    }
    LaneWorkflowRuns other = (LaneWorkflowRuns) object;
    if ((this.laneWorkflowRunsId == null && other.laneWorkflowRunsId != null) || (this.laneWorkflowRunsId != null && !this.laneWorkflowRunsId.equals(other.laneWorkflowRunsId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.LaneWorkflowRuns[ laneWorkflowRunsId=" + laneWorkflowRunsId + " ]";
  }
  
}
