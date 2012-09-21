package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.WorkflowRunParamDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowRunParam;

public interface WorkflowRunParamService {

    public static final String NAME = "WorkflowRunParamService";

    public void setWorkflowRunParamDAO(WorkflowRunParamDAO workflowRunParamDAO);

    public void insert(WorkflowRunParam workflowRunParam);

    public void insert(Registration registration, WorkflowRunParam workflowRunParam);

    public void update(WorkflowRunParam workflowRunParam);

    public void update(Registration registration, WorkflowRunParam workflowRunParam);

    public void delete(WorkflowRunParam workflowRunParam);

    WorkflowRunParam updateDetached(WorkflowRunParam workflowRunParam);
    
    WorkflowRunParam updateDetached(Registration registration, WorkflowRunParam workflowRunParam);

    public List<WorkflowRunParam> list();
}
