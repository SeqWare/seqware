package com.github.seqware.queryengine.model;

import com.github.seqware.queryengine.factory.CreateUpdateManager;
import com.github.seqware.queryengine.model.impl.AtomImpl;
import com.github.seqware.queryengine.model.interfaces.BaseBuilder;
import com.github.seqware.queryengine.model.interfaces.MolSetInterface;

/**
 * An Plugin object groups pluginrun events that are created by software
 * suites or related tools.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public interface Plugin extends MolSetInterface<Plugin, PluginRun> {
    /** Constant <code>prefix="Plugin"</code> */
    public final static String prefix = "Plugin";

    /**
     * Get the name of the plugin
     *
     * @return the name of the plugin
     */
    public String getName();
    
    /**
     * Get the description associated with this plugin
     *
     * @return the description associated with this plugin
     */
    public String getDescription();

    /**
     * {@inheritDoc}
     *
     * Create a Plugin builder started with a copy of this
     */
    @Override
    public abstract Plugin.Builder toBuilder();

    public abstract static class Builder extends BaseBuilder {

        public Plugin aSet;
        
        @Override
        public Plugin build() {
           return build(true);
        }

        public abstract Plugin build(boolean newObject);

        @Override
        public Plugin.Builder setManager(CreateUpdateManager aThis) {
            ((AtomImpl)aSet).setManager(aThis);
            return this;
        }

        public abstract Plugin.Builder setName(String name);
        
        public abstract Plugin.Builder setDescription(String description);
        
        @Override
        public Plugin.Builder setFriendlyRowKey(String rowKey) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }


}
