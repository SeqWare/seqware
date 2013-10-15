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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "workflow_param_value")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "WorkflowParamValue.findAll", query = "SELECT w FROM WorkflowParamValue w"),
  @NamedQuery(name = "WorkflowParamValue.findByWorkflowParamValueId", query = "SELECT w FROM WorkflowParamValue w WHERE w.workflowParamValueId = :workflowParamValueId"),
  @NamedQuery(name = "WorkflowParamValue.findByDisplayName", query = "SELECT w FROM WorkflowParamValue w WHERE w.displayName = :displayName"),
  @NamedQuery(name = "WorkflowParamValue.findByValue", query = "SELECT w FROM WorkflowParamValue w WHERE w.value = :value")})
public class WorkflowParamValue implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "workflow_param_value_id")
  private Integer workflowParamValueId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "display_name")
  private String displayName;
  @Size(max = 2147483647)
  @Column(name = "value")
  private String value;
  @JoinColumn(name = "workflow_param_id", referencedColumnName = "workflow_param_id")
  @ManyToOne(optional = false)
  private WorkflowParam workflowParamId;

  public WorkflowParamValue() {
  }

  public WorkflowParamValue(Integer workflowParamValueId) {
    this.workflowParamValueId = workflowParamValueId;
  }

  public WorkflowParamValue(Integer workflowParamValueId, String displayName) {
    this.workflowParamValueId = workflowParamValueId;
    this.displayName = displayName;
  }

  public Integer getWorkflowParamValueId() {
    return workflowParamValueId;
  }

  public void setWorkflowParamValueId(Integer workflowParamValueId) {
    this.workflowParamValueId = workflowParamValueId;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public WorkflowParam getWorkflowParamId() {
    return workflowParamId;
  }

  public void setWorkflowParamId(WorkflowParam workflowParamId) {
    this.workflowParamId = workflowParamId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (workflowParamValueId != null ? workflowParamValueId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof WorkflowParamValue)) {
      return false;
    }
    WorkflowParamValue other = (WorkflowParamValue) object;
    if ((this.workflowParamValueId == null && other.workflowParamValueId != null) || (this.workflowParamValueId != null && !this.workflowParamValueId.equals(other.workflowParamValueId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.WorkflowParamValue[ workflowParamValueId=" + workflowParamValueId + " ]";
  }
  
}
