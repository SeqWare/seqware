/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.importers.workers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

import org.apache.commons.compress.compressors.CompressorStreamFactory;

import net.sourceforge.seqware.queryengine.backend.model.Variant;

/**
 * <p>U87TranslocationVariantImportWorker class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class U87TranslocationVariantImportWorker extends ImportWorker {


  /**
   * <p>Constructor for U87TranslocationVariantImportWorker.</p>
   */
  public U87TranslocationVariantImportWorker() { }


  /**
   * <p>run.</p>
   */
  public void run() {

    // open the file
    BufferedReader inputStream = null;
    try {

      // first ask for a token from semaphore
      pmi.getLock();
      
      // Attempting to guess the file format
      if (compressed) {
        if (input.endsWith("bz2") || input.endsWith("bzip2")) {
          inputStream = new BufferedReader(new InputStreamReader(new CompressorStreamFactory().createCompressorInputStream("bzip2", new BufferedInputStream(new FileInputStream(input)))));
        } else if (input.endsWith("gz") || input.endsWith("gzip")) {
          inputStream = new BufferedReader(new InputStreamReader(new CompressorStreamFactory().createCompressorInputStream("gz", new BufferedInputStream(new FileInputStream(input)))));          
        } else {
          throw new Exception("Don't know how to interpret the filename extension for: "+input+" we support 'bz2', 'bzip2', 'gz', and 'gzip'");
        }
      } else {
        inputStream = 
          new BufferedReader(new FileReader(input));
      }
      String l;
      
      int count = 0;

      while ((l = inputStream.readLine()) != null) {

        // ignore commented lines
        if (!l.startsWith("#")) {
          
          count++;

          // pileup string
          String[] t = l.split("\t+");

          Variant t1 = new Variant();
          Variant t2 = new Variant();
          
          String[] loc1 = t[0].split(":");
          String[] loc2 = t[1].split(":");
          String contig1 = loc1[0];
          String[] coords1 = loc1[1].split("-");
          String contig2 = loc2[0];
          String[] coords2 = loc2[1].split("-");
          
          t1.addTag("translocation", null);
          t1.setContig(contig1);
          t1.setStartPosition(Integer.parseInt(coords1[0]));
          t1.setStopPosition(Integer.parseInt(coords1[1]));
          t1.setTranslocationDestinationContig(contig2);
          t1.setTranslocationDestinationStartPosition(Integer.parseInt(coords2[0]));
          t1.setTranslocationDestinationStopPosition(Integer.parseInt(coords2[1]));
          if ("intra".equals(t[2])) t1.setTranslocationType(Variant.TRANSLOCATION_INTRACHR);
          else if ("intra".equals(t[2])) t1.setTranslocationType(Variant.TRANSLOCATION_INTERCHR);
          else t1.setTranslocationType(Variant.TRANSLOCATION_UNKNOWN);
          t1.setType(Variant.TRANSLOCATION);
          t1.setZygosity(Variant.UNKNOWN_ZYGOSITY);
          
          t2.addTag("translocation", null);
          t2.setContig(contig2);
          t2.setStartPosition(Integer.parseInt(coords2[0]));
          t2.setStopPosition(Integer.parseInt(coords2[1]));
          t2.setTranslocationDestinationContig(contig1);
          t2.setTranslocationDestinationStartPosition(Integer.parseInt(coords1[0]));
          t2.setTranslocationDestinationStopPosition(Integer.parseInt(coords1[1]));
          if ("intra".equals(t[2])) t2.setTranslocationType(Variant.TRANSLOCATION_INTRACHR);
          else if ("intra".equals(t[2])) t2.setTranslocationType(Variant.TRANSLOCATION_INTERCHR);
          else t2.setTranslocationType(Variant.TRANSLOCATION_UNKNOWN);
          t2.setType(Variant.TRANSLOCATION);
          t2.setZygosity(Variant.UNKNOWN_ZYGOSITY);
          
          store.putMismatch(t1);
          store.putMismatch(t2);
          System.out.println(workerName+": adding translocation to db: "+t1.getContig()+":"+t1.getStartPosition()+"-"+t1.getStopPosition()+" "+
                t2.getContig()+":"+t2.getStartPosition()+"-"+t2.getStopPosition()+
                " total lines processed so far: "+count);
        }
      }

      // close file
      inputStream.close();

      System.out.print("\n");

    } 
    catch (Exception e) {
      System.out.println("Exception with file: "+input+"\n"+e.getMessage());
      e.printStackTrace();
    } finally {
      pmi.releaseLock();
    }
  }
}
