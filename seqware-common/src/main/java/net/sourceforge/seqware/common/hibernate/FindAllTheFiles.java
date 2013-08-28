/*
 * Copyright (C) 2012 SeqWare
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.seqware.common.hibernate;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.FileAttribute;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.SequencerRunAttribute;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.StudyAttribute;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.model.WorkflowRunStatus;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;

import org.apache.log4j.Logger;

/**
 * Should only be used for one query and then discarded and re-instantiated. The
 * Session needs to be open for it to work (InSessionExecution). The list of
 * files is returned as-is, so I make no guarantees on constancy.
 *
 * @author mtaschuk
 * @version $Id: $Id
 */
public class FindAllTheFiles {

    public boolean isReportInputFiles() {
        return reportInputFiles;
    }

    public void setReportInputFiles(boolean reportInputFiles) {
        this.reportInputFiles = reportInputFiles;
    }

    private void addAttributeToReturnValue(ReturnValue ret, String key, String value) {
        if (ret.getAttribute(key) != null) {
          ret.setAttribute(key, ret.getAttribute(key) + ";" + value);
        } else {
          ret.setAttribute(key, value);
        }
    }

  public enum Header {
    STUDY_TITLE("Study Title"), STUDY_SWA("Study SWID"), STUDY_TAG_PREFIX("study."), STUDY_ATTRIBUTES(
        "Study Attributes"), EXPERIMENT_NAME("Experiment Name"), EXPERIMENT_SWA("Experiment SWID"), EXPERIMENT_TAG_PREFIX(
        "experiment."), EXPERIMENT_ATTRIBUTES("Experiment Attributes"), PARENT_SAMPLE_NAME("Parent Sample Name"), PARENT_SAMPLE_SWA(
        "Parent Sample SWID"), PARENT_SAMPLE_TAG_PREFIX("parent_sample."), PARENT_SAMPLE_ATTRIBUTES(
        "Parent Sample Attributes"), SAMPLE_NAME("Sample Name"), SAMPLE_SWA("Sample SWID"), SAMPLE_TAG_PREFIX("sample."), SAMPLE_ATTRIBUTES(
        "Sample Attributes"), IUS_SWA("IUS SWID"), IUS_TAG("IUS Tag"), IUS_TAG_PREFIX("ius."), IUS_ATTRIBUTES(
        "IUS Attributes"), LANE_NAME("Lane Name"), LANE_SWA("Lane SWID"), LANE_NUM("Lane Number"), LANE_TAG_PREFIX(
        "lane."), LANE_ATTRIBUTES("Lane Attributes"), SEQUENCER_RUN_NAME("Sequencer Run Name"), SEQUENCER_RUN_SWA(
        "Sequencer Run SWID"), SEQUENCER_RUN_TAG_PREFIX("sequencerrun."), SEQUENCER_RUN_ATTRIBUTES(
        "Sequencer Run Attributes"), WORKFLOW_RUN_NAME("Workflow Run Name"), WORKFLOW_RUN_SWA("Workflow Run SWID"), WORKFLOW_RUN_STATUS(
        "Workflow Run Status"), WORKFLOW_NAME("Workflow Name"), WORKFLOW_SWA("Workflow SWID"), WORKFLOW_VERSION(
        "Workflow Version"), FILE_SWA("File SWID"), FILE_TAG_PREFIX("file."), FILE_ATTRIBUTES("File Attributes"), PROCESSING_DATE("Last Modified"), PROCESSING_SWID("Processing SWID"), PROCESSING_ALGO(
        "Processing Algorithm"), PROCESSING_TAG_PREFIX("processing."), PROCESSING_ATTRIBUTES("Processing Attributes"),
        INPUT_FILE_META_TYPES("Input File Meta-Types"), INPUT_FILE_SWIDS("Input File SWIDs"), INPUT_FILE_PATHS("Input File Paths");
        ;
    private final String title;

    Header(String title) {
      this.title = title;
    }

    public String getTitle() {
      return title;
    }
  }

  private List<ReturnValue> returnValues = new ArrayList<ReturnValue>();
  /** Constant <code>STUDY_TITLE="Header.STUDY_TITLE.getTitle()"</code> */
  public static String STUDY_TITLE = Header.STUDY_TITLE.getTitle();
  /** Constant <code>STUDY_SWA="Header.STUDY_SWA.getTitle()"</code> */
  public static String STUDY_SWA = Header.STUDY_SWA.getTitle();
  /** Constant <code>STUDY_TAG_PREFIX="Header.STUDY_TAG_PREFIX.getTitle()"</code> */
  public static String STUDY_TAG_PREFIX = Header.STUDY_TAG_PREFIX.getTitle();
  /** Constant <code>STUDY_ATTRIBUTES="Header.STUDY_ATTRIBUTES.getTitle()"</code> */
  public static String STUDY_ATTRIBUTES = Header.STUDY_ATTRIBUTES.getTitle();
  /** Constant <code>EXPERIMENT_NAME="Header.EXPERIMENT_NAME.getTitle()"</code> */
  public static String EXPERIMENT_NAME = Header.EXPERIMENT_NAME.getTitle();
  /** Constant <code>EXPERIMENT_SWA="Header.EXPERIMENT_SWA.getTitle()"</code> */
  public static String EXPERIMENT_SWA = Header.EXPERIMENT_SWA.getTitle();
  /** Constant <code>EXPERIMENT_TAG_PREFIX="Header.EXPERIMENT_TAG_PREFIX.getTitle()"</code> */
  public static String EXPERIMENT_TAG_PREFIX = Header.EXPERIMENT_TAG_PREFIX.getTitle();
  /** Constant <code>EXPERIMENT_ATTRIBUTES="Header.EXPERIMENT_ATTRIBUTES.getTitle()"</code> */
  public static String EXPERIMENT_ATTRIBUTES = Header.EXPERIMENT_ATTRIBUTES.getTitle();
  /** Constant <code>PARENT_SAMPLE_NAME="Header.PARENT_SAMPLE_NAME.getTitle()"</code> */
  public static String PARENT_SAMPLE_NAME = Header.PARENT_SAMPLE_NAME.getTitle();
  /** Constant <code>PARENT_SAMPLE_SWA="Header.PARENT_SAMPLE_SWA.getTitle()"</code> */
  public static String PARENT_SAMPLE_SWA = Header.PARENT_SAMPLE_SWA.getTitle();
  /** Constant <code>PARENT_SAMPLE_TAG_PREFIX="Header.PARENT_SAMPLE_TAG_PREFIX.getTitl"{trunked}</code> */
  public static String PARENT_SAMPLE_TAG_PREFIX = Header.PARENT_SAMPLE_TAG_PREFIX.getTitle();
  /** Constant <code>PARENT_SAMPLE_ATTRIBUTES="Header.PARENT_SAMPLE_ATTRIBUTES.getTitl"{trunked}</code> */
  public static String PARENT_SAMPLE_ATTRIBUTES = Header.PARENT_SAMPLE_ATTRIBUTES.getTitle();
  /** Constant <code>SAMPLE_NAME="Header.SAMPLE_NAME.getTitle()"</code> */
  public static String SAMPLE_NAME = Header.SAMPLE_NAME.getTitle();
  /** Constant <code>SAMPLE_SWA="Header.SAMPLE_SWA.getTitle()"</code> */
  public static String SAMPLE_SWA = Header.SAMPLE_SWA.getTitle();
  /** Constant <code>SAMPLE_TAG_PREFIX="Header.SAMPLE_TAG_PREFIX.getTitle()"</code> */
  public static String SAMPLE_TAG_PREFIX = Header.SAMPLE_TAG_PREFIX.getTitle();
  /** Constant <code>SAMPLE_ATTRIBUTES="Header.SAMPLE_ATTRIBUTES.getTitle()"</code> */
  public static String SAMPLE_ATTRIBUTES = Header.SAMPLE_ATTRIBUTES.getTitle();
  /** Constant <code>IUS_SWA="Header.IUS_SWA.getTitle()"</code> */
  public static String IUS_SWA = Header.IUS_SWA.getTitle();
  /** Constant <code>IUS_TAG="Header.IUS_TAG.getTitle()"</code> */
  public static String IUS_TAG = Header.IUS_TAG.getTitle();
  /** Constant <code>IUS_TAG_PREFIX="Header.IUS_TAG_PREFIX.getTitle()"</code> */
  public static String IUS_TAG_PREFIX = Header.IUS_TAG_PREFIX.getTitle();
  /** Constant <code>IUS_ATTRIBUTES="Header.IUS_ATTRIBUTES.getTitle()"</code> */
  public static String IUS_ATTRIBUTES = Header.IUS_ATTRIBUTES.getTitle();
  /** Constant <code>LANE_NAME="Header.LANE_NAME.getTitle()"</code> */
  public static String LANE_NAME = Header.LANE_NAME.getTitle();
  /** Constant <code>LANE_SWA="Header.LANE_SWA.getTitle()"</code> */
  public static String LANE_SWA = Header.LANE_SWA.getTitle();
  /** Constant <code>LANE_NUM="Header.LANE_NUM.getTitle()"</code> */
  public static String LANE_NUM = Header.LANE_NUM.getTitle();
  /** Constant <code>LANE_TAG_PREFIX="Header.LANE_TAG_PREFIX.getTitle()"</code> */
  public static String LANE_TAG_PREFIX = Header.LANE_TAG_PREFIX.getTitle();
  /** Constant <code>LANE_ATTRIBUTES="Header.LANE_ATTRIBUTES.getTitle()"</code> */
  public static String LANE_ATTRIBUTES = Header.LANE_ATTRIBUTES.getTitle();
  /** Constant <code>SEQUENCER_RUN_NAME="Header.SEQUENCER_RUN_NAME.getTitle()"</code> */
  public static String SEQUENCER_RUN_NAME = Header.SEQUENCER_RUN_NAME.getTitle();
  /** Constant <code>SEQUENCER_RUN_SWA="Header.SEQUENCER_RUN_SWA.getTitle()"</code> */
  public static String SEQUENCER_RUN_SWA = Header.SEQUENCER_RUN_SWA.getTitle();
  /** Constant <code>SEQUENCER_RUN_TAG_PREFIX="Header.SEQUENCER_RUN_TAG_PREFIX.getTitl"{trunked}</code> */
  public static String SEQUENCER_RUN_TAG_PREFIX = Header.SEQUENCER_RUN_TAG_PREFIX.getTitle();
  /** Constant <code>SEQUENCER_RUN_ATTRIBUTES="Header.SEQUENCER_RUN_ATTRIBUTES.getTitl"{trunked}</code> */
  public static String SEQUENCER_RUN_ATTRIBUTES = Header.SEQUENCER_RUN_ATTRIBUTES.getTitle();
  /** Constant <code>WORKFLOW_RUN_NAME="Header.WORKFLOW_RUN_NAME.getTitle()"</code> */
  public static String WORKFLOW_RUN_NAME = Header.WORKFLOW_RUN_NAME.getTitle();
  /** Constant <code>WORKFLOW_RUN_SWA="Header.WORKFLOW_RUN_SWA.getTitle()"</code> */
  public static String WORKFLOW_RUN_SWA = Header.WORKFLOW_RUN_SWA.getTitle();
  /** Constant <code>WORKFLOW_RUN_STATUS="Header.WORKFLOW_RUN_STATUS.getTitle()"</code> */
  public static String WORKFLOW_RUN_STATUS = Header.WORKFLOW_RUN_STATUS.getTitle();
  /** Constant <code>WORKFLOW_NAME="Header.WORKFLOW_NAME.getTitle()"</code> */
  public static String WORKFLOW_NAME = Header.WORKFLOW_NAME.getTitle();
  /** Constant <code>WORKFLOW_SWA="Header.WORKFLOW_SWA.getTitle()"</code> */
  public static String WORKFLOW_SWA = Header.WORKFLOW_SWA.getTitle();
  /** Constant <code>WORKFLOW_VERSION="Header.WORKFLOW_VERSION.getTitle()"</code> */
  public static String WORKFLOW_VERSION = Header.WORKFLOW_VERSION.getTitle();
  /** Constant <code>FILE_SWA="Header.FILE_SWA.getTitle()"</code> */
  public static String FILE_SWA = Header.FILE_SWA.getTitle();
  public static String FILE_TAG_PREFIX = Header.FILE_TAG_PREFIX.getTitle();
  public static String FILE_ATTRIBUTES = Header.FILE_ATTRIBUTES.getTitle();
  /** Constant <code>PROCESSING_DATE="Header.PROCESSING_DATE.getTitle()"</code> */
  public static String PROCESSING_DATE = Header.PROCESSING_DATE.getTitle();
  /** Constant <code>PROCESSING_SWID="Header.PROCESSING_SWID.getTitle()"</code> */
  public static String PROCESSING_SWID = Header.PROCESSING_SWID.getTitle();
  /** Constant <code>PROCESSING_ALGO="Header.PROCESSING_ALGO.getTitle()"</code> */
  public static String PROCESSING_ALGO = Header.PROCESSING_ALGO.getTitle();
  /** Constant <code>PROCESSING_TAG_PREFIX="Header.PROCESSING_TAG_PREFIX.getTitle()"</code> */
  public static String PROCESSING_TAG_PREFIX = Header.PROCESSING_TAG_PREFIX.getTitle();
  /** Constant <code>PROCESSING_ATTRIBUTES="Header.PROCESSING_ATTRIBUTES.getTitle()"</code> */
  public static String PROCESSING_ATTRIBUTES = Header.PROCESSING_ATTRIBUTES.getTitle();
  
  public static String INPUT_FILE_META_TYPES = Header.INPUT_FILE_META_TYPES.getTitle();
  public static String INPUT_FILE_FILE_PATHS = Header.INPUT_FILE_PATHS.getTitle();
  public static String INPUT_FILE_SWIDS = Header.INPUT_FILE_SWIDS.getTitle();
  
  /** Constant <code>FILETYPE_ALL="all"</code> */
  public static final String FILETYPE_ALL = "all";
  private Set<Integer> fileSwas = new HashSet<Integer>();
  private Set<Integer> usefulProcessings = new TreeSet<Integer>();
  private Set<Integer> unUsefulProcessings = new TreeSet<Integer>();
  private Set<Integer> seenWorkflowRuns = new TreeSet<Integer>();
  /**
   * When true, we report only leafs that are nodes.
   * When false, we report all leafs regardless of whether they are nodes.
   */
  private boolean requireFiles = true;
  
  /**
   * When true, we also crawl and report input files for input files.
   * This could be quite resource intensive in this naive first approach.
   */
  private boolean reportInputFiles = false;
  
  private Logger logger = Logger.getLogger(FindAllTheFiles.class);

  /**
   * FIXME: notice processings directly attached to study aren't examined!
   *
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a {@link java.util.List} object.
   */
  public List<ReturnValue> filesFromStudy(Study study) {
    logger.debug("filesFromStudy. There are " + study.getExperiments().size() + " experiments.");
    for (Experiment e : study.getExperiments()) {
      filesFromExperiment(e, study);
    }
    return returnValues;
  }

  /**
   * FIXME: notice processings directly attached to study aren't examined!
   *
   * @param e a {@link net.sourceforge.seqware.common.model.Experiment} object.
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a {@link java.util.List} object.
   */
  public List<ReturnValue> filesFromExperiment(Experiment e, Study study) {
    logger.debug("filesFromExperiment. There are " + e.getSamples().size() + " samples.");
    for (Sample parentSample : e.getSamples()) {
      filesFromSample(parentSample, e, study);
    }
    return returnValues;
  }

  /**
   * <p>filesFromSample.</p>
   *
   * @param parentSample a {@link net.sourceforge.seqware.common.model.Sample} object.
   * @param e a {@link net.sourceforge.seqware.common.model.Experiment} object.
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a {@link java.util.List} object.
   */
  public List<ReturnValue> filesFromSample(Sample parentSample, Experiment e, Study study) {
    Stack<Sample> sampleStack = new Stack<Sample>();
    sampleStack.add(parentSample);
    Set<Sample> usefulSamples = new TreeSet<Sample>();
    logger.debug("filesFromSample Parent:" + parentSample.getName());
    while (!sampleStack.isEmpty()) {
      Sample sample = sampleStack.pop();
      logger.debug("filesFromSample Child:" + sample.getName());
      sampleStack.addAll(sample.getChildren());
      if (sample.getIUS().size() > 0 || sample.getProcessings().size() > 0) {
        usefulSamples.add(sample);
      }
    }

    for (Sample sample : usefulSamples) {

      // check for files attached to IUS
      SortedSet<IUS> iuses = sample.getIUS();
      for (IUS ius : iuses) {
        logger.debug("filesFromSample IUS: " + ius.getSwAccession());
        filesFromIUS(ius, e, sample, study);
      }

      // try alternate route if there are no iuses
      if (iuses != null && iuses.isEmpty()) {
        Set<Lane> lanes = sample.getLanes();
        for (Lane lane : lanes) {
          logger.debug("filesFromSample Lane: " + lane.getName());
          filesFromLane(lane, e, study);
        }
        // print this sample information if we can't go any further
        if (!requireFiles && lanes != null && lanes.isEmpty()) {
          ReturnValue ret = createReturnValue(sample, null, study, e, null, null, null, null, null);
          returnValues.add(ret);
        }

      }

      // now deal with processings directly attached to this
      Set<Processing> processings = sample.getProcessings();
      if (processings != null && processings.size() > 0) {

        Set<Processing> processingStack = new TreeSet<Processing>();

        for (Processing processing : processings) {
          parseProcessingsFromStack(processing, processingStack);
        }

        for (Processing processing : processingStack) {
          filesFromProcessing(processing, e, sample, study, null, null, null);
        }
      }
    }

    return returnValues;
  }

  /**
   * <p>filesFromIUS.</p>
   *
   * @param ius a {@link net.sourceforge.seqware.common.model.IUS} object.
   * @param e a {@link net.sourceforge.seqware.common.model.Experiment} object.
   * @param sample a {@link net.sourceforge.seqware.common.model.Sample} object.
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a {@link java.util.List} object.
   */
  public List<ReturnValue> filesFromIUS(IUS ius, Experiment e, Sample sample, Study study) {
    Lane lane = ius.getLane();
    SequencerRun sequencerRun = lane.getSequencerRun();
    Set<Processing> currentProcessings = new TreeSet<Processing>();

    parseProcessingsFromStack(ius, lane, currentProcessings);
    // if (study != null) { // because the workflow_run level amalgamates
    // multiple samples
    // logger.debug("Iterating through workflow runs");
    // Set<WorkflowRun> workflowRuns = parseWorkflowRunsFromIUSandLane(ius,
    // lane);
    // parseProcessingsFromWorkflowRuns(workflowRuns, currentProcessings);
    // }
    // SEQWARE-1297 - we need to go down the ius_workflow_run link table to report 
    // workflow_runs which may not be complete yet, but we cannot return files from it since
    // there is ambiguity as to which ius they may belong to
    if (!requireFiles) {
        Set<WorkflowRun> workflowRuns = ius.getWorkflowRuns();
        for(WorkflowRun wr : workflowRuns){
            ReturnValue ret = createReturnValue(sample, null, study, e, ius, lane, sequencerRun, wr, wr.getWorkflow());
            returnValues.add(ret);
        }
    }

    
    for (Processing processing : currentProcessings) {
      filesFromProcessing(processing, e, sample, study, ius, lane, sequencerRun);
    }

    // if we go down ius_workflow_run then the ius is no longer a leaf
    if (currentProcessings.isEmpty() && !requireFiles && ius.getWorkflowRuns().isEmpty()) {
      ReturnValue ret = createReturnValue(sample, null, study, e, ius, lane, sequencerRun, null, null);
      returnValues.add(ret);
    }
    return returnValues;
  }

  /**
   * <p>filesFromLane.</p>
   *
   * @param lane a {@link net.sourceforge.seqware.common.model.Lane} object.
   * @param e a {@link net.sourceforge.seqware.common.model.Experiment} object.
   * @param study a {@link net.sourceforge.seqware.common.model.Study} object.
   * @return a {@link java.util.List} object.
   */
  public List<ReturnValue> filesFromLane(Lane lane, Experiment e, Study study) {
    SortedSet<IUS> ius = lane.getIUS();
    // logger.debug("filesFromLane. There are " + ius.size() + " IUSes");
    for (IUS i : ius) {
      filesFromIUS(i, e, i.getSample(), study);
    }

    // try going through the Lanes to get the workflow runs and the processing
    // entries
    if (ius != null && ius.isEmpty()) {
      Set<WorkflowRun> runs = lane.getWorkflowRuns();
      Set<Processing> ps = new TreeSet<Processing>();
      parseProcessingsFromWorkflowRuns(runs, ps);

      for (Processing processing : ps) {
        filesFromProcessing(processing, e, null, study, null, lane, lane.getSequencerRun());
      }
      if (!requireFiles && ps.isEmpty()) {
        ReturnValue ret = createReturnValue(null, null, study, e, null, lane, lane.getSequencerRun(), null, null);
        returnValues.add(ret);
      }
    }

    return returnValues;
  }

  private int filesFromProcessing(Processing processing, Experiment e, Sample sample, Study study, IUS ius, Lane lane,
      SequencerRun sequencerRun) {
    WorkflowRun workflowRun = processing.getWorkflowRun();
    Workflow workflow = null;
    if (workflowRun == null) {
      workflowRun = processing.getWorkflowRunByAncestorWorkflowRunId();
    }
    if (workflowRun != null) {
      workflow = workflowRun.getWorkflow();
    }
    int numFiles = processing.getFiles().size();

    // SEQWARE-1297
    // before this ticket, this either reported empty processing events when we don't require files 
    // OR we reported the files. We actually want to report the files if they are present
    if (!requireFiles && processing.getFiles().isEmpty()) {
      ReturnValue ret = createReturnValue(sample, processing, study, e, ius, lane, sequencerRun, workflowRun, workflow);
      returnValues.add(ret);
    } else {
      for (File file : processing.getFiles()) {
        printFileInfo(e, sample, study, ius, lane, sequencerRun, processing, workflowRun, workflow, file);
      }
    }
    return numFiles;
  }
  
  private void parseProcessingsFromStack(IUS ius, Lane lane, Set<Processing> currentProcessings) {
    Stack<Processing> processingStack = new Stack<Processing>();
    processingStack.addAll(ius.getProcessings());
    Stack<Processing> parents = new Stack<Processing>();

    for (Processing p : ius.getProcessings()) {
      parents.addAll(p.getParents());
    }

    while (!processingStack.isEmpty()) {
      Processing processing = processingStack.pop();
      if (!usefulProcessings.contains(processing.getProcessingId())
          && !unUsefulProcessings.contains(processing.getProcessingId())) {
        processingStack.addAll(processing.getChildren());
        if (processing.getFiles() != null && processing.getFiles().size() > 0) {
          logger.debug("parseProcessingsFromStack adding " + processing.getAlgorithm() + ":"
              + processing.getSwAccession());
          usefulProcessings.add(processing.getProcessingId());
          currentProcessings.add(processing);
        } else {
          unUsefulProcessings.add(processing.getProcessingId());
        }
      }
    }

    while (!parents.isEmpty()) {
      Processing processing = parents.pop();
      if (!usefulProcessings.contains(processing.getProcessingId())
          && !unUsefulProcessings.contains(processing.getProcessingId())) {
        parents.addAll(processing.getParents());
        if (processing.getFiles() != null && processing.getFiles().size() > 0) {
          logger.debug("parseProcessingsFromStack adding " + processing.getAlgorithm() + ":"
              + processing.getSwAccession());
          usefulProcessings.add(processing.getProcessingId());
          currentProcessings.add(processing);
        } else {
          unUsefulProcessings.add(processing.getProcessingId());
        }
      }
    }

  }

  private void parseProcessingsFromStack(Processing currProcessing, Set<Processing> currentProcessings) {
    Stack<Processing> processingStack = new Stack<Processing>();
    processingStack.add(currProcessing);
    Stack<Processing> parents = new Stack<Processing>();
    parents.add(currProcessing);

    while (!processingStack.isEmpty()) {
      Processing processing = processingStack.pop();
      if (!usefulProcessings.contains(processing.getProcessingId())
          && !unUsefulProcessings.contains(processing.getProcessingId())) {
        processingStack.addAll(processing.getChildren());
        if (processing.getFiles() != null && processing.getFiles().size() > 0) {
          logger.debug("parseProcessingsFromStack adding " + processing.getAlgorithm() + ":"
              + processing.getSwAccession());
          usefulProcessings.add(processing.getProcessingId());
          currentProcessings.add(processing);
        } else {
          unUsefulProcessings.add(processing.getProcessingId());
        }
      }
    }

    while (!parents.isEmpty()) {
      Processing processing = parents.pop();
      if (!usefulProcessings.contains(processing.getProcessingId())
          && !unUsefulProcessings.contains(processing.getProcessingId())) {
        parents.addAll(processing.getParents());
        if (processing.getFiles() != null && processing.getFiles().size() > 0) {
          logger.debug("parseProcessingsFromStack adding " + processing.getAlgorithm() + ":"
              + processing.getSwAccession());
          usefulProcessings.add(processing.getProcessingId());
          currentProcessings.add(processing);
        } else {
          unUsefulProcessings.add(processing.getProcessingId());
        }
      }
    }

  }

  private void parseProcessingsFromWorkflowRuns(Set<WorkflowRun> workflowRuns, Set<Processing> currentProcessings) {
    for (WorkflowRun run : workflowRuns) {
      if (!seenWorkflowRuns.contains(run.getWorkflowRunId())) {
        for (Processing p : run.getProcessings()) {
          if (!usefulProcessings.contains(p.getProcessingId()) && !unUsefulProcessings.contains(p.getProcessingId())) {
            if (p.getFiles() != null && p.getFiles().size() > 0) {
              usefulProcessings.add(p.getProcessingId());
              currentProcessings.add(p);
            } else {
              unUsefulProcessings.add(p.getProcessingId());
            }
          }
        }
        seenWorkflowRuns.add(run.getWorkflowRunId());
      }
    }
  }

  private List<ReturnValue> printFileInfo(Experiment e, Sample sample, Study study, IUS ius, Lane lane,
      SequencerRun sequencerRun, Processing processing, WorkflowRun workflowRun, Workflow workflow, File file) {

    if (fileSwas.contains(file.getSwAccession())) {
      return null;
    } else {
      fileSwas.add(file.getSwAccession());
    }
    ReturnValue ret = createReturnValue(sample, processing, study, e, ius, lane, sequencerRun, workflowRun, workflow);
    // File
    FileMetadata fm = new FileMetadata();
    ret.setAttribute(FILE_SWA, file.getSwAccession().toString());
      if (file.getSkip() != null && file.getSkip()) {
          addAttributeToReturnValue(ret, FILE_TAG_PREFIX + "skip", "");
      }
      for (FileAttribute fa : file.getFileAttributes()) {
          String key = FILE_TAG_PREFIX + fa.getTag();
          addAttributeToReturnValue(ret, key, fa.getValue());
      }
    fm.setFilePath(file.getFilePath());
    fm.setMetaType(file.getMetaType());
    fm.setDescription(file.getSwAccession().toString());
    ArrayList<FileMetadata> files = new ArrayList<FileMetadata>();
    files.add(fm);

    ret.setFiles(files);

    returnValues.add(ret);
    return returnValues;
  }

  private ReturnValue createReturnValue(Sample sample, Processing processing, Study study, Experiment e, IUS ius,
      Lane lane, SequencerRun sequencerRun, WorkflowRun workflowRun, Workflow workflow) {
    ReturnValue ret = new ReturnValue();

    // SET ALL ATTRIBUTES
    // Study
    if (study != null) {
      ret.setAttribute(STUDY_TITLE, study.getTitle());
      ret.setAttribute(STUDY_SWA, study.getSwAccession().toString());
      for (StudyAttribute sa : study.getStudyAttributes()) {
        String key = STUDY_TAG_PREFIX + sa.getTag();
        if (ret.getAttribute(key) != null) {
          ret.setAttribute(key, ret.getAttribute(key) + ";" + sa.getValue());
        } else {
          ret.setAttribute(key, sa.getValue());
        }
      }
    }
    // Experiment
    if (e != null) {
      String eName = e.getName();
      if (eName == null || eName.isEmpty()) {
        eName = e.getTitle();
      }
      ret.setAttribute(EXPERIMENT_NAME, eName);
      ret.setAttribute(EXPERIMENT_SWA, e.getSwAccession().toString());
      for (ExperimentAttribute ea : e.getExperimentAttributes()) {
        String key = EXPERIMENT_TAG_PREFIX + ea.getTag();
        if (ret.getAttribute(key) != null) {
          ret.setAttribute(key, ret.getAttribute(key) + ";" + ea.getValue());
        } else {
          ret.setAttribute(key, ea.getValue());
        }
      }
    }

    // Sample
    if (sample != null) {
      // Portal uses titles by default and names may be blank. Pipeline always
      // uses names. So check both. (Title should always be populated, but we
      // prefer name).
      String sName = sample.getName();
      if (sName == null || sName.isEmpty()) {
        sName = sample.getTitle();
      }
      StringBuilder psName = new StringBuilder();
      StringBuilder psSwa = new StringBuilder();

      Stack<Sample> parentSamples = new Stack<Sample>();
      parentSamples.addAll(sample.getParents());
      while (!parentSamples.isEmpty()) {
        Sample parentSample = parentSamples.pop();
        parentSamples.addAll(parentSample.getParents());
        String name = parentSample.getName();
        if (name == null || name.isEmpty()) {
          name = parentSample.getTitle();
        }
        psName.append(name).append(":");
        psSwa.append(parentSample.getSwAccession()).append(":");
        if (parentSample.getSkip() != null && parentSample.getSkip()) {
          addAttributeToReturnValue(ret, PARENT_SAMPLE_TAG_PREFIX + "skip." + parentSample.getSwAccession(), "");
        }
        for (SampleAttribute satt : parentSample.getSampleAttributes()) {
          String key = PARENT_SAMPLE_TAG_PREFIX + satt.getTag() + "." + parentSample.getSwAccession();
          addAttributeToReturnValue(ret, key, satt.getValue());
        }
      }
      // Parent Sample
      if (sample.getParents() != null) {
        ret.setAttribute(PARENT_SAMPLE_NAME, psName.toString());
        ret.setAttribute(PARENT_SAMPLE_SWA, psSwa.toString());

      }
      ret.setAttribute(SAMPLE_NAME, sName);
      ret.setAttribute(SAMPLE_SWA, sample.getSwAccession().toString());
      if (sample.getSkip() != null && sample.getSkip()){
          addAttributeToReturnValue(ret, SAMPLE_TAG_PREFIX + "skip", "");
      }
      for (SampleAttribute satt : sample.getSampleAttributes()) {
        String key = SAMPLE_TAG_PREFIX + satt.getTag();
        addAttributeToReturnValue(ret, key, satt.getValue());
      }
    }
    // IUS
    if (ius != null) {
      ret.setAttribute(IUS_SWA, ius.getSwAccession().toString());
      ret.setAttribute(IUS_TAG, (ius.getTag() == null ? "NoIndex" : ius.getTag()));
      if (ius.getSkip() != null && ius.getSkip()){
          addAttributeToReturnValue(ret, IUS_TAG_PREFIX + "skip", "");
      }
      for (IUSAttribute iatt : ius.getIusAttributes()) {
        String key = Header.IUS_TAG_PREFIX.getTitle() + iatt.getTag();
        addAttributeToReturnValue(ret, key, iatt.getValue());
      }
    }
    // Lane
    if (lane != null) {
      ret.setAttribute(LANE_NAME, lane.getName());
      ret.setAttribute(LANE_SWA, lane.getSwAccession().toString());
      int num = 0;
      if (lane.getLaneIndex() != null) {
        num = lane.getLaneIndex();
      }
      ret.setAttribute(LANE_NUM, new Integer(num + 1).toString());
      if (lane.getSkip() != null && lane.getSkip()){
          addAttributeToReturnValue(ret, LANE_TAG_PREFIX + "skip", "");
      }
      for (LaneAttribute latt : lane.getLaneAttributes()) {
        String key = LANE_TAG_PREFIX + latt.getTag();
        addAttributeToReturnValue(ret, key, latt.getValue());
      }
    }
    // Sequencer Run
    if (sequencerRun != null) {
      ret.setAttribute(SEQUENCER_RUN_NAME, sequencerRun.getName());
      ret.setAttribute(SEQUENCER_RUN_SWA, sequencerRun.getSwAccession().toString());
      if (sequencerRun.getSkip() != null && sequencerRun.getSkip()){
          addAttributeToReturnValue(ret, SEQUENCER_RUN_TAG_PREFIX + "skip", "");
      }
      for (SequencerRunAttribute sra : sequencerRun.getSequencerRunAttributes()) {
        String key = SEQUENCER_RUN_TAG_PREFIX + sra.getTag();
        addAttributeToReturnValue(ret, key, sra.getValue());
      }
    }
      // WorkflowRun
      if (workflowRun != null) {
          ret.setAttribute(WORKFLOW_RUN_NAME, workflowRun.getName());
          ret.setAttribute(WORKFLOW_RUN_SWA, workflowRun.getSwAccession().toString());
          ret.setAttribute(WORKFLOW_RUN_STATUS, workflowRun.getStatus().name());

          if (this.isReportInputFiles()) {
              WorkflowRunReport wrr = new WorkflowRunReport();
              Collection<Processing> processings = wrr.collectProcessings(workflowRun);
              Collection<Processing> parentProcessings = wrr.findParents(processings, workflowRun.getSwAccession(), false);
              Collection<File> inputFiles = wrr.findFiles(parentProcessings);
              StringBuilder fileTypes = new StringBuilder();
              StringBuilder filePaths = new StringBuilder();
              StringBuilder fileSWIDs = new StringBuilder();
              for (File f : inputFiles) {
                  if (fileTypes.length() != 0) {
                      fileTypes.append(",");
                  }
                  if (filePaths.length() != 0) {
                      filePaths.append(",");
                  }
                  if (fileSWIDs.length() != 0) {
                      fileSWIDs.append(",");
                  }
                  fileTypes.append(f.getMetaType());
                  filePaths.append(f.getFilePath());
                  fileSWIDs.append(f.getSwAccession());
              }
              ret.setAttribute(INPUT_FILE_META_TYPES, fileTypes.toString());
              ret.setAttribute(INPUT_FILE_SWIDS, fileSWIDs.toString());
              ret.setAttribute(INPUT_FILE_FILE_PATHS, filePaths.toString());
          }

      }
    // Workflow
    if (workflow != null) {
      ret.setAttribute(WORKFLOW_NAME, workflow.getName());
      ret.setAttribute(WORKFLOW_SWA, workflow.getSwAccession().toString());
      ret.setAttribute(WORKFLOW_VERSION, workflow.getVersion());
    }
    if (processing != null) {
      ret.setAlgorithm(processing.getAlgorithm());
      ret.setAttribute(PROCESSING_DATE, processing.getUpdateTimestamp().toString());
      ret.setRunStopTstmp(processing.getUpdateTimestamp());
      ret.setAttribute(PROCESSING_SWID, processing.getSwAccession().toString());
      ret.setAttribute(PROCESSING_ALGO, processing.getAlgorithm());
      for (ProcessingAttribute pa : processing.getProcessingAttributes()) {
        String key = PROCESSING_TAG_PREFIX + pa.getTag();
        if (ret.getAttribute(key) != null) {
          ret.setAttribute(key, ret.getAttribute(key) + ";" + pa.getValue());
        } else {
          ret.setAttribute(key, pa.getValue());
        }
      }
    }

    return ret;
  }

  /**
   * <p>Getter for the field <code>returnValues</code>.</p>
   *
   * @return a {@link java.util.List} object.
   */
  public List<ReturnValue> getReturnValues() {
    return returnValues;
  }

  /**
   * <p>isRequireFiles.</p>
   *
   * @return a boolean.
   */
  public boolean isRequireFiles() {
    return requireFiles;
  }

  /**
   * <p>Setter for the field <code>requireFiles</code>.</p>
   *
   * @param requireFiles a boolean.
   */
  public void setRequireFiles(boolean requireFiles) {
    this.requireFiles = requireFiles;
  }

  /**
   * <p>filterReturnValues.</p>
   *
   * @param returnValues a {@link java.util.List} object.
   * @param studyName a {@link java.lang.String} object.
   * @param fileType a {@link java.lang.String} object.
   * @param duplicates a boolean.
   * @param showFailedAndRunning a boolean.
   * @param showStatus a boolean.
   * @return a {@link java.util.List} object.
   * @throws java.io.IOException if any.
   */
  public static List<ReturnValue> filterReturnValues(List<ReturnValue> returnValues, String studyName, String fileType,
      boolean duplicates, boolean showFailedAndRunning, boolean showStatus) throws IOException {

    List<ReturnValue> newReturnValues = new ArrayList<ReturnValue>();

    Log.info("There are " + returnValues.size() + " files in total before filtering");
    Set<FileMetadata> set = new TreeSet<FileMetadata>(new Comparator<FileMetadata>() {

      @Override
      public int compare(FileMetadata t, FileMetadata t1) {
        return t.getFilePath().compareTo(t1.getFilePath());
      }
    });
    for (ReturnValue rv : returnValues) {
      ArrayList<FileMetadata> cloneFiles = (ArrayList<FileMetadata>) rv.getFiles().clone();

      if (!duplicates) {
        for (FileMetadata file : cloneFiles) {
          if (!set.add(file)) {
            Log.debug("Removing file because file is a duplicate: " + file.getFilePath());
            rv.getFiles().remove(file);
          }
        }
      }

      for (FileMetadata file : cloneFiles) {
        if (!fileType.equals(FILETYPE_ALL)) {
          if (!file.getMetaType().equals(fileType)) {
            Log.debug("Removing file because filetype is wrong:" + file.getMetaType() + " is not " + fileType);
            rv.getFiles().remove(file);
          }
        }
      }
      if (rv.getFiles().isEmpty()) {
        Log.debug("Files are empty. Skipping.");
        continue;
      }

      // if the returnValue isn't successful, remove it
      if (rv.getExitStatus() != ReturnValue.SUCCESS) {
        Log.error("Exit status: " + rv.getExitStatus());
        continue;
      }

      // if the workflow run is not successful and we don't want to see all of
      // the files, then skip it.
      String workflowRunStatus = rv.getAttribute(FindAllTheFiles.WORKFLOW_RUN_STATUS);
      if (workflowRunStatus != null && WorkflowRunStatus.valueOf(workflowRunStatus) != WorkflowRunStatus.completed) {
        if (!showFailedAndRunning) {
          Log.debug("Not showing failed or running workflow run" + workflowRunStatus);
          continue;
        }
      }

      replaceSpaces(rv, FindAllTheFiles.EXPERIMENT_NAME);
      replaceSpaces(rv, FindAllTheFiles.PARENT_SAMPLE_NAME);
      replaceSpaces(rv, FindAllTheFiles.SAMPLE_NAME);
      replaceSpaces(rv, FindAllTheFiles.LANE_NAME);
      replaceSpaces(rv, FindAllTheFiles.SEQUENCER_RUN_NAME);
      replaceSpaces(rv, FindAllTheFiles.WORKFLOW_RUN_NAME);
      replaceSpaces(rv, FindAllTheFiles.WORKFLOW_NAME);
      newReturnValues.add(rv);
    }
    Log.info("There are " + newReturnValues.size() + " files in total after filtering");
    return newReturnValues;
  }
  
  public static void printTSVFile(Writer writer, boolean showStatus, List<ReturnValue> returnValues, String studyName) throws IOException {
      printTSVFile(writer, showStatus, returnValues, studyName, false);
  }

  /**
   * <p>printTSVFile.</p>
   *
   * @param writer a {@link java.io.Writer} object.
   * @param showStatus a boolean.
   * @param returnValues a {@link java.util.List} object.
   * @param studyName a {@link java.lang.String} object.
   * @throws java.io.IOException if any.
   */
  public static void printTSVFile(Writer writer, boolean showStatus, List<ReturnValue> returnValues, String studyName, boolean reportInputFiles)
      throws IOException {
    Log.info("Creating TSV file");
    // Write the Excel file
    printHeader(writer, showStatus, reportInputFiles);
    for (ReturnValue rv : returnValues) {
      for (FileMetadata fm : rv.getFiles()) {
        print(writer, rv, studyName, showStatus, fm, reportInputFiles);
      }
    }
  }

  private static void replaceSpaces(ReturnValue rv, String attribute) {
    String att = rv.getAttribute(attribute);
    if (att != null) {
      att = att.replace(" ", "_");
    }
    rv.setAttribute(attribute, att);
  }

  public static void print(Writer writer, ReturnValue ret, String studyName, boolean showStatus, FileMetadata fm)
      throws IOException {
      print(writer, ret, studyName, showStatus, fm, false);
  }
  
  /**
   * Prints a line to the Excel spreadsheet.
   *
   * @throws java.io.IOException if any.
   * @param writer a {@link java.io.Writer} object.
   * @param ret a {@link net.sourceforge.seqware.common.module.ReturnValue} object.
   * @param studyName a {@link java.lang.String} object.
   * @param showStatus a boolean.
   * @param fm a {@link net.sourceforge.seqware.common.module.FileMetadata} object.
   */
  public static void print(Writer writer, ReturnValue ret, String studyName, boolean showStatus, FileMetadata fm, boolean reportInputFiles)
      throws IOException {
    StringBuilder parentSampleTag = new StringBuilder();
    StringBuilder sampleTag = new StringBuilder();
    StringBuilder laneTag = new StringBuilder();
    StringBuilder studyTag = new StringBuilder();
    StringBuilder experimentTag = new StringBuilder();
    StringBuilder iusTag = new StringBuilder();
    StringBuilder seqencerrunTag = new StringBuilder();
    StringBuilder processingTag = new StringBuilder();
    StringBuilder fileTag = new StringBuilder();
    for (String key : ret.getAttributes().keySet()) {
      if (key.startsWith(FindAllTheFiles.PARENT_SAMPLE_TAG_PREFIX)) {
        parentSampleTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
      } else if (key.startsWith(FindAllTheFiles.SAMPLE_TAG_PREFIX)) {
        sampleTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
      } else if (key.startsWith(FindAllTheFiles.LANE_TAG_PREFIX)) {
        laneTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
      } else if (key.startsWith(FindAllTheFiles.STUDY_TAG_PREFIX)) {
        studyTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
      } else if (key.startsWith(FindAllTheFiles.EXPERIMENT_TAG_PREFIX)) {
        experimentTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
      } else if (key.startsWith(FindAllTheFiles.IUS_TAG_PREFIX)) {
        iusTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
      } else if (key.startsWith(FindAllTheFiles.SEQUENCER_RUN_TAG_PREFIX)) {
        seqencerrunTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
      } else if (key.startsWith(FindAllTheFiles.PROCESSING_TAG_PREFIX)) {
        processingTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
      } else if (key.startsWith(FindAllTheFiles.FILE_TAG_PREFIX)){
        fileTag.append(key).append("=").append(ret.getAttribute(key)).append(";");
      }
    }
    StringBuilder sb = new StringBuilder();
    sb.append(ret.getAttribute(PROCESSING_DATE)).append("\t");
    sb.append(studyName).append("\t");
    sb.append(ret.getAttribute(STUDY_SWA)).append("\t");
    sb.append(studyTag.toString()).append("\t");
    sb.append(ret.getAttribute(EXPERIMENT_NAME)).append("\t");
    sb.append(ret.getAttribute(EXPERIMENT_SWA)).append("\t");
    sb.append(experimentTag.toString()).append("\t");
    sb.append(ret.getAttribute(PARENT_SAMPLE_NAME)).append("\t");
    sb.append(ret.getAttribute(PARENT_SAMPLE_SWA)).append("\t");
    sb.append(parentSampleTag.toString()).append("\t");
    sb.append(ret.getAttribute(SAMPLE_NAME)).append("\t");
    sb.append(ret.getAttribute(SAMPLE_SWA)).append("\t");
    sb.append(sampleTag.toString()).append("\t");
    sb.append(ret.getAttribute(SEQUENCER_RUN_NAME)).append("\t");
    sb.append(ret.getAttribute(SEQUENCER_RUN_SWA)).append("\t");
    sb.append(seqencerrunTag.toString()).append("\t");
    sb.append(ret.getAttribute(LANE_NAME)).append("\t");
    sb.append(ret.getAttribute(LANE_NUM)).append("\t");
    sb.append(ret.getAttribute(LANE_SWA)).append("\t");
    sb.append(laneTag.toString()).append("\t");
    sb.append(ret.getAttribute(IUS_TAG)).append("\t");
    sb.append(ret.getAttribute(IUS_SWA)).append("\t");
    sb.append(iusTag.toString()).append("\t");
    sb.append(ret.getAttribute(WORKFLOW_NAME)).append("\t");
    sb.append(ret.getAttribute(WORKFLOW_VERSION)).append("\t");
    sb.append(ret.getAttribute(WORKFLOW_SWA)).append("\t");
    sb.append(ret.getAttribute(WORKFLOW_RUN_NAME)).append("\t");
    if (showStatus) {
      sb.append(ret.getAttribute(WORKFLOW_RUN_STATUS)).append("\t");
    }
    sb.append(ret.getAttribute(WORKFLOW_RUN_SWA)).append("\t");
    sb.append(ret.getAttribute(PROCESSING_ALGO)).append("\t");
    sb.append(ret.getAttribute(PROCESSING_SWID)).append("\t");
    sb.append(processingTag.toString()).append("\t");
    sb.append(fm.getMetaType()).append("\t");
    sb.append(ret.getAttribute(FILE_SWA)).append("\t");
    sb.append(fm.getFilePath()).append("\t");
    sb.append(fileTag.toString());
    if (reportInputFiles){
        sb.append("\t");
        sb.append(ret.getAttribute(INPUT_FILE_META_TYPES)).append("\t");
        sb.append(ret.getAttribute(INPUT_FILE_SWIDS)).append("\t");
        sb.append(ret.getAttribute(INPUT_FILE_FILE_PATHS)).append("\t");
    } 
    sb.append("\n");
    
    writer.write(sb.toString());

  }
  
  public static void printHeader(Writer writer, boolean showStatus) throws IOException {
     printHeader(writer, showStatus, false); 
  }

  /**
   * Print the header of the Excel spreadsheet to file.
   *
   * @throws java.io.IOException if any.
   * @param writer a {@link java.io.Writer} object.
   * @param showStatus a boolean.
   */
  public static void printHeader(Writer writer, boolean showStatus, boolean reportInputFiles) throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append(PROCESSING_DATE).append("\t");
    sb.append(STUDY_TITLE).append("\t");
    sb.append(STUDY_SWA).append("\t");
    sb.append(STUDY_ATTRIBUTES).append("\t");
    sb.append(EXPERIMENT_NAME).append("\t");
    sb.append(EXPERIMENT_SWA).append("\t");
    sb.append(EXPERIMENT_ATTRIBUTES).append("\t");
    sb.append(PARENT_SAMPLE_NAME).append("\t");
    sb.append(PARENT_SAMPLE_SWA).append("\t");
    sb.append(PARENT_SAMPLE_ATTRIBUTES).append("\t");
    sb.append(SAMPLE_NAME).append("\t");
    sb.append(SAMPLE_SWA).append("\t");
    sb.append(SAMPLE_ATTRIBUTES).append("\t");
    sb.append(SEQUENCER_RUN_NAME).append("\t");
    sb.append(SEQUENCER_RUN_SWA).append("\t");
    sb.append(SEQUENCER_RUN_ATTRIBUTES).append("\t");
    sb.append(LANE_NAME).append("\t");
    sb.append(LANE_NUM).append("\t");
    sb.append(LANE_SWA).append("\t");
    sb.append(LANE_ATTRIBUTES).append("\t");
    sb.append(IUS_TAG).append("\t");
    sb.append(IUS_SWA).append("\t");
    sb.append(IUS_ATTRIBUTES).append("\t");
    sb.append(WORKFLOW_NAME).append("\t");
    sb.append(WORKFLOW_VERSION).append("\t");
    sb.append(WORKFLOW_SWA).append("\t");
    sb.append(WORKFLOW_RUN_NAME).append("\t");
    if (showStatus) {
      sb.append(WORKFLOW_RUN_STATUS).append("\t");
    }
    sb.append(WORKFLOW_RUN_SWA).append("\t");
    sb.append(PROCESSING_ALGO).append("\t");
    sb.append(PROCESSING_SWID).append("\t");
    sb.append(PROCESSING_ATTRIBUTES).append("\t");
    sb.append("File Meta-Type").append("\t");
    sb.append(FILE_SWA).append("\t");
    sb.append("File Path").append("\t");
    sb.append(FILE_ATTRIBUTES);
    if (reportInputFiles){
        sb.append("\t");
        sb.append(INPUT_FILE_META_TYPES).append("\t");
        sb.append(INPUT_FILE_SWIDS).append("\t");
        sb.append(INPUT_FILE_FILE_PATHS).append("\t");
    } 
    sb.append("\n");
    

    writer.write(sb.toString());
  }
}
