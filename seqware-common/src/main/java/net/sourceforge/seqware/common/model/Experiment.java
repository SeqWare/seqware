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
/**
 * <p>Experiment class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
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

  /**
   * <p>Constructor for Experiment.</p>
   */
  public Experiment() {
    super();
  }

  /** {@inheritDoc} */
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
  /** {@inheritDoc} */
  @Override
  public String toString() {
    return "Experiment{" + "experimentId=" + experimentId + ", swAccession=" + swAccession + ", title=" + title
        + ", name=" + name + ", description=" + description + ", status=" + status + ", createTimestamp="
        + createTimestamp + ", updateTimestamp=" + updateTimestamp + '}';
  }

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
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
  /**
   * <p>Getter for the field <code>platform</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.Platform} object.
   */
  public Platform getPlatform() {
    return platform;
  }

  /**
   * <p>Setter for the field <code>platform</code>.</p>
   *
   * @param platform a {@link net.sourceforge.seqware.common.model.Platform} object.
   */
  public void setPlatform(Platform platform) {
    this.platform = platform;
  }

  /**
   * <p>Getter for the field <code>experimentLibraryDesign</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.ExperimentLibraryDesign} object.
   */
  public ExperimentLibraryDesign getExperimentLibraryDesign() {
    return experimentLibraryDesign;
  }

  /**
   * <p>Setter for the field <code>experimentLibraryDesign</code>.</p>
   *
   * @param experimentLibraryDesign a {@link net.sourceforge.seqware.common.model.ExperimentLibraryDesign} object.
   */
  public void setExperimentLibraryDesign(ExperimentLibraryDesign experimentLibraryDesign) {
    this.experimentLibraryDesign = experimentLibraryDesign;
  }

  /**
   * <p>Getter for the field <code>expLibDesignName</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getExpLibDesignName() {
    return expLibDesignName;
  }

  /**
   * <p>Setter for the field <code>expLibDesignName</code>.</p>
   *
   * @param expLibDesignName a {@link java.lang.String} object.
   */
  public void setExpLibDesignName(String expLibDesignName) {
    this.expLibDesignName = expLibDesignName;
  }

  /**
   * <p>Getter for the field <code>expLibDesignDesc</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getExpLibDesignDesc() {
    return expLibDesignDesc;
  }

  /**
   * <p>Setter for the field <code>expLibDesignDesc</code>.</p>
   *
   * @param expLibDesignDesc a {@link java.lang.String} object.
   */
  public void setExpLibDesignDesc(String expLibDesignDesc) {
    this.expLibDesignDesc = expLibDesignDesc;
  }

  /**
   * <p>Getter for the field <code>expLibDesignProtocol</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getExpLibDesignProtocol() {
    return expLibDesignProtocol;
  }

  /**
   * <p>Setter for the field <code>expLibDesignProtocol</code>.</p>
   *
   * @param expLibDesignProtocol a {@link java.lang.String} object.
   */
  public void setExpLibDesignProtocol(String expLibDesignProtocol) {
    this.expLibDesignProtocol = expLibDesignProtocol;
  }

  /**
   * <p>Getter for the field <code>expLibDesignStrategy</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getExpLibDesignStrategy() {
    return expLibDesignStrategy;
  }

  /**
   * <p>Setter for the field <code>expLibDesignStrategy</code>.</p>
   *
   * @param expLibDesignStrategy a {@link java.lang.Integer} object.
   */
  public void setExpLibDesignStrategy(Integer expLibDesignStrategy) {
    this.expLibDesignStrategy = expLibDesignStrategy;
  }

  /**
   * <p>Getter for the field <code>expLibDesignSource</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getExpLibDesignSource() {
    return expLibDesignSource;
  }

  /**
   * <p>Setter for the field <code>expLibDesignSource</code>.</p>
   *
   * @param expLibDesignSource a {@link java.lang.Integer} object.
   */
  public void setExpLibDesignSource(Integer expLibDesignSource) {
    this.expLibDesignSource = expLibDesignSource;
  }

  /**
   * <p>Getter for the field <code>expLibDesignSelection</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getExpLibDesignSelection() {
    return expLibDesignSelection;
  }

  /**
   * <p>Setter for the field <code>expLibDesignSelection</code>.</p>
   *
   * @param expLibDesignSelection a {@link java.lang.Integer} object.
   */
  public void setExpLibDesignSelection(Integer expLibDesignSelection) {
    this.expLibDesignSelection = expLibDesignSelection;
  }

  /**
   * <p>Getter for the field <code>spotDesignReadSpec</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getSpotDesignReadSpec() {
    return spotDesignReadSpec;
  }

  /**
   * <p>Setter for the field <code>spotDesignReadSpec</code>.</p>
   *
   * @param spotDesignReadSpec a {@link java.lang.String} object.
   */
  public void setSpotDesignReadSpec(String spotDesignReadSpec) {
    this.spotDesignReadSpec = spotDesignReadSpec;
  }

  /**
   * <p>Getter for the field <code>platformInt</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getPlatformInt() {
    return platformInt;
  }

  /**
   * <p>Setter for the field <code>platformInt</code>.</p>
   *
   * @param platformInt a {@link java.lang.Integer} object.
   */
  public void setPlatformInt(Integer platformInt) {
    this.platformInt = platformInt;
  }

  /**
   * <p>Getter for the field <code>experimentId</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getExperimentId() {
    return experimentId;
  }

  /**
   * <p>Setter for the field <code>experimentId</code>.</p>
   *
   * @param experimentId a {@link java.lang.Integer} object.
   */
  public void setExperimentId(Integer experimentId) {
    this.experimentId = experimentId;
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
   * <p>Getter for the field <code>name</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getName() {
    return name;
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
   * <p>Setter for the field <code>name</code>.</p>
   *
   * @param name a {@link java.lang.String} object.
   */
  public void setName(String name) {
    this.name = name;
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
    if (description.length() > 200) {
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
   * <p>Getter for the field <code>sequenceSpace</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getSequenceSpace() {
    return sequenceSpace;
  }

  /**
   * <p>Setter for the field <code>sequenceSpace</code>.</p>
   *
   * @param sequenceSpace a {@link java.lang.String} object.
   */
  public void setSequenceSpace(String sequenceSpace) {
    this.sequenceSpace = sequenceSpace;
  }

  /**
   * <p>Getter for the field <code>baseCaller</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getBaseCaller() {
    return baseCaller;
  }

  /**
   * <p>Setter for the field <code>baseCaller</code>.</p>
   *
   * @param baseCaller a {@link java.lang.String} object.
   */
  public void setBaseCaller(String baseCaller) {
    this.baseCaller = baseCaller;
  }

  /**
   * <p>Getter for the field <code>qualityScorer</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getQualityScorer() {
    return qualityScorer;
  }

  /**
   * <p>Setter for the field <code>qualityScorer</code>.</p>
   *
   * @param qualityScorer a {@link java.lang.String} object.
   */
  public void setQualityScorer(String qualityScorer) {
    this.qualityScorer = qualityScorer;
  }

  /**
   * <p>Getter for the field <code>qualityNumberOfLevels</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getQualityNumberOfLevels() {
    return qualityNumberOfLevels;
  }

  /**
   * <p>Setter for the field <code>qualityNumberOfLevels</code>.</p>
   *
   * @param qualityNumberOfLevels a {@link java.lang.Integer} object.
   */
  public void setQualityNumberOfLevels(Integer qualityNumberOfLevels) {
    this.qualityNumberOfLevels = qualityNumberOfLevels;
  }

  /**
   * <p>Getter for the field <code>qualityMultiplier</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getQualityMultiplier() {
    return qualityMultiplier;
  }

  /**
   * <p>Setter for the field <code>qualityMultiplier</code>.</p>
   *
   * @param qualityMultiplier a {@link java.lang.Integer} object.
   */
  public void setQualityMultiplier(Integer qualityMultiplier) {
    this.qualityMultiplier = qualityMultiplier;
  }

  /**
   * <p>Getter for the field <code>qualityType</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getQualityType() {
    return qualityType;
  }

  /**
   * <p>Setter for the field <code>qualityType</code>.</p>
   *
   * @param qualityType a {@link java.lang.String} object.
   */
  public void setQualityType(String qualityType) {
    this.qualityType = qualityType;
  }

  /**
   * <p>Getter for the field <code>expectedNumberRuns</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getExpectedNumberRuns() {
    return expectedNumberRuns;
  }

  /**
   * <p>Setter for the field <code>expectedNumberRuns</code>.</p>
   *
   * @param expectedNumberRuns a {@link java.lang.Integer} object.
   */
  public void setExpectedNumberRuns(Integer expectedNumberRuns) {
    if (expectedNumberRuns != null) {
      this.strExpectedNumberRuns = expectedNumberRuns.toString();
    }
    this.expectedNumberRuns = expectedNumberRuns;
  }

  /**
   * <p>Getter for the field <code>expectedNumberSpots</code>.</p>
   *
   * @return a {@link java.lang.Long} object.
   */
  public Long getExpectedNumberSpots() {
    return expectedNumberSpots;
  }

  /**
   * <p>Setter for the field <code>expectedNumberSpots</code>.</p>
   *
   * @param expectedNumberSpots a {@link java.lang.Long} object.
   */
  public void setExpectedNumberSpots(Long expectedNumberSpots) {
    this.expectedNumberSpots = expectedNumberSpots;
  }

  /**
   * <p>Getter for the field <code>expectedNumberReads</code>.</p>
   *
   * @return a {@link java.lang.Long} object.
   */
  public Long getExpectedNumberReads() {
    return expectedNumberReads;
  }

  /**
   * <p>Setter for the field <code>expectedNumberReads</code>.</p>
   *
   * @param expectedNumberReads a {@link java.lang.Long} object.
   */
  public void setExpectedNumberReads(Long expectedNumberReads) {
    if (expectedNumberReads != null) {
      this.strExpectedNumberReads = expectedNumberReads.toString();
    }
    this.expectedNumberReads = expectedNumberReads;
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
   * <p>Getter for the field <code>study</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.Study} object.
   */
  public Study getStudy() {
    return study;
  }

  /**
   * <p>Setter for the field <code>study</code>.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   */
  public void setStudy(Study study) {
    this.study = study;
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
   * <p>Getter for the field <code>serialVersionUID</code>.</p>
   *
   * @return a long.
   */
  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  /**
   * <p>Getter for the field <code>samples</code>.</p>
   *
   * @return a {@link java.util.SortedSet} object.
   */
  public SortedSet<Sample> getSamples() {
    return samples;
  }

  /**
   * <p>Setter for the field <code>samples</code>.</p>
   *
   * @param samples a {@link java.util.SortedSet} object.
   */
  public void setSamples(SortedSet<Sample> samples) {
    this.samples = samples;
  }

  /**
   * <p>Getter for the field <code>expSpotDesignTagSpec</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getExpSpotDesignTagSpec() {
    return expSpotDesignTagSpec;
  }

  /**
   * <p>Setter for the field <code>expSpotDesignTagSpec</code>.</p>
   *
   * @param expSpotDesignTagSpec a {@link java.lang.String} object.
   */
  public void setExpSpotDesignTagSpec(String expSpotDesignTagSpec) {
    this.expSpotDesignTagSpec = expSpotDesignTagSpec;
  }

  /**
   * <p>Getter for the field <code>expSpotDesignAdapterSpec</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getExpSpotDesignAdapterSpec() {
    return expSpotDesignAdapterSpec;
  }

  /**
   * <p>Setter for the field <code>expSpotDesignAdapterSpec</code>.</p>
   *
   * @param expSpotDesignAdapterSpec a {@link java.lang.String} object.
   */
  public void setExpSpotDesignAdapterSpec(String expSpotDesignAdapterSpec) {
    this.expSpotDesignAdapterSpec = expSpotDesignAdapterSpec;
  }

  /**
   * <p>Getter for the field <code>expSpotDesignReadsPerSpot</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getExpSpotDesignReadsPerSpot() {
    return expSpotDesignReadsPerSpot;
  }

  /**
   * <p>Setter for the field <code>expSpotDesignReadsPerSpot</code>.</p>
   *
   * @param expSpotDesignReadsPerSpot a {@link java.lang.Integer} object.
   */
  public void setExpSpotDesignReadsPerSpot(Integer expSpotDesignReadsPerSpot) {
    this.expSpotDesignReadsPerSpot = expSpotDesignReadsPerSpot;
  }

  /**
   * <p>Getter for the field <code>experimentSpotDesign</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesign} object.
   */
  public ExperimentSpotDesign getExperimentSpotDesign() {
    return experimentSpotDesign;
  }

  /**
   * <p>Setter for the field <code>experimentSpotDesign</code>.</p>
   *
   * @param experimentSpotDesign a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesign} object.
   */
  public void setExperimentSpotDesign(ExperimentSpotDesign experimentSpotDesign) {
    this.experimentSpotDesign = experimentSpotDesign;
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
   * <p>Getter for the field <code>strExpectedNumberRuns</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getStrExpectedNumberRuns() {
    return strExpectedNumberRuns;
  }

  /**
   * <p>Setter for the field <code>strExpectedNumberRuns</code>.</p>
   *
   * @param strExpectedNumberRuns a {@link java.lang.String} object.
   */
  public void setStrExpectedNumberRuns(String strExpectedNumberRuns) {
    // try{
    // this.expectedNumberRuns = Integer.parseInt(strExpectedNumberRuns);
    // }catch (Exception e) {}
    this.strExpectedNumberRuns = strExpectedNumberRuns;
  }

  /**
   * <p>Getter for the field <code>strExpectedNumberReads</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getStrExpectedNumberReads() {
    return strExpectedNumberReads;
  }

  /**
   * <p>Setter for the field <code>strExpectedNumberReads</code>.</p>
   *
   * @param strExpectedNumberReads a {@link java.lang.String} object.
   */
  public void setStrExpectedNumberReads(String strExpectedNumberReads) {
    // try{
    // this.expectedNumberRuns = Integer.parseInt(strExpectedNumberReads);
    // }catch (Exception e) {}
    this.strExpectedNumberReads = strExpectedNumberReads;
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
   * <p>Getter for the field <code>experimentAttributes</code>.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  @XmlElementWrapper(name = "ExperimentAttributes")
  @XmlElement(name = "ExperimentAttribute")
  public Set<ExperimentAttribute> getExperimentAttributes() {
    return experimentAttributes;
  }

  /**
   * <p>Setter for the field <code>experimentAttributes</code>.</p>
   *
   * @param experimentAttributes a {@link java.util.Set} object.
   */
  public void setExperimentAttributes(Set<ExperimentAttribute> experimentAttributes) {
    this.experimentAttributes = experimentAttributes;
  }

  /**
   * <p>Getter for the field <code>experimentLinks</code>.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public Set<ExperimentLink> getExperimentLinks() {
    return experimentLinks;
  }

  /**
   * <p>Setter for the field <code>experimentLinks</code>.</p>
   *
   * @param experimentLinks a {@link java.util.Set} object.
   */
  public void setExperimentLinks(Set<ExperimentLink> experimentLinks) {
    this.experimentLinks = experimentLinks;
  }

  /** {@inheritDoc} */
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
