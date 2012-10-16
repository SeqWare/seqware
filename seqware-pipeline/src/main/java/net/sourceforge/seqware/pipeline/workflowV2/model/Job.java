package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.seqware.common.util.Log;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * <p>Job class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class Job {
    private String id;
    private JobModule module;
    private Collection<Job> parents;
    protected String name;
    protected String version;
    // non default profiles
    private Collection<JobProfile> profiles;
    private List<Argument> jobArgs;
    private WorkflowExecutable executable;
    private String algo;
    private boolean hasProcessingFile;
    private String inputFile;
    private String outputDir;
    private String command;
    private String queue;
    private boolean hasCommandIO = false;
    private String preJobDir;
    private boolean metadataOverridable = false;
    private List<Argument> moduleArgs;
    // default 1
    private int threadCount = 1;
    // default 5000
    private int maxMem = 5000;

    /**
     * <p>Constructor for Job.</p>
     */
    public Job() {
	this("");

    }

    /**
     * <p>Constructor for Job.</p>
     *
     * @param algo a {@link java.lang.String} object.
     */
    public Job(String algo) {
	this.algo = algo;
	this.name = "bash";
	this.version = "";
	this.parents = new ArrayList<Job>();
	this.moduleArgs = new ArrayList<Argument>();
	this.profiles = new ArrayList<JobProfile>();
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getId() {
	return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link java.lang.String} object.
     */
    public void setId(String id) {
	this.id = id;
    }

    /**
     * if executable is not null && executable is not CUSTOM
     *
     * @return executable.getName();
     */
    public String getName() {
	return this.name;
    }

    // not sure what the version number for bash
    /**
     * <p>Getter for the field <code>version</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getVersion() {
	return this.version;
    }

    /**
     * <p>getJobModule.</p>
     *
     * @return a {@link net.sourceforge.seqware.pipeline.workflowV2.model.JobModule} object.
     */
    public JobModule getJobModule() {
	return module;
    }

    /**
     * <p>Getter for the field <code>parents</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Job> getParents() {
	return this.parents;
    }

    /**
     * <p>addParent.</p>
     *
     * @param parent a {@link net.sourceforge.seqware.pipeline.workflowV2.model.Job} object.
     */
    public void addParent(Job parent) {
	Log.debug("adding parent " + parent);
	if (parent != null && !this.parents.contains(parent)) {
	    Log.debug("add parent " + parent);
	    this.parents.add(parent);
	}
    }

    /**
     * <p>addJobArgument.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     */
    public void addJobArgument(String key, String value) {
	Argument arg = new Argument(key, value);
	if (!this.jobArgs.contains(arg)) {
	    this.jobArgs.add(arg);
	}
    }

    /**
     * <p>addModuleArgument.</p>
     *
     * @param key a {@link java.lang.String} object.
     */
    public void addModuleArgument(String key) {
	Argument arg = new Argument(key, null);
	this.moduleArgs.add(arg);
    }

    /**
     * <p>addModuleArgument.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     */
    public void addModuleArgument(String key, String value) {
	Argument arg = new Argument(key, value);
	this.moduleArgs.add(arg);
    }

    /**
     * <p>getModuleArguments.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Argument> getModuleArguments() {
	return this.moduleArgs;
    }

    /**
     * <p>getJobArgument.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Argument> getJobArgument() {
	return this.jobArgs;
    }

    /**
     * <p>addProfile.</p>
     *
     * @param pf a {@link net.sourceforge.seqware.pipeline.workflowV2.model.JobProfile} object.
     */
    public void addProfile(JobProfile pf) {
	this.profiles.add(pf);
    }

    /**
     * <p>Getter for the field <code>profiles</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<JobProfile> getProfiles() {
	return this.profiles;
    }

    /**
     * <p>setAlgorithm.</p>
     *
     * @param algo a {@link java.lang.String} object.
     */
    public void setAlgorithm(String algo) {
	this.algo = algo;
    }

    /**
     * <p>getAlgorithm.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAlgorithm() {
	return this.algo;
    }

    /**
     * <p>Setter for the field <code>hasProcessingFile</code>.</p>
     *
     * @param b a boolean.
     */
    public void setHasProcessingFile(boolean b) {
	this.hasProcessingFile = b;
    }

    /**
     * <p>hasProcessingFile.</p>
     *
     * @return a boolean.
     */
    public boolean hasProcessingFile() {
	return this.hasProcessingFile;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
	if (obj instanceof Job == false)
	    return false;
	if (obj == this)
	    return true;
	Job rhs = (Job) obj;
	return new EqualsBuilder().append(id, rhs.id).isEquals();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    /**
     * <p>Setter for the field <code>inputFile</code>.</p>
     *
     * @param input a {@link java.lang.String} object.
     */
    public void setInputFile(String input) {
	this.inputFile = input;
    }

    /**
     * <p>Getter for the field <code>inputFile</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getInputFile() {
	return this.inputFile;
    }

    /**
     * <p>Setter for the field <code>outputDir</code>.</p>
     *
     * @param output a {@link java.lang.String} object.
     */
    public void setOutputDir(String output) {
	this.outputDir = output;
    }

    /**
     * <p>Getter for the field <code>outputDir</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getOutputDir() {
	return this.outputDir;
    }

    /**
     * <p>Setter for the field <code>threadCount</code>.</p>
     *
     * @param count a {@link java.lang.String} object.
     */
    public void setThreadCount(String count) {
	this.threadCount = Integer.parseInt(count);
    }

    /**
     * <p>Getter for the field <code>threadCount</code>.</p>
     *
     * @return a int.
     */
    public int getThreadCount() {
	return this.threadCount;
    }

    /**
     * <p>Setter for the field <code>command</code>.</p>
     *
     * @param command a {@link java.lang.String} object.
     */
    public void setCommand(String command) {
	this.setCommand(command, false);
    }

    /**
     * <p>Setter for the field <code>command</code>.</p>
     *
     * @param command a {@link java.lang.String} object.
     * @param autoIO a boolean.
     */
    public void setCommand(String command, boolean autoIO) {
	this.command = command;
	this.hasCommandIO = autoIO;
    }

    /**
     * <p>Getter for the field <code>command</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getCommand() {
	return this.command;
    }

    /**
     * <p>getMaxMemory.</p>
     *
     * @return a int.
     */
    public int getMaxMemory() {
	return this.maxMem;
    }

    /**
     * <p>setMaxMemory.</p>
     *
     * @param mem a {@link java.lang.String} object.
     */
    public void setMaxMemory(String mem) {
	this.maxMem = Integer.parseInt(mem);
    }

    /**
     * <p>Setter for the field <code>queue</code>.</p>
     *
     * @param queue a {@link java.lang.String} object.
     */
    public void setQueue(String queue) {
	this.queue = queue;
    }

    /**
     * <p>Getter for the field <code>queue</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getQueue() {
	return this.queue;
    }

    /**
     * <p>checkCommandIO.</p>
     *
     * @return a boolean.
     */
    public boolean checkCommandIO() {
	return this.hasCommandIO;
    }

    /**
     * <p>createPreJobDirtory.</p>
     *
     * @param dir a {@link java.lang.String} object.
     */
    public void createPreJobDirtory(String dir) {
	this.preJobDir = dir;
    }

    /**
     * <p>getPreJobDirectory.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPreJobDirectory() {
	return this.preJobDir;
    }

    /**
     * <p>isMetadataOverridden.</p>
     *
     * @return a boolean.
     */
    public boolean isMetadataOverridden() {
	return metadataOverridable;
    }

    /**
     * <p>setMetadataOverridden.</p>
     *
     * @param metadata a boolean.
     */
    public void setMetadataOverridden(boolean metadata) {
	this.metadataOverridable = metadata;
    }

}
