package net.sourceforge.seqware.common.util.workflowtools;

public class WorkflowInfo {

  String name;
  String version;
  String description;
  String testCmd;
  String templatePath;
  String configPath;
  String computeReq;
  String memReq;
  String networkReq;
  String command;
  String workflowDir;
  String host;
  String permBundleLocation;
  int workflowAccession = 0;
  private String classesDir;
  private String baseDir;
  private String workflow_sqw_version;

    public String getPermBundleLocation() {
        return permBundleLocation;
    }

    public void setPermBundleLocation(String permBundleLocation) {
        this.permBundleLocation = permBundleLocation;
    }

  
  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getWorkflowAccession() {
    return workflowAccession;
  }

  public void setWorkflowAccession(int workflowAccession) {
    this.workflowAccession = workflowAccession;
  }

  public String getWorkflowDir() {
    return workflowDir;
  }

  public void setWorkflowDir(String workflowDir) {
    this.workflowDir = workflowDir;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTestCmd() {
    return testCmd;
  }

  public void setTestCmd(String testCmd) {
    this.testCmd = testCmd;
  }

  public String getTemplatePath() {
    return templatePath;
  }

  public void setTemplatePath(String templatePath) {
    this.templatePath = templatePath;
  }

  public String getConfigPath() {
    return configPath;
  }

  public void setConfigPath(String configPath) {
    this.configPath = configPath;
  }

  public String getComputeReq() {
    return computeReq;
  }

  public void setComputeReq(String computeReq) {
    this.computeReq = computeReq;
  }

  public String getMemReq() {
    return memReq;
  }

  public void setMemReq(String memReq) {
    this.memReq = memReq;
  }

  public String getNetworkReq() {
    return networkReq;
  }

  public void setNetworkReq(String networkReq) {
    this.networkReq = networkReq;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getClassesDir() {
    return this.classesDir;
  }

  public void setClassesDir(String value) {
    this.classesDir = value;
  }

  public String getBaseDir() {
    return baseDir;
  }

  public void setBaseDir(String baseDir) {
    this.baseDir = baseDir;
  }

  public String getWorkflowSqwVersion() {
    return workflow_sqw_version;
  }

  public void setWorkflowSqwVersion(String workflow_sqw_version) {
    this.workflow_sqw_version = workflow_sqw_version;
  }
}
