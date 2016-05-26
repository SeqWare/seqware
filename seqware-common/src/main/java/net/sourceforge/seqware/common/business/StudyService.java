package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.StudyDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;

/**
 * <p>
 * StudyService interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface StudyService {

    /** Constant <code>NAME="StudyService"</code> */
    String NAME = "StudyService";

    /**
     * <p>
     * setStudyDAO.
     * </p>
     * 
     * @param studyDAO
     *            a {@link net.sourceforge.seqware.common.dao.StudyDAO} object.
     */
    void setStudyDAO(StudyDAO studyDAO);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param study
     *            a {@link net.sourceforge.seqware.common.model.Study} object.
     * @return a {@link java.lang.Integer} object.
     */
    Integer insert(Study study);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param study
     *            a {@link net.sourceforge.seqware.common.model.Study} object.
     * @return a {@link java.lang.Integer} object.
     */
    Integer insert(Registration registration, Study study);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param study
     *            a {@link net.sourceforge.seqware.common.model.Study} object.
     */
    void update(Study study);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param study
     *            a {@link net.sourceforge.seqware.common.model.Study} object.
     */
    void update(Registration registration, Study study);

    /**
     * <p>
     * updateOwners.
     * </p>
     * 
     * @param swAccession
     *            a {@link java.lang.Integer} object.
     */
    void updateOwners(Integer swAccession);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param study
     * @param deleteRealFiles
     */
    void delete(Study study, boolean deleteRealFiles);

    /**
     * <p>
     * merge.
     * </p>
     * 
     * @param study
     *            a {@link net.sourceforge.seqware.common.model.Study} object.
     */
    void merge(Study study);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @return a {@link java.util.List} object.
     */
    List<Study> list(Registration registration);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param isAcs
     *            a {@link java.lang.Boolean} object.
     * @return a {@link java.util.List} object.
     */
    List<Study> list(Registration registration, Boolean isAcs);

    /**
     * <p>
     * listMyShared.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param isAcs
     *            a {@link java.lang.Boolean} object.
     * @return a {@link java.util.List} object.
     */
    List<Study> listMyShared(Registration registration, Boolean isAcs);

    /**
     * <p>
     * listSharedWithMe.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @return a {@link java.util.List} object.
     */
    List<Study> listSharedWithMe(Registration registration);

    /**
     * <p>
     * listSharedWithMe.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param isAcs
     *            a {@link java.lang.Boolean} object.
     * @return a {@link java.util.List} object.
     */
    List<Study> listSharedWithMe(Registration registration, Boolean isAcs);

    /**
     * <p>
     * findByTitle.
     * </p>
     * 
     * @param title
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.Study} object.
     */
    List<Study> findByTitle(String title);

    /**
     * <p>
     * findByID.
     * </p>
     * 
     * @param expID
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Study} object.
     */
    Study findByID(Integer expID);

    /**
     * <p>
     * findBySWAccession.
     * </p>
     * 
     * @param swAccession
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Study} object.
     */
    Study findBySWAccession(Integer swAccession);

    /**
     * <p>
     * findByOwnerID.
     * </p>
     * 
     * @param registrationId
     *            a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    List<Study> findByOwnerID(Integer registrationId);

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
    List<Study> findByCriteria(String criteria, boolean isCaseSens);

    /**
     * <p>
     * findByCriteria.
     * </p>
     * 
     * @param criteria
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    List<Study> findByCriteria(String criteria);

    /**
     * <p>
     * hasTitleBeenUsed.
     * </p>
     * 
     * @param oldTitle
     *            a {@link java.lang.String} object.
     * @param newTitle
     *            a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean hasTitleBeenUsed(String oldTitle, String newTitle);

    /**
     * <p>
     * getFiles.
     * </p>
     * 
     * @param studyId
     *            a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    List<File> getFiles(Integer studyId);

    /**
     * <p>
     * isHasFile.
     * </p>
     * 
     * @param studyId
     *            a {@link java.lang.Integer} object.
     * @return a boolean.
     */
    boolean isHasFile(Integer studyId);

    /**
     * <p>
     * listWithHasFile.
     * </p>
     * 
     * @param list
     *            a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
    List<Study> listWithHasFile(List<Study> list);

    /**
     * <p>
     * getFiles.
     * </p>
     * 
     * @param studyId
     *            a {@link java.lang.Integer} object.
     * @param metaType
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    List<File> getFiles(Integer studyId, String metaType);

    /**
     * <p>
     * isHasFile.
     * </p>
     * 
     * @param studyId
     *            a {@link java.lang.Integer} object.
     * @param metaType
     *            a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean isHasFile(Integer studyId, String metaType);

    /**
     * <p>
     * listWithHasFile.
     * </p>
     * 
     * @param list
     *            a {@link java.util.List} object.
     * @param metaType
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    List<Study> listWithHasFile(List<Study> list, String metaType);

    /**
     * <p>
     * listStudyHasFile.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param metaType
     *            a {@link java.lang.String} object.
     * @param isAcs
     *            a {@link java.lang.Boolean} object.
     * @return a {@link java.util.List} object.
     */
    List<Study> listStudyHasFile(Registration registration, String metaType, Boolean isAcs);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param study
     *            a {@link net.sourceforge.seqware.common.model.Study} object.
     * @return a {@link net.sourceforge.seqware.common.model.Study} object.
     */
    Study updateDetached(Study study);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param study
     *            a {@link net.sourceforge.seqware.common.model.Study} object.
     * @return a {@link net.sourceforge.seqware.common.model.Study} object.
     */
    Study updateDetached(Registration registration, Study study);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<Study> list();

    /**
     * <p>
     * getRunningCount.
     * </p>
     * 
     * @param study
     *            a {@link net.sourceforge.seqware.common.model.Study} object.
     * @return a int.
     */
    int getRunningCount(Study study);

    /**
     * <p>
     * getFinishedCount.
     * </p>
     * 
     * @param study
     *            a {@link net.sourceforge.seqware.common.model.Study} object.
     * @return a int.
     */
    int getFinishedCount(Study study);

    /**
     * <p>
     * getFailedCount.
     * </p>
     * 
     * @param study
     *            a {@link net.sourceforge.seqware.common.model.Study} object.
     * @return a int.
     */
    int getFailedCount(Study study);
}

// ex:sw=4:ts=4:
