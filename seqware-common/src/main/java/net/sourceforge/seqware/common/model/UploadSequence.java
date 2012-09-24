package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlTransient;
import org.springframework.web.multipart.MultipartFile;

@XmlTransient
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
	
	public UploadSequence() {
		super();
	}
	
	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	public Integer getSampleId() {
		return sampleId;
	}

	public void setSampleId(Integer sampleId) {
		this.sampleId = sampleId;
	}
	
	public String getPathToFirstFile() {
		return pathToFirstFile;
	}

	public void setPathToFirstFile(String pathToFirstFile) {
		this.pathToFirstFile = pathToFirstFile;
	}

	public String getPathToSecondFile() {
		return pathToSecondFile;
	}

	public void setPathToSecondFile(String pathToSecondFile) {
		this.pathToSecondFile = pathToSecondFile;
	}

	public MultipartFile getFileOne() {
		return fileOne;
	}

	public void setFileOne(MultipartFile fileOne) {
		this.fileOne = fileOne;
	}

	public MultipartFile getFileTwo() {
		return fileTwo;
	}

	public void setFileTwo(MultipartFile fileTwo) {
		this.fileTwo = fileTwo;
	}

	public String getFolderStore() {
		return folderStore;
	}

	public void setFolderStore(String folderStore) {
		this.folderStore = folderStore;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFileURL() {
		return fileURL;
	}

	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}

	public String getFileTwoURL() {
		return fileTwoURL;
	}

	public void setFileTwoURL(String fileTwoURL) {
		this.fileTwoURL = fileTwoURL;
	}

	public String getStrStartURL() {
		return strStartURL;
	}

	public void setStrStartURL(String strStartURL) {
		this.strStartURL = strStartURL;
	}

	public Boolean getUseOneURL() {
		return useOneURL;
	}

	public void setUseOneURL(Boolean useOneURL) {
		this.useOneURL = useOneURL;
	}

	public Integer getFileTypeId() {
		return fileTypeId;
	}

	public void setFileTypeId(Integer fileTypeId) {
		this.fileTypeId = fileTypeId;
	}

	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public Boolean getUseTwoURL() {
		return useTwoURL;
	}

	public void setUseTwoURL(Boolean useTwoURL) {
		this.useTwoURL = useTwoURL;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}
	
	public boolean isUsePairedFile(){
		boolean isUsePairedFile = false;
		if("paired".equals(getEnd())){
			isUsePairedFile = true;
		}
		return isUsePairedFile;
	}
}
