package net.sourceforge.seqware.common.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "file_attribute", uniqueConstraints = { @UniqueConstraint(columnNames = { "file_id", "tag", "value" }) })
public class FileAttribute implements Attribute {

  @Id
  @SequenceGenerator(name = "file_attribute_id_seq_gen", sequenceName = "file_attribute_id_seq")
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "file_attribute_id_seq_gen")
  @Column(name = "file_attribute_id")
  private Integer fileAttributeId;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "file_id", nullable = false)
  private File file;

  @Column(nullable = false)
  private String tag;

  @Column(nullable = false)
  private String value;

  private String unit;

  public FileAttribute() {

  }

  public FileAttribute(Integer fileAttributeId) {
    this.fileAttributeId = fileAttributeId;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  @Override
  public String getTag() {
    return tag;
  }

  @Override
  public void setTag(String tag) {
    this.tag = tag;
  }

  @Override
  public String getValue() {
    return value;
  }

  @Override
  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String getUnit() {
    return unit;
  }

  @Override
  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Integer getFileAttributeId() {
    return fileAttributeId;
  }

  public void setFileAttributeId(Integer fileAttributeId) {
    this.fileAttributeId = fileAttributeId;
  }

}
