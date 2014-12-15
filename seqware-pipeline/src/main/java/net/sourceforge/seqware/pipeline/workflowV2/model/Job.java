package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.Collection;
import java.util.Map;

public interface Job {
    /**
     * add a job specific file
     *
     * @param file
     */
    public void addFile(SqwFile file);

    /**
     * set the job command
     *
     * @param cmd
     * @return
     */
    public Command setCommand(String cmd);

    /**
     *
     * @return the job command object
     */
    public Command getCommand();

    /**
     * add job's parent
     *
     * @param parent
     * @return
     */
    public Job addParent(Job parent);

    /**
     *
     * @return job's parents
     */
    public Collection<Job> getParents();

    /**
     * set max memory
     *
     * @param mem
     * @return
     */
    public Job setMaxMemory(String mem);

    /**
     *
     * @return the max memory
     */
    public String getMaxMemory();

    /**
     * set the max thread number
     *
     * @param count
     * @return
     */
    public Job setThreads(int count);

    /**
     *
     * @return the max thread number
     */
    public int getThreads();

    /**
     * set the queue
     *
     * @param queue
     * @return
     */
    public Job setQueue(String queue);

    /**
     *
     * @return queue
     */
    public String getQueue();

    /**
     * job specific metadata is not supported yet
     *
     * @param metadata
     */
    public void setHasMetadataWriteback(boolean metadata);

    /**
     * job specific metadata is not supported yet
     *
     * @return
     */
    public boolean hasMetadataWriteback();

    /**
     * set parent accessiosns for the job
     *
     * @param parentAccessions
     */
    public void setParentAccessions(Collection<String> parentAccessions);

    /**
     * Whether this job should run locally instead of out on a cluster.
     *
     * @return true if it will run locally
     */
    public boolean isLocal();

    /**
     * Sets this job to run locally.
     */
    public void setLocal();

    /**
     * Sets whether this job should run locally.
     *
     * @param runLocal
     *            true to run locally, false otherwise
     */
    public void setLocal(boolean runLocal);

    /**
     * Set arbitrary key-value annotations on jobs.
     * 
     * @return
     */
    public Map<String, String> getAnnotations();

}
