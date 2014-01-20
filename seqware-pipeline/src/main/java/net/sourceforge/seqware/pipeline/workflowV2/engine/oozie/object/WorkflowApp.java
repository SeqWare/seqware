package net.sourceforge.seqware.pipeline.workflowV2.engine.oozie.object;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.pipeline.workflowV2.AbstractWorkflowDataModel;
import net.sourceforge.seqware.pipeline.workflowV2.model.AbstractJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.BashJob;
import net.sourceforge.seqware.pipeline.workflowV2.model.Job;
import net.sourceforge.seqware.pipeline.workflowV2.model.SqwFile;

import org.apache.hadoop.fs.Path;
import org.jdom.Element;

public class WorkflowApp {
  public static final String URIOOZIEWORKFLOW = "uri:oozie:workflow:0.4";
  public static org.jdom.Namespace NAMESPACE = org.jdom.Namespace.getNamespace(URIOOZIEWORKFLOW);

  private AbstractWorkflowDataModel wfdm;
  private List<OozieJob> jobs;
  private String lastJoin;
  private Map<SqwFile, OozieProvisionFileJob> fileJobMap;
  private String unqiueWorkingDir;
  private Path hdfsWorkDir;
  private boolean useSge;
  private File seqwareJar;
  private String threadsSgeParamFormat;
  private String maxMemorySgeParamFormat;

  public WorkflowApp(AbstractWorkflowDataModel wfdm, String nfsWorkDir, Path hdfsWorkDir, boolean useSge, File seqwareJar,
                     String threadsSgeParamFormat, String maxMemorySgeParamFormat) {
    this.wfdm = wfdm;
    this.unqiueWorkingDir = nfsWorkDir;
    this.hdfsWorkDir = hdfsWorkDir;
    this.jobs = new ArrayList<OozieJob>();
    this.fileJobMap = new HashMap<SqwFile, OozieProvisionFileJob>();
    this.useSge = useSge;
    this.seqwareJar = seqwareJar;
    this.threadsSgeParamFormat = threadsSgeParamFormat;
    this.maxMemorySgeParamFormat = maxMemorySgeParamFormat;
    this.parseDataModel(wfdm);
  }

  // TODO: Emit an end node that cleans up the generated script files.
  public Element serializeXML() {
    Element wf = new Element("workflow-app", NAMESPACE);
    wf.setAttribute("name", wfdm.getName());

    if (!this.jobs.isEmpty()) {
    OozieJob job0 = this.jobs.get(0);
    Element start = new Element("start", NAMESPACE);
    start.setAttribute("to", job0.getName());
    wf.addContent(start);
    // Set<String> nodes = new HashSet<String>();
    // Set<OozieJob> jobs = new HashSet<OozieJob>();
    List<List<OozieJob>> graph = this.reOrganizeGraph(job0);
    this.generateWorkflowXml2(wf, graph);
    }

    if (this.lastJoin != null && !this.lastJoin.isEmpty()) {
      Element lastJoinLocal = new Element("join", NAMESPACE);
      lastJoinLocal.setAttribute("name", this.lastJoin);
      lastJoinLocal.setAttribute("to", "done");
      wf.addContent(lastJoinLocal);
    }

    Element done = new Element("action", NAMESPACE).setAttribute("name", "done");
    Element fs = new Element("fs", NAMESPACE);
    Element delete = new Element("delete", NAMESPACE).setAttribute("path", hdfsWorkDir.toString());
    Element ok = new Element("ok", NAMESPACE).setAttribute("to", "end");
    Element error = new Element("error", NAMESPACE).setAttribute("to", "fail");
    wf.addContent(done);
    done.addContent(fs);
    fs.addContent(delete);
    done.addContent(ok);
    done.addContent(error);

    Element kill = new Element("kill", NAMESPACE);
    kill.setAttribute("name", "fail");
    Element message = new Element("message", NAMESPACE);
    message.setText("Java failed, error message[${wf:errorMessage(wf:lastErrorNode())}]");
    kill.addContent(message);
    wf.addContent(kill);

    Element end = new Element("end", NAMESPACE);
    end.setAttribute("name", "end");
    wf.addContent(end);
    return wf;
  }

  private void generateWorkflowXml2(Element rootElement, List<List<OozieJob>> graph) {
    OozieJob root = graph.get(0).get(0);
    Element currentE = root.serializeXML();
    rootElement.addContent(currentE);
    for (int i = 1; i < graph.size(); i++) {
      currentE = this.generateNextLevelXml(rootElement, graph.get(i), currentE, i - 1);
    }
    // point the last one to end
    if (currentE.getName().equals("action")) {
      currentE.getChild("ok", NAMESPACE).setAttribute("to", "done");
    } else {
      currentE.setAttribute("to", "done");
    }

  }

  private Element generateNextLevelXml(Element rootElement, List<OozieJob> joblist, Element currentElement, int count) {
    Element ret;
    // currentElement could be action or join
    // need to set the next to, action: ok element, join: currentElement
    Element setNext = currentElement;
    if (currentElement.getName().equals("action")) {
      setNext = currentElement.getChild("ok", NAMESPACE);
    }

    if (joblist.size() > 1) {
      // has fork and join
      String forkName = "fork_" + count;
      setNext.setAttribute("to", forkName);
      Element forkE = new Element("fork", NAMESPACE);
      forkE.setAttribute("name", forkName);
      for (OozieJob job : joblist) {
        Element path = new Element("path", NAMESPACE);
        path.setAttribute("start", job.getName());
        forkE.addContent(path);
      }
      rootElement.addContent(forkE);
      String joinName = "join_" + count;
      // add action for job
      for (OozieJob job : joblist) {
        job.setOkTo(joinName);
        rootElement.addContent(job.serializeXML());
      }
      // add join element
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
    // first job create dirs
    // mkdir data job
    AbstractJob job0 = new BashJob("createdirs");
    job0.getCommand().addArgument("mkdir -p provisionfiles; ");
    // check if there are user defined directory
    if (!wfdm.getDirectories().isEmpty()) {
      for (String dir : wfdm.getDirectories()) {
        job0.getCommand().addArgument("mkdir -p " + dir + "; ");
      }
    }

    OozieJob oJob0 = new OozieBashJob(job0, "start_" + this.jobs.size(), this.unqiueWorkingDir, this.useSge,
                                  this.seqwareJar, this.threadsSgeParamFormat, this.maxMemorySgeParamFormat);
    oJob0.setMetadataWriteback(metadatawriteback);
    // if has parent-accessions, assign it to first job
    Collection<String> parentAccession = wfdm.getParentAccessions();
    if (parentAccession != null && !parentAccession.isEmpty()) {
      oJob0.setParentAccessions(parentAccession);
    }
    String workflowRunAccession = wfdm.getWorkflow_run_accession();
    if (workflowRunAccession != null && !workflowRunAccession.isEmpty()) {
      oJob0.setWorkflowRunAccession(workflowRunAccession);
      oJob0.setWorkflowRunAncesstor(true);
    }
    this.jobs.add(oJob0);
    parents.add(oJob0);
    // provisionFiles job
    // sqwfiles
    if (!wfdm.getFiles().isEmpty()) {
      Collection<OozieJob> newParents = new ArrayList<OozieJob>();
      for (Map.Entry<String, SqwFile> entry : wfdm.getFiles().entrySet()) {
        AbstractJob job = new BashJob("provisionFile_" + entry.getKey().replaceAll("\\.", "_"));
        job.addFile(entry.getValue());
        OozieProvisionFileJob ojob = new OozieProvisionFileJob(job, entry.getValue(), job.getAlgo() + this.jobs.size(),
                                                               this.unqiueWorkingDir, this.useSge, this.seqwareJar,
                                                               this.threadsSgeParamFormat, this.maxMemorySgeParamFormat);
        ojob.setMetadataWriteback(metadatawriteback);
        if (workflowRunAccession != null && !workflowRunAccession.isEmpty()) {
          ojob.setWorkflowRunAccession(workflowRunAccession);
        }
          // SEQWARE-1804 transfer setParentAccessions information ala Pegasus version in net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.Adag 
          if (!entry.getValue().getParentAccessions().isEmpty()) {
              ojob.setParentAccessions(entry.getValue().getParentAccessions());
          }
        this.jobs.add(ojob);
        this.fileJobMap.put(entry.getValue(), ojob);

        // handle in
        if (entry.getValue().isInput()) {
          newParents.add(ojob);
          for (OozieJob parent : parents) {
            ojob.addParent(parent);
          }
          // add mkdir to the first job, then set the file path
          String outputDir = this.unqiueWorkingDir + "/provisionfiles/" + entry.getValue().getUniqueDir();
          job0.getCommand().addArgument("mkdir -p " + outputDir + "; ");
          ojob.setOutputDir(outputDir);
        } else {
          ojob.setMetadataOutputPrefix(wfdm.getMetadata_output_file_prefix());
          ojob.setOutputDir(wfdm.getMetadata_output_dir());
          // set the filepath
        }
      }
      // reset parents
      parents.clear();
      parents.addAll(newParents);
    }

    // need to remember the provisionOut and reset the job's children to
    // provisionout's children
    Map<OozieJob, OozieJob> hasProvisionOut = new HashMap<OozieJob, OozieJob>();
    for (AbstractJob job : wfdm.getWorkflow().getJobs()) {
      OozieJob pjob = this.createOozieJobObject(job, wfdm);
      pjob.setMetadataWriteback(metadatawriteback);
      if (workflowRunAccession != null && !workflowRunAccession.isEmpty()) {
        pjob.setWorkflowRunAccession(workflowRunAccession);
      }
      // SEQWARE-1804 transfer setParentAccessions information ala Pegasus version in net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.Adag 
        if (!job.getParentAccessions().isEmpty()) {
            pjob.setParentAccessions(job.getParentAccessions());
        }
      
      this.jobs.add(pjob);
      for (Job parent : job.getParents()) {
        pjob.addParent(this.getOozieJobObject((AbstractJob) parent));
      }

      // has provisionfiles dependency?
      // this based on the assumption that the provisionFiles job is always in
      // the beginning or the end.
      if (job.getFiles().isEmpty() == false) {
        for (SqwFile file : job.getFiles()) {
          // create a provisionfile job\
          if (file.isInput()) {
            // create a provisionFileJob;
            AbstractJob pfjob = new BashJob("provisionFile_in");
            pfjob.addFile(file);
            OozieProvisionFileJob parentPfjob = new OozieProvisionFileJob(pfjob, file, pfjob.getAlgo() + "_"
                + jobs.size(), this.unqiueWorkingDir, this.useSge, this.seqwareJar, this.threadsSgeParamFormat,
                                                                          this.maxMemorySgeParamFormat);
            parentPfjob.addParent(oJob0);
            parentPfjob.setMetadataWriteback(metadatawriteback);
            if (workflowRunAccession != null && !workflowRunAccession.isEmpty()) {
              parentPfjob.setWorkflowRunAccession(workflowRunAccession);
            }
            // SEQWARE-1804 transfer setParentAccessions information ala Pegasus version in net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.Adag 
              if (!file.getParentAccessions().isEmpty()) {
                  parentPfjob.setParentAccessions(file.getParentAccessions());
              }
            this.jobs.add(parentPfjob);
            parentPfjob.setOutputDir("provisionfiles/" + file.getUniqueDir());
            pjob.addParent(parentPfjob);
            // add mkdir to the first job, then set the file path
            job0.getCommand().addArgument("mkdir -p " + "provisionfiles/" + file.getUniqueDir() + "; ");
          } else {
            // create a provisionFileJob;
            AbstractJob pfjob = new BashJob("provisionFile_out");
            pfjob.addFile(file);
            OozieProvisionFileJob parentPfjob = new OozieProvisionFileJob(pfjob, file, pfjob.getAlgo() + "_"
                + jobs.size(), this.unqiueWorkingDir, this.useSge, this.seqwareJar, this.threadsSgeParamFormat,
                                                                          this.maxMemorySgeParamFormat);
            parentPfjob.addParent(pjob);
            parentPfjob.setMetadataWriteback(metadatawriteback);
            parentPfjob.setMetadataOutputPrefix(wfdm.getMetadata_output_file_prefix());
            parentPfjob.setOutputDir(wfdm.getMetadata_output_dir());
            if (workflowRunAccession != null && !workflowRunAccession.isEmpty()) {
              parentPfjob.setWorkflowRunAccession(workflowRunAccession);
            }
            // SEQWARE-1804 transfer setParentAccessions information ala Pegasus version in net.sourceforge.seqware.pipeline.workflowV2.engine.pegasus.object.Adag 
              if (!file.getParentAccessions().isEmpty()) {
                  parentPfjob.setParentAccessions(file.getParentAccessions());
              }
            this.jobs.add(parentPfjob);
            hasProvisionOut.put(pjob, parentPfjob);
          }
        }
      }

      // if no parent, set parents after provisionfiles
      if (pjob.getParents().isEmpty()) {
        for (OozieJob parent : parents) {
          pjob.addParent(parent);
        }
      }
    }

        // what is this for? Theory is that setting up dependencies between jobs may have been used as a rate-limiting mechanism
//        if(!hasProvisionOut.isEmpty()) {
//            for(Map.Entry<OozieJob, OozieJob> entry: hasProvisionOut.entrySet()) {
//                //get all children
//                Collection<OozieJob> children = entry.getKey().getChildren();
//                if(children.size()<=1)
//                    continue;
//                // and set other's parent as the value
//                for(OozieJob child: children ) {
//                    if(child == entry.getValue())
//                        continue;
//                    child.addParent(entry.getValue());
//                }
//            }
//        }

    // add all provision out job
    // get all the leaf job
    List<OozieJob> leaves = new ArrayList<OozieJob>();
    for (OozieJob _job : this.jobs) {
      // Note: the leaves accumulated are to be parents of output provisions,
      //       thus the leaves themselves should not be file provisions
      if((_job instanceof OozieProvisionFileJob == false)
          && _job.getChildren().isEmpty()) {
        leaves.add(_job);
      }
    }
    for (Map.Entry<SqwFile, OozieProvisionFileJob> entry : fileJobMap.entrySet()) {
      if (entry.getKey().isOutput()) {
        // set parents to all leaf jobs
        for (OozieJob leaf : leaves) {
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
    if (needLastJoin()) {
      // set a unique name for the join action in case of name conflict
      this.lastJoin = "join_" + Long.toString(System.nanoTime());
      for (OozieJob job : this.jobs) {
        if (job.getChildren().isEmpty()) {
          job.setOkTo(this.lastJoin);
        }
      }
    }
  }

  private boolean needLastJoin() {
    int leafCount = 0;
    for (OozieJob job : this.jobs) {
      if (job.getChildren().isEmpty())
        leafCount++;
    }
    return leafCount > 1;
  }

  private OozieJob createOozieJobObject(AbstractJob job, AbstractWorkflowDataModel wfdm) {
    if (job instanceof BashJob) {
      return new OozieBashJob(job, job.getAlgo() + "_" + this.jobs.size(), this.unqiueWorkingDir, this.useSge,
                         this.seqwareJar, this.threadsSgeParamFormat, this.maxMemorySgeParamFormat);
    } else {
      throw new UnsupportedOperationException("No oozie support for job type "+job.getClass());
    }
  }

  private OozieJob getOozieJobObject(AbstractJob job) {
    for (OozieJob pjob : this.jobs) {
      if (job.equals(pjob.getJobObject()))
        return pjob;
    }
    return null;
  }

  private List<List<OozieJob>> reOrganizeGraph(OozieJob root) {
    List<List<OozieJob>> newGraph = new ArrayList<List<OozieJob>>();
    // to avoid duplicated action
    Set<String> jobName = new HashSet<String>();
    // add the root
    List<OozieJob> rootList = new ArrayList<OozieJob>();
    rootList.add(root);
    newGraph.add(rootList);
    jobName.add(root.getName());
    this.getNextLevel(newGraph, jobName);
    return newGraph;
  }

  private void getNextLevel(List<List<OozieJob>> graph, Set<String> existingJob) {
    List<OozieJob> lastLevel = graph.get(graph.size() - 1);
    List<OozieJob> nextLevel = new ArrayList<OozieJob>();
    Set<OozieJob> removed = new HashSet<OozieJob>();
    for (OozieJob job : lastLevel) {
      for (OozieJob child : job.getChildren()) {
        if (!nextLevel.contains(child))
          nextLevel.add(child);
        // remove it from the upper level
        if (existingJob.contains(child.getName())) {
          removed.add(child);
        }
        existingJob.add(child.getName());
      }
    }
    if (!removed.isEmpty()) {
      for (OozieJob rm : removed) {
        for (List<OozieJob> level : graph) {
          if (level.contains(rm)) {
            level.remove(rm);
          }
        }
      }
    }
    if (!nextLevel.isEmpty()) {
      graph.add(nextLevel);
      getNextLevel(graph, existingJob);
    }
  }

  private void setAccessionFileRelations(OozieJob parent){
      this.setAccessionFileRelations(parent, 0);
  }
  
  private void setAccessionFileRelations(OozieJob parent, int level) {
    Log.debug(level + ": SETTING ACCESSIONS FOR CHILDREN FOR PARENT JOB " + parent.getName());
    for (OozieJob pjob : parent.getChildren()) {
      Log.debug(level + ": RECURSIVE SETTING ACCESSIONS FOR CHILDOB " + pjob.getName());
      boolean added = pjob.addParentAccessionFile(parent.getAccessionFile());
      Log.debug(level + ": Added success: " + added);
      if (!added){
          // if no parent accession file was added, then recursive calls beyond this level 
          // of recursion should be unnecessary and can be ignored
          // this takes a substantial amount of time beyond five large forks in the workflow 
          continue;
      }
      // FIXME: there is some (potentially very serious) bug here were loops
      // exist in the processing output provision parent/child relationships!
      // if (!pjob.getChildren().contains(parent)) {
      // setAccessionFileRelations(pjob); }
      
      // don't bother calling this when it has already been called 
      setAccessionFileRelations(pjob, level + 1);
    }
  }
}