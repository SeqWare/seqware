/**
 * 
 */
package net.sourceforge.seqware.queryengine.backend.factory.impl;

import net.sourceforge.seqware.queryengine.backend.store.Store;
import net.sourceforge.seqware.queryengine.backend.store.impl.BerkeleyDBStore;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareException;
import net.sourceforge.seqware.queryengine.backend.util.SeqWareSettings;
import net.sourceforge.seqware.queryengine.backend.factory.Factory;

/**
 * <p>BerkeleyDBFactory class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class BerkeleyDBFactory extends Factory {

    /** {@inheritDoc} */
    public BerkeleyDBStore getStore(SeqWareSettings settings) throws SeqWareException, Exception {
        super.getStore(settings);
        if ("berkeleydb-mismatch-store".equals(getSettings().getStoreType()) ||
        	"berkeleydb-consequence-annotation-store".equals(getSettings().getStoreType()) ||
        	"berkeleydb-dbSNP-annotation-store".equals(getSettings().getStoreType())) {
              BerkeleyDBStore ms = new BerkeleyDBStore();
              ms.setup(getSettings());
              return(ms);
        } else {
            throw new SeqWareException("SeqWare Exception: don't know how to generate a store object for "+getSettings().getStoreType());
        }
    }

}
