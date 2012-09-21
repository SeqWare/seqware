package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

public class WorkflowRun implements Serializable, Comparable<WorkflowRun>, PermissionsAware {

  public static final String RUNNING = "running";
  public static final String FINISHED = "completed";
  public static final String FAILED = "failed";

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
  // addition fileds
  private String status;
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
  public WorkflowRun() {
    super();
  }

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

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("swAccession", getSwAccession()).toString();
  }

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

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(getSwAccession()).toHashCode();
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public Integer getWorkflowRunId() {
    return workflowRunId;
  }

  public void setWorkflowRunId(Integer workflowRunId) {
    this.workflowRunId = workflowRunId;
  }

  public Workflow getWorkflow() {
    return workflow;
  }

  public void setWorkflow(Workflow workflow) {
    this.workflow = workflow;
  }

  public Integer getSwAccession() {
    return swAccession;
  }

  public void setSwAccession(Integer swAccession) {
    this.swAccession = swAccession;
  }

  public String getName() {
    return name;
  }

  public String getJsonEscapeName() {
    return JsonUtil.forJSON(name);
  }

  public void setName(String name) {
    this.name = name;
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

  public Registration getOwner() {
    return owner;
  }

  public void setOwner(Registration owner) {
    this.owner = owner;
  }

  public SortedSet<Processing> getProcessings() {
    return processings;
  }

  public void setProcessings(SortedSet<Processing> processings) {
    this.processings = processings;
  }

  public SortedSet<Processing> getOffspringProcessings() {
    return offspringProcessings;
  }

  public void setOffspringProcessings(SortedSet<Processing> offspringProcessings) {
    this.offspringProcessings = offspringProcessings;
  }

  public SortedSet<Sample> getSamples() {
    return samples;
  }

  public void setSamples(SortedSet<Sample> samples) {
    this.samples = samples;
  }

  public SortedSet<ShareWorkflowRun> getSharedWorkflowRuns() {
    return sharedWorkflowRuns;
  }

  public void setSharedWorkflowRuns(SortedSet<ShareWorkflowRun> sharedWorkflowRuns) {
    if (sharedWorkflowRuns == null) {
      this.sharedWorkflowRuns = sharedWorkflowRuns;
    } else {
      this.sharedWorkflowRuns.clear();
      this.sharedWorkflowRuns.addAll(sharedWorkflowRuns);
    }
  }

  public Boolean getIsSelected() {
    return isSelected;
  }

  public void setIsSelected(Boolean isSelected) {
    this.isSelected = isSelected;
  }

  public String getHtml() {
    return html;
  }

  public void setHtml(String html) {
    this.html = html;
  }

  public Boolean getIsHasFile() {
    return isHasFile;
  }

  public void setIsHasFile(Boolean isHasFile) {
    this.isHasFile = isHasFile;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getStatusCmd() {
    return statusCmd;
  }

  public void setStatusCmd(String statusCmd) {
    this.statusCmd = statusCmd;
  }

  public String getSeqwareRevision() {
    return seqwareRevision;
  }

  public void setSeqwareRevision(String seqwareRevision) {
    this.seqwareRevision = seqwareRevision;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getCurrentWorkingDir() {
    return currentWorkingDir;
  }

  public void setCurrentWorkingDir(String currentWorkingDir) {
    this.currentWorkingDir = currentWorkingDir;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String username) {
    this.userName = username;
  }

  public SortedSet<WorkflowRunParam> getWorkflowRunParams() {
    return workflowRunParams;
  }

  public void setWorkflowRunParams(SortedSet<WorkflowRunParam> workflowRunParams) {
    this.workflowRunParams = workflowRunParams;
  }

  // @XmlJavaTypeAdapter(XmlizeIUSSortedSet.class)
  public SortedSet<IUS> getIus() {
    return ius;
  }

  public void setIus(SortedSet<IUS> ius) {
    this.ius = ius;
  }

  @XmlJavaTypeAdapter(XmlizeXML.class)
  public String getDax() {
    return dax;
  }

  public void setDax(String dax) {
    this.dax = dax;
  }

  public String getIniFile() {
    return iniFile;
  }

  public void setIniFile(String iniFile) {
    this.iniFile = iniFile;
  }

  // @XmlJavaTypeAdapter(XmlizeLaneSortedSet.class)
  public SortedSet<Lane> getLanes() {
    return lanes;
  }

  public void setLanes(SortedSet<Lane> lanes) {
    this.lanes = lanes;
  }

    public String getStdErr() {
        return stdErr;
    }

    public void setStdErr(String stdErr) {
        this.stdErr = stdErr;
    }

    public String getStdOut() {
        return stdOut;
    }

    public void setStdOut(String stdOut) {
        this.stdOut = stdOut;
    }
  
  

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

  public static WorkflowRun cloneFromDB(int wrId) throws SQLException {
    WorkflowRun wr = null;
    try {
      ResultSet rs = DBAccess.get().executeQuery("SELECT * FROM workflow_run WHERE workflow_run_id=" + wrId);
      if (rs.next()) {
        wr = new WorkflowRun();
        wr.setWorkflowRunId(rs.getInt("workflow_run_id"));
        wr.setName(rs.getString("name"));
        wr.setIniFile(rs.getString("ini_file"));
        wr.setCommand(rs.getString("cmd"));
        wr.setTemplate(rs.getString("workflow_template"));
        wr.setDax(rs.getString("dax"));
        wr.setStatus(rs.getString("status"));
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
      DBAccess.close();
    }
    return wr;
  }

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

  public Set<WorkflowRunAttribute> getWorkflowRunAttributes() {
    return workflowRunAttributes;
  }

  public void setWorkflowRunAttributes(Set<WorkflowRunAttribute> workflowRunAttributes) {
    this.workflowRunAttributes = workflowRunAttributes;
  }

}
