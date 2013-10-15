/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "experiment")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Experiment.findAll", query = "SELECT e FROM Experiment e"),
  @NamedQuery(name = "Experiment.findByExperimentId", query = "SELECT e FROM Experiment e WHERE e.experimentId = :experimentId"),
  @NamedQuery(name = "Experiment.findByName", query = "SELECT e FROM Experiment e WHERE e.name = :name"),
  @NamedQuery(name = "Experiment.findByTitle", query = "SELECT e FROM Experiment e WHERE e.title = :title"),
  @NamedQuery(name = "Experiment.findByDescription", query = "SELECT e FROM Experiment e WHERE e.description = :description"),
  @NamedQuery(name = "Experiment.findByAlias", query = "SELECT e FROM Experiment e WHERE e.alias = :alias"),
  @NamedQuery(name = "Experiment.findByAccession", query = "SELECT e FROM Experiment e WHERE e.accession = :accession"),
  @NamedQuery(name = "Experiment.findByStatus", query = "SELECT e FROM Experiment e WHERE e.status = :status"),
  @NamedQuery(name = "Experiment.findByCenterName", query = "SELECT e FROM Experiment e WHERE e.centerName = :centerName"),
  @NamedQuery(name = "Experiment.findBySequenceSpace", query = "SELECT e FROM Experiment e WHERE e.sequenceSpace = :sequenceSpace"),
  @NamedQuery(name = "Experiment.findByBaseCaller", query = "SELECT e FROM Experiment e WHERE e.baseCaller = :baseCaller"),
  @NamedQuery(name = "Experiment.findByQualityScorer", query = "SELECT e FROM Experiment e WHERE e.qualityScorer = :qualityScorer"),
  @NamedQuery(name = "Experiment.findByQualityNumberOfLevels", query = "SELECT e FROM Experiment e WHERE e.qualityNumberOfLevels = :qualityNumberOfLevels"),
  @NamedQuery(name = "Experiment.findByQualityMultiplier", query = "SELECT e FROM Experiment e WHERE e.qualityMultiplier = :qualityMultiplier"),
  @NamedQuery(name = "Experiment.findByQualityType", query = "SELECT e FROM Experiment e WHERE e.qualityType = :qualityType"),
  @NamedQuery(name = "Experiment.findByExpectedNumberRuns", query = "SELECT e FROM Experiment e WHERE e.expectedNumberRuns = :expectedNumberRuns"),
  @NamedQuery(name = "Experiment.findByExpectedNumberSpots", query = "SELECT e FROM Experiment e WHERE e.expectedNumberSpots = :expectedNumberSpots"),
  @NamedQuery(name = "Experiment.findByExpectedNumberReads", query = "SELECT e FROM Experiment e WHERE e.expectedNumberReads = :expectedNumberReads"),
  @NamedQuery(name = "Experiment.findBySwAccession", query = "SELECT e FROM Experiment e WHERE e.swAccession = :swAccession"),
  @NamedQuery(name = "Experiment.findByCreateTstmp", query = "SELECT e FROM Experiment e WHERE e.createTstmp = :createTstmp"),
  @NamedQuery(name = "Experiment.findByUpdateTstmp", query = "SELECT e FROM Experiment e WHERE e.updateTstmp = :updateTstmp")})
public class Experiment implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "experiment_id")
  private Integer experimentId;
  @Size(max = 2147483647)
  @Column(name = "name")
  private String name;
  @Size(max = 2147483647)
  @Column(name = "title")
  private String title;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @Size(max = 2147483647)
  @Column(name = "alias")
  private String alias;
  @Size(max = 2147483647)
  @Column(name = "accession")
  private String accession;
  @Size(max = 2147483647)
  @Column(name = "status")
  private String status;
  @Size(max = 2147483647)
  @Column(name = "center_name")
  private String centerName;
  @Size(max = 2147483647)
  @Column(name = "sequence_space")
  private String sequenceSpace;
  @Size(max = 2147483647)
  @Column(name = "base_caller")
  private String baseCaller;
  @Size(max = 2147483647)
  @Column(name = "quality_scorer")
  private String qualityScorer;
  @Column(name = "quality_number_of_levels")
  private Integer qualityNumberOfLevels;
  @Column(name = "quality_multiplier")
  private Integer qualityMultiplier;
  @Size(max = 2147483647)
  @Column(name = "quality_type")
  private String qualityType;
  @Column(name = "expected_number_runs")
  private Integer expectedNumberRuns;
  @Column(name = "expected_number_spots")
  private BigInteger expectedNumberSpots;
  @Column(name = "expected_number_reads")
  private BigInteger expectedNumberReads;
  @Column(name = "sw_accession")
  private Integer swAccession;
  @Basic(optional = false)
  @NotNull
  @Column(name = "create_tstmp")
  @Temporal(TemporalType.TIMESTAMP)
  private Date createTstmp;
  @Column(name = "update_tstmp")
  @Temporal(TemporalType.TIMESTAMP)
  private Date updateTstmp;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "experimentId")
  private Collection<ExperimentAttribute> experimentAttributeCollection;
  @JoinColumn(name = "study_id", referencedColumnName = "study_id")
  @ManyToOne(optional = false)
  private Study studyId;
  @JoinColumn(name = "owner_id", referencedColumnName = "registration_id")
  @ManyToOne
  private Registration ownerId;
  @JoinColumn(name = "platform_id", referencedColumnName = "platform_id")
  @ManyToOne
  private Platform platformId;
  @JoinColumn(name = "experiment_spot_design_id", referencedColumnName = "experiment_spot_design_id")
  @ManyToOne
  private ExperimentSpotDesign experimentSpotDesignId;
  @JoinColumn(name = "experiment_library_design_id", referencedColumnName = "experiment_library_design_id")
  @ManyToOne
  private ExperimentLibraryDesign experimentLibraryDesignId;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "experimentId")
  private Collection<ProcessingExperiments> processingExperimentsCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "experimentId")
  private Collection<ShareExperiment> shareExperimentCollection;
  @OneToMany(mappedBy = "experimentId")
  private Collection<Sample> sampleCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "experimentId")
  private Collection<ExperimentLink> experimentLinkCollection;

  public Experiment() {
  }

  public Experiment(Integer experimentId) {
    this.experimentId = experimentId;
  }

  public Experiment(Integer experimentId, Date createTstmp) {
    this.experimentId = experimentId;
    this.createTstmp = createTstmp;
  }

  public Integer getExperimentId() {
    return experimentId;
  }

  public void setExperimentId(Integer experimentId) {
    this.experimentId = experimentId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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
    this.expectedNumberRuns = expectedNumberRuns;
  }

  public BigInteger getExpectedNumberSpots() {
    return expectedNumberSpots;
  }

  public void setExpectedNumberSpots(BigInteger expectedNumberSpots) {
    this.expectedNumberSpots = expectedNumberSpots;
  }

  public BigInteger getExpectedNumberReads() {
    return expectedNumberReads;
  }

  public void setExpectedNumberReads(BigInteger expectedNumberReads) {
    this.expectedNumberReads = expectedNumberReads;
  }

  public Integer getSwAccession() {
    return swAccession;
  }

  public void setSwAccession(Integer swAccession) {
    this.swAccession = swAccession;
  }

  public Date getCreateTstmp() {
    return createTstmp;
  }

  public void setCreateTstmp(Date createTstmp) {
    this.createTstmp = createTstmp;
  }

  public Date getUpdateTstmp() {
    return updateTstmp;
  }

  public void setUpdateTstmp(Date updateTstmp) {
    this.updateTstmp = updateTstmp;
  }

  @XmlTransient
  public Collection<ExperimentAttribute> getExperimentAttributeCollection() {
    return experimentAttributeCollection;
  }

  public void setExperimentAttributeCollection(Collection<ExperimentAttribute> experimentAttributeCollection) {
    this.experimentAttributeCollection = experimentAttributeCollection;
  }

  public Study getStudyId() {
    return studyId;
  }

  public void setStudyId(Study studyId) {
    this.studyId = studyId;
  }

  public Registration getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Registration ownerId) {
    this.ownerId = ownerId;
  }

  public Platform getPlatformId() {
    return platformId;
  }

  public void setPlatformId(Platform platformId) {
    this.platformId = platformId;
  }

  public ExperimentSpotDesign getExperimentSpotDesignId() {
    return experimentSpotDesignId;
  }

  public void setExperimentSpotDesignId(ExperimentSpotDesign experimentSpotDesignId) {
    this.experimentSpotDesignId = experimentSpotDesignId;
  }

  public ExperimentLibraryDesign getExperimentLibraryDesignId() {
    return experimentLibraryDesignId;
  }

  public void setExperimentLibraryDesignId(ExperimentLibraryDesign experimentLibraryDesignId) {
    this.experimentLibraryDesignId = experimentLibraryDesignId;
  }

  @XmlTransient
  public Collection<ProcessingExperiments> getProcessingExperimentsCollection() {
    return processingExperimentsCollection;
  }

  public void setProcessingExperimentsCollection(Collection<ProcessingExperiments> processingExperimentsCollection) {
    this.processingExperimentsCollection = processingExperimentsCollection;
  }

  @XmlTransient
  public Collection<ShareExperiment> getShareExperimentCollection() {
    return shareExperimentCollection;
  }

  public void setShareExperimentCollection(Collection<ShareExperiment> shareExperimentCollection) {
    this.shareExperimentCollection = shareExperimentCollection;
  }

  @XmlTransient
  public Collection<Sample> getSampleCollection() {
    return sampleCollection;
  }

  public void setSampleCollection(Collection<Sample> sampleCollection) {
    this.sampleCollection = sampleCollection;
  }

  @XmlTransient
  public Collection<ExperimentLink> getExperimentLinkCollection() {
    return experimentLinkCollection;
  }

  public void setExperimentLinkCollection(Collection<ExperimentLink> experimentLinkCollection) {
    this.experimentLinkCollection = experimentLinkCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (experimentId != null ? experimentId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Experiment)) {
      return false;
    }
    Experiment other = (Experiment) object;
    if ((this.experimentId == null && other.experimentId != null) || (this.experimentId != null && !this.experimentId.equals(other.experimentId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.Experiment[ experimentId=" + experimentId + " ]";
  }
  
}
