package com.github.seqware.model.impl.inMemory;

import com.github.seqware.model.Analysis;
import com.github.seqware.model.AnalysisPluginInterface;
import com.github.seqware.model.FeatureSet;
import java.util.Set;

/**
 * An in-memory representation of an Analysis.
 *
 * @author dyuen
 */
public class InMemoryAnalysis extends Analysis {
    
    /**
     * Construct an In-Memory analysis with a specific plugin 
     * @param api 
     */
    public InMemoryAnalysis(AnalysisPluginInterface api){
        super(api);
    }

    @Override
    public Analysis getParentAnalysis() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Analysis> getSuccessorAnalysisSet() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureSet get() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isDone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
