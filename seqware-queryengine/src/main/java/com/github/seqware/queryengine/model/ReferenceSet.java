package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;

/**
 * A ReferenceSet is a collection of references which are a collection of
 * contigs and coordinates. Consider an example to be "Homo Sapiens".
 *
 * @author dyuen
 * @author jbaran
 * @version $Id: $Id
 */
public interface ReferenceSet extends MolSetInterface<ReferenceSet, Reference> {
    /** Constant <code>prefix="ReferenceSet"</code> */
    public final static String prefix = "ReferenceSet";

    /**
     * Get the name of the reference set
     *
     * @return the name of the reference set
     */
    public String getName();
    
    /**
     * Get the organism associated with this reference set
     *
     * @return the organism associated with this reference set
     */
    public String getOrganism();

    /**
     * {@inheritDoc}
     *
     * Create a ReferenceSet builder started with a copy of this
     */
    @Override
    public abstract ReferenceSet.Builder toBuilder();

    public abstract static class Builder extends BaseBuilder {

        public ReferenceSet aSet;
        
        @Override
        public ReferenceSet build() {
           return build(true);
        }

        public abstract ReferenceSet build(boolean newObject);

        @Override
        public ReferenceSet.Builder setManager(CreateUpdateManager aThis) {
            ((AtomImpl)aSet).setManager(aThis);
            return this;
        }

        public abstract ReferenceSet.Builder setName(String name);
        
        public abstract ReferenceSet.Builder setOrganism(String organism);
        
        @Override
        public ReferenceSet.Builder setFriendlyRowKey(String rowKey) {
            super.checkFriendlyRowKey(rowKey);
            aSet.getSGID().setFriendlyRowKey(rowKey);
            return this;
        }
    }


}
