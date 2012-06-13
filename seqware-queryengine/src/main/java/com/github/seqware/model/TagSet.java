package com.github.seqware.model;

import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.impl.AtomImpl;
import com.github.seqware.model.interfaces.AbstractSet;
import com.github.seqware.model.interfaces.BaseBuilder;

/**
 * A set of Tags
 *
 * @author dyuen
 */
public interface TagSet extends AbstractSet<TagSet, Tag> {

    /**
     * Get the name of the tag set
     *
     * @return the name of the tag set
     */
    public String getName();

    /**
     * Create an FeatureSet builder started with a copy of this
     * @return 
     */
    @Override
    public abstract TagSet.Builder toBuilder();

    public abstract static class Builder implements BaseBuilder {

        public TagSet aSet;
        
        @Override
        public TagSet build() {
           return build(true);
        }

        public abstract TagSet build(boolean newObject);

        @Override
        public Builder setManager(ModelManager aThis) {
            ((AtomImpl)aSet).setManager(aThis);
            return this;
        }

        public abstract Builder setName(String name);
    }


}
