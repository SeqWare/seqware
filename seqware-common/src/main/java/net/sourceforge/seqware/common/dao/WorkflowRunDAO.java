package net.sourceforge.seqware.common.dao;

import java.util.List;
import java.util.Set;

import net.sourceforge.seqware.common.model.*;

public interface WorkflowRunDAO {

    /**
     * Inserts a new WorkflowRun and returns its sw_accession number.
     *
     * @param workflowRun Workflow run to be inserted.
     * @return The SeqWare Accession number for the newly inserted workflow.
     */
    public Integer insert(WorkflowRun workflowRun);

    public Integer insert(Registration registration, WorkflowRun workflowRun);

    public void update(WorkflowRun workflowRun);

    public void update(Registration registration, WorkflowRun workflowRun);

    public void delete(WorkflowRun workflowRun);

    public void update(WorkflowRun workflowRun, List<Integer> laneIds);

    public List<WorkflowRun> list();

    // public List<WorkflowRun> list(Registration registration);
    // public List<WorkflowRun> listMyShared(Registration registration);
    // public List<WorkflowRun> listSharedWithMe(Registration registration);
    // public List<WorkflowRun> listRunning(Registration registration);
    public List<WorkflowRun> list(Registration registration, Boolean isAsc);

    public List<WorkflowRun> listMyShared(Registration registration, Boolean isAsc);

    public List<WorkflowRun> listSharedWithMe(Registration registration, Boolean isAsc);

    public List<WorkflowRun> listRunning(Registration registration, Boolean isAsc);

    public WorkflowRun findByName(String name);

    public WorkflowRun findByID(Integer wfrID);

    public WorkflowRun findBySWAccession(Integer swAccession);

    public WorkflowRun updateDetached(WorkflowRun workflowRun);

    public WorkflowRun updateDetached(Registration registration, WorkflowRun workflowRun);

    public List<WorkflowRun> findByOwnerID(Integer registrationID);

    public List<WorkflowRun> findByCriteria(String criteria, boolean isCaseSens);

    public List<WorkflowRun> findByCriteria(String criteria);

    public List<Workflow> listRelatedWorkflows(Registration registration);

    public Set<WorkflowRun> findRunsForIUS(IUS ius);
}
