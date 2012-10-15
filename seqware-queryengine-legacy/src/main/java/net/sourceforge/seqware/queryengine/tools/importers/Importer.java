/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.importers;

import java.util.concurrent.Semaphore;

/**
 * <p>Importer class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Importer {
  
  protected Semaphore available = null;
  protected int maxThreads = 0;
  
  /**
   * <p>Constructor for Importer.</p>
   *
   * @param maxThreads a int.
   */
  public Importer(int maxThreads) {
    this.maxThreads = maxThreads;
    this.available = new Semaphore(maxThreads, true);
  }
  
  /**
   * <p>Constructor for Importer.</p>
   */
  public Importer() {
    // nothing
  }

  /**
   * <p>getLock.</p>
   *
   * @throws java.lang.InterruptedException if any.
   */
  public void getLock() throws InterruptedException {
    available.acquire();
  }
  
  /**
   * <p>releaseLock.</p>
   */
  public void releaseLock() {
    available.release();
  }
  
}
