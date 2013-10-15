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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
@Table(name = "sample")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Sample.findAll", query = "SELECT s FROM Sample s"),
  @NamedQuery(name = "Sample.findBySampleId", query = "SELECT s FROM Sample s WHERE s.sampleId = :sampleId"),
  @NamedQuery(name = "Sample.findByName", query = "SELECT s FROM Sample s WHERE s.name = :name"),
  @NamedQuery(name = "Sample.findByTitle", query = "SELECT s FROM Sample s WHERE s.title = :title"),
  @NamedQuery(name = "Sample.findByAlias", query = "SELECT s FROM Sample s WHERE s.alias = :alias"),
  @NamedQuery(name = "Sample.findByType", query = "SELECT s FROM Sample s WHERE s.type = :type"),
  @NamedQuery(name = "Sample.findByScientificName", query = "SELECT s FROM Sample s WHERE s.scientificName = :scientificName"),
  @NamedQuery(name = "Sample.findByCommonName", query = "SELECT s FROM Sample s WHERE s.commonName = :commonName"),
  @NamedQuery(name = "Sample.findByAnonymizedName", query = "SELECT s FROM Sample s WHERE s.anonymizedName = :anonymizedName"),
  @NamedQuery(name = "Sample.findByIndividualName", query = "SELECT s FROM Sample s WHERE s.individualName = :individualName"),
  @NamedQuery(name = "Sample.findByDescription", query = "SELECT s FROM Sample s WHERE s.description = :description"),
  @NamedQuery(name = "Sample.findByTaxonId", query = "SELECT s FROM Sample s WHERE s.taxonId = :taxonId"),
  @NamedQuery(name = "Sample.findByTags", query = "SELECT s FROM Sample s WHERE s.tags = :tags"),
  @NamedQuery(name = "Sample.findByAdapters", query = "SELECT s FROM Sample s WHERE s.adapters = :adapters"),
  @NamedQuery(name = "Sample.findByRegions", query = "SELECT s FROM Sample s WHERE s.regions = :regions"),
  @NamedQuery(name = "Sample.findByExpectedNumberRuns", query = "SELECT s FROM Sample s WHERE s.expectedNumberRuns = :expectedNumberRuns"),
  @NamedQuery(name = "Sample.findByExpectedNumberSpots", query = "SELECT s FROM Sample s WHERE s.expectedNumberSpots = :expectedNumberSpots"),
  @NamedQuery(name = "Sample.findByExpectedNumberReads", query = "SELECT s FROM Sample s WHERE s.expectedNumberReads = :expectedNumberReads"),
  @NamedQuery(name = "Sample.findBySkip", query = "SELECT s FROM Sample s WHERE s.skip = :skip"),
  @NamedQuery(name = "Sample.findByIsPublic", query = "SELECT s FROM Sample s WHERE s.isPublic = :isPublic"),
  @NamedQuery(name = "Sample.findBySwAccession", query = "SELECT s FROM Sample s WHERE s.swAccession = :swAccession"),
  @NamedQuery(name = "Sample.findByCreateTstmp", query = "SELECT s FROM Sample s WHERE s.createTstmp = :createTstmp"),
  @NamedQuery(name = "Sample.findByUpdateTstmp", query = "SELECT s FROM Sample s WHERE s.updateTstmp = :updateTstmp")})
public class Sample implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "sample_id")
  private Integer sampleId;
  @Size(max = 2147483647)
  @Column(name = "name")
  private String name;
  @Size(max = 2147483647)
  @Column(name = "title")
  private String title;
  @Size(max = 2147483647)
  @Column(name = "alias")
  private String alias;
  @Size(max = 2147483647)
  @Column(name = "type")
  private String type;
  @Size(max = 2147483647)
  @Column(name = "scientific_name")
  private String scientificName;
  @Size(max = 2147483647)
  @Column(name = "common_name")
  private String commonName;
  @Size(max = 2147483647)
  @Column(name = "anonymized_name")
  private String anonymizedName;
  @Size(max = 2147483647)
  @Column(name = "individual_name")
  private String individualName;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @Column(name = "taxon_id")
  private Integer taxonId;
  @Size(max = 2147483647)
  @Column(name = "tags")
  private String tags;
  @Size(max = 2147483647)
  @Column(name = "adapters")
  private String adapters;
  @Size(max = 2147483647)
  @Column(name = "regions")
  private String regions;
  @Column(name = "expected_number_runs")
  private Integer expectedNumberRuns;
  @Column(name = "expected_number_spots")
  private Integer expectedNumberSpots;
  @Column(name = "expected_number_reads")
  private Integer expectedNumberReads;
  @Column(name = "skip")
  private Boolean skip;
  @Column(name = "is_public")
  private Boolean isPublic;
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
  @JoinTable(name = "sample_hierarchy", joinColumns = {
    @JoinColumn(name = "parent_id", referencedColumnName = "sample_id")}, inverseJoinColumns = {
    @JoinColumn(name = "sample_id", referencedColumnName = "sample_id")})
  @ManyToMany
  private Collection<Sample> sampleCollection;
  @ManyToMany(mappedBy = "sampleCollection")
  private Collection<Sample> sampleCollection1;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "sampleId")
  private Collection<SampleLink> sampleLinkCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "sampleId")
  private Collection<SampleSearch> sampleSearchCollection;
  @OneToMany(mappedBy = "sampleId")
  private Collection<Lane> laneCollection;
  @JoinColumn(name = "owner_id", referencedColumnName = "registration_id")
  @ManyToOne
  private Registration ownerId;
  @JoinColumn(name = "organism_id", referencedColumnName = "organism_id")
  @ManyToOne
  private Organism organismId;
  @JoinColumn(name = "experiment_id", referencedColumnName = "experiment_id")
  @ManyToOne
  private Experiment experimentId;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "sampleId")
  private Collection<Ius> iusCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "sampleId")
  private Collection<ProcessingSamples> processingSamplesCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "sampleId")
  private Collection<SampleAttribute> sampleAttributeCollection;
  @OneToMany(mappedBy = "parentId")
  private Collection<SampleRelationship> sampleRelationshipCollection;
  @OneToMany(mappedBy = "childId")
  private Collection<SampleRelationship> sampleRelationshipCollection1;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "sampleId")
  private Collection<ShareSample> shareSampleCollection;

  public Sample() {
  }

  public Sample(Integer sampleId) {
    this.sampleId = sampleId;
  }

  public Sample(Integer sampleId, Date createTstmp) {
    this.sampleId = sampleId;
    this.createTstmp = createTstmp;
  }

  public Integer getSampleId() {
    return sampleId;
  }

  public void setSampleId(Integer sampleId) {
    this.sampleId = sampleId;
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

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getScientificName() {
    return scientificName;
  }

  public void setScientificName(String scientificName) {
    this.scientificName = scientificName;
  }

  public String getCommonName() {
    return commonName;
  }

  public void setCommonName(String commonName) {
    this.commonName = commonName;
  }

  public String getAnonymizedName() {
    return anonymizedName;
  }

  public void setAnonymizedName(String anonymizedName) {
    this.anonymizedName = anonymizedName;
  }

  public String getIndividualName() {
    return individualName;
  }

  public void setIndividualName(String individualName) {
    this.individualName = individualName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getTaxonId() {
    return taxonId;
  }

  public void setTaxonId(Integer taxonId) {
    this.taxonId = taxonId;
  }

  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

  public String getAdapters() {
    return adapters;
  }

  public void setAdapters(String adapters) {
    this.adapters = adapters;
  }

  public String getRegions() {
    return regions;
  }

  public void setRegions(String regions) {
    this.regions = regions;
  }

  public Integer getExpectedNumberRuns() {
    return expectedNumberRuns;
  }

  public void setExpectedNumberRuns(Integer expectedNumberRuns) {
    this.expectedNumberRuns = expectedNumberRuns;
  }

  public Integer getExpectedNumberSpots() {
    return expectedNumberSpots;
  }

  public void setExpectedNumberSpots(Integer expectedNumberSpots) {
    this.expectedNumberSpots = expectedNumberSpots;
  }

  public Integer getExpectedNumberReads() {
    return expectedNumberReads;
  }

  public void setExpectedNumberReads(Integer expectedNumberReads) {
    this.expectedNumberReads = expectedNumberReads;
  }

  public Boolean getSkip() {
    return skip;
  }

  public void setSkip(Boolean skip) {
    this.skip = skip;
  }

  public Boolean getIsPublic() {
    return isPublic;
  }

  public void setIsPublic(Boolean isPublic) {
    this.isPublic = isPublic;
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
  public Collection<Sample> getSampleCollection() {
    return sampleCollection;
  }

  public void setSampleCollection(Collection<Sample> sampleCollection) {
    this.sampleCollection = sampleCollection;
  }

  @XmlTransient
  public Collection<Sample> getSampleCollection1() {
    return sampleCollection1;
  }

  public void setSampleCollection1(Collection<Sample> sampleCollection1) {
    this.sampleCollection1 = sampleCollection1;
  }

  @XmlTransient
  public Collection<SampleLink> getSampleLinkCollection() {
    return sampleLinkCollection;
  }

  public void setSampleLinkCollection(Collection<SampleLink> sampleLinkCollection) {
    this.sampleLinkCollection = sampleLinkCollection;
  }

  @XmlTransient
  public Collection<SampleSearch> getSampleSearchCollection() {
    return sampleSearchCollection;
  }

  public void setSampleSearchCollection(Collection<SampleSearch> sampleSearchCollection) {
    this.sampleSearchCollection = sampleSearchCollection;
  }

  @XmlTransient
  public Collection<Lane> getLaneCollection() {
    return laneCollection;
  }

  public void setLaneCollection(Collection<Lane> laneCollection) {
    this.laneCollection = laneCollection;
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

  public Experiment getExperimentId() {
    return experimentId;
  }

  public void setExperimentId(Experiment experimentId) {
    this.experimentId = experimentId;
  }

  @XmlTransient
  public Collection<Ius> getIusCollection() {
    return iusCollection;
  }

  public void setIusCollection(Collection<Ius> iusCollection) {
    this.iusCollection = iusCollection;
  }

  @XmlTransient
  public Collection<ProcessingSamples> getProcessingSamplesCollection() {
    return processingSamplesCollection;
  }

  public void setProcessingSamplesCollection(Collection<ProcessingSamples> processingSamplesCollection) {
    this.processingSamplesCollection = processingSamplesCollection;
  }

  @XmlTransient
  public Collection<SampleAttribute> getSampleAttributeCollection() {
    return sampleAttributeCollection;
  }

  public void setSampleAttributeCollection(Collection<SampleAttribute> sampleAttributeCollection) {
    this.sampleAttributeCollection = sampleAttributeCollection;
  }

  @XmlTransient
  public Collection<SampleRelationship> getSampleRelationshipCollection() {
    return sampleRelationshipCollection;
  }

  public void setSampleRelationshipCollection(Collection<SampleRelationship> sampleRelationshipCollection) {
    this.sampleRelationshipCollection = sampleRelationshipCollection;
  }

  @XmlTransient
  public Collection<SampleRelationship> getSampleRelationshipCollection1() {
    return sampleRelationshipCollection1;
  }

  public void setSampleRelationshipCollection1(Collection<SampleRelationship> sampleRelationshipCollection1) {
    this.sampleRelationshipCollection1 = sampleRelationshipCollection1;
  }

  @XmlTransient
  public Collection<ShareSample> getShareSampleCollection() {
    return shareSampleCollection;
  }

  public void setShareSampleCollection(Collection<ShareSample> shareSampleCollection) {
    this.shareSampleCollection = shareSampleCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (sampleId != null ? sampleId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Sample)) {
      return false;
    }
    Sample other = (Sample) object;
    if ((this.sampleId == null && other.sampleId != null) || (this.sampleId != null && !this.sampleId.equals(other.sampleId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.Sample[ sampleId=" + sampleId + " ]";
  }
  
}
