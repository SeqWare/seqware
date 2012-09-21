package net.sourceforge.seqware.common.model;

import java.io.Serializable;

import net.sourceforge.seqware.common.security.PermissionsAware;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

public class WorkflowRunParam implements Serializable, Comparable<WorkflowRunParam>, PermissionsAware {

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

    public WorkflowRunParam() {
        super();
    }

    @Override
    public int compareTo(WorkflowRunParam that) {
        if (that == null) {
            return -1;
        }
        if (WorkflowRunParamId == null || that.getWorkflowRunParamId() == null) {
            return key.compareTo(that.getKey()) + value.compareTo(that.getValue());
        }

        if (that.getWorkflowRunParamId() == this.getWorkflowRunParamId()) // when both names are null
        {
            return 0;
        }

        return (that.getWorkflowRunParamId().compareTo(this.getWorkflowRunParamId()));
    }

    public String toString() {
        return new ToStringBuilder(this).append("swAccession", getWorkflowRunParamId()).toString();
    }

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

    public int hashCode() {
        return new HashCodeBuilder().append(getWorkflowRunParamId()).toHashCode();
    }

    public Integer getParentProcessingAccession() {
        return parentProcessingAccession;
    }

    public void setParentProcessingAccession(Integer parentProcessingAccession) {
        this.parentProcessingAccession = parentProcessingAccession;
    }

    public Integer getWorkflowRunParamId() {
        return WorkflowRunParamId;
    }

    public void setWorkflowRunParamId(Integer WorkflowRunParamId) {
        this.WorkflowRunParamId = WorkflowRunParamId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public WorkflowRun getWorkflowRun() {
        return workflowRun;
    }

    public void setWorkflowRun(WorkflowRun workflowRun) {
        this.workflowRun = workflowRun;
    }

    @Override
    public boolean givesPermission(Registration registration) {
        boolean hasPermission = true;
        if (workflowRun != null) {
            hasPermission = workflowRun.givesPermission(registration);
        } else {//Orphaned WorkflowRunParam
            if (registration.isLIMSAdmin()) {
                Logger.getLogger(WorkflowRunParam.class).warn("Modifying Orphan WorkflowRunParam: " + this.getKey());
                hasPermission = true;
            } else {
                Logger.getLogger(WorkflowRunParam.class).warn("Not modifying Orphan WorkflowRunParam: " + this.getKey());
                hasPermission = false;
            }
        }
        if (!hasPermission) {
            Logger.getLogger(WorkflowRunParam.class).info("WorkflowRunParam does not give permission");
            throw new SecurityException("User " + registration.getEmailAddress() + " does not have permission to modify " + this.getKey());
        }
        return hasPermission;
    }
}
