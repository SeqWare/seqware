package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ProcessingSequencerRunsDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingSequencerRuns;
import net.sourceforge.seqware.common.model.SequencerRun;

/**
 * <p>
 * ProcessingSequencerRunsService interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface ProcessingSequencerRunsService {

    /**
     * <p>
     * setProcessingSequencerRunsDAO.
     * </p>
     * 
     * @param dao
     *            a {@link net.sourceforge.seqware.common.dao.ProcessingSequencerRunsDAO} object.
     */
    void setProcessingSequencerRunsDAO(ProcessingSequencerRunsDAO dao);

    /**
     * <p>
     * findByProcessingSequencerRun.
     * </p>
     * 
     * @param processing
     *            a {@link net.sourceforge.seqware.common.model.Processing} object.
     * @param sequencerRun
     *            a {@link net.sourceforge.seqware.common.model.SequencerRun} object.
     * @return a {@link net.sourceforge.seqware.common.model.ProcessingSequencerRuns} object.
     */
    ProcessingSequencerRuns findByProcessingSequencerRun(Processing processing, SequencerRun sequencerRun);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param processingSequencerRuns
     *            a {@link net.sourceforge.seqware.common.model.ProcessingSequencerRuns} object.
     */
    void delete(ProcessingSequencerRuns processingSequencerRuns);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param processingSequencerRuns
     *            a {@link net.sourceforge.seqware.common.model.ProcessingSequencerRuns} object.
     */
    void update(ProcessingSequencerRuns processingSequencerRuns);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param processingSequencerRuns
     *            a {@link net.sourceforge.seqware.common.model.ProcessingSequencerRuns} object.
     */
    void insert(ProcessingSequencerRuns processingSequencerRuns);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param processingSequencerRuns
     *            a {@link net.sourceforge.seqware.common.model.ProcessingSequencerRuns} object.
     * @return a {@link net.sourceforge.seqware.common.model.ProcessingSequencerRuns} object.
     */
    ProcessingSequencerRuns updateDetached(ProcessingSequencerRuns processingSequencerRuns);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<ProcessingSequencerRuns> list();

}
