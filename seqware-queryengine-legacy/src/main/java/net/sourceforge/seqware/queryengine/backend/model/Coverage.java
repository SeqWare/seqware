/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author boconnor
 * 
 * 
 */
public class Coverage extends LocatableModel {

  protected HashMap<Integer, Integer> coverage = new HashMap<Integer, Integer>();
  protected int count = 0;
  protected int sum = 0;

  // Custom Methods
  public void putCoverage(Integer location, Integer count) {
    coverage.put(location, count);
    count++;
  }

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

  public HashMap<Integer, Integer> getCoverage() {
    return coverage;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public void setCoverage(HashMap<Integer, Integer> coverage) {
    this.coverage = coverage;
  }

  public int getSum() {
    return sum;
  }

  public void setSum(int sum) {
    this.sum = sum;
  }


}
