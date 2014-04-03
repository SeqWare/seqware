package net.sourceforge.seqware.common.business;

import java.util.List;

import net.sourceforge.seqware.common.dao.SequencerRunDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunWizardDTO;

/**
 * <p>SequencerRunService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface SequencerRunService {

    /** Constant <code>NAME="SequencerRunService"</code> */
    public static final String NAME = "SequencerRunService";

    /**
     * <p>setSequencerRunDAO.</p>
     *
     * @param sequencerRunDAO a {@link net.sourceforge.seqware.common.dao.SequencerRunDAO} object.
     */
    public void setSequencerRunDAO(SequencerRunDAO sequencerRunDAO);

    /**
     * <p>insert.</p>
     *
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     */
    public Integer insert(SequencerRun sequencerRun);

    /**
     * <p>insert.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     */
    public Integer insert(Registration registration, SequencerRun sequencerRun);

    /**
     * <p>insert.</p>
     *
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRunWizardDTO} object.
     */
    public Integer insert(SequencerRunWizardDTO sequencerRun);

    /**
     * <p>insert.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param sequencerRun a {@link net.sourceforge.seqware.common.model.SequencerRunWizardDTO} object.
     */
    public Integer insert(Registration registration, SequencerRunWizardDTO sequencerRun);

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
     * @return a {@link java.util.List} object.
     */
    public List<SequencerRun> list();

    /**
     * <p>list.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @return a {@link java.util.List} object.
     */
    public List<SequencerRun> list(Registration registration);

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
     * <p>findBySWAccession.</p>
     *
     * @param swAccession a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     */
    public SequencerRun findBySWAccession(Integer swAccession);

    /**
     * <p>findByOwnerID.</p>
     *
     * @param registrationId a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    public List<SequencerRun> findByOwnerID(Integer registrationId);

    /**
     * <p>hasNameBeenUsed.</p>
     *
     * @param oldName a {@link java.lang.String} object.
     * @param newName a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean hasNameBeenUsed(String oldName, String newName);

    /**
     * <p>setProcCountInfo.</p>
     *
     * @param list a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
    public List<SequencerRun> setProcCountInfo(List<SequencerRun> list);

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
     * <p>findByCriteria.</p>
     *
     * @param criteria a {@link java.lang.String} object.
     * @param isCaseSens a boolean.
     * @return a {@link java.util.List} object.
     */
    public List<SequencerRun> findByCriteria(String criteria, boolean isCaseSens);
}

// ex:sw=4:ts=4:
