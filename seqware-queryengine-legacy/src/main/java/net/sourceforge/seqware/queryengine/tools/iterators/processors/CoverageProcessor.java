/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.iterators.processors;

import java.util.HashMap;

import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;

/**
 * <p>CoverageProcessor class.</p>
 *
 * @author boconnor
 *
 * A simple interface that defines the methods an iterator processor needs to support.
 * @version $Id: $Id
 */
public class CoverageProcessor implements ProcessorInterface {
  
  HashMap stats;
  String outputFilename;
  int start;
  int stop;
  String contig;
  BerkeleyDBStore store;
  
  
  /** {@inheritDoc} */
  public Object process (Object obj) {
    return(null);
  }
  
  /** {@inheritDoc} */
  public String report(Object obj) {
    return("");
  }

  /**
   * <p>Getter for the field <code>stats</code>.</p>
   *
   * @return a {@link java.util.HashMap} object.
   */
  public HashMap getStats() {
    return stats;
  }

  /**
   * <p>Setter for the field <code>stats</code>.</p>
   *
   * @param stats a {@link java.util.HashMap} object.
   */
  public void setStats(HashMap stats) {
    this.stats = stats;
  }

  /**
   * <p>Getter for the field <code>outputFilename</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getOutputFilename() {
    return outputFilename;
  }

  /**
   * <p>Setter for the field <code>outputFilename</code>.</p>
   *
   * @param outputFilename a {@link java.lang.String} object.
   */
  public void setOutputFilename(String outputFilename) {
    this.outputFilename = outputFilename;
  }

  /**
   * <p>Getter for the field <code>start</code>.</p>
   *
   * @return a int.
   */
  public int getStart() {
    return start;
  }

  /**
   * <p>Setter for the field <code>start</code>.</p>
   *
   * @param start a int.
   */
  public void setStart(int start) {
    this.start = start;
  }

  /**
   * <p>Getter for the field <code>stop</code>.</p>
   *
   * @return a int.
   */
  public int getStop() {
    return stop;
  }

  /**
   * <p>Setter for the field <code>stop</code>.</p>
   *
   * @param stop a int.
   */
  public void setStop(int stop) {
    this.stop = stop;
  }

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
   * <p>Getter for the field <code>store</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore} object.
   */
  public BerkeleyDBStore getStore() {
    return store;
  }

  /**
   * <p>Setter for the field <code>store</code>.</p>
   *
   * @param store a {@link net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore} object.
   */
  public void setStore(BerkeleyDBStore store) {
    this.store = store;
  }

}
