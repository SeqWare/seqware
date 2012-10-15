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
import com.sleepycat.db.SecondaryCursor;

/**
 * <p>SecondaryCursorIterator class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SecondaryCursorIterator implements SeqWareIterator {

  DatabaseEntry result = new DatabaseEntry();
  DatabaseEntry pKey = new DatabaseEntry();
  DatabaseEntry searchKey = new DatabaseEntry();
  Model model = null;
  TupleBinding tb = null;
  SecondaryCursor cursor = null;
  OperationStatus status;
  boolean first = true;
  boolean search = true;
  boolean duplicates = true;

  
  /**
   * Used to iterate over an entire secondary table with or without duplicates reported
   *
   * @param cursor a {@link com.sleepycat.db.SecondaryCursor} object.
   * @param tb a {@link com.sleepycat.bind.tuple.TupleBinding} object.
   * @param reportDuplicates a boolean.
   * @throws java.lang.Exception if any.
   */
  public SecondaryCursorIterator(SecondaryCursor cursor, TupleBinding tb, boolean reportDuplicates) throws Exception {
    this.cursor = cursor;
    this.tb = tb;
    search = false;
    duplicates = reportDuplicates;
  }
  
  /**
   * Used to search the secondary table with only duplicates reported back
   *
   * @param cursor a {@link com.sleepycat.db.SecondaryCursor} object.
   * @param searchKey a {@link com.sleepycat.db.DatabaseEntry} object.
   * @param tb a {@link com.sleepycat.bind.tuple.TupleBinding} object.
   * @throws java.lang.Exception if any.
   */
  public SecondaryCursorIterator(SecondaryCursor cursor, DatabaseEntry searchKey, TupleBinding tb) throws Exception {
    this.cursor = cursor;
    this.searchKey = searchKey;
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
        // get the first search hit record
        if (search) { status = cursor.getSearchKey(searchKey, pKey, result, LockMode.READ_UNCOMMITTED); }
        // else get the first record
        else { status = cursor.getFirst(searchKey, pKey, result, LockMode.READ_UNCOMMITTED); }
        first = false;
      } else {
        // get the next record, if duplicates are supported
        if (duplicates) {
          status = cursor.getNextDup(searchKey, pKey, result, LockMode.READ_UNCOMMITTED);
        } else {
          // return nextnodup if iterating over all records
          status = cursor.getNextNoDup(searchKey, pKey, result, LockMode.READ_UNCOMMITTED);
        }
        // if this isn't successful then no more duplicates and time to move to next key
        // or there are no more entries in the db. Try to getNext one more time only if 
        // not doing a search
        // FIXME: for now I'm only allowing for exact matches with or without duplicates reported back, so this is removed
        /*if (status != OperationStatus.SUCCESS && !search) {
          status = cursor.getNext(searchKey, pKey, result, LockMode.READ_UNCOMMITTED);
        }*/
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
    if (status == OperationStatus.SUCCESS && result != null) {
      model = (Model) tb.entryToObject(result);
      return(model);
    } else {
      return null;
    }
  }
  
  /**
   * Used to get the keys in this secondary table as strings
   * (won't work for non string keys!).
   *
   * @return a {@link java.lang.Object} object.
   * @throws java.io.UnsupportedEncodingException if any.
   */
  public Object nextSecondaryKey() throws UnsupportedEncodingException {
    if (status == OperationStatus.SUCCESS && searchKey != null) {
      model = (Model) tb.entryToObject(searchKey);
      return(model);
    } else {
      return null;
    }
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
    // FIXME: this doesn't do anything!
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
