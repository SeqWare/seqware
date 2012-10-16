package net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jdom.Element;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.BashJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.Command;
import net.sourceforge.seqware.pipeline.workflowV2.model.JavaJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.JavaSeqwareModuleJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.PerlJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.Requirement;

public class PegasusJob {
	protected AbstractJob jobObj;
	private int id;
	protected String basedir;
	private List<PegasusJob> parents;
	private List<PegasusJob> children;
	protected static String NS = "seqware";
	private boolean metadataWriteback;
	protected String parentAccession;
	protected String wfrAccession;
	protected boolean wfrAncesstor;
	
	public PegasusJob(AbstractJob job, String basedir) {
		this.jobObj = job;
		this.basedir = basedir;
		this.parents = new ArrayList<PegasusJob>();
		this.children = new ArrayList<PegasusJob>();
	}
	
	public Element serializeXML() {
		String name = "java";
		//FIXME should not hardcode here
		String version = "1.6.0";
/*		if(this.jobObj instanceof PerlJob) {
			name = "perl";
			//FIXME should put in property file
			version = "5.14.1";
		} */
		
		Element element = new Element("job", Adag.NAMESPACE);
		element.setAttribute("id", this.jobObj.getAlgo()+"_"+this.id);
		element.setAttribute("name", name);
		element.setAttribute("namespace", NS);
		element.setAttribute("version", version);
		
		element.addContent(this.getArgumentElement());
		for(Requirement r: this.jobObj.getRequirements()) {
			Element profileE = new Element("profile", Adag.NAMESPACE);
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
		Element argumentE = new Element("argument", Adag.NAMESPACE);
		//set command argument
		StringBuilder sb = new StringBuilder();
		sb.append(this.buildCommandString());
		sb.append("\n");
		argumentE.setText(sb.toString());
		return argumentE;
	}
	
	protected String buildCommandString() {
		StringBuilder sb = new StringBuilder();
		//add memory, classpath, module for bash
		
		sb.append("-Xmx").append(this.jobObj.getCommand().getMaxMemory()).append("\n");
		sb.append("-classpath ").append(basedir).append("/lib/").append(Adag.PIPELINE).append("\n");
		sb.append("net.sourceforge.seqware.pipeline.runner.Runner").append("\n");
		if(this.hasMetadataWriteback()) {
			sb.append("--metadata").append("\n");
		} else {
			sb.append("--no-metadata").append("\n");
		}
		if(this.hasMetadataWriteback()) {
			if(this.parentAccession!=null){
				sb.append("--metadata-parent-accession " + this.parentAccession).append("\n");
			}
			if(this.wfrAccession!=null) {
				if(!this.wfrAncesstor) {
					sb.append("--metadata-workflow-run-ancestor-accession " + this.wfrAccession).append("\n");
				} else {
					sb.append("--metadata-workflow-run-accession " + this.wfrAccession).append("\n");
				}
			}
		}
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
			sb.append(this.jobObj.getCommand().toString());	
		}
		return sb.toString();
	}
	
	public AbstractJob getJobObject() {
		return this.jobObj;
	}
	
	public Collection<PegasusJob> getParents() {
		return this.parents;
	}
	
	public Collection<PegasusJob> getChildren() {
		return this.children;
	}
	
	public void addParent(PegasusJob parent) {
		this.parents.add(parent);
		parent.getChildren().add(this);
	}
	
    public Element getDependentElement(PegasusJob parent) {
		Element element = new Element("child", Adag.NAMESPACE);
		element.setAttribute("ref", this.jobObj.getAlgo() + "_" + this.getId());
		Element parentE = new Element("parent", Adag.NAMESPACE);
		parentE.setAttribute("ref", parent.getJobObject().getAlgo() + "_" + parent.getId());
		element.addContent(parentE);
		return element;
    }

	public boolean hasMetadataWriteback() {
		return metadataWriteback;
	}

	public void setMetadataWriteback(boolean metadataWriteback) {
		this.metadataWriteback = metadataWriteback;
	}

	public String getParentAccession() {
		return parentAccession;
	}

	public void setParentAccession(String parentAccession) {
		this.parentAccession = parentAccession;
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
}
