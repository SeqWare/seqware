package net.sourceforge.seqware.common.business.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.seqware.common.business.ProcessingService;
import net.sourceforge.seqware.common.dao.FileDAO;
import net.sourceforge.seqware.common.dao.ProcessingDAO;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Registration;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.WorkflowRun;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProcessingServiceImpl implements ProcessingService {

  private ProcessingDAO processingDAO = null;
  private FileDAO fileDAO = null;
  private static final Log log = LogFactory.getLog(ProcessingServiceImpl.class);

  public ProcessingServiceImpl() {
    super();
  }

  /**
   * Sets a private member variable with an instance of an implementation of
   * ProcessingDAO. This method is called by the Spring framework at run time.
   * 
   * @param processingDAO
   *          implementation of ProcessingDAO
   * @see ProcessingDAO
   */
  public void setProcessingDAO(ProcessingDAO processingDAO) {
    this.processingDAO = processingDAO;
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

  /**
   * Inserts an instance of Processing into the database.
   * 
   * @param processingDAO
   *          instance of ProcessingDAO
   */
  public void insert(SequencerRun sequencerRun, Processing processing) {

    if (processing.getStatus() == null) {
      processing.setStatus("pending");
    }
    // processing.setExperimentId(experiment.getExperimentId());
    processing.setCreateTimestamp(new Date());
    processingDAO.insert(processing);

  }

  @Override
  public void insert(Registration registration, SequencerRun sequencerRun, Processing processing) {
    if (processing.getStatus() == null) {
      processing.setStatus("pending");
    }
    // processing.setExperimentId(experiment.getExperimentId());
    processing.setCreateTimestamp(new Date());
    processingDAO.insert(registration, processing);
  }

  @Override
  public Integer insert(Processing processing) {

    if (processing.getStatus() == null) {
      processing.setStatus("pending");
    }
    // processing.setExperimentId(experiment.getExperimentId());
    processing.setCreateTimestamp(new Date());
    return processingDAO.insert(processing);

  }

  /**
   * Updates an instance of Processing in the database.
   * 
   * @param processing
   *          instance of Processing
   */
  public void update(Processing processing) {
    processingDAO.update(processing);
  }

  public void delete(Processing processing, String deleteRealFiles) {
    List<File> deleteFiles = null;
    if ("yes".equals(deleteRealFiles)) {
      deleteFiles = processingDAO.getFiles(processing.getProcessingId());
    }

    Set<Study> studies = processing.getStudies();
    for (Study study : studies) {
      study.getProcessings().remove(processing);
    }

    Set<Experiment> experiments = processing.getExperiments();
    for (Experiment experiment : experiments) {
      experiment.getProcessings().remove(processing);
    }

    Set<Sample> samples = processing.getSamples();
    for (Sample sample : samples) {
      sample.getProcessings().remove(processing);
    }

    Set<SequencerRun> sequencerRuns = processing.getSequencerRuns();
    for (SequencerRun sequencerRun : sequencerRuns) {
      sequencerRun.getProcessings().remove(processing);
    }

    Set<Lane> lanes = processing.getLanes();
    for (Lane lane : lanes) {
      lane.getProcessings().remove(processing);
    }

    Set<IUS> iuses = processing.getIUS();
    for (IUS ius : iuses) {
      ius.getProcessings().remove(processing);
    }

    Set<Processing> parents = processing.getParents();
    for (Processing parent : parents) {
      parent.getChildren().remove(processing);
    }

    Set<Processing> children = processing.getChildren();
    for (Processing child : children) {
      child.getParents().remove(processing);
    }

    processingDAO.delete(processing);

    if ("yes".equals(deleteRealFiles)) {
      fileDAO.deleteAllWithFolderStore(deleteFiles);
    }
  }

  public List<File> getFiles(Integer processingId) {
    return processingDAO.getFiles(processingId);
  }

  public boolean isHasFile(Integer processingId) {
    return processingDAO.isHasFile(processingId);
  }

  public Set<Processing> setWithHasFile(Set<Processing> list) {
    for (Processing processing : list) {
      boolean isHasFile = isHasFile(processing.getProcessingId());
      processing.setIsHasFile(isHasFile);
      if (processing.getWorkflowRun() != null) {
        processing.getWorkflowRun().setIsHasFile(isHasFile);
      }
    }
    return list;
  }

  public List<File> getFiles(Integer processingId, String metaType) {
    return processingDAO.getFiles(processingId, metaType);
  }

  public boolean isHasFile(Integer processingId, String metaType) {
    return processingDAO.isHasFile(processingId, metaType);
  }

  public Set<Processing> setWithHasFile(Set<Processing> list, String metaType) {
    Set<Processing> result = new TreeSet<Processing>();
    for (Processing processing : list) {
      boolean isHasFile = isHasFile(processing.getProcessingId(), metaType);
      if (isHasFile) {
        processing.setIsHasFile(isHasFile);
        if (processing.getWorkflowRun() != null) {
          processing.getWorkflowRun().setIsHasFile(isHasFile);
        }
        result.add(processing);
      }
    }
    return result;
  }

  public Processing findByID(Integer id) {

    Processing processing = null;
    if (id != null) {
      try {
        processing = processingDAO.findByID(id);
      } catch (Exception exception) {
        log.error("Cannot find Processing by ID " + id);
        log.error(exception.getMessage());
      }
    }
    return processing;

  }

  public Processing findByIDOnlyWithRunningWR(Integer processingID) {
    Processing processing = findByID(processingID);
    /*
     * Set<Processing> all = processing.getChildren(); Set<Processing> res = new
     * TreeSet<Processing>();
     * 
     * // get processing with workflow run has not status equal completed for
     * (Processing pr : all) { WorkflowRun workflowRun = pr.getWorkflowRun();
     * if(workflowRun == null || !workflowRun.getStatus().equals("completed")){
     * res.add(pr); } }
     * 
     * processing.setChildren(res);
     */
    return processing;
  }

  @Override
  public Processing findBySWAccession(Integer swAccession) {
    Processing processing = null;
    if (swAccession != null) {
      try {
        processing = processingDAO.findBySWAccession(swAccession);
      } catch (Exception exception) {
        log.error("Cannot find Processing by swAccession " + swAccession);
        log.error(exception.getMessage());
      }
    }
    return processing;
  }

  @Override
  public List<Processing> findByOwnerID(Integer registrationId) {
    List<Processing> processings = null;
    if (registrationId != null) {
      try {
        processings = processingDAO.findByOwnerID(registrationId);
      } catch (Exception exception) {
        log.error("Cannot find Processings by registrationId " + registrationId);
        log.error(exception.getMessage());
      }
    }
    return processings;
  }

  @Override
  public List<Processing> findByCriteria(String criteria, boolean isCaseSens) {
    return processingDAO.findByCriteria(criteria, isCaseSens);
  }

  @Override
  public Processing updateDetached(Processing processing) {
    return processingDAO.updateDetached(processing);
  }

  @Override
  public List<Processing> list() {
    return processingDAO.list();
  }

  @Override
  public void update(Registration registration, Processing processing) {
    processingDAO.update(registration, processing);
  }

  @Override
  public Integer insert(Registration registration, Processing processing) {
    if (processing.getStatus() == null) {
      processing.setStatus("pending");
    }
    processing.setCreateTimestamp(new Date());
    return processingDAO.insert(registration, processing);
  }

  @Override
  public Processing updateDetached(Registration registration, Processing processing) {
    return processingDAO.updateDetached(registration, processing);
  }

  /**
   * Returns the Set of the Processings belongs to the specific Sample,
   * WorkflowRun.
   */
  @Override
  public Set<Processing> findFor(Sample sample, WorkflowRun workflowRun) {
    Set<Processing> processings = new HashSet<Processing>();
    if (sample.getProcessings() != null) {
      processings.addAll(sample.getProcessings());
      for (Processing proc : sample.getProcessings()) {
        addNestedProcessings(processings, proc);
      }
    }
    for (IUS ius : sample.getIUS()) {
      processings.addAll(ius.getProcessings());
      for (Processing proc : ius.getProcessings()) {
        addNestedProcessings(processings, proc);
      }
    }

    // Remove processings doesn't belong to the workflow run.
    if (workflowRun != null) {
      Iterator<Processing> processingIter = processings.iterator();
      while (processingIter.hasNext()) {
        Processing currProcessing = processingIter.next();
        if (!workflowRun.equals(currProcessing.getWorkflowRun())) {
          processingIter.remove();
        }
      }
    }
    return processings;
  }

  private void addNestedProcessings(Set<Processing> processings, Processing processing) {
    if (processing.getChildren() != null) {
      Set<Processing> childrenProcessings = processing.getChildren();
      for (Processing child : childrenProcessings) {
        addNestedProcessings(processings, child);
      }
      processings.addAll(childrenProcessings);
    }
  }

  @Override
  public Set<Processing> findFor(Sample sample) {
    return findFor(sample, null);
  }
}

// ex:sw=4:ts=4:
