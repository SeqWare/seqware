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
@Table(name = "ius_attribute")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "IusAttribute.findAll", query = "SELECT i FROM IusAttribute i"),
  @NamedQuery(name = "IusAttribute.findByIusAttributeId", query = "SELECT i FROM IusAttribute i WHERE i.iusAttributeId = :iusAttributeId"),
  @NamedQuery(name = "IusAttribute.findByTag", query = "SELECT i FROM IusAttribute i WHERE i.tag = :tag"),
  @NamedQuery(name = "IusAttribute.findByValue", query = "SELECT i FROM IusAttribute i WHERE i.value = :value"),
  @NamedQuery(name = "IusAttribute.findByUnits", query = "SELECT i FROM IusAttribute i WHERE i.units = :units")})
public class IusAttribute implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ius_attribute_id")
  private Integer iusAttributeId;
  @Size(max = 2147483647)
  @Column(name = "tag")
  private String tag;
  @Size(max = 2147483647)
  @Column(name = "value")
  private String value;
  @Size(max = 2147483647)
  @Column(name = "units")
  private String units;
  @JoinColumn(name = "ius_id", referencedColumnName = "ius_id")
  @ManyToOne(optional = false)
  private Ius iusId;

  public IusAttribute() {
  }

  public IusAttribute(Integer iusAttributeId) {
    this.iusAttributeId = iusAttributeId;
  }

  public Integer getIusAttributeId() {
    return iusAttributeId;
  }

  public void setIusAttributeId(Integer iusAttributeId) {
    this.iusAttributeId = iusAttributeId;
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

  public Ius getIusId() {
    return iusId;
  }

  public void setIusId(Ius iusId) {
    this.iusId = iusId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (iusAttributeId != null ? iusAttributeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof IusAttribute)) {
      return false;
    }
    IusAttribute other = (IusAttribute) object;
    if ((this.iusAttributeId == null && other.iusAttributeId != null) || (this.iusAttributeId != null && !this.iusAttributeId.equals(other.iusAttributeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.IusAttribute[ iusAttributeId=" + iusAttributeId + " ]";
  }
  
}
