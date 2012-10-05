package net.sourceforge.seqware.pipeline.workflowV2;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.freemarker.Freemarker;
import net.sourceforge.seqware.pipeline.workflowV2.model.Job;
import net.sourceforge.seqware.pipeline.workflowV2.model.Job1;
import net.sourceforge.seqware.pipeline.workflowV2.model.Module;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow;
import net.sourceforge.seqware.pipeline.workflowV2.model.Workflow2;
import net.sourceforge.seqware.pipeline.workflowV2.model.XmlWorkflowDataModel;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import freemarker.template.TemplateException;

public class WorkflowXmlParser {
	
	public AbstractWorkflowDataModel parseXml(String file, Map<String,String> configs) {
		//generate tmp xml file from FTL file
		File xml = null;
		try {
		    xml = FileTools.createFileWithUniqueName(new File("/tmp"), "xml");
		} catch (IOException e) {
		    e.printStackTrace();
		}

		try {
	         // Merge template with data
	         boolean changed = Freemarker.merge(file, xml.getAbsolutePath(), configs);

	         // While there are variables left, merge output file with hash
	         while (changed == true) {
	             changed = Freemarker.merge(xml.getAbsolutePath(), xml.getAbsolutePath(), configs);
	         }
	     } catch (IOException e) {
	         Log.error("IOException", e);
	            System.exit(ReturnValue.PROGRAMFAILED);
	     } catch (TemplateException e) {
	         // If we caught a template exception, warn and exit
	         Log.error("Freemarker threw an exception: " + e.getMessage());
	         System.exit(ReturnValue.FREEMARKEREXCEPTION);
	     }

		AbstractWorkflowDataModel ret = null;
		SAXBuilder builder = new SAXBuilder();
		try {
		    Document document = (Document) builder.build(xml);
		    Element root = document.getRootElement();
		    ret = this.getWorkflowFromXml(root);
		} catch (JDOMException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}		
		return ret;
	}
	
	private AbstractWorkflowDataModel getWorkflowFromXml(Element root) {
		AbstractWorkflowDataModel dm = new XmlWorkflowDataModel();
		//handle files
		Element filesE = root.getChild("files");
		if(filesE != null) {
			List<Element> fileES = filesE.getChildren();
			for(Element fe: fileES) {
				SqwFile file0 = new SqwFile();
				file0.setLocation(fe.getAttributeValue("location"));
				file0.setType(fe.getAttributeValue("type"));
				file0.setIsInput(Boolean.parseBoolean(fe.getAttributeValue("input")));
				if(fe.getAttribute("forcecopy") != null) {
					file0.setForceCopy(Boolean.parseBoolean(fe.getAttributeValue("forcecopy")));
				}
				dm.getFiles().put(fe.getAttributeValue("name"), file0); 
			}
		}
		//handle jobs
		Element jobsE = root.getChild("jobs");
		if(jobsE != null) {
			List<Element> jobES = jobsE.getChildren();
			for(Element je: jobES) {
				Job job = this.createJobFromElement(je, dm.getWorkflow());
			}
		}
		return dm;
	}
	
    public void parseXml(Workflow2 wf, File file) {
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

    private void createWorkflowFromXml(Workflow2 wf, Element root) {
	wf.setName(root.getAttributeValue("name"));
	// parse jobs
	Element jobsElement = root.getChild("jobs");
	List<Element> jobElements = jobsElement.getChildren();
	for (Element jobE : jobElements) {
	    this.unserializeJob(wf, jobE);
	}
    }

    private void unserializeJob(Workflow2 wf, Element element) {
	Job1 job = null;
	Module module = Module.Seqware_GenericCommandRunner; // default;
	String algo = element.getChildText("algorithm");
	// default it is gcr
	String type = element.getAttributeValue("type");
	if (null == type) {
	    job = wf.createSeqwareModuleJob(algo);
	} else {
	    module = Module.valueFrom(element.getAttributeValue("module"));
	    job = wf.createSeqwareModuleJob(algo, module);
	}

	if (module == Module.Seqware_ProvisionFiles) {
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
    
    private Job createJobFromElement(Element jobE, Workflow workflow) {
    	Job job = null;
    	String algo = jobE.getChildText("algorithm");
    	if(jobE.getAttribute("type") == null || jobE.getAttributeValue("type").equals("bash")) {
    		//bashJob
    		job = workflow.createBashJob(algo);
    	} else if(jobE.getAttributeValue("type").equals("java")) {
    		String cp = jobE.getAttributeValue("classpath");
    		String mainclass = jobE.getAttributeValue("main");
    		job = workflow.createJavaJob(algo, cp, mainclass);
    	} else if(jobE.getAttributeValue("type").equals("perl")) {
    		String script = jobE.getAttributeValue("main");
    		job = workflow.createPerlJob(algo, script);
    	} else if(jobE.getAttributeValue("type").toLowerCase().equals("javamodule")) {
    		job = workflow.createJavaModuleJob(algo);
    	}
    	return job;
    }
}