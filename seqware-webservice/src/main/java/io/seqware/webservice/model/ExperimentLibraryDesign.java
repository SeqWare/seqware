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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "experiment_library_design")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ExperimentLibraryDesign.findAll", query = "SELECT e FROM ExperimentLibraryDesign e"),
  @NamedQuery(name = "ExperimentLibraryDesign.findByExperimentLibraryDesignId", query = "SELECT e FROM ExperimentLibraryDesign e WHERE e.experimentLibraryDesignId = :experimentLibraryDesignId"),
  @NamedQuery(name = "ExperimentLibraryDesign.findByName", query = "SELECT e FROM ExperimentLibraryDesign e WHERE e.name = :name"),
  @NamedQuery(name = "ExperimentLibraryDesign.findByDescription", query = "SELECT e FROM ExperimentLibraryDesign e WHERE e.description = :description"),
  @NamedQuery(name = "ExperimentLibraryDesign.findByConstructionProtocol", query = "SELECT e FROM ExperimentLibraryDesign e WHERE e.constructionProtocol = :constructionProtocol"),
  @NamedQuery(name = "ExperimentLibraryDesign.findByLayout", query = "SELECT e FROM ExperimentLibraryDesign e WHERE e.layout = :layout"),
  @NamedQuery(name = "ExperimentLibraryDesign.findByPairedOrientation", query = "SELECT e FROM ExperimentLibraryDesign e WHERE e.pairedOrientation = :pairedOrientation"),
  @NamedQuery(name = "ExperimentLibraryDesign.findByNominalLength", query = "SELECT e FROM ExperimentLibraryDesign e WHERE e.nominalLength = :nominalLength"),
  @NamedQuery(name = "ExperimentLibraryDesign.findByNominalSdev", query = "SELECT e FROM ExperimentLibraryDesign e WHERE e.nominalSdev = :nominalSdev")})
public class ExperimentLibraryDesign implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "experiment_library_design_id")
  private Integer experimentLibraryDesignId;
  @Size(max = 2147483647)
  @Column(name = "name")
  private String name;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @Size(max = 2147483647)
  @Column(name = "construction_protocol")
  private String constructionProtocol;
  @Size(max = 2147483647)
  @Column(name = "layout")
  private String layout;
  @Size(max = 2147483647)
  @Column(name = "paired_orientation")
  private String pairedOrientation;
  @Column(name = "nominal_length")
  private Integer nominalLength;
  // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
  @Column(name = "nominal_sdev")
  private Double nominalSdev;
  @OneToMany(mappedBy = "experimentLibraryDesignId")
  private Collection<Experiment> experimentCollection;
  @JoinColumn(name = "strategy", referencedColumnName = "library_strategy_id")
  @ManyToOne
  private LibraryStrategy strategy;
  @JoinColumn(name = "source", referencedColumnName = "library_source_id")
  @ManyToOne
  private LibrarySource source;
  @JoinColumn(name = "selection", referencedColumnName = "library_selection_id")
  @ManyToOne
  private LibrarySelection selection;

  public ExperimentLibraryDesign() {
  }

  public ExperimentLibraryDesign(Integer experimentLibraryDesignId) {
    this.experimentLibraryDesignId = experimentLibraryDesignId;
  }

  public Integer getExperimentLibraryDesignId() {
    return experimentLibraryDesignId;
  }

  public void setExperimentLibraryDesignId(Integer experimentLibraryDesignId) {
    this.experimentLibraryDesignId = experimentLibraryDesignId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getConstructionProtocol() {
    return constructionProtocol;
  }

  public void setConstructionProtocol(String constructionProtocol) {
    this.constructionProtocol = constructionProtocol;
  }

  public String getLayout() {
    return layout;
  }

  public void setLayout(String layout) {
    this.layout = layout;
  }

  public String getPairedOrientation() {
    return pairedOrientation;
  }

  public void setPairedOrientation(String pairedOrientation) {
    this.pairedOrientation = pairedOrientation;
  }

  public Integer getNominalLength() {
    return nominalLength;
  }

  public void setNominalLength(Integer nominalLength) {
    this.nominalLength = nominalLength;
  }

  public Double getNominalSdev() {
    return nominalSdev;
  }

  public void setNominalSdev(Double nominalSdev) {
    this.nominalSdev = nominalSdev;
  }

  @XmlTransient
  public Collection<Experiment> getExperimentCollection() {
    return experimentCollection;
  }

  public void setExperimentCollection(Collection<Experiment> experimentCollection) {
    this.experimentCollection = experimentCollection;
  }

  public LibraryStrategy getStrategy() {
    return strategy;
  }

  public void setStrategy(LibraryStrategy strategy) {
    this.strategy = strategy;
  }

  public LibrarySource getSource() {
    return source;
  }

  public void setSource(LibrarySource source) {
    this.source = source;
  }

  public LibrarySelection getSelection() {
    return selection;
  }

  public void setSelection(LibrarySelection selection) {
    this.selection = selection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (experimentLibraryDesignId != null ? experimentLibraryDesignId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ExperimentLibraryDesign)) {
      return false;
    }
    ExperimentLibraryDesign other = (ExperimentLibraryDesign) object;
    if ((this.experimentLibraryDesignId == null && other.experimentLibraryDesignId != null) || (this.experimentLibraryDesignId != null && !this.experimentLibraryDesignId.equals(other.experimentLibraryDesignId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ExperimentLibraryDesign[ experimentLibraryDesignId=" + experimentLibraryDesignId + " ]";
  }
  
}
