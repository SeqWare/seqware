package net.sourceforge.seqware.common.dao;

import java.util.List;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowParamValue;

/**
 * <p>WorkflowParamValueDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface WorkflowParamValueDAO {

    /**
     * Inserts a new WorkflowParamValue and returns its primary key.
     *
     * @return The primary key for the newly inserted WorkflowParamValue.
     * @param workflowParamValue a {@link net.sourceforge.seqware.common.model.WorkflowParamValue} object.
     */
    public Integer insert(WorkflowParamValue workflowParamValue);

    /**
     * <p>insert.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflowParamValue a {@link net.sourceforge.seqware.common.model.WorkflowParamValue} object.
     * @return a {@link java.lang.Integer} object.
     */
    public Integer insert(Registration registration, WorkflowParamValue workflowParamValue);

    /**
     * <p>update.</p>
     *
     * @param workflowParamValue a {@link net.sourceforge.seqware.common.model.WorkflowParamValue} object.
     */
    public void update(WorkflowParamValue workflowParamValue);

    /**
     * <p>update.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflowParamValue a {@link net.sourceforge.seqware.common.model.WorkflowParamValue} object.
     */
    public void update(Registration registration, WorkflowParamValue workflowParamValue);

    /**
     * <p>delete.</p>
     *
     * @param workflowParamValue a {@link net.sourceforge.seqware.common.model.WorkflowParamValue} object.
     */
    public void delete(WorkflowParamValue workflowParamValue);

    /**
     * <p>findByID.</p>
     *
     * @param id a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowParamValue} object.
     */
    public WorkflowParamValue findByID(Integer id);

    /**
     * <p>updateDetached.</p>
     *
     * @param workflowParamValue a {@link net.sourceforge.seqware.common.model.WorkflowParamValue} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowParamValue} object.
     */
    public WorkflowParamValue updateDetached(WorkflowParamValue workflowParamValue);
    
    /**
     * <p>updateDetached.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflowParamValue a {@link net.sourceforge.seqware.common.model.WorkflowParamValue} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowParamValue} object.
     */
    public WorkflowParamValue updateDetached(Registration registration, WorkflowParamValue workflowParamValue);

    /**
     * <p>list.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<WorkflowParamValue> list();
}
