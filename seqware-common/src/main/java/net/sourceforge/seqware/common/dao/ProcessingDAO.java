package net.sourceforge.seqware.common.dao;

import java.util.List;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;

/**
 * <p>ProcessingDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface ProcessingDAO {

    /**
     * Inserts a new Processing and returns its sw_accession number.
     *
     * @param processing Processing to be inserted.
     * @return The SeqWare Accession number for the newly inserted Processing.
     */
    public Integer insert(Processing processing);
    /**
     * <p>insert.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer insert(Registration registration,Processing processing);
    /**
     * <p>update.</p>
     *
     * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
     */
    public void update(Processing processing);
    
    /**
     * <p>update.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
     */
    public void update(Registration registration, Processing processing);

    /**
     * <p>delete.</p>
     *
     * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
     */
    public void delete(Processing processing);

    /**
     * <p>findByFilePath.</p>
     *
     * @param filePath a {@link java.lang.String} object.
     * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
     */
    public Processing findByFilePath(String filePath);

    /**
     * <p>findByID.</p>
     *
     * @param processingId a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
     */
    public Processing findByID(Integer processingId);

    /**
     * <p>getFiles.</p>
     *
     * @param processingId a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    public List<File> getFiles(Integer processingId);

    /**
     * <p>isHasFile.</p>
     *
     * @param processingId a {@link java.lang.Integer} object.
     * @return a boolean.
     */
    public boolean isHasFile(Integer processingId);

    /**
     * <p>getFiles.</p>
     *
     * @param processingId a {@link java.lang.Integer} object.
     * @param metaType a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     */
    public List<File> getFiles(Integer processingId, String metaType);

    /**
     * <p>isHasFile.</p>
     *
     * @param processingId a {@link java.lang.Integer} object.
     * @param metaType a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean isHasFile(Integer processingId, String metaType);

    /**
     * <p>findBySWAccession.</p>
     *
     * @param swAccession a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
     */
    public Processing findBySWAccession(Integer swAccession);

    /**
     * <p>updateDetached.</p>
     *
     * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
     * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
     */
    public Processing updateDetached(Processing processing);
    
    /**
     * <p>updateDetached.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param processing a {@link net.sourceforge.seqware.common.model.Processing} object.
     * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
     */
    public Processing updateDetached(Registration registration, Processing processing);

    /**
     * <p>findByOwnerID.</p>
     *
     * @param registrationId a {@link java.lang.Integer} object.
     * @return a {@link java.util.List} object.
     */
    public List<Processing> findByOwnerID(Integer registrationId);

    /**
     * <p>findByCriteria.</p>
     *
     * @param criteria a {@link java.lang.String} object.
     * @param isCaseSens a boolean.
     * @return a {@link java.util.List} object.
     */
    public List<Processing> findByCriteria(String criteria, boolean isCaseSens);
    
    /**
     * <p>list.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Processing> list();
}
