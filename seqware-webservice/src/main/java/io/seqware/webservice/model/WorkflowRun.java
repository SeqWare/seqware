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
@Table(name = "workflow_run")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "WorkflowRun.findAll", query = "SELECT w FROM WorkflowRun w"),
  @NamedQuery(name = "WorkflowRun.findByWorkflowRunId", query = "SELECT w FROM WorkflowRun w WHERE w.workflowRunId = :workflowRunId"),
  @NamedQuery(name = "WorkflowRun.findByName", query = "SELECT w FROM WorkflowRun w WHERE w.name = :name"),
  @NamedQuery(name = "WorkflowRun.findByIniFile", query = "SELECT w FROM WorkflowRun w WHERE w.iniFile = :iniFile"),
  @NamedQuery(name = "WorkflowRun.findByCmd", query = "SELECT w FROM WorkflowRun w WHERE w.cmd = :cmd"),
  @NamedQuery(name = "WorkflowRun.findByWorkflowTemplate", query = "SELECT w FROM WorkflowRun w WHERE w.workflowTemplate = :workflowTemplate"),
  @NamedQuery(name = "WorkflowRun.findByDax", query = "SELECT w FROM WorkflowRun w WHERE w.dax = :dax"),
  @NamedQuery(name = "WorkflowRun.findByStatus", query = "SELECT w FROM WorkflowRun w WHERE w.status = :status"),
  @NamedQuery(name = "WorkflowRun.findByStatusCmd", query = "SELECT w FROM WorkflowRun w WHERE w.statusCmd = :statusCmd"),
  @NamedQuery(name = "WorkflowRun.findBySeqwareRevision", query = "SELECT w FROM WorkflowRun w WHERE w.seqwareRevision = :seqwareRevision"),
  @NamedQuery(name = "WorkflowRun.findByHost", query = "SELECT w FROM WorkflowRun w WHERE w.host = :host"),
  @NamedQuery(name = "WorkflowRun.findByCurrentWorkingDir", query = "SELECT w FROM WorkflowRun w WHERE w.currentWorkingDir = :currentWorkingDir"),
  @NamedQuery(name = "WorkflowRun.findByUsername", query = "SELECT w FROM WorkflowRun w WHERE w.username = :username"),
  @NamedQuery(name = "WorkflowRun.findByStderr", query = "SELECT w FROM WorkflowRun w WHERE w.stderr = :stderr"),
  @NamedQuery(name = "WorkflowRun.findByStdout", query = "SELECT w FROM WorkflowRun w WHERE w.stdout = :stdout"),
  @NamedQuery(name = "WorkflowRun.findByCreateTstmp", query = "SELECT w FROM WorkflowRun w WHERE w.createTstmp = :createTstmp"),
  @NamedQuery(name = "WorkflowRun.findByUpdateTstmp", query = "SELECT w FROM WorkflowRun w WHERE w.updateTstmp = :updateTstmp"),
  @NamedQuery(name = "WorkflowRun.findBySwAccession", query = "SELECT w FROM WorkflowRun w WHERE w.swAccession = :swAccession"),
  @NamedQuery(name = "WorkflowRun.findByWorkflowEngine", query = "SELECT w FROM WorkflowRun w WHERE w.workflowEngine = :workflowEngine")})
public class WorkflowRun implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "workflow_run_id")
  private Integer workflowRunId;
  @Size(max = 2147483647)
  @Column(name = "name")
  private String name;
  @Size(max = 2147483647)
  @Column(name = "ini_file")
  private String iniFile;
  @Size(max = 2147483647)
  @Column(name = "cmd")
  private String cmd;
  @Size(max = 2147483647)
  @Column(name = "workflow_template")
  private String workflowTemplate;
  @Size(max = 2147483647)
  @Column(name = "dax")
  private String dax;
  @Size(max = 2147483647)
  @Column(name = "status")
  private String status;
  @Size(max = 2147483647)
  @Column(name = "status_cmd")
  private String statusCmd;
  @Size(max = 2147483647)
  @Column(name = "seqware_revision")
  private String seqwareRevision;
  @Size(max = 2147483647)
  @Column(name = "host")
  private String host;
  @Size(max = 2147483647)
  @Column(name = "current_working_dir")
  private String currentWorkingDir;
  @Size(max = 2147483647)
  @Column(name = "username")
  private String username;
  @Size(max = 2147483647)
  @Column(name = "stderr")
  private String stderr;
  @Size(max = 2147483647)
  @Column(name = "stdout")
  private String stdout;
  @Basic(optional = false)
  @NotNull
  @Column(name = "create_tstmp")
  @Temporal(TemporalType.TIMESTAMP)
  private Date createTstmp;
  @Column(name = "update_tstmp")
  @Temporal(TemporalType.TIMESTAMP)
  private Date updateTstmp;
  @Column(name = "sw_accession")
  private Integer swAccession;
  @Size(max = 2147483647)
  @Column(name = "workflow_engine")
  private String workflowEngine;
  @OneToMany(mappedBy = "workflowRunId", fetch = FetchType.EAGER)
  private Collection<Processing> processingCollection;
  @OneToMany(mappedBy = "ancestorWorkflowRunId", fetch = FetchType.EAGER)
  private Collection<Processing> processingCollection1;
  @JoinColumn(name = "workflow_id", referencedColumnName = "workflow_id")
  @ManyToOne(optional = false)
  private Workflow workflowId;
  @JoinColumn(name = "owner_id", referencedColumnName = "registration_id")
  @ManyToOne
  private Registration ownerId;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowRunId")
  private Collection<IusWorkflowRuns> iusWorkflowRunsCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowRunId")
  private Collection<WorkflowRunAttribute> workflowRunAttributeCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowRunId")
  private Collection<LaneWorkflowRuns> laneWorkflowRunsCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowRunId")
  private Collection<ShareWorkflowRun> shareWorkflowRunCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowRunId")
  private Collection<WorkflowRunParam> workflowRunParamCollection;

  public WorkflowRun() {
  }

  public WorkflowRun(Integer workflowRunId) {
    this.workflowRunId = workflowRunId;
  }

  public WorkflowRun(Integer workflowRunId, Date createTstmp) {
    this.workflowRunId = workflowRunId;
    this.createTstmp = createTstmp;
  }

  public Integer getWorkflowRunId() {
    return workflowRunId;
  }

  public void setWorkflowRunId(Integer workflowRunId) {
    this.workflowRunId = workflowRunId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getIniFile() {
    return iniFile;
  }

  public void setIniFile(String iniFile) {
    this.iniFile = iniFile;
  }

  public String getCmd() {
    return cmd;
  }

  public void setCmd(String cmd) {
    this.cmd = cmd;
  }

  public String getWorkflowTemplate() {
    return workflowTemplate;
  }

  public void setWorkflowTemplate(String workflowTemplate) {
    this.workflowTemplate = workflowTemplate;
  }

  public String getDax() {
    return dax;
  }

  public void setDax(String dax) {
    this.dax = dax;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getStatusCmd() {
    return statusCmd;
  }

  public void setStatusCmd(String statusCmd) {
    this.statusCmd = statusCmd;
  }

  public String getSeqwareRevision() {
    return seqwareRevision;
  }

  public void setSeqwareRevision(String seqwareRevision) {
    this.seqwareRevision = seqwareRevision;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getCurrentWorkingDir() {
    return currentWorkingDir;
  }

  public void setCurrentWorkingDir(String currentWorkingDir) {
    this.currentWorkingDir = currentWorkingDir;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getStderr() {
    return stderr;
  }

  public void setStderr(String stderr) {
    this.stderr = stderr;
  }

  public String getStdout() {
    return stdout;
  }

  public void setStdout(String stdout) {
    this.stdout = stdout;
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

  public Integer getSwAccession() {
    return swAccession;
  }

  public void setSwAccession(Integer swAccession) {
    this.swAccession = swAccession;
  }

  public String getWorkflowEngine() {
    return workflowEngine;
  }

  public void setWorkflowEngine(String workflowEngine) {
    this.workflowEngine = workflowEngine;
  }

  //XmlTransient
  public Collection<Processing> getProcessingCollection() {
    return processingCollection;
  }

  public void setProcessingCollection(Collection<Processing> processingCollection) {
    this.processingCollection = processingCollection;
  }

  //XmlTransient
  public Collection<Processing> getProcessingCollection1() {
    return processingCollection1;
  }

  public void setProcessingCollection1(Collection<Processing> processingCollection1) {
    this.processingCollection1 = processingCollection1;
  }

  public Workflow getWorkflowId() {
    return workflowId;
  }

  public void setWorkflowId(Workflow workflowId) {
    this.workflowId = workflowId;
  }

  public Registration getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Registration ownerId) {
    this.ownerId = ownerId;
  }

  @XmlTransient
  public Collection<IusWorkflowRuns> getIusWorkflowRunsCollection() {
    return iusWorkflowRunsCollection;
  }

  public void setIusWorkflowRunsCollection(Collection<IusWorkflowRuns> iusWorkflowRunsCollection) {
    this.iusWorkflowRunsCollection = iusWorkflowRunsCollection;
  }

  @XmlTransient
  public Collection<WorkflowRunAttribute> getWorkflowRunAttributeCollection() {
    return workflowRunAttributeCollection;
  }

  public void setWorkflowRunAttributeCollection(Collection<WorkflowRunAttribute> workflowRunAttributeCollection) {
    this.workflowRunAttributeCollection = workflowRunAttributeCollection;
  }

  @XmlTransient
  public Collection<LaneWorkflowRuns> getLaneWorkflowRunsCollection() {
    return laneWorkflowRunsCollection;
  }

  public void setLaneWorkflowRunsCollection(Collection<LaneWorkflowRuns> laneWorkflowRunsCollection) {
    this.laneWorkflowRunsCollection = laneWorkflowRunsCollection;
  }

  @XmlTransient
  public Collection<ShareWorkflowRun> getShareWorkflowRunCollection() {
    return shareWorkflowRunCollection;
  }

  public void setShareWorkflowRunCollection(Collection<ShareWorkflowRun> shareWorkflowRunCollection) {
    this.shareWorkflowRunCollection = shareWorkflowRunCollection;
  }

  @XmlTransient
  public Collection<WorkflowRunParam> getWorkflowRunParamCollection() {
    return workflowRunParamCollection;
  }

  public void setWorkflowRunParamCollection(Collection<WorkflowRunParam> workflowRunParamCollection) {
    this.workflowRunParamCollection = workflowRunParamCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (workflowRunId != null ? workflowRunId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof WorkflowRun)) {
      return false;
    }
    WorkflowRun other = (WorkflowRun) object;
    if ((this.workflowRunId == null && other.workflowRunId != null) || (this.workflowRunId != null && !this.workflowRunId.equals(other.workflowRunId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.WorkflowRun[ workflowRunId=" + workflowRunId + " ]";
  }
  
}
