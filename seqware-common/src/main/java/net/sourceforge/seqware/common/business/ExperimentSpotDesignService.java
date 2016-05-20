package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ExperimentSpotDesignDAO;
import net.sourceforge.seqware.common.model.ExperimentSpotDesign;

/**
 * <p>
 * ExperimentSpotDesignService interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface ExperimentSpotDesignService {
    /** Constant <code>NAME="ExperimentSpotDesignService"</code> */
    String NAME = "ExperimentSpotDesignService";

    /**
     * <p>
     * setExperimentSpotDesignDAO.
     * </p>
     * 
     * @param dao
     *            a {@link net.sourceforge.seqware.common.dao.ExperimentSpotDesignDAO} object.
     */
    void setExperimentSpotDesignDAO(ExperimentSpotDesignDAO dao);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param obj
     *            a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesign} object.
     */
    void insert(ExperimentSpotDesign obj);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param obj
     *            a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesign} object.
     */
    void update(ExperimentSpotDesign obj);

    /**
     * <p>
     * findByID.
     * </p>
     * 
     * @param expID
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesign} object.
     */
    ExperimentSpotDesign findByID(Integer expID);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param experiment
     *            a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesign} object.
     * @return a {@link net.sourceforge.seqware.common.model.ExperimentSpotDesign} object.
     */
    ExperimentSpotDesign updateDetached(ExperimentSpotDesign experiment);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<ExperimentSpotDesign> list();

}

// ex:sw=4:ts=4:
