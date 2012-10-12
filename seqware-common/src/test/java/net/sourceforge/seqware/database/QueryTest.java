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
package net.sourceforge.seqware.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import net.sourceforge.seqware.common.business.StudyService;
import net.sourceforge.seqware.common.factory.BeanFactory;
import net.sourceforge.seqware.common.hibernate.InSessionExecutions;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.File;
import net.sourceforge.seqware.common.model.IUS;
import net.sourceforge.seqware.common.model.Lane;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.Processing;
import net.sourceforge.seqware.common.model.Sample;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.common.model.SequencerRun;
import net.sourceforge.seqware.common.model.Study;
import net.sourceforge.seqware.common.model.Workflow;
import net.sourceforge.seqware.common.model.WorkflowRun;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;

import org.junit.Test;

/**
 * <p>QueryTest class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 * @since 0.13.3
 */
public class QueryTest {

  private Collection<ReturnValue> returnValues = new ArrayList<ReturnValue>();

  /**
   * <p>Constructor for QueryTest.</p>
   */
  public QueryTest() {
  }

  /**
   * <p>hello.</p>
   */
  @Test
  public void hello() {
    StudyService ss = BeanFactory.getStudyServiceBean();
    // Writer writer = null;
    try {
      InSessionExecutions.bindSessionToThread();
      Study study = ss.findBySWAccession(6144);
      for (Experiment e : study.getExperiments()) {
        for (Sample parentSample : e.getSamples()) {
          Stack<Sample> sampleStack = new Stack<Sample>();
          sampleStack.addAll(parentSample.getChildren());
          while (!sampleStack.isEmpty()) {
            Sample sample = sampleStack.pop();
            sampleStack.addAll(sample.getChildren());
            filesFromSample(parentSample, sample, e, study);
          }
        }
      }
    } finally {
      InSessionExecutions.unBindSessionFromTheThread();
    }
  }

  private void filesFromSample(Sample parentSample, Sample sample, Experiment e, Study study) {
    for (IUS ius : sample.getIUS()) {
      filesFromIUS(ius, e, parentSample, sample, study);
    }
  }

  private void filesFromIUS(IUS ius, Experiment e, Sample parentSample, Sample sample, Study study) {
    Lane lane = ius.getLane();
    SequencerRun sequencerRun = lane.getSequencerRun();
    Stack<Processing> processingStack = new Stack<Processing>();
    processingStack.addAll(ius.getProcessings());
    while (!processingStack.isEmpty()) {
      Processing processing = processingStack.pop();
      processingStack.addAll(processing.getChildren());
      filesFromProcessing(processing, e, parentSample, sample, study, ius, lane, sequencerRun);
    }
  }

  private void filesFromProcessing(Processing processing, Experiment e, Sample parentSample, Sample sample,
      Study study, IUS ius, Lane lane, SequencerRun sequencerRun) {

    WorkflowRun workflowRun = processing.getWorkflowRun();
    Workflow workflow = null;
    if (workflowRun == null) {
      workflowRun = processing.getWorkflowRunByAncestorWorkflowRunId();
    }
    if (workflowRun != null) {
      workflow = workflowRun.getWorkflow();
    }
    for (File file : processing.getFiles()) {
      printFileInfo(e, parentSample, sample, study, ius, lane, sequencerRun, processing, workflowRun, workflow, file);
    }
  }

  private void printFileInfo(Experiment e, Sample parentSample, Sample sample, Study study, IUS ius, Lane lane,
      SequencerRun sequencerRun, Processing processing, WorkflowRun workflowRun, Workflow workflow, File file) {

    ReturnValue ret = new ReturnValue();

    // Portal uses titles by default and names may be blank. Pipeline always
    // uses names. So check both. (Title should always be populated, but we
    // prefer name).
    String eName = e.getName();
    if (eName == null || eName.isEmpty()) {
      eName = e.getTitle();
    }
    String sName = sample.getName();
    if (sName == null || sName.isEmpty()) {
      sName = sample.getTitle();
    }
    String psName = parentSample.getName();
    if (psName == null || psName.isEmpty()) {
      psName = parentSample.getTitle();
    }
    // SET ALL ATTRIBUTES

    // processing
    ret.setAlgorithm(processing.getAlgorithm());

    // Study
    ret.setAttribute("study_title", study.getTitle());
    ret.setAttribute("study_swaccession", study.getSwAccession().toString());

    // Experiment
    if (e != null) {
      ret.setAttribute("experiment_name", eName);
      ret.setAttribute("experiment_swaccession", e.getSwAccession().toString());
    }

    // Parent Sample
    if (parentSample != null) {
      ret.setAttribute("parent_sample_name", psName);
      ret.setAttribute("parent_sample_swaccession", parentSample.getSwAccession().toString());
      for (SampleAttribute satt : parentSample.getSampleAttributes()) {
        String key = "parent_sample." + satt.getTag();
        if (ret.getAttribute(key) != null) {
          ret.setAttribute(key, ret.getAttribute(key) + ";" + satt.getValue());
        } else {
          ret.setAttribute(key, satt.getValue());
        }
      }
    }

    // Sample
    if (sample != null) {
      ret.setAttribute("sample_name", sName);
      ret.setAttribute("sample_swaccession", sample.getSwAccession().toString());
      for (SampleAttribute satt : sample.getSampleAttributes()) {
        String key = "sample." + satt.getTag();
        if (ret.getAttribute(key) != null) {
          ret.setAttribute(key, ret.getAttribute(key) + ";" + satt.getValue());
        } else {
          ret.setAttribute(key, satt.getValue());
        }
      }
    }
    // IUS
    if (ius != null) {
      ret.setAttribute("ius_swaccession", ius.getSwAccession().toString());
    }

    // Lane
    if (lane != null) {
      ret.setAttribute("lane_name", lane.getName());
      ret.setAttribute("lane_swaccession", lane.getSwAccession().toString());
      for (LaneAttribute latt : lane.getLaneAttributes()) {
        String key = "lane." + latt.getTag();
        if (ret.getAttribute(key) != null) {
          ret.setAttribute(key, ret.getAttribute(key) + ";" + latt.getValue());
        } else {
          ret.setAttribute(key, latt.getValue());
        }
      }
    }
    // Sequencer Run
    if (sequencerRun != null) {
      ret.setAttribute("sequencer_run_name", sequencerRun.getName());
      ret.setAttribute("sequencer_run_swaccession", sequencerRun.getSwAccession().toString());
    }

    // WorkflowRun
    if (workflowRun != null) {
      ret.setAttribute("workflow_run_name", workflowRun.getName());
      ret.setAttribute("workflow_run_swaccession", workflowRun.getSwAccession().toString());
    }

    // Workflow
    if (workflow != null) {
      ret.setAttribute("workflow_name", workflow.getName());
      ret.setAttribute("workflow_swaccession", workflow.getSwAccession().toString());
    }

    // File
    FileMetadata fm = new FileMetadata();
    fm.setFilePath(file.getFilePath());
    fm.setMetaType(file.getMetaType());
    fm.setDescription(file.getSwAccession().toString());
    ArrayList<FileMetadata> files = new ArrayList<FileMetadata>();
    files.add(fm);

    ret.setFiles(files);

    returnValues.add(ret);
  }
}
