package net.sourceforge.seqware.common.dao;

import java.util.List;
import java.util.Map;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.module.ReturnValue;

/**
 * <p>SampleDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface SampleDAO {

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
     */
    public void delete(Sample sample);

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
     * <p>findByName.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public Sample findByName(String name);

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
     * @param sampleId a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public Sample findByID(Integer sampleId);

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
     * <p>findFiles.</p>
     *
     * @param swAccession a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    public List<ReturnValue> findFiles(Integer swAccession);

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
     * @param sampleId a {@link java.lang.Integer} object.
     * @param metaType a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public List<File> getFiles(Integer sampleId, String metaType);

    /**
     * <p>isHasFile.</p>
     *
     * @param sampleId a {@link java.lang.Integer} object.
     * @param metaType a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean isHasFile(Integer sampleId, String metaType);

    /**
     * <p>getCountFiles.</p>
     *
     * @param expId a {@link java.lang.Integer} object.
     * @return a {@link java.util.Map} object.
     */
    public Map<Integer, Integer> getCountFiles(Integer expId);

    /**
     * <p>getCountFiles.</p>
     *
     * @param expId a {@link java.lang.Integer} object.
     * @param metaType a {@link java.lang.String} object.
     * @return a {@link java.util.Map} object.
     */
    public Map<Integer, Integer> getCountFiles(Integer expId, String metaType);

    /**
     * <p>findBySWAccession.</p>
     *
     * @param swAccession a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public Sample findBySWAccession(Integer swAccession);

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
     * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
     * @return a {@link net.sourceforge.seqware.common.model.Sample} object.
     */
    public Sample getRootSample(Sample sample);

    /**
     * <p>list.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Sample> list();
}
