package net.sourceforge.seqware.common.dao;

import java.io.IOException;
import java.util.List;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;

import org.springframework.web.multipart.MultipartFile;

/**
 * <p>FileDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface FileDAO {

    /**
     * <p>insert.</p>
     *
     * @param file a {@link net.sourceforge.seqware.common.model.File} object.
     */
    public void insert(File file);

    /**
     * <p>insert.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param file a {@link net.sourceforge.seqware.common.model.File} object.
     */
    public void insert(Registration registration, File file);

    /**
     * <p>update.</p>
     *
     * @param file a {@link net.sourceforge.seqware.common.model.File} object.
     */
    public void update(File file);

    /**
     * <p>update.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param file a {@link net.sourceforge.seqware.common.model.File} object.
     */
    public void update(Registration registration, File file);

    /**
     * <p>delete.</p>
     *
     * @param file a {@link net.sourceforge.seqware.common.model.File} object.
     */
    public void delete(File file);

    /**
     * <p>deleteAll.</p>
     *
     * @param files a {@link java.util.List} object.
     */
    public void deleteAll(List<File> files);

    /**
     * <p>deleteAllWithFolderStore.</p>
     *
     * @param list a {@link java.util.List} object.
     */
    public void deleteAllWithFolderStore(List<File> list);

    /**
     * <p>findByID.</p>
     *
     * @param id a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.File} object.
     */
    public File findByID(Integer id);

    /**
     * <p>findByPath.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.File} object.
     */
    public File findByPath(String path);

    /**
     * <p>saveFile.</p>
     *
     * @param uploadFile a {@link org.springframework.web.multipart.MultipartFile} object.
     * @param folderStore a {@link java.lang.String} object.
     * @param owner a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @return a {@link java.io.File} object.
     * @throws java.io.IOException if any.
     */
    public java.io.File saveFile(MultipartFile uploadFile, String folderStore, Registration owner) throws IOException;

    /**
     * <p>findBySWAccession.</p>
     *
     * @param swAccession a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.File} object.
     */
    public File findBySWAccession(Integer swAccession);

    /**
     * <p>updateDetached.</p>
     *
     * @param file a {@link net.sourceforge.seqware.common.model.File} object.
     * @return a {@link net.sourceforge.seqware.common.model.File} object.
     */
    public File updateDetached(File file);
    
    /**
     * <p>updateDetached.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param file a {@link net.sourceforge.seqware.common.model.File} object.
     * @return a {@link net.sourceforge.seqware.common.model.File} object.
     */
    public File updateDetached(Registration registration, File file);

    /**
     * <p>findByOwnerId.</p>
     *
     * @param registrationId a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    public List<File> findByOwnerId(Integer registrationId);

    /**
     * <p>findByCriteria.</p>
     *
     * @param criteria a {@link java.lang.String} object.
     * @param isCaseSens a boolean.
     * @return a {@link java.util.List} object.
     */
    public List<File> findByCriteria(String criteria, boolean isCaseSens);

    /**
     * <p>list.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<File> list();
}
