package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.ProcessingSamplesDAO;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingSamples;
import net.sourceforge.seqware.common.model.Sample;

/**
 * <p>
 * ProcessingSamplesService interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface ProcessingSamplesService {

    /**
     * <p>
     * setProcessingSamplesDAO.
     * </p>
     * 
     * @param dao
     *            a {@link net.sourceforge.seqware.common.dao.ProcessingSamplesDAO} object.
     */
    public abstract void setProcessingSamplesDAO(ProcessingSamplesDAO dao);

    /**
     * <p>
     * findByProcessingSample.
     * </p>
     * 
     * @param processing
     *            a {@link net.sourceforge.seqware.common.model.Processing} object.
     * @param sample
     *            a {@link net.sourceforge.seqware.common.model.Sample} object.
     * @return a {@link net.sourceforge.seqware.common.model.ProcessingSamples} object.
     */
    public abstract ProcessingSamples findByProcessingSample(Processing processing, Sample sample);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param processingSamples
     *            a {@link net.sourceforge.seqware.common.model.ProcessingSamples} object.
     */
    public abstract void delete(ProcessingSamples processingSamples);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param processingSamples
     *            a {@link net.sourceforge.seqware.common.model.ProcessingSamples} object.
     */
    public abstract void update(ProcessingSamples processingSamples);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param processingSamples
     *            a {@link net.sourceforge.seqware.common.model.ProcessingSamples} object.
     */
    public abstract void insert(ProcessingSamples processingSamples);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param processingSamples
     *            a {@link net.sourceforge.seqware.common.model.ProcessingSamples} object.
     * @return a {@link net.sourceforge.seqware.common.model.ProcessingSamples} object.
     */
    public abstract ProcessingSamples updateDetached(ProcessingSamples processingSamples);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    public List<ProcessingSamples> list();

}
