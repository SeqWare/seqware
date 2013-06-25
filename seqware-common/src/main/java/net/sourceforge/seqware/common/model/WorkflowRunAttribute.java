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
 * <p>WorkflowRunAttribute class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
@Table(name = "workflow_run_attribute", uniqueConstraints = { @UniqueConstraint(columnNames = { "workflow_run_id",
    "tag", "value" }) })
public class WorkflowRunAttribute implements Attribute<WorkflowRun>, Comparable<WorkflowRunAttribute> {

  @Id
  @SequenceGenerator(name = "workflow_run_attribute_id_seq_gen", sequenceName = "workflow_run_attribute_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "workflow_run_attribute_id_seq_gen")
  @Column(name = "workflow_run_attribute_id")
  private Integer workflowRunAttributeId;

  @ManyToOne( cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH} )
  // a special Hibernate workaround since http://tai-dev.blog.co.uk/2011/11/18/fun-with-hibernates-cascadetype-persist-and-cascadetype-all-the-case-of-the-unsaved-transient-instance-12187037/
  @Cascade( org.hibernate.annotations.CascadeType.SAVE_UPDATE )
  @JoinColumn(name = "workflow_run_id", nullable = false)
  private WorkflowRun workflowRun;

  @Column(nullable = false)
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
   * <p>Getter for the field <code>workflowRun</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  public WorkflowRun getWorkflowRun() {
    return workflowRun;
  }

  /**
   * <p>Setter for the field <code>workflowRun</code>.</p>
   *
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  public void setWorkflowRun(WorkflowRun workflowRun) {
    this.workflowRun = workflowRun;
  }

  /**
   * <p>Getter for the field <code>workflowRunAttributeId</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getWorkflowRunAttributeId() {
    return workflowRunAttributeId;
  }

	/** {@inheritDoc} */
	@Override
	public int compareTo(WorkflowRunAttribute t) {
		return (t.tag + t.value).compareTo(this.tag+this.value);
	}

    @Override
    public void setAttributeParent(WorkflowRun parent) {
        this.setWorkflowRun(parent);
    }

}
