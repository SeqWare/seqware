/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.iterators;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.tools.iterators.processors.VariantProcessor;
import net.sourceforge.seqware.queryengine.tools.iterators.processors.VariantStatsProcessor;

/**
 * @author boconnor
 *
 * TODO: this should replace most of the code in BEDExporter
 * FIXME: need to support passing zygosity filter to processor
 * 
 */
public class VariantIteratorProcessor {

  /**
   * @param args
   */
  public static void main(String[] args) {

    if (args.length < 15) {
      // need to add ratio of forward/reverse strand reads
      System.out.println("VariantIteratorProcessor <db_dir> <cacheSize> <locks> <output_file> <include_indels> <include_snv> " +
          "<minCoverage> <maxCoverage> <minObservations> <minObservationsPerStrand> <minSNVPhred> <minPercent> <iterator_processor_module> " +
          "<lookup_by_contigs> <lookup_by_tags> <contig_str[s]_comma_sep> <tag_str[s]_comma_sep>\n" +
          "Some notes:\n" +
          "* Tags are used to filter results if contigs are supplied.\n" +
          "* The first tag in the list is used for the query if no contigs are supplied,\n" +
          "  subsequent tags are used to filter the resulting mismatch list.\n" +
          "* Improve performance by selecting the tag that will reduce the result set the most as the first.\n" +
          "* The tags are AND'd together for the purposes of the query.\n");
      System.exit(-1);
    }

    // shared vars
    DecimalFormat df = new DecimalFormat("##0.0");

    // command line args
    String dbDir = args[0];
    long cacheSize = Long.parseLong(args[1]);
    int locks = Integer.parseInt(args[2]);
    String outputFilename = args[3];
    boolean includeIndels = false;
    if ("true".equals(args[4])) { includeIndels = true; }
    boolean includeSNVs = false;
    if ("true".equals(args[5])) { includeSNVs = true; }
    int minCoverage = Integer.parseInt(args[6]);
    int maxCoverage = Integer.parseInt(args[7]);
    int minObservations = Integer.parseInt(args[8]);
    int minObservationsPerStrand = Integer.parseInt(args[9]);
    int minSNVPhred = Integer.parseInt(args[10]);
    int minPercent = Integer.parseInt(args[11]);
    String iteratorProcessorModule = args[12];
    boolean splitOnContig = false;
    if ("true".equals(args[13])) { splitOnContig = true; }
    boolean lookupByTags = false;
    if ("true".equals(args[14])) { lookupByTags = true; }
    ArrayList<String> contigs = new ArrayList<String>();
    if (splitOnContig && args.length > 15) {
      String[] locations = args[15].split(",");
      for (int i=0; i<locations.length; i++) {
        contigs.add(locations[i]);
      }
    } else {
      contigs.add("all");
    }
    ArrayList<String> tags = new ArrayList<String>();
    if (lookupByTags && args.length > 15) {
      if (args.length == 16) {
        for (String tag : args[15].split(",")) {
          tags.add(tag);
        }
      } else if (args.length == 17) {
        for (String tag : args[16].split(",")) {
          tags.add(tag);
        }
      } else { System.out.println("Don't know how to parse command line args!"); System.exit(-1); }
    }


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

        Iterator<String> it = contigs.iterator();
        while (it.hasNext()) {

          // chr
          String contig = (String) it.next();
          System.out.println("Processing Contig: "+contig);

          // get iterator of mismatches
          SeqWareIterator matchIt = null;
          if ("all".equals(contig)) { 
            //System.out.println("test: all "+contig); System.exit(0);
            if (lookupByTags && tags.size() > 0) {
              // only uses first tag for this query, better choose well!
              matchIt = store.getMismatchesByTag(tags.get(0)); 
            } else {
              matchIt = store.getMismatches(); 
            }
          } else if (contig.matches("(\\S+):(\\d+)-(\\d+)")) {
            String[] t = contig.split("[-:]");
            contig = t[0];
            matchIt = store.getMismatches(t[0], Integer.parseInt(t[1]), Integer.parseInt(t[2]));
          }
          else { 
            //System.out.println("test contig: "+contig); System.exit(0); 
            matchIt = store.getMismatches(contig);
          }

          // class load parameterized
          Class processorClass = Class.forName("net.sourceforge.seqware.queryengine.tools.iterators.processors."+iteratorProcessorModule);
          VariantProcessor mp = (VariantProcessor) processorClass.newInstance();
          // setup the class
          mp.setOutputFilename(outputFilename);
          mp.setIncludeIndels(includeIndels);
          mp.setIncludeSNVs(includeSNVs);
          mp.setMinCoverage(minCoverage);
          mp.setMaxCoverage(maxCoverage);
          mp.setMinObservations(minObservations);
          mp.setMinObservationsPerStrand(minObservationsPerStrand);
          mp.setMinSNVPhred(minSNVPhred);
          mp.setMinPercent(minPercent);
          mp.setStore(store);
          
          // iterate over contents
          while(matchIt.hasNext()) {

            Variant m = (Variant) matchIt.next();
            
            // now hand this off to the processor
            mp.process(m);
            
          }
          
          // now print the report
          String report = mp.report(null);
          System.out.println(report); // FIXME: should be a file out
        }
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
