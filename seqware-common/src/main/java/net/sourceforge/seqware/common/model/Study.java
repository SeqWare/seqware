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
/**
 * <p>Study class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
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

  /**
   * <p>Constructor for Study.</p>
   */
  public Study() {
    super();
  }

  /**
   * <p>compareTo.</p>
   *
   * @param that a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a int.
   */
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

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
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
  /**
   * <p>Getter for the field <code>studyId</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getStudyId() {
    return studyId;
  }

  /**
   * <p>Setter for the field <code>studyId</code>.</p>
   *
   * @param studyId a {@link java.lang.Integer} object.
   */
  public void setStudyId(Integer studyId) {
    this.studyId = studyId;
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
   * <p>Getter for the field <code>title</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getTitle() {
    return title;
  }

  /**
   * <p>getJsonEscapeTitle.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getJsonEscapeTitle() {
    return JsonUtil.forJSON(title);
  }

  /**
   * <p>Setter for the field <code>title</code>.</p>
   *
   * @param title a {@link java.lang.String} object.
   */
  public void setTitle(String title) {
    this.title = title;
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
   * <p>getJsonEscapeDescription200.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getJsonEscapeDescription200() {
    if (description != null && description.length() > 200) {
      return JsonUtil.forJSON(description.substring(0, 200)) + " ...";
    } else {
      return getJsonEscapeDescription();
    }
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
   * <p>Getter for the field <code>accession</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getAccession() {
    return accession;
  }

  /**
   * <p>Setter for the field <code>accession</code>.</p>
   *
   * @param accession a {@link java.lang.String} object.
   */
  public void setAccession(String accession) {
    this.accession = accession;
  }

  /**
   * <p>Getter for the field <code>status</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getStatus() {
    return status;
  }

  /**
   * <p>Setter for the field <code>status</code>.</p>
   *
   * @param status a {@link java.lang.String} object.
   */
  public void setStatus(String status) {
    this.status = status;
  }

  /**
   * <p>Getter for the field <code>abstractStr</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getAbstractStr() {
    return abstractStr;
  }

  /**
   * <p>Setter for the field <code>abstractStr</code>.</p>
   *
   * @param abstractStr a {@link java.lang.String} object.
   */
  public void setAbstractStr(String abstractStr) {
    this.abstractStr = abstractStr;
  }

  /**
   * <p>Getter for the field <code>newType</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getNewType() {
    return newType;
  }

  /**
   * <p>Setter for the field <code>newType</code>.</p>
   *
   * @param newType a {@link java.lang.String} object.
   */
  public void setNewType(String newType) {
    this.newType = newType;
  }

  /**
   * <p>Getter for the field <code>centerName</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getCenterName() {
    return centerName;
  }

  /**
   * <p>Setter for the field <code>centerName</code>.</p>
   *
   * @param centerName a {@link java.lang.String} object.
   */
  public void setCenterName(String centerName) {
    this.centerName = centerName;
  }

  /**
   * <p>Getter for the field <code>centerProjectName</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getCenterProjectName() {
    return centerProjectName;
  }

  /**
   * <p>Setter for the field <code>centerProjectName</code>.</p>
   *
   * @param centerProjectName a {@link java.lang.String} object.
   */
  public void setCenterProjectName(String centerProjectName) {
    this.centerProjectName = centerProjectName;
  }

  /**
   * <p>Getter for the field <code>projectId</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getProjectId() {
    return projectId;
  }

  /**
   * <p>Setter for the field <code>projectId</code>.</p>
   *
   * @param projectId a {@link java.lang.Integer} object.
   */
  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
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
   * <p>Getter for the field <code>existingType</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.StudyType} object.
   */
  public StudyType getExistingType() {
    return existingType;
  }

  /**
   * <p>Setter for the field <code>existingType</code>.</p>
   *
   * @param existingType a {@link net.sourceforge.seqware.common.model.StudyType} object.
   */
  public void setExistingType(StudyType existingType) {
    this.existingType = existingType;
  }

  /**
   * <p>Getter for the field <code>experiments</code>.</p>
   *
   * @return a {@link java.util.SortedSet} object.
   */
  public SortedSet<Experiment> getExperiments() {
    return experiments;
  }

  /**
   * <p>Setter for the field <code>experiments</code>.</p>
   *
   * @param experiments a {@link java.util.SortedSet} object.
   */
  public void setExperiments(SortedSet<Experiment> experiments) {
    this.experiments = experiments;
  }

  /**
   * <p>Getter for the field <code>serialVersionUID</code>.</p>
   *
   * @return a long.
   */
  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  /**
   * <p>Getter for the field <code>existingTypeInt</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getExistingTypeInt() {
    return existingTypeInt;
  }

  /**
   * <p>Setter for the field <code>existingTypeInt</code>.</p>
   *
   * @param existingTypeInt a {@link java.lang.Integer} object.
   */
  public void setExistingTypeInt(Integer existingTypeInt) {
    this.existingTypeInt = existingTypeInt;
  }

  /**
   * <p>Getter for the field <code>sharedStudies</code>.</p>
   *
   * @return a {@link java.util.SortedSet} object.
   */
  public SortedSet<ShareStudy> getSharedStudies() {
    return sharedStudies;
  }

  /**
   * <p>Setter for the field <code>sharedStudies</code>.</p>
   *
   * @param sharedStudies a {@link java.util.SortedSet} object.
   */
  public void setSharedStudies(SortedSet<ShareStudy> sharedStudies) {
    this.sharedStudies = sharedStudies;
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
   * <p>Getter for the field <code>html</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getHtml() {
    return html;
  }

  /**
   * <p>Setter for the field <code>html</code>.</p>
   *
   * @param html a {@link java.lang.String} object.
   */
  public void setHtml(String html) {
    this.html = html;
  }

  /**
   * <p>Getter for the field <code>studyLinks</code>.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public Set<StudyLink> getStudyLinks() {
    return studyLinks;
  }

  /**
   * <p>Setter for the field <code>studyLinks</code>.</p>
   *
   * @param studyLinks a {@link java.util.Set} object.
   */
  public void setStudyLinks(Set<StudyLink> studyLinks) {
    this.studyLinks = studyLinks;
  }

  /**
   * <p>Getter for the field <code>studyAttributes</code>.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  @XmlElementWrapper(name = "StudyAttributes")
  @XmlElement(name = "StudyAttribute")
  public Set<StudyAttribute> getStudyAttributes() {
    return studyAttributes;
  }

  /**
   * <p>Setter for the field <code>studyAttributes</code>.</p>
   *
   * @param studyAttributes a {@link java.util.Set} object.
   */
  public void setStudyAttributes(Set<StudyAttribute> studyAttributes) {
    this.studyAttributes = studyAttributes;
  }

  /** {@inheritDoc} */
  @Override
  public boolean givesPermission(Registration registration) {
    boolean hasPermission = false;
    if (registration == null) {
      Logger.getLogger(Study.class).warn("Registration is null!");
      hasPermission = false;
    } else if (registration.isLIMSAdmin()) {
      Logger.getLogger(Study.class).info("Study gives permission");
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
