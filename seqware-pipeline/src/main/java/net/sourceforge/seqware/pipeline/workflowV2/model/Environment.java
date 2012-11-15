package net.sourceforge.seqware.pipeline.workflowV2.model;

public class Environment {
	
	enum ExecutionEnvironment {
		CLUSTER,
		NODE
	}
	
	private int peakThreads;
	private int peakMemory;
	private ExecutionEnvironment exeEnv;
	
	//these variables are from metadata.xml
	private String compute;
	private String memory;
	private String network;
	//from .seqware/settings
	private String SW_PEGASUS_CONFIG_DIR;
	private String SW_DAX_DIR;
	private String SW_CLUSTER;
	
	
	private String OOZIE_URL;
	private String OOZIE_APP_ROOT;
	private String OOZIE_JOBTRACKER;
	private String OOZIE_NAMENODE;
	private String OOZIE_QUEUENAME;
	private String HADOOP_CORE_XML;
	private String HADOOP_HDFS_SITE_XML;
	private String HADOOP_MAPRED_SITE_XML;
	private String OOZIE_WORK_DIR;
	private String OOZIE_APP_PATH;
	
	/**
	 * 
	 * @return the max threads for the workflow
	 */
	public int getPeakThreads() {
		return peakThreads;
	}
	/**
	 * set the max threads for the workflow
	 * @param peakThreads
	 */
	public void setPeakThreads(int peakThreads) {
		this.peakThreads = peakThreads;
	}
	/**
	 * @return the max memory for the workflow
	 */
	public int getPeakMemory() {
		return peakMemory;
	}
	/**
	 * set the max memory for the workflow
	 * @param peakMemory
	 */
	public void setPeakMemory(int peakMemory) {
		this.peakMemory = peakMemory;
	}
	/**
	 * 
	 * @return the execution environment
	 */
	public ExecutionEnvironment getExecutionEnv() {
		return exeEnv;
	}
	/**
	 * set the execution environment
	 * @param exeEnv
	 */
	public void setExecutionEnv(ExecutionEnvironment exeEnv) {
		this.exeEnv = exeEnv;
	}
	public String getCompute() {
		return compute;
	}
	public void setCompute(String compute) {
		this.compute = compute;
	}
	public String getMemory() {
		return memory;
	}
	public void setMemory(String memory) {
		this.memory = memory;
	}
	public String getNetwork() {
		return network;
	}
	public void setNetwork(String network) {
		this.network = network;
	}
	public String getPegasusConfigDir() {
		return SW_PEGASUS_CONFIG_DIR;
	}
	public void setPegasusConfigDir(String sW_PEGASUS_CONFIG_DIR) {
		SW_PEGASUS_CONFIG_DIR = sW_PEGASUS_CONFIG_DIR;
	}
	public String getDaxDir() {
		return SW_DAX_DIR;
	}
	public void setDaxDir(String sW_DAX_DIR) {
		SW_DAX_DIR = sW_DAX_DIR;
	}
	public String getSwCluster() {
		return SW_CLUSTER;
	}
	public void setSwCluster(String sW_CLUSTER) {
		SW_CLUSTER = sW_CLUSTER;
	}
	public String getOOZIE_URL() {
		return OOZIE_URL;
	}
	public void setOOZIE_URL(String oOZIE_URL) {
		OOZIE_URL = oOZIE_URL;
	}
	public String getOOZIE_APP_ROOT() {
		return OOZIE_APP_ROOT;
	}
	public void setOOZIE_APP_ROOT(String oOZIE_APP_ROOT) {
		OOZIE_APP_ROOT = oOZIE_APP_ROOT;
	}
	public String getOOZIE_JOBTRACKER() {
		return OOZIE_JOBTRACKER;
	}
	public void setOOZIE_JOBTRACKER(String oOZIE_JOBTRACKER) {
		OOZIE_JOBTRACKER = oOZIE_JOBTRACKER;
	}
	public String getOOZIE_NAMENODE() {
		return OOZIE_NAMENODE;
	}
	public void setOOZIE_NAMENODE(String oOZIE_NAMENODE) {
		OOZIE_NAMENODE = oOZIE_NAMENODE;
	}
	public String getOOZIE_QUEUENAME() {
		return OOZIE_QUEUENAME;
	}
	public void setOOZIE_QUEUENAME(String oOZIE_QUEUENAME) {
		OOZIE_QUEUENAME = oOZIE_QUEUENAME;
	}
	public String getHADOOP_CORE_XML() {
		return HADOOP_CORE_XML;
	}
	public void setHADOOP_CORE_XML(String hADOOP_CORE_XML) {
		HADOOP_CORE_XML = hADOOP_CORE_XML;
	}
	public String getHADOOP_HDFS_SITE_XML() {
		return HADOOP_HDFS_SITE_XML;
	}
	public void setHADOOP_HDFS_SITE_XML(String hADOOP_HDFS_SITE_XML) {
		HADOOP_HDFS_SITE_XML = hADOOP_HDFS_SITE_XML;
	}
	public String getHADOOP_MAPRED_SITE_XML() {
		return HADOOP_MAPRED_SITE_XML;
	}
	public void setHADOOP_MAPRED_SITE_XML(String hADOOP_MAPRED_SITE_XML) {
		HADOOP_MAPRED_SITE_XML = hADOOP_MAPRED_SITE_XML;
	}
	public String getOOZIE_WORK_DIR() {
		return OOZIE_WORK_DIR;
	}
	public void setOOZIE_WORK_DIR(String oOZIE_WORK_DIR) {
		OOZIE_WORK_DIR = oOZIE_WORK_DIR;
	}
	public String getOOZIE_APP_PATH() {
		return OOZIE_APP_PATH;
	}
	public void setOOZIE_APP_PATH(String oOZIE_APP_PATH) {
		OOZIE_APP_PATH = oOZIE_APP_PATH;
	}
	
}