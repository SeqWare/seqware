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
@Table(name = "lane_attribute")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "LaneAttribute.findAll", query = "SELECT l FROM LaneAttribute l"),
  @NamedQuery(name = "LaneAttribute.findByLaneAttributeId", query = "SELECT l FROM LaneAttribute l WHERE l.laneAttributeId = :laneAttributeId"),
  @NamedQuery(name = "LaneAttribute.findByTag", query = "SELECT l FROM LaneAttribute l WHERE l.tag = :tag"),
  @NamedQuery(name = "LaneAttribute.findByValue", query = "SELECT l FROM LaneAttribute l WHERE l.value = :value"),
  @NamedQuery(name = "LaneAttribute.findByUnits", query = "SELECT l FROM LaneAttribute l WHERE l.units = :units")})
public class LaneAttribute implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "lane_attribute_id")
  private Integer laneAttributeId;
  @Size(max = 2147483647)
  @Column(name = "tag")
  private String tag;
  @Size(max = 2147483647)
  @Column(name = "value")
  private String value;
  @Size(max = 2147483647)
  @Column(name = "units")
  private String units;
  @JoinColumn(name = "lane_id", referencedColumnName = "lane_id")
  @ManyToOne(optional = false)
  private Lane laneId;

  public LaneAttribute() {
  }

  public LaneAttribute(Integer laneAttributeId) {
    this.laneAttributeId = laneAttributeId;
  }

  public Integer getLaneAttributeId() {
    return laneAttributeId;
  }

  public void setLaneAttributeId(Integer laneAttributeId) {
    this.laneAttributeId = laneAttributeId;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
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
    hash += (laneAttributeId != null ? laneAttributeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof LaneAttribute)) {
      return false;
    }
    LaneAttribute other = (LaneAttribute) object;
    if ((this.laneAttributeId == null && other.laneAttributeId != null) || (this.laneAttributeId != null && !this.laneAttributeId.equals(other.laneAttributeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.LaneAttribute[ laneAttributeId=" + laneAttributeId + " ]";
  }
  
}
