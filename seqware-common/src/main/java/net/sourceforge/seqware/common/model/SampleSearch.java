package net.sourceforge.seqware.common.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * A search for samples that match a given list of attributes. The search is
 * saved and the id can be reused to identify all the samples that match the
 * search at a later date.
 *
 * Purpose: The id can be used in a bam file header to aggregate libraries (a
 * type of sample) that have common attributes. For example the search id for
 * all samples that with geo_tissue_prepartion=FFPE could be used in a bam file
 * to treat all libraries for a patient with the same preparation method as the
 * same.
 *
 * @author boconnor
 * @version $Id: $Id
 */
@Entity
@Table(name = "sample_search")
public class SampleSearch {

  @Id
  @SequenceGenerator(name = "sample_search_id_seq_gen", sequenceName = "sample_search_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "sample_search_id_seq_gen")
  @Column(name = "sample_search_id")
  private Integer sampleSearchId;

  @ManyToOne
  @JoinColumn(name = "sample_id", nullable = false)
  private Sample sample;

  @OneToMany(mappedBy = "sampleSearch", cascade = CascadeType.ALL)
  @Column(name = "sample_search_attributes")
  private Set<SampleSearchAttribute> sampleSearchAttributes;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_tstmp", nullable = false)
  private Date created;

  /**
   * <p>Getter for the field <code>sampleSearchId</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getSampleSearchId() {
    return sampleSearchId;
  }

  /**
   * <p>Setter for the field <code>sampleSearchId</code>.</p>
   *
   * @param sampleSearchId a {@link java.lang.Integer} object.
   */
  public void setSampleSearchId(Integer sampleSearchId) {
    this.sampleSearchId = sampleSearchId;
  }

  /**
   * <p>Getter for the field <code>sampleSearchAttributes</code>.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public Set<SampleSearchAttribute> getSampleSearchAttributes() {
    return sampleSearchAttributes;
  }

  /**
   * <p>Setter for the field <code>sampleSearchAttributes</code>.</p>
   *
   * @param sampleSearchAttributes a {@link java.util.Set} object.
   */
  public void setSampleSearchAttributes(Set<SampleSearchAttribute> sampleSearchAttributes) {
    this.sampleSearchAttributes = sampleSearchAttributes;
  }

  /**
   * <p>Getter for the field <code>sample</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
   */
  public Sample getSample() {
    return sample;
  }

  /**
   * <p>Setter for the field <code>sample</code>.</p>
   *
   * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
   */
  public void setSample(Sample sample) {
    this.sample = sample;
  }

  /**
   * <p>Getter for the field <code>created</code>.</p>
   *
   * @return a {@link java.util.Date} object.
   */
  public Date getCreated() {
    return created;
  }

  /**
   * <p>Setter for the field <code>created</code>.</p>
   *
   * @param created a {@link java.util.Date} object.
   */
  public void setCreated(Date created) {
    this.created = created;
  }
}
