package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;

import org.jdom.Element;

public class WorkflowApp {
	private static String NAMESPACE = "uri:oozie:workflow:0.2";
	
	private AbstractWorkflowDataModel wfdm;
	
	public WorkflowApp(AbstractWorkflowDataModel wfdm) {
		this.wfdm = wfdm;
	}
	
	public Element serializeXML() {
		 Element element = new Element("workflow-app", NAMESPACE);
		 element.setAttribute("name",wfdm.getName());
		 return element;
	}
}