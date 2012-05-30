/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.exporters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
/**
 * @author boconnor
 * FIXME: the sorted output orders by start and not chr then start
 */
public class ListBEDExporter {

  /**
   * @param args
   * LEFT OFF WITH: need to figure out why ID is null
   */
  public static void main(String[] args) {

    if (args.length < 18) {
      // need to add ratio of forward/reverse strand reads
      System.out.println("BEDExporter <db_dir> <output_prefix> <output_dir> <include_indels> <include_snv> " +
          "<minCoverage> <maxCoverage> <minObservations> <minObservationsPerStrand> <minSNPPhred> <SNPPhredGreaterThanGenomePhred> " +
      "<minPercent> <heterozygousRange> <homozygousRange> <cacheSize> <locks> <output_file> <contig_str_file[s]>");
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
    long cacheSize = Long.parseLong(args[14]);
    int locks = Integer.parseInt(args[15]);

    DecimalFormat df = new DecimalFormat("##0.0");

    String output = args[16];
    ArrayList<String> inputLists = new ArrayList<String>();
    if (args.length > 17) {
      for (int i=17; i<args.length; i++) {
        inputLists.add(args[i]);
      }
    }

    // open output file
    BufferedWriter outputStream = null;

    BerkeleyDBFactory factory = new BerkeleyDBFactory();
    BerkeleyDBStore store = null;
    try {

      outputStream = new BufferedWriter(new FileWriter(output));

      //store = factory.getStore("berkeleydb-mismatch-store", null, null, null, dbDir, true, false, 1073741824);
      SeqWareSettings settings = new SeqWareSettings();
      settings.setStoreType("berkeleydb-mismatch-store");
      settings.setFilePath(dbDir);
      settings.setCacheSize(cacheSize);
      settings.setCreateMismatchDB(false);
      settings.setCreateConsequenceAnnotationDB(false);
      settings.setCreateDbSNPAnnotationDB(false);
      settings.setReadOnly(true);
      settings.setMaxLockers(locks);
      settings.setMaxLockObjects(locks);
      settings.setMaxLocks(locks);
      
      store = factory.getStore(settings);

      if (store == null) { throw new Exception("Store is null"); }
      if (store != null) {

        Iterator<String> it = inputLists.iterator();
        while (it.hasNext()) {

          // open regions file
          BufferedReader list = new BufferedReader(new FileReader(it.next()));           

          int count = 0;
          boolean ok = true;
          String currLine = list.readLine();
          while (currLine != null) {
            
            if (!ok) { System.out.println("Couldn't find entry for: "+currLine); }
            ok = false; 
            
            // get iterator of mismatches
            SeqWareIterator matchIt = null;
            String contig = null;
            if (currLine.matches("(\\S+):(\\d+)-(\\d+)\\t(\\w+)\\t(\\w+)")) {
              count++;
              String[] t = currLine.split("[-:\\t]");
              contig = t[0];
              matchIt = store.getMismatches(t[0], Integer.parseInt(t[1]), Integer.parseInt(t[2]));
              if (count % 1000 == 0) {
                System.out.println("read: "+t[0]+":"+t[1]+"-"+t[2]);
              }
            }
            else { 
              System.out.println("don't know how to deal"); System.exit(-1); 
            }
            
            // move to the next
            currLine = list.readLine();

            // iterate over contents
            while(matchIt.hasNext()) {

              Variant m = (Variant) matchIt.next();

              if (m != null && m.getReadCount() >= minCoverage && m.getReadCount() <= maxCoverage 
                  && m.getConsensusCallQuality() >= minPhred) { 

                // process a SNV
                if (m.getType() == Variant.SNV && includeSNVs && ("all".equals(contig) || m.getContig().equals(contig))) {
                  //System.out.println(m.getContig()+" "+m.getStartPosition()+" "+m.getConsensusBase()); 

                  // now at this point all this data has been calcualted when the mismatch object was created
                  double calledPercent = ((double)m.getCalledBaseCount() / (double)m.getReadCount()) * (double)100.0;
                  double calledFwdPercent = ((double)m.getCalledBaseCountForward() / (double)m.getReadCount()) * (double)100.0;
                  double calledRvsPercent = ((double)m.getCalledBaseCountReverse() / (double)m.getReadCount()) * (double)100.0;

                  String color = "80,175,175";
                  String callStr = "heterozygous";
                  if (m.getZygosity() == m.HOMOZYGOUS) { color = "0,50,180"; callStr = "homozygous"; }


                  if (m.getCalledBaseCount() >= minObservations && (!snpPhredGTGenome || m.getConsensusCallQuality() > m.getReferenceCallQuality())
                      && m.getCalledBaseCountForward() >= minObservationsPerStrand 
                      && m.getCalledBaseCountReverse() >= minObservationsPerStrand
                      && calledPercent >= minPercent) {

                    int testTotal = m.getCalledBaseCountForward() + m.getCalledBaseCountReverse();
                    if (testTotal != m.getCalledBaseCount()) { throw new Exception("Forward and reverse don't add to total\n"); }
                    if (count % 1000 == 0) {
                      System.out.println("writing out SNV entry");
                    }
                    ok = true;
                    outputStream.write(m.getContig()+"\t"+m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+m.getReferenceBase()+"->"+m.getCalledBase()+"("+
                        m.getReadCount()+":"+m.getCalledBaseCount()+":"+df.format(calledPercent)+"%[F:"+m.getCalledBaseCountForward()+":"+df.format(calledFwdPercent)+"%|R:"+m.getCalledBaseCountReverse()+":"+df.format(calledRvsPercent)+"%]"+
                        "call="+callStr+":genome_phred="+m.getReferenceCallQuality()+":snp_phred="+m.getConsensusCallQuality()+
                        ":max_mapping_qual="+m.getMaximumMappingQuality()+":genome_max_qual="+m.getReferenceMaxSeqQuality()+":genome_ave_qual="+df.format(m.getReferenceAveSeqQuality())+
                        ":snp_max_qual="+m.getConsensusMaxSeqQuality()+":snp_ave_qual="+df.format(m.getConsensusAveSeqQuality())+":mismatch_id="+m.getId());
                    Iterator<String> tagIt = m.getTags().keySet().iterator();
                    while(tagIt.hasNext()) {
                      String tag = tagIt.next();
                      String value = m.getTagValue(tag);
                      outputStream.write(":"+tag);
                      if (value != null) { outputStream.write("="+value); }
                    }
                    outputStream.write(")\t"+m.getConsensusCallQuality()+"\t+\t"+
                        m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+color+"\t1\t1\t0\n"
                    );
                    // FIXME: looks like the mean qual is not getting done properly
                  }

                } else if ((m.getType() == Variant.INSERTION || m.getType() == Variant.DELETION)
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

                    if (count % 1000 == 0) {
                      System.out.println("writing out SNV entry");
                    }
                    ok = true;
                    outputStream.write(m.getContig()+"\t"+m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+bedString+"("+
                        m.getReadCount()+":"+m.getCalledBaseCount()+":"+df.format(calledPercent)+"%[F:"+m.getCalledBaseCountForward()+":"+df.format(calledFwdPercent)+"%|R:"+m.getCalledBaseCountReverse()+":"+df.format(calledRvsPercent)+"%]"+
                        "call="+callStr+":genome_phred="+m.getReferenceCallQuality()+":snp_phred="+m.getConsensusCallQuality()+
                        ":max_mapping_qual="+m.getMaximumMappingQuality()+":mismatch_id="+m.getId());
                    Iterator<String> tagIt = m.getTags().keySet().iterator();
                    while(tagIt.hasNext()) {
                      String tag = tagIt.next();
                      String value = m.getTagValue(tag);
                      outputStream.write(":"+tag);
                      if (value != null) { outputStream.write("="+value); }
                    }
                    outputStream.write(")\t"+m.getConsensusCallQuality()+"\t+\t"+
                        m.getStartPosition()+"\t"+m.getStopPosition()+"\t"+color+"\t1\t1\t0\n"
                    );
                  }
                }
              }
            }
            matchIt.close();
          }
          list.close();
        }

        // finally close
        store.close();
        
        // close output
        outputStream.close();
        
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
