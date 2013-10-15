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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "processing_studies")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ProcessingStudies.findAll", query = "SELECT p FROM ProcessingStudies p"),
  @NamedQuery(name = "ProcessingStudies.findByProcessingStudiesId", query = "SELECT p FROM ProcessingStudies p WHERE p.processingStudiesId = :processingStudiesId"),
  @NamedQuery(name = "ProcessingStudies.findByDescription", query = "SELECT p FROM ProcessingStudies p WHERE p.description = :description"),
  @NamedQuery(name = "ProcessingStudies.findByLabel", query = "SELECT p FROM ProcessingStudies p WHERE p.label = :label"),
  @NamedQuery(name = "ProcessingStudies.findByUrl", query = "SELECT p FROM ProcessingStudies p WHERE p.url = :url"),
  @NamedQuery(name = "ProcessingStudies.findBySwAccession", query = "SELECT p FROM ProcessingStudies p WHERE p.swAccession = :swAccession")})
public class ProcessingStudies implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "processing_studies_id")
  private Integer processingStudiesId;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @Size(max = 2147483647)
  @Column(name = "label")
  private String label;
  @Size(max = 2147483647)
  @Column(name = "url")
  private String url;
  @Column(name = "sw_accession")
  private Integer swAccession;
  @JoinColumn(name = "study_id", referencedColumnName = "study_id")
  @ManyToOne(optional = false)
  private Study studyId;
  @JoinColumn(name = "processing_id", referencedColumnName = "processing_id")
  @ManyToOne(optional = false)
  private Processing processingId;

  public ProcessingStudies() {
  }

  public ProcessingStudies(Integer processingStudiesId) {
    this.processingStudiesId = processingStudiesId;
  }

  public Integer getProcessingStudiesId() {
    return processingStudiesId;
  }

  public void setProcessingStudiesId(Integer processingStudiesId) {
    this.processingStudiesId = processingStudiesId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Integer getSwAccession() {
    return swAccession;
  }

  public void setSwAccession(Integer swAccession) {
    this.swAccession = swAccession;
  }

  public Study getStudyId() {
    return studyId;
  }

  public void setStudyId(Study studyId) {
    this.studyId = studyId;
  }

  public Processing getProcessingId() {
    return processingId;
  }

  public void setProcessingId(Processing processingId) {
    this.processingId = processingId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (processingStudiesId != null ? processingStudiesId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ProcessingStudies)) {
      return false;
    }
    ProcessingStudies other = (ProcessingStudies) object;
    if ((this.processingStudiesId == null && other.processingStudiesId != null) || (this.processingStudiesId != null && !this.processingStudiesId.equals(other.processingStudiesId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ProcessingStudies[ processingStudiesId=" + processingStudiesId + " ]";
  }
  
}
