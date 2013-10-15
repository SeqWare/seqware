/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.model;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "experiment_spot_design")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ExperimentSpotDesign.findAll", query = "SELECT e FROM ExperimentSpotDesign e"),
  @NamedQuery(name = "ExperimentSpotDesign.findByExperimentSpotDesignId", query = "SELECT e FROM ExperimentSpotDesign e WHERE e.experimentSpotDesignId = :experimentSpotDesignId"),
  @NamedQuery(name = "ExperimentSpotDesign.findByDecodeMethod", query = "SELECT e FROM ExperimentSpotDesign e WHERE e.decodeMethod = :decodeMethod"),
  @NamedQuery(name = "ExperimentSpotDesign.findByReadsPerSpot", query = "SELECT e FROM ExperimentSpotDesign e WHERE e.readsPerSpot = :readsPerSpot"),
  @NamedQuery(name = "ExperimentSpotDesign.findByReadSpec", query = "SELECT e FROM ExperimentSpotDesign e WHERE e.readSpec = :readSpec"),
  @NamedQuery(name = "ExperimentSpotDesign.findByTagSpec", query = "SELECT e FROM ExperimentSpotDesign e WHERE e.tagSpec = :tagSpec"),
  @NamedQuery(name = "ExperimentSpotDesign.findByAdapterSpec", query = "SELECT e FROM ExperimentSpotDesign e WHERE e.adapterSpec = :adapterSpec")})
public class ExperimentSpotDesign implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "experiment_spot_design_id")
  private Integer experimentSpotDesignId;
  @Column(name = "decode_method")
  private Integer decodeMethod;
  @Column(name = "reads_per_spot")
  private Integer readsPerSpot;
  @Size(max = 2147483647)
  @Column(name = "read_spec")
  private String readSpec;
  @Size(max = 2147483647)
  @Column(name = "tag_spec")
  private String tagSpec;
  @Size(max = 2147483647)
  @Column(name = "adapter_spec")
  private String adapterSpec;
  @OneToMany(mappedBy = "experimentSpotDesignId")
  private Collection<Experiment> experimentCollection;
  @OneToMany(mappedBy = "experimentSpotDesignId")
  private Collection<ExperimentSpotDesignReadSpec> experimentSpotDesignReadSpecCollection;

  public ExperimentSpotDesign() {
  }

  public ExperimentSpotDesign(Integer experimentSpotDesignId) {
    this.experimentSpotDesignId = experimentSpotDesignId;
  }

  public Integer getExperimentSpotDesignId() {
    return experimentSpotDesignId;
  }

  public void setExperimentSpotDesignId(Integer experimentSpotDesignId) {
    this.experimentSpotDesignId = experimentSpotDesignId;
  }

  public Integer getDecodeMethod() {
    return decodeMethod;
  }

  public void setDecodeMethod(Integer decodeMethod) {
    this.decodeMethod = decodeMethod;
  }

  public Integer getReadsPerSpot() {
    return readsPerSpot;
  }

  public void setReadsPerSpot(Integer readsPerSpot) {
    this.readsPerSpot = readsPerSpot;
  }

  public String getReadSpec() {
    return readSpec;
  }

  public void setReadSpec(String readSpec) {
    this.readSpec = readSpec;
  }

  public String getTagSpec() {
    return tagSpec;
  }

  public void setTagSpec(String tagSpec) {
    this.tagSpec = tagSpec;
  }

  public String getAdapterSpec() {
    return adapterSpec;
  }

  public void setAdapterSpec(String adapterSpec) {
    this.adapterSpec = adapterSpec;
  }

  @XmlTransient
  public Collection<Experiment> getExperimentCollection() {
    return experimentCollection;
  }

  public void setExperimentCollection(Collection<Experiment> experimentCollection) {
    this.experimentCollection = experimentCollection;
  }

  @XmlTransient
  public Collection<ExperimentSpotDesignReadSpec> getExperimentSpotDesignReadSpecCollection() {
    return experimentSpotDesignReadSpecCollection;
  }

  public void setExperimentSpotDesignReadSpecCollection(Collection<ExperimentSpotDesignReadSpec> experimentSpotDesignReadSpecCollection) {
    this.experimentSpotDesignReadSpecCollection = experimentSpotDesignReadSpecCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (experimentSpotDesignId != null ? experimentSpotDesignId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ExperimentSpotDesign)) {
      return false;
    }
    ExperimentSpotDesign other = (ExperimentSpotDesign) object;
    if ((this.experimentSpotDesignId == null && other.experimentSpotDesignId != null) || (this.experimentSpotDesignId != null && !this.experimentSpotDesignId.equals(other.experimentSpotDesignId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ExperimentSpotDesign[ experimentSpotDesignId=" + experimentSpotDesignId + " ]";
  }
  
}
