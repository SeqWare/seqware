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

/**
 * Attributes that are part of a sample search.
 *
 * @author boconnor
 * @version $Id: $Id
 */
@Entity
@Table(name = "sample_search_attribute", uniqueConstraints = { @UniqueConstraint(columnNames = { "sample_search_id",
    "tag", "value" }) })
public class SampleSearchAttribute implements Attribute<SampleSearch> {

  @Id
  @SequenceGenerator(name = "sample_search_attribute_id_seq_gen", sequenceName = "sample_search_attribute_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "sample_search_attribute_id_seq_gen")
  @Column(name = "sample_search_attribute_id")
  private Integer sampleSearchAttributeId;

  @ManyToOne ( cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH} )
  @JoinColumn(name = "sample_search_id")
  private SampleSearch sampleSearch;

  @Column(nullable = false)
  private String tag;

  @Column(nullable = false)
  private String value;

  /**
   * <p>Getter for the field <code>sampleSearchAttributeId</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getSampleSearchAttributeId() {
    return sampleSearchAttributeId;
  }

  /**
   * <p>Setter for the field <code>sampleSearchAttributeId</code>.</p>
   *
   * @param sampleSearchAttributeId a {@link java.lang.Integer} object.
   */
  public void setSampleSearchAttributeId(Integer sampleSearchAttributeId) {
    this.sampleSearchAttributeId = sampleSearchAttributeId;
  }

  /**
   * <p>Getter for the field <code>sampleSearch</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.SampleSearch} object.
   */
  public SampleSearch getSampleSearch() {
    return sampleSearch;
  }

  /**
   * <p>Setter for the field <code>sampleSearch</code>.</p>
   *
   * @param sampleSearch a {@link net.sourceforge.seqware.common.model.SampleSearch} object.
   */
  public void setSampleSearch(SampleSearch sampleSearch) {
    this.sampleSearch = sampleSearch;
  }

  /** {@inheritDoc} */
  @Override
  public String getTag() {
    return this.tag;
  }

  /** {@inheritDoc} */
  @Override
  public void setTag(String tag) {
    this.tag = tag;
  }

  /** {@inheritDoc} */
  @Override
  public String getValue() {
    return this.value;
  }

  /** {@inheritDoc} */
  @Override
  public void setValue(String value) {
    this.value = value;
  }

  /** {@inheritDoc} */
  @Override
  public String getUnit() {
    // Sample Search Attributes have no units.
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public void setUnit(String unit) {
    throw new UnsupportedOperationException("SampleSearchAttributes have no units.");

  }

    @Override
    public void setAttributeParent(SampleSearch parent) {
        this.setSampleSearch(parent);
    }

}
