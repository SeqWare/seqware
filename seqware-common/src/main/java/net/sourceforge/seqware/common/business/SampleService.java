package net.sourceforge.seqware.common.business;

import java.util.List;
import java.util.SortedSet;
import net.sourceforge.seqware.common.dao.SampleDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;

/**
 * <p>
 * SampleService interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface SampleService {

    /** Constant <code>NAME="sampleService"</code> */
    String NAME = "sampleService";

    /**
     * <p>
     * setSampleDAO.
     * </p>
     * 
     * @param sampleDAO
     *            a {@link net.sourceforge.seqware.common.dao.SampleDAO} object.
     */
    void setSampleDAO(SampleDAO sampleDAO);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param sample
     *            a {@link net.sourceforge.seqware.common.model.Sample} object.
     * @return a {@link java.lang.Integer} object.
     */
    Integer insert(Sample sample);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param sample
     *            a {@link net.sourceforge.seqware.common.model.Sample} object.
     * @return a {@link java.lang.Integer} object.
     */
    Integer insert(Registration registration, Sample sample);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param sample
     *            a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    void update(Sample sample);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param sample
     *            a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    void update(Registration registration, Sample sample);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param sample
     * @param deleteRealFiles
     */
    void delete(Sample sample, boolean deleteRealFiles);

    /**
     * <p>
     * findByName.
     * </p>
     * 
     * @param name
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    Sample findByName(String name);

    List<Sample> matchName(String name);

    /**
     * <p>
     * findByTitle.
     * </p>
     * 
     * @param title
     *            a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    Sample findByTitle(String title);

    /**
     * <p>
     * findByID.
     * </p>
     * 
     * @param id
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    Sample findByID(Integer id);

    /**
     * <p>
     * findBySWAccession.
     * </p>
     * 
     * @param swAccession
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    Sample findBySWAccession(Integer swAccession);

    /**
     * <p>
     * listComplete.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<Sample> listComplete();

    /**
     * <p>
     * listIncomplete.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<Sample> listIncomplete();

    /**
     * <p>
     * hasNameBeenUsed.
     * </p>
     * 
     * @param oldName
     *            a {@link java.lang.String} object.
     * @param newName
     *            a {@link java.lang.String} object.
     * @return a boolean.
     */
    boolean hasNameBeenUsed(String oldName, String newName);

    /**
     * <p>
     * listSample.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @return a {@link java.util.List} object.
     */
    List<Sample> listSample(Registration registration);

    /**
     * <p>
     * getFiles.
     * </p>
     * 
     * @param sampleId
     *            a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    List<File> getFiles(Integer sampleId);

    /**
     * <p>
     * isHasFile.
     * </p>
     * 
     * @param sampleId
     *            a {@link java.lang.Integer} object.
     * @return a boolean.
     */
    boolean isHasFile(Integer sampleId);

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
     * @param expId
     *            a {@link java.lang.Integer} object.
     * @param list
     *            a {@link java.util.SortedSet} object.
     * @return a {@link java.util.SortedSet} object.
     */
    SortedSet<Sample> setWithHasFile(Integer expId, SortedSet<Sample> list);

    /**
     * <p>
     * listWithHasFile.
     * </p>
     * 
     * @param expId
     *            a {@link java.lang.Integer} object.
     * @param list
     *            a {@link java.util.SortedSet} object.
     * @param metaType
     *            a {@link java.lang.String} object.
     * @return a {@link java.util.SortedSet} object.
     */
    SortedSet<Sample> listWithHasFile(Integer expId, SortedSet<Sample> list, String metaType);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param sample
     *            a {@link net.sourceforge.seqware.common.model.Sample} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    Sample updateDetached(Sample sample);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param sample
     *            a {@link net.sourceforge.seqware.common.model.Sample} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    Sample updateDetached(Registration registration, Sample sample);

    /**
     * <p>
     * findByOwnerID.
     * </p>
     * 
     * @param registrationId
     *            a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    List<Sample> findByOwnerID(Integer registrationId);

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
    List<Sample> findByCriteria(String criteria, boolean isCaseSens);

    /**
     * <p>
     * getRootSamples.
     * </p>
     * 
     * @param study
     *            a {@link net.sourceforge.seqware.common.model.Study} object.
     * @return a {@link java.util.List} object.
     */
    List<Sample> getRootSamples(Study study);

    /**
     * <p>
     * getRootSample.
     * </p>
     * 
     * @param childSample
     *            a {@link net.sourceforge.seqware.common.model.Sample} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    Sample getRootSample(Sample childSample);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<Sample> list();
}

// ex:sw=4:ts=4:
