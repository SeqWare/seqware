package net.sourceforge.seqware.pipeline.workflowV2.model;

public class Environment {

    enum ExecutionEnvironment {
        CLUSTER, NODE
    }

    private int peakThreads;
    private int peakMemory;
    private ExecutionEnvironment exeEnv;

    // these variables are from metadata.xml
    private String compute;
    private String memory;
    private String network;
    // from .seqware/settings

    private String OOZIE_URL;
    private String OOZIE_APP_ROOT;
    private String OOZIE_JOBTRACKER;
    private String OOZIE_NAMENODE;
    private String OOZIE_QUEUENAME;

    private String OOZIE_WORK_DIR;

    private String mapred_job_tracker;
    private String fs_default_name;
    private String fs_defaultFS;
    private String fs_hdfs_impl;

    /**
     * 
     * @return the max threads for the workflow
     */
    public int getPeakThreads() {
        return peakThreads;
    }

    /**
     * set the max threads for the workflow
     * 
     * @param peakThreads
     */
    public void setPeakThreads(int peakThreads) {
        this.peakThreads = peakThreads;
    }

    /**
     * @return the max memory for the workflow
     */
    public int getPeakMemory() {
        return peakMemory;
    }

    /**
     * set the max memory for the workflow
     * 
     * @param peakMemory
     */
    public void setPeakMemory(int peakMemory) {
        this.peakMemory = peakMemory;
    }

    /**
     * 
     * @return the execution environment
     */
    public ExecutionEnvironment getExecutionEnv() {
        return exeEnv;
    }

    /**
     * set the execution environment
     * 
     * @param exeEnv
     */
    public void setExecutionEnv(ExecutionEnvironment exeEnv) {
        this.exeEnv = exeEnv;
    }

    public String getCompute() {
        return compute;
    }

    public void setCompute(String compute) {
        this.compute = compute;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getOOZIE_URL() {
        return OOZIE_URL;
    }

    public void setOOZIE_URL(String oOZIE_URL) {
        OOZIE_URL = oOZIE_URL;
    }

    public String getOOZIE_APP_ROOT() {
        return OOZIE_APP_ROOT;
    }

    public void setOOZIE_APP_ROOT(String oOZIE_APP_ROOT) {
        OOZIE_APP_ROOT = oOZIE_APP_ROOT;
    }

    public String getOOZIE_JOBTRACKER() {
        return OOZIE_JOBTRACKER;
    }

    public void setOOZIE_JOBTRACKER(String oOZIE_JOBTRACKER) {
        OOZIE_JOBTRACKER = oOZIE_JOBTRACKER;
    }

    public String getOOZIE_NAMENODE() {
        return OOZIE_NAMENODE;
    }

    public void setOOZIE_NAMENODE(String oOZIE_NAMENODE) {
        OOZIE_NAMENODE = oOZIE_NAMENODE;
    }

    public String getOOZIE_QUEUENAME() {
        return OOZIE_QUEUENAME;
    }

    public void setOOZIE_QUEUENAME(String oOZIE_QUEUENAME) {
        OOZIE_QUEUENAME = oOZIE_QUEUENAME;
    }

    public String getOOZIE_WORK_DIR() {
        return OOZIE_WORK_DIR;
    }

    public void setOOZIE_WORK_DIR(String oOZIE_WORK_DIR) {
        OOZIE_WORK_DIR = oOZIE_WORK_DIR;
    }

    public String getMapred_job_tracker() {
        return mapred_job_tracker;
    }

    public void setMapred_job_tracker(String mapred_job_tracker) {
        this.mapred_job_tracker = mapred_job_tracker;
    }

    public String getFs_default_name() {
        return fs_default_name;
    }

    public void setFs_default_name(String fs_default_name) {
        this.fs_default_name = fs_default_name;
    }

    public String getFs_defaultFS() {
        return fs_defaultFS;
    }

    public void setFs_defaultFS(String fs_defaultFS) {
        this.fs_defaultFS = fs_defaultFS;
    }

    public String getFs_hdfs_impl() {
        return fs_hdfs_impl;
    }

    public void setFs_hdfs_impl(String fs_hdfs_impl) {
        this.fs_hdfs_impl = fs_hdfs_impl;
    }

}
