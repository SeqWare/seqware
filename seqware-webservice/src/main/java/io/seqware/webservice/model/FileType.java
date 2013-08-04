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
@Table(name = "file_type")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "FileType.findAll", query = "SELECT f FROM FileType f"),
  @NamedQuery(name = "FileType.findByFileTypeId", query = "SELECT f FROM FileType f WHERE f.fileTypeId = :fileTypeId"),
  @NamedQuery(name = "FileType.findByDisplayName", query = "SELECT f FROM FileType f WHERE f.displayName = :displayName"),
  @NamedQuery(name = "FileType.findByMetaType", query = "SELECT f FROM FileType f WHERE f.metaType = :metaType"),
  @NamedQuery(name = "FileType.findByExtension", query = "SELECT f FROM FileType f WHERE f.extension = :extension")})
public class FileType implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "file_type_id")
  private Integer fileTypeId;
  @Size(max = 2147483647)
  @Column(name = "display_name")
  private String displayName;
  @Size(max = 2147483647)
  @Column(name = "meta_type")
  private String metaType;
  @Size(max = 2147483647)
  @Column(name = "extension")
  private String extension;
  @OneToMany(mappedBy = "fileTypeId")
  private Collection<File> fileCollection;

  public FileType() {
  }

  public FileType(Integer fileTypeId) {
    this.fileTypeId = fileTypeId;
  }

  public Integer getFileTypeId() {
    return fileTypeId;
  }

  public void setFileTypeId(Integer fileTypeId) {
    this.fileTypeId = fileTypeId;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getMetaType() {
    return metaType;
  }

  public void setMetaType(String metaType) {
    this.metaType = metaType;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  @XmlTransient
  public Collection<File> getFileCollection() {
    return fileCollection;
  }

  public void setFileCollection(Collection<File> fileCollection) {
    this.fileCollection = fileCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (fileTypeId != null ? fileTypeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof FileType)) {
      return false;
    }
    FileType other = (FileType) object;
    if ((this.fileTypeId == null && other.fileTypeId != null) || (this.fileTypeId != null && !this.fileTypeId.equals(other.fileTypeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.FileType[ fileTypeId=" + fileTypeId + " ]";
  }
  
}
