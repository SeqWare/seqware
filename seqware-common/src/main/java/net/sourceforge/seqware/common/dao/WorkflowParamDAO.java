package net.sourceforge.seqware.common.dao;

import java.util.List;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowParam;

/**
 * <p>
 * WorkflowParamDAO interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface WorkflowParamDAO {

    /**
     * Inserts a new WorkflowParam and returns its primary key.
     * 
     * @param workflowParam
     *            WorkflowParam to be inserted.
     * @return The primary key for the newly inserted WorkflowParam.
     */
    Integer insert(WorkflowParam workflowParam);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflowParam
     *            a {@link net.sourceforge.seqware.common.model.WorkflowParam} object.
     * @return a {@link java.lang.Integer} object.
     */
    Integer insert(Registration registration, WorkflowParam workflowParam);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param workflowParam
     *            a {@link net.sourceforge.seqware.common.model.WorkflowParam} object.
     */
    void update(WorkflowParam workflowParam);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflowParam
     *            a {@link net.sourceforge.seqware.common.model.WorkflowParam} object.
     */
    void update(Registration registration, WorkflowParam workflowParam);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param workflowParam
     *            a {@link net.sourceforge.seqware.common.model.WorkflowParam} object.
     */
    void delete(WorkflowParam workflowParam);

    /**
     * <p>
     * findByID.
     * </p>
     * 
     * @param id
     *            a {@link java.lang.Integer} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowParam} object.
     */
    WorkflowParam findByID(Integer id);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param workflowParam
     *            a {@link net.sourceforge.seqware.common.model.WorkflowParam} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowParam} object.
     */
    WorkflowParam updateDetached(WorkflowParam workflowParam);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflowParam
     *            a {@link net.sourceforge.seqware.common.model.WorkflowParam} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowParam} object.
     */
    WorkflowParam updateDetached(Registration registration, WorkflowParam workflowParam);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<WorkflowParam> list();
}
