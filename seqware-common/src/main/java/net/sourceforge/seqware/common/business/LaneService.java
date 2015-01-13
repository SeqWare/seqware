package net.sourceforge.seqware.common.business;

import java.util.List;
import java.util.SortedSet;
import net.sourceforge.seqware.common.dao.LaneDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>
 * LaneService interface.
 * </p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface LaneService {

    /** Constant <code>NAME="laneService"</code> */
    public static final String NAME = "laneService";

    /**
     * <p>
     * setLaneDAO.
     * </p>
     *
     * @param laneDAO
     *            a {@link net.sourceforge.seqware.common.dao.LaneDAO} object.
     */
    public void setLaneDAO(LaneDAO laneDAO);

    /**
     * <p>
     * insert.
     * </p>
     *
     * @param lane
     *            a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public void insert(Lane lane);

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
    public Integer insert(Registration registration, Lane lane);

    /**
     * <p>
     * update.
     * </p>
     *
     * @param lane
     *            a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public void update(Lane lane);

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
    public void update(Registration registration, Lane lane);

    /**
     * <p>
     * delete.
     * </p>
     *
     * @param lane
     * @param deleteRealFiles
     */
    public void delete(Lane lane, boolean deleteRealFiles);

    /**
     * <p>
     * findByName.
     * </p>
     *
     * @param name
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public Lane findByName(String name);

    /**
     * <p>
     * findByID.
     * </p>
     *
     * @param laneID
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public Lane findByID(Integer laneID);

    /**
     * <p>
     * findBySWAccession.
     * </p>
     *
     * @param swAccession
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public Lane findBySWAccession(Integer swAccession);

    /**
     * <p>
     * listFile.
     * </p>
     *
     * @param reqistration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param typeNode
     *            a {@link java.lang.String} object.
     * @param list
     *            a {@link java.util.List} object.
     * @param ids
     *            an array of {@link java.lang.String} objects.
     * @param statuses
     *            an array of {@link java.lang.String} objects.
     * @return a {@link java.util.List} object.
     */
    public List<File> listFile(Registration reqistration, String typeNode, List<File> list, String[] ids, String[] statuses);

    /**
     * <p>
     * getFiles.
     * </p>
     *
     * @param laneId
     *            a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    public List<File> getFiles(Integer laneId);

    /**
     * <p>
     * isHasFile.
     * </p>
     *
     * @param laneId
     *            a {@link java.lang.Integer} object.
     * @return a boolean.
     */
    public boolean isHasFile(Integer laneId);

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
    public List<File> getFiles(Integer studyId, String metaType);

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
    public boolean isHasFile(Integer studyId, String metaType);

    /**
     * <p>
     * setWithHasFile.
     * </p>
     *
     * @param list
     *            a {@link java.util.SortedSet} object.
     * @return a {@link java.util.SortedSet} object.
     */
    public SortedSet<Lane> setWithHasFile(SortedSet<Lane> list);

    /**
     * <p>
     * listWithHasFile.
     * </p>
     *
     * @param list
     *            a {@link java.util.SortedSet} object.
     * @param metaType
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.SortedSet} object.
     */
    public SortedSet<Lane> listWithHasFile(SortedSet<Lane> list, String metaType);

    /**
     * <p>
     * list.
     * </p>
     *
     * @param laneIds
     *            a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
    public List<Lane> list(List<Integer> laneIds);

    /**
     * <p>
     * updateDetached.
     * </p>
     *
     * @param lane
     *            a {@link net.sourceforge.seqware.common.model.Lane} object.
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public Lane updateDetached(Lane lane);

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
    public Lane updateDetached(Registration registration, Lane lane);

    /**
     * <p>
     * findByOwnerID.
     * </p>
     *
     * @param registrationId
     *            a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    public List<Lane> findByOwnerID(Integer registrationId);

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
    public List<Lane> findByCriteria(String criteria, boolean isCaseSens);

    /**
     * <p>
     * list.
     * </p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Lane> list();
}

// ex:sw=4:ts=4:
