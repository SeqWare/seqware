package net.sourceforge.seqware.common.dao;

import java.util.List;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;

/**
 * <p>
 * ExperimentDAO interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface ExperimentDAO {

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param sequencerRun
     *            a {@link net.sourceforge.seqware.common.model.Experiment} object.
     * @return a {@link java.lang.Integer} object.
     */
    Integer insert(Experiment sequencerRun);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param sequencerRun
     *            a {@link net.sourceforge.seqware.common.model.Experiment} object.
     * @return a {@link java.lang.Integer} object.
     */
    Integer insert(Registration registration, Experiment sequencerRun);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param sequencerRun
     *            a {@link net.sourceforge.seqware.common.model.Experiment} object.
     */
    void update(Experiment sequencerRun);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param experiment
     *            a {@link net.sourceforge.seqware.common.model.Experiment} object.
     */
    void update(Registration registration, Experiment experiment);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param sequencerRun
     *            a {@link net.sourceforge.seqware.common.model.Experiment} object.
     */
    void delete(Experiment sequencerRun);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<Experiment> list();

    /**
     * <p>
     * list.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @return a {@link java.util.List} object.
     */
    List<Experiment> list(Registration registration);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @param study
     *            a {@link net.sourceforge.seqware.common.model.Study} object.
     * @return a {@link java.util.List} object.
     */
    List<Experiment> list(Study study);

    /**
     * <p>
     * findByTitle.
     * </p>
     * 
     * @param title
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.Experiment} object.
     */
    Experiment findByTitle(String title);

    /**
     * <p>
     * findByID.
     * </p>
     * 
     * @param studyID
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Experiment} object.
     */
    Experiment findByID(Integer studyID);

    /**
     * <p>
     * findBySWAccession.
     * </p>
     * 
     * @param swAccession
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Experiment} object.
     */
    Experiment findBySWAccession(Integer swAccession);

    /**
     * <p>
     * getFiles.
     * </p>
     * 
     * @param experimentId
     *            a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    List<File> getFiles(Integer experimentId);

    /**
     * <p>
     * isHasFile.
     * </p>
     * 
     * @param experimentId
     *            a {@link java.lang.Integer} object.
     * @return a boolean.
     */
    boolean isHasFile(Integer experimentId);

    /**
     * <p>
     * getFiles.
     * </p>
     * 
     * @param expId
     *            a {@link java.lang.Integer} object.
     * @param metaType
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    List<File> getFiles(Integer expId, String metaType);

    /**
     * <p>
     * isHasFile.
     * </p>
     * 
     * @param expId
     *            a {@link java.lang.Integer} object.
     * @param metaType
     *            a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean isHasFile(Integer expId, String metaType);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param experiment
     *            a {@link net.sourceforge.seqware.common.model.Experiment} object.
     * @return a {@link net.sourceforge.seqware.common.model.Experiment} object.
     */
    Experiment updateDetached(Experiment experiment);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param experiment
     *            a {@link net.sourceforge.seqware.common.model.Experiment} object.
     * @return a {@link net.sourceforge.seqware.common.model.Experiment} object.
     */
    Experiment updateDetached(Registration registration, Experiment experiment);

    /**
     * <p>
     * findByOwnerID.
     * </p>
     * 
     * @param registrationID
     *            a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    List<Experiment> findByOwnerID(Integer registrationID);

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
    List<Experiment> findByCriteria(String criteria, boolean isCaseSens);

    /**
     * <p>
     * merge.
     * </p>
     * 
     * @param experiment
     *            a {@link net.sourceforge.seqware.common.model.Experiment} object.
     */
    void merge(Experiment experiment);
}
