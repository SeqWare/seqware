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
@Table(name = "library_source")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "LibrarySource.findAll", query = "SELECT l FROM LibrarySource l"),
  @NamedQuery(name = "LibrarySource.findByLibrarySourceId", query = "SELECT l FROM LibrarySource l WHERE l.librarySourceId = :librarySourceId"),
  @NamedQuery(name = "LibrarySource.findByName", query = "SELECT l FROM LibrarySource l WHERE l.name = :name"),
  @NamedQuery(name = "LibrarySource.findByDescription", query = "SELECT l FROM LibrarySource l WHERE l.description = :description")})
public class LibrarySource implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "library_source_id")
  private Integer librarySourceId;
  @Size(max = 2147483647)
  @Column(name = "name")
  private String name;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @OneToMany(mappedBy = "librarySource")
  private Collection<Lane> laneCollection;
  @OneToMany(mappedBy = "source")
  private Collection<ExperimentLibraryDesign> experimentLibraryDesignCollection;

  public LibrarySource() {
  }

  public LibrarySource(Integer librarySourceId) {
    this.librarySourceId = librarySourceId;
  }

  public Integer getLibrarySourceId() {
    return librarySourceId;
  }

  public void setLibrarySourceId(Integer librarySourceId) {
    this.librarySourceId = librarySourceId;
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
    hash += (librarySourceId != null ? librarySourceId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof LibrarySource)) {
      return false;
    }
    LibrarySource other = (LibrarySource) object;
    if ((this.librarySourceId == null && other.librarySourceId != null) || (this.librarySourceId != null && !this.librarySourceId.equals(other.librarySourceId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.LibrarySource[ librarySourceId=" + librarySourceId + " ]";
  }
  
}
