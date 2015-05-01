package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import net.sourceforge.seqware.common.security.PermissionsAware;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Invoice class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Invoice extends PermissionsAware implements Serializable, Comparable<Invoice>, FirstTierModel {
    /**
     * LEFT OFF WITH: this needs to be finished
     */
    private static final long serialVersionUID = 1L;
    private Integer invoiceId;
    private Registration owner;
    private Date startDate;
    private Date endDate;
    private InvoiceState state;
    private boolean finalized;
    private boolean fullyPaid;
    private Double paidAmount;
    private Integer daysUntilDue;
    private String externalId;
    private String clientNotes;
    private String notes;
    private Integer swAccession;
    private Date createTimestamp;
    private static final Logger LOGGER = LoggerFactory.getLogger(Invoice.class);
    private Set<Expense> expenses = new TreeSet<>();

    // private Set<InvoiceAttribute> invoiceAttributes = new TreeSet<InvoiceAttribute>();

    /**
     * <p>
     * Constructor for Invoice.
     * </p>
     */
    public Invoice() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @param that
     */
    @Override
    public int compareTo(Invoice that) {
        if (that == null) return -1;

        if (that.getSwAccession() == this.getSwAccession()) // when both names are
                                                            // null
            return 0;

        if (that.getSwAccession() == null) return -1; // when only the other name is null

        return (that.getSwAccession().compareTo(this.getSwAccession()));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("swAccession", getSwAccession()).toString();
    }

    /**
     * {@inheritDoc}
     *
     * @param other
     */
    @Override
    public boolean equals(Object other) {
        if ((this == other)) return true;
        if (!(other instanceof Invoice)) return false;
        Invoice castOther = (Invoice) other;
        return new EqualsBuilder().append(this.getSwAccession(), castOther.getSwAccession()).isEquals();
    }

    @Override
    public boolean givesPermissionInternal(Registration registration, Set<Integer> considered) {
        boolean consideredBefore = considered.contains(this.getSwAccession());
        if (!consideredBefore) {
            considered.add(this.getSwAccession());
        } else {
            return true;
        }

        boolean hasPermission = true;
        if (registration == null) {
            hasPermission = false;
        }

        if (!hasPermission) {
            LOGGER.info("Invoice does not give permission");
            throw new SecurityException("User " + registration.getEmailAddress()
                    + " does not have permission to modify aspects of invoice " + this.getSwAccession());
        } else {
            LOGGER.info("Invoices are public by default");
        }
        return hasPermission;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getSwAccession()).toHashCode();
    }

    /**
     * <p>
     * Getter for the field <code>invoiceId</code>.
     * </p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getInvoiceId() {
        return invoiceId;
    }

    /**
     * <p>
     * Setter for the field <code>invoiceId</code>.
     * </p>
     *
     * @param invoiceId
     *            a {@link java.lang.Integer} object.
     */
    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
    }

    /**
     * <p>
     * Getter for the field <code>owner</code>.
     * </p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.Registration} object.
     */
    public Registration getOwner() {
        return owner;
    }

    /**
     * <p>
     * Setter for the field <code>owner</code>.
     * </p>
     *
     * @param owner
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     */
    public void setOwner(Registration owner) {
        this.owner = owner;
    }

    /**
     * <p>
     * Getter for the field <code>startDate</code>.
     * </p>
     *
     * @return a {@link java.util.Date} object.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * <p>
     * Setter for the field <code>startDate</code>.
     * </p>
     *
     * @param startDate
     *            a {@link java.util.Date} object.
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * <p>
     * Getter for the field <code>endDate</code>.
     * </p>
     *
     * @return a {@link java.util.Date} object.
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * <p>
     * Setter for the field <code>endDate</code>.
     * </p>
     *
     * @param endDate
     *            a {@link java.util.Date} object.
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * <p>
     * Getter for the field <code>state</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public InvoiceState getState() {
        return state;
    }

    /**
     * <p>
     * Setter for the field <code>state</code>.
     * </p>
     *
     * @param state
     *            a {@link java.lang.String} object.
     */
    public void setState(InvoiceState state) {
        this.state = state;
    }

    /**
     * <p>
     * isFinalized.
     * </p>
     *
     * @return a boolean.
     */
    public boolean isFinalized() {
        return finalized;
    }

    /**
     * <p>
     * Setter for the field <code>finalized</code>.
     * </p>
     *
     * @param finalized
     *            a boolean.
     */
    public void setFinalized(boolean finalized) {
        this.finalized = finalized;
    }

    /**
     * <p>
     * isFullyPaid.
     * </p>
     *
     * @return a boolean.
     */
    public boolean isFullyPaid() {
        return fullyPaid;
    }

    /**
     * <p>
     * Setter for the field <code>fullyPaid</code>.
     * </p>
     *
     * @param fullyPaid
     *            a boolean.
     */
    public void setFullyPaid(boolean fullyPaid) {
        this.fullyPaid = fullyPaid;
    }

    /**
     * <p>
     * Getter for the field <code>paidAmount</code>.
     * </p>
     *
     * @return a {@link java.lang.Double} object.
     */
    public Double getPaidAmount() {
        return paidAmount;
    }

    /**
     * <p>
     * Setter for the field <code>paidAmount</code>.
     * </p>
     *
     * @param paidAmount
     *            a {@link java.lang.Double} object.
     */
    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    /**
     * <p>
     * Getter for the field <code>daysUntilDue</code>.
     * </p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getDaysUntilDue() {
        return daysUntilDue;
    }

    /**
     * <p>
     * Setter for the field <code>daysUntilDue</code>.
     * </p>
     *
     * @param daysUntilDue
     *            a {@link java.lang.Integer} object.
     */
    public void setDaysUntilDue(Integer daysUntilDue) {
        this.daysUntilDue = daysUntilDue;
    }

    /**
     * <p>
     * Getter for the field <code>externalId</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * <p>
     * Setter for the field <code>externalId</code>.
     * </p>
     *
     * @param externalId
     *            a {@link java.lang.String} object.
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * <p>
     * Getter for the field <code>clientNotes</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getClientNotes() {
        return clientNotes;
    }

    /**
     * <p>
     * Setter for the field <code>clientNotes</code>.
     * </p>
     *
     * @param clientNotes
     *            a {@link java.lang.String} object.
     */
    public void setClientNotes(String clientNotes) {
        this.clientNotes = clientNotes;
    }

    /**
     * <p>
     * Getter for the field <code>notes</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getNotes() {
        return notes;
    }

    /**
     * <p>
     * Setter for the field <code>notes</code>.
     * </p>
     *
     * @param notes
     *            a {@link java.lang.String} object.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * <p>
     * Getter for the field <code>swAccession</code>.
     * </p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getSwAccession() {
        return swAccession;
    }

    /**
     * <p>
     * Setter for the field <code>swAccession</code>.
     * </p>
     *
     * @param swAccession
     *            a {@link java.lang.Integer} object.
     */
    public void setSwAccession(Integer swAccession) {
        this.swAccession = swAccession;
    }

    /**
     * <p>
     * Getter for the field <code>createTimestamp</code>.
     * </p>
     *
     * @return a {@link java.util.Date} object.
     */
    public Date getCreateTimestamp() {
        return createTimestamp;
    }

    /**
     * <p>
     * Setter for the field <code>createTimestamp</code>.
     * </p>
     *
     * @param createTimestamp
     *            a {@link java.util.Date} object.
     */
    public void setCreateTimestamp(Date createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    /**
     * <p>
     * Getter for the field <code>expenses</code>.
     * </p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Expense> getExpenses() {
        return expenses;
    }

    /**
     * <p>
     * Setter for the field <code>expenses</code>.
     * </p>
     *
     * @param expenses
     *            a {@link java.util.Set} object.
     */
    public void setExpenses(Set<Expense> expenses) {
        this.expenses = expenses;
    }

}
