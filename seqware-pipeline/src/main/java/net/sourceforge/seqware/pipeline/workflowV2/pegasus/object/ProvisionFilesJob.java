package net.sourceforge.seqware.pipeline.workflowV2.pegasus.object;

import net.sourceforge.seqware.pipeline.workflowV2.model.Job;


public class ProvisionFilesJob extends PegasusJobObject {

	public ProvisionFilesJob(Job job, String basedir) {
		super(job, basedir);
	}

	@Override
	protected String buildCommandString() {
		StringBuilder sb = new StringBuilder();
		//add memory, classpath, module for bash

		sb.append("-Xmx").append(this.jobObj.getCommand().getMaxMemory()).append("\n");
		sb.append("-classpath ").append(basedir).append("/lib").append("\n");
		sb.append("net.sourceforge.seqware.pipeline.runner.Runner").append("\n");
		sb.append("--no-metadata").append("\n");
		sb.append("--module module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles").append("\n");
		sb.append("--").append("\n");
		
		if(this.jobObj.getCommand().toString().isEmpty() == false) {
			sb.append("--gcr-command").append("\n");
			sb.append(this.jobObj.getCommand().toString()).append("\n");	
		}
		// set input, output
		sb.append("--input-file ").append(this.jobObj.getFiles().iterator().next().getLocation()).append("\n");
		sb.append("--output-dir data").append("\n");
		
		return sb.toString();
	}

}
