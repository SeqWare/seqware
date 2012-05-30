/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.importers.workers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Feature;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.tools.importers.VariantImporter;

/**
 * @author boconnor
 *
 */
public class BedFeatureImportWorker extends ImportWorker {


  public BedFeatureImportWorker() { }


  public void run() {

    // open the file
    BufferedReader inputStream = null;
    try {

      // first ask for a token from semaphore
      pmi.getLock();

      if (compressed) {
        inputStream = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(input))));
      } else {
        inputStream = 
          new BufferedReader(new FileReader(input));
      }
      String l;
      Feature f = null;

      int count = 0;

      while ((l = inputStream.readLine()) != null) {

        // display progress
        count++;
        if (count % 1000 == 0) { 
          System.out.print(count+"\r");
        }

        // ignore commented lines
        if (!l.startsWith("#")) {

          // feature
          f = new Feature();
          
          // pileup string
          String[] t = l.split("\t+");

          // now populate the feature object
          f.setContig(t[0]);
          f.setStartPosition(Integer.parseInt(t[1]));
          f.setStopPosition(Integer.parseInt(t[2]));
          f.setName(t[3]);
          f.setScore(Integer.parseInt(t[4]));
          f.setStrand(t[5].charAt(0));
          f.setThickStart(Integer.parseInt(t[6]));
          f.setThickEnd(Integer.parseInt(t[7]));
          f.setItemRgb(t[8]);
          f.setBlockCount(Integer.parseInt(t[9]));
          String[] blockSizes = t[10].split(",");
          String[] blockStarts = t[11].split(",");
          int[] blockSizesInt = new int[f.getBlockCount()];
          int[] blockStartsInt = new int[f.getBlockCount()];
          for (int i=0; i<f.getBlockCount(); i++) {
            blockSizesInt[i] = Integer.parseInt(blockSizes[i]);
            blockStartsInt[i] = Integer.parseInt(blockStarts[i]);
          }
          f.setBlockSizes(blockSizesInt);
          f.setBlockStarts(blockStartsInt);
          
          // save the feature object
          store.putFeature(f);
        }
      }
      System.out.print("\n");

      // close file
      inputStream.close();

    } 
    catch (Exception e) {
      System.out.println("Exception! "+e.getMessage());
      e.printStackTrace();
    } finally {
      pmi.releaseLock();
    }
  }
  
}
