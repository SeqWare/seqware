package net.sourceforge.solexatools.util;

import java.util.List;
import java.util.SortedSet;

import net.sourceforge.seqware.common.model.WorkflowParam;

/**
 * <p>SummaryData class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SummaryData {
	private SortedSet<WorkflowParam> visibleParams;
	private List<SummaryLine> summaryLines;
	
	/**
	 * <p>Constructor for SummaryData.</p>
	 */
	public SummaryData() {
	}

	/**
	 * <p>Getter for the field <code>visibleParams</code>.</p>
	 *
	 * @return a {@link java.util.SortedSet} object.
	 */
	public SortedSet<WorkflowParam> getVisibleParams() {
		return visibleParams;
	}

	/**
	 * <p>Setter for the field <code>visibleParams</code>.</p>
	 *
	 * @param visibleParams a {@link java.util.SortedSet} object.
	 */
	public void setVisibleParams(SortedSet<WorkflowParam> visibleParams) {
		this.visibleParams = visibleParams;
	}

	/**
	 * <p>Getter for the field <code>summaryLines</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<SummaryLine> getSummaryLines() {
		return summaryLines;
	}

	/**
	 * <p>Setter for the field <code>summaryLines</code>.</p>
	 *
	 * @param summaryLines a {@link java.util.List} object.
	 */
	public void setSummaryLines(List<SummaryLine> summaryLines) {
		this.summaryLines = summaryLines;
	}
}
