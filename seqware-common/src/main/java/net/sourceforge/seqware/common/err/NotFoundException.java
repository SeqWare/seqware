package net.sourceforge.seqware.common.err;

/**
 * <p>NotFoundException class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class NotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * <p>Constructor for NotFoundException.</p>
   */
  public NotFoundException() {
    super();
  }

  /**
   * <p>Constructor for NotFoundException.</p>
   *
   * @param message a {@link java.lang.String} object.
   * @param e a {@link java.lang.Throwable} object.
   */
  public NotFoundException(String message, Throwable e) {
    super(message, e);
  }

  /**
   * <p>Constructor for NotFoundException.</p>
   *
   * @param message a {@link java.lang.String} object.
   */
  public NotFoundException(String message) {
    super(message);
  }

  /**
   * <p>Constructor for NotFoundException.</p>
   *
   * @param e a {@link java.lang.Throwable} object.
   */
  public NotFoundException(Throwable e) {
    super(e);
  }

}
