package net.sourceforge.seqware.common.dao.hibernate;

import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;

import java.util.List;

/**
 * <p>
 * LaneLinkDAO interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface LaneLinkDAO {

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param laneAttribute
     *            a {@link net.sourceforge.seqware.common.model.LaneAttribute} object.
     */
    void insert(LaneAttribute laneAttribute);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param laneAttribute
     *            a {@link net.sourceforge.seqware.common.model.LaneAttribute} object.
     */
    void update(LaneAttribute laneAttribute);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param laneAttribute
     *            a {@link net.sourceforge.seqware.common.model.LaneAttribute} object.
     */
    void delete(LaneAttribute laneAttribute);

    /**
     * <p>
     * findAll.
     * </p>
     * 
     * @param lane
     *            a {@link net.sourceforge.seqware.common.model.Lane} object.
     * @return a {@link java.util.List} object.
     */
    @SuppressWarnings("unchecked")
    List<LaneAttribute> findAll(Lane lane);

}
