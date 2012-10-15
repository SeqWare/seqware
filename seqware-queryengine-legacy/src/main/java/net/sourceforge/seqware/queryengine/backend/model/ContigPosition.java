/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.model;

/**
 * <p>ContigPosition class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class ContigPosition extends Model {
  private String contig;
  private int startPosition;
  private int stopPosition;
  
  /**
   * <p>Getter for the field <code>contig</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getContig() {
    return contig;
  }
  /**
   * <p>Setter for the field <code>contig</code>.</p>
   *
   * @param contig a {@link java.lang.String} object.
   */
  public void setContig(String contig) {
    this.contig = contig;
  }
  /**
   * <p>Getter for the field <code>startPosition</code>.</p>
   *
   * @return a int.
   */
  public int getStartPosition() {
    return startPosition;
  }
  /**
   * <p>Setter for the field <code>startPosition</code>.</p>
   *
   * @param startPosition a int.
   */
  public void setStartPosition(int startPosition) {
    this.startPosition = startPosition;
  }
  /**
   * <p>Getter for the field <code>stopPosition</code>.</p>
   *
   * @return a int.
   */
  public int getStopPosition() {
    return stopPosition;
  }
  /**
   * <p>Setter for the field <code>stopPosition</code>.</p>
   *
   * @param stopPosition a int.
   */
  public void setStopPosition(int stopPosition) {
    this.stopPosition = stopPosition;
  }
  
  
}
