package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object;

import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;

public class PegasusPerlJob extends PegasusJob {

	public PegasusPerlJob(AbstractJob job, String basedir) {
		super(job, basedir);
	}

	@Override
	protected String buildCommandString() {
		StringBuilder sb = new StringBuilder();
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
