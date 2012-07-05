package com.github.seqware.queryengine.util;

import java.util.Iterator;
/**
 * An in-memory iterator for unit testing of SeqWareIterator.
 *
 * @author jbaran
 */
public class InMemoryIterator<T> extends SeqWareIterator<T> {
    private Iterator<T> iterator;

    /**
     * Creates a new in-memory SeqWareIterator implementation.
     *
     * @param iterator Iterator that goes through the actual data.
     */
    public InMemoryIterator(Iterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public void closeImpl() {
    }

    @Override
    protected boolean hasNextImpl() {
        return iterator.hasNext();
    }

    @Override
    protected T nextImpl() {
        return iterator.next();
    }
}
