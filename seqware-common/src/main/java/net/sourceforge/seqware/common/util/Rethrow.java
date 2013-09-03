package net.sourceforge.seqware.common.util;

public class Rethrow {

  /**
   * <p>
   * Rethrows <tt>t</tt> if unchecked, otherwise throws <tt>t</tt> wrapped in a
   * RuntimeException.
   * 
   * <p>
   * Use pattern:
   * 
   * <pre>
   * try {
   *   foobar();
   * } catch (Exception e) {
   *   rethrow(e); // import static
   * }
   * </pre>
   * 
   * @param t
   */
  public static void rethrow(Throwable t) {
    if (t instanceof RuntimeException)
      throw (RuntimeException) t;
    if (t instanceof Error)
      throw (Error) t;
    throw new RuntimeException(t);
  }

}
