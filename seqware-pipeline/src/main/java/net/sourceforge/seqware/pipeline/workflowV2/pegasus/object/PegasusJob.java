package net.sourceforge.seqware.pipeline.workflowV2.pegasus.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.seqware.pipeline.workflowV2.model.Argument;
import net.sourceforge.seqware.pipeline.workflowV2.model.Job;
import net.sourceforge.seqware.pipeline.workflowV2.model.JobProfile;
import net.sourceforge.seqware.pipeline.workflowV2.pegasus.StringUtils;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.jdom.Element;

public class PegasusJob extends PegasusAbstract {

    protected static String NS = "seqware";
    protected Job jobObj;
    protected Arguments argument;
    private Collection<PegasusJob> parents;
    private Collection<PegasusJob> childrens;
    protected String parentAccessionId;
    protected Collection<String> inputs;

    public PegasusJob(Job job) {
	this.jobObj = job;
	this.parents = new ArrayList<PegasusJob>();
	this.childrens = new ArrayList<PegasusJob>();
	this.argument = new Arguments();
	this.inputs = new ArrayList<String>();
	initJob();
    }

    private void initJob() {
	this.setJobContext();
    }

    @Override
    public Element serializeXML() {
	Element element = new Element("job", NAMESPACE);
	element.setAttribute("id", this.jobObj.getId());
	element.setAttribute("name", this.jobObj.getName());
	element.setAttribute("namespace", NS);
	element.setAttribute("version", this.jobObj.getVersion());

	if (this.argument.hasOption("--metadata")) {
	    if (null != this.parentAccessionId) {
		this.argument.addSysOption("--metadata-parent-accession",
			this.parentAccessionId);
		this.argument.addSysOption("--metadata-workflow-run-accession",
			this.getWorkflowProperty("workflow-run-accession"));
	    }

	    if (!this.isProvisionFilesJob()) {
		this.argument.addSysOption(
			"--metadata-processing-accession-file",
			"data/" + this.getAlgorithm() + "_" + this.getId()
				+ "_accession");
		this.argument.addSysOption("--metadata-output-file-prefix",
			"./");
		if (null == this.parentAccessionId) {
		    for (PegasusJob parent : this.getParents()) {
			if (!parent.isProvisionFilesJob()) {
			    this.argument.addSysOption(
				    "--metadata-parent-accession-file", "data/"
					    + parent.getAlgorithm() + "_"
					    + parent.getId() + "_accession");
			}
		    }
		    this.argument.addSysOption(
			    "--metadata-workflow-run-ancestor-accession",
			    this.getWorkflowProperty("workflow-run-accession"));
		}

	    }
	}
	element.addContent(this.argument.serializeXML());

	for (Element profile : this.generateDefaultProfileElements()) {
	    element.addContent(profile);
	}

	return element;
    }

    protected Collection<Element> generateDefaultProfileElements() {
	List<Element> res = new ArrayList<Element>();
	Element e1 = new Element("profile", NAMESPACE);
	e1.setAttribute("namespace", "globus");
	e1.setAttribute("key", "jobtype");
	e1.setText("condor");
	res.add(e1);

	Element e2 = new Element("profile", NAMESPACE);
	e2.setAttribute("namespace", "globus");
	e2.setAttribute("key", "count");
	e2.setText("" + this.jobObj.getThreadCount());
	res.add(e2);

	Element e3 = new Element("profile", NAMESPACE);
	e3.setAttribute("namespace", "globus");
	e3.setAttribute("key", "maxmemory");
	e3.setText("" + this.jobObj.getMaxMemory());
	res.add(e3);

	if (this.jobObj.getQueue() != null) {
	    Element e4 = new Element("profile", NAMESPACE);
	    e4.setAttribute("namespace", "globus");
	    e4.setAttribute("key", "queue");
	    e4.setText(this.jobObj.getQueue());
	    res.add(e4);
	}

	if (this.jobObj.getPreJobDirectory() != null) {
	    Element e5 = new Element("profile", NAMESPACE);
	    e5.setAttribute("namespace", "env");
	    e5.setAttribute("key", "GRIDSTART_PREJOB");
	    e5.setText(this.jobObj.getPreJobDirectory());
	    res.add(e5);
	}
	for (JobProfile pf : this.jobObj.getProfiles()) {
	    Element e = new Element("profile", NAMESPACE);
	    e.setAttribute("namespace", pf.getNamespace());
	    e.setAttribute("key", pf.getKey());
	    e.setText(pf.getValue());
	    res.add(e);
	}
	return res;
    }

    private void setPerlContext(Arguments argument) {

    }

    private void setModuleArgument(Arguments argument) {
	Collection<Argument> args = this.jobObj.getJobModule().getArguments();
	if (null != args) {
	    for (Argument arg : args) {
		String value = arg.getValue();
		if (null != value && StringUtils.hasVariable(value)) {
		    value = StringUtils.replace(value, this.getWorkflow()
			    .getProperties());
		}
		argument.addModuleOption(arg.getKey(), value);
	    }
	}
    }

    public Element getDependentElement(PegasusJob parent) {
	Element element = new Element("child", NAMESPACE);
	element.setAttribute("ref", this.jobObj.getId());
	Element parentE = new Element("parent", NAMESPACE);
	parentE.setAttribute("ref", parent.getId());
	element.addContent(parentE);
	return element;

    }

    private void addJobArguments() {
	Collection<Argument> args = this.jobObj.getJobArgument();
	for (Argument arg : args) {
	    String value = arg.getValue();
	    if (null != value && StringUtils.hasVariable(value)) {
		value = StringUtils.replace(value, this.getWorkflow()
			.getProperties());
	    }
	    argument.addSysOption(arg.getKey(), value);
	}
    }

    public String getParentAccessionId() {
	if (this.getParents().isEmpty()) {
	    // return this.getParent().get
	}
	// check the input_files, and the parent_accession_id should be the same
	// order as the input_files
	String input_files = this.getWorkflowProperty("input_bam_files");
	if (null == input_files)
	    return null;
	return "";
    }

    public String getId() {
	return this.jobObj.getId();
    }

    public Job getJobObject() {
	return this.jobObj;
    }

    public void addParent(PegasusJob parent) {
	if (!this.parents.contains(parent)) {
	    this.parents.add(parent);
	    parent.addChild(this);
	}
    }

    public Collection<PegasusJob> getParents() {
	return this.parents;
    }

    public boolean hasProcessingFile() {
	return this.jobObj.hasProcessingFile();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj instanceof PegasusJob == false)
	    return false;
	if (obj == this)
	    return true;
	PegasusJob rhs = (PegasusJob) obj;
	return new EqualsBuilder().append(this.getId(), rhs.getId()).isEquals();
    }

    @Override
    public int hashCode() {
	return new HashCodeBuilder(17, 37).append(this.getId()).toHashCode();
    }

    private void addChild(PegasusJob child) {
	this.childrens.add(child);
    }

    public Collection<PegasusJob> getChildren() {
	return this.childrens;
    }

    public void setParentAccessionId(String id) {
	this.parentAccessionId = id;
    }

    protected void setJobContext() {
	for (Argument arg : this.jobObj.getModuleArguments()) {
	    this.argument.addModuleOption(arg);
	}
    }

    public boolean hasProvisionFilesDependent() {
	return false;
    }

    public String getAlgorithm() {
	return this.jobObj.getAlgorithm();
    }

    public boolean isProvisionFilesJob() {
	return false;
    }

    public boolean checkCommandIO() {
	return this.jobObj.checkCommandIO();
    }

    public boolean hasCommandOutput() {
	String[] commands = this.jobObj.getCommand().split("\\s+");
	for (String c : commands) {
	    if (c.startsWith("OUTPUT"))
		return true;
	    if (c.equals(">") || c.equals("&gt;"))
		return true;
	}
	return false;
    }

    public String getCommandOutput() {
	String[] commands = this.jobObj.getCommand().split("\\s+");
	for (int i = 0; i < commands.length; i++) {
	    String c = commands[i];
	    if (c.startsWith("OUTPUT")) {
		int index = c.indexOf("=");
		return c.substring(index + 1);
	    }
	    if (c.equals(">") || c.equals("&gt;"))
		return commands[i + 1];

	}
	return null;
    }

    public void addCommandInput(String input) {
	this.inputs.add("INPUT=" + input);
    }

    @Override
    public String toString() {
	return this.jobObj.getAlgorithm() + "--" + this.jobObj.getId();
    }
}
