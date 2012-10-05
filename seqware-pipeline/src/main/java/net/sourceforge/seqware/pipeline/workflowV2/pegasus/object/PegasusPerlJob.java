package net.sourceforge.seqware.pipeline.workflowV2.pegasus.object;

import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;

public class PegasusPerlJob extends PegasusJobObject {

	public PegasusPerlJob(AbstractJob job, String basedir) {
		super(job, basedir);
	}

	@Override
	protected String buildCommandString() {
		StringBuilder sb = new StringBuilder();
		if(this.jobObj.getMainClass().isEmpty() == false) {
			sb.append(this.jobObj.getMainClass()).append("\n");
		}
		return sb.toString();
	}
}
