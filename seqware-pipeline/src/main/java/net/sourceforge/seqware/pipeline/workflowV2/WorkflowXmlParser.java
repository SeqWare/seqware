package net.sourceforge.seqware.pipeline.workflowV2;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.sourceforge.seqware.pipeline.workflowV2.model.Job;
import net.sourceforge.seqware.pipeline.workflowV2.model.Module;
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class WorkflowXmlParser {
    public void parseXml(Workflow wf, File file) {
	SAXBuilder builder = new SAXBuilder();
	try {
	    Document document = (Document) builder.build(file);
	    Element root = document.getRootElement();
	    this.createWorkflowFromXml(wf, root);
	} catch (JDOMException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return;
    }

    private void createWorkflowFromXml(Workflow wf, Element root) {
	wf.setName(root.getAttributeValue("name"));
	// parse jobs
	Element jobsElement = root.getChild("jobs");
	List<Element> jobElements = jobsElement.getChildren();
	for (Element jobE : jobElements) {
	    this.unserializeJob(wf, jobE);
	}
    }

    private void unserializeJob(Workflow wf, Element element) {
	Job job = null;
	Module module = Module.GenericCommandRunner; // default;
	String algo = element.getChildText("algorithm");
	// default it is gcr
	String type = element.getAttributeValue("type");
	if (null == type) {
	    job = wf.createSeqwareModuleJob(algo);
	} else {
	    module = Module.valueFrom(element.getAttributeValue("module"));
	    job = wf.createSeqwareModuleJob(algo, module);
	}

	if (module == Module.ProvisionFiles) {
	    Element inputfileE = element.getChild("inputfile");
	    String inputfiles = inputfileE.getText();
	    String b = inputfileE.getAttributeValue("metadata");
	    String outputdir = element.getChildText("outputdir");
	    job.setInputFile(inputfiles, Boolean.parseBoolean(b));
	    job.setOutputDir(outputdir);
	} else {
	    // set command
	    String command = element.getChildText("command");
	    if (null != command) {
		job.setCommand(command);
	    }

	}
	// set argument
	String argument = element.getChildText("argument");
	if (null != argument) {
	    job.setModuleArgumentString(argument);
	}
	Element classpath = element.getChild("classpath");
	if (null != classpath) {
	    job.addClassPath(classpath.getText());
	}
    }
}