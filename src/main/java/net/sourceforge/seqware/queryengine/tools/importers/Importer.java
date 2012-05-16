/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.importers;

import java.util.concurrent.Semaphore;

/**
 * @author boconnor
 *
 */
public class Importer {
  
  protected Semaphore available = null;
  protected int maxThreads = 0;
  
  public Importer(int maxThreads) {
    this.maxThreads = maxThreads;
    this.available = new Semaphore(maxThreads, true);
  }
  
  public Importer() {
    // nothing
  }

  public void getLock() throws InterruptedException {
    available.acquire();
  }
  
  public void releaseLock() {
    available.release();
  }
  
}
