/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.factory;

import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.backend.factory.FactoryInterface;

/**
 * @author Brian O'Connor
 *
 */
public abstract class Factory implements FactoryInterface {
    
    SeqWareSettings settings;
     
    public Store getStore(SeqWareSettings settings) throws SeqWareException, Exception {
       this.settings = settings;
       return(null);
    }

    public SeqWareSettings getSettings() {
        return settings;
    }
    
}
