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
 * A simple worker thread to parse the file http://yh.genomics.org.cn/do.downServlet?file=data/snps/yhsnp_add.gff
 * These are the SNV calls from the YanHuang genome, see http://yh.genomics.org.cn for more information
 * From their README:
 * 
SNP gff3:
Col 1: chromosome ID
Col 2: source of result derived from (for SNP gff3 always "SoapSNP")
Col 3: type of item (for SNP gff always "SNP")
Col 4: start (SNP position)
Col 5: end (certainly it is same to start in SNP)
Col 6: quality score in phred unit
Col 7: strand (always "+" because of method)
Col 8: phase of SNPs, only available if the SNP is in the coding region
Col 9: this field contain some sub-fields separated by space
  ID: the unique ID of a SNP. "rs***" is for SNP in dbSNP, “NOM1_” for novel SNPs.
  status: if the SNP is known or novel? "dbSNP" is for those in NCBI dbSNP dataset and "novel" for those not found in dbSNP
  ref: reference base of NCBI at the site
  allele: Diploid alleles of on this position
  support1: number of reads support for first allele.
  support2: number of reads support for second allele
  location: annotated region where the SNP located
 * 
 */
public class YanHuangGenomeVariantImportWorker extends ImportWorker {


  public YanHuangGenomeVariantImportWorker() { }


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
        if (!l.startsWith("#")) {
          
          // the input file is 1-based
          // chr1    SoapSNP SNP     556955  556955  36      +       .       ID=rs9326622; status=dbSNP; ref=T; allele=C/T; support1=28; support2=8;
          // corresponds to chr1:556954-556955
          // in the genome browser this is a "T" and the dbSNP record says its T -> C
          // I do see this over the correct dbSNP entry in the UCSC browser
          // genome contains novel SNVs
          
          m.setContig(t[0]);
          m.addTag(t[0], null);
          m.setType(m.SNV);
          int stop = Integer.parseInt(t[4]);
          int start = stop - 1;
          m.setStartPosition(start);
          m.setStopPosition(stop);
          m.setConsensusCallQuality(Float.parseFloat(t[5]));
          String[] info = t[8].split(" ");
          String[] status = info[1].split("[=;]");
          String[] id = info[0].split("[=;]");
          if ("dbSNP".equals(status[1])) {
            m.addTag("is_dbSNP", id[1]);
            m.addTag(id[1], null);
          } else if ("novel".equals(status[1])) {
            m.addTag("not_dbSNP", "novel");
            m.addTag(id[1], null);
            m.addTag("native_id", id[1]);
          }
          String[] ref = info[2].split("[=;]");
          m.setReferenceBase(ref[1]);
          String[] allele = info[3].split("[=;]");
          String[] alleles = allele[1].split("/");
          String[] count1 = info[4].split("[=;]");
          if (alleles[0].equals(alleles[1])) {
            m.setZygosity(m.HOMOZYGOUS);
            m.setCalledBase(alleles[1]);
            m.setConsensusBase(alleles[1]);
            m.setCalledBaseCount(Integer.parseInt(count1[1]));
            m.setCalledBaseCountForward(Integer.parseInt(count1[1]));
            m.setReadCount(Integer.parseInt(count1[1]));
          } else if (!alleles[0].equals(alleles[1])) {
            m.setZygosity(m.HETEROZYGOUS);
            String[] count2 = info[5].split("[=;]");
            if (alleles[0].equals(ref[1])) {
              m.setCalledBase(alleles[1]);
              m.setCalledBaseCount(Integer.parseInt(count2[1]));
              m.setCalledBaseCountForward(Integer.parseInt(count2[1]));
              m.setReadCount(Integer.parseInt(count1[1])+Integer.parseInt(count2[1]));
            } else {
              m.setCalledBaseCount(Integer.parseInt(count1[1]));
              m.setCalledBaseCountForward(Integer.parseInt(count1[1]));
              m.setReadCount(Integer.parseInt(count1[1])+Integer.parseInt(count2[1]));
              m.setCalledBase(alleles[0]);
            }
          } else {
            throw new Exception("can't tell het/homo."); 
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
