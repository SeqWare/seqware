package net.sourceforge.solexatools.util;

import java.util.List;
import java.util.SortedSet;

import net.sourceforge.seqware.common.model.WorkflowParam;

public class SummaryData {
	private SortedSet<WorkflowParam> visibleParams;
	private List<SummaryLine> summaryLines;
	
	public SummaryData() {
	}

	public SortedSet<WorkflowParam> getVisibleParams() {
		return visibleParams;
	}

	public void setVisibleParams(SortedSet<WorkflowParam> visibleParams) {
		this.visibleParams = visibleParams;
	}

	public List<SummaryLine> getSummaryLines() {
		return summaryLines;
	}

	public void setSummaryLines(List<SummaryLine> summaryLines) {
		this.summaryLines = summaryLines;
	}
}
