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

import java.util.Map;

import net.sourceforge.seqware.pipeline.workflowV2.model.Job;
import net.sourceforge.seqware.pipeline.workflowV2.model.Module;
import net.sourceforge.seqware.pipeline.workflowV2.model.SeqwareModuleJob;
import net.sourceforge.seqware.pipeline.workflowV2.pegasus.ArgumentUtils;

import org.jdom.Element;

/**
 * 
 * @author yongliang
 */
public class PegasusSeqwareModuleJob extends PegasusJob {

    public PegasusSeqwareModuleJob(Job job) {
	super(job);
    }

    @Override
    public Element serializeXML() {
	Element element = new Element("job", NAMESPACE);
	element.setAttribute("id", this.jobObj.getId());
	element.setAttribute("name", this.jobObj.getName());
	element.setAttribute("namespace", NS);
	element.setAttribute("version", this.jobObj.getVersion());

	if (Module.Seqware_ProvisionFiles == this.getModule()) {
	    this.argument.removeModuleOption("--gcr-algorithm");
	    if (null != this.getInputFile()) {
		this.argument.addModuleOption(this.jobObj.getInputFileKey(),
			this.getInputFile());
	    }
	    if (null != this.getOutputDir()) {
		this.argument.addModuleOption("--output-dir",
			this.getOutputDir());
	    }
	} else {
	    if (!this.argument.hasOption("--no-metadata")) {
		if (null != this.parentAccessionId) {
		    this.argument.addSysOption("--metadata-parent-accession",
			    this.parentAccessionId);
		    this.argument.addSysOption(
			    "--metadata-workflow-run-accession",
			    this.getWorkflowProperty("workflow-run-accession"));
		} else {
		    for (PegasusJob parent : this.getParents()) {
			if (!parent.isProvisionFilesJob()) {
			    this.argument.addSysOption(
				    "--metadata-parent-accession-file",
				    "accession/" + parent.getAlgorithm() + "_"
					    + parent.getId() + "_accession");
			}
		    }
		    this.argument.addSysOption(
			    "--metadata-workflow-run-ancestor-accession",
			    this.getWorkflowProperty("workflow-run-accession"));
		}

		this.argument.addSysOption(
			"--metadata-processing-accession-file", "accession/"
				+ this.getAlgorithm() + "_" + this.getId()
				+ "_accession");
		this.argument.addSysOption("--metadata-output-file-prefix",
			"./");
	    }
	    this.resetGCRCommand();
	}

	Element arg = this.argument.serializeXML();
	element.addContent(arg);
	this.addModuleArgumentString(arg);

	for (Element profile : this.generateDefaultProfileElements()) {
	    element.addContent(profile);
	}

	return element;
    }

    private void resetGCRCommand() {
	if (this.jobObj.getCommand() == null
		|| this.jobObj.getCommand().isEmpty()) {
	    return;
	}
	StringBuilder cmd = new StringBuilder(this.jobObj.getCommand());
	for (String input : this.inputs) {
	    cmd.append(" " + input);
	}
	this.argument.addModuleOption("--gcr-command", cmd.toString());
    }

    @Override
    protected void setJobContext() {
	super.setJobContext();
	this.setSeqwareContext();
	Module module = this.getModule();
	if (null == module)
	    module = Module.Seqware_GenericCommandRunner;
	this.argument.addSysOption("--module", module.getName());
	if (module == Module.Seqware_GenericCommandRunner) {
	    this.argument.addModuleOption("--gcr-algorithm",
		    this.jobObj.getAlgorithm());
	}
	// has extra classpath
	if (this.jobObj.getClassPath() != null) {
	    this.argument
		    .updateOption("-classpath", this.jobObj.getClassPath());
	}
    }

    private void setSeqwareContext() {
	for (Map.Entry<String, String> entry : ArgumentUtils
		.getDefaultJavaArguments(this.getWorkflow().getProperties())
		.entrySet()) {
	    this.argument.addSysOption(entry.getKey(), entry.getValue());
	}
	if (this.jobObj.isMetadataOverridden()
		&& !this.getWorkflow().getProperties()
			.containsKey("no-metadata")) {
	    this.argument.addSysOption("--metadata", null);
	} else
	    this.argument.addSysOption("--no-metadata", null);
    }

    @Override
    public boolean hasProvisionFilesDependent() {
	Module module = this.getModule();

	return this.jobObj.getInputFile() != null
		&& Module.Seqware_ProvisionFiles != module;
    }

    public Module getModule() {
	return ((SeqwareModuleJob) this.jobObj).getModule();
    }

    public String getInputFile() {
	return this.jobObj.getInputFile();
    }

    public String getOutputDir() {
	return this.jobObj.getOutputDir();
    }

    @Override
    public boolean isProvisionFilesJob() {
	return Module.Seqware_ProvisionFiles == this.getModule();
    }
}
