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
@Table(name = "processing_experiments")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ProcessingExperiments.findAll", query = "SELECT p FROM ProcessingExperiments p"),
  @NamedQuery(name = "ProcessingExperiments.findByProcessingExperimentsId", query = "SELECT p FROM ProcessingExperiments p WHERE p.processingExperimentsId = :processingExperimentsId"),
  @NamedQuery(name = "ProcessingExperiments.findByDescription", query = "SELECT p FROM ProcessingExperiments p WHERE p.description = :description"),
  @NamedQuery(name = "ProcessingExperiments.findByLabel", query = "SELECT p FROM ProcessingExperiments p WHERE p.label = :label"),
  @NamedQuery(name = "ProcessingExperiments.findByUrl", query = "SELECT p FROM ProcessingExperiments p WHERE p.url = :url"),
  @NamedQuery(name = "ProcessingExperiments.findBySwAccession", query = "SELECT p FROM ProcessingExperiments p WHERE p.swAccession = :swAccession")})
public class ProcessingExperiments implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "processing_experiments_id")
  private Integer processingExperimentsId;
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
  @JoinColumn(name = "processing_id", referencedColumnName = "processing_id")
  @ManyToOne(optional = false)
  private Processing processingId;
  @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")
  @ManyToOne(optional = false)
  private Experiment experimentId;

  public ProcessingExperiments() {
  }

  public ProcessingExperiments(Integer processingExperimentsId) {
    this.processingExperimentsId = processingExperimentsId;
  }

  public Integer getProcessingExperimentsId() {
    return processingExperimentsId;
  }

  public void setProcessingExperimentsId(Integer processingExperimentsId) {
    this.processingExperimentsId = processingExperimentsId;
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

  public Processing getProcessingId() {
    return processingId;
  }

  public void setProcessingId(Processing processingId) {
    this.processingId = processingId;
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
    hash += (processingExperimentsId != null ? processingExperimentsId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ProcessingExperiments)) {
      return false;
    }
    ProcessingExperiments other = (ProcessingExperiments) object;
    if ((this.processingExperimentsId == null && other.processingExperimentsId != null) || (this.processingExperimentsId != null && !this.processingExperimentsId.equals(other.processingExperimentsId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ProcessingExperiments[ processingExperimentsId=" + processingExperimentsId + " ]";
  }
  
}
