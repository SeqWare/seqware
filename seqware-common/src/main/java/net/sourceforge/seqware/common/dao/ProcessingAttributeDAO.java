package net.sourceforge.seqware.common.dao;

import java.util.List;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingAttribute;

/**
 * <p>
 * ProcessingAttributeDAO interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface ProcessingAttributeDAO {

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param processingAttribute
     *            a {@link net.sourceforge.seqware.common.model.ProcessingAttribute} object.
     */
    void insert(ProcessingAttribute processingAttribute);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param processingAttribute
     *            a {@link net.sourceforge.seqware.common.model.ProcessingAttribute} object.
     */
    void update(ProcessingAttribute processingAttribute);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param processingAttribute
     *            a {@link net.sourceforge.seqware.common.model.ProcessingAttribute} object.
     */
    void delete(ProcessingAttribute processingAttribute);

    /**
     * <p>
     * findAll.
     * </p>
     * 
     * @param processing
     *            a {@link net.sourceforge.seqware.common.model.Processing} object.
     * @return a {@link java.util.List} object.
     */
    @SuppressWarnings("unchecked") List<ProcessingAttribute> findAll(Processing processing);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<ProcessingAttribute> list();

}
