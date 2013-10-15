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
@Table(name = "library_strategy")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "LibraryStrategy.findAll", query = "SELECT l FROM LibraryStrategy l"),
  @NamedQuery(name = "LibraryStrategy.findByLibraryStrategyId", query = "SELECT l FROM LibraryStrategy l WHERE l.libraryStrategyId = :libraryStrategyId"),
  @NamedQuery(name = "LibraryStrategy.findByName", query = "SELECT l FROM LibraryStrategy l WHERE l.name = :name"),
  @NamedQuery(name = "LibraryStrategy.findByDescription", query = "SELECT l FROM LibraryStrategy l WHERE l.description = :description")})
public class LibraryStrategy implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "library_strategy_id")
  private Integer libraryStrategyId;
  @Size(max = 2147483647)
  @Column(name = "name")
  private String name;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @OneToMany(mappedBy = "libraryStrategy")
  private Collection<Lane> laneCollection;
  @OneToMany(mappedBy = "strategy")
  private Collection<ExperimentLibraryDesign> experimentLibraryDesignCollection;

  public LibraryStrategy() {
  }

  public LibraryStrategy(Integer libraryStrategyId) {
    this.libraryStrategyId = libraryStrategyId;
  }

  public Integer getLibraryStrategyId() {
    return libraryStrategyId;
  }

  public void setLibraryStrategyId(Integer libraryStrategyId) {
    this.libraryStrategyId = libraryStrategyId;
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

  @XmlTransient
  public Collection<Lane> getLaneCollection() {
    return laneCollection;
  }

  public void setLaneCollection(Collection<Lane> laneCollection) {
    this.laneCollection = laneCollection;
  }

  @XmlTransient
  public Collection<ExperimentLibraryDesign> getExperimentLibraryDesignCollection() {
    return experimentLibraryDesignCollection;
  }

  public void setExperimentLibraryDesignCollection(Collection<ExperimentLibraryDesign> experimentLibraryDesignCollection) {
    this.experimentLibraryDesignCollection = experimentLibraryDesignCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (libraryStrategyId != null ? libraryStrategyId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof LibraryStrategy)) {
      return false;
    }
    LibraryStrategy other = (LibraryStrategy) object;
    if ((this.libraryStrategyId == null && other.libraryStrategyId != null) || (this.libraryStrategyId != null && !this.libraryStrategyId.equals(other.libraryStrategyId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.LibraryStrategy[ libraryStrategyId=" + libraryStrategyId + " ]";
  }
  
}
