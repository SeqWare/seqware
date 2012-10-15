package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunWizardDTO;

/**
 * <p>SequencerRunDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface SequencerRunDAO {

    /**
     * <p>insert.</p>
     *
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     */
    public void insert(SequencerRun sequencerRun);

    /**
     * <p>insert.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     */
    public void insert(Registration registration, SequencerRun sequencerRun);

    /**
     * <p>insert.</p>
     *
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRunWizardDTO} object.
     */
    public void insert(SequencerRunWizardDTO sequencerRun);

    /**
     * <p>insert.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRunWizardDTO} object.
     */
    public void insert(Registration registration, SequencerRunWizardDTO sequencerRun);

    /**
     * <p>update.</p>
     *
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     */
    public void update(SequencerRun sequencerRun);

    /**
     * <p>update.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     */
    public void update(Registration registration, SequencerRun sequencerRun);

    /**
     * <p>delete.</p>
     *
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     */
    public void delete(SequencerRun sequencerRun);

    /**
     * <p>list.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param isAsc a {@link java.lang.Boolean} object.
     * @return a {@link java.util.List} object.
     */
    public List<SequencerRun> list(Registration registration, Boolean isAsc);

    /**
     * <p>findByName.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     */
    public SequencerRun findByName(String name);

    /**
     * <p>findByID.</p>
     *
     * @param expID a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     */
    public SequencerRun findByID(Integer expID);

    /**
     * <p>getProcessedCnt.</p>
     *
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getProcessedCnt(SequencerRun sequencerRun);

    /**
     * <p>getProcessingCnt.</p>
     *
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getProcessingCnt(SequencerRun sequencerRun);

    /**
     * <p>getErrorCnt.</p>
     *
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer getErrorCnt(SequencerRun sequencerRun);

    /**
     * <p>getProcStatuses.</p>
     *
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     * @return a {@link java.util.List} object.
     */
    public List<Integer> getProcStatuses(SequencerRun sequencerRun);

    /**
     * <p>findBySWAccession.</p>
     *
     * @param swAccession a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     */
    public SequencerRun findBySWAccession(Integer swAccession);

    /**
     * <p>updateDetached.</p>
     *
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     * @return a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     */
    public SequencerRun updateDetached(SequencerRun sequencerRun);
    
    /**
     * <p>updateDetached.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     * @return a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     */
    public SequencerRun updateDetached(Registration registration, SequencerRun sequencerRun);

    /**
     * <p>findByOwnerID.</p>
     *
     * @param registrationId a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    public List<SequencerRun> findByOwnerID(Integer registrationId);

    /**
     * <p>findByCriteria.</p>
     *
     * @param criteria a {@link java.lang.String} object.
     * @param isCaseSens a boolean.
     * @return a {@link java.util.List} object.
     */
    public List<SequencerRun> findByCriteria(String criteria, boolean isCaseSens);

    /**
     * <p>list.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<SequencerRun> list();
}
