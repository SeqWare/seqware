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
@Table(name = "workflow_run_attribute")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "WorkflowRunAttribute.findAll", query = "SELECT w FROM WorkflowRunAttribute w"),
  @NamedQuery(name = "WorkflowRunAttribute.findByWorkflowRunAttributeId", query = "SELECT w FROM WorkflowRunAttribute w WHERE w.workflowRunAttributeId = :workflowRunAttributeId"),
  @NamedQuery(name = "WorkflowRunAttribute.findByTag", query = "SELECT w FROM WorkflowRunAttribute w WHERE w.tag = :tag"),
  @NamedQuery(name = "WorkflowRunAttribute.findByValue", query = "SELECT w FROM WorkflowRunAttribute w WHERE w.value = :value"),
  @NamedQuery(name = "WorkflowRunAttribute.findByUnit", query = "SELECT w FROM WorkflowRunAttribute w WHERE w.unit = :unit")})
public class WorkflowRunAttribute implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "workflow_run_attribute_id")
  private Integer workflowRunAttributeId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "tag")
  private String tag;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "value")
  private String value;
  @Size(max = 255)
  @Column(name = "unit")
  private String unit;
  @JoinColumn(name = "workflow_run_id", referencedColumnName = "workflow_run_id")
  @ManyToOne(optional = false)
  private WorkflowRun workflowRunId;

  public WorkflowRunAttribute() {
  }

  public WorkflowRunAttribute(Integer workflowRunAttributeId) {
    this.workflowRunAttributeId = workflowRunAttributeId;
  }

  public WorkflowRunAttribute(Integer workflowRunAttributeId, String tag, String value) {
    this.workflowRunAttributeId = workflowRunAttributeId;
    this.tag = tag;
    this.value = value;
  }

  public Integer getWorkflowRunAttributeId() {
    return workflowRunAttributeId;
  }

  public void setWorkflowRunAttributeId(Integer workflowRunAttributeId) {
    this.workflowRunAttributeId = workflowRunAttributeId;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
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
    hash += (workflowRunAttributeId != null ? workflowRunAttributeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof WorkflowRunAttribute)) {
      return false;
    }
    WorkflowRunAttribute other = (WorkflowRunAttribute) object;
    if ((this.workflowRunAttributeId == null && other.workflowRunAttributeId != null) || (this.workflowRunAttributeId != null && !this.workflowRunAttributeId.equals(other.workflowRunAttributeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.WorkflowRunAttribute[ workflowRunAttributeId=" + workflowRunAttributeId + " ]";
  }
  
}
