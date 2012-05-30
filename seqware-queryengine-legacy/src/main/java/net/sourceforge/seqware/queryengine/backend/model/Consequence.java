/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author boconnor
 * 
 * 
 */
public class Consequence extends LocatableModel {
    
    // constants
    public static final byte NO_STRAND = 0;
    public static final byte PLUS_STRAND = 1;
    public static final byte MINUS_STRAND = 2;
    
    // vars
    protected String id = null;
    protected String mismatchId = null;
    protected String geneId = "";
    protected String geneChr = "";
    protected int codingStart = 0;
    protected int codingStop = 0;
    protected byte strand = Consequence.NO_STRAND;
    protected byte mismatchType = Variant.UNKNOWN_TYPE;
    protected String mismatchChr = "";
    protected int mismatchStart = 0;
    protected int mismatchStop = 0;
    protected int mismatchCodonPosition = 0;
    protected String mismatchCodonChange = "";
    protected String mismatchAminoAcidChange = "";
    protected float mismatchAAChangeBlosumScore = (float)0.0;
    protected String genomicSequence = "";
    protected String mutatedGenomicSequence = "";
    protected String translatedSequence = "";
    protected String mutatedTranslatedSequence = "";

    // custom methods
    public void addTag (String key, String value) {
      tags.put(key, value);
    }
    
    public String getTagValue (String key) {
      return(tags.get(key));
    }
    
    // generated methods
    
    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public String getMismatchId() {
        return mismatchId;
    }

    public void setMismatchId(String mismatchId) {
        this.mismatchId = mismatchId;
    }

    public String getGeneChr() {
        return geneChr;
    }

    public void setGeneChr(String geneChr) {
        this.geneChr = geneChr;
    }

    public int getCodingStart() {
        return codingStart;
    }

    public void setCodingStart(int codingStart) {
        this.codingStart = codingStart;
    }

    public int getCodingStop() {
        return codingStop;
    }

    public void setCodingStop(int codingStop) {
        this.codingStop = codingStop;
    }

    public byte getStrand() {
        return strand;
    }

    public void setStrand(byte strand) {
        this.strand = strand;
    }

    public byte getMismatchType() {
        return mismatchType;
    }

    public void setMismatchType(byte mismatchType) {
        this.mismatchType = mismatchType;
    }

    public String getMismatchChr() {
        return mismatchChr;
    }

    public void setMismatchChr(String mismatchChr) {
        this.mismatchChr = mismatchChr;
    }

    public int getMismatchStart() {
        return mismatchStart;
    }

    public void setMismatchStart(int mismatchStart) {
        this.mismatchStart = mismatchStart;
    }

    public int getMismatchStop() {
        return mismatchStop;
    }

    public void setMismatchStop(int mismatchStop) {
        this.mismatchStop = mismatchStop;
    }

    public int getMismatchCodonPosition() {
        return mismatchCodonPosition;
    }

    public void setMismatchCodonPosition(int mismatchCodonPosition) {
        this.mismatchCodonPosition = mismatchCodonPosition;
    }

    public String getMismatchCodonChange() {
        return mismatchCodonChange;
    }

    public void setMismatchCodonChange(String mismatchCodonChange) {
        this.mismatchCodonChange = mismatchCodonChange;
    }

    public String getMismatchAminoAcidChange() {
        return mismatchAminoAcidChange;
    }

    public void setMismatchAminoAcidChange(String mismatchAminoAcidChange) {
        this.mismatchAminoAcidChange = mismatchAminoAcidChange;
    }

    public float getMismatchAAChangeBlosumScore() {
        return mismatchAAChangeBlosumScore;
    }

    public void setMismatchAAChangeBlosumScore(float mismatchAAChangeBlosumScore) {
        this.mismatchAAChangeBlosumScore = mismatchAAChangeBlosumScore;
    }

    public String getGenomicSequence() {
        return genomicSequence;
    }

    public void setGenomicSequence(String genomicSequence) {
        this.genomicSequence = genomicSequence;
    }

    public String getMutatedGenomicSequence() {
        return mutatedGenomicSequence;
    }

    public void setMutatedGenomicSequence(String mutatedGenomicSequence) {
        this.mutatedGenomicSequence = mutatedGenomicSequence;
    }

    public String getTranslatedSequence() {
        return translatedSequence;
    }

    public void setTranslatedSequence(String translatedSequence) {
        this.translatedSequence = translatedSequence;
    }

    public String getMutatedTranslatedSequence() {
        return mutatedTranslatedSequence;
    }

    public void setMutatedTranslatedSequence(String mutatedTranslatedSequence) {
        this.mutatedTranslatedSequence = mutatedTranslatedSequence;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
