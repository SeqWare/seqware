/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.model;

import java.io.Serializable;
import java.util.Collection;
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
@Table(name = "workflow_param")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "WorkflowParam.findAll", query = "SELECT w FROM WorkflowParam w"),
  @NamedQuery(name = "WorkflowParam.findByWorkflowParamId", query = "SELECT w FROM WorkflowParam w WHERE w.workflowParamId = :workflowParamId"),
  @NamedQuery(name = "WorkflowParam.findByType", query = "SELECT w FROM WorkflowParam w WHERE w.type = :type"),
  @NamedQuery(name = "WorkflowParam.findByKey", query = "SELECT w FROM WorkflowParam w WHERE w.key = :key"),
  @NamedQuery(name = "WorkflowParam.findByDisplay", query = "SELECT w FROM WorkflowParam w WHERE w.display = :display"),
  @NamedQuery(name = "WorkflowParam.findByDisplayName", query = "SELECT w FROM WorkflowParam w WHERE w.displayName = :displayName"),
  @NamedQuery(name = "WorkflowParam.findByFileMetaType", query = "SELECT w FROM WorkflowParam w WHERE w.fileMetaType = :fileMetaType"),
  @NamedQuery(name = "WorkflowParam.findByDefaultValue", query = "SELECT w FROM WorkflowParam w WHERE w.defaultValue = :defaultValue")})
public class WorkflowParam implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "workflow_param_id")
  private Integer workflowParamId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "type")
  private String type;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "key")
  private String key;
  @Column(name = "display")
  private Boolean display;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "display_name")
  private String displayName;
  @Size(max = 2147483647)
  @Column(name = "file_meta_type")
  private String fileMetaType;
  @Size(max = 2147483647)
  @Column(name = "default_value")
  private String defaultValue;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "workflowParamId")
  private Collection<WorkflowParamValue> workflowParamValueCollection;
  @JoinColumn(name = "workflow_id", referencedColumnName = "workflow_id")
  @ManyToOne(optional = false)
  private Workflow workflowId;

  public WorkflowParam() {
  }

  public WorkflowParam(Integer workflowParamId) {
    this.workflowParamId = workflowParamId;
  }

  public WorkflowParam(Integer workflowParamId, String type, String key, String displayName) {
    this.workflowParamId = workflowParamId;
    this.type = type;
    this.key = key;
    this.displayName = displayName;
  }

  public Integer getWorkflowParamId() {
    return workflowParamId;
  }

  public void setWorkflowParamId(Integer workflowParamId) {
    this.workflowParamId = workflowParamId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Boolean getDisplay() {
    return display;
  }

  public void setDisplay(Boolean display) {
    this.display = display;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getFileMetaType() {
    return fileMetaType;
  }

  public void setFileMetaType(String fileMetaType) {
    this.fileMetaType = fileMetaType;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  @XmlTransient
  public Collection<WorkflowParamValue> getWorkflowParamValueCollection() {
    return workflowParamValueCollection;
  }

  public void setWorkflowParamValueCollection(Collection<WorkflowParamValue> workflowParamValueCollection) {
    this.workflowParamValueCollection = workflowParamValueCollection;
  }

  public Workflow getWorkflowId() {
    return workflowId;
  }

  public void setWorkflowId(Workflow workflowId) {
    this.workflowId = workflowId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (workflowParamId != null ? workflowParamId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof WorkflowParam)) {
      return false;
    }
    WorkflowParam other = (WorkflowParam) object;
    if ((this.workflowParamId == null && other.workflowParamId != null) || (this.workflowParamId != null && !this.workflowParamId.equals(other.workflowParamId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.WorkflowParam[ workflowParamId=" + workflowParamId + " ]";
  }
  
}
