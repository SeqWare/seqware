package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.WorkflowRunParamDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowRunParam;

/**
 * <p>
 * WorkflowRunParamService interface.
 * </p>
 * 
 * @author boconnor
 * @version $Id: $Id
 */
public interface WorkflowRunParamService {

    /** Constant <code>NAME="WorkflowRunParamService"</code> */
    String NAME = "WorkflowRunParamService";

    /**
     * <p>
     * setWorkflowRunParamDAO.
     * </p>
     * 
     * @param workflowRunParamDAO
     *            a {@link net.sourceforge.seqware.common.dao.WorkflowRunParamDAO} object.
     */
    void setWorkflowRunParamDAO(WorkflowRunParamDAO workflowRunParamDAO);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param workflowRunParam
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     */
    void insert(WorkflowRunParam workflowRunParam);

    /**
     * <p>
     * insert.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflowRunParam
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     */
    void insert(Registration registration, WorkflowRunParam workflowRunParam);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param workflowRunParam
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     */
    void update(WorkflowRunParam workflowRunParam);

    /**
     * <p>
     * update.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflowRunParam
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     */
    void update(Registration registration, WorkflowRunParam workflowRunParam);

    /**
     * <p>
     * delete.
     * </p>
     * 
     * @param workflowRunParam
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     */
    void delete(WorkflowRunParam workflowRunParam);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param workflowRunParam
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     */
    WorkflowRunParam updateDetached(WorkflowRunParam workflowRunParam);

    /**
     * <p>
     * updateDetached.
     * </p>
     * 
     * @param registration
     *            a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflowRunParam
     *            a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     */
    WorkflowRunParam updateDetached(Registration registration, WorkflowRunParam workflowRunParam);

    /**
     * <p>
     * list.
     * </p>
     * 
     * @return a {@link java.util.List} object.
     */
    List<WorkflowRunParam> list();
}
