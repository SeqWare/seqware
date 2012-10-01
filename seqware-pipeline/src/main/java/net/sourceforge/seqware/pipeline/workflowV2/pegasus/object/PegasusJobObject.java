package net.sourceforge.seqware.pipeline.workflowV2.pegasus.object;

import org.jdom.Element;

import net.sourceforge.seqware.pipeline.workflowV2.model.Job;
import net.sourceforge.seqware.pipeline.workflowV2.model.Module;
import net.sourceforge.seqware.pipeline.workflowV2.model.Requirement;

public class PegasusJobObject {
	private Job jobObj;
	private int id;
	private static String NS = "seqware";
	
	public PegasusJobObject(Job job) {
		this.jobObj = job;
	}
	
	public Element serializeXML() {
		Element element = new Element("job", AdagObject.NAMESPACE);
		element.setAttribute("id", this.jobObj.getAlgo()+"_"+this.id);
		element.setAttribute("name", this.jobObj.getModule().getName());
		element.setAttribute("namespace", NS);
		element.setAttribute("version", this.jobObj.getModule().getVersion());
		
		element.addContent(this.getArgumentElement());
		for(Requirement r: this.jobObj.getRequirements()) {
			Element profileE = new Element("profile", AdagObject.NAMESPACE);
			profileE.setAttribute("namespace", r.getNamespace());
			profileE.setAttribute("key", r.getType().toString().toLowerCase());
			profileE.setText(r.getValue());
			element.addContent(profileE);
		}
		return element;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	private Element getArgumentElement() {
		Element argumentE = new Element("argument", AdagObject.NAMESPACE);
		//set command argument
		StringBuilder sb = new StringBuilder();
		sb.append(this.buildCommandString());
		sb.append("\n");
		//set non command argument
		argumentE.setText(sb.toString());
		return argumentE;
	}
	
	private String buildCommandString() {
		StringBuilder sb = new StringBuilder();
		//add memory, classpath, module for bash
		//FIXME
		if(this.jobObj.getModule() == Module.Bash) {
			sb.append("-Xmx500M").append("\n");
			sb.append("-classpath /lib").append("\n");
			sb.append("net.sourceforge.seqware.pipeline.runner.Runner").append("\n");
			sb.append("--no-metadata").append("\n");
			sb.append("--module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner").append("\n");
			sb.append("--").append("\n");
		}
		sb.append(this.jobObj.getCommand().toString());	
		return sb.toString();
	}
}
