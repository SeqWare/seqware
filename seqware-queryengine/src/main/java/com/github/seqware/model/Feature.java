package com.github.seqware.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Features represent a GVF (which is a more generic version of a VCF). See
 * http://genomebiology.com/2010/11/8/R88 or better
 * http://www.sequenceontology.org/resources/gvf.html#quick_gvf_examples
 *
 * We will want to tag features and version features, however we probably do not
 * want ACL features on a Feature level since there will be many many features
 *
 * Immutable (but tags are not)
 *
 * @author dyuen
 * @author jbaran
 */
public class Feature extends Atom<Feature> {

    /**
     * Strand locations of features.
     *
     * Positive/negative strand are relative to the landmark.
     */
    public enum Strand {

        POSITIVE, NEGATIVE, NOT_STRANDED, UNKNOWN
    }
    
    private String pragma;
    private String source;
    private String type;
    private Double score;
    private String phase = ".";
    private String id;
    private long start;
    private long stop;
    private Strand strand;

    private Feature() {
        super();
    }

    /**
     * Create a new location based non-stranded feature.
     *
     * @param featureSet parent featureSet, enforces the requirement that
     * features be contained by the contigs/coordinates represented by the
     * Reference.
     * @param start Start coordinate.
     * @param stop Stop coordinate.
     */
    public Feature(FeatureSet featureSet, long start, long stop) {
        this();
        this.start = start;
        this.stop = stop;
    }

    /**
     * Create a new location based feature.
     *
     * @param featureSet parent featureSet, enforces the requirement that
     * features be contained by the contigs/coordinates represented by the
     * Reference.
     * @param start Start coordinate.
     * @param stop Stop coordinate.
     * @param strand Strand of the feature.
     */
    public Feature(FeatureSet featureSet, long start, long stop, Strand strand) {
        this(featureSet, start, stop);

        if (strand != null) {
            this.strand = strand;
        } else {
            this.strand = Strand.NOT_STRANDED;
        }
    }

    /**
     * Create a new location based feature with full information
     * @param featureSet parent featureSet, enforces the requirement that
     * features be contained by the contigs/coordinates represented by the
     * Reference.
     * @param start Start coordinate.
     * @param stop Stop coordinate.
     * @param strand Strand of the feature.
     * @param type 
     * @param score
     * @param source
     * @param pragma 
     */
    public Feature(FeatureSet featureSet, long start, long stop, Strand strand, String type, Double score, String source, String pragma, String phase) {
        this(featureSet, start, stop, strand);
        this.type = type;
        this.score = score;
        this.source = source;
        this.pragma = pragma;
        this.phase = phase;
    }

    /**
     * User provided ID for the feature (optional).
     * @return user provided ID
     */
    public String getId() {
        return id;
    }

    /**
     * (GVF: The score of the feature, an integer or floating point number.)
     * @return score for the feature
     */
    public Double getScore() {
        return score;
    }
    
    /**
     * (GVF: The phase column is not used in GVF, but is maintained with the
     * placeholder '.' (period) for compatibility with GFF3 and tools that
     * conform to the GFF3 specification (and do change it))
     * @return phase for the feature
     */
    public String getPhase(){
        return phase;
    }

    /**
     * (GVF: The source is a free text qualifier intended to describe the
     * algorithm or operating procedure that generated this feature.)
     * @return a String description of the source for this feature
     */
    public String getSource() {
        return source;
    }

    /**
     * Start coordinate, 0-based (GVF: 1-based integer for the beginning of the
     * sequence_alteration locus on the plus strand (integer))
     * @return Start coordinate, 0-based
     */
    public long getStart() {
        return start;
    }

    /**
     * Stop coordinate, 0-based (GVF: 1-based integer of the end of the
     * sequence_alteration on plus strand (integer).)
     * @return Stop coordinate, 0-based
     */
    public long getStop() {
        return stop;
    }

    /**
     * Strand of the feature. (GVF: The strand of the feature (+/-))
     * @return the strand of the feature
     */
    public Strand getStrand() {
        return strand;
    }

    /**
     * (From GVF: The type of the feature, this is constrained to be either: (a) the
     * SO term sequence_alteration (SO:0001059), (b) a child term of
     * sequence_alteration, (c) the SO term gap (SO:0000730), or (d) the SO
     * accession number for any of the previous terms.)
     * @return the type of feature
     */
    public String getType() {
        return type;
    }

    /**
     * GVF provides pragmas that are file-wide.
     * Not sure if they affect us, but I guess we'll need to store them for a
     * round-trip import and export
     * @return a simple String pragma from the original GVF file
     */
    public String getPragma() {
        return pragma;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
