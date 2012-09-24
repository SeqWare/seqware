package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import net.sourceforge.seqware.common.security.PermissionsAware;
import net.sourceforge.seqware.common.util.jsontools.JsonUtil;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

public class Workflow implements Serializable, Comparable<Workflow>, PermissionsAware {
  /**
   * LEFT OFF WITH: this needs to be finished
   */
  private static final long serialVersionUID = 1L;
  private Integer workflowId;
  private Integer swAccession;
  private String name;
  private String description;
  private String inputAlgorithm;
  private String version;
  private String seqwareVersion;
  private String baseIniFile;
  private String cwd;
  private String command;
  private String template;
  private String host;
  private String username;
  private String permanentBundleLocation;

  private Date createTimestamp;
  private Date updateTimestamp;

  private boolean isPrivate;
  private boolean isPublic;

  private Registration owner;

  private SortedSet<WorkflowRun> workflowRuns;
  private SortedSet<WorkflowParam> workflowParams;
  private Logger logger;
  private Set<WorkflowAttribute> workflowAttributes = new TreeSet<WorkflowAttribute>();

  public Workflow() {
    super();
    logger = Logger.getLogger(Workflow.class);
  }

  @Override
  public int compareTo(Workflow that) {
    if (that == null)
      return -1;

    if (that.getSwAccession() == this.getSwAccession()) // when both names are
                                                        // null
      return 0;

    if (that.getSwAccession() == null)
      return -1; // when only the other name is null

    return (that.getSwAccession().compareTo(this.getSwAccession()));
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("swAccession", getSwAccession()).toString();
  }

  @Override
  public boolean equals(Object other) {
    if ((this == other))
      return true;
    if (!(other instanceof Workflow))
      return false;
    Workflow castOther = (Workflow) other;
    return new EqualsBuilder().append(this.getSwAccession(), castOther.getSwAccession()).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(getSwAccession()).toHashCode();
  }

  public String getCwd() {
    return cwd;
  }

  public void setCwd(String cwd) {
    this.cwd = cwd;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public Integer getWorkflowId() {
    return workflowId;
  }

  public void setWorkflowId(Integer workflowId) {
    this.workflowId = workflowId;
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

  public String getJsonEscapeDescription() {
    return JsonUtil.forJSON(description);
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getInputAlgorithm() {
    return inputAlgorithm;
  }

  public void setInputAlgorithm(String inputAlgorithm) {
    this.inputAlgorithm = inputAlgorithm;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getSeqwareVersion() {
    return seqwareVersion;
  }

  public void setSeqwareVersion(String seqwareVersion) {
    this.seqwareVersion = seqwareVersion;
  }

  public String getBaseIniFile() {
    return baseIniFile;
  }

  public void setBaseIniFile(String baseIniFile) {
    this.baseIniFile = baseIniFile;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
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

  public boolean isPrivate() {
    return isPrivate;
  }

  public boolean getPrivate() {
    return isPrivate;
  }

  public void setPrivate(boolean isPrivate) {
    this.isPrivate = isPrivate;
  }

  public boolean isPublic() {
    return isPublic;
  }

  public boolean getPublic() {
    return isPublic;
  }

  public void setPublic(boolean isPublic) {
    this.isPublic = isPublic;
  }

  public Registration getOwner() {
    return owner;
  }

  public void setOwner(Registration owner) {
    this.owner = owner;
  }

  public SortedSet<WorkflowRun> getWorkflowRuns() {
    return workflowRuns;
  }

  public void setWorkflowRuns(SortedSet<WorkflowRun> workflowRuns) {
    this.workflowRuns = workflowRuns;
  }

  public SortedSet<WorkflowParam> getWorkflowParams() {
    return workflowParams;
  }

  public SortedSet<WorkflowParam> getVisibleWorkflowParams() {
    if (workflowParams == null)
      return null;

    SortedSet<WorkflowParam> visibleParams = new TreeSet<WorkflowParam>();
    for (WorkflowParam workflowParam : workflowParams) {
      if (workflowParam.getDisplay() != null && workflowParam.getDisplay() && !"file".equals(workflowParam.getType())) {
        visibleParams.add(workflowParam);
      }
    }
    return visibleParams;
  }

  public SortedSet<WorkflowParam> getVisibleWorkflowParamsWithDifferentValue() {
    if (workflowParams == null)
      return null;

    SortedSet<WorkflowParam> visibleParamsWDV = getVisibleWorkflowParams();

    SortedSet<WorkflowParam> res = new TreeSet<WorkflowParam>();

    for (WorkflowParam workflowParam : visibleParamsWDV) {
      String defaultValue = workflowParam.getDefaultValue();

      if (defaultValue != null) {
        // logger.debug("New param");
        // logger.debug("Def value = " + defaultValue);
        SortedSet<WorkflowParamValue> differentValues = new TreeSet<WorkflowParamValue>();
        SortedSet<WorkflowParamValue> values = workflowParam.getValues();
        for (WorkflowParamValue workflowParamValue : values) {
          // System.out.print("value = " + workflowParamValue.getValue());
          if (defaultValue.equals(workflowParamValue.getValue())) {
            logger.debug("Set Default value = " + workflowParamValue.getDisplayName());
            workflowParam.setDisplayName(workflowParamValue.getDisplayName());
          } else {
            logger.debug(" -> Add value!");
            differentValues.add(workflowParamValue);
          }
        }
        workflowParam.setValues(differentValues);
        res.add(workflowParam);

      }
    }
    return res;
  }

  public SortedSet<WorkflowParam> getWorkflowParamsWithDifferentFileMetaType() {
    // return getVisibleWorkflowParams();
    if (workflowParams == null)
      return null;

    SortedSet<WorkflowParam> paramsWithDifFMT = new TreeSet<WorkflowParam>();

    // SortedSet<WorkflowParam> params = getVisibleWorkflowParams();
    SortedSet<WorkflowParam> params = workflowParams;

    for (WorkflowParam param : params) {
      // boolean isAdd = true;
      String type = param.getType();
      String paramFMT = param.getFileMetaType();

      // logger.debug("type = " + type + "; meta type = " + paramFMT);

      if (paramFMT != null && !"".equals(paramFMT) && type != null && "file".equals(type)) {
        /*
         * for(WorkflowParam paramWithDifFMT: paramsWithDifFMT){
         * if(paramFMT.equals(paramWithDifFMT.getFileMetaType())){ isAdd =
         * false; break; } }
         * 
         * if(isAdd){ paramsWithDifFMT.add(param); }
         */
        logger.debug("Add this param");
        paramsWithDifFMT.add(param);
      }
    }
    return paramsWithDifFMT;
  }

  public boolean isLaunch() {
    boolean isLaunch = false;
    if (this.workflowParams.size() > 0) {
      isLaunch = true;
    }
    return isLaunch;
  }

  public void setWorkflowParams(SortedSet<WorkflowParam> workflowParams) {
    this.workflowParams = workflowParams;
  }

  public String getFullName() {
    String fullName = "";
    if (name != null) {
      fullName = name;
      if (version != null) {
        fullName = fullName + " " + version;
      }
    }
    return fullName;
  }

  public String getJsonEscapeFullName() {
    return JsonUtil.forJSON(getFullName());
  }

  public String getPermanentBundleLocation() {
    return permanentBundleLocation;
  }

  public void setPermanentBundleLocation(String permanentBundleLocation) {
    this.permanentBundleLocation = permanentBundleLocation;
  }

  @Override
  public boolean givesPermission(Registration registration) {
    boolean hasPermission = true;
    if (registration == null) {
      hasPermission = false;
    }

    // else if (registration.equals(owner) || registration.isLIMSAdmin())
    // {
    // hasPermission = true;
    // }
    // else
    // {
    // hasPermission = false;
    // }
    if (!hasPermission) {
      Logger.getLogger(Workflow.class).info("Workflow does not give permission");
      throw new SecurityException("User " + registration.getEmailAddress()
          + " does not have permission to modify aspects of workflow " + this.getName());
    } else {
      Logger.getLogger(Workflow.class).info("Workflows are public by default");
    }
    return hasPermission;
  }

  public Set<WorkflowAttribute> getWorkflowAttributes() {
    return workflowAttributes;
  }

  public void setWorkflowAttributes(Set<WorkflowAttribute> workflowAttributes) {
    this.workflowAttributes = workflowAttributes;
  }

}
