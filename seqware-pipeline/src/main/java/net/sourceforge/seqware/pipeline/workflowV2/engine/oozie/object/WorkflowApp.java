package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.PegasusJob;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.ProvisionFilesJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.BashJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;

import org.jdom.Element;

public class WorkflowApp {
	public static org.jdom.Namespace NAMESPACE = org.jdom.Namespace.getNamespace("uri:oozie:workflow:0.2");
	
	private AbstractWorkflowDataModel wfdm;
	private List<OozieJob> jobs;
	
	public WorkflowApp(AbstractWorkflowDataModel wfdm) {
		this.wfdm = wfdm;
		this.jobs = new ArrayList<OozieJob>();
		this.parseDataModel(wfdm);
	}
	
	public Element serializeXML() {
		 Element element = new Element("workflow-app", NAMESPACE);
		 element.setAttribute("name",wfdm.getName());
		 
		 if(this.jobs.isEmpty())
			 return element;
		 
		 OozieJob job0 = this.jobs.get(0);
		 Element start = new Element("start", NAMESPACE);
		 start.setAttribute("to", job0.getName());
		 element.addContent(start);
		 
		 for(OozieJob job: this.jobs) {
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
		boolean metadatawriteback = wfdm.isMetadataWriteBack();
		List<OozieJob> parents = new ArrayList<OozieJob>();
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
		
		OozieJob oJob0 = new OozieJob(job0, "start_"+count++);
		oJob0.setMetadataWriteback(metadatawriteback);
		//if has parent-accessions, assign it to first job
		String parentAccession = wfdm.getParent_accessions();
		if(parentAccession!=null && !parentAccession.isEmpty()) {
			oJob0.setParentAccession(parentAccession);
		}
		String workflowRunAccession = wfdm.getWorkflow_run_accession();
		if(workflowRunAccession!=null && !workflowRunAccession.isEmpty()) {
			oJob0.setWorkflowRunAccession(workflowRunAccession);
			oJob0.setWorkflowRunAncesstor(true);
		}
		this.jobs.add(oJob0);
		
		//provisionFiles job
		//sqwfiles
		if(!wfdm.getFiles().isEmpty()) {
			Collection<OozieJob> newParents = new ArrayList<OozieJob>();
			for(Map.Entry<String,SqwFile> entry: wfdm.getFiles().entrySet()) {
				AbstractJob job = new BashJob("provisionFile_"+entry.getKey());
				job.addFile(entry.getValue());
				OozieProvisionFileJob pjob = new OozieProvisionFileJob(job,job.getAlgo()+count++);
				pjob.setMetadataWriteback(metadatawriteback);
				if(workflowRunAccession!=null && !workflowRunAccession.isEmpty()) {
					pjob.setWorkflowRunAccession(workflowRunAccession);
				}
				this.jobs.add(pjob);
				//this.fileJobMap.put(entry.getValue(), pjob);
	
				//handle in 
				if(entry.getValue().isInput()) {
					newParents.add(pjob);
					for(OozieJob parent: parents) {
						pjob.addParent(parent);
					}
					//add mkdir to the first job, then set the file path
					String outputDir = "provisionfiles/" + entry.getValue().getUniqueDir() ;
					job0.getCommand().addArgument("mkdir -p " + outputDir + "; ");
					pjob.setOutputDir(outputDir);
				} else {
					pjob.setMetadataOutputPrefix(wfdm.getMetadata_output_file_prefix());
					pjob.setOutputDir(wfdm.getMetadata_output_dir());
					//set the filepath
				}
			}
			//reset parents
			parents.clear();
			parents.addAll(newParents);
		}

	}
}