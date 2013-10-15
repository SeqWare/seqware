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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "file_attribute")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "FileAttribute.findAll", query = "SELECT f FROM FileAttribute f"),
  @NamedQuery(name = "FileAttribute.findByFileAttributeId", query = "SELECT f FROM FileAttribute f WHERE f.fileAttributeId = :fileAttributeId"),
  @NamedQuery(name = "FileAttribute.findByTag", query = "SELECT f FROM FileAttribute f WHERE f.tag = :tag"),
  @NamedQuery(name = "FileAttribute.findByValue", query = "SELECT f FROM FileAttribute f WHERE f.value = :value"),
  @NamedQuery(name = "FileAttribute.findByUnit", query = "SELECT f FROM FileAttribute f WHERE f.unit = :unit")})
public class FileAttribute implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "file_attribute_id")
  private Integer fileAttributeId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "tag")
  private String tag;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "value")
  private String value;
  @Size(max = 255)
  @Column(name = "unit")
  private String unit;
  @JoinColumn(name = "file_id", referencedColumnName = "file_id")
  @ManyToOne(optional = false)
  private File fileId;

  public FileAttribute() {
  }

  public FileAttribute(Integer fileAttributeId) {
    this.fileAttributeId = fileAttributeId;
  }

  public FileAttribute(Integer fileAttributeId, String tag, String value) {
    this.fileAttributeId = fileAttributeId;
    this.tag = tag;
    this.value = value;
  }

  public Integer getFileAttributeId() {
    return fileAttributeId;
  }

  public void setFileAttributeId(Integer fileAttributeId) {
    this.fileAttributeId = fileAttributeId;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public File getFileId() {
    return fileId;
  }

  public void setFileId(File fileId) {
    this.fileId = fileId;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (fileAttributeId != null ? fileAttributeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof FileAttribute)) {
      return false;
    }
    FileAttribute other = (FileAttribute) object;
    if ((this.fileAttributeId == null && other.fileAttributeId != null) || (this.fileAttributeId != null && !this.fileAttributeId.equals(other.fileAttributeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.FileAttribute[ fileAttributeId=" + fileAttributeId + " ]";
  }
  
}
