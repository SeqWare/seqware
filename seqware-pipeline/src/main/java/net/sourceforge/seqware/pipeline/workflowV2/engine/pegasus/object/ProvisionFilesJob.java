package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object;

import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;


public class ProvisionFilesJob extends PegasusJob {

	public ProvisionFilesJob(AbstractJob job, String basedir) {
		super(job, basedir);
	}

	@Override
	protected String buildCommandString() {
		StringBuilder sb = new StringBuilder();
		//add memory, classpath, module for bash

		sb.append("-Xmx").append(this.jobObj.getCommand().getMaxMemory()).append("\n");
		sb.append("-classpath ").append(basedir).append("/lib/").append(Adag.PIPELINE).append("\n");
		sb.append("net.sourceforge.seqware.pipeline.runner.Runner").append("\n");
		if(this.hasMetadataWriteback()) {
			sb.append("--metadata").append("\n");
		} else {
			sb.append("--no-metadata").append("\n");
		}

		sb.append("--module module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles").append("\n");
		sb.append("--").append("\n");
		
		if(this.jobObj.getCommand().toString().isEmpty() == false) {
			sb.append("--gcr-command").append("\n");
			sb.append(this.jobObj.getCommand().toString()).append("\n");	
		}
		// set input, output
		SqwFile file = this.jobObj.getFiles().iterator().next();
		String inputType = "--input-file ";
		if(file.getType().indexOf("::")>=0) {
			inputType = "--input-file-metadata "+ file.getType() + "/";
		}
		sb.append(inputType).append(this.jobObj.getFiles().iterator().next().getLocation()).append("\n");
		sb.append("--output-dir provisionfiles").append("\n");
		
		return sb.toString();
	}

}
