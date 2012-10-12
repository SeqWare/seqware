package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import net.sourceforge.seqware.common.security.PermissionsAware;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.jsontools.JsonUtil;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

@XmlRootElement
/**
 * <p>IUS class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class IUS implements Serializable, Comparable<IUS>, PermissionsAware {

    private static final long serialVersionUID = 3472028115923390568L;
    private Integer iusId;
    private Lane lane;
    private Registration owner;
    private Sample sample;
    private String name;
    private String alias;
    private String description;
    private String tag;
    private Integer swAccession;
    private Date createTimestamp;
    private Date updateTimestamp;
    private Set<Processing> processings = new TreeSet<Processing>();
    private Set<WorkflowRun> workflowRuns = new TreeSet<WorkflowRun>();
    private Set<IUSAttribute> iusAttributes = new TreeSet<IUSAttribute>();
    private Set<IUSLink> iusLinks = new TreeSet<IUSLink>();
    private Boolean skip;
    // not persist
    private Boolean isHasFile = false;
    private Boolean isSelected = false;

    /**
     * <p>Constructor for IUS.</p>
     */
    public IUS() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(IUS that) {
        if (that == null) {
            return -1;
        }

        if (that.getIusId() == this.getIusId()) // when both names are null
        {
            return 0;
        }

        if (that.getIusId() == null) {
            return -1; // when only the other name is null
        }
        if (this.getIusId() == null) {
            return 1;
        }

        return (that.getIusId().compareTo(this.getIusId()));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "IUS{" + "iusId=" + iusId + ", lane=" + lane + ", owner=" + owner + ", name=" + name + ", alias=" + alias
                + ", description=" + description + ", tag=" + tag + ", swAccession=" + swAccession + ", createTimestamp="
                + createTimestamp + ", updateTimestamp=" + updateTimestamp + ", skip=" + skip + ", isHasFile=" + isHasFile
                + ", isSelected=" + isSelected + '}';
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        if ((this == other)) {
            return true;
        }
        if (!(other instanceof IUS)) {
            return false;
        }
        IUS castOther = (IUS) other;
        return new EqualsBuilder().append(this.getIusId(), castOther.getIusId()).isEquals();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getIusId()).toHashCode();
    }

    /**
     * <p>Getter for the field <code>owner</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.Registration} object.
     */
    public Registration getOwner() {
        return owner;
    }

    /**
     * <p>Setter for the field <code>owner</code>.</p>
     *
     * @param owner a {@link net.sourceforge.seqware.common.model.Registration} object.
     */
    public void setOwner(Registration owner) {
        this.owner = owner;
    }

    /**
     * <p>Getter for the field <code>iusId</code>.</p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getIusId() {
        return iusId;
    }

    /**
     * <p>Setter for the field <code>iusId</code>.</p>
     *
     * @param iusId a {@link java.lang.Integer} object.
     */
    public void setIusId(Integer iusId) {
        this.iusId = iusId;
    }

    /**
     * <p>Getter for the field <code>lane</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public Lane getLane() {
        return lane;
    }

    /**
     * <p>Setter for the field <code>lane</code>.</p>
     *
     * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public void setLane(Lane lane) {
        this.lane = lane;
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
     * <p>getJsonEscapeName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeName() {
        return JsonUtil.forJSON(name);
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>alias</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAlias() {
        return alias;
    }

    /**
     * <p>Setter for the field <code>alias</code>.</p>
     *
     * @param alias a {@link java.lang.String} object.
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * <p>getJsonEscapeDescription.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeDescription() {
        return JsonUtil.forJSON(description);
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
     * <p>Getter for the field <code>tag</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTag() {
        return tag;
    }

    /**
     * <p>Setter for the field <code>tag</code>.</p>
     *
     * @param tag a {@link java.lang.String} object.
     */
    public void setTag(String tag) {
        this.tag = tag;
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
     * <p>Getter for the field <code>createTimestamp</code>.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    public Date getCreateTimestamp() {
        return createTimestamp;
    }

    /**
     * <p>Setter for the field <code>createTimestamp</code>.</p>
     *
     * @param createTimestamp a {@link java.util.Date} object.
     */
    public void setCreateTimestamp(Date createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    /**
     * <p>Getter for the field <code>updateTimestamp</code>.</p>
     *
     * @return a {@link java.util.Date} object.
     */
    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    /**
     * <p>Setter for the field <code>updateTimestamp</code>.</p>
     *
     * @param updateTimestamp a {@link java.util.Date} object.
     */
    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    /**
     * <p>Getter for the field <code>processings</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Processing> getProcessings() {
        return processings;
    }

    /**
     * <p>Setter for the field <code>processings</code>.</p>
     *
     * @param processings a {@link java.util.Set} object.
     */
    public void setProcessings(Set<Processing> processings) {
        this.processings = processings;
    }

    /**
     * <p>Getter for the field <code>workflowRuns</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<WorkflowRun> getWorkflowRuns() {
        return workflowRuns;
    }

    /**
     * <p>Setter for the field <code>workflowRuns</code>.</p>
     *
     * @param workflowRuns a {@link java.util.Set} object.
     */
    public void setWorkflowRuns(Set<WorkflowRun> workflowRuns) {
        this.workflowRuns = workflowRuns;
    }

    /**
     * <p>Getter for the field <code>isHasFile</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getIsHasFile() {
        return isHasFile;
    }

    /**
     * <p>Setter for the field <code>isHasFile</code>.</p>
     *
     * @param isHasFile a {@link java.lang.Boolean} object.
     */
    public void setIsHasFile(Boolean isHasFile) {
        this.isHasFile = isHasFile;
    }

    /**
     * <p>Getter for the field <code>isSelected</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getIsSelected() {
        return isSelected;
    }

    /**
     * <p>Setter for the field <code>isSelected</code>.</p>
     *
     * @param isSelected a {@link java.lang.Boolean} object.
     */
    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * <p>Getter for the field <code>iusAttributes</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    @XmlElementWrapper(name = "IUSAttributes")
    @XmlElement(name = "IUSAttribute")
    public Set<IUSAttribute> getIusAttributes() {
        return iusAttributes;
    }

    /**
     * <p>Setter for the field <code>iusAttributes</code>.</p>
     *
     * @param iusAttributes a {@link java.util.Set} object.
     */
    public void setIusAttributes(Set<IUSAttribute> iusAttributes) {
        this.iusAttributes = iusAttributes;
    }

    /**
     * <p>Getter for the field <code>iusLinks</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<IUSLink> getIusLinks() {
        return iusLinks;
    }

    /**
     * <p>Setter for the field <code>iusLinks</code>.</p>
     *
     * @param iusLinks a {@link java.util.Set} object.
     */
    public void setIusLinks(Set<IUSLink> iusLinks) {
        this.iusLinks = iusLinks;
    }

    /**
     * <p>Getter for the field <code>skip</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getSkip() {
        return skip;
    }

    /**
     * <p>Setter for the field <code>skip</code>.</p>
     *
     * @param skip a {@link java.lang.Boolean} object.
     */
    public void setSkip(Boolean skip) {
        if (skip != null && this.skip!=skip) {
            Log.debug("Skipping ius " + getSwAccession());
            this.skip = skip;
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean givesPermission(Registration registration) {
        boolean hasPermission = true;
        if (sample != null) {
            hasPermission = sample.givesPermission(registration);
        } else {// orphaned IUS
            if (registration.equals(this.owner) || registration.isLIMSAdmin()) {
                Logger.getLogger(IUS.class).warn("Modifying Orphan IUS: " + this.getTag());
                hasPermission = true;
            } else if (owner == null) {
                Logger.getLogger(IUS.class).warn("Orphan IUS has no owner! Allowing modifications: " + this.getTag());
                hasPermission = true;
            } else {
                Logger.getLogger(IUS.class).warn("Not modifying Orphan IUS: " + this.getTag());
                hasPermission = false;
            }
        }
        if (!hasPermission) {
            Logger.getLogger(IUS.class).info("IUS does not give permission");
            throw new SecurityException("User " + registration.getEmailAddress() + " does not have permission to modify "
                    + this.getTag());
        }
        return hasPermission;
    }
}
