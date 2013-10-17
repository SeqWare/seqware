/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "expense")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Expense.findAll", query = "SELECT e FROM Expense e"),
  @NamedQuery(name = "Expense.findByExpenseId", query = "SELECT e FROM Expense e WHERE e.expenseId = :expenseId"),
  @NamedQuery(name = "Expense.findByWorkflowRunId", query = "SELECT e FROM Expense e WHERE e.workflowRunId = :workflowRunId"),
  @NamedQuery(name = "Expense.findByAgent", query = "SELECT e FROM Expense e WHERE e.agent = :agent"),
  @NamedQuery(name = "Expense.findByExpenseType", query = "SELECT e FROM Expense e WHERE e.expenseType = :expenseType"),
  @NamedQuery(name = "Expense.findByDescription", query = "SELECT e FROM Expense e WHERE e.description = :description"),
  @NamedQuery(name = "Expense.findByPricePerUnit", query = "SELECT e FROM Expense e WHERE e.pricePerUnit = :pricePerUnit"),
  @NamedQuery(name = "Expense.findByTotalUnits", query = "SELECT e FROM Expense e WHERE e.totalUnits = :totalUnits"),
  @NamedQuery(name = "Expense.findByTotalPrice", query = "SELECT e FROM Expense e WHERE e.totalPrice = :totalPrice"),
  @NamedQuery(name = "Expense.findByAddedSurcharge", query = "SELECT e FROM Expense e WHERE e.addedSurcharge = :addedSurcharge"),
  @NamedQuery(name = "Expense.findBySwAccession", query = "SELECT e FROM Expense e WHERE e.swAccession = :swAccession"),
  @NamedQuery(name = "Expense.findByExpenseFinishedTstmp", query = "SELECT e FROM Expense e WHERE e.expenseFinishedTstmp = :expenseFinishedTstmp")})
public class Expense implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "expense_id")
  private Integer expenseId;
  @Column(name = "workflow_run_id")
  private Integer workflowRunId;
  @Size(max = 2147483647)
  @Column(name = "agent")
  private String agent;
  @Size(max = 2147483647)
  @Column(name = "expense_type")
  private String expenseType;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @Column(name = "price_per_unit")
  private BigInteger pricePerUnit;
  @Column(name = "total_units")
  private BigInteger totalUnits;
  @Column(name = "total_price")
  private BigInteger totalPrice;
  @Column(name = "added_surcharge")
  private BigInteger addedSurcharge;
  @Column(name = "sw_accession")
  private Integer swAccession;
  @Column(name = "expense_finished_tstmp")
  @Temporal(TemporalType.TIMESTAMP)
  private Date expenseFinishedTstmp;
  @JoinColumn(name = "invoice_id", referencedColumnName = "invoice_id")
  @ManyToOne(optional = false)
  private Invoice invoiceId;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "expenseId")
  private Collection<ExpenseAttribute> expenseAttributeCollection;

  public Expense() {
  }

  public Expense(Integer expenseId) {
    this.expenseId = expenseId;
  }

  public Integer getExpenseId() {
    return expenseId;
  }

  public void setExpenseId(Integer expenseId) {
    this.expenseId = expenseId;
  }

  public Integer getWorkflowRunId() {
    return workflowRunId;
  }

  public void setWorkflowRunId(Integer workflowRunId) {
    this.workflowRunId = workflowRunId;
  }

  public String getAgent() {
    return agent;
  }

  public void setAgent(String agent) {
    this.agent = agent;
  }

  public String getExpenseType() {
    return expenseType;
  }

  public void setExpenseType(String expenseType) {
    this.expenseType = expenseType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigInteger getPricePerUnit() {
    return pricePerUnit;
  }

  public void setPricePerUnit(BigInteger pricePerUnit) {
    this.pricePerUnit = pricePerUnit;
  }

  public BigInteger getTotalUnits() {
    return totalUnits;
  }

  public void setTotalUnits(BigInteger totalUnits) {
    this.totalUnits = totalUnits;
  }

  public BigInteger getTotalPrice() {
    return totalPrice;
  }

  public void setTotalPrice(BigInteger totalPrice) {
    this.totalPrice = totalPrice;
  }

  public BigInteger getAddedSurcharge() {
    return addedSurcharge;
  }

  public void setAddedSurcharge(BigInteger addedSurcharge) {
    this.addedSurcharge = addedSurcharge;
  }

  public Integer getSwAccession() {
    return swAccession;
  }

  public void setSwAccession(Integer swAccession) {
    this.swAccession = swAccession;
  }

  public Date getExpenseFinishedTstmp() {
    return expenseFinishedTstmp;
  }

  public void setExpenseFinishedTstmp(Date expenseFinishedTstmp) {
    this.expenseFinishedTstmp = expenseFinishedTstmp;
  }

  public Invoice getInvoiceId() {
    return invoiceId;
  }

  public void setInvoiceId(Invoice invoiceId) {
    this.invoiceId = invoiceId;
  }

  @XmlTransient
  public Collection<ExpenseAttribute> getExpenseAttributeCollection() {
    return expenseAttributeCollection;
  }

  public void setExpenseAttributeCollection(Collection<ExpenseAttribute> expenseAttributeCollection) {
    this.expenseAttributeCollection = expenseAttributeCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (expenseId != null ? expenseId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Expense)) {
      return false;
    }
    Expense other = (Expense) object;
    if ((this.expenseId == null && other.expenseId != null) || (this.expenseId != null && !this.expenseId.equals(other.expenseId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.Expense[ expenseId=" + expenseId + " ]";
  }
  
}
