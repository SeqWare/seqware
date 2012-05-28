package com.github.seqware.model;

import java.util.Set;

/**
 * An Analysis object represents specific calls to analysis components, most of
 * which will be implemented as an analysis plugin on the backend. An example
 * would be a coding consequence plugin.
 *
 * @author dyuen
 */
public abstract class Analysis extends Atom implements QueryFuture {

    private Object[] parameters;
    private AnalysisPluginInterface plugin;
    
    /**
     * Create a new analysis
     *
     * @param plugin an analysis must have an associated plugin that created/is
     * creating its results
     */
    public Analysis(AnalysisPluginInterface plugin) {
        super();
        this.plugin = plugin;
    }

    /**
     * Get the analysis event that this analysis was derived from
     *
     * @return parental analysis event, null if n/a
     */
    public abstract Analysis getParentAnalysis();

    /**
     * Get the analysis events that are derived from this one
     *
     * @return a set of analysis events that derive from this one
     */
    public abstract Set<Analysis> getSuccessorAnalysisSet();
    
    /**
     * Get the parameters for this particular creation of an analysis plug-in
     * @return paramaters for the plugin
     */
    public Object[] getParameters(){
        return parameters;
    }

    /**
     * Get the plugin that created this analysis
     * @return plugin for this analysis
     */
    public AnalysisPluginInterface getPlugin() {
        return plugin;
    }
    
    
}
