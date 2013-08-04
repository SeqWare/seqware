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
@Table(name = "processing_ius")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ProcessingIus.findAll", query = "SELECT p FROM ProcessingIus p"),
  @NamedQuery(name = "ProcessingIus.findByProcessingIusId", query = "SELECT p FROM ProcessingIus p WHERE p.processingIusId = :processingIusId"),
  @NamedQuery(name = "ProcessingIus.findByDescription", query = "SELECT p FROM ProcessingIus p WHERE p.description = :description"),
  @NamedQuery(name = "ProcessingIus.findByLabel", query = "SELECT p FROM ProcessingIus p WHERE p.label = :label"),
  @NamedQuery(name = "ProcessingIus.findByUrl", query = "SELECT p FROM ProcessingIus p WHERE p.url = :url"),
  @NamedQuery(name = "ProcessingIus.findBySwAccession", query = "SELECT p FROM ProcessingIus p WHERE p.swAccession = :swAccession")})
public class ProcessingIus implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "processing_ius_id")
  private Integer processingIusId;
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
  @JoinColumn(name = "ius_id", referencedColumnName = "ius_id")
  @ManyToOne(optional = false)
  private Ius iusId;

  public ProcessingIus() {
  }

  public ProcessingIus(Integer processingIusId) {
    this.processingIusId = processingIusId;
  }

  public Integer getProcessingIusId() {
    return processingIusId;
  }

  public void setProcessingIusId(Integer processingIusId) {
    this.processingIusId = processingIusId;
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

  public Ius getIusId() {
    return iusId;
  }

  public void setIusId(Ius iusId) {
    this.iusId = iusId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (processingIusId != null ? processingIusId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ProcessingIus)) {
      return false;
    }
    ProcessingIus other = (ProcessingIus) object;
    if ((this.processingIusId == null && other.processingIusId != null) || (this.processingIusId != null && !this.processingIusId.equals(other.processingIusId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ProcessingIus[ processingIusId=" + processingIusId + " ]";
  }
  
}
