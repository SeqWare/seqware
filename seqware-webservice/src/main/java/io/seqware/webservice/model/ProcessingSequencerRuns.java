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
@Table(name = "processing_sequencer_runs")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ProcessingSequencerRuns.findAll", query = "SELECT p FROM ProcessingSequencerRuns p"),
  @NamedQuery(name = "ProcessingSequencerRuns.findByProcessingSequencerRunsId", query = "SELECT p FROM ProcessingSequencerRuns p WHERE p.processingSequencerRunsId = :processingSequencerRunsId"),
  @NamedQuery(name = "ProcessingSequencerRuns.findByDescription", query = "SELECT p FROM ProcessingSequencerRuns p WHERE p.description = :description"),
  @NamedQuery(name = "ProcessingSequencerRuns.findByLabel", query = "SELECT p FROM ProcessingSequencerRuns p WHERE p.label = :label"),
  @NamedQuery(name = "ProcessingSequencerRuns.findByUrl", query = "SELECT p FROM ProcessingSequencerRuns p WHERE p.url = :url"),
  @NamedQuery(name = "ProcessingSequencerRuns.findBySwAccession", query = "SELECT p FROM ProcessingSequencerRuns p WHERE p.swAccession = :swAccession")})
public class ProcessingSequencerRuns implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "processing_sequencer_runs_id")
  private Integer processingSequencerRunsId;
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
  @JoinColumn(name = "sequencer_run_id", referencedColumnName = "sequencer_run_id")
  @ManyToOne(optional = false)
  private SequencerRun sequencerRunId;
  @JoinColumn(name = "processing_id", referencedColumnName = "processing_id")
  @ManyToOne(optional = false)
  private Processing processingId;

  public ProcessingSequencerRuns() {
  }

  public ProcessingSequencerRuns(Integer processingSequencerRunsId) {
    this.processingSequencerRunsId = processingSequencerRunsId;
  }

  public Integer getProcessingSequencerRunsId() {
    return processingSequencerRunsId;
  }

  public void setProcessingSequencerRunsId(Integer processingSequencerRunsId) {
    this.processingSequencerRunsId = processingSequencerRunsId;
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

  public SequencerRun getSequencerRunId() {
    return sequencerRunId;
  }

  public void setSequencerRunId(SequencerRun sequencerRunId) {
    this.sequencerRunId = sequencerRunId;
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
    hash += (processingSequencerRunsId != null ? processingSequencerRunsId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ProcessingSequencerRuns)) {
      return false;
    }
    ProcessingSequencerRuns other = (ProcessingSequencerRuns) object;
    if ((this.processingSequencerRunsId == null && other.processingSequencerRunsId != null) || (this.processingSequencerRunsId != null && !this.processingSequencerRunsId.equals(other.processingSequencerRunsId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ProcessingSequencerRuns[ processingSequencerRunsId=" + processingSequencerRunsId + " ]";
  }
  
}
