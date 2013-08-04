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
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "processing")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Processing.findAll", query = "SELECT p FROM Processing p"),
  @NamedQuery(name = "Processing.findByProcessingId", query = "SELECT p FROM Processing p WHERE p.processingId = :processingId"),
  @NamedQuery(name = "Processing.findByAlgorithm", query = "SELECT p FROM Processing p WHERE p.algorithm = :algorithm"),
  @NamedQuery(name = "Processing.findByStatus", query = "SELECT p FROM Processing p WHERE p.status = :status"),
  @NamedQuery(name = "Processing.findByDescription", query = "SELECT p FROM Processing p WHERE p.description = :description"),
  @NamedQuery(name = "Processing.findByUrl", query = "SELECT p FROM Processing p WHERE p.url = :url"),
  @NamedQuery(name = "Processing.findByUrlLabel", query = "SELECT p FROM Processing p WHERE p.urlLabel = :urlLabel"),
  @NamedQuery(name = "Processing.findByVersion", query = "SELECT p FROM Processing p WHERE p.version = :version"),
  @NamedQuery(name = "Processing.findByParameters", query = "SELECT p FROM Processing p WHERE p.parameters = :parameters"),
  @NamedQuery(name = "Processing.findByStdout", query = "SELECT p FROM Processing p WHERE p.stdout = :stdout"),
  @NamedQuery(name = "Processing.findByStderr", query = "SELECT p FROM Processing p WHERE p.stderr = :stderr"),
  @NamedQuery(name = "Processing.findByExitStatus", query = "SELECT p FROM Processing p WHERE p.exitStatus = :exitStatus"),
  @NamedQuery(name = "Processing.findByProcessExitStatus", query = "SELECT p FROM Processing p WHERE p.processExitStatus = :processExitStatus"),
  @NamedQuery(name = "Processing.findByTaskGroup", query = "SELECT p FROM Processing p WHERE p.taskGroup = :taskGroup"),
  @NamedQuery(name = "Processing.findBySwAccession", query = "SELECT p FROM Processing p WHERE p.swAccession = :swAccession"),
  @NamedQuery(name = "Processing.findByRunStartTstmp", query = "SELECT p FROM Processing p WHERE p.runStartTstmp = :runStartTstmp"),
  @NamedQuery(name = "Processing.findByRunStopTstmp", query = "SELECT p FROM Processing p WHERE p.runStopTstmp = :runStopTstmp"),
  @NamedQuery(name = "Processing.findByCreateTstmp", query = "SELECT p FROM Processing p WHERE p.createTstmp = :createTstmp"),
  @NamedQuery(name = "Processing.findByUpdateTstmp", query = "SELECT p FROM Processing p WHERE p.updateTstmp = :updateTstmp")})
public class Processing implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "processing_id")
  private Integer processingId;
  @Size(max = 2147483647)
  @Column(name = "algorithm")
  private String algorithm;
  @Size(max = 2147483647)
  @Column(name = "status")
  private String status;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @Size(max = 2147483647)
  @Column(name = "url")
  private String url;
  @Size(max = 2147483647)
  @Column(name = "url_label")
  private String urlLabel;
  @Size(max = 2147483647)
  @Column(name = "version")
  private String version;
  @Size(max = 2147483647)
  @Column(name = "parameters")
  private String parameters;
  @Size(max = 2147483647)
  @Column(name = "stdout")
  private String stdout;
  @Size(max = 2147483647)
  @Column(name = "stderr")
  private String stderr;
  @Column(name = "exit_status")
  private Integer exitStatus;
  @Column(name = "process_exit_status")
  private Integer processExitStatus;
  @Column(name = "task_group")
  private Boolean taskGroup;
  @Column(name = "sw_accession")
  private Integer swAccession;
  @Column(name = "run_start_tstmp")
  @Temporal(TemporalType.TIMESTAMP)
  private Date runStartTstmp;
  @Column(name = "run_stop_tstmp")
  @Temporal(TemporalType.TIMESTAMP)
  private Date runStopTstmp;
  @Basic(optional = false)
  @NotNull
  @Column(name = "create_tstmp")
  @Temporal(TemporalType.TIMESTAMP)
  private Date createTstmp;
  @Column(name = "update_tstmp")
  @Temporal(TemporalType.TIMESTAMP)
  private Date updateTstmp;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "processingId")
  private Collection<ProcessingAttribute> processingAttributeCollection;
  @JoinColumn(name = "workflow_run_id", referencedColumnName = "workflow_run_id")
  @ManyToOne
  private WorkflowRun workflowRunId;
  @JoinColumn(name = "ancestor_workflow_run_id", referencedColumnName = "workflow_run_id")
  @ManyToOne
  private WorkflowRun ancestorWorkflowRunId;
  @JoinColumn(name = "owner_id", referencedColumnName = "registration_id")
  @ManyToOne
  private Registration ownerId;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "processingId")
  private Collection<ProcessingLanes> processingLanesCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "processingId")
  private Collection<ProcessingExperiments> processingExperimentsCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "processingId")
  private Collection<ProcessingSequencerRuns> processingSequencerRunsCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "processingId")
  private Collection<ShareProcessing> shareProcessingCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "processingId")
  private Collection<ProcessingSamples> processingSamplesCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "processingId")
  private Collection<ProcessingIus> processingIusCollection;
  @OneToMany(mappedBy = "parentId")
  private Collection<ProcessingRelationship> processingRelationshipCollection;
  @OneToMany(mappedBy = "childId")
  private Collection<ProcessingRelationship> processingRelationshipCollection1;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "processingId")
  private Collection<ProcessingStudies> processingStudiesCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "processingId")
  private Collection<ProcessingFiles> processingFilesCollection;

  public Processing() {
  }

  public Processing(Integer processingId) {
    this.processingId = processingId;
  }

  public Processing(Integer processingId, Date createTstmp) {
    this.processingId = processingId;
    this.createTstmp = createTstmp;
  }

  public Integer getProcessingId() {
    return processingId;
  }

  public void setProcessingId(Integer processingId) {
    this.processingId = processingId;
  }

  public String getAlgorithm() {
    return algorithm;
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrlLabel() {
    return urlLabel;
  }

  public void setUrlLabel(String urlLabel) {
    this.urlLabel = urlLabel;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getParameters() {
    return parameters;
  }

  public void setParameters(String parameters) {
    this.parameters = parameters;
  }

  public String getStdout() {
    return stdout;
  }

  public void setStdout(String stdout) {
    this.stdout = stdout;
  }

  public String getStderr() {
    return stderr;
  }

  public void setStderr(String stderr) {
    this.stderr = stderr;
  }

  public Integer getExitStatus() {
    return exitStatus;
  }

  public void setExitStatus(Integer exitStatus) {
    this.exitStatus = exitStatus;
  }

  public Integer getProcessExitStatus() {
    return processExitStatus;
  }

  public void setProcessExitStatus(Integer processExitStatus) {
    this.processExitStatus = processExitStatus;
  }

  public Boolean getTaskGroup() {
    return taskGroup;
  }

  public void setTaskGroup(Boolean taskGroup) {
    this.taskGroup = taskGroup;
  }

  public Integer getSwAccession() {
    return swAccession;
  }

  public void setSwAccession(Integer swAccession) {
    this.swAccession = swAccession;
  }

  public Date getRunStartTstmp() {
    return runStartTstmp;
  }

  public void setRunStartTstmp(Date runStartTstmp) {
    this.runStartTstmp = runStartTstmp;
  }

  public Date getRunStopTstmp() {
    return runStopTstmp;
  }

  public void setRunStopTstmp(Date runStopTstmp) {
    this.runStopTstmp = runStopTstmp;
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
  public Collection<ProcessingAttribute> getProcessingAttributeCollection() {
    return processingAttributeCollection;
  }

  public void setProcessingAttributeCollection(Collection<ProcessingAttribute> processingAttributeCollection) {
    this.processingAttributeCollection = processingAttributeCollection;
  }

  @XmlTransient
  public WorkflowRun getWorkflowRunId() {
    return workflowRunId;
  }
  
  public void afterUnmarshal(Unmarshaller u, Object parent) {
    this.workflowRunId = (WorkflowRun)parent;
  }

  public void setWorkflowRunId(WorkflowRun workflowRunId) {
    this.workflowRunId = workflowRunId;
  }

  @XmlTransient
  public WorkflowRun getAncestorWorkflowRunId() {
    return ancestorWorkflowRunId;
  }

  public void setAncestorWorkflowRunId(WorkflowRun ancestorWorkflowRunId) {
    this.ancestorWorkflowRunId = ancestorWorkflowRunId;
  }

  public Registration getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Registration ownerId) {
    this.ownerId = ownerId;
  }

  @XmlTransient
  public Collection<ProcessingLanes> getProcessingLanesCollection() {
    return processingLanesCollection;
  }

  public void setProcessingLanesCollection(Collection<ProcessingLanes> processingLanesCollection) {
    this.processingLanesCollection = processingLanesCollection;
  }

  @XmlTransient
  public Collection<ProcessingExperiments> getProcessingExperimentsCollection() {
    return processingExperimentsCollection;
  }

  public void setProcessingExperimentsCollection(Collection<ProcessingExperiments> processingExperimentsCollection) {
    this.processingExperimentsCollection = processingExperimentsCollection;
  }

  @XmlTransient
  public Collection<ProcessingSequencerRuns> getProcessingSequencerRunsCollection() {
    return processingSequencerRunsCollection;
  }

  public void setProcessingSequencerRunsCollection(Collection<ProcessingSequencerRuns> processingSequencerRunsCollection) {
    this.processingSequencerRunsCollection = processingSequencerRunsCollection;
  }

  @XmlTransient
  public Collection<ShareProcessing> getShareProcessingCollection() {
    return shareProcessingCollection;
  }

  public void setShareProcessingCollection(Collection<ShareProcessing> shareProcessingCollection) {
    this.shareProcessingCollection = shareProcessingCollection;
  }

  @XmlTransient
  public Collection<ProcessingSamples> getProcessingSamplesCollection() {
    return processingSamplesCollection;
  }

  public void setProcessingSamplesCollection(Collection<ProcessingSamples> processingSamplesCollection) {
    this.processingSamplesCollection = processingSamplesCollection;
  }

  @XmlTransient
  public Collection<ProcessingIus> getProcessingIusCollection() {
    return processingIusCollection;
  }

  public void setProcessingIusCollection(Collection<ProcessingIus> processingIusCollection) {
    this.processingIusCollection = processingIusCollection;
  }

  @XmlTransient
  public Collection<ProcessingRelationship> getProcessingRelationshipCollection() {
    return processingRelationshipCollection;
  }

  public void setProcessingRelationshipCollection(Collection<ProcessingRelationship> processingRelationshipCollection) {
    this.processingRelationshipCollection = processingRelationshipCollection;
  }

  @XmlTransient
  public Collection<ProcessingRelationship> getProcessingRelationshipCollection1() {
    return processingRelationshipCollection1;
  }

  public void setProcessingRelationshipCollection1(Collection<ProcessingRelationship> processingRelationshipCollection1) {
    this.processingRelationshipCollection1 = processingRelationshipCollection1;
  }

  @XmlTransient
  public Collection<ProcessingStudies> getProcessingStudiesCollection() {
    return processingStudiesCollection;
  }

  public void setProcessingStudiesCollection(Collection<ProcessingStudies> processingStudiesCollection) {
    this.processingStudiesCollection = processingStudiesCollection;
  }

  @XmlTransient
  public Collection<ProcessingFiles> getProcessingFilesCollection() {
    return processingFilesCollection;
  }

  public void setProcessingFilesCollection(Collection<ProcessingFiles> processingFilesCollection) {
    this.processingFilesCollection = processingFilesCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (processingId != null ? processingId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Processing)) {
      return false;
    }
    Processing other = (Processing) object;
    if ((this.processingId == null && other.processingId != null) || (this.processingId != null && !this.processingId.equals(other.processingId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.Processing[ processingId=" + processingId + " ]";
  }
  
}
