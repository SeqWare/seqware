package net.sourceforge.seqware.common.business;

import java.util.List;
import net.sourceforge.seqware.common.dao.WorkflowParamValueDAO;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.WorkflowParamValue;

public interface WorkflowParamValueService {

    public static final String NAME = "WorkflowParamValueService";

    public void setWorkflowParamValueDAO(WorkflowParamValueDAO workflowParamValueDAO);

    /**
     * Inserts a new WorkflowParamValue and returns its primary key.
     *
     * @param workflowParam WorkflowParamValue to be inserted.
     * @return The primary key for the newly inserted WorkflowParamValue.
     */
    public Integer insert(WorkflowParamValue workflowParamValue);

    public Integer insert(Registration registration, WorkflowParamValue workflowParamValue);

    public void update(WorkflowParamValue workflowParamValue);

    public void update(Registration registration, WorkflowParamValue workflowParamValue);

    public void delete(WorkflowParamValue workflowParamValue);

    public WorkflowParamValue findByID(Integer id);

    WorkflowParamValue updateDetached(WorkflowParamValue workflowParamValue);
    
    WorkflowParamValue updateDetached(Registration registration, WorkflowParamValue workflowParamValue);

    public List<WorkflowParamValue> list();
}
