package net.sourceforge.seqware.queryengine.tools.annotators;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.backend.util.iterators.CursorIterator;

/**
 * 
 * @author boconnor
 * 
 *         A simple program to iterate over the mismatch entries, adding their
 *         contig and hetero/homo as a tag. This makes finding all mismatches on a given contig
 *         faster than searching by the location secondary index.
 * 
 */
public class AnnotateVariantsWithContigAndZygosity {

  /**
   * @param args
   */
  public static void main(String[] args) {

    if (args.length < 6) {
      System.out.println("AnnotateVariantsWithContigAndZygosity <backend_type_[BerkeleyDB|HBase]> <genome_id> <reference_genome_id> <db_dir> <cacheSize> <locks>");
      System.exit(-1);
    }

    String backendType = args[0];
    String genomeId = args[1];
    String referenceId = args[2];
    String dbDir = args[3];
    long cacheSize = Long.parseLong(args[4]);
    int locks = Integer.parseInt(args[5]);

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
        settings.setReadOnly(false);
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

      if (store != null) {

        store.startTransaction();

        SeqWareIterator ci = store.getMismatchesUnordered();

        int count = 0;
        Variant m = null;
        while (ci.hasNext()) {
          m = (Variant) ci.next();
          //System.out.println("HERE!");
          if (m != null) {
            String contig = m.getContig();
            m.addTag(contig, null);
            String zygosity = null;
            if (m.getZygosity() == m.HOMOZYGOUS) { zygosity = "homozygous"; }
            else if (m.getZygosity() == m.HETEROZYGOUS) { zygosity = "heterozygous"; }
            else if (m.getZygosity() == m.HEMIZYGOUS) { zygosity = "hemizygous"; }
            else if (m.getZygosity() == m.NULLIZYGOUS) { zygosity = "nullizygous"; }
            if (zygosity != null) { m.addTag(zygosity, null); }
            //System.out.println("COUNT: "+count);
            store.putMismatch(m, ci, false);
            count++;
            if (count % 1000 == 0) {
              System.out.print("Processing mismatch on "+contig+":\t"+count+"\r");
            }
          }
        }
        ci.close();

        System.out.print("\nDone\n");

        store.finishTransaction();
        store.close();
      }

    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
      if (store != null && store.isActiveTransaction()) {
        try {
          store.abortTransaction();
        } catch (Exception e2) {
          System.out.println(e2.getMessage());
          e2.printStackTrace();
        }
      }
      System.exit(-1);
    }
  }
}
