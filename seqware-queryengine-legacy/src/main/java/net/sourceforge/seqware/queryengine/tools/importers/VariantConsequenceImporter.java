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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.BufferedReader;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.model.Consequence;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;

/**
 * <p>VariantConsequenceImporter class.</p>
 *
 * @author boconnor
 *
 * TODO:
 * FIXME: this doesn't support parsing the sequences from the knownGene or refGene consequence files
 * @version $Id: $Id
 */
public class VariantConsequenceImporter {

  /**
   * <p>main.</p>
   *
   * @param args an array of {@link java.lang.String} objects.
   */
  public static void main(String[] args) {

    if (args.length < 7) {
      System.out.println("VariantConsequenceImporter <backend_type_[BerkeleyDB|HBase]> <genome_id> <reference_genome_id> <db_dir> <cacheSize> <locks> <consequence_file(s)>");
      System.exit(-1);
    }

    String backendType = args[0];
    String genomeId = args[1];
    String referenceId = args[2];
    String dbDir = args[3];
    long cacheSize = Long.parseLong(args[4]);
    int locks = Integer.parseInt(args[5]);

    ArrayList<String> consequenceInputs = new ArrayList<String>();
    for (int i=6; i<args.length; i++) {
      consequenceInputs.add(args[i]);
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

      if (store != null) {

        int fileCount = 0;
        for(String fileName : consequenceInputs) {

          // keep track of the number of files
          fileCount++;

          // open the file
          BufferedReader inputStream = null;

          try {

            System.out.println("Processing "+fileCount+" of "+consequenceInputs.size()+": "+fileName);

            inputStream = new BufferedReader(new FileReader(fileName));
            String l;
            int count = 0;

            // start transaction
            store.startTransaction();

            while ((l = inputStream.readLine()) != null) {
              count++;
              if (count % 1000 == 0) { 
                System.out.print(count+"\r");
              }

              String[] t = l.split("\t");

              if (!l.startsWith("#") && !l.startsWith("Known_Gene")) {

                // figure out the number of columns, we have to support optional columns which give 

                Consequence c = new Consequence();

                // gene name
                c.setGeneId(t[0]);

                // coding region
                Pattern p = Pattern.compile("[:-]");
                String[] codingRegion = p.split(t[2]);
                c.setGeneChr(codingRegion[0]);
                c.setCodingStart(Integer.parseInt(codingRegion[1]));
                c.setCodingStop(Integer.parseInt(codingRegion[2]));

                // strand
                if ("-".equals(t[3])) { c.setStrand(Consequence.MINUS_STRAND); }
                else if ("+".equals(t[3])) { c.setStrand(Consequence.PLUS_STRAND); }
                else {
                  throw new Exception("Don't know what strand "+t[3]+" is.");
                }

                // parse out mismatch ID
                Variant variant = null;
                // FIXME: assumes id is a digit
                p = Pattern.compile("mismatch_id=([^=:\\)]+)");
                Matcher m = p.matcher(t[4]);
                if (m.find()) {
                  //System.out.println("MismatchID: "+m.group(1));
                  c.setMismatchId(m.group(1));
                  variant = store.getMismatch(m.group(1));
                  if (variant == null) {
                    System.out.println("Can't find associated mismatch for this consequence record! ID: "+m.group(1));
                    continue;
                    //throw new Exception("Can't find associated mismatch for this consequence record! ID: "+m.group(1));
                  }
                } else {
                  throw new Exception("Need to have a mismatch_id in 5th column");
                }
                
                // transfer enough information from the variant to the consequence
                // this is needed for the HBase backend where everything is stored in a common genome table
                c.setContig(variant.getContig());
                c.setStartPosition(variant.getStartPosition());
                c.setStopPosition(variant.getStopPosition());
                
                // read type
                if ("VAR".equals(t[5])) {
                  c.setMismatchType(Variant.SNV);
                } else if ("DEL".equals(t[5])) {
                  c.setMismatchType(Variant.DELETION);
                } else if ("INS".equals(t[5])) {
                  c.setMismatchType(Variant.INSERTION);
                } else {
                  throw new Exception("Type needs to be [VAR|DEL|INS] at 6th column");
                }

                // mutation event location
                p = Pattern.compile("[:-]");
                String[] mutationRegion = p.split(t[6]);
                c.setMismatchChr(mutationRegion[0]);
                c.setMismatchStart(Integer.parseInt(mutationRegion[1]));
                c.setMismatchStop(Integer.parseInt(mutationRegion[2]));

                // codon info
                if (!"NA".equals(t[7])) { c.setMismatchCodonPosition(Integer.parseInt(t[7])); }
                if (!"NA->NA".equals(t[8])) { }
                if (!"NA".equals(t[9])) { }

                // now parse the tags
                // populate the mismatch entry's tags too
                if (!"".equals(t[10])) {
                  String[] tags = t[10].split(",");
                  for (int i=0; i<tags.length; i++) {
                    c.getTags().put(tags[i], null);
                    variant.addTag(tags[i], null);
                  }
                }

                // BLOSUM score for SNV causing a codon change
                if (!"NA".equals(t[11])) { c.setMismatchAAChangeBlosumScore(Float.parseFloat(t[11])); }

                // parse the normal and mutated sequences if present
                if (t.length > 12 && t.length <= 16) {
                  if (!"".equals(t[12])) { c.setGenomicSequence(t[12]); }
                  if (!"".equals(t[13])) { c.setMutatedGenomicSequence(t[13]); }
                  if (!"".equals(t[14])) { c.setTranslatedSequence(t[14]); }
                  if (!"".equals(t[15])) { c.setMutatedTranslatedSequence(t[15]); }
                }

                // save objects to db
                store.putMismatch(variant);
                store.putConsequence(c, false);

              }
            }
            System.out.print("\n");

            // finish transaction
            store.finishTransaction();

          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if (store.isActiveTransaction()) {
              store.abortTransaction();
            }
            System.exit(-1);
          } finally {
            if (inputStream != null) { inputStream.close(); }
            if (store.isActiveTransaction()) {
              store.finishTransaction();
            }
          }
        }
        // finally close
        store.close();
      }
    } catch (SeqWareException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      if (store.isActiveTransaction()) {
        try { 
          store.abortTransaction();
        } catch (Exception e2) {
          e.printStackTrace();
          System.exit(-1);
        }
      }
      System.exit(-1);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      if (store.isActiveTransaction()) {
        try { 
          store.abortTransaction();
        } catch (Exception e2) {
          e.printStackTrace();
          System.exit(-1);
        }
      }
      System.exit(-1);
    }

  }

}
