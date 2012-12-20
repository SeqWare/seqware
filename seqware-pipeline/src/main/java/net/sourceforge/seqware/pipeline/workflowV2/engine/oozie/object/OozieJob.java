package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Element;

public class OozieJob {
	protected String okTo = "end";
	//private String errorTo; always to fail now
	protected String name;
	protected Collection<String> parentAccessions;
	protected String wfrAccession;
	protected boolean wfrAncesstor;
	protected AbstractJob jobObj;
	protected boolean metadataWriteback;
	protected List<OozieJob> parents;
	protected List<OozieJob> children;
	protected String oozie_working_dir;
	protected List<String> parentAccessionFiles;
	
	
	public OozieJob(AbstractJob job, String name, String oozie_working_dir) {
		this.name = name;
		this.jobObj = job;
		this.oozie_working_dir = oozie_working_dir;
		this.parents = new ArrayList<OozieJob>();
		this.children = new ArrayList<OozieJob>();
		this.parentAccessionFiles = new ArrayList<String>();
		this.parentAccessions = new ArrayList<String>();
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
	
	public String getName() {
		return this.name;
	}

        public String getOozieWorkingDir() {
          return oozie_working_dir;
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
		javaE.addContent(config);
		
		Element mainClass = new Element("main-class", WorkflowApp.NAMESPACE);
		mainClass.setText("net.sourceforge.seqware.pipeline.runner.Runner");
		javaE.addContent(mainClass);
		
		this.buildMetadataString(javaE);
		
		Element arg1 = new Element("arg", WorkflowApp.NAMESPACE);
		arg1.setText("--module");
		javaE.addContent(arg1);
		
		Element argModule = new Element("arg", WorkflowApp.NAMESPACE);
		argModule.setText("net.sourceforge.seqware.pipeline.modules.GenericCommandRunner");
		javaE.addContent(argModule);
		
		Element dash = new Element("arg", WorkflowApp.NAMESPACE);
		dash.setText("--");
		javaE.addContent(dash);
		
		Element algo = new Element("arg", WorkflowApp.NAMESPACE);
		algo.setText("--gcr-algorithm");
		javaE.addContent(algo);
		
		Element algoValue = new Element("arg", WorkflowApp.NAMESPACE);
		algoValue.setText(this.jobObj.getAlgo());
		javaE.addContent(algoValue);
		
		Element command = new Element("arg", WorkflowApp.NAMESPACE);
		command.setText("--gcr-command");
		javaE.addContent(command);
		
		//add cd command for every gcr job
		Element cdE = new Element("arg", WorkflowApp.NAMESPACE);
		cdE.setText("cd " + this.oozie_working_dir + "; ");
		javaE.addContent(cdE);
		
		for(String arg: this.jobObj.getCommand().getArguments()) {
			Element cmdArg = new Element("arg", WorkflowApp.NAMESPACE);
			cmdArg.setText(arg);
			javaE.addContent(cmdArg);
		}
			
		return javaE;
	}
	
	public void setParentAccessions(Collection<String> parentAccessions) {
		this.parentAccessions.addAll(parentAccessions);
	}

	public boolean hasMetadataWriteback() {
		return metadataWriteback;
	}

	public void setMetadataWriteback(boolean metadataWriteback) {
		this.metadataWriteback = metadataWriteback;
	}
	
	public String getWorkflowRunAccession() {
		return wfrAccession;
	}

	public void setWorkflowRunAccession(String wfrAccession) {
		this.wfrAccession = wfrAccession;
	}

	public boolean isWorkflowRunAncesstor() {
		return wfrAncesstor;
	}

	public void setWorkflowRunAncesstor(boolean wfrAncesstor) {
		this.wfrAncesstor = wfrAncesstor;
	}

	public void addParent(OozieJob parent) {
		if(!this.parents.contains(parent))
			this.parents.add(parent);
		if(!parent.getChildren().contains(this))
			parent.getChildren().add(this);
	}
	
	public Collection<OozieJob> getParents() {
		return this.parents;
	}
	
	public Collection<OozieJob> getChildren() {
		return this.children;
	}
	
	public void setOkTo(String okTo) {
		this.okTo = okTo;
	}
	
	public String getOkTo() {
		return this.okTo;
	}
	
	public boolean hasFork() {
		return this.getChildren().size()>1;
	}
	
	public boolean hasJoin() {
		return this.getParents().size()>1;
	}
	
	public AbstractJob getJobObject() {
		return this.jobObj;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || obj instanceof OozieJob == false)
			return false;
		if(obj == this)
			return true;
		OozieJob rhs = (OozieJob) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj)).append(name, rhs.name).isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17,37).append(name).toHashCode();
	}
	
	@Override
	public String toString() {
		return this.name;
	}
	
	protected void buildMetadataString(Element javaElement) {
		Element metaArg = new Element("arg", WorkflowApp.NAMESPACE);
		if(this.hasMetadataWriteback()) {
			metaArg.setText("--metadata");
		} else {
			metaArg.setText("--no-metadata");
		}
		javaElement.addContent(metaArg);
		
		
		if(this.parentAccessions!=null && !this.parentAccessions.isEmpty()){
			for(String pa: this.parentAccessions) {
				Element paArg = new Element("arg", WorkflowApp.NAMESPACE);
				paArg.setText("--metadata-parent-accession");
				javaElement.addContent(paArg);
				
				Element paVal = new Element("arg", WorkflowApp.NAMESPACE);
				paVal.setText(pa);
				javaElement.addContent(paVal);
			}
		}
		
		if(this.wfrAccession!=null) {
			Element wfraArg = new Element("arg", WorkflowApp.NAMESPACE);
			Element wfraVal = new Element("arg", WorkflowApp.NAMESPACE);
			if(!this.wfrAncesstor) {
				wfraArg.setText("--metadata-workflow-run-ancestor-accession");
			} else {
				wfraArg.setText("--metadata-workflow-run-accession");
			}
			wfraVal.setText(this.wfrAccession);
			javaElement.addContent(wfraArg);
			javaElement.addContent(wfraVal);
		}
		
		if(!this.parentAccessionFiles.isEmpty()) {
			for(String paf: this.parentAccessionFiles) {
				Element pafArg = new Element("arg", WorkflowApp.NAMESPACE);
				pafArg.setText("--metadata-parent-accession-file");
				Element pafVal = new Element("arg", WorkflowApp.NAMESPACE);
				pafVal.setText(paf);
				javaElement.addContent(pafArg);
				javaElement.addContent(pafVal);
			}
		}
		Element pafArg = new Element("arg", WorkflowApp.NAMESPACE);
		pafArg.setText("--metadata-processing-accession-file");
		
		Element pafVal = new Element("arg", WorkflowApp.NAMESPACE);
		pafVal.setText(this.getAccessionFile());
		javaElement.addContent(pafArg);
		javaElement.addContent(pafVal);
	}
	
	public void addParentAccessionFile(String paf) {
		if(!this.parentAccessionFiles.contains(paf))
			this.parentAccessionFiles.add(paf);
	}

	public String getAccessionFile() {
		return this.oozie_working_dir + "/" + this.getName() + "_accession";
	}
}
