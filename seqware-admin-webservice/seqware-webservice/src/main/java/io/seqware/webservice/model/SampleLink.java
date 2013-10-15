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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "sample_link")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "SampleLink.findAll", query = "SELECT s FROM SampleLink s"),
  @NamedQuery(name = "SampleLink.findBySampleLinkId", query = "SELECT s FROM SampleLink s WHERE s.sampleLinkId = :sampleLinkId"),
  @NamedQuery(name = "SampleLink.findByLabel", query = "SELECT s FROM SampleLink s WHERE s.label = :label"),
  @NamedQuery(name = "SampleLink.findByUrl", query = "SELECT s FROM SampleLink s WHERE s.url = :url"),
  @NamedQuery(name = "SampleLink.findByDb", query = "SELECT s FROM SampleLink s WHERE s.db = :db"),
  @NamedQuery(name = "SampleLink.findById", query = "SELECT s FROM SampleLink s WHERE s.id = :id")})
public class SampleLink implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "sample_link_id")
  private Integer sampleLinkId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "label")
  private String label;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "url")
  private String url;
  @Size(max = 2147483647)
  @Column(name = "db")
  private String db;
  @Size(max = 2147483647)
  @Column(name = "id")
  private String id;
  @JoinColumn(name = "sample_id", referencedColumnName = "sample_id")
  @ManyToOne(optional = false)
  private Sample sampleId;

  public SampleLink() {
  }

  public SampleLink(Integer sampleLinkId) {
    this.sampleLinkId = sampleLinkId;
  }

  public SampleLink(Integer sampleLinkId, String label, String url) {
    this.sampleLinkId = sampleLinkId;
    this.label = label;
    this.url = url;
  }

  public Integer getSampleLinkId() {
    return sampleLinkId;
  }

  public void setSampleLinkId(Integer sampleLinkId) {
    this.sampleLinkId = sampleLinkId;
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

  public Sample getSampleId() {
    return sampleId;
  }

  public void setSampleId(Sample sampleId) {
    this.sampleId = sampleId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (sampleLinkId != null ? sampleLinkId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof SampleLink)) {
      return false;
    }
    SampleLink other = (SampleLink) object;
    if ((this.sampleLinkId == null && other.sampleLinkId != null) || (this.sampleLinkId != null && !this.sampleLinkId.equals(other.sampleLinkId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.SampleLink[ sampleLinkId=" + sampleLinkId + " ]";
  }
  
}
