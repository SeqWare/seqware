package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.security.PermissionsAware;
import net.sourceforge.seqware.common.util.jsontools.JsonUtil;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

public class File implements Serializable, Comparable<File>, PermissionsAware {

  private static final long serialVersionUID = 3681322115923390568L;
  private Integer fileId;
  private String filePath;
  private String type;
  private String metaType;
  private String description;
  private Integer swAccession;
  private Boolean isSelected = false;
  private Registration owner;
  private String url;
  private String urlLabel;
  private String md5sum;
  private FileType fileType;
  private Set<Processing> processings = new TreeSet<Processing>();
  private Set<FileAttribute> fileAttributes = new TreeSet<FileAttribute>();
  private Long size;

  public Set<Processing> getProcessings() {
    return processings;
  }

  public void setProcessings(Set<Processing> processing) {
    this.processings = processing;
  }

  public File() {
    super();
  }

  @Override
  public int compareTo(File that) {
    return (that.getFileId().compareTo(this.getFileId()));
  }

  @Override
  public String toString() {
    return "File{" + "fileId=" + fileId + ", filePath=" + filePath + ", type=" + type + ", metaType=" + metaType
        + ", description=" + description + ", swAccession=" + swAccession + ", isSelected=" + isSelected + ", owner="
        + owner + ", url=" + url + ", urlLabel=" + urlLabel + ", md5sum=" + md5sum + ", fileType=" + fileType + '}';
  }

  @Override
  public boolean equals(Object other) {
    if ((this == other)) {
      return true;
    }
    if (!(other instanceof File)) {
      return false;
    }
    File castOther = (File) other;
    return new EqualsBuilder().append(this.getFileId(), castOther.getFileId()).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(getFileId()).toHashCode();
  }

  public String getFileName() {
    return (getFilePath().substring(this.getFilePath().lastIndexOf('/') + 1));
  }

  public String getJsonEscapeFileName() {
    return JsonUtil.forJSON(getFileName());
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

  public String getJsonEscapeDescription() {
    return JsonUtil.forJSON(description);
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

  public Registration getOwner() {
    return owner;
  }

  public void setOwner(Registration owner) {
    this.owner = owner;
  }

  public Boolean getIsSelected() {
    return isSelected;
  }

  public void setIsSelected(Boolean isSelected) {
    this.isSelected = isSelected;
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

  public String getMd5sum() {
    return md5sum;
  }

  public void setMd5sum(String md5sum) {
    this.md5sum = md5sum;
  }

  public FileType getFileType() {
    return fileType;
  }

  public void setFileType(FileType fileType) {
    this.fileType = fileType;
  }

  public static File cloneFromDB(int fileId) throws SQLException {
    File file = null;
    try {
      ResultSet rs = DBAccess.get().executeQuery("SELECT * FROM file WHERE file_id=" + fileId);
      if (rs.next()) {
        file = new File();
        file.setFileId(rs.getInt("file_id"));
        file.setFilePath(rs.getString("file_path"));
        file.setMd5sum(rs.getString("md5sum"));
        file.setUrl(rs.getString("url"));
        file.setUrlLabel(rs.getString("url_label"));
        file.setType(rs.getString("type"));
        file.setMetaType(rs.getString("meta_type"));
        file.setDescription(rs.getString("description"));
        file.setSwAccession(rs.getInt("sw_accession"));
      }
    } finally {
      DBAccess.close();
    }

    return file;
  }

  @Override
  public boolean givesPermission(Registration registration) {
    boolean hasPermission = true;
    if (processings != null) {
      for (Processing p : processings) {
        if (!p.givesPermission(registration)) {
          hasPermission = false;
          break;
        }
      }
    } else {// orphaned File
      if (registration.equals(this.owner) || registration.isLIMSAdmin()) {
        Logger.getLogger(File.class).warn("Modifying Orphan File: " + toString());
        hasPermission = true;

      } else if (owner == null) {
        Logger.getLogger(File.class).warn("File has no owner! Modifying Orphan File: " + toString());
        hasPermission = true;
      } else {
        Logger.getLogger(File.class).warn("Not modifying Orphan File: " + toString());
        hasPermission = false;
      }
    }
    if (!hasPermission) {
      Logger.getLogger(File.class).info("File does not give permission");
      throw new SecurityException("User " + registration.getEmailAddress() + " does not have permission to modify "
          + toString());
    }
    return hasPermission;
  }

  public Set<FileAttribute> getFileAttributes() {
    return fileAttributes;
  }

  public void setFileAttributes(Set<FileAttribute> fileAttributes) {
    this.fileAttributes = fileAttributes;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

}
