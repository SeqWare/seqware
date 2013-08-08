package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import net.sourceforge.seqware.common.business.IUSService;
import net.sourceforge.seqware.common.business.LaneService;
import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.business.RegistrationService;
import net.sourceforge.seqware.common.business.SampleService;
import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.factory.DBAccess;
import net.sourceforge.seqware.common.model.adapters.XmlizeXML;
import net.sourceforge.seqware.common.security.PermissionsAware;
import net.sourceforge.seqware.common.util.jsontools.JsonUtil;
import org.apache.commons.dbutils.DbUtils;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

/**
 * <p>WorkflowRun class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowRun implements Serializable, Comparable<WorkflowRun>, PermissionsAware {
  
  private static final long serialVersionUID = 1L;
  private Integer workflowRunId;
  private Workflow workflow;
  private Integer swAccession;
  private String name;
  private Date createTimestamp;
  private Date updateTimestamp;
  private Boolean isSelected = false;
  private Registration owner;
  private SortedSet<Processing> processings;
  private SortedSet<Processing> offspringProcessings;
  private SortedSet<Sample> samples;
  // many-to-many link
  private SortedSet<IUS> ius;
  private SortedSet<Lane> lanes;
  private SortedSet<ShareWorkflowRun> sharedWorkflowRuns;
  private SortedSet<WorkflowRunParam> workflowRunParams;
  private String html;
  private Boolean isHasFile = false;
  // additional fields
  private WorkflowRunStatus status;
  private String statusCmd;
  private String seqwareRevision;
  private String host;
  private String currentWorkingDir;
  private String userName;
  private String command;
  private String template;
  private String dax;
  private String iniFile;
  private String stdErr;
  private String stdOut;
  private Set<WorkflowRunAttribute> workflowRunAttributes = new TreeSet<WorkflowRunAttribute>();
  private String workflowEngine;
  private Set<Integer> inputFileAccessions = new HashSet<Integer>();
  
  // artificial fields for SEQWARE-1134, we will need to populate these artificially
  // this is an ugly hack, need to get a better solution 
  private Integer workflowAccession;
  private String ownerUserName;
  
  /**
   * <p>Constructor for WorkflowRun.</p>
   */
  public WorkflowRun() {
    super();
  }

  /**
   * <p>compareTo.</p>
   *
   * @param that a {@link net.sourceforge.seqware.common.model.Workflow} object.
   * @return a int.
   */
  public int compareTo(Workflow that) {
    if (that == null) {
      return -1;
    }

    if (that.getSwAccession() == this.getSwAccession()) // when both names are
    // null
    {
      return 0;
    }

    if (that.getSwAccession() == null) {
      return -1; // when only the other name is null
    }
    return (that.getSwAccession().compareTo(this.getSwAccession()));
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return new ToStringBuilder(this).append("swAccession", getSwAccession()).toString();
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(Object other) {
    if ((this == other)) {
      return true;
    }
    if (!(other instanceof WorkflowRun)) {
      return false;
    }
    WorkflowRun castOther = (WorkflowRun) other;
    return new EqualsBuilder().append(this.getSwAccession(), castOther.getSwAccession()).isEquals();
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(getSwAccession()).toHashCode();
  }

  /**
   * <p>Getter for the field <code>template</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getTemplate() {
    return template;
  }

  /**
   * <p>Setter for the field <code>template</code>.</p>
   *
   * @param template a {@link java.lang.String} object.
   */
  public void setTemplate(String template) {
    this.template = template;
  }

  /**
   * <p>Getter for the field <code>command</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getCommand() {
    return command;
  }

  /**
   * <p>Setter for the field <code>command</code>.</p>
   *
   * @param command a {@link java.lang.String} object.
   */
  public void setCommand(String command) {
    this.command = command;
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
   * <p>Getter for the field <code>workflow</code>.</p>
   *
   * @return a {@link net.sourceforge.seqware.common.model.Workflow} object.
   */
  public Workflow getWorkflow() {
    return workflow;
  }

  /**
   * <p>Setter for the field <code>workflow</code>.</p>
   *
   * @param workflow a {@link net.sourceforge.seqware.common.model.Workflow} object.
   */
  public void setWorkflow(Workflow workflow) {
    this.workflow = workflow;
    if (workflow != null && workflow.getSwAccession() != null){
        this.setWorkflowAccession(workflow.getSwAccession());
    }
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
   * <p>Getter for the field <code>name</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getName() {
    return name;
  }

  /**
   * <p>getJsonEscapeName.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getJsonEscapeName() {
    return JsonUtil.forJSON(name);
  }

  /**
   * <p>Setter for the field <code>name</code>.</p>
   *
   * @param name a {@link java.lang.String} object.
   */
  public void setName(String name) {
    this.name = name;
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
    if (owner != null && owner.getEmailAddress() != null){
        this.setOwnerUserName(owner.getEmailAddress());
    }
  }

  /**
   * <p>Getter for the field <code>processings</code>.</p>
   *
   * @return a {@link java.util.SortedSet} object.
   */
  public SortedSet<Processing> getProcessings() {
    return processings;
  }

  /**
   * <p>Setter for the field <code>processings</code>.</p>
   *
   * @param processings a {@link java.util.SortedSet} object.
   */
  public void setProcessings(SortedSet<Processing> processings) {
    this.processings = processings;
  }

  /**
   * <p>Getter for the field <code>offspringProcessings</code>.</p>
   *
   * @return a {@link java.util.SortedSet} object.
   */
  public SortedSet<Processing> getOffspringProcessings() {
    return offspringProcessings;
  }

  /**
   * <p>Setter for the field <code>offspringProcessings</code>.</p>
   *
   * @param offspringProcessings a {@link java.util.SortedSet} object.
   */
  public void setOffspringProcessings(SortedSet<Processing> offspringProcessings) {
    this.offspringProcessings = offspringProcessings;
  }

  /**
   * <p>Getter for the field <code>samples</code>.</p>
   *
   * @return a {@link java.util.SortedSet} object.
   */
  public SortedSet<Sample> getSamples() {
    return samples;
  }

  /**
   * <p>Setter for the field <code>samples</code>.</p>
   *
   * @param samples a {@link java.util.SortedSet} object.
   */
  public void setSamples(SortedSet<Sample> samples) {
    this.samples = samples;
  }

  /**
   * <p>Getter for the field <code>sharedWorkflowRuns</code>.</p>
   *
   * @return a {@link java.util.SortedSet} object.
   */
  public SortedSet<ShareWorkflowRun> getSharedWorkflowRuns() {
    return sharedWorkflowRuns;
  }

  /**
   * <p>Setter for the field <code>sharedWorkflowRuns</code>.</p>
   *
   * @param sharedWorkflowRuns a {@link java.util.SortedSet} object.
   */
  public void setSharedWorkflowRuns(SortedSet<ShareWorkflowRun> sharedWorkflowRuns) {
    if (sharedWorkflowRuns == null) {
      this.sharedWorkflowRuns = sharedWorkflowRuns;
    } else {
      this.sharedWorkflowRuns.clear();
      this.sharedWorkflowRuns.addAll(sharedWorkflowRuns);
    }
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
   * <p>Getter for the field <code>html</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getHtml() {
    return html;
  }

  /**
   * <p>Setter for the field <code>html</code>.</p>
   *
   * @param html a {@link java.lang.String} object.
   */
  public void setHtml(String html) {
    this.html = html;
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
   * <p>Getter for the field <code>status</code>.</p>
   *
   * @return the status of the workflow run
   */
  public WorkflowRunStatus getStatus() {
    return status;
  }

  /**
   * <p>Setter for the field <code>status</code>.</p>
   *
   * @param status the status of the workflow run
   */
  public void setStatus(WorkflowRunStatus status) {
    this.status = status;
  }

  /**
   * <p>Getter for the field <code>statusCmd</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getStatusCmd() {
    return statusCmd;
  }

  /**
   * <p>Setter for the field <code>statusCmd</code>.</p>
   *
   * @param statusCmd a {@link java.lang.String} object.
   */
  public void setStatusCmd(String statusCmd) {
    this.statusCmd = statusCmd;
  }

  /**
   * <p>Getter for the field <code>seqwareRevision</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getSeqwareRevision() {
    return seqwareRevision;
  }

  /**
   * <p>Setter for the field <code>seqwareRevision</code>.</p>
   *
   * @param seqwareRevision a {@link java.lang.String} object.
   */
  public void setSeqwareRevision(String seqwareRevision) {
    this.seqwareRevision = seqwareRevision;
  }

  /**
   * <p>Getter for the field <code>host</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getHost() {
    return host;
  }

  /**
   * <p>Setter for the field <code>host</code>.</p>
   *
   * @param host a {@link java.lang.String} object.
   */
  public void setHost(String host) {
    this.host = host;
  }

  /**
   * <p>Getter for the field <code>currentWorkingDir</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getCurrentWorkingDir() {
    return currentWorkingDir;
  }

  /**
   * <p>Setter for the field <code>currentWorkingDir</code>.</p>
   *
   * @param currentWorkingDir a {@link java.lang.String} object.
   */
  public void setCurrentWorkingDir(String currentWorkingDir) {
    this.currentWorkingDir = currentWorkingDir;
  }

  /**
   * <p>Getter for the field <code>userName</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getUserName() {
    return userName;
  }

  /**
   * <p>Setter for the field <code>userName</code>.</p>
   *
   * @param username a {@link java.lang.String} object.
   */
  public void setUserName(String username) {
    this.userName = username;
  }

  /**
   * <p>Getter for the field <code>workflowRunParams</code>.</p>
   *
   * @return a {@link java.util.SortedSet} object.
   */
  public SortedSet<WorkflowRunParam> getWorkflowRunParams() {
    return workflowRunParams;
  }

  /**
   * <p>Setter for the field <code>workflowRunParams</code>.</p>
   *
   * @param workflowRunParams a {@link java.util.SortedSet} object.
   */
  public void setWorkflowRunParams(SortedSet<WorkflowRunParam> workflowRunParams) {
    this.workflowRunParams = workflowRunParams;
  }

  // @XmlJavaTypeAdapter(XmlizeIUSSortedSet.class)
  /**
   * <p>Getter for the field <code>ius</code>.</p>
   *
   * @return a {@link java.util.SortedSet} object.
   */
  public SortedSet<IUS> getIus() {
    return ius;
  }

  /**
   * <p>Setter for the field <code>ius</code>.</p>
   *
   * @param ius a {@link java.util.SortedSet} object.
   */
  public void setIus(SortedSet<IUS> ius) {
    this.ius = ius;
  }

  /**
   * <p>Getter for the field <code>dax</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  @XmlJavaTypeAdapter(XmlizeXML.class)
  public String getDax() {
    return dax;
  }

  /**
   * <p>Setter for the field <code>dax</code>.</p>
   *
   * @param dax a {@link java.lang.String} object.
   */
  public void setDax(String dax) {
    this.dax = dax;
  }

  /**
   * <p>Getter for the field <code>iniFile</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getIniFile() {
    return iniFile;
  }

  /**
   * <p>Setter for the field <code>iniFile</code>.</p>
   *
   * @param iniFile a {@link java.lang.String} object.
   */
  public void setIniFile(String iniFile) {
    this.iniFile = iniFile;
  }

  // @XmlJavaTypeAdapter(XmlizeLaneSortedSet.class)
  /**
   * <p>Getter for the field <code>lanes</code>.</p>
   *
   * @return a {@link java.util.SortedSet} object.
   */
  public SortedSet<Lane> getLanes() {
    return lanes;
  }

  /**
   * <p>Setter for the field <code>lanes</code>.</p>
   *
   * @param lanes a {@link java.util.SortedSet} object.
   */
  public void setLanes(SortedSet<Lane> lanes) {
    this.lanes = lanes;
  }

    /**
     * <p>Getter for the field <code>stdErr</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStdErr() {
        return stdErr;
    }

    /**
     * <p>Setter for the field <code>stdErr</code>.</p>
     *
     * @param stdErr a {@link java.lang.String} object.
     */
    public void setStdErr(String stdErr) {
        this.stdErr = stdErr;
    }

    /**
     * <p>Getter for the field <code>stdOut</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getStdOut() {
        return stdOut;
    }

    /**
     * <p>Setter for the field <code>stdOut</code>.</p>
     *
     * @param stdOut a {@link java.lang.String} object.
     */
    public void setStdOut(String stdOut) {
        this.stdOut = stdOut;
    }
  
  

  /**
   * <p>cloneToHibernate.</p>
   *
   * @param newWR a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  public static WorkflowRun cloneToHibernate(WorkflowRun newWR) {
    WorkflowRunService wrs = BeanFactory.getWorkflowRunServiceBean();
    WorkflowRun wr = wrs.findByID(newWR.getWorkflowRunId());
    wr.setCommand(newWR.getCommand());
    wr.setCurrentWorkingDir(newWR.getCurrentWorkingDir());
    wr.setDax(newWR.getDax());
    wr.setHost(newWR.getHost());
    wr.setHtml(newWR.getHtml());
    wr.setIniFile(newWR.getIniFile());
    wr.setIsHasFile(newWR.getIsHasFile());
    wr.setIsSelected(newWR.getIsSelected());
    wr.setName(newWR.getName());
    wr.setSeqwareRevision(newWR.getSeqwareRevision());
    wr.setStatus(newWR.getStatus());
    wr.setStatusCmd(newWR.getStatusCmd());
    wr.setTemplate(newWR.getTemplate());
    wr.setUpdateTimestamp(newWR.getUpdateTimestamp());
    wr.setUserName(newWR.getUserName());
    wr.setStdErr(newWR.getStdErr());
    wr.setStdOut(newWR.getStdOut());

    Registration owner = newWR.getOwner();
    if (owner != null) {
      RegistrationService rs = BeanFactory.getRegistrationServiceBean();
      Registration o = rs.findByEmailAddressAndPassword(owner.getEmailAddress(), owner.getPassword());
      if (o != null) {
        wr.setOwner(o);
      }
    }

    SortedSet<Lane> newLanes = newWR.getLanes();
    SortedSet<IUS> iuses = newWR.getIus();
    SortedSet<Processing> offProcessings = newWR.getOffspringProcessings();
    SortedSet<Processing> newProcessings = newWR.getProcessings();
    SortedSet<Sample> newSamples = newWR.getSamples();
    SortedSet<ShareWorkflowRun> sharedWRs = newWR.getSharedWorkflowRuns();
    SortedSet<WorkflowRunParam> wrParams = newWR.getWorkflowRunParams();

    if (newLanes != null && !newLanes.isEmpty()) {
      LaneService ls = BeanFactory.getLaneServiceBean();
      for (Lane lane : newLanes) {
        wr.getLanes().add(ls.findByID(lane.getLaneId()));
      }
    }
    if (iuses != null && !iuses.isEmpty()) {
      IUSService is = BeanFactory.getIUSServiceBean();
      for (IUS i : iuses) {
        wr.getIus().add(is.findByID(i.getIusId()));
      }
    }
    if (offProcessings != null && !offProcessings.isEmpty()) {
      ProcessingService ps = BeanFactory.getProcessingServiceBean();
      for (Processing p : offProcessings) {
        wr.getOffspringProcessings().add(ps.findByID(p.getProcessingId()));
      }
    }
    if (newProcessings != null && !newProcessings.isEmpty()) {
      ProcessingService ps = BeanFactory.getProcessingServiceBean();
      for (Processing p : newProcessings) {
        wr.getProcessings().add(ps.findByID(p.getProcessingId()));
      }
    }
    if (newSamples != null && !newSamples.isEmpty()) {
      SampleService ss = BeanFactory.getSampleServiceBean();
      for (Sample s : newSamples) {
        wr.getSamples().add(ss.findByID(s.getSampleId()));
      }
    }

    if (sharedWRs != null && !sharedWRs.isEmpty()) {
      throw new NotImplementedException("Adding ShareWorkflowRuns is not implemented");
    }
    if (wrParams != null && !wrParams.isEmpty()) {
      throw new NotImplementedException("Adding WorkflowRunParams is not implemented");
    }
    return wr;
  }

  /** {@inheritDoc} */
  @Override
  public int compareTo(WorkflowRun that) {
    // TODO Auto-generated method stub
    if (that == null) {
      return -1;
    }

    if (that.getSwAccession() == this.getSwAccession()) // when both names are
    // null
    {
      return 0;
    }

    if (that.getSwAccession() == null) {
      return -1; // when only the other name is null
    }
    if (this.getSwAccession() == null) {
      return 1;
    }

    return (that.getSwAccession().compareTo(this.getSwAccession()));
  }

  /**
   * <p>cloneFromDB.</p>
   *
   * @param wrId a int.
   * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @throws java.sql.SQLException if any.
   */
  public static WorkflowRun cloneFromDB(int wrId) throws SQLException {
    WorkflowRun wr = null;
    ResultSet rs = null;
    try {
      rs = DBAccess.get().executeQuery("SELECT * FROM workflow_run WHERE workflow_run_id=" + wrId);
      if (rs.next()) {
        wr = new WorkflowRun();
        wr.setWorkflowRunId(rs.getInt("workflow_run_id"));
        wr.setName(rs.getString("name"));
        wr.setIniFile(rs.getString("ini_file"));
        wr.setCommand(rs.getString("cmd"));
        wr.setTemplate(rs.getString("workflow_template"));
        wr.setDax(rs.getString("dax"));
        wr.setStatus(WorkflowRunStatus.valueOf(rs.getString("status")));
        wr.setStatusCmd(rs.getString("status_cmd"));
        wr.setSeqwareRevision(rs.getString("seqware_revision"));
        wr.setHost(rs.getString("host"));
        wr.setCurrentWorkingDir(rs.getString("current_working_dir"));
        wr.setUserName(rs.getString("username"));
        wr.setCreateTimestamp(rs.getTimestamp("create_tstmp"));
        wr.setUpdateTimestamp(rs.getTimestamp("update_tstmp"));
        wr.setSwAccession(rs.getInt("sw_accession"));
        wr.setStdErr(rs.getString("stderr"));
        wr.setStdOut(rs.getString("stdout"));

        // owner
        
        // workflowid

      }
    } finally {
      DbUtils.closeQuietly(rs);
      DBAccess.close();
    }
    return wr;
  }

  /** {@inheritDoc} */
  @Override
  public boolean givesPermission(Registration registration) {
    boolean hasPermission = true;
    if (workflow != null) {
      workflow.givesPermission(registration);
      if (ius != null) {
        for (IUS i : ius) {
          i.givesPermission(registration);
        }
      }
      if (lanes != null) {
        for (Lane l : lanes) {
          l.givesPermission(registration);
        }
      }
    } else {// orphaned WorkflowRun
      if (registration.equals(this.owner) || registration.isLIMSAdmin()) {
        Logger.getLogger(WorkflowRun.class).warn("Modifying Orphan WorkflowRun: " + this.getCommand());
        hasPermission = true;
      } else {
        Logger.getLogger(WorkflowRun.class).warn("Not modifying Orphan WorkflowRun: " + this.getCommand());
        hasPermission = false;
      }
    }
    if (!hasPermission) {
      Logger.getLogger(WorkflowRun.class).info("WorkflowRun does not give permission");
      throw new SecurityException("User " + registration.getEmailAddress() + " does not have permission to modify "
          + this.getCommand());
    }
    return hasPermission;
  }

  /**
   * <p>Getter for the field <code>workflowRunAttributes</code>.</p>
   *
   * @return a {@link java.util.Set} object.
   */
  public Set<WorkflowRunAttribute> getWorkflowRunAttributes() {
    return workflowRunAttributes;
  }

  /**
   * <p>Setter for the field <code>workflowRunAttributes</code>.</p>
   *
   * @param workflowRunAttributes a {@link java.util.Set} object.
   */
  public void setWorkflowRunAttributes(Set<WorkflowRunAttribute> workflowRunAttributes) {
    this.workflowRunAttributes = workflowRunAttributes;
  }

    public String getWorkflowEngine() {
        return workflowEngine;
    }

    public void setWorkflowEngine(String workflowEngine) {
        this.workflowEngine = workflowEngine;
    }

    public Integer getWorkflowAccession() {
        return workflowAccession;
    }

    public void setWorkflowAccession(Integer workflowAccession) {
        this.workflowAccession = workflowAccession;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }
  

    /**
     * @return the parentAccessions
     */
    public Set<Integer> getInputFileAccessions() {
        return inputFileAccessions;
    }

    /**
     * @param parentAccessions the parentAccessions to set
     */
    public void setInputFileAccessions(Set<Integer> inputFiles) {
        this.inputFileAccessions = inputFiles;
    }
  
    

}
