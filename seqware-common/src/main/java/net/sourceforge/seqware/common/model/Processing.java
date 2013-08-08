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
import org.apache.commons.dbutils.DbUtils;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

@XmlRootElement
/**
 * <p>Processing class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
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
  private ProcessingStatus status;
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

  /**
   * <p>Constructor for Processing.</p>
   */
  public Processing() {
    super();
  }

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
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

  /** {@inheritDoc} */
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
  /**
   * <p>Getter for the field <code>exitStatus</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getExitStatus() {
    return exitStatus;
  }

  /**
   * <p>Setter for the field <code>exitStatus</code>.</p>
   *
   * @param exitStatus a {@link java.lang.Integer} object.
   */
  public void setExitStatus(Integer exitStatus) {
    this.exitStatus = exitStatus;
  }

  /**
   * <p>Getter for the field <code>processExitStatus</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getProcessExitStatus() {
    return processExitStatus;
  }

  /**
   * <p>Setter for the field <code>processExitStatus</code>.</p>
   *
   * @param processExitStatus a {@link java.lang.Integer} object.
   */
  public void setProcessExitStatus(Integer processExitStatus) {
    this.processExitStatus = processExitStatus;
  }

  /**
   * <p>Getter for the field <code>parents</code>.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public Set<Processing> getParents() {
    return parents;
  }

  /**
   * <p>Setter for the field <code>parents</code>.</p>
   *
   * @param parents a {@link java.util.Set} object.
   */
  public void setParents(Set<Processing> parents) {
    this.parents = parents;
  }

  /**
   * <p>Getter for the field <code>children</code>.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public Set<Processing> getChildren() {
    return children;
  }

  /**
   * <p>Setter for the field <code>children</code>.</p>
   *
   * @param children a {@link java.util.Set} object.
   */
  public void setChildren(Set<Processing> children) {
    this.children = children;
  }

  /**
   * <p>isTaskGroup.</p>
   *
   * @return a boolean.
   */
  public boolean isTaskGroup() {
    return taskGroup;
  }

  /**
   * <p>Setter for the field <code>taskGroup</code>.</p>
   *
   * @param taskGroup a boolean.
   */
  public void setTaskGroup(boolean taskGroup) {
    this.taskGroup = taskGroup;
  }

  /**
   * <p>Getter for the field <code>swAccession</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getSwAccession() {
    return swAccession;
  }

  /**
   * <p>Setter for the field <code>swAccession</code>.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   */
  public void setSwAccession(Integer swAccession) {
    this.swAccession = swAccession;
  }

  /**
   * <p>Getter for the field <code>createTimestamp</code>.</p>
   *
   * @return a {@link java.util.Date} object.
   */
  public Date getCreateTimestamp() {
    return createTimestamp;
  }

  /**
   * <p>Setter for the field <code>createTimestamp</code>.</p>
   *
   * @param createTimestamp a {@link java.util.Date} object.
   */
  public void setCreateTimestamp(Date createTimestamp) {
    this.createTimestamp = createTimestamp;
  }

  /**
   * <p>Getter for the field <code>updateTimestamp</code>.</p>
   *
   * @return a {@link java.util.Date} object.
   */
  public Date getUpdateTimestamp() {
    return updateTimestamp;
  }

  /**
   * <p>Setter for the field <code>updateTimestamp</code>.</p>
   *
   * @param updateTimestamp a {@link java.util.Date} object.
   */
  public void setUpdateTimestamp(Date updateTimestamp) {
    this.updateTimestamp = updateTimestamp;
  }

  /**
   * <p>Getter for the field <code>serialVersionUID</code>.</p>
   *
   * @return a long.
   */
  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  /**
   * <p>Getter for the field <code>algorithm</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getAlgorithm() {
    return algorithm;
  }

  /**
   * <p>getJsonEscapeAlgorithm.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getJsonEscapeAlgorithm() {
    return JsonUtil.forJSON(getAlgorithm());
  }

  /**
   * <p>Setter for the field <code>algorithm</code>.</p>
   *
   * @param algorithm a {@link java.lang.String} object.
   */
  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
  }

  /**
   * <p>Getter for the field <code>filePath</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getFilePath() {
    return filePath;
  }

  /**
   * <p>Setter for the field <code>filePath</code>.</p>
   *
   * @param filePath a {@link java.lang.String} object.
   */
  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  /**
   * <p>Getter for the field <code>processingId</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getProcessingId() {
    return processingId;
  }

  /**
   * <p>Setter for the field <code>processingId</code>.</p>
   *
   * @param processingId a {@link java.lang.Integer} object.
   */
  public void setProcessingId(Integer processingId) {
    this.processingId = processingId;
  }

  public ProcessingStatus getStatus() {
    return status;
  }

  public void setStatus(ProcessingStatus status) {
    this.status = status;
  }

  /**
   * <p>getJsonEscapeDescription.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getJsonEscapeDescription() {
    return JsonUtil.forJSON(description);
  }

  /**
   * <p>Getter for the field <code>description</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getDescription() {
    return description;
  }

  /**
   * <p>Setter for the field <code>description</code>.</p>
   *
   * @param description a {@link java.lang.String} object.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * <p>Getter for the field <code>url</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getUrl() {
    return url;
  }

  /**
   * <p>Setter for the field <code>url</code>.</p>
   *
   * @param url a {@link java.lang.String} object.
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * <p>Getter for the field <code>lanes</code>.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public Set<Lane> getLanes() {
    /*
     * Set<Lane> lns = new TreeSet<Lane>(); Set<IUS> setIUS = getIUS();
     * logger.debug("IUS size = " + setIUS.size()); for(IUS i : setIUS){
     * lns.add(i.getLane()); } return lns;
     */
    return lanes;
  }

  /**
   * <p>Setter for the field <code>lanes</code>.</p>
   *
   * @param lanes a {@link java.util.Set} object.
   */
  public void setLanes(Set<Lane> lanes) {
    this.lanes = lanes;
  }

  /**
   * <p>Getter for the field <code>urlLabel</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getUrlLabel() {
    return urlLabel;
  }

  /**
   * <p>Setter for the field <code>urlLabel</code>.</p>
   *
   * @param urlLabel a {@link java.lang.String} object.
   */
  public void setUrlLabel(String urlLabel) {
    this.urlLabel = urlLabel;
  }

  /**
   * <p>Getter for the field <code>version</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getVersion() {
    return version;
  }

  /**
   * <p>Setter for the field <code>version</code>.</p>
   *
   * @param version a {@link java.lang.String} object.
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * <p>Getter for the field <code>files</code>.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  @XmlJavaTypeAdapter(XmlizeFileSet.class)
  public Set<File> getFiles() {
    return files;
  }

  /**
   * <p>Setter for the field <code>files</code>.</p>
   *
   * @param files a {@link java.util.Set} object.
   */
  public void setFiles(Set<File> files) {
    if (this.files == null) {
      this.files = files;
    } else {
      this.files.clear();
      this.files.addAll(files);
    }
  }

  /**
   * <p>Getter for the field <code>workflowRunId</code>.</p>
   *
   * @return a {@link java.lang.Integer} object.
   */
  public Integer getWorkflowRunId() {
    return workflowRunId;
  }

  /**
   * <p>Setter for the field <code>workflowRunId</code>.</p>
   *
   * @param workflowRunId a {@link java.lang.Integer} object.
   */
  public void setWorkflowRunId(Integer workflowRunId) {
    this.workflowRunId = workflowRunId;
  }

  /**
   * <p>Getter for the field <code>owner</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.Registration} object.
   */
  public Registration getOwner() {
    return owner;
  }

  /**
   * <p>Setter for the field <code>owner</code>.</p>
   *
   * @param owner a {@link net.sourceforge.seqware.common.model.Registration} object.
   */
  public void setOwner(Registration owner) {
    this.owner = owner;
  }

  /**
   * <p>Getter for the field <code>workflowRun</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  public WorkflowRun getWorkflowRun() {
    return workflowRun;
  }

  /**
   * <p>Setter for the field <code>workflowRun</code>.</p>
   *
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  public void setWorkflowRun(WorkflowRun workflowRun) {
    this.workflowRun = workflowRun;
  }

  /**
   * <p>Getter for the field <code>isSelected</code>.</p>
   *
   * @return a {@link java.lang.Boolean} object.
   */
  public Boolean getIsSelected() {
    return isSelected;
  }

  /**
   * <p>Setter for the field <code>isSelected</code>.</p>
   *
   * @param isSelected a {@link java.lang.Boolean} object.
   */
  public void setIsSelected(Boolean isSelected) {
    this.isSelected = isSelected;
  }

  /**
   * <p>Getter for the field <code>isHasFile</code>.</p>
   *
   * @return a {@link java.lang.Boolean} object.
   */
  public Boolean getIsHasFile() {
    return isHasFile;
  }

  /**
   * <p>Setter for the field <code>isHasFile</code>.</p>
   *
   * @param isHasFile a {@link java.lang.Boolean} object.
   */
  public void setIsHasFile(Boolean isHasFile) {
    this.isHasFile = isHasFile;
  }

  /**
   * <p>getIUS.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public Set<IUS> getIUS() {
    return ius;
  }

  /**
   * <p>setIUS.</p>
   *
   * @param ius a {@link java.util.Set} object.
   */
  public void setIUS(Set<IUS> ius) {
    this.ius = ius;
  }

  /**
   * <p>Getter for the field <code>studies</code>.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public Set<Study> getStudies() {
    return studies;
  }

  /**
   * <p>Setter for the field <code>studies</code>.</p>
   *
   * @param studies a {@link java.util.Set} object.
   */
  public void setStudies(Set<Study> studies) {
    this.studies = studies;
  }

  /**
   * <p>Getter for the field <code>samples</code>.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public Set<Sample> getSamples() {
    return samples;
  }

  /**
   * <p>Setter for the field <code>samples</code>.</p>
   *
   * @param samples a {@link java.util.Set} object.
   */
  public void setSamples(Set<Sample> samples) {
    this.samples = samples;
  }

  /**
   * <p>Getter for the field <code>sequencerRuns</code>.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public Set<SequencerRun> getSequencerRuns() {
    return sequencerRuns;
  }

  /**
   * <p>Setter for the field <code>sequencerRuns</code>.</p>
   *
   * @param sequencerRuns a {@link java.util.Set} object.
   */
  public void setSequencerRuns(Set<SequencerRun> sequencerRuns) {
    this.sequencerRuns = sequencerRuns;
  }

  /**
   * <p>Getter for the field <code>experiments</code>.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public Set<Experiment> getExperiments() {
    return experiments;
  }

  /**
   * <p>Setter for the field <code>experiments</code>.</p>
   *
   * @param experiments a {@link java.util.Set} object.
   */
  public void setExperiments(Set<Experiment> experiments) {
    this.experiments = experiments;
  }

  /**
   * <p>Getter for the field <code>runStartTimestamp</code>.</p>
   *
   * @return a {@link java.util.Date} object.
   */
  public Date getRunStartTimestamp() {
    return runStartTimestamp;
  }

  /**
   * <p>Setter for the field <code>runStartTimestamp</code>.</p>
   *
   * @param runStartTimestamp a {@link java.util.Date} object.
   */
  public void setRunStartTimestamp(Date runStartTimestamp) {
    this.runStartTimestamp = runStartTimestamp;
  }

  /**
   * <p>Getter for the field <code>runStopTimestamp</code>.</p>
   *
   * @return a {@link java.util.Date} object.
   */
  public Date getRunStopTimestamp() {
    return runStopTimestamp;
  }

  /**
   * <p>Setter for the field <code>runStopTimestamp</code>.</p>
   *
   * @param runStopTimestamp a {@link java.util.Date} object.
   */
  public void setRunStopTimestamp(Date runStopTimestamp) {
    this.runStopTimestamp = runStopTimestamp;
  }

  /**
   * <p>Getter for the field <code>parameters</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getParameters() {
    return parameters;
  }

  /**
   * <p>Setter for the field <code>parameters</code>.</p>
   *
   * @param parameters a {@link java.lang.String} object.
   */
  public void setParameters(String parameters) {
    this.parameters = parameters;
  }

  /**
   * <p>Getter for the field <code>stdout</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  @XmlJavaTypeAdapter(XmlizeXML.class)
  public String getStdout() {
    return stdout;
  }

  /**
   * <p>Setter for the field <code>stdout</code>.</p>
   *
   * @param stdout a {@link java.lang.String} object.
   */
  public void setStdout(String stdout) {
    this.stdout = stdout;
  }

  /**
   * <p>Getter for the field <code>stderr</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getStderr() {
    return stderr;
  }

  /**
   * <p>Setter for the field <code>stderr</code>.</p>
   *
   * @param stderr a {@link java.lang.String} object.
   */
  @XmlJavaTypeAdapter(XmlizeXML.class)
  public void setStderr(String stderr) {
    this.stderr = stderr;
  }

  /**
   * <p>Getter for the field <code>workflowRunByAncestorWorkflowRunId</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  public WorkflowRun getWorkflowRunByAncestorWorkflowRunId() {
    return workflowRunByAncestorWorkflowRunId;
  }

  /**
   * <p>Setter for the field <code>workflowRunByAncestorWorkflowRunId</code>.</p>
   *
   * @param workflowRunByAncestorWorkflowRunId a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  public void setWorkflowRunByAncestorWorkflowRunId(WorkflowRun workflowRunByAncestorWorkflowRunId) {
    this.workflowRunByAncestorWorkflowRunId = workflowRunByAncestorWorkflowRunId;
  }

  /**
   * <p>Getter for the field <code>processingAttributes</code>.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  @XmlElementWrapper(name = "ProcessingAttributes", nillable = true)
  @XmlElement(name = "ProcessingAttribute")
  public Set<ProcessingAttribute> getProcessingAttributes() {
    return processingAttributes;
  }

  /**
   * <p>Setter for the field <code>processingAttributes</code>.</p>
   *
   * @param processingAttributes a {@link java.util.Set} object.
   */
  public void setProcessingAttributes(Set<ProcessingAttribute> processingAttributes) {
    this.processingAttributes = processingAttributes;
  }

  /**
   * <p>resetCompletedChildren.</p>
   */
  public void resetCompletedChildren() {
    Set<Processing> res = new TreeSet<Processing>();
    Set<Processing> all = this.getChildren();

    // get processing with workflow run has not status equal completed
    for (Processing pr : all) {
      WorkflowRun wr = pr.getWorkflowRun();
      if (wr == null || wr.getStatus() == WorkflowRunStatus.completed) {
        res.add(pr);
      }
    }
    this.setChildren(res);

  }

  /**
   * <p>resetRunningChildren.</p>
   */
  public void resetRunningChildren() {
    Set<Processing> res = new TreeSet<Processing>();
    Set<Processing> all = this.getChildren();

    // get processing with workflow run has not status equal completed
    for (Processing pr : all) {
      WorkflowRun wr = pr.getWorkflowRun();
      if (wr == null || wr.getStatus() != WorkflowRunStatus.completed) {
        res.add(pr);
      }
    }
    this.setChildren(res);
  }

  /**
   * <p>clone.</p>
   *
   * @param newP a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   * @throws org.restlet.resource.ResourceException if any.
   * @throws java.sql.SQLException if any.
   */
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

  /**
   * <p>cloneFromDB.</p>
   *
   * @param processingId a int.
   * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @throws org.restlet.resource.ResourceException if any.
   * @throws java.sql.SQLException if any.
   */
  public static Processing cloneFromDB(int processingId) throws ResourceException, SQLException {
    Processing updatedProcessing = null;
    ResultSet rs = null;
    try {
      rs = DBAccess.get().executeQuery("SELECT * FROM processing WHERE processing_id=" + processingId);
      int ownerId;
      int ancestorWorkflowRunId;
      int workflowRunId;
      if (rs.next()) {
        updatedProcessing = new Processing();
        updatedProcessing.setProcessingId(rs.getInt("processing_id"));
        updatedProcessing.setAlgorithm(rs.getString("algorithm"));
        updatedProcessing.setStatus(ProcessingStatus.valueOf(rs.getString("status")));
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
        throw new ResourceException(org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST, "The Processing does not exist");
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
      DbUtils.closeQuietly(rs);
      DBAccess.close();
    }
    return updatedProcessing;
  }

  /**
   * <p>cloneToHibernate.</p>
   *
   * @param newP a {@link net.sourceforge.seqware.common.model.Processing} object.
   * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
   */
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

  /** {@inheritDoc} */
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
