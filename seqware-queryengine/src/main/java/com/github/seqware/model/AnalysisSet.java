package com.github.seqware.model;

import com.github.seqware.impl.SimpleModelManager;
import com.github.seqware.util.SeqWareIterable;
import java.util.Set;

/**
 * An AnalysisSet object groups analysis events that are created by software
 * suites or related tools.
 *
 * @author dyuen
 */
public abstract class AnalysisSet extends Molecule implements SeqWareIterable<Analysis> {

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
    public abstract AnalysisSet.Builder toBuilder();

    public abstract static class Builder {

        public AnalysisSet aSet;

        public AnalysisSet.Builder setName(String name) {
            aSet.name = name;
            return this;
        }

        public AnalysisSet.Builder setDescription(String description) {
            aSet.description = description;
            return this;
        }
        
        public AnalysisSet build() {
           return build(true);
        }

        public abstract AnalysisSet build(boolean newObject);

        public Builder setManager(SimpleModelManager aThis) {
            aSet.setManager(aThis);
            return this;
        }
    }
}
