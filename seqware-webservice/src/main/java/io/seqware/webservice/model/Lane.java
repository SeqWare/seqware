/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.model;

import java.io.Serializable;
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
@Table(name = "lane")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Lane.findAll", query = "SELECT l FROM Lane l"),
  @NamedQuery(name = "Lane.findByLaneId", query = "SELECT l FROM Lane l WHERE l.laneId = :laneId"),
  @NamedQuery(name = "Lane.findByName", query = "SELECT l FROM Lane l WHERE l.name = :name"),
  @NamedQuery(name = "Lane.findByAlias", query = "SELECT l FROM Lane l WHERE l.alias = :alias"),
  @NamedQuery(name = "Lane.findByDescription", query = "SELECT l FROM Lane l WHERE l.description = :description"),
  @NamedQuery(name = "Lane.findByLaneIndex", query = "SELECT l FROM Lane l WHERE l.laneIndex = :laneIndex"),
  @NamedQuery(name = "Lane.findByCycleDescriptor", query = "SELECT l FROM Lane l WHERE l.cycleDescriptor = :cycleDescriptor"),
  @NamedQuery(name = "Lane.findByCycleCount", query = "SELECT l FROM Lane l WHERE l.cycleCount = :cycleCount"),
  @NamedQuery(name = "Lane.findByCycleSequence", query = "SELECT l FROM Lane l WHERE l.cycleSequence = :cycleSequence"),
  @NamedQuery(name = "Lane.findBySkip", query = "SELECT l FROM Lane l WHERE l.skip = :skip"),
  @NamedQuery(name = "Lane.findByTags", query = "SELECT l FROM Lane l WHERE l.tags = :tags"),
  @NamedQuery(name = "Lane.findByRegions", query = "SELECT l FROM Lane l WHERE l.regions = :regions"),
  @NamedQuery(name = "Lane.findBySwAccession", query = "SELECT l FROM Lane l WHERE l.swAccession = :swAccession"),
  @NamedQuery(name = "Lane.findByCreateTstmp", query = "SELECT l FROM Lane l WHERE l.createTstmp = :createTstmp"),
  @NamedQuery(name = "Lane.findByUpdateTstmp", query = "SELECT l FROM Lane l WHERE l.updateTstmp = :updateTstmp")})
public class Lane implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "lane_id")
  private Integer laneId;
  @Size(max = 2147483647)
  @Column(name = "name")
  private String name;
  @Size(max = 2147483647)
  @Column(name = "alias")
  private String alias;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @Column(name = "lane_index")
  private Integer laneIndex;
  @Size(max = 2147483647)
  @Column(name = "cycle_descriptor")
  private String cycleDescriptor;
  @Column(name = "cycle_count")
  private Integer cycleCount;
  @Size(max = 2147483647)
  @Column(name = "cycle_sequence")
  private String cycleSequence;
  @Column(name = "skip")
  private Boolean skip;
  @Size(max = 2147483647)
  @Column(name = "tags")
  private String tags;
  @Size(max = 2147483647)
  @Column(name = "regions")
  private String regions;
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
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "laneId")
  private Collection<ShareLane> shareLaneCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "laneId")
  private Collection<LaneLink> laneLinkCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "laneId")
  private Collection<LaneAttribute> laneAttributeCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "laneId")
  private Collection<ProcessingLanes> processingLanesCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "laneId")
  private Collection<LaneWorkflowRuns> laneWorkflowRunsCollection;
  @JoinColumn(name = "study_type", referencedColumnName = "study_type_id")
  @ManyToOne
  private StudyType studyType;
  @JoinColumn(name = "sequencer_run_id", referencedColumnName = "sequencer_run_id")
  @ManyToOne
  private SequencerRun sequencerRunId;
  @JoinColumn(name = "sample_id", referencedColumnName = "sample_id")
  @ManyToOne
  private Sample sampleId;
  @JoinColumn(name = "owner_id", referencedColumnName = "registration_id")
  @ManyToOne
  private Registration ownerId;
  @JoinColumn(name = "organism_id", referencedColumnName = "organism_id")
  @ManyToOne
  private Organism organismId;
  @JoinColumn(name = "library_strategy", referencedColumnName = "library_strategy_id")
  @ManyToOne
  private LibraryStrategy libraryStrategy;
  @JoinColumn(name = "library_source", referencedColumnName = "library_source_id")
  @ManyToOne
  private LibrarySource librarySource;
  @JoinColumn(name = "library_selection", referencedColumnName = "library_selection_id")
  @ManyToOne
  private LibrarySelection librarySelection;
  @JoinColumn(name = "type", referencedColumnName = "lane_type_id")
  @ManyToOne
  private LaneType type;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "laneId")
  private Collection<Ius> iusCollection;

  public Lane() {
  }

  public Lane(Integer laneId) {
    this.laneId = laneId;
  }

  public Lane(Integer laneId, Date createTstmp) {
    this.laneId = laneId;
    this.createTstmp = createTstmp;
  }

  public Integer getLaneId() {
    return laneId;
  }

  public void setLaneId(Integer laneId) {
    this.laneId = laneId;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getLaneIndex() {
    return laneIndex;
  }

  public void setLaneIndex(Integer laneIndex) {
    this.laneIndex = laneIndex;
  }

  public String getCycleDescriptor() {
    return cycleDescriptor;
  }

  public void setCycleDescriptor(String cycleDescriptor) {
    this.cycleDescriptor = cycleDescriptor;
  }

  public Integer getCycleCount() {
    return cycleCount;
  }

  public void setCycleCount(Integer cycleCount) {
    this.cycleCount = cycleCount;
  }

  public String getCycleSequence() {
    return cycleSequence;
  }

  public void setCycleSequence(String cycleSequence) {
    this.cycleSequence = cycleSequence;
  }

  public Boolean getSkip() {
    return skip;
  }

  public void setSkip(Boolean skip) {
    this.skip = skip;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

  public String getRegions() {
    return regions;
  }

  public void setRegions(String regions) {
    this.regions = regions;
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
  public Collection<ShareLane> getShareLaneCollection() {
    return shareLaneCollection;
  }

  public void setShareLaneCollection(Collection<ShareLane> shareLaneCollection) {
    this.shareLaneCollection = shareLaneCollection;
  }

  @XmlTransient
  public Collection<LaneLink> getLaneLinkCollection() {
    return laneLinkCollection;
  }

  public void setLaneLinkCollection(Collection<LaneLink> laneLinkCollection) {
    this.laneLinkCollection = laneLinkCollection;
  }

  @XmlTransient
  public Collection<LaneAttribute> getLaneAttributeCollection() {
    return laneAttributeCollection;
  }

  public void setLaneAttributeCollection(Collection<LaneAttribute> laneAttributeCollection) {
    this.laneAttributeCollection = laneAttributeCollection;
  }

  @XmlTransient
  public Collection<ProcessingLanes> getProcessingLanesCollection() {
    return processingLanesCollection;
  }

  public void setProcessingLanesCollection(Collection<ProcessingLanes> processingLanesCollection) {
    this.processingLanesCollection = processingLanesCollection;
  }

  @XmlTransient
  public Collection<LaneWorkflowRuns> getLaneWorkflowRunsCollection() {
    return laneWorkflowRunsCollection;
  }

  public void setLaneWorkflowRunsCollection(Collection<LaneWorkflowRuns> laneWorkflowRunsCollection) {
    this.laneWorkflowRunsCollection = laneWorkflowRunsCollection;
  }

  public StudyType getStudyType() {
    return studyType;
  }

  public void setStudyType(StudyType studyType) {
    this.studyType = studyType;
  }

  public SequencerRun getSequencerRunId() {
    return sequencerRunId;
  }

  public void setSequencerRunId(SequencerRun sequencerRunId) {
    this.sequencerRunId = sequencerRunId;
  }

  public Sample getSampleId() {
    return sampleId;
  }

  public void setSampleId(Sample sampleId) {
    this.sampleId = sampleId;
  }

  public Registration getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Registration ownerId) {
    this.ownerId = ownerId;
  }

  public Organism getOrganismId() {
    return organismId;
  }

  public void setOrganismId(Organism organismId) {
    this.organismId = organismId;
  }

  public LibraryStrategy getLibraryStrategy() {
    return libraryStrategy;
  }

  public void setLibraryStrategy(LibraryStrategy libraryStrategy) {
    this.libraryStrategy = libraryStrategy;
  }

  public LibrarySource getLibrarySource() {
    return librarySource;
  }

  public void setLibrarySource(LibrarySource librarySource) {
    this.librarySource = librarySource;
  }

  public LibrarySelection getLibrarySelection() {
    return librarySelection;
  }

  public void setLibrarySelection(LibrarySelection librarySelection) {
    this.librarySelection = librarySelection;
  }

  public LaneType getType() {
    return type;
  }

  public void setType(LaneType type) {
    this.type = type;
  }

  @XmlTransient
  public Collection<Ius> getIusCollection() {
    return iusCollection;
  }

  public void setIusCollection(Collection<Ius> iusCollection) {
    this.iusCollection = iusCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (laneId != null ? laneId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Lane)) {
      return false;
    }
    Lane other = (Lane) object;
    if ((this.laneId == null && other.laneId != null) || (this.laneId != null && !this.laneId.equals(other.laneId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.Lane[ laneId=" + laneId + " ]";
  }
  
}
