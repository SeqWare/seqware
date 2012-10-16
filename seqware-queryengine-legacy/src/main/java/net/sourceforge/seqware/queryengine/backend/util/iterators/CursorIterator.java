/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.util.iterators;

import java.io.UnsupportedEncodingException;

import net.sourceforge.seqware.queryengine.backend.model.Model;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.db.Cursor;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;

/**
 * <p>CursorIterator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class CursorIterator implements SeqWareIterator {

  private Cursor cursor = null;
  DatabaseEntry key = new DatabaseEntry();
  DatabaseEntry value = new DatabaseEntry();
  Model model = null;
  TupleBinding tb = null;
  OperationStatus status = null;
  boolean first = true;

  /**
   * <p>Constructor for CursorIterator.</p>
   *
   * @param cursor a {@link com.sleepycat.db.Cursor} object.
   * @param tb a {@link com.sleepycat.bind.tuple.TupleBinding} object.
   * @throws java.lang.Exception if any.
   */
  public CursorIterator(Cursor cursor, TupleBinding tb) throws Exception {
    this.cursor = cursor;
    this.tb = tb;
  }

  /**
   * <p>hasNext.</p>
   *
   * @return a boolean.
   */
  public boolean hasNext() {
    try {
      if (first) {
        // get the first record
        status = cursor.getFirst(key, value, LockMode.READ_UNCOMMITTED);
        first = false;
      } else {
        // get the next record    
        status = cursor.getNextDup(key, value, LockMode.READ_UNCOMMITTED);
        // if this isn't successful then no more duplicates and time to move to next key
        // or there are no more entries in the db. If I don't care about exact match for
        // this key try getNext
        if (status != OperationStatus.SUCCESS) {
          status = cursor.getNext(key, value, LockMode.READ_UNCOMMITTED);
        }
      }
      if (status == OperationStatus.SUCCESS) {
        return(true);
      } else {
        cursor.close();
        return(false);
      }
    } catch (DatabaseException e) {
      System.out.println("Exception: "+e.getMessage());
      e.printStackTrace();
      return(false);
    }
  }

  /**
   * <p>getCount.</p>
   *
   * @return a int.
   * @throws java.lang.Exception if any.
   */
  public int getCount() throws Exception {
    if (status == OperationStatus.SUCCESS) {
      return(cursor.count());
    } else {
      return(0);
    }
  }

  /**
   * <p>next.</p>
   *
   * @return a {@link java.lang.Object} object.
   */
  public Object next() {
    if (status == OperationStatus.SUCCESS && value != null) {
      model = (Model) tb.entryToObject(value);
      return(model);
    } else {
      return null;
    }
  }

  /**
   * <p>nextSecondaryKey.</p>
   *
   * @return a {@link java.lang.Object} object.
   * @throws java.io.UnsupportedEncodingException if any.
   */
  public Object nextSecondaryKey() throws UnsupportedEncodingException {
	  return null;
  }
  
  /**
   * <p>close.</p>
   *
   * @throws java.lang.Exception if any.
   */
  public void close() throws Exception {
    cursor.close();
  }

  /**
   * <p>remove.</p>
   */
  public void remove() {
    // FIXME
  }

  /**
   * <p>Getter for the field <code>cursor</code>.</p>
   *
   * @return a {@link com.sleepycat.db.Cursor} object.
   */
  public Cursor getCursor() {
    return(cursor);
  }

}
