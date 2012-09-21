package net.sourceforge.seqware.common.util.processtools;

/**
 * Sleep for N seconds. Return true on success, return false if we were interrupted during sleep or time was less than 0.
 * 
 * @author jmendler
 *
 */
public class ProcessTools {
  public static boolean sleep(int seconds) {
    if ( seconds > 0 ) {
      try {
        Thread.sleep( seconds * 1000);
        return true;
      } catch (InterruptedException e) {
        return false;
      }
    }
    return false;
  }
}
