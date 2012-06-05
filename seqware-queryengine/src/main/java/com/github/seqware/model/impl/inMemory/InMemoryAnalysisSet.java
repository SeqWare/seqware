package com.github.seqware.model.impl.inMemory;

import com.github.seqware.model.Analysis;
import com.github.seqware.model.AnalysisPluginInterface;
import com.github.seqware.model.AnalysisSet;
import java.util.Collections;
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
     * Construct AnalysisSet
     *
     */
    protected InMemoryAnalysisSet(){
        super();
    }

    @Override
    public Set<Analysis> getAnalysisSet() {
        return (Set<Analysis>) Collections.unmodifiableCollection(analysisSet);
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

    /**
     * Create a new AnalysisSet builder
     *
     * @return
     */
    public static AnalysisSet.Builder newBuilder() {
        return new InMemoryAnalysisSet.Builder();
    }

    @Override
    public Builder toBuilder() {
        Builder b = new Builder();
        b.aSet = (InMemoryAnalysisSet) this.copy(false);
        return b;
    }

    public static class Builder extends AnalysisSet.Builder {

        public Builder(){
            aSet = new InMemoryAnalysisSet();
        }

        @Override
        public AnalysisSet build() {
            if (aSet.getName() == null || aSet.getDescription() == null) {
                throw new RuntimeException("Invalid build of AnalysisSet");
            }
            aSet.getManager().objectCreated(aSet);
            return aSet;
        }
    }
}
