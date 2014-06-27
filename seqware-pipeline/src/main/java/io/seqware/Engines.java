package io.seqware;

public class Engines {

    /**
     * Check whether the workflow engine is Oozie-based.
     * 
     * @param engine
     * @return
     */
    public static boolean isOozie(final String engine) {
        return engine != null && engine.startsWith("oozie");
    }

    /**
     * Check whether the workflow engine supports cancel.
     * 
     * @param engine
     * @return
     */
    public static boolean supportsCancel(final String engine) {
        return isOozie(engine);
    }

    /**
     * Check whether the workflow engine supports retry.
     * 
     * @param engine
     * @return
     */
    public static boolean supportsRetry(final String engine) {
        return isOozie(engine);
    }

}
