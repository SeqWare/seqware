package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import net.sourceforge.seqware.common.security.PermissionsAware;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * WorkflowRunParam class.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowRunParam extends PermissionsAware implements Serializable, Comparable<WorkflowRunParam> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Integer WorkflowRunParamId;
    private String type;
    private String key;
    private String value;
    private Integer parentProcessingAccession;
    private WorkflowRun workflowRun;
    final Logger logger = LoggerFactory.getLogger(WorkflowRunParam.class);

    /**
     * <p>
     * Constructor for WorkflowRunParam.
     * </p>
     */
    public WorkflowRunParam() {
        super();
    }

    /**
     * {@inheritDoc}
     * 
     * @param that
     */
    @Override
    public int compareTo(WorkflowRunParam that) {
        if (that == null) {
            return -1;
        }
        if (WorkflowRunParamId == null || that.getWorkflowRunParamId() == null) {
            return key.compareTo(that.getKey()) + value.compareTo(that.getValue());
        }

        if (Objects.equals(that.getWorkflowRunParamId(), this.getWorkflowRunParamId())) // when both names are null
        {
            return 0;
        }

        return (that.getWorkflowRunParamId().compareTo(this.getWorkflowRunParamId()));
    }

    /**
     * <p>
     * toString.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("swAccession", getWorkflowRunParamId()).toString();
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
        if (!(other instanceof WorkflowRunParam)) {
            return false;
        }
        return (this.compareTo(((WorkflowRunParam) other)) == 0);
    }

    /**
     * <p>
     * hashCode.
     * </p>
     * 
     * @return a int.
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getWorkflowRunParamId()).toHashCode();
    }

    /**
     * <p>
     * Getter for the field <code>parentProcessingAccession</code>.
     * </p>
     * 
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getParentProcessingAccession() {
        return parentProcessingAccession;
    }

    /**
     * <p>
     * Setter for the field <code>parentProcessingAccession</code>.
     * </p>
     * 
     * @param parentProcessingAccession
     *            a {@link java.lang.Integer} object.
     */
    public void setParentProcessingAccession(Integer parentProcessingAccession) {
        this.parentProcessingAccession = parentProcessingAccession;
    }

    /**
     * <p>
     * getWorkflowRunParamId.
     * </p>
     * 
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getWorkflowRunParamId() {
        return WorkflowRunParamId;
    }

    /**
     * <p>
     * setWorkflowRunParamId.
     * </p>
     * 
     * @param WorkflowRunParamId
     *            a {@link java.lang.Integer} object.
     */
    public void setWorkflowRunParamId(Integer WorkflowRunParamId) {
        this.WorkflowRunParamId = WorkflowRunParamId;
    }

    /**
     * <p>
     * Getter for the field <code>type</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getType() {
        return type;
    }

    /**
     * <p>
     * Setter for the field <code>type</code>.
     * </p>
     * 
     * @param type
     *            a {@link java.lang.String} object.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * <p>
     * Getter for the field <code>key</code>.
     * </p>
     * 
     * @return a {@link java.lang.String} object.
     */
    public String getKey() {
        return key;
    }

    /**
     * <p>
     * Setter for the field <code>key</code>.
     * </p>
     * 
     * @param key
     *            a {@link java.lang.String} object.
     */
    public void setKey(String key) {
        this.key = key;
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
     * Getter for the field <code>workflowRun</code>.
     * </p>
     * 
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    public WorkflowRun getWorkflowRun() {
        return workflowRun;
    }

    /**
     * <p>
     * Setter for the field <code>workflowRun</code>.
     * </p>
     * 
     * @param workflowRun
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     */
    public void setWorkflowRun(WorkflowRun workflowRun) {
        this.workflowRun = workflowRun;
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public boolean givesPermissionInternal(Registration registration, Set<Integer> considered) {
        boolean hasPermission;
        if (workflowRun != null) {
            hasPermission = workflowRun.givesPermission(registration, considered);
        } else {// Orphaned WorkflowRunParam
            if (registration.isLIMSAdmin()) {
                logger.warn("Modifying Orphan WorkflowRunParam: " + this.getKey());
                hasPermission = true;
            } else {
                logger.warn("Not modifying Orphan WorkflowRunParam: " + this.getKey());
                hasPermission = false;
            }
        }
        if (!hasPermission) {
            logger.info("WorkflowRunParam does not give permission");
            throw new SecurityException("User " + registration.getEmailAddress() + " does not have permission to modify " + this.getKey());
        }
        return hasPermission;
    }
}
