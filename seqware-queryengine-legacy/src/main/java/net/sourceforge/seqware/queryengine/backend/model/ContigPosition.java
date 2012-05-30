/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.model;

/**
 * @author boconnor
 *
 */
public class ContigPosition extends Model {
  private String contig;
  private int startPosition;
  private int stopPosition;
  
  public String getContig() {
    return contig;
  }
  public void setContig(String contig) {
    this.contig = contig;
  }
  public int getStartPosition() {
    return startPosition;
  }
  public void setStartPosition(int startPosition) {
    this.startPosition = startPosition;
  }
  public int getStopPosition() {
    return stopPosition;
  }
  public void setStopPosition(int stopPosition) {
    this.stopPosition = stopPosition;
  }
  
  
}
