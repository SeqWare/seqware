// Fetched from http://www.higherpass.com/java/Tutorials/Basic-File-Manipulation-With-Java/2/
package net.sourceforge.seqware.common.util.filetools.lock;

import java.io.File;

/**
 * Takes in a fileName String or File object that we want to create a lockfile
 * for
 * 
 * This object is no longer needed since LockingFileTools now uses standard Java locking mechanisms
 * 
 * @deprecated
 * @author jmendler
 * 
 */

@Deprecated
public class SimpleFileLock {
  private String fileName;
  private File file;

  public SimpleFileLock(String fileName) {
    this.fileName = fileName;
    this.file = new File(this.fileName + ".LOCK");
  }

  public SimpleFileLock(File fileToLock) {
    this.fileName = fileToLock.getAbsolutePath();
    this.file = new File(this.fileName + ".LOCK");
  }

  /**
   * Lock file, do not clear on exit
   * 
   * @return
   */
  public boolean getLock() {
    return getLock(false);
  }

  /**
   * Lock file, but give option to free lock when the JVM exits. Avoids dead
   * locks if lock holder fails.
   * 
   * @param freeOnExit
   * @return
   */
  public boolean getLock(boolean freeOnExit) {
    if (!this.isLocked()) {
      return (this.setLock(freeOnExit));
    }
    return (false);
  }

  public boolean getLock(int tries, int seconds) throws InterruptedException {
    boolean locked = false;
    for (int i = 0; i < tries; i++) {
      locked = this.getLock();
      if (locked) {
        return (true);
      }
      Thread.sleep(seconds * 1000);
    }
    return (false);
  }

  public void releaseLock() {
    file.delete();
  }

  public boolean isLocked() {
    return (file.exists());
  }

  private boolean setLock(boolean freeOnExit) {
    try {
      boolean ret = file.createNewFile();
      if (ret && freeOnExit) {
        file.deleteOnExit();
      }

      return ret;
    } catch (Exception e) {
      return (false);
    }
  }
}