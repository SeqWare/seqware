package net.sourceforge.seqware.queryengine.backend.util.iterators;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


import com.sleepycat.db.Cursor;

import net.sourceforge.seqware.queryengine.backend.store.impl.PostgreSQLStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareBase;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;

/**
 * An interator on top of the standard Java SQL iterator
 *
 * @author boconnor
 *
 * FIXME: is it really wise to pass the store object here!?!?
 * @version $Id: $Id
 */
public class PostgresTagModelIterator extends SeqWareBase implements SeqWareIterator {

  ResultSet rs = null;
  ArrayList<String> currModel = null;
  int currId = 0;
  PostgreSQLStore store = null;
  
  /**
   * <p>Constructor for PostgresTagModelIterator.</p>
   *
   * @param rs a {@link java.sql.ResultSet} object.
   * @param store a {@link net.sourceforge.seqware.queryengine.backend.store.impl.PostgreSQLStore} object.
   * @throws java.lang.Exception if any.
   */
  public PostgresTagModelIterator(ResultSet rs, PostgreSQLStore store) throws Exception {

    this.rs = rs;
    this.store = store;

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
          String tag = rs.getString(1);
          String value = rs.getString(2);
          currModel = new ArrayList<String>();
          currModel.add(tag);
          currModel.add(value);
          if (tag != null) { return(true); }
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
