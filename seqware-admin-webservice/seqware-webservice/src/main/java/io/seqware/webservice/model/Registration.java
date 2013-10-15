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
@Table(name = "registration")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Registration.findAll", query = "SELECT r FROM Registration r"),
  @NamedQuery(name = "Registration.findByRegistrationId", query = "SELECT r FROM Registration r WHERE r.registrationId = :registrationId"),
  @NamedQuery(name = "Registration.findByEmail", query = "SELECT r FROM Registration r WHERE r.email = :email"),
  @NamedQuery(name = "Registration.findByPassword", query = "SELECT r FROM Registration r WHERE r.password = :password"),
  @NamedQuery(name = "Registration.findByPasswordHint", query = "SELECT r FROM Registration r WHERE r.passwordHint = :passwordHint"),
  @NamedQuery(name = "Registration.findByFirstName", query = "SELECT r FROM Registration r WHERE r.firstName = :firstName"),
  @NamedQuery(name = "Registration.findByLastName", query = "SELECT r FROM Registration r WHERE r.lastName = :lastName"),
  @NamedQuery(name = "Registration.findByInstitution", query = "SELECT r FROM Registration r WHERE r.institution = :institution"),
  @NamedQuery(name = "Registration.findByInvitationCode", query = "SELECT r FROM Registration r WHERE r.invitationCode = :invitationCode"),
  @NamedQuery(name = "Registration.findByLimsAdmin", query = "SELECT r FROM Registration r WHERE r.limsAdmin = :limsAdmin"),
  @NamedQuery(name = "Registration.findByCreateTstmp", query = "SELECT r FROM Registration r WHERE r.createTstmp = :createTstmp"),
  @NamedQuery(name = "Registration.findByLastUpdateTstmp", query = "SELECT r FROM Registration r WHERE r.lastUpdateTstmp = :lastUpdateTstmp"),
  @NamedQuery(name = "Registration.findByDeveloperMl", query = "SELECT r FROM Registration r WHERE r.developerMl = :developerMl"),
  @NamedQuery(name = "Registration.findByUserMl", query = "SELECT r FROM Registration r WHERE r.userMl = :userMl"),
  @NamedQuery(name = "Registration.findByPayee", query = "SELECT r FROM Registration r WHERE r.payee = :payee")})
public class Registration implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "registration_id")
  private Integer registrationId;
  // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "email")
  private String email;
  @Size(max = 2147483647)
  @Column(name = "password")
  private String password;
  @Size(max = 2147483647)
  @Column(name = "password_hint")
  private String passwordHint;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "first_name")
  private String firstName;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "last_name")
  private String lastName;
  @Size(max = 2147483647)
  @Column(name = "institution")
  private String institution;
  @Size(max = 2147483647)
  @Column(name = "invitation_code")
  private String invitationCode;
  @Basic(optional = false)
  @NotNull
  @Column(name = "lims_admin")
  private boolean limsAdmin;
  @Basic(optional = false)
  @NotNull
  @Column(name = "create_tstmp")
  @Temporal(TemporalType.TIMESTAMP)
  private Date createTstmp;
  @Basic(optional = false)
  @NotNull
  @Column(name = "last_update_tstmp")
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastUpdateTstmp;
  @Basic(optional = false)
  @NotNull
  @Column(name = "developer_ml")
  private boolean developerMl;
  @Basic(optional = false)
  @NotNull
  @Column(name = "user_ml")
  private boolean userMl;
  @Basic(optional = false)
  @NotNull
  @Column(name = "payee")
  private boolean payee;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "registrationId")
  private Collection<ShareLane> shareLaneCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "ownerId")
  private Collection<Invoice> invoiceCollection;
  @OneToMany(mappedBy = "ownerId")
  private Collection<Study> studyCollection;
  @OneToMany(mappedBy = "ownerId")
  private Collection<File> fileCollection;
  @OneToMany(mappedBy = "ownerId")
  private Collection<Experiment> experimentCollection;
  @OneToMany(mappedBy = "ownerId")
  private Collection<Processing> processingCollection;
  @OneToMany(mappedBy = "ownerId")
  private Collection<WorkflowRun> workflowRunCollection;
  @OneToMany(mappedBy = "ownerId")
  private Collection<SequencerRun> sequencerRunCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "registrationId")
  private Collection<ShareExperiment> shareExperimentCollection;
  @OneToMany(mappedBy = "ownerId")
  private Collection<Workflow> workflowCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "registrationId")
  private Collection<ShareProcessing> shareProcessingCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "registrationId")
  private Collection<ShareFile> shareFileCollection;
  @OneToMany(mappedBy = "ownerId")
  private Collection<Lane> laneCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "registrationId")
  private Collection<ShareWorkflowRun> shareWorkflowRunCollection;
  @OneToMany(mappedBy = "ownerId")
  private Collection<Sample> sampleCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "registrationId")
  private Collection<ShareStudy> shareStudyCollection;
  @OneToMany(mappedBy = "ownerId")
  private Collection<Ius> iusCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "registrationId")
  private Collection<ShareSample> shareSampleCollection;

  public Registration() {
  }

  public Registration(Integer registrationId) {
    this.registrationId = registrationId;
  }

  public Registration(Integer registrationId, String email, String firstName, String lastName, boolean limsAdmin, Date createTstmp, Date lastUpdateTstmp, boolean developerMl, boolean userMl, boolean payee) {
    this.registrationId = registrationId;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.limsAdmin = limsAdmin;
    this.createTstmp = createTstmp;
    this.lastUpdateTstmp = lastUpdateTstmp;
    this.developerMl = developerMl;
    this.userMl = userMl;
    this.payee = payee;
  }

  public Integer getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(Integer registrationId) {
    this.registrationId = registrationId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPasswordHint() {
    return passwordHint;
  }

  public void setPasswordHint(String passwordHint) {
    this.passwordHint = passwordHint;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getInstitution() {
    return institution;
  }

  public void setInstitution(String institution) {
    this.institution = institution;
  }

  public String getInvitationCode() {
    return invitationCode;
  }

  public void setInvitationCode(String invitationCode) {
    this.invitationCode = invitationCode;
  }

  public boolean getLimsAdmin() {
    return limsAdmin;
  }

  public void setLimsAdmin(boolean limsAdmin) {
    this.limsAdmin = limsAdmin;
  }

  public Date getCreateTstmp() {
    return createTstmp;
  }

  public void setCreateTstmp(Date createTstmp) {
    this.createTstmp = createTstmp;
  }

  public Date getLastUpdateTstmp() {
    return lastUpdateTstmp;
  }

  public void setLastUpdateTstmp(Date lastUpdateTstmp) {
    this.lastUpdateTstmp = lastUpdateTstmp;
  }

  public boolean getDeveloperMl() {
    return developerMl;
  }

  public void setDeveloperMl(boolean developerMl) {
    this.developerMl = developerMl;
  }

  public boolean getUserMl() {
    return userMl;
  }

  public void setUserMl(boolean userMl) {
    this.userMl = userMl;
  }

  public boolean getPayee() {
    return payee;
  }

  public void setPayee(boolean payee) {
    this.payee = payee;
  }

  @XmlTransient
  public Collection<ShareLane> getShareLaneCollection() {
    return shareLaneCollection;
  }

  public void setShareLaneCollection(Collection<ShareLane> shareLaneCollection) {
    this.shareLaneCollection = shareLaneCollection;
  }

  @XmlTransient
  public Collection<Invoice> getInvoiceCollection() {
    return invoiceCollection;
  }

  public void setInvoiceCollection(Collection<Invoice> invoiceCollection) {
    this.invoiceCollection = invoiceCollection;
  }

  @XmlTransient
  public Collection<Study> getStudyCollection() {
    return studyCollection;
  }

  public void setStudyCollection(Collection<Study> studyCollection) {
    this.studyCollection = studyCollection;
  }

  @XmlTransient
  public Collection<File> getFileCollection() {
    return fileCollection;
  }

  public void setFileCollection(Collection<File> fileCollection) {
    this.fileCollection = fileCollection;
  }

  @XmlTransient
  public Collection<Experiment> getExperimentCollection() {
    return experimentCollection;
  }

  public void setExperimentCollection(Collection<Experiment> experimentCollection) {
    this.experimentCollection = experimentCollection;
  }

  @XmlTransient
  public Collection<Processing> getProcessingCollection() {
    return processingCollection;
  }

  public void setProcessingCollection(Collection<Processing> processingCollection) {
    this.processingCollection = processingCollection;
  }

  @XmlTransient
  public Collection<WorkflowRun> getWorkflowRunCollection() {
    return workflowRunCollection;
  }

  public void setWorkflowRunCollection(Collection<WorkflowRun> workflowRunCollection) {
    this.workflowRunCollection = workflowRunCollection;
  }

  @XmlTransient
  public Collection<SequencerRun> getSequencerRunCollection() {
    return sequencerRunCollection;
  }

  public void setSequencerRunCollection(Collection<SequencerRun> sequencerRunCollection) {
    this.sequencerRunCollection = sequencerRunCollection;
  }

  @XmlTransient
  public Collection<ShareExperiment> getShareExperimentCollection() {
    return shareExperimentCollection;
  }

  public void setShareExperimentCollection(Collection<ShareExperiment> shareExperimentCollection) {
    this.shareExperimentCollection = shareExperimentCollection;
  }

  @XmlTransient
  public Collection<Workflow> getWorkflowCollection() {
    return workflowCollection;
  }

  public void setWorkflowCollection(Collection<Workflow> workflowCollection) {
    this.workflowCollection = workflowCollection;
  }

  @XmlTransient
  public Collection<ShareProcessing> getShareProcessingCollection() {
    return shareProcessingCollection;
  }

  public void setShareProcessingCollection(Collection<ShareProcessing> shareProcessingCollection) {
    this.shareProcessingCollection = shareProcessingCollection;
  }

  @XmlTransient
  public Collection<ShareFile> getShareFileCollection() {
    return shareFileCollection;
  }

  public void setShareFileCollection(Collection<ShareFile> shareFileCollection) {
    this.shareFileCollection = shareFileCollection;
  }

  @XmlTransient
  public Collection<Lane> getLaneCollection() {
    return laneCollection;
  }

  public void setLaneCollection(Collection<Lane> laneCollection) {
    this.laneCollection = laneCollection;
  }

  @XmlTransient
  public Collection<ShareWorkflowRun> getShareWorkflowRunCollection() {
    return shareWorkflowRunCollection;
  }

  public void setShareWorkflowRunCollection(Collection<ShareWorkflowRun> shareWorkflowRunCollection) {
    this.shareWorkflowRunCollection = shareWorkflowRunCollection;
  }

  @XmlTransient
  public Collection<Sample> getSampleCollection() {
    return sampleCollection;
  }

  public void setSampleCollection(Collection<Sample> sampleCollection) {
    this.sampleCollection = sampleCollection;
  }

  @XmlTransient
  public Collection<ShareStudy> getShareStudyCollection() {
    return shareStudyCollection;
  }

  public void setShareStudyCollection(Collection<ShareStudy> shareStudyCollection) {
    this.shareStudyCollection = shareStudyCollection;
  }

  @XmlTransient
  public Collection<Ius> getIusCollection() {
    return iusCollection;
  }

  public void setIusCollection(Collection<Ius> iusCollection) {
    this.iusCollection = iusCollection;
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
    hash += (registrationId != null ? registrationId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Registration)) {
      return false;
    }
    Registration other = (Registration) object;
    if ((this.registrationId == null && other.registrationId != null) || (this.registrationId != null && !this.registrationId.equals(other.registrationId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.Registration[ registrationId=" + registrationId + " ]";
  }
  
}
