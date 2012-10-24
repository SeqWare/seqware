package com.github.seqware.queryengine.util;

import java.util.Iterator;

/**
 * Iterator implementation that deals with database iterators that require explicit closing.
 *
 * Uses a Delegation Pattern to call implementation (*Impl) methods for specific database implementations.
 *
 * @author boconnor
 * @author jbaran
 * @version $Id: $Id
 */
public abstract class SeqWareIterator<T> implements Iterator<T> {

    /**
     * True if the database iterator has been closed, or false when it is still open.
     */
    private boolean closed = false;

    /*
      So the Oracle Collection's iterator class provides: hasNext(), next(), remove()

      These are methods related to Berkeley DB's (http://docs.oracle.com/cd/E17276_01/html/java/com/sleepycat/collections/StoredIterator.html):
      public void close() throws Exception;
      public int getCount() throws Exception;
      // FIXME: is there a way I don't have to expose this?
      // FIXME: this should be wrapped by a SeqWare object
      public Cursor getCursor() throws Exception;
      // FIXME: this is a BerkeleyDB specific method, try to abstract away!
      public Object nextSecondaryKey() throws UnsupportedEncodingException;
     */

    /**
     * Closes the database iterator.
     */
    public abstract void closeImpl();

    /**
     * Implementation of hasNext().
     *
     * @return a boolean.
     */
    protected abstract boolean hasNextImpl();

    /**
     * Implementation of next().
     *
     * @return a T object.
     */
    protected abstract T nextImpl();

    /**
     * {@inheritDoc}
     *
     * Super method for dealing with database iterator specifics, such as closing the DB-iterator when no more data is available.
     */
    @Override
    public final boolean hasNext() {
        if (!this.hasNextImpl() && !this.closed) {
            this.closed = true;
            this.closeImpl();
        }

        return this.hasNextImpl();
    }

    /**
     * {@inheritDoc}
     *
     * Super method for dealing with database iterator specifics, such as closing the DB-iterator when no more data is available.
     */
    @Override
    public final T next() {
        this.hasNext(); // Ensures that the iterator is closed -- if no more elements are present.

        return this.nextImpl();
    }

    /**
     * {@inheritDoc}
     *
     * Removal of subjects cannot be done implicitly from an iterator -- this method is not supported.
     */
    @Override
    public final void remove() {
        throw new UnsupportedOperationException("Removal is not supported through iterators.");
    }
}
