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
 */
@Entity
@Table(name = "sample_search_attribute", uniqueConstraints = { @UniqueConstraint(columnNames = { "sample_search_id",
    "tag", "value" }) })
public class SampleSearchAttribute implements Attribute {

  @Id
  @SequenceGenerator(name = "sample_search_attribute_id_seq_gen", sequenceName = "sample_search_attribute_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "sample_search_attribute_id_seq_gen")
  @Column(name = "sample_search_attribute_id")
  private Integer sampleSearchAttributeId;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "sample_search_id")
  private SampleSearch sampleSearch;

  @Column(nullable = false)
  private String tag;

  @Column(nullable = false)
  private String value;

  public Integer getSampleSearchAttributeId() {
    return sampleSearchAttributeId;
  }

  public void setSampleSearchAttributeId(Integer sampleSearchAttributeId) {
    this.sampleSearchAttributeId = sampleSearchAttributeId;
  }

  public SampleSearch getSampleSearch() {
    return sampleSearch;
  }

  public void setSampleSearch(SampleSearch sampleSearch) {
    this.sampleSearch = sampleSearch;
  }

  @Override
  public String getTag() {
    return this.tag;
  }

  @Override
  public void setTag(String tag) {
    this.tag = tag;
  }

  @Override
  public String getValue() {
    return this.value;
  }

  @Override
  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String getUnit() {
    // Sample Search Attributes have no units.
    return null;
  }

  @Override
  public void setUnit(String unit) {
    throw new UnsupportedOperationException("SampleSearchAttributes have no units.");

  }

}
