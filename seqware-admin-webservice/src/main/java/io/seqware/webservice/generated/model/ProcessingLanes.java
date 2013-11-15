/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.generated.model;

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
@Table(name = "processing_lanes")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ProcessingLanes.findAll", query = "SELECT p FROM ProcessingLanes p"),
  @NamedQuery(name = "ProcessingLanes.findByProcessingLanesId", query = "SELECT p FROM ProcessingLanes p WHERE p.processingLanesId = :processingLanesId"),
  @NamedQuery(name = "ProcessingLanes.findByDescription", query = "SELECT p FROM ProcessingLanes p WHERE p.description = :description"),
  @NamedQuery(name = "ProcessingLanes.findByLabel", query = "SELECT p FROM ProcessingLanes p WHERE p.label = :label"),
  @NamedQuery(name = "ProcessingLanes.findByUrl", query = "SELECT p FROM ProcessingLanes p WHERE p.url = :url"),
  @NamedQuery(name = "ProcessingLanes.findBySwAccession", query = "SELECT p FROM ProcessingLanes p WHERE p.swAccession = :swAccession")})
public class ProcessingLanes implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "processing_lanes_id")
  private Integer processingLanesId;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @Size(max = 2147483647)
  @Column(name = "label")
  private String label;
  @Size(max = 2147483647)
  @Column(name = "url")
  private String url;
  @Column(name = "sw_accession", insertable=false,updatable=false)
  private Integer swAccession;
  @JoinColumn(name = "processing_id", referencedColumnName = "processing_id")
  @ManyToOne(optional = false)
  private Processing processingId;
  @JoinColumn(name = "lane_id", referencedColumnName = "lane_id")
  @ManyToOne(optional = false)
  private Lane laneId;

  public ProcessingLanes() {
  }

  public ProcessingLanes(Integer processingLanesId) {
    this.processingLanesId = processingLanesId;
  }

  public Integer getProcessingLanesId() {
    return processingLanesId;
  }

  public void setProcessingLanesId(Integer processingLanesId) {
    this.processingLanesId = processingLanesId;
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

  public Lane getLaneId() {
    return laneId;
  }

  public void setLaneId(Lane laneId) {
    this.laneId = laneId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (processingLanesId != null ? processingLanesId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ProcessingLanes)) {
      return false;
    }
    ProcessingLanes other = (ProcessingLanes) object;
    if ((this.processingLanesId == null && other.processingLanesId != null) || (this.processingLanesId != null && !this.processingLanesId.equals(other.processingLanesId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ProcessingLanes[ processingLanesId=" + processingLanesId + " ]";
  }
  
}
