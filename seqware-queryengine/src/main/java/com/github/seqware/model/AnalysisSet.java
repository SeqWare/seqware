package com.github.seqware.model;

import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.impl.AtomImpl;
import com.github.seqware.model.interfaces.AbstractSet;
import com.github.seqware.model.interfaces.BaseBuilder;

/**
 * An AnalysisSet object groups analysis events that are created by software
 * suites or related tools.
 *
 * @author dyuen
 */
public interface AnalysisSet extends AbstractSet<AnalysisSet, Analysis> {
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

    public abstract static class Builder implements BaseBuilder {

        public AnalysisSet aSet;
        
        @Override
        public AnalysisSet build() {
           return build(true);
        }

        public abstract AnalysisSet build(boolean newObject);

        @Override
        public AnalysisSet.Builder setManager(ModelManager aThis) {
            ((AtomImpl)aSet).setManager(aThis);
            return this;
        }

        public abstract AnalysisSet.Builder setName(String name);
        
        public abstract AnalysisSet.Builder setDescription(String description);
    }


}
