package com.github.seqware.model;

import com.github.seqware.factory.ModelManager;
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
    private String pragma = null;
    private String source = null;
    private String type = null;
    private Double score = null;
    private String phase = ".";
    private String id = null;
    private long start = 0;
    private long stop = 0;
    private Strand strand = null;

    private Feature() {
        super();
    }

    /**
     * User provided ID for the feature (optional).
     *
     * @return user provided ID
     */
    public String getId() {
        return id;
    }

    /**
     * (GVF: The score of the feature, an integer or floating point number.)
     *
     * @return score for the feature
     */
    public Double getScore() {
        return score;
    }

    /**
     * (GVF: The phase column is not used in GVF, but is maintained with the
     * placeholder '.' (period) for compatibility with GFF3 and tools that
     * conform to the GFF3 specification (and do change it))
     *
     * @return phase for the feature
     */
    public String getPhase() {
        return phase;
    }

    /**
     * (GVF: The source is a free text qualifier intended to describe the
     * algorithm or operating procedure that generated this feature.)
     *
     * @return a String description of the source for this feature
     */
    public String getSource() {
        return source;
    }

    /**
     * Start coordinate, 0-based (GVF: 1-based integer for the beginning of the
     * sequence_alteration locus on the plus strand (integer))
     *
     * @return Start coordinate, 0-based
     */
    public long getStart() {
        return start;
    }

    /**
     * Stop coordinate, 0-based (GVF: 1-based integer of the end of the
     * sequence_alteration on plus strand (integer).)
     *
     * @return Stop coordinate, 0-based
     */
    public long getStop() {
        return stop;
    }

    /**
     * Strand of the feature. (GVF: The strand of the feature (+/-))
     *
     * @return the strand of the feature
     */
    public Strand getStrand() {
        return strand;
    }

    /**
     * (From GVF: The type of the feature, this is constrained to be either: (a)
     * the SO term sequence_alteration (SO:0001059), (b) a child term of
     * sequence_alteration, (c) the SO term gap (SO:0000730), or (d) the SO
     * accession number for any of the previous terms.)
     *
     * @return the type of feature
     */
    public String getType() {
        return type;
    }

    /**
     * GVF provides pragmas that are file-wide. Not sure if they affect us, but
     * I guess we'll need to store them for a round-trip import and export
     *
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

    public static Feature.Builder newBuilder() {
        return new Feature.Builder();
    }

    public Feature.Builder toBuilder() {
        Feature.Builder b = new Feature.Builder();
        b.feature = this;
        return b;
    }

    public static class Builder implements BaseBuilder {

        private Feature feature = new Feature();

        public Feature.Builder setPragma(String pragma) {
            feature.pragma = pragma;
            return this;
        }

        public Feature.Builder setSource(String source) {
            feature.source = source;
            return this;
        }

        public Feature.Builder setId(String id) {
            feature.id = id;
            return this;
        }

        public Feature.Builder setPhase(String phase) {
            feature.phase = phase;
            return this;
        }

        public Feature.Builder setScore(Double score) {
            feature.score = score;
            return this;
        }

        public Feature.Builder setStart(long start) {
            feature.start = start;
            return this;
        }

        public Feature.Builder setStop(long stop) {
            feature.stop = stop;
            return this;
        }

        public Feature.Builder setStrand(Strand strand) {
            feature.strand = strand;
            return this;
        }

        public Feature.Builder setType(String type) {
            feature.type = type;
            return this;
        }    

        @Override
        public Feature build() {
            if (feature.strand == null) {
                feature.strand = Strand.NOT_STRANDED;
            }
            if(feature.getManager() != null){
                feature.getManager().objectCreated(feature);
            }
            return feature;
        }

        @Override
        public Builder setManager(ModelManager aThis) {
            feature.setManager(aThis);
            return this;
        }
    }
}
