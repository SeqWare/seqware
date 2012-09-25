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
 * 
 * @author yongliang
 */
@XmlRootElement
public class Workflow {
    private String name;
    private String version;
    private String finalDestination;
    private Map<String, String> properties;

    private Map<String, Job> jobs;

    public Workflow() {
	this.jobs = new LinkedHashMap<String, Job>();
    }

    public Workflow(Map<String, String> properties) {
	this.properties = properties;
	this.jobs = new LinkedHashMap<String, Job>();
    }

    public void setName(String name) {
	this.name = name;
    }

    @XmlAttribute
    public String getName() {
	return this.name;
    }

    public void setVersion(String version) {
	this.version = version;
    }

    @XmlAttribute
    public String getVersion() {
	return this.version;
    }

    @XmlElementWrapper(name = "jobs")
    @XmlElement(name = "job")
    public Collection<Job> getJobs() {
	return this.jobs.values();
    }

    public void setJobs(Collection<Job> jobs) {
	for (Job job : jobs) {
	    this.jobs.put(job.getName(), job);
	}
    }

    public void addJob(Job job) {
	int count = this.jobs.size();
	job.setId("" + count);
	this.jobs.put(job.getId(), job);
    }

    public Job getJobById(String id) {
	return this.jobs.get(id);
    }

    public void setFinalDestination(String input) {
	this.finalDestination = input;
    }

    public String getFinalDestination() {
	return this.finalDestination;
    }

    public String getProperty(String key) {
	return this.properties.get(key);
    }

    public String getBundleDir() {
	return this.properties.get("workflow_bundle_dir")
		+ System.getProperty("file.separator") + "Workflow_Bundle_"
		+ this.properties.get("workflow_name")
		+ System.getProperty("file.separator")
		+ this.properties.get("workflow_version");
    }

    public Map<String, String> getProperties() {
	return this.properties;
    }

    public Job createJavaJob(String algo) {
	Job job = new JavaJob(algo);
	this.addJob(job);
	return job;
    }

    public SeqwareModuleJob createSeqwareModuleJob(String algo) {
	return this.createSeqwareModuleJob(algo, Module.GenericCommandRunner,
		false);
    }

    public SeqwareModuleJob createSeqwareModuleJob(String algo, boolean local) {
	return this.createSeqwareModuleJob(algo, Module.GenericCommandRunner,
		local);
    }

    public SeqwareModuleJob createSeqwareModuleJob(String algo, Module module) {
	return this.createSeqwareModuleJob(algo, module, false);
    }

    public SeqwareModuleJob createSeqwareModuleJob(String algo, Module module,
	    boolean local) {
	SeqwareModuleJob job = new SeqwareModuleJob(algo, module, local);
	this.addJob(job);
	return job;
    }

    public Collection<Job> getJobsByAlgo(String algo) {
	List<Job> res = new ArrayList<Job>();
	for (Job job : this.jobs.values()) {
	    if (job.getAlgorithm().equals(algo)) {
		res.add(job);
	    }
	}
	return res;
    }

    public Job createJob(String algo) {
	Job job = new Job(algo);
	this.addJob(job);
	return job;
    }
}
