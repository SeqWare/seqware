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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "invoice")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Invoice.findAll", query = "SELECT i FROM Invoice i"),
  @NamedQuery(name = "Invoice.findByInvoiceId", query = "SELECT i FROM Invoice i WHERE i.invoiceId = :invoiceId"),
  @NamedQuery(name = "Invoice.findByStartDate", query = "SELECT i FROM Invoice i WHERE i.startDate = :startDate"),
  @NamedQuery(name = "Invoice.findByEndDate", query = "SELECT i FROM Invoice i WHERE i.endDate = :endDate"),
  @NamedQuery(name = "Invoice.findByState", query = "SELECT i FROM Invoice i WHERE i.state = :state"),
  @NamedQuery(name = "Invoice.findByFinalized", query = "SELECT i FROM Invoice i WHERE i.finalized = :finalized"),
  @NamedQuery(name = "Invoice.findByFullyPaid", query = "SELECT i FROM Invoice i WHERE i.fullyPaid = :fullyPaid"),
  @NamedQuery(name = "Invoice.findByPaidAmount", query = "SELECT i FROM Invoice i WHERE i.paidAmount = :paidAmount"),
  @NamedQuery(name = "Invoice.findByDaysUntilDue", query = "SELECT i FROM Invoice i WHERE i.daysUntilDue = :daysUntilDue"),
  @NamedQuery(name = "Invoice.findByExternalId", query = "SELECT i FROM Invoice i WHERE i.externalId = :externalId"),
  @NamedQuery(name = "Invoice.findByClientNotes", query = "SELECT i FROM Invoice i WHERE i.clientNotes = :clientNotes"),
  @NamedQuery(name = "Invoice.findByNotes", query = "SELECT i FROM Invoice i WHERE i.notes = :notes"),
  @NamedQuery(name = "Invoice.findBySwAccession", query = "SELECT i FROM Invoice i WHERE i.swAccession = :swAccession"),
  @NamedQuery(name = "Invoice.findByCreateTstmp", query = "SELECT i FROM Invoice i WHERE i.createTstmp = :createTstmp")})
public class Invoice implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "invoice_id")
  private Integer invoiceId;
  @Basic(optional = false)
  @NotNull
  @Column(name = "start_date")
  @Temporal(TemporalType.DATE)
  private Date startDate;
  @Basic(optional = false)
  @NotNull
  @Column(name = "end_date")
  @Temporal(TemporalType.DATE)
  private Date endDate;
  @Size(max = 2147483647)
  @Column(name = "state")
  private String state;
  @Column(name = "finalized")
  private Boolean finalized;
  @Column(name = "fully_paid")
  private Boolean fullyPaid;
  @Column(name = "paid_amount")
  private BigInteger paidAmount;
  @Column(name = "days_until_due")
  private Integer daysUntilDue;
  @Size(max = 2147483647)
  @Column(name = "external_id")
  private String externalId;
  @Size(max = 2147483647)
  @Column(name = "client_notes")
  private String clientNotes;
  @Size(max = 2147483647)
  @Column(name = "notes")
  private String notes;
  @Column(name = "sw_accession")
  private Integer swAccession;
  @Basic(optional = false)
  @NotNull
  @Column(name = "create_tstmp")
  @Temporal(TemporalType.TIMESTAMP)
  private Date createTstmp;
  @JoinColumn(name = "owner_id", referencedColumnName = "registration_id")
  @ManyToOne(optional = false)
  private Registration ownerId;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "invoiceId")
  private Collection<InvoiceAttribute> invoiceAttributeCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "invoiceId")
  private Collection<Expense> expenseCollection;

  public Invoice() {
  }

  public Invoice(Integer invoiceId) {
    this.invoiceId = invoiceId;
  }

  public Invoice(Integer invoiceId, Date startDate, Date endDate, Date createTstmp) {
    this.invoiceId = invoiceId;
    this.startDate = startDate;
    this.endDate = endDate;
    this.createTstmp = createTstmp;
  }

  public Integer getInvoiceId() {
    return invoiceId;
  }

  public void setInvoiceId(Integer invoiceId) {
    this.invoiceId = invoiceId;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public Boolean getFinalized() {
    return finalized;
  }

  public void setFinalized(Boolean finalized) {
    this.finalized = finalized;
  }

  public Boolean getFullyPaid() {
    return fullyPaid;
  }

  public void setFullyPaid(Boolean fullyPaid) {
    this.fullyPaid = fullyPaid;
  }

  public BigInteger getPaidAmount() {
    return paidAmount;
  }

  public void setPaidAmount(BigInteger paidAmount) {
    this.paidAmount = paidAmount;
  }

  public Integer getDaysUntilDue() {
    return daysUntilDue;
  }

  public void setDaysUntilDue(Integer daysUntilDue) {
    this.daysUntilDue = daysUntilDue;
  }

  public String getExternalId() {
    return externalId;
  }

  public void setExternalId(String externalId) {
    this.externalId = externalId;
  }

  public String getClientNotes() {
    return clientNotes;
  }

  public void setClientNotes(String clientNotes) {
    this.clientNotes = clientNotes;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public Integer getSwAccession() {
    return swAccession;
  }

  public void setSwAccession(Integer swAccession) {
    this.swAccession = swAccession;
  }

  public Date getCreateTstmp() {
    return createTstmp;
  }

  public void setCreateTstmp(Date createTstmp) {
    this.createTstmp = createTstmp;
  }

  public Registration getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Registration ownerId) {
    this.ownerId = ownerId;
  }

  @XmlTransient
  public Collection<InvoiceAttribute> getInvoiceAttributeCollection() {
    return invoiceAttributeCollection;
  }

  public void setInvoiceAttributeCollection(Collection<InvoiceAttribute> invoiceAttributeCollection) {
    this.invoiceAttributeCollection = invoiceAttributeCollection;
  }

  @XmlTransient
  public Collection<Expense> getExpenseCollection() {
    return expenseCollection;
  }

  public void setExpenseCollection(Collection<Expense> expenseCollection) {
    this.expenseCollection = expenseCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (invoiceId != null ? invoiceId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Invoice)) {
      return false;
    }
    Invoice other = (Invoice) object;
    if ((this.invoiceId == null && other.invoiceId != null) || (this.invoiceId != null && !this.invoiceId.equals(other.invoiceId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.Invoice[ invoiceId=" + invoiceId + " ]";
  }
  
}
