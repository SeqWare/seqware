package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sourceforge.seqware.common.business.ExperimentService;
import net.sourceforge.seqware.common.business.FileService;
import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingExperimentsService;
import net.sourceforge.seqware.common.business.ProcessingIUSService;
import net.sourceforge.seqware.common.business.ProcessingLanesService;
import net.sourceforge.seqware.common.business.ProcessingRelationshipService;
import net.sourceforge.seqware.common.business.ProcessingSamplesService;
import net.sourceforge.seqware.common.business.ProcessingSequencerRunsService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.ProcessingStudiesService;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.SequencerRunService;
import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.model.adapters.XmlizeFileSet;
import net.sourceforge.seqware.common.model.adapters.XmlizeXML;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.security.PermissionsAware;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.jsontools.JsonUtil;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

@XmlRootElement
public class Processing implements Serializable, Comparable<Processing>, PermissionsAware {

  private static final long serialVersionUID = 4681328115923390568L;
  private Integer processingId;
  private String filePath;
  private Set<Study> studies = new TreeSet<Study>();
  private Set<Sample> samples = new TreeSet<Sample>();
  private Set<IUS> ius = new TreeSet<IUS>();
  private Set<Lane> lanes = new TreeSet<Lane>();
  private Set<File> files = new TreeSet<File>();
  private Set<SequencerRun> sequencerRuns = new TreeSet<SequencerRun>();
  private Set<Experiment> experiments = new TreeSet<Experiment>();
  private Set<Processing> children = new TreeSet<Processing>();
  private Set<Processing> parents = new TreeSet<Processing>(); // typically just
  // one parent!
  private Set<ProcessingAttribute> processingAttributes = new TreeSet<ProcessingAttribute>();
  private WorkflowRun workflowRunByAncestorWorkflowRunId;
  private String algorithm;
  private String status;
  private Integer exitStatus;
  private Integer processExitStatus;
  private String description;
  private String url;
  private String urlLabel;
  private String version;
  private boolean taskGroup;
  private Date createTimestamp;
  private Date updateTimestamp;
  private Date runStartTimestamp;
  private Date runStopTimestamp;
  private Integer swAccession;
  private Integer workflowRunId;
  private Registration owner;
  private WorkflowRun workflowRun;
  private Boolean isSelected = false;
  private Boolean isHasFile = false;
  private String parameters;
  private String stdout;
  private String stderr;

  public Processing() {
    super();
  }

  @Override
  public int compareTo(Processing that) {
    if (that == null || that.getProcessingId() == null) {
      return 1;
    }
    if (this.getProcessingId() == null) {
      return -1;
    }
    return (that.getProcessingId().compareTo(this.getProcessingId()));
  }

  @Override
  public String toString() {
    return "Processing{" + "processingId=" + processingId + ", filePath=" + filePath
        + ", workflowRunByAncestorWorkflowRunId=" + workflowRunByAncestorWorkflowRunId + ", algorithm=" + algorithm
        + ", status=" + status + ", exitStatus=" + exitStatus + ", processExitStatus=" + processExitStatus
        + ", description=" + description + ", url=" + url + ", urlLabel=" + urlLabel + ", version=" + version
        + ", taskGroup=" + taskGroup + ", createTimestamp=" + createTimestamp + ", updateTimestamp=" + updateTimestamp
        + ", runStartTimestamp=" + runStartTimestamp + ", runStopTimestamp=" + runStopTimestamp + ", swAccession="
        + swAccession + ", workflowRunId=" + workflowRunId + ", owner=" + owner + ", workflowRun=" + workflowRun
        + ", isSelected=" + isSelected + ", isHasFile=" + isHasFile + ", parameters=" + parameters + ", stdout="
        + stdout + ", stderr=" + stderr + '}';
  }

  @Override
  public boolean equals(Object other) {
    if ((this == other)) {
      return true;
    }
    if (!(other instanceof Processing)) {
      return false;
    }
    Processing castOther = (Processing) other;
    return new EqualsBuilder().append(this.getProcessingId(), castOther.getProcessingId()).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(getProcessingId()).toHashCode();
  }

  /*
   * public int compareTo(Processing that) {
   * return(that.getSwAccession().compareTo(this.getSwAccession())); }
   * 
   * public String toString() { return new ToStringBuilder(this)
   * .append("processingId", getProcessingId()) .append("filePath",
   * getFilePath()) .toString(); }
   * 
   * public boolean equals(Object other) { if ( (this == other ) ) return true;
   * if ( !(other instanceof Processing) ) return false; Processing castOther =
   * (Processing) other; return new EqualsBuilder()
   * .append(this.getSwAccession(), castOther.getSwAccession()) .isEquals(); }
   * 
   * public int hashCode() { return new HashCodeBuilder()
   * .append(getSwAccession()) .toHashCode(); }
   */
  public Integer getExitStatus() {
    return exitStatus;
  }

  public void setExitStatus(Integer exitStatus) {
    this.exitStatus = exitStatus;
  }

  public Integer getProcessExitStatus() {
    return processExitStatus;
  }

  public void setProcessExitStatus(Integer processExitStatus) {
    this.processExitStatus = processExitStatus;
  }

  public Set<Processing> getParents() {
    return parents;
  }

  public void setParents(Set<Processing> parents) {
    this.parents = parents;
  }

  public Set<Processing> getChildren() {
    return children;
  }

  public void setChildren(Set<Processing> children) {
    this.children = children;
  }

  public boolean isTaskGroup() {
    return taskGroup;
  }

  public void setTaskGroup(boolean taskGroup) {
    this.taskGroup = taskGroup;
  }

  public Integer getSwAccession() {
    return swAccession;
  }

  public void setSwAccession(Integer swAccession) {
    this.swAccession = swAccession;
  }

  public Date getCreateTimestamp() {
    return createTimestamp;
  }

  public void setCreateTimestamp(Date createTimestamp) {
    this.createTimestamp = createTimestamp;
  }

  public Date getUpdateTimestamp() {
    return updateTimestamp;
  }

  public void setUpdateTimestamp(Date updateTimestamp) {
    this.updateTimestamp = updateTimestamp;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public String getAlgorithm() {
    return algorithm;
  }

  public String getJsonEscapeAlgorithm() {
    return JsonUtil.forJSON(getAlgorithm());
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public Integer getProcessingId() {
    return processingId;
  }

  public void setProcessingId(Integer processingId) {
    this.processingId = processingId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getJsonEscapeDescription() {
    return JsonUtil.forJSON(description);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Set<Lane> getLanes() {
    /*
     * Set<Lane> lns = new TreeSet<Lane>(); Set<IUS> setIUS = getIUS();
     * logger.debug("IUS size = " + setIUS.size()); for(IUS i : setIUS){
     * lns.add(i.getLane()); } return lns;
     */
    return lanes;
  }

  public void setLanes(Set<Lane> lanes) {
    this.lanes = lanes;
  }

  public String getUrlLabel() {
    return urlLabel;
  }

  public void setUrlLabel(String urlLabel) {
    this.urlLabel = urlLabel;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @XmlJavaTypeAdapter(XmlizeFileSet.class)
  public Set<File> getFiles() {
    return files;
  }

  public void setFiles(Set<File> files) {
    if (this.files == null) {
      this.files = files;
    } else {
      this.files.clear();
      this.files.addAll(files);
    }
  }

  public Integer getWorkflowRunId() {
    return workflowRunId;
  }

  public void setWorkflowRunId(Integer workflowRunId) {
    this.workflowRunId = workflowRunId;
  }

  public Registration getOwner() {
    return owner;
  }

  public void setOwner(Registration owner) {
    this.owner = owner;
  }

  public WorkflowRun getWorkflowRun() {
    return workflowRun;
  }

  public void setWorkflowRun(WorkflowRun workflowRun) {
    this.workflowRun = workflowRun;
  }

  public Boolean getIsSelected() {
    return isSelected;
  }

  public void setIsSelected(Boolean isSelected) {
    this.isSelected = isSelected;
  }

  public Boolean getIsHasFile() {
    return isHasFile;
  }

  public void setIsHasFile(Boolean isHasFile) {
    this.isHasFile = isHasFile;
  }

  public Set<IUS> getIUS() {
    return ius;
  }

  public void setIUS(Set<IUS> ius) {
    this.ius = ius;
  }

  public Set<Study> getStudies() {
    return studies;
  }

  public void setStudies(Set<Study> studies) {
    this.studies = studies;
  }

  public Set<Sample> getSamples() {
    return samples;
  }

  public void setSamples(Set<Sample> samples) {
    this.samples = samples;
  }

  public Set<SequencerRun> getSequencerRuns() {
    return sequencerRuns;
  }

  public void setSequencerRuns(Set<SequencerRun> sequencerRuns) {
    this.sequencerRuns = sequencerRuns;
  }

  public Set<Experiment> getExperiments() {
    return experiments;
  }

  public void setExperiments(Set<Experiment> experiments) {
    this.experiments = experiments;
  }

  public Date getRunStartTimestamp() {
    return runStartTimestamp;
  }

  public void setRunStartTimestamp(Date runStartTimestamp) {
    this.runStartTimestamp = runStartTimestamp;
  }

  public Date getRunStopTimestamp() {
    return runStopTimestamp;
  }

  public void setRunStopTimestamp(Date runStopTimestamp) {
    this.runStopTimestamp = runStopTimestamp;
  }

  public String getParameters() {
    return parameters;
  }

  public void setParameters(String parameters) {
    this.parameters = parameters;
  }

  @XmlJavaTypeAdapter(XmlizeXML.class)
  public String getStdout() {
    return stdout;
  }

  public void setStdout(String stdout) {
    this.stdout = stdout;
  }

  public String getStderr() {
    return stderr;
  }

  @XmlJavaTypeAdapter(XmlizeXML.class)
  public void setStderr(String stderr) {
    this.stderr = stderr;
  }

  public WorkflowRun getWorkflowRunByAncestorWorkflowRunId() {
    return workflowRunByAncestorWorkflowRunId;
  }

  public void setWorkflowRunByAncestorWorkflowRunId(WorkflowRun workflowRunByAncestorWorkflowRunId) {
    this.workflowRunByAncestorWorkflowRunId = workflowRunByAncestorWorkflowRunId;
  }

  @XmlElementWrapper(name = "ProcessingAttributes", nillable = true)
  @XmlElement(name = "ProcessingAttribute")
  public Set<ProcessingAttribute> getProcessingAttributes() {
    return processingAttributes;
  }

  public void setProcessingAttributes(Set<ProcessingAttribute> processingAttributes) {
    this.processingAttributes = processingAttributes;
  }

  public void resetCompletedChildren() {
    Set<Processing> res = new TreeSet<Processing>();
    Set<Processing> all = this.getChildren();

    // get processing with workflow run has not status equal completed
    for (Processing pr : all) {
      WorkflowRun wr = pr.getWorkflowRun();
      if (wr == null || wr.getStatus().equals("completed")) {
        res.add(pr);
      }
    }
    this.setChildren(res);

  }

  public void resetRunningChildren() {
    Set<Processing> res = new TreeSet<Processing>();
    Set<Processing> all = this.getChildren();

    // get processing with workflow run has not status equal completed
    for (Processing pr : all) {
      WorkflowRun wr = pr.getWorkflowRun();
      if (wr == null || !wr.getStatus().equals("completed")) {
        res.add(pr);
      }
    }
    this.setChildren(res);
  }

  public static ReturnValue clone(Processing newP) throws ResourceException, SQLException {
    ReturnValue p = new ReturnValue();
    p.setAlgorithm(newP.getAlgorithm());
    p.setDescription(newP.getDescription());
    if (newP.getExitStatus() != null) {
      p.setExitStatus(newP.getExitStatus());
    }
    p.setParameters(newP.getParameters());
    if (newP.getProcessExitStatus() != null) {
      p.setProcessExitStatus(newP.getProcessExitStatus());
    }
    p.setStderr(newP.getStderr());
    p.setStdout(newP.getStdout());
    p.setUrl(newP.getUrl());
    p.setUrlLabel(newP.getUrlLabel());
    p.setVersion(newP.getVersion());
    return p;
  }

  public static Processing cloneFromDB(int processingId) throws ResourceException, SQLException {
    Processing updatedProcessing = null;
    try {
      ResultSet rs = DBAccess.get().executeQuery("SELECT * FROM processing WHERE processing_id=" + processingId);
      int ownerId;
      int ancestorWorkflowRunId;
      int workflowRunId;
      if (rs.next()) {
        updatedProcessing = new Processing();
        updatedProcessing.setProcessingId(rs.getInt("processing_id"));
        updatedProcessing.setAlgorithm(rs.getString("algorithm"));
        updatedProcessing.setStatus(rs.getString("status"));
        updatedProcessing.setDescription(rs.getString("description"));
        updatedProcessing.setUrl(rs.getString("url"));
        updatedProcessing.setUrlLabel(rs.getString("url_label"));
        updatedProcessing.setVersion(rs.getString("version"));
        updatedProcessing.setParameters(rs.getString("parameters"));
        updatedProcessing.setStdout(rs.getString("stdout"));
        updatedProcessing.setStderr(rs.getString("stderr"));
        updatedProcessing.setExitStatus(rs.getInt("exit_status"));
        updatedProcessing.setProcessExitStatus(rs.getInt("process_exit_status"));
        updatedProcessing.setTaskGroup(rs.getBoolean("task_group"));
        updatedProcessing.setSwAccession(rs.getInt("sw_accession"));
        updatedProcessing.setRunStartTimestamp(rs.getTimestamp("run_start_tstmp"));
        updatedProcessing.setRunStopTimestamp(rs.getTimestamp("run_stop_tstmp"));
        updatedProcessing.setCreateTimestamp(rs.getTimestamp("create_tstmp"));
        updatedProcessing.setUpdateTimestamp(rs.getTimestamp("update_tstmp"));
        ownerId = rs.getInt("owner_id");
        ancestorWorkflowRunId = rs.getInt("ancestor_workflow_run_id");
        workflowRunId = rs.getInt("workflow_run_id");
      } else {
        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "The Processing does not exist");
      }

      if (ownerId != 0) {
        Registration r = Registration.cloneFromDB(ownerId);
        updatedProcessing.setOwner(r);
      }

      if (ancestorWorkflowRunId != 0) {
        WorkflowRun aWR = WorkflowRun.cloneFromDB(ancestorWorkflowRunId);
        updatedProcessing.setWorkflowRunByAncestorWorkflowRunId(aWR);
      }

      if (workflowRunId != 0) {
        WorkflowRun wr = WorkflowRun.cloneFromDB(workflowRunId);
        updatedProcessing.setWorkflowRun(wr);
      }
    } finally {
      DBAccess.close();
    }
    return updatedProcessing;
  }

  public static Processing cloneToHibernate(Processing newP) {
    Logger logger = Logger.getLogger(Processing.class);
    ProcessingService ps = BeanFactory.getProcessingServiceBean();
    Processing p = ps.findByID(newP.getProcessingId());

    p.setAlgorithm(newP.getAlgorithm());
    p.setDescription(newP.getDescription());
    p.setExitStatus(newP.getExitStatus());
    p.setFilePath(newP.getFilePath());
    p.setIsHasFile(newP.getIsHasFile());
    p.setIsSelected(newP.getIsSelected());
    p.setParameters(newP.getParameters());
    p.setProcessExitStatus(newP.getProcessExitStatus());
    p.setRunStartTimestamp(newP.getRunStartTimestamp());
    p.setRunStopTimestamp(newP.getRunStopTimestamp());
    p.setStatus(newP.getStatus());
    p.setStderr(newP.getStderr());
    p.setStdout(newP.getStdout());
    p.setTaskGroup(newP.isTaskGroup());
    p.setUrl(newP.getUrl());
    p.setUrlLabel(newP.getUrlLabel());
    p.setVersion(newP.getVersion());

    for (File f : p.getFiles()) {
      f.getFileId();
    }

    Set<Processing> children = newP.getChildren();
    Set<Experiment> experiments = newP.getExperiments();
    Set<File> files = newP.getFiles();
    Set<IUS> iuses = newP.getIUS();
    Set<Lane> lanes = newP.getLanes();
    Set<Processing> parents = newP.getParents();
    Set<ProcessingAttribute> pAtts = newP.getProcessingAttributes();
    Set<Sample> samples = newP.getSamples();
    Set<SequencerRun> sequencerRuns = newP.getSequencerRuns();
    Set<Study> studies = newP.getStudies();
    WorkflowRun workR = newP.getWorkflowRun();
    WorkflowRun ancestorWR = newP.getWorkflowRunByAncestorWorkflowRunId();

    if (children != null && !children.isEmpty()) {
      ProcessingRelationshipService prs = BeanFactory.getProcessingRelationshipServiceBean();
      for (Processing child : children) {
        Processing newC = ps.findByID(child.getProcessingId());
        logger.debug("Child id:" + newC.getProcessingId() + " swa:" + newC.getSwAccession());
        if (prs.findByProcessings(p, newC) == null) {
          ProcessingRelationship pr = new ProcessingRelationship();
          pr.setProcessingByParentId(p);
          pr.setProcessingByChildId(newC);
          prs.insert(pr);
        }
      }
    }

    if (parents != null && !parents.isEmpty()) {
      ProcessingRelationshipService prs = BeanFactory.getProcessingRelationshipServiceBean();
      for (Processing parent : parents) {
        Processing newC = ps.findByID(parent.getProcessingId());
        logger.debug("Parent id:" + newC.getProcessingId() + " swa:" + newC.getSwAccession());
        if (prs.findByProcessings(newC, p) == null) {
          ProcessingRelationship pr = new ProcessingRelationship();
          pr.setProcessingByParentId(newC);
          pr.setProcessingByChildId(p);
          prs.insert(pr);
        }
      }
    }

    if (lanes != null && !lanes.isEmpty()) {
      LaneService ls = BeanFactory.getLaneServiceBean();
      ProcessingLanesService pls = BeanFactory.getProcessingLaneServiceBean();
      for (Lane lane : lanes) {
        Lane newL = ls.findByID(lane.getLaneId());
        logger.debug("Lane id:" + newL.getLaneId() + " swa:" + newL.getSwAccession());
        if (pls.findByProcessingLane(p, newL) == null) {
          ProcessingLanes pl = new ProcessingLanes();
          pl.setProcessing(p);
          pl.setLane(newL);
          pls.insert(pl);
        }
      }
    }

    if (iuses != null && !iuses.isEmpty()) {
      IUSService is = BeanFactory.getIUSServiceBean();
      ProcessingIUSService pis = BeanFactory.getProcessingIUSServiceBean();
      for (IUS i : iuses) {
        IUS newI = is.findByID(i.getIusId());
        logger.debug("IUS id:" + newI.getIusId() + " swa:" + newI.getSwAccession());
        if (pis.findByProcessingIUS(p, newI) == null) {
          ProcessingIus pi = new ProcessingIus();
          pi.setIus(newI);
          pi.setProcessing(p);
          pis.insert(pi);
        }
      }
    }

    if (samples != null && !samples.isEmpty()) {
      SampleService ss = BeanFactory.getSampleServiceBean();
      ProcessingSamplesService pss = BeanFactory.getProcessingSampleServiceBean();
      for (Sample s : samples) {
        Sample newS = ss.findByID(s.getSampleId());
        logger.debug(" Sample id:" + newS.getSampleId() + " swa:" + newS.getSwAccession());
        if (pss.findByProcessingSample(p, newS) == null) {
          ProcessingSamples prs = new ProcessingSamples();
          prs.setProcessing(p);
          prs.setSample(newS);
          pss.insert(prs);
        }
      }
    }

    if (experiments != null && !experiments.isEmpty()) {
      ExperimentService es = BeanFactory.getExperimentServiceBean();
      ProcessingExperimentsService pes = BeanFactory.getProcessingExperimentServiceBean();
      for (Experiment e : experiments) {
        Experiment newE = es.findByID(e.getExperimentId());
        logger.debug(" Experiment id:" + newE.getExperimentId() + " swa:" + newE.getSwAccession());
        if (pes.findByProcessingExperiment(p, newE) == null) {
          ProcessingExperiments pe = new ProcessingExperiments();
          pe.setProcessing(p);
          pe.setExperiment(newE);
          pes.insert(pe);
        }
      }
    }

    if (files != null && !files.isEmpty()) {
      FileService es = BeanFactory.getFileServiceBean();
      for (File e : files) {
        File newF = es.findByID(e.getFileId());

        logger.debug(" File id:" + newF.getFileId() + " swa:" + newF.getSwAccession());
        if (!p.getFiles().contains(newF)) {
          p.getFiles().add(newF);
        }
      }
    }

    if (sequencerRuns != null && !sequencerRuns.isEmpty()) {
      SequencerRunService es = BeanFactory.getSequencerRunServiceBean();
      ProcessingSequencerRunsService psrs = BeanFactory.getProcessingSequencerRunsServiceBean();
      for (SequencerRun e : sequencerRuns) {
        SequencerRun newSR = es.findByID(e.getSequencerRunId());
        logger.debug(" SequencerRun id:" + newSR.getSequencerRunId() + " swa:" + newSR.getSwAccession());
        if (psrs.findByProcessingSequencerRun(p, newSR) == null) {
          ProcessingSequencerRuns psr = new ProcessingSequencerRuns();
          psr.setProcessing(p);
          psr.setSequencerRun(newSR);
          psrs.insert(psr);
        }
      }
    }

    if (studies != null && !studies.isEmpty()) {
      StudyService es = BeanFactory.getStudyServiceBean();
      ProcessingStudiesService pss = BeanFactory.getProcessingStudiesServiceBean();
      for (Study e : studies) {
        Study newS = es.findByID(e.getStudyId());
        logger.debug(" Study id:" + newS.getStudyId() + " swa:" + newS.getSwAccession());
        if (pss.findByProcessingStudy(p, newS) == null) {
          ProcessingStudies prs = new ProcessingStudies();
          prs.setProcessing(p);
          prs.setStudy(newS);
          pss.insert(prs);
        }
      }
    }

    if (workR != null) {
      WorkflowRunService wrs = BeanFactory.getWorkflowRunServiceBean();
      WorkflowRun newWR = wrs.findByID(workR.getWorkflowRunId());
      logger.debug(" WorkflowRun id:" + newWR.getWorkflowRunId() + " swa:" + newWR.getSwAccession());
      p.setWorkflowRun(newWR);
    }

    if (ancestorWR != null) {
      WorkflowRunService wrs = BeanFactory.getWorkflowRunServiceBean();
      WorkflowRun newWR = wrs.findByID(ancestorWR.getWorkflowRunId());
      logger.debug(" Ancestor WorkflowRun id:" + newWR.getWorkflowRunId() + " swa:" + newWR.getSwAccession());
      // p.setWorkflowRunByAncestorWorkflowRunId(newWR);
      newWR.getOffspringProcessings().add(p);
      // wrs.update(newWR);
    }

    if (pAtts != null && !pAtts.isEmpty()) {
      throw new NotImplementedException("Adding ProcessingAttributes is not implemented");
    }

    Registration owner = newP.getOwner();
    if (owner != null) {
      RegistrationService rs = BeanFactory.getRegistrationServiceBean();
      Registration o = rs.findByEmailAddressAndPassword(owner.getEmailAddress(), owner.getPassword());
      logger.debug(" Registration id:" + o.getRegistrationId());
      if (o != null) {
        p.setOwner(o);
      }
    }

    return p;
  }

  @Override
  public boolean givesPermission(Registration registration) {
    boolean hasPermission = true;
    Log.debug("Checking permissions for processing object " + swAccession + " with user " + registration);
    Set<PermissionsAware> list = null;
    if (ius != null && !ius.isEmpty()) {
      for (IUS i : ius) {
        if (!i.givesPermission(registration)) {
          hasPermission = false;
          break;
        }

      }
    } else if (lanes != null && !lanes.isEmpty()) {
      for (Lane i : lanes) {
        if (!i.givesPermission(registration)) {
          hasPermission = false;
          break;
        }

      }
    } else if (parents != null && !parents.isEmpty()) {
      for (Processing i : parents) {
        if (!i.givesPermission(registration)) {
          hasPermission = false;
          break;
        }

      }
    } else if (samples != null && !samples.isEmpty()) {
      for (Sample i : samples) {
        if (!i.givesPermission(registration)) {
          hasPermission = false;
          break;
        }

      }
    } else if (sequencerRuns != null && !sequencerRuns.isEmpty()) {
      for (SequencerRun i : sequencerRuns) {
        if (!i.givesPermission(registration)) {
          hasPermission = false;
          break;
        }

      }
    } else if (workflowRun != null) {
      hasPermission = workflowRun.givesPermission(registration);
    } else {
      if (registration.equals(this.owner) || registration.isLIMSAdmin()) {
        Logger.getLogger(Processing.class).warn("Modifying Orphan Processing: " + this.toString());
        hasPermission = true;
      } else if (owner == null) {
        Logger.getLogger(Processing.class).warn("Orphan Processing has no owner! Allowing write: " + this.toString());
        hasPermission = true;
        ;
      } else {
        Logger.getLogger(Processing.class).warn("Not modifying Orphan Processing: " + this.toString());
        hasPermission = false;
      }
    }

    if (!hasPermission) {
      Logger.getLogger(Processing.class).info("Processing does not give permission");
      throw new SecurityException("User " + registration.getEmailAddress() + " does not have permission to modify "
          + this.toString());
    } else {
      Logger.getLogger(Processing.class).info(
          "Processing gives permission to " + registration.getEmailAddress() + " " + registration.getPassword());
    }
    return hasPermission;
  }
}
