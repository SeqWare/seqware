/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "workflow_run_param")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "WorkflowRunParam.findAll", query = "SELECT w FROM WorkflowRunParam w"),
  @NamedQuery(name = "WorkflowRunParam.findByWorkflowRunParamId", query = "SELECT w FROM WorkflowRunParam w WHERE w.workflowRunParamId = :workflowRunParamId"),
  @NamedQuery(name = "WorkflowRunParam.findByType", query = "SELECT w FROM WorkflowRunParam w WHERE w.type = :type"),
  @NamedQuery(name = "WorkflowRunParam.findByKey", query = "SELECT w FROM WorkflowRunParam w WHERE w.key = :key"),
  @NamedQuery(name = "WorkflowRunParam.findByParentProcessingAccession", query = "SELECT w FROM WorkflowRunParam w WHERE w.parentProcessingAccession = :parentProcessingAccession"),
  @NamedQuery(name = "WorkflowRunParam.findByValue", query = "SELECT w FROM WorkflowRunParam w WHERE w.value = :value")})
public class WorkflowRunParam implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "workflow_run_param_id")
  private Integer workflowRunParamId;
  @Size(max = 2147483647)
  @Column(name = "type")
  private String type;
  @Size(max = 2147483647)
  @Column(name = "key")
  private String key;
  @Column(name = "parent_processing_accession")
  private Integer parentProcessingAccession;
  @Size(max = 2147483647)
  @Column(name = "value")
  private String value;
  @JoinColumn(name = "workflow_run_id", referencedColumnName = "workflow_run_id")
  @ManyToOne(optional = false)
  private WorkflowRun workflowRunId;

  public WorkflowRunParam() {
  }

  public WorkflowRunParam(Integer workflowRunParamId) {
    this.workflowRunParamId = workflowRunParamId;
  }

  public Integer getWorkflowRunParamId() {
    return workflowRunParamId;
  }

  public void setWorkflowRunParamId(Integer workflowRunParamId) {
    this.workflowRunParamId = workflowRunParamId;
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

  public Integer getParentProcessingAccession() {
    return parentProcessingAccession;
  }

  public void setParentProcessingAccession(Integer parentProcessingAccession) {
    this.parentProcessingAccession = parentProcessingAccession;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public WorkflowRun getWorkflowRunId() {
    return workflowRunId;
  }

  public void setWorkflowRunId(WorkflowRun workflowRunId) {
    this.workflowRunId = workflowRunId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (workflowRunParamId != null ? workflowRunParamId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof WorkflowRunParam)) {
      return false;
    }
    WorkflowRunParam other = (WorkflowRunParam) object;
    if ((this.workflowRunParamId == null && other.workflowRunParamId != null) || (this.workflowRunParamId != null && !this.workflowRunParamId.equals(other.workflowRunParamId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.WorkflowRunParam[ workflowRunParamId=" + workflowRunParamId + " ]";
  }
  
}
