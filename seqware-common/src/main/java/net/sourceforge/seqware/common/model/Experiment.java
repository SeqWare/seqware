package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.security.PermissionsAware;
import net.sourceforge.seqware.common.util.jsontools.JsonUtil;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

@XmlRootElement
public class Experiment implements Serializable, Comparable<Experiment>, PermissionsAware {

  private static final long serialVersionUID = 2L;
  private Integer experimentId;
  private Integer swAccession;
  private String title;
  private String name;
  private String description;
  private String alias;
  private String accession;
  private String status;
  private String centerName;
  private String sequenceSpace;
  private String baseCaller;
  private String qualityScorer;
  private Integer qualityNumberOfLevels;
  private Integer qualityMultiplier;
  private String qualityType;
  private Integer expectedNumberRuns;
  private Long expectedNumberSpots;
  private Long expectedNumberReads;
  private Registration owner;
  private Study study;
  private ExperimentLibraryDesign experimentLibraryDesign;
  private ExperimentSpotDesign experimentSpotDesign;
  private Platform platform;
  // these are non-persisted fields used by the experiment_library_design table
  private String expLibDesignName;
  private String expLibDesignDesc;
  private String expLibDesignProtocol;
  private Integer expLibDesignStrategy;
  private Integer expLibDesignSource;
  private Integer expLibDesignSelection;
  // these are non-persisted fields used by the experiment_spot_design and
  // experiment_spot_design_read_spec tables
  private String spotDesignReadSpec;
  private String expSpotDesignTagSpec;
  private String expSpotDesignAdapterSpec;
  private Integer expSpotDesignReadsPerSpot;
  private Set<Processing> processings = new TreeSet<Processing>();
  private Set<ExperimentAttribute> experimentAttributes = new TreeSet<ExperimentAttribute>();
  private Set<ExperimentLink> experimentLinks = new TreeSet<ExperimentLink>();
  // these are non-persisted fields used by the platform connection
  private Integer platformInt;
  private SortedSet<Sample> samples;
  private Date createTimestamp;
  private Date updateTimestamp;
  private Boolean isSelected = false;
  private Boolean isHasFile = false;
  // addition form fields
  private String strExpectedNumberRuns;
  private String strExpectedNumberReads;

  public Experiment() {
    super();
  }

  @Override
  public int compareTo(Experiment that) {
    if (that == null) {
      return -1;
    }

    if (that.getExperimentId() == this.getExperimentId()) // when both names are
    // null
    {
      return 0;
    }

    if (that.getExperimentId() == null) {
      return -1; // when only the other name is null
    }
    return (that.getExperimentId().compareTo(this.getExperimentId()));
  }

  // @Override
  // public String toString() {
  // return new ToStringBuilder(this).append("experimentId",
  // getExperimentId()).append("name", getName()).toString();
  // }
  @Override
  public String toString() {
    return "Experiment{" + "experimentId=" + experimentId + ", swAccession=" + swAccession + ", title=" + title
        + ", name=" + name + ", description=" + description + ", status=" + status + ", createTimestamp="
        + createTimestamp + ", updateTimestamp=" + updateTimestamp + '}';
  }

  @Override
  public boolean equals(Object other) {
    if ((this == other)) {
      return true;
    }
    if (!(other instanceof Experiment)) {
      return false;
    }
    Experiment castOther = (Experiment) other;
    return new EqualsBuilder().append(this.getExperimentId(), castOther.getExperimentId()).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(getName()).toHashCode();
  }

  /*
   * public int compareTo(Experiment that) { if(that == null) return -1;
   * 
   * if(that.getName() == this.getName()) // when both names are null return 0;
   * 
   * if(that.getName() == null) return -1; // when only the other name is null
   * 
   * return(that.getName().compareTo(this.getName())); }
   * 
   * public String toString() { return new ToStringBuilder(this)
   * .append("experimentId", getExperimentId()) .append("name", getName())
   * .toString(); }
   * 
   * public boolean equals(Object other) { if ( (this == other ) ) return true;
   * if ( !(other instanceof Experiment) ) return false; Experiment castOther =
   * (Experiment) other; return new EqualsBuilder() .append(this.getName(),
   * castOther.getName()) .isEquals(); }
   * 
   * public int hashCode() { return new HashCodeBuilder() .append(getName())
   * .toHashCode(); }
   */
  public Platform getPlatform() {
    return platform;
  }

  public void setPlatform(Platform platform) {
    this.platform = platform;
  }

  public ExperimentLibraryDesign getExperimentLibraryDesign() {
    return experimentLibraryDesign;
  }

  public void setExperimentLibraryDesign(ExperimentLibraryDesign experimentLibraryDesign) {
    this.experimentLibraryDesign = experimentLibraryDesign;
  }

  public String getExpLibDesignName() {
    return expLibDesignName;
  }

  public void setExpLibDesignName(String expLibDesignName) {
    this.expLibDesignName = expLibDesignName;
  }

  public String getExpLibDesignDesc() {
    return expLibDesignDesc;
  }

  public void setExpLibDesignDesc(String expLibDesignDesc) {
    this.expLibDesignDesc = expLibDesignDesc;
  }

  public String getExpLibDesignProtocol() {
    return expLibDesignProtocol;
  }

  public void setExpLibDesignProtocol(String expLibDesignProtocol) {
    this.expLibDesignProtocol = expLibDesignProtocol;
  }

  public Integer getExpLibDesignStrategy() {
    return expLibDesignStrategy;
  }

  public void setExpLibDesignStrategy(Integer expLibDesignStrategy) {
    this.expLibDesignStrategy = expLibDesignStrategy;
  }

  public Integer getExpLibDesignSource() {
    return expLibDesignSource;
  }

  public void setExpLibDesignSource(Integer expLibDesignSource) {
    this.expLibDesignSource = expLibDesignSource;
  }

  public Integer getExpLibDesignSelection() {
    return expLibDesignSelection;
  }

  public void setExpLibDesignSelection(Integer expLibDesignSelection) {
    this.expLibDesignSelection = expLibDesignSelection;
  }

  public String getSpotDesignReadSpec() {
    return spotDesignReadSpec;
  }

  public void setSpotDesignReadSpec(String spotDesignReadSpec) {
    this.spotDesignReadSpec = spotDesignReadSpec;
  }

  public Integer getPlatformInt() {
    return platformInt;
  }

  public void setPlatformInt(Integer platformInt) {
    this.platformInt = platformInt;
  }

  public Integer getExperimentId() {
    return experimentId;
  }

  public void setExperimentId(Integer experimentId) {
    this.experimentId = experimentId;
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

  public String getName() {
    return name;
  }

  public String getJsonEscapeName() {
    return JsonUtil.forJSON(name);
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getJsonEscapeDescription() {
    return JsonUtil.forJSON(description);
  }

  public String getJsonEscapeDescription200() {
    if (description.length() > 200) {
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

  public String getCenterName() {
    return centerName;
  }

  public void setCenterName(String centerName) {
    this.centerName = centerName;
  }

  public String getSequenceSpace() {
    return sequenceSpace;
  }

  public void setSequenceSpace(String sequenceSpace) {
    this.sequenceSpace = sequenceSpace;
  }

  public String getBaseCaller() {
    return baseCaller;
  }

  public void setBaseCaller(String baseCaller) {
    this.baseCaller = baseCaller;
  }

  public String getQualityScorer() {
    return qualityScorer;
  }

  public void setQualityScorer(String qualityScorer) {
    this.qualityScorer = qualityScorer;
  }

  public Integer getQualityNumberOfLevels() {
    return qualityNumberOfLevels;
  }

  public void setQualityNumberOfLevels(Integer qualityNumberOfLevels) {
    this.qualityNumberOfLevels = qualityNumberOfLevels;
  }

  public Integer getQualityMultiplier() {
    return qualityMultiplier;
  }

  public void setQualityMultiplier(Integer qualityMultiplier) {
    this.qualityMultiplier = qualityMultiplier;
  }

  public String getQualityType() {
    return qualityType;
  }

  public void setQualityType(String qualityType) {
    this.qualityType = qualityType;
  }

  public Integer getExpectedNumberRuns() {
    return expectedNumberRuns;
  }

  public void setExpectedNumberRuns(Integer expectedNumberRuns) {
    if (expectedNumberRuns != null) {
      this.strExpectedNumberRuns = expectedNumberRuns.toString();
    }
    this.expectedNumberRuns = expectedNumberRuns;
  }

  public Long getExpectedNumberSpots() {
    return expectedNumberSpots;
  }

  public void setExpectedNumberSpots(Long expectedNumberSpots) {
    this.expectedNumberSpots = expectedNumberSpots;
  }

  public Long getExpectedNumberReads() {
    return expectedNumberReads;
  }

  public void setExpectedNumberReads(Long expectedNumberReads) {
    if (expectedNumberReads != null) {
      this.strExpectedNumberReads = expectedNumberReads.toString();
    }
    this.expectedNumberReads = expectedNumberReads;
  }

  public Registration getOwner() {
    return owner;
  }

  public void setOwner(Registration owner) {
    this.owner = owner;
  }

  public Study getStudy() {
    return study;
  }

  public void setStudy(Study study) {
    this.study = study;
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

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public SortedSet<Sample> getSamples() {
    return samples;
  }

  public void setSamples(SortedSet<Sample> samples) {
    this.samples = samples;
  }

  public String getExpSpotDesignTagSpec() {
    return expSpotDesignTagSpec;
  }

  public void setExpSpotDesignTagSpec(String expSpotDesignTagSpec) {
    this.expSpotDesignTagSpec = expSpotDesignTagSpec;
  }

  public String getExpSpotDesignAdapterSpec() {
    return expSpotDesignAdapterSpec;
  }

  public void setExpSpotDesignAdapterSpec(String expSpotDesignAdapterSpec) {
    this.expSpotDesignAdapterSpec = expSpotDesignAdapterSpec;
  }

  public Integer getExpSpotDesignReadsPerSpot() {
    return expSpotDesignReadsPerSpot;
  }

  public void setExpSpotDesignReadsPerSpot(Integer expSpotDesignReadsPerSpot) {
    this.expSpotDesignReadsPerSpot = expSpotDesignReadsPerSpot;
  }

  public ExperimentSpotDesign getExperimentSpotDesign() {
    return experimentSpotDesign;
  }

  public void setExperimentSpotDesign(ExperimentSpotDesign experimentSpotDesign) {
    this.experimentSpotDesign = experimentSpotDesign;
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

  public String getStrExpectedNumberRuns() {
    return strExpectedNumberRuns;
  }

  public void setStrExpectedNumberRuns(String strExpectedNumberRuns) {
    // try{
    // this.expectedNumberRuns = Integer.parseInt(strExpectedNumberRuns);
    // }catch (Exception e) {}
    this.strExpectedNumberRuns = strExpectedNumberRuns;
  }

  public String getStrExpectedNumberReads() {
    return strExpectedNumberReads;
  }

  public void setStrExpectedNumberReads(String strExpectedNumberReads) {
    // try{
    // this.expectedNumberRuns = Integer.parseInt(strExpectedNumberReads);
    // }catch (Exception e) {}
    this.strExpectedNumberReads = strExpectedNumberReads;
  }

  public Set<Processing> getProcessings() {
    return processings;
  }

  public void setProcessings(Set<Processing> processings) {
    this.processings = processings;
  }

  @XmlElementWrapper(name = "ExperimentAttributes")
  @XmlElement(name = "ExperimentAttribute")
  public Set<ExperimentAttribute> getExperimentAttributes() {
    return experimentAttributes;
  }

  public void setExperimentAttributes(Set<ExperimentAttribute> experimentAttributes) {
    this.experimentAttributes = experimentAttributes;
  }

  public Set<ExperimentLink> getExperimentLinks() {
    return experimentLinks;
  }

  public void setExperimentLinks(Set<ExperimentLink> experimentLinks) {
    this.experimentLinks = experimentLinks;
  }

  @Override
  public boolean givesPermission(Registration registration) {
    boolean hasPermission = false;
    if (study != null) {
      StudyService ss = BeanFactory.getStudyServiceBean();
      Study newStudy = ss.findBySWAccession(study.getSwAccession());
      hasPermission = newStudy.givesPermission(registration);
    } else {// orphaned Experiment
      if (registration.equals(this.owner) || registration.isLIMSAdmin()) {
        Logger.getLogger(Experiment.class).warn("Modifying Orphan Experiment: " + this.getName());
        hasPermission = true;
      } else if (owner == null) {
        Logger.getLogger(Experiment.class).warn(
            "Experiment has no owner! Modifying Orphan Experiment: " + this.getName());
        hasPermission = true;
      } else {
        Logger.getLogger(Experiment.class).warn("Not modifying Orphan Experiment: " + this.getName());
        hasPermission = false;
      }
    }
    if (!hasPermission) {
      Logger.getLogger(Experiment.class).info("Experiment does not give permission");
      throw new SecurityException("User " + registration.getEmailAddress() + " does not have permission to modify "
          + this.getName());
    }
    return hasPermission;
  }
}
