package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ProcessingLanesDAO;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingLanes;

/**
 * <p>
 * ProcessingLanesService interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface ProcessingLanesService {

    /**
     * <p>
     * setProcessingLanesDAO.
     * </p>
     * 
     * @param dao
     *            a {@link net.sourceforge.seqware.common.dao.ProcessingLanesDAO} object.
     */
    void setProcessingLanesDAO(ProcessingLanesDAO dao);

    /**
     * <p>
     * findByProcessingLane.
     * </p>
     * 
     * @param processing
     *            a {@link net.sourceforge.seqware.common.model.Processing} object.
     * @param lane
     *            a {@link net.sourceforge.seqware.common.model.Lane} object.
     * @return a {@link net.sourceforge.seqware.common.model.ProcessingLanes} object.
     */
    ProcessingLanes findByProcessingLane(Processing processing, Lane lane);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param processingLanes
     *            a {@link net.sourceforge.seqware.common.model.ProcessingLanes} object.
     */
    void delete(ProcessingLanes processingLanes);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param processingLanes
     *            a {@link net.sourceforge.seqware.common.model.ProcessingLanes} object.
     */
    void update(ProcessingLanes processingLanes);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param processingLanes
     *            a {@link net.sourceforge.seqware.common.model.ProcessingLanes} object.
     */
    void insert(ProcessingLanes processingLanes);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param processingLanes
     *            a {@link net.sourceforge.seqware.common.model.ProcessingLanes} object.
     * @return a {@link net.sourceforge.seqware.common.model.ProcessingLanes} object.
     */
    ProcessingLanes updateDetached(ProcessingLanes processingLanes);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<ProcessingLanes> list();

}
