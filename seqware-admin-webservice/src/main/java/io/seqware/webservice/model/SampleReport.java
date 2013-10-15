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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "sample_report")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "SampleReport.findAll", query = "SELECT s FROM SampleReport s"),
  @NamedQuery(name = "SampleReport.findByStudyId", query = "SELECT s FROM SampleReport s WHERE s.studyId = :studyId"),
  @NamedQuery(name = "SampleReport.findByChildSampleId", query = "SELECT s FROM SampleReport s WHERE s.childSampleId = :childSampleId"),
  @NamedQuery(name = "SampleReport.findByWorkflowId", query = "SELECT s FROM SampleReport s WHERE s.workflowId = :workflowId"),
  @NamedQuery(name = "SampleReport.findByStatus", query = "SELECT s FROM SampleReport s WHERE s.status = :status"),
  @NamedQuery(name = "SampleReport.findBySequencerRunId", query = "SELECT s FROM SampleReport s WHERE s.sequencerRunId = :sequencerRunId"),
  @NamedQuery(name = "SampleReport.findByLaneId", query = "SELECT s FROM SampleReport s WHERE s.laneId = :laneId"),
  @NamedQuery(name = "SampleReport.findByIusId", query = "SELECT s FROM SampleReport s WHERE s.iusId = :iusId"),
  @NamedQuery(name = "SampleReport.findByRowId", query = "SELECT s FROM SampleReport s WHERE s.rowId = :rowId")})
public class SampleReport implements Serializable {
  private static final long serialVersionUID = 1L;
  @Column(name = "study_id")
  private Integer studyId;
  @Column(name = "child_sample_id")
  private Integer childSampleId;
  @Column(name = "workflow_id")
  private Integer workflowId;
  @Size(max = 255)
  @Column(name = "status")
  private String status;
  @Column(name = "sequencer_run_id")
  private Integer sequencerRunId;
  @Column(name = "lane_id")
  private Integer laneId;
  @Column(name = "ius_id")
  private Integer iusId;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "row_id")
  private Integer rowId;

  public SampleReport() {
  }

  public SampleReport(Integer rowId) {
    this.rowId = rowId;
  }

  public Integer getStudyId() {
    return studyId;
  }

  public void setStudyId(Integer studyId) {
    this.studyId = studyId;
  }

  public Integer getChildSampleId() {
    return childSampleId;
  }

  public void setChildSampleId(Integer childSampleId) {
    this.childSampleId = childSampleId;
  }

  public Integer getWorkflowId() {
    return workflowId;
  }

  public void setWorkflowId(Integer workflowId) {
    this.workflowId = workflowId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getSequencerRunId() {
    return sequencerRunId;
  }

  public void setSequencerRunId(Integer sequencerRunId) {
    this.sequencerRunId = sequencerRunId;
  }

  public Integer getLaneId() {
    return laneId;
  }

  public void setLaneId(Integer laneId) {
    this.laneId = laneId;
  }

  public Integer getIusId() {
    return iusId;
  }

  public void setIusId(Integer iusId) {
    this.iusId = iusId;
  }

  public Integer getRowId() {
    return rowId;
  }

  public void setRowId(Integer rowId) {
    this.rowId = rowId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (rowId != null ? rowId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof SampleReport)) {
      return false;
    }
    SampleReport other = (SampleReport) object;
    if ((this.rowId == null && other.rowId != null) || (this.rowId != null && !this.rowId.equals(other.rowId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.SampleReport[ rowId=" + rowId + " ]";
  }
  
}
