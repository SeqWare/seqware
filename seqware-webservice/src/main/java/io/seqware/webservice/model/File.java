/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package io.seqware.webservice.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author
 * boconnor
 */
@Entity
@Table(name = "file")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "File.findAll", query = "SELECT f FROM File f"),
  @NamedQuery(name = "File.findByFileId", query = "SELECT f FROM File f WHERE f.fileId = :fileId"),
  @NamedQuery(name = "File.findByFilePath", query = "SELECT f FROM File f WHERE f.filePath = :filePath"),
  @NamedQuery(name = "File.findByMd5sum", query = "SELECT f FROM File f WHERE f.md5sum = :md5sum"),
  @NamedQuery(name = "File.findByUrl", query = "SELECT f FROM File f WHERE f.url = :url"),
  @NamedQuery(name = "File.findByUrlLabel", query = "SELECT f FROM File f WHERE f.urlLabel = :urlLabel"),
  @NamedQuery(name = "File.findByType", query = "SELECT f FROM File f WHERE f.type = :type"),
  @NamedQuery(name = "File.findByMetaType", query = "SELECT f FROM File f WHERE f.metaType = :metaType"),
  @NamedQuery(name = "File.findByDescription", query = "SELECT f FROM File f WHERE f.description = :description"),
  @NamedQuery(name = "File.findBySwAccession", query = "SELECT f FROM File f WHERE f.swAccession = :swAccession"),
  @NamedQuery(name = "File.findBySize", query = "SELECT f FROM File f WHERE f.size = :size"),
  @NamedQuery(name = "File.findBySkip", query = "SELECT f FROM File f WHERE f.skip = :skip")})
public class File implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "file_id")
  private Integer fileId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2147483647)
  @Column(name = "file_path")
  private String filePath;
  @Size(max = 2147483647)
  @Column(name = "md5sum")
  private String md5sum;
  @Size(max = 2147483647)
  @Column(name = "url")
  private String url;
  @Size(max = 2147483647)
  @Column(name = "url_label")
  private String urlLabel;
  @Size(max = 2147483647)
  @Column(name = "type")
  private String type;
  @Size(max = 2147483647)
  @Column(name = "meta_type")
  private String metaType;
  @Size(max = 2147483647)
  @Column(name = "description")
  private String description;
  @Column(name = "sw_accession")
  private Integer swAccession;
  @Column(name = "size")
  private BigInteger size;
  @Column(name = "skip")
  private Boolean skip;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "fileId")
  private Collection<FileAttribute> fileAttributeCollection;
  @JoinColumn(name = "owner_id", referencedColumnName = "registration_id")
  @ManyToOne
  private Registration ownerId;
  @JoinColumn(name = "file_type_id", referencedColumnName = "file_type_id")
  @ManyToOne
  private FileType fileTypeId;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "fileId")
  private Collection<ShareFile> shareFileCollection;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "fileId")
  private Collection<ProcessingFiles> processingFilesCollection;

  public File() {
  }

  public File(Integer fileId) {
    this.fileId = fileId;
  }

  public File(Integer fileId, String filePath) {
    this.fileId = fileId;
    this.filePath = filePath;
  }

  public Integer getFileId() {
    return fileId;
  }

  public void setFileId(Integer fileId) {
    this.fileId = fileId;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getMd5sum() {
    return md5sum;
  }

  public void setMd5sum(String md5sum) {
    this.md5sum = md5sum;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUrlLabel() {
    return urlLabel;
  }

  public void setUrlLabel(String urlLabel) {
    this.urlLabel = urlLabel;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMetaType() {
    return metaType;
  }

  public void setMetaType(String metaType) {
    this.metaType = metaType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getSwAccession() {
    return swAccession;
  }

  public void setSwAccession(Integer swAccession) {
    this.swAccession = swAccession;
  }

  public BigInteger getSize() {
    return size;
  }

  public void setSize(BigInteger size) {
    this.size = size;
  }

  public Boolean getSkip() {
    return skip;
  }

  public void setSkip(Boolean skip) {
    this.skip = skip;
  }

  @XmlTransient
  public Collection<FileAttribute> getFileAttributeCollection() {
    return fileAttributeCollection;
  }

  public void setFileAttributeCollection(Collection<FileAttribute> fileAttributeCollection) {
    this.fileAttributeCollection = fileAttributeCollection;
  }

  public Registration getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Registration ownerId) {
    this.ownerId = ownerId;
  }

  public FileType getFileTypeId() {
    return fileTypeId;
  }

  public void setFileTypeId(FileType fileTypeId) {
    this.fileTypeId = fileTypeId;
  }

  @XmlTransient
  public Collection<ShareFile> getShareFileCollection() {
    return shareFileCollection;
  }

  public void setShareFileCollection(Collection<ShareFile> shareFileCollection) {
    this.shareFileCollection = shareFileCollection;
  }

  @XmlTransient
  public Collection<ProcessingFiles> getProcessingFilesCollection() {
    return processingFilesCollection;
  }

  public void setProcessingFilesCollection(Collection<ProcessingFiles> processingFilesCollection) {
    this.processingFilesCollection = processingFilesCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (fileId != null ? fileId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof File)) {
      return false;
    }
    File other = (File) object;
    if ((this.fileId == null && other.fileId != null) || (this.fileId != null && !this.fileId.equals(other.fileId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "io.seqware.webservice.model.File[ fileId=" + fileId + " ]";
  }
  
}
