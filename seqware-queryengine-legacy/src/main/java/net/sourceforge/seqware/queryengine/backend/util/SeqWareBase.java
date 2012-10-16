package net.sourceforge.seqware.queryengine.backend.util;

/**
 * <p>SeqWareBase class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SeqWareBase {
  
  protected boolean debug = false;

  /**
   * <p>log.</p>
   *
   * @param message a {@link java.lang.String} object.
   */
  protected void log(String message) {
    if (debug) {
      System.out.println(message);
    }
  }

  /**
   * <p>isDebug.</p>
   *
   * @return a boolean.
   */
  public boolean isDebug() {
    return debug;
  }

  /**
   * <p>Setter for the field <code>debug</code>.</p>
   *
   * @param debug a boolean.
   */
  public void setDebug(boolean debug) {
    this.debug = debug;
  }
  
}
