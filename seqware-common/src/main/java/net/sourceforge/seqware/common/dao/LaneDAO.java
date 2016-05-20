package net.sourceforge.seqware.common.dao;

import java.util.List;
import java.util.SortedSet;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>
 * LaneDAO interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface LaneDAO {

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param lane
     *            a {@link net.sourceforge.seqware.common.model.Lane} object.
     * @return
     */
    Integer insert(Lane lane);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param lane
     *            a {@link net.sourceforge.seqware.common.model.Lane} object.
     * @return
     */
    Integer insert(Registration registration, Lane lane);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param lane
     *            a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    void update(Lane lane);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param lane
     *            a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    void update(Registration registration, Lane lane);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param lane
     *            a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    void delete(Lane lane);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param lanes
     *            a {@link java.util.SortedSet} object.
     */
    void delete(SortedSet<Lane> lanes);

    /**
     * <p>
     * findByName.
     * </p>
     * 
     * @param name
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    Lane findByName(String name);

    /**
     * <p>
     * findByID.
     * </p>
     * 
     * @param laneId
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    Lane findByID(Integer laneId);

    /**
     * <p>
     * getFiles.
     * </p>
     * 
     * @param laneId
     *            a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    List<File> getFiles(Integer laneId);

    /**
     * <p>
     * isHasFile.
     * </p>
     * 
     * @param laneId
     *            a {@link java.lang.Integer} object.
     * @return a boolean.
     */
    boolean isHasFile(Integer laneId);

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
     * list.
     * </p>
     * 
     * @param laneIds
     *            a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
    List<Lane> list(List<Integer> laneIds);

    /**
     * <p>
     * findBySWAccession.
     * </p>
     * 
     * @param swAccession
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    Lane findBySWAccession(Integer swAccession);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param lane
     *            a {@link net.sourceforge.seqware.common.model.Lane} object.
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    Lane updateDetached(Lane lane);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param lane
     *            a {@link net.sourceforge.seqware.common.model.Lane} object.
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    Lane updateDetached(Registration registration, Lane lane);

    /**
     * <p>
     * findByOwnerID.
     * </p>
     * 
     * @param registrationId
     *            a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    List<Lane> findByOwnerID(Integer registrationId);

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
    List<Lane> findByCriteria(String criteria, boolean isCaseSens);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<Lane> list();
}
