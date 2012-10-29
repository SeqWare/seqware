package net.sourceforge.seqware.common.business;

import java.util.List;
import java.util.SortedSet;

import net.sourceforge.seqware.common.dao.SampleDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.module.ReturnValue;

/**
 * <p>SampleService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface SampleService {

    /** Constant <code>NAME="sampleService"</code> */
    public static final String NAME = "sampleService";

    /**
     * <p>setSampleDAO.</p>
     *
     * @param sampleDAO a {@link net.sourceforge.seqware.common.dao.SampleDAO} object.
     */
    public void setSampleDAO(SampleDAO sampleDAO);

    /**
     * <p>insert.</p>
     *
     * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer insert(Sample sample);

    /**
     * <p>insert.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer insert(Registration registration, Sample sample);

    /**
     * <p>update.</p>
     *
     * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public void update(Sample sample);

    /**
     * <p>update.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public void update(Registration registration, Sample sample);

    /**
     * <p>delete.</p>
     *
     * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
     * @param deleteRealFiles a {@link java.lang.String} object.
     */
    public void delete(Sample sample, String deleteRealFiles);

    /**
     * <p>findByName.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public Sample findByName(String name);

    public List<Sample> matchName(String name);
    
    /**
     * <p>findByTitle.</p>
     *
     * @param title a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public Sample findByTitle(String title);

    /**
     * <p>findByID.</p>
     *
     * @param id a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public Sample findByID(Integer id);

    /**
     * <p>findBySWAccession.</p>
     *
     * @param swAccession a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public Sample findBySWAccession(Integer swAccession);

    /**
     * <p>listComplete.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Sample> listComplete();

    /**
     * <p>listIncomplete.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Sample> listIncomplete();

    /**
     * <p>hasNameBeenUsed.</p>
     *
     * @param oldName a {@link java.lang.String} object.
     * @param newName a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean hasNameBeenUsed(String oldName, String newName);

    /**
     * <p>listSample.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @return a {@link java.util.List} object.
     */
    public List<Sample> listSample(Registration registration);

    /**
     * <p>getFiles.</p>
     *
     * @param sampleId a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    public List<File> getFiles(Integer sampleId);

    /**
     * <p>isHasFile.</p>
     *
     * @param sampleId a {@link java.lang.Integer} object.
     * @return a boolean.
     */
    public boolean isHasFile(Integer sampleId);

    /**
     * <p>getFiles.</p>
     *
     * @param studyId a {@link java.lang.Integer} object.
     * @param metaType a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public List<File> getFiles(Integer studyId, String metaType);

    /**
     * <p>findFiles.</p>
     *
     * @param swAccession a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    public List<ReturnValue> findFiles(Integer swAccession);

    /**
     * <p>isHasFile.</p>
     *
     * @param studyId a {@link java.lang.Integer} object.
     * @param metaType a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean isHasFile(Integer studyId, String metaType);

    /**
     * <p>setWithHasFile.</p>
     *
     * @param expId a {@link java.lang.Integer} object.
     * @param list a {@link java.util.SortedSet} object.
     * @return a {@link java.util.SortedSet} object.
     */
    public SortedSet<Sample> setWithHasFile(Integer expId, SortedSet<Sample> list);

    /**
     * <p>listWithHasFile.</p>
     *
     * @param expId a {@link java.lang.Integer} object.
     * @param list a {@link java.util.SortedSet} object.
     * @param metaType a {@link java.lang.String} object.
     * @return a {@link java.util.SortedSet} object.
     */
    public SortedSet<Sample> listWithHasFile(Integer expId, SortedSet<Sample> list, String metaType);

    /**
     * <p>updateDetached.</p>
     *
     * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public Sample updateDetached(Sample sample);
    
    /**
     * <p>updateDetached.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public Sample updateDetached(Registration registration, Sample sample);

    /**
     * <p>findByOwnerID.</p>
     *
     * @param registrationId a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    public List<Sample> findByOwnerID(Integer registrationId);

    /**
     * <p>findByCriteria.</p>
     *
     * @param criteria a {@link java.lang.String} object.
     * @param isCaseSens a boolean.
     * @return a {@link java.util.List} object.
     */
    public List<Sample> findByCriteria(String criteria, boolean isCaseSens);

    /**
     * <p>getRootSamples.</p>
     *
     * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
     * @return a {@link java.util.List} object.
     */
    public List<Sample> getRootSamples(Study study);

    /**
     * <p>getRootSample.</p>
     *
     * @param childSample a {@link net.sourceforge.seqware.common.model.Sample} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public Sample getRootSample(Sample childSample);

    /**
     * <p>list.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Sample> list();
}

// ex:sw=4:ts=4:
