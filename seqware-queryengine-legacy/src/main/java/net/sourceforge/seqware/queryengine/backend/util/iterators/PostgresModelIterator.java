package net.sourceforge.seqware.queryengine.backend.util.iterators;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgresql.largeobject.LargeObject;
import org.postgresql.largeobject.LargeObjectManager;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.db.Cursor;
import com.sleepycat.db.DatabaseEntry;

import net.sourceforge.seqware.queryengine.backend.model.Model;
import net.sourceforge.seqware.queryengine.backend.store.impl.PostgreSQLStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareBase;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;

/**
 * An interator on top of the standard Java SQL iterator
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class PostgresModelIterator extends SeqWareBase implements SeqWareIterator {

  ResultSet rs = null;
  Model currModel = null;
  int currId = 0;
  TupleBinding binder = null;
  int persistenceMethod = PostgreSQLStore.OID;
  LargeObjectManager lobj = null;
  
  /**
   * <p>Constructor for PostgresModelIterator.</p>
   *
   * @param rs a {@link java.sql.ResultSet} object.
   * @param binder a {@link com.sleepycat.bind.tuple.TupleBinding} object.
   * @param persistenceMethod a int.
   * @param lobj a {@link org.postgresql.largeobject.LargeObjectManager} object.
   * @throws java.lang.Exception if any.
   */
  public PostgresModelIterator(ResultSet rs, TupleBinding binder, int persistenceMethod, LargeObjectManager lobj) throws Exception {

    this.rs = rs;
    this.binder = binder;
    this.persistenceMethod = persistenceMethod;
    this.lobj = lobj;

    setDebug(false);
    
  }

  // so the iterator class provides: hasNext(), next(), remove()
  /**
   * <p>close.</p>
   *
   * @throws java.lang.Exception if any.
   */
  public void close() throws Exception {
    rs.close();
  }

  /**
   * Not supported for this backend!
   *
   * @return a int.
   * @throws java.lang.Exception if any.
   */
  public int getCount() throws Exception {
    return(-1);
  }

  /**
   * Not applicable for Postgres backend.
   * FIXME: is there a way to get rid of this?
   *
   * @return a {@link com.sleepycat.db.Cursor} object.
   * @throws java.lang.Exception if any.
   */
  public Cursor getCursor() throws Exception {
    return(null);
  }

  /**
   * Not applicable for Postgres backend.
   * FIXME: this is a BerkeleyDB specific method
   *
   * @return a {@link java.lang.Object} object.
   * @throws java.io.UnsupportedEncodingException if any.
   */
  public Object nextSecondaryKey() throws UnsupportedEncodingException {
    return(null);
  }

  /**
   * <p>hasNext.</p>
   *
   * @return a boolean.
   */
  public boolean hasNext() {
    try {
      if (rs != null) {
        if(rs.next()) {
          // FIXME: always column 1?
          byte[] data = null;
          if (persistenceMethod == PostgreSQLStore.BYTEA) {
            data = rs.getBytes(1);
          } else if (persistenceMethod == PostgreSQLStore.OID || persistenceMethod == PostgreSQLStore.FIELDS) {
            int oid = rs.getInt(1);
            LargeObject obj = lobj.open(oid, LargeObjectManager.READ);
            data = new byte[obj.size()];
            obj.read(data, 0, obj.size());
            obj.close();
          }
          // get the ID back
          currId = rs.getInt(2);
          currModel = (Model)binder.entryToObject(new DatabaseEntry(data));
          if (currModel != null) { currModel.setId(Integer.toString(currId)); return(true); }
        }
      }
    } catch (SQLException se) {
      System.out.println("Couldn't connect: print out a stack trace and exit.");
      se.printStackTrace();
      System.exit(1);
    }
    return(false);
  }
  
  /**
   * <p>next.</p>
   *
   * @return a {@link java.lang.Object} object.
   */
  public Object next() {
    
    return(currModel);
    
  }

  /**
   * Removes are unimplemented.
   */
  public void remove() {
    // FIXME: this doesn't do anything!
  }

}
