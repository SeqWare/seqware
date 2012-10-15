/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Variant class.</p>
 *
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
 * @version $Id: $Id
 */
public class Variant extends LocatableModel {

  // constants
  /** Constant <code>UNKNOWN_ZYGOSITY=0</code> */
  public static final byte UNKNOWN_ZYGOSITY = 0;
  /** Constant <code>HOMOZYGOUS=1</code> */
  public static final byte HOMOZYGOUS = 1;
  /** Constant <code>HETEROZYGOUS=2</code> */
  public static final byte HETEROZYGOUS = 2;
  /** Constant <code>HEMIZYGOUS=3</code> */
  public static final byte HEMIZYGOUS = 3;
  /** Constant <code>NULLIZYGOUS=4</code> */
  public static final byte NULLIZYGOUS = 4;
  /** Constant <code>UNKNOWN_TYPE=0</code> */
  public static final byte UNKNOWN_TYPE = 0;
  /** Constant <code>SNV=1</code> */
  public static final byte SNV = 1;
  /** Constant <code>INSERTION=2</code> */
  public static final byte INSERTION = 2;
  /** Constant <code>DELETION=3</code> */
  public static final byte DELETION = 3;
  /** Constant <code>SV=4</code> */
  public static final byte SV = 4;
  /** Constant <code>TRANSLOCATION=5</code> */
  public static final byte TRANSLOCATION = 5;  
  // SV types
  /** Constant <code>SV_UNKNOWN=0</code> */
  public static final byte SV_UNKNOWN = 0;
  /** Constant <code>SV_COMPLEX=1</code> */
  public static final byte SV_COMPLEX = 1;
  /** Constant <code>SV_DELETION=2</code> */
  public static final byte SV_DELETION = 2;
  /** Constant <code>SV_DELETION_AND_INVERSION=3</code> */
  public static final byte SV_DELETION_AND_INVERSION = 3;
  /** Constant <code>SV_DISPERSED_DUPLICATION=4</code> */
  public static final byte SV_DISPERSED_DUPLICATION = 4;
  /** Constant <code>SV_DUPLICATION_AND_INVERSION=5</code> */
  public static final byte SV_DUPLICATION_AND_INVERSION = 5;
  /** Constant <code>SV_INVERSION=6</code> */
  public static final byte SV_INVERSION = 6;
  /** Constant <code>SV_TANDEM_DUPLICATION=7</code> */
  public static final byte SV_TANDEM_DUPLICATION = 7;
  /** Constant <code>SV_LOCATION_UNKNOWN=0</code> */
  public static final byte SV_LOCATION_UNKNOWN = 0;
  /** Constant <code>SV_LOCATION_UPSTREAM=1</code> */
  public static final byte SV_LOCATION_UPSTREAM = 1;
  /** Constant <code>SV_LOCATION_DOWNSTREAM=2</code> */
  public static final byte SV_LOCATION_DOWNSTREAM = 2;
  // translocation types
  /** Constant <code>TRANSLOCATION_UNKNOWN=0</code> */
  public static final byte TRANSLOCATION_UNKNOWN = 0;
  /** Constant <code>TRANSLOCATION_INTRACHR=1</code> */
  public static final byte TRANSLOCATION_INTRACHR = 1;
  /** Constant <code>TRANSLOCATION_INTERCHR=2</code> */
  public static final byte TRANSLOCATION_INTERCHR = 2;
  /** Constant <code>TRANSLOCATION_COMPLEX=3</code> */
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
  /**
   * <p>addTag.</p>
   *
   * @param key a {@link java.lang.String} object.
   * @param value a {@link java.lang.String} object.
   */
  public void addTag (String key, String value) {
    tags.put(key, value);
  }

  /**
   * <p>getTagValue.</p>
   *
   * @param key a {@link java.lang.String} object.
   * @return a {@link java.lang.String} object.
   */
  public String getTagValue (String key) {
    return(tags.get(key));
  }

  // Generated Methods
  
  /**
   * <p>Getter for the field <code>referenceCallQuality</code>.</p>
   *
   * @return a float.
   */
  public float getReferenceCallQuality() {
    return referenceCallQuality;
  }

  /**
   * <p>Getter for the field <code>translocationType</code>.</p>
   *
   * @return a byte.
   */
  public byte getTranslocationType() {
    return translocationType;
  }

  /**
   * <p>Setter for the field <code>translocationType</code>.</p>
   *
   * @param translocationType a byte.
   */
  public void setTranslocationType(byte translocationType) {
    this.translocationType = translocationType;
  }

  /**
   * <p>Getter for the field <code>translocationDestinationContig</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getTranslocationDestinationContig() {
    return translocationDestinationContig;
  }

  /**
   * <p>Setter for the field <code>translocationDestinationContig</code>.</p>
   *
   * @param translocationDestinationContig a {@link java.lang.String} object.
   */
  public void setTranslocationDestinationContig(
      String translocationDestinationContig) {
    this.translocationDestinationContig = translocationDestinationContig;
  }

  /**
   * <p>Getter for the field <code>translocationDestinationStartPosition</code>.</p>
   *
   * @return a int.
   */
  public int getTranslocationDestinationStartPosition() {
    return translocationDestinationStartPosition;
  }

  /**
   * <p>Setter for the field <code>translocationDestinationStartPosition</code>.</p>
   *
   * @param translocationDestinationStartPosition a int.
   */
  public void setTranslocationDestinationStartPosition(
      int translocationDestinationStartPosition) {
    this.translocationDestinationStartPosition = translocationDestinationStartPosition;
  }

  /**
   * <p>Getter for the field <code>translocationDestinationStopPosition</code>.</p>
   *
   * @return a int.
   */
  public int getTranslocationDestinationStopPosition() {
    return translocationDestinationStopPosition;
  }

  /**
   * <p>Setter for the field <code>translocationDestinationStopPosition</code>.</p>
   *
   * @param translocationDestinationStopPosition a int.
   */
  public void setTranslocationDestinationStopPosition(
      int translocationDestinationStopPosition) {
    this.translocationDestinationStopPosition = translocationDestinationStopPosition;
  }

  /**
   * <p>Getter for the field <code>fuzzyStartPositionMax</code>.</p>
   *
   * @return a int.
   */
  public int getFuzzyStartPositionMax() {
    return fuzzyStartPositionMax;
  }

  /**
   * <p>Setter for the field <code>fuzzyStartPositionMax</code>.</p>
   *
   * @param fuzzyStartPositionMax a int.
   */
  public void setFuzzyStartPositionMax(int fuzzyStartPositionMax) {
    this.fuzzyStartPositionMax = fuzzyStartPositionMax;
  }

  /**
   * <p>Getter for the field <code>fuzzyStopPositionMin</code>.</p>
   *
   * @return a int.
   */
  public int getFuzzyStopPositionMin() {
    return fuzzyStopPositionMin;
  }

  /**
   * <p>Setter for the field <code>fuzzyStopPositionMin</code>.</p>
   *
   * @param fuzzyStopPositionMin a int.
   */
  public void setFuzzyStopPositionMin(int fuzzyStopPositionMin) {
    this.fuzzyStopPositionMin = fuzzyStopPositionMin;
  }

  /**
   * <p>Getter for the field <code>svType</code>.</p>
   *
   * @return a byte.
   */
  public byte getSvType() {
    return svType;
  }

  /**
   * <p>Setter for the field <code>svType</code>.</p>
   *
   * @param svType a byte.
   */
  public void setSvType(byte svType) {
    this.svType = svType;
  }

  /**
   * <p>Getter for the field <code>relativeLocation</code>.</p>
   *
   * @return a byte.
   */
  public byte getRelativeLocation() {
    return relativeLocation;
  }

  /**
   * <p>Setter for the field <code>relativeLocation</code>.</p>
   *
   * @param relativeLocation a byte.
   */
  public void setRelativeLocation(byte relativeLocation) {
    this.relativeLocation = relativeLocation;
  }

  /**
   * <p>Setter for the field <code>referenceCallQuality</code>.</p>
   *
   * @param referenceCallQuality a float.
   */
  public void setReferenceCallQuality(float referenceCallQuality) {
    this.referenceCallQuality = referenceCallQuality;
  }
  /**
   * <p>Getter for the field <code>consensusCallQuality</code>.</p>
   *
   * @return a float.
   */
  public float getConsensusCallQuality() {
    return consensusCallQuality;
  }
  /**
   * <p>Setter for the field <code>consensusCallQuality</code>.</p>
   *
   * @param consensusCallQuality a float.
   */
  public void setConsensusCallQuality(float consensusCallQuality) {
    this.consensusCallQuality = consensusCallQuality;
  }
  /**
   * <p>Getter for the field <code>maximumMappingQuality</code>.</p>
   *
   * @return a float.
   */
  public float getMaximumMappingQuality() {
    return maximumMappingQuality;
  }
  /**
   * <p>Setter for the field <code>maximumMappingQuality</code>.</p>
   *
   * @param maximumMappingQuality a float.
   */
  public void setMaximumMappingQuality(float maximumMappingQuality) {
    this.maximumMappingQuality = maximumMappingQuality;
  }
  /**
   * <p>Getter for the field <code>referenceMaxSeqQuality</code>.</p>
   *
   * @return a float.
   */
  public float getReferenceMaxSeqQuality() {
    return referenceMaxSeqQuality;
  }
  /**
   * <p>Setter for the field <code>referenceMaxSeqQuality</code>.</p>
   *
   * @param referenceMaxSeqQuality a float.
   */
  public void setReferenceMaxSeqQuality(float referenceMaxSeqQuality) {
    this.referenceMaxSeqQuality = referenceMaxSeqQuality;
  }
  /**
   * <p>Getter for the field <code>referenceAveSeqQuality</code>.</p>
   *
   * @return a float.
   */
  public float getReferenceAveSeqQuality() {
    return referenceAveSeqQuality;
  }
  /**
   * <p>Setter for the field <code>referenceAveSeqQuality</code>.</p>
   *
   * @param referenceAveSeqQuality a float.
   */
  public void setReferenceAveSeqQuality(float referenceAveSeqQuality) {
    this.referenceAveSeqQuality = referenceAveSeqQuality;
  }
  /**
   * <p>Getter for the field <code>consensusMaxSeqQuality</code>.</p>
   *
   * @return a float.
   */
  public float getConsensusMaxSeqQuality() {
    return consensusMaxSeqQuality;
  }
  /**
   * <p>Setter for the field <code>consensusMaxSeqQuality</code>.</p>
   *
   * @param consensusMaxSeqQuality a float.
   */
  public void setConsensusMaxSeqQuality(float consensusMaxSeqQuality) {
    this.consensusMaxSeqQuality = consensusMaxSeqQuality;
  }
  /**
   * <p>Getter for the field <code>consensusAveSeqQuality</code>.</p>
   *
   * @return a float.
   */
  public float getConsensusAveSeqQuality() {
    return consensusAveSeqQuality;
  }
  /**
   * <p>Setter for the field <code>consensusAveSeqQuality</code>.</p>
   *
   * @param consensusAveSeqQuality a float.
   */
  public void setConsensusAveSeqQuality(float consensusAveSeqQuality) {
    this.consensusAveSeqQuality = consensusAveSeqQuality;
  }
  /**
   * <p>Getter for the field <code>zygosity</code>.</p>
   *
   * @return a byte.
   */
  public byte getZygosity() {
    return zygosity;
  }
  /**
   * <p>Setter for the field <code>zygosity</code>.</p>
   *
   * @param zygosity a byte.
   */
  public void setZygosity(byte zygosity) {
    this.zygosity = zygosity;
  }
  /**
   * <p>Getter for the field <code>calledBaseCountForward</code>.</p>
   *
   * @return a int.
   */
  public int getCalledBaseCountForward() {
    return calledBaseCountForward;
  }
  /**
   * <p>Setter for the field <code>calledBaseCountForward</code>.</p>
   *
   * @param calledBaseCountForward a int.
   */
  public void setCalledBaseCountForward(int calledBaseCountForward) {
    this.calledBaseCountForward = calledBaseCountForward;
  }
  /**
   * <p>Getter for the field <code>calledBaseCountReverse</code>.</p>
   *
   * @return a int.
   */
  public int getCalledBaseCountReverse() {
    return calledBaseCountReverse;
  }
  /**
   * <p>Setter for the field <code>calledBaseCountReverse</code>.</p>
   *
   * @param calledBaseCountReverse a int.
   */
  public void setCalledBaseCountReverse(int calledBaseCountReverse) {
    this.calledBaseCountReverse = calledBaseCountReverse;
  }
  /**
   * <p>Getter for the field <code>calledBaseCount</code>.</p>
   *
   * @return a int.
   */
  public int getCalledBaseCount() {
    return calledBaseCount;
  }
  /**
   * <p>Setter for the field <code>calledBaseCount</code>.</p>
   *
   * @param calledBaseCount a int.
   */
  public void setCalledBaseCount(int calledBaseCount) {
    this.calledBaseCount = calledBaseCount;
  }
  /**
   * <p>Getter for the field <code>contig</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getContig() {
    return contig;
  }
  /** {@inheritDoc} */
  public void setContig(String contig) {
    this.contig = contig;
  }
  /**
   * <p>Getter for the field <code>startPosition</code>.</p>
   *
   * @return a int.
   */
  public int getStartPosition() {
    return startPosition;
  }
  /** {@inheritDoc} */
  public void setStartPosition(int startPosition) {
    this.startPosition = startPosition;
  }
  /**
   * <p>Getter for the field <code>stopPosition</code>.</p>
   *
   * @return a int.
   */
  public int getStopPosition() {
    return stopPosition;
  }
  /** {@inheritDoc} */
  public void setStopPosition(int stopPosition) {
    this.stopPosition = stopPosition;
  }
  /**
   * <p>Getter for the field <code>referenceBase</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getReferenceBase() {
    return referenceBase;
  }
  /**
   * <p>Setter for the field <code>referenceBase</code>.</p>
   *
   * @param referenceBase a {@link java.lang.String} object.
   */
  public void setReferenceBase(String referenceBase) {
    this.referenceBase = referenceBase;
  }
  /**
   * <p>Getter for the field <code>consensusBase</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getConsensusBase() {
    return consensusBase;
  }
  /**
   * <p>Setter for the field <code>consensusBase</code>.</p>
   *
   * @param consensusBase a {@link java.lang.String} object.
   */
  public void setConsensusBase(String consensusBase) {
    this.consensusBase = consensusBase;
  }
  /**
   * <p>Getter for the field <code>calledBase</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getCalledBase() {
    return calledBase;
  }
  /**
   * <p>Setter for the field <code>calledBase</code>.</p>
   *
   * @param calledBase a {@link java.lang.String} object.
   */
  public void setCalledBase(String calledBase) {
    this.calledBase = calledBase;
  }
  /**
   * <p>Setter for the field <code>maximumMappingQuality</code>.</p>
   *
   * @param maximumMappingQuality a int.
   */
  public void setMaximumMappingQuality(int maximumMappingQuality) {
    this.maximumMappingQuality = maximumMappingQuality;
  }
  /**
   * <p>Getter for the field <code>readCount</code>.</p>
   *
   * @return a int.
   */
  public int getReadCount() {
    return readCount;
  }
  /**
   * <p>Setter for the field <code>readCount</code>.</p>
   *
   * @param readCount a int.
   */
  public void setReadCount(int readCount) {
    this.readCount = readCount;
  }
  /**
   * <p>Getter for the field <code>type</code>.</p>
   *
   * @return a byte.
   */
  public byte getType() {
    return type;
  }
  /**
   * <p>Setter for the field <code>type</code>.</p>
   *
   * @param type a byte.
   */
  public void setType(byte type) {
    this.type = type;
  }
  /**
   * <p>Getter for the field <code>readBases</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getReadBases() {
    return readBases;
  }
  /**
   * <p>Setter for the field <code>readBases</code>.</p>
   *
   * @param readBases a {@link java.lang.String} object.
   */
  public void setReadBases(String readBases) {
    this.readBases = readBases;
  }
  /**
   * <p>Getter for the field <code>baseQualities</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getBaseQualities() {
    return baseQualities;
  }
  /**
   * <p>Setter for the field <code>baseQualities</code>.</p>
   *
   * @param baseQualities a {@link java.lang.String} object.
   */
  public void setBaseQualities(String baseQualities) {
    this.baseQualities = baseQualities;
  }
  /**
   * <p>Getter for the field <code>callOne</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getCallOne() {
    return callOne;
  }
  /**
   * <p>Setter for the field <code>callOne</code>.</p>
   *
   * @param callOne a {@link java.lang.String} object.
   */
  public void setCallOne(String callOne) {
    this.callOne = callOne;
  }
  /**
   * <p>Getter for the field <code>callTwo</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getCallTwo() {
    return callTwo;
  }
  /**
   * <p>Setter for the field <code>callTwo</code>.</p>
   *
   * @param callTwo a {@link java.lang.String} object.
   */
  public void setCallTwo(String callTwo) {
    this.callTwo = callTwo;
  }
  /**
   * <p>Getter for the field <code>readsSupportingCallOne</code>.</p>
   *
   * @return a int.
   */
  public int getReadsSupportingCallOne() {
    return readsSupportingCallOne;
  }
  /**
   * <p>Setter for the field <code>readsSupportingCallOne</code>.</p>
   *
   * @param readsSupportingCallOne a int.
   */
  public void setReadsSupportingCallOne(int readsSupportingCallOne) {
    this.readsSupportingCallOne = readsSupportingCallOne;
  }
  /**
   * <p>Getter for the field <code>readsSupportingCallTwo</code>.</p>
   *
   * @return a int.
   */
  public int getReadsSupportingCallTwo() {
    return readsSupportingCallTwo;
  }
  /**
   * <p>Setter for the field <code>readsSupportingCallTwo</code>.</p>
   *
   * @param readsSupportingCallTwo a int.
   */
  public void setReadsSupportingCallTwo(int readsSupportingCallTwo) {
    this.readsSupportingCallTwo = readsSupportingCallTwo;
  }
  /**
   * <p>Getter for the field <code>readsSupportingCallThree</code>.</p>
   *
   * @return a int.
   */
  public int getReadsSupportingCallThree() {
    return readsSupportingCallThree;
  }
  /**
   * <p>Setter for the field <code>readsSupportingCallThree</code>.</p>
   *
   * @param readsSupportingCallThree a int.
   */
  public void setReadsSupportingCallThree(int readsSupportingCallThree) {
    this.readsSupportingCallThree = readsSupportingCallThree;
  }
  /**
   * <p>Getter for the field <code>tags</code>.</p>
   *
   * @return a {@link java.util.HashMap} object.
   */
  public HashMap<String, String> getTags() {
    return tags;
  }
  /** {@inheritDoc} */
  public void setTags(HashMap<String, String> tags) {
    this.tags = tags;
  }

}
