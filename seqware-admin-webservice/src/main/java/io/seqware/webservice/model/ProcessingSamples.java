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
@Table(name = "processing_samples")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ProcessingSamples.findAll", query = "SELECT p FROM ProcessingSamples p"),
  @NamedQuery(name = "ProcessingSamples.findByProcessingSamplesId", query = "SELECT p FROM ProcessingSamples p WHERE p.processingSamplesId = :processingSamplesId"),
  @NamedQuery(name = "ProcessingSamples.findByDescription", query = "SELECT p FROM ProcessingSamples p WHERE p.description = :description"),
  @NamedQuery(name = "ProcessingSamples.findByLabel", query = "SELECT p FROM ProcessingSamples p WHERE p.label = :label"),
  @NamedQuery(name = "ProcessingSamples.findByUrl", query = "SELECT p FROM ProcessingSamples p WHERE p.url = :url"),
  @NamedQuery(name = "ProcessingSamples.findBySwAccession", query = "SELECT p FROM ProcessingSamples p WHERE p.swAccession = :swAccession")})
public class ProcessingSamples implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "processing_samples_id")
  private Integer processingSamplesId;
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
  @JoinColumn(name = "sample_id", referencedColumnName = "sample_id")
  @ManyToOne(optional = false)
  private Sample sampleId;
  @JoinColumn(name = "processing_id", referencedColumnName = "processing_id")
  @ManyToOne(optional = false)
  private Processing processingId;

  public ProcessingSamples() {
  }

  public ProcessingSamples(Integer processingSamplesId) {
    this.processingSamplesId = processingSamplesId;
  }

  public Integer getProcessingSamplesId() {
    return processingSamplesId;
  }

  public void setProcessingSamplesId(Integer processingSamplesId) {
    this.processingSamplesId = processingSamplesId;
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

  public Sample getSampleId() {
    return sampleId;
  }

  public void setSampleId(Sample sampleId) {
    this.sampleId = sampleId;
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
    hash += (processingSamplesId != null ? processingSamplesId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ProcessingSamples)) {
      return false;
    }
    ProcessingSamples other = (ProcessingSamples) object;
    if ((this.processingSamplesId == null && other.processingSamplesId != null) || (this.processingSamplesId != null && !this.processingSamplesId.equals(other.processingSamplesId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ProcessingSamples[ processingSamplesId=" + processingSamplesId + " ]";
  }
  
}
