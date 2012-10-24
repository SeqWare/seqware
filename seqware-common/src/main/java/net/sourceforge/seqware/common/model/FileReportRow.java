package net.sourceforge.seqware.common.model;

import java.io.Serializable;

/**
 * <p>FileReportRow class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class FileReportRow implements Serializable {

  private static final long serialVersionUID = 1L;

  private Integer rowId;
  private Study study;
  private Experiment experiment;
  private Sample sample;
  private Sample childSample;
  private IUS ius;
  private Lane lane;
  private Processing processing;
  private File file;

  /**
   * <p>Getter for the field <code>rowId</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getRowId() {
    return rowId;
  }

  /**
   * <p>Setter for the field <code>rowId</code>.</p>
   *
   * @param rowId a {@link java.lang.Integer} object.
   */
  public void setRowId(Integer rowId) {
    this.rowId = rowId;
  }

  /**
   * <p>Getter for the field <code>study</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.Study} object.
   */
  public Study getStudy() {
    return study;
  }

  /**
   * <p>Setter for the field <code>study</code>.</p>
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   */
  public void setStudy(Study study) {
    this.study = study;
  }

  /**
   * <p>Getter for the field <code>sample</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
   */
  public Sample getSample() {
    return sample;
  }

  /**
   * <p>Setter for the field <code>sample</code>.</p>
   *
   * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
   */
  public void setSample(Sample sample) {
    this.sample = sample;
  }

  /**
   * <p>Getter for the field <code>ius</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.IUS} object.
   */
  public IUS getIus() {
    return ius;
  }

  /**
   * <p>Setter for the field <code>ius</code>.</p>
   *
   * @param ius a {@link net.sourceforge.seqware.common.model.IUS} object.
   */
  public void setIus(IUS ius) {
    this.ius = ius;
  }

  /**
   * <p>Getter for the field <code>lane</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
   */
  public Lane getLane() {
    return lane;
  }

  /**
   * <p>Setter for the field <code>lane</code>.</p>
   *
   * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
   */
  public void setLane(Lane lane) {
    this.lane = lane;
  }

  /**
   * <p>Getter for the field <code>file</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.File} object.
   */
  public File getFile() {
    return file;
  }

  /**
   * <p>Setter for the field <code>file</code>.</p>
   *
   * @param file a {@link net.sourceforge.seqware.common.model.File} object.
   */
  public void setFile(File file) {
    this.file = file;
  }

  /**
   * <p>Getter for the field <code>experiment</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.Experiment} object.
   */
  public Experiment getExperiment() {
    return experiment;
  }

  /**
   * <p>Setter for the field <code>experiment</code>.</p>
   *
   * @param experiment a {@link net.sourceforge.seqware.common.model.Experiment} object.
   */
  public void setExperiment(Experiment experiment) {
    this.experiment = experiment;
  }

  /**
   * <p>Getter for the field <code>childSample</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
   */
  public Sample getChildSample() {
    return childSample;
  }

  /**
   * <p>Setter for the field <code>childSample</code>.</p>
   *
   * @param childSample a {@link net.sourceforge.seqware.common.model.Sample} object.
   */
  public void setChildSample(Sample childSample) {
    this.childSample = childSample;
  }

  /**
   * <p>Getter for the field <code>processing</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
   */
  public Processing getProcessing() {
    return processing;
  }

  /**
   * <p>Setter for the field <code>processing</code>.</p>
   *
   * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
   */
  public void setProcessing(Processing processing) {
    this.processing = processing;
  }

}
