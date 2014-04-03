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
import org.hibernate.annotations.Cascade;

@Entity
/**
 * <p>WorkflowAttribute class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
@Table(name = "workflow_attribute", uniqueConstraints = { @UniqueConstraint(columnNames = { "workflow_id", "tag",
    "value" }) })
public class WorkflowAttribute implements Attribute<Workflow>, Comparable<WorkflowAttribute> {

  @Id
  @SequenceGenerator(name = "workflow_attribute_id_seq_gen", sequenceName = "workflow_attribute_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "workflow_attribute_id_seq_gen")
  @Column(name = "workflow_attribute_id")
  private Integer workflowAttributeId;

  // SEQWARE-1578 
  @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH} )
  // a special Hibernate workaround since http://tai-dev.blog.co.uk/2011/11/18/fun-with-hibernates-cascadetype-persist-and-cascadetype-all-the-case-of-the-unsaved-transient-instance-12187037/
  @Cascade( org.hibernate.annotations.CascadeType.SAVE_UPDATE )
  @JoinColumn(name = "workflow_id", nullable = false)
  private Workflow workflow;

  @Column(name = "tag", nullable = false)
  private String tag;

  @Column(nullable = false)
  private String value;

  private String unit;

  /** {@inheritDoc} */
  @Override
  public String getTag() {
    return tag;
  }

  /** {@inheritDoc} */
  @Override
  public void setTag(String tag) {
    this.tag = tag;
  }

  /** {@inheritDoc} */
  @Override
  public String getValue() {
    return value;
  }

  /** {@inheritDoc} */
  @Override
  public void setValue(String value) {
    this.value = value;
  }

  /** {@inheritDoc} */
  @Override
  public String getUnit() {
    return unit;
  }

  /** {@inheritDoc} */
  @Override
  public void setUnit(String unit) {
    this.unit = unit;
  }

  /**
   * <p>Getter for the field <code>workflow</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.Workflow} object.
   */
  public Workflow getWorkflow() {
    return workflow;
  }

  /**
   * <p>Setter for the field <code>workflow</code>.</p>
   *
   * @param workflow a {@link net.sourceforge.seqware.common.model.Workflow} object.
   */
  public void setWorkflow(Workflow workflow) {
    this.workflow = workflow;
  }

  /**
   * <p>Getter for the field <code>workflowAttributeId</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getWorkflowAttributeId() {
    return workflowAttributeId;
  }

  /** {@inheritDoc} */
  @Override
  public int compareTo(WorkflowAttribute wfa) {
    return (this.tag + this.value).compareTo(wfa.tag + wfa.value);
  }

    @Override
    public void setAttributeParent(Workflow parent) {
        this.setWorkflow(parent);
    }

}
