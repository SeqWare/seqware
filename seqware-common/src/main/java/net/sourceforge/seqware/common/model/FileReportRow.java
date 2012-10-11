package net.sourceforge.seqware.common.model;

import java.io.Serializable;

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

  public Integer getRowId() {
    return rowId;
  }

  public void setRowId(Integer rowId) {
    this.rowId = rowId;
  }

  public Study getStudy() {
    return study;
  }

  public void setStudy(Study study) {
    this.study = study;
  }

  public Sample getSample() {
    return sample;
  }

  public void setSample(Sample sample) {
    this.sample = sample;
  }

  public IUS getIus() {
    return ius;
  }

  public void setIus(IUS ius) {
    this.ius = ius;
  }

  public Lane getLane() {
    return lane;
  }

  public void setLane(Lane lane) {
    this.lane = lane;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public Experiment getExperiment() {
    return experiment;
  }

  public void setExperiment(Experiment experiment) {
    this.experiment = experiment;
  }

  public Sample getChildSample() {
    return childSample;
  }

  public void setChildSample(Sample childSample) {
    this.childSample = childSample;
  }

  public Processing getProcessing() {
    return processing;
  }

  public void setProcessing(Processing processing) {
    this.processing = processing;
  }

}
