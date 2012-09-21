package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import net.sourceforge.seqware.common.security.PermissionsAware;
import net.sourceforge.seqware.common.util.jsontools.JsonUtil;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

@XmlRootElement
public class Study implements Serializable, PermissionsAware {

  private static final long serialVersionUID = 2L;
  private Integer studyId;
  private Integer swAccession;
  private String title;
  private String description;
  private String alias;
  private String accession;
  private String status;
  private String abstractStr;
  private String newType;
  private String centerName;
  private String centerProjectName;
  private Integer projectId;
  private Date createTimestamp;
  private Date updateTimestamp;
  private Boolean isSelected = false;
  private Boolean isHasFile = false;
  private String html;
  private Registration owner;
  private StudyType existingType;
  private Integer existingTypeInt;
  private SortedSet<Experiment> experiments;
  private SortedSet<ShareStudy> sharedStudies;
  private Set<Processing> processings = new TreeSet<Processing>();
  private Set<StudyLink> studyLinks = new TreeSet<StudyLink>();
  private Set<StudyAttribute> studyAttributes = new TreeSet<StudyAttribute>();

  public Study() {
    super();
  }

  public int compareTo(Study that) {
    if (that == null) {
      return -1;
    }

    if (that.getStudyId() == this.getStudyId()) // when both names are null
    {
      return 0;
    }

    if (that.getStudyId() == null) {
      return -1; // when only the other name is null
    }
    return (that.getStudyId().compareTo(this.getStudyId()));
  }

  @Override
  public String toString() {
    return "Study{" + "studyId=" + studyId + ", swAccession=" + swAccession + ", title=" + title + ", description="
        + description + ", alias=" + alias + ", accession=" + accession + ", status=" + status + ", abstractStr="
        + abstractStr + ", newType=" + newType + ", centerName=" + centerName + ", centerProjectName="
        + centerProjectName + ", projectId=" + projectId + ", createTimestamp=" + createTimestamp
        + ", updateTimestamp=" + updateTimestamp + ", isSelected=" + isSelected + ", isHasFile=" + isHasFile
        + ", html=" + html + ", owner=" + owner + ", existingType=" + existingType + ", existingTypeInt="
        + existingTypeInt + '}';
  }

  @Override
  public boolean equals(Object other) {
    if ((this == other)) {
      return true;
    }
    if (!(other instanceof Study)) {
      return false;
    }
    Study castOther = (Study) other;
    return new EqualsBuilder().append(this.getStudyId(), castOther.getStudyId()).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(getStudyId()).toHashCode();
  }

  /*
   * public int compareTo(Study that) { if(that == null) return -1;
   * 
   * if(that.getTitle() == this.getTitle()) // when both names are null return
   * 0;
   * 
   * if(that.getTitle() == null) return -1; // when only the other name is null
   * 
   * return(that.getTitle().compareTo(this.getTitle())); }
   * 
   * public String toString() { return new ToStringBuilder(this)
   * .append("studyId", getStudyId()) .append("title", getTitle())
   * .append("swAccession", getSwAccession()) .toString(); }
   * 
   * public boolean equals(Object other) { if ( (this == other ) ) return true;
   * if ( !(other instanceof Study) ) return false; Study castOther = (Study)
   * other; return new EqualsBuilder() .append(this.getTitle(),
   * castOther.getTitle()) .isEquals(); }
   * 
   * public int hashCode() { return new HashCodeBuilder() .append(getTitle())
   * .toHashCode(); }
   */
  public Integer getStudyId() {
    return studyId;
  }

  public void setStudyId(Integer studyId) {
    this.studyId = studyId;
  }

  public Integer getSwAccession() {
    return swAccession;
  }

  public void setSwAccession(Integer swAccession) {
    this.swAccession = swAccession;
  }

  public String getTitle() {
    return title;
  }

  public String getJsonEscapeTitle() {
    return JsonUtil.forJSON(title);
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getJsonEscapeDescription() {
    return JsonUtil.forJSON(description);
  }

  public String getJsonEscapeDescription200() {
    if (description != null && description.length() > 200) {
      return JsonUtil.forJSON(description.substring(0, 200)) + " ...";
    } else {
      return getJsonEscapeDescription();
    }
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getAccession() {
    return accession;
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getAbstractStr() {
    return abstractStr;
  }

  public void setAbstractStr(String abstractStr) {
    this.abstractStr = abstractStr;
  }

  public String getNewType() {
    return newType;
  }

  public void setNewType(String newType) {
    this.newType = newType;
  }

  public String getCenterName() {
    return centerName;
  }

  public void setCenterName(String centerName) {
    this.centerName = centerName;
  }

  public String getCenterProjectName() {
    return centerProjectName;
  }

  public void setCenterProjectName(String centerProjectName) {
    this.centerProjectName = centerProjectName;
  }

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
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

  public Registration getOwner() {
    return owner;
  }

  public void setOwner(Registration owner) {
    this.owner = owner;
  }

  public StudyType getExistingType() {
    return existingType;
  }

  public void setExistingType(StudyType existingType) {
    this.existingType = existingType;
  }

  public SortedSet<Experiment> getExperiments() {
    return experiments;
  }

  public void setExperiments(SortedSet<Experiment> experiments) {
    this.experiments = experiments;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public Integer getExistingTypeInt() {
    return existingTypeInt;
  }

  public void setExistingTypeInt(Integer existingTypeInt) {
    this.existingTypeInt = existingTypeInt;
  }

  public SortedSet<ShareStudy> getSharedStudies() {
    return sharedStudies;
  }

  public void setSharedStudies(SortedSet<ShareStudy> sharedStudies) {
    this.sharedStudies = sharedStudies;
  }

  public Set<Processing> getProcessings() {
    return processings;
  }

  public void setProcessings(Set<Processing> processings) {
    this.processings = processings;
  }

  public Boolean getIsSelected() {
    return isSelected;
  }

  public void setIsSelected(Boolean isSelected) {
    this.isSelected = isSelected;
  }

  public Boolean getIsHasFile() {
    return isHasFile;
  }

  public void setIsHasFile(Boolean isHasFile) {
    this.isHasFile = isHasFile;
  }

  public String getHtml() {
    return html;
  }

  public void setHtml(String html) {
    this.html = html;
  }

  public Set<StudyLink> getStudyLinks() {
    return studyLinks;
  }

  public void setStudyLinks(Set<StudyLink> studyLinks) {
    this.studyLinks = studyLinks;
  }

  @XmlElementWrapper(name = "StudyAttributes")
  @XmlElement(name = "StudyAttribute")
  public Set<StudyAttribute> getStudyAttributes() {
    return studyAttributes;
  }

  public void setStudyAttributes(Set<StudyAttribute> studyAttributes) {
    this.studyAttributes = studyAttributes;
  }

  @Override
  public boolean givesPermission(Registration registration) {
    boolean hasPermission = false;
    if (registration == null) {
      Logger.getLogger(Study.class).warn("Registration is null!");
      hasPermission = false;
    } else if (registration.isLIMSAdmin()) {
      Logger.getLogger(Study.class).warn("Study gives permission");
      hasPermission = true;
    } else if (owner != null || sharedStudies != null) {
      hasPermission = false;
      if (owner != null && registration.equals(this.getOwner())) {
        Logger.getLogger(Study.class).warn("User owns study");
        hasPermission = true;
      }
      if (sharedStudies != null) {
        for (ShareStudy ss : sharedStudies) {
          if (registration.equals(ss.getRegistration())) {
            Logger.getLogger(Study.class).warn("User is linked to study");
            hasPermission = true;
            break;
          } else if (owner != null || sharedStudies != null) {
            hasPermission = false;
            if (owner != null && registration.equals(this.getOwner())) {
              Logger.getLogger(Study.class).info("User owns study");
              hasPermission = true;
            }
            if (sharedStudies != null) {
              for (ShareStudy shares : sharedStudies) {
                if (registration.equals(shares.getRegistration())) {
                  Logger.getLogger(Study.class).info("User is linked to study");
                  hasPermission = true;
                  break;
                }
              }
            }
          } else {
            Logger.getLogger(Study.class).warn("Study does not give permission");
            hasPermission = false;
          }
        }
      }
    } else {
      Logger.getLogger(Study.class).warn("Study does not give permission");
      hasPermission = false;
    }
    if (!hasPermission) {
      throw new SecurityException("User " + registration.getEmailAddress()
          + " does not have permission to modify aspects of study " + toString());
    }
    return hasPermission;

  }
}
