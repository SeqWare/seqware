package net.sourceforge.seqware.common.business;

import java.util.List;
import java.util.Set;
import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>
 * FileService interface.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface FileService {

    /** Constant <code>NAME="fileService"</code> */
    String NAME = "fileService";

    /**
     * <p>
     * setFileDAO.
     * </p>
     *
     * @param fileDAO
     *            a {@link net.sourceforge.seqware.common.dao.FileDAO} object.
     */
    void setFileDAO(FileDAO fileDAO);

    /**
     * <p>
     * insert.
     * </p>
     *
     * @param file
     *            a {@link net.sourceforge.seqware.common.model.File} object.
     */
    Integer insert(File file);

    /**
     * <p>
     * insert.
     * </p>
     *
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param file
     *            a {@link net.sourceforge.seqware.common.model.File} object.
     * @return sw_accession for created file
     */
    Integer insert(Registration registration, File file);

    /**
     * <p>
     * update.
     * </p>
     *
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param file
     *            a {@link net.sourceforge.seqware.common.model.File} object.
     */
    void update(Registration registration, File file);

    /**
     * <p>
     * update.
     * </p>
     *
     * @param file
     *            a {@link net.sourceforge.seqware.common.model.File} object.
     */
    void update(File file);

    /**
     * <p>
     * delete.
     * </p>
     *
     * @param file
     * @param deleteRealFiles
     */
    void delete(File file, boolean deleteRealFiles);

    /**
     * <p>
     * deleteAll.
     * </p>
     *
     * @param file
     * @param deleteRealFiles
     */
    void deleteAll(List<File> file, boolean deleteRealFiles);

    /**
     * <p>
     * findByID.
     * </p>
     *
     * @param id
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.File} object.
     */
    File findByID(Integer id);

    /**
     * <p>
     * findByPath.
     * </p>
     *
     * @param path
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.File} object.
     */
    File findByPath(String path);

    /**
     * <p>
     * findBySWAccession.
     * </p>
     *
     * @param swAccession
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.File} object.
     */
    File findBySWAccession(Integer swAccession);

    /**
     * <p>
     * findByOwnerId.
     * </p>
     *
     * @param registrationId
     *            a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    List<File> findByOwnerId(Integer registrationId);

    /**
     * <p>
     * getFiles.
     * </p>
     *
     * @param fileId
     *            a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    List<File> getFiles(Integer fileId);

    /**
     * <p>
     * getFiles.
     * </p>
     *
     * @param fileId
     *            a {@link java.lang.Integer} object.
     * @param metaType
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    List<File> getFiles(Integer fileId, String metaType);

    /**
     * <p>
     * setWithHasFile.
     * </p>
     *
     * @param list
     *            a {@link java.util.Set} object.
     * @param metaType
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.Set} object.
     */
    Set<File> setWithHasFile(Set<File> list, String metaType);

    /**
     * <p>
     * isExists.
     * </p>
     *
     * @param fileName
     *            a {@link java.lang.String} object.
     * @param folderStore
     *            a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean isExists(String fileName, String folderStore);

    /**
     * <p>
     * updateDetached.
     * </p>
     *
     * @param file
     *            a {@link net.sourceforge.seqware.common.model.File} object.
     * @return a {@link net.sourceforge.seqware.common.model.File} object.
     */
    File updateDetached(File file);

    /**
     * <p>
     * updateDetached.
     * </p>
     *
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param file
     *            a {@link net.sourceforge.seqware.common.model.File} object.
     * @return a {@link net.sourceforge.seqware.common.model.File} object.
     */
    File updateDetached(Registration registration, File file);

    /**
     * <p>
     * findByCriteria.
     * </p>
     *
     * @param criteria
     *            a {@link java.lang.String} object.
     * @param isCaseSens
     *            a boolean.
     * @return a {@link java.util.List} object.
     */
    List<File> findByCriteria(String criteria, boolean isCaseSens);

    /**
     * <p>
     * list.
     * </p>
     *
     * @return a {@link java.util.List} object.
     */
    List<File> list();
}
