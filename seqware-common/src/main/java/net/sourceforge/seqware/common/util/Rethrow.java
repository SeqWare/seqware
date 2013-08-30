package net.sourceforge.seqware.common.util;

public class Rethrow {

  public static void rethrow(Throwable t) {
    if (t instanceof RuntimeException)
      throw (RuntimeException) t;
    if (t instanceof Error)
      throw (Error) t;
    throw new RuntimeException(t);
  }

}
