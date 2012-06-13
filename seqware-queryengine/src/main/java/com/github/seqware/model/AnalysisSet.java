package com.github.seqware.model;

import com.github.seqware.model.interfaces.BaseBuilder;
import com.github.seqware.model.interfaces.AbstractSet;
import com.github.seqware.factory.ModelManager;
import com.github.seqware.model.impl.MoleculeImpl;
import java.util.Set;

/**
 * An AnalysisSet object groups analysis events that are created by software
 * suites or related tools.
 *
 * @author dyuen
 */
public abstract class AnalysisSet extends MoleculeImpl<AnalysisSet> implements AbstractSet<AnalysisSet, Analysis> {

    private String name = "AnalysisSet name place-holder";
    private String description = "AnalysisSet descripion placeholder";

    /**
     * Creates an instance of an anonymous feature set.
     */
    protected AnalysisSet() {
        super();
    }

    /**
     * The set of analysis this instance represents.
     *
     * @return get the set of Analysis events
     */
    public abstract Set<Analysis> getAnalysisSet();

    /**
     * The set of plug-ins that this AnalysisSet uses
     *
     * @return get the set of relevant plug-ins for this AnalysisSet
     */
    public abstract Set<AnalysisPluginInterface> getPlugins();

    /**
     * Description of this analysis set (ex: funky software suite that really
     * rocks)
     *
     * @return description of this analysis set
     */
    public String getDescription() {
        return description;
    }

    /**
     * Name of this analysis set (ex: Funky Suite v1)
     * @return name of the analysis set
     */
    public String getName() {
        return name;
    }
    
     /**
     * Create an ACL builder started with a copy of this
     * @return 
     */
    @Override
    public abstract AnalysisSet.Builder toBuilder();

    public abstract static class Builder implements BaseBuilder{

        public AnalysisSet aSet;

        public AnalysisSet.Builder setName(String name) {
            aSet.name = name;
            return this;
        }

        public AnalysisSet.Builder setDescription(String description) {
            aSet.description = description;
            return this;
        }
        
        @Override
        public abstract AnalysisSet build();

        @Override
        public Builder setManager(ModelManager aThis) {
            aSet.setManager(aThis);
            return this;
        }
    }
}
