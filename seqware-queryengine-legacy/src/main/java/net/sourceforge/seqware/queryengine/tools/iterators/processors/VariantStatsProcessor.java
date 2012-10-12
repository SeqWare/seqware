/**
 * 
 */
package net.sourceforge.seqware.queryengine.tools.iterators.processors;

import java.util.HashMap;

import net.sourceforge.seqware.queryengine.backend.model.Variant;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;

/**
 * <p>VariantStatsProcessor class.</p>
 *
 * @author boconnor
 *net.sourceforge.seqware.queryengine.tools.iterators.processors.VariantStatsProcessor
 * @version $Id: $Id
 */
public class VariantStatsProcessor extends VariantProcessor implements ProcessorInterface {

  // vars
  // indels
  HashMap<Integer,Integer> deletionSizesInCoding = new HashMap<Integer,Integer>();
  HashMap<Integer,Integer> deletionSizesOutsideCoding = new HashMap<Integer,Integer>();
  HashMap<Integer,Integer> insertionSizesInCoding = new HashMap<Integer,Integer>();
  HashMap<Integer,Integer> insertionSizesOutsideCoding = new HashMap<Integer,Integer>();
  // snv
  HashMap<String, Integer> subFrequencies = new HashMap<String, Integer>();
  HashMap<String, Integer> subFreqCoding = new HashMap<String, Integer>();
  HashMap<String, Integer> subFreqCodingAA = new HashMap<String, Integer>();
  HashMap<String, Integer> subFreqCodingSynon = new HashMap<String, Integer>();
  HashMap<String, Integer> subFreqCodingAndUTR = new HashMap<String, Integer>();
  HashMap<String, Integer> subFreqNotCoding = new HashMap<String, Integer>();

  /**
   * <p>Constructor for VariantStatsProcessor.</p>
   *
   * @param outputFilename a {@link java.lang.String} object.
   * @param includeIndels a boolean.
   * @param includeSNVs a boolean.
   * @param minCoverage a int.
   * @param maxCoverage a int.
   * @param minObservations
   * @param minObservationsPerStrand
   * @param minObservationsPerStrand a int.
   * @param minSNVPhred a int.
   * @param minPercent a int.
   * @param store a {@link net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore} object.
   */
  public VariantStatsProcessor(String outputFilename, BerkeleyDBStore store, boolean includeIndels,
      boolean includeSNVs, int minCoverage, int maxCoverage,
      int minObservations, int minObservationsPerStrand, int minSNVPhred,
      int minPercent) {
    super(outputFilename, store, includeIndels, includeSNVs, minCoverage, maxCoverage,
        minObservations, minObservationsPerStrand, minSNVPhred, minPercent);
    // TODO Auto-generated constructor stub
  }
  
  /**
   * <p>Constructor for VariantStatsProcessor.</p>
   */
  public VariantStatsProcessor() {
    
  }

  /** {@inheritDoc} */
  public Object process (Object obj) {

    // the mismatch object
    Variant m = (Variant) obj;

    // first, make sure it passes
    if (m != null && (((m.getType() == m.INSERTION || m.getType() == m.DELETION) && includeIndels) || (m.getType() == m.SNV && includeSNVs)) &&
        m.getReadCount() >= minCoverage && m.getReadCount() <= maxCoverage && m.getCalledBaseCount() >= minObservations &&
        (m.getCalledBaseCountForward() >= minObservationsPerStrand && m.getCalledBaseCountReverse() >= minObservationsPerStrand) &&
        m.getConsensusCallQuality() >= minSNVPhred) {

      double calledPercent = ((double)m.getCalledBaseCount() / (double)m.getReadCount()) * (double)100.0;
      if (calledPercent >= minPercent) {

        // then it's met all the filtering criteria

        // collect stats about indel sizes
        if (m.getType() == m.INSERTION || m.getType() == m.DELETION) {

          // figure out if it's coding
          if (m.getTags().containsKey("coding-nonsynonymous") || 
              m.getTags().containsKey("early-termination") || 
              m.getTags().containsKey("frameshift") || 
              m.getTags().containsKey("inframe-indel") || 
              //m.getTags().containsKey("intron-splice-site-mutation") || 
              m.getTags().containsKey("start-codon-loss") || 
              m.getTags().containsKey("stop-codon-loss") || 
              m.getTags().containsKey("coding-synonymous")
          ) {
            // keep track of the indel size
            if (m.getType() == m.INSERTION) {
              //System.out.println(m.getReferenceBase()+"->"+m.getCalledBase()+" length: "+m.getCalledBase().length());
              updateCount(insertionSizesInCoding, m.getCalledBase().length());
            } else if (m.getType() == m.DELETION) {
              //System.out.println(m.getReferenceBase()+"->"+m.getCalledBase()+" length: "+m.getCalledBase().length());
              updateCount(deletionSizesInCoding, m.getCalledBase().length());
            }
          } 
          // then it's not coding
          else {
            // keep track of the indel size
            if (m.getType() == m.INSERTION) {
              updateCount(insertionSizesOutsideCoding, m.getCalledBase().length());
            } else if (m.getType() == m.DELETION) {
              updateCount(deletionSizesOutsideCoding, m.getCalledBase().length());
            }
          }
        }

        // collect mutation substitution stats
        if (m.getType() == m.SNV) {
          
          // overall
          updateCountWithKey(subFrequencies, m.getReferenceBase()+"->"+m.getCalledBase());

          // coding sequence
          if (m.getTags().containsKey("coding-nonsynonymous") || 
              m.getTags().containsKey("early-termination") || 
              m.getTags().containsKey("intron-splice-site-mutation") || 
              m.getTags().containsKey("start-codon-loss") || 
              m.getTags().containsKey("stop-codon-loss") || 
              m.getTags().containsKey("coding-synonymous")) {
            updateCountWithKey(subFreqCoding, m.getReferenceBase()+"->"+m.getCalledBase());
          }
          
          // coding sequence affecting aa
          if (m.getTags().containsKey("coding-nonsynonymous") || 
              m.getTags().containsKey("early-termination") || 
              m.getTags().containsKey("start-codon-loss") || 
              m.getTags().containsKey("stop-codon-loss")) {
            updateCountWithKey(subFreqCodingAA, m.getReferenceBase()+"->"+m.getCalledBase());
          }
          
          // coding sequence synonymous
          if (m.getTags().containsKey("coding-synonymous")) {
            updateCountWithKey(subFreqCodingSynon, m.getReferenceBase()+"->"+m.getCalledBase());
          }
          
          // coding sequence + UTR and introns FIXME: didn't really annotate introns!
          if (m.getTags().containsKey("coding-nonsynonymous") || 
              m.getTags().containsKey("early-termination") || 
              m.getTags().containsKey("intron-splice-site-mutation") || 
              m.getTags().containsKey("start-codon-loss") || 
              m.getTags().containsKey("stop-codon-loss") || 
              m.getTags().containsKey("coding-synonymous") ||
              m.getTags().containsKey("utr-mutation")) {
            updateCountWithKey(subFreqCodingAndUTR, m.getReferenceBase()+"->"+m.getCalledBase());
          }
          
          // not coding sequence
          if (!m.getTags().containsKey("coding-nonsynonymous") && 
              !m.getTags().containsKey("early-termination") && 
              !m.getTags().containsKey("intron-splice-site-mutation") && 
              !m.getTags().containsKey("start-codon-loss") &&
              !m.getTags().containsKey("stop-codon-loss") && 
              !m.getTags().containsKey("coding-synonymous") &&
              !m.getTags().containsKey("utr-mutation")) {
            updateCountWithKey(subFreqNotCoding, m.getReferenceBase()+"->"+m.getCalledBase());
          }
        }
      }
    }
    return(null);
  }
  
  /** {@inheritDoc} */
  public String report(Object obj) {
    
    StringBuffer sb = new StringBuffer();
    
    // indels
    sb.append("Deletion Sizes Inside Coding:\n");
    for (Integer size : deletionSizesInCoding.keySet()) {
      sb.append(size+"\t"+deletionSizesInCoding.get(size)+"\n");
    }
    sb.append("Deletion Sizes Outside Coding:\n");
    for (Integer size : deletionSizesOutsideCoding.keySet()) {
      sb.append(size+"\t"+deletionSizesOutsideCoding.get(size)+"\n");
    }
    sb.append("Insertion Sizes Inside Coding:\n");
    for (Integer size : insertionSizesInCoding.keySet()) {
      sb.append(size+"\t"+insertionSizesInCoding.get(size)+"\n");
    }
    sb.append("Insertion Sizes Outside Coding:\n");
    for (Integer size : insertionSizesOutsideCoding.keySet()) {
      sb.append(size+"\t"+insertionSizesOutsideCoding.get(size)+"\n");
    }
    sb.append("\n");
    
    // snvs
    sb.append("Substitution Frequency Overall\n");
    for (String key : subFrequencies.keySet()) {
      sb.append(key+"\t"+subFrequencies.get(key)+"\n");
    }
    sb.append("Substitution Frequency Coding Sequence\n");
    for (String key : subFreqCoding.keySet()) {
      sb.append(key+"\t"+subFreqCoding.get(key)+"\n");
    }
    sb.append("Substitution Frequency Coding Sequence Affecting AA\n");
    for (String key : subFreqCodingAA.keySet()) {
      sb.append(key+"\t"+subFreqCodingAA.get(key)+"\n");
    }
    sb.append("Substitution Frequency Coding Sequence Synonymous\n");
    for (String key : subFreqCodingSynon.keySet()) {
      sb.append(key+"\t"+subFreqCodingSynon.get(key)+"\n");
    }
    sb.append("Substitution Frequency Coding Sequence Plus UTR\n");
    for (String key : subFreqCodingAndUTR.keySet()) {
      sb.append(key+"\t"+subFreqCodingAndUTR.get(key)+"\n");
    }
    sb.append("Substitution Frequency Not Transcribed Sequence\n");
    for (String key : subFreqNotCoding.keySet()) {
      sb.append(key+"\t"+subFreqNotCoding.get(key)+"\n");
    }
    
    return(sb.toString());
  }
  
  private void updateCount(HashMap<Integer,Integer> hm, int size) {
    Integer count = hm.get(size);
    if (count == null) { count = new Integer(1); }
    else { count++; }
    hm.put(size, count);
  }
  
  private void updateCountWithKey(HashMap<String,Integer> hm, String key) {
    Integer count = hm.get(key);
    if (count == null) { count = new Integer(1); }
    else { count++; }
    hm.put(key, count);
  }

}
