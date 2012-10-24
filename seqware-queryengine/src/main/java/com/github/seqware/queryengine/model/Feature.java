package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import com.github.seqware.queryengine.util.FSGID;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
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
 * @version $Id: $Id
 */
public class Feature extends AtomImpl<Feature> {

    /**
     * Strand locations of features.
     *
     * Positive/negative strand are relative to the landmark.
     */
    public enum Strand {
        POSITIVE, NEGATIVE, NOT_STRANDED, UNKNOWN
    }

    /**
     * Names that cannot be used for additional attributes, since they
     * are already covered by instance variables.
     */
    private final static String[] reservedAttributeNames = new String[] { "pragma", "source", "type", "score", "phase", "seqid", "start", "stop", "strand" };

    private String pragma = null;
    private String source = null;
    private String type = null;
    private Double score = null;
    private String phase = ".";
    private String seqid = null;
    private long start = 0;
    private long stop = 0;
    private Strand strand = null;

    public enum AdditionalAttributeType { STRING, FLOAT, DOUBLE, LONG, INTEGER };

    static {
        // Array has to be sorted, so that it is possible to use a binary search on it:
        Arrays.sort(reservedAttributeNames);
    }

    /**
     * Additional attributes can be freely added via a map.
     */
    private HashMap<String, AdditionalAttributeType> additionalAttributes = null;

    private Feature() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    public Class getHBaseClass() {
        return Feature.class;
    }

    /** {@inheritDoc} */
    @Override
    public String getHBasePrefix() {
        assert(this.getSGID() instanceof FSGID);
        FSGID fsgid = (FSGID) this.getSGID();
        return fsgid.getTablename();
    }

    /**
     * User provided ID for the feature (GVF: The chromosome or contig on which
     * the sequence_alteration is located (text).)
     *
     * @return user provided ID
     */
    public String getSeqid() {
        return seqid;
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

    /**
     * Sets an additional attribute not covered by GVF. It is not permitted to have an additional
     * attribute with the same name as the instance variables (case insensitive).
     *
     * @param name Attribute name, which cannot be a GVF attribute (start, stop, pragma, etc).
     * @param value Value of the variable to be set.
     */
    public void setAdditionalAttribute(String name, AdditionalAttributeType value) {
        if (this.additionalAttributes == null)
            this.additionalAttributes = new HashMap<String, AdditionalAttributeType>();

        if (Arrays.binarySearch(reservedAttributeNames, name.toLowerCase()) >= 0)
            throw new IllegalArgumentException("Invalid name for an additional attribute. Reserved names are: " + StringUtils.join(reservedAttributeNames, ", "));

        this.additionalAttributes.put(name, value);
    }

    /**
     * Returns the value of an additional attribute or null of the attribute does not exist.
     *
     * @param attribute The name of the attribute whose value should be returned.
     * @return Value of the attribute or null.
     */
    public AdditionalAttributeType getAdditionalAttribute(String attribute) {
        if (this.additionalAttributes == null)
            return null;

        return this.additionalAttributes.get(attribute);
    }

    /**
     * Returns the names of the additional attributes stored.
     *
     * @return Set of additional attribute names.
     */
    public Set<String> getAdditionalAttributeNames() {
        if (this.additionalAttributes == null)
            return new HashSet<String>();

        return this.additionalAttributes.keySet();
    }

    /**
     * Generic implementation for retrieving the value of a GVF or additional attribute.
     *
     * @param name Name of the attribute to be retrieved, which can be either "start", "stop", etc, or an additional attribute name.
     * @return The value of the attribute, or null if the attribute is not present in this feature.
     */
    public Object getAttribute(String name) {
        String nameLowerCase = name.toLowerCase();

        if (nameLowerCase.equals("pragma"))
            return this.getPragma();
        else if (nameLowerCase.equals("source"))
            return this.getSource();
        else if (nameLowerCase.equals("type"))
            return this.getType();
        else if (nameLowerCase.equals("score"))
            return this.getScore();
        else if (nameLowerCase.equals("phase"))
            return this.getPhase();
        else if (nameLowerCase.equals("seqid"))
            return this.getSeqid();
        else if (nameLowerCase.equals("start"))
            return this.getStart();
        else if (nameLowerCase.equals("stop"))
            return this.getStop();
        else if (nameLowerCase.equals("strand"))
            return this.getStrand();
        else
            return this.getAdditionalAttribute(name);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * <p>newBuilder.</p>
     *
     * @return a {@link com.github.seqware.queryengine.model.Feature.Builder} object.
     */
    public static Feature.Builder newBuilder() {
        return new Feature.Builder();
    }

    /** {@inheritDoc} */
    @Override
    public Feature.Builder toBuilder() {
        Feature.Builder b = new Feature.Builder();
        b.feature = (Feature) this.copy(true);
        return b;
    }

    public static class Builder extends BaseBuilder {

        private Feature feature = new Feature();

        public Feature.Builder setPragma(String pragma) {
            feature.pragma = pragma;
            return this;
        }

        public Feature.Builder setSource(String source) {
            feature.source = source;
            return this;
        }

        public Feature.Builder setSeqid(String seqid) {
            feature.seqid = seqid;
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
            // let's mandate an seqid for rowKey purposes 
            if (feature.seqid == null) {
                throw new RuntimeException("Ensure that Feature is built with an id for rowKey purposes");
            }
// with lazy molecule sets, it makes less sense to notify that a feature is created on build
//            if (feature.getManager() != null) {
//                feature.getManager().objectCreated(feature);
//            }
            return feature;
        }

        @Override
        public Builder setManager(CreateUpdateManager aThis) {
            feature.setManager(aThis);
            return this;
        }

        @Override
        public Builder setFriendlyRowKey(String rowKey) {
            throw new UnsupportedOperationException("Feature does not support custom rowkey.");
        }
    }
}
