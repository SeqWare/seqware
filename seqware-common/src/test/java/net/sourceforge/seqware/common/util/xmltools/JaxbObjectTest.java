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
package net.sourceforge.seqware.common.util.xmltools;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Assert;
import net.sourceforge.seqware.common.model.Experiment;
import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.FileType;
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
import net.sourceforge.seqware.common.model.lists.ReturnValueList;
import net.sourceforge.seqware.common.model.lists.WorkflowList;
import net.sourceforge.seqware.common.model.lists.WorkflowRunList2;
import net.sourceforge.seqware.common.module.FileMetadata;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.Log;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <p>JaxbObjectTest class.</p>
 *
 * @author mtaschuk
 * @version $Id: $Id
 * @since 0.13.3
 */
public class JaxbObjectTest {

  /**
   * <p>Constructor for JaxbObjectTest.</p>
   */
  public JaxbObjectTest() {
  }

  /**
   * <p>setUpClass.</p>
   *
   * @throws java.lang.Exception if any.
   */
  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  /**
   * <p>tearDownClass.</p>
   *
   * @throws java.lang.Exception if any.
   */
  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  /**
   * <p>setUp.</p>
   */
  @Before
  public void setUp() {
  }

  /**
   * <p>tearDown.</p>
   */
  @After
  public void tearDown() {
  }

  /**
   * Test of marshal method, of class JaxbObject.
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testMarshal() throws Exception {
    Log.info("marshal");
    FileType type = new FileType();
    type.setDisplayName("Display Name");
    type.setExtension("extension");
    type.setFileTypeId(-1);
    type.setMetaType("meta-type");

    JaxbObject<FileType> instance = new JaxbObject<FileType>();
    String expResult = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><FileType><displayName>Display Name</displayName><extension>extension</extension><fileTypeId>-1</fileTypeId><metaType>meta-type</metaType></FileType>";
    String result = instance.marshal(type);
    assertEquals(expResult, result);
  }

  /**
   * Test of unMarshal method, of class JaxbObject.
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testUnMarshal() throws Exception {
    Log.info("unMarshal");
    FileType type = new FileType();
    type.setDisplayName("Display Name");
    type.setExtension("extension");
    type.setFileTypeId(-1);
    type.setMetaType("meta-type");

    JaxbObject<FileType> instance = new JaxbObject<FileType>();
    String result = instance.marshal(type);

    Reader in = new StringReader(result);

    FileType type2 = instance.unMarshal(type, in);
    assertEquals(type, type2);
  }

  /**
   * <p>testSampleJaxb.</p>
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testSampleJaxb() throws Exception {
    Sample sample = new Sample();
    sample.setSampleId(Integer.MIN_VALUE);

    SampleAttribute sa1 = new SampleAttribute();
    sa1.setTag("t1");
    sa1.setValue("v1");
    sample.getSampleAttributes().add(sa1);

    SampleAttribute sa2 = new SampleAttribute();
    sa2.setTag("t2");
    sa2.setValue("t2");
    sample.getSampleAttributes().add(sa2);

    JaxbObject<Sample> instance = new JaxbObject<Sample>();
    String result = instance.marshal(sample);
    System.out.println(result);
    Reader in = new StringReader(result);
    Sample sample2 = instance.unMarshal(sample, in);
    assertEquals(sample, sample2);
  }

  /**
   * <p>testStudyJaxb.</p>
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testStudyJaxb() throws Exception {
    Study study = new Study();
    study.setStudyId(Integer.MIN_VALUE);

    StudyAttribute sa1 = new StudyAttribute();
    sa1.setTag("t1");
    sa1.setValue("v1");
    study.getStudyAttributes().add(sa1);

    StudyAttribute sa2 = new StudyAttribute();
    sa2.setTag("t2");
    sa2.setValue("t2");
    study.getStudyAttributes().add(sa2);

    JaxbObject<Study> instance = new JaxbObject<Study>();
    String result = instance.marshal(study);
    System.out.println(result);
    Reader in = new StringReader(result);
    Study st = instance.unMarshal(study, in);
    assertEquals(study, st);
  }

  /**
   * <p>testIUSJaxb.</p>
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testIUSJaxb() throws Exception {
    IUS ius = new IUS();
    ius.setIusId(Integer.MIN_VALUE);

    IUSAttribute sa1 = new IUSAttribute();
    sa1.setTag("t1");
    sa1.setValue("v1");
    ius.getIusAttributes().add(sa1);

    IUSAttribute sa2 = new IUSAttribute();
    sa2.setTag("t2");
    sa2.setValue("t2");
    ius.getIusAttributes().add(sa2);

    JaxbObject<IUS> instance = new JaxbObject<IUS>();
    String result = instance.marshal(ius);
    System.out.println(result);
    Reader in = new StringReader(result);
    IUS st = instance.unMarshal(ius, in);
    assertEquals(ius, st);
  }

  /**
   * <p>testLaneJaxb.</p>
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testLaneJaxb() throws Exception {
    Lane lane = new Lane();
    lane.setLaneId(Integer.MIN_VALUE);

    LaneAttribute sa1 = new LaneAttribute();
    sa1.setTag("t1");
    sa1.setValue("v1");
    lane.getLaneAttributes().add(sa1);

    LaneAttribute sa2 = new LaneAttribute();
    sa2.setTag("t2");
    sa2.setValue("t2");
    lane.getLaneAttributes().add(sa2);

    JaxbObject<Lane> instance = new JaxbObject<Lane>();
    String result = instance.marshal(lane);
    System.out.println(result);
    Reader in = new StringReader(result);
    Lane st = instance.unMarshal(lane, in);
    assertEquals(lane, st);
  }

  /**
   * <p>testExperimentJaxb.</p>
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testExperimentJaxb() throws Exception {
    Experiment ius = new Experiment();
    ius.setExperimentId(Integer.MIN_VALUE);

    ExperimentAttribute sa1 = new ExperimentAttribute();
    sa1.setTag("t1");
    sa1.setValue("v1");
    ius.getExperimentAttributes().add(sa1);

    ExperimentAttribute sa2 = new ExperimentAttribute();
    sa2.setTag("t2");
    sa2.setValue("t2");
    ius.getExperimentAttributes().add(sa2);

    JaxbObject<Experiment> instance = new JaxbObject<Experiment>();
    String result = instance.marshal(ius);
    System.out.println(result);
    Reader in = new StringReader(result);
    Experiment st = instance.unMarshal(ius, in);
    assertEquals(ius, st);
  }

  /**
   * <p>testSequencerRunJaxb.</p>
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testSequencerRunJaxb() throws Exception {
    SequencerRun ius = new SequencerRun();
    ius.setSequencerRunId(Integer.MIN_VALUE);

    SequencerRunAttribute sa1 = new SequencerRunAttribute();
    sa1.setTag("t1");
    sa1.setValue("v1");
    ius.getSequencerRunAttributes().add(sa1);

    SequencerRunAttribute sa2 = new SequencerRunAttribute();
    sa2.setTag("t2");
    sa2.setValue("t2");
    ius.getSequencerRunAttributes().add(sa2);

    JaxbObject<SequencerRun> instance = new JaxbObject<SequencerRun>();
    String result = instance.marshal(ius);
    System.out.println(result);
    Reader in = new StringReader(result);
    SequencerRun st = instance.unMarshal(ius, in);
    assertEquals(ius, st);
  }

  /**
   * <p>testProcessingJaxb.</p>
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testProcessingJaxb() throws Exception {
    Processing ius = new Processing();
    ius.setProcessingId(Integer.MIN_VALUE);

    ProcessingAttribute sa1 = new ProcessingAttribute();
    sa1.setTag("t1");
    sa1.setValue("v1");
    ius.getProcessingAttributes().add(sa1);

    ProcessingAttribute sa2 = new ProcessingAttribute();
    sa2.setTag("t2");
    sa2.setValue("t2");
    ius.getProcessingAttributes().add(sa2);

    JaxbObject<Processing> instance = new JaxbObject<Processing>();
    String result = instance.marshal(ius);
    System.out.println(result);
    Reader in = new StringReader(result);
    Processing st = instance.unMarshal(ius, in);
    assertEquals(ius, st);
  }

  /**
   * <p>testNullProcessingAttribute.</p>
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testNullProcessingAttribute() throws Exception {
    Processing ius = new Processing();
    ius.setProcessingId(Integer.MIN_VALUE);

    JaxbObject<Processing> instance = new JaxbObject<Processing>();
    String result = instance.marshal(ius);
    System.out.println(result);
    Reader in = new StringReader(result);
    Processing st = instance.unMarshal(ius, in);
    assertEquals(ius, st);
  }

  /**
   * Test marshalling and unmarshalling of WorkflowRun object.
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testWorkflowRunJaxb() throws Exception {
    Log.info("workflowRunJaxb");
    WorkflowRun wr = new WorkflowRun();
    wr.setWorkflowRunId(Integer.MIN_VALUE);

    Processing p = new Processing();
    p.setProcessingId(Integer.MAX_VALUE);

    IUS ius = new IUS();
    ius.setIusId(Integer.MAX_VALUE);

    Lane lane = new Lane();
    lane.setLaneId(Integer.MIN_VALUE);

    SortedSet iuses = new TreeSet();
    iuses.add(ius);

    SortedSet lanes = new TreeSet();
    lanes.add(lane);

    wr.setIus(iuses);
    wr.setLanes(lanes);
    p.setIUS(iuses);
    p.setLanes(lanes);

    JaxbObject<WorkflowRun> jaxb = new JaxbObject<WorkflowRun>();
    String wrResult = jaxb.marshal(wr);

    JaxbObject<Processing> jaxbP = new JaxbObject<Processing>();
    String pResult = jaxbP.marshal(p);

    WorkflowRun x = (WorkflowRun) XmlTools.unMarshal(jaxb, new WorkflowRun(), wrResult);
    Processing y = (Processing) XmlTools.unMarshal(jaxbP, new Processing(), pResult);
    assertEquals(wr.getWorkflowRunId(), x.getWorkflowRunId());
    assertEquals(p.getProcessingId(), y.getProcessingId());

    assertEquals(1, y.getLanes().size());
    assertEquals(1, x.getLanes().size());
    assertEquals(1, y.getIUS().size());
    assertEquals(1, x.getIus().size());
  }

  /**
   * <p>testWorkflowRunListJaxb.</p>
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testWorkflowRunListJaxb() throws Exception {
    WorkflowRun r1 = new WorkflowRun();
    r1.setCommand("r1");
    WorkflowRun r2 = new WorkflowRun();
    r2.setCommand("r2");

    Sample s1 = new Sample();
    s1.setAlias("alias1");
    SortedSet<Sample> samples = new TreeSet<Sample>();
    r1.setSamples(samples);

    ArrayList<WorkflowRun> list = new ArrayList<WorkflowRun>();
    list.add(r1);
    list.add(r2);

    WorkflowRunList2 rvl = new WorkflowRunList2();
    rvl.setList(list);

    JaxbObject<WorkflowRunList2> jaxb = new JaxbObject<WorkflowRunList2>();
    String text = jaxb.marshal(rvl);
    if (!text.contains("r1") && !text.contains("r2") && !text.contains("alias1")) {
      Assert.fail("Marshalling WorkflowRunList failed");
    }

    Log.info(text);

    WorkflowRunList2 unmarshalledList = (WorkflowRunList2) XmlTools.unMarshal(jaxb, rvl, text);
    Assert.assertEquals(2, unmarshalledList.getList().size());
    for (WorkflowRun rv : unmarshalledList.getList()) {
      Log.info(rv.getCommand());
    }
  }

  /**
   * <p>testWorkflowListJaxb.</p>
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testWorkflowListJaxb() throws Exception {
    Workflow r1 = new Workflow();
    r1.setCommand("r1");
    Workflow r2 = new Workflow();
    r2.setCommand("r2");

    // Sample s1 = new Sample();
    // s1.setAlias("alias1");
    // SortedSet<Sample> samples = new TreeSet<Sample>();
    // r1.setSamples(samples);

    ArrayList<Workflow> list = new ArrayList<Workflow>();
    list.add(r1);
    list.add(r2);

    WorkflowList rvl = new WorkflowList();
    rvl.setList(list);

    JaxbObject<WorkflowList> jaxb = new JaxbObject<WorkflowList>();
    String text = jaxb.marshal(rvl);
    if (!text.contains("r1") && !text.contains("r2") && !text.contains("alias1")) {
      Assert.fail("Marshalling WorkflowRunList failed");
    }

    WorkflowList unmarshalledList = (WorkflowList) XmlTools.unMarshal(jaxb, rvl, text);
    Assert.assertEquals(2, unmarshalledList.getList().size());
    for (Workflow rv : unmarshalledList.getList()) {
      Log.info(rv.getCommand());
    }
  }

  /**
   * <p>testReturnValueJaxb.</p>
   *
   * @throws java.lang.Exception if any.
   */
  @Test
  public void testReturnValueJaxb() throws Exception {
    ReturnValue r1 = new ReturnValue();
    r1.setAlgorithm("r1");
    ReturnValue r2 = new ReturnValue();
    r2.setAlgorithm("r2");

    FileMetadata fm = new FileMetadata("filepath", "metatype");
    ArrayList<FileMetadata> fmlist = new ArrayList<FileMetadata>();
    fmlist.add(fm);
    r1.setFiles(fmlist);

    r1.getAttributes().put("key", "value1");
    r2.getAttributes().put("key", "value2");

    ArrayList<ReturnValue> list = new ArrayList<ReturnValue>();
    list.add(r1);
    list.add(r2);

    ReturnValueList rvl = new ReturnValueList();
    rvl.setList(list);

    JaxbObject<ReturnValueList> jaxb = new JaxbObject<ReturnValueList>();
    String text = jaxb.marshal(rvl);
    if (!text.contains("r1") && text.contains("r2")) {
      Assert.fail("Marshalling ReturnValueList failed - simple types");
    }

    if (!text.contains("key") && text.contains("value1") && text.contains("value2")) {
      Assert.fail("Marshalling ReturnValueList failed - attributes");
    }

    ReturnValueList unmarshalledList = (ReturnValueList) XmlTools.unMarshal(jaxb, rvl, text);
    Assert.assertEquals(2, unmarshalledList.getList().size());
    for (ReturnValue rv : unmarshalledList.getList()) {
      Assert.assertNotNull("Algorithm should not be null", rv.getAlgorithm());
      Assert.assertNotNull("Attribute key is null.", rv.getAttribute("key"));
    }

  }
}
