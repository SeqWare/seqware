/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <p>Consequence class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Consequence extends LocatableModel {
    
    // constants
    /** Constant <code>NO_STRAND=0</code> */
    public static final byte NO_STRAND = 0;
    /** Constant <code>PLUS_STRAND=1</code> */
    public static final byte PLUS_STRAND = 1;
    /** Constant <code>MINUS_STRAND=2</code> */
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
    
    // generated methods
    
    /**
     * <p>Getter for the field <code>geneId</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGeneId() {
        return geneId;
    }

    /**
     * <p>Setter for the field <code>geneId</code>.</p>
     *
     * @param geneId a {@link java.lang.String} object.
     */
    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    /**
     * <p>Getter for the field <code>mismatchId</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMismatchId() {
        return mismatchId;
    }

    /**
     * <p>Setter for the field <code>mismatchId</code>.</p>
     *
     * @param mismatchId a {@link java.lang.String} object.
     */
    public void setMismatchId(String mismatchId) {
        this.mismatchId = mismatchId;
    }

    /**
     * <p>Getter for the field <code>geneChr</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGeneChr() {
        return geneChr;
    }

    /**
     * <p>Setter for the field <code>geneChr</code>.</p>
     *
     * @param geneChr a {@link java.lang.String} object.
     */
    public void setGeneChr(String geneChr) {
        this.geneChr = geneChr;
    }

    /**
     * <p>Getter for the field <code>codingStart</code>.</p>
     *
     * @return a int.
     */
    public int getCodingStart() {
        return codingStart;
    }

    /**
     * <p>Setter for the field <code>codingStart</code>.</p>
     *
     * @param codingStart a int.
     */
    public void setCodingStart(int codingStart) {
        this.codingStart = codingStart;
    }

    /**
     * <p>Getter for the field <code>codingStop</code>.</p>
     *
     * @return a int.
     */
    public int getCodingStop() {
        return codingStop;
    }

    /**
     * <p>Setter for the field <code>codingStop</code>.</p>
     *
     * @param codingStop a int.
     */
    public void setCodingStop(int codingStop) {
        this.codingStop = codingStop;
    }

    /**
     * <p>Getter for the field <code>strand</code>.</p>
     *
     * @return a byte.
     */
    public byte getStrand() {
        return strand;
    }

    /**
     * <p>Setter for the field <code>strand</code>.</p>
     *
     * @param strand a byte.
     */
    public void setStrand(byte strand) {
        this.strand = strand;
    }

    /**
     * <p>Getter for the field <code>mismatchType</code>.</p>
     *
     * @return a byte.
     */
    public byte getMismatchType() {
        return mismatchType;
    }

    /**
     * <p>Setter for the field <code>mismatchType</code>.</p>
     *
     * @param mismatchType a byte.
     */
    public void setMismatchType(byte mismatchType) {
        this.mismatchType = mismatchType;
    }

    /**
     * <p>Getter for the field <code>mismatchChr</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMismatchChr() {
        return mismatchChr;
    }

    /**
     * <p>Setter for the field <code>mismatchChr</code>.</p>
     *
     * @param mismatchChr a {@link java.lang.String} object.
     */
    public void setMismatchChr(String mismatchChr) {
        this.mismatchChr = mismatchChr;
    }

    /**
     * <p>Getter for the field <code>mismatchStart</code>.</p>
     *
     * @return a int.
     */
    public int getMismatchStart() {
        return mismatchStart;
    }

    /**
     * <p>Setter for the field <code>mismatchStart</code>.</p>
     *
     * @param mismatchStart a int.
     */
    public void setMismatchStart(int mismatchStart) {
        this.mismatchStart = mismatchStart;
    }

    /**
     * <p>Getter for the field <code>mismatchStop</code>.</p>
     *
     * @return a int.
     */
    public int getMismatchStop() {
        return mismatchStop;
    }

    /**
     * <p>Setter for the field <code>mismatchStop</code>.</p>
     *
     * @param mismatchStop a int.
     */
    public void setMismatchStop(int mismatchStop) {
        this.mismatchStop = mismatchStop;
    }

    /**
     * <p>Getter for the field <code>mismatchCodonPosition</code>.</p>
     *
     * @return a int.
     */
    public int getMismatchCodonPosition() {
        return mismatchCodonPosition;
    }

    /**
     * <p>Setter for the field <code>mismatchCodonPosition</code>.</p>
     *
     * @param mismatchCodonPosition a int.
     */
    public void setMismatchCodonPosition(int mismatchCodonPosition) {
        this.mismatchCodonPosition = mismatchCodonPosition;
    }

    /**
     * <p>Getter for the field <code>mismatchCodonChange</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMismatchCodonChange() {
        return mismatchCodonChange;
    }

    /**
     * <p>Setter for the field <code>mismatchCodonChange</code>.</p>
     *
     * @param mismatchCodonChange a {@link java.lang.String} object.
     */
    public void setMismatchCodonChange(String mismatchCodonChange) {
        this.mismatchCodonChange = mismatchCodonChange;
    }

    /**
     * <p>Getter for the field <code>mismatchAminoAcidChange</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMismatchAminoAcidChange() {
        return mismatchAminoAcidChange;
    }

    /**
     * <p>Setter for the field <code>mismatchAminoAcidChange</code>.</p>
     *
     * @param mismatchAminoAcidChange a {@link java.lang.String} object.
     */
    public void setMismatchAminoAcidChange(String mismatchAminoAcidChange) {
        this.mismatchAminoAcidChange = mismatchAminoAcidChange;
    }

    /**
     * <p>Getter for the field <code>mismatchAAChangeBlosumScore</code>.</p>
     *
     * @return a float.
     */
    public float getMismatchAAChangeBlosumScore() {
        return mismatchAAChangeBlosumScore;
    }

    /**
     * <p>Setter for the field <code>mismatchAAChangeBlosumScore</code>.</p>
     *
     * @param mismatchAAChangeBlosumScore a float.
     */
    public void setMismatchAAChangeBlosumScore(float mismatchAAChangeBlosumScore) {
        this.mismatchAAChangeBlosumScore = mismatchAAChangeBlosumScore;
    }

    /**
     * <p>Getter for the field <code>genomicSequence</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getGenomicSequence() {
        return genomicSequence;
    }

    /**
     * <p>Setter for the field <code>genomicSequence</code>.</p>
     *
     * @param genomicSequence a {@link java.lang.String} object.
     */
    public void setGenomicSequence(String genomicSequence) {
        this.genomicSequence = genomicSequence;
    }

    /**
     * <p>Getter for the field <code>mutatedGenomicSequence</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMutatedGenomicSequence() {
        return mutatedGenomicSequence;
    }

    /**
     * <p>Setter for the field <code>mutatedGenomicSequence</code>.</p>
     *
     * @param mutatedGenomicSequence a {@link java.lang.String} object.
     */
    public void setMutatedGenomicSequence(String mutatedGenomicSequence) {
        this.mutatedGenomicSequence = mutatedGenomicSequence;
    }

    /**
     * <p>Getter for the field <code>translatedSequence</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getTranslatedSequence() {
        return translatedSequence;
    }

    /**
     * <p>Setter for the field <code>translatedSequence</code>.</p>
     *
     * @param translatedSequence a {@link java.lang.String} object.
     */
    public void setTranslatedSequence(String translatedSequence) {
        this.translatedSequence = translatedSequence;
    }

    /**
     * <p>Getter for the field <code>mutatedTranslatedSequence</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMutatedTranslatedSequence() {
        return mutatedTranslatedSequence;
    }

    /**
     * <p>Setter for the field <code>mutatedTranslatedSequence</code>.</p>
     *
     * @param mutatedTranslatedSequence a {@link java.lang.String} object.
     */
    public void setMutatedTranslatedSequence(String mutatedTranslatedSequence) {
        this.mutatedTranslatedSequence = mutatedTranslatedSequence;
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
        return id;
    }

    /** {@inheritDoc} */
    public void setId(String id) {
        this.id = id;
    }
    
}
