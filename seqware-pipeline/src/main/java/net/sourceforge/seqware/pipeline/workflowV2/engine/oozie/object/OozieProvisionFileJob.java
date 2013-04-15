package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import org.jdom.Element;

import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;

public class OozieProvisionFileJob extends OozieJob {

	private String metadataOutputPrefix;
	private String outputDir;
	private SqwFile file;
	
	public OozieProvisionFileJob(AbstractJob job, SqwFile file, String name, String oozie_working_dir) {
		super(job, name, oozie_working_dir);
		this.file = file;
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
	
	public Element serializeXML() {
		Element element = new Element("action", WorkflowApp.NAMESPACE);
		element.setAttribute("name", this.name);
		Element javaE = this.getJavaElement();
		element.addContent(javaE);
		
		//okTo
		Element ok = new Element("ok", WorkflowApp.NAMESPACE);
		ok.setAttribute("to",this.okTo);
		element.addContent(ok);
		
		Element error = new Element("error", WorkflowApp.NAMESPACE);
		error.setAttribute("to","fail");
		element.addContent(error);

		return element;
	}

	protected Element getJavaElement() {
		Element javaE = new Element("java", WorkflowApp.NAMESPACE);
		Element jobTracker = new Element("job-tracker", WorkflowApp.NAMESPACE);
		jobTracker.setText("${jobTracker}");
		javaE.addContent(jobTracker);
		
		Element nameNode = new Element("name-node", WorkflowApp.NAMESPACE);
		nameNode.setText("${nameNode}");
		javaE.addContent(nameNode);
		
		Element config = new Element("configuration", WorkflowApp.NAMESPACE);
		Element p0 = new Element("property", WorkflowApp.NAMESPACE);
		config.addContent(p0);
		Element name0 = new Element("name",WorkflowApp.NAMESPACE);
		name0.setText("mapred.job.queue.name");
		p0.addContent(name0);
		Element value0 = new Element("value",WorkflowApp.NAMESPACE);
		value0.setText("${queueName}");
		p0.addContent(value0);
                
                // map memory
                p0 = new Element("property", WorkflowApp.NAMESPACE);
                config.addContent(p0);
                name0 = new Element("name", WorkflowApp.NAMESPACE);
                name0.setText("oozie.launcher.mapred.job.map.memory.mb");
                p0.addContent(name0);
                value0 = new Element("value", WorkflowApp.NAMESPACE);
                value0.setText(this.getJobObject().getMaxMemory());
                p0.addContent(value0);

                p0 = new Element("property", WorkflowApp.NAMESPACE);
                config.addContent(p0);
                name0 = new Element("name", WorkflowApp.NAMESPACE);
                name0.setText("oozie.launcher.mapred.job.reduce.memory.mb");
                p0.addContent(name0);
                value0 = new Element("value", WorkflowApp.NAMESPACE);
                value0.setText(this.getJobObject().getMaxMemory());
                p0.addContent(value0);
                
                p0 = new Element("property", WorkflowApp.NAMESPACE);
                config.addContent(p0);
                name0 = new Element("name", WorkflowApp.NAMESPACE);
                name0.setText("oozie.launcher.mapreduce.map.memory.physical.mb");
                p0.addContent(name0);
                value0 = new Element("value", WorkflowApp.NAMESPACE);
                value0.setText(this.getJobObject().getMaxMemory());
                p0.addContent(value0);

                p0 = new Element("property", WorkflowApp.NAMESPACE);
                config.addContent(p0);
                name0 = new Element("name", WorkflowApp.NAMESPACE);
                name0.setText("oozie.launcher.mapreduce.reduce.memory.physical.mb");
                p0.addContent(name0);
                value0 = new Element("value", WorkflowApp.NAMESPACE);
                value0.setText(this.getJobObject().getMaxMemory());
                p0.addContent(value0);
                
                // add configuration
                javaE.addContent(config);
		
		Element mainClass = new Element("main-class", WorkflowApp.NAMESPACE);
		mainClass.setText("net.sourceforge.seqware.pipeline.runner.Runner");
		javaE.addContent(mainClass);
		
		this.buildMetadataString(javaE);
		
		Element arg1 = new Element("arg", WorkflowApp.NAMESPACE);
		arg1.setText("--module");
		javaE.addContent(arg1);
		
		Element argModule = new Element("arg", WorkflowApp.NAMESPACE);
		argModule.setText("net.sourceforge.seqware.pipeline.modules.utilities.ProvisionFiles");
		javaE.addContent(argModule);
		
		Element dash = new Element("arg", WorkflowApp.NAMESPACE);
		dash.setText("--");
		javaE.addContent(dash);
		
		// set input, output
		String inputArg = "--input-file";
		String inputValue = this.file.getSourcePath();
		String output = this.outputDir;
		if(this.file.isOutput()) {
			inputArg = "--input-file-metadata";
			inputValue =  this.jobObj.getAlgo() + "::" + this.file.getType() + "::" + this.getOozieWorkingDir() + "/" + this.file.getSourcePath();
			output = this.metadataOutputPrefix + "/" + this.outputDir;
                        // if this is true then we have the full output path, not just prefix and output dir!
                        if (this.file.getOutputPath() != null) {
                          output = this.file.getOutputPath();
                        }
		}
		Element inputTE = new Element("arg", WorkflowApp.NAMESPACE);
		inputTE.setText(inputArg);
		javaE.addContent(inputTE);
		
		Element inputVE = new Element("arg", WorkflowApp.NAMESPACE);
		inputVE.setText(inputValue);
		javaE.addContent(inputVE);
		
		Element outputArg = new Element("arg", WorkflowApp.NAMESPACE);
		// now just check to see if it's a full path or just output dir
                if (this.file.getOutputPath() != null) { 
                  outputArg.setText("--output-file");
                } else {
                  outputArg.setText("--output-dir");
                }
		javaE.addContent(outputArg);
		
		Element outputValue = new Element("arg", WorkflowApp.NAMESPACE);
		outputValue.setText(output);
		javaE.addContent(outputValue);

		if(this.file.isForceCopy()) {
			Element forceE = new Element("arg", WorkflowApp.NAMESPACE);
			forceE.setText("--force-copy");
			javaE.addContent(forceE);
		}

			
		return javaE;
	}

}
