package io.seqware;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Engines {

    public static final String ENGINES_LIST = "oozie, oozie-sge";
    public static final String DEFAULT_ENGINE = "oozie";
    public static final Set<String> ENGINES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(ENGINES_LIST.split(", "))));

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
