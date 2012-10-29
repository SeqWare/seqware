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
	
}