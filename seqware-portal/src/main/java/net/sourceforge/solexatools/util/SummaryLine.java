package net.sourceforge.solexatools.util;

import java.util.List;
import java.util.SortedSet;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.WorkflowParam;

/**
 * <p>SummaryLine class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class SummaryLine {

	private String displayName;
	private String fileMetaType;
	private SortedSet<WorkflowParam> params;
	private List<File> files;
	
	/**
	 * <p>Constructor for SummaryLine.</p>
	 */
	public SummaryLine() {
	}
	
	/**
	 * <p>Getter for the field <code>displayName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * <p>Setter for the field <code>displayName</code>.</p>
	 *
	 * @param displayName a {@link java.lang.String} object.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * <p>Getter for the field <code>fileMetaType</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFileMetaType() {
		return fileMetaType;
	}

	/**
	 * <p>Setter for the field <code>fileMetaType</code>.</p>
	 *
	 * @param fileMetaType a {@link java.lang.String} object.
	 */
	public void setFileMetaType(String fileMetaType) {
		this.fileMetaType = fileMetaType;
	}

	/**
	 * <p>Getter for the field <code>params</code>.</p>
	 *
	 * @return a {@link java.util.SortedSet} object.
	 */
	public SortedSet<WorkflowParam> getParams() {
		return params;
	}

	/**
	 * <p>Setter for the field <code>params</code>.</p>
	 *
	 * @param params a {@link java.util.SortedSet} object.
	 */
	public void setParams(SortedSet<WorkflowParam> params) {
		this.params = params;
	}

	/**
	 * <p>Getter for the field <code>files</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<File> getFiles() {
		return files;
	}

	/**
	 * <p>Setter for the field <code>files</code>.</p>
	 *
	 * @param files a {@link java.util.List} object.
	 */
	public void setFiles(List<File> files) {
		this.files = files;
	}	
}
