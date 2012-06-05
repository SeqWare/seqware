/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.iterators.processors;

import java.util.HashMap;

import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;

/**
 * @author boconnor
 *
 * A simple interface that defines the methods an iterator processor needs to support.
 *
 */
public class CoverageProcessor implements ProcessorInterface {
  
  HashMap stats;
  String outputFilename;
  int start;
  int stop;
  String contig;
  BerkeleyDBStore store;
  
  
  public Object process (Object obj) {
    return(null);
  }
  
  public String report(Object obj) {
    return("");
  }

  public HashMap getStats() {
    return stats;
  }

  public void setStats(HashMap stats) {
    this.stats = stats;
  }

  public String getOutputFilename() {
    return outputFilename;
  }

  public void setOutputFilename(String outputFilename) {
    this.outputFilename = outputFilename;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public int getStop() {
    return stop;
  }

  public void setStop(int stop) {
    this.stop = stop;
  }

  public String getContig() {
    return contig;
  }

  public void setContig(String contig) {
    this.contig = contig;
  }

  public BerkeleyDBStore getStore() {
    return store;
  }

  public void setStore(BerkeleyDBStore store) {
    this.store = store;
  }

}
