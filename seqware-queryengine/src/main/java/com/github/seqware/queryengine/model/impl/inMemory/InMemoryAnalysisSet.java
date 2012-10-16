package com.github.seqware.queryengine.model.impl.inMemory;

import com.github.seqware.queryengine.model.Analysis;
import com.github.seqware.queryengine.model.AnalysisSet;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.impl.AtomImpl;

/**
 * An in-memory representation of a AnalysisSet.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class InMemoryAnalysisSet extends AbstractInMemorySet<AnalysisSet, Analysis> implements AnalysisSet{
    private String name = null;
    private String description = null;
    
    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }
    
    /** {@inheritDoc} */
    @Override 
    public String getDescription(){
        return description;
    }

    
    /**
     * <p>newBuilder.</p>
     *
     * @return a {@link com.github.seqware.queryengine.model.AnalysisSet.Builder} object.
     */
    public static AnalysisSet.Builder newBuilder() {
        return new InMemoryAnalysisSet.Builder();
    }

    /** {@inheritDoc} */
    @Override
    public AnalysisSet.Builder toBuilder() {
        InMemoryAnalysisSet.Builder b = new InMemoryAnalysisSet.Builder();
        b.aSet = (InMemoryAnalysisSet) this.copy(true);
        return b;
    }

    /** {@inheritDoc} */
    @Override
    public Class getHBaseClass() {
        return AnalysisSet.class;
    }

    /** {@inheritDoc} */
    @Override
    public String getHBasePrefix() {
        return AnalysisSet.prefix;
    }

    public static class Builder extends AnalysisSet.Builder {
        
        public Builder(){
            aSet = new InMemoryAnalysisSet();
        }

        @Override
        public AnalysisSet build(boolean newObject) {
            if(((AtomImpl)aSet).getManager() != null){
                ((AtomImpl)aSet).getManager().objectCreated((Atom)aSet);
            }
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
