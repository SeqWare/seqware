/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
@Table(name = "ius_link")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "IusLink.findAll", query = "SELECT i FROM IusLink i"),
  @NamedQuery(name = "IusLink.findByIusLinkId", query = "SELECT i FROM IusLink i WHERE i.iusLinkId = :iusLinkId"),
  @NamedQuery(name = "IusLink.findByLabel", query = "SELECT i FROM IusLink i WHERE i.label = :label"),
  @NamedQuery(name = "IusLink.findByUrl", query = "SELECT i FROM IusLink i WHERE i.url = :url"),
  @NamedQuery(name = "IusLink.findByDb", query = "SELECT i FROM IusLink i WHERE i.db = :db"),
  @NamedQuery(name = "IusLink.findById", query = "SELECT i FROM IusLink i WHERE i.id = :id")})
public class IusLink implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "ius_link_id")
  private Integer iusLinkId;
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
  @JoinColumn(name = "ius_id", referencedColumnName = "ius_id")
  @ManyToOne(optional = false)
  private Ius iusId;

  public IusLink() {
  }

  public IusLink(Integer iusLinkId) {
    this.iusLinkId = iusLinkId;
  }

  public IusLink(Integer iusLinkId, String label, String url) {
    this.iusLinkId = iusLinkId;
    this.label = label;
    this.url = url;
  }

  public Integer getIusLinkId() {
    return iusLinkId;
  }

  public void setIusLinkId(Integer iusLinkId) {
    this.iusLinkId = iusLinkId;
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

  public Ius getIusId() {
    return iusId;
  }

  public void setIusId(Ius iusId) {
    this.iusId = iusId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (iusLinkId != null ? iusLinkId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof IusLink)) {
      return false;
    }
    IusLink other = (IusLink) object;
    if ((this.iusLinkId == null && other.iusLinkId != null) || (this.iusLinkId != null && !this.iusLinkId.equals(other.iusLinkId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.IusLink[ iusLinkId=" + iusLinkId + " ]";
  }
  
}
