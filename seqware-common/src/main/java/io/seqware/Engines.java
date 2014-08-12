package io.seqware;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Engines {

    public enum TYPES {
        oozie("oozie"), oozie_sge("oozie-sge"), whitestar("whitestar"), whitestar_parallel("whitestar-parallel"), whitestar_sge(
                "whitestar-sge");
        private final String cliString;

        TYPES(String cliString) {
            this.cliString = cliString;
        }

        /**
         * @return the cliString
         */
        public String getCliString() {
            return cliString;
        }

        @Override
        public String toString() {
            return cliString;
        }
    }

    public static final String ENGINES_LIST = Engines.TYPES.oozie + ", " + Engines.TYPES.oozie_sge + ", " + Engines.TYPES.whitestar + ", "
            + Engines.TYPES.whitestar_parallel + ", " + Engines.TYPES.whitestar_sge;
    public static final String DEFAULT_ENGINE = Engines.TYPES.oozie.toString();
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
     * Check whether the workflow engine is whitestar-based.
     * 
     * @param engine
     * @return
     */
    public static boolean isWhiteStar(final String engine) {
        return engine != null && engine.startsWith("whitestar");
    }

    /**
     * Check whether the workflow engine supports parallel operation.
     * 
     * @param engine
     * @return
     */
    public static boolean isWhiteStarParallel(final String engine) {
        return engine != null && engine.startsWith(TYPES.whitestar_parallel.getCliString());
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
