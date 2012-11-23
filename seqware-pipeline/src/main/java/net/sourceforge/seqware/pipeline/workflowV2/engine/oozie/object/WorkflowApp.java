package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.BashJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.JavaJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.JavaSeqwareModuleJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.Job;
import net.sourceforge.seqware.pipeline.workflowV2.model.PerlJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;

import org.jdom.Element;

public class WorkflowApp {
	public static org.jdom.Namespace NAMESPACE = org.jdom.Namespace.getNamespace("uri:oozie:workflow:0.2");
	
	private AbstractWorkflowDataModel wfdm;
	private List<OozieJob> jobs;
	private String lastJoin;
    private Map<SqwFile, OozieJob> fileJobMap;
    private String unqiueWorkingDir;

	
	public WorkflowApp(AbstractWorkflowDataModel wfdm, String dir) {
		this.wfdm = wfdm;
		this.unqiueWorkingDir = dir;
		this.jobs = new ArrayList<OozieJob>();
		this.fileJobMap = new HashMap<SqwFile, OozieJob>();
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
		// Set<String> nodes = new HashSet<String>();
		 //Set<OozieJob> jobs = new HashSet<OozieJob>();
		 List<List<OozieJob>> graph = this.reOrganizeGraph(job0);
		 this.generateWorkflowXml2(element, graph);
		 
		 Element kill = new Element("kill", NAMESPACE);
		 kill.setAttribute("name","fail");
		 Element message = new Element("message", NAMESPACE);
		 message.setText("Java failed, error message[${wf:errorMessage(wf:lastErrorNode())}]");
		 kill.addContent(message);
		 element.addContent(kill);
		 
		 if(this.lastJoin !=null && !this.lastJoin.isEmpty()) {
			 Element lastJoin = new Element("join", NAMESPACE);
			 lastJoin.setAttribute("name", this.lastJoin);
			 lastJoin.setAttribute("to","end");
			 element.addContent(lastJoin);
		 }
		 
		 Element end = new Element("end", NAMESPACE);
		 end.setAttribute("name","end");
		 element.addContent(end);
		 return element;
	}
	
	
	private void generateWorkflowXml2(Element rootElement, List<List<OozieJob>> graph) {
		OozieJob root = graph.get(0).get(0);
		Element currentE = root.serializeXML();
		rootElement.addContent(currentE);
		for(int i = 1; i<graph.size(); i++) {
			currentE = this.generateNextLevelXml(rootElement, graph.get(i), currentE, i-1);
		}
		//point the last one to end
		if(currentE.getName().equals("action")) {
			currentE.getChild("ok",NAMESPACE).setAttribute("to","end");
		} else {
			currentE.setAttribute("to","end");
		}
			
	}
	
	private Element generateNextLevelXml(Element rootElement, List<OozieJob> joblist, Element currentElement, int count) {
		Element ret = null;
		//currentElement could be action or join
		//need to set the next to, action: ok element, join: currentElement
		Element setNext = currentElement;
		if(currentElement.getName().equals("action")) {
			setNext = currentElement.getChild("ok", NAMESPACE);
		}
			

		if(joblist.size()>1) {
			//has fork and join
			String forkName = "fork_"+ count;
			setNext.setAttribute("to",forkName);
			Element forkE = new Element("fork", NAMESPACE);
			forkE.setAttribute("name", forkName);
			for(OozieJob job: joblist) {
				Element path = new Element("path", NAMESPACE);
				path.setAttribute("start",job.getName());
				forkE.addContent(path);
			}
			rootElement.addContent(forkE);
			String joinName = "join_" + count;
			//add action for job
			for(OozieJob job: joblist) {
				job.setOkTo(joinName);
				rootElement.addContent(job.serializeXML());
			}
			//add join element
			Element joinE = new Element("join", NAMESPACE);
			joinE.setAttribute("name", joinName);
			rootElement.addContent(joinE);
			ret = joinE;
		} else {
			OozieJob job = joblist.get(0);
			setNext.setAttribute("to", job.getName());
			Element nextE = job.serializeXML();
			rootElement.addContent(nextE);
			ret = nextE;
		}
		return ret;
	}

	
	private void parseDataModel(AbstractWorkflowDataModel wfdm) {
		boolean metadatawriteback = wfdm.isMetadataWriteBack();
		List<OozieJob> parents = new ArrayList<OozieJob>();
		//first job create dirs
		//mkdir data job
		AbstractJob job0 = new BashJob("createdirs");
		job0.getCommand().addArgument("mkdir -p provisionfiles; ");
		//check if there are user defined directory
		if(!wfdm.getDirectories().isEmpty()) {
			for(String dir: wfdm.getDirectories()) {
				job0.getCommand().addArgument("mkdir -p " + dir + "; ");
			}
		}
		
		OozieJob oJob0 = new OozieJob(job0, "start_"+this.jobs.size(), 
				this.unqiueWorkingDir);
		oJob0.setMetadataWriteback(metadatawriteback);
		//if has parent-accessions, assign it to first job
		Collection<String> parentAccession = wfdm.getParentAccessions();
		if(parentAccession!=null && !parentAccession.isEmpty()) {
			oJob0.setParentAccessions(parentAccession);
		}
		String workflowRunAccession = wfdm.getWorkflow_run_accession();
		if(workflowRunAccession!=null && !workflowRunAccession.isEmpty()) {
			oJob0.setWorkflowRunAccession(workflowRunAccession);
			oJob0.setWorkflowRunAncesstor(true);
		}
		this.jobs.add(oJob0);
		parents.add(oJob0);
		//provisionFiles job
		//sqwfiles
		if(!wfdm.getFiles().isEmpty()) {
			Collection<OozieJob> newParents = new ArrayList<OozieJob>();
			for(Map.Entry<String,SqwFile> entry: wfdm.getFiles().entrySet()) {
				AbstractJob job = new BashJob("provisionFile_"+entry.getKey());
				job.addFile(entry.getValue());
				OozieProvisionFileJob ojob = new OozieProvisionFileJob(job,
						entry.getValue(),job.getAlgo()+this.jobs.size(), wfdm.getEnv().getOOZIE_WORK_DIR());
				ojob.setMetadataWriteback(metadatawriteback);
				if(workflowRunAccession!=null && !workflowRunAccession.isEmpty()) {
					ojob.setWorkflowRunAccession(workflowRunAccession);
				}
				this.jobs.add(ojob);
				this.fileJobMap.put(entry.getValue(), ojob);
	
				//handle in 
				if(entry.getValue().isInput()) {
					newParents.add(ojob);
					for(OozieJob parent: parents) {
						ojob.addParent(parent);
					}
					//add mkdir to the first job, then set the file path
					String outputDir = wfdm.getEnv().getOOZIE_WORK_DIR()+"/provisionfiles/" + entry.getValue().getUniqueDir() ;
					job0.getCommand().addArgument("mkdir -p " + outputDir + "; ");
					ojob.setOutputDir(outputDir);
				} else {
					ojob.setMetadataOutputPrefix(wfdm.getMetadata_output_file_prefix());
					ojob.setOutputDir(wfdm.getMetadata_output_dir());
					//set the filepath
				}
			}
			//reset parents
			parents.clear();
			parents.addAll(newParents);
		}
		
		//need to remember the provisionOut and reset the job's children to provisionout's children
		Map<OozieJob, OozieJob> hasProvisionOut = new HashMap<OozieJob, OozieJob>();
		for(AbstractJob job: wfdm.getWorkflow().getJobs()) {
			OozieJob pjob = this.createOozieJobObject(job, wfdm);
			pjob.setMetadataWriteback(metadatawriteback);
			if(workflowRunAccession!=null && !workflowRunAccession.isEmpty()) {
				pjob.setWorkflowRunAccession(workflowRunAccession);
			}
			this.jobs.add(pjob);
			for(Job parent: job.getParents()) {
				pjob.addParent(this.getOozieJobObject((AbstractJob)parent));
			}
			
			
			//has provisionfiles dependency?
			// this based on the assumption that the provisionFiles job is always in the beginning or the end.
			if(job.getFiles().isEmpty() == false) {
				for(SqwFile file: job.getFiles()) {
					//create a provisionfile job\
					if(file.isInput()) {					
							//create a provisionFileJob;
							AbstractJob pfjob = new BashJob("provisionFile_in");
							pfjob.addFile(file);
							OozieProvisionFileJob parentPfjob = new OozieProvisionFileJob(pfjob, file,
									pfjob.getAlgo()+"_"+jobs.size(), wfdm.getEnv().getOOZIE_WORK_DIR());
							parentPfjob.addParent(oJob0);
							parentPfjob.setMetadataWriteback(metadatawriteback);
							if(workflowRunAccession!=null && !workflowRunAccession.isEmpty()) {
								parentPfjob.setWorkflowRunAccession(workflowRunAccession);
							}
							this.jobs.add(parentPfjob);
							parentPfjob.setOutputDir("provisionfiles/"+file.getUniqueDir()) ;
							pjob.addParent(parentPfjob);	
							//add mkdir to the first job, then set the file path
							job0.getCommand().addArgument("mkdir -p " + "provisionfiles/"+file.getUniqueDir() + "; ");
					} else {
							//create a provisionFileJob;
							AbstractJob pfjob = new BashJob("provisionFile_out");
							pfjob.addFile(file);
							OozieProvisionFileJob parentPfjob = new OozieProvisionFileJob(pfjob, file,
									pfjob.getAlgo()+"_"+jobs.size(), wfdm.getEnv().getOOZIE_WORK_DIR());
							parentPfjob.addParent(pjob);
							parentPfjob.setMetadataWriteback(metadatawriteback);
							parentPfjob.setMetadataOutputPrefix(wfdm.getMetadata_output_file_prefix());
							parentPfjob.setOutputDir(wfdm.getMetadata_output_dir());
							if(workflowRunAccession!=null && !workflowRunAccession.isEmpty()) {
								parentPfjob.setWorkflowRunAccession(workflowRunAccession);
							}
							this.jobs.add(parentPfjob);
							hasProvisionOut.put(pjob, parentPfjob);
					}
				}
			}
			
			//if no parent, set parents after provisionfiles
			if(pjob.getParents().isEmpty()) {
				for(OozieJob parent: parents) {
					pjob.addParent(parent);
				}
			}
		}
		
		if(!hasProvisionOut.isEmpty()) {
			for(Map.Entry<OozieJob, OozieJob> entry: hasProvisionOut.entrySet()) {
				//get all children
				Collection<OozieJob> children = entry.getKey().getChildren();
				if(children.size()<=1)
					continue;
				// and set other's parent as the value
				for(OozieJob child: children ) {
					if(child == entry.getValue())
						continue;
					child.addParent(entry.getValue());
				}
			}
		}
		
		//add all provision out job
		//get all the leaf job
		List<OozieJob> leaves = new ArrayList<OozieJob>();
		for(OozieJob _job: this.jobs) {
			if(_job.getChildren().isEmpty()) {
				leaves.add(_job);
			}
		}
		for(Map.Entry<SqwFile, OozieJob> entry: fileJobMap.entrySet()) {
			if(entry.getKey().isOutput()) {
				//set parents to all leaf jobs
				for(OozieJob leaf: leaves) {
					if(leaf!=entry.getValue())
						entry.getValue().addParent(leaf);
				}
			}
		}
		this.setEndJob();
		this.setAccessionFileRelations(oJob0);
	}
	
	/**
	 * if the objectmodel has multiple leaves job, need to join them before end
	 */
	private void setEndJob() {
		if(needLastJoin()) {
			//set a unique name for the join action in case of name conflict
			this.lastJoin = "join_"+Long.toString(System.nanoTime());
			for(OozieJob job: this.jobs) {
				if(job.getChildren().size() == 0) {
					job.setOkTo(this.lastJoin);
				}
			}
		}
	}
	
	private boolean needLastJoin() {
		int leafCount = 0;
		for(OozieJob job: this.jobs) {
			if(job.getChildren().size() == 0)
				leafCount ++;
		}
		return leafCount > 1;
	}
	
	private OozieJob createOozieJobObject(AbstractJob job, AbstractWorkflowDataModel wfdm) {
		OozieJob ret = null;
		if(job instanceof JavaJob) {
			//ret = new PegasusJavaJob(job,wfdm.getWorkflowBaseDir(), wfdm.getTags().get("seqware_version"));
		} else if(job instanceof PerlJob) {
			ret = new OozieJob(job, job.getAlgo() + "_" + this.jobs.size(), 
					this.unqiueWorkingDir);
		} else if (job instanceof JavaSeqwareModuleJob){
			//ret = new PegasusJavaSeqwareModuleJob(job, wfdm.getWorkflowBaseDir(), wfdm.getTags().get("seqware_version"));
		} else {
			ret = new OozieJob(job, job.getAlgo() + "_" + this.jobs.size(), 
					this.unqiueWorkingDir);
		}
		return ret;
	}

	private OozieJob getOozieJobObject(AbstractJob job) {
		for(OozieJob pjob: this.jobs) {
			if(job.equals(pjob.getJobObject()))
				return pjob;
		}
		return null;
	}
	
	private List<List<OozieJob>> reOrganizeGraph(OozieJob root) {
		List<List<OozieJob>> newGraph = new ArrayList<List<OozieJob>>();
		//to avoid duplicated action
		Set<String> jobName = new HashSet<String>();
		//add the root
		List<OozieJob> rootList = new ArrayList<OozieJob>();
		rootList.add(root);
		newGraph.add(rootList);
		jobName.add(root.getName());
		this.getNextLevel(newGraph, jobName);
		return newGraph;
	}
	
	private void getNextLevel(List<List<OozieJob>> graph, Set<String> existingJob) {
		List<OozieJob> lastLevel = graph.get(graph.size() -1);
		List<OozieJob> nextLevel = new ArrayList<OozieJob>();
		Set<OozieJob> removed = new HashSet<OozieJob>();
		for(OozieJob job: lastLevel) {
			for(OozieJob child: job.getChildren()) { 
				if(!nextLevel.contains(child))
					nextLevel.add(child);
				//remove it from the upper level
				if(existingJob.contains(child.getName())) {
					removed.add(child);
				}
				existingJob.add(child.getName());
			}
		}
		if(!removed.isEmpty()) {
			for(OozieJob rm: removed) {
				for(List<OozieJob> level: graph) {
					if(level.contains(rm)) {
						level.remove(rm);
					}
				}
			}
		}
		if(!nextLevel.isEmpty()) {
			graph.add(nextLevel);
			getNextLevel(graph, existingJob);
		}
	}
	
	private void setAccessionFileRelations(OozieJob parent) {
		for(OozieJob pjob: parent.getChildren()) {
			pjob.addParentAccessionFile(parent.getAccessionFile());
			setAccessionFileRelations(pjob);
		}
	}
}