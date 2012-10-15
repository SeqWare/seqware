package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlTransient;
import org.springframework.web.multipart.MultipartFile;

@XmlTransient
/**
 * <p>UploadSequence class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class UploadSequence implements Serializable{
	private static final long serialVersionUID = 1L;

	private Sample sample;
	private Integer sampleId;
	private String pathToFirstFile;
	private String pathToSecondFile;
	private String type;
	private Integer fileTypeId;
	private FileType fileType;
	
	private MultipartFile fileOne;
	private MultipartFile fileTwo;
	
	private String fileURL;
	private String fileTwoURL;
	private String strStartURL;
	private Boolean useOneURL = false;
	private Boolean useTwoURL = false;
	
	private String end = "single";
	
	private String folderStore;
	
	/**
	 * <p>Constructor for UploadSequence.</p>
	 */
	public UploadSequence() {
		super();
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
	 * <p>Getter for the field <code>sampleId</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getSampleId() {
		return sampleId;
	}

	/**
	 * <p>Setter for the field <code>sampleId</code>.</p>
	 *
	 * @param sampleId a {@link java.lang.Integer} object.
	 */
	public void setSampleId(Integer sampleId) {
		this.sampleId = sampleId;
	}
	
	/**
	 * <p>Getter for the field <code>pathToFirstFile</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getPathToFirstFile() {
		return pathToFirstFile;
	}

	/**
	 * <p>Setter for the field <code>pathToFirstFile</code>.</p>
	 *
	 * @param pathToFirstFile a {@link java.lang.String} object.
	 */
	public void setPathToFirstFile(String pathToFirstFile) {
		this.pathToFirstFile = pathToFirstFile;
	}

	/**
	 * <p>Getter for the field <code>pathToSecondFile</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getPathToSecondFile() {
		return pathToSecondFile;
	}

	/**
	 * <p>Setter for the field <code>pathToSecondFile</code>.</p>
	 *
	 * @param pathToSecondFile a {@link java.lang.String} object.
	 */
	public void setPathToSecondFile(String pathToSecondFile) {
		this.pathToSecondFile = pathToSecondFile;
	}

	/**
	 * <p>Getter for the field <code>fileOne</code>.</p>
	 *
	 * @return a {@link org.springframework.web.multipart.MultipartFile} object.
	 */
	public MultipartFile getFileOne() {
		return fileOne;
	}

	/**
	 * <p>Setter for the field <code>fileOne</code>.</p>
	 *
	 * @param fileOne a {@link org.springframework.web.multipart.MultipartFile} object.
	 */
	public void setFileOne(MultipartFile fileOne) {
		this.fileOne = fileOne;
	}

	/**
	 * <p>Getter for the field <code>fileTwo</code>.</p>
	 *
	 * @return a {@link org.springframework.web.multipart.MultipartFile} object.
	 */
	public MultipartFile getFileTwo() {
		return fileTwo;
	}

	/**
	 * <p>Setter for the field <code>fileTwo</code>.</p>
	 *
	 * @param fileTwo a {@link org.springframework.web.multipart.MultipartFile} object.
	 */
	public void setFileTwo(MultipartFile fileTwo) {
		this.fileTwo = fileTwo;
	}

	/**
	 * <p>Getter for the field <code>folderStore</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFolderStore() {
		return folderStore;
	}

	/**
	 * <p>Setter for the field <code>folderStore</code>.</p>
	 *
	 * @param folderStore a {@link java.lang.String} object.
	 */
	public void setFolderStore(String folderStore) {
		this.folderStore = folderStore;
	}

	/**
	 * <p>Getter for the field <code>type</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getType() {
		return type;
	}

	/**
	 * <p>Setter for the field <code>type</code>.</p>
	 *
	 * @param type a {@link java.lang.String} object.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * <p>Getter for the field <code>fileURL</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFileURL() {
		return fileURL;
	}

	/**
	 * <p>Setter for the field <code>fileURL</code>.</p>
	 *
	 * @param fileURL a {@link java.lang.String} object.
	 */
	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}

	/**
	 * <p>Getter for the field <code>fileTwoURL</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFileTwoURL() {
		return fileTwoURL;
	}

	/**
	 * <p>Setter for the field <code>fileTwoURL</code>.</p>
	 *
	 * @param fileTwoURL a {@link java.lang.String} object.
	 */
	public void setFileTwoURL(String fileTwoURL) {
		this.fileTwoURL = fileTwoURL;
	}

	/**
	 * <p>Getter for the field <code>strStartURL</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getStrStartURL() {
		return strStartURL;
	}

	/**
	 * <p>Setter for the field <code>strStartURL</code>.</p>
	 *
	 * @param strStartURL a {@link java.lang.String} object.
	 */
	public void setStrStartURL(String strStartURL) {
		this.strStartURL = strStartURL;
	}

	/**
	 * <p>Getter for the field <code>useOneURL</code>.</p>
	 *
	 * @return a {@link java.lang.Boolean} object.
	 */
	public Boolean getUseOneURL() {
		return useOneURL;
	}

	/**
	 * <p>Setter for the field <code>useOneURL</code>.</p>
	 *
	 * @param useOneURL a {@link java.lang.Boolean} object.
	 */
	public void setUseOneURL(Boolean useOneURL) {
		this.useOneURL = useOneURL;
	}

	/**
	 * <p>Getter for the field <code>fileTypeId</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getFileTypeId() {
		return fileTypeId;
	}

	/**
	 * <p>Setter for the field <code>fileTypeId</code>.</p>
	 *
	 * @param fileTypeId a {@link java.lang.Integer} object.
	 */
	public void setFileTypeId(Integer fileTypeId) {
		this.fileTypeId = fileTypeId;
	}

	/**
	 * <p>Getter for the field <code>fileType</code>.</p>
	 *
	 * @return a {@link net.sourceforge.seqware.common.model.FileType} object.
	 */
	public FileType getFileType() {
		return fileType;
	}

	/**
	 * <p>Setter for the field <code>fileType</code>.</p>
	 *
	 * @param fileType a {@link net.sourceforge.seqware.common.model.FileType} object.
	 */
	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	/**
	 * <p>Getter for the field <code>useTwoURL</code>.</p>
	 *
	 * @return a {@link java.lang.Boolean} object.
	 */
	public Boolean getUseTwoURL() {
		return useTwoURL;
	}

	/**
	 * <p>Setter for the field <code>useTwoURL</code>.</p>
	 *
	 * @param useTwoURL a {@link java.lang.Boolean} object.
	 */
	public void setUseTwoURL(Boolean useTwoURL) {
		this.useTwoURL = useTwoURL;
	}

	/**
	 * <p>Getter for the field <code>end</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getEnd() {
		return end;
	}

	/**
	 * <p>Setter for the field <code>end</code>.</p>
	 *
	 * @param end a {@link java.lang.String} object.
	 */
	public void setEnd(String end) {
		this.end = end;
	}
	
	/**
	 * <p>isUsePairedFile.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isUsePairedFile(){
		boolean isUsePairedFile = false;
		if("paired".equals(getEnd())){
			isUsePairedFile = true;
		}
		return isUsePairedFile;
	}
}
