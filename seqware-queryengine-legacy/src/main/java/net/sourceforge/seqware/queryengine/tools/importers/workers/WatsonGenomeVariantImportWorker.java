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
import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.tools.importers.VariantImporter;

/**
 * <p>WatsonGenomeVariantImportWorker class.</p>
 *
 * @author boconnor
 *
 * A simple worker thread to parse the file ftp://jimwatsonsequence.cshl.edu/jimwatsonsequence/watson-454-snp-v01.txt.gz
 * These are the SNV calls from the Watson genome, see http://jimwatsonsequence.cshl.edu for more information
 * From their README:
 *
 * "watson-454-snp-v01.txt.gz" -- SNP mapping coordinates, with "genotypes". Looks like...
 *BJW-1117373     chr1    41921   G       C       .       novel   .       2      0       4      het
 *BJW-1117523     chr1    42101   T       G       Y       rs2691277.1     .      1       0       1
 *BJW-1119675     chr1    45408   C       T       Y       rs28396308      .      3       0       3
 *
 *The columns are:
 *BCM_local_SNP_ID -- unique ID for referring to the SNPs ahead of submission to dbSNP (we can talk about what and when to submit to dbSNP).
 *
 *chromosome --  (self explanatory)
 *
 *coordinate -- (self explanatory)
 *
 *reference_allele -- plus strand reference base
 *
 *variant_allele -- plus strand variant base
 *
 *match_status -- a Y, N or "." if a dbSNP allele, Y if the variant matches the dbSNP allele, or N if it doesn't; a "." if it's a novel SNP.
 *
 *rs# -- the rsid if dbSNP, "novel" otherwise.
 *
 *alternate_allele -- usually a "." (surrogate for null). A, C, T or G if a third allele is seen in the reads at the given position, it's listed here.  I'm don't expect you to display 3d allele information.
 *
 *variant_count -- number of reads in which variant allele was seen. Can be 1 variants matching dbSNP alleles ("Y" in match_status column), must be 2 for novel alleles, for dbSNP positions that don't match the dbSNP alleles ("N" in match_status column) or for dbSNP positions where there is an alternate allele.
 *
 *alternate_allele_count -- number of reads in which an alternate_allele is seen. Generally these are seen in only one read and are probably errors, and should not be mentioned. In some rare instances (134 times), both the variant allele and the alternate allele are seen multiple times.
 *
 *total_coverage -- the total number of reads at a given SNP position.
 *
 *"genotype" -- "het" if the reference allele is seen at least once. "." (null) if not. These are the sites that are confidently heterozygotes. The others provisionally homozygotes, and in cases where the coverage is deep enough probably they are.
 * @version $Id: $Id
 */
public class WatsonGenomeVariantImportWorker extends ImportWorker {


  /**
   * <p>Constructor for WatsonGenomeVariantImportWorker.</p>
   */
  public WatsonGenomeVariantImportWorker() { }


  /**
   * <p>run.</p>
   */
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
      Variant m = new Variant();
      Coverage c = null;
      int currBin = 0;
      int count = 0;
      String previousPos = null;
      Pattern p =  Pattern.compile("-([ATGCNatgcn]+)");

      while ((l = inputStream.readLine()) != null) {

        // display progress
        count++;
        if (count % 1000 == 0) { 
          //System.out.print(count+"\r");
        }

        // ignore commented lines
        if (!l.startsWith("#")) {

          // pileup string
          String[] t = l.split("\t+");
          
          // the input file is 1-based
          // BJW-1121620     chr1    48074   A       G       Y       rs2691335.2     .       1       0       1       .
          // corresponds to chr1:48073-48074
          // I do see this over the correct dbSNP entry in the UCSC browser
          m.addTag("native_id", t[0]);
          m.addTag(t[0], null);
          m.addTag(t[1], null);
          int stop = Integer.parseInt(t[2]);
          int start = stop - 1;
          m.setStartPosition(start);
          m.setStopPosition(stop);
          m.setContig(t[1]);
          m.setReferenceBase(t[3]);
          m.setCalledBase(t[4]);
          if ("Y".equals(t[5])) {
            // then it's dbSNP
            m.addTag("is_dbSNP", t[6]);
          } else if ("N".equals(t[5]) || ".".equals(t[5])) {
            // then it's novel
            m.addTag("not_dbSNP", t[6]);
          }
          m.addTag(t[6], null);
          // check for third allele
          if (!".".equals(t[7])) {
            m.setCallTwo(t[7]);
          }
          m.setType(m.SNV);
          m.setCalledBaseCount(Integer.parseInt(t[8]));
          m.setCalledBaseCountForward(Integer.parseInt(t[8]));
          m.setReadsSupportingCallTwo(Integer.parseInt(t[9]));
          m.setReadCount(Integer.parseInt(t[10]));
          if (t.length >= 12 && "het".equals(t[11])) {
            m.setZygosity(m.HETEROZYGOUS);
            m.addTag("heterozygous", null);
          } else {
            m.setZygosity(m.UNKNOWN_ZYGOSITY);
            m.addTag("unknown_zygosity", null);
          }
          store.putMismatch(m);
          m = new Variant();
        }
      }
      
      // put the last coverage object
      // TODO: causes transactional problems so don't use it right now.
      if (this.includeCoverage && c != null) {
        store.putCoverage(c);
      }

      // close file
      inputStream.close();

      System.out.print("\n");

    } 
    catch (Exception e) {
      System.out.println("Exception! "+e.getMessage());
      e.printStackTrace();
    } finally {
      pmi.releaseLock();
    }
  }
}
