package com.github.seqware.model;

import com.github.seqware.factory.ModelManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An Analysis object represents specific calls to analysis components, most of
 * which will be implemented as an analysis plugin on the backend. An example
 * would be a coding consequence plugin.
 *
 * @author dyuen
 */
public abstract class Analysis extends Molecule<Analysis> implements QueryFuture {

    private List<Object> parameters = new ArrayList<Object>();
    
    /**
     * Create a new analysis
     *
     * @param plugin an analysis must have an associated plugin that created/is
     * creating its results
     */
    protected Analysis() {
        super();
    }
    
    /**
     * Get the parameters for this particular creation of an analysis plug-in
     * @return parameters for the plugin
     */
    public List<Object> getParameters(){
        return Collections.unmodifiableList(parameters);
    }

    /**
     * Get analysis plugin
     * @return 
     */
    public abstract AnalysisPluginInterface getPlugin();
    
    

    @Override
    public abstract FeatureSet get();

    @Override
    public abstract boolean isDone();
    
    @Override
    public abstract Analysis.Builder toBuilder();

    /**
     * Set up the analysis plugin 
     * @param plugin
     * @return 
     */
    protected abstract void setPlugin(AnalysisPluginInterface plugin);

    public abstract static class Builder implements BaseBuilder {

        public Analysis analysis;

        /**
         * Set the group for the current Atom
         *
         * @param group
         */
        public Analysis.Builder setParameters(List<Object> parameters) {
            analysis.parameters = parameters;
            return this;
        }
        
        public Analysis.Builder setPlugin(AnalysisPluginInterface plugin){
            analysis.setPlugin(plugin);
            return this;
        }

        @Override
        public abstract Analysis build();
        
        @Override
        public Analysis.Builder setManager(ModelManager aThis) {
            analysis.setManager(aThis);
            return this;
        }
    }
    
}
