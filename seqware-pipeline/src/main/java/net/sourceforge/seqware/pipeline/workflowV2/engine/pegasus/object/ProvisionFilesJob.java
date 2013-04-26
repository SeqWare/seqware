package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object;

import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;


public class ProvisionFilesJob extends PegasusJob {

	private String metadataOutputPrefix;
	private String outputDir;
	private SqwFile file;
	
	public ProvisionFilesJob(AbstractJob job, String basedir, SqwFile file, String sqwVersion) {
		super(job, basedir, sqwVersion);
		this.file = file;
	}

	@Override
	protected String buildCommandString() {
		StringBuilder sb = new StringBuilder();
		//add memory, classpath, module for bash

		sb.append("-Xmx").append(this.jobObj.getCommand().getMaxMemory()).append("\n");
		sb.append("-classpath ").append(basedir).append("/lib/").append(this.getPipelinePath()).append("\n");
		sb.append("net.sourceforge.seqware.pipeline.runner.Runner").append("\n");
		sb.append(this.buildMetadataString());
                // ok, if this is being provisioned by using output_prefix and output_dir along with the name of the
                // file then we need to tell the metadb about the output_prefix and output_dir!
                // not applicable if the full path was provided
                if (this.file.getOutputPath() == null) {
                    sb.append("--metadata-output-file-prefix "+this.metadataOutputPrefix + "/" + this.outputDir + "\n");
                }
		sb.append("--module net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles").append("\n");
		sb.append("--").append("\n");
		
		if(this.jobObj.getCommand().toString().isEmpty() == false) {
			sb.append("--gcr-command").append("\n");
			sb.append(this.jobObj.getCommand().toString()).append("\n");	
		}
		// set input, output
		String inputType = "--input-file ";
		String output = "--output-dir " + this.outputDir;
		if(this.file.isOutput()) {
			inputType = "--input-file-metadata "+ this.jobObj.getAlgo() + "::" + this.file.getType() + "::";
			output = "--output-dir " + this.metadataOutputPrefix + "/" + this.outputDir;
                        // however, if the output was manually specified use it instead of the generic output_prefix & output_dir
                        if (this.file.getOutputPath() != null) {
                          output = "--output-file " + this.file.getOutputPath();
                        }
		}else{
                    //SEQWARE-1608
                    sb.append("--skip-record-file\n");
                } 
                
		sb.append(inputType).append(this.file.getSourcePath()).append("\n");
		sb.append(output).append("\n");
                
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
