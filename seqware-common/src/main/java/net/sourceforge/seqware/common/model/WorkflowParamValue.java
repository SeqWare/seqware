package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import net.sourceforge.seqware.common.security.PermissionsAware;
import net.sourceforge.seqware.common.util.jsontools.JsonUtil;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

public class WorkflowParamValue implements Serializable, Comparable<WorkflowParamValue>, PermissionsAware {

    private static final long serialVersionUID = 1L;
    private Integer workflowParamValueId;
    private Integer workflowParamId;
    private String displayName;
    private String value;
    private WorkflowParam workflowParam;

    public WorkflowParamValue() {
        super();
    }

    @Override
    public int compareTo(WorkflowParamValue that) {
        if (that == null) {
            return -1;
        }

        if (that.getWorkflowParamValueId() == this.getWorkflowParamValueId()) // when both names are null
        {
            return 0;
        }

        if (that.getWorkflowParamValueId() == null) {
            return -1;							// when only the other name is null
        }
        return (that.getWorkflowParamValueId().compareTo(this.getWorkflowParamValueId()));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("value", getValue()).toString();
    }

    @Override
    public boolean equals(Object other) {
        if ((this == other)) {
            return true;
        }
        if (!(other instanceof WorkflowParam)) {
            return false;
        }
        WorkflowParamValue castOther = (WorkflowParamValue) other;
        return new EqualsBuilder().append(this.getWorkflowParamValueId(), castOther.getWorkflowParamValueId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getWorkflowParamValueId()).toHashCode();
    }

    public Integer getWorkflowParamValueId() {
        return workflowParamValueId;
    }

    public void setWorkflowParamValueId(Integer workflowParamValueId) {
        this.workflowParamValueId = workflowParamValueId;
    }

    public Integer getWorkflowParamId() {
        return workflowParamId;
    }

    public void setWorkflowParamId(Integer workflowParamId) {
        this.workflowParamId = workflowParamId;
    }

    public String getJsonEscapeDisplayName() {
        return JsonUtil.forJSON(displayName);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getJsonEscapeValue() {
        return JsonUtil.forJSON(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public WorkflowParam getWorkflowParam() {
        return workflowParam;
    }

    public void setWorkflowParam(WorkflowParam workflowParam) {
        this.workflowParam = workflowParam;
    }

    @Override
    public WorkflowParamValue clone() {
        WorkflowParamValue wp = this;
        try {
            wp = (WorkflowParamValue) super.clone();

            WorkflowParam newParam = (workflowParam == null ? null : workflowParam.clone());
            wp.setWorkflowParam(newParam);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return wp;
    }

    @Override
    public boolean givesPermission(Registration registration) {
        boolean hasPermission = true;
        if (workflowParam != null) {
            hasPermission = workflowParam.givesPermission(registration);
        } else {//orphaned WorkflowParamValue
            if (registration.isLIMSAdmin()) {
                Logger.getLogger(WorkflowParamValue.class).warn("Modifying Orphan WorkflowParamValue: " + this.getDisplayName());
                hasPermission = true;
            } else {
                Logger.getLogger(WorkflowParamValue.class).warn("Not modifying Orphan WorkflowParamValue: " + this.getDisplayName());
                hasPermission = false;
            }
        }
        if (!hasPermission) {
            Logger.getLogger(WorkflowParamValue.class).info("WorkflowParamValue does not give permission");
            throw new SecurityException("User "+registration.getEmailAddress()+" does not have permission to modify "+this.getDisplayName());
        }
        return hasPermission;
    }
}
