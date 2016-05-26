package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ExperimentLibraryDesignDAO;
import net.sourceforge.seqware.common.model.ExperimentLibraryDesign;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>
 * ExperimentLibraryDesignService interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface ExperimentLibraryDesignService {
    /** Constant <code>NAME="ExperimentLibraryDesignService"</code> */
    String NAME = "ExperimentLibraryDesignService";

    /**
     * <p>
     * setExperimentLibraryDesignDAO.
     * </p>
     * 
     * @param dao
     *            a {@link net.sourceforge.seqware.common.dao.ExperimentLibraryDesignDAO} object.
     */
    void setExperimentLibraryDesignDAO(ExperimentLibraryDesignDAO dao);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param obj
     *            a {@link net.sourceforge.seqware.common.model.ExperimentLibraryDesign} object.
     */
    void insert(ExperimentLibraryDesign obj);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param obj
     *            a {@link net.sourceforge.seqware.common.model.ExperimentLibraryDesign} object.
     */
    void update(ExperimentLibraryDesign obj);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<ExperimentLibraryDesign> list();

    /**
     * <p>
     * list.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @return a {@link java.util.List} object.
     */
    List<ExperimentLibraryDesign> list(Registration registration);

    /**
     * <p>
     * findByID.
     * </p>
     * 
     * @param expID
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.ExperimentLibraryDesign} object.
     */
    ExperimentLibraryDesign findByID(Integer expID);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param eld
     *            a {@link net.sourceforge.seqware.common.model.ExperimentLibraryDesign} object.
     * @return a {@link net.sourceforge.seqware.common.model.ExperimentLibraryDesign} object.
     */
    ExperimentLibraryDesign updateDetached(ExperimentLibraryDesign eld);

}

// ex:sw=4:ts=4:
