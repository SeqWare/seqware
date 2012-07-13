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
