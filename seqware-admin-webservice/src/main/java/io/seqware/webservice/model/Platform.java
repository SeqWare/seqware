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
@Table(name = "platform")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Platform.findAll", query = "SELECT p FROM Platform p"),
  @NamedQuery(name = "Platform.findByPlatformId", query = "SELECT p FROM Platform p WHERE p.platformId = :platformId"),
  @NamedQuery(name = "Platform.findByName", query = "SELECT p FROM Platform p WHERE p.name = :name"),
  @NamedQuery(name = "Platform.findByInstrumentModel", query = "SELECT p FROM Platform p WHERE p.instrumentModel = :instrumentModel"),
  @NamedQuery(name = "Platform.findByDescription", query = "SELECT p FROM Platform p WHERE p.description = :description")})
public class Platform implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "platform_id")
  private Integer platformId;
  @Size(max = 2147483647)
  @Column(name = "name")
  private String name;
  @Size(max = 2147483647)
  @Column(name = "instrument_model")
  private String instrumentModel;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @OneToMany(mappedBy = "platformId")
  private Collection<Experiment> experimentCollection;
  @OneToMany(mappedBy = "platformId")
  private Collection<SequencerRun> sequencerRunCollection;

  public Platform() {
  }

  public Platform(Integer platformId) {
    this.platformId = platformId;
  }

  public Integer getPlatformId() {
    return platformId;
  }

  public void setPlatformId(Integer platformId) {
    this.platformId = platformId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getInstrumentModel() {
    return instrumentModel;
  }

  public void setInstrumentModel(String instrumentModel) {
    this.instrumentModel = instrumentModel;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @XmlTransient
  public Collection<Experiment> getExperimentCollection() {
    return experimentCollection;
  }

  public void setExperimentCollection(Collection<Experiment> experimentCollection) {
    this.experimentCollection = experimentCollection;
  }

  @XmlTransient
  public Collection<SequencerRun> getSequencerRunCollection() {
    return sequencerRunCollection;
  }

  public void setSequencerRunCollection(Collection<SequencerRun> sequencerRunCollection) {
    this.sequencerRunCollection = sequencerRunCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (platformId != null ? platformId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Platform)) {
      return false;
    }
    Platform other = (Platform) object;
    if ((this.platformId == null && other.platformId != null) || (this.platformId != null && !this.platformId.equals(other.platformId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.Platform[ platformId=" + platformId + " ]";
  }
  
}
