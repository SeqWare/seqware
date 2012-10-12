package net.sourceforge.seqware.common.dao;

import java.util.List;
import java.util.Map;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunParam;

/**
 * <p>WorkflowRunParamDAO interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface WorkflowRunParamDAO {

    /**
     * <p>insert.</p>
     *
     * @param workflowRunParam a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     */
    public void insert(WorkflowRunParam workflowRunParam);

    /**
     * <p>insert.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflowRunParam a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     */
    public void insert(Registration registration, WorkflowRunParam workflowRunParam);

    /**
     * <p>update.</p>
     *
     * @param workflowRunParam a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     */
    public void update(WorkflowRunParam workflowRunParam);

    /**
     * <p>update.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflowRunParam a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     */
    public void update(Registration registration, WorkflowRunParam workflowRunParam);

    /**
     * <p>delete.</p>
     *
     * @param workflowRunParam a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     */
    public void delete(WorkflowRunParam workflowRunParam);

    /**
     * <p>insertFilesAsWorkflowRunParam.</p>
     *
     * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     * @param files a {@link java.util.Map} object.
     */
    public void insertFilesAsWorkflowRunParam(WorkflowRun workflowRun, Map<String, List<File>> files);
    
    /**
     * <p>insertFilesAsWorkflowRunParam.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
     * @param files a {@link java.util.Map} object.
     */
    public void insertFilesAsWorkflowRunParam(Registration registration, WorkflowRun workflowRun, Map<String, List<File>> files);

    /**
     * <p>updateDetached.</p>
     *
     * @param workflowRunParam a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     */
    public WorkflowRunParam updateDetached(WorkflowRunParam workflowRunParam);

    /**
     * <p>updateDetached.</p>
     *
     * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
     * @param workflowRunParam a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     * @return a {@link net.sourceforge.seqware.common.model.WorkflowRunParam} object.
     */
    public WorkflowRunParam updateDetached(Registration registration, WorkflowRunParam workflowRunParam);

    /**
     * <p>list.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<WorkflowRunParam> list();
}
