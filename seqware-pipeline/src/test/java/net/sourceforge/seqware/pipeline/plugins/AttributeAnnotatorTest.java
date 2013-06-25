/*
 * Copyright (C) 2013 SeqWare
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

import java.util.Map;
import java.util.Set;
import joptsimple.OptionSet;
import net.sourceforge.seqware.common.model.ExperimentAttribute;
import net.sourceforge.seqware.common.model.FileAttribute;
import net.sourceforge.seqware.common.model.IUSAttribute;
import net.sourceforge.seqware.common.model.LaneAttribute;
import net.sourceforge.seqware.common.model.ProcessingAttribute;
import net.sourceforge.seqware.common.model.SampleAttribute;
import net.sourceforge.seqware.common.model.SequencerRunAttribute;
import net.sourceforge.seqware.common.model.StudyAttribute;
import net.sourceforge.seqware.common.model.WorkflowAttribute;
import net.sourceforge.seqware.common.model.WorkflowRunAttribute;
import net.sourceforge.seqware.common.module.ReturnValue;
import net.sourceforge.seqware.common.util.filetools.FileTools;
import net.sourceforge.seqware.common.util.workflowtools.WorkflowTools;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 *
 * @author dyuen
 */
@PrepareForTest({WorkflowTools.class, FileTools.class, AttributeAnnotator.class})
@RunWith(PowerMockRunner.class)
public class AttributeAnnotatorTest {

    @Mock
    private Map<String, String> config;
    @Mock
    private OptionSet options;
    @Mock
    private net.sourceforge.seqware.common.metadata.Metadata metadata;
    @InjectMocks
    private AttributeAnnotator attributeAnnotator;

    @Before
    public void initMocks() throws Exception {
        reset(config, options, metadata);
        attributeAnnotator = new AttributeAnnotator(); // this is kind of hacky
        // apparantly testNG retains the state of mocks and statuschecker from test to test, so we need to rebuild everything
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testInvalidSyntax() {
        ReturnValue ret = attributeAnnotator.init();
        Assert.assertTrue("attributeAnnotator could not init", ret.getExitStatus() == ReturnValue.SUCCESS);
        ret = attributeAnnotator.do_run();
        Assert.assertTrue("attributeAnnotator did not return invalid syntax", ret.getExitStatus() == ReturnValue.INVALIDPARAMETERS);
        attributeAnnotator.do_test();
        verifyNoMoreInteractions(metadata);
    }

    @Test
    public void testSequencerRun() {
        ReturnValue ret = attributeAnnotator.init();
        Assert.assertTrue("attributeAnnotator could not init", ret.getExitStatus() == ReturnValue.SUCCESS);
        createSimpleAnnotation();

        when(options.has("sequencer-run-accession")).thenReturn(true);
        when(options.valueOf("sequencer-run-accession")).thenReturn("1");

        ret = attributeAnnotator.do_run();
        Assert.assertTrue("attributeAnnotator completed successfully", ret.getExitStatus() == ReturnValue.SUCCESS);
        verify(metadata).annotateSequencerRun(eq(1), (SequencerRunAttribute) anyObject(), anyBoolean());
        verifyNoMoreInteractions(metadata);
    }

    @Test
    public void testEveryX() {
        ReturnValue ret = attributeAnnotator.init();
        Assert.assertTrue("attributeAnnotator could not init", ret.getExitStatus() == ReturnValue.SUCCESS);

        String[] types = {"sequencer-run", "lane", "ius", "experiment", "processing", "sample", "study", "workflow", "workflow-run", "file"};
        for (String type : types) {
            createSimpleAnnotation();
            when(options.has(type + "-accession")).thenReturn(true);
            when(options.valueOf(type + "-accession")).thenReturn("1");
            ret = attributeAnnotator.do_run();
            Assert.assertTrue("attributeAnnotator did not complete successfully", ret.getExitStatus() == ReturnValue.SUCCESS);
            when(options.has(type + "-accession")).thenReturn(false);
        }
        verify(metadata).annotateSequencerRun(eq(1), isA(SequencerRunAttribute.class), anyBoolean());
        verify(metadata).annotateLane(eq(1), isA(LaneAttribute.class), anyBoolean());
        verify(metadata).annotateIUS(eq(1), isA(IUSAttribute.class), anyBoolean());
        verify(metadata).annotateExperiment(eq(1), isA(ExperimentAttribute.class), anyBoolean());
        verify(metadata).annotateProcessing(eq(1), isA(ProcessingAttribute.class), anyBoolean());
        verify(metadata).annotateSample(eq(1), isA(SampleAttribute.class), anyBoolean());
        verify(metadata).annotateStudy(eq(1), isA(StudyAttribute.class), anyBoolean());
        verify(metadata).annotateWorkflow(eq(1), isA(WorkflowAttribute.class), anyBoolean());
        verify(metadata).annotateWorkflowRun(eq(1), isA(WorkflowRunAttribute.class), anyBoolean());
        verify(metadata).annotateFile(eq(1), isA(FileAttribute.class), anyBoolean());
        verifyNoMoreInteractions(metadata);
    }
    
    @Test
    public void testBulkInsert() {
        ReturnValue ret = attributeAnnotator.init();
        Assert.assertTrue("attributeAnnotator could not init", ret.getExitStatus() == ReturnValue.SUCCESS);
        String path = AttributeAnnotatorTest.class.getResource("attributeAnnotator.csv").getPath();
        when(options.has("file")).thenReturn(true);
        when(options.valueOf("file")).thenReturn(path);
        
        ret = attributeAnnotator.do_run();
        Assert.assertTrue("attributeAnnotator did not complete successfully", ret.getExitStatus() == ReturnValue.SUCCESS);
  
        verify(metadata).annotateSequencerRun(anyInt(), isA(Set.class));
        verify(metadata).annotateLane(anyInt(), isA(Set.class));
        verify(metadata).annotateIUS(anyInt(), isA(Set.class));
        verify(metadata).annotateExperiment(anyInt(), isA(Set.class));
        verify(metadata).annotateProcessing(anyInt(), isA(Set.class));
        verify(metadata).annotateSample(anyInt(), isA(Set.class));
        verify(metadata).annotateStudy(anyInt(), isA(Set.class));
        verify(metadata).annotateWorkflow(anyInt(), isA(Set.class));
        verify(metadata).annotateWorkflowRun(anyInt(), isA(Set.class));
        verifyNoMoreInteractions(metadata);
    }

    private void createSimpleAnnotation() {
        when(options.has("value")).thenReturn(true);
        when(options.valueOf("value")).thenReturn("Marvin");
        when(options.has("skip")).thenReturn(false);
        //when(options.valueOf("skip")).thenReturn("false");
    }
    
    
}
