/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.pipeline.workflowV2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>Workflow class.</p>
 *
 * @author yongliang
 * @version $Id: $Id
 */
@XmlRootElement
public class Workflow {
    private String name;
    private String version;
    private String finalDestination;
    private Map<String, String> properties;

    private Map<String, Job> jobs;

    /**
     * <p>Constructor for Workflow.</p>
     */
    public Workflow() {
	this.jobs = new LinkedHashMap<String, Job>();
    }

    /**
     * <p>Constructor for Workflow.</p>
     *
     * @param properties a {@link java.util.Map} object.
     */
    public Workflow(Map<String, String> properties) {
	this.properties = properties;
	this.jobs = new LinkedHashMap<String, Job>();
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @XmlAttribute
    public String getName() {
	return this.name;
    }

    /**
     * <p>Setter for the field <code>version</code>.</p>
     *
     * @param version a {@link java.lang.String} object.
     */
    public void setVersion(String version) {
	this.version = version;
    }

    /**
     * <p>Getter for the field <code>version</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @XmlAttribute
    public String getVersion() {
	return this.version;
    }

    /**
     * <p>Getter for the field <code>jobs</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    @XmlElementWrapper(name = "Jobs")
    @XmlElement(name = "Job")
    public Collection<Job> getJobs() {
	return this.jobs.values();
    }

    /**
     * <p>Setter for the field <code>jobs</code>.</p>
     *
     * @param jobs a {@link java.util.Collection} object.
     */
    public void setJobs(Collection<Job> jobs) {
	for (Job job : jobs) {
	    this.jobs.put(job.getName(), job);
	}
    }

    /**
     * <p>addJob.</p>
     *
     * @param job a {@link net.sourceforge.seqware.pipeline.workflowV2.model.Job} object.
     */
    public void addJob(Job job) {
	int count = this.jobs.size();
	job.setId("" + count);
	this.jobs.put(job.getId(), job);
    }

    /**
     * <p>getJobById.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.pipeline.workflowV2.model.Job} object.
     */
    public Job getJobById(String id) {
	return this.jobs.get(id);
    }

    /**
     * <p>Setter for the field <code>finalDestination</code>.</p>
     *
     * @param input a {@link java.lang.String} object.
     */
    public void setFinalDestination(String input) {
	this.finalDestination = input;
    }

    /**
     * <p>Getter for the field <code>finalDestination</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFinalDestination() {
	return this.finalDestination;
    }

    /**
     * <p>getProperty.</p>
     *
     * @param key a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getProperty(String key) {
	return this.properties.get(key);
    }

    /**
     * <p>getBundleDir.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getBundleDir() {
	return this.properties.get("workflow_bundle_dir")
		+ System.getProperty("file.separator") + "Workflow_Bundle_"
		+ this.properties.get("workflow_name")
		+ System.getProperty("file.separator")
		+ this.properties.get("workflow_version");
    }

    /**
     * <p>Getter for the field <code>properties</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<String, String> getProperties() {
	return this.properties;
    }

    /**
     * <p>createJavaJob.</p>
     *
     * @param algo a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.pipeline.workflowV2.model.Job} object.
     */
    public Job createJavaJob(String algo) {
	Job job = new JavaJob(algo);
	this.addJob(job);
	return job;
    }

    /**
     * <p>createSeqwareModuleJob.</p>
     *
     * @param algo a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.pipeline.workflowV2.model.SeqwareModuleJob} object.
     */
    public SeqwareModuleJob createSeqwareModuleJob(String algo) {
	return this.createSeqwareModuleJob(algo, Module.GenericCommandRunner,
		false);
    }

    /**
     * <p>createSeqwareModuleJob.</p>
     *
     * @param algo a {@link java.lang.String} object.
     * @param local a boolean.
     * @return a {@link net.sourceforge.seqware.pipeline.workflowV2.model.SeqwareModuleJob} object.
     */
    public SeqwareModuleJob createSeqwareModuleJob(String algo, boolean local) {
	return this.createSeqwareModuleJob(algo, Module.GenericCommandRunner,
		local);
    }

    /**
     * <p>createSeqwareModuleJob.</p>
     *
     * @param algo a {@link java.lang.String} object.
     * @param module a {@link net.sourceforge.seqware.pipeline.workflowV2.model.Module} object.
     * @return a {@link net.sourceforge.seqware.pipeline.workflowV2.model.SeqwareModuleJob} object.
     */
    public SeqwareModuleJob createSeqwareModuleJob(String algo, Module module) {
	return this.createSeqwareModuleJob(algo, module, false);
    }

    /**
     * <p>createSeqwareModuleJob.</p>
     *
     * @param algo a {@link java.lang.String} object.
     * @param module a {@link net.sourceforge.seqware.pipeline.workflowV2.model.Module} object.
     * @param local a boolean.
     * @return a {@link net.sourceforge.seqware.pipeline.workflowV2.model.SeqwareModuleJob} object.
     */
    public SeqwareModuleJob createSeqwareModuleJob(String algo, Module module,
	    boolean local) {
	SeqwareModuleJob job = new SeqwareModuleJob(algo, module, local);
	this.addJob(job);
	return job;
    }

    /**
     * <p>getJobsByAlgo.</p>
     *
     * @param algo a {@link java.lang.String} object.
     * @return a {@link java.util.Collection} object.
     */
    public Collection<Job> getJobsByAlgo(String algo) {
	List<Job> res = new ArrayList<Job>();
	for (Job job : this.jobs.values()) {
	    if (job.getAlgorithm().equals(algo)) {
		res.add(job);
	    }
	}
	return res;
    }

    /**
     * <p>createJob.</p>
     *
     * @param algo a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.pipeline.workflowV2.model.Job} object.
     */
    public Job createJob(String algo) {
	Job job = new Job(algo);
	this.addJob(job);
	return job;
    }
}
