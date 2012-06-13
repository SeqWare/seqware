package com.github.seqware.model.impl.inMemory;

import com.github.seqware.model.Analysis;
import com.github.seqware.model.AnalysisSet;
import com.github.seqware.model.Atom;
import com.github.seqware.model.impl.AtomImpl;

/**
 * An in-memory representation of a AnalysisSet.
 *
 * @author dyuen
 */
public class InMemoryAnalysisSet extends AbstractInMemorySet<AnalysisSet, Analysis> implements AnalysisSet{
    
    private String name = null;
    private String description = null;
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override 
    public String getDescription(){
        return description;
    }

    
    public static AnalysisSet.Builder newBuilder() {
        return new InMemoryAnalysisSet.Builder();
    }

    @Override
    public AnalysisSet.Builder toBuilder() {
        InMemoryAnalysisSet.Builder b = new InMemoryAnalysisSet.Builder();
        b.aSet = (InMemoryAnalysisSet) this.copy(false);
        return b;
    }

    public static class Builder extends AnalysisSet.Builder {
        
        public Builder(){
            aSet = new InMemoryAnalysisSet();
        }

        @Override
        public AnalysisSet build(boolean newObject) {
            ((AtomImpl)aSet).getManager().objectCreated((Atom)aSet);
            return aSet;
        }

        @Override
        public InMemoryAnalysisSet.Builder setName(String name) {
            ((InMemoryAnalysisSet)aSet).name = name;
            return this;
        }
        
        @Override
        public InMemoryAnalysisSet.Builder setDescription(String description) {
            ((InMemoryAnalysisSet)aSet).description = description;
            return this;
        }
    }

}
