package net.sourceforge.seqware.pipeline.tools;

import static net.sourceforge.seqware.common.util.Rethrow.rethrow;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

/**
 * <p>
 * Allows for preventing concurrent processes <em>owned by the same user</em>.
 * 
 * <p>
 * By default the lock identifier is a constant. If more flexibility is needed, provide a value to <tt>SW_LOCK_ID</tt> in the seqware
 * settings file.
 * 
 * <p>
 * WARNING: This system will fail to prevent concurrent processes if the value of the SW_LOCK_ID is changed/added/removed in the time
 * between multiple processes acquiring a lock.
 * 
 */
public class RunLock {

    /**
     * Acquires a lock or throws an unchecked exception if it could not be acquired.
     */
    public static void acquire() {
        try {
            JUnique.acquireLock(id());
        } catch (AlreadyLockedException e) {
            rethrow(e);
        }
    }

    /**
     * Releases a lock if one was acquired, otherwise no-op.
     */
    public static void release() {
        JUnique.releaseLock(id());
    }

    private static String id() {
        // SEQWARE-1732 custom lock ID
        String id = ConfigTools.getSettings().get("SW_LOCK_ID");
        if (id == null) {
            id = "seqware";
        }
        return id;
    }
}
