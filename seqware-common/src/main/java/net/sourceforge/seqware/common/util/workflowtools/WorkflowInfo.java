package net.sourceforge.seqware.common.util.workflowtools;

/**
 * <p>WorkflowInfo class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowInfo {

  private String name;
  private String version;
  private String description;
  private String testCmd;
  private String templatePath;
  private String configPath;
  private String computeReq;
  private String memReq;
  private String networkReq;
  private String command;
  private String workflowDir;
  private String host;
  private String permBundleLocation;
  private int workflowAccession = 0;
  private String classesDir;
  private String baseDir;
  private String workflow_sqw_version;
  private String workflowClass;
  private String workflowType;
  private String workflowEngine;

    public String getWorkflowClass() {
        return workflowClass;
    }

    public void setWorkflowClass(String workflowClass) {
        this.workflowClass = workflowClass;
    }

    public String getWorkflowType() {
        return workflowType;
    }

    public void setWorkflowType(String workflowType) {
        this.workflowType = workflowType;
    }

    public String getWorkflowEngine() {
        return workflowEngine;
    }

    public void setWorkflowEngine(String workflowEngine) {
        this.workflowEngine = workflowEngine;
    }
  
  
  

    /**
     * <p>Getter for the field <code>permBundleLocation</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPermBundleLocation() {
        return permBundleLocation;
    }

    /**
     * <p>Setter for the field <code>permBundleLocation</code>.</p>
     *
     * @param permBundleLocation a {@link java.lang.String} object.
     */
    public void setPermBundleLocation(String permBundleLocation) {
        this.permBundleLocation = permBundleLocation;
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
   * <p>Getter for the field <code>workflowAccession</code>.</p>
   *
   * @return a int.
   */
  public int getWorkflowAccession() {
    return workflowAccession;
  }

  /**
   * <p>Setter for the field <code>workflowAccession</code>.</p>
   *
   * @param workflowAccession a int.
   */
  public void setWorkflowAccession(int workflowAccession) {
    this.workflowAccession = workflowAccession;
  }

  /**
   * <p>Getter for the field <code>workflowDir</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getWorkflowDir() {
    return workflowDir;
  }

  /**
   * <p>Setter for the field <code>workflowDir</code>.</p>
   *
   * @param workflowDir a {@link java.lang.String} object.
   */
  public void setWorkflowDir(String workflowDir) {
    this.workflowDir = workflowDir;
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
   * <p>Getter for the field <code>testCmd</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getTestCmd() {
    return testCmd;
  }

  /**
   * <p>Setter for the field <code>testCmd</code>.</p>
   *
   * @param testCmd a {@link java.lang.String} object.
   */
  public void setTestCmd(String testCmd) {
    this.testCmd = testCmd;
  }

  /**
   * <p>Getter for the field <code>templatePath</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getTemplatePath() {
    return templatePath;
  }

  /**
   * <p>Setter for the field <code>templatePath</code>.</p>
   *
   * @param templatePath a {@link java.lang.String} object.
   */
  public void setTemplatePath(String templatePath) {
    this.templatePath = templatePath;
  }

  /**
   * <p>Getter for the field <code>configPath</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getConfigPath() {
    return configPath;
  }

  /**
   * <p>Setter for the field <code>configPath</code>.</p>
   *
   * @param configPath a {@link java.lang.String} object.
   */
  public void setConfigPath(String configPath) {
    this.configPath = configPath;
  }

  /**
   * <p>Getter for the field <code>computeReq</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getComputeReq() {
    return computeReq;
  }

  /**
   * <p>Setter for the field <code>computeReq</code>.</p>
   *
   * @param computeReq a {@link java.lang.String} object.
   */
  public void setComputeReq(String computeReq) {
    this.computeReq = computeReq;
  }

  /**
   * <p>Getter for the field <code>memReq</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getMemReq() {
    return memReq;
  }

  /**
   * <p>Setter for the field <code>memReq</code>.</p>
   *
   * @param memReq a {@link java.lang.String} object.
   */
  public void setMemReq(String memReq) {
    this.memReq = memReq;
  }

  /**
   * <p>Getter for the field <code>networkReq</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getNetworkReq() {
    return networkReq;
  }

  /**
   * <p>Setter for the field <code>networkReq</code>.</p>
   *
   * @param networkReq a {@link java.lang.String} object.
   */
  public void setNetworkReq(String networkReq) {
    this.networkReq = networkReq;
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
   * <p>Setter for the field <code>name</code>.</p>
   *
   * @param name a {@link java.lang.String} object.
   */
  public void setName(String name) {
    this.name = name;
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
   * <p>Getter for the field <code>classesDir</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getClassesDir() {
    return this.classesDir;
  }

  /**
   * <p>Setter for the field <code>classesDir</code>.</p>
   *
   * @param value a {@link java.lang.String} object.
   */
  public void setClassesDir(String value) {
    this.classesDir = value;
  }

  /**
   * <p>Getter for the field <code>baseDir</code>.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getBaseDir() {
    return baseDir;
  }

  /**
   * <p>Setter for the field <code>baseDir</code>.</p>
   *
   * @param baseDir a {@link java.lang.String} object.
   */
  public void setBaseDir(String baseDir) {
    this.baseDir = baseDir;
  }

  /**
   * <p>getWorkflowSqwVersion.</p>
   *
   * @return a {@link java.lang.String} object.
   */
  public String getWorkflowSqwVersion() {
    return workflow_sqw_version;
  }

  /**
   * <p>setWorkflowSqwVersion.</p>
   *
   * @param workflow_sqw_version a {@link java.lang.String} object.
   */
  public void setWorkflowSqwVersion(String workflow_sqw_version) {
    this.workflow_sqw_version = workflow_sqw_version;
  }
}
