package io.seqware;

public class Engines {

  public static boolean isOozie(String engine) {
    return engine != null && engine.startsWith("oozie");
  }

  public static boolean supportsCancel(String engine) {
    return isOozie(engine);
  }

  public static boolean supportsRetry(String engine) {
    return isOozie(engine);
  }

}
