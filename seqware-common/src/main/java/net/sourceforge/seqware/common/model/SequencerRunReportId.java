package net.sourceforge.seqware.common.model;

/**
 * <p>SequencerRunReportId class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SequencerRunReportId {

  private Study study;
  private Sample childSample;
  private SequencerRun sequencerRun;
  private Lane lane;
  private IUS ius;

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
   * <p>Getter for the field <code>sequencerRun</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   */
  public SequencerRun getSequencerRun() {
    return sequencerRun;
  }

  /**
   * <p>Setter for the field <code>sequencerRun</code>.</p>
   *
   * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
   */
  public void setSequencerRun(SequencerRun sequencerRun) {
    this.sequencerRun = sequencerRun;
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
}
