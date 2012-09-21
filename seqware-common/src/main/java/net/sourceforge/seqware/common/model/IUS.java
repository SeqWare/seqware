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

    public IUS() {
        super();
    }

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

    @Override
    public String toString() {
        return "IUS{" + "iusId=" + iusId + ", lane=" + lane + ", owner=" + owner + ", name=" + name + ", alias=" + alias
                + ", description=" + description + ", tag=" + tag + ", swAccession=" + swAccession + ", createTimestamp="
                + createTimestamp + ", updateTimestamp=" + updateTimestamp + ", skip=" + skip + ", isHasFile=" + isHasFile
                + ", isSelected=" + isSelected + '}';
    }

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

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getIusId()).toHashCode();
    }

    public Registration getOwner() {
        return owner;
    }

    public void setOwner(Registration owner) {
        this.owner = owner;
    }

    public Integer getIusId() {
        return iusId;
    }

    public void setIusId(Integer iusId) {
        this.iusId = iusId;
    }

    public Lane getLane() {
        return lane;
    }

    public void setLane(Lane lane) {
        this.lane = lane;
    }

    public Sample getSample() {
        return sample;
    }

    public void setSample(Sample sample) {
        this.sample = sample;
    }

    public String getJsonEscapeName() {
        return JsonUtil.forJSON(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getJsonEscapeDescription() {
        return JsonUtil.forJSON(description);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getSwAccession() {
        return swAccession;
    }

    public void setSwAccession(Integer swAccession) {
        this.swAccession = swAccession;
    }

    public Date getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Date createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public Date getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(Date updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public Set<Processing> getProcessings() {
        return processings;
    }

    public void setProcessings(Set<Processing> processings) {
        this.processings = processings;
    }

    public Set<WorkflowRun> getWorkflowRuns() {
        return workflowRuns;
    }

    public void setWorkflowRuns(Set<WorkflowRun> workflowRuns) {
        this.workflowRuns = workflowRuns;
    }

    public Boolean getIsHasFile() {
        return isHasFile;
    }

    public void setIsHasFile(Boolean isHasFile) {
        this.isHasFile = isHasFile;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    @XmlElementWrapper(name = "IUSAttributes")
    @XmlElement(name = "IUSAttribute")
    public Set<IUSAttribute> getIusAttributes() {
        return iusAttributes;
    }

    public void setIusAttributes(Set<IUSAttribute> iusAttributes) {
        this.iusAttributes = iusAttributes;
    }

    public Set<IUSLink> getIusLinks() {
        return iusLinks;
    }

    public void setIusLinks(Set<IUSLink> iusLinks) {
        this.iusLinks = iusLinks;
    }

    public Boolean getSkip() {
        return skip;
    }

    public void setSkip(Boolean skip) {
        if (skip != null && this.skip!=skip) {
            Log.debug("Skipping ius " + getSwAccession());
            this.skip = skip;
        }
    }

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
