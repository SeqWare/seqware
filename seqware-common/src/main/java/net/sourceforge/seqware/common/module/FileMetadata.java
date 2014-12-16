package net.sourceforge.seqware.common.module;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import net.sourceforge.seqware.common.model.FileAttribute;

/**
 *
 * This is a simple data structure to represent a file and it's metadata. It should roughly match the DB schema. FIXME: Instead of doing
 * this, should be using something like Hibernate to represent database schema in objects? You bet, refactoring opportunity in 1.2.
 *
 * @author jmendler
 * @version $Id: $Id
 */
public class FileMetadata {
    private String url;
    private String urlLabel;
    private String filePath;
    private String type;
    private String metaType;
    private String description;
    private String md5sum;
    private Long size;
    private final Set<FileAttribute> annotations = new TreeSet<>();

    // Default constructor initializes everything to empty strings,
    public FileMetadata() {
        filePath = "";
        type = "";
        metaType = "";
        description = "";
    }

    // Another Constructor to take the minimum
    public FileMetadata(String filePath_start, String metaType_start) {
        filePath = filePath_start;
        metaType = metaType_start;
    }

    // Another Constructor to populate as desired
    public FileMetadata(String filePath_start, String type_start, String metaType_start, String description_start) {
        filePath = filePath_start;
        type = type_start;
        metaType = metaType_start;
        description = description_start;
    }

    public String getFilePath() {
        return filePath;
    }

    public void prependToFilePath(String pathToPrepend) {
        if (pathToPrepend.endsWith("/") || this.getFilePath().startsWith("/")) {
            this.filePath = pathToPrepend + this.getFilePath();
        } else {
            this.filePath = pathToPrepend + "/" + this.getFilePath();
        }
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFilePath(File file) {
        this.filePath = file.getAbsolutePath();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMetaType() {
        return metaType;
    }

    public void setMetaType(String metaType) {
        this.metaType = metaType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlLabel() {
        return urlLabel;
    }

    public void setUrlLabel(String urlLabel) {
        this.urlLabel = urlLabel;
    }

    @Override
    public String toString() {
        return "FileMetadata{" + "url=" + url + ", urlLabel=" + urlLabel + ", filePath=" + filePath + ", type=" + type + ", metaType="
                + metaType + ", description=" + description + ", md5sum=" + md5sum + ", size=" + size + '}';
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * @return the annotations
     */
    public Set<FileAttribute> getAnnotations() {
        return annotations;
    }
}
