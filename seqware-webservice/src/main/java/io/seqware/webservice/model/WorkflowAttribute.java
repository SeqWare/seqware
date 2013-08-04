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
@Table(name = "workflow_attribute")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "WorkflowAttribute.findAll", query = "SELECT w FROM WorkflowAttribute w"),
  @NamedQuery(name = "WorkflowAttribute.findByWorkflowAttributeId", query = "SELECT w FROM WorkflowAttribute w WHERE w.workflowAttributeId = :workflowAttributeId"),
  @NamedQuery(name = "WorkflowAttribute.findByTag", query = "SELECT w FROM WorkflowAttribute w WHERE w.tag = :tag"),
  @NamedQuery(name = "WorkflowAttribute.findByValue", query = "SELECT w FROM WorkflowAttribute w WHERE w.value = :value"),
  @NamedQuery(name = "WorkflowAttribute.findByUnit", query = "SELECT w FROM WorkflowAttribute w WHERE w.unit = :unit")})
public class WorkflowAttribute implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "workflow_attribute_id")
  private Integer workflowAttributeId;
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
  @JoinColumn(name = "workflow_id", referencedColumnName = "workflow_id")
  @ManyToOne(optional = false)
  private Workflow workflowId;

  public WorkflowAttribute() {
  }

  public WorkflowAttribute(Integer workflowAttributeId) {
    this.workflowAttributeId = workflowAttributeId;
  }

  public WorkflowAttribute(Integer workflowAttributeId, String tag, String value) {
    this.workflowAttributeId = workflowAttributeId;
    this.tag = tag;
    this.value = value;
  }

  public Integer getWorkflowAttributeId() {
    return workflowAttributeId;
  }

  public void setWorkflowAttributeId(Integer workflowAttributeId) {
    this.workflowAttributeId = workflowAttributeId;
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

  public Workflow getWorkflowId() {
    return workflowId;
  }

  public void setWorkflowId(Workflow workflowId) {
    this.workflowId = workflowId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (workflowAttributeId != null ? workflowAttributeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof WorkflowAttribute)) {
      return false;
    }
    WorkflowAttribute other = (WorkflowAttribute) object;
    if ((this.workflowAttributeId == null && other.workflowAttributeId != null) || (this.workflowAttributeId != null && !this.workflowAttributeId.equals(other.workflowAttributeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.WorkflowAttribute[ workflowAttributeId=" + workflowAttributeId + " ]";
  }
  
}
