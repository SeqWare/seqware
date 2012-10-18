package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object;

import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.Command;

public class PegasusPerlJob extends PegasusJob {

	public PegasusPerlJob(AbstractJob job, String basedir) {
		super(job, basedir);
	}

	@Override
	protected String buildCommandString() {
		StringBuilder sb = new StringBuilder();
		//add memory, classpath, module for bash
		
		sb.append("-Xmx").append(this.jobObj.getCommand().getMaxMemory()).append("\n");
		sb.append("-classpath ").append(basedir).append("/lib/").append(Adag.PIPELINE).append("\n");
		sb.append("net.sourceforge.seqware.pipeline.runner.Runner").append("\n");
		sb.append(this.buildMetadataString());
		sb.append("--module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner").append("\n");
		sb.append("--").append("\n");
		sb.append("--gcr-algorithm ").append(this.jobObj.getAlgo()).append("\n");
		
		Command cmd = this.jobObj.getCommand();
		if(cmd.toString().isEmpty() == false) {
			//append these setting first
			//gcr-output-file
			//gcr-skip-if-output-exists
			//gcr-skip-if-missing
			if(cmd.getGcrOutputFile() != null && cmd.getGcrOutputFile().isEmpty() == false) {
				sb.append("--gcr-output-file " + cmd.getGcrOutputFile() + "\n");
			}
			if(cmd.isGcrSkipIfMissing()) {
				sb.append("--gcr-skip-if-missing true");
			}
			if(cmd.isGcrSkipIfOutputExists()) {
				sb.append("--gcr-skip-if-output-exists true");
			}
			sb.append("--gcr-command").append("\n");
			sb.append("perl ");
		}
		
		if(this.jobObj.getMainClass().isEmpty() == false) {
			sb.append(this.jobObj.getMainClass()).append("\n");
		}
		//get argument
		for(String str: this.jobObj.getCommand().getArguments()) {
			sb.append(str).append("\n");
		}
		return sb.toString();
	}
}
