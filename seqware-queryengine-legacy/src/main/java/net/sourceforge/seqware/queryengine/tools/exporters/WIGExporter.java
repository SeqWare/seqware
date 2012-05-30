/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.exporters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
/**
 * @author boconnor
 * FIXME: the sorted output orders by start and not chr then start
 * TODO: most of this code should be migrated to the more flexible VariantIteratorProcessor
 */
public class WIGExporter {

  /**
   * @param args
   * LEFT OFF WITH: need to figure out why ID is null
   */
  public static void main(String[] args) {

    if (args.length < 10) {
      // need to add ratio of forward/reverse strand reads
      System.out.println("WIGExporter <backend_type_[BerkeleyDB|HBase]> <genome_id> <reference_genome_id> <db_dir> <cacheSize> <locks> <output_prefix> <output_dir> " +
          "<format [wig|wig_verbose|ave_wig])> <contig_str[s]_comma_sep>");
      System.out.println("Some notes:\n" +
          "* you have to specify one or more contigs\n");
      System.exit(-1);
    }

    String backendType = args[0];
    String genomeId = args[1];
    String referenceId = args[2];
    String dbDir = args[3];
    long cacheSize = Long.parseLong(args[4]);
    int locks = Integer.parseInt(args[5]);
    String outputPrefix = args[6];
    String outputDir = args[7];
    String format = args[8];
    ArrayList<String> contigs = new ArrayList<String>();
    String[] locations = args[9].split(",");
    for (int i=0; i<locations.length; i++) {
      contigs.add(locations[i]);
    } 

    BerkeleyDBFactory factory = new BerkeleyDBFactory();
    Store store = null;
    
    try {
      if ("BerkeleyDB".equals(backendType)) {
        // settings
        SeqWareSettings settings = new SeqWareSettings();
        settings.setStoreType("berkeleydb-mismatch-store");
        settings.setFilePath(dbDir);
        settings.setCacheSize(cacheSize);
        settings.setCreateMismatchDB(false);
        settings.setCreateConsequenceAnnotationDB(false);
        settings.setCreateDbSNPAnnotationDB(false);
        settings.setCreateCoverageDB(false);
        settings.setReadOnly(true);
        settings.setMaxLockers(locks);
        settings.setMaxLockObjects(locks);
        settings.setMaxLocks(locks);
        
        // store object
        store = factory.getStore(settings);
      } else if ("HBase".equals(backendType)) {
        SeqWareSettings settings = new SeqWareSettings();
        settings.setStoreType("hbase-mismatch-store");
        settings.setGenomeId(genomeId);
        settings.setReferenceId(referenceId);
        store = new HBaseStore();
        store.setSettings(settings);
        store.setup(settings);
      }
      /*
      //store = factory.getStore("berkeleydb-mismatch-store", null, null, null, dbDir, true, false, 1073741824);
      SeqWareSettings settings = new SeqWareSettings();
      settings.setStoreType("berkeleydb-mismatch-store");
      settings.setFilePath(dbDir);
      settings.setCacheSize(cacheSize);
      settings.setCreateMismatchDB(false);
      settings.setCreateConsequenceAnnotationDB(false);
      settings.setCreateDbSNPAnnotationDB(false);
      settings.setReadOnly(true);
      */

      if (store == null) { throw new Exception("Store is null"); }
      if (store != null) {

        Iterator<String> it = contigs.iterator();
        while (it.hasNext()) {

          // chr
          String contig = (String) it.next();
          System.out.println("Processing Contig: "+contig);

          // open the file
          BufferedWriter outputStream = null;
          try {
            outputStream = 
              new BufferedWriter(new FileWriter(outputDir+"/"+outputPrefix+"."+contig+".wig"));

            // get iterator of mismatches
            SeqWareIterator wigIt = null;
            if ("all".equals(contig)) { 
              System.out.println("all contigs are not supported, supply a list: "+contig); System.exit(-1);
            } else if (contig.matches("(\\S+):(\\d+)-(\\d+)")) {
              String[] t = contig.split("[-:]");
              contig = t[0];
              wigIt = store.getCoverages(t[0], Integer.parseInt(t[1]), Integer.parseInt(t[2]));
            }
            else { 
              wigIt = store.getCoverages(contig);
            }

            // iterate over contents
            while(wigIt != null && wigIt.hasNext()) {

              Coverage cov = (Coverage) wigIt.next();
              
              if ("wig".equals(format) || "wig_verbose".equals(format)) {
                //System.out.println("Coverage: "+cov.getContig()+":"+cov.getStartPosition()+"-"+cov.getStopPosition());
                //System.out.println("count: "+cov.getCount());
                //System.out.println("count size: "+cov.getCoverage().size());
                Integer iInt = null;
                for(int i=cov.getStartPosition(); i<=cov.getStopPosition(); i++) {
                  int iplus = i+1;
                  iInt = new Integer(i);
                  if (cov.getCoverage().get(iInt) == null && "wig_verbose".equals(format)) {
                    outputStream.write(cov.getContig()+"\t"+i+"\t"+iplus+"\t0\n");
                  }
                  if (cov.getCoverage().get(iInt) != null) {
                    //System.out.println(cov.getContig()+"\t"+i+"\t"+iplus+"\t"+cov.getCoverage().get(iInt));
                    outputStream.write(cov.getContig()+"\t"+i+"\t"+iplus+"\t"+cov.getCoverage().get(iInt)+"\n");
                  }
                }
              } else if ("ave_wig".equals(format)) {
                int binSize = (cov.getStopPosition() - cov.getStartPosition()) + 1;
                int sum = cov.getSum();
                int ave = sum / binSize;
                int stop = cov.getStopPosition()+1;
                outputStream.write(cov.getContig()+"\t"+cov.getStartPosition()+"\t"+stop+"\t"+ave+"\n");
              }
              
            }
            wigIt.close();

          } finally {
            outputStream.close();
          }

        }

        // finally close
        store.close();
      }
    } catch (SeqWareException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
