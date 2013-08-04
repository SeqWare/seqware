/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.model;

import java.io.Serializable;
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
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "experiment_spot_design_read_spec")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "ExperimentSpotDesignReadSpec.findAll", query = "SELECT e FROM ExperimentSpotDesignReadSpec e"),
  @NamedQuery(name = "ExperimentSpotDesignReadSpec.findByExperimentSpotDesignReadSpecId", query = "SELECT e FROM ExperimentSpotDesignReadSpec e WHERE e.experimentSpotDesignReadSpecId = :experimentSpotDesignReadSpecId"),
  @NamedQuery(name = "ExperimentSpotDesignReadSpec.findByReadIndex", query = "SELECT e FROM ExperimentSpotDesignReadSpec e WHERE e.readIndex = :readIndex"),
  @NamedQuery(name = "ExperimentSpotDesignReadSpec.findByReadLabel", query = "SELECT e FROM ExperimentSpotDesignReadSpec e WHERE e.readLabel = :readLabel"),
  @NamedQuery(name = "ExperimentSpotDesignReadSpec.findByReadClass", query = "SELECT e FROM ExperimentSpotDesignReadSpec e WHERE e.readClass = :readClass"),
  @NamedQuery(name = "ExperimentSpotDesignReadSpec.findByReadType", query = "SELECT e FROM ExperimentSpotDesignReadSpec e WHERE e.readType = :readType"),
  @NamedQuery(name = "ExperimentSpotDesignReadSpec.findByBaseCoord", query = "SELECT e FROM ExperimentSpotDesignReadSpec e WHERE e.baseCoord = :baseCoord"),
  @NamedQuery(name = "ExperimentSpotDesignReadSpec.findByCycleCoord", query = "SELECT e FROM ExperimentSpotDesignReadSpec e WHERE e.cycleCoord = :cycleCoord"),
  @NamedQuery(name = "ExperimentSpotDesignReadSpec.findByLength", query = "SELECT e FROM ExperimentSpotDesignReadSpec e WHERE e.length = :length"),
  @NamedQuery(name = "ExperimentSpotDesignReadSpec.findByExpectedBasecall", query = "SELECT e FROM ExperimentSpotDesignReadSpec e WHERE e.expectedBasecall = :expectedBasecall")})
public class ExperimentSpotDesignReadSpec implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "experiment_spot_design_read_spec_id")
  private Integer experimentSpotDesignReadSpecId;
  @Column(name = "read_index")
  private Integer readIndex;
  @Size(max = 2147483647)
  @Column(name = "read_label")
  private String readLabel;
  @Size(max = 2147483647)
  @Column(name = "read_class")
  private String readClass;
  @Size(max = 2147483647)
  @Column(name = "read_type")
  private String readType;
  @Column(name = "base_coord")
  private Integer baseCoord;
  @Column(name = "cycle_coord")
  private Integer cycleCoord;
  @Column(name = "length")
  private Integer length;
  @Size(max = 2147483647)
  @Column(name = "expected_basecall")
  private String expectedBasecall;
  @JoinColumn(name = "experiment_spot_design_id", referencedColumnName = "experiment_spot_design_id")
  @ManyToOne
  private ExperimentSpotDesign experimentSpotDesignId;

  public ExperimentSpotDesignReadSpec() {
  }

  public ExperimentSpotDesignReadSpec(Integer experimentSpotDesignReadSpecId) {
    this.experimentSpotDesignReadSpecId = experimentSpotDesignReadSpecId;
  }

  public Integer getExperimentSpotDesignReadSpecId() {
    return experimentSpotDesignReadSpecId;
  }

  public void setExperimentSpotDesignReadSpecId(Integer experimentSpotDesignReadSpecId) {
    this.experimentSpotDesignReadSpecId = experimentSpotDesignReadSpecId;
  }

  public Integer getReadIndex() {
    return readIndex;
  }

  public void setReadIndex(Integer readIndex) {
    this.readIndex = readIndex;
  }

  public String getReadLabel() {
    return readLabel;
  }

  public void setReadLabel(String readLabel) {
    this.readLabel = readLabel;
  }

  public String getReadClass() {
    return readClass;
  }

  public void setReadClass(String readClass) {
    this.readClass = readClass;
  }

  public String getReadType() {
    return readType;
  }

  public void setReadType(String readType) {
    this.readType = readType;
  }

  public Integer getBaseCoord() {
    return baseCoord;
  }

  public void setBaseCoord(Integer baseCoord) {
    this.baseCoord = baseCoord;
  }

  public Integer getCycleCoord() {
    return cycleCoord;
  }

  public void setCycleCoord(Integer cycleCoord) {
    this.cycleCoord = cycleCoord;
  }

  public Integer getLength() {
    return length;
  }

  public void setLength(Integer length) {
    this.length = length;
  }

  public String getExpectedBasecall() {
    return expectedBasecall;
  }

  public void setExpectedBasecall(String expectedBasecall) {
    this.expectedBasecall = expectedBasecall;
  }

  public ExperimentSpotDesign getExperimentSpotDesignId() {
    return experimentSpotDesignId;
  }

  public void setExperimentSpotDesignId(ExperimentSpotDesign experimentSpotDesignId) {
    this.experimentSpotDesignId = experimentSpotDesignId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (experimentSpotDesignReadSpecId != null ? experimentSpotDesignReadSpecId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof ExperimentSpotDesignReadSpec)) {
      return false;
    }
    ExperimentSpotDesignReadSpec other = (ExperimentSpotDesignReadSpec) object;
    if ((this.experimentSpotDesignReadSpecId == null && other.experimentSpotDesignReadSpecId != null) || (this.experimentSpotDesignReadSpecId != null && !this.experimentSpotDesignReadSpecId.equals(other.experimentSpotDesignReadSpecId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.ExperimentSpotDesignReadSpec[ experimentSpotDesignReadSpecId=" + experimentSpotDesignReadSpecId + " ]";
  }
  
}
