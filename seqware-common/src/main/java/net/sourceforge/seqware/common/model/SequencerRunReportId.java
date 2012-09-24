package net.sourceforge.seqware.common.model;

public class SequencerRunReportId {

  private Study study;
  private Sample childSample;
  private SequencerRun sequencerRun;
  private Lane lane;
  private IUS ius;

  public Study getStudy() {
    return study;
  }

  public void setStudy(Study study) {
    this.study = study;
  }

  public Sample getChildSample() {
    return childSample;
  }

  public void setChildSample(Sample childSample) {
    this.childSample = childSample;
  }

  public SequencerRun getSequencerRun() {
    return sequencerRun;
  }

  public void setSequencerRun(SequencerRun sequencerRun) {
    this.sequencerRun = sequencerRun;
  }

  public Lane getLane() {
    return lane;
  }

  public void setLane(Lane lane) {
    this.lane = lane;
  }

  public IUS getIus() {
    return ius;
  }

  public void setIus(IUS ius) {
    this.ius = ius;
  }
}
