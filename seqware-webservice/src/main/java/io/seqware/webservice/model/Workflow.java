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
@Table(name = "workflow")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Workflow.findAll", query = "SELECT w FROM Workflow w"),
  @NamedQuery(name = "Workflow.findByWorkflowId", query = "SELECT w FROM Workflow w WHERE w.workflowId = :workflowId"),
  @NamedQuery(name = "Workflow.findByName", query = "SELECT w FROM Workflow w WHERE w.name = :name"),
  @NamedQuery(name = "Workflow.findByDescription", query = "SELECT w FROM Workflow w WHERE w.description = :description"),
  @NamedQuery(name = "Workflow.findByInputAlgorithm", query = "SELECT w FROM Workflow w WHERE w.inputAlgorithm = :inputAlgorithm"),
  @NamedQuery(name = "Workflow.findByVersion", query = "SELECT w FROM Workflow w WHERE w.version = :version"),
  @NamedQuery(name = "Workflow.findBySeqwareVersion", query = "SELECT w FROM Workflow w WHERE w.seqwareVersion = :seqwareVersion"),
  @NamedQuery(name = "Workflow.findByBaseIniFile", query = "SELECT w FROM Workflow w WHERE w.baseIniFile = :baseIniFile"),
  @NamedQuery(name = "Workflow.findByCmd", query = "SELECT w FROM Workflow w WHERE w.cmd = :cmd"),
  @NamedQuery(name = "Workflow.findByCurrentWorkingDir", query = "SELECT w FROM Workflow w WHERE w.currentWorkingDir = :currentWorkingDir"),
  @NamedQuery(name = "Workflow.findByHost", query = "SELECT w FROM Workflow w WHERE w.host = :host"),
  @NamedQuery(name = "Workflow.findByUsername", query = "SELECT w FROM Workflow w WHERE w.username = :username"),
  @NamedQuery(name = "Workflow.findByWorkflowTemplate", query = "SELECT w FROM Workflow w WHERE w.workflowTemplate = :workflowTemplate"),
  @NamedQuery(name = "Workflow.findByCreateTstmp", query = "SELECT w FROM Workflow w WHERE w.createTstmp = :createTstmp"),
  @NamedQuery(name = "Workflow.findByUpdateTstmp", query = "SELECT w FROM Workflow w WHERE w.updateTstmp = :updateTstmp"),
  @NamedQuery(name = "Workflow.findBySwAccession", query = "SELECT w FROM Workflow w WHERE w.swAccession = :swAccession"),
  @NamedQuery(name = "Workflow.findByPermanentBundleLocation", query = "SELECT w FROM Workflow w WHERE w.permanentBundleLocation = :permanentBundleLocation"),
  @NamedQuery(name = "Workflow.findByWorkflowClass", query = "SELECT w FROM Workflow w WHERE w.workflowClass = :workflowClass"),
  @NamedQuery(name = "Workflow.findByWorkflowType", query = "SELECT w FROM Workflow w WHERE w.workflowType = :workflowType"),
  @NamedQuery(name = "Workflow.findByWorkflowEngine", query = "SELECT w FROM Workflow w WHERE w.workflowEngine = :workflowEngine")})
public class Workflow implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "workflow_id")
  private Integer workflowId;
  @Size(max = 2147483647)
  @Column(name = "name")
  private String name;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @Size(max = 2147483647)
  @Column(name = "input_algorithm")
  private String inputAlgorithm;
  @Size(max = 2147483647)
  @Column(name = "version")
  private String version;
  @Size(max = 2147483647)
  @Column(name = "seqware_version")
  private String seqwareVersion;
  @Size(max = 2147483647)
  @Column(name = "base_ini_file")
  private String baseIniFile;
  @Size(max = 2147483647)
  @Column(name = "cmd")
  private String cmd;
  @Size(max = 2147483647)
  @Column(name = "current_working_dir")
  private String currentWorkingDir;
  @Size(max = 2147483647)
  @Column(name = "host")
  private String host;
  @Size(max = 2147483647)
  @Column(name = "username")
  private String username;
  @Size(max = 2147483647)
  @Column(name = "workflow_template")
  private String workflowTemplate;
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
  @Column(name = "permanent_bundle_location")
  private String permanentBundleLocation;
  @Size(max = 2147483647)
  @Column(name = "workflow_class")
  private String workflowClass;
  @Size(max = 2147483647)
  @Column(name = "workflow_type")
  private String workflowType;
  @Size(max = 2147483647)
  @Column(name = "workflow_engine")
  private String workflowEngine;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowId")
  private Collection<WorkflowParam> workflowParamCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowId")
  private Collection<WorkflowRun> workflowRunCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowId")
  private Collection<WorkflowAttribute> workflowAttributeCollection;
  @JoinColumn(name = "owner_id", referencedColumnName = "registration_id")
  @ManyToOne
  private Registration ownerId;

  public Workflow() {
  }

  public Workflow(Integer workflowId) {
    this.workflowId = workflowId;
  }

  public Workflow(Integer workflowId, Date createTstmp) {
    this.workflowId = workflowId;
    this.createTstmp = createTstmp;
  }

  public Integer getWorkflowId() {
    return workflowId;
  }

  public void setWorkflowId(Integer workflowId) {
    this.workflowId = workflowId;
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

  public String getInputAlgorithm() {
    return inputAlgorithm;
  }

  public void setInputAlgorithm(String inputAlgorithm) {
    this.inputAlgorithm = inputAlgorithm;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getSeqwareVersion() {
    return seqwareVersion;
  }

  public void setSeqwareVersion(String seqwareVersion) {
    this.seqwareVersion = seqwareVersion;
  }

  public String getBaseIniFile() {
    return baseIniFile;
  }

  public void setBaseIniFile(String baseIniFile) {
    this.baseIniFile = baseIniFile;
  }

  public String getCmd() {
    return cmd;
  }

  public void setCmd(String cmd) {
    this.cmd = cmd;
  }

  public String getCurrentWorkingDir() {
    return currentWorkingDir;
  }

  public void setCurrentWorkingDir(String currentWorkingDir) {
    this.currentWorkingDir = currentWorkingDir;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getWorkflowTemplate() {
    return workflowTemplate;
  }

  public void setWorkflowTemplate(String workflowTemplate) {
    this.workflowTemplate = workflowTemplate;
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

  public String getPermanentBundleLocation() {
    return permanentBundleLocation;
  }

  public void setPermanentBundleLocation(String permanentBundleLocation) {
    this.permanentBundleLocation = permanentBundleLocation;
  }

  public String getWorkflowClass() {
    return workflowClass;
  }

  public void setWorkflowClass(String workflowClass) {
    this.workflowClass = workflowClass;
  }

  public String getWorkflowType() {
    return workflowType;
  }

  public void setWorkflowType(String workflowType) {
    this.workflowType = workflowType;
  }

  public String getWorkflowEngine() {
    return workflowEngine;
  }

  public void setWorkflowEngine(String workflowEngine) {
    this.workflowEngine = workflowEngine;
  }

  @XmlTransient
  public Collection<WorkflowParam> getWorkflowParamCollection() {
    return workflowParamCollection;
  }

  public void setWorkflowParamCollection(Collection<WorkflowParam> workflowParamCollection) {
    this.workflowParamCollection = workflowParamCollection;
  }

  @XmlTransient
  public Collection<WorkflowRun> getWorkflowRunCollection() {
    return workflowRunCollection;
  }

  public void setWorkflowRunCollection(Collection<WorkflowRun> workflowRunCollection) {
    this.workflowRunCollection = workflowRunCollection;
  }

  @XmlTransient
  public Collection<WorkflowAttribute> getWorkflowAttributeCollection() {
    return workflowAttributeCollection;
  }

  public void setWorkflowAttributeCollection(Collection<WorkflowAttribute> workflowAttributeCollection) {
    this.workflowAttributeCollection = workflowAttributeCollection;
  }

  public Registration getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Registration ownerId) {
    this.ownerId = ownerId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (workflowId != null ? workflowId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Workflow)) {
      return false;
    }
    Workflow other = (Workflow) object;
    if ((this.workflowId == null && other.workflowId != null) || (this.workflowId != null && !this.workflowId.equals(other.workflowId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.Workflow[ workflowId=" + workflowId + " ]";
  }
  
}
