// Commenting this for now, not sure what happened to secondary indexes in hbase 0.90.6
//
//package net.sourceforge.seqware.queryengine.prototypes.hadoop;
//
//import java.io.DataInput;
//import java.io.DataOutput;
//import java.io.IOException;
//import java.util.Map;
//
//import org.apache.hadoop.hbase.client.tableindexed.IndexKeyGenerator;
//import org.apache.hadoop.hbase.util.Bytes;
//
//public class TagIndexKeyGenerator implements IndexKeyGenerator {
//  
//  private byte [] column;
//
//  public TagIndexKeyGenerator(byte [] column) {
//    this.column = column;
//  }
//
//  public TagIndexKeyGenerator() {
//    // For Writable
//  }
//  
//  @Override
//  /*public byte[][] createIndexKey(byte[] rowKey, Map<byte[], byte[]> columns) {
//    
//    // break apart by space
//    String[] tags = Bytes.toString(columns.get(column)).split("\\s+");
//    
//    // iterate over and add each tag
//    byte[][] result = new byte[tags.length][1];
//    for (int i=0; i<result.length; i++) {
//      result[i] = Bytes.add(Bytes.toBytes(tags[i]), rowKey);
//    }
//    
//    // return result
//    return(result);
//  }*/
//
//  // FIXME: so I need to come up with a way of generating multiple keys from the same input
//  public byte[] createIndexKey(byte[] rowKey, Map<byte[], byte[]> columns) {
//    
//    // break apart by space
//    String[] tags = Bytes.toString(columns.get(column)).split("\\s+");
//    
//    // iterate over and add each tag
//    byte[] result = new byte[1];
//    for (int i=0; i<result.length; i++) {
//      // FIXME: clearly this isn't doing the correct thing!!!
//      result = Bytes.add(Bytes.toBytes(tags[i]), rowKey);
//    }
//    
//    // return result
//    return(result);
//  }
//  
//  @Override
//  public void readFields(DataInput in) throws IOException {
//    column = Bytes.readByteArray(in);
//  }
//
//  @Override
//  public void write(DataOutput out) throws IOException {
//    Bytes.writeByteArray(out, column);
//  }
//
// }
