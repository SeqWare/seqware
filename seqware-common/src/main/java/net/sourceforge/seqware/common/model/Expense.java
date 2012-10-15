package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.seqware.common.security.PermissionsAware;
import net.sourceforge.seqware.common.util.jsontools.JsonUtil;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

/**
 * <p>Expense class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Expense implements Serializable, Comparable<Expense>, PermissionsAware {
  
  private static final long serialVersionUID = 1L;
  private Integer expenseId;
  private Invoice invoice;
  private WorkflowRun workflowRun;
  private String agent;
  private String expenseType;
  private String description;
  private Double pricePerUnit;
  private Double totalUnits;
  private Double totalPrice;
  private Double addedSurcharge;
  private Integer swAccession;
  private Date finishedTimestamp;
  private Logger logger;
  //private Set<ExpenseAttribute> invoiceAttributes = new TreeSet<ExpenseAttribute>();

  /**
   * <p>Constructor for Expense.</p>
   */
  public Expense() {
    super();
    logger = Logger.getLogger(Expense.class);
  }

  /** {@inheritDoc} */
  @Override
  public int compareTo(Expense that) {
    if (that == null)
      return -1;

    if (that.getSwAccession() == this.getSwAccession()) // when both names are
                                                        // null
      return 0;

    if (that.getSwAccession() == null)
      return -1; // when only the other name is null

    return (that.getSwAccession().compareTo(this.getSwAccession()));
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return new ToStringBuilder(this).append("swAccession", getSwAccession()).toString();
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object other) {
    if ((this == other))
      return true;
    if (!(other instanceof Expense))
      return false;
    Expense castOther = (Expense) other;
    return new EqualsBuilder().append(this.getSwAccession(), castOther.getSwAccession()).isEquals();
  }
  
  /** {@inheritDoc} */
  @Override
  public boolean givesPermission(Registration registration) {
    boolean hasPermission = true;
    if (registration == null) {
      hasPermission = false;
    }

    if (!hasPermission) {
      Logger.getLogger(Workflow.class).info("Expense does not give permission");
      throw new SecurityException("User " + registration.getEmailAddress()
          + " does not have permission to modify aspects of invoice " + this.getSwAccession());
    } else {
      Logger.getLogger(Workflow.class).info("Expenses are public by default");
    }
    return hasPermission;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(getSwAccession()).toHashCode();
  }

    /**
     * <p>Getter for the field <code>expenseId</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getExpenseId() {
        return expenseId;
    }

    /**
     * <p>Setter for the field <code>expenseId</code>.</p>
     *
     * @param expenseId a {@link java.lang.Integer} object.
     */
    public void setExpenseId(Integer expenseId) {
        this.expenseId = expenseId;
    }

    /**
     * <p>Getter for the field <code>invoice</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.Invoice} object.
     */
    public Invoice getInvoice() {
        return invoice;
    }

    /**
     * <p>Setter for the field <code>invoice</code>.</p>
     *
     * @param invoice a {@link net.sourceforge.seqware.common.model.Invoice} object.
     */
    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    /**
     * <p>Getter for the field <code>workflowRun</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    public WorkflowRun getWorkflowRun() {
        return workflowRun;
    }

    /**
     * <p>Setter for the field <code>workflowRun</code>.</p>
     *
     * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    public void setWorkflowRun(WorkflowRun workflowRun) {
        this.workflowRun = workflowRun;
    }

    /**
     * <p>Getter for the field <code>agent</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAgent() {
        return agent;
    }

    /**
     * <p>Setter for the field <code>agent</code>.</p>
     *
     * @param agent a {@link java.lang.String} object.
     */
    public void setAgent(String agent) {
        this.agent = agent;
    }

    /**
     * <p>Getter for the field <code>expenseType</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getExpenseType() {
        return expenseType;
    }

    /**
     * <p>Setter for the field <code>expenseType</code>.</p>
     *
     * @param expenseType a {@link java.lang.String} object.
     */
    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    /**
     * <p>Getter for the field <code>description</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>Setter for the field <code>description</code>.</p>
     *
     * @param description a {@link java.lang.String} object.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>Getter for the field <code>pricePerUnit</code>.</p>
     *
     * @return a {@link java.lang.Double} object.
     */
    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    /**
     * <p>Setter for the field <code>pricePerUnit</code>.</p>
     *
     * @param pricePerUnit a {@link java.lang.Double} object.
     */
    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    /**
     * <p>Getter for the field <code>totalUnits</code>.</p>
     *
     * @return a {@link java.lang.Double} object.
     */
    public Double getTotalUnits() {
        return totalUnits;
    }

    /**
     * <p>Setter for the field <code>totalUnits</code>.</p>
     *
     * @param totalUnits a {@link java.lang.Double} object.
     */
    public void setTotalUnits(Double totalUnits) {
        this.totalUnits = totalUnits;
    }

    /**
     * <p>Getter for the field <code>totalPrice</code>.</p>
     *
     * @return a {@link java.lang.Double} object.
     */
    public Double getTotalPrice() {
        return totalPrice;
    }

    /**
     * <p>Setter for the field <code>totalPrice</code>.</p>
     *
     * @param totalPrice a {@link java.lang.Double} object.
     */
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * <p>Getter for the field <code>addedSurcharge</code>.</p>
     *
     * @return a {@link java.lang.Double} object.
     */
    public Double getAddedSurcharge() {
        return addedSurcharge;
    }

    /**
     * <p>Setter for the field <code>addedSurcharge</code>.</p>
     *
     * @param addedSurcharge a {@link java.lang.Double} object.
     */
    public void setAddedSurcharge(Double addedSurcharge) {
        this.addedSurcharge = addedSurcharge;
    }

    /**
     * <p>Getter for the field <code>swAccession</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getSwAccession() {
        return swAccession;
    }

    /**
     * <p>Setter for the field <code>swAccession</code>.</p>
     *
     * @param swAccession a {@link java.lang.Integer} object.
     */
    public void setSwAccession(Integer swAccession) {
        this.swAccession = swAccession;
    }

    /**
     * <p>Getter for the field <code>finishedTimestamp</code>.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    public Date getFinishedTimestamp() {
        return finishedTimestamp;
    }

    /**
     * <p>Setter for the field <code>finishedTimestamp</code>.</p>
     *
     * @param finishedTimestamp a {@link java.util.Date} object.
     */
    public void setFinishedTimestamp(Date finishedTimestamp) {
        this.finishedTimestamp = finishedTimestamp;
    }
  
  
}
