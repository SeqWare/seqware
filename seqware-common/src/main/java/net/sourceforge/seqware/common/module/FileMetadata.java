package net.sourceforge.seqware.common.module;

import java.io.File;

/**
 *
 * This is a simple data structure to represent a file and it's metadata. It
 * should roughly match the DB schema. FIXME: Instead of doing this, should be
 * using something like Hibernate to represent database schema in objects?
 *
 * @author jmendler
 * @version $Id: $Id
 */
public class FileMetadata {
  String url;
  String urlLabel;
  String filePath;
  String type;
  String metaType;
  String description;

  // Default constructor initializes everything to empty strings,
  /**
   * <p>Constructor for FileMetadata.</p>
   */
  public FileMetadata() {
    filePath = new String();
    type = new String();
    metaType = new String();
    description = new String();
  }

  // Another Constructor to take the minimum
  /**
   * <p>Constructor for FileMetadata.</p>
   *
   * @param filePath_start a {@link java.lang.String} object.
   * @param metaType_start a {@link java.lang.String} object.
   */
  public FileMetadata(String filePath_start, String metaType_start) {
    filePath = filePath_start;
    metaType = metaType_start;
  }

  // Another Constructor to populate as desired
  /**
   * <p>Constructor for FileMetadata.</p>
   *
   * @param filePath_start a {@link java.lang.String} object.
   * @param type_start a {@link java.lang.String} object.
   * @param metaType_start a {@link java.lang.String} object.
   * @param description_start a {@link java.lang.String} object.
   */
  public FileMetadata(String filePath_start, String type_start, String metaType_start, String description_start) {
    filePath = filePath_start;
    type = type_start;
    metaType = metaType_start;
    description = description_start;
  }

  /**
   * <p>Getter for the field <code>filePath</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getFilePath() {
    return filePath;
  }

  /**
   * <p>prependToFilePath.</p>
   *
   * @param pathToPrepend a {@link java.lang.String} object.
   */
  public void prependToFilePath(String pathToPrepend) {
    if (pathToPrepend.endsWith("/") || this.getFilePath().startsWith("/")) {
      this.filePath = pathToPrepend + this.getFilePath();
    } else {
      this.filePath = pathToPrepend + "/" + this.getFilePath();
    }
  }

  /**
   * <p>Setter for the field <code>filePath</code>.</p>
   *
   * @param filePath a {@link java.lang.String} object.
   */
  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  /**
   * <p>Setter for the field <code>filePath</code>.</p>
   *
   * @param file a {@link java.io.File} object.
   */
  public void setFilePath(File file) {
    this.filePath = file.getAbsolutePath();
  }

  /**
   * <p>Getter for the field <code>type</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getType() {
    return type;
  }

  /**
   * <p>Setter for the field <code>type</code>.</p>
   *
   * @param type a {@link java.lang.String} object.
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * <p>Getter for the field <code>metaType</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getMetaType() {
    return metaType;
  }

  /**
   * <p>Setter for the field <code>metaType</code>.</p>
   *
   * @param metaType a {@link java.lang.String} object.
   */
  public void setMetaType(String metaType) {
    this.metaType = metaType;
  }

  /**
   * <p>Getter for the field <code>description</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getDescription() {
    return description;
  }

  /**
   * <p>Setter for the field <code>description</code>.</p>
   *
   * @param description a {@link java.lang.String} object.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * <p>Getter for the field <code>url</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getUrl() {
    return url;
  }

  /**
   * <p>Setter for the field <code>url</code>.</p>
   *
   * @param url a {@link java.lang.String} object.
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * <p>Getter for the field <code>urlLabel</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getUrlLabel() {
    return urlLabel;
  }

  /**
   * <p>Setter for the field <code>urlLabel</code>.</p>
   *
   * @param urlLabel a {@link java.lang.String} object.
   */
  public void setUrlLabel(String urlLabel) {
    this.urlLabel = urlLabel;
  }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "FileMetadata{" + "url=" + url + ", urlLabel=" + urlLabel + ", filePath=" + filePath + ", type=" + type + ", metaType=" + metaType + ", description=" + description + '}';
    }
  
  
}
