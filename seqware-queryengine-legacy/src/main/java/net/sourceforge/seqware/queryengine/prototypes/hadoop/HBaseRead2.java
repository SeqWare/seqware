package net.sourceforge.seqware.queryengine.prototypes.hadoop;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import net.sourceforge.seqware.queryengine.backend.model.Feature;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;

/**
 * <p>HBaseRead2 class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class HBaseRead2 {

  /**
   * <p>main.</p>
   *
   * @param args an array of {@link java.lang.String} objects.
   * @throws java.lang.Exception if any.
   */
  public static void main(String[] args) throws Exception {
    
    /* System.out.println("Trying HBase Store");
    
    HBaseStore store = new HBaseStore();
    SeqWareSettings settings = new SeqWareSettings();
    settings.setReferenceId("hg18HuRefSOLiDChr1");
    settings.setGenomeId("HuRefSOLiDChr1");
    store.setup(settings);
    SeqWareIterator it = store.getMismatches();
    while(it.hasNext()) {
      Variant v = (Variant)it.next();
      System.out.println("VariantID: "+v.getId()); 
    }
    it.close();
    store.close();*/
    
    System.out.println("Trying Manual");
    
    HBaseConfiguration config = new HBaseConfiguration();
    HTable table = new HTable(config, "hg18HuRefSOLiDChr1Table");
    
    Scan s = new Scan();
    //s.addColumn(Bytes.toBytes("variant"), Bytes.toBytes("GenomeHuRefSOLiDChr1"));
    ResultScanner scanner = table.getScanner(s);
    try {
      // Scanners return Result instances.
      // Now, for the actual iteration. One way is to use a while loop like so:
      for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
        // print out the row we found and the columns we were looking for
        System.out.println("Found row: " + rr);
      }

      // The other approach is to use a foreach loop. Scanners are iterable!
      // for (Result rr : scanner) {
      //   System.out.println("Found row: " + rr);
      // }
    } catch (Exception e) { 
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
    finally {
      // Make sure you close your scanners when you are done!
      // Thats why we have it inside a try/finally clause
      scanner.close();
      table.close();
    }

  }
  
}
