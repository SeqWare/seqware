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

  public Expense() {
    super();
    logger = Logger.getLogger(Expense.class);
  }

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

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("swAccession", getSwAccession()).toString();
  }

  @Override
  public boolean equals(Object other) {
    if ((this == other))
      return true;
    if (!(other instanceof Expense))
      return false;
    Expense castOther = (Expense) other;
    return new EqualsBuilder().append(this.getSwAccession(), castOther.getSwAccession()).isEquals();
  }
  
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

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(getSwAccession()).toHashCode();
  }

    public Integer getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(Integer expenseId) {
        this.expenseId = expenseId;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public WorkflowRun getWorkflowRun() {
        return workflowRun;
    }

    public void setWorkflowRun(WorkflowRun workflowRun) {
        this.workflowRun = workflowRun;
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

    public Double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(Double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Double getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(Double totalUnits) {
        this.totalUnits = totalUnits;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getAddedSurcharge() {
        return addedSurcharge;
    }

    public void setAddedSurcharge(Double addedSurcharge) {
        this.addedSurcharge = addedSurcharge;
    }

    public Integer getSwAccession() {
        return swAccession;
    }

    public void setSwAccession(Integer swAccession) {
        this.swAccession = swAccession;
    }

    public Date getFinishedTimestamp() {
        return finishedTimestamp;
    }

    public void setFinishedTimestamp(Date finishedTimestamp) {
        this.finishedTimestamp = finishedTimestamp;
    }
  
  
}
