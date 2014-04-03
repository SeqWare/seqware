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

/**
 * <p>WorkflowParam class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
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

    /**
     * <p>Constructor for WorkflowParam.</p>
     */
    public WorkflowParam() {
        super();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("swAccession", getWorkflowParamId()).toString();
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getWorkflowParamId()).toHashCode();
    }

    /**
     * <p>Getter for the field <code>workflowParamId</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getWorkflowParamId() {
        return workflowParamId;
    }

    /**
     * <p>Setter for the field <code>workflowParamId</code>.</p>
     *
     * @param workflowParamId a {@link java.lang.Integer} object.
     */
    public void setWorkflowParamId(Integer workflowParamId) {
        this.workflowParamId = workflowParamId;
    }

    /**
     * <p>Getter for the field <code>workflow</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.Workflow} object.
     */
    public Workflow getWorkflow() {
        return workflow;
    }

    /**
     * <p>Setter for the field <code>workflow</code>.</p>
     *
     * @param workflow a {@link net.sourceforge.seqware.common.model.Workflow} object.
     */
    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getType() {
        return type;
    }

    /**
     * <p>getJsonEscapeType.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeType() {
        return JsonUtil.forJSON(type);
    }

    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link java.lang.String} object.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * <p>Getter for the field <code>key</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getKey() {
        return key;
    }

    /**
     * <p>getJsonEscapeKey.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeKey() {
        return JsonUtil.forJSON(key);
    }

    /**
     * <p>Setter for the field <code>key</code>.</p>
     *
     * @param key a {@link java.lang.String} object.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * <p>getJsonEscapeDefaultValue.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeDefaultValue() {
        return JsonUtil.forJSON(defaultValue);
    }

    /**
     * <p>Getter for the field <code>defaultValue</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * <p>Setter for the field <code>defaultValue</code>.</p>
     *
     * @param defaultValue a {@link java.lang.String} object.
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * <p>isDisplay.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean isDisplay() {
        return display;
    }

    /**
     * <p>Getter for the field <code>display</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getDisplay() {
        return display;
    }

    /**
     * <p>Setter for the field <code>display</code>.</p>
     *
     * @param display a {@link java.lang.Boolean} object.
     */
    public void setDisplay(Boolean display) {
        this.display = display;
    }

    /**
     * <p>Getter for the field <code>fileMetaType</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFileMetaType() {
        return fileMetaType;
    }

    /**
     * <p>Setter for the field <code>fileMetaType</code>.</p>
     *
     * @param fileMetaType a {@link java.lang.String} object.
     */
    public void setFileMetaType(String fileMetaType) {
        this.fileMetaType = fileMetaType;
    }

    /**
     * <p>getJsonEscapeDisplayName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeDisplayName() {
        return JsonUtil.forJSON(displayName);
    }

    /**
     * <p>Getter for the field <code>displayName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * <p>Setter for the field <code>displayName</code>.</p>
     *
     * @param displayName a {@link java.lang.String} object.
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * <p>Getter for the field <code>values</code>.</p>
     *
     * @return a {@link java.util.SortedSet} object.
     */
    public SortedSet<WorkflowParamValue> getValues() {
        return values;
    }

    /**
     * <p>Setter for the field <code>values</code>.</p>
     *
     * @param values a {@link java.util.SortedSet} object.
     */
    public void setValues(SortedSet<WorkflowParamValue> values) {
        this.values = values;
    }

    /**
     * <p>getJsonEscapeValue.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeValue() {
        return JsonUtil.forJSON(value);
    }

    /**
     * <p>Getter for the field <code>value</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getValue() {
        return value;
    }

    /**
     * <p>Setter for the field <code>value</code>.</p>
     *
     * @param value a {@link java.lang.String} object.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * <p>Getter for the field <code>displayValue</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDisplayValue() {
        return displayValue;
    }

    /**
     * <p>Setter for the field <code>displayValue</code>.</p>
     *
     * @param displayValue a {@link java.lang.String} object.
     */
    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    /**
     * <p>Getter for the field <code>sample</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public Sample getSample() {
        return sample;
    }

    /**
     * <p>Setter for the field <code>sample</code>.</p>
     *
     * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public void setSample(Sample sample) {
        this.sample = sample;
    }

    /**
     * <p>Getter for the field <code>files</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<File> getFiles() {
        return files;
    }

    /**
     * <p>Setter for the field <code>files</code>.</p>
     *
     * @param files a {@link java.util.List} object.
     */
    public void setFiles(List<File> files) {
        this.files = files;
    }

    /** {@inheritDoc} */
    @Override
    public WorkflowParam clone() throws CloneNotSupportedException {
        WorkflowParam wp = (WorkflowParam) super.clone();

        List newFiles = (files == null ? null : new ArrayList(files));
        wp.setFiles(newFiles);
        SortedSet newValues = (values == null ? null : new TreeSet<WorkflowParamValue>(values));
        wp.setValues(newValues);
        return wp;
    }

    /** {@inheritDoc} */
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
