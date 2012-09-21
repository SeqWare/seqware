/*
 * Copyright (C) 2011 SeqWare
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
package net.sourceforge.seqware.pipeline.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.common.model.SequencerRunAttribute;
import net.sourceforge.seqware.common.model.StudyAttribute;
import net.sourceforge.seqware.common.model.WorkflowAttribute;
import net.sourceforge.seqware.common.model.WorkflowRunAttribute;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.pipeline.plugin.Plugin;
import net.sourceforge.seqware.pipeline.plugin.PluginInterface;

import org.openide.util.lookup.ServiceProvider;

/**
 * 
 * @author mtaschuk
 */
@ServiceProvider(service = PluginInterface.class)
public class AttributeAnnotator extends Plugin {

  ReturnValue ret = new ReturnValue();

  public AttributeAnnotator() {
    super();
    parser.acceptsAll(Arrays.asList("sequencer-run-accession", "sr"),
        "The SWID of the sequencer run to annotate. One of the -accession options is required.").withRequiredArg();
    parser.acceptsAll(Arrays.asList("lane-accession", "l"),
        "The SWID of the Lane to annotate. One of the -accession options is required.").withRequiredArg();
    parser.acceptsAll(Arrays.asList("ius-accession", "i"),
        "The SWID of the IUS to annotate. One of the -accession options is required.").withRequiredArg();

    parser.acceptsAll(Arrays.asList("experiment-accession", "e"),
        "The SWID of the Experiment to annotate. One of the -accession options is required.").withRequiredArg();
    parser.acceptsAll(Arrays.asList("processing-accession", "p"),
        "The SWID of the Processing to annotate. One of the -accession options is required.").withRequiredArg();
    parser.acceptsAll(Arrays.asList("sample-accession", "s"),
        "The SWID of the Sample to annotate. One of the -accession options is required.").withRequiredArg();
    parser.acceptsAll(Arrays.asList("study-accession", "st"),
        "The SWID of the Study to annotate. One of the -accession options is required.").withRequiredArg();
    parser.acceptsAll(Arrays.asList("workflow-accession", "w"),
        "The SWID of the workflow to annotate. One of the -accession options is required.").withRequiredArg();
    parser.acceptsAll(Arrays.asList("workflow-run-accession", "wr"),
        "The SWID of the workflow run to annotate. One of the -accession options is required.").withRequiredArg();
    // parser.acceptsAll(Arrays.asList("file-accession", "f"),
    // "The SWID of the file run to annotate. One of the -accession options is required.").withRequiredArg();

    parser.accepts("file", "The CSV file for bulk insert").withRequiredArg();
    parser.acceptsAll(Arrays.asList("skip"),
        "Optional: Sets the 'skip' flag to either true or false for sequencer-run, lane, ius, or sample only.")
        .withRequiredArg();
    parser.acceptsAll(Arrays.asList("key"),
        "Optional: The field that defines this attribute. The default value is 'skip'.").withRequiredArg();
    parser.acceptsAll(Arrays.asList("value"),
        "Optional: The description of this field. If not specified, no attribute will be created.").withRequiredArg();

    ret.setExitStatus(ReturnValue.SUCCESS);
  }

  @Override
  public ReturnValue init() {
    return ret;
  }

  @Override
  public ReturnValue do_test() {
    return ret;
  }

  @Override
  public ReturnValue do_run() {

    // Key/Value pair
    String key = "skip", value = "";
    boolean hasKey = options.has("key");
    boolean hasValue = options.has("value");
    if (hasKey) {
      key = (String) options.valueOf("key");
    }
    if (hasValue) {
      value = (String) options.valueOf("value");
    }

    if (options.has("file")) {
      this.bulkInsert();

      return ret;
    }

    // skip
    Boolean skip = null;
    boolean hasSkip = options.has("skip");
    if (hasSkip) {
      skip = Boolean.parseBoolean((String) options.valueOf("skip"));
    }

    boolean hasSequencerRun = options.has("sequencer-run-accession");
    boolean hasLane = options.has("lane-accession");
    boolean hasIus = options.has("ius-accession");
    boolean hasExperimentAccession = options.has("experiment-accession");
    boolean hasProcessingAccession = options.has("processing-accession");
    boolean hasSampleAccession = options.has("sample-accession");
    boolean hasStudyAccession = options.has("study-accession");
    boolean hasWorkflowAccession = options.has("workflow-accession");
    boolean hasWorkflowRunAccession = options.has("workflow-run-accession");
    boolean hasFileAccession = options.has("file-accession");

    if (hasSequencerRun) {
      Integer sequencerRunSWID = Integer.parseInt((String) options.valueOf("sequencer-run-accession"));
      SequencerRunAttribute sra = null;
      if (hasValue) {
        sra = new SequencerRunAttribute();
        sra.setTag(key);
        sra.setValue(value);
        // sra.setSequencerRunAttributeId(new
        // Random(System.currentTimeMillis()).nextInt(Integer.MAX_VALUE));
      }
      metadata.annotateSequencerRun(sequencerRunSWID, sra, skip);
    } else if (hasLane) {
      Integer laneSWID = Integer.parseInt((String) options.valueOf("lane-accession"));
      LaneAttribute la = null;
      if (hasValue) {
        la = new LaneAttribute();
        la.setTag(key);
        la.setValue(value);
        // la.setLaneAttributeId(new
        // Random(System.currentTimeMillis()).nextInt(Integer.MAX_VALUE));
      }
      metadata.annotateLane(laneSWID, la, skip);

    } else if (hasIus) {
      Integer iusSWID = Integer.parseInt((String) options.valueOf("ius-accession"));
      IUSAttribute ia = null;
      if (hasValue) {
        ia = new IUSAttribute();
        ia.setTag(key);
        ia.setValue(value);
        // ia.setIusAttributeId(new
        // Random(System.currentTimeMillis()).nextInt(Integer.MAX_VALUE));
      }
      metadata.annotateIUS(iusSWID, ia, skip);
    } else if (hasExperimentAccession) {
      Integer swid = Integer.parseInt((String) options.valueOf("experiment-accession"));
      ExperimentAttribute a = null;
      if (hasValue) {
        a = new ExperimentAttribute();
        a.setTag(key);
        a.setValue(value);
        // a.setExperimentAttributeId(new
        // Random(System.currentTimeMillis()).nextInt(Integer.MAX_VALUE));
      }
      metadata.annotateExperiment(swid, a, skip);
    } else if (hasProcessingAccession) {
      Integer swid = Integer.parseInt((String) options.valueOf("processing-accession"));
      ProcessingAttribute a = null;
      if (hasValue) {
        a = new ProcessingAttribute();
        a.setTag(key);
        a.setValue(value);
        // a.setProcessingAttributeId(new
        // Random(System.currentTimeMillis()).nextInt(Integer.MAX_VALUE));
      }
      metadata.annotateProcessing(swid, a, skip);
    } else if (hasSampleAccession) {
      Integer swid = Integer.parseInt((String) options.valueOf("sample-accession"));
      SampleAttribute a = null;
      if (hasValue) {
        a = new SampleAttribute();
        a.setTag(key);
        a.setValue(value);
        // a.setSampleAttributeId(new
        // Random(System.currentTimeMillis()).nextInt(Integer.MAX_VALUE));
      }
      metadata.annotateSample(swid, a, skip);
    } else if (hasStudyAccession) {
      Integer swid = Integer.parseInt((String) options.valueOf("study-accession"));
      StudyAttribute a = null;
      if (hasValue) {
        a = new StudyAttribute();
        a.setTag(key);
        a.setValue(value);
        // a.setStudyAttributeId(new
        // Random(System.currentTimeMillis()).nextInt(Integer.MAX_VALUE));
      }
      metadata.annotateStudy(swid, a, skip);
    } else if (hasWorkflowAccession) {
      Integer swid = Integer.parseInt((String) options.valueOf("workflow-accession"));
      WorkflowAttribute a = null;
      if (hasValue) {
        a = new WorkflowAttribute();
        a.setTag(key);
        a.setValue(value);
      }
      metadata.annotateWorkflow(swid, a, skip);
    } else if (hasWorkflowRunAccession) {
      Integer swid = Integer.parseInt((String) options.valueOf("workflow-run-accession"));
      WorkflowRunAttribute a = null;
      if (hasValue) {
        a = new WorkflowRunAttribute();
        a.setTag(key);
        a.setValue(value);
      }
      metadata.annotateWorkflowRun(swid, a, skip);
    } /*
       * else if(hasFileAccession) { Integer swid = Integer.parseInt((String)
       * options.valueOf("file-accession")); FileAttribute a = null; if
       * (hasValue) { a = new FileAttribute(); a.setTag(key); a.setValue(value);
       * } metadata.annotateFile(swid, a, skip); }
       */
    else {
      println("Combination of parameters not recognized!");
      println(this.get_syntax());
      ret.setExitStatus(ReturnValue.INVALIDPARAMETERS);
    }

    return ret;
  }

  @Override
  public ReturnValue clean_up() {
    return ret;
  }

  @Override
  public String get_description() {
    return "Experimental plugin. Allows the annotation of objects in the database with 'skip' values.";
  }

  private boolean parseFile(Map<String, Map<String, Map<String, String>>> bulkMap) {
    String filepath = (String) options.valueOf("file");
    File file = new File(filepath);
    try {
      BufferedReader freader = new BufferedReader(new FileReader(file));
      String line = null;
      while ((line = freader.readLine()) != null) {
        String[] args = line.split(",");
        if (!checkArgs(args))
          return false;
        Map<String, Map<String, String>> types = bulkMap.get(args[0]);
        if (types == null) {
          types = new HashMap<String, Map<String, String>>();
          bulkMap.put(args[0], types);
        }
        Map<String, String> ids = types.get(args[1]);
        if (ids == null) {
          ids = new LinkedHashMap<String, String>();
          types.put(args[1], ids);
        }
        ids.put(args[2], args[3]);
      }
      freader.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      return false;
    } catch (IOException ex) {
      ex.printStackTrace();
      return false;
    }
    return true;
  }

  private boolean checkArgs(String[] args) {
    if (args.length != 4)
      return false;
    try {
      Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }

  private void bulkInsert() {
    Map<String, Map<String, Map<String, String>>> bulkMap = new HashMap<String, Map<String, Map<String, String>>>();
    if (this.parseFile(bulkMap)) {
      for (Map.Entry<String, Map<String, Map<String, String>>> entry : bulkMap.entrySet()) {
        if (entry.getKey().equals("w") || entry.getKey().equals("workflow-accesion")) {
          this.bulkInsertWorkflow(entry.getValue());
        }
        if (entry.getKey().equals("wr") || entry.getKey().equals("workflow-run-accession")) {
          this.bulkInsertWorkflowRun(entry.getValue());
        }
        if (entry.getKey().equals("sr") || entry.getKey().equals("sequencer-run-accession")) {
          this.bulkInsertSequencerRun(entry.getValue());
        }
        if (entry.getKey().equals("l") || entry.getKey().equals("lane-accession")) {
          this.bulkInsertLane(entry.getValue());
        }
        if (entry.getKey().equals("i") || entry.getKey().equals("ius-accession")) {
          this.bulkInsertIUS(entry.getValue());
        }
        if (entry.getKey().equals("e") || entry.getKey().equals("experiment-accession")) {
          this.bulkInsertExperiment(entry.getValue());
        }
        if (entry.getKey().equals("st") || entry.getKey().equals("study-accession")) {
          this.bulkInsertStudy(entry.getValue());
        }
        if (entry.getKey().equals("p") || entry.getKey().equals("processing-accession")) {
          this.bulkInsertProcessing(entry.getValue());
        }
        if (entry.getKey().equals("s") || entry.getKey().equals("sample-accession")) {
          this.bulkInsertSample(entry.getValue());
        }
      }
    }
  }

  private void bulkInsertSample(Map<String, Map<String, String>> wmap) {
    for (Map.Entry<String, Map<String, String>> entry : wmap.entrySet()) {
      Integer swid = Integer.parseInt((String) entry.getKey());
      Map<String, String> keyvalueMap = entry.getValue();
      Set<SampleAttribute> atts = new TreeSet<SampleAttribute>();
      for (Map.Entry<String, String> entry2 : keyvalueMap.entrySet()) {
        SampleAttribute a = new SampleAttribute();
        a.setTag(entry2.getKey());
        a.setValue(entry2.getValue());
        atts.add(a);
      }
      metadata.annotateSample(swid, atts);
    }
  }

  private void bulkInsertProcessing(Map<String, Map<String, String>> wmap) {
    for (Map.Entry<String, Map<String, String>> entry : wmap.entrySet()) {
      Integer swid = Integer.parseInt((String) entry.getKey());
      Map<String, String> keyvalueMap = entry.getValue();
      Set<ProcessingAttribute> atts = new TreeSet<ProcessingAttribute>();
      for (Map.Entry<String, String> entry2 : keyvalueMap.entrySet()) {
        ProcessingAttribute a = new ProcessingAttribute();
        a.setTag(entry2.getKey());
        a.setValue(entry2.getValue());
        atts.add(a);
      }
      metadata.annotateProcessing(swid, atts);
    }
  }

  private void bulkInsertStudy(Map<String, Map<String, String>> wmap) {
    for (Map.Entry<String, Map<String, String>> entry : wmap.entrySet()) {
      Integer swid = Integer.parseInt((String) entry.getKey());
      Map<String, String> keyvalueMap = entry.getValue();
      Set<StudyAttribute> atts = new TreeSet<StudyAttribute>();
      for (Map.Entry<String, String> entry2 : keyvalueMap.entrySet()) {
        StudyAttribute a = new StudyAttribute();
        a.setTag(entry2.getKey());
        a.setValue(entry2.getValue());
        atts.add(a);
      }
      metadata.annotateStudy(swid, atts);
    }
  }

  private void bulkInsertIUS(Map<String, Map<String, String>> wmap) {
    for (Map.Entry<String, Map<String, String>> entry : wmap.entrySet()) {
      Integer swid = Integer.parseInt((String) entry.getKey());
      Map<String, String> keyvalueMap = entry.getValue();
      Set<IUSAttribute> atts = new TreeSet<IUSAttribute>();
      for (Map.Entry<String, String> entry2 : keyvalueMap.entrySet()) {
        IUSAttribute a = new IUSAttribute();
        a.setTag(entry2.getKey());
        a.setValue(entry2.getValue());
        atts.add(a);
      }
      metadata.annotateIUS(swid, atts);
    }
  }

  private void bulkInsertExperiment(Map<String, Map<String, String>> wmap) {
    for (Map.Entry<String, Map<String, String>> entry : wmap.entrySet()) {
      Integer swid = Integer.parseInt((String) entry.getKey());
      Map<String, String> keyvalueMap = entry.getValue();
      Set<ExperimentAttribute> atts = new TreeSet<ExperimentAttribute>();
      for (Map.Entry<String, String> entry2 : keyvalueMap.entrySet()) {
        ExperimentAttribute a = new ExperimentAttribute();
        a.setTag(entry2.getKey());
        a.setValue(entry2.getValue());
        atts.add(a);
      }
      metadata.annotateExperiment(swid, atts);
    }
  }

  private void bulkInsertLane(Map<String, Map<String, String>> wmap) {
    for (Map.Entry<String, Map<String, String>> entry : wmap.entrySet()) {
      Integer swid = Integer.parseInt((String) entry.getKey());
      Map<String, String> keyvalueMap = entry.getValue();
      Set<LaneAttribute> atts = new TreeSet<LaneAttribute>();
      for (Map.Entry<String, String> entry2 : keyvalueMap.entrySet()) {
        LaneAttribute a = new LaneAttribute();
        a.setTag(entry2.getKey());
        a.setValue(entry2.getValue());
        atts.add(a);
      }
      metadata.annotateLane(swid, atts);
    }
  }

  private void bulkInsertSequencerRun(Map<String, Map<String, String>> wmap) {
    for (Map.Entry<String, Map<String, String>> entry : wmap.entrySet()) {
      Integer swid = Integer.parseInt((String) entry.getKey());
      Map<String, String> keyvalueMap = entry.getValue();
      Set<SequencerRunAttribute> atts = new TreeSet<SequencerRunAttribute>();
      for (Map.Entry<String, String> entry2 : keyvalueMap.entrySet()) {
        SequencerRunAttribute a = new SequencerRunAttribute();
        a.setTag(entry2.getKey());
        a.setValue(entry2.getValue());
        atts.add(a);
      }
      metadata.annotateSequencerRun(swid, atts);
    }
  }

  private void bulkInsertWorkflow(Map<String, Map<String, String>> wmap) {
    for (Map.Entry<String, Map<String, String>> entry : wmap.entrySet()) {
      Integer swid = Integer.parseInt((String) entry.getKey());
      Map<String, String> keyvalueMap = entry.getValue();
      Set<WorkflowAttribute> atts = new TreeSet<WorkflowAttribute>();
      for (Map.Entry<String, String> entry2 : keyvalueMap.entrySet()) {
        WorkflowAttribute a = new WorkflowAttribute();
        a.setTag(entry2.getKey());
        a.setValue(entry2.getValue());
        atts.add(a);
      }
      metadata.annotateWorkflow(swid, atts);
    }
  }

  private void bulkInsertWorkflowRun(Map<String, Map<String, String>> wmap) {
    for (Map.Entry<String, Map<String, String>> entry : wmap.entrySet()) {
      Integer swid = Integer.parseInt((String) entry.getKey());
      Map<String, String> keyvalueMap = entry.getValue();
      Set<WorkflowRunAttribute> atts = new TreeSet<WorkflowRunAttribute>();
      for (Map.Entry<String, String> entry2 : keyvalueMap.entrySet()) {
        WorkflowRunAttribute a = new WorkflowRunAttribute();
        a.setTag(entry2.getKey());
        a.setValue(entry2.getValue());
        atts.add(a);
      }
      metadata.annotateWorkflowRun(swid, atts);
    }
  }
}
