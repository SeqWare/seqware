package net.sourceforge.seqware.common.business;

import java.io.Serializable;

import net.sourceforge.seqware.common.model.FileType;

import org.springframework.web.multipart.MultipartFile;

public class UploadFile implements Serializable{
	private static final long serialVersionUID = 1L;

	private String	typeNode;
	private Integer id;
	private String  nameNode;
	private Integer fileTypeId;
	private FileType fileType;
	private MultipartFile file;
	private String fileURL;
	private String strStartURL;
	private Boolean useURL = new Boolean(false);
	
	private String folderStore;
	
	public UploadFile() {
		super();
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

	public String getNameNode() {
		return nameNode;
	}

	public void setNameNode(String nameNode) {
		this.nameNode = nameNode;
	}

	public String getTypeNode() {
		return typeNode;
	}

	public void setTypeNode(String typeNode) {
		this.typeNode = typeNode;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public String getFileURL() {
		return fileURL;
	}

	public void setFileURL(String fileURL) {
		this.fileURL = fileURL;
	}

	public Boolean getUseURL() {
		return useURL;
	}

	public void setUseURL(Boolean useURL) {
		this.useURL = useURL;
	}

	public String getFolderStore() {
		return folderStore;
	}

	public void setFolderStore(String folderStore) {
		this.folderStore = folderStore;
	}

	public String getStrStartURL() {
		return strStartURL;
	}

	public void setStrStartURL(String strStartURL) {
		this.strStartURL = strStartURL;
	}
}
