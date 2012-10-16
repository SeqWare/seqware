/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.importers.workers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.compress.compressors.CompressorStreamFactory;

import net.sourceforge.seqware.queryengine.backend.model.Coverage;
import net.sourceforge.seqware.queryengine.backend.model.Variant;

/**
 * <p>PileupVariantImportWorker class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class PileupVariantImportWorker extends ImportWorker {


  /**
   * <p>Constructor for PileupVariantImportWorker.</p>
   */
  public PileupVariantImportWorker() { }


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

          // deal with coverage
          // TODO: this will cause transactional problems when multithreaded so I don't use it right now.
          if (includeCoverage && !"*".equals(t[2])) {
            String contig = t[0];
            Integer stopPos = Integer.parseInt(t[1]);
            Integer startPos = stopPos-1;
            Integer currCount = Integer.parseInt(t[7]);

            int bin = startPos / binSize;

            if (c == null || bin != currBin) {
              if (c != null) { 
                // save the previous cov obj
                store.putCoverage(c);
              }
              c = new Coverage();
              currBin = bin;
              c.setContig(contig);
              c.setStartPosition(bin*binSize);
              c.setStopPosition((bin*binSize)+(binSize-1));
            }

            c.putCoverage(startPos, currCount);
          }

          //always record some info in case the next line is an indel and needs it
          if (!"*".equals(t[2]) && t.length == 10) {
            m.setContig(t[0]);
            m.addTag(t[0], null);
            m.setReferenceBase(t[2].toUpperCase());
            m.setConsensusBase(t[3].toUpperCase());
            m.setReadCount(Integer.parseInt(t[7]));
            m.setReadBases(t[8]);
            m.setBaseQualities(t[9]);
          }

          // now populate information for SNV
          if (!"*".equals(t[2]) && t.length == 10 && Integer.parseInt(t[7]) >= minCoverage && Integer.parseInt(t[7]) <= maxCoverage && Integer.parseInt(t[5]) >= minSnpQuality) {

            // type
            m.setType(Variant.SNV);

            // position
            previousPos = t[1];
            Integer pos = Integer.parseInt(t[1]);
            m.setStartPosition(pos-1);
            m.setStopPosition(pos);

            // read qualities
            m.setConsensusCallQuality(Integer.parseInt(t[5]));
            m.setReferenceCallQuality(Integer.parseInt(t[4]));
            m.setMaximumMappingQuality(Integer.parseInt(t[6]));

            // now calculate a bunch of different stats if this is actually going to be stored
            if (includeSNV) {

              // calculates and stores various stats
              parseMismatch(m, fastqConvNum);

              // finally store the records
              store.putMismatch(m); 
              if (count % 10000 == 0) { 
                System.out.println(workerName+": adding mismatch to db: "+m.getContig()+":"+m.getStartPosition()+"-"+m.getStopPosition()+
                    " total records added: "+m.getId()+" total lines so far: "+count);
              }

              // clear out the ID and zygosity and type in case this object is used for an indel
              m.setId(null);
              m.getTags().remove("SNV");
              m.getTags().remove("indel");
              m.getTags().remove("homozygous");
              m.getTags().remove("heterozygous");
            }
          }

          else if (includeIndels && "*".equals(t[2]) && !"*/*".equals(t[3]) && t.length >= 13 && Integer.parseInt(t[7]) >= minCoverage && Integer.parseInt(t[7]) <= maxCoverage && Integer.parseInt(t[5]) >= minSnpQuality) {

            // position
            Integer pos = Integer.parseInt(t[1]);
            m.setStartPosition(pos);
            m.setStopPosition(pos+1);

            // set the base calls
            m.setReferenceBase(t[2].toUpperCase()); // a "*" for indels
            m.setConsensusBase(t[3].toUpperCase()); // needs to be parsed out to be used "\w+/\w+"

            // correct stop position if deletion
            if (m.getConsensusBase().contains("-")) {
              Matcher matcher = p.matcher(m.getConsensusBase());
              matcher.find();
              String lengthStr = matcher.group(1);
              Integer newStopPos = new Integer(pos+lengthStr.length());
              m.setStopPosition(newStopPos);
            }

            // read qualities
            m.setReferenceCallQuality(Integer.parseInt(t[4]));
            m.setConsensusCallQuality(Integer.parseInt(t[5]));
            m.setMaximumMappingQuality(Integer.parseInt(t[6]));

            // Indel-specific
            m.setCallOne(t[8].toUpperCase());
            m.setCallTwo(t[9].toUpperCase());
            m.setReadsSupportingCallOne(Integer.parseInt(t[10]));
            m.setReadsSupportingCallTwo(Integer.parseInt(t[11]));
            m.setReadsSupportingCallThree(Integer.parseInt(t[12]));

            // These really aren't available for the indel since 
            // the base quality is reported for the base upstream of indel even
            m.setConsensusAveSeqQuality((float) 0.0);
            m.setConsensusMaxSeqQuality((float) 0.0);
            m.setReferenceAveSeqQuality((float) 0.0);
            m.setReferenceMaxSeqQuality((float) 0.0);

            // at this point the mismatch object if filled with info from the 
            // previous line plus updated information from this indel line, now
            // parse that info and calculate various stats
            parseIndel(m, fastqConvNum);

            // now store the object
            store.putMismatch(m);
            if (count % 10000 == 0) { 
              System.out.println(workerName+": adding indel to db: "+m.getContig()+":"+m.getStartPosition()+"-"+m.getStopPosition()+
                  " total records added: "+m.getId()+" total lines processed so far: "+count);
            }

            // now prepare for next mismatch
            m = new Variant();
          }
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
      System.out.println("Exception with file: "+input+"\n"+e.getMessage());
      e.printStackTrace();
    } finally {
      pmi.releaseLock();
    }
  }

  /** 
   * This method is actually doing the heavy lifting of parsing a given 
   * indel entry and calculating various stats on it.
   */
  private void parseIndel(Variant m, int fastqConvNum) throws Exception {

    // the read counts, total sequenced depth at this position
    int readCount = m.getReadCount();
    
    // always save type as tag
    m.addTag("indel", null);

    // now figure out if this is homozygous or heterozygous call
    String[] callTokens = m.getConsensusBase().split("/");
    if (callTokens[0].equals(callTokens[1])) {
      // then it's a homozygous call
      m.setZygosity(Variant.HOMOZYGOUS);
      m.addTag("homozygous", null);
    } else {
      m.setZygosity(Variant.HETEROZYGOUS);
      m.addTag("heterozygous", null);
    }

    // figure out ratio of genome to consensus call
    // I've seen consensus reads be off from the indel count!
    // the entry below says there are two indels but I only see support for one in the string
    /*
     * chr12   46499   c       C       32      0       9       60      ,$*,,,..+5CCCCG,,,..,,,$...G,..+12CCCCCCCCCGGA....,.+5GGTGG..,..,,.*,.,.A*,*.,,,...+11CCCCCCCCCCG......,.       B!A9B##A3A##>?B####@######A###=1#AA##;'?###;##A=>###54####A#
         chr12   46499   *       * /+CCCCG        198     198     17      60      *       +CCCCG  56      2       2
     */
    int consensusReads = 0;
    int genomeReads = 0;
    if ("*".equals(m.getCallOne())) {
      genomeReads = m.getReadsSupportingCallOne();
      consensusReads = m.getReadsSupportingCallTwo();
    } else {
      consensusReads = m.getReadsSupportingCallOne();
      genomeReads = m.getReadsSupportingCallTwo();
    }
    // I'm going to set this below based on forward/reverse counts
    //m.setCalledBaseCount(consensusReads);
    //float mutationPercent = (float) (((float)consensusReads / (float)readCount) * 100.0);

    // now figure out if it's an insertion or a deletion
    String eventBases = null;
    if (callTokens[0].startsWith("+")) {
      m.setType(Variant.INSERTION);
      //indelSize = callTokens[0].length() - 1;
      eventBases = callTokens[0].substring(1);
    } else if (callTokens[1].startsWith("+")) {
      m.setType(Variant.INSERTION);
      //indelSize = callTokens[1].length() - 1;
      eventBases = callTokens[1].substring(1);
    } else if (callTokens[0].startsWith("-")) {
      m.setType(Variant.DELETION);
      //indelSize = callTokens[0].length() - 1;
      eventBases = callTokens[0].substring(1);
    }  else if (callTokens[1].startsWith("-")) {
      m.setType(Variant.DELETION);
      //indelSize = callTokens[1].length() - 1;
      eventBases = callTokens[1].substring(1);
    }

    // save called base
    m.setCalledBase(eventBases.toUpperCase());

    // event base length
    int eBaseLength = eventBases.length();

    // flip these if it's a deletion, e.g. ref base would now 
    // be "AATG" and consensus base would be "*" for "*/-AATG"
    if (m.getType() == Variant.DELETION) {
      String oldRefBase = m.getReferenceBase();
      m.setReferenceBase(m.getConsensusBase());
      m.setConsensusBase(oldRefBase);
    }

    // figure out the forward & reverse reads
    String startChr = "\\+"+eBaseLength;
    if (m.getType() == Variant.DELETION) { startChr = "\\-"+eBaseLength; }
    String ucEventBases = startChr+eventBases.toUpperCase();
    String lcEventBases = startChr+eventBases.toLowerCase();
    String readString = m.getReadBases();
    //System.out.println("forward: "+ucEventBases);
    //System.out.println("reverse: "+lcEventBases);
    //System.out.println("readstr: "+readString);
    Pattern pF = Pattern.compile(ucEventBases);
    Pattern pR = Pattern.compile(lcEventBases);

    Matcher mF = pF.matcher(readString);
    Matcher mR = pR.matcher(readString);
    int forwardCnt = 0;
    int reverseCnt = 0;
    while(mF.find()) { forwardCnt++; }
    while(mR.find()) { reverseCnt++; }
    m.setCalledBaseCountForward(forwardCnt);
    m.setCalledBaseCountReverse(reverseCnt);
    int testTotal = forwardCnt + reverseCnt;
    m.setCalledBaseCount(testTotal);
    // FIXME: in some cases it looks like the pileup file is inconsistent in it's
    // indel counts. So I'm going to base total/forward/reverse on the actual string
    // this is the total for previous SNV
    if (testTotal != consensusReads) {
      //System.out.println("string: "+m.getReadBases());
      //System.out.println("fc: "+forwardCnt+" r: "+reverseCnt+" total "+testTotal+" real total "+readCount+" consensus reads: "+consensusReads);
      //throw new Exception("Variant between read count and forward/reverse sum!");
    }

    //double forwardPercent = ((double)forwardCnt/(double)m.getReadCount())*100;
    //double reversePercent = ((double)reverseCnt/(double)m.getReadCount())*100;

  }

  /** 
   * This method is actually doing the heavy lifting of parsing a given 
   * mismatch entry and calculating various stats on it.
   */
  private void parseMismatch(Variant m, int fastqConvNum) throws Exception {

    // actually parse the string now
    // chr12   46499   c       C       32      0       9       60      ,$*,,,..+5CCCCG,,,..,,,$...G,..+12CCCCCCCCCGGA....,.+5GGTGG.. \
    // ,..,,.*,.,.A*,*.,,,...+11CCCCCCCCCCG......,.       B!A9B##A3A##>?B####@######A###=1#AA##;'?###;##A=>###54####A#
    // chr12   46499   *       * /+CCCCG        198     198     17      60      *       +CCCCG  56      2       2
    String[] rbArr = m.getReadBases().split("");

    // always save a tag
    m.addTag("SNV", null);
    
    // vars for storing
    int forwardGenomeCnt = 0;
    int reverseGenomeCnt = 0;
    // indels
    int forwardIndelCnt = 0;
    int reverseIndelCnt = 0;
    // ACGTN
    HashMap<String,Integer> forwardCalledCnts = new HashMap<String,Integer>();
    HashMap<String,Integer> reverseCalledCnts = new HashMap<String,Integer>();

    // vars for quality scores
    int qualPosition = 0;
    int genomeQualMax = 0;
    int genomeQualCnt = 0;
    int genomeQualTotal = 0;
    float genomeQualMean = 0;
    int consQualMax = 0;
    int consQualCnt = 0;
    int consQualTotal = 0;
    float consQualMean = 0;
    char[] qualities = m.getBaseQualities().toCharArray();

    // parse the read string
    int skipCount = 0;
    boolean readingIndel = false;
    StringBuffer countStr = new StringBuffer();
    // start at one since the split function returns "" for the first element of the array
    for(int i=1; i<rbArr.length; i++) {
      if (skipCount > 0) { skipCount--; }
      else {
        if("$".equals(rbArr[i])) {
          // move to the next
        } else if ("^".equals(rbArr[i])) {
          skipCount = 1;
        } else if ("+".equals(rbArr[i]) || "-".equals(rbArr[i])) {
          readingIndel = true;
        } else if (rbArr[i].matches("\\d") && readingIndel) {
          countStr.append(rbArr[i]);
        } else if (readingIndel && rbArr[i].matches("[ACGTN]")) {
          int length = Integer.parseInt(countStr.toString());
          countStr = new StringBuffer();
          skipCount = length - 1;
          forwardIndelCnt++;
          readingIndel = false;
        } else if (readingIndel && rbArr[i].matches("[acgtn]")) {
          int length = Integer.parseInt(countStr.toString());
          countStr = new StringBuffer();
          skipCount = length - 1;
          reverseIndelCnt++;
          readingIndel = false;
        } else if (!readingIndel && ",".equals(rbArr[i])) {
          reverseGenomeCnt++;
          char currQual = qualities[qualPosition];
          int qual = currQual;
          qual = qual - fastqConvNum;
          //System.out.println("Quality for genome F: "+qual);
          if (qual > genomeQualMax) { genomeQualMax = qual; }
          genomeQualCnt++;
          genomeQualTotal += qual;
          qualPosition++;
        } else if (!readingIndel && ".".equals(rbArr[i])) {
          forwardGenomeCnt++;
          char currQual = qualities[qualPosition];
          int qual = currQual;
          qual = qual - fastqConvNum;
          //System.out.println("Quality for genome R: "+qual);
          if (qual > genomeQualMax) { genomeQualMax = qual; }
          genomeQualCnt++;
          genomeQualTotal += qual;
          qualPosition++;
        } else if (!readingIndel && rbArr[i].matches("[ACGTN]")) {
          Integer currCounts = forwardCalledCnts.get(rbArr[i].toUpperCase());
          if (currCounts == null) { currCounts = new Integer(0); }
          currCounts++;
          //System.out.println("putting: "+currCounts+" to "+rbArr[i].toUpperCase());
          forwardCalledCnts.put(rbArr[i].toUpperCase(), currCounts);
          char currQual = qualities[qualPosition];
          int qual = currQual;
          qual = qual - fastqConvNum;
          //System.out.println("Quality for mismatch F: "+qual);
          if (qual > consQualMax) { consQualMax = qual; }
          consQualCnt++;
          consQualTotal += qual;
          qualPosition++;
        } else if (!readingIndel && rbArr[i].matches("[acgtn]")) {
          Integer currCounts = reverseCalledCnts.get(rbArr[i].toUpperCase());
          if (currCounts == null) { currCounts = new Integer(0); }
          currCounts++;
          reverseCalledCnts.put(rbArr[i].toUpperCase(), currCounts);
          char currQual = qualities[qualPosition];
          int qual = currQual;
          qual = qual - fastqConvNum;
          //System.out.println("Quality for mismatch R: "+qual);
          if (qual > consQualMax) { consQualMax = qual; }
          consQualCnt++;
          consQualTotal += qual;
          qualPosition++;
        }
      }
    }

    // figure out what was called
    // IUPAC nucleotide codes: http://www.bioinformatics.org/sms/iupac.html
    byte zygosity = Variant.HETEROZYGOUS;
    int readCount = m.getReadCount();
    String calledBase = m.getConsensusBase();
    if (calledBase.matches("[ATGCU]")) {
      zygosity = Variant.HOMOZYGOUS;
    } else if ("M".equals(calledBase)) {
      if (m.getReferenceBase().equals("A")) { calledBase = "C"; }
      else { calledBase = "A"; }
    } else if ("R".equals(calledBase)) {
      if (m.getReferenceBase().equals("A")) { calledBase = "G"; }
      else { calledBase = "A"; }
    } else if ("W".equals(calledBase)) {
      if (m.getReferenceBase().equals("A")) { calledBase = "T"; }
      else { calledBase = "A"; }
    } else if ("S".equals(calledBase)) {
      if (m.getReferenceBase().equals("C")) { calledBase = "G"; }
      else { calledBase = "C"; }
    } else if ("Y".equals(calledBase)) {
      if (m.getReferenceBase().equals("C")) { calledBase = "T"; }
      else { calledBase = "C"; }
    } else if ("K".equals(calledBase)) {
      if (m.getReferenceBase().equals("G")) { calledBase = "T"; }
      else { calledBase = "G"; }
    } else {
      throw new Exception("Don't know what "+m.getReferenceBase()+"->"+m.getConsensusBase()+" is!!!");
    }

    // now pull back the forward & reverse strand info
    Integer calledBaseForwardCnt = forwardCalledCnts.get(calledBase);
    Integer calledBaseReverseCnt = reverseCalledCnts.get(calledBase);
    if (calledBaseForwardCnt == null) { calledBaseForwardCnt = new Integer(0); }
    if (calledBaseReverseCnt == null) { calledBaseReverseCnt = new Integer(0); }
    int calledCntTotal = calledBaseForwardCnt + calledBaseReverseCnt;
    double calledPercent = ((double)calledCntTotal / (double)readCount) * (double)100.0;
    double calledFwdPercent = ((double)calledBaseForwardCnt / (double)readCount) * (double)100.0;
    double calledRvsPercent = ((double)calledBaseReverseCnt / (double)readCount) * (double)100.0;

    // calc some stats based on quality scores
    genomeQualMean = (float)genomeQualTotal / (float)genomeQualCnt;
    consQualMean = (float)consQualTotal / (float)consQualCnt;

    // store what was just calculated
    m.setCalledBase(calledBase);
    m.setCalledBaseCount(calledCntTotal);
    m.setCalledBaseCountForward(calledBaseForwardCnt);
    m.setCalledBaseCountReverse(calledBaseReverseCnt);
    int totalCount = m.getCalledBaseCountForward() + m.getCalledBaseCountReverse();
    if (m.getCalledBaseCount() != totalCount) {
      throw new Exception ("Count mismatch!");
    }
    m.setZygosity(zygosity);
    if (zygosity == Variant.HOMOZYGOUS) { m.addTag("homozygous", null); }
    else if (zygosity == Variant.HETEROZYGOUS) { m.addTag("heterozygous", null); }
    // finish setting up the extra fields
    m.setReferenceMaxSeqQuality(genomeQualMax);
    m.setReferenceAveSeqQuality(genomeQualMean);
    m.setConsensusMaxSeqQuality(consQualMax);
    m.setConsensusAveSeqQuality(consQualMean);

  }

  
  
}
