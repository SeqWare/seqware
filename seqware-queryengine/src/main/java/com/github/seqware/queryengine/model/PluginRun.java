package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.github.seqware.queryengine.model.impl.inMemory.InMemoryFeatureSet;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import com.github.seqware.queryengine.plugins.PluginRunnerInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An PluginRun object represents specific calls to pluginRun components, most
 * of which will be implemented as an pluginRun plug-in on the backend. An
 * example would be a coding consequence plug-in.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public final class PluginRun<ReturnType> extends MoleculeImpl<PluginRun> implements QueryFuture<ReturnType> {

    /**
     * Constant
     * <code>prefix="PluginRun"</code>
     */
    public final static String prefix = "PluginRun";
    private List<Object> parameters = new ArrayList<Object>();
    private PluginRunnerInterface<ReturnType> pluginRunner;

    /**
     * Create a new pluginrun
     */
    protected PluginRun() {
        super();
    }

    /**
     * Get the parameters for this particular creation of an pluginRun plug-in
     *
     * @return parameters for the plugin
     */
    public List<Object> getParameters() {
        return Collections.unmodifiableList(parameters);
    }
    
    /**
     * <p>newBuilder.</p>
     *
     * @return a {@link com.github.seqware.queryengine.model.FeatureSet.Builder} object.
     */
    public static PluginRun.Builder newBuilder() {
        return new PluginRun.Builder();
    }

    @Override
    public PluginRun.Builder toBuilder() {
        PluginRun.Builder b = new PluginRun.Builder();
        b.pRun = this.copy(true);
        return b;
    }

    /**
     * Get pluginRun plugin
     *
     * @return This returns the actual plugin that was used to create this
     * instance of an pluginRun.
     */
    public PluginRunnerInterface getPluginRunner() {
        return this.pluginRunner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReturnType get() {
        return pluginRunner.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDone() {
        return pluginRunner.getPlugin().isComplete();
    }

    @Override
    public Class getHBaseClass() {
        return PluginRun.class;
    }

    @Override
    public String getHBasePrefix() {
        return "PluginRun";
    }

    public static class Builder extends BaseBuilder {
        private PluginRun pRun = new PluginRun();
        private List<Object> parameters;
        private PluginRunnerInterface pluginRunner;

        /**
         * Set the group for the current Atom
         *
         * @param group
         */
        public PluginRun.Builder setParameters(List<Object> parameters) {
            this.parameters = parameters;
            return this;
        }

        public PluginRun.Builder setPluginRunner(PluginRunnerInterface pluginRunner) {
            this.pluginRunner = pluginRunner;
            return this;
        }

        @Override
        public PluginRun build() {
            // TODO: add this check when persisting results for real
            //if (parameters == null && pluginRunner == null) {
            //    throw new RuntimeException("Invalid build of PluginRun");
            //}
            pRun.pluginRunner = pluginRunner;
            pRun.parameters = parameters;
            
            if (pRun.getManager() != null) {
                pRun.getManager().objectCreated(pRun);
            }
            return pRun;
        }

        @Override
        public PluginRun.Builder setManager(CreateUpdateManager aThis) {
            pRun.setManager(aThis);
            return this;
        }

        @Override
        public PluginRun.Builder setFriendlyRowKey(String rowKey) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
