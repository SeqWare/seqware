package com.github.seqware.queryengine.model.impl.inMemory;

import com.github.seqware.queryengine.model.AnalysisRun;
import com.github.seqware.queryengine.model.AnalysisType;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.impl.AtomImpl;

/**
 * An in-memory representation of a AnalysisType.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class InMemoryAnalysisType extends AbstractInMemorySet<AnalysisType, AnalysisRun> implements AnalysisType{
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
     * @return a {@link com.github.seqware.queryengine.model.AnalysisType.Builder} object.
     */
    public static AnalysisType.Builder newBuilder() {
        return new InMemoryAnalysisType.Builder();
    }

    /** {@inheritDoc} */
    @Override
    public AnalysisType.Builder toBuilder() {
        InMemoryAnalysisType.Builder b = new InMemoryAnalysisType.Builder();
        b.aSet = (InMemoryAnalysisType) this.copy(true);
        return b;
    }

    /** {@inheritDoc} */
    @Override
    public Class getHBaseClass() {
        return AnalysisType.class;
    }

    /** {@inheritDoc} */
    @Override
    public String getHBasePrefix() {
        return AnalysisType.prefix;
    }

    public static class Builder extends AnalysisType.Builder {
        
        public Builder(){
            aSet = new InMemoryAnalysisType();
        }

        @Override
        public AnalysisType build(boolean newObject) {
            if(((AtomImpl)aSet).getManager() != null){
                ((AtomImpl)aSet).getManager().objectCreated((Atom)aSet);
            }
            return aSet;
        }

        @Override
        public InMemoryAnalysisType.Builder setName(String name) {
            ((InMemoryAnalysisType)aSet).name = name;
            return this;
        }
        
        @Override
        public InMemoryAnalysisType.Builder setDescription(String description) {
            ((InMemoryAnalysisType)aSet).description = description;
            return this;
        }
    }

}
