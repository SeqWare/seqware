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
package net.sourceforge.seqware.pipeline.workflowV2.pegasus.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.workflowV2.model.Job1;
import net.sourceforge.seqware.pipeline.workflowV2.model.Module;
import net.sourceforge.seqware.pipeline.workflowV2.model.SeqwareModuleJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow2;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.WorkflowExecutableUtils;

import org.jdom.Element;

/**
 * 
 * @author yongliang
 */
public class Adag extends PegasusAbstract {
    private Collection<WorkflowExecutable> executables;
    private Map<String, PegasusJob> jobs;

    private String schemaLocation = "http://pegasus.isi.edu/schema/DAX http://pegasus.isi.edu/schema/dax-3.2.xsd";
    private String version = "3.2";
    private String count = "1";
    private String index = "0";

    public Adag(Workflow2 wf) {
	this.jobs = new LinkedHashMap<String, PegasusJob>();
	this.parseWorkflow(wf);
	this.setDefaultExcutables();
    }
    


    private void setDefaultExcutables() {
	executables = new ArrayList<WorkflowExecutable>();
	executables.add(WorkflowExecutableUtils.getDefaultJavaExcutable(this
		.getWorkflow().getProperties()));
	executables.add(WorkflowExecutableUtils.getLocalJavaExcutable(this
		.getWorkflow().getProperties()));
	executables.add(WorkflowExecutableUtils.getDefaultPerlExcutable(this
		.getWorkflow().getProperties()));
	executables.add(WorkflowExecutableUtils
		.getDefaultDirManagerExcutable(this.getWorkflow()
			.getProperties()));
	executables
		.add(WorkflowExecutableUtils.getDefaultSeqwareExecutable(this
			.getWorkflow().getProperties()));
    }

    @Override
    public Element serializeXML() {
	Element adag = new Element("adag", NAMESPACE);
	adag.addNamespaceDeclaration(XSI);
	adag.setAttribute("schemaLocation", schemaLocation, XSI);
	adag.setAttribute("version", version);
	adag.setAttribute("count", count);
	adag.setAttribute("index", index);
	adag.setAttribute("name", this.getWorkflow().getName());

	for (WorkflowExecutable ex : executables) {
	    adag.addContent(ex.serializeXML());
	}

	for (PegasusJob pjob : this.jobs.values()) {
	    adag.addContent(pjob.serializeXML());
	}
	// dependencies
	for (PegasusJob pjob : this.jobs.values()) {
	    if (pjob.getParents().isEmpty())
		continue;
	    for (PegasusJob parent : pjob.getParents()) {

		adag.addContent(pjob.getDependentElement(parent));

	    }
	}
	return adag;
    }

    private void parseWorkflow(Workflow2 wf) {
	this.setWorkflow(wf);
	for (Job1 job : wf.getJobs()) {
	    PegasusJob pjob = this.createPegasusJob(job);
	    this.addJob(pjob);
	}
	// preprocess jobs
	this.preprocessJobs();
	this.setParentAccessionIds();
	this.setCommandIO();
    }

    private void setCommandIO() {
	for (PegasusJob job : this.jobs.values()) {
	    if (!job.checkCommandIO())
		continue;

	    for (PegasusJob pjob : job.getParents()) {
		if (!pjob.hasCommandOutput()) {
		    Log.error("******* check output ****** ");
		}
		job.addCommandInput(pjob.getCommandOutput());
	    }

	}
    }

    private PegasusJob createPegasusJob(Job1 job) {
	PegasusJob pjob = null;
	if (job instanceof SeqwareModuleJob) {
	    pjob = new PegasusSeqwareModuleJob(job);
	} else {
	    pjob = new PegasusJob(job);
	}
	return pjob;
    }

    public void addJob(PegasusJob job) {
	this.jobs.put(job.getId(), job);
    }

    private void preprocessJobs() {
	List<PegasusJob> provisionFiles = new ArrayList<PegasusJob>();
	// set parents
	for (Map.Entry<String, PegasusJob> entry : this.jobs.entrySet()) {
	    // check if the job has provisionfiles dependency
	    PegasusJob pjob = entry.getValue();
	    if (pjob.hasProvisionFilesDependent()) {
		provisionFiles.add(pjob);
	    }

	    this.autoDependency();
	    /*
	     * Collection<Job> parentJobs = pjob.getJobObject().getParents(); if
	     * (!parentJobs.isEmpty()) { for (Job job : parentJobs) {
	     * pjob.addParent(this.jobs.get(job.getId())); }
	     * 
	     * }
	     */
	}
	// set provisionfiles job
	for (PegasusJob job : provisionFiles) {
	    if (job.hasProvisionFilesDependent()) {
		// create provisionfiles job
		Job1 jobO = this.getWorkflow().createSeqwareModuleJob(
			job.getAlgorithm(), Module.Seqware_ProvisionFiles);
		PegasusSeqwareModuleJob pjob = new PegasusSeqwareModuleJob(jobO);
		this.addJob(pjob);
		job.addParent(pjob);
	    }
	}
    }

    private void setParentAccessionIds() {
	String parentAccessions = this.getWorkflowProperty("parent_accessions");
	if (null == parentAccessions)
	    return;
	if (null == parentAccessionCheck(parentAccessions)) {
	    return;
	}
	String[] pas = parentAccessions.split(",");
	int i = -1;
	for (PegasusJob job : this.jobs.values()) {
	    // find the first non provisionfiles job, then set the
	    // parentaccessionid
	    if (job.getParents().isEmpty()) {
		i++;
		// set the first non provisionfiles job
		/*
		 * for (PegasusJob child : this
		 * .getFirstLevelNonProvisionFilesJobs(job)) { // child.
		 * child.setParentAccessionId(pas[i]); }
		 */
	    }

	}
    }

    private Collection<PegasusJob> getFirstLevelNonProvisionFilesJobs(
	    PegasusJob parent) {
	Collection<PegasusJob> res = new ArrayList<PegasusJob>();
	if (!parent.isProvisionFilesJob())
	    res.add(parent);
	else {
	    for (PegasusJob child : parent.getChildren()) {
		res.addAll(this.getFirstLevelNonProvisionFilesJobs(child));
	    }
	}
	return res;
    }

    /**
     * return an array of parentAccessionId with the same order of input bam
     * files
     * 
     * @param parentAccessions
     * @return
     */
    private String[] parentAccessionCheck(String parentAccessions) {
	// check with input_files, the order should be the same, or only one
	// number
	String input_files = this.getWorkflowProperty("input_files");
	if (null == input_files)
	    return null;
	String[] _files = input_files.split(",");
	String[] res = new String[_files.length];
	String[] _pid = parentAccessions.split(",");
	// FIXME, will check with database, no parentAccession
	if (_pid.length == 1 && parentAccessions.equals("0")) {
	    for (int i = 0; i < res.length; i++) {
		res[i] = "0";
	    }
	    return res;
	}
	if (_files.length != _pid.length) {
	    return null;
	}
	return _pid;
    }

    private void autoDependency() {
	Map<String, List<PegasusJob>> orderedMap = new LinkedHashMap<String, List<PegasusJob>>();
	for (Map.Entry<String, PegasusJob> entry : this.jobs.entrySet()) {
	    List<PegasusJob> list = orderedMap.get(entry.getValue()
		    .getAlgorithm());
	    if (null == list) {
		list = new ArrayList<PegasusJob>();
		orderedMap.put(entry.getValue().getAlgorithm(), list);
	    }
	    list.add(entry.getValue());
	}
	// set parents
	if (orderedMap.isEmpty())
	    return;
	Iterator<Entry<String, List<PegasusJob>>> it = orderedMap.entrySet()
		.iterator();
	Entry<String, List<PegasusJob>> parent = it.next();
	while (it.hasNext()) {
	    Entry<String, List<PegasusJob>> child = it.next();
	    for (PegasusJob c : child.getValue()) {
		for (PegasusJob p : parent.getValue()) {
		    c.addParent(p);
		}
	    }
	    parent = child;
	}
    }
}
