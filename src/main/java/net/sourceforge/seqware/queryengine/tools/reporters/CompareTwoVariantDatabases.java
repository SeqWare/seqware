package net.sourceforge.seqware.queryengine.tools.reporters;

/* 
 * This is prototype code.
 * 
 * This is going to look at the mismatches in two databases and figure out how many
 * calls meet the criteria in both, how many meet the criteria just in a, and how many meet 
 * the criteria just in b.  I can then use the summary stats to create a Venn diagram to 
 * get a general idea of how many events are "lost" post filtering.
 * 
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;

public class CompareTwoVariantDatabases {
  
  /**
   * FIXME: add param to pass in range to compare over eg chr22 or chr1:123-1231231
   * @param args
   */
  public static void main(String[] args) {
      
      if (args.length < 28) {
          System.out.println("CompareTwoVariantDatabases <db_dir_a> <db_dir_b> <include_snv> " +
              "<include_indels> <minCoverageA> <minCoverageB> <maxCoverageA> <maxCoverageB> <minObservationsA> <minObservationsB> <minObservationsPerStrandA> <minObservationsPerStrandB> " +
              "<minSNPPhredA> <minSNPPhredB> <minCallPhredA> <minCallPhredB> <minPercentA> <minPercentB> <cacheSizeA> <cacheSizeB> <uniq_a_output> <uniq_b_output> " +
              "<disagree_output> <summary_output> <lenient> <offset> <verbose> <range_str>");
          System.exit(-1);
      }
      
      try {
        
        // read vars
        String dbDirA = args[0];
        String dbDirB = args[1];
        boolean includeSNVs = false;
        if ("true".equals(args[2])) { includeSNVs = true; }
        boolean includeIndels = false;
        if ("true".equals(args[3])) { includeIndels = true; }
        int minCoverageA = Integer.parseInt(args[4]);
        int minCoverageB = Integer.parseInt(args[5]);
        int maxCoverageA = Integer.parseInt(args[6]);
        int maxCoverageB = Integer.parseInt(args[7]);
        int minObservationsA = Integer.parseInt(args[8]);
        int minObservationsB = Integer.parseInt(args[9]);
        int minObservationsPerStrandA = Integer.parseInt(args[10]);
        int minObservationsPerStrandB = Integer.parseInt(args[11]);
        int minSNPPhredA = Integer.parseInt(args[12]);
        int minSNPPhredB = Integer.parseInt(args[13]);
        int minCallPhredA = Integer.parseInt(args[14]);
        int minCallPhredB = Integer.parseInt(args[15]);
        int minPercentA = Integer.parseInt(args[16]);
        int minPercentB = Integer.parseInt(args[17]);
        long cacheSizeA =  Long.parseLong(args[18]);
        long cacheSizeB =  Long.parseLong(args[19]);
        String uniqAOutputFile = args[20];
        String uniqBOutputFile = args[21];
        String disagreeOutputFile = args[22];
        String summaryOutputFile = args[23];
        boolean lenient = false;
        if ("true".equals(args[24])) { lenient = true; }
        int offset = Integer.parseInt(args[25]);
        boolean verbose = false;
        if ("true".equals(args[26])) { verbose = true; }
        String rangeStr = args[27];
        
        // files for output
        BufferedWriter uniqAWriter = new BufferedWriter(new FileWriter(new File(uniqAOutputFile)));
        BufferedWriter uniqBWriter = new BufferedWriter(new FileWriter(new File(uniqBOutputFile)));
        BufferedWriter disagreeWriter = new BufferedWriter(new FileWriter(new File(disagreeOutputFile)));
        BufferedWriter summaryWriter = new BufferedWriter(new FileWriter(new File(summaryOutputFile)));
        
        
        // vars for tracking overlap of both mismatch sets
        int totalMismatches = 0;
        int aMismatches = 0;
        int bMismatches = 0;
        int mismatchesOnlyInA = 0;
        int mismatchesOnlyInB = 0;
        int mismatchesAgreeingA = 0;
        int mismatchesDisagreeingA = 0;
        int mismatchesAgreeingB = 0;
        int mismatchesDisagreeingB = 0;
        
        // create factory
        BerkeleyDBFactory factory = new BerkeleyDBFactory();
        BerkeleyDBStore storeA = null;
        BerkeleyDBStore storeB = null;
        
        // open database a
        SeqWareSettings settings = new SeqWareSettings();
        settings.setStoreType("berkeleydb-mismatch-store");
        settings.setFilePath(dbDirA);
        settings.setCacheSize(cacheSizeA);
        settings.setCreateMismatchDB(false);
        settings.setCreateConsequenceAnnotationDB(false);
        settings.setCreateDbSNPAnnotationDB(false);
        settings.setCreateCoverageDB(false);
        settings.setReadOnly(true);
        storeA = factory.getStore(settings);
        
        // open database b
        settings = new SeqWareSettings();
        settings.setStoreType("berkeleydb-mismatch-store");
        settings.setFilePath(dbDirB);
        settings.setCacheSize(cacheSizeB);
        settings.setCreateMismatchDB(false);
        settings.setCreateConsequenceAnnotationDB(false);
        settings.setCreateDbSNPAnnotationDB(false);
        settings.setCreateCoverageDB(false);
        settings.setReadOnly(true);
        storeB = factory.getStore(settings);
        
        // now proceed if both are not null
        if (storeA != null && storeB != null) {
          
          // for printing status
          int count = 0;
          
          // FIXME: need to make a store method that takes full range str e.g. chr1:1212-1231232
          // next step is to iterate over each and compare the other one
          // iterate over A
          SeqWareIterator matchIt = null;
          if ("all".equals(rangeStr)) {
            matchIt = storeA.getMismatches();
          } else if (rangeStr.indexOf(":") > -1) {
            String[] t = rangeStr.split("[:-]");
            matchIt = storeA.getMismatches(t[0], Integer.parseInt(t[1]), Integer.parseInt(t[2]));
          } else {
            matchIt = storeA.getMismatches(rangeStr);
          }
          
          // intro
          System.out.println("Starting examination of A:\n"+dbDirA);

          while(matchIt.hasNext()) {
            
            // get the match
            Variant ma = (Variant) matchIt.next();
            
            // print progress
            count++;
            //System.out.println(count);
            if (count % 1000 == 0) { 
                System.out.print(count+"        \r");
            }
            
            if (ma != null && ma.getReadCount() >= minCoverageA && ma.getReadCount() <= maxCoverageA 
                && ma.getConsensusCallQuality() >= minSNPPhredA && ma.getReferenceCallQuality() >= minCallPhredA
                && ((includeSNVs && ma.getType() == Variant.SNV) || (includeIndels && (ma.getType() == Variant.INSERTION || ma.getType() == Variant.DELETION)))) {
              aMismatches++;
              if (verbose) { System.out.println("MA: "+ma.getContig()+":"+ma.getStartPosition()+"-"+ma.getStopPosition()+" "+ma.getReferenceBase()+"->"+ma.getCalledBase()); }
              SeqWareIterator storeBIt = storeB.getMismatches(ma.getContig(), ma.getStartPosition()-offset, ma.getStartPosition()+offset);
              boolean foundMatch = false;
              int matchCount = 0;
              while(storeBIt.hasNext()) {
                Variant mb = (Variant) storeBIt.next();
                if (mb != null && mb.getReadCount() >= minCoverageB && mb.getReadCount() <= maxCoverageB 
                    && mb.getConsensusCallQuality() >= minSNPPhredB && mb.getReferenceCallQuality() >= minCallPhredB
                    && ((includeSNVs && mb.getType() == Variant.SNV) || (includeIndels && (mb.getType() == Variant.INSERTION || mb.getType() == Variant.DELETION)))) {
                  matchCount++;
                  if (verbose) { System.out.println(" MB Potential Match: "+mb.getContig()+":"+mb.getStartPosition()+"-"+mb.getStopPosition()+" "+mb.getReferenceBase()+"->"+mb.getCalledBase()+" coverage: "+mb.getReadCount()); }
                  if (mb != null && !lenient && !mb.getReferenceBase().equals(ma.getReferenceBase())) {
                    throw (new Exception("The reference bases for A: "+ma.getReferenceBase()+" and B: "+mb.getReferenceBase()+" must agree"));
                  }
                  if (mb != null && !lenient && mb.getCalledBase().equals(ma.getCalledBase())) {
                    foundMatch = true;
                  }
                  if (mb != null && lenient && (mb.getCalledBase().equals(ma.getCalledBase()) || mb.getCalledBase().equals(ma.getReferenceBase()))) {
                    foundMatch = true;
                    if (verbose) { System.out.println(" MB Found Match: "+mb.getContig()+":"+mb.getStartPosition()+"-"+mb.getStopPosition()+" "+mb.getReferenceBase()+"->"+mb.getCalledBase()+" coverage: "+mb.getReadCount()); }
                  }
                }
              }
              storeBIt.close();
              if (!foundMatch && matchCount > 0) {
                mismatchesDisagreeingA++;
                disagreeWriter.write(ma.getId()+"\n");
              }
              else if (foundMatch && matchCount > 0) { mismatchesAgreeingA++; }
              else if (matchCount == 0) {
                mismatchesOnlyInA++;
                uniqAWriter.write(ma.getId()+"\n");
              }
            }
          }
          matchIt.close();
          
          // count
          count = 0;
          
          // iterate over B
          matchIt = null;
          if ("all".equals(rangeStr)) {
            matchIt = storeB.getMismatches();
          } else if (rangeStr.indexOf(":") > -1) {
            String[] t = rangeStr.split("[:-]");
            matchIt = storeB.getMismatches(t[0], Integer.parseInt(t[1]), Integer.parseInt(t[2]));
          } else {
            matchIt = storeB.getMismatches(rangeStr);
          }
          
          // intro
          System.out.println("\nStarting examination of B:\n"+dbDirB);

          while(matchIt.hasNext()) {
            
            // get the match
            Variant mb = (Variant) matchIt.next();
            
            // print progress
            count++;
            if (count % 1000 == 0) { 
                System.out.print(count+"         \r");
            }
            
            if (mb != null && mb.getReadCount() >= minCoverageB && mb.getReadCount() <= maxCoverageB 
                && mb.getConsensusCallQuality() >= minSNPPhredB && mb.getReferenceCallQuality() >= minCallPhredB
                && ((includeSNVs && mb.getType() == Variant.SNV) || (includeIndels && (mb.getType() == Variant.INSERTION || mb.getType() == Variant.DELETION)))) {
              bMismatches++;
              if (verbose) { System.out.println("MB: "+mb.getContig()+":"+mb.getStartPosition()+"-"+mb.getStopPosition()+" "+mb.getReferenceBase()+"->"+mb.getCalledBase()+" coverage: "+mb.getReadCount()+" seen: "+mb.getCalledBaseCount()+" forward "+
                  mb.getCalledBaseCountForward()+" reverse: "+mb.getCalledBaseCountReverse()+" phred: "+mb.getConsensusCallQuality()+" ref phred: "+mb.getReferenceCallQuality()
                  ); }
              SeqWareIterator storeAIt = storeA.getMismatches(mb.getContig(), mb.getStartPosition()-offset, mb.getStartPosition()+offset);
              boolean foundMatch = false;
              int matchCount = 0;
              while(storeAIt.hasNext()) {
                Variant ma = (Variant) storeAIt.next();
                if (ma != null && ma.getReadCount() >= minCoverageA && ma.getReadCount() <= maxCoverageA 
                    && ma.getConsensusCallQuality() >= minSNPPhredA && ma.getReferenceCallQuality() >= minCallPhredA
                    && ((includeSNVs && ma.getType() == Variant.SNV) || (includeIndels && (ma.getType() == Variant.INSERTION || ma.getType() == Variant.DELETION)))) {
                  matchCount++;
                  if (verbose) { System.out.println(" MA Potential Match: "+ma.getContig()+":"+ma.getStartPosition()+"-"+ma.getStopPosition()+" "+ma.getReferenceBase()+"->"+ma.getCalledBase()+" coverage: "+ma.getReadCount()); }
                  if (ma != null && !lenient && !ma.getReferenceBase().equals(mb.getReferenceBase())) {
                    throw (new Exception("The reference bases for A: "+ma.getReferenceBase()+" and B: "+mb.getReferenceBase()+" must agree"));
                  }
                  if (ma != null && !lenient && ma.getCalledBase().equals(mb.getCalledBase())) {
                    foundMatch = true;
                  }
                  if (ma != null && lenient && (ma.getCalledBase().equals(mb.getCalledBase()) || ma.getCalledBase().equals(mb.getReferenceBase()))) {
                    foundMatch = true;
                    if (verbose) { System.out.println(" MA Found Match: "+ma.getContig()+":"+ma.getStartPosition()+"-"+ma.getStopPosition()+" "+ma.getReferenceBase()+"->"+ma.getCalledBase()+" coverage: "+ma.getReadCount()); }
                  }
                }
              }
              storeAIt.close();
              if (!foundMatch && matchCount > 0) { mismatchesDisagreeingB++; }
              else if (foundMatch && matchCount > 0) { mismatchesAgreeingB++; }
              else if (matchCount == 0) {
                mismatchesOnlyInB++;
                uniqBWriter.write(mb.getId()+"\n");
              }
            }
          }
          matchIt.close();
          
          // now print out the summary counts
          totalMismatches = mismatchesOnlyInA + mismatchesOnlyInB + mismatchesAgreeingA + mismatchesDisagreeingA;
          summaryWriter.write("\nTotal Unique Mismatches: "+totalMismatches+"\n" +
              "Database Dir A: "+dbDirA+"\n"+
              "Database Dir B: "+dbDirB+"\n"+
              "Number of Mismatches in A: "+aMismatches+"\n"+
              "Number of Mismatches in B: "+bMismatches+"\n"+
              "Mismatches Only in A: "+mismatchesOnlyInA+"\n"+
              "Mismatches Only in B: "+mismatchesOnlyInB+"\n"+
              "Should be the same:\n"+
              "Mismatches Agreeing (calculated from A): "+mismatchesAgreeingA+"\n"+
              "Mismatches Agreeing (calculated from B): "+mismatchesAgreeingB+"\n"+
              "Should be the same:\n"+
              "Mismatches Disagreeing (calcualted from A): "+mismatchesDisagreeingA+"\n"+
              "Mismatches Disagreeing (calcualted from B): "+mismatchesDisagreeingB+"\n"
              );
          
          // cleanup
          storeA.close();
          storeB.close();
          
        } else {
          System.out.println("One or both stores are null: storeA: "+storeA+" storeB: "+storeB);
        }
        
        //cleanup
        uniqAWriter.close();
        uniqBWriter.close();
        disagreeWriter.close();
        summaryWriter.close();
        
      } catch (Exception e) {
        System.out.println("There has been an exception of type: "+e.getClass()+"\nMessage: "+e.getMessage());
        e.printStackTrace();
      }
  }
}
