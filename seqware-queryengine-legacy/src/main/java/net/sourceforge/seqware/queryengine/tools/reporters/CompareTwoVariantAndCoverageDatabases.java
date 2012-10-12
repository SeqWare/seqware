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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import net.sourceforge.seqware.queryengine.backend.factory.impl.BerkeleyDBFactory;
import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareIterator;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.backend.util.iterators.LocatableSecondaryCursorIterator;

/**
 * <p>CompareTwoVariantAndCoverageDatabases class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class CompareTwoVariantAndCoverageDatabases {

  /**
   * A simple program that takes a list of posit
   *
   * @param args an array of {@link java.lang.String} objects.
   */
  public static void main(String[] args) {

    if (args.length < 23) {
      System.out.println("CompareTwoVariantDatabases <db_dir_a> <db_dir_b> <cacheSize> <locks> <coverage_bin_size> <include_snv> " +
          "<include_indels> <minCoverageA> <minCoverageB> <maxCoverageA> <maxCoverageA> <minObservationsA> <minObservationsB> " +
          "<minObservationsPerStrandA> <minObservationsPerStrandB> " +
          "<minSNPPhred> <positions_list> <uniq_a_output> <uniq_b_output> <somatic_a_output> <somatic_b_output> " +
          "<disagree_output> <summary_output>");
      System.exit(-1);
    }

    try {

      // read vars
      String dbDirA = args[0];
      String dbDirB = args[1];
      long cacheSize =  Long.parseLong(args[2]);
      int locks = Integer.parseInt(args[3]);
      int binSize = Integer.parseInt(args[4]);
      boolean includeSNVs = false;
      if ("true".equals(args[5])) { includeSNVs = true; }
      boolean includeIndels = false;
      if ("true".equals(args[6])) { includeIndels = true; }
      int minCoverageA = Integer.parseInt(args[7]);
      int minCoverageB = Integer.parseInt(args[8]);
      int maxCoverageA = Integer.parseInt(args[9]);
      int maxCoverageB = Integer.parseInt(args[10]);
      int minObservationsA = Integer.parseInt(args[11]);
      int minObservationsB = Integer.parseInt(args[12]);
      int minObservationsPerStrandA = Integer.parseInt(args[13]);
      int minObservationsPerStrandB = Integer.parseInt(args[14]);
      int minSNPPhred = Integer.parseInt(args[15]);
      String positionsList = args[16];
      String uniqAOutputFile = args[17];
      String uniqBOutputFile = args[18];
      String somaticAOutputFile = args[19];
      String somaticBOutputFile = args[20]; 
      String disagreeOutputFile = args[21];
      String summaryOutputFile = args[22];

      //position list
      BufferedReader positions = new BufferedReader(new FileReader(new File(positionsList)));

      // files for output
      BufferedWriter somaticAWriter = new BufferedWriter(new FileWriter(new File(somaticAOutputFile)));
      BufferedWriter somaticBWriter = new BufferedWriter(new FileWriter(new File(somaticBOutputFile)));
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
      settings.setCacheSize(cacheSize);
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
      settings.setCacheSize(cacheSize);
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

        // iterate over positions of interest
        String l = "";
        while((l = positions.readLine()) != null) {
          
          // position info
          String[] lInfo = l.split("\t");

          // FIXME: need to make a store method that takes full range str e.g. chr1:1212-1231232
          // next step is to iterate over each and compare the other one
          // iterate over A
          SeqWareIterator matchIt = null;
          if ("all".equals(lInfo[0])) {
            matchIt = storeA.getMismatches();
          } else {
            matchIt = storeA.getMismatches(lInfo[0], Integer.parseInt(lInfo[1]), Integer.parseInt(lInfo[2]));
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
              //System.out.print("Hit:"+ma.getContig()+":"+ma.getStartPosition()+"-"+ma.getStopPosition()+" "+ma.getReferenceBase()+"->"+ma.getCalledBase()+"("+ma.getCalledBaseCount()+")                        \r");
            }

            if (ma != null && ma.getReadCount() >= minCoverageA && ma.getReadCount() <= maxCoverageA 
                && ma.getConsensusCallQuality() >= minSNPPhred && ma.getCalledBaseCount() >= minObservationsA && ma.getCalledBaseCountForward() >= minObservationsPerStrandA && ma.getCalledBaseCountReverse() >= minObservationsPerStrandA
                && ((includeSNVs && ma.getType() == Variant.SNV) || (includeIndels && (ma.getType() == Variant.INSERTION || ma.getType() == Variant.DELETION)))) {
              aMismatches++;
              //System.out.println("Here");
              
              // get other DBs coverage
              boolean otherDbPassesCoverage = false;
              int otherDbCoverage = 0;
              // get coverage iterator
              int bin = ma.getStartPosition() / binSize;
              int bin2 = ma.getStopPosition() / binSize;
              int newStart = bin*binSize;
              int newStop = (bin2*binSize)+(binSize-1);
              LocatableSecondaryCursorIterator covIt = storeB.getCoverages(ma.getContig(), newStart, newStop);
              if (covIt != null && covIt.hasNext()) {
                Coverage c = (Coverage)covIt.next();
                if (c == null ) {
                  System.out.println("C is null");
                }
                if (ma == null ) {
                  System.out.println("ma is null");
                }
                if (ma.getStartPosition() == 0 ) {
                  System.out.println("ma start is 0");
                }
                // check to see if the coverage in the other db at this position meets requirement, used for somatic detection
                if (c.getCoverage(ma.getStartPosition()) != null && c.getCoverage(ma.getStartPosition()) > minCoverageB) {
                  otherDbPassesCoverage = true;
                  otherDbCoverage = c.getCoverage(ma.getStartPosition());
                  //System.out.println("Coverage: "+ma.getContig()+":"+newStart+"-"+newStop+" mismatch coverage: "+ma.getReadCount()+" cov: "+newStart+" "+newStop+" obj cov "+c.getCoverage(ma.getStartPosition()));
                }
                
              }
              covIt.close();
              
              // get other DBs matches
              SeqWareIterator storeBIt = storeB.getMismatches(ma.getContig(), ma.getStartPosition(), ma.getStopPosition());
              boolean foundMatch = false;
              int matchCount = 0;
              while(storeBIt.hasNext()) {
                Variant mb = (Variant) storeBIt.next();
                //System.out.println("Next");
                if (mb != null && mb.getContig().equals(ma.getContig()) && mb.getStartPosition() == ma.getStartPosition()
                    && ma.getStopPosition() == mb.getStopPosition()
                    // FIXME: need a better way to do this, really want to record if this event was seen at all in the B database
                    //&& mb.getReadCount() >= minCoverageB && mb.getReadCount() <= maxCoverageB
                    //&& mb.getConsensusCallQuality() >= minSNPPhred
                    && ((includeSNVs && mb.getType() == Variant.SNV) || (includeIndels && (mb.getType() == Variant.INSERTION || mb.getType() == Variant.DELETION)))) {
                  matchCount++;
                  // FIXME, support indels better here, now just ignoring anything with *
                  if (mb != null && mb.getType() == Variant.SNV && ma.getType() == Variant.SNV && !mb.getReferenceBase().equals(ma.getReferenceBase())) {
                   throw (new Exception("The reference bases for A: "+ma.getReferenceBase()+" type "+ma.getType()+" "+ma.getContig()+":"+ma.getStartPosition()+"-"+ma.getStopPosition()+" and B: "+mb.getReferenceBase()+" type "+mb.getType()+" "+mb.getContig()+":"+mb.getStartPosition()+"-"+mb.getStopPosition()+" must agree"));
                  }
                  if (mb != null && mb.getCalledBase().equals(ma.getCalledBase())) {
                    //System.out.println("Called base MA: "+ma.getCalledBase()+" called base MB: "+mb.getCalledBase());
                    foundMatch = true;
                    /* if (count % 1000 == 0) {
                      System.out.print("Hit:"+ma.getContig()+":"+ma.getStartPosition()+"-"+ma.getStopPosition()+" "+ma.getReferenceBase()+"->"+ma.getCalledBase()+"("+ma.getCalledBaseCount()+") " +
                          "Match:"+mb.getContig()+":"+mb.getStartPosition()+"-"+mb.getStopPosition()+" "+mb.getReferenceBase()+"->"+mb.getCalledBase()+"("+mb.getCalledBaseCount()+")" +
                      		"\n");
                    } */
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
              
              // now keep track of somatic mutations only in A
              if (!foundMatch && matchCount == 0 && otherDbPassesCoverage ) { // FIXME: taking this out for now, need to parameterize && ma.getZygosity() == Variant.HOMOZYGOUS
                // ([INSDEL]*):*([-ATGC]+)->([-ATGC]+)\((\d+):(\d+):(\d+\.*\d*)\%
                String bedString = ma.getReferenceBase()+"->"+ma.getCalledBase();
                StringBuffer lengthString = new StringBuffer();
                int blockSize = 1;
                for (int i=0; i<ma.getCalledBase().length(); i++) { lengthString.append("-"); }
                if (ma.getType() == Variant.INSERTION) {
                  bedString = "INS:"+lengthString+"->"+ma.getCalledBase();
                } else if (ma.getType() == Variant.DELETION) {
                  bedString = "DEL:"+ma.getCalledBase()+"->"+lengthString;
                  blockSize = lengthString.length();
                }
                somaticAWriter.write(ma.getContig()+"\t"+ma.getStartPosition()+"\t"+ma.getStopPosition()+"\t"+bedString+"("+ma.getReadCount()+":"+ma.getCalledBaseCount()+":0%:[F:"+ma.getCalledBaseCountForward()+"|R:"+ma.getCalledBaseCountReverse()+"]:"+otherDbCoverage+")\n");
              }
            }
          }
          matchIt.close();

          // count
          count = 0;

          // iterate over B
          matchIt = null;
          if ("all".equals(lInfo[0])) {
            matchIt = storeB.getMismatches();
          } else {
            matchIt = storeB.getMismatches(lInfo[0], Integer.parseInt(lInfo[1]), Integer.parseInt(lInfo[2]));
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
              //System.out.print("Hit:"+mb.getContig()+":"+mb.getStartPosition()+"-"+mb.getStopPosition()+"                        \r");
            }

            if (mb != null && mb.getReadCount() >= minCoverageB && mb.getReadCount() <= maxCoverageB 
                && mb.getConsensusCallQuality() >= minSNPPhred 
                && ((includeSNVs && mb.getType() == Variant.SNV) || (includeIndels && (mb.getType() == Variant.INSERTION || mb.getType() == Variant.DELETION)))) {
              bMismatches++;
              SeqWareIterator storeAIt = storeA.getMismatches(mb.getContig(), mb.getStartPosition(), mb.getStopPosition());
              boolean foundMatch = false;
              int matchCount = 0;
              while(storeAIt.hasNext()) {
                Variant ma = (Variant) storeAIt.next();
                if (ma != null && ma.getContig().equals(mb.getContig()) && ma.getStartPosition() == mb.getStartPosition()
                    && ma.getStopPosition() == mb.getStopPosition()
                    && ma.getReadCount() >= minCoverageA && ma.getReadCount() <= maxCoverageA 
                    && ma.getConsensusCallQuality() >= minSNPPhred 
                    && ((includeSNVs && ma.getType() == Variant.SNV) || (includeIndels && (ma.getType() == Variant.INSERTION || ma.getType() == Variant.DELETION)))) {
                  matchCount++;
                  if (ma != null && mb.getType() == Variant.SNV && ma.getType() == Variant.SNV && !ma.getReferenceBase().equals(mb.getReferenceBase())) {
                    throw (new Exception("The reference bases for A: "+ma.getReferenceBase()+" and B: "+mb.getReferenceBase()+" must agree"));
                  }
                  if (ma != null && ma.getCalledBase().equals(mb.getCalledBase())) {
                    
                    foundMatch = true;
                    /* if (count % 1000 == 0) {
                      System.out.print("Hit:"+ma.getContig()+":"+ma.getStartPosition()+"-"+ma.getStopPosition()+" "+ma.getReferenceBase()+"->"+ma.getCalledBase()+"("+ma.getCalledBaseCount()+") " +
                          "Match:"+mb.getContig()+":"+mb.getStartPosition()+"-"+mb.getStopPosition()+" "+mb.getReferenceBase()+"->"+mb.getCalledBase()+"("+mb.getCalledBaseCount()+")" +
                          "\n");
                    } */
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

        }
      } else {
        System.out.println("One or both stores are null: storeA: "+storeA+" storeB: "+storeB);
      }

      //cleanup
      somaticAWriter.close();
      somaticBWriter.close();
      uniqAWriter.close();
      uniqBWriter.close();
      disagreeWriter.close();
      summaryWriter.close();
      
      storeA.close();
      storeB.close();
      

    } catch (Exception e) {
      System.out.println("There has been an exception of type: "+e.getClass()+"\nMessage: "+e.getMessage());
      e.printStackTrace();
    }
  }
}
