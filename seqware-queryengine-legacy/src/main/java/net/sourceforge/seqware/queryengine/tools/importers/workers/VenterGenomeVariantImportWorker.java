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
 * @author boconnor
 * 
 * FIXME: I think this is storing ref and consensus swapped at least for heterozygous!!
 * 
 * FIXME: calledBaseCount 0 calledBaseCountForward: 0 calledBaseCountReverse: 0 readCount: 0 percent: 0.0
 * these are all coming back 0!?!?! Why aren't they saved?
 *
 * A simple worker thread to parse the file ftp://ftp.jcvi.org/pub/data/huref/HuRef.InternalHuRef-NCBI.gff
 * These are the SNV calls from the Venter genome, see http://www.jcvi.org for more information
 * From their README:
 * 
The file "variants.InternalHuRef-NCBI.gff" contains the filtered list 
of variants in Levy, et al. PLoS Biology 5:e254 (2007). 5 million 
variants were filtered down to ~3.5 million variants (Table 3 in 
Levy, et al.).  This file contains 3,355,459 million filtered variants 
found within the HuRef-NCBI one-to-one assembly map.



Column descriptions are as follows:

1.  Chromosome (NCBI 36) 

2.  Variant identifier (unique to each variant)

3.  Variant Type
  There are 7 different variant types in this file.  See  Fig. 4
  in Levy, et al. for a description of the variant types.
  "heterozygous_mixed_sequence_variant" in this file corresponds
  to "complex" in the figure. 

4.  Chromosome start position (NCBI 36, in space-based coordinates)

5.  Chromosome end position (NCBI 36, space-based coordinates)

6.  Not used.

7.  Orientation with respect to NCBI.  The sequence is give in column.
If the value in column 7 is "+" or "-", this means that the sequence
corresponds to the positive or negative strand of NCBI, respectively.
A "." is given for variants that had ambiguous mapping.

8.  This field is delimited by semicolons. In the first field, the
    alleles of the variant are given (e.g. A/B).  For homozygous
    variants, the first allele A matches NCBI 36, the second allele B
    matches HuRef.  There can be more than 2 alleles, which may occur
    when reads may pile up in repititive regions.  

    The second field "RMR" indicates RepeatMasker status.   RMR=1
    indicates that the variant occurs in a region identified by
    RepeatMasker; RMR=0 indicates that the variant does not.  

    The third field "TR" indicates TandemRepeat status. TR=1 indicates
    that the variant occurs in a tandem repeat, as identified by
    TandemRepeatFinder.  TR=0 indicates that the variant does not.   


9.  This column indicates whether a variant was post-processed.
    Method1 indicates the variant was kept in its original form and
    not post-processed.  Method1_MSV_clean corresponds to a modified
    variant call where heterozygous mixed sequence variants were
    changed to indels.  Method2 indicates that the variant is composed of a
    cluster of Method1 variants that were within 10 bp of each other
    (see paper for procedure).  Method2_AmbiguousMapping indicates
    that after the clustering, the position of the new variant could
    not be easily placed on NCBI 36, and there may be some error in
    the mapping position given. 

*Please note that the values provided in Table 4 of Levy, et al. are based on Method1
variants.

 * 
 */
public class VenterGenomeVariantImportWorker extends ImportWorker {


  public VenterGenomeVariantImportWorker() { }


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
      int count = 0;

      while ((l = inputStream.readLine()) != null) {

        // display progress
        count++;
        if (count % 1000 == 0) { 
          System.out.print(count+"\r");
        }

        // string
        String[] t = l.split("\t+");
        
        // ignore commented lines
        // just load SNVs for now
        if (!l.startsWith("#") && ("homozygous_SNP".equals(t[2]) || "heterozygous_SNP".equals(t[2]))) {
          
          // the input file is 0-based and I can just use the start and stop 
          // 1       1104754756879   heterozygous_SNP        15991514        15991515        .       -       C/A;RMR=1;TR=0  Method2
          // corresponds to chr1:15991514-15991515
          // in the genome browser this is a "G" and the dbSNP record says its G -> T
          // I do see this over the correct dbSNP entry in the UCSC browser
          // actually it appears all huref SNVs have been added to dbSNP!
          m.setContig("chr"+t[0]);
          m.addTag("native_id", t[1]);
          m.addTag(t[1], null);
          m.addTag("chr"+t[0], null);
          int stop = Integer.parseInt(t[4]);
          int start = Integer.parseInt(t[3]);
          m.setStartPosition(start);
          m.setStopPosition(stop);
          
          // hack
          m.setReadCount(2);
          m.setCalledBaseCount(2);
          m.setCalledBaseCountForward(1);
          m.setCalledBaseCountReverse(1);
          
          String[] bases = t[7].split("[/;=]");
          if ("+".equals(t[6])) {
            m.setReferenceBase(bases[0]);
            m.setCalledBase(bases[1]);
          } else if ("-".equals(t[6])) {
            m.setReferenceBase(comp(bases[0]));
            m.setCalledBase(comp(bases[1]));
          }
          m.setType(m.SNV);
          if ("homozygous_SNP".equals(t[2])) {
            m.setZygosity(m.HOMOZYGOUS);
            m.addTag("homozygous", null);
          } else if ("heterozygous_SNP".equals(t[2])) {
            m.setZygosity(m.HETEROZYGOUS);
            m.addTag("heterozygous", null);
          }
          
          store.putMismatch(m);
          m = new Variant();
        }
      }
      
      // put the last coverage object
      // TODO: causes transactional problems so don't use it right now.
      /* if (this.includeCoverage && c != null) {
        store.putCoverage(c);
      }*/

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
  
  private String comp(String nucleotide) throws Exception {
    String start = nucleotide;
    start = start.toUpperCase();
    if ("A".equals(start)) { return "T"; }
    else if ("T".equals(start)) { return "A"; }
    else if ("C".equals(start)) { return "G"; }
    else if ("G".equals(start)) { return "C"; }
    else { throw new Exception("unknown nucleo type "+start); }
  }
}
