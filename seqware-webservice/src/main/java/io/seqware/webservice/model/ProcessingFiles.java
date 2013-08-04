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
@Table(name = "processing_files")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ProcessingFiles.findAll", query = "SELECT p FROM ProcessingFiles p"),
  @NamedQuery(name = "ProcessingFiles.findByProcessingFilesId", query = "SELECT p FROM ProcessingFiles p WHERE p.processingFilesId = :processingFilesId")})
public class ProcessingFiles implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "processing_files_id")
  private Integer processingFilesId;
  @JoinColumn(name = "processing_id", referencedColumnName = "processing_id")
  @ManyToOne(optional = false)
  private Processing processingId;
  @JoinColumn(name = "file_id", referencedColumnName = "file_id")
  @ManyToOne(optional = false)
  private File fileId;

  public ProcessingFiles() {
  }

  public ProcessingFiles(Integer processingFilesId) {
    this.processingFilesId = processingFilesId;
  }

  public Integer getProcessingFilesId() {
    return processingFilesId;
  }

  public void setProcessingFilesId(Integer processingFilesId) {
    this.processingFilesId = processingFilesId;
  }

  public Processing getProcessingId() {
    return processingId;
  }

  public void setProcessingId(Processing processingId) {
    this.processingId = processingId;
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
    hash += (processingFilesId != null ? processingFilesId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ProcessingFiles)) {
      return false;
    }
    ProcessingFiles other = (ProcessingFiles) object;
    if ((this.processingFilesId == null && other.processingFilesId != null) || (this.processingFilesId != null && !this.processingFilesId.equals(other.processingFilesId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ProcessingFiles[ processingFilesId=" + processingFilesId + " ]";
  }
  
}
