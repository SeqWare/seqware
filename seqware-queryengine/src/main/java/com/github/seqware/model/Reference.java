package com.github.seqware.model;

import com.github.seqware.model.interfaces.BaseBuilder;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.impl.SimpleModelManager;
import com.github.seqware.model.impl.MoleculeImpl;
import java.util.Iterator;

/**
 * A reference is a particular build version such as "hg19" or "hg18".
 *
 * Note: Every reference needs to be part of a ReferenceSet.
 *
 * @author dyuen
 * @author jbaran
 */
public abstract class Reference extends MoleculeImpl {

    private String name = "Reference name place-holder";

    /**
     * Create a anonymous new reference
     */
    protected Reference() {
        super();
    }

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
    public String getName() {
        return name;
    }

    /**
     * Create an ACL builder started with a copy of this
     *
     * @return
     */
    @Override
    public abstract Reference.Builder toBuilder();

    public abstract static class Builder implements BaseBuilder {

        public Reference reference;

        public Reference.Builder setName(String name) {
            reference.name = name;
            return this;
        }

        @Override
        public abstract Reference build();

        @Override
        public Builder setManager(ModelManager aThis) {
            reference.setManager(aThis);
            return this;
        }
    }
}
