/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "lane_type")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "LaneType.findAll", query = "SELECT l FROM LaneType l"),
  @NamedQuery(name = "LaneType.findByLaneTypeId", query = "SELECT l FROM LaneType l WHERE l.laneTypeId = :laneTypeId"),
  @NamedQuery(name = "LaneType.findByCode", query = "SELECT l FROM LaneType l WHERE l.code = :code"),
  @NamedQuery(name = "LaneType.findByName", query = "SELECT l FROM LaneType l WHERE l.name = :name")})
public class LaneType implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "lane_type_id")
  private Integer laneTypeId;
  @Size(max = 2147483647)
  @Column(name = "code")
  private String code;
  @Size(max = 2147483647)
  @Column(name = "name")
  private String name;
  @OneToMany(mappedBy = "type")
  private Collection<Lane> laneCollection;

  public LaneType() {
  }

  public LaneType(Integer laneTypeId) {
    this.laneTypeId = laneTypeId;
  }

  public Integer getLaneTypeId() {
    return laneTypeId;
  }

  public void setLaneTypeId(Integer laneTypeId) {
    this.laneTypeId = laneTypeId;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @XmlTransient
  public Collection<Lane> getLaneCollection() {
    return laneCollection;
  }

  public void setLaneCollection(Collection<Lane> laneCollection) {
    this.laneCollection = laneCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (laneTypeId != null ? laneTypeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof LaneType)) {
      return false;
    }
    LaneType other = (LaneType) object;
    if ((this.laneTypeId == null && other.laneTypeId != null) || (this.laneTypeId != null && !this.laneTypeId.equals(other.laneTypeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.LaneType[ laneTypeId=" + laneTypeId + " ]";
  }
  
}
