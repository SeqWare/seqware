package net.sourceforge.seqware.common.dao;

import java.util.List;
import java.util.SortedSet;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.module.ReturnValue;

/**
 * <p>LaneDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface LaneDAO {

    /**
     * <p>insert.</p>
     *
     * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public Integer insert(Lane lane);

    /**
     * <p>insert.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public Integer insert(Registration registration, Lane lane);

    /**
     * <p>update.</p>
     *
     * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public void update(Lane lane);

    /**
     * <p>update.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public void update(Registration registration, Lane lane);

    /**
     * <p>delete.</p>
     *
     * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public void delete(Lane lane);

    /**
     * <p>delete.</p>
     *
     * @param lanes a {@link java.util.SortedSet} object.
     */
    public void delete(SortedSet<Lane> lanes);

    /**
     * <p>findByName.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public Lane findByName(String name);

    /**
     * <p>findByID.</p>
     *
     * @param laneId a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public Lane findByID(Integer laneId);

    /**
     * <p>getFiles.</p>
     *
     * @param laneId a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    public List<File> getFiles(Integer laneId);

    /**
     * <p>isHasFile.</p>
     *
     * @param laneId a {@link java.lang.Integer} object.
     * @return a boolean.
     */
    public boolean isHasFile(Integer laneId);

    /**
     * <p>getFiles.</p>
     *
     * @param studyId a {@link java.lang.Integer} object.
     * @param metaType a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public List<File> getFiles(Integer studyId, String metaType);

    /**
     * <p>isHasFile.</p>
     *
     * @param studyId a {@link java.lang.Integer} object.
     * @param metaType a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean isHasFile(Integer studyId, String metaType);

    /**
     * <p>list.</p>
     *
     * @param laneIds a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
    public List<Lane> list(List<Integer> laneIds);

    /**
     * <p>findBySWAccession.</p>
     *
     * @param swAccession a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public Lane findBySWAccession(Integer swAccession);

    /**
     * <p>updateDetached.</p>
     *
     * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public Lane updateDetached(Lane lane);
    
    /**
     * <p>updateDetached.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
     * @return a {@link net.sourceforge.seqware.common.model.Lane} object.
     */
    public Lane updateDetached(Registration registration, Lane lane);

    /**
     * <p>findByOwnerID.</p>
     *
     * @param registrationId a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    public List<Lane> findByOwnerID(Integer registrationId);

    /**
     * <p>findByCriteria.</p>
     *
     * @param criteria a {@link java.lang.String} object.
     * @param isCaseSens a boolean.
     * @return a {@link java.util.List} object.
     */
    public List<Lane> findByCriteria(String criteria, boolean isCaseSens);

    /**
     * <p>findFiles.</p>
     *
     * @param swAccession a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    public List<ReturnValue> findFiles(Integer swAccession);

    /**
     * <p>list.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Lane> list();
}
