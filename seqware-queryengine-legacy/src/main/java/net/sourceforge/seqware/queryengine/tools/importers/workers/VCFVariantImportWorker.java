/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.importers.workers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import org.apache.commons.compress.compressors.CompressorStreamFactory;

import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Variant;

/**
 * <p>VCFVariantImportWorker class.</p>
 *
 * @author boconnor
 *
 * FIXME: need to support indels
 * FIXME: need to support alternative alleles, each should get its own variant object I think
 * @version $Id: $Id
 */
public class VCFVariantImportWorker extends ImportWorker {


  /**
   * <p>Constructor for VCFVariantImportWorker.</p>
   */
  public VCFVariantImportWorker() { }


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

          // load the variant object

          m.setContig(t[0]);
          if (!t[0].startsWith("chr")) {
            m.setContig("chr"+t[0]);
          }
          m.addTag(t[0], null);
          m.setReferenceBase(t[3].toUpperCase());
          m.setConsensusBase(t[4].toUpperCase());
          m.setCalledBase(t[4].toUpperCase());
          
          // figure out the consensusCallQuality
          m.setConsensusCallQuality(Float.parseFloat(t[5]));
          
          // parse ID
          if (!".".equals(t[2])) {
            m.addTag("ID", t[2]);
          }
          if (!".".equals(t[2])) {
            m.addTag(t[2], null);
          }
          
          // FIXME: only supports two alleles for now, see http://users.ox.ac.uk/~linc1775/blueprint.htm
          // if there are multiple alleles then both the consensus and called bases should be 
          String calledBase = m.getCalledBase();
          if (t[4].toUpperCase().length() > 1 && t[4].toUpperCase().contains(",")) {
            if ("C,A".equals(calledBase) || "A,C".equals(calledBase)) {
              calledBase = "M";
            } else if ("A,G".equals(calledBase) || "G,A".equals(calledBase)) {
              calledBase = "R";
            } else if ("A,T".equals(calledBase) || "T,A".equals(calledBase)) {
              calledBase = "W";
            } else if ("C,G".equals(calledBase) || "G,C".equals(calledBase)) {
              calledBase = "S";
            } else if ("C,T".equals(calledBase) || "T,C".equals(calledBase)) {
              calledBase = "Y";
            } else if ("G,T".equals(calledBase) || "T,G".equals(calledBase)) {
              calledBase = "K";
            } else {
              // this doesn't work when consensus base comes back like:
              // TGCACGTCA,TAA 
              //throw new Exception("Don't know what "+m.getReferenceBase()+"->"+m.getConsensusBase()+" is!!!");
            }
            m.setCalledBase(calledBase);
            // leave the consensus base as the original call syntax from the VCF file
          }
          
          /* I've seen another alternative way of representing alleles where the FQ and AF1 can be used to caall homozygous
           * From http://seqanswers.com/forums/showthread.php?t=11651
           * In English, the first line means "check the FQ"; the FQ is negative when the SNP is homozygous, and positive when it's mixed, and the bigger the absolute value, the more confident the SNP.

So if it's < 0, it does the first part of code (it checks against the AF1, and if the AF1 is > 0.5, which it should be for a homozygous SNP, it sets $b as the alternate letter, if for some reason the AF1 is < .5, it sets $b as the old reference letter.)

If the FQ is positive, then the SNP should be mixed, and it concatenates the two letters, and checks the hash above to know what single letter to set $b to. Then it adds $b to the growing sequence.

$q, which is derived from the FQ, ends up being the quality score, though it gets tweaked a little; it adds 33.449 to the figure, then converts it to a letter, capping it at a quality of 126.

$q = int($q + 33 + .499);
$q = chr($q <= 126? $q : 126);

Gaps are handled as they were in the old program, where they are NOT added in, there is just a window of lowercase letters around them. Personally, I made a little perl script, and I feed it the genome, and a conservatively filtered list of SNPs, and I put the changes in that way. 
           */

          // FIXME: hard-coded for now
          m.setType(Variant.SNV);
          // always save a tag
          m.addTag("SNV", null);
          Integer pos = Integer.parseInt(t[1]);
          m.setStartPosition(pos-1);
          m.setStopPosition(pos);
          
          // now parse field 8
          m.addTag(t[6], null);
          
          // if FQ is < 0 and AF1 < 0.5 then the algorithm is calling homozygous reference so skip
          boolean af1LtHalf = false;
          boolean fqLt0 = false;
         
          String[] tags = t[7].split(";");
          for (String tag : tags) {
            if (tag.contains("=")) {
              String[] kv = tag.split("=");
              m.addTag(kv[0], kv[1]);
              if ("DP".equals(kv[0])) {
                m.setReadCount(Integer.parseInt(kv[1]));
              }
              // see above
              if ("FQ".equals(kv[0])) {
                float fq = Float.parseFloat(kv[1]);
                if (fq < 0) { 
                  m.setZygosity(m.HOMOZYGOUS); 
                  m.getTags().put("homozygous", null);
                  fqLt0 = true;
                }
                else { m.setZygosity(m.HETEROZYGOUS); m.getTags().put("heterozygous", null); }
              }
              if ("AF1".equals(kv[0])) {
                float af1 = Float.parseFloat(kv[1]);
                if (af1 < 0.5) {
                  af1LtHalf = true;
                }
              }
            } else {
              m.addTag(tag, null);
            }
          }
          
          // yet another way to encode hom/het
          // FIXME: this doesn't conform to the standard
          if (t.length > 9 && t[8].contains("GT") && t[9].contains("het")) {
            m.setZygosity(m.HETEROZYGOUS); m.getTags().put("heterozygous", null);
          } else if (t.length > 9 && t[8].contains("GT") && t[9].contains("hom")) {
            m.setZygosity(m.HOMOZYGOUS); 
            m.getTags().put("homozygous", null);
          }
          
          // if this is true then it's just being called homozygous reference so don't even store
          if (af1LtHalf && fqLt0) {
            System.out.println("Dropping variant because FQ < 0 and AF1 < 0.5!");
          }
          else {
            store.putMismatch(m); 
          }
          
          if (count % 10000 == 0) { 
            System.out.println(workerName+": adding mismatch to db: "+m.getContig()+":"+m.getStartPosition()+"-"+m.getStopPosition()+
                " total records added: "+m.getId()+" total lines so far: "+count);
          }
          
          // now prepare for next mismatch
          m = new Variant();
        }
      }
      
      // close file
      inputStream.close();
      System.out.print("\n");
      
    } catch (Exception e) {
      System.out.println("Exception with file: "+input+"\n"+e.getMessage());
      e.printStackTrace();
    } finally {
      pmi.releaseLock();
    }
  }

}
