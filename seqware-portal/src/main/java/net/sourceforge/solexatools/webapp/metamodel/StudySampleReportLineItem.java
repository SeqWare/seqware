package net.sourceforge.solexatools.webapp.metamodel;

import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.WorkflowRun;

public class StudySampleReportLineItem implements Comparable<StudySampleReportLineItem>{
	
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

	public Sample getSample() {
		return sample;
	}
	public void setSample(Sample sample) {
		this.sample = sample;
	}
	public Sample getChildSample() {
		return childSample;
	}
	public void setChildSample(Sample childSample) {
		this.childSample = childSample;
	}
	public WorkflowRun[] getWfRuns() {
		return wfRuns;
	}
	public void setWfRuns(WorkflowRun[] wfRuns) {
		this.wfRuns = wfRuns;
	}

	public String[] getStatuses() {
		return statuses;
	}

	public void setStatuses(String[] statuses) {
		this.statuses = statuses;
	}

	public int getCompleted() {
		return completed;
	}

	public void setCompleted(int completed) {
		this.completed = completed;
	}

	public int getPending() {
		return pending;
	}

	public void setPending(int pending) {
		this.pending = pending;
	}

	public int getFailed() {
		return failed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	public int getNotRunned() {
		return notRunned;
	}

	public void setNotRunned(int notRunned) {
		this.notRunned = notRunned;
	}

	public Sample getSortKeySample() {
		return sortKeySample;
	}

	public void setSortKeySample(Sample sortKeySample) {
		this.sortKeySample = sortKeySample;
	}

	@Override
	public int compareTo(StudySampleReportLineItem lineItem) {
		return lineItem.getSortKeySample().getName().compareTo(sortKeySample.getName());
	}
	
}
