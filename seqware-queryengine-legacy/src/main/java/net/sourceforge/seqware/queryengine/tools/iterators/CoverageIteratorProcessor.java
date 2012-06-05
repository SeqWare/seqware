/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.iterators;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.backend.util.iterators.LocatableSecondaryCursorIterator;
import net.sourceforge.seqware.queryengine.tools.iterators.processors.CoverageProcessor;
import net.sourceforge.seqware.queryengine.tools.iterators.processors.VariantProcessor;
import net.sourceforge.seqware.queryengine.tools.iterators.processors.VariantStatsProcessor;

/**
 * @author boconnor
 *
 * A very simple iterator that takes a list of positions, gets the coverage objects for those positions, and feeds
 * them to whatever processor is specified.
 * 
 * The list input should look like "contig\tstart\tstop\n"
 * 
 */
public class CoverageIteratorProcessor {

  /**
   * @param args
   */
  public static void main(String[] args) {

    if (args.length < 7) {
      // need to add ratio of forward/reverse strand reads
      System.out.println("CoverageIteratorProcessor <iterator_processor_module_name> <db_dir> <cacheSize> <locks> <bin_size> <list_of_positions> <output_file> " +
          "Some notes:\n" +
          "* The list input should look like \"contig\tstart\tstop\n\"\n");
      System.exit(-1);
    }

    // shared vars
    DecimalFormat df = new DecimalFormat("##0.0");

    // command line args
    String iteratorProcessorModule = args[0];
    String dbDir = args[1];
    long cacheSize = Long.parseLong(args[2]);
    int locks = Integer.parseInt(args[3]);
    int binSize = Integer.parseInt(args[4]);
    String listOfPositions = args[5];
    String outputFilename = args[6];

    BerkeleyDBFactory factory = new BerkeleyDBFactory();
    BerkeleyDBStore store = null;
    try {

      //store = factory.getStore("berkeleydb-mismatch-store", null, null, null, dbDir, true, false, 1073741824);
      SeqWareSettings settings = new SeqWareSettings();
      settings.setStoreType("berkeleydb-mismatch-store");
      settings.setFilePath(dbDir);
      settings.setCacheSize(cacheSize);
      settings.setCreateMismatchDB(false);
      settings.setCreateConsequenceAnnotationDB(false);
      settings.setCreateDbSNPAnnotationDB(false);
      settings.setReadOnly(true);

      store = factory.getStore(settings);

      if (store == null) { throw new Exception("Store is null"); }
      if (store != null) { 

        // open the list of positions
        BufferedReader inputStream = 
          new BufferedReader(new FileReader(listOfPositions));
        
        // for each position, get the coverages and pass to CoverageStatsProcessor
        String l;
        int count = 0;
        HashMap stats = new HashMap();
        System.out.println("Processing coverage with: net.sourceforge.seqware.queryengine.tools.iterators.processors."+iteratorProcessorModule);
        Class processorClass = Class.forName("net.sourceforge.seqware.queryengine.tools.iterators.processors."+iteratorProcessorModule);
        // class load parameterized
        CoverageProcessor cp = (CoverageProcessor) processorClass.newInstance();
        while((l = inputStream.readLine()) != null) {
          
          // display progress
          count++;
          if (count % 1000 == 0) { 
            System.err.print(count+"\r");
          }
          
          // ignore commented lines
          if (!l.startsWith("#")) {
            
            // locations string
            String[] t = l.split("\t+");
            String contig = t[0];
            int start = Integer.parseInt(t[1]);
            int stop = Integer.parseInt(t[2]);
            System.out.println("Examining "+contig+":"+start+"-"+stop);
            
            // get coverage iterator
            int bin = start / binSize;
            int bin2 = stop / binSize;
            int newStart = bin*binSize;
            int newStop = (bin2*binSize)+(binSize-1);
            LocatableSecondaryCursorIterator covIt = store.getCoverages(contig, newStart, newStop);
            
            // setup the class
            cp.setOutputFilename(outputFilename);
            cp.setContig(contig);
            cp.setStart(start);
            cp.setStop(stop);
            cp.setStore(store);
            cp.setStats(stats);
            
            // iterate over coverage
            
            while(covIt.hasNext()) {
              count++;
              
              Coverage c = (Coverage) covIt.next();
              
              // now hand this off to the processor
              cp.process(c);
              
            }
            covIt.close();
            
          }
          
        }
        inputStream.close();
        System.out.print("\n");
        
        // now print out stats
        BufferedWriter outputStream = 
          new BufferedWriter(new FileWriter(outputFilename));
        outputStream.write(cp.report(stats)); // FIXME: should be a file out
        outputStream.close();
        
        // get the stats hash back, repeat and pass the stats object to the next position
        // print out the stats object as tab-delimited
        // want total number of bases seen within the region (used to calculate effective fold coverage and enrich)
       
        store.close();
      }

    } catch (SeqWareException e) {
      // TODO Auto-generated catch block
      System.out.println(e.getMessage());
      e.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}
