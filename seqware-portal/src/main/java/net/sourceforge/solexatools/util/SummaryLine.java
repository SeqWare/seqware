package net.sourceforge.solexatools.util;

import java.util.List;
import java.util.SortedSet;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.WorkflowParam;

public class SummaryLine {

	private String displayName;
	private String fileMetaType;
	private SortedSet<WorkflowParam> params;
	private List<File> files;
	
	public SummaryLine() {
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getFileMetaType() {
		return fileMetaType;
	}

	public void setFileMetaType(String fileMetaType) {
		this.fileMetaType = fileMetaType;
	}

	public SortedSet<WorkflowParam> getParams() {
		return params;
	}

	public void setParams(SortedSet<WorkflowParam> params) {
		this.params = params;
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}	
}