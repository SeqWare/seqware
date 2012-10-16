package net.sourceforge.solexatools.webapp.metamodel;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Sample;

/**
 * <p>StudySampleReportFileLineItem class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class StudySampleReportFileLineItem implements Comparable<StudySampleReportFileLineItem>{

	private Sample rootSample;
	private Sample childSample;
	private IUS ius;
	private File file;
	
	// Attributes
	private String tissSource;
	private String templateType;
	private String libraryType;
	
	/**
	 * <p>Constructor for StudySampleReportFileLineItem.</p>
	 *
	 * @param root a {@link net.sourceforge.seqware.common.model.Sample} object.
	 * @param child a {@link net.sourceforge.seqware.common.model.Sample} object.
	 * @param ius a {@link net.sourceforge.seqware.common.model.IUS} object.
	 * @param file a {@link net.sourceforge.seqware.common.model.File} object.
	 */
	public StudySampleReportFileLineItem(Sample root, Sample child, IUS ius, File file) {
		this.rootSample = root;
		this.childSample = child;
		this.ius = ius;
		this.setFile(file);
	}
	
	/**
	 * <p>Getter for the field <code>rootSample</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
	 */
	public Sample getRootSample() {
		return rootSample;
	}
	/**
	 * <p>Setter for the field <code>rootSample</code>.</p>
	 *
	 * @param rootSample a {@link net.sourceforge.seqware.common.model.Sample} object.
	 */
	public void setRootSample(Sample rootSample) {
		this.rootSample = rootSample;
	}
	/**
	 * <p>Getter for the field <code>ius</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.IUS} object.
	 */
	public IUS getIus() {
		return ius;
	}
	/**
	 * <p>Setter for the field <code>ius</code>.</p>
	 *
	 * @param ius a {@link net.sourceforge.seqware.common.model.IUS} object.
	 */
	public void setIus(IUS ius) {
		this.ius = ius;
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
	 * <p>Getter for the field <code>file</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.File} object.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * <p>Setter for the field <code>file</code>.</p>
	 *
	 * @param file a {@link net.sourceforge.seqware.common.model.File} object.
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * <p>Getter for the field <code>tissSource</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTissSource() {
		return tissSource;
	}

	/**
	 * <p>Setter for the field <code>tissSource</code>.</p>
	 *
	 * @param tissSource a {@link java.lang.String} object.
	 */
	public void setTissSource(String tissSource) {
		this.tissSource = tissSource;
	}

	/**
	 * <p>Getter for the field <code>templateType</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTemplateType() {
		return templateType;
	}

	/**
	 * <p>Setter for the field <code>templateType</code>.</p>
	 *
	 * @param templateType a {@link java.lang.String} object.
	 */
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	/**
	 * <p>Getter for the field <code>libraryType</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getLibraryType() {
		return libraryType;
	}

	/**
	 * <p>Setter for the field <code>libraryType</code>.</p>
	 *
	 * @param libraryType a {@link java.lang.String} object.
	 */
	public void setLibraryType(String libraryType) {
		this.libraryType = libraryType;
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(StudySampleReportFileLineItem arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}
