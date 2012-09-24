package net.sourceforge.seqware.common.dao;

import java.util.List;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowParam;

public interface WorkflowParamDAO {

    /**
     * Inserts a new WorkflowParam and returns its primary key.
     *
     * @param workflowParam WorkflowParam to be inserted.
     * @return The primary key for the newly inserted WorkflowParam.
     */
    public Integer insert(WorkflowParam workflowParam);

    public Integer insert(Registration registration, WorkflowParam workflowParam);

    public void update(WorkflowParam workflowParam);

    public void update(Registration registration, WorkflowParam workflowParam);

    public void delete(WorkflowParam workflowParam);

    public WorkflowParam findByID(Integer id);

    public WorkflowParam updateDetached(WorkflowParam workflowParam);
    
    public WorkflowParam updateDetached(Registration registration, WorkflowParam workflowParam);

    public List<WorkflowParam> list();
}
