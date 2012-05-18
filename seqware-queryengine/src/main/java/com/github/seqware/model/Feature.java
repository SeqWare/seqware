package com.github.seqware.model;

import java.util.UUID;

/**
 * Features represent a GVF (which is a more generic version of a VCF). See
 * http://genomebiology.com/2010/11/8/R88 or better
 * http://www.sequenceontology.org/resources/gvf.html#quick_gvf_examples
 *
 * We will want to tag features and version features, however we probably do not
 * want ACL features on a Feature level since there will be many many features
 *
 * @author dyuen
 * @author jbaran
 */
public class Feature extends Atom {

    /**
     * Strand locations of features.
     *
     * Positive/negative strand are relative to the landmark.
     */
    public enum Strand {

        POSITIVE, NEGATIVE, NOT_STRANDED, UNKNOWN
    }
    /**
     * Internally used unique identifier of this feature.
     */
    private UUID uuid;
    /**
     * (GVF: The source is a free text qualifier intended to describe the
     * algorithm or operating procedure that generated this feature.)
     */
    private String source;
    /**
     * (GVF: The type of the feature. This is constrained to be either: (a) the
     * SO term sequence_alteration (SO:0001059), (b) a child term of
     * sequence_alteration, (c) the SO term gap (SO:0000730), or (d) the SO
     * accession number for any of the previous terms.)
     */
    private String type;
    /**
     * (GVF: The score of the feature, an integer or floating point number.)
     */
    private Double score;
    /**
     * (GVF: The phase column is not used in GVF, but is maintained with the
     * placeholder '.' (period) for compatibility with GFF3 and tools that
     * conform to the GFF3 specification.)
     */
    private static final String phase = ".";
    /**
     * User provided ID for the feature (optional).
     */
    private String id;
    /**
     * Reference system used for coordinates, for example, versioned name of
     * genome assembly used. (GVF: The ID of the landmark used to establish the
     * coordinate system for the current feature.)
     */
    private String reference;
    /**
     * Start coordinate, 0-based. (GVF: 1-based integer for the beginning of the
     * sequence_alteration locus on the plus strand (integer))
     */
    private long start;
    /**
     * Stop coordinate, 0-based. (GVF: 1-based integer of the end of the
     * sequence_alteration on plus strand (integer).)
     */
    private long stop;
    /**
     * Strand of the feature. (GVF: The strand of the feature (+/-))
     */
    private Strand strand;

    private Feature() {
        // TODO This will have to be replaced with a stronger UUID generation method.
        this.uuid = UUID.randomUUID();
    }

    /**
     * Create a new location based non-stranded feature.
     *
     * @param reference Reference system used for coordinates, for example,
     * versioned name of genome assembly used.
     * @param start Start coordinate.
     * @param stop Stop coordinate.
     */
    public Feature(String reference, long start, long stop) {
        this();
    }

    /**
     * Create a new location based feature.
     *
     * @param reference Reference system used for coordinates, for example,
     * versioned name of genome assembly used.
     * @param start Start coordinate.
     * @param stop Stop coordinate.
     * @param strand Strand of the feature.
     */
    public Feature(String reference, long start, long stop, Strand strand) {
        this(reference, start, stop);

        if (strand != null) {
            this.strand = strand;
        } else {
            this.strand = Strand.NOT_STRANDED;
        }
    }

    /**
     * Get the universally unique identifier of this feature.
     */
    public UUID getUUID() {
        return this.uuid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getStop() {
        return stop;
    }

    public void setStop(long stop) {
        this.stop = stop;
    }

    public Strand getStrand() {
        return strand;
    }

    public void setStrand(Strand strand) {
        this.strand = strand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
