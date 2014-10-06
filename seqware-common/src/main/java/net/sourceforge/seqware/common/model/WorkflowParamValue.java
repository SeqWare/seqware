package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import net.sourceforge.seqware.common.security.PermissionsAware;
import net.sourceforge.seqware.common.util.jsontools.JsonUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * WorkflowParamValue class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowParamValue extends PermissionsAware implements Serializable, Comparable<WorkflowParamValue>, Cloneable {

    private static final long serialVersionUID = 1L;
    private Integer workflowParamValueId;
    private Integer workflowParamId;
    private String displayName;
    private String value;
    private WorkflowParam workflowParam;
    final Logger logger = LoggerFactory.getLogger(WorkflowParamValue.class);

    /**
     * <p>
     * Constructor for WorkflowParamValue.
     * </p>
     */
    public WorkflowParamValue() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @param that
     */
    @Override
    public int compareTo(WorkflowParamValue that) {
        if (that == null) {
            return -1;
        }

        if (Objects.equals(that.getWorkflowParamValueId(), this.getWorkflowParamValueId())) // when both names are null
        {
            return 0;
        }

        if (that.getWorkflowParamValueId() == null) {
            return -1; // when only the other name is null
        }
        return (that.getWorkflowParamValueId().compareTo(this.getWorkflowParamValueId()));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("value", getValue()).toString();
    }

    /**
     * {@inheritDoc}
     * 
     * @param other
     */
    @Override
    public boolean equals(Object other) {
        if ((this == other)) {
            return true;
        }
        if (!(other instanceof WorkflowParamValue)) {
            return false;
        }
        WorkflowParamValue castOther = (WorkflowParamValue) other;
        return new EqualsBuilder().append(this.getWorkflowParamValueId(), castOther.getWorkflowParamValueId()).isEquals();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getWorkflowParamValueId()).toHashCode();
    }

    /**
     * <p>
     * Getter for the field <code>workflowParamValueId</code>.
     * </p>
     * 
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getWorkflowParamValueId() {
        return workflowParamValueId;
    }

    /**
     * <p>
     * Setter for the field <code>workflowParamValueId</code>.
     * </p>
     * 
     * @param workflowParamValueId
     *            a {@link java.lang.Integer} object.
     */
    public void setWorkflowParamValueId(Integer workflowParamValueId) {
        this.workflowParamValueId = workflowParamValueId;
    }

    /**
     * <p>
     * Getter for the field <code>workflowParamId</code>.
     * </p>
     * 
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getWorkflowParamId() {
        return workflowParamId;
    }

    /**
     * <p>
     * Setter for the field <code>workflowParamId</code>.
     * </p>
     * 
     * @param workflowParamId
     *            a {@link java.lang.Integer} object.
     */
    public void setWorkflowParamId(Integer workflowParamId) {
        this.workflowParamId = workflowParamId;
    }

    /**
     * <p>
     * getJsonEscapeDisplayName.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeDisplayName() {
        return JsonUtil.forJSON(displayName);
    }

    /**
     * <p>
     * Getter for the field <code>displayName</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * <p>
     * Setter for the field <code>displayName</code>.
     * </p>
     * 
     * @param displayName
     *            a {@link java.lang.String} object.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * <p>
     * getJsonEscapeValue.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeValue() {
        return JsonUtil.forJSON(value);
    }

    /**
     * <p>
     * Getter for the field <code>value</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getValue() {
        return value;
    }

    /**
     * <p>
     * Setter for the field <code>value</code>.
     * </p>
     * 
     * @param value
     *            a {@link java.lang.String} object.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * <p>
     * Getter for the field <code>workflowParam</code>.
     * </p>
     * 
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowParam} object.
     */
    public WorkflowParam getWorkflowParam() {
        return workflowParam;
    }

    /**
     * <p>
     * Setter for the field <code>workflowParam</code>.
     * </p>
     * 
     * @param workflowParam
     *            a {@link net.sourceforge.seqware.common.model.WorkflowParam} object.
     */
    public void setWorkflowParam(WorkflowParam workflowParam) {
        this.workflowParam = workflowParam;
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     * @throws java.lang.CloneNotSupportedException
     */
    @Override
    public WorkflowParamValue clone() throws CloneNotSupportedException {
        WorkflowParamValue wp = this;
        try {
            wp = (WorkflowParamValue) super.clone();

            WorkflowParam newParam = (workflowParam == null ? null : workflowParam.clone());
            wp.setWorkflowParam(newParam);
        } catch (CloneNotSupportedException e) {
            logger.error("CloneNotSupported", e);
        }
        return wp;
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public boolean givesPermissionInternal(Registration registration, Set<Integer> considered) {
        boolean hasPermission;
        if (workflowParam != null) {
            hasPermission = workflowParam.givesPermission(registration, considered);
        } else {// orphaned WorkflowParamValue
            if (registration.isLIMSAdmin()) {
                logger.warn("Modifying Orphan WorkflowParamValue: " + this.getDisplayName());
                hasPermission = true;
            } else {
                logger.warn("Not modifying Orphan WorkflowParamValue: " + this.getDisplayName());
                hasPermission = false;
            }
        }
        if (!hasPermission) {
            logger.info("WorkflowParamValue does not give permission");
            throw new SecurityException("User " + registration.getEmailAddress() + " does not have permission to modify "
                    + this.getDisplayName());
        }
        return hasPermission;
    }
}
