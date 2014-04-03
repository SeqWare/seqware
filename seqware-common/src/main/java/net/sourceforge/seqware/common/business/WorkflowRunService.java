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

/**
 * <p>WorkflowRunService interface.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public interface WorkflowRunService {

  /** Constant <code>NAME="WorkflowRunService"</code> */
  public static final String NAME = "WorkflowRunService";

  /**
   * <p>setWorkflowRunDAO.</p>
   *
   * @param workflowRunDAO a {@link net.sourceforge.seqware.common.dao.WorkflowRunDAO} object.
   */
  public void setWorkflowRunDAO(WorkflowRunDAO workflowRunDAO);

  /**
   * Inserts a new WorkflowRun and returns its sw_accession number.
   *
   * @param workflowRun
   *          Workflow run to be inserted.
   * @return The SeqWare Accession number for the newly inserted workflow.
   */
  public Integer insert(WorkflowRun workflowRun);

  /**
   * <p>insert.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @return a {@link java.lang.Integer} object.
   */
  public Integer insert(Registration registration, WorkflowRun workflowRun);

  /**
   * <p>update.</p>
   *
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  public void update(WorkflowRun workflowRun);

  /**
   * <p>delete.</p>
   */
  public void delete(WorkflowRun workflowRun, boolean deleteRealFiles);

  /**
   * <p>insert.</p>
   *
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @param workflowRunParams a {@link java.util.SortedSet} object.
   * @param allSelectedFiles a {@link java.util.Map} object.
   * @return a {@link java.lang.Integer} object.
   */
  public Integer insert(WorkflowRun workflowRun, SortedSet<WorkflowRunParam> workflowRunParams,
      Map<String, List<File>> allSelectedFiles);

  /**
   * <p>insert.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @param workflowRunParams a {@link java.util.SortedSet} object.
   * @param allSelectedFiles a {@link java.util.Map} object.
   * @return a {@link java.lang.Integer} object.
   */
  public Integer insert(Registration registration, WorkflowRun workflowRun,
      SortedSet<WorkflowRunParam> workflowRunParams, Map<String, List<File>> allSelectedFiles);

  /**
   * <p>update.</p>
   *
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @param laneIds a {@link java.util.List} object.
   */
  public void update(WorkflowRun workflowRun, List<Integer> laneIds);

  /**
   * <p>update.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @param laneIds a {@link java.util.List} object.
   */
  public void update(Registration registration, WorkflowRun workflowRun, List<Integer> laneIds);

  /**
   * <p>update.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  public void update(Registration registration, WorkflowRun workflowRun);

  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> list();

  /**
   * <p>list.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> list(Registration registration);

  /**
   * <p>listMyShared.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> listMyShared(Registration registration);

  /**
   * <p>listSharedWithMe.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> listSharedWithMe(Registration registration);

  /**
   * <p>listSharedWithMeWithSample.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> listSharedWithMeWithSample(Registration registration);

  /**
   * <p>listRunning.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> listRunning(Registration registration);

  /**
   * <p>list.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param isAsc a {@link java.lang.Boolean} object.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> list(Registration registration, Boolean isAsc);

  /**
   * <p>listMyShared.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param isAsc a {@link java.lang.Boolean} object.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> listMyShared(Registration registration, Boolean isAsc);

  /**
   * <p>listSharedWithMe.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param isAsc a {@link java.lang.Boolean} object.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> listSharedWithMe(Registration registration, Boolean isAsc);

  /**
   * <p>listRunning.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @param isAsc a {@link java.lang.Boolean} object.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> listRunning(Registration registration, Boolean isAsc);

  /**
   * <p>findByName.</p>
   *
   * @param name a {@link java.lang.String} object.
   * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  public WorkflowRun findByName(String name);

  /**
   * <p>findByID.</p>
   *
   * @param wfrID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  public WorkflowRun findByID(Integer wfrID);

  /**
   * <p>findByIDWithIUS.</p>
   *
   * @param wfrID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  public WorkflowRun findByIDWithIUS(Integer wfrID);

  /**
   * <p>findByIDWithIUSAndRunningWR.</p>
   *
   * @param wfrID a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  public WorkflowRun findByIDWithIUSAndRunningWR(Integer wfrID);

  /**
   * <p>findBySWAccession.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  public WorkflowRun findBySWAccession(Integer swAccession);

  /**
   * <p>findByOwnerID.</p>
   *
   * @param registrationID a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> findByOwnerID(Integer registrationID);

  /**
   * <p>getRootProcessing.</p>
   *
   * @param wfrId a {@link java.lang.Integer} object.
   * @return a {@link net.sourceforge.seqware.common.model.Processing} object.
   */
  public Processing getRootProcessing(Integer wfrId);

  /**
   * <p>getFiles.</p>
   *
   * @param wfrId a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  public List<File> getFiles(Integer wfrId);

  /**
   * <p>listWithHasFile.</p>
   *
   * @param list a {@link java.util.List} object.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> listWithHasFile(List<WorkflowRun> list);

  /**
   * <p>updateDetached.</p>
   *
   * @param workflowRun a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   * @return a {@link net.sourceforge.seqware.common.model.WorkflowRun} object.
   */
  public WorkflowRun updateDetached(WorkflowRun workflowRun);

  /**
   * <p>findByCriteria.</p>
   *
   * @param criteria a {@link java.lang.String} object.
   * @param isCaseSens a boolean.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> findByCriteria(String criteria, boolean isCaseSens);

  /**
   * <p>findByCriteria.</p>
   *
   * @param criteria a {@link java.lang.String} object.
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> findByCriteria(String criteria);

  /**
   * <p>listRelatedWorkflows.</p>
   *
   * @param registration a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link java.util.List} object.
   */
  public List<Workflow> listRelatedWorkflows(Registration registration);

  /**
   * <p>findRunsForIUS.</p>
   *
   * @param ius a {@link net.sourceforge.seqware.common.model.IUS} object.
   * @return a {@link java.util.Set} object.
   */
  public Set<WorkflowRun> findRunsForIUS(IUS ius);

  /**
   * Find all the workflow runs for the specific sample. This function doesn't
   * look into the nested samples.
   *
   * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
   * @return a {@link java.util.Set} object.
   */
  public Set<WorkflowRun> findRunsForSample(Sample sample);

  /**
   * <p>runWorkflow.</p>
   *
   * @param wi a {@link net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo} object.
   * @param workflowRunAccession a {@link java.lang.String} object.
   * @param iniFilesStr a {@link java.lang.String} object.
   * @param noMetadata a boolean.
   * @param parentAccessionsStr a {@link java.lang.String} object.
   * @param parentsLinkedToWR a {@link java.util.ArrayList} object.
   * @param owner a {@link net.sourceforge.seqware.common.model.Registration} object.
   * @return a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   */
  public ReturnValue runWorkflow(WorkflowInfo wi, String workflowRunAccession, String iniFilesStr, boolean noMetadata,
      String parentAccessionsStr, ArrayList<String> parentsLinkedToWR, Registration owner);

  /**
   * <p>findFiles.</p>
   *
   * @param swAccession a {@link java.lang.Integer} object.
   * @return a {@link java.util.List} object.
   */
  public List<File> findFiles(Integer swAccession);
}
