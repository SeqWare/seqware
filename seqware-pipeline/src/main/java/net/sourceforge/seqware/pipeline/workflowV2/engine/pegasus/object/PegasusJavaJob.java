package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object;

import java.io.File;

import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;

public class PegasusJavaJob extends PegasusJob {

	public PegasusJavaJob(AbstractJob job, String basedir) {
		super(job, basedir);
	}

	@Override
	protected String buildCommandString() {
		StringBuilder sb = new StringBuilder();
		sb.append("-Xmx").append(this.jobObj.getCommand().getMaxMemory()).append("\n");
		sb.append("-classpath ").append(basedir).append("/lib/").append(Adag.PIPELINE).append(":");
		sb.append(basedir).append(File.separator).append("lib").append("\n");
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
