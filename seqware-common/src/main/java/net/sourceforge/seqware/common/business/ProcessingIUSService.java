package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ProcessingIUSDAO;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingIus;

/**
 * <p>
 * ProcessingIUSService interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface ProcessingIUSService {

    /**
     * <p>
     * setProcessingIUSDAO.
     * </p>
     * 
     * @param dao
     *            a {@link net.sourceforge.seqware.common.dao.ProcessingIUSDAO} object.
     */
    void setProcessingIUSDAO(ProcessingIUSDAO dao);

    /**
     * <p>
     * findByProcessingIUS.
     * </p>
     * 
     * @param processing
     *            a {@link net.sourceforge.seqware.common.model.Processing} object.
     * @param ius
     *            a {@link net.sourceforge.seqware.common.model.IUS} object.
     * @return a {@link net.sourceforge.seqware.common.model.ProcessingIus} object.
     */
    ProcessingIus findByProcessingIUS(Processing processing, IUS ius);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param processingIus
     *            a {@link net.sourceforge.seqware.common.model.ProcessingIus} object.
     */
    void delete(ProcessingIus processingIus);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param processingIus
     *            a {@link net.sourceforge.seqware.common.model.ProcessingIus} object.
     */
    void update(ProcessingIus processingIus);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param processingIus
     *            a {@link net.sourceforge.seqware.common.model.ProcessingIus} object.
     */
    void insert(ProcessingIus processingIus);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param processingIus
     *            a {@link net.sourceforge.seqware.common.model.ProcessingIus} object.
     * @return a {@link net.sourceforge.seqware.common.model.ProcessingIus} object.
     */
    ProcessingIus updateDetached(ProcessingIus processingIus);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<ProcessingIus> list();

}
