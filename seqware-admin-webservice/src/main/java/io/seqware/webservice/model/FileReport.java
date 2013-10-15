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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "file_report")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "FileReport.findAll", query = "SELECT f FROM FileReport f"),
  @NamedQuery(name = "FileReport.findByRowId", query = "SELECT f FROM FileReport f WHERE f.rowId = :rowId"),
  @NamedQuery(name = "FileReport.findByStudyId", query = "SELECT f FROM FileReport f WHERE f.studyId = :studyId"),
  @NamedQuery(name = "FileReport.findByIusId", query = "SELECT f FROM FileReport f WHERE f.iusId = :iusId"),
  @NamedQuery(name = "FileReport.findByLaneId", query = "SELECT f FROM FileReport f WHERE f.laneId = :laneId"),
  @NamedQuery(name = "FileReport.findByFileId", query = "SELECT f FROM FileReport f WHERE f.fileId = :fileId"),
  @NamedQuery(name = "FileReport.findBySampleId", query = "SELECT f FROM FileReport f WHERE f.sampleId = :sampleId"),
  @NamedQuery(name = "FileReport.findByExperimentId", query = "SELECT f FROM FileReport f WHERE f.experimentId = :experimentId"),
  @NamedQuery(name = "FileReport.findByChildSampleId", query = "SELECT f FROM FileReport f WHERE f.childSampleId = :childSampleId"),
  @NamedQuery(name = "FileReport.findByProcessingId", query = "SELECT f FROM FileReport f WHERE f.processingId = :processingId")})
public class FileReport implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "row_id")
  private Integer rowId;
  @Column(name = "study_id")
  private Integer studyId;
  @Column(name = "ius_id")
  private Integer iusId;
  @Column(name = "lane_id")
  private Integer laneId;
  @Column(name = "file_id")
  private Integer fileId;
  @Column(name = "sample_id")
  private Integer sampleId;
  @Column(name = "experiment_id")
  private Integer experimentId;
  @Column(name = "child_sample_id")
  private Integer childSampleId;
  @Column(name = "processing_id")
  private Integer processingId;

  public FileReport() {
  }

  public FileReport(Integer rowId) {
    this.rowId = rowId;
  }

  public Integer getRowId() {
    return rowId;
  }

  public void setRowId(Integer rowId) {
    this.rowId = rowId;
  }

  public Integer getStudyId() {
    return studyId;
  }

  public void setStudyId(Integer studyId) {
    this.studyId = studyId;
  }

  public Integer getIusId() {
    return iusId;
  }

  public void setIusId(Integer iusId) {
    this.iusId = iusId;
  }

  public Integer getLaneId() {
    return laneId;
  }

  public void setLaneId(Integer laneId) {
    this.laneId = laneId;
  }

  public Integer getFileId() {
    return fileId;
  }

  public void setFileId(Integer fileId) {
    this.fileId = fileId;
  }

  public Integer getSampleId() {
    return sampleId;
  }

  public void setSampleId(Integer sampleId) {
    this.sampleId = sampleId;
  }

  public Integer getExperimentId() {
    return experimentId;
  }

  public void setExperimentId(Integer experimentId) {
    this.experimentId = experimentId;
  }

  public Integer getChildSampleId() {
    return childSampleId;
  }

  public void setChildSampleId(Integer childSampleId) {
    this.childSampleId = childSampleId;
  }

  public Integer getProcessingId() {
    return processingId;
  }

  public void setProcessingId(Integer processingId) {
    this.processingId = processingId;
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
    if (!(object instanceof FileReport)) {
      return false;
    }
    FileReport other = (FileReport) object;
    if ((this.rowId == null && other.rowId != null) || (this.rowId != null && !this.rowId.equals(other.rowId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.FileReport[ rowId=" + rowId + " ]";
  }
  
}
