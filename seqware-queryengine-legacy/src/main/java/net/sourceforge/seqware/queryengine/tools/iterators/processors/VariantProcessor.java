/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.iterators.processors;

import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;

/**
 * <p>VariantProcessor class.</p>
 *
 * @author boconnor
 *
 * A simple interface that defines the methods an iterator processor needs to support.
 * @version $Id: $Id
 */
public class VariantProcessor implements ProcessorInterface {
  
  String outputFilename;
  boolean includeIndels;
  boolean includeSNVs;
  int minCoverage;
  int maxCoverage;
  int minObservations;
  int minObservationsPerStrand;
  int minSNVPhred;
  int minPercent;
  Store store;
  
  /**
   * <p>Constructor for VariantProcessor.</p>
   *
   * @param outputFilename a {@link java.lang.String} object.
   * @param store a {@link net.sourceforge.seqware.queryengine.backend.store.Store} object.
   * @param includeIndels a boolean.
   * @param includeSNVs a boolean.
   * @param minCoverage a int.
   * @param maxCoverage a int.
   * @param minObservations a int.
   * @param minObservationsPerStrand a int.
   * @param minSNVPhred a int.
   * @param minPercent a int.
   */
  public VariantProcessor(String outputFilename, Store store, boolean includeIndels, boolean includeSNVs, int minCoverage, int maxCoverage, int minObservations, int minObservationsPerStrand, int minSNVPhred, int minPercent) {
    setOutputFilename(outputFilename);
    setIncludeIndels(includeIndels);
    setIncludeSNVs(includeSNVs);
    setMinCoverage(minCoverage);
    setMaxCoverage(maxCoverage);
    setMinObservations(minObservations);
    setMinObservationsPerStrand(minObservationsPerStrand);
    setMinSNVPhred(minSNVPhred);
    setMinPercent(minPercent);
    setStore(store);
  }
  
  /**
   * <p>Constructor for VariantProcessor.</p>
   */
  public VariantProcessor() {
    
  }
  
  /** {@inheritDoc} */
  public Object process (Object obj) {
    return null;
  }
  
  /** {@inheritDoc} */
  public String report(Object obj) {
    return("");
  }
  
  // generated methods

  
  /**
   * <p>Getter for the field <code>outputFilename</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getOutputFilename() {
    return outputFilename;
  }

  /**
   * <p>Getter for the field <code>store</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.queryengine.backend.store.Store} object.
   */
  public Store getStore() {
    return store;
  }

  /**
   * <p>Setter for the field <code>store</code>.</p>
   *
   * @param store a {@link net.sourceforge.seqware.queryengine.backend.store.Store} object.
   */
  public void setStore(Store store) {
    this.store = store;
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
   * <p>isIncludeIndels.</p>
   *
   * @return a boolean.
   */
  public boolean isIncludeIndels() {
    return includeIndels;
  }

  /**
   * <p>Setter for the field <code>includeIndels</code>.</p>
   *
   * @param includeIndels a boolean.
   */
  public void setIncludeIndels(boolean includeIndels) {
    this.includeIndels = includeIndels;
  }

  /**
   * <p>isIncludeSNVs.</p>
   *
   * @return a boolean.
   */
  public boolean isIncludeSNVs() {
    return includeSNVs;
  }

  /**
   * <p>Setter for the field <code>includeSNVs</code>.</p>
   *
   * @param includeSNVs a boolean.
   */
  public void setIncludeSNVs(boolean includeSNVs) {
    this.includeSNVs = includeSNVs;
  }

  /**
   * <p>Getter for the field <code>minCoverage</code>.</p>
   *
   * @return a int.
   */
  public int getMinCoverage() {
    return minCoverage;
  }

  /**
   * <p>Setter for the field <code>minCoverage</code>.</p>
   *
   * @param minCoverage a int.
   */
  public void setMinCoverage(int minCoverage) {
    this.minCoverage = minCoverage;
  }

  /**
   * <p>Getter for the field <code>maxCoverage</code>.</p>
   *
   * @return a int.
   */
  public int getMaxCoverage() {
    return maxCoverage;
  }

  /**
   * <p>Setter for the field <code>maxCoverage</code>.</p>
   *
   * @param maxCoverage a int.
   */
  public void setMaxCoverage(int maxCoverage) {
    this.maxCoverage = maxCoverage;
  }

  /**
   * <p>Getter for the field <code>minObservations</code>.</p>
   *
   * @return a int.
   */
  public int getMinObservations() {
    return minObservations;
  }

  /**
   * <p>Setter for the field <code>minObservations</code>.</p>
   *
   * @param minObservations a int.
   */
  public void setMinObservations(int minObservations) {
    this.minObservations = minObservations;
  }

  /**
   * <p>Getter for the field <code>minObservationsPerStrand</code>.</p>
   *
   * @return a int.
   */
  public int getMinObservationsPerStrand() {
    return minObservationsPerStrand;
  }

  /**
   * <p>Setter for the field <code>minObservationsPerStrand</code>.</p>
   *
   * @param minObservationsPerStrand a int.
   */
  public void setMinObservationsPerStrand(int minObservationsPerStrand) {
    this.minObservationsPerStrand = minObservationsPerStrand;
  }

  /**
   * <p>Getter for the field <code>minSNVPhred</code>.</p>
   *
   * @return a int.
   */
  public int getMinSNVPhred() {
    return minSNVPhred;
  }

  /**
   * <p>Setter for the field <code>minSNVPhred</code>.</p>
   *
   * @param minSNVPhred a int.
   */
  public void setMinSNVPhred(int minSNVPhred) {
    this.minSNVPhred = minSNVPhred;
  }

  /**
   * <p>Getter for the field <code>minPercent</code>.</p>
   *
   * @return a int.
   */
  public int getMinPercent() {
    return minPercent;
  }

  /**
   * <p>Setter for the field <code>minPercent</code>.</p>
   *
   * @param minPercent a int.
   */
  public void setMinPercent(int minPercent) {
    this.minPercent = minPercent;
  }
  
}
