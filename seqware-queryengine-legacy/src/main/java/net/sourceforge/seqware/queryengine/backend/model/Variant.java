/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author boconnor
 * 
 * Variant class modeled pretty heavily on the output from SamTools 0.1.3 pileup format ("samtools pileup -c").
 * In the future this class will actually represent interpreted information from the pileup output
 * such as hetero/homozygous calls, insertion/deletion calls, etc.
 * 
 * Just to be really clear, the pileup output uses consensus base to mean non-reference
 * and the snpQuality to represent its quality.  ConsensusQuality refers to the reference genome
 * base quality.
 * 
 * For the purposes of this API, referenceBase is the genome base at that position, consensusBase
 * is what the SNP/indel caller called (e.g. K, S, W etc for hetero or A, T, etc for homo),
 * calledBase is the observed non-reference base, referenceQuality is the phred score for calling
 * this position as the reference base while consensusQuality is the score for calling the 
 * consensusBase.
 * 
 */
public class Variant extends LocatableModel {

  // constants
  public static final byte UNKNOWN_ZYGOSITY = 0;
  public static final byte HOMOZYGOUS = 1;
  public static final byte HETEROZYGOUS = 2;
  public static final byte HEMIZYGOUS = 3;
  public static final byte NULLIZYGOUS = 4;
  public static final byte UNKNOWN_TYPE = 0;
  public static final byte SNV = 1;
  public static final byte INSERTION = 2;
  public static final byte DELETION = 3;
  public static final byte SV = 4;
  public static final byte TRANSLOCATION = 5;  
  // SV types
  public static final byte SV_UNKNOWN = 0;
  public static final byte SV_COMPLEX = 1;
  public static final byte SV_DELETION = 2;
  public static final byte SV_DELETION_AND_INVERSION = 3;
  public static final byte SV_DISPERSED_DUPLICATION = 4;
  public static final byte SV_DUPLICATION_AND_INVERSION = 5;
  public static final byte SV_INVERSION = 6;
  public static final byte SV_TANDEM_DUPLICATION = 7;
  public static final byte SV_LOCATION_UNKNOWN = 0;
  public static final byte SV_LOCATION_UPSTREAM = 1;
  public static final byte SV_LOCATION_DOWNSTREAM = 2;
  // translocation types
  public static final byte TRANSLOCATION_UNKNOWN = 0;
  public static final byte TRANSLOCATION_INTRACHR = 1;
  public static final byte TRANSLOCATION_INTERCHR = 2;
  public static final byte TRANSLOCATION_COMPLEX = 3;  

  // generic to all types of mismatches
  protected String contig = "";
  protected int startPosition = 0;
  protected int stopPosition = 0;
  protected int fuzzyStartPositionMax = 0;
  protected int fuzzyStopPositionMin = 0;
  // genome base
  protected String referenceBase = "";
  // this is what the SNP callers is calling (may be symbol that represents heterozygous bases)
  protected String consensusBase = "";
  // the non-reference base translated, eg A->C, A would be referenceBase, C would be 
  protected String calledBase = "";
  protected float referenceCallQuality = 0;
  protected float consensusCallQuality = 0;
  protected float maximumMappingQuality = 0;
  protected int readCount = 0;
  // tells you what type of records this is: 0=snv, 1=indel
  protected byte type = UNKNOWN_TYPE;
  // SNV-specific
  protected String readBases = "";
  protected String baseQualities = "";
  protected int calledBaseCount = 0;
  protected int calledBaseCountForward = 0;
  protected int calledBaseCountReverse = 0;
  protected byte zygosity = UNKNOWN_ZYGOSITY;
  protected float referenceMaxSeqQuality = (float)0.0;
  protected float referenceAveSeqQuality = (float)0.0;
  protected float consensusMaxSeqQuality = (float)0.0;
  protected float consensusAveSeqQuality = (float)0.0;
  // Indel-specific
  protected String callOne = "";
  protected String callTwo = "";
  protected int readsSupportingCallOne = 0;
  protected int readsSupportingCallTwo = 0;
  protected int readsSupportingCallThree = 0;
  // SV-specific
  protected byte svType = 0;
  protected byte relativeLocation = 0;
  // translocation-specific
  protected byte translocationType = 0;
  protected String translocationDestinationContig = "";
  protected int translocationDestinationStartPosition = 0;
  protected int translocationDestinationStopPosition = 0;
  
  // tags
  protected HashMap <String, String>tags = new HashMap<String, String>();

  // Custom methods
  public void addTag (String key, String value) {
    tags.put(key, value);
  }

  public String getTagValue (String key) {
    return(tags.get(key));
  }

  // Generated Methods
  
  public float getReferenceCallQuality() {
    return referenceCallQuality;
  }

  public byte getTranslocationType() {
    return translocationType;
  }

  public void setTranslocationType(byte translocationType) {
    this.translocationType = translocationType;
  }

  public String getTranslocationDestinationContig() {
    return translocationDestinationContig;
  }

  public void setTranslocationDestinationContig(
      String translocationDestinationContig) {
    this.translocationDestinationContig = translocationDestinationContig;
  }

  public int getTranslocationDestinationStartPosition() {
    return translocationDestinationStartPosition;
  }

  public void setTranslocationDestinationStartPosition(
      int translocationDestinationStartPosition) {
    this.translocationDestinationStartPosition = translocationDestinationStartPosition;
  }

  public int getTranslocationDestinationStopPosition() {
    return translocationDestinationStopPosition;
  }

  public void setTranslocationDestinationStopPosition(
      int translocationDestinationStopPosition) {
    this.translocationDestinationStopPosition = translocationDestinationStopPosition;
  }

  public int getFuzzyStartPositionMax() {
    return fuzzyStartPositionMax;
  }

  public void setFuzzyStartPositionMax(int fuzzyStartPositionMax) {
    this.fuzzyStartPositionMax = fuzzyStartPositionMax;
  }

  public int getFuzzyStopPositionMin() {
    return fuzzyStopPositionMin;
  }

  public void setFuzzyStopPositionMin(int fuzzyStopPositionMin) {
    this.fuzzyStopPositionMin = fuzzyStopPositionMin;
  }

  public byte getSvType() {
    return svType;
  }

  public void setSvType(byte svType) {
    this.svType = svType;
  }

  public byte getRelativeLocation() {
    return relativeLocation;
  }

  public void setRelativeLocation(byte relativeLocation) {
    this.relativeLocation = relativeLocation;
  }

  public void setReferenceCallQuality(float referenceCallQuality) {
    this.referenceCallQuality = referenceCallQuality;
  }
  public float getConsensusCallQuality() {
    return consensusCallQuality;
  }
  public void setConsensusCallQuality(float consensusCallQuality) {
    this.consensusCallQuality = consensusCallQuality;
  }
  public float getMaximumMappingQuality() {
    return maximumMappingQuality;
  }
  public void setMaximumMappingQuality(float maximumMappingQuality) {
    this.maximumMappingQuality = maximumMappingQuality;
  }
  public float getReferenceMaxSeqQuality() {
    return referenceMaxSeqQuality;
  }
  public void setReferenceMaxSeqQuality(float referenceMaxSeqQuality) {
    this.referenceMaxSeqQuality = referenceMaxSeqQuality;
  }
  public float getReferenceAveSeqQuality() {
    return referenceAveSeqQuality;
  }
  public void setReferenceAveSeqQuality(float referenceAveSeqQuality) {
    this.referenceAveSeqQuality = referenceAveSeqQuality;
  }
  public float getConsensusMaxSeqQuality() {
    return consensusMaxSeqQuality;
  }
  public void setConsensusMaxSeqQuality(float consensusMaxSeqQuality) {
    this.consensusMaxSeqQuality = consensusMaxSeqQuality;
  }
  public float getConsensusAveSeqQuality() {
    return consensusAveSeqQuality;
  }
  public void setConsensusAveSeqQuality(float consensusAveSeqQuality) {
    this.consensusAveSeqQuality = consensusAveSeqQuality;
  }
  public byte getZygosity() {
    return zygosity;
  }
  public void setZygosity(byte zygosity) {
    this.zygosity = zygosity;
  }
  public int getCalledBaseCountForward() {
    return calledBaseCountForward;
  }
  public void setCalledBaseCountForward(int calledBaseCountForward) {
    this.calledBaseCountForward = calledBaseCountForward;
  }
  public int getCalledBaseCountReverse() {
    return calledBaseCountReverse;
  }
  public void setCalledBaseCountReverse(int calledBaseCountReverse) {
    this.calledBaseCountReverse = calledBaseCountReverse;
  }
  public int getCalledBaseCount() {
    return calledBaseCount;
  }
  public void setCalledBaseCount(int calledBaseCount) {
    this.calledBaseCount = calledBaseCount;
  }
  public String getContig() {
    return contig;
  }
  public void setContig(String contig) {
    this.contig = contig;
  }
  public int getStartPosition() {
    return startPosition;
  }
  public void setStartPosition(int startPosition) {
    this.startPosition = startPosition;
  }
  public int getStopPosition() {
    return stopPosition;
  }
  public void setStopPosition(int stopPosition) {
    this.stopPosition = stopPosition;
  }
  public String getReferenceBase() {
    return referenceBase;
  }
  public void setReferenceBase(String referenceBase) {
    this.referenceBase = referenceBase;
  }
  public String getConsensusBase() {
    return consensusBase;
  }
  public void setConsensusBase(String consensusBase) {
    this.consensusBase = consensusBase;
  }
  public String getCalledBase() {
    return calledBase;
  }
  public void setCalledBase(String calledBase) {
    this.calledBase = calledBase;
  }
  public void setMaximumMappingQuality(int maximumMappingQuality) {
    this.maximumMappingQuality = maximumMappingQuality;
  }
  public int getReadCount() {
    return readCount;
  }
  public void setReadCount(int readCount) {
    this.readCount = readCount;
  }
  public byte getType() {
    return type;
  }
  public void setType(byte type) {
    this.type = type;
  }
  public String getReadBases() {
    return readBases;
  }
  public void setReadBases(String readBases) {
    this.readBases = readBases;
  }
  public String getBaseQualities() {
    return baseQualities;
  }
  public void setBaseQualities(String baseQualities) {
    this.baseQualities = baseQualities;
  }
  public String getCallOne() {
    return callOne;
  }
  public void setCallOne(String callOne) {
    this.callOne = callOne;
  }
  public String getCallTwo() {
    return callTwo;
  }
  public void setCallTwo(String callTwo) {
    this.callTwo = callTwo;
  }
  public int getReadsSupportingCallOne() {
    return readsSupportingCallOne;
  }
  public void setReadsSupportingCallOne(int readsSupportingCallOne) {
    this.readsSupportingCallOne = readsSupportingCallOne;
  }
  public int getReadsSupportingCallTwo() {
    return readsSupportingCallTwo;
  }
  public void setReadsSupportingCallTwo(int readsSupportingCallTwo) {
    this.readsSupportingCallTwo = readsSupportingCallTwo;
  }
  public int getReadsSupportingCallThree() {
    return readsSupportingCallThree;
  }
  public void setReadsSupportingCallThree(int readsSupportingCallThree) {
    this.readsSupportingCallThree = readsSupportingCallThree;
  }
  public HashMap<String, String> getTags() {
    return tags;
  }
  public void setTags(HashMap<String, String> tags) {
    this.tags = tags;
  }

}
