package net.sourceforge.seqware.common.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import net.sourceforge.seqware.common.dao.WorkflowRunDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunParam;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;

public interface WorkflowRunService {

  public static final String NAME = "WorkflowRunService";

  public void setWorkflowRunDAO(WorkflowRunDAO workflowRunDAO);

  /**
   * Inserts a new WorkflowRun and returns its sw_accession number.
   * 
   * @param workflowRun
   *          Workflow run to be inserted.
   * @return The SeqWare Accession number for the newly inserted workflow.
   */
  public Integer insert(WorkflowRun workflowRun);

  public Integer insert(Registration registration, WorkflowRun workflowRun);

  public void update(WorkflowRun workflowRun);

  public void delete(WorkflowRun workflowRun, String deleteRealFiles);

  public Integer insert(WorkflowRun workflowRun, SortedSet<WorkflowRunParam> workflowRunParams,
      Map<String, List<File>> allSelectedFiles);

  public Integer insert(Registration registration, WorkflowRun workflowRun,
      SortedSet<WorkflowRunParam> workflowRunParams, Map<String, List<File>> allSelectedFiles);

  public void update(WorkflowRun workflowRun, List<Integer> laneIds);

  public void update(Registration registration, WorkflowRun workflowRun, List<Integer> laneIds);

  public void update(Registration registration, WorkflowRun workflowRun);

  public List<WorkflowRun> list();

  public List<WorkflowRun> list(Registration registration);

  public List<WorkflowRun> listMyShared(Registration registration);

  public List<WorkflowRun> listSharedWithMe(Registration registration);

  public List<WorkflowRun> listSharedWithMeWithSample(Registration registration);

  public List<WorkflowRun> listRunning(Registration registration);

  public List<WorkflowRun> list(Registration registration, Boolean isAsc);

  public List<WorkflowRun> listMyShared(Registration registration, Boolean isAsc);

  public List<WorkflowRun> listSharedWithMe(Registration registration, Boolean isAsc);

  public List<WorkflowRun> listRunning(Registration registration, Boolean isAsc);

  public WorkflowRun findByName(String name);

  public WorkflowRun findByID(Integer wfrID);

  public WorkflowRun findByIDWithIUS(Integer wfrID);

  public WorkflowRun findByIDWithIUSAndRunningWR(Integer wfrID);

  public WorkflowRun findBySWAccession(Integer swAccession);

  public List<WorkflowRun> findByOwnerID(Integer registrationID);

  public Processing getRootProcessing(Integer wfrId);

  public List<File> getFiles(Integer wfrId);

  public List<WorkflowRun> listWithHasFile(List<WorkflowRun> list);

  public WorkflowRun updateDetached(WorkflowRun workflowRun);

  public List<WorkflowRun> findByCriteria(String criteria, boolean isCaseSens);

  public List<WorkflowRun> findByCriteria(String criteria);

  public List<Workflow> listRelatedWorkflows(Registration registration);

  public Set<WorkflowRun> findRunsForIUS(IUS ius);

  /**
   * Find all the workflow runs for the specific sample. This function doesn't
   * look into the nested samples.
   * 
   * @param sample
   * @return
   */
  public Set<WorkflowRun> findRunsForSample(Sample sample);

  public ReturnValue runWorkflow(WorkflowInfo wi, String workflowRunAccession, String iniFilesStr, boolean noMetadata,
      String parentAccessionsStr, ArrayList<String> parentsLinkedToWR, Registration owner);

  public List<File> findFiles(Integer swAccession);
}
