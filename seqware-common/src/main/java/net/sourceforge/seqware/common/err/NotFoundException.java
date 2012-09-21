package net.sourceforge.seqware.common.err;

public class NotFoundException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public NotFoundException() {
    super();
  }

  public NotFoundException(String message, Throwable e) {
    super(message, e);
  }

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException(Throwable e) {
    super(e);
  }

}
