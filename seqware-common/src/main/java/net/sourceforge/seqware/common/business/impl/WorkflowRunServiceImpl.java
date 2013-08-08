package net.sourceforge.seqware.common.business.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import net.sourceforge.seqware.common.business.WorkflowRunService;
import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.dao.ProcessingDAO;
import net.sourceforge.seqware.common.dao.WorkflowRunDAO;
import net.sourceforge.seqware.common.dao.WorkflowRunParamDAO;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunParam;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowInfo;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowManager;

import org.apache.log4j.Logger;

/**
 * <p>WorkflowRunServiceImpl class.</p>
 *
 * @author boconnor
 * @version $Id: $Id
 */
public class WorkflowRunServiceImpl implements WorkflowRunService {

  private WorkflowRunDAO workflowRunDAO = null;
  private ProcessingDAO processingDAO = null;
  private WorkflowRunParamDAO workflowRunParamDAO = null;
  private FileDAO fileDAO = null;
  private Logger log;

  /**
   * <p>Constructor for WorkflowRunServiceImpl.</p>
   */
  public WorkflowRunServiceImpl() {
    super();
    log = Logger.getLogger(WorkflowRunServiceImpl.class);
  }

  /**
   * {@inheritDoc}
   *
   * Sets a private member variable with an instance of an implementation of
   * WorkflowRunDAO. This method is called by the Spring framework at run time.
   * @see WorkflowRunDAO
   */
  public void setWorkflowRunDAO(WorkflowRunDAO workflowRunDAO) {
    this.workflowRunDAO = workflowRunDAO;
  }

  /**
   * <p>Setter for the field <code>processingDAO</code>.</p>
   *
   * @param processingDAO a {@link net.sourceforge.seqware.common.dao.ProcessingDAO} object.
   */
  public void setProcessingDAO(ProcessingDAO processingDAO) {
    this.processingDAO = processingDAO;
  }

  /**
   * <p>Setter for the field <code>workflowRunParamDAO</code>.</p>
   *
   * @param workflowRunParamDAO a {@link net.sourceforge.seqware.common.dao.WorkflowRunParamDAO} object.
   */
  public void setWorkflowRunParamDAO(WorkflowRunParamDAO workflowRunParamDAO) {
    this.workflowRunParamDAO = workflowRunParamDAO;
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * FileDAO. This method is called by the Spring framework at run time.
   *
   * @param fileDAO
   *          implementation of FileDAO
   * @see FileDAO
   */
  public void setFileDAO(FileDAO fileDAO) {
    this.fileDAO = fileDAO;
  }

  /** {@inheritDoc} */
  @Override
  public Integer insert(WorkflowRun workflowRun) {
    workflowRun.setCreateTimestamp(new Date());
    return workflowRunDAO.insert(workflowRun);
  }

  /** {@inheritDoc} */
  @Override
  public Integer insert(WorkflowRun workflowRun, SortedSet<WorkflowRunParam> workflowRunParams,
      Map<String, List<File>> allSelectedFiles) {
    Integer id = insert(workflowRun);

    workflowRun = findByID(workflowRun.getWorkflowRunId());

    log.info("Insert Workflow Run Param:");

    // LEFT OFF HERE

    // insert new workflow run params
    for (WorkflowRunParam workflowRunParam : workflowRunParams) {
      // workflowRun.getWorkflowRunParams().add(workflowRunParam);
      workflowRunParam.setWorkflowRun(workflowRun);
      workflowRunParamDAO.insert(workflowRunParam);
    }

    // set workflow run
    workflowRunParamDAO.insertFilesAsWorkflowRunParam(workflowRun, allSelectedFiles);

    // workflowRunDAO.update(workflowRun, laneIds);
    return id;

  }

  /** {@inheritDoc} */
  @Override
  public Integer insert(Registration registration, WorkflowRun workflowRun,
      SortedSet<WorkflowRunParam> workflowRunParams, Map<String, List<File>> allSelectedFiles) {
    Integer id = insert(registration, workflowRun);

    workflowRun = findByID(workflowRun.getWorkflowRunId());

    log.info("Insert Workflow Run Param with registration:");

    // LEFT OFF HERE

    // insert new workflow run params
    for (WorkflowRunParam workflowRunParam : workflowRunParams) {
      // workflowRun.getWorkflowRunParams().add(workflowRunParam);
      workflowRunParam.setWorkflowRun(workflowRun);
      workflowRunParamDAO.insert(registration, workflowRunParam);
    }

    // set workflow run
    workflowRunParamDAO.insertFilesAsWorkflowRunParam(registration, workflowRun, allSelectedFiles);

    // workflowRunDAO.update(workflowRun, laneIds);
    return id;
  }

  /** {@inheritDoc} */
  public void update(WorkflowRun workflowRun) {
    workflowRunDAO.update(workflowRun);
  }

  /** {@inheritDoc} */
  public void update(WorkflowRun workflowRun, List<Integer> laneIds) {
    workflowRunDAO.update(workflowRun, laneIds);
  }

  /** {@inheritDoc} */
  public void delete(WorkflowRun workflowRun, boolean deleteRealFiles) {
    Set<Processing> processings = workflowRun.getProcessings();

    // get all files from processings
    List<File> deleteFiles = new LinkedList<File>();

    if (deleteRealFiles) {
      if (processings != null) {
        for (Processing processing : processings) {
          deleteFiles.addAll(processingDAO.getFiles(processing.getProcessingId()));
        }
      }
    }

    if (processings != null) {
      for (Processing processing : processings) {
        /*
         * Set<Lane> lanes = processing.getLanes(); for (Lane lane : lanes) {
         * lane.getProcessings().remove(processing); }
         */
        Set<IUS> setIUS = processing.getIUS();
        for (IUS ius : setIUS) {
          ius.getProcessings().remove(processing);
        }

        Set<Processing> parents = processing.getParents();
        for (Processing parent : parents) {
          parent.getChildren().remove(parent);
        }

        Set<Processing> children = processing.getChildren();
        for (Processing child : children) {
          child.getParents().remove(processing);
        }

        processing.setWorkflowRun(null);
      }
      workflowRun.getProcessings().clear();
    }

    workflowRunDAO.delete(workflowRun);

    if (deleteRealFiles) {
      fileDAO.deleteAllWithFolderStore(deleteFiles);
    }
  }

  /** {@inheritDoc} */
  public Processing getRootProcessing(Integer wfrId) {
    Processing proc = new Processing();
    Set<Processing> processings = findByID(wfrId).getProcessings();
    for (Processing processing : processings) {
      proc = processing;
      break;
    }
    return proc;
  }

  /** {@inheritDoc} */
  public List<File> getFiles(Integer wfrId) {
    List<File> files = new LinkedList<File>();
    WorkflowRun workflowRun = findByID(wfrId);
    SortedSet<Processing> processings = workflowRun.getProcessings();
    for (Processing proc : processings) {
      files.addAll(processingDAO.getFiles(proc.getProcessingId()));
    }
    return files;
  }

  /** {@inheritDoc} */
  public List<WorkflowRun> listWithHasFile(List<WorkflowRun> list) {
    for (WorkflowRun workflowRun : list) {
      // Processing processing = getProcessing(workflowRun);
      // workflowRun.setIsHasFile(processingDAO.isHasFile(processing.getProcessingId()));
      SortedSet<Processing> processings = workflowRun.getProcessings();
      boolean isHasFile = false;
      for (Processing processing : processings) {
        if (processing != null) {
          if (processingDAO.isHasFile(processing.getProcessingId())) {
            isHasFile = true;
            break;
          }
        }
      }

      workflowRun.setIsHasFile(isHasFile);
    }
    return list;
  }

  /**
   * <p>list.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<WorkflowRun> list() {
    return workflowRunDAO.list();
  }

  /** {@inheritDoc} */
  public List<WorkflowRun> list(Registration registration) {
    return workflowRunDAO.list(registration, true);
  }

  /** {@inheritDoc} */
  public List<WorkflowRun> listMyShared(Registration registration) {
    List<WorkflowRun> sharedWorkflowRuns = new ArrayList<WorkflowRun>();
    sharedWorkflowRuns = workflowRunDAO.listMyShared(registration, true);
    return sharedWorkflowRuns;
  }

  /** {@inheritDoc} */
  public List<WorkflowRun> listSharedWithMe(Registration registration) {
    List<WorkflowRun> sharedWithMeWorkflowRuns = new ArrayList<WorkflowRun>();
    sharedWithMeWorkflowRuns = workflowRunDAO.listSharedWithMe(registration, true);
    return sharedWithMeWorkflowRuns;
  }

  /** {@inheritDoc} */
  public List<WorkflowRun> listRunning(Registration registration) {
    return workflowRunDAO.listRunning(registration, true);
  }

  /** {@inheritDoc} */
  public List<WorkflowRun> list(Registration registration, Boolean isAsc) {
    return workflowRunDAO.list(registration, isAsc);
  }

  /** {@inheritDoc} */
  public List<WorkflowRun> listMyShared(Registration registration, Boolean isAsc) {
    List<WorkflowRun> sharedWorkflowRuns = new ArrayList<WorkflowRun>();
    sharedWorkflowRuns = workflowRunDAO.listMyShared(registration, isAsc);
    return sharedWorkflowRuns;
  }

  /** {@inheritDoc} */
  public List<WorkflowRun> listSharedWithMe(Registration registration, Boolean isAsc) {
    List<WorkflowRun> sharedWithMeWorkflowRuns = new ArrayList<WorkflowRun>();
    sharedWithMeWorkflowRuns = workflowRunDAO.listSharedWithMe(registration, isAsc);
    return sharedWithMeWorkflowRuns;
  }

  /** {@inheritDoc} */
  public List<WorkflowRun> listRunning(Registration registration, Boolean isAsc) {
    return workflowRunDAO.listRunning(registration, isAsc);
  }

  /** {@inheritDoc} */
  @Override
  public List<Workflow> listRelatedWorkflows(Registration registration) {
    return workflowRunDAO.listRelatedWorkflows(registration);
  }

  /** {@inheritDoc} */
  public WorkflowRun findByName(String name) {
    WorkflowRun workflowRun = null;
    if (name != null) {
      try {
        workflowRun = workflowRunDAO.findByName(name.trim().toLowerCase());
      } catch (Exception exception) {
        log.debug("Cannot find WorkflowRun by name " + name);
      }
    }
    return workflowRun;
  }

  /** {@inheritDoc} */
  public WorkflowRun findByID(Integer wfrID) {
    WorkflowRun workflowRun = null;
    if (wfrID != null) {
      try {
        workflowRun = workflowRunDAO.findByID(wfrID);
      } catch (Exception exception) {
        log.error("Cannot find WorkflowRun by wfrID " + wfrID);
        log.error(exception.getMessage());
      }
    }
    return workflowRun;
  }

  /** {@inheritDoc} */
  @Override
  public WorkflowRun findBySWAccession(Integer swAccession) {
    WorkflowRun workflowRun = null;
    if (swAccession != null) {
      try {
        workflowRun = workflowRunDAO.findBySWAccession(swAccession);
      } catch (Exception exception) {
        log.error("Cannot find WorkflowRun by swAccession " + swAccession);
        log.error(exception.getMessage());
      }
    }
    return workflowRun;
  }

  /** {@inheritDoc} */
  @Override
  public List<WorkflowRun> findByOwnerID(Integer registrationID) {
    List<WorkflowRun> workflowRuns = null;
    if (registrationID != null) {
      try {
        workflowRuns = workflowRunDAO.findByOwnerID(registrationID);
      } catch (Exception exception) {
        log.error("Cannot find WorkflowRun by registrationID " + registrationID);
        log.error(exception.getMessage());
      }
    }
    return workflowRuns;
  }

  /** {@inheritDoc} */
  public WorkflowRun findByIDWithIUS(Integer wfrID) {
    WorkflowRun workflowRun = findByID(wfrID);

    SortedSet<Processing> processings = workflowRun.getProcessings();
    for (Processing pr : processings) {
      pr.resetCompletedChildren();
    }

    // set ius which has parent processing
    workflowRun.setIus(getAllIUS(workflowRun));
    return workflowRun;
  }

  /** {@inheritDoc} */
  public WorkflowRun findByIDWithIUSAndRunningWR(Integer wfrID) {
    WorkflowRun workflowRun = findByID(wfrID);

    SortedSet<Processing> processings = workflowRun.getProcessings();
    for (Processing pr : processings) {
      pr.resetRunningChildren();
    }

    // set ius which has parent processing
    workflowRun.setIus(getAllIUS(workflowRun));
    return workflowRun;
  }

  /*
   * public WorkflowRun findByIDWithSample(Integer wfrID){ WorkflowRun
   * workflowRun = findByID(wfrID);
   * 
   * SortedSet<Processing> processings = workflowRun.getProcessings(); for
   * (Processing pr : processings) { pr.resetCompletedChildren(); }
   * 
   * SortedSet<Sample> samples = new TreeSet<Sample>(); samples =
   * getSamples(workflowRun); workflowRun.setSamples(samples); return
   * workflowRun; }
   * 
   * public WorkflowRun findByIDWithSampleAndRunningWR(Integer wfrID){
   * WorkflowRun workflowRun = findByID(wfrID);
   * 
   * SortedSet<Processing> processings = workflowRun.getProcessings(); for
   * (Processing pr : processings) { pr.resetRunningChildren(); }
   * 
   * SortedSet<Sample> samples = new TreeSet<Sample>(); samples =
   * getSamples(workflowRun); workflowRun.setSamples(samples); return
   * workflowRun; }
   */
  /** {@inheritDoc} */
  public List<WorkflowRun> listSharedWithMeWithSample(Registration registration) {
    List<WorkflowRun> list = listSharedWithMe(registration);
    for (WorkflowRun workflowRun : list) {
      SortedSet<Sample> samples = new TreeSet<Sample>();
      samples = getSamples(workflowRun);
      workflowRun.setSamples(samples);
    }
    return list;
  }

  private SortedSet<IUS> getAllIUS(WorkflowRun workflowRun) {
    SortedSet<IUS> ius = new TreeSet<IUS>();

    Set<Processing> processings = workflowRun.getProcessings();

    if (processings.isEmpty()) {
      return ius;
    }

    for (Processing processing : processings) {
      ius.addAll(getIUS(ius, processing));
    }
    return ius;
  }

  private SortedSet<IUS> getIUS(SortedSet<IUS> ius, Processing processing) {
    Set<Processing> parents = processing.getParents();
    ius.addAll(processing.getIUS());
    if (!parents.isEmpty()) {
      for (Processing parent : parents) {
        ius.addAll(getIUS(ius, parent));
      }
    }
    return ius;
  }

  private SortedSet<Sample> getSamples(WorkflowRun workflowRun) {
    SortedSet<Sample> samples = new TreeSet<Sample>();
    Set<Processing> processings = workflowRun.getProcessings();

    if (processings.isEmpty()) {
      return samples;
    }

    // Iterator<Processing> itp = processings.iterator();
    // if (itp.hasNext()) {
    // processing = (Processing) itp.next();
    // }

    Set<Lane> lanes = new TreeSet<Lane>();

    for (Processing processing : processings) {
      lanes.addAll(processing.getLanes());
      while (!processing.getParents().isEmpty()) {
        Iterator<Processing> it2 = processing.getParents().iterator();
        if (it2.hasNext()) {
          processing = it2.next();
        }
        lanes.addAll(processing.getLanes());
      }
    }

    for (Lane lane : lanes) {
      if (lane.getSamples() != null) {
        samples.addAll(lane.getSamples());
      }
    }

    return samples;
  }

  /** {@inheritDoc} */
  @Override
  public List<WorkflowRun> findByCriteria(String criteria, boolean isCaseSens) {
    return workflowRunDAO.findByCriteria(criteria, isCaseSens);
  }

  /** {@inheritDoc} */
  @Override
  public List<WorkflowRun> findByCriteria(String criteria) {
    return workflowRunDAO.findByCriteria(criteria);
  }

  /** {@inheritDoc} */
  @Override
  public WorkflowRun updateDetached(WorkflowRun workflowRun) {
    return workflowRunDAO.updateDetached(workflowRun);
  }

  /** {@inheritDoc} */
  @Override
  public Set<WorkflowRun> findRunsForIUS(IUS ius) {
    return workflowRunDAO.findRunsForIUS(ius);
  }

  /** {@inheritDoc} */
  public List<File> findFiles(Integer swAccession) {
    WorkflowRun wr = findBySWAccession(swAccession);
    Set<File> files = new HashSet<File>();
    Stack<Processing> processings = new Stack<Processing>();
    processings.addAll(wr.getProcessings());
    processings.addAll(wr.getOffspringProcessings());
    Set<Integer> seen = new TreeSet<Integer>();

    while (!processings.isEmpty()) {
      Processing processing = processings.pop();

      // make sure we don't progress past this workflow run in the processing
      // tree
      Integer wrSWID = (processing.getWorkflowRun() == null ? null : processing.getWorkflowRun().getSwAccession());
      if (wrSWID == null) {
        wrSWID = (processing.getWorkflowRunByAncestorWorkflowRunId() == null ? null : processing
            .getWorkflowRunByAncestorWorkflowRunId().getSwAccession());
      }

      if (!seen.contains(processing.getSwAccession())) {
        if (wrSWID == null || wrSWID.equals(swAccession)) {
          files.addAll(processing.getFiles());
          processings.addAll(processing.getChildren());
          seen.add(processing.getSwAccession());
        }
      }
    }
    return new ArrayList<File>(files);
  }

  /** {@inheritDoc} */
  @Override
  public Set<WorkflowRun> findRunsForSample(Sample sample) {
    Set<WorkflowRun> runs = new HashSet<WorkflowRun>();
    Set<IUS> iuses = sample.getIUS();
    if (iuses != null) {
      Iterator<IUS> iusIter = iuses.iterator();
      while (iusIter.hasNext()) {
        IUS ius = iusIter.next();
        Set<WorkflowRun> wfRuns = ius.getWorkflowRuns();
        runs.addAll(wfRuns);
      }
    }
    Set<Processing> processings = sample.getProcessings();
    if (processings != null) {
      for (Processing proc : processings) {
        if (proc.getWorkflowRun() != null) {
          runs.add(proc.getWorkflowRun());
        }
      }
    }
    return runs;
  }

  /** {@inheritDoc} */
  @Override
  public ReturnValue runWorkflow(WorkflowInfo wi, String workflowRunAccession, String iniFilesStr, boolean noMetadata,
      String parentAccessionsStr, ArrayList<String> parentsLinkedToWR, Registration owner) {
    return new WorkflowManager().runWorkflow(wi, workflowRunAccession, iniFilesStr, noMetadata, parentAccessionsStr,
        parentsLinkedToWR, owner);
  }

  /** {@inheritDoc} */
  @Override
  public void update(Registration registration, WorkflowRun workflowRun) {
    workflowRunDAO.update(registration, workflowRun);
  }

  /** {@inheritDoc} */
  @Override
  public Integer insert(Registration registration, WorkflowRun workflowRun) {
    workflowRun.setCreateTimestamp(new Date());
    return workflowRunDAO.insert(registration, workflowRun);
  }

  /** {@inheritDoc} */
  @Override
  public void update(Registration registration, WorkflowRun workflowRun, List<Integer> laneIds) {
    workflowRunDAO.updateDetached(registration, workflowRun);
  }
}

// ex:sw=4:ts=4:
