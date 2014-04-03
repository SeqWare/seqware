package net.sourceforge.solexatools.webapp.metamodel;

import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.WorkflowRun;

/**
 * <p>StudySampleReportLineItem class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StudySampleReportLineItem implements Comparable<StudySampleReportLineItem>{
	static{
	  if(true)
	    throw new Error("This class needs to have the status strings changed to some enum, but it's not clear what they should refer to.");
	}
	private final static String SUCCESS = "completed";
	private final static String PENDING = "pending";
	private final static String RUNNING = "running";
	private final static String FAILED = "failed";
	private final static String NOT_RUNNING = "notrunned";

	private Sample sample;
	private Sample childSample;
	private WorkflowRun[] wfRuns;
	private String[] statuses;
	private Sample sortKeySample;
	
	// Overall status
	private int completed;
	private int pending;
	private int failed;
	private int notRunned;
	
	/**
	 * <p>Constructor for StudySampleReportLineItem.</p>
	 *
	 * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
	 * @param child a {@link net.sourceforge.seqware.common.model.Sample} object.
	 * @param statuses an array of {@link java.lang.String} objects.
	 */
	public StudySampleReportLineItem(Sample sample, Sample child, String[] statuses) {
		this.sample = sample;
		this.childSample = child;
		this.setStatuses(statuses);
		this.sortKeySample = sample;
		initOverall();
	}
	
	private void initOverall() {
		this.completed = this.pending = this.failed = this.notRunned = 0;
		for (String status: statuses) {
			if (SUCCESS.equals(status)) {
				completed++;
			}
			if (PENDING.equals(status)) {
				pending++;
			}
			if (RUNNING.equals(status)) {
				//run++;
			}
			if (FAILED.equals(status)) {
				failed++;
			}
			if (NOT_RUNNING.equals(status)) {
				notRunned++;
			}
                    
		}
	}

	/**
	 * <p>Getter for the field <code>sample</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
	 */
	public Sample getSample() {
		return sample;
	}
	/**
	 * <p>Setter for the field <code>sample</code>.</p>
	 *
	 * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
	 */
	public void setSample(Sample sample) {
		this.sample = sample;
	}
	/**
	 * <p>Getter for the field <code>childSample</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
	 */
	public Sample getChildSample() {
		return childSample;
	}
	/**
	 * <p>Setter for the field <code>childSample</code>.</p>
	 *
	 * @param childSample a {@link net.sourceforge.seqware.common.model.Sample} object.
	 */
	public void setChildSample(Sample childSample) {
		this.childSample = childSample;
	}
	/**
	 * <p>Getter for the field <code>wfRuns</code>.</p>
	 *
	 * @return an array of {@link net.sourceforge.seqware.common.model.WorkflowRun} objects.
	 */
	public WorkflowRun[] getWfRuns() {
		return wfRuns;
	}
	/**
	 * <p>Setter for the field <code>wfRuns</code>.</p>
	 *
	 * @param wfRuns an array of {@link net.sourceforge.seqware.common.model.WorkflowRun} objects.
	 */
	public void setWfRuns(WorkflowRun[] wfRuns) {
		this.wfRuns = wfRuns;
	}

	/**
	 * <p>Getter for the field <code>statuses</code>.</p>
	 *
	 * @return an array of {@link java.lang.String} objects.
	 */
	public String[] getStatuses() {
		return statuses;
	}

	/**
	 * <p>Setter for the field <code>statuses</code>.</p>
	 *
	 * @param statuses an array of {@link java.lang.String} objects.
	 */
	public void setStatuses(String[] statuses) {
		this.statuses = statuses;
	}

	/**
	 * <p>Getter for the field <code>completed</code>.</p>
	 *
	 * @return a int.
	 */
	public int getCompleted() {
		return completed;
	}

	/**
	 * <p>Setter for the field <code>completed</code>.</p>
	 *
	 * @param completed a int.
	 */
	public void setCompleted(int completed) {
		this.completed = completed;
	}

	/**
	 * <p>Getter for the field <code>pending</code>.</p>
	 *
	 * @return a int.
	 */
	public int getPending() {
		return pending;
	}

	/**
	 * <p>Setter for the field <code>pending</code>.</p>
	 *
	 * @param pending a int.
	 */
	public void setPending(int pending) {
		this.pending = pending;
	}

	/**
	 * <p>Getter for the field <code>failed</code>.</p>
	 *
	 * @return a int.
	 */
	public int getFailed() {
		return failed;
	}

	/**
	 * <p>Setter for the field <code>failed</code>.</p>
	 *
	 * @param failed a int.
	 */
	public void setFailed(int failed) {
		this.failed = failed;
	}

	/**
	 * <p>Getter for the field <code>notRunned</code>.</p>
	 *
	 * @return a int.
	 */
	public int getNotRunned() {
		return notRunned;
	}

	/**
	 * <p>Setter for the field <code>notRunned</code>.</p>
	 *
	 * @param notRunned a int.
	 */
	public void setNotRunned(int notRunned) {
		this.notRunned = notRunned;
	}

	/**
	 * <p>Getter for the field <code>sortKeySample</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
	 */
	public Sample getSortKeySample() {
		return sortKeySample;
	}

	/**
	 * <p>Setter for the field <code>sortKeySample</code>.</p>
	 *
	 * @param sortKeySample a {@link net.sourceforge.seqware.common.model.Sample} object.
	 */
	public void setSortKeySample(Sample sortKeySample) {
		this.sortKeySample = sortKeySample;
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(StudySampleReportLineItem lineItem) {
		return lineItem.getSortKeySample().getName().compareTo(sortKeySample.getName());
	}
	
}
