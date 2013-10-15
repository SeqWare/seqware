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
@Table(name = "sequencer_run")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "SequencerRun.findAll", query = "SELECT s FROM SequencerRun s"),
  @NamedQuery(name = "SequencerRun.findBySequencerRunId", query = "SELECT s FROM SequencerRun s WHERE s.sequencerRunId = :sequencerRunId"),
  @NamedQuery(name = "SequencerRun.findByName", query = "SELECT s FROM SequencerRun s WHERE s.name = :name"),
  @NamedQuery(name = "SequencerRun.findByDescription", query = "SELECT s FROM SequencerRun s WHERE s.description = :description"),
  @NamedQuery(name = "SequencerRun.findByStatus", query = "SELECT s FROM SequencerRun s WHERE s.status = :status"),
  @NamedQuery(name = "SequencerRun.findByInstrumentName", query = "SELECT s FROM SequencerRun s WHERE s.instrumentName = :instrumentName"),
  @NamedQuery(name = "SequencerRun.findByCycleDescriptor", query = "SELECT s FROM SequencerRun s WHERE s.cycleDescriptor = :cycleDescriptor"),
  @NamedQuery(name = "SequencerRun.findByCycleCount", query = "SELECT s FROM SequencerRun s WHERE s.cycleCount = :cycleCount"),
  @NamedQuery(name = "SequencerRun.findByCycleSequence", query = "SELECT s FROM SequencerRun s WHERE s.cycleSequence = :cycleSequence"),
  @NamedQuery(name = "SequencerRun.findByFilePath", query = "SELECT s FROM SequencerRun s WHERE s.filePath = :filePath"),
  @NamedQuery(name = "SequencerRun.findByPairedEnd", query = "SELECT s FROM SequencerRun s WHERE s.pairedEnd = :pairedEnd"),
  @NamedQuery(name = "SequencerRun.findByProcess", query = "SELECT s FROM SequencerRun s WHERE s.process = :process"),
  @NamedQuery(name = "SequencerRun.findByRefLane", query = "SELECT s FROM SequencerRun s WHERE s.refLane = :refLane"),
  @NamedQuery(name = "SequencerRun.findByPairedFilePath", query = "SELECT s FROM SequencerRun s WHERE s.pairedFilePath = :pairedFilePath"),
  @NamedQuery(name = "SequencerRun.findByUseIparIntensities", query = "SELECT s FROM SequencerRun s WHERE s.useIparIntensities = :useIparIntensities"),
  @NamedQuery(name = "SequencerRun.findByColorMatrix", query = "SELECT s FROM SequencerRun s WHERE s.colorMatrix = :colorMatrix"),
  @NamedQuery(name = "SequencerRun.findByColorMatrixCode", query = "SELECT s FROM SequencerRun s WHERE s.colorMatrixCode = :colorMatrixCode"),
  @NamedQuery(name = "SequencerRun.findBySlideCount", query = "SELECT s FROM SequencerRun s WHERE s.slideCount = :slideCount"),
  @NamedQuery(name = "SequencerRun.findBySlide1LaneCount", query = "SELECT s FROM SequencerRun s WHERE s.slide1LaneCount = :slide1LaneCount"),
  @NamedQuery(name = "SequencerRun.findBySlide1FilePath", query = "SELECT s FROM SequencerRun s WHERE s.slide1FilePath = :slide1FilePath"),
  @NamedQuery(name = "SequencerRun.findBySlide2LaneCount", query = "SELECT s FROM SequencerRun s WHERE s.slide2LaneCount = :slide2LaneCount"),
  @NamedQuery(name = "SequencerRun.findBySlide2FilePath", query = "SELECT s FROM SequencerRun s WHERE s.slide2FilePath = :slide2FilePath"),
  @NamedQuery(name = "SequencerRun.findByFlowSequence", query = "SELECT s FROM SequencerRun s WHERE s.flowSequence = :flowSequence"),
  @NamedQuery(name = "SequencerRun.findByFlowCount", query = "SELECT s FROM SequencerRun s WHERE s.flowCount = :flowCount"),
  @NamedQuery(name = "SequencerRun.findByRunCenter", query = "SELECT s FROM SequencerRun s WHERE s.runCenter = :runCenter"),
  @NamedQuery(name = "SequencerRun.findByBaseCaller", query = "SELECT s FROM SequencerRun s WHERE s.baseCaller = :baseCaller"),
  @NamedQuery(name = "SequencerRun.findByQualityScorer", query = "SELECT s FROM SequencerRun s WHERE s.qualityScorer = :qualityScorer"),
  @NamedQuery(name = "SequencerRun.findBySwAccession", query = "SELECT s FROM SequencerRun s WHERE s.swAccession = :swAccession"),
  @NamedQuery(name = "SequencerRun.findByCreateTstmp", query = "SELECT s FROM SequencerRun s WHERE s.createTstmp = :createTstmp"),
  @NamedQuery(name = "SequencerRun.findByUpdateTstmp", query = "SELECT s FROM SequencerRun s WHERE s.updateTstmp = :updateTstmp"),
  @NamedQuery(name = "SequencerRun.findBySkip", query = "SELECT s FROM SequencerRun s WHERE s.skip = :skip")})
public class SequencerRun implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "sequencer_run_id")
  private Integer sequencerRunId;
  @Size(max = 2147483647)
  @Column(name = "name")
  private String name;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @Size(max = 2147483647)
  @Column(name = "status")
  private String status;
  @Size(max = 2147483647)
  @Column(name = "instrument_name")
  private String instrumentName;
  @Size(max = 2147483647)
  @Column(name = "cycle_descriptor")
  private String cycleDescriptor;
  @Column(name = "cycle_count")
  private Integer cycleCount;
  @Size(max = 2147483647)
  @Column(name = "cycle_sequence")
  private String cycleSequence;
  @Size(max = 2147483647)
  @Column(name = "file_path")
  private String filePath;
  @Column(name = "paired_end")
  private Boolean pairedEnd;
  @Column(name = "process")
  private Boolean process;
  @Column(name = "ref_lane")
  private Integer refLane;
  @Size(max = 2147483647)
  @Column(name = "paired_file_path")
  private String pairedFilePath;
  @Column(name = "use_ipar_intensities")
  private Boolean useIparIntensities;
  @Size(max = 2147483647)
  @Column(name = "color_matrix")
  private String colorMatrix;
  @Size(max = 2147483647)
  @Column(name = "color_matrix_code")
  private String colorMatrixCode;
  @Column(name = "slide_count")
  private Integer slideCount;
  @Column(name = "slide_1_lane_count")
  private Integer slide1LaneCount;
  @Size(max = 2147483647)
  @Column(name = "slide_1_file_path")
  private String slide1FilePath;
  @Column(name = "slide_2_lane_count")
  private Integer slide2LaneCount;
  @Size(max = 2147483647)
  @Column(name = "slide_2_file_path")
  private String slide2FilePath;
  @Size(max = 2147483647)
  @Column(name = "flow_sequence")
  private String flowSequence;
  @Column(name = "flow_count")
  private Integer flowCount;
  @Size(max = 2147483647)
  @Column(name = "run_center")
  private String runCenter;
  @Size(max = 2147483647)
  @Column(name = "base_caller")
  private String baseCaller;
  @Size(max = 2147483647)
  @Column(name = "quality_scorer")
  private String qualityScorer;
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
  @Column(name = "skip")
  private Boolean skip;
  @JoinColumn(name = "owner_id", referencedColumnName = "registration_id")
  @ManyToOne
  private Registration ownerId;
  @JoinColumn(name = "platform_id", referencedColumnName = "platform_id")
  @ManyToOne
  private Platform platformId;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "sequencerRunId")
  private Collection<ProcessingSequencerRuns> processingSequencerRunsCollection;
  @OneToMany(mappedBy = "sequencerRunId")
  private Collection<Lane> laneCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "sampleId")
  private Collection<SequencerRunAttribute> sequencerRunAttributeCollection;

  public SequencerRun() {
  }

  public SequencerRun(Integer sequencerRunId) {
    this.sequencerRunId = sequencerRunId;
  }

  public SequencerRun(Integer sequencerRunId, Date createTstmp) {
    this.sequencerRunId = sequencerRunId;
    this.createTstmp = createTstmp;
  }

  public Integer getSequencerRunId() {
    return sequencerRunId;
  }

  public void setSequencerRunId(Integer sequencerRunId) {
    this.sequencerRunId = sequencerRunId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getInstrumentName() {
    return instrumentName;
  }

  public void setInstrumentName(String instrumentName) {
    this.instrumentName = instrumentName;
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

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public Boolean getPairedEnd() {
    return pairedEnd;
  }

  public void setPairedEnd(Boolean pairedEnd) {
    this.pairedEnd = pairedEnd;
  }

  public Boolean getProcess() {
    return process;
  }

  public void setProcess(Boolean process) {
    this.process = process;
  }

  public Integer getRefLane() {
    return refLane;
  }

  public void setRefLane(Integer refLane) {
    this.refLane = refLane;
  }

  public String getPairedFilePath() {
    return pairedFilePath;
  }

  public void setPairedFilePath(String pairedFilePath) {
    this.pairedFilePath = pairedFilePath;
  }

  public Boolean getUseIparIntensities() {
    return useIparIntensities;
  }

  public void setUseIparIntensities(Boolean useIparIntensities) {
    this.useIparIntensities = useIparIntensities;
  }

  public String getColorMatrix() {
    return colorMatrix;
  }

  public void setColorMatrix(String colorMatrix) {
    this.colorMatrix = colorMatrix;
  }

  public String getColorMatrixCode() {
    return colorMatrixCode;
  }

  public void setColorMatrixCode(String colorMatrixCode) {
    this.colorMatrixCode = colorMatrixCode;
  }

  public Integer getSlideCount() {
    return slideCount;
  }

  public void setSlideCount(Integer slideCount) {
    this.slideCount = slideCount;
  }

  public Integer getSlide1LaneCount() {
    return slide1LaneCount;
  }

  public void setSlide1LaneCount(Integer slide1LaneCount) {
    this.slide1LaneCount = slide1LaneCount;
  }

  public String getSlide1FilePath() {
    return slide1FilePath;
  }

  public void setSlide1FilePath(String slide1FilePath) {
    this.slide1FilePath = slide1FilePath;
  }

  public Integer getSlide2LaneCount() {
    return slide2LaneCount;
  }

  public void setSlide2LaneCount(Integer slide2LaneCount) {
    this.slide2LaneCount = slide2LaneCount;
  }

  public String getSlide2FilePath() {
    return slide2FilePath;
  }

  public void setSlide2FilePath(String slide2FilePath) {
    this.slide2FilePath = slide2FilePath;
  }

  public String getFlowSequence() {
    return flowSequence;
  }

  public void setFlowSequence(String flowSequence) {
    this.flowSequence = flowSequence;
  }

  public Integer getFlowCount() {
    return flowCount;
  }

  public void setFlowCount(Integer flowCount) {
    this.flowCount = flowCount;
  }

  public String getRunCenter() {
    return runCenter;
  }

  public void setRunCenter(String runCenter) {
    this.runCenter = runCenter;
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

  public Boolean getSkip() {
    return skip;
  }

  public void setSkip(Boolean skip) {
    this.skip = skip;
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

  @XmlTransient
  public Collection<ProcessingSequencerRuns> getProcessingSequencerRunsCollection() {
    return processingSequencerRunsCollection;
  }

  public void setProcessingSequencerRunsCollection(Collection<ProcessingSequencerRuns> processingSequencerRunsCollection) {
    this.processingSequencerRunsCollection = processingSequencerRunsCollection;
  }

  @XmlTransient
  public Collection<Lane> getLaneCollection() {
    return laneCollection;
  }

  public void setLaneCollection(Collection<Lane> laneCollection) {
    this.laneCollection = laneCollection;
  }

  @XmlTransient
  public Collection<SequencerRunAttribute> getSequencerRunAttributeCollection() {
    return sequencerRunAttributeCollection;
  }

  public void setSequencerRunAttributeCollection(Collection<SequencerRunAttribute> sequencerRunAttributeCollection) {
    this.sequencerRunAttributeCollection = sequencerRunAttributeCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (sequencerRunId != null ? sequencerRunId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof SequencerRun)) {
      return false;
    }
    SequencerRun other = (SequencerRun) object;
    if ((this.sequencerRunId == null && other.sequencerRunId != null) || (this.sequencerRunId != null && !this.sequencerRunId.equals(other.sequencerRunId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.SequencerRun[ sequencerRunId=" + sequencerRunId + " ]";
  }
  
}
