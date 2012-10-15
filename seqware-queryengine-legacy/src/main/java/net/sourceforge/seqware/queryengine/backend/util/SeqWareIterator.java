/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.util;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import com.sleepycat.db.Cursor;

/**
 * <p>SeqWareIterator interface.</p>
 *
 * @author boconnor
 * TODO:
 * - need to make this support generics
 * - need to hide anything from BerkeleyDB
 * @version $Id: $Id
 */
public interface SeqWareIterator extends Iterator {

	// so the iterator class provides: hasNext(), next(), remove()
    /**
     * <p>close.</p>
     *
     * @throws java.lang.Exception if any.
     */
    public void close() throws Exception;
    /**
     * <p>getCount.</p>
     *
     * @return a int.
     * @throws java.lang.Exception if any.
     */
    public int getCount() throws Exception;
    // FIXME: is there a way I don't have to expose this?
    // FIXME: this should be wrapped by a SeqWare object
    /**
     * <p>getCursor.</p>
     *
     * @return a {@link com.sleepycat.db.Cursor} object.
     * @throws java.lang.Exception if any.
     */
    public Cursor getCursor() throws Exception;
    // FIXME: this is a BerkeleyDB specific method, try to abstract away!
    /**
     * <p>nextSecondaryKey.</p>
     *
     * @return a {@link java.lang.Object} object.
     * @throws java.io.UnsupportedEncodingException if any.
     */
    public Object nextSecondaryKey() throws UnsupportedEncodingException;
}
