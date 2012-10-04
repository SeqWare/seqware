package net.sourceforge.seqware.pipeline.plugins.filelinker;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileLinkerLine {

   private static final Logger log = LoggerFactory.getLogger(FileLinkerLine.class);

   private String sequencerRun;
   private String sample;
   private Integer lane;
   private Integer seqwareAccession;
   private String fileStatus;
   private String mimeType;
   private String filename;
   private Integer size;
   private String md5sum;

   public String getSequencerRun() {
      return sequencerRun;
   }

   public void setSequencerRun(String sequencerRun) {
      this.sequencerRun = sequencerRun;
   }

   public String getSample() {
      return sample;
   }

   public void setSample(String sample) {
      this.sample = sample;
   }

   public Integer getLane() {
      return lane;
   }

   public void setLane(Integer lane) {
      this.lane = lane;
   }

   public Integer getSeqwareAccession() {
      return seqwareAccession;
   }

   public void setSeqwareAccession(Integer seqwareAccession) {
      this.seqwareAccession = seqwareAccession;
   }

   public String getFileStatus() {
      return fileStatus;
   }

   public void setFileStatus(String fileStatus) {
      this.fileStatus = fileStatus;
   }

   public String getMimeType() {
      return mimeType;
   }

   public void setMimeType(String mimeType) {
      this.mimeType = mimeType;
   }

   public String getFilename() {
      return filename;
   }

   public void setFilename(String filename) {
      this.filename = filename;
   }

   public Integer getSize() {
      return size;
   }

   public void setSize(Integer size) {
      this.size = size;
   }

   public String getMd5sum() {
      return md5sum;
   }

   public void setMd5sum(String md5sum) {
      this.md5sum = md5sum;
   }

   public void setLaneString(String laneString) {
      if (!StringUtils.isBlank(laneString)) {
         try {
            setLane(Integer.parseInt(laneString));
         } catch (NumberFormatException e) {
            log.error("The lane [{}] is not a valid integer value. {}", laneString, e);
         }
      }
   }

   public void setSeqwareAccessionString(String seqwareAccessionString) {
      if (!StringUtils.isBlank(seqwareAccessionString)) {
         try {
            setSeqwareAccession(Integer.parseInt(seqwareAccessionString));
         } catch (NumberFormatException e) {
            log.error("The SeqWare accession number [{}] is not a valid integer value. {}", seqwareAccessionString, e);
         }
      }
   }

   public void setSizeString(String sizeString) {
      if (!StringUtils.isBlank(sizeString)) {
         try {
            setSize(Integer.parseInt(sizeString));
         } catch (NumberFormatException e) {
            log.error("The size [{}] is not a valid integer value. {}", sizeString, e);
         }
      }
   }

   public boolean hasRequiredValues() {
      return lane != null && seqwareAccession != null && !StringUtils.isBlank(mimeType) && !StringUtils.isBlank(filename);
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("FileLinkerLine:{");
      sb.append("sequencerRun=").append(sequencerRun);
      sb.append(", sample=").append(sample);
      sb.append(", lane=").append(lane);
      sb.append(", seqwareAccession=").append(seqwareAccession);
      sb.append(", fileStatus=").append(fileStatus);
      sb.append(", mimeType=").append(mimeType);
      sb.append(", filename=").append(filename);
      sb.append(", size=").append(size);
      sb.append(", md5sum=").append(md5sum);
      sb.append("}");
      return sb.toString();
   }


}
