/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.iterators.processors;

import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;

/**
 * @author boconnor
 *
 * A simple interface that defines the methods an iterator processor needs to support.
 *
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
  
  public VariantProcessor() {
    
  }
  
  public Object process (Object obj) {
    return null;
  }
  
  public String report(Object obj) {
    return("");
  }
  
  // generated methods

  
  public String getOutputFilename() {
    return outputFilename;
  }

  public Store getStore() {
    return store;
  }

  public void setStore(Store store) {
    this.store = store;
  }

  public void setOutputFilename(String outputFilename) {
    this.outputFilename = outputFilename;
  }

  public boolean isIncludeIndels() {
    return includeIndels;
  }

  public void setIncludeIndels(boolean includeIndels) {
    this.includeIndels = includeIndels;
  }

  public boolean isIncludeSNVs() {
    return includeSNVs;
  }

  public void setIncludeSNVs(boolean includeSNVs) {
    this.includeSNVs = includeSNVs;
  }

  public int getMinCoverage() {
    return minCoverage;
  }

  public void setMinCoverage(int minCoverage) {
    this.minCoverage = minCoverage;
  }

  public int getMaxCoverage() {
    return maxCoverage;
  }

  public void setMaxCoverage(int maxCoverage) {
    this.maxCoverage = maxCoverage;
  }

  public int getMinObservations() {
    return minObservations;
  }

  public void setMinObservations(int minObservations) {
    this.minObservations = minObservations;
  }

  public int getMinObservationsPerStrand() {
    return minObservationsPerStrand;
  }

  public void setMinObservationsPerStrand(int minObservationsPerStrand) {
    this.minObservationsPerStrand = minObservationsPerStrand;
  }

  public int getMinSNVPhred() {
    return minSNVPhred;
  }

  public void setMinSNVPhred(int minSNVPhred) {
    this.minSNVPhred = minSNVPhred;
  }

  public int getMinPercent() {
    return minPercent;
  }

  public void setMinPercent(int minPercent) {
    this.minPercent = minPercent;
  }
  
}
