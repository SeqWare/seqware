/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.factory;

import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;

/**
 * @author Brian O'Connor
 *
 */
public interface FactoryInterface {

    public Store getStore(SeqWareSettings settings) throws SeqWareException, Exception;
    public SeqWareSettings getSettings();
    
}
