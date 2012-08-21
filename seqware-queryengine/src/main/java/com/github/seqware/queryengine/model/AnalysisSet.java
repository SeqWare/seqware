package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;

/**
 * An AnalysisSet object groups analysis events that are created by software
 * suites or related tools.
 *
 * @author dyuen
 */
public interface AnalysisSet extends MolSetInterface<AnalysisSet, Analysis> {
    public final static String prefix = "AnalysisSet";

    /**
     * Get the name of the analysisSet
     *
     * @return the name of the analysisSet
     */
    public String getName();
    
    /**
     * Get the description associated with this analysisSet
     * @return the description associated with this analysisSet
     */
    public String getDescription();

    /**
     * Create a AnalysisSet builder started with a copy of this
     * @return 
     */
    @Override
    public abstract AnalysisSet.Builder toBuilder();

    public abstract static class Builder extends BaseBuilder {

        public AnalysisSet aSet;
        
        @Override
        public AnalysisSet build() {
           return build(true);
        }

        public abstract AnalysisSet build(boolean newObject);

        @Override
        public AnalysisSet.Builder setManager(CreateUpdateManager aThis) {
            ((AtomImpl)aSet).setManager(aThis);
            return this;
        }

        public abstract AnalysisSet.Builder setName(String name);
        
        public abstract AnalysisSet.Builder setDescription(String description);
        
        @Override
        public AnalysisSet.Builder setFriendlyRowKey(String rowKey) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }


}
