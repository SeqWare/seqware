package net.sourceforge.seqware.common.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "workflow_attribute", uniqueConstraints = { @UniqueConstraint(columnNames = { "workflow_id", "tag",
    "value" }) })
public class WorkflowAttribute implements Attribute, Comparable<WorkflowAttribute> {

  @Id
  @SequenceGenerator(name = "workflow_attribute_id_seq_gen", sequenceName = "workflow_attribute_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "workflow_attribute_id_seq_gen")
  @Column(name = "workflow_attribute_id")
  private Integer workflowAttributeId;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "workflow_id", nullable = false)
  private Workflow workflow;

  @Column(name = "tag", nullable = false)
  private String tag;

  @Column(nullable = false)
  private String value;

  private String unit;

  @Override
  public String getTag() {
    return tag;
  }

  @Override
  public void setTag(String tag) {
    this.tag = tag;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String getUnit() {
    return unit;
  }

  @Override
  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Workflow getWorkflow() {
    return workflow;
  }

  public void setWorkflow(Workflow workflow) {
    this.workflow = workflow;
  }

  public Integer getWorkflowAttributeId() {
    return workflowAttributeId;
  }

  @Override
  public int compareTo(WorkflowAttribute wfa) {
    return (this.tag + this.value).compareTo(wfa.tag + wfa.value);
  }

}
