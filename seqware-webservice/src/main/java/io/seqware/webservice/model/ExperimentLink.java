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
@Table(name = "experiment_link")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ExperimentLink.findAll", query = "SELECT e FROM ExperimentLink e"),
  @NamedQuery(name = "ExperimentLink.findByExperimentLinkId", query = "SELECT e FROM ExperimentLink e WHERE e.experimentLinkId = :experimentLinkId"),
  @NamedQuery(name = "ExperimentLink.findByLabel", query = "SELECT e FROM ExperimentLink e WHERE e.label = :label"),
  @NamedQuery(name = "ExperimentLink.findByUrl", query = "SELECT e FROM ExperimentLink e WHERE e.url = :url"),
  @NamedQuery(name = "ExperimentLink.findByDb", query = "SELECT e FROM ExperimentLink e WHERE e.db = :db"),
  @NamedQuery(name = "ExperimentLink.findById", query = "SELECT e FROM ExperimentLink e WHERE e.id = :id")})
public class ExperimentLink implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "experiment_link_id")
  private Integer experimentLinkId;
  @Size(max = 2147483647)
  @Column(name = "label")
  private String label;
  @Size(max = 2147483647)
  @Column(name = "url")
  private String url;
  @Size(max = 2147483647)
  @Column(name = "db")
  private String db;
  @Size(max = 2147483647)
  @Column(name = "id")
  private String id;
  @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")
  @ManyToOne(optional = false)
  private Experiment experimentId;

  public ExperimentLink() {
  }

  public ExperimentLink(Integer experimentLinkId) {
    this.experimentLinkId = experimentLinkId;
  }

  public Integer getExperimentLinkId() {
    return experimentLinkId;
  }

  public void setExperimentLinkId(Integer experimentLinkId) {
    this.experimentLinkId = experimentLinkId;
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

  public String getDb() {
    return db;
  }

  public void setDb(String db) {
    this.db = db;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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
    hash += (experimentLinkId != null ? experimentLinkId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ExperimentLink)) {
      return false;
    }
    ExperimentLink other = (ExperimentLink) object;
    if ((this.experimentLinkId == null && other.experimentLinkId != null) || (this.experimentLinkId != null && !this.experimentLinkId.equals(other.experimentLinkId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ExperimentLink[ experimentLinkId=" + experimentLinkId + " ]";
  }
  
}
