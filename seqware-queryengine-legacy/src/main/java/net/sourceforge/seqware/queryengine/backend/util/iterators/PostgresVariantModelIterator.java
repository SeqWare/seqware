package net.sourceforge.seqware.queryengine.backend.util.iterators;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;


import com.sleepycat.db.Cursor;

import net.sourceforge.seqware.queryengine.backend.model.Variant;
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
public class PostgresVariantModelIterator extends SeqWareBase implements SeqWareIterator {

  ResultSet rs = null;
  Variant currModel = null;
  int currId = 0;
  PostgreSQLStore store = null;
  
  /**
   * <p>Constructor for PostgresVariantModelIterator.</p>
   *
   * @param rs a {@link java.sql.ResultSet} object.
   * @param store a {@link net.sourceforge.seqware.queryengine.backend.store.impl.PostgreSQLStore} object.
   * @throws java.lang.Exception if any.
   */
  public PostgresVariantModelIterator(ResultSet rs, PostgreSQLStore store) throws Exception {

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
          Variant v = new Variant();
          v.setId((new Integer(rs.getInt(1))).toString());
          v.setType(rs.getByte(2));
          v.setContig(rs.getString(3));
          v.setStartPosition(rs.getInt(4));
          v.setStopPosition(rs.getInt(5));
          v.setFuzzyStartPositionMax(rs.getInt(6));
          v.setFuzzyStopPositionMin(rs.getInt(7));
          v.setReferenceBase(rs.getString(8));
          v.setConsensusBase(rs.getString(9));
          v.setCalledBase(rs.getString(10));
          v.setReferenceCallQuality(rs.getFloat(11));
          v.setConsensusCallQuality(rs.getFloat(12));
          v.setMaximumMappingQuality(rs.getFloat(13));
          v.setReadCount(rs.getInt(14));
          v.setReadBases(rs.getString(15));
          v.setBaseQualities(rs.getString(16));
          v.setCalledBaseCount(rs.getInt(17));
          v.setCalledBaseCountForward(rs.getInt(18));
          v.setCalledBaseCountReverse(rs.getInt(19));
          v.setZygosity(rs.getByte(20));
          v.setReferenceMaxSeqQuality(rs.getFloat(21));
          v.setReferenceAveSeqQuality(rs.getFloat(22));
          v.setConsensusMaxSeqQuality(rs.getFloat(23));
          v.setConsensusAveSeqQuality(rs.getFloat(24));
          v.setCallOne(rs.getString(25));
          v.setCallTwo(rs.getString(26));
          v.setReadsSupportingCallOne(rs.getInt(27));
          v.setReadsSupportingCallTwo(rs.getInt(28));
          v.setReadsSupportingCallThree(rs.getInt(29));
          v.setSvType(rs.getByte(30));
          v.setRelativeLocation(rs.getByte(31));
          v.setTranslocationType(rs.getByte(32));
          v.setTranslocationDestinationContig(rs.getString(33));
          v.setTranslocationDestinationStartPosition(rs.getInt(34));
          v.setTranslocationDestinationStopPosition(rs.getInt(35));
          v.setKeyvalues(rs.getString(36));
          // TODO: add tag support!!
          // populate the tags
          // FIXME: is this the best way to do this?  passing a store object?
          //store.readTags(v, "variant");
          // populate the tags
          // disable join on second table and use local cache instead for performance reasons
          String[] keyvalues = v.getKeyvalues().split(":");
          for (String keyval : keyvalues) {
            String[] keyvalue = keyval.split("=");
            if (keyvalue.length == 2) {
              v.addTag(keyvalue[0], keyvalue[1]);
            } else {
              v.addTag(keyvalue[0], null);              
            }
          }
          
          if (v != null) { currModel = v; return(true); }
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
