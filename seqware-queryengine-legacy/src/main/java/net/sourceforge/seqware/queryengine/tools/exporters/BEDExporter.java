/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.exporters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * <p>BEDExporter class.</p>
 *
 * @author boconnor
 * FIXME: the sorted output orders by start and not chr then start
 * TODO: most of this code should be migrated to the more flexible VariantIteratorProcessor
 * @version $Id: $Id
 */
public class BEDExporter {

  /**
   * <p>main.</p>
   *
   * @param args
   * LEFT OFF WITH: need to figure out why ID is null
   */
  public static void main(String[] args) {

    if (args.length < 22) {
      // need to add ratio of forward/reverse strand reads
      System.out.println("BEDExporter <db_dir> <output_prefix> <output_dir> <include_indels> <include_snv> " +
          "<minCoverage> <maxCoverage> <minObservations> <minObservationsPerStrand> <minSNPPhred> <SNPPhredGreaterThanGenomePhred> " +
      "<minPercent> <heterozygousRange> <homozygousRange> <cacheSize> <locks> <backend_type_[BerkeleyDB|HBase|PostgreSQL]> <genome_id> " +
      "<reference_genome_id> <output_type [full|short|min]> <split_on_contig> <lookup_by_tags> <contig_str[s]_comma_sep> <tag_str[s]_comma_sep>");
      System.out.println("Some notes:\n" +
          "* tags are used to filter results if contigs are supplied\n" +
          "* the first tag in the list is used for the query if no contigs are supplied\n" +
          "  subsequent tags are used to filter the resulting mismatch list\n" +
          "  Improve performance by selecting the tag that will reduce the result set the most as the first." +
      "* the tags are AND'd together for the purposes of the query");
      System.exit(-1);
    }


    String dbDir = args[0];
    String outputPrefix = args[1];
    String outputDir = args[2];
    boolean includeIndels = false;
    if ("true".equals(args[3])) { includeIndels = true; }
    boolean includeSNVs = false;
    if ("true".equals(args[4])) { includeSNVs = true; }
    int minCoverage = Integer.parseInt(args[5]);
    int maxCoverage = Integer.parseInt(args[6]);
    int minObservations = Integer.parseInt(args[7]);
    int minObservationsPerStrand = Integer.parseInt(args[8]);
    int minPhred = Integer.parseInt(args[9]);
    boolean snpPhredGTGenome = false;
    if ("true".equals(args[10])) { snpPhredGTGenome = true; }
    int minPercent = Integer.parseInt(args[11]);
    String heterozygousRange = args[12];
    String homozygousRange = args[13];
    int homozygousMin = 0;
    int homozygousMax = 0;
    int heterozygousMin = 0;
    int heterozygousMax = 0;
    String[] homoTokens = homozygousRange.split("-");
    String[] heteroTokens = heterozygousRange.split("-");
    homozygousMin = Integer.parseInt(homoTokens[0]);
    homozygousMax = Integer.parseInt(homoTokens[1]);
    heterozygousMin = Integer.parseInt(heteroTokens[0]);
    heterozygousMax = Integer.parseInt(heteroTokens[1]);
    //int fastqConvNum = Integer.parseInt(args[13]);
    long cacheSize = Long.parseLong(args[14]);
    int locks = Integer.parseInt(args[15]);
    String backendType = args[16];
    String genomeId = args[17];
    String referenceId = args[18];
    String printType = args[19];

    DecimalFormat df = new DecimalFormat("##0.0");

    boolean splitOnContig = false;
    if ("true".equals(args[20])) { splitOnContig = true; }
    boolean lookupByTags = false;
    if ("true".equals(args[21])) { lookupByTags = true; }
    ArrayList<String> contigs = new ArrayList<String>();
    if (splitOnContig && args.length > 22) {
      String[] locations = args[22].split(",");
      for (int i=0; i<locations.length; i++) {
        contigs.add(locations[i]);
      }
    } else {
      contigs.add("all");
    }
    ArrayList<String> tags = new ArrayList<String>();
    if (lookupByTags && args.length > 22) {
      if (args.length == 23) {
        for (String tag : args[22].split(",")) {
          tags.add(tag);
        }
      } else if (args.length == 24) {
        for (String tag : args[23].split(",")) {
          tags.add(tag);
        }
      } else { System.out.println("Don't know how to parse command line args!"); System.exit(-1); }
    }


    BerkeleyDBFactory factory = new BerkeleyDBFactory();
    Store store = null;
    try {
      if ("BerkeleyDB".equals(backendType)) {
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

      if (store == null) { throw new Exception("Store is null"); }
      if (store != null) {

        Iterator<String> it = contigs.iterator();
        while (it.hasNext()) {

          // chr
          String contig = (String) it.next();
          System.out.println("Processing Contig: "+contig);

          // open the file
          BufferedWriter outputStream = null;
          try {
            outputStream = 
              new BufferedWriter(new FileWriter(outputDir+"/"+outputPrefix+"."+contig+".bed"));

            // get iterator of mismatches
            SeqWareIterator matchIt = null;
            if ("all".equals(contig)) { 
              //System.out.println("test: all "+contig); System.exit(0);
              if (lookupByTags && tags.size() > 0) {
                // only uses first tag for this query, better choose well!
                matchIt = store.getMismatchesByTag(tags.get(0)); 
              } else {
                matchIt = store.getMismatches(); 
                System.out.println("getting all mismatches");
              }
            } else if (contig.matches("(\\S+):(\\d+)-(\\d+)")) {
              String[] t = contig.split("[-:]");
              contig = t[0];
              matchIt = store.getMismatches(t[0], Integer.parseInt(t[1]), Integer.parseInt(t[2]));
              System.out.println("getting by contig, start, stop: "+contig);
            }
            else { 
              System.out.println("getting by contig: "+contig); 
              matchIt = store.getMismatches(contig);
            }

            int variantCount = 0;
            // iterate over contents
            while(matchIt.hasNext()) {
              
              variantCount++;
              if (variantCount % 1000 == 0) {
                System.out.print(variantCount+"\r");
              }

              Variant m = (Variant) matchIt.next();
              
              if (m == null) {
                System.out.println("Variant is null");
              }
              
              // debug
              //System.out.println("Variant: "+m.getReferenceBase()+"->"+m.getCalledBase()+" "+m.getConsensusCallQuality());

              if (m != null && m.getReadCount() >= minCoverage && m.getReadCount() <= maxCoverage 
                  && m.getConsensusCallQuality() >= minPhred) { 

                //System.out.println("here");
                
                // test tags, must pass all tags
                // FIXME: could imagine wanting OR and NOT operations
                boolean passesTagFilter = true;
                int seen = 0;
                if (tags.size() > 0) {
                  HashMap<String,String> mTags = m.getTags();
                  for (String tag : tags) {
                    //System.out.println("Testing tag: "+tag);
                    if (mTags.containsKey(tag)) { seen++; }
                  }
                  if (seen < tags.size()) { passesTagFilter = false; }
                }

                // process a SNV
                if (passesTagFilter && m.getType() == Variant.SNV && includeSNVs && ("all".equals(contig) || m.getContig().equals(contig))) {
                  //System.out.println(m.getContig()+" "+m.getStartPosition()+" "+m.getConsensusBase()); 
                 

                  // now at this point all this data has been calcualted when the mismatch object was created
                  double calledPercent = 0.0;
                  double calledFwdPercent = 0.0;
                  double calledRvsPercent = 0.0;
                  if (m.getReadCount() > 0) {
                    calledPercent = ((double)m.getCalledBaseCount() / (double)m.getReadCount()) * (double)100.0;
                    calledFwdPercent = ((double)m.getCalledBaseCountForward() / (double)m.getReadCount()) * (double)100.0;
                    calledRvsPercent = ((double)m.getCalledBaseCountReverse() / (double)m.getReadCount()) * (double)100.0;
                  }

                  String color = "80,175,175";
                  String callStr = "heterozygous";
                  if (m.getZygosity() == m.HOMOZYGOUS) { color = "0,50,180"; callStr = "homozygous"; }

                  //System.out.println("Here 2: calledBaseCount "+m.getCalledBaseCount()+" calledBaseCountForward: "+m.getCalledBaseCountForward()+" calledBaseCountReverse: "+m.getCalledBaseCountReverse()+" readCount: "+m.getReadCount()+" percent: "+calledPercent);
                  
                  if (m.getCalledBaseCount() >= minObservations && (!snpPhredGTGenome || m.getConsensusCallQuality() > m.getReferenceCallQuality())
                      && m.getCalledBaseCountForward() >= minObservationsPerStrand 
                      && m.getCalledBaseCountReverse() >= minObservationsPerStrand
                      && calledPercent >= minPercent) {

                    //System.out.println("here 3 ");
                    
                    int testTotal = m.getCalledBaseCountForward() + m.getCalledBaseCountReverse();
                    if (testTotal != m.getCalledBaseCount()) { throw new Exception("Forward and reverse don't add to total\n"); }
                    if ("short".equals(printType)) { outputStream.write(m.getContig()+"\t"+m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+m.getReferenceBase()+"->"+m.getCalledBase()+"("+
                        m.getReadCount()+":"+m.getCalledBaseCount()+":"+df.format(calledPercent)+"%[F:"+m.getCalledBaseCountForward()+":"+df.format(calledFwdPercent)+"%|R:"+m.getCalledBaseCountReverse()+":"+df.format(calledRvsPercent)+"%])\n"); }
                    else if ("min".equals(printType)) { outputStream.write(m.getContig()+"\t"+m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+m.getReferenceBase()+"->"+m.getCalledBase()+"("+m.getReadCount()+")\n"); }
                    else { outputStream.write(m.getContig()+"\t"+m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+m.getReferenceBase()+"->"+m.getCalledBase()+"("+
                        m.getReadCount()+":"+m.getCalledBaseCount()+":"+df.format(calledPercent)+"%[F:"+m.getCalledBaseCountForward()+":"+df.format(calledFwdPercent)+"%|R:"+m.getCalledBaseCountReverse()+":"+df.format(calledRvsPercent)+"%]"+
                        "call="+callStr+":genome_phred="+m.getReferenceCallQuality()+":snp_phred="+m.getConsensusCallQuality()+
                        ":max_mapping_qual="+m.getMaximumMappingQuality()+":genome_max_qual="+m.getReferenceMaxSeqQuality()+":genome_ave_qual="+df.format(m.getReferenceAveSeqQuality())+
                        ":snp_max_qual="+m.getConsensusMaxSeqQuality()+":snp_ave_qual="+df.format(m.getConsensusAveSeqQuality())+":mismatch_id="+m.getId());
                    Iterator<String> tagIt = m.getTags().keySet().iterator();
                    while(tagIt.hasNext()) {
                      String tag = tagIt.next();
                      String value = m.getTagValue(tag);
                      if (tag != null) tag = tag.replace(' ', '_');
                      if (value != null) value = value.replace(' ', '_');
                      outputStream.write(":"+tag);
                      if (value != null) { outputStream.write("="+value); }
                    }
                    outputStream.write(")\t"+m.getConsensusCallQuality()+"\t+\t"+
                        m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+color+"\t1\t1\t0\n"
                    );
                    }
                    // FIXME: looks like the mean qual is not getting done properly
                  }

                } else if (passesTagFilter && (m.getType() == Variant.INSERTION || m.getType() == Variant.DELETION)
                    && includeIndels && ("all".equals(contig) || m.getContig().equals(contig))) {

                  double calledPercent = ((double)m.getCalledBaseCount() / (double)m.getReadCount()) * (double)100.0;
                  double calledFwdPercent = ((double)m.getCalledBaseCountForward() / (double)m.getReadCount()) * (double)100.0;
                  double calledRvsPercent = ((double)m.getCalledBaseCountReverse() / (double)m.getReadCount()) * (double)100.0;

                  String color = "80,175,175";
                  String callStr = "heterozygous";
                  if (m.getZygosity() == Variant.HOMOZYGOUS) { color = "0,50,180"; callStr = "homozygous"; }

                  if (m.getCalledBaseCount() >= minObservations && (!snpPhredGTGenome || m.getConsensusCallQuality() > m.getReferenceCallQuality())
                      && m.getCalledBaseCountForward() >= minObservationsPerStrand 
                      && m.getCalledBaseCountReverse() >= minObservationsPerStrand
                      && calledPercent >= minPercent) {


                    // make the string used in the printout
                    String bedString = null;
                    StringBuffer lengthString = new StringBuffer();
                    int blockSize = 1;
                    for (int i=0; i<m.getCalledBase().length(); i++) { lengthString.append("-"); }
                    if (m.getType() == Variant.INSERTION) {
                      bedString = "INS:"+lengthString+"->"+m.getCalledBase();
                    } else if (m.getType() == Variant.DELETION) {
                      bedString = "DEL:"+m.getCalledBase()+"->"+lengthString;
                      blockSize = lengthString.length();
                    } else { throw new Exception("What is type: "+m.getType()); }

                    int testTotal = m.getCalledBaseCountForward() + m.getCalledBaseCountReverse();
                    if (testTotal != m.getCalledBaseCount()) { throw new Exception("Forward and reverse don't add to total\n"); }

                    if ("short".equals(printType)) {
                      outputStream.write(m.getContig()+"\t"+m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+bedString+"("+
                          m.getReadCount()+":"+m.getCalledBaseCount()+":"+df.format(calledPercent)+"%[F:"+m.getCalledBaseCountForward()+":"+df.format(calledFwdPercent)+"%|R:"+m.getCalledBaseCountReverse()+":"+df.format(calledRvsPercent)+"%]\n");
                    }
                    else if ("min".equals(printType)) { outputStream.write(m.getContig()+"\t"+m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+bedString+"("+
                        m.getReadCount()+")\n"); }
                    else {
                        outputStream.write(m.getContig()+"\t"+m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+bedString+"("+
                        m.getReadCount()+":"+m.getCalledBaseCount()+":"+df.format(calledPercent)+"%[F:"+m.getCalledBaseCountForward()+":"+df.format(calledFwdPercent)+"%|R:"+m.getCalledBaseCountReverse()+":"+df.format(calledRvsPercent)+"%]"+
                        "call="+callStr+":genome_phred="+m.getReferenceCallQuality()+":snp_phred="+m.getConsensusCallQuality()+
                        ":max_mapping_qual="+m.getMaximumMappingQuality()+":mismatch_id="+m.getId());
                    Iterator<String> tagIt = m.getTags().keySet().iterator();
                    while(tagIt.hasNext()) {
                      String tag = tagIt.next();
                      String value = m.getTagValue(tag);
                      if (tag != null) tag = tag.replace(' ', '_');
                      if (value != null) value = value.replace(' ', '_');
                      outputStream.write(":"+tag);
                      if (value != null) { outputStream.write("="+value); }
                    }
                    outputStream.write(")\t"+m.getConsensusCallQuality()+"\t+\t"+
                        m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+color+"\t1\t1\t0\n"
                    );
                    }
                  }
                } else if (passesTagFilter && m.getType() == Variant.TRANSLOCATION && ("all".equals(contig) || m.getContig().equals(contig))) { // FIXME: need to have a flag for including translocations
                  // just going to output these for now, add more filters later
                  
                  String color = "80,175,175";
                  outputStream.write(m.getContig()+"\t"+m.getStartPosition()+"\t"+m.getStopPosition()+"\tFOO:"+
                      m.getTranslocationDestinationContig()+":"+m.getTranslocationDestinationStartPosition()+"-"+
                      m.getTranslocationDestinationStopPosition()+":mismatch_id="+m.getId());
                      Iterator<String> tagIt = m.getTags().keySet().iterator();
                      while(tagIt.hasNext()) {
                        String tag = tagIt.next();
                        String value = m.getTagValue(tag);
                        if (tag != null) tag = tag.replace(' ', '_');
                        if (value != null) value = value.replace(' ', '_');
                        outputStream.write(":"+tag);
                        if (value != null) { outputStream.write("="+value); }
                      }
                      
                  outputStream.write(")\t0\t+\t"+
                      m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+color+"\t1\t1\t0\n"
                  );
                } else if (passesTagFilter && m.getType() == Variant.SV && ("all".equals(contig) || m.getContig().equals(contig))) { // FIXME: need to have a flag for including SV
                  // just going to output these for now, add more filters later
                  
                  String color = "80,175,175";
                  outputStream.write(m.getContig()+"\t"+m.getStartPosition()+"\t"+m.getStopPosition()+"\tSV:"+
                      m.getContig()+":"+m.getFuzzyStartPositionMax()+"-"+
                      m.getFuzzyStopPositionMin()+":mismatch_id="+m.getId());
                      Iterator<String> tagIt = m.getTags().keySet().iterator();
                      while(tagIt.hasNext()) {
                        String tag = tagIt.next();
                        String value = m.getTagValue(tag);
                        if (tag != null) tag = tag.replace(' ', '_');
                        if (value != null) value = value.replace(' ', '_');
                        outputStream.write(":"+tag);
                        if (value != null) { outputStream.write("="+value); }
                      }
                      
                  outputStream.write(")\t0\t+\t"+
                      m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+color+"\t1\t1\t0\n"
                  );
                }
              }
            }
            System.out.print("\n");
            matchIt.close();
          } catch (Exception e) {
            System.out.println("SeqWareException: "+e.getMessage());
            e.printStackTrace();
          } finally {
            outputStream.close();
          }

        }

        // finally close
        store.close();
      }
      
      System.out.println("Finished BED Export");
      
    } catch (SeqWareException e) {
      // TODO Auto-generated catch block
      System.out.println("SeqWareException: "+e.getMessage());
      e.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      System.out.println("Exception: "+e.getMessage());
      e.printStackTrace();
    }
  }
}
