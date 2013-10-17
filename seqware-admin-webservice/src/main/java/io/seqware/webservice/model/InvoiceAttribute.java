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
@Table(name = "invoice_attribute")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "InvoiceAttribute.findAll", query = "SELECT i FROM InvoiceAttribute i"),
  @NamedQuery(name = "InvoiceAttribute.findByInvoiceAttributeId", query = "SELECT i FROM InvoiceAttribute i WHERE i.invoiceAttributeId = :invoiceAttributeId"),
  @NamedQuery(name = "InvoiceAttribute.findByTag", query = "SELECT i FROM InvoiceAttribute i WHERE i.tag = :tag"),
  @NamedQuery(name = "InvoiceAttribute.findByValue", query = "SELECT i FROM InvoiceAttribute i WHERE i.value = :value"),
  @NamedQuery(name = "InvoiceAttribute.findByUnits", query = "SELECT i FROM InvoiceAttribute i WHERE i.units = :units")})
public class InvoiceAttribute implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "invoice_attribute_id")
  private Integer invoiceAttributeId;
  @Size(max = 2147483647)
  @Column(name = "tag")
  private String tag;
  @Size(max = 2147483647)
  @Column(name = "value")
  private String value;
  @Size(max = 2147483647)
  @Column(name = "units")
  private String units;
  @JoinColumn(name = "invoice_id", referencedColumnName = "invoice_id")
  @ManyToOne(optional = false)
  private Invoice invoiceId;

  public InvoiceAttribute() {
  }

  public InvoiceAttribute(Integer invoiceAttributeId) {
    this.invoiceAttributeId = invoiceAttributeId;
  }

  public Integer getInvoiceAttributeId() {
    return invoiceAttributeId;
  }

  public void setInvoiceAttributeId(Integer invoiceAttributeId) {
    this.invoiceAttributeId = invoiceAttributeId;
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

  public Invoice getInvoiceId() {
    return invoiceId;
  }

  public void setInvoiceId(Invoice invoiceId) {
    this.invoiceId = invoiceId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (invoiceAttributeId != null ? invoiceAttributeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof InvoiceAttribute)) {
      return false;
    }
    InvoiceAttribute other = (InvoiceAttribute) object;
    if ((this.invoiceAttributeId == null && other.invoiceAttributeId != null) || (this.invoiceAttributeId != null && !this.invoiceAttributeId.equals(other.invoiceAttributeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.InvoiceAttribute[ invoiceAttributeId=" + invoiceAttributeId + " ]";
  }
  
}
