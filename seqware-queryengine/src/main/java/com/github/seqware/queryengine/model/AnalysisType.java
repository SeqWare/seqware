package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;

/**
 * An AnalysisType object groups analysis events that are created by software
 * suites or related tools.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public interface AnalysisType extends MolSetInterface<AnalysisType, AnalysisRun> {
    /** Constant <code>prefix="AnalysisType"</code> */
    public final static String prefix = "AnalysisType";

    /**
     * Get the name of the analysisSet
     *
     * @return the name of the analysisSet
     */
    public String getName();
    
    /**
     * Get the description associated with this analysisSet
     *
     * @return the description associated with this analysisSet
     */
    public String getDescription();

    /**
     * {@inheritDoc}
     *
     * Create a AnalysisType builder started with a copy of this
     */
    @Override
    public abstract AnalysisType.Builder toBuilder();

    public abstract static class Builder extends BaseBuilder {

        public AnalysisType aSet;
        
        @Override
        public AnalysisType build() {
           return build(true);
        }

        public abstract AnalysisType build(boolean newObject);

        @Override
        public AnalysisType.Builder setManager(CreateUpdateManager aThis) {
            ((AtomImpl)aSet).setManager(aThis);
            return this;
        }

        public abstract AnalysisType.Builder setName(String name);
        
        public abstract AnalysisType.Builder setDescription(String description);
        
        @Override
        public AnalysisType.Builder setFriendlyRowKey(String rowKey) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }


}
