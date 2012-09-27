package net.sourceforge.seqware.pipeline.workflowV2.model;

public class Environment {
	
	enum ExecutionEnvironment {
		CLUSTER,
		NODE
	}
	
	private int peakThreads;
	private int peakMemory;
	private ExecutionEnvironment exeEnv;
	
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
	public ExecutionEnvironment getExeEnv() {
		return exeEnv;
	}
	/**
	 * set the execution environment
	 * @param exeEnv
	 */
	public void setExeEnv(ExecutionEnvironment exeEnv) {
		this.exeEnv = exeEnv;
	}
	
}