package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.ModelManager;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;

/**
 * A set of Tag specifications
 *
 * @author dyuen
 */
public interface TagSpecSet extends MolSetInterface<TagSpecSet, Tag> {
    public final static String prefix = "TagSpecSet";

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
    public abstract TagSpecSet.Builder toBuilder();

    public abstract static class Builder implements BaseBuilder {

        public TagSpecSet aSet;
        
        @Override
        public TagSpecSet build() {
           return build(true);
        }

        public abstract TagSpecSet build(boolean newObject);

        @Override
        public Builder setManager(ModelManager aThis) {
            ((AtomImpl)aSet).setManager(aThis);
            return this;
        }

        public abstract Builder setName(String name);
    }


}
