package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;

/**
 * A set of Tag specifications
 *
 * @author dyuen
 */
public interface TagSet extends MolSetInterface<TagSet, Tag> {
    public final static String prefix = "TagSet";

    /**
     * Get the name of the tag set
     *
     * @return the name of the tag set
     */
    public String getName();
    
    /**
     * 
     * @return true iff this set already contains the key for tag  
     */
    public boolean containsKey(String tagKey);
    
    /**
     * 
     * @param tagKey
     * @return a tag specification given a particular key
     */
    public Tag get(String tagKey);

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
        public Builder setManager(CreateUpdateManager aThis) {
            ((AtomImpl)aSet).setManager(aThis);
            return this;
        }

        public abstract Builder setName(String name);
    }


}
