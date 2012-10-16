/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.factory;

import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;

/**
 * <p>FactoryInterface interface.</p>
 *
 * @author Brian O'Connor
 * @version $Id: $Id
 */
public interface FactoryInterface {

    /**
     * <p>getStore.</p>
     *
     * @param settings a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings} object.
     * @return a {@link net.sourceforge.seqware.queryengine.backend.store.Store} object.
     * @throws net.sourceforge.seqware.queryengine.backend.util.SeqWareException if any.
     * @throws java.lang.Exception if any.
     */
    public Store getStore(SeqWareSettings settings) throws SeqWareException, Exception;
    /**
     * <p>getSettings.</p>
     *
     * @return a {@link net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings} object.
     */
    public SeqWareSettings getSettings();
    
}
