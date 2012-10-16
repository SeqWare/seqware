// Fetched from http://www.higherpass.com/java/Tutorials/Basic-File-Manipulation-With-Java/2/
package net.sourceforge.seqware.common.util.filetools.lock;

import java.io.File;

/**
 * Takes in a fileName String or File object that we want to create a lockfile
 * for
 *
 * This object is no longer needed since LockingFileTools now uses standard Java locking mechanisms
 *
 * @author jmendler
 * @version $Id: $Id
 */

@Deprecated
public class SimpleFileLock {
  private String fileName;
  private File file;

  /**
   * <p>Constructor for SimpleFileLock.</p>
   *
   * @param fileName a {@link java.lang.String} object.
   */
  public SimpleFileLock(String fileName) {
    this.fileName = fileName;
    this.file = new File(this.fileName + ".LOCK");
  }

  /**
   * <p>Constructor for SimpleFileLock.</p>
   *
   * @param fileToLock a {@link java.io.File} object.
   */
  public SimpleFileLock(File fileToLock) {
    this.fileName = fileToLock.getAbsolutePath();
    this.file = new File(this.fileName + ".LOCK");
  }

  /**
   * Lock file, do not clear on exit
   *
   * @return a boolean.
   */
  public boolean getLock() {
    return getLock(false);
  }

  /**
   * Lock file, but give option to free lock when the JVM exits. Avoids dead
   * locks if lock holder fails.
   *
   * @param freeOnExit a boolean.
   * @return a boolean.
   */
  public boolean getLock(boolean freeOnExit) {
    if (!this.isLocked()) {
      return (this.setLock(freeOnExit));
    }
    return (false);
  }

  /**
   * <p>getLock.</p>
   *
   * @param tries a int.
   * @param seconds a int.
   * @return a boolean.
   * @throws java.lang.InterruptedException if any.
   */
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

  /**
   * <p>releaseLock.</p>
   */
  public void releaseLock() {
    file.delete();
  }

  /**
   * <p>isLocked.</p>
   *
   * @return a boolean.
   */
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
