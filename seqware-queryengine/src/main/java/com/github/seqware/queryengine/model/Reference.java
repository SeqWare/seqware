package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;
import java.util.Iterator;

/**
 * A reference is a particular build version such as "hg19" or "hg18".
 *
 * Note: Every reference needs to be part of a ReferenceSet.
 *
 * @author dyuen
 * @author jbaran
 * @version $Id: $Id
 */
public interface Reference extends MolSetInterface<Reference, FeatureSet> {
    /** Constant <code>prefix="Reference"</code> */
    public final static String prefix = "Reference";

    /**
     * Get the list of feature sets associated with this reference.
     *
     * @return Iterator of feature sets associated with this reference.
     */
    public abstract Iterator<FeatureSet> featureSets();

    /**
     * The name of this reference (ex: "hg 19")
     *
     * @return return the name of this reference.
     */
    public String getName();

    /**
     * {@inheritDoc}
     *
     * Create an ACL builder started with a copy of this
     */
    @Override
    public abstract Reference.Builder toBuilder();

    public abstract static class Builder extends BaseBuilder {

        public Reference reference;

        public abstract Reference.Builder setName(String name);

        @Override
        public abstract Reference build();

        @Override
        public Builder setManager(CreateUpdateManager aThis) {
            ((AtomImpl)reference).setManager(aThis);
            return this;
        }
        
        @Override
        public Builder setFriendlyRowKey(String rowKey) {
            super.checkFriendlyRowKey(rowKey);
            reference.getSGID().setFriendlyRowKey(rowKey);
            return this;
        }
    }
}
