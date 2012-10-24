package net.sourceforge.seqware.queryengine.backend.util.iterators;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.db.Cursor;
import com.sleepycat.db.DatabaseEntry;

import net.sourceforge.seqware.queryengine.backend.model.Model;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareBase;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;

/**
 * FIXME: need to create a locatable model iterator and then pass in the end point, otherwise the code
 * that uses this object will need to do bounds checking!
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class HBaseModelIterator extends SeqWareBase implements SeqWareIterator {

  ResultScanner scanner = null;
  HTable table = null;
  TupleBinding binder = null;
  String family = null;
  String label = null;
  boolean returnAllVersions = false;

  // the next model object
  Model currModel = null;
  
  // the next result
  Result result = null;
  
  // local vars
  Iterator<KeyValue> timestampIterator = null;

  /**
   * <p>Constructor for HBaseModelIterator.</p>
   *
   * @param scanner a {@link org.apache.hadoop.hbase.client.ResultScanner} object.
   * @param table a {@link org.apache.hadoop.hbase.client.HTable} object.
   * @param binder a {@link com.sleepycat.bind.tuple.TupleBinding} object.
   * @param family a {@link java.lang.String} object.
   * @param label a {@link java.lang.String} object.
   * @param returnAllVersions a boolean.
   * @throws java.lang.Exception if any.
   */
  public HBaseModelIterator(ResultScanner scanner, HTable table, TupleBinding binder, String family, String label, boolean returnAllVersions) throws Exception {

    this.scanner = scanner;
    this.table = table;
    this.binder = binder;
    this.family = family;
    this.label = label;
    this.returnAllVersions = returnAllVersions;
    
    setDebug(false);
    
  }

  // so the iterator class provides: hasNext(), next(), remove()
  /**
   * <p>close.</p>
   *
   * @throws java.lang.Exception if any.
   */
  public void close() throws Exception {
    scanner.close();
  }

  /**
   * Not supported for HBase backend!
   *
   * @return a int.
   * @throws java.lang.Exception if any.
   */
  public int getCount() throws Exception {
    return(0);
  }

  /**
   * Not applicable for HBase backend.
   * FIXME: is there a way to get rid of this?
   *
   * @return a {@link com.sleepycat.db.Cursor} object.
   * @throws java.lang.Exception if any.
   */
  public Cursor getCursor() throws Exception {
    return(null);
  }

  /**
   * Not applicable for HBase backend.
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
    if (returnAllVersions) {
      return(hasNextWithTimestamps());
    } else {
      return(hasNextWithoutTimestamps());
    }
  }
  
  private boolean hasNextWithoutTimestamps() {
    boolean hasNext = false;
    try {
      timestampIterator = null;
      Result result = scanner.next();
      if (result != null) {
        byte[] bytes = result.getValue(Bytes.toBytes(family), Bytes.toBytes(label));
        if (bytes != null && bytes.length > 0) {
          DatabaseEntry value = new DatabaseEntry(bytes);
          currModel = (Model)binder.entryToObject(value);
          if (currModel != null) { hasNext = true; }
        } else {
          // if there was a result returned but it doesn't have the family/label I want move to the next one
          return(hasNextWithoutTimestamps());
        }
      }
    } catch(IOException e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
    return(hasNext);
  }
  
  /**
   * FIXME: the fact that this code gets a complete KeyValue list means it's pulling more data
   * than it needs to, is this significantly slowing down the code just to look for the rare
   * case of multiple variants at the same position?
   */
  private boolean hasNextWithTimestamps() {
    //Result currResult = scanner.next();
    currModel = null;
    boolean hasNext = false;
    try {
      // first, if we're currently examining an entry with multiple timestamps iterate through that
      if (timestampIterator != null && timestampIterator.hasNext()) {
        log("In timestampiterator");
        KeyValue entry = timestampIterator.next();
        DatabaseEntry value = new DatabaseEntry(entry.getValue());
        currModel = (Model)binder.entryToObject(value);
        if (currModel != null) { hasNext = true; }
      } else {
        log("In main hasNext");
        timestampIterator = null;
        Result result = scanner.next();
        log("result: "+result);
        
        if (result != null) {
                   
          List<KeyValue> kvList = result.list();
          List<KeyValue> filteredKvList = new ArrayList<KeyValue>();
          long numberOfTimestamps = 0;
          
          /*
          // another way to traverse timestamps
          if (map != null) {
            for (byte[] familyB : map.keySet()) {
              for (byte[] qualifierB : map.get(Bytes.toBytes(family)).keySet()) {
                for (long timestampB : map.get(Bytes.toBytes(family)).get(Bytes.toBytes(label)).keySet()) {
                  log("FAMILY: "+Bytes.toString(familyB)+" QUALI "+Bytes.toString(qualifierB)+" TIME: "+timestampB);
                  if (Bytes.toString(familyB).equals(family) && Bytes.toString(qualifierB).equals(label)) {
                    log("The timestamp was: "+timestampB);
                    //filteredKvList.add(kv);
                  }
                }
              }
            }
          }*/
          
          if (kvList != null) { 
            for(KeyValue kv : kvList) {
              //log("Looking at family: "+Bytes.toString(kv.getFamily())+" label "+Bytes.toString(kv.getQualifier()));
              if (Bytes.toString(kv.getFamily()).equals(family) && Bytes.toString(kv.getQualifier()).equals(label)) {
                //log("The timestamp was: "+kv.getTimestamp());
                filteredKvList.add(kv);
                log("   Adding to kv array");
              }
            }
          }
          
          // now decide if I'm going to iterate over timestamps or just return the first
          if (filteredKvList.size() > 1) {
            log("More than one timestamp, calling recursive");
            //log("FilteredKvList size > 1: "+filteredKvList.size());
            timestampIterator = filteredKvList.iterator();
            // this should be true otherwise getNumValues doesn't do what I think it should
            if (timestampIterator != null && timestampIterator.hasNext()) { 
              return(hasNext());
            } else {
              timestampIterator = null;
            }
          } 
          // otherwise just return the single timestamp entry
          // need to be careful because this returns the most recent value
          // should only be called if there's one value/timestamp
          else if (filteredKvList.size() == 1) {
            log("Only one result");
            DatabaseEntry value = new DatabaseEntry(filteredKvList.get(0).getValue());
            currModel = (Model)binder.entryToObject(value);
            if (currModel != null) { hasNext = true; }
          } else {
            // FIXME: this could be empty if the column simple doesn't exist, should use server-side filters here for 
            // better performance
            log("KeyValue array is empty, probably current row doesn't have the family/label of interest, moving to next");
            return(hasNext());
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
    //debug
    return(hasNext);

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
