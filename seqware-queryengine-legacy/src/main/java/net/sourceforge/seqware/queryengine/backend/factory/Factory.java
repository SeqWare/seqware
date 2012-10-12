/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.factory;

import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.backend.factory.FactoryInterface;

/**
 * <p>Abstract Factory class.</p>
 *
 * @author Brian O'Connor
 * @version $Id: $Id
 */
public abstract class Factory implements FactoryInterface {
    
    SeqWareSettings settings;
     
    /** {@inheritDoc} */
    public Store getStore(SeqWareSettings settings) throws SeqWareException, Exception {
       this.settings = settings;
       return(null);
    }

    /**
     * <p>Getter for the field <code>settings</code>.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings} object.
     */
    public SeqWareSettings getSettings() {
        return settings;
    }
    
}
