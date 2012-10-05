package net.sourceforge.seqware.pipeline.workflowV2.pegasus.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jdom.Element;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.BashJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.Command;
import net.sourceforge.seqware.pipeline.workflowV2.model.Module;
import net.sourceforge.seqware.pipeline.workflowV2.model.PerlJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.Requirement;

public class PegasusJobObject {
	protected AbstractJob jobObj;
	private int id;
	protected String basedir;
	private List<PegasusJobObject> parents;
	protected static String NS = "seqware";
	
	public PegasusJobObject(AbstractJob job, String basedir) {
		this.jobObj = job;
		this.basedir = basedir;
		this.parents = new ArrayList<PegasusJobObject>();
	}
	
	public Element serializeXML() {
		String name = "bash";
		//FIXME should not hardcode here
		String version = "1.6.0";
		if(this.jobObj instanceof PerlJob) {
			name = "perl";
			//FIXME should put in property file
			version = "5.14.1";
		}
		Element element = new Element("job", AdagObject.NAMESPACE);
		element.setAttribute("id", this.jobObj.getAlgo()+"_"+this.id);
		element.setAttribute("name", name);
		element.setAttribute("namespace", NS);
		element.setAttribute("version", version);
		
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
		for(String arg: this.jobObj.getArguments()) {
			sb.append(arg).append("\n");
		}
		argumentE.setText(sb.toString());
		return argumentE;
	}
	
	protected String buildCommandString() {
		StringBuilder sb = new StringBuilder();
		//add memory, classpath, module for bash
		if(this.jobObj instanceof BashJob) {
			sb.append("-Xmx").append(this.jobObj.getCommand().getMaxMemory()).append("\n");
			sb.append("-classpath ").append(basedir).append("/lib/").append(AdagObject.PIPELINE).append("\n");
			sb.append("net.sourceforge.seqware.pipeline.runner.Runner").append("\n");
			sb.append("--no-metadata").append("\n");
			sb.append("--module net.sourceforge.seqware.pipeline.modules.GenericCommandRunner").append("\n");
			sb.append("--").append("\n");
			sb.append("--gcr-algorithm ").append(this.jobObj.getAlgo()).append("\n");
		}
		Command cmd = this.jobObj.getCommand();
		if(cmd.toString().isEmpty() == false) {
			//append these setting first
			//gcr-output-file
			//gcr-skip-if-output-exists
			//gcr-skip-if-missing
			if(cmd.getGcrOutputFile().isEmpty() == false) {
				sb.append("--gcr-output-file " + cmd.getGcrOutputFile() + "\n");
			}
			if(cmd.isGcrSkipIfMissing()) {
				sb.append("--gcr-skip-if-missing true");
			}
			if(cmd.isGcrSkipIfOutputExists()) {
				sb.append("--gcr-skip-if-output-exists true");
			}
			sb.append("--gcr-command").append("\n");
			sb.append(this.jobObj.getCommand().toString());	
		}
		return sb.toString();
	}
	
	public AbstractJob getJobObject() {
		return this.jobObj;
	}
	
	public Collection<PegasusJobObject> getParents() {
		return this.parents;
	}
	
    public Element getDependentElement(PegasusJobObject parent) {
		Element element = new Element("child", AdagObject.NAMESPACE);
		element.setAttribute("ref", this.jobObj.getAlgo() + "_" + this.getId());
		Element parentE = new Element("parent", AdagObject.NAMESPACE);
		parentE.setAttribute("ref", parent.getJobObject().getAlgo() + "_" + parent.getId());
		element.addContent(parentE);
		return element;
    }
}
