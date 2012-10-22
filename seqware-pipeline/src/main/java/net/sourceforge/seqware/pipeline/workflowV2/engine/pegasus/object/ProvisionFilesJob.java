package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object;

import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;


public class ProvisionFilesJob extends PegasusJob {

	private String metadataOutputPrefix;
	private String outputDir;
	private SqwFile file;
	
	public ProvisionFilesJob(AbstractJob job, String basedir, SqwFile file) {
		super(job, basedir);
		this.file = file;
	}

	@Override
	protected String buildCommandString() {
		StringBuilder sb = new StringBuilder();
		//add memory, classpath, module for bash

		sb.append("-Xmx").append(this.jobObj.getCommand().getMaxMemory()).append("\n");
		sb.append("-classpath ").append(basedir).append("/lib/").append(Adag.PIPELINE).append("\n");
		sb.append("net.sourceforge.seqware.pipeline.runner.Runner").append("\n");
		sb.append(this.buildMetadataString());

		sb.append("--module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles").append("\n");
		sb.append("--").append("\n");
		
		if(this.jobObj.getCommand().toString().isEmpty() == false) {
			sb.append("--gcr-command").append("\n");
			sb.append(this.jobObj.getCommand().toString()).append("\n");	
		}
		// set input, output
		String inputType = "--input-file ";
		String output = this.outputDir;
		if(this.file.isOutput()) {
			inputType = "--input-file-metadata "+ this.jobObj.getAlgo() + "::" + this.file.getType() + "::";
			output = this.metadataOutputPrefix + "/" + this.outputDir;
		}
		sb.append(inputType).append(this.file.getSourcePath()).append("\n");
		sb.append("--output-dir " + output).append("\n");
		if(this.file.isForceCopy()) {
			sb.append("--force-copy");
		}
		
		return sb.toString();
	}

	public String getMetadataOutputPrefix() {
		return metadataOutputPrefix;
	}

	public void setMetadataOutputPrefix(String metadataOutputPrefix) {
		this.metadataOutputPrefix = metadataOutputPrefix;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

}
