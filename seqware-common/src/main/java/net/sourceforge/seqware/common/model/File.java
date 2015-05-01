package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;
import net.sourceforge.seqware.common.security.PermissionsAware;
import net.sourceforge.seqware.common.util.Log;
import net.sourceforge.seqware.common.util.jsontools.JsonUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * File class.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class File extends PermissionsAware implements Serializable, Comparable<File>, Annotatable<FileAttribute>, FirstTierModel {

    private static final long serialVersionUID = 3681322115923390568L;
    private Integer fileId;
    private String filePath;
    private String type;
    private String metaType;
    private String description;
    private Integer swAccession;
    private Boolean isSelected = false;
    private Registration owner;
    private String url;
    private String urlLabel;
    private String md5sum;
    private FileType fileType;
    private Set<Processing> processings = new TreeSet<>();
    private Set<FileAttribute> fileAttributes = new TreeSet<>();
    private Long size;
    private Boolean skip;
    private static final Logger LOGGER = LoggerFactory.getLogger(File.class);

    /**
     * <p>
     * Getter for the field <code>processings</code>.
     * </p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Processing> getProcessings() {
        return processings;
    }

    /**
     * <p>
     * Setter for the field <code>processings</code>.
     * </p>
     *
     * @param processing
     *            a {@link java.util.Set} object.
     */
    public void setProcessings(Set<Processing> processing) {
        this.processings = processing;
    }

    /**
     * <p>
     * Constructor for File.
     * </p>
     */
    public File() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @param that
     */
    @Override
    public int compareTo(File that) {
        return (that.getFileId().compareTo(this.getFileId()));
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "File{" + "fileId=" + fileId + ", filePath=" + filePath + ", type=" + type + ", metaType=" + metaType + ", description="
                + description + ", swAccession=" + swAccession + ", isSelected=" + isSelected + ", owner=" + owner + ", url=" + url
                + ", urlLabel=" + urlLabel + ", md5sum=" + md5sum + ", fileType=" + fileType + ", skip=" + getSkip() + '}';
    }

    /**
     * {@inheritDoc}
     *
     * @param other
     */
    @Override
    public boolean equals(Object other) {
        if ((this == other)) {
            return true;
        }
        if (!(other instanceof File)) {
            return false;
        }
        File castOther = (File) other;
        return new EqualsBuilder().append(this.getFileId(), castOther.getFileId()).isEquals();
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getFileId()).toHashCode();
    }

    /**
     * <p>
     * getFileName.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFileName() {
        return (getFilePath().substring(this.getFilePath().lastIndexOf('/') + 1));
    }

    /**
     * <p>
     * getJsonEscapeFileName.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeFileName() {
        return JsonUtil.forJSON(getFileName());
    }

    /**
     * <p>
     * Getter for the field <code>fileId</code>.
     * </p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getFileId() {
        return fileId;
    }

    /**
     * <p>
     * Setter for the field <code>fileId</code>.
     * </p>
     *
     * @param fileId
     *            a {@link java.lang.Integer} object.
     */
    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    /**
     * <p>
     * Getter for the field <code>filePath</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * <p>
     * Setter for the field <code>filePath</code>.
     * </p>
     *
     * @param filePath
     *            a {@link java.lang.String} object.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * <p>
     * Getter for the field <code>type</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getType() {
        return type;
    }

    /**
     * <p>
     * Setter for the field <code>type</code>.
     * </p>
     *
     * @param type
     *            a {@link java.lang.String} object.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * <p>
     * Getter for the field <code>metaType</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMetaType() {
        return metaType;
    }

    /**
     * <p>
     * Setter for the field <code>metaType</code>.
     * </p>
     *
     * @param metaType
     *            a {@link java.lang.String} object.
     */
    public void setMetaType(String metaType) {
        this.metaType = metaType;
    }

    /**
     * <p>
     * getJsonEscapeDescription.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getJsonEscapeDescription() {
        return JsonUtil.forJSON(description);
    }

    /**
     * <p>
     * Getter for the field <code>description</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getDescription() {
        return description;
    }

    /**
     * <p>
     * Setter for the field <code>description</code>.
     * </p>
     *
     * @param description
     *            a {@link java.lang.String} object.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * <p>
     * Getter for the field <code>swAccession</code>.
     * </p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    @Override
    public Integer getSwAccession() {
        return swAccession;
    }

    /**
     * <p>
     * Setter for the field <code>swAccession</code>.
     * </p>
     *
     * @param swAccession
     *            a {@link java.lang.Integer} object.
     */
    public void setSwAccession(Integer swAccession) {
        this.swAccession = swAccession;
    }

    /**
     * <p>
     * Getter for the field <code>owner</code>.
     * </p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.Registration} object.
     */
    public Registration getOwner() {
        return owner;
    }

    /**
     * <p>
     * Setter for the field <code>owner</code>.
     * </p>
     *
     * @param owner
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     */
    public void setOwner(Registration owner) {
        this.owner = owner;
    }

    /**
     * <p>
     * Getter for the field <code>isSelected</code>.
     * </p>
     *
     * @return a {@link java.lang.Boolean} object.
     */
    public Boolean getIsSelected() {
        return isSelected;
    }

    /**
     * <p>
     * Setter for the field <code>isSelected</code>.
     * </p>
     *
     * @param isSelected
     *            a {@link java.lang.Boolean} object.
     */
    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * <p>
     * Getter for the field <code>url</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUrl() {
        return url;
    }

    /**
     * <p>
     * Setter for the field <code>url</code>.
     * </p>
     *
     * @param url
     *            a {@link java.lang.String} object.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * <p>
     * Getter for the field <code>urlLabel</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getUrlLabel() {
        return urlLabel;
    }

    /**
     * <p>
     * Setter for the field <code>urlLabel</code>.
     * </p>
     *
     * @param urlLabel
     *            a {@link java.lang.String} object.
     */
    public void setUrlLabel(String urlLabel) {
        this.urlLabel = urlLabel;
    }

    /**
     * <p>
     * Getter for the field <code>md5sum</code>.
     * </p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMd5sum() {
        return md5sum;
    }

    /**
     * <p>
     * Setter for the field <code>md5sum</code>.
     * </p>
     *
     * @param md5sum
     *            a {@link java.lang.String} object.
     */
    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    /**
     * <p>
     * Getter for the field <code>fileType</code>.
     * </p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.FileType} object.
     */
    public FileType getFileType() {
        return fileType;
    }

    /**
     * <p>
     * Setter for the field <code>fileType</code>.
     * </p>
     *
     * @param fileType
     *            a {@link net.sourceforge.seqware.common.model.FileType} object.
     */
    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    @Override
    public boolean givesPermissionInternal(Registration registration, Set<Integer> considered) {
        if (registration.isLIMSAdmin()) {
            Log.debug("Skipping permissions admin on File object " + swAccession);
            return true;
        }
        boolean consideredBefore = considered.contains(this.getSwAccession());
        if (!consideredBefore) {
            considered.add(this.getSwAccession());
            Log.debug("Checking permissions for File object " + swAccession);
        } else {
            Log.debug("Skipping permissions for File object " + swAccession + " , checked before");
            return true;
        }

        boolean hasPermission = true;
        if (processings != null) {
            for (Processing p : processings) {
                if (!p.givesPermission(registration, considered)) {
                    hasPermission = false;
                    break;
                }
            }
        } else {// orphaned File
            if (registration.equals(this.owner) || registration.isLIMSAdmin()) {
                LOGGER.warn("Modifying Orphan File: " + toString());
                hasPermission = true;

            } else if (owner == null) {
                LOGGER.warn("File has no owner! Modifying Orphan File: " + toString());
                hasPermission = true;
            } else {
                LOGGER.warn("Not modifying Orphan File: " + toString());
                hasPermission = false;
            }
        }
        if (!hasPermission) {
            LOGGER.info("File does not give permission");
            throw new SecurityException("User " + registration.getEmailAddress() + " does not have permission to modify " + toString());
        }
        return hasPermission;
    }

    /**
     * <p>
     * Getter for the field <code>fileAttributes</code>.
     * </p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<FileAttribute> getFileAttributes() {
        return fileAttributes;
    }

    /**
     * <p>
     * Setter for the field <code>fileAttributes</code>.
     * </p>
     *
     * @param fileAttributes
     *            a {@link java.util.Set} object.
     */
    public void setFileAttributes(Set<FileAttribute> fileAttributes) {
        this.fileAttributes = fileAttributes;
    }

    /**
     * <p>
     * Getter for the field <code>size</code>.
     * </p>
     *
     * @return a {@link java.lang.Long} object.
     */
    public Long getSize() {
        return size;
    }

    /**
     * <p>
     * Setter for the field <code>size</code>.
     * </p>
     *
     * @param size
     *            a {@link java.lang.Long} object.
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * @return the skip
     */
    public Boolean getSkip() {
        return skip;
    }

    /**
     * @param skip
     *            the skip to set
     */
    public void setSkip(Boolean skip) {
        this.skip = skip;
    }

    @Override
    public Set<FileAttribute> getAnnotations() {
        return this.getFileAttributes();
    }

}
