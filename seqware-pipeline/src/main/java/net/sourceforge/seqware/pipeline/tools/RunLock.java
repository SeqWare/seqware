package net.sourceforge.seqware.pipeline.tools;

import static net.sourceforge.seqware.common.util.Rethrow.rethrow;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import net.sourceforge.seqware.common.util.configtools.ConfigTools;

public class RunLock {

  public static void acquire() {
    try {
      JUnique.acquireLock(id());
    } catch (AlreadyLockedException e) {
      rethrow(e);
    }
  }

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
