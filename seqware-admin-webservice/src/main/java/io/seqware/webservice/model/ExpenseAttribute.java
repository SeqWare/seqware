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
@Table(name = "expense_attribute")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ExpenseAttribute.findAll", query = "SELECT e FROM ExpenseAttribute e"),
  @NamedQuery(name = "ExpenseAttribute.findByExpenseAttributeId", query = "SELECT e FROM ExpenseAttribute e WHERE e.expenseAttributeId = :expenseAttributeId"),
  @NamedQuery(name = "ExpenseAttribute.findByTag", query = "SELECT e FROM ExpenseAttribute e WHERE e.tag = :tag"),
  @NamedQuery(name = "ExpenseAttribute.findByValue", query = "SELECT e FROM ExpenseAttribute e WHERE e.value = :value"),
  @NamedQuery(name = "ExpenseAttribute.findByUnits", query = "SELECT e FROM ExpenseAttribute e WHERE e.units = :units")})
public class ExpenseAttribute implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "expense_attribute_id")
  private Integer expenseAttributeId;
  @Size(max = 2147483647)
  @Column(name = "tag")
  private String tag;
  @Size(max = 2147483647)
  @Column(name = "value")
  private String value;
  @Size(max = 2147483647)
  @Column(name = "units")
  private String units;
  @JoinColumn(name = "expense_id", referencedColumnName = "expense_id")
  @ManyToOne(optional = false)
  private Expense expenseId;

  public ExpenseAttribute() {
  }

  public ExpenseAttribute(Integer expenseAttributeId) {
    this.expenseAttributeId = expenseAttributeId;
  }

  public Integer getExpenseAttributeId() {
    return expenseAttributeId;
  }

  public void setExpenseAttributeId(Integer expenseAttributeId) {
    this.expenseAttributeId = expenseAttributeId;
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

  public Expense getExpenseId() {
    return expenseId;
  }

  public void setExpenseId(Expense expenseId) {
    this.expenseId = expenseId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (expenseAttributeId != null ? expenseAttributeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ExpenseAttribute)) {
      return false;
    }
    ExpenseAttribute other = (ExpenseAttribute) object;
    if ((this.expenseAttributeId == null && other.expenseAttributeId != null) || (this.expenseAttributeId != null && !this.expenseAttributeId.equals(other.expenseAttributeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ExpenseAttribute[ expenseAttributeId=" + expenseAttributeId + " ]";
  }
  
}
