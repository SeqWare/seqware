/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Coverage class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Coverage extends LocatableModel {

  protected HashMap<Integer, Integer> coverage = new HashMap<Integer, Integer>();
  protected int count = 0;
  protected int sum = 0;

  // Custom Methods
  /**
   * <p>putCoverage.</p>
   *
   * @param location a {@link java.lang.Integer} object.
   * @param count a {@link java.lang.Integer} object.
   */
  public void putCoverage(Integer location, Integer count) {
    coverage.put(location, count);
    count++;
  }

  /**
   * <p>Getter for the field <code>coverage</code>.</p>
   *
   * @param location a {@link java.lang.Integer} object.
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getCoverage(Integer location) {
    return (coverage.get(location));
  }

  private void initialize() {
    coverage = new HashMap<Integer, Integer>();
    count = 0;
    sum = 0;
    setContig("");
    setStartPosition(0);
    setStopPosition(0);
  }

  // Generated Methods

  /**
   * <p>Getter for the field <code>coverage</code>.</p>
   *
   * @return a {@link java.util.HashMap} object.
   */
  public HashMap<Integer, Integer> getCoverage() {
    return coverage;
  }

  /**
   * <p>Getter for the field <code>count</code>.</p>
   *
   * @return a int.
   */
  public int getCount() {
    return count;
  }

  /**
   * <p>Setter for the field <code>count</code>.</p>
   *
   * @param count a int.
   */
  public void setCount(int count) {
    this.count = count;
  }

  /**
   * <p>Setter for the field <code>coverage</code>.</p>
   *
   * @param coverage a {@link java.util.HashMap} object.
   */
  public void setCoverage(HashMap<Integer, Integer> coverage) {
    this.coverage = coverage;
  }

  /**
   * <p>Getter for the field <code>sum</code>.</p>
   *
   * @return a int.
   */
  public int getSum() {
    return sum;
  }

  /**
   * <p>Setter for the field <code>sum</code>.</p>
   *
   * @param sum a int.
   */
  public void setSum(int sum) {
    this.sum = sum;
  }


}
