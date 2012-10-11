package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import net.sourceforge.seqware.common.security.PermissionsAware;
import net.sourceforge.seqware.common.util.jsontools.JsonUtil;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

public class WorkflowParam implements Serializable, Comparable<WorkflowParam>, PermissionsAware {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Integer workflowParamId;
    private Workflow workflow;
    private String type;
    private String key;
    private String defaultValue;
    private Boolean display;
    private String fileMetaType;
    private String displayName;
    private String value;
    private String displayValue;
    private SortedSet<WorkflowParamValue> values;
    // none persistents(for Summary Launch Workflow Page) 
    private Sample sample;
    private List<File> files;

    public WorkflowParam() {
        super();
    }

    @Override
    public int compareTo(WorkflowParam that) {
        if (that == null) {
            return -1;
        }

        if (that.getWorkflowParamId() == this.getWorkflowParamId()) // when both names are null
        {
            return 0;
        }

        if (that.getWorkflowParamId() == null) {
            return -1;							// when only the other name is null
        }
        return (that.getWorkflowParamId().compareTo(this.getWorkflowParamId()));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("swAccession", getWorkflowParamId()).toString();
    }

    @Override
    public boolean equals(Object other) {
        if ((this == other)) {
            return true;
        }
        if (!(other instanceof WorkflowParam)) {
            return false;
        }
        WorkflowParam castOther = (WorkflowParam) other;
        return new EqualsBuilder().append(this.getWorkflowParamId(), castOther.getWorkflowParamId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getWorkflowParamId()).toHashCode();
    }

    public Integer getWorkflowParamId() {
        return workflowParamId;
    }

    public void setWorkflowParamId(Integer workflowParamId) {
        this.workflowParamId = workflowParamId;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    public String getType() {
        return type;
    }

    public String getJsonEscapeType() {
        return JsonUtil.forJSON(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public String getJsonEscapeKey() {
        return JsonUtil.forJSON(key);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getJsonEscapeDefaultValue() {
        return JsonUtil.forJSON(defaultValue);
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean isDisplay() {
        return display;
    }

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public String getFileMetaType() {
        return fileMetaType;
    }

    public void setFileMetaType(String fileMetaType) {
        this.fileMetaType = fileMetaType;
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

    public SortedSet<WorkflowParamValue> getValues() {
        return values;
    }

    public void setValues(SortedSet<WorkflowParamValue> values) {
        this.values = values;
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

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    @Override
    public WorkflowParam clone() throws CloneNotSupportedException {
        WorkflowParam wp = (WorkflowParam) super.clone();

        List newFiles = (files == null ? null : new ArrayList(files));
        wp.setFiles(newFiles);
        SortedSet newValues = (values == null ? null : new TreeSet<WorkflowParamValue>(values));
        wp.setValues(newValues);
        return wp;
    }

    @Override
    public boolean givesPermission(Registration registration) {
        boolean hasPermission = true;

        if (workflow != null) {
            hasPermission = workflow.givesPermission(registration);
        } else {//orphaned WorkflowParam
            if (registration.isLIMSAdmin()) {
                Logger.getLogger(WorkflowParam.class).warn("Modifying Orphan WorkflowParam: " + this.getDisplayName());
                hasPermission = true;
            } else {
                Logger.getLogger(WorkflowParam.class).warn("Not modifying Orphan WorkflowParam: " + this.getDisplayName());
                hasPermission = false;
            }
        }

        if (!hasPermission) {
            Logger.getLogger(WorkflowParam.class).info("WorkflowParam does not give permission");
            throw new SecurityException("User "+registration.getEmailAddress()+" does not have permission to modify "+this.getDisplayName());
        }
        return hasPermission;

    }
}
