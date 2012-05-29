package com.github.seqware.model.impl.inMemory;

import com.github.seqware.model.Analysis;
import com.github.seqware.model.AnalysisPluginInterface;
import com.github.seqware.model.AnalysisSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An in-memory representation of a AnalysisSet.
 *
 * @author dyuen
 */
public class InMemoryAnalysisSet extends AnalysisSet {
    
    private Set<Analysis> analysisSet = new HashSet<Analysis>();
    
    /**
     * Construct AnalysisSet with a name
     * @param name AnalysisSet with a name
     */
    public InMemoryAnalysisSet(String name, String description){
        super(name, description);
    }

    @Override
    public Set<Analysis> getAnalysisSet() {
        return analysisSet;
    }

    @Override
    public Set<AnalysisPluginInterface> getPlugins() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Iterator<Analysis> iterator() {
        return analysisSet.iterator();
    }

    @Override
    public long getCount() {
        return analysisSet.size();
    }


}
