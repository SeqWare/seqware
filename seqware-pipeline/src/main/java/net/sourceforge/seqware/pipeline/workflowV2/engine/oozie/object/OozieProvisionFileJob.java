package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;

public class OozieProvisionFileJob extends OozieJob {

	private String metadataOutputPrefix;
	private String outputDir;
	private SqwFile file;
	
	public OozieProvisionFileJob(AbstractJob job, String name) {
		super(job, name);

	}

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
	
	public String getMetadataOutputPrefix() {
		return metadataOutputPrefix;
	}

	public void setMetadataOutputPrefix(String metadataOutputPrefix) {
		this.metadataOutputPrefix = metadataOutputPrefix;
	}
}
