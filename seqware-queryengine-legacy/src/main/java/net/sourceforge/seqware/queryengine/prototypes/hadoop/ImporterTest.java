/**
 * 
 */
package net.sourceforge.seqware.queryengine.prototypes.hadoop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;

/**
 * @author boconnor
 * 
 * This is a simple script that annotates DB records with tags.
 *
 * TODO:
 */
public class ImporterTest {

  /**
   * @param args
   */
  public static void main(String[] args) {

    if (args.length < 8) {
      System.out.println("TagAnnotationImporter <backend_type_[BerkeleyDB|HBase]> <genome_id> <reference_genome_id> <db_dir> <table> <cacheSize> <locks> <tag_file(s)>");
      System.exit(-1);
    }

    String backendType = args[0];
    String genomeId = args[1];
    String referenceId = args[2];
    String dbDir = args[3];
    String table = args[4];
    long cacheSize = Long.parseLong(args[5]);
    int locks = Integer.parseInt(args[6]);

    ArrayList<String> inputFiles = new ArrayList<String>();
    for (int i=7; i<args.length; i++) {
      inputFiles.add(args[i]);
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

      System.out.println("Finished opeining database");

      if (store != null) {

        // open the file
        BufferedReader inputStream = null;

        for(String tagFile : inputFiles) {

          try {

            inputStream = new BufferedReader(new FileReader(tagFile));
            String l;
            int count = 0;
            String currMismatchId = null;
            while ((l = inputStream.readLine()) != null) {
              count++;
              if (count % 1000 == 0) { 
                System.out.print("mismatchId: "+currMismatchId+" count: "+count+"\r");
              }

              String[] t = l.split("\t");
              if (t.length < 2 || t.length > 3) { throw (new Exception("input file has too many or too few columns.")); }

              String mismatchId = t[0];
              String tag = t[1];
              String value = null;
              if (t.length == 3) { value = t[2]; }

              Variant m = store.getMismatch(mismatchId);
              
              if (m != null) {
              
                m.getTags().put(tag, value);
                
                Pattern versionPat = Pattern.compile("^.*\\.v(\\d+)$");
                Matcher versionMat = versionPat.matcher(m.getId());
                if (versionMat.find()) {
                  long currTimestamp = Long.parseLong(versionMat.group(1));
                  System.err.println("THE VERSION: "+currTimestamp);
                } 
                
                store.putMismatch(m);
                
                System.out.println("The variant is:\n"+
                    "Variant: "+m.getId()+"\n"
                    );
  
                /*if (m != null) { 
                  currMismatchId = mismatchId;
                  //System.out.println("Found mismatch: "+mismatchId);
                  m.addTag(tag, value);
                  store.putMismatch(m);
                } else { throw new Exception("Variant "+mismatchId+" shouldn't be null!"); }
                */
                
              } else {
                System.out.println("Can't find variant: "+mismatchId);
              }
            }
            System.out.print("\n");

          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } finally {
            if (inputStream != null) { inputStream.close(); }
          }

        }

        // finally close
        store.close();
      } else { 
        throw new Exception("store should not be null!"); 
      }
    } catch (SeqWareException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.out.println(e.getMessage());
    }

  }

}
