package net.sourceforge.seqware.common.business;

import java.util.List;
import java.util.SortedSet;
import net.sourceforge.seqware.common.dao.ExperimentDAO;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Study;

/**
 * <p>
 * ExperimentService interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface ExperimentService {

    /** Constant <code>NAME="ExperimentService"</code> */
    String NAME = "ExperimentService";

    /**
     * <p>
     * setExperimentDAO.
     * </p>
     * 
     * @param experimentDAO
     *            a {@link net.sourceforge.seqware.common.dao.ExperimentDAO} object.
     */
    void setExperimentDAO(ExperimentDAO experimentDAO);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param experiment
     *            a {@link net.sourceforge.seqware.common.model.Experiment} object.
     * @return a {@link java.lang.Integer} object.
     */
    Integer insert(Experiment experiment);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param experiment
     *            a {@link net.sourceforge.seqware.common.model.Experiment} object.
     * @return a {@link java.lang.Integer} object.
     */
    Integer insert(Registration registration, Experiment experiment);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param experiment
     *            a {@link net.sourceforge.seqware.common.model.Experiment} object.
     */
    void update(Experiment experiment);

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
     * @param experiment
     * @param deleteRealFiles
     */
    void delete(Experiment experiment, boolean deleteRealFiles);

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
     * @param name
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.Experiment} object.
     */
    Experiment findByTitle(String name);

    /**
     * <p>
     * findByID.
     * </p>
     * 
     * @param expID
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Experiment} object.
     */
    Experiment findByID(Integer expID);

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
     * hasTitleBeenUsed.
     * </p>
     * 
     * @param oldName
     *            a {@link java.lang.String} object.
     * @param newName
     *            a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean hasTitleBeenUsed(String oldName, String newName);

    /**
     * <p>
     * getFiles.
     * </p>
     * 
     * @param expId
     *            a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    List<File> getFiles(Integer expId);

    /**
     * <p>
     * isHasFile.
     * </p>
     * 
     * @param expId
     *            a {@link java.lang.Integer} object.
     * @return a boolean.
     */
    boolean isHasFile(Integer expId);

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
     * setWithHasFile.
     * </p>
     * 
     * @param list
     *            a {@link java.util.SortedSet} object.
     * @return a {@link java.util.SortedSet} object.
     */
    SortedSet<Experiment> setWithHasFile(SortedSet<Experiment> list);

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
    SortedSet<Experiment> listWithHasFile(SortedSet<Experiment> list, String metaType);

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

// ex:sw=4:ts=4:
