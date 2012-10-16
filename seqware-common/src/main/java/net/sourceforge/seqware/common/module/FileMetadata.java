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
   private String md5sum;
   private Long size;

   // Default constructor initializes everything to empty strings,
   public FileMetadata() {
      filePath = new String();
      type = new String();
      metaType = new String();
      description = new String();
   }

   // Another Constructor to take the minimum
   public FileMetadata(String filePath_start, String metaType_start) {
      filePath = filePath_start;
      metaType = metaType_start;
   }

   // Another Constructor to populate as desired
   public FileMetadata(String filePath_start, String type_start, String metaType_start, String description_start) {
      filePath = filePath_start;
      type = type_start;
      metaType = metaType_start;
      description = description_start;
   }

   public String getFilePath() {
      return filePath;
   }

   public void prependToFilePath(String pathToPrepend) {
      if (pathToPrepend.endsWith("/") || this.getFilePath().startsWith("/")) {
         this.filePath = pathToPrepend + this.getFilePath();
      } else {
         this.filePath = pathToPrepend + "/" + this.getFilePath();
      }
   }

   public void setFilePath(String filePath) {
      this.filePath = filePath;
   }

   public void setFilePath(File file) {
      this.filePath = file.getAbsolutePath();
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

   @Override
   public String toString() {
      return "FileMetadata{" + "url=" + url + ", urlLabel=" + urlLabel + ", filePath=" + filePath + ", type=" + type
            + ", metaType=" + metaType + ", description=" + description + ", md5sum=" + md5sum + ", size=" + size + '}';
   }

   public String getMd5sum() {
      return md5sum;
   }

   public void setMd5sum(String md5sum) {
      this.md5sum = md5sum;
   }

   public Long getSize() {
      return size;
   }

   public void setSize(Long size) {
      this.size = size;
   }
}
