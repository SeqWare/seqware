package net.sourceforge.seqware.common.model;

public class SampleReportRow {

  private Integer rowId;
  private Study study;
  private Sample childSample;
  private Workflow workflow;
  private SequencerRun sequencerRun;
  private Lane lane;
  private IUS ius;
  private String status;

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

  public Sample getChildSample() {
    return childSample;
  }

  public void setChildSample(Sample childSample) {
    this.childSample = childSample;
  }

  public Workflow getWorkflow() {
    return workflow;
  }

  public void setWorkflow(Workflow workflow) {
    this.workflow = workflow;
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

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

}
