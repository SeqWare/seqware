/**
 *
 */
package com.github.seqware.queryengine.system.importers;

import java.util.concurrent.Semaphore;

/**
 * This looks like a class to control concurrency for multiple ImportWorkers,
 * one per input file
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
