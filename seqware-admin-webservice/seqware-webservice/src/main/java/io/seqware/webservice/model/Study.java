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
import javax.persistence.FetchType;
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
@Table(name = "study")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Study.findAll", query = "SELECT s FROM Study s"),
  @NamedQuery(name = "Study.findByStudyId", query = "SELECT s FROM Study s WHERE s.studyId = :studyId"),
  @NamedQuery(name = "Study.findByTitle", query = "SELECT s FROM Study s WHERE s.title = :title"),
  @NamedQuery(name = "Study.findByAlias", query = "SELECT s FROM Study s WHERE s.alias = :alias"),
  @NamedQuery(name = "Study.findByDescription", query = "SELECT s FROM Study s WHERE s.description = :description"),
  @NamedQuery(name = "Study.findByAccession", query = "SELECT s FROM Study s WHERE s.accession = :accession"),
  @NamedQuery(name = "Study.findByAbstract1", query = "SELECT s FROM Study s WHERE s.abstract1 = :abstract1"),
  @NamedQuery(name = "Study.findByNewType", query = "SELECT s FROM Study s WHERE s.newType = :newType"),
  @NamedQuery(name = "Study.findByCenterName", query = "SELECT s FROM Study s WHERE s.centerName = :centerName"),
  @NamedQuery(name = "Study.findByCenterProjectName", query = "SELECT s FROM Study s WHERE s.centerProjectName = :centerProjectName"),
  @NamedQuery(name = "Study.findByProjectId", query = "SELECT s FROM Study s WHERE s.projectId = :projectId"),
  @NamedQuery(name = "Study.findByStatus", query = "SELECT s FROM Study s WHERE s.status = :status"),
  @NamedQuery(name = "Study.findBySwAccession", query = "SELECT s FROM Study s WHERE s.swAccession = :swAccession"),
  @NamedQuery(name = "Study.findByCreateTstmp", query = "SELECT s FROM Study s WHERE s.createTstmp = :createTstmp"),
  @NamedQuery(name = "Study.findByUpdateTstmp", query = "SELECT s FROM Study s WHERE s.updateTstmp = :updateTstmp")})
public class Study implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "study_id")
  private Integer studyId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "title")
  private String title;
  @Size(max = 2147483647)
  @Column(name = "alias")
  private String alias;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @Size(max = 2147483647)
  @Column(name = "accession")
  private String accession;
  @Size(max = 2147483647)
  @Column(name = "abstract")
  private String abstract1;
  @Size(max = 2147483647)
  @Column(name = "new_type")
  private String newType;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "center_name")
  private String centerName;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "center_project_name")
  private String centerProjectName;
  @Basic(optional = false)
  @NotNull
  @Column(name = "project_id")
  private int projectId;
  @Size(max = 2147483647)
  @Column(name = "status")
  private String status;
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
  @JoinColumn(name = "existing_type", referencedColumnName = "study_type_id")
  @ManyToOne(optional = false)
  private StudyType existingType;
  @JoinColumn(name = "owner_id", referencedColumnName = "registration_id")
  @ManyToOne(fetch = FetchType.LAZY)
  private Registration ownerId;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyId")
  private Collection<Experiment> experimentCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyId")
  private Collection<StudyAttribute> studyAttributeCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyId")
  private Collection<ShareStudy> shareStudyCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyId")
  private Collection<StudyLink> studyLinkCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "studyId")
  private Collection<ProcessingStudies> processingStudiesCollection;

  public Study() {
  }

  public Study(Integer studyId) {
    this.studyId = studyId;
  }

  public Study(Integer studyId, String title, String centerName, String centerProjectName, int projectId, Date createTstmp) {
    this.studyId = studyId;
    this.title = title;
    this.centerName = centerName;
    this.centerProjectName = centerProjectName;
    this.projectId = projectId;
    this.createTstmp = createTstmp;
  }

  public Integer getStudyId() {
    return studyId;
  }

  public void setStudyId(Integer studyId) {
    this.studyId = studyId;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAccession() {
    return accession;
  }

  public void setAccession(String accession) {
    this.accession = accession;
  }

  public String getAbstract1() {
    return abstract1;
  }

  public void setAbstract1(String abstract1) {
    this.abstract1 = abstract1;
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

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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

  public StudyType getExistingType() {
    return existingType;
  }

  public void setExistingType(StudyType existingType) {
    this.existingType = existingType;
  }

  public Registration getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Registration ownerId) {
    this.ownerId = ownerId;
  }

  @XmlTransient
  public Collection<Experiment> getExperimentCollection() {
    return experimentCollection;
  }

  public void setExperimentCollection(Collection<Experiment> experimentCollection) {
    this.experimentCollection = experimentCollection;
  }

  @XmlTransient
  public Collection<StudyAttribute> getStudyAttributeCollection() {
    return studyAttributeCollection;
  }

  public void setStudyAttributeCollection(Collection<StudyAttribute> studyAttributeCollection) {
    this.studyAttributeCollection = studyAttributeCollection;
  }

  @XmlTransient
  public Collection<ShareStudy> getShareStudyCollection() {
    return shareStudyCollection;
  }

  public void setShareStudyCollection(Collection<ShareStudy> shareStudyCollection) {
    this.shareStudyCollection = shareStudyCollection;
  }

  @XmlTransient
  public Collection<StudyLink> getStudyLinkCollection() {
    return studyLinkCollection;
  }

  public void setStudyLinkCollection(Collection<StudyLink> studyLinkCollection) {
    this.studyLinkCollection = studyLinkCollection;
  }

  @XmlTransient
  public Collection<ProcessingStudies> getProcessingStudiesCollection() {
    return processingStudiesCollection;
  }

  public void setProcessingStudiesCollection(Collection<ProcessingStudies> processingStudiesCollection) {
    this.processingStudiesCollection = processingStudiesCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (studyId != null ? studyId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Study)) {
      return false;
    }
    Study other = (Study) object;
    if ((this.studyId == null && other.studyId != null) || (this.studyId != null && !this.studyId.equals(other.studyId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.Study[ studyId=" + studyId + " ]";
  }
  
}
