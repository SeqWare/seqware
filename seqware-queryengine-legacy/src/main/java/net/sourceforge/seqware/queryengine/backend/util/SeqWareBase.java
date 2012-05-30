package net.sourceforge.seqware.queryengine.backend.util;

public class SeqWareBase {
  
  protected boolean debug = false;

  protected void log(String message) {
    if (debug) {
      System.out.println(message);
    }
  }

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }
  
}
