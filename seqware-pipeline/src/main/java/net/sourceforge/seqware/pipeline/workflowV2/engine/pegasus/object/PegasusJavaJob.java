package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object;

import java.io.File;

import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;

public class PegasusJavaJob extends PegasusJob {

	public PegasusJavaJob(AbstractJob job, String basedir, String sqw_version) {
		super(job, basedir, sqw_version);
	}

	@Override
	protected String buildCommandString() {
		StringBuilder sb = new StringBuilder();
		sb.append("-Xmx").append(this.jobObj.getCommand().getMaxMemory()).append("\n");
		sb.append("-classpath ").append(basedir).append("/lib/").append(this.getPipelinePath()).append(":");
		sb.append(basedir).append(File.separator).append("lib").append("\n");
		sb.append("net.sourceforge.seqware.pipeline.runner.Runner").append("\n");
		sb.append(this.buildMetadataString());
		sb.append("--module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner").append("\n");
		sb.append("--").append("\n");
		sb.append("--gcr-algorithm ").append(this.jobObj.getAlgo()).append("\n");
		sb.append("--gcr-command").append("\n");
		
		sb.append("java -classpath ").append(basedir).append(File.separator).append("lib").append("\n");
		if(this.jobObj.getClassPath()!=null && this.jobObj.getClassPath().isEmpty() == false) {
			sb.append(":"+basedir+File.separator+this.jobObj.getClassPath());
		}
		sb.append("\n");
		if(this.jobObj.getMainClass().isEmpty() == false) {
			sb.append(this.jobObj.getMainClass()).append("\n");
		}
		sb.append(this.jobObj.getCommand().toString());	
		return sb.toString();
	}
}
