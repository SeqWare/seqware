package net.sourceforge.solexatools.authentication;

/**
 * <p>Abstract Authentication class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public abstract class Authentication {
  /**
   * <p>loginSuccess.</p>
   *
   * @param uid a {@link java.lang.String} object.
   * @param password a {@link java.lang.String} object.
   * @return a boolean.
   */
  public boolean loginSuccess(String uid, String password) {
    return false;
  }
}
