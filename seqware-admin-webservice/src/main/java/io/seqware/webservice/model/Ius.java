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
@Table(name = "ius")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Ius.findAll", query = "SELECT i FROM Ius i"),
  @NamedQuery(name = "Ius.findByIusId", query = "SELECT i FROM Ius i WHERE i.iusId = :iusId"),
  @NamedQuery(name = "Ius.findByName", query = "SELECT i FROM Ius i WHERE i.name = :name"),
  @NamedQuery(name = "Ius.findByAlias", query = "SELECT i FROM Ius i WHERE i.alias = :alias"),
  @NamedQuery(name = "Ius.findByDescription", query = "SELECT i FROM Ius i WHERE i.description = :description"),
  @NamedQuery(name = "Ius.findByTag", query = "SELECT i FROM Ius i WHERE i.tag = :tag"),
  @NamedQuery(name = "Ius.findBySwAccession", query = "SELECT i FROM Ius i WHERE i.swAccession = :swAccession"),
  @NamedQuery(name = "Ius.findByCreateTstmp", query = "SELECT i FROM Ius i WHERE i.createTstmp = :createTstmp"),
  @NamedQuery(name = "Ius.findByUpdateTstmp", query = "SELECT i FROM Ius i WHERE i.updateTstmp = :updateTstmp"),
  @NamedQuery(name = "Ius.findBySkip", query = "SELECT i FROM Ius i WHERE i.skip = :skip")})
public class Ius implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "ius_id")
  private Integer iusId;
  @Size(max = 2147483647)
  @Column(name = "name")
  private String name;
  @Size(max = 2147483647)
  @Column(name = "alias")
  private String alias;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @Size(max = 2147483647)
  @Column(name = "tag")
  private String tag;
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
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "iusId")
  private Collection<IusWorkflowRuns> iusWorkflowRunsCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "iusId")
  private Collection<IusAttribute> iusAttributeCollection;
  @JoinColumn(name = "sample_id", referencedColumnName = "sample_id")
  @ManyToOne(optional = false)
  private Sample sampleId;
  @JoinColumn(name = "owner_id", referencedColumnName = "registration_id")
  @ManyToOne
  private Registration ownerId;
  @JoinColumn(name = "lane_id", referencedColumnName = "lane_id")
  @ManyToOne(optional = false)
  private Lane laneId;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "iusId")
  private Collection<ProcessingIus> processingIusCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "iusId")
  private Collection<IusLink> iusLinkCollection;

  public Ius() {
  }

  public Ius(Integer iusId) {
    this.iusId = iusId;
  }

  public Ius(Integer iusId, Date createTstmp) {
    this.iusId = iusId;
    this.createTstmp = createTstmp;
  }

  public Integer getIusId() {
    return iusId;
  }

  public void setIusId(Integer iusId) {
    this.iusId = iusId;
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

  @XmlTransient
  public Collection<IusWorkflowRuns> getIusWorkflowRunsCollection() {
    return iusWorkflowRunsCollection;
  }

  public void setIusWorkflowRunsCollection(Collection<IusWorkflowRuns> iusWorkflowRunsCollection) {
    this.iusWorkflowRunsCollection = iusWorkflowRunsCollection;
  }

  @XmlTransient
  public Collection<IusAttribute> getIusAttributeCollection() {
    return iusAttributeCollection;
  }

  public void setIusAttributeCollection(Collection<IusAttribute> iusAttributeCollection) {
    this.iusAttributeCollection = iusAttributeCollection;
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

  public Lane getLaneId() {
    return laneId;
  }

  public void setLaneId(Lane laneId) {
    this.laneId = laneId;
  }

  @XmlTransient
  public Collection<ProcessingIus> getProcessingIusCollection() {
    return processingIusCollection;
  }

  public void setProcessingIusCollection(Collection<ProcessingIus> processingIusCollection) {
    this.processingIusCollection = processingIusCollection;
  }

  @XmlTransient
  public Collection<IusLink> getIusLinkCollection() {
    return iusLinkCollection;
  }

  public void setIusLinkCollection(Collection<IusLink> iusLinkCollection) {
    this.iusLinkCollection = iusLinkCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (iusId != null ? iusId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Ius)) {
      return false;
    }
    Ius other = (Ius) object;
    if ((this.iusId == null && other.iusId != null) || (this.iusId != null && !this.iusId.equals(other.iusId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.Ius[ iusId=" + iusId + " ]";
  }
  
}
