package net.sourceforge.seqware.common.dao;

import java.util.List;
import java.util.Map;

import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunParam;

public interface WorkflowRunParamDAO {

    public void insert(WorkflowRunParam workflowRunParam);

    public void insert(Registration registration, WorkflowRunParam workflowRunParam);

    public void update(WorkflowRunParam workflowRunParam);

    public void update(Registration registration, WorkflowRunParam workflowRunParam);

    public void delete(WorkflowRunParam workflowRunParam);

    public void insertFilesAsWorkflowRunParam(WorkflowRun workflowRun, Map<String, List<File>> files);
    
    public void insertFilesAsWorkflowRunParam(Registration registration, WorkflowRun workflowRun, Map<String, List<File>> files);

    public WorkflowRunParam updateDetached(WorkflowRunParam workflowRunParam);

    public WorkflowRunParam updateDetached(Registration registration, WorkflowRunParam workflowRunParam);

    public List<WorkflowRunParam> list();
}
