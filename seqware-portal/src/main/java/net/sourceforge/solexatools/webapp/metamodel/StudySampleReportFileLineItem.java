package net.sourceforge.solexatools.webapp.metamodel;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Sample;

public class StudySampleReportFileLineItem implements Comparable<StudySampleReportFileLineItem>{

	private Sample rootSample;
	private Sample childSample;
	private IUS ius;
	private File file;
	
	// Attributes
	private String tissSource;
	private String templateType;
	private String libraryType;
	
	public StudySampleReportFileLineItem(Sample root, Sample child, IUS ius, File file) {
		this.rootSample = root;
		this.childSample = child;
		this.ius = ius;
		this.setFile(file);
	}
	
	public Sample getRootSample() {
		return rootSample;
	}
	public void setRootSample(Sample rootSample) {
		this.rootSample = rootSample;
	}
	public IUS getIus() {
		return ius;
	}
	public void setIus(IUS ius) {
		this.ius = ius;
	}
	public Sample getChildSample() {
		return childSample;
	}
	public void setChildSample(Sample childSample) {
		this.childSample = childSample;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getTissSource() {
		return tissSource;
	}

	public void setTissSource(String tissSource) {
		this.tissSource = tissSource;
	}

	public String getTemplateType() {
		return templateType;
	}

	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	public String getLibraryType() {
		return libraryType;
	}

	public void setLibraryType(String libraryType) {
		this.libraryType = libraryType;
	}

	@Override
	public int compareTo(StudySampleReportFileLineItem arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}
