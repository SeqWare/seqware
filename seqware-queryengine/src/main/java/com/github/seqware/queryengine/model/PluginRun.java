package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.MoleculeImpl;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import com.github.seqware.queryengine.plugins.PluginInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An PluginRun object represents specific calls to pluginRun components, most of
 * which will be implemented as an pluginRun plug-in on the backend. An example
 * would be a coding consequence plug-in.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public abstract class PluginRun<ReturnType> extends MoleculeImpl<PluginRun> implements QueryFuture<ReturnType> {

    /** Constant <code>prefix="PluginRun"</code> */
    public final static String prefix = "PluginRun";
    private List<Object> parameters = new ArrayList<Object>();

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
     * Get pluginRun plugin
     *
     * @return This returns the actual plugin that was used to create this
     * instance of an pluginRun.
     */
    public abstract PluginInterface getPlugin();

    /** {@inheritDoc} */
    @Override
    public abstract ReturnType get();

    /** {@inheritDoc} */
    @Override
    public abstract boolean isDone();

    /** {@inheritDoc} */
    @Override
    public abstract PluginRun.Builder toBuilder();

    /**
     * Set up the pluginRun plug-in
     *
     * @param plugin Set the plug-in used to create this plug-in (should be in the builder)
     */
    protected abstract void setPlugin(PluginInterface plugin);

    public abstract static class Builder extends BaseBuilder {

        public PluginRun pluginRun;

        /**
         * Set the group for the current Atom
         *
         * @param group
         */
        public PluginRun.Builder setParameters(List<Object> parameters) {
            pluginRun.parameters = parameters;
            return this;
        }

        public PluginRun.Builder setPlugin(PluginInterface plugin) {
            pluginRun.setPlugin(plugin);
            return this;
        }

        @Override
        public abstract PluginRun build();

        @Override
        public PluginRun.Builder setManager(CreateUpdateManager aThis) {
            pluginRun.setManager(aThis);
            return this;
        }
        
        @Override
        public PluginRun.Builder setFriendlyRowKey(String rowKey) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
