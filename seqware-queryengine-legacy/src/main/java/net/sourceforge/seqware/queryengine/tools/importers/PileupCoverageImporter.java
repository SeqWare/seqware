/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.importers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.BufferedReader;

import org.apache.commons.compress.compressors.CompressorStreamFactory;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.HBaseStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;

/**
 * @author boconnor
 *
 * A simple coverage importer that imports coverage data from a pileup 
 * file. Pileup file is the format from SAMtools
 *
 * TODO:
 * 
 */
public class PileupCoverageImporter {

  /**
   * @param args
   */
  public static void main(String[] args) {

    if (args.length < 9) {
      System.out.println("PileupCoverageImporter <backend_type_[BerkeleyDB|HBase]> <genome_id> <reference_genome_id> <db_dir> <cacheSize> " +
      		"<locks> <compressed> <coverage_block_size> <pileup_file(s)>");
      System.exit(-1);
    }

    String backendType = args[0];
    String genomeId = args[1];
    String referenceId = args[2];
    String dbDir = args[3];
    long cacheSize = Long.parseLong(args[4]);
    int locks = Integer.parseInt(args[5]);
    boolean compressed = false;
    if ("true".equals(args[6])) { compressed = true; }
    int binSize = Integer.parseInt(args[7]);

    ArrayList<String> pileupInputs = new ArrayList<String>();
    for (int i=8; i<args.length; i++) {
      pileupInputs.add(args[i]);
    }

    HashMap<Integer, Coverage> locations = new HashMap<Integer, Coverage>();

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

        try {
          // loop over the files
          Iterator<String> it = pileupInputs.iterator();
          while (it.hasNext()) {

            String pileupInput = it.next();
            System.out.println("Processing: "+pileupInput);
            BufferedReader inputStream;
            if (compressed) {
              if (pileupInput.endsWith("bz2") || pileupInput.endsWith("bzip2")) {
                inputStream = new BufferedReader(new InputStreamReader(new CompressorStreamFactory().createCompressorInputStream("bzip2", new BufferedInputStream(new FileInputStream(pileupInput)))));
              } else if (pileupInput.endsWith("gz") || pileupInput.endsWith("gzip")) {
                inputStream = new BufferedReader(new InputStreamReader(new CompressorStreamFactory().createCompressorInputStream("gz", new BufferedInputStream(new FileInputStream(pileupInput)))));          
              } else {
                throw new Exception("Don't know how to interpret the filename extension for: "+pileupInput+" we support 'bz2', 'bzip2', 'gz', and 'gzip'");
              }            } else {
              inputStream = 
                new BufferedReader(new FileReader(pileupInput));
            }
            String l;
            int count = 0;
            Coverage c = null;
            int currBin = 0;
            int sum = 0;
            while ((l = inputStream.readLine()) != null) {
              if (!l.startsWith("#")) {

                count++;
                if (count % 1000 == 0) { 
                  System.out.print(count+"\r");
                }

                String[] t = l.split("\t");

                // skip the indel rows
                if (!"*".equals(t[2])) {

                  String contig = t[0];
                  Integer stopPos = Integer.parseInt(t[1]);
                  Integer startPos = stopPos-1;
                  Integer currCount = Integer.parseInt(t[7]);

                  int bin = startPos / binSize;
                  //System.out.println(" + curr bin: "+bin+" start pos: "+startPos+" currCount: "+currCount);


                  if (c == null || bin != currBin) {
                    if (c != null) { 
                      // save the previous cov obj
                      c.setSum(sum);
                      store.putCoverage(c);
                    }
                    c = new Coverage();
                    currBin = bin;
                    c.setContig(contig);
                    c.setStartPosition(bin*binSize);
                    c.setStopPosition((bin*binSize)+(binSize-1));
                    sum = 0;
                    //locations.put(bin, c);
                    //System.out.println("Creating new cov: start "+c.getStartPosition()+" stop: "+c.getStopPosition());
                  }

                  c.putCoverage(startPos, currCount);
                  sum += currCount;

                }
              }
            }
            
            // put the last coverage object
            if (c != null) {
              c.setSum(sum);
              store.putCoverage(c);
            }
            
            inputStream.close();
            System.out.print("\n");
          }

        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        } finally {
          //if (inputStream != null) { inputStream.close(); }
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
