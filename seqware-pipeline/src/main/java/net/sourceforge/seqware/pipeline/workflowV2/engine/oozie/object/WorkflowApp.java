package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.BashJob;

import org.jdom.Element;

public class WorkflowApp {
	public static org.jdom.Namespace NAMESPACE = org.jdom.Namespace.getNamespace("uri:oozie:workflow:0.2");
	
	private AbstractWorkflowDataModel wfdm;
	private List<OozieJob> jobList;
	
	public WorkflowApp(AbstractWorkflowDataModel wfdm) {
		this.wfdm = wfdm;
		this.jobList = new ArrayList<OozieJob>();
		this.parseDataModel(wfdm);
	}
	
	public Element serializeXML() {
		 Element element = new Element("workflow-app", NAMESPACE);
		 element.setAttribute("name",wfdm.getName());
		 
		 if(this.jobList.isEmpty())
			 return element;
		 
		 OozieJob job0 = this.jobList.get(0);
		 Element start = new Element("start", NAMESPACE);
		 start.setAttribute("to", job0.getName());
		 element.addContent(start);
		 
		 for(OozieJob job: this.jobList) {
			 element.addContent(job.serializeXML());
		 }
		 
		 Element kill = new Element("kill", NAMESPACE);
		 kill.setAttribute("name","fail");
		 Element message = new Element("message", NAMESPACE);
		 message.setText("Java failed, error message[${wf:errorMessage(wf:lastErrorNode())}]");
		 kill.addContent(message);
		 element.addContent(kill);
		 
		 Element end = new Element("end", NAMESPACE);
		 end.setAttribute("name","end");
		 element.addContent(end);
		 return element;
	}
	
	private void parseDataModel(AbstractWorkflowDataModel wfdm) {
		int count = 0;
		//first job create dirs
		//mkdir data job
		AbstractJob job0 = new BashJob("createdirs");
		job0.getCommand().addArgument("cd " + wfdm.getConfigs().get("oozie_working_dir") + "; ");
		job0.getCommand().addArgument("mkdir -p provisionfiles; ");
		//check if there are user defined directory
		if(!wfdm.getDirectories().isEmpty()) {
			for(String dir: wfdm.getDirectories()) {
				job0.getCommand().addArgument("mkdir -p " + dir + "; ");
			}
		}
		
		OozieJob oJob0 = new OozieJob(job0, "start_"+count);
		this.jobList.add(oJob0);
		
	}
}