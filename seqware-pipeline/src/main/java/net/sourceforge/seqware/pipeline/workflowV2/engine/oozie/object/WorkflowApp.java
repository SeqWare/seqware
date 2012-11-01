package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.PegasusJavaJob;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.PegasusJavaSeqwareModuleJob;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.PegasusJob;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.PegasusPerlJob;
import net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.ProvisionFilesJob;
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
	
	public WorkflowApp(AbstractWorkflowDataModel wfdm) {
		this.wfdm = wfdm;
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
		 
		 this.generateWorkflowXml(element, this.jobs.get(0));
		 
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
	
	/*
	 * any okTo need to check if the child has join? if YES, okTo->join->childJob
	 */
	private void generateWorkflowXml(Element rootElement, OozieJob parentJob) {
		String okTo = parentJob.getOkTo();
		if(parentJob.hasFork()) {
			//set parentJob.okTo to forkjob
			okTo = "fork_" + parentJob.getName();
			Element fork = new Element("fork", NAMESPACE);
			fork.setAttribute("name",okTo);
			for(OozieJob childJob: parentJob.getChildren()) {
				Element pathE = new Element("path", NAMESPACE);
				//check join
				String forkStartTo = childJob.getName();
				if(childJob.hasJoin()) {
					//create join
					Element joinE = new Element("join", NAMESPACE);
					joinE.setAttribute("name","join_"+childJob.getName());
					joinE.setAttribute("to",childJob.getName());
					rootElement.addContent(joinE);
					forkStartTo = "join_"+childJob.getName();
				}
				pathE.setAttribute("start",forkStartTo);
				fork.addContent(pathE);
			}
			rootElement.addContent(fork);
		} else if(parentJob.getChildren().size()==1) {
			//check join
			okTo = parentJob.getChildren().iterator().next().getName();
		} 
		
		parentJob.setOkTo(okTo);
		
		rootElement.addContent(parentJob.serializeXML());
		//recursively 
		for(OozieJob childJob: parentJob.getChildren()) {
			this.generateWorkflowXml(rootElement, childJob);
		}
		

	}
	
	private void parseDataModel(AbstractWorkflowDataModel wfdm) {
		boolean metadatawriteback = wfdm.isMetadataWriteBack();
		List<OozieJob> parents = new ArrayList<OozieJob>();
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
		
		OozieJob oJob0 = new OozieJob(job0, "start_"+this.jobs.size());
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
		parents.add(oJob0);
		//provisionFiles job
		//sqwfiles
		if(!wfdm.getFiles().isEmpty()) {
			Collection<OozieJob> newParents = new ArrayList<OozieJob>();
			for(Map.Entry<String,SqwFile> entry: wfdm.getFiles().entrySet()) {
				AbstractJob job = new BashJob("provisionFile_"+entry.getKey());
				job.addFile(entry.getValue());
				OozieProvisionFileJob ojob = new OozieProvisionFileJob(job,
						entry.getValue(),job.getAlgo()+this.jobs.size());
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
					String outputDir = wfdm.getConfigs().get("oozie_working_dir")+"/provisionfiles/" + entry.getValue().getUniqueDir() ;
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
		
		int idCount = this.jobs.size();
		//need to remember the provisionOut and reset the job's children to provisionout's children
		Map<OozieJob, OozieJob> hasProvisionOut = new HashMap<OozieJob, OozieJob>();
		for(AbstractJob job: wfdm.getWorkflow().getJobs()) {
			OozieJob pjob = this.createOozieJobObject(job, wfdm);
			pjob.setMetadataWriteback(metadatawriteback);
			if(workflowRunAccession!=null && !workflowRunAccession.isEmpty()) {
				pjob.setWorkflowRunAccession(workflowRunAccession);
			}
			this.jobs.add(pjob);
			idCount++;
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
									pfjob.getAlgo()+"_"+jobs.size());
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
									pfjob.getAlgo()+"_"+jobs.size());
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
			ret = new OozieJob(job, job.getAlgo() + "_" + this.jobs.size());
		} else if (job instanceof JavaSeqwareModuleJob){
			//ret = new PegasusJavaSeqwareModuleJob(job, wfdm.getWorkflowBaseDir(), wfdm.getTags().get("seqware_version"));
		} else {
			ret = new OozieJob(job, job.getAlgo() + "_" + this.jobs.size());
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
}