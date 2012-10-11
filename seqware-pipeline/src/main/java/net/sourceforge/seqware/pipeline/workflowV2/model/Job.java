package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.seqware.common.util.Log;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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

    public Job() {
	this("");

    }

    public Job(String algo) {
	this.algo = algo;
	this.name = "bash";
	this.version = "";
	this.parents = new ArrayList<Job>();
	this.moduleArgs = new ArrayList<Argument>();
	this.profiles = new ArrayList<JobProfile>();
    }

    public String getId() {
	return id;
    }

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
    public String getVersion() {
	return this.version;
    }

    public JobModule getJobModule() {
	return module;
    }

    public Collection<Job> getParents() {
	return this.parents;
    }

    public void addParent(Job parent) {
	Log.debug("adding parent " + parent);
	if (parent != null && !this.parents.contains(parent)) {
	    Log.debug("add parent " + parent);
	    this.parents.add(parent);
	}
    }

    public void addJobArgument(String key, String value) {
	Argument arg = new Argument(key, value);
	if (!this.jobArgs.contains(arg)) {
	    this.jobArgs.add(arg);
	}
    }

    public void addModuleArgument(String key) {
	Argument arg = new Argument(key, null);
	this.moduleArgs.add(arg);
    }

    public void addModuleArgument(String key, String value) {
	Argument arg = new Argument(key, value);
	this.moduleArgs.add(arg);
    }

    public Collection<Argument> getModuleArguments() {
	return this.moduleArgs;
    }

    public Collection<Argument> getJobArgument() {
	return this.jobArgs;
    }

    public void addProfile(JobProfile pf) {
	this.profiles.add(pf);
    }

    public Collection<JobProfile> getProfiles() {
	return this.profiles;
    }

    public void setAlgorithm(String algo) {
	this.algo = algo;
    }

    public String getAlgorithm() {
	return this.algo;
    }

    public void setHasProcessingFile(boolean b) {
	this.hasProcessingFile = b;
    }

    public boolean hasProcessingFile() {
	return this.hasProcessingFile;
    }

    @Override
    public boolean equals(Object obj) {
	if (obj instanceof Job == false)
	    return false;
	if (obj == this)
	    return true;
	Job rhs = (Job) obj;
	return new EqualsBuilder().append(id, rhs.id).isEquals();
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    public void setInputFile(String input) {
	this.inputFile = input;
    }

    public String getInputFile() {
	return this.inputFile;
    }

    public void setOutputDir(String output) {
	this.outputDir = output;
    }

    public String getOutputDir() {
	return this.outputDir;
    }

    public void setThreadCount(String count) {
	this.threadCount = Integer.parseInt(count);
    }

    public int getThreadCount() {
	return this.threadCount;
    }

    public void setCommand(String command) {
	this.setCommand(command, false);
    }

    public void setCommand(String command, boolean autoIO) {
	this.command = command;
	this.hasCommandIO = autoIO;
    }

    public String getCommand() {
	return this.command;
    }

    public int getMaxMemory() {
	return this.maxMem;
    }

    public void setMaxMemory(String mem) {
	this.maxMem = Integer.parseInt(mem);
    }

    public void setQueue(String queue) {
	this.queue = queue;
    }

    public String getQueue() {
	return this.queue;
    }

    public boolean checkCommandIO() {
	return this.hasCommandIO;
    }

    public void createPreJobDirtory(String dir) {
	this.preJobDir = dir;
    }

    public String getPreJobDirectory() {
	return this.preJobDir;
    }

    public boolean isMetadataOverridden() {
	return metadataOverridable;
    }

    public void setMetadataOverridden(boolean metadata) {
	this.metadataOverridable = metadata;
    }

}
