package net.sourceforge.seqware.common.dao;

import java.util.List;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;

/**
 * <p>
 * IUSDAO interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface IUSDAO {

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param obj
     *            a {@link net.sourceforge.seqware.common.model.IUS} object.
     * @return
     */
    Integer insert(IUS obj);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param obj
     *            a {@link net.sourceforge.seqware.common.model.IUS} object.
     * @return
     */
    Integer insert(Registration registration, IUS obj);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param obj
     *            a {@link net.sourceforge.seqware.common.model.IUS} object.
     */
    void update(IUS obj);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param ius
     *            a {@link net.sourceforge.seqware.common.model.IUS} object.
     */
    void update(Registration registration, IUS ius);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param obj
     *            a {@link net.sourceforge.seqware.common.model.IUS} object.
     */
    void delete(IUS obj);

    /**
     * <p>
     * findByID.
     * </p>
     * 
     * @param id
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.IUS} object.
     */
    IUS findByID(Integer id);

    /**
     * <p>
     * getFiles.
     * </p>
     * 
     * @param iusId
     *            a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    List<File> getFiles(Integer iusId);

    /**
     * <p>
     * isHasFile.
     * </p>
     * 
     * @param iusId
     *            a {@link java.lang.Integer} object.
     * @return a boolean.
     */
    boolean isHasFile(Integer iusId);

    /**
     * <p>
     * getFiles.
     * </p>
     * 
     * @param iusId
     *            a {@link java.lang.Integer} object.
     * @param metaType
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    List<File> getFiles(Integer iusId, String metaType);

    /**
     * <p>
     * isHasFile.
     * </p>
     * 
     * @param iusId
     *            a {@link java.lang.Integer} object.
     * @param metaType
     *            a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean isHasFile(Integer iusId, String metaType);

    /**
     * <p>
     * findBySWAccession.
     * </p>
     * 
     * @param swAccession
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.IUS} object.
     */
    IUS findBySWAccession(Integer swAccession);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param ius
     *            a {@link net.sourceforge.seqware.common.model.IUS} object.
     * @return a {@link net.sourceforge.seqware.common.model.IUS} object.
     */
    IUS updateDetached(IUS ius);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param ius
     *            a {@link net.sourceforge.seqware.common.model.IUS} object.
     * @return a {@link net.sourceforge.seqware.common.model.IUS} object.
     */
    IUS updateDetached(Registration registration, IUS ius);

    /**
     * <p>
     * findByOwnerID.
     * </p>
     * 
     * @param registrationId
     *            a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    List<IUS> findByOwnerID(Integer registrationId);

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
    List<IUS> findByCriteria(String criteria, boolean isCaseSens);

    /**
     * <p>
     * findBelongsToStudy.
     * </p>
     * 
     * @param study
     *            a {@link net.sourceforge.seqware.common.model.Study} object.
     * @return a {@link java.util.List} object.
     */
    List<IUS> findBelongsToStudy(Study study);

    /**
     * <p>
     * find.
     * </p>
     * 
     * @param sequencerRunName
     *            a {@link java.lang.String} object.
     * @param lane
     *            a {@link java.lang.Integer} object.
     * @param sampleName
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    List<IUS> find(String sequencerRunName, Integer lane, String sampleName);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<IUS> list();
}
