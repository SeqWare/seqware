/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.exporters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.BufferedReader;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.model.Consequence;
import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.model.Tag;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.backend.util.iterators.CursorIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.LocatableSecondaryCursorIterator;
import net.sourceforge.seqware.queryengine.backend.util.iterators.SecondaryCursorIterator;

/**
 * <p>TagDumper class.</p>
 *
 * @author boconnor
 *
 * A very simple program that will iterate over the mismatches
 *  and display info about each one for testing.
 *
 * FIXME: this object should actually become a regression test not a tool, I should also provide a dumper util, though, that can dump based on chr, start, and stop.
 * @version $Id: $Id
 */
public class TagDumper {

  /**
   * <p>main.</p>
   *
   * @param args an array of {@link java.lang.String} objects.
   */
  public static void main(String[] args) {

    if (args.length < 3) {
      System.out.println("TagDumper <backend_type_[BerkeleyDB|HBase]> <genome_id> <reference_genome_id> <dbDir> <cacheSize> <locks> <outputFile>");
      System.exit(0);
    }
    
    String backendType = args[0];
    String genomeId = args[1];
    String referenceId = args[2];
    String dbDir = args[3];
    long cacheSize = Long.parseLong(args[4]);
    int locks = Integer.parseInt(args[5]);
    String outputFile = args[6];

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

        // open the file
        BufferedWriter outputStream = null;

        try {

          outputStream = new BufferedWriter(new FileWriter(outputFile));
          SeqWareIterator cursor = store.getMismatchesTags();

          int count = 0;

          outputStream.write("tag\tcount\n");

          while (cursor.hasNext()) {

            count++;
            if (count % 1000 == 0) { 
              System.out.print(count+"\r");
            }

            Tag tag = (Tag)cursor.nextSecondaryKey();
            int tagCount = cursor.getCount();

            outputStream.write(tag.getTag()+"\t"+tagCount+"\n");

          }
          cursor.close();
          System.out.print("\n");

        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } finally {
          if (outputStream != null) { outputStream.close(); }
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
