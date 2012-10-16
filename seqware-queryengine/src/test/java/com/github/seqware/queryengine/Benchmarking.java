package com.github.seqware.queryengine;

/**
 * Global settings for benchmarking implementations.
 *
 * @author jbaran
 * @version $Id: $Id
 * @since 0.13.3
 */
public interface Benchmarking {

    /**
     * Number of runs to execute to determine average
     * serialization/de-serialization times.
     */
    public static final int BENCHMARK_RUNS = Integer.parseInt(System.getProperty("com.github.seqware.benchmark.runs", "10"));

    /**
     * Number of features that should be used for benchmarking
     * serialization/de-serialization.
     */
    public static final int BENCHMARK_FEATURES = Integer.parseInt(System.getProperty("com.github.seqware.benchmark.features", "10000"));

    /**
     * Run benchmarks: yes/no?
     */
    public static final boolean BENCHMARK = System.getProperty("com.github.seqware.benchmark", "false").equals("true");

}
