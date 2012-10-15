/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.importers;

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
import net.sourceforge.seqware.queryengine.backend.store.impl.PostgreSQLStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;

/**
 * <p>TagAnnotationByPositionImporter class.</p>
 *
 * @author boconnor
 *
 * This is a simple script that annotates DB records with tags.
 *
 * TODO: I should try to merge this with the mismatchId-based import program
 * @version $Id: $Id
 */
public class TagAnnotationByPositionImporter {

  /**
   * <p>main.</p>
   *
   * @param args an array of {@link java.lang.String} objects.
   */
  public static void main(String[] args) {

    if (args.length < 5) {
      System.out.println("TagAnnotationByPositionImporter <backend_type_[BerkeleyDB|HBase|PostgreSQL]> <genome_id> <reference_genome_id> <db_dir> <table> <cacheSize> <locks> <tag_file(s)>");
      System.out.println("tag file: chrx:1212-123232\\tA->T(\\ttag\\tvalue)+");
      System.exit(-1);
    }

    // number of missing variants in the DB
    int missingVariants = 0;

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
      } else if ("PostgreSQL".equals(backendType)) {
        SeqWareSettings settings = new SeqWareSettings();
        settings.setStoreType("postgresql-mismatch-store");
        // FIXME: need to make these params at some point
        settings.setDatabase(System.getProperty("db"));
        settings.setUsername(System.getProperty("user"));
        settings.setPassword(System.getProperty("pass"));
        settings.setServer(System.getProperty("dbserver"));
        settings.setGenomeId(genomeId);
        settings.setReferenceId(referenceId);
        settings.setReturnIds(false);
        settings.setPostgresqlPersistenceStrategy(settings.FIELDS);
        store = new PostgreSQLStore();
        store.setSettings(settings);
        store.setup(settings);
      }

      System.out.println("Finished opeining database");

      if (store != null) {

        // open the file
        BufferedReader inputStream = null;
        String currChrName = "";

        for(String tagFile : inputFiles) {

          try {

            inputStream = new BufferedReader(new FileReader(tagFile));
            String l;
            int count = 0;
            String currPosition = "";
            while ((l = inputStream.readLine()) != null) {
              count++;
              if (count % 1000 == 0) { 
                System.out.print("Chr: "+currChrName+" Count: "+count+"\r");
              }

              String[] t = l.split("\t");
              //System.out.println("Line: "+l);
              if (t.length < 3) { throw (new Exception("input file has too too few columns.")); }

              String[] p = t[0].split("[:-]");
              String[] c = t[1].split("->");

              Pattern pat = Pattern.compile("(\\w+)->(\\w+)");
              Matcher m = pat.matcher(t[1]);
              String refBase = null;
              String calledBase = null;
              String type = null;
              if (m.find()) {
                refBase = m.group(1);
                calledBase = m.group(2);
                type = "snv";
              } else {
                pat = Pattern.compile("INS:(-+)->(\\w+)");
                m = pat.matcher(t[1]);
                if (m.find()) {
                  refBase = m.group(1);
                  calledBase = m.group(2);
                  type = "insertion";
                } else {
                  pat = Pattern.compile("DEL:(\\w+)->(-+)");
                  m = pat.matcher(t[1]);
                  if (m.find()) {
                    refBase = m.group(1);
                    calledBase = m.group(2);
                    type = "deletion";
                  } else {
                    throw new Exception("Can't parse the mutation event from column 2: "+t[1]+".");
                  }
                }
              }

              //System.out.println("split: "+p[0]+" "+p[1]+" "+p[2]);
              //System.out.println("call: "+c[0]+" "+c[1]);

              //long mismatchId = Long.parseLong(t[0]);


              SeqWareIterator matchIt = null;
              matchIt = store.getMismatches(p[0], Integer.parseInt(p[1]), Integer.parseInt(p[2]));

              // iterate over contents
              while(matchIt.hasNext()) {

                Variant variant = (Variant) matchIt.next();

                //if (m != null && m.getReferenceBase().equals(c[0]) && m.getCalledBase().equals(c[1]) && 
                //    m.getStartPosition() == Integer.parseInt(p[1]) && m.getStopPosition() == Integer.parseInt(p[2])

                // FIXME: this is potentially ambiguous for indels with the same start and stop positions
                if (variant != null && ((variant.getReferenceBase().equals(refBase) && variant.getCalledBase().equals(calledBase)) ||
                    "insertion".equals(type) || "deletion".equals(type)) && 
                    variant.getStartPosition() == Integer.parseInt(p[1]) && variant.getStopPosition() == Integer.parseInt(p[2])) {
                  currChrName = variant.getContig();
                  //System.out.println("Variant: "+m.getCalledBase()+" "+m.getConsensusBase()+" "+m.getReferenceBase());
                  //System.out.println("Duplicates? "+m.getContig()+" "+m.getStartPosition()+" "+m.getStopPosition());
                  // FIXME: is there a way to have the update handled by the iterator?
                  for (int i=2; i<t.length; i+=2) {
                    String tag = t[i];
                    String value = null;
                    if (t.length >= i+2) { if (!"".equals(t[i+1])) { value = t[i+1]; } }
                    variant.addTag(tag, value);
                  }
                  // FIXME: why can't I write to the mismatch record while the matchIt is open!?!?!
                  matchIt.close();
                  store.putMismatch(variant);
                  break;
                }
              }
              //close the iterator
              matchIt.close(); 
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
