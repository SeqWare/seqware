package net.sourceforge.seqware.common.dao;

import java.util.List;
import net.sourceforge.seqware.common.model.ExperimentLibraryDesign;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>
 * ExperimentLibraryDesignDAO interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface ExperimentLibraryDesignDAO {
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
     * findByID.
     * </p>
     * 
     * @param id
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.ExperimentLibraryDesign} object.
     */
    ExperimentLibraryDesign findByID(Integer id);

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

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<ExperimentLibraryDesign> list();
}
