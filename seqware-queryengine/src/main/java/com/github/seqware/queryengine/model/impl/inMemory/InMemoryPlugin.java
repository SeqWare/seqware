package com.github.seqware.queryengine.model.impl.inMemory;

import com.github.seqware.queryengine.model.PluginRun;
import com.github.seqware.queryengine.model.Plugin;
import com.github.seqware.queryengine.model.Atom;
import com.github.seqware.queryengine.model.impl.AtomImpl;

/**
 * An in-memory representation of a Plugin.
 *
 * @author dyuen
 * @version $Id: $Id
 */
public class InMemoryPlugin extends AbstractInMemorySet<Plugin, PluginRun> implements Plugin{
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
     * @return a {@link com.github.seqware.queryengine.model.Plugin.Builder} object.
     */
    public static Plugin.Builder newBuilder() {
        return new InMemoryPlugin.Builder();
    }

    /** {@inheritDoc} */
    @Override
    public Plugin.Builder toBuilder() {
        InMemoryPlugin.Builder b = new InMemoryPlugin.Builder();
        b.aSet = (InMemoryPlugin) this.copy(true);
        return b;
    }

    /** {@inheritDoc} */
    @Override
    public Class getHBaseClass() {
        return Plugin.class;
    }

    /** {@inheritDoc} */
    @Override
    public String getHBasePrefix() {
        return Plugin.prefix;
    }

    public static class Builder extends Plugin.Builder {
        
        public Builder(){
            aSet = new InMemoryPlugin();
        }

        @Override
        public Plugin build(boolean newObject) {
            if(((AtomImpl)aSet).getManager() != null){
                ((AtomImpl)aSet).getManager().objectCreated((Atom)aSet);
            }
            return aSet;
        }

        @Override
        public InMemoryPlugin.Builder setName(String name) {
            ((InMemoryPlugin)aSet).name = name;
            return this;
        }
        
        @Override
        public InMemoryPlugin.Builder setDescription(String description) {
            ((InMemoryPlugin)aSet).description = description;
            return this;
        }
    }

}
