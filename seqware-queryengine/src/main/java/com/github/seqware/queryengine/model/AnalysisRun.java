package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import com.github.seqware.queryengine.plugins.AnalysisPluginInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An AnalysisRun object represents specific calls to analysis components, most of
 * which will be implemented as an analysis plug-in on the backend. An example
 * would be a coding consequence plug-in.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public abstract class AnalysisRun<ReturnType> extends MoleculeImpl<AnalysisRun> implements QueryFuture<ReturnType> {

    /** Constant <code>prefix="AnalysisRun"</code> */
    public final static String prefix = "Analysis";
    private List<Object> parameters = new ArrayList<Object>();

    /**
     * Create a new analysis
     */
    protected AnalysisRun() {
        super();
    }

    /**
     * Get the parameters for this particular creation of an analysis plug-in
     *
     * @return parameters for the plugin
     */
    public List<Object> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    /**
     * Get analysis plugin
     *
     * @return This returns the actual plugin that was used to create this
     * instance of an analysis.
     */
    public abstract AnalysisPluginInterface getPlugin();

    /** {@inheritDoc} */
    @Override
    public abstract ReturnType get();

    /** {@inheritDoc} */
    @Override
    public abstract boolean isDone();

    /** {@inheritDoc} */
    @Override
    public abstract AnalysisRun.Builder toBuilder();

    /**
     * Set up the analysis plug-in
     *
     * @param plugin Set the plug-in used to create this plug-in (should be in the builder)
     */
    protected abstract void setPlugin(AnalysisPluginInterface plugin);

    public abstract static class Builder extends BaseBuilder {

        public AnalysisRun analysis;

        /**
         * Set the group for the current Atom
         *
         * @param group
         */
        public AnalysisRun.Builder setParameters(List<Object> parameters) {
            analysis.parameters = parameters;
            return this;
        }

        public AnalysisRun.Builder setPlugin(AnalysisPluginInterface plugin) {
            analysis.setPlugin(plugin);
            return this;
        }

        @Override
        public abstract AnalysisRun build();

        @Override
        public AnalysisRun.Builder setManager(CreateUpdateManager aThis) {
            analysis.setManager(aThis);
            return this;
        }
        
        @Override
        public AnalysisRun.Builder setFriendlyRowKey(String rowKey) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
