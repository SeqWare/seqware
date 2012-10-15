package net.sourceforge.seqware.common.business;

import java.io.Serializable;

import net.sourceforge.seqware.common.model.FileType;

import org.springframework.web.multipart.MultipartFile;

/**
 * <p>UploadFile class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
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
	
	/**
	 * <p>Constructor for UploadFile.</p>
	 */
	public UploadFile() {
		super();
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
	 * <p>Getter for the field <code>nameNode</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getNameNode() {
		return nameNode;
	}

	/**
	 * <p>Setter for the field <code>nameNode</code>.</p>
	 *
	 * @param nameNode a {@link java.lang.String} object.
	 */
	public void setNameNode(String nameNode) {
		this.nameNode = nameNode;
	}

	/**
	 * <p>Getter for the field <code>typeNode</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTypeNode() {
		return typeNode;
	}

	/**
	 * <p>Setter for the field <code>typeNode</code>.</p>
	 *
	 * @param typeNode a {@link java.lang.String} object.
	 */
	public void setTypeNode(String typeNode) {
		this.typeNode = typeNode;
	}

	/**
	 * <p>Getter for the field <code>id</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * <p>Setter for the field <code>id</code>.</p>
	 *
	 * @param id a {@link java.lang.Integer} object.
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * <p>Getter for the field <code>file</code>.</p>
	 *
	 * @return a {@link org.springframework.web.multipart.MultipartFile} object.
	 */
	public MultipartFile getFile() {
		return file;
	}

	/**
	 * <p>Setter for the field <code>file</code>.</p>
	 *
	 * @param file a {@link org.springframework.web.multipart.MultipartFile} object.
	 */
	public void setFile(MultipartFile file) {
		this.file = file;
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
	 * <p>Getter for the field <code>useURL</code>.</p>
	 *
	 * @return a {@link java.lang.Boolean} object.
	 */
	public Boolean getUseURL() {
		return useURL;
	}

	/**
	 * <p>Setter for the field <code>useURL</code>.</p>
	 *
	 * @param useURL a {@link java.lang.Boolean} object.
	 */
	public void setUseURL(Boolean useURL) {
		this.useURL = useURL;
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
}
