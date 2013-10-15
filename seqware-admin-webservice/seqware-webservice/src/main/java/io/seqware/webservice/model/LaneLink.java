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
@Table(name = "lane_link")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "LaneLink.findAll", query = "SELECT l FROM LaneLink l"),
  @NamedQuery(name = "LaneLink.findByLaneLinkId", query = "SELECT l FROM LaneLink l WHERE l.laneLinkId = :laneLinkId"),
  @NamedQuery(name = "LaneLink.findByLabel", query = "SELECT l FROM LaneLink l WHERE l.label = :label"),
  @NamedQuery(name = "LaneLink.findByUrl", query = "SELECT l FROM LaneLink l WHERE l.url = :url"),
  @NamedQuery(name = "LaneLink.findByDb", query = "SELECT l FROM LaneLink l WHERE l.db = :db"),
  @NamedQuery(name = "LaneLink.findById", query = "SELECT l FROM LaneLink l WHERE l.id = :id")})
public class LaneLink implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "lane_link_id")
  private Integer laneLinkId;
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
  @JoinColumn(name = "lane_id", referencedColumnName = "lane_id")
  @ManyToOne(optional = false)
  private Lane laneId;

  public LaneLink() {
  }

  public LaneLink(Integer laneLinkId) {
    this.laneLinkId = laneLinkId;
  }

  public LaneLink(Integer laneLinkId, String label, String url) {
    this.laneLinkId = laneLinkId;
    this.label = label;
    this.url = url;
  }

  public Integer getLaneLinkId() {
    return laneLinkId;
  }

  public void setLaneLinkId(Integer laneLinkId) {
    this.laneLinkId = laneLinkId;
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

  public Lane getLaneId() {
    return laneId;
  }

  public void setLaneId(Lane laneId) {
    this.laneId = laneId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (laneLinkId != null ? laneLinkId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof LaneLink)) {
      return false;
    }
    LaneLink other = (LaneLink) object;
    if ((this.laneLinkId == null && other.laneLinkId != null) || (this.laneLinkId != null && !this.laneLinkId.equals(other.laneLinkId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.LaneLink[ laneLinkId=" + laneLinkId + " ]";
  }
  
}
