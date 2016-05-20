package net.sourceforge.seqware.common.dao;

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentAttribute;

import java.util.List;

/**
 * <p>
 * ExperimentAttributeDAO interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface ExperimentAttributeDAO {

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param experimentAttribute
     *            a {@link net.sourceforge.seqware.common.model.ExperimentAttribute} object.
     */
    void insert(ExperimentAttribute experimentAttribute);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param experimentAttribute
     *            a {@link net.sourceforge.seqware.common.model.ExperimentAttribute} object.
     */
    void update(ExperimentAttribute experimentAttribute);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param experimentAttribute
     *            a {@link net.sourceforge.seqware.common.model.ExperimentAttribute} object.
     */
    void delete(ExperimentAttribute experimentAttribute);

    /**
     * <p>
     * findAll.
     * </p>
     * 
     * @param experiment
     *            a {@link net.sourceforge.seqware.common.model.Experiment} object.
     * @return a {@link java.util.List} object.
     */
    @SuppressWarnings("unchecked") List<ExperimentAttribute> findAll(Experiment experiment);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<ExperimentAttribute> list();

}
