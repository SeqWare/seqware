/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.util.iterators;

import java.io.UnsupportedEncodingException;

import net.sourceforge.seqware.queryengine.backend.model.LocatableModel;
import net.sourceforge.seqware.queryengine.backend.model.ContigPosition;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.LockMode;
import com.sleepycat.db.OperationStatus;
import com.sleepycat.db.SecondaryCursor;

/**
 * @author boconnor
 *  
 */
public class LocatableSecondaryCursorIterator extends SecondaryCursorIterator implements SeqWareIterator {

  ContigPosition cp = null;

  public LocatableSecondaryCursorIterator(SecondaryCursor cursor, DatabaseEntry searchKey, TupleBinding tb) throws Exception {
    super(cursor, searchKey, tb);
    throw new Exception("Constructor not implemented, must provide a contigPosition.");
  }

  public LocatableSecondaryCursorIterator(SecondaryCursor cursor, DatabaseEntry searchKey, ContigPosition cp, TupleBinding tb) throws Exception {
    super(cursor, searchKey, tb);
    this.cp = cp;
  }

  public Object nextSecondaryKey() throws UnsupportedEncodingException {
	  return(null);
  }
  
  public boolean hasNext() {
    try {
      if (first) {
        // get the first record
        status = cursor.getSearchKeyRange(searchKey, pKey, result, LockMode.READ_UNCOMMITTED);
        first = false;
      } else {
        // get the next record    
        status = cursor.getNextDup(searchKey, pKey, result, LockMode.READ_UNCOMMITTED);
        // if this isn't successful then no more duplicates and time to move to next key
        // or there are no more entries in the db.
        if (status != OperationStatus.SUCCESS) {
          status = cursor.getNext(searchKey, pKey, result, LockMode.READ_UNCOMMITTED);
        }
      }
      // if empty just return false
      //if (cursor.count() < 1) { cursor.close(); return(false); } // this causes and exception
      if (status == OperationStatus.SUCCESS) {
        LocatableModel model = (LocatableModel) tb.entryToObject(result);
        if (model != null && model.getContig().equals(cp.getContig()) && model.getStartPosition() <= cp.getStopPosition()) {
          return(true);
        } else {
          cursor.close();
          return(false);
        }
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
}
