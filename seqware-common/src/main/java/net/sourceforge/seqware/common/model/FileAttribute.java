package net.sourceforge.seqware.common.model;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.Cascade;

@Entity
/**
 * <p>FileAttribute class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
@Table(name = "file_attribute", uniqueConstraints = { @UniqueConstraint(columnNames = { "file_id", "tag", "value" }) })
public class FileAttribute extends Attribute<File, FileAttribute> implements Serializable {

    @Id
    @SequenceGenerator(name = "file_attribute_id_seq_gen", sequenceName = "file_attribute_id_seq")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "file_attribute_id_seq_gen")
    @Column(name = "file_attribute_id")
    private Integer fileAttributeId;

    // SEQWARE-1578
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH })
    // a special Hibernate workaround since
    // http://tai-dev.blog.co.uk/2011/11/18/fun-with-hibernates-cascadetype-persist-and-cascadetype-all-the-case-of-the-unsaved-transient-instance-12187037/
    @Cascade(org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    @Column(nullable = false)
    private String tag;

    @Column(nullable = false)
    private String value;

    private String unit;

    /**
     * <p>
     * Constructor for FileAttribute.
     * </p>
     */
    public FileAttribute() {

    }

    /**
     * <p>
     * Constructor for FileAttribute.
     * </p>
     *
     * @param fileAttributeId
     *            a {@link java.lang.Integer} object.
     */
    public FileAttribute(Integer fileAttributeId) {
        this.fileAttributeId = fileAttributeId;
    }

    /**
     * <p>
     * Getter for the field <code>file</code>.
     * </p>
     *
     * @return a {@link net.sourceforge.seqware.common.model.File} object.
     */
    public File getFile() {
        return file;
    }

    /**
     * <p>
     * Setter for the field <code>file</code>.
     * </p>
     *
     * @param file
     *            a {@link net.sourceforge.seqware.common.model.File} object.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public String getTag() {
        return tag;
    }

    /** {@inheritDoc} */
    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public String getValue() {
        return value;
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
    @Override
    public String getUnit() {
        return unit;
    }

    /** {@inheritDoc} */
    @Override
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * <p>
     * Getter for the field <code>fileAttributeId</code>.
     * </p>
     *
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getFileAttributeId() {
        return fileAttributeId;
    }

    /**
     * <p>
     * Setter for the field <code>fileAttributeId</code>.
     * </p>
     *
     * @param fileAttributeId
     *            a {@link java.lang.Integer} object.
     */
    public void setFileAttributeId(Integer fileAttributeId) {
        this.fileAttributeId = fileAttributeId;
    }

    @Override
    public void setAttributeParent(File parent) {
        this.setFile(parent);
    }

}
